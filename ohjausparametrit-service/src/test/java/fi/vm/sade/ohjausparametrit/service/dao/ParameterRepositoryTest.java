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

import com.mongodb.Mongo;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.MongodConfig;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;
import java.io.IOException;
import java.net.UnknownHostException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 *
 * @author mlyly
 */
@ContextConfiguration(locations = {"classpath:test-context.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class ParameterRepositoryTest {

    private static final Logger LOG = LoggerFactory.getLogger(ParameterRepositoryTest.class);

    @Autowired
    private ParameterRepository parameterRepository;

    private static Integer MONGO_PORT = 12345;

    // Start stop mongo
    private static MongodExecutable _mongodExe;
    private static MongodProcess _mongod;
    private Mongo _mongo;

    @BeforeClass
    public static void setUpClass() throws UnknownHostException, IOException {
        LOG.info("setUpClass()...");
        MongodStarter runtime = MongodStarter.getDefaultInstance();
        _mongodExe = runtime.prepare(new MongodConfig(Version.Main.PRODUCTION, MONGO_PORT, Network.localhostIsIPv6()));
        _mongod = _mongodExe.start();
        LOG.info("setUpClass()... done.");
    }

    @AfterClass
    public static void tearDownClass() throws InterruptedException {
        LOG.info("tearDownClass()...");
        _mongod.stop();
        _mongod.waitFor();
        _mongodExe.stop();

        LOG.info("tearDownClass()... done.");
    }

    @Before
    public void setUp() throws UnknownHostException {
        _mongo = new Mongo("localhost", 12345);
    }

    @After
    public void tearDown() {
        _mongo.dropDatabase("test");
    }

    @Test
    public void testSomeMethod() {
        LOG.info("testSomeMethod()...");

        LOG.info("  repo = {}", parameterRepository);

        LOG.info("testSomeMethod()... done.");
    }
}