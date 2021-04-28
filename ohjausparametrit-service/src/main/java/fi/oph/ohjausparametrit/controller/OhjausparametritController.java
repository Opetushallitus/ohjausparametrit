package fi.oph.ohjausparametrit.controller;

import fi.oph.ohjausparametrit.service.ParameterService;
import fi.oph.ohjausparametrit.model.JSONParameter;
import fi.oph.ohjausparametrit.util.SecurityUtil;
import io.swagger.annotations.Api;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import static fi.oph.ohjausparametrit.util.JsonUtil.*;

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
    private ParameterService parameterService;

    @GetMapping("/authorize")
    @PreAuthorize("hasAnyRole('ROLE_APP_TARJONTA_READ', 'ROLE_APP_TARJONTA_READ_UPDATE', 'ROLE_APP_TARJONTA_CRUD', 'ROLE_APP_KOUTA_OPHPAAKAYTTAJA')")
    public String doAuthorize() {
        return SecurityUtil.getCurrentUserName();
    }
    
    /**
     * @return all parameters to all targets as JSON.
     */
    @GetMapping("/ALL")
    public String doGetAll() {
        try {
            JSONObject result = new JSONObject();
            for (JSONParameter jSONParameter : parameterService.findAll()) {
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
        JSONParameter parameter = parameterService.findByTarget(target);
        if (parameter == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "target not found");
        }
        return parameter.getJsonValue();
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
            parameterService.setParameters(target, (String) null);
        } else {
            JSONObject json = getAsJSON(value);
            if (json == null) {
                LOG.error("Could not parse json for {}: {}", target, value);
                throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE);
            }
            parameterService.setParameters(target, json);
        }
    }

}
