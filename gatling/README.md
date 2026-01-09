# Gatling terheléses tesztelés

A modul a rendszer terheléses tesztelését végzi Gatling segítségével.

## Futtatás

A teszt futtatásához a következő parancsot használd a projekt gyökérkönyvtárából:

```bash
./mvnw clean gatling:test -pl gatling
```

Ez a parancs lefuttatja a `training.IdentityPostSimulation` szimulációt (amely alapértelmezettként be van állítva a `pom.xml`-ben).

A jelentések (HTML report) a futtatás után a `gatling/target/gatling` könyvtárban generálódnak.
