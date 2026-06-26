package net.cacaovisualclient.mod.feature.bedwars;

import net.cacaovisualclient.mod.ui.toast.CacaoToastType;
import net.cacaovisualclient.mod.utils.MessageUtils;

import java.util.Locale;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BedWarsMessageParser {

    private static final Pattern TEAM_BED_PATTERN = Pattern.compile(
            "(?i)\\b(red|blue|green|yellow|aqua|white|pink|gray|grey) bed (?:was )?(?:destroyed|broken)(?: by ([^!]+))?"
    );

    public Optional<BedWarsNotice> parse(String rawMessage, String playerName) {
        final String message = MessageUtils.clean(rawMessage);
        final String lower = message.toLowerCase(Locale.ROOT);

        if (message.isBlank()) {
            return Optional.empty();
        }

        if (isVictory(lower)) {
            return Optional.of(new BedWarsNotice(
                    BedWarsNoticeType.MATCH_RESULT,
                    CacaoToastType.SUCCESS,
                    "Victory",
                    "Match won",
                    "result:victory"
            ));
        }

        if (isDefeat(lower)) {
            return Optional.of(new BedWarsNotice(
                    BedWarsNoticeType.MATCH_RESULT,
                    CacaoToastType.DANGER,
                    "Defeat",
                    "Match lost",
                    "result:defeat"
            ));
        }

        if (lower.contains("final kill")) {
            return parseFinalKill(message, playerName);
        }

        if (lower.contains("bed") && (lower.contains("destroyed") || lower.contains("broken"))) {
            return parseBedDestroyed(message, playerName);
        }

        return Optional.empty();
    }

    private Optional<BedWarsNotice> parseBedDestroyed(String message, String playerName) {
        final String lower = message.toLowerCase(Locale.ROOT);
        final boolean ownBed = lower.contains("your bed");

        if (ownBed) {
            return Optional.of(new BedWarsNotice(
                    BedWarsNoticeType.BED,
                    CacaoToastType.DANGER,
                    "Your bed is gone",
                    "Play safe now",
                    "bed:own"
            ));
        }

        final Matcher matcher = TEAM_BED_PATTERN.matcher(message);
        if (!matcher.find()) {
            return Optional.of(new BedWarsNotice(
                    BedWarsNoticeType.BED,
                    CacaoToastType.WARNING,
                    "Bed destroyed",
                    trimNoise(message),
                    "bed:" + lower
            ));
        }

        final String team = normalizeTeamName(matcher.group(1));
        final String breaker = matcher.group(2) == null ? "" : matcher.group(2).trim();
        final boolean playerBrokeBed = MessageUtils.mentionsPlayerAsActor(message, playerName);

        if (playerBrokeBed) {
            return Optional.of(new BedWarsNotice(
                    BedWarsNoticeType.BED,
                    CacaoToastType.SUCCESS,
                    "Bed broken",
                    "You destroyed " + team + " bed",
                    "bed:" + team.toLowerCase(Locale.ROOT)
            ));
        }

        final String details = breaker.isBlank()
                ? team + " bed destroyed"
                : team + " bed by " + breaker;

        return Optional.of(new BedWarsNotice(
                BedWarsNoticeType.BED,
                CacaoToastType.WARNING,
                "Bed destroyed",
                details,
                "bed:" + team.toLowerCase(Locale.ROOT)
        ));
    }

    private Optional<BedWarsNotice> parseFinalKill(String message, String playerName) {
        final boolean playerKilled = MessageUtils.mentionsPlayerAsActor(message, playerName);
        final String cleaned = trimNoise(message.replaceAll("(?i)final kill!?", ""));

        return Optional.of(new BedWarsNotice(
                BedWarsNoticeType.FINAL_KILL,
                playerKilled ? CacaoToastType.SUCCESS : CacaoToastType.WARNING,
                playerKilled ? "Final kill" : "Final kill nearby",
                playerKilled ? "You eliminated a player" : cleaned,
                "final:" + MessageUtils.clean(message).toLowerCase(Locale.ROOT)
        ));
    }

    private static boolean isVictory(String lower) {
        return lower.equals("victory!")
                || lower.equals("victory")
                || lower.contains("you won");
    }

    private static boolean isDefeat(String lower) {
        return lower.equals("defeat!")
                || lower.equals("defeat")
                || lower.contains("game over")
                || lower.contains("you lost");
    }

    private static String normalizeTeamName(String rawTeam) {
        if (rawTeam == null || rawTeam.isBlank()) {
            return "Unknown";
        }

        final String lower = rawTeam.toLowerCase(Locale.ROOT);
        final String normalized = lower.equals("grey") ? "gray" : lower;
        return Character.toUpperCase(normalized.charAt(0)) + normalized.substring(1);
    }

    private static String trimNoise(String message) {
        String cleaned = message
                .replaceAll("(?i)^bed destruction\\s*>\\s*", "")
                .replaceAll("(?i)^bed destroyed\\s*>\\s*", "")
                .replaceAll("\\s+", " ")
                .trim();

        if (cleaned.length() > 54) {
            cleaned = cleaned.substring(0, 51).trim() + "...";
        }

        return cleaned;
    }
}
