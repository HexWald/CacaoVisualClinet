package net.cacaovisualclient.mod.utils;

import lombok.experimental.UtilityClass;

import java.util.Locale;
import java.util.regex.Pattern;

@UtilityClass
public class MessageUtils {

    private static final Pattern COLOR_CODES = Pattern.compile("(?i)\\u00A7[0-9A-FK-OR]");
    private static final Pattern RANK_PREFIX = Pattern.compile("(?:\\[[^\\]]+\\]\\s*)*");

    public String clean(String message) {
        if (message == null) {
            return "";
        }

        return COLOR_CODES.matcher(message)
                .replaceAll("")
                .replace('\u00BB', '>')
                .replaceAll("\\s+", " ")
                .trim();
    }

    public String lowerClean(String message) {
        return clean(message).toLowerCase(Locale.ROOT);
    }

    public boolean mentionsPlayerAsActor(String message, String playerName) {
        if (playerName == null || playerName.isBlank()) {
            return false;
        }

        final String cleanMessage = clean(message);
        final String safePlayerName = Pattern.quote(playerName);
        final Pattern actorPattern = Pattern.compile(
                "(?i)\\b(?:by|fighting|to)\\s+" + RANK_PREFIX.pattern() + safePlayerName + "\\b"
        );

        return actorPattern.matcher(cleanMessage).find()
                || cleanMessage.toLowerCase(Locale.ROOT).contains("you killed")
                || cleanMessage.toLowerCase(Locale.ROOT).contains("you eliminated");
    }
}
