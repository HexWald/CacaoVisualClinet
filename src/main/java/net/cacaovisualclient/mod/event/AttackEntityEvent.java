package net.cacaovisualclient.mod.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public interface AttackEntityEvent {

    void onAttack(Player player, Entity target);

    Event<AttackEntityEvent> ATTACK_ENTITY_EVENT = EventFactory.createArrayBacked(
            AttackEntityEvent.class,
            callbacks -> (player, target) -> {
                for (AttackEntityEvent event : callbacks) {
                    event.onAttack(player, target);
                }
            }
    );
}
