package net.cacaovisualclient.mod.module;

import net.cacaovisualclient.mod.module.modules.*;

import java.util.*;

public class ModuleManager {

    private final Map<Class<? extends Module>, Module> modules = new LinkedHashMap<>();

    public ModuleManager() {
        register(new ArmorHudModule());
        register(new AspectModule());
        register(new AutoGGModule());
        register(new ClockModule());
        register(new CoordinatesModule());
        register(new CPSModule());
        register(new DiscordPresenceModule());
        register(new ScoreboardModule());
        register(new FpsModule());
        register(new FullBrightModule());
        register(new NametagsModule());
        register(new NoBackgroundModule());
        register(new NoChatIndicatorsModule());
        register(new PingModule());
        register(new ServerAddressModule());
        register(new ViewTweaksModule());
        register(new ZoomModule());
    }

    private void register(Module module) {
        modules.put(module.getClass(), module);
    }

    public <T extends Module> T getModule(Class<T> moduleClass) {
        return moduleClass.cast(modules.get(moduleClass));
    }

    public Module getModule(String name) {
        return modules.values().stream()
                .filter(module -> module.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    public Collection<Module> getModules() {
        return modules.values();
    }

    public List<HudModule> getHudModules() {
        return modules.values().stream()
                .filter(module -> module instanceof HudModule)
                .map(module -> (HudModule) module)
                .toList();
    }

    public List<Module> getEnabledModules() {
        return modules.values().stream()
                .filter(Module::isEnabled)
                .toList();
    }
}
