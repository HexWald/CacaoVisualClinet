package net.cacaovisualclient.mod.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.input.MouseButtonInfo;

public interface MouseButtonClickedEvent {

    void onMouseClicked(MouseButtonInfo mouseButtonInfo);

    Event<MouseButtonClickedEvent> MOUSE_BUTTON_CLICKED_EVENT = EventFactory.createArrayBacked(MouseButtonClickedEvent.class, callbacks -> mouseButtonInfo -> {
        for (MouseButtonClickedEvent event : callbacks) {
            event.onMouseClicked(mouseButtonInfo);
        }
    });

}
