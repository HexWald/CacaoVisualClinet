# Manual test checklist

Use this before cutting a CacaoVisualClient jar. Do not trust a UI change until it has been opened in-game at least once.

## Menu and settings

- Open the client menu with Right Shift.
- Switch between Mods, Themes, Profiles and About.
- Open settings for every enabled visual module.
- Change a setting, close the screen, open it again, and check that the value stayed.
- Restart the game once and check that the same config is loaded.

## HUD editor

- Move a few HUD elements around.
- Change scale for at least one small element and one wide element.
- Check that reset puts elements back somewhere visible.
- Make sure nothing blocks chat, scoreboard or hotbar in normal gameplay.

## PvP visuals

- Test custom crosshair preview.
- Test hit animation from settings.
- Hit an entity in-game and check that the animation is centered.
- Check low HP effect at low health and after healing.

## Release sanity

- Build a fresh jar.
- Start the game with only Fabric API and CacaoVisualClient.
- Join a local world and one multiplayer server.
- Write release notes from the tested list, not from memory.
