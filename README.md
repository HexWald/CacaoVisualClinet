# CacaoVisualClient

Fabric mod for Minecraft 1.21.11 with a movable HUD, zoom, crosshair settings and BedWars alerts.

## Install

Use Java 21, Fabric Loader 0.18.2 or newer and Fabric API for 1.21.11. Download the JAR from a successful build and put it in `mods`. [Builds](https://github.com/HexWald/CacaoVisualClinet/actions/workflows/build.yml)

## In game

Open the Mods screen with **Right Shift**. Search for a module, click its row to toggle it and open **Settings** to change its options.

| Key | Action |
| --- | --- |
| K | Edit the HUD |
| C | Hold to zoom, scroll to change magnification |
| V | Inspect the held sword |

Enable Zoom or Sword Inspect in Mods to use its key. Bindings are in Minecraft's Controls screen. If the HUD editor still uses P after updating, reset its binding to switch to K.

In the HUD editor, drag an element to move it or scroll over it to resize it. Select it to move by one pixel with the arrow keys, or five with Shift. Right click restores its position.

Profiles store module settings and HUD positions in `cacaovisualclient/profiles`. Create a profile from the current setup, then use **Load** to switch.

## Build

Run `./gradlew build` on Linux or macOS, or `gradlew.bat build` on Windows. The mod JAR is in `build/libs`.

## Credits

Based on [CoralMod by jirmjahu](https://github.com/jirmjahu/coralmod). CC0 1.0.
