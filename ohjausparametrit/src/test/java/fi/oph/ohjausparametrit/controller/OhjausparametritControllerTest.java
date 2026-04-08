package fi.oph.ohjausparametrit.controller;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import fi.oph.ohjausparametrit.TestApplication;
import fi.oph.ohjausparametrit.client.KoutaClient;
import fi.oph.ohjausparametrit.client.OrganisaatioClient;
import fi.oph.ohjausparametrit.client.dto.KoutaHaku;
import fi.oph.ohjausparametrit.exception.ForbiddenException;
import fi.oph.ohjausparametrit.exception.NotFoundException;
import fi.oph.ohjausparametrit.repository.JSONParameterRepository;
import java.util.ArrayList;
import java.util.List;
import org.hamcrest.core.StringContains;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.NONE,
    classes = {TestApplication.class})
@EnableConfigurationProperties
@ActiveProfiles("test")
public class OhjausparametritControllerTest {

  @Autowired private OhjausparametritController op;

  @Autowired private OrganisaatioClient organisaatioClient;

  @Autowired private KoutaClient koutaClient;

  @Autowired private JSONParameterRepository repository;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  private static final String VALUE_1 = "{ \"key1\": true, \"key2\": false }";
  private static final String VALUE_2 = "{ \"key1\": true, \"key2\": true }";

  @Test
  @WithMockUser(username = "1.2.246.562.24.25763910658", roles = "APP_TARJONTA_CRUD")
  public void testCreateReadParam() {
    repository.deleteAll();
    String target = "TARGET";
    String param = "PARAM";
    String value = "{ \"PARAM\": { \"foo\": true } }";

    // Find
    assertThrows(NotFoundException.class, () -> op.doGet(param));

    // Save
    op.doPost(target, value);

    // Find
    String res = op.doGet(target);
    assertThat(res, StringContains.containsString("{\"PARAM\": {\"foo\": true}}"));

    // Find all
    res = op.doGetAll();
    assertThat(res, StringContains.containsString("{\"TARGET\":{\"PARAM\":{\"foo\":true}"));
  }

  @Test
  @WithMockUser(
      username = "1.2.246.562.24.25763910658",
      roles = {"APP_KOUTA_HAKU_CRUD", "APP_KOUTA_HAKU_CRUD_1.2.246.562.10.59078453392"})
  public void testCreateReadParamWithAuthorizationWithRequiredRolesToimipisteForbidden() {
    when(organisaatioClient.getChildOids("1.2.246.562.10.59078453392"))
        .thenReturn(new ArrayList<>());
    when(koutaClient.getHaku("1.2.246.562.29.00000000000000000800"))
        .thenReturn(
            new KoutaHaku("1.2.246.562.29.00000000000000000800", "1.2.246.562.10.00000000001"));
    assertThrows(
        ForbiddenException.class, () -> op.doPost("1.2.246.562.29.00000000000000000800", VALUE_1));
  }

  @Test
  @WithMockUser(
      username = "1.2.246.562.24.25763910658",
      roles = {"APP_KOUTA_HAKU_CRUD", "APP_KOUTA_HAKU_CRUD_1.2.246.562.10.59078453392"})
  public void testCreateReadParamWithAuthorizationWithRequiredRolesToimipisteAllowed() {
    when(organisaatioClient.getChildOids("1.2.246.562.10.59078453392"))
        .thenReturn(new ArrayList<>());
    when(koutaClient.getHaku("1.2.246.562.29.00000000000000000801"))
        .thenReturn(
            new KoutaHaku("1.2.246.562.29.00000000000000000801", "1.2.246.562.10.59078453392"));
    op.doPost("1.2.246.562.29.00000000000000000801", VALUE_1);
  }

  @Test
  @WithMockUser(
      username = "1.2.246.562.24.25763910658",
      roles = {"APP_KOUTA_HAKU_CRUD", "APP_KOUTA_HAKU_CRUD_1.2.246.562.10.67476956288"})
  public void testCreateReadParamWithAuthorizationWithRequiredRolesOppilaitosForbidden() {
    when(organisaatioClient.getChildOids("1.2.246.562.10.67476956288"))
        .thenReturn(List.of("1.2.246.562.10.59078453392"));
    when(koutaClient.getHaku("1.2.246.562.29.00000000000000000800"))
        .thenReturn(
            new KoutaHaku("1.2.246.562.29.00000000000000000800", "1.2.246.562.10.00000000001"));
    assertThrows(
        ForbiddenException.class, () -> op.doPost("1.2.246.562.29.00000000000000000800", VALUE_1));
  }

  @Test
  @WithMockUser(
      username = "1.2.246.562.24.25763910658",
      roles = {"APP_KOUTA_HAKU_CRUD", "APP_KOUTA_HAKU_CRUD_1.2.246.562.10.67476956288"})
  public void testCreateReadParamWithAuthorizationWithRequiredRolesOppilaitosAllowed() {
    when(organisaatioClient.getChildOids("1.2.246.562.10.67476956288"))
        .thenReturn(List.of("1.2.246.562.10.59078453392"));
    when(koutaClient.getHaku("1.2.246.562.29.00000000000000000802"))
        .thenReturn(
            new KoutaHaku("1.2.246.562.29.00000000000000000802", "1.2.246.562.10.59078453392"));
    op.doPost("1.2.246.562.29.00000000000000000802", VALUE_1);
  }

  @Test
  @WithMockUser(
      username = "1.2.246.562.24.25763910658",
      roles = {"APP_KOUTA_OPHPAAKAYTTAJA", "APP_KOUTA_OPHPAAKAYTTAJA_1.2.246.562.10.00000000001"})
  public void testCreateReadParamWithAuthorizationWithOphPaakayttajaRole() {
    when(organisaatioClient.getChildOids("1.2.246.562.10.67476956288"))
        .thenReturn(List.of("1.2.246.562.10.59078453392"));
    when(koutaClient.getHaku("1.2.246.562.29.00000000000000000800"))
        .thenReturn(
            new KoutaHaku("1.2.246.562.29.00000000000000000800", "1.2.246.562.10.59078453392"));
    op.doPost("1.2.246.562.29.00000000000000000800", VALUE_1);
    op.doPost("1.2.246.562.29.00000000000000000800", VALUE_2);
  }
}
