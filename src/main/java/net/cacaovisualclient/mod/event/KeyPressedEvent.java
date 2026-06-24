package net.cacaovisualclient.mod.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface KeyPressedEvent {

    void onKeyPressed(int key);

    Event<KeyPressedEvent> KEY_PRESSED_EVENT = EventFactory.createArrayBacked(KeyPressedEvent.class, callbacks -> key -> {
        for (KeyPressedEvent event : callbacks) {
            event.onKeyPressed(key);
        }
    });
}
