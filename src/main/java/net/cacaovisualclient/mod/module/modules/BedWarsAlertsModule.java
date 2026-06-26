package net.cacaovisualclient.mod.module.modules;

import net.cacaovisualclient.mod.feature.bedwars.BedWarsMessageParser;
import net.cacaovisualclient.mod.feature.bedwars.BedWarsNotice;
import net.cacaovisualclient.mod.feature.bedwars.BedWarsNoticeType;
import net.cacaovisualclient.mod.event.AddChatMessageEvent;
import net.cacaovisualclient.mod.event.TitleTextEvent;
import net.cacaovisualclient.mod.module.Module;
import net.cacaovisualclient.mod.module.ModuleInfo;
import net.cacaovisualclient.mod.module.settings.BooleanSetting;
import net.cacaovisualclient.mod.utils.Notification;

import java.util.Optional;

@ModuleInfo(
        name = "BedWars Alerts",
        description = "Shows compact BedWars toasts for beds, final kills, and match results"
)
public class BedWarsAlertsModule extends Module {

    private static final long DEDUPE_WINDOW_NANOS = 1_250_000_000L;

    private final BedWarsMessageParser parser = new BedWarsMessageParser();

    private final BooleanSetting bedDestroyed = new BooleanSetting("Bed destroyed", true);
    private final BooleanSetting finalKills = new BooleanSetting("Final kills", true);
    private final BooleanSetting matchResults = new BooleanSetting("Victory and defeat", true);

    private String lastDedupeKey = "";
    private long lastNoticeAtNanos;

    public BedWarsAlertsModule() {
        addSettings(bedDestroyed, finalKills, matchResults);
        setEnabled(true);

        AddChatMessageEvent.ADD_CHAT_MESSAGE_EVENT.register(this::handleMessage);
        TitleTextEvent.TITLE_TEXT_EVENT.register(this::handleMessage);
    }

    private void handleMessage(String message) {
        if (!isEnabled() || mc.player == null) {
            return;
        }

        final Optional<BedWarsNotice> notice = parser.parse(message, mc.player.getScoreboardName());
        if (notice.isEmpty() || !isNoticeEnabled(notice.get())) {
            return;
        }

        if (isDuplicate(notice.get())) {
            return;
        }

        Notification.send(notice.get().title(), notice.get().message(), notice.get().toastType());
    }

    private boolean isNoticeEnabled(BedWarsNotice notice) {
        return switch (notice.noticeType()) {
            case BED -> bedDestroyed.getValue();
            case FINAL_KILL -> finalKills.getValue();
            case MATCH_RESULT -> matchResults.getValue();
        };
    }

    private boolean isDuplicate(BedWarsNotice notice) {
        final long now = System.nanoTime();
        final boolean duplicate = notice.dedupeKey().equals(lastDedupeKey)
                && now - lastNoticeAtNanos < DEDUPE_WINDOW_NANOS;

        lastDedupeKey = notice.dedupeKey();
        lastNoticeAtNanos = now;

        return duplicate;
    }
}
