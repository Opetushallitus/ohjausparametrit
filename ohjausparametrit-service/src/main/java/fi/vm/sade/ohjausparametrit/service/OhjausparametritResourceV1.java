package fi.vm.sade.ohjausparametrit.service;

import fi.vm.sade.ohjausparametrit.service.dao.JSONParameterRepository;
import fi.vm.sade.ohjausparametrit.service.model.JSONParameter;
import java.util.Date;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

/**
 * Simpler parameters:
 * <pre>
 * GET  /v1/rest/parametri/TARGET_KEY_OID_OR_SOMETHING -> json of all parameters
 *
 * POST /v1/rest/parametri/TARGET_KEY_OID_OR_SOMETHING <- json of all parameters updated/inserted/removed
 * POST /v1/rest/parametri/TARGET_KEY_OID_OR_SOMETHING/PARAM_NAME <- json of single parameter updated/inserted/removed
 * </pre>
 * 
 * @author mlyly
 */
@Path("/parametri")
@Transactional
public class OhjausparametritResourceV1 {

    private static final Logger LOG = LoggerFactory.getLogger(OhjausparametritResourceV1.class);

    public static final String ROLE_READ = "ROLE_APP_TARJONTA_READ";
    public static final String ROLE_CRUD = "ROLE_APP_TARJONTA_CRUD";
    public static final String ROLE_UPDATE = "ROLE_APP_TARJONTA_READ_UPDATE";
    
    @Autowired
    private JSONParameterRepository dao;

    /**
     * "Ping" method to see if service is "alive".
     * 
     * @return "Hello: " + date.
     */
    @GET
    @Path("/hello")
    @Produces(MediaType.TEXT_PLAIN)
    public String doHello() {
        return "HELLO: " + new Date();
    }

    @GET
    @Path("/authorize")
    @Produces(MediaType.TEXT_PLAIN)
    @Secured({ROLE_READ, ROLE_UPDATE, ROLE_CRUD})
    public String doAuthorize() {
        LOG.debug("GET /authorize");
        return getCurrentUserName();
    }
    
    /**
     * @return all parameters to all targets as JSON.
     */
    @GET
    @Path("/ALL")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public Response doGetAll() {
        LOG.debug("GET /ALL");

        Response.ResponseBuilder rb = Response.status(Response.Status.OK);

        try {
            JSONObject result = new JSONObject();
        
            for (JSONParameter jSONParameter : dao.findAll()) {
                result.put(jSONParameter.getTarget(), getAsJSON(jSONParameter.getJsonValue()));
            }

            rb.entity(result.toString(2));
        } catch (JSONException ex) {
            LOG.error("Failed to produce json output...?", ex);
            rb = Response.status(Response.Status.INTERNAL_SERVER_ERROR);
        }
        return rb.build();
    }
    
    /**
     * Load parameters for given target.
     * 
     * @param target ex. Haku oid
     * @return parameters as JSON
     */
    @GET
    @Path("/{target}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public Response doGet(@PathParam("target") String target) {
        LOG.debug("GET /{}", target);

        String value = getParametersAsString(target);
        if (value == null) {
            // Maybe return "{}" ?
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        Response.ResponseBuilder rb = Response.status(Response.Status.OK);
        rb.entity(value);
        LOG.debug("  --> {} = {}", target, value);
        return rb.build();
    }

    /**
     * Save parameters for given "target"
     * 
     * @param target ex. Haku oid
     * @param value parameters (multiple) of given target as json
     * @return success == OK, failure NOT_ACCEPTABLE or UNAUTHORIZED
     */
    @POST
    @Path("/{target}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Secured({ROLE_UPDATE, ROLE_CRUD})
    public Response doPost(@PathParam("target") String target, String value) {
        LOG.debug("POST /{} <= {}", new Object[]{target, value});

        if (value == null || value.trim().isEmpty()) {
            // Remove parameters
            setParameters(target, (String) null);
        } else {
            JSONObject json = getAsJSON(value);
            if (json == null) {
                // Not JSON data
                return Response.status(Response.Status.NOT_ACCEPTABLE).build();
            }

            // Save parameters
            setParameters(target, json);
        }

        return Response.ok().build();
    }

    /**
     * Partial parameters update.
     * 
     * @param target ex. haku oid
     * @param paramName ex. PH_HKMT
     * @param value parameter value as json
     * @return success == OK, failure == NOT_AUTHORIZED or NOT_ACCEPTABLE
     */
    @POST
    @Path("/{target}/{paramName}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Secured({ROLE_UPDATE, ROLE_CRUD})
    public Response doPost(@PathParam("target") String target, @PathParam("paramName") String paramName, String value) {
        LOG.debug("POST /{}/{} <= {}", new Object[]{target, paramName, value});

        // Find existing parameters
        JSONObject jsonParameters = getParameters(target);
        if (jsonParameters == null) {
            jsonParameters = getAsJSON("{}");
        }

        // Remove named parameter?
        if (value == null || value.trim().isEmpty()) {
            // Remove selected parameter in parameters
            jsonParameters.remove(paramName);
        } else {
            // Add / modify
            JSONObject jsonParam = getAsJSON(value);
            if (jsonParam == null) {
                // Not JSON data
                return Response.status(Response.Status.NOT_ACCEPTABLE).build();
            }
            // Modify parameters object
            try {
                jsonParameters.put(paramName, jsonParam);
            } catch (JSONException ex) {
                LOG.error("Failed to add/change parameter", ex);
                // Some other JSON error?
                return Response.status(Response.Status.NOT_ACCEPTABLE).build();
            }
        }

        // Save modified parameters
        setParameters(target, jsonParameters);

        return Response.ok().build();
    }

    //
    // Helpers
    //

    /**
     * Parse string to JSONObject.
     * 
     * @param data
     * @return null on error
     */
    private JSONObject getAsJSON(String data) {
        try {
            if (data == null) {
                return null;
            }
            return new JSONObject(data);
        } catch (JSONException ex) {
            return null;
        }
    }

    /**
     * Format JSON to string.
     * 
     * @param json
     * @return null on error
     */
    private String getJSONAsString(JSONObject json) {
        try {
            if (json == null) {
                return null;
            }
            return json.toString(2);
        } catch (JSONException ex) {
            return null;
        }
    }

    /**
     * Saves role to given json to field "__role__".
     * 
     * @param json
     * @param role 
     */
    private void setRoleToJson(JSONObject json, String role) {
        try {
            if (json != null && role != null) {
                json.put("__role__", role);
            }
        } catch (JSONException ex) {
            LOG.error("Invalid role def: " + role, ex);
        }
    }

    /**
     * Returns field "__role__" from JSON.
     * 
     * @param json
     * @return 
     */
    private String getRoleFromJson(JSONObject json) {
        if (json == null) {
            return null;
        }
        try {
            return json.getString("__role__");
        } catch (JSONException ex) {
            return null;
        }
    }

    /**
     * Verifies that "role" equals "__role__" in JSON.
     * 
     * @param role
     * @param target
     * @param json
     * @return 
     */
    private boolean verifyRoleInParameters(String role, String target, JSONObject json) {
        if (role == null) {
            return false;
        }

        if (json == null) {
            json = getParameters(target);
        }

        if (json == null) {
            // New parameter
            return true;
        }

        return role.equalsIgnoreCase(getRoleFromJson(json));
    }

    /**
     * Gets current username - for "auditing" purposes.
     *
     * @return
     */
    private String getCurrentUserName() {
        if (isLoggedInUser()) {
            return SecurityContextHolder.getContext().getAuthentication().getName();
        } else {
            return "NA";
        }
    }

    /**
     * @return true if user has been logged in AND is not "anonymousUser"
     */
    private boolean isLoggedInUser() {
        boolean result = true;

        result = result && SecurityContextHolder.getContext() != null;
        result = result && SecurityContextHolder.getContext().getAuthentication() != null;
        result = result && SecurityContextHolder.getContext().getAuthentication().isAuthenticated();
        result = result && SecurityContextHolder.getContext().getAuthentication().getPrincipal() != null;
        result = result && SecurityContextHolder.getContext().getAuthentication().getAuthorities() != null;
        result = result && SecurityContextHolder.getContext().getAuthentication().getAuthorities().isEmpty() == false;

        // TODO how to make this check more robust?
        result = result && !"anonymousUser".equalsIgnoreCase(SecurityContextHolder.getContext().getAuthentication().getName());

        LOG.debug("isLoggedInUser(): {} - {}", result, result ? SecurityContextHolder.getContext().getAuthentication().getName() : "NA");
        // LOG.debug("  authorities = {}", result ? SecurityContextHolder.getContext().getAuthentication().getAuthorities() : null);

        return result;
    }
   
    /**
     * Returns true if user has given role / authority
     * 
     * @param role
     * @return true if has
     */
    private boolean userHasRole(String role)  {
        // No empty roles
        if (role == null || role.trim().isEmpty()) {
            return false;
        }

        // Is user logged in?
        if (!isLoggedInUser()) {
            return false;
        }
        
        // Does he have the required role?
        for (GrantedAuthority grantedAuthority : SecurityContextHolder.getContext().getAuthentication().getAuthorities()) {
            String grantedRole = grantedAuthority.getAuthority();
            boolean hasRole = grantedRole != null && grantedRole.startsWith(role);
            LOG.info("checking: {} ==? {} --> {}", new Object[] {grantedAuthority, role, hasRole});
            if (hasRole) {
                return true;
            }
        }

        return false;
    }
    
    
    //
    // "DAO" Layer
    //
    
    /**
     * Insert / update parameters for given "target"
     * 
     * @param target ex. Haku oid
     * @param value parameters (multiple) as JSON
     */
    private void setParameters(String target, String value) {

        // Find existing parameter if any
        JSONParameter p = dao.findByTarget(target);

        if (value == null || value.trim().isEmpty()) {
            if (p != null) {
                dao.delete(p);
            }
        } else {
            if (p == null) {
                // New target
                p = new JSONParameter();
                p.setTarget(target);
            }
            p.setJsonValue(value);

            dao.save(p);
        }
    }

    /**
     * Save params.
     * 
     * @param target
     * @param value 
     */
    private void setParameters(String target, JSONObject value) {
        if (value != null) {
            try {
                value.put("__modified__",  new Date().getTime());
                value.put("__modifiedBy__",  getCurrentUserName());
            } catch (JSONException ex) {
                LOG.error("Failed to set modified / modified by");
            }
        }
        
        setParameters(target, getJSONAsString(value));
    }

    /**
     * Load params by target
     * 
     * @param target ex. Haku oid
     * @return JSON
     */
    private JSONObject getParameters(String target) {
        return getAsJSON(getParametersAsString(target));
    }

    /**
     * Load params as JSON.
     * 
     * @param target ex. Haku oid
     * @return JSON as string
     */
    private String getParametersAsString(String target) {
        String result;

        JSONParameter p = dao.findByTarget(target);
        if (p != null) {
            result = p.getJsonValue();
        } else {
            result = null;
        }

        LOG.info("getParametersAsString({}) --> {}", target, result);
        return result;
    }
}
