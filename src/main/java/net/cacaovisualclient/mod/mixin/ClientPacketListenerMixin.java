package net.cacaovisualclient.mod.mixin;

import net.cacaovisualclient.mod.CacaoVisualClient;
import net.cacaovisualclient.mod.module.modules.CustomCrosshairModule;
import net.cacaovisualclient.mod.module.modules.HitmarkerModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundDamageEventPacket;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {
    @Inject(method = "handleDamageEvent", at = @At("TAIL"))
    private void onDamage(ClientboundDamageEventPacket packet, CallbackInfo info) {
        Minecraft client = Minecraft.getInstance();
        if (client.player == null || client.level == null
                || packet.sourceCauseId() != client.player.getId() || packet.entityId() == client.player.getId()
                || !(client.level.getEntity(packet.entityId()) instanceof LivingEntity)) {
            return;
        }
        HitmarkerModule marker = CacaoVisualClient.getModule(HitmarkerModule.class);
        if (marker != null) marker.onHit();
        CustomCrosshairModule crosshair = CacaoVisualClient.getModule(CustomCrosshairModule.class);
        if (crosshair != null) crosshair.onHit();
    }
}
