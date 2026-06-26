package net.cacaovisualclient.mod.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface TitleTextEvent {

    void onTitleText(String message);

    Event<TitleTextEvent> TITLE_TEXT_EVENT = EventFactory.createArrayBacked(
            TitleTextEvent.class,
            callbacks -> message -> {
                for (TitleTextEvent event : callbacks) {
                    event.onTitleText(message);
                }
            }
    );
}
