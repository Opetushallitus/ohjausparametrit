package fi.vm.sade.ohjausparametrit.service;


import java.util.Random;
import javax.ws.rs.core.Response;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@ContextConfiguration(locations = { "classpath:test-context.xml" })
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("test")
public class OhjausparametritResourceTest {

    @Autowired
    private OhjausparametritResourceV1 op;
    
    @Test
    public void testCreateReadParam() {

        String paramName = "" + System.currentTimeMillis();
        String value1 = "{ foo: true }";
        String value2 = "{ foo: false }";
                
        // Find
        Response res = op.doGet(paramName);
        Assert.assertEquals("0 Parameter should not have been found", Response.Status.NOT_FOUND.getStatusCode(), res.getStatus());
        
        // Save
        res = op.doPost("APP_TARJONTA_CRUD", paramName, value1);
        Assert.assertEquals("1 Parameter should have been created", Response.Status.OK.getStatusCode(), res.getStatus());

        // Find
        res = op.doGet(paramName);
        Assert.assertEquals("2 Parameter should have been found", Response.Status.OK.getStatusCode(), res.getStatus());
        
        // Delete
        res = op.doPost("APP_TARJONTA_CRUD", paramName, null);
        Assert.assertEquals("3 Parameter should have been created", Response.Status.OK.getStatusCode(), res.getStatus());
        
        res = op.doGet(paramName);
        Assert.assertEquals("4 Parameter should not have been found after delete", Response.Status.NOT_FOUND.getStatusCode(), res.getStatus());
    }
    
}
