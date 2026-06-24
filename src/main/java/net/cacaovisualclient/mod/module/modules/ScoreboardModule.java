package net.cacaovisualclient.mod.module.modules;

import lombok.Getter;
import net.cacaovisualclient.mod.module.Module;
import net.cacaovisualclient.mod.module.ModuleInfo;
import net.cacaovisualclient.mod.module.settings.BooleanSetting;

@Getter
@ModuleInfo(name = "Scoreboard", description = "Customize your scoreboard")
public class ScoreboardModule extends Module {

    private final BooleanSetting enableScoreboard = new BooleanSetting("Enable Scoreboard", true);
    private final BooleanSetting numbers = new BooleanSetting("Numbers", false);
    private final BooleanSetting background = new BooleanSetting("Background", true);
    private final BooleanSetting titleBackground = new BooleanSetting("Title Background", true);
    private final BooleanSetting textShadow = new BooleanSetting("Text Shadow", false);

    public ScoreboardModule() {
        addSettings(enableScoreboard, numbers, background, titleBackground, textShadow);
    }
}
