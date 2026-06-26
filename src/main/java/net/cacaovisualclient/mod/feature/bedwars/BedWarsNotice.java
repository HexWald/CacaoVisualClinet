package net.cacaovisualclient.mod.feature.bedwars;

import net.cacaovisualclient.mod.ui.toast.CacaoToastType;

public record BedWarsNotice(
        BedWarsNoticeType noticeType,
        CacaoToastType toastType,
        String title,
        String message,
        String dedupeKey
) {
}
