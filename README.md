# CacaoVisualClient

CacaoVisualClient is a small Fabric client for Minecraft 1.21.11 focused on PvP comfort, clean visuals, and BedWars quality-of-life.

It started as a fork of CoralMod, but the goal is different now: make a lightweight client that feels nice to use every match without turning the game into a mess of noisy overlays.

Maintained by [HexWald](https://github.com/HexWald).

## What it does

- keeps useful PvP info visible without covering half of the screen
- adds small visual feedback for hits, low HP, BedWars events, and round results
- lets you tweak HUD elements, crosshair, viewmodel, and first-person sword animations
- saves different setups through profiles
- keeps the UI simple enough to change settings in-game

## Installation

1. Install Fabric Loader for Minecraft 1.21.11.
2. Install Fabric API.
3. Download the latest `.jar` from releases.
4. Put the `.jar` into your `mods` folder.
5. Launch Minecraft with the Fabric profile.

## Main features

### PvP visuals

- `Custom Crosshair` with styles, size, gap, outline, center dot, and hit animation preview.
- `Hitmarker` with animated marker styles, sounds, and kill confirmation.
- `Low HP Effect` with a soft vignette and warning toast.
- `Viewmodel Editor` for first-person item position, rotation, and scale.
- `Sword Inspect` with CS-style inspect animations on `V`.

### BedWars polish

- `AutoGG` with custom patterns and server-specific triggers.
- `BedWars Alerts` for bed breaks, final kills, victory, and defeat.
- compact Cacao toasts instead of huge center-screen noise.

### HUD and utility

- FPS, ping, CPS, coordinates, armor, clock, server address.
- HUD editor with drag, scaling, snap guides, and quick reset.
- profiles for switching between different client setups.
- themes, including Cacao and the original coral-style themes.

## Default controls

| Key | Action |
| --- | --- |
| Right Shift | Open CacaoVisualClient menu |
| P | Open HUD editor |
| C | Zoom |
| V | Sword inspect |

## Current focus

The next big step is making the client feel more like a serious all-in-one PvP client:

- better mod menu navigation
- cleaner HUD editing
- stronger BedWars tools
- profile sharing
- more polished crosshair and viewmodel presets

No promises about becoming a giant client overnight. The plan is to keep it fast, readable, and actually useful.
