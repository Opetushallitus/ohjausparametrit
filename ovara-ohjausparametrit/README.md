# ovara-ohjausparametrit #

Erillinen moduuli siirtotiedostojen ajastetulle luomiselle. Main-luokka OvaraApp etsii käynnistyessään
sovelluksen kannasta viimeisimmän onnistuneen siirtotiedostojen muodostuksen aikaikkunan loppuhetken.
Uusi aikaikkuna määritellään operaation alkaessa edellisen onnistuneen aikaikkunan lopusta nykyhetkeen.

Jos muuttuneita tietoja on aikavälillä paljon (kts. konffiarvo max-itemcount-in-file), muodostuu useita tiedostoja.

Muodostetut tiedostot tallennetaan sovellukselle konffattuun s3-ämpäriin seuraavien konffiarvojen perusteella:
suoritusrekisteri.ovara.s3.region
suoritusrekisteri.ovara.s3.bucket
suoritusrekisteri.ovara.s3.target-role-arn

Sovelluksen ajoympäristö kts. cloud-base -> ovara-generic-stack.ts.

## Ajoympäristöjen versiot

- Java 17

## Ajaminen lokaalisti

Sovelluksen lokaali ajaminen vaatii sopivan kannan tai putkituksen sellaiseen, tarvittaessa ovara-ohjausparametrit/dev/ovara-dev.yml kanssa yhteensopivan tyhjän kannan saa pystyyn tähän tapaan:
``docker run --rm --name ohjausparametrit-db -p 5489:5432 -e POSTGRES_USER=oph -e POSTGRES_PASSWORD=oph -e POSTGRES_DB=ohjausparametrit -d postgres:11.5``

Käynnistetään ajamalla OvaraApp-luokka. Tämän voi tehdä joko IDEstä (katso alta tarvittavat konffi- ja profiiliparametrit kuntoon)
tai projektin juuresta suoraan ovara-ohjausparametrien spring boot-jaria suoraan esimerkiksi näin:
``mvn clean install``
``java -Dspring.config.additional-location=ovara-ohjausparametrit/dev/ovara-dev.yml -jar ovara-ohjausparametrit/target/ovara-ohjausparametrit.jar``
