
mvn -Dflyway.user=oph -Dflyway.password=oph -P flyway flyway:init

mvn -Dflyway.user=oph -Dflyway.password=oph -P flyway flyway:migrate

mvn -Dlog4j.configuration=file://`pwd`/src/test/resources/log4j.xml  jetty:run
