package net.coralmod.mod.config;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Config {

    public static final int VERSION = 1;

    private int version;
    private String currentProfile;
    private String selectedTheme;

    public Config() {
        setDefaultValues();
    }

    public void setDefaultValues() {
        this.version = VERSION;
        this.currentProfile = "Default";
        this.selectedTheme = "TUBE";
    }
}
