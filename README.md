# Alex's Egg Timer (Forge 1.20.1)

Huevos de Alex's Mobs (cocodrilo, caimán, ornitorrinco, terrapin, triops) con **tiempo de eclosión real**
y visibles en **Jade**: `Eclosiona en: 4:32`.

## Releases automáticos
Cada push a `main` compila en GitHub Actions (descarga Alex's Mobs y Jade desde Modrinth) y publica un **Release** con el `.jar`. Descárgalo en la pestaña *Releases*.

## Cómo compilar a mano
1. Copia a `libs/` (renómbralos EXACTAMENTE así):
   - `alexsmobs-1.0.jar`  (tu jar de Alex's Mobs 1.20.1)
   - `jade-1.0.jar`       (tu jar de Jade 11.x para Forge 1.20.1)
2. `./gradlew build`  → el jar queda en `build/libs/alexs_egg_timer-1.0.0.jar`
3. Mételo en `mods/` junto con Alex's Mobs, Citadel y (opcional) Jade. Instálalo en servidor Y cliente.

## Cómo funciona
- Mixin a `BlockReptileEgg` y `BlockTerrapinEgg`: se cancela el random tick original; un temporizador propio
  (guardado por dimensión, solo avanza con el chunk cargado) ejecuta el randomTick original de forma forzada cada etapa.
  Así se conservan sonidos, grietas, spawn, tame, etc.
- Mixin a `FrogspawnBlock.onPlace` (solo `BlockTriopsEggs`): delay fijo y conocido.
- El servidor envía cada segundo los tiempos de los huevos a <64 bloques; el cliente los interpola y Jade los muestra.

## Config (`config/alexs_egg_timer-common.toml`)
`enabled`, `reptileEggHatchTicks` (9000), `terrapinEggHatchTicks` (9000), `triopsEggHatchTicks` (7800). 20 ticks = 1 s.

## Notas
- Huevos puestos antes de instalar el addon muestran "calculando..." hasta su primer random tick (~1 min).
- Sin hábitat (arena / bloque válido debajo) el huevo se pausa, como en el original.
