package net.cacaovisualclient.mod.module.modules;

import net.cacaovisualclient.mod.module.HudModule;
import net.cacaovisualclient.mod.module.ModuleInfo;
import net.cacaovisualclient.mod.module.settings.BooleanSetting;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@ModuleInfo(name = "Clock", description = "Displays the current time")
public class ClockModule extends HudModule {

    private final BooleanSetting use24HourFormat = new BooleanSetting("24-Hour Format", true);
    private final BooleanSetting showSeconds = new BooleanSetting("Show Seconds", true);

    public ClockModule() {
        super(20, 20);
        addSettings(use24HourFormat, showSeconds);
    }

    @Override
    public String getText() {
        final LocalDateTime now = LocalDateTime.now(ZoneId.systemDefault());
        String pattern;

        if (use24HourFormat.getValue()) {
            pattern = showSeconds.getValue() ? "HH:mm:ss" : "HH:mm";
        } else {
            pattern = showSeconds.getValue() ? "hh:mm:ss a" : "hh:mm a";
        }

        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return now.format(formatter);
    }
}
