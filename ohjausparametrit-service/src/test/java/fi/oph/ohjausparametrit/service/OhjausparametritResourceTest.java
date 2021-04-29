package fi.oph.ohjausparametrit.service;

import static org.junit.Assert.*;

import fi.oph.ohjausparametrit.TestApplication;
import fi.oph.ohjausparametrit.client.KoutaClient;
import fi.oph.ohjausparametrit.client.OrganisaatioClient;
import fi.oph.ohjausparametrit.client.dto.KoutaHaku;
import fi.oph.ohjausparametrit.configurations.H2Configuration;
import fi.oph.ohjausparametrit.controller.OhjausparametritController;
import org.assertj.core.util.Lists;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.StringContains;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.server.ResponseStatusException;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;

import static org.mockito.Mockito.*;

//@RunWith(SpringRunner.class)
//@RunWith(MockitoJUnitRunner.class)
//@RunWith(SpringRunner.class)
@RunWith(SpringJUnit4ClassRunner.class)
//@ExtendWith(MockitoExtension.class)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.NONE,
    classes = {TestApplication.class, H2Configuration.class})
@ActiveProfiles("test")
public class OhjausparametritResourceTest {

  @InjectMocks
  private OhjausparametritController op;

  @Mock
  OrganisaatioClient organisaatioClient;

  @Mock
  KoutaClient koutaClient;

  @Before
  public void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Ignore
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

  @Test
  @WithMockUser(roles = "APP_TARJONTA_CRUD")
  public void testCreateReadParamWithAuthorizationWithRequiredRolesToimipiste() {

    when(organisaatioClient.getChildOids("1.2.246.562.10.59078453392")).thenReturn(Lists.emptyList());

    when(koutaClient.getHaku("1.2.246.562.29.00000000000000000800")).thenReturn(new KoutaHaku("1.2.246.562.29.00000000000000000800", "1.2.246.562.10.00000000001"));
    when(koutaClient.getHaku("1.2.246.562.29.00000000000000000801")).thenReturn(new KoutaHaku("1.2.246.562.29.00000000000000000801", "1.2.246.562.10.59078453392"));

    String param = "PARAM";
    String value1 = "{ " + param + ": { foo: true } }";

    // Save
    op.doPost("1.2.246.562.29.00000000000000000800", value1);
    op.doPost("1.2.246.562.29.00000000000000000801", value1);

  }
}
