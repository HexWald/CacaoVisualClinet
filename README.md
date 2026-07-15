# CacaoVisualClient

CacaoVisualClient is a Fabric client for Minecraft 1.21.11 focused on PvP visuals, BedWars tools, and settings that can be changed in-game.

It started as a CoralMod fork. The client is moving toward its own Cacao style now: darker UI, small animated feedback, and fewer giant overlays fighting for the screen.

Maintained by [HexWald](https://github.com/HexWald).

## What it does

- keeps PvP info visible without covering half of the screen
- adds small visual feedback for hits, low HP, BedWars events, and round results
- lets you edit HUD elements, crosshair, viewmodel, and first-person sword animations
- saves different setups through profiles
- keeps normal setup inside the UI instead of hiding it behind chat commands

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

### BedWars

- `AutoGG` with custom patterns and server-specific triggers.
- `BedWars Alerts` for bed breaks, final kills, victory, and defeat.
- compact Cacao toasts instead of huge center-screen messages.

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

## Current work

The next pass is mostly about making the client easier to use during a real match:

- better Mods screen navigation
- cleaner HUD editing
- BedWars tools that are useful without being loud
- profile sharing
- crosshair and viewmodel presets that do not get in the way of PvP

If a feature looks good in a screenshot but feels bad in a fight, it does not belong enabled by default.
