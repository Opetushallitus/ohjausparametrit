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

import fi.vm.sade.javautils.http.OphHttpClient;
import fi.vm.sade.javautils.http.OphHttpRequest;
import fi.vm.sade.javautils.http.OphHttpResponse;
import fi.vm.sade.javautils.http.auth.CasAuthenticator;

import fi.vm.sade.properties.OphProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import java.util.concurrent.Semaphore;

import static java.util.function.Function.identity;

/**
 * This bean is called from business process that gets created when relevant parameter (PH_TJT.date) has been modified
 * for a given Haku.
 * 
 * Calls tarjonta service to publish the haku
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

    @Value("${host.virkailija}")
    private String hostVirkailija;

    private Semaphore semaphore;

    @Value("${ohjausparametrit.tarjonta.publish.slotlimit}")
    private int semaphoreSlotLimit;

    private OphProperties properties = new OphProperties().addFiles("/ohjausparametrit-service_oph.properties");
    private OphHttpClient ophHttpClient = null;

    public TarjontaHakuPublicationBean() {
        LOG.info("TarjontaHakuPublicationBean()");
    }


    @PostConstruct
    public void initialize() {
        properties.addDefault("baseUrl", "https://" + hostVirkailija);

        CasAuthenticator casAuthenticator = new CasAuthenticator.Builder()
                .username(username)
                .password(password)
                .webCasUrl(casServiceUrl)
                .casServiceUrl(serviceUrl)
                .build();

        semaphore = new Semaphore(semaphoreSlotLimit);

        ophHttpClient = new OphHttpClient.Builder("ohjausparametrit-service")
                .authenticator(casAuthenticator)
                .build();
    }

    public void printConfig() {
        LOG.info("TarjontaHakuPublicationBean() configuration");
        LOG.info("  username= {}", username);
        LOG.info("  password= {}", (password != null) ? "<provided>" : "MISSING!");
        LOG.info("  serviceUrl= {}", serviceUrl);
        LOG.info("  casServiceUrl= {}", casServiceUrl);
        LOG.info("  semaphoreSlotLimit= {}", semaphoreSlotLimit);
    }

    public boolean publish(String hakuOid) {
        LOG.info("publish(oid={})", hakuOid);
        this.printConfig();

        if (semaphore.tryAcquire()) {
            LOG.info("Acquired permit. Remaining available: " + semaphore.availablePermits());
            try {
                LOG.info("publishing haku: " + hakuOid);
                OphHttpRequest request = OphHttpRequest.Builder
                        .put(properties.url("tarjonta-service.haku.julkaise", hakuOid))
                        .addHeader("Caller-Id", "ohjausparametrit-service")
                        .build();

                String result = ophHttpClient.<String>execute(request)
                        .expectedStatus(200)
                        .mapWith(identity())
                        .orElse(null);
                if (result == null) {
                    String msg = "Failed to publish haku: Invalid status code from tarjonta";
                    LOG.error(msg);
                    throw new RuntimeException(msg);
                } else {
                    LOG.info("Published haku" + hakuOid + ". Changes: " + result);
                    return true;
                }
            } catch (Exception ex) {
                LOG.error("Failed to publish haku: " + hakuOid, ex);
                return false;
            } finally {
                LOG.info("Releasing permit.");
                semaphore.release();
                LOG.info("Available permits: " + semaphore.availablePermits());
            }
        } else {
            LOG.error("Could not acquire permit to publish haku: " + hakuOid + ", remaining available permits: " + semaphore.availablePermits());
            return false;
        }

    }

}
