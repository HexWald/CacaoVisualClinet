package net.cacaovisualclient.mod.module.modules;

import lombok.RequiredArgsConstructor;
import net.cacaovisualclient.mod.event.AddChatMessageEvent;
import net.cacaovisualclient.mod.module.Module;
import net.cacaovisualclient.mod.module.ModuleInfo;
import net.cacaovisualclient.mod.module.settings.BooleanSetting;
import net.cacaovisualclient.mod.utils.ChatUtils;
import net.cacaovisualclient.mod.utils.ServerUtils;
import net.cacaovisualclient.mod.utils.TimeDelay;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

import java.util.List;

@ModuleInfo(name = "AutoGG", description = "Automatically sends GG at the end of a round")
public class AutoGGModule extends Module {

    private static final long DELAY_TIME = 1000;

    private final BooleanSetting gomme = new BooleanSetting("GommeHD", true);
    private final BooleanSetting hypixel = new BooleanSetting("Hypixel", true);
    private final BooleanSetting cytooxien = new BooleanSetting("Cytooxien", true);

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

    public AutoGGModule() {
        addSettings(gomme, hypixel, cytooxien);

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
                return;
            }

            if (shouldSend && delay.hasPassed(DELAY_TIME)) {
                ChatUtils.sendAsPlayer("GG");
                shouldSend = false;
            }
        });
    }

    private boolean shouldSendGG(String message) {
        final String currentIp = ServerUtils.getCurrentServerIp();
        if (currentIp == null) {
            return false;
        }

        return servers.stream()
                .filter(ServerConfig::isEnabled)
                .anyMatch(s -> s.matchesServer(currentIp) && s.matchesMessage(message));
    }

    @RequiredArgsConstructor
    private static class ServerConfig {

        private final BooleanSetting setting;
        private final List<String> ips;
        private final List<String> triggers;

        public boolean isEnabled() {
            return setting.getValue();
        }

        public boolean matchesServer(String currentIp) {
            return ips.stream().anyMatch(currentIp::contains);
        }

        public boolean matchesMessage(String message) {
            return triggers.stream().anyMatch(message::contains);
        }
    }
}
