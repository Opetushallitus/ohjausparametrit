package fi.oph.ohjausparametrit.service;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import fi.oph.ohjausparametrit.TestApplication;
import fi.oph.ohjausparametrit.client.KoutaClient;
import fi.oph.ohjausparametrit.client.OrganisaatioClient;
import fi.oph.ohjausparametrit.client.dto.KoutaHaku;
import fi.oph.ohjausparametrit.configurations.H2Configuration;
import fi.oph.ohjausparametrit.controller.OhjausparametritController;
import fi.oph.ohjausparametrit.repository.JSONParameterRepository;
import java.util.ArrayList;
import java.util.Arrays;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.StringContains;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.server.ResponseStatusException;

@RunWith(SpringRunner.class)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.NONE,
    classes = {TestApplication.class, H2Configuration.class})
@EnableConfigurationProperties
@ActiveProfiles("test")
public class OhjausparametritResourceTest {

  @Autowired private OhjausparametritController op;

  @Autowired private OrganisaatioClient organisaatioClient;

  @Autowired private KoutaClient koutaClient;

  @Autowired private JSONParameterRepository repository;

  @Before
  public void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @BeforeEach
  void beforeEach() {
    repository.deleteAll();
  }

  @Test
  @WithMockUser(roles = "APP_TARJONTA_CRUD")
  public void testCreateReadParam() {
    repository.deleteAll();
    String target = "TARGET";
    String param = "PARAM";
    String value1 = "{ PARAM: { foo: true } }";

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
  @WithMockUser(roles = {"APP_KOUTA_HAKU_CRUD", "APP_KOUTA_HAKU_CRUD_1.2.246.562.10.59078453392"})
  public void testCreateReadParamWithAuthorizationWithRequiredRolesToimipiste() {
    String value = "{}";
    when(organisaatioClient.getChildOids("1.2.246.562.10.59078453392"))
        .thenReturn(new ArrayList<>());
    when(koutaClient.getHaku("1.2.246.562.29.00000000000000000800"))
        .thenReturn(
            new KoutaHaku("1.2.246.562.29.00000000000000000800", "1.2.246.562.10.00000000001"));
    Exception exception =
        assertThrows(
            ResponseStatusException.class,
            () -> {
              op.doPost("1.2.246.562.29.00000000000000000800", value);
            });
    assertEquals("403 FORBIDDEN", exception.getMessage());
  }

  @Test
  @WithMockUser(roles = {"APP_KOUTA_HAKU_CRUD", "APP_KOUTA_HAKU_CRUD_1.2.246.562.10.59078453392"})
  public void testCreateReadParamWithAuthorizationWithRequiredRolesToimipisteAllowed() {
    String value = "{}";
    when(organisaatioClient.getChildOids("1.2.246.562.10.59078453392"))
        .thenReturn(new ArrayList<>());
    when(koutaClient.getHaku("1.2.246.562.29.00000000000000000801"))
        .thenReturn(
            new KoutaHaku("1.2.246.562.29.00000000000000000801", "1.2.246.562.10.59078453392"));
    op.doPost("1.2.246.562.29.00000000000000000801", value);
  }

  @Test
  @WithMockUser(roles = {"APP_KOUTA_HAKU_CRUD", "APP_KOUTA_HAKU_CRUD_1.2.246.562.10.67476956288"})
  public void testCreateReadParamWithAuthorizationWithRequiredRolesOppilaitosForbidden() {
    String value = "{}";
    when(organisaatioClient.getChildOids("1.2.246.562.10.59078453392"))
        .thenReturn(new ArrayList<>(Arrays.asList("1.2.246.562.10.67476956288")));
    when(koutaClient.getHaku("1.2.246.562.29.00000000000000000800"))
        .thenReturn(
            new KoutaHaku("1.2.246.562.29.00000000000000000800", "1.2.246.562.10.00000000001"));
    Exception exception =
        assertThrows(
            ResponseStatusException.class,
            () -> {
              op.doPost("1.2.246.562.29.00000000000000000800", value);
            });
    assertEquals("403 FORBIDDEN", exception.getMessage());
  }

  @Test
  @WithMockUser(roles = {"APP_KOUTA_HAKU_CRUD", "APP_KOUTA_HAKU_CRUD_1.2.246.562.10.67476956288"})
  public void testCreateReadParamWithAuthorizationWithRequiredRolesOppilaitosAllowed() {
    String value = "{}";
    when(organisaatioClient.getChildOids("1.2.246.562.10.59078453392"))
        .thenReturn(new ArrayList<>(Arrays.asList("1.2.246.562.10.67476956288")));
    when(koutaClient.getHaku("1.2.246.562.29.00000000000000000802"))
        .thenReturn(
            new KoutaHaku("1.2.246.562.29.00000000000000000802", "1.2.246.562.10.59078453392"));
    op.doPost("1.2.246.562.29.00000000000000000802", value);
  }

  @Test
  @WithMockUser(
      roles = {"APP_KOUTA_OPHPAAKAYTTAJA", "APP_KOUTA_OPHPAAKAYTTAJA_1.2.246.562.10.00000000001"})
  public void testCreateReadParamWithAuthorizationWithOphPaakayttajaRole() {
    String value = "{}";
    when(organisaatioClient.getChildOids("1.2.246.562.10.59078453392"))
        .thenReturn(new ArrayList<>(Arrays.asList("1.2.246.562.10.67476956288")));
    when(koutaClient.getHaku("1.2.246.562.29.00000000000000000800"))
        .thenReturn(
            new KoutaHaku("1.2.246.562.29.00000000000000000800", "1.2.246.562.10.59078453392"));
    op.doPost("1.2.246.562.29.00000000000000000800", value);
  }
}
