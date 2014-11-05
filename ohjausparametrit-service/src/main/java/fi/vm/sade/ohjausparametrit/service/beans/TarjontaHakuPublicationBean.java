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
import fi.vm.sade.mail.Mailer;
import fi.vm.sade.mail.dto.MailMessage;
import java.io.IOException;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * This bean is called from business process that gets created when relevant parameter (PH_TJT.date) has been modified
 * for a given Haku.
 * 
 * Calls tarjonta service to publish the haku, sends email when publication fails.
 * 
 * @author mlyly
 */
@Component("tarjontaHakuPublicationBean")
public class TarjontaHakuPublicationBean {

    private static final Logger LOG = LoggerFactory.getLogger(TarjontaHakuPublicationBean.class);
    
    @Value("${ohjausparametrit.tarjonta.publish.casService:CAS_SERVICE_URL_MISSING}")
    private String casServiceUrl;
    
    @Value("${ohjausparametrit.tarjonta.publish.user}")
    private String username;

    @Value("${ohjausparametrit.tarjonta.publish.password}")
    private String password;

    @Value("${ohjausparametrit.tarjonta.publish.service}")
    private String serviceUrl;

    @Value("${ohjausparametrit.tarjonta.publish.error.enabled}")
    private String errorEmailEnabled;

    @Value("${ohjausparametrit.tarjonta.publish.error.email}")
    private String errorEmailAddress;

    @Autowired
    private Mailer mailer;
    
    public TarjontaHakuPublicationBean() {
        LOG.info("TarjontaHakuPublicationBean()");
    }

    public void printConfig() {
        LOG.info("TarjontaHakuPublicationBean() configuration");
        LOG.info("  username= {}", username);
        LOG.info("  password= {}", (password != null) ? "<provided>" : "MISSING!");
        LOG.info("  serviceUrl= {}", serviceUrl);
        LOG.info("  casServiceUrl= {}", casServiceUrl);
        LOG.info("  errorEmailEnabled= {}", errorEmailEnabled);
        LOG.info("  errorEmailAddress= {}", errorEmailAddress);
    }

    public boolean publish(String hakuOid) {
        LOG.info("publish(oid={})", hakuOid);
        this.printConfig();
        
        boolean result = false;
        
        String urlStr = serviceUrl + "/rest/v1/haku/" + hakuOid + "/state?state=JULKAISTU";
        LOG.info("  target url = {}", urlStr);
        
        try {
            CachingRestClient client = getCachingRestClient();

            HttpPut put = new HttpPut(urlStr);
            LOG.info("  do request: {}", put);

            HttpResponse response = client.execute(put, "application/json", null);
            LOG.info("  done request: {}", response);

            result = (response != null && response.getStatusLine() != null && response.getStatusLine().getStatusCode() == 200);

            LOG.info("  done request, result = {}", result);
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
            LOG.info("  send the error mail now to: {}", errorEmailAddress);        
            try {
                MailMessage message = new MailMessage();
                message.setTo(errorEmailAddress);
                message.setFrom("ohjausparametrit-noreply@opintopolku.fi");
                message.setSubject("ERROR - Haun julkaisu epäonnistui: haun oid = " + hakuOid);
                
                String body = "/n";
                body += "ERROR - Haun julkaisu epäonnistui: haun oid = " + hakuOid;
                body += "\n";
                
                body += "Asetukset:\n";
                body += "  username=" + username;
                body += "  password=" + ((password != null) ? "<provided>" : "MISSING!");
                body += "  serviceUrl=" + serviceUrl;
                body += "  casServiceUrl=" + casServiceUrl;
                body += "  errorEmailEnabled=" + errorEmailEnabled;
                body += "  errorEmailAddress=" + errorEmailAddress;
                body += "\n";

                if (ex != null) {
                    body += "Virhe: \n";
                    body += ex.toString();
                    body += "\n";
                }
                body += "Tähän viestiin ei voi vastata.\n";
                
                message.setBody(body);
                
                mailer.sendMail(message);
            } catch (Exception ex2) {
                LOG.error("Failed to send error email about Haku publication failure.", ex2);
            }
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
