package net.cacaovisualclient.mod.config;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Config {

    public static final int VERSION = 2;

    private int version;
    private String currentProfile;
    private String selectedTheme;
    private String discordApplicationId;

    public Config() {
        setDefaultValues();
    }

    public void setDefaultValues() {
        this.version = VERSION;
        this.currentProfile = "Default";
        this.selectedTheme = "TUBE";
        this.discordApplicationId = "";
    }
}
