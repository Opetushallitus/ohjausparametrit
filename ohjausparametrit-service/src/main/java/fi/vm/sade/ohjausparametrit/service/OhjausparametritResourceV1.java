package fi.vm.sade.ohjausparametrit.service;

import fi.vm.sade.ohjausparametrit.service.dao.JSONParameterRepository;
import fi.vm.sade.ohjausparametrit.service.model.JSONParameter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceQuery;
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
 * POST /v1/rest/parametri/TARGET_KEY_OID_OR_SOMETHING <- json of all parameters updated/inserted/removed (replaces existing)
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

    @Autowired
    private ProcessEngine processEngine;
    
    
    /**
     * "Ping" method to see if service is "alive".
     * 
     * @return "Hello: " + date.
     */
    @GET
    @Path("/hello")
    @Produces(MediaType.TEXT_PLAIN)
    public String doHello() {
        LOG.info("/hello - date now {} == {}", new Date(), new Date().getTime());
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
            
            // Removes old processess for target
            hakuPublishProcessStart(target, (JSONObject) null);
        } else {
            JSONObject json = getAsJSON(value);
            if (json == null) {
                // Not JSON data
                return Response.status(Response.Status.NOT_ACCEPTABLE).build();
            }

            // Save parameters
            setParameters(target, json);

            // Removes old and possibly starts new process for haku TJT parameter
            hakuPublishProcessStart(target, json);
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

        if ("PH_TJT".equalsIgnoreCase(paramName)) {
            // Removes old and possibly stats new process for haku TJT parameter
            hakuPublishProcessStart(target, jsonParameters);
        }
        
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

        LOG.debug("getParametersAsString({}) --> {}", target, result);
        return result;
    }

    /**
     * Removes old "tarjontaHakuPublication" processes and starts new one to run on given date.
     * 
     * @param target
     * @param jsonParameters 
     */
    private void hakuPublishProcessStart(String target, JSONObject jsonParameters) {

        LOG.info("hakuPublishProcessStart({}, {})", target, jsonParameters);
        
        if (jsonParameters == null || !jsonParameters.has("PH_TJT")) {
            LOG.info("  remove old process because NULL or no PH_TJT");
            hakuPublishProcessStart(target, (Date) null);
            return;
        }

        JSONObject ph_tjt;
        try {
            ph_tjt = jsonParameters.getJSONObject("PH_TJT");
        } catch (JSONException ex) {
            LOG.error("Failed to get parameter PH_TJT!", ex);
            return;
        }
        
        if (ph_tjt == null) {
            LOG.info("  remove old process because NULL PH_TJT");
            hakuPublishProcessStart(target, (Date) null);
            return;
        }

        if (!ph_tjt.has("date") || ph_tjt.isNull("date")) {
            LOG.info("  remove old process because PH_TJT does not have 'date' or it is null");
            hakuPublishProcessStart(target, (Date) null);
            return;
        }

        try {
            long ts = ph_tjt.getLong("date");
            Date date = new Date(ts);
            hakuPublishProcessStart(target, date);
        } catch (JSONException ex) {
            LOG.error("Invalid json value for 'PH_TJT.date' - not long! params={}", jsonParameters);
        }
    }

    /**
     * Removes old "tarjontaHakuPublication" processes and starts new one to run on given date.
     * 
     * @param target
     * @param date 
     */
    private void hakuPublishProcessStart(String target, Date date) {
        LOG.info("hakuPublishProcessStart(target={}, date={})...", target, date);

        // Find processes to modify
        ProcessInstanceQuery q = processEngine.getRuntimeService().createProcessInstanceQuery();
        q.processDefinitionKey("tarjontaHakuPublication");
        q.variableValueEquals("target", target);
        q.variableValueEquals("type", "PH_TJT");

        // Delete old instances of targets processes
        for (ProcessInstance processInstance : q.list()) {
            LOG.info("  found old process instance to delete: id={}", processInstance.getId());
            processEngine.getRuntimeService().deleteProcessInstance(processInstance.getProcessInstanceId(), "new values acquired");
        }

        if (date != null) {
            try {
                Map model = new HashMap();
                model.put("target", target);
                model.put("type", "PH_TJT");
                model.put("date", date);
                // model.put("process_start_time", new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(date));
            
                ProcessInstance pi = processEngine.getRuntimeService().startProcessInstanceByKey("tarjontaHakuPublication", model);
                LOG.info("started process instance: {} - model = {}", pi.getId(), model);
            } catch (Exception ex) {
                LOG.error("Failed to start process for " + target, ex);
            }
        } else {
            LOG.info("  no process to start for target: {}", target);
        }

        LOG.info("hakuPublishProcessStart()... done.");
    }
}
