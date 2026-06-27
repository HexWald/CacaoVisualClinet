package net.cacaovisualclient.mod.feature.autogg;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.cacaovisualclient.mod.utils.MessageUtils;

import java.util.Locale;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

@Getter
@Setter
@NoArgsConstructor
public class AutoGGPattern {

    private String name;
    private String server;
    private String trigger;
    private String mode = AutoGGMatchMode.CONTAINS.name();
    private boolean enabled = true;

    public AutoGGPattern(String name, String server, String trigger, AutoGGMatchMode mode) {
        this.name = sanitize(name);
        this.server = sanitize(server);
        this.trigger = sanitize(trigger);
        this.mode = mode.name();
    }

    public boolean isValid() {
        return name != null && !name.isBlank()
                && server != null && !server.isBlank()
                && trigger != null && !trigger.isBlank();
    }

    public boolean matches(String currentServer, String chatMessage) {
        return matches(currentServer, chatMessage, false);
    }

    public boolean matches(String currentServer, String chatMessage, boolean caseSensitive) {
        if (!enabled || !isValid() || currentServer == null) {
            return false;
        }

        if (!currentServer.toLowerCase(Locale.ROOT).contains(server.toLowerCase(Locale.ROOT))) {
            return false;
        }

        final String cleanMessage = MessageUtils.clean(chatMessage);
        final AutoGGMatchMode matchMode = AutoGGMatchMode.fromString(mode);

        return switch (matchMode) {
            case CONTAINS -> matchesText(cleanMessage, caseSensitive);
            case REGEX -> matchesRegex(cleanMessage, caseSensitive);
        };
    }

    private boolean matchesText(String cleanMessage, boolean caseSensitive) {
        if (caseSensitive) {
            return cleanMessage.contains(trigger);
        }

        return cleanMessage.toLowerCase(Locale.ROOT).contains(trigger.toLowerCase(Locale.ROOT));
    }

    private boolean matchesRegex(String cleanMessage, boolean caseSensitive) {
        try {
            final int flags = caseSensitive ? 0 : Pattern.CASE_INSENSITIVE;
            return Pattern.compile(trigger, flags).matcher(cleanMessage).find();
        } catch (PatternSyntaxException ignored) {
            return false;
        }
    }

    private String sanitize(String value) {
        return value == null ? null : value.trim();
    }
}
