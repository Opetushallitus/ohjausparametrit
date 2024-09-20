package fi.oph.ohjausparametrit.ovara.ajastus;

import fi.oph.ohjausparametrit.service.ovara.SiirtotiedostoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SiirtotiedostoAjastusService {

    private static final Logger logger = LoggerFactory.getLogger(SiirtotiedostoAjastusService.class);

    private final SiirtotiedostoProsessiRepository siirtotiedostoProsessiRepository;
    private final SiirtotiedostoService siirtotiedostoService;

    public SiirtotiedostoAjastusService(SiirtotiedostoProsessiRepository siirtotiedostoProsessiRepository,
                                        SiirtotiedostoService siirtotiedostoService
    ) {
        this.siirtotiedostoProsessiRepository = siirtotiedostoProsessiRepository;
        this.siirtotiedostoService = siirtotiedostoService;
    }

    public String createNextSiirtotiedosto() {
        logger.info("Creating siirtotiedosto by ajastus!");
        SiirtotiedostoProsessi latest = siirtotiedostoProsessiRepository.findLatestSuccessful();
        logger.info("Latest: {}", latest);
        return "OK" + latest.toString();
        //todo: hae tieto edellisestä onnistuneesta
        //todo: persistoi uusi prosessi
        //todo: hae muutokset uuden prosessin aikaikkunassa
        //olemassaoleva: luo siirtotiedosto(t) muutosdatalle
        //todo: persistoi prosessi kantaan (joko onnistuneena tai virheellisenä)
    }
}
