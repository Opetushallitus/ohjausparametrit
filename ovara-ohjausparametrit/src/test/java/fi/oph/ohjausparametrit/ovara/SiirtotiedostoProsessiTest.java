package fi.oph.ohjausparametrit.ovara;

import static org.junit.jupiter.api.Assertions.assertEquals;

import fi.oph.ohjausparametrit.ovara.ajastus.SiirtotiedostoProsessi;
import fi.oph.ohjausparametrit.ovara.ajastus.SiirtotiedostoProsessiRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class SiirtotiedostoProsessiTest {

  @Autowired private SiirtotiedostoProsessiRepository spr;

  @Test
  void testProcessCreationAndPersisting() {
    {
      System.out.println("Running SiirtotiedostoProsessiRepository test 2!");
      SiirtotiedostoProsessi edellinenOnnistunut = spr.findLatestSuccessful();

      SiirtotiedostoProsessi uusiProsessi = edellinenOnnistunut.createNewProcessBasedOnThis();
      assertEquals(
          uusiProsessi.getWindowStart(),
          edellinenOnnistunut.getWindowEnd(),
          "Uuden prosessin ikkuna alkaa siitä mihin edellisen onnistuneen ikkuna loppui");
      uusiProsessi.setSuccess(true);
      spr.save(uusiProsessi);

      edellinenOnnistunut = spr.findLatestSuccessful();
      assertEquals(
          edellinenOnnistunut.getExecutionUuid(),
          uusiProsessi.getExecutionUuid(),
          "Seuraava onnistunut prosessi on persistoitu oikein");
    }
  }
}
