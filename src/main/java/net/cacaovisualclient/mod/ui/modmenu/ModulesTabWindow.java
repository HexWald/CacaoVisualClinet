package net.cacaovisualclient.mod.ui.modmenu;

import net.cacaovisualclient.mod.CacaoVisualClient;
import net.cacaovisualclient.mod.ui.Window;

public class ModulesTabWindow extends Window {

    public ModulesTabWindow(ModMenuScreen parent, String title, int x, int y) {
        super(parent, title, x, y);
    }

    @Override
    public void init() {
        GridUtil.layoutGrid(
                CacaoVisualClient.getInstance().getModuleManager().getModules(),
                x,
                y,
                (module, pos) -> {
                    addWidget(new ModuleButtonWidget(
                            module,
                            pos.getX(), pos.getY(),
                            pos.getWidth(), pos.getHeight()
                    ));
                }
        );

        super.init();
    }
}
