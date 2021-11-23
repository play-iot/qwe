param(
    $SLEEP_TIME = 3,
    $DOCKER_ADDRESS = 'localhost'
)

$pg_dir = 'build/db/pg'
$pg_img = 'postgres:10-alpine'

New-Item $pg_dir -ItemType Directory -ea 0
Copy-Item -Recurse -Force -Path sql/src/test/resources/pg_schema.sql -Destination $pg_dir

docker pull $pg_img

try
{
    docker rm -f postgres-gen postgres-temp
}
catch
{
    #slient
}

try
{
    docker volume rm -f postgres-gen-db
}
catch
{

}

docker volume create --name postgres-gen-db
docker container create --name postgres-temp -v postgres-gen-db:/data hello-world
docker cp "$( Get-Location )/$pg_dir/." postgres-temp:/data
docker rm -f postgres-temp

docker run -d --name postgres-gen -p 5423:5432 `
    -v postgres-gen-db:"/docker-entrypoint-initdb.d" `
    -e "POSTGRES_PASSWORD=123" `
    -e "POSTGRES_DB=testdb" `
    $pg_img postgres

sleep "$SLEEP_TIME"
gradle clean generateJooq -PpgHost="$DOCKER_ADDRESS"

docker rm -f postgres-gen
