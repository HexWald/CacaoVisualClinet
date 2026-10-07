package net.cacaovisualclient.mod.config.profile;

import java.util.regex.Pattern;

public final class ProfileNames {

    private static final Pattern RESERVED_NAME = Pattern.compile(
            "(?:CON|PRN|AUX|NUL|CONIN\\$|CONOUT\\$|COM[1-9¹²³]|LPT[1-9¹²³])(?:\\..*)?",
            Pattern.CASE_INSENSITIVE
    );

    private ProfileNames() {
    }

    public static boolean isValid(String name) {
        if (name == null || name.isBlank() || !name.equals(name.trim()) || name.endsWith(".")) {
            return false;
        }

        for (int i = 0; i < name.length(); i++) {
            char character = name.charAt(i);
            if (Character.isISOControl(character) || "\\/:*?\"<>|".indexOf(character) >= 0) {
                return false;
            }
        }

        return !RESERVED_NAME.matcher(name).matches();
    }
}
