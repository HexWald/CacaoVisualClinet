# CacaoVisualClient roadmap

This is not a strict promise list. It is a working note for the next parts of the client that should make it feel less like a random mod pack and more like a clean PvP client.

## Short term

- finish the new Mods screen with search, categories, and favorites
- test profile sharing before shipping it
- clean up HUD editor controls so moving, scaling, locking, and resetting elements feels obvious
- keep BedWars alerts compact and useful instead of turning the screen into a notice board
- add a few good default presets for crosshair and viewmodel settings

## Daily polish queue

Small changes that are worth doing between bigger feature passes:

- add a proper preview block for every visual module that has color or animation settings
- make hit feedback easier to test from settings, without joining a server first
- keep all new features configurable from the client UI, not only through config files
- write short release notes while testing, so the final changelog is based on real checks
- remove anything that looks nice in screenshots but feels annoying during PvP

## Client feel

The client should stay fast and quiet. A good feature here is something you notice when it helps, not something that shouts at you every second.

Things worth keeping:

- small side toasts
- readable HUD elements
- simple in-game settings
- profiles that can be switched quickly
- Cacao theme as the main identity, with coral themes kept as legacy flavor

## Before the next big release

- test the 0.9.0 UI pass in-game
- check old configs still migrate cleanly
- make sure every new setting can be changed from the UI
- build a fresh jar and write release notes from real tested changes

If a feature looks cool but makes PvP worse, it should be disabled by default or cut.
