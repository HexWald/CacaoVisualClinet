package net.cacaovisualclient.mod.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.cacaovisualclient.mod.CacaoVisualClient;
import net.cacaovisualclient.mod.command.sub.ModuleCommand;
import net.cacaovisualclient.mod.command.sub.ResetCommand;
import net.cacaovisualclient.mod.utils.ChatUtils;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

public class CacaoVisualClientCommand {

    public CacaoVisualClientCommand() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(buildCommand("cacaovisualclient"));
            dispatcher.register(buildCommand("cacao"));
        });
    }

    private static LiteralArgumentBuilder<FabricClientCommandSource> buildCommand(String name) {
        return ClientCommandManager.literal(name)
                .executes(ctx -> {
                    ChatUtils.sendToPlayer(CacaoVisualClient.MOD_NAME + " v" + CacaoVisualClient.MOD_VERSION);
                    return 1;
                })
                .then(ModuleCommand.build())
                .then(ResetCommand.build());
    }
}
