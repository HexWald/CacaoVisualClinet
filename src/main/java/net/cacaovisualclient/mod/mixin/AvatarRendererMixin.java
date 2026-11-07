package net.cacaovisualclient.mod.mixin;

import net.cacaovisualclient.mod.CacaoVisualClient;
import net.cacaovisualclient.mod.module.modules.NametagsModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.scores.Team;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AvatarRenderer.class)
public class AvatarRendererMixin {

    @Inject(method = "shouldShowName(Lnet/minecraft/world/entity/Avatar;D)Z", at = @At("HEAD"), cancellable = true)
    private void showOwnNametag(Avatar avatar, double distance, CallbackInfoReturnable<Boolean> info) {
        Minecraft client = Minecraft.getInstance();
        if (avatar != client.player || client.getCameraEntity() != avatar
                || client.options.getCameraType().isFirstPerson() || !Minecraft.renderNames() || avatar.isInvisible()) {
            return;
        }

        NametagsModule module = CacaoVisualClient.getModule(NametagsModule.class);
        if (module == null || !module.showsOwnName()) {
            return;
        }

        Team team = avatar.getTeam();
        if (team == null || team.getNameTagVisibility() == Team.Visibility.ALWAYS
                || team.getNameTagVisibility() == Team.Visibility.HIDE_FOR_OTHER_TEAMS) {
            info.setReturnValue(true);
        }
    }
}
