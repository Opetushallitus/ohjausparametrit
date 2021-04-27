#!/bin/bash

export PGUSER=oph
export PGPASSWORD=oph

#psql --host localhost --port 5489 --no-password --dbname ohjausparametrit -c "select * from parameter;"
psql --host localhost --port 5489 --no-password --dbname ohjausparametrit -c "delete from parameter;"
psql --host localhost --port 5489 --no-password --dbname ohjausparametrit -f test_data.sql