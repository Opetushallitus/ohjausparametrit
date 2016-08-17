# Ohjausparametrit #

Ohjausparametripalvelussa pidetään järjestelmän toimintaa ylläpitäviä ja ohjaavia parametreja.

Palvelu tarjoaa REST-rajapinnan. 

Yksi keskeinen käyttökohde on haun aikatauluasetukset.

## Käynnistys lokaalisti

1. Luo ohjausparametreille tyhjä postgre kanta lokaalisti.

2. Luo ~/oph-configuration/common.properties tiedosto. Aseta propertiesit&kantakonffit. (esim luokalta pohja)

3. Kopioi /docs/security-context-backend.xml tiedosto ~/oph-configuration/ hakemistoon

3. Käynnistä OhjausparametritTomcat

4. Testaa esim. http://localhost:9093/ohjausparametrit-service/api/v1/rest/parametri/ALL (palauttaa tyhjän listan)

5. test/test on basic auth käyttäjätunnus esim POST metodeihin.

## Teknologiat

- Spring
- Flyway
- PostgreSql
- http://activiti.org/, bpm työkalu. Enää käytössä vain "Tarjonnan julkaisun takaraja" päivämäärän (PH_TJT) takaisinkutsussa.

## Sekalaista ##

mvn -Dflyway.user=oph -Dflyway.password=oph -P flyway flyway:init

mvn -Dflyway.user=oph -Dflyway.password=oph -P flyway flyway:migrate

mvn -Dlog4j.configuration=file://`pwd`/src/test/resources/log4j.xml  jetty:run
