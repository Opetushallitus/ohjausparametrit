/*
 * Copyright (c) 2013 The Finnish Board of Education - Opetushallitus
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
package fi.vm.sade.ohjausparametrit.service.dao;

import java.util.List;

import fi.vm.sade.ohjausparametrit.service.beans.TarjontaHakuPublicationBean;
import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.common.collect.Lists;
import fi.vm.sade.ohjausparametrit.service.model.JSONParameter;


@ContextConfiguration(locations = { "classpath:test-context.xml" })
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("test")
public class ParameterRepositoryTest {

    @Autowired
    private JSONParameterRepository dao;

    @Autowired
    private TarjontaHakuPublicationBean bean;

    @Before
    public void setup(){
        dao.deleteAll();
    }
    
    @Test
    public void testCrud() {

        {
            List<JSONParameter> ps = Lists.newArrayList(dao.findAll());
            Assert.assertEquals("Param repo should be empty", ps.size(), 0);
        }

        {
            JSONParameter p = new JSONParameter();
            p.setTarget("A");
            p.setJsonValue("{ foo: false}");
            dao.save(p);
        }

        {
            List<JSONParameter> ps = Lists.newArrayList(dao.findAll());
            Assert.assertEquals("Param repo should be populated with one entry", ps.size(), 1);
        }

        dao.delete("A");
        
        {
            List<JSONParameter> ps = Lists.newArrayList(dao.findAll());
            Assert.assertEquals("Param repo should be empty", ps.size(), 0);
        }
    }

}