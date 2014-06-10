/*
 * Copyright (c) 2014 The Finnish Board of Education - Opetushallitus
 * 
 * This program is free software:  Licensed under the EUPL, Version 1.1 or - as
 * soon as they will be approved by the European Commission - subsequent versions
 * of the EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at: http://www.osor.eu/eupl/
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 */

package fi.vm.sade.ohjausparametrit.service.beans;

import fi.vm.sade.generic.rest.CachingRestClient;
import java.io.IOException;
import java.io.InputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 *
 * @author mlyly
 */
@Component("tarjontaKelaExportBean")
public class TarjontaKelaExportBean {

    public static final Logger LOG = LoggerFactory.getLogger(TarjontaKelaExportBean.class);

    @Value("${ohjausparametrit.tarjonta.export.kela.casService:CAS_SERVICE_URL_MISSING}")
    private String casServiceUrl;
    
    @Value("${ohjausparametrit.tarjonta.export.kela.user:ophadmin}")
    private String username;

    @Value("${ohjausparametrit.tarjonta.export.kela.password:ilonkautta!}")
    private String password;

    @Value("${ohjausparametrit.tarjonta.export.kela.service:https://itest-virkailija.oph.ware.fi/tarjonta-service}")
    private String serviceUrl;

    @Value("${ohjausparametrit.tarjonta.export.kela.error.enabled:false}")
    private String errorEmailEnabled;

    @Value("${ohjausparametrit.tarjonta.export.kela.error.email:sd@cybercom.com}")
    private String errorEmailAddress;

    public TarjontaKelaExportBean() {
        LOG.info("TarjontaKelaExportBean()");
    }

    public void printConfig() {
        LOG.info("TarjontaKelaExportBean() configuration");
        LOG.info("  username= {}", username);
        LOG.info("  password= {}", (password != null) ? "OK" : "missing!");
        LOG.info("  serviceUrl= {}", serviceUrl);
        LOG.info("  casServiceUrl= {}", casServiceUrl);
        LOG.info("  errorEmailEnabled= {}", errorEmailEnabled);
        LOG.info("  errorEmailAddress= {}", errorEmailAddress);
    }
    
    public boolean export() {
        LOG.info("export()...");
        this.printConfig();

        boolean result;
        
        CachingRestClient client = getCachingRestClient();
        
        try {
            InputStream in = client.get(serviceUrl + "/rest/v1/export/kela");
            result = true;
        } catch (IOException ex) {
            LOG.error("Failed to do kela export.", ex);
            result = false;
        }

        LOG.info("export()... done.");
        
        return result;
    }
    
    public boolean sendErrorEmail(Exception ex) {
        LOG.info("sendErrorEmail()...");
        this.printConfig();

        boolean result;
        
        if (Boolean.parseBoolean(errorEmailEnabled)) {
            // TODO send email
            LOG.info("  TODO send email with exception: {}", ex);
            result = true;
        } else {
            LOG.info("  error email sending disabled.");
            result = false;
        }

        LOG.info("sendErrorEmail()... done.");
        return result;
    }
    
    
    private CachingRestClient cachingRestClient = null;
    
    private CachingRestClient getCachingRestClient() {
        if (cachingRestClient == null) {
            cachingRestClient = new CachingRestClient();
            cachingRestClient.setWebCasUrl(casServiceUrl);
            cachingRestClient.setCasService(serviceUrl);
            cachingRestClient.setUsername(username);
            cachingRestClient.setPassword(password);
        }
        
        return cachingRestClient;
    }
    
    private void setCachingRestClient(CachingRestClient client) {
        cachingRestClient = client;
    }
    
}
