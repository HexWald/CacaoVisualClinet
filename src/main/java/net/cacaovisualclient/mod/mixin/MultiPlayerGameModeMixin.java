package net.cacaovisualclient.mod.mixin;

import net.cacaovisualclient.mod.event.AttackEntityEvent;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MultiPlayerGameMode.class)
public class MultiPlayerGameModeMixin {

    @Inject(method = "attack", at = @At("TAIL"))
    private void onAttack(Player player, Entity target, CallbackInfo info) {
        AttackEntityEvent.ATTACK_ENTITY_EVENT.invoker().onAttack(player, target);
    }
}
