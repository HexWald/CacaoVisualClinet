# CacaoVisualClient roadmap

Working notes for the next CacaoVisualClient passes. This is closer to a bench checklist than a promise list.

## Short term

- finish the Mods screen search, categories, and favorites
- test profile sharing with old configs before shipping it
- make HUD moving, scaling, locking, and reset controls hard to miss
- keep BedWars alerts short enough to ignore during a fight
- add a small set of crosshair and viewmodel presets that feel safe for PvP

## Small fixes worth doing between feature passes

- add a preview block for visual modules with color or animation settings
- add test buttons for effects that are hard to trigger on demand
- keep new options reachable from the UI, not only from JSON files
- write release notes while testing, not from memory two days later
- cut effects that look fine in screenshots but feel annoying in PvP

## Client feel

The client should stay fast and quiet. The best features here are the ones that help once, then get out of the way.

Things worth keeping:

- small side toasts
- readable HUD text
- simple in-game settings
- profiles that can be switched quickly
- Cacao theme as the main identity, with coral themes kept as legacy flavor

## Before the next big release

- test the current UI pass in-game
- check old configs still migrate cleanly
- make sure every new setting can be changed from the UI
- build a fresh jar and write release notes from what was actually tested

If a feature looks cool but makes PvP worse, it should be disabled by default or cut.
