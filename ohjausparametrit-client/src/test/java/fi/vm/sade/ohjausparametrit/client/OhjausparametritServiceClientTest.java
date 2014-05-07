/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package fi.vm.sade.ohjausparametrit.client;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author mlyly
 */
public class OhjausparametritServiceClientTest {
    
    public OhjausparametritServiceClientTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of getParameters method, of class OhjausparametritServiceClient.
     */
    @Test
    public void testGetParameters() throws Exception {
        System.out.println("getParameters");
        String target = "1.2.246.562.29.36319310482";
        OhjausparametritServiceClient instance = new OhjausparametritServiceClient("https://itest-virkailija.oph.ware.fi/ohjausparametrit-service/api/rest/parametri");
        // instance.getParameters(target);
    }
    
}
