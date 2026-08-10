package com.lunkoashtail.avaliproject.creativetab;

import com.lunkoashtail.avaliproject.item.ModCreativeModeTabs;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.flag.FeatureFlagSet;
import org.lwjgl.glfw.GLFW;

import java.util.LinkedHashMap;
import java.util.Map;

public class GalaxyCreativeScreen extends CreativeModeInventoryScreen {
    private static final int ICON_SIZE = 16;
    private static final int CELL_GAP = 4;
    private static final int COLUMNS = 2;
    private static final int SIDEBAR_GAP = 8;
    private static final int PANEL_PADDING = 4;
    private static final int PANEL_COLOR = 0xC0101010;
    private static final int SELECTED_HIGHLIGHT_COLOR = 0xA0FFFFFF;

    private static final int SIDEBAR_WIDTH = COLUMNS * ICON_SIZE + (COLUMNS - 1) * CELL_GAP;

    private final Map<GalaxyCategory, ImageButton> categoryButtons = new LinkedHashMap<>();
    private GalaxyCategory category = GalaxyCategory.ALL;

    public GalaxyCreativeScreen(LocalPlayer player, FeatureFlagSet enabledFeatures, boolean displayOperatorCreativeTab) {
        super(player, enabledFeatures, displayOperatorCreativeTab);
    }

    @Override
    protected void init() {
        super.init();

        this.categoryButtons.clear();
        GalaxyCategory[] categories = GalaxyCategory.values();
        for (int i = 0; i < categories.length; i++) {
            GalaxyCategory cat = categories[i];
            int col = i % COLUMNS;
            int row = i / COLUMNS;
            int x = sidebarX() + col * (ICON_SIZE + CELL_GAP);
            int y = this.topPos + row * (ICON_SIZE + CELL_GAP);

            WidgetSprites sprites = new WidgetSprites(cat.icon(), cat.icon());
            ImageButton button = new ImageButton(x, y, ICON_SIZE, ICON_SIZE, sprites, b -> selectCategory(cat), cat.displayName());
            button.setTooltip(Tooltip.create(cat.displayName()));

            this.categoryButtons.put(cat, button);
            this.addRenderableWidget(button);
        }

        applyCategoryFilter();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        boolean showSidebar = isGalaxyTabSelected();

        if (showSidebar && !this.categoryButtons.isEmpty()) {
            int rows = (this.categoryButtons.size() + COLUMNS - 1) / COLUMNS;
            int panelLeft = sidebarX() - PANEL_PADDING;
            int panelTop = this.topPos - PANEL_PADDING;
            int panelRight = sidebarX() + SIDEBAR_WIDTH + PANEL_PADDING;
            int panelBottom = this.topPos + rows * (ICON_SIZE + CELL_GAP) - CELL_GAP + PANEL_PADDING;
            guiGraphics.fill(panelLeft, panelTop, panelRight, panelBottom, PANEL_COLOR);

            ImageButton active = this.categoryButtons.get(this.category);
            if (active != null) {
                guiGraphics.fill(active.getX() - 1, active.getY() - 1, active.getX() + ICON_SIZE + 1, active.getY() + ICON_SIZE + 1, SELECTED_HIGHLIGHT_COLOR);
            }
        }

        for (ImageButton button : this.categoryButtons.values()) {
            button.visible = showSidebar;
            button.active = showSidebar;
        }

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        boolean handled = super.charTyped(codePoint, modifiers);
        narrowToCategory();
        return handled;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        boolean handled = super.keyPressed(keyCode, scanCode, modifiers);
        if (keyCode == GLFW.GLFW_KEY_BACKSPACE || keyCode == GLFW.GLFW_KEY_DELETE) {
            narrowToCategory();
        }
        return handled;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        var tabBeforeRelease = selectedTab;
        boolean handled = super.mouseReleased(mouseX, mouseY, button);
        if (selectedTab != tabBeforeRelease) {
            applyCategoryFilter();
        }
        return handled;
    }

    @Override
    public void resize(Minecraft minecraft, int width, int height) {
        super.resize(minecraft, width, height);
        applyCategoryFilter();
    }

    private void selectCategory(GalaxyCategory category) {
        this.category = category;
        applyCategoryFilter();
    }

    private void applyCategoryFilter() {
        if (!isGalaxyTabSelected()) {
            return;
        }
        this.refreshSearchResults();
        narrowToCategory();
    }

    private void narrowToCategory() {
        if (!isGalaxyTabSelected()) {
            return;
        }
        this.menu.items.removeIf(stack -> !this.category.matches(stack));
        this.menu.scrollTo(0.0F);
    }

    private static boolean isGalaxyTabSelected() {
        return selectedTab == ModCreativeModeTabs.GALAXY_TAB.get();
    }

    private int sidebarX() {
        return this.leftPos - SIDEBAR_WIDTH - SIDEBAR_GAP;
    }
}
