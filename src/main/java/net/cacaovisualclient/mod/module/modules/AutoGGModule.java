package net.cacaovisualclient.mod.module.modules;

import lombok.RequiredArgsConstructor;
import net.cacaovisualclient.mod.feature.autogg.AutoGGPatternStorage;
import net.cacaovisualclient.mod.event.AddChatMessageEvent;
import net.cacaovisualclient.mod.module.Module;
import net.cacaovisualclient.mod.module.ModuleInfo;
import net.cacaovisualclient.mod.module.settings.BooleanSetting;
import net.cacaovisualclient.mod.utils.ChatUtils;
import net.cacaovisualclient.mod.utils.MessageUtils;
import net.cacaovisualclient.mod.utils.ServerUtils;
import net.cacaovisualclient.mod.utils.TimeDelay;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

import java.util.List;
import java.util.Locale;

@ModuleInfo(name = "AutoGG", description = "Automatically sends GG at the end of a round")
public class AutoGGModule extends Module {

    private static final long SEND_COOLDOWN_MS = 5000L;

    private final BooleanSetting gomme = new BooleanSetting("GommeHD", true);
    private final BooleanSetting hypixel = new BooleanSetting("Hypixel", true);
    private final BooleanSetting cytooxien = new BooleanSetting("Cytooxien", true);
    private final BooleanSetting customPatterns = new BooleanSetting("Custom Patterns", true);
    private final BooleanSetting caseSensitive = new BooleanSetting("Case Sensitive", false);

    private final AutoGGPatternStorage customStorage = new AutoGGPatternStorage();

    private final List<ServerConfig> servers = List.of(
            new ServerConfig(gomme, List.of("gommehd.net"), List.of(
                    "-= Statistiken dieser Runde =-",
                    "-= Statistics of this game =-"
            )),
            new ServerConfig(hypixel, List.of("hypixel.net"), List.of(
                    "1st Killer -",
                    "Winner:",
                    "Winning Team",
                    "won the game!",
                    "Top Survivors",
                    "Your Overall Winstreak:"
            )),
            new ServerConfig(cytooxien, List.of("cytooxien.de", "cytooxien.net"), List.of(
                    "Statistiken dieser Runde",
                    "Statistics of the game"
            ))
    );

    private final TimeDelay delay = new TimeDelay();
    private boolean shouldSend = false;
    private long lastSentAt = 0L;

    public AutoGGModule() {
        addSettings(gomme, hypixel, cytooxien, customPatterns, caseSensitive);

        AddChatMessageEvent.ADD_CHAT_MESSAGE_EVENT.register(message -> {
            if (!isEnabled() || ServerUtils.getCurrentServerIp() == null) {
                return;
            }

            if (shouldSendGG(message)) {
                shouldSend = true;
                delay.reset();
            }
        });

        ClientTickEvents.START_CLIENT_TICK.register(mc -> {
            if (!isEnabled()) {
                shouldSend = false;
                return;
            }

            if (shouldSend && delay.hasPassed(customStorage.getConfig().getDelayMs())) {
                ChatUtils.sendAsPlayer(customStorage.getConfig().getMessage());
                lastSentAt = System.currentTimeMillis();
                shouldSend = false;
            }
        });
    }

    public AutoGGPatternStorage getCustomStorage() {
        return customStorage;
    }

    private boolean shouldSendGG(String message) {
        final String currentIp = ServerUtils.getCurrentServerIp();
        if (currentIp == null) {
            return false;
        }

        if (System.currentTimeMillis() - lastSentAt < SEND_COOLDOWN_MS) {
            return false;
        }

        final boolean builtInMatch = servers.stream()
                .filter(ServerConfig::isEnabled)
                .anyMatch(s -> s.matchesServer(currentIp) && s.matchesMessage(message));

        if (builtInMatch) {
            return true;
        }

        return customPatterns.getValue()
                && customStorage.getPatterns().stream()
                .anyMatch(pattern -> pattern.matches(currentIp, message, caseSensitive.getValue()));
    }

    @RequiredArgsConstructor
    private class ServerConfig {

        private final BooleanSetting setting;
        private final List<String> ips;
        private final List<String> triggers;

        public boolean isEnabled() {
            return setting.getValue();
        }

        public boolean matchesServer(String currentIp) {
            final String normalizedIp = currentIp.toLowerCase(Locale.ROOT);
            return ips.stream().anyMatch(ip -> normalizedIp.contains(ip.toLowerCase(Locale.ROOT)));
        }

        public boolean matchesMessage(String message) {
            final String cleanMessage = MessageUtils.clean(message);

            if (caseSensitive.getValue()) {
                return triggers.stream().anyMatch(cleanMessage::contains);
            }

            final String normalizedMessage = cleanMessage.toLowerCase(Locale.ROOT);
            return triggers.stream()
                    .map(trigger -> trigger.toLowerCase(Locale.ROOT))
                    .anyMatch(normalizedMessage::contains);
        }
    }
}
