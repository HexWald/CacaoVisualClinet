package net.coralmod.mod.utils;

import com.mojang.blaze3d.platform.NativeImage;
import net.coralmod.mod.mixin.accessors.MinecraftServerAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.FaviconTexture;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.protocol.status.ServerStatus;
import net.minecraft.resources.Identifier;

import java.util.Optional;

public final class ServerUtils {

    private static final Identifier MISSING_LOCATION = Identifier.withDefaultNamespace("textures/misc/unknown_server.png");

    private ServerUtils() {
    }

    public static String getCurrentServerIp() {
        final Minecraft mc = Minecraft.getInstance();
        if (mc.getCurrentServer() == null) {
            return null;
        }
        return mc.getCurrentServer().ip.toLowerCase();
    }

    public static Identifier getServerIcon() {
        final Minecraft mc = Minecraft.getInstance();

        if (mc.isSingleplayer()) {
            if (mc.getSingleplayerServer() == null) {
                return MISSING_LOCATION;
            }

            final Optional<ServerStatus.Favicon> favicon = mc.getSingleplayerServer().getStatus().favicon();
            if (favicon.isEmpty()) {
                return MISSING_LOCATION;
            }

            // Get the same image the select world screen would display
            final FaviconTexture icon = FaviconTexture.forWorld(mc.getTextureManager(), ((MinecraftServerAccessor) mc.getSingleplayerServer()).getStorageSource().getLevelId());

            return uploadIcon(icon, favicon.get().iconBytes());
        }

        final ServerData server = mc.getCurrentServer();
        if (server == null) {
            return MISSING_LOCATION;
        }

        final FaviconTexture icon = FaviconTexture.forServer(mc.getTextureManager(), server.ip);

        return uploadIcon(icon, server.getIconBytes());
    }

    private static Identifier uploadIcon(FaviconTexture icon, byte[] iconBytes) {
        try (NativeImage image = NativeImage.read(iconBytes)) {
            icon.upload(image);
            return icon.textureLocation();
        } catch (Exception e) {
            return MISSING_LOCATION;
        }
    }
}
