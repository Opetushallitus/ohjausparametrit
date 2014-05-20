package fi.vm.sade.ohjausparametrit.service;

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
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

/**
 * Simpler parameters:
 * <pre>
 * GET  /v1/rest/parametri/TARGET_KEY_OID_OR_SOMETHING -> json of all parameters
 * 
 * POST /v1/rest/parametri/REQUIRED_ROLE/TARGET_KEY_OID_OR_SOMETHING <- json of all parameters
 * POST /v1/rest/parametri/REQUIRED_ROLE/TARGET_KEY_OID_OR_SOMETHING/PARAM_NAME <- json of single parameter updated/inserted 
 * </pre>
 * 
 * @author mlyly
 */
@Path("/parametri")
@Transactional
public class OhjausparametritResourceV1 {

    private static final Logger LOG = LoggerFactory.getLogger(OhjausparametritResourceV1.class);

    private Map<String, String> parameters = new HashMap<String, String>();

    @GET
    @Path("/hello")
    @Produces("text/plain")
    public String doHello() {
        parameters.put("TEST", "{boolean: true, long: 283768746264, str: \"string value\"}");
        return "HELLO: " + new Date();
    }

    @GET
    @Path("/{target}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public Response doGet(@PathParam("target") String target) {
        LOG.info("GET /{}", target);

        String value = getParametersAsString(target);
        if (value == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        Response.ResponseBuilder rb = Response.status(Response.Status.OK);
        rb.entity(value);
        LOG.info("  --> media type = {}", MediaType.APPLICATION_JSON + ";charset=UTF-8");
        LOG.info("  --> {} = {}", target, value);
        return rb.build();
    }

    @POST
    @Path("/{role}/{target}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public Response doPost(@PathParam("role") String role, @PathParam("target") String target, String value) {
        LOG.info("POST /{}/{} <= {}", new Object[] {role, target, value});
        
        // TODO authenticate!
        
        JSONObject jsonParameters = getParameters(target);
        if (jsonParameters != null) {
            if (!verifyRoleInParameters(role, target, jsonParameters)) {
                LOG.error("NOT AUTHORIZED TO UPDATE.");
                return Response.status(Response.Status.UNAUTHORIZED).build();
            }
        }
        
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
            setRoleToJson(json, role);
            setParameters(target, json);
        }

        return Response.ok().build();
    }

    @POST
    @Path("/{role}/{target}/{paramName}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public Response doPost(@PathParam("role") String role, 
            @PathParam("target") String target,
            @PathParam("paramName") String paramName, 
            String value) {
        LOG.info("POST /{}/{}/{} <= {}", new Object[] {role, target, paramName, value});

        // TODO authenticate required role!
        
        JSONObject jsonParameters = getParameters(target);
        if (jsonParameters == null) {
            jsonParameters = getAsJSON("{}");
            setRoleToJson(jsonParameters, role);
        }

        if (!verifyRoleInParameters(role, target, jsonParameters)) {
            LOG.error("NOT AUTHORIZED TO UPDATE.");
            return Response.status(Response.Status.UNAUTHORIZED).build();
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
            }
        }

        // Save modified parameters, reset required role
        setRoleToJson(jsonParameters, role);
        setParameters(target, jsonParameters);

        return Response.ok().build();
    }
    
    //
    // Helpers
    //
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

    private void setRoleToJson(JSONObject json, String role) {
        try {
            if (json != null && role != null) {
                json.put("__role__", role);
            }
        } catch (JSONException ex) {
            LOG.error("Invalid role def: " + role, ex);
        }
    }
    
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
    
    
    //
    // "DAO"
    //
    private void setParameters(String target, String value) {
        if (value == null || value.trim().isEmpty()) {
            parameters.remove(target);
        } else {
            parameters.put(target, value);
        }
    }

    private void setParameters(String target, JSONObject value) {
        setParameters(target, getJSONAsString(value));
    }
    
    private JSONObject getParameters(String target) {
        return getAsJSON(getParametersAsString(target));
    }

    private String getParametersAsString(String target) {
        return parameters.get(target);
    }
}
