#!/usr/bin/env bash

SLEEP_TIME=${1:-3}
pg_dir=build/db/pg
pg_img=postgres:10-alpine

mkdir -p $pg_dir
cp -rf sql/src/test/resources/pg_schema.sql $pg_dir

docker pull $pg_img

docker rm -f postgres-gen

docker run -d --name postgres-gen -p 5423:5432 \
        -v "$(pwd)/$pg_dir":/docker-entrypoint-initdb.d/ \
        -e POSTGRES_PASSWORD=123 -e POSTGRES_DB=testdb \
        $pg_img postgres

sleep "$SLEEP_TIME"
./gradlew clean generateJooq

docker rm -f postgres-gen
