package net.cacaovisualclient.mod.ui.modmenu;

import net.cacaovisualclient.mod.CacaoVisualClient;
import net.cacaovisualclient.mod.module.Module;
import net.cacaovisualclient.mod.ui.Window;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

import java.util.Comparator;
import java.util.Locale;

public class ModulesTabWindow extends Window {
    private static final int ROW_HEIGHT = 24;

    public ModulesTabWindow(ModMenuScreen parent, String title, int x, int y) {
        super(parent, title, x, y);
    }

    @Override
    public void init() {
        EditBox search = new EditBox(Minecraft.getInstance().font, x + 10, y + 6,
                width - 20, 18, Component.literal("Search modules"));
        search.setHint(Component.literal("Search modules"));
        search.setValue(parent.getModuleQuery());
        search.setResponder(query -> {
            parent.setModuleQuery(query);
            updateRows(query);
        });
        parent.showSearch(search);
        updateRows(search.getValue());
    }

    private void updateRows(String query) {
        String filter = query.strip().toLowerCase(Locale.ROOT);
        var matches = CacaoVisualClient.getInstance().getModuleManager().getModules().stream()
                .filter(module -> module.getName().toLowerCase(Locale.ROOT).contains(filter)
                        || module.getDescription().toLowerCase(Locale.ROOT).contains(filter))
                .sorted(Comparator.comparing(Module::getName, String.CASE_INSENSITIVE_ORDER))
                .toList();
        widgets.clear();
        scrollOffset = 0;
        int rowY = getContentTop();
        for (Module module : matches) {
            addWidget(new ModuleButtonWidget(module, x + 10, rowY, width - 20, ROW_HEIGHT));
            rowY += ROW_HEIGHT + 4;
        }
        recalculateScroll();
    }

    @Override
    protected int getContentTop() {
        return y + 34;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY) {
        super.render(graphics, mouseX, mouseY);
        if (widgets.isEmpty()) {
            graphics.drawString(Minecraft.getInstance().font, "No matching modules",
                    x + 12, getContentTop() + 5, 0xFFAAAAAA, false);
        }
    }
}
