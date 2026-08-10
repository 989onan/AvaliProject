package com.lunkoashtail.avaliproject.screen.custom;

import com.lunkoashtail.avaliproject.AvaliProject;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;

public class AugmentScreen extends AbstractContainerScreen<AugmentMenu> {
    private static final ResourceLocation GUI_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(AvaliProject.MOD_ID, "textures/gui/augment/augment_gui.png");
    private static final float REFERENCE_WIDTH = 400f;
    private static final float REFERENCE_HEIGHT = 320f;
    private static final float MIN_SCALE = 0.5f;
    private static final float MAX_SCALE = 2.0f;

    public AugmentScreen(AugmentMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 192;
        this.imageHeight = 210;
        this.titleLabelY = 10000;
        this.inventoryLabelY = 10000;
    }

    private float scaleFactor() {
        var window = Minecraft.getInstance().getWindow();
        float scale = Math.min(window.getGuiScaledWidth() / REFERENCE_WIDTH, window.getGuiScaledHeight() / REFERENCE_HEIGHT);
        return Mth.clamp(scale, MIN_SCALE, MAX_SCALE);
    }

    @Override
    public void renderTransparentBackground(GuiGraphics guiGraphics) {
    }

    @Override
    protected void init() {
        super.init();
        float scale = scaleFactor();
        this.leftPos = (int) ((width / scale - imageWidth) / 2);
        this.topPos = (int) ((height / scale - imageHeight) / 2);
    }

    @Override
    protected void renderBg(GuiGraphics gfx, float partialTick, int mouseX, int mouseY) {
        gfx.blit(GUI_TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight, imageWidth, imageHeight);
    }

    @Override
    public void render(GuiGraphics gfx, int mouseX, int mouseY, float partialTick) {
        float scale = scaleFactor();
        int scaledMouseX = (int) (mouseX / scale);
        int scaledMouseY = (int) (mouseY / scale);
        gfx.pose().pushPose();
        gfx.pose().scale(scale, scale, 1.0f);
        super.render(gfx, scaledMouseX, scaledMouseY, partialTick);
        renderTooltip(gfx, scaledMouseX, scaledMouseY);

        gfx.drawCenteredString(font, title, leftPos + imageWidth / 2, topPos + 6, 0xFFFFFFFF);

        gfx.pose().popPose();

        renderPlayerModel(gfx, mouseX, mouseY, scale);
    }

    private void renderPlayerModel(GuiGraphics gfx, int mouseX, int mouseY, float scale) {
        Player player = Minecraft.getInstance().player;
        if (player == null)
            return;
        int centerX = Math.round((leftPos + imageWidth / 2f) * scale);
        int centerY = Math.round((topPos + 68) * scale);
        int halfW = Math.round(45 * scale);
        int halfH = Math.round(54 * scale);
        InventoryScreen.renderEntityInInventoryFollowsMouse(
                gfx, centerX - halfW, centerY - halfH, centerX + halfW, centerY + halfH,
                Math.round(30 * scale), 0.0f, (float) mouseX, (float) mouseY, player);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        float scale = scaleFactor();
        return super.mouseClicked(mouseX / scale, mouseY / scale, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        float scale = scaleFactor();
        return super.mouseReleased(mouseX / scale, mouseY / scale, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        float scale = scaleFactor();
        return super.mouseDragged(mouseX / scale, mouseY / scale, button, dragX / scale, dragY / scale);
    }

    @Override
    protected void renderBlurredBackground(float partialTick) {
    }
}
