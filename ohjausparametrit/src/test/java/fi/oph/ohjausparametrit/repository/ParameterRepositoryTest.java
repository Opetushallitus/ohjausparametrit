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
package fi.oph.ohjausparametrit.repository;

import fi.oph.ohjausparametrit.TestApplication;
import fi.oph.ohjausparametrit.model.JSONParameter;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.NONE,
    classes = {TestApplication.class})
@ActiveProfiles("test")
public class ParameterRepositoryTest {

  @Autowired private JSONParameterRepository dao;

  @Before
  public void setup() {
    dao.deleteAll();
  }

  @Test
  public void testCrud() {

    {
      List<JSONParameter> ps =
          StreamSupport.stream(dao.findAll().spliterator(), false).collect(Collectors.toList());
      Assert.assertEquals("Param repo should be empty", ps.size(), 0);
    }

    {
      JSONParameter p = new JSONParameter();
      p.setTarget("A");
      p.setJsonValue("{ \"foo\": false}");
      dao.save(p);
    }

    {
      List<JSONParameter> ps =
          StreamSupport.stream(dao.findAll().spliterator(), false).collect(Collectors.toList());
      Assert.assertEquals("Param repo should be populated with one entry", ps.size(), 1);
    }

    dao.deleteById("A");

    {
      List<JSONParameter> ps =
          StreamSupport.stream(dao.findAll().spliterator(), false).collect(Collectors.toList());
      Assert.assertEquals("Param repo should be empty", ps.size(), 0);
    }
  }

  @Test
  public void testFindByTimeRange() {
    {
      JSONParameter p = new JSONParameter();
      p.setTarget("A");
      p.setJsonValue("{ \"foo\": false}");
      p.setMuokkaaja("Frank");
      p.setMuokattu(new Date());
      dao.save(p);

      p = new JSONParameter();
      p.setTarget("B");
      p.setJsonValue("{ \"foo\": true}");
      p.setMuokkaaja("Frank Armoton");
      p.setMuokattu(new Date());
      dao.save(p);

      OffsetDateTime nyt = OffsetDateTime.now();
      OffsetDateTime eilen = nyt.minusDays(1);
      List<JSONParameter> results =
          dao.findByTimeRange(
              Date.from(eilen.toInstant()), Date.from(nyt.toInstant()), PageRequest.of(0, 5000));
      Assert.assertEquals("kaikki löytyy", 2, results.size());
    }
  }
}
