# Ohjausparametrit

Ohjausparametripalvelussa pidetään järjestelmän toimintaa ylläpitäviä ja ohjaavia parametreja.

Palvelu tarjoaa REST-rajapinnan. 

Yksi keskeinen käyttökohde on haun aikatauluasetukset.

[![Ohjausparametrit](https://github.com/Opetushallitus/ohjausparametrit/actions/workflows/build.yml/badge.svg)](https://github.com/Opetushallitus/ohjausparametrit/actions/workflows/build.yml)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=Opetushallitus_ohjausparametrit&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=Opetushallitus_ohjausparametrit)


## Käynnistys lokaalisti

1. Luo ohjausparametreille tyhjä postgre kanta lokaalisti. Esim dockerilla: 
```
docker run --rm --name ohjausparametrit-db -p 5489:5432 -e POSTGRES_USER=oph -e POSTGRES_PASSWORD=oph -e POSTGRES_DB=ohjausparametrit -d postgres:11.5
```
ja lisää halutessasi testidataa tietokantaan: `tools/add_test_data_to_local_db.sh` (koneella pitää olla asennettuna psql)
2. Luo `dev/dev.yml`-tiedosto. Käytä pohjana `dev/dev.yml.template`-tiedostoa. Aseta puuttuvat arvot.

3. Käännä ja paketoi sovellus: `mvn clean package`

### Käynnistys komentoriviltä

4. Käynnistä sovellus:
```
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Dspring.config.additional-location=$(pwd)/dev/dev.yml -Dlogging.config=file://$(pwd)/dev/logback.xml"
```
tai 
```
java -Dspring.config.additional-location=$(pwd)/dev/dev.yml -Dlogging.config=file://$(pwd)/dev/logback.xml -jar target/ohjausparametrit.jar
```

### Käynnistys Intellij IDEA:ssa

4. Ota käyttöön Spring Bootin auto restart ja live reload: https://www.codejava.net/frameworks/spring-boot/spring-boot-auto-restart-and-live-reload-in-intellij-idea

5. Käynnistä Spring Boot -sovellus: `fi.oph.ohjausparametrit.App` (Päätyy virheeseen)

6. *Run* -> *Edit configurations...* -> *Spring Boot* -> *App* -> *VM Options*: `-Dspring.config.additional-location=dev.yml -Dlogging.config=file://logback.xml` (Laita absoluuttiset polut)

7. Käynnistä sovellus uudestaan

## Swagger

Avaa selaimessa: http://localhost:8080/ohjausparametrit-service/swagger-ui/index.html

## Testien ajaminen

`mvn clean test`

## Teknologiat

- Spring Boot
- Flyway
- Postgresql
- Docker

