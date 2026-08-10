package com.lunkoashtail.avaliproject.screen.custom;

import com.lunkoashtail.avaliproject.network.ExpieHugPayload;
import com.lunkoashtail.avaliproject.network.ExpieOpenTradePayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.network.PacketDistributor;

public class ExpieInteractionScreen extends Screen {
    private static final float REFERENCE_WIDTH = 400f;
    private static final float REFERENCE_HEIGHT = 320f;
    private static final float MIN_SCALE = 0.5f;
    private static final float MAX_SCALE = 2.0f;

    private final int entityId;

    public ExpieInteractionScreen(int entityId) {
        super(Component.translatable("screen.avaliproject.expie_interaction"));
        this.entityId = entityId;
    }

    private float scaleFactor() {
        var window = Minecraft.getInstance().getWindow();
        float scale = Math.min(window.getGuiScaledWidth() / REFERENCE_WIDTH, window.getGuiScaledHeight() / REFERENCE_HEIGHT);
        return Mth.clamp(scale, MIN_SCALE, MAX_SCALE);
    }

    @Override
    protected void renderMenuBackground(GuiGraphics guiGraphics) {
    }

    @Override
    protected void renderBlurredBackground(float partialTick) {
    }

    @Override
    protected void init() {
        super.init();

        float scale = scaleFactor();
        int virtualWidth = (int) (width / scale);
        int virtualHeight = (int) (height / scale);

        int buttonWidth = 90;
        int buttonHeight = 20;
        int spacing = 6;
        int totalWidth = 2 * buttonWidth + spacing;
        int startX = (virtualWidth - totalWidth) / 2;
        int y = virtualHeight - buttonHeight - 16;

        addRenderableWidget(Button.builder(Component.translatable("screen.avaliproject.expie_interaction.hug"),
                        b -> PacketDistributor.sendToServer(new ExpieHugPayload(entityId)))
                .bounds(startX, y, buttonWidth, buttonHeight).build());

        addRenderableWidget(Button.builder(Component.translatable("screen.avaliproject.expie_interaction.trade"),
                        b -> PacketDistributor.sendToServer(new ExpieOpenTradePayload(entityId)))
                .bounds(startX + buttonWidth + spacing, y, buttonWidth, buttonHeight).build());
    }

    @Override
    public void render(GuiGraphics gfx, int mouseX, int mouseY, float partialTick) {
        renderBackground(gfx, mouseX, mouseY, partialTick);
        float scale = scaleFactor();
        int scaledMouseX = (int) (mouseX / scale);
        int scaledMouseY = (int) (mouseY / scale);
        int virtualWidth = (int) (width / scale);
        int virtualHeight = (int) (height / scale);

        gfx.pose().pushPose();
        gfx.pose().scale(scale, scale, 1.0f);

        super.render(gfx, scaledMouseX, scaledMouseY, partialTick);

        gfx.drawCenteredString(font, title, virtualWidth / 2, virtualHeight / 2 - 40, 0xFFFFFFFF);

        gfx.pose().popPose();
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
    public boolean isPauseScreen() {
        return false;
    }
}
