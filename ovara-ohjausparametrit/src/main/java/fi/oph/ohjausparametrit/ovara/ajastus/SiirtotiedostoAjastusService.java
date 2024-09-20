package fi.oph.ohjausparametrit.ovara.ajastus;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fi.oph.ohjausparametrit.configurations.properties.SiirtotiedostoProperties;
import fi.oph.ohjausparametrit.model.JSONParameter;
import fi.oph.ohjausparametrit.service.common.ParameterService;
import fi.oph.ohjausparametrit.service.ovara.SiirtotiedostoService;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Service;

@Service
@EnableJpaRepositories(basePackages = {"fi.oph.ohjausparametrit.ovara.ajastus"})
public class SiirtotiedostoAjastusService {

    private static final Logger logger = LoggerFactory.getLogger(SiirtotiedostoAjastusService.class);

    ObjectMapper mapper = new ObjectMapper();

    private final SiirtotiedostoProsessiRepository siirtotiedostoProsessiRepository;
    private final SiirtotiedostoService siirtotiedostoService;
    private final ParameterService parameterService;
    private final SiirtotiedostoProperties siirtotiedostoProperties;

    public SiirtotiedostoAjastusService(SiirtotiedostoProsessiRepository siirtotiedostoProsessiRepository,
                                        SiirtotiedostoService siirtotiedostoService, ParameterService parameterService, SiirtotiedostoProperties siirtotiedostoProperties
    ) {
        this.siirtotiedostoProsessiRepository = siirtotiedostoProsessiRepository;
        this.siirtotiedostoService = siirtotiedostoService;
        this.parameterService = parameterService;
        this.siirtotiedostoProperties = siirtotiedostoProperties;
    }

    public String createNextSiirtotiedosto() {
        logger.info("Creating siirtotiedosto by ajastus!");
        SiirtotiedostoProsessi latest = siirtotiedostoProsessiRepository.findLatestSuccessful();
        logger.info("Latest: {}", latest);
        SiirtotiedostoProsessi uusi = latest.createNewProcessBasedOnThis();
        logger.info("New process: {}", uusi);
        siirtotiedostoProsessiRepository.save(uusi);

        try {
            Map<String, Integer> infoMap = new HashMap<>();

            int partitionSize = siirtotiedostoProperties.getMaxItemcountInFile();
            int page = 0;
            List<String> keys = new ArrayList<>();
            int total = 0;
            List<JSONParameter> dbResults =
                    parameterService.getPartitionByModifyDatetime(
                            Date.from(uusi.getWindowStart().toInstant()),
                            Date.from(uusi.getWindowEnd().toInstant()),
                            partitionSize,
                            page++);;
            while (!dbResults.isEmpty()) {
                total += dbResults.size();
                logger.info("{} Tallennetaan {} tulosta, sivu {}", uusi.getExecutionUuid(), dbResults.size(), page);
                keys.add(siirtotiedostoService.createSiirtotiedosto(dbResults, uusi.getExecutionUuid(), page));
                dbResults = parameterService.getPartitionByModifyDatetime(Date.from(uusi.getWindowStart().toInstant()), Date.from(uusi.getWindowEnd().toInstant()), partitionSize, page++);
            }

            keys.forEach((key) -> logger.info("Tiedosto {}", key));
            logger.info("{} tuloksia: {}", uusi.getExecutionUuid(), total);
            infoMap.put("ohjausparametrit", total);
            JsonNode jsonNode = mapper.valueToTree(infoMap);
            uusi.setInfo(jsonNode);
            logger.info("{} Onnistui! Persistoidaan prosessi.", uusi.getExecutionUuid());
            uusi.setSuccess(true);
            uusi.setRunEnd(OffsetDateTime.now());
            siirtotiedostoProsessiRepository.save(uusi);
        } catch (Throwable t) {
            logger.error("{} Tapahtui virhe muodostettaessa ajastettua siirtotiedostoa:", uusi.getExecutionUuid(), t);
            uusi.setErrorMessage(t.getMessage());
            uusi.setSuccess(false);
            uusi.setInfo(null);
            uusi.setRunEnd(OffsetDateTime.now());
            siirtotiedostoProsessiRepository.save(uusi);
        }
        return "DONE";
    }
}
