package net.cacaovisualclient.mod;

import lombok.Getter;
import net.cacaovisualclient.mod.command.CacaoVisualClientCommand;
import net.cacaovisualclient.mod.config.Config;
import net.cacaovisualclient.mod.config.ConfigManager;
import net.cacaovisualclient.mod.config.ConfigStorage;
import net.cacaovisualclient.mod.config.profile.ProfileManager;
import net.cacaovisualclient.mod.config.profile.ProfileStorage;
import net.cacaovisualclient.mod.event.KeyPressedEvent;
import net.cacaovisualclient.mod.module.ModuleManager;
import net.cacaovisualclient.mod.theme.Theme;
import net.cacaovisualclient.mod.ui.editor.EditHudScreen;
import net.cacaovisualclient.mod.ui.modmenu.ModMenuScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Getter
public class CacaoVisualClient implements ClientModInitializer {

    public static final String MOD_ID = "cacaovisualclient";
    public static final String MOD_NAME = "CacaoVisualClient";

    public static final String MOD_VERSION = FabricLoader.getInstance()
            .getModContainer(MOD_ID)
            .map(container -> container.getMetadata().getVersion().getFriendlyString())
            .orElse("unknown");

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static final KeyMapping.Category KEY_CATEGORY = KeyMapping.Category.register(
            Identifier.fromNamespaceAndPath(MOD_ID, "controls")
    );

    public static final KeyMapping ZOOM_KEY_MAPPING = KeyBindingHelper.registerKeyBinding(
            new KeyMapping(
                    "key.cacaovisualclient.zoom",
                    GLFW.GLFW_KEY_C,
                    KEY_CATEGORY
            )
    );

    public static final KeyMapping MODMENU_KEY_MAPPING = KeyBindingHelper.registerKeyBinding(
            new KeyMapping(
                    "key.cacaovisualclient.modmenu",
                    GLFW.GLFW_KEY_RIGHT_SHIFT,
                    KEY_CATEGORY
            )
    );

    public static final KeyMapping HUD_EDITOR_KEY_MAPPING = KeyBindingHelper.registerKeyBinding(
            new KeyMapping(
                    "key.cacaovisualclient.hud_editor",
                    GLFW.GLFW_KEY_P,
                    KEY_CATEGORY
            )
    );

    public static final KeyMapping INSPECT_KEY_MAPPING = KeyBindingHelper.registerKeyBinding(
            new KeyMapping(
                    "key.cacaovisualclient.inspect",
                    GLFW.GLFW_KEY_V,
                    KEY_CATEGORY
            )
    );

    @Getter
    private static CacaoVisualClient instance;

    private ModuleManager moduleManager;
    private ConfigStorage configStorage;
    private ConfigManager configManager;
    private ProfileStorage profileStorage;
    private ProfileManager profileManager;
    private Theme selectedTheme;

    @Override
    public void onInitializeClient() {
        LOGGER.info("Starting {} v{}...", MOD_NAME, MOD_VERSION);

        instance = this;

        ClientLifecycleEvents.CLIENT_STARTED.register(mc -> {
            moduleManager = new ModuleManager();
            configStorage = new ConfigStorage();
            configManager = new ConfigManager(configStorage);
            profileStorage = new ProfileStorage(moduleManager);
            profileManager = new ProfileManager(getConfig(), profileStorage, moduleManager);

            final String configuredTheme = getConfig().getSelectedTheme();

            final Theme theme = Theme.fromConfigValue(configuredTheme).orElseGet(() -> {
                LOGGER.warn("Unknown theme '{}', falling back to {}", configuredTheme, Theme.CACAO);
                return Theme.CACAO;
            });

            setSelectedTheme(theme);

            new CacaoVisualClientCommand();

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

            if (profileManager != null && configManager != null) {
                save();
            }
        }));
    }

    public void save() {
        profileManager.saveCurrentProfile();
        configManager.save();
    }

    public Config getConfig() {
        return configManager.getConfig();
    }

    public void setSelectedTheme(Theme selectedTheme) {
        this.selectedTheme = selectedTheme;
        getConfig().setSelectedTheme(selectedTheme.toString());
    }
}
