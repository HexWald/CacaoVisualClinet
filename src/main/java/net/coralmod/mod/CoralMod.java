package net.coralmod.mod;

import lombok.Getter;
import net.coralmod.mod.command.CoralModCommand;
import net.coralmod.mod.config.Config;
import net.coralmod.mod.config.ConfigManager;
import net.coralmod.mod.config.ConfigStorage;
import net.coralmod.mod.config.profile.Profile;
import net.coralmod.mod.config.profile.ProfileManager;
import net.coralmod.mod.config.profile.ProfileStorage;
import net.coralmod.mod.event.KeyPressedEvent;
import net.coralmod.mod.module.ModuleManager;
import net.coralmod.mod.theme.Theme;
import net.coralmod.mod.ui.editor.EditHudScreen;
import net.coralmod.mod.ui.modmenu.ModMenuScreen;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Getter
public class CoralMod implements ModInitializer {

    public static final String MOD_ID = "coralmod";
    public static final String MOD_NAME = "CoralMod";
    public static final String MOD_VERSION = "0.7.0";

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static KeyMapping ZOOM_KEY_MAPPING = KeyBindingHelper.registerKeyBinding(new KeyMapping(
            "Zoom",
            GLFW.GLFW_KEY_C,
            KeyMapping.Category.register(Identifier.parse("coralmod.key.zoom"))
    ));

    public static KeyMapping MODMENU_KEY_MAPPING = KeyBindingHelper.registerKeyBinding(new KeyMapping(
            "Mod Menu",
            GLFW.GLFW_KEY_RIGHT_SHIFT,
            KeyMapping.Category.register(Identifier.parse("coralmod.key.modmenu"))
    ));

    public static KeyMapping HUD_EDITOR_KEY_MAPPING = KeyBindingHelper.registerKeyBinding(new KeyMapping(
            "Hud Editor",
            GLFW.GLFW_KEY_P,
            KeyMapping.Category.register(Identifier.parse("coralmod.key.editor"))
    ));

    @Getter
    private static CoralMod instance;

    private ModuleManager moduleManager;
    private ConfigStorage configStorage;
    private ConfigManager configManager;
    private ProfileStorage profileStorage;
    private ProfileManager profileManager;

    private Theme selectedTheme;

    @Override
    public void onInitialize() {
        LOGGER.info("Starting {} v{}...", MOD_NAME, MOD_VERSION);

        instance = this;

        ClientLifecycleEvents.CLIENT_STARTED.register(mc -> {
            moduleManager = new ModuleManager();
            configStorage = new ConfigStorage();
            configManager = new ConfigManager(configStorage);
            profileStorage = new ProfileStorage(moduleManager);
            profileManager = new ProfileManager(getConfig(), configStorage, profileStorage, moduleManager);

            setSelectedTheme(Theme.valueOf(getConfig().getSelectedTheme()));

            new CoralModCommand();

            LOGGER.info("Successfully initialized {}", MOD_NAME);
        });

        KeyPressedEvent.KEY_PRESSED_EVENT.register(key -> {
            if (Minecraft.getInstance().screen != null) {
                return;
            }

            if (key == KeyBindingHelper.getBoundKeyOf(MODMENU_KEY_MAPPING).getValue()) {
                Minecraft.getInstance().setScreen(new ModMenuScreen());
            }

            if (key == KeyBindingHelper.getBoundKeyOf(HUD_EDITOR_KEY_MAPPING).getValue()) {
                Minecraft.getInstance().setScreen(EditHudScreen.INSTANCE);
            }
        });

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            LOGGER.info("Shutting down...");
            LOGGER.info("Saving modules...");

            profileManager.saveCurrentProfile();
            configManager.save();
        }));
    }

    public Config getConfig() {
        return configManager.getConfig();
    }

    public Profile getCurrentProfile() {
        return profileManager.getCurrentProfile();
    }

    public void setSelectedTheme(Theme selectedTheme) {
        this.selectedTheme = selectedTheme;
        getConfig().setSelectedTheme(selectedTheme.toString());
    }
}