package net.cacaovisualclient.mod.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface AddChatMessageEvent {

    void onChatMessage(String message);

    Event<AddChatMessageEvent> ADD_CHAT_MESSAGE_EVENT = EventFactory.createArrayBacked(AddChatMessageEvent.class, callbacks -> message -> {
        for (AddChatMessageEvent event : callbacks) {
            event.onChatMessage(message);
        }
    });
}
