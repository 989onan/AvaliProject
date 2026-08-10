package com.lunkoashtail.avaliproject.screen.custom;

import com.lunkoashtail.avaliproject.network.AvaliSocializeInteractionPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.network.PacketDistributor;

public class AvaliSocializeScreen extends Screen {
    private static final float REFERENCE_WIDTH = 400f;
    private static final float REFERENCE_HEIGHT = 320f;
    private static final float MIN_SCALE = 0.5f;
    private static final float MAX_SCALE = 2.0f;

    private final int entityId;

    private static final String[] LABEL_KEYS = {
            "talk", "gossip", "be_rude", "flirt", "play", "joke"
    };
    private static final int[] ACTIONS = {
            AvaliSocializeInteractionPayload.ACTION_TALK,
            AvaliSocializeInteractionPayload.ACTION_GOSSIP,
            AvaliSocializeInteractionPayload.ACTION_BE_RUDE,
            AvaliSocializeInteractionPayload.ACTION_FLIRT,
            AvaliSocializeInteractionPayload.ACTION_PLAY,
            AvaliSocializeInteractionPayload.ACTION_JOKE
    };

    public AvaliSocializeScreen(int entityId) {
        super(Component.translatable("screen.avaliproject.avali_socialize"));
        this.entityId = entityId;
    }

    private float scaleFactor() {
        var window = Minecraft.getInstance().getWindow();
        float scale = Math.min(window.getGuiScaledWidth() / REFERENCE_WIDTH, window.getGuiScaledHeight() / REFERENCE_HEIGHT);
        return Mth.clamp(scale, MIN_SCALE, MAX_SCALE);
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
        int count = LABEL_KEYS.length;
        int totalWidth = count * buttonWidth + (count - 1) * spacing;
        int startX = (virtualWidth - totalWidth) / 2;
        int y = virtualHeight - buttonHeight - 16;

        for (int i = 0; i < count; i++) {
            int action = ACTIONS[i];
            int x = startX + i * (buttonWidth + spacing);
            addRenderableWidget(Button.builder(Component.translatable("screen.avaliproject.avali_socialize." + LABEL_KEYS[i]),
                            b -> {
                                PacketDistributor.sendToServer(new AvaliSocializeInteractionPayload(entityId, action));
                                onClose();
                            })
                    .bounds(x, y, buttonWidth, buttonHeight).build());
        }
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
        gfx.drawCenteredString(font, title, virtualWidth / 2, virtualHeight / 2 - 60, 0xFFFFFFFF);

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
    protected void renderMenuBackground(GuiGraphics guiGraphics) {
    }

    @Override
    protected void renderBlurredBackground(float partialTick) {
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
