package fi.oph.ohjausparametrit.controller;

import fi.oph.ohjausparametrit.service.model.JSONParameter;
import fi.oph.ohjausparametrit.service.dao.JSONParameterRepository;

import java.util.Date;

import io.swagger.annotations.Api;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

/**
 * Simpler parameters:
 * <pre>
 * GET  /v1/rest/parametri/TARGET_KEY_OID_OR_SOMETHING -> json of all parameters
 *
 * POST /v1/rest/parametri/TARGET_KEY_OID_OR_SOMETHING <- json of all parameters updated/inserted/removed (replaces existing)
 * POST /v1/rest/parametri/TARGET_KEY_OID_OR_SOMETHING/PARAM_NAME <- json of single parameter updated/inserted/removed
 * </pre>
 *
 */
@RestController
@RequestMapping(value = "/api/v1/rest/parametri")
@Api(tags = "Henkilöön liittyvät operaatiot")
@Transactional
public class OhjausparametritController {

    private static final Logger LOG = LoggerFactory.getLogger(OhjausparametritController.class);

    @Autowired
    private JSONParameterRepository dao;

    @GetMapping("/authorize")
    @PreAuthorize("hasAnyRole('ROLE_APP_TARJONTA_READ', 'ROLE_APP_TARJONTA_READ_UPDATE', 'ROLE_APP_TARJONTA_CRUD', 'ROLE_APP_KOUTA_OPHPAAKAYTTAJA')")
    public String doAuthorize() {
        LOG.debug("GET /authorize");
        return getCurrentUserName();
    }
    
    /**
     * @return all parameters to all targets as JSON.
     */
    @GetMapping("/ALL")
    public String doGetAll() {
        try {
            JSONObject result = new JSONObject();
        
            for (JSONParameter jSONParameter : dao.findAll()) {
                result.put(jSONParameter.getTarget(), getAsJSON(jSONParameter.getJsonValue()));
            }

            return result.toString();
        } catch (JSONException ex) {
            LOG.error("Failed to produce json output...?", ex);
            throw new RuntimeException(ex);
        }
    }
    
    /**
     * Load parameters for given target.
     * 
     * @param target ex. Haku oid
     * @return parameters as JSON
     */
    @GetMapping("/{target}")
    public String doGet(@PathVariable String target) {
        String value = getParametersAsString(target);
        if (value == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "target not found");
        }
        return value;
    }

    /**
     * Save parameters for given "target"
     * 
     * @param target ex. Haku oid
     * @param value parameters (multiple) of given target as json
     * @return success == OK, failure NOT_ACCEPTABLE or UNAUTHORIZED
     */
    @PostMapping("/{target}")
    @PreAuthorize("hasAnyRole('ROLE_APP_TARJONTA_READ_UPDATE', 'ROLE_APP_TARJONTA_CRUD', 'ROLE_APP_KOUTA_OPHPAAKAYTTAJA')")
    public void doPost(@PathVariable String target, @RequestBody String value) {
        if (value == null || value.trim().isEmpty()) {
            setParameters(target, (String) null);
        } else {
            JSONObject json = getAsJSON(value);
            if (json == null) {
                LOG.error("Could not parse json for {}: {}", target, value);
                throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE);
            }
            setParameters(target, json);
        }
    }

    /**
     * Partial parameters update.
     * 
     * @param target ex. haku oid
     * @param paramName ex. PH_HKMT
     * @param value parameter value as json
     * @return success == OK, failure == NOT_AUTHORIZED or NOT_ACCEPTABLE
     */
    @PostMapping("/{target}/{paramName}")
    @PreAuthorize("hasAnyRole('ROLE_APP_TARJONTA_READ_UPDATE', 'ROLE_APP_TARJONTA_CRUD', 'ROLE_APP_KOUTA_OPHPAAKAYTTAJA')")
    public void doPost(@PathVariable String target, @PathVariable String paramName, @RequestBody String value) {
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
                LOG.error("Could not parse json for {}, {}: {}", target, paramName, value);
                throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE);
            }
            // Modify parameters object
            try {
                jsonParameters.put(paramName, jsonParam);
            } catch (JSONException ex) {
                LOG.error("Failed to add/change parameter", ex);
                // Some other JSON error?
                throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE);
            }
        }
        
        // Save modified parameters
        setParameters(target, jsonParameters);

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

}
