package net.cacaovisualclient.mod.command;

import net.cacaovisualclient.mod.CacaoVisualClient;
import net.cacaovisualclient.mod.command.sub.ModuleCommand;
import net.cacaovisualclient.mod.command.sub.ResetCommand;
import net.cacaovisualclient.mod.utils.ChatUtils;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;

public class CacaoVisualClientCommand {

    public CacaoVisualClientCommand() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(
                    ClientCommandManager.literal("cacaovisualclient")
                            .executes(ctx -> {
                                ChatUtils.sendToPlayer(CacaoVisualClient.MOD_NAME + " v" + CacaoVisualClient.MOD_VERSION);
                                return 1;
                            })
                            .then(ModuleCommand.build())
                            .then(ResetCommand.build())
            );
        });
    }
}
