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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Logging to prosess qrovy script
 * 
 * @author mlyly
 */
@Component(value = "myLogger")
public class MyLoggerBean {
    
    private static final Logger LOG = LoggerFactory.getLogger(MyLoggerBean.class);

    public void debug(String msg, Object... args) {
        LOG.debug("LOG: " + msg, args);
    }

    public void info(String msg, Object... args) {
        LOG.info("LOG: " + msg, args);
    }

    public void warn(String msg, Object... args) {
        LOG.warn("LOG: " + msg, args);
    }

    public void error(String msg) {
        LOG.error("LOG: " + msg);
    }

    public void error(String msg, Throwable ex) {
        LOG.error("LOG: " + msg, ex);
    }
}
