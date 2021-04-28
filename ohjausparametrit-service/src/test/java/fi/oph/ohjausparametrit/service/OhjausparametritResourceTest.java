package fi.oph.ohjausparametrit.service;

import static org.junit.Assert.*;

import fi.oph.ohjausparametrit.TestApplication;
import fi.oph.ohjausparametrit.configurations.H2Configuration;
import fi.oph.ohjausparametrit.controller.OhjausparametritController;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.StringContains;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.server.ResponseStatusException;

@RunWith(SpringRunner.class)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.NONE,
    classes = {TestApplication.class, H2Configuration.class})
@ActiveProfiles("test")
public class OhjausparametritResourceTest {

  @Autowired private OhjausparametritController op;

  @Test
  @WithMockUser(roles = "APP_TARJONTA_CRUD")
  public void testCreateReadParam() {

    String target = "TARGET";
    String param = "PARAM";
    String value1 = "{ " + param + ": { foo: true } }";

    // Find
    Exception exception =
        assertThrows(
            ResponseStatusException.class,
            () -> {
              op.doGet(param);
            });
    assertEquals("404 NOT_FOUND \"target not found\"", exception.getMessage());

    // Save
    op.doPost(target, value1);

    // Find
    String res = op.doGet(target);
    MatcherAssert.assertThat(
        res, StringContains.containsString("\"PARAM\": {\n" + "    \"foo\": true\n" + "  }"));

    // Find all
    res = op.doGetAll();
    MatcherAssert.assertThat(
        res, StringContains.containsString("{\"TARGET\":{\"PARAM\":{\"foo\":true}"));
  }
}
