package fi.oph.ohjausparametrit.ovara;

import fi.oph.ohjausparametrit.ovara.ajastus.SiirtotiedostoProsessi;
import fi.oph.ohjausparametrit.ovara.ajastus.SiirtotiedostoProsessiRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class SiirtotiedostoProsessiTest {

  @Autowired private SiirtotiedostoProsessiRepository spr;

  @Test
  public void testProcessCreationAndPersisting() {
    {
      System.out.println("Running SiirtotiedostoProsessiRepository test 2!");
      SiirtotiedostoProsessi edellinenOnnistunut = spr.findLatestSuccessful();

      SiirtotiedostoProsessi uusiProsessi = edellinenOnnistunut.createNewProcessBasedOnThis();
      Assert.assertEquals(
          "Uuden prosessin ikkuna alkaa siitä mihin edellisen onnistuneen ikkuna loppui",
          uusiProsessi.getWindowStart(),
          edellinenOnnistunut.getWindowEnd());
      uusiProsessi.setSuccess(true);
      spr.save(uusiProsessi);

      edellinenOnnistunut = spr.findLatestSuccessful();
      Assert.assertEquals(
          "Seuraava onnistunut prosessi on persistoitu oikein",
          edellinenOnnistunut.getExecutionUuid(),
          uusiProsessi.getExecutionUuid());
    }
  }
}
