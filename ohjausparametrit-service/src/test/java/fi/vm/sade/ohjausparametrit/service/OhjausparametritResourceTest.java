package fi.vm.sade.ohjausparametrit.service;

import java.util.List;

import javax.ws.rs.core.Response;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import fi.vm.sade.ohjausparametrit.api.model.ParameterRDTO;

@ContextConfiguration(locations = { "classpath:test-context.xml" })
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("test")
public class OhjausparametritResourceTest {

    @Autowired
    OhjausparametritResource op;

    @Test
    public void testParamAutocreatesTemplateString() {
        List<ParameterRDTO> params = (List<ParameterRDTO>)op.getParameterByPathAndName("HK_foo", "oid-1234").getEntity();
        Assert.assertEquals(0, params.size());
        Response r = op.setParameter("HK_foo", "oid-1234", new Param("string-value"));
        Assert.assertEquals(200, r.getStatus());
        params = (List<ParameterRDTO>)op.getParameterByPathAndName("HK_foo", "oid-1234").getEntity();
        Assert.assertEquals(1, params.size());
        Assert.assertTrue(params.get(0).getValue().getClass()==String.class);
        Assert.assertEquals("string-value", params.get(0).getValue());
        
        //try to save as different type
        r = op.setParameter("HK_foo", "oid-1234", new Param(true));
        Assert.assertEquals(400, r.getStatus());
    }
    
    @Test
    public void testParamAutocreatesTemplateLong(){
        List<ParameterRDTO> params = (List<ParameterRDTO>)op.getParameterByPathAndName("HK_foo1", "oid-1234").getEntity();
        Assert.assertEquals(0, params.size());
        Response r = op.setParameter("HK_foo1", "oid-1234", new Param(100l));
        Assert.assertEquals(200, r.getStatus());
        params = (List<ParameterRDTO>)op.getParameterByPathAndName("HK_foo1", "oid-1234").getEntity();
        Assert.assertEquals(1, params.size());
        Assert.assertTrue(params.get(0).getValue().getClass()==Long.class);
        Assert.assertEquals(100l, params.get(0).getValue());

    
        //try to save as different type
        r = op.setParameter("HK_foo1", "oid-1234", new Param("text"));
        Assert.assertEquals(400, r.getStatus());
    }
    
    @Test
    public void testParamAutocreatesTemplateBoolean(){
        List<ParameterRDTO> params = (List<ParameterRDTO>)op.getParameterByPathAndName("HK_foo2", "oid-1234").getEntity();
        Assert.assertEquals(0, params.size());
        Response r = op.setParameter("HK_foo2", "oid-1234", new Param(true));
        Assert.assertEquals(200, r.getStatus());
        params = (List<ParameterRDTO>)op.getParameterByPathAndName("HK_foo2", "oid-1234").getEntity();
        Assert.assertEquals(1, params.size());
        Assert.assertTrue(params.get(0).getValue().getClass()==Boolean.class);
        Assert.assertEquals(true, params.get(0).getValue());

        //try to save as different type
        r = op.setParameter("HK_foo2", "oid-1234", new Param("text"));
        Assert.assertEquals(400, r.getStatus());

    }

}
