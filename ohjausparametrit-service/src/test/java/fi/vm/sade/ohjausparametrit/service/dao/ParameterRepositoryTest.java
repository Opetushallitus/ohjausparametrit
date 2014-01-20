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

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.common.collect.Lists;

import fi.vm.sade.ohjausparametrit.service.model.Parameter;
import fi.vm.sade.ohjausparametrit.service.model.Parameter.Type;

@ContextConfiguration(locations = {"classpath:test-context.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("test")
public class ParameterRepositoryTest {

    @Autowired
    private ParamRepository paramRepository;

    @Test
    public void testCrud() {
        Parameter param = new Parameter();
        param.setCreatedBy("user1");
        param.setPath("path");
        param.setValue("value");
        param.setType(Type.STRING);
        param.setName("name");
        Parameter param2 = new Parameter();
        param2.setCreatedBy("user2");
        param2.setPath("path");
        param2.setValue("value");
        param2.setType(Type.STRING);
        param2.setName("name1");

        Parameter saved = paramRepository.save(param);
        long id = saved.getId();
        List<Parameter> params = Lists.newArrayList(paramRepository.findAll());
        Assert.assertEquals(1,params.size());
        Assert.assertEquals("user1", params.get(0).getCreatedBy());
        saved.setCreatedBy("user2");
        paramRepository.save(param);
        params = Lists.newArrayList(paramRepository.findAll());
        Assert.assertEquals(1,params.size());
        saved = paramRepository.findOne(id);
        Assert.assertEquals("user2", params.get(0).getCreatedBy());
        saved = paramRepository.save(param2);
        saved = paramRepository.findOne(saved.getId());
        Assert.assertEquals("user2", saved.getCreatedBy());
        params = Lists.newArrayList(paramRepository.findAll());
        Assert.assertEquals(2,params.size());
        
        
    }
}