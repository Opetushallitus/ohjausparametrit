package fi.oph.ohjausparametrit.ovara.ajastus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(
    scanBasePackages = {
      "fi.oph.ohjausparametrit.audit",
      "fi.oph.ohjausparametrit.service.common",
      "fi.oph.ohjausparametrit.service.ovara",
      "fi.oph.ohjausparametrit.configurations.properties",
      "fi.oph.ohjausparametrit.ovara.ajastus",
    })
public class OvaraApp implements CommandLineRunner {
  final SiirtotiedostoAjastusService siirtotiedostoAjastusService;

  private static final Logger logger = LoggerFactory.getLogger(OvaraApp.class);

  public OvaraApp(SiirtotiedostoAjastusService siirtotiedostoAjastusService) {
    this.siirtotiedostoAjastusService = siirtotiedostoAjastusService;
  }

  public static void main(String[] args) {
    logger.info("Starting spring application!");
    SpringApplication.run(OvaraApp.class, args);
    System.exit(0);
  }

  @Override
  public void run(String... args) {
    System.out.println("Running!");
    try {
      logger.info("Kutsutaan siirtotiedostoServiceä");
      String result = siirtotiedostoAjastusService.createNextSiirtotiedosto();
      logger.info("Result: " + result);
    } catch (Exception e) {
      logger.error("Tapahtui virhe:", e);
      throw e;
    }
  }
}
