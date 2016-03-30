# Ohjausparametrit #

Ohjausparametripalvelussa pidetään järjestelmän toimintaa ylläpitäviä ja ohjaavia parametreja.

Palvelu tarjoaa REST-rajapinnan. 

Yksi keskeinen käyttökohde on haun aikatauluasetukset.

## Teknologiat

- Spring
- Flyway
- PostgreSql
- http://activiti.org/, bpm työkalu. Enää käytössä vain "Tarjonnan julkaisun takaraja" päivämäärän (PH_TJT) takaisinkutsussa.

## Sekalaista ##

mvn -Dflyway.user=oph -Dflyway.password=oph -P flyway flyway:init

mvn -Dflyway.user=oph -Dflyway.password=oph -P flyway flyway:migrate

mvn -Dlog4j.configuration=file://`pwd`/src/test/resources/log4j.xml  jetty:run
