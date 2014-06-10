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
import java.util.logging.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 *
 * @author mlyly
 */
@Component("tarjontaHakuPublicationBean")
public class TarjontaHakuPublicationBean {

    private static final Logger LOG = LoggerFactory.getLogger(TarjontaHakuPublicationBean.class);
    
// /service-access/accessTicket?client_id=" + env.user + "&client_secret=" + env.pass + "&service_url=" + env.remote + "/authentication-service
// https://itest-virkailija.oph.ware.fi/service-access/accessTicket?client_id=ophadmin&client_secret=ophadmin&service_url=https://itest-virkailija.oph.ware.fi/tarjonta-service

    @Value("${ohjausparametrit.tarjonta.publish.casService}")
    private String casServiceUrl;
    
    @Value("${ohjausparametrit.tarjonta.publish.user:ophadmin}")
    private String username;

    @Value("${ohjausparametrit.tarjonta.publish.password:ilonkautta!}")
    private String password;

    @Value("${ohjausparametrit.tarjonta.publish.service:https://itest-virkailija.oph.ware.fi/tarjonta-service}")
    private String serviceUrl;

    @Value("${ohjausparametrit.tarjonta.publish.error.enabled:false}")
    private String errorEmailEnabled;

    @Value("${ohjausparametrit.tarjonta.publish.error.email:sd@cybercom.com}")
    private String errorEmailAddress;

    public TarjontaHakuPublicationBean() {
        LOG.info("TarjontaHakuPublicationBean()");
    }

    public void printConfig() {
        LOG.info("TarjontaHakuPublicationBean() configuration");
        LOG.info("  username= {}", username);
        LOG.info("  password= {}", (password != null) ? "OK" : "missing!");
        LOG.info("  serviceUrl= {}", serviceUrl);
        LOG.info("  casServiceUrl= {}", casServiceUrl);
        LOG.info("  errorEmailEnabled= {}", errorEmailEnabled);
        LOG.info("  errorEmailAddress= {}", errorEmailAddress);
    }

    public boolean publish(String hakuOid) {
        LOG.info("publish(oid={})", hakuOid);

        this.printConfig();
        
        boolean result;
        
        CachingRestClient client = getCachingRestClient();
        
        try {
            InputStream in = client.get(serviceUrl + "/rest/v1/permission/authorize");
            result = true;
        } catch (IOException ex) {
            LOG.error("Failed to publish haku: " + hakuOid, ex);
            result = false;
        }
        
        return result;
    }

    public boolean sendErrorEmail(String hakuOid, Exception ex) {
        LOG.info("sendErrorEmail - failed to publish haku with OID = {}", hakuOid);

        this.printConfig();
        
        if (Boolean.valueOf(errorEmailEnabled)) {
            LOG.info("  should send the mail now to: {}", errorEmailAddress);
        } else {
            LOG.info("  error email sending disabled.");
        }

        return true;
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
