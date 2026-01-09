# Gatling terheléses tesztelés

A modul a rendszer terheléses tesztelését végzi Gatling segítségével.

## Futtatás

A teszt futtatásához a következő parancsot használd a projekt gyökérkönyvtárából:

```bash
./mvnw clean gatling:test -pl gatling
```

Ez a parancs lefuttatja a `training.IdentityPostSimulation` szimulációt (amely alapértelmezettként be van állítva a `pom.xml`-ben).

A jelentések (HTML report) a futtatás után a `gatling/target/gatling` könyvtárban generálódnak.

## Tapasztalatok

Azon gépen amin futtatam a terhelés határ kb 2750 párhuzamos szál körül billeget.

Csináltam performancia teszteket 17-es java fordítással 17-es java futtatással valamint ugynezeket a teszteket 21-es java-val is.

Az a tapasztalat, hogy 17-es java esetén nagy terhelésnél nagyobb volt az áteresztése, 10-20%-kal tudott többet átereszetni.
21-es java esetén csökkent az előny a virtual trhead-ek miatt, de még mindig maradr egy kicsi, többszöri futtatás után pár százalékkal gyorsabb volt.

## Jövőbeni teljesítmény teszt ötletek.

Ki kéne próbálni mind 17-es, mind 21-es java-val native image-el, hogy akkor hogyan változik a teljesítmény.