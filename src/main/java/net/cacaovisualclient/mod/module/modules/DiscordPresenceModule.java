package net.cacaovisualclient.mod.module.modules;

import net.cacaovisualclient.mod.CacaoVisualClient;
import net.cacaovisualclient.mod.integration.discord.DiscordRichPresenceService;
import net.cacaovisualclient.mod.module.Module;
import net.cacaovisualclient.mod.module.ModuleInfo;
import net.cacaovisualclient.mod.module.settings.BooleanSetting;
import net.cacaovisualclient.mod.utils.Notification;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;

@ModuleInfo(
        name = "DiscordPresence",
        description = "Shows CacaoVisualClient as your Discord activity"
)
public class DiscordPresenceModule extends Module {

    private static final int UPDATE_INTERVAL_TICKS = 100;

    private final BooleanSetting showServerAddress = new BooleanSetting("Show server address", false);
    private final DiscordRichPresenceService service = new DiscordRichPresenceService();

    private int ticksUntilUpdate;
    private boolean missingApplicationIdNotified;

    public DiscordPresenceModule() {
        addSettings(showServerAddress);

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!isEnabled()) {
                return;
            }

            if (ticksUntilUpdate-- > 0) {
                return;
            }

            ticksUntilUpdate = UPDATE_INTERVAL_TICKS;
            updatePresence(client);
        });
    }

    @Override
    public void onEnable() {
        ticksUntilUpdate = 0;
        updatePresence(mc);
    }

    @Override
    public void onDisable() {
        service.close();
        missingApplicationIdNotified = false;
    }

    private void updatePresence(Minecraft client) {
        final String applicationId = CacaoVisualClient.getInstance().getConfig().getDiscordApplicationId().trim();
        if (applicationId.isEmpty()) {
            notifyMissingApplicationId();
            return;
        }

        if (!service.start(applicationId)) {
            notifyMissingApplicationId();
            return;
        }

        service.update(
                "Minecraft " + SharedConstants.getCurrentVersion().name(),
                getActivityState(client)
        );
    }

    private String getActivityState(Minecraft client) {
        if (client.level == null) {
            return "Main Menu";
        }

        if (client.isSingleplayer()) {
            return "Singleplayer";
        }

        final ServerData server = client.getCurrentServer();
        if (showServerAddress.getValue() && server != null) {
            return "Playing on " + server.ip;
        }

        return "Multiplayer";
    }

    private void notifyMissingApplicationId() {
        if (missingApplicationIdNotified) {
            return;
        }

        Notification.sendNotification(
                "Discord Presence",
                "Set discordApplicationId in cacaovisualclient/config.json"
        );
        missingApplicationIdNotified = true;
    }
}
