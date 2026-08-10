package com.lunkoashtail.avaliproject.screen.custom;

import com.lunkoashtail.avaliproject.network.AvaliOpenEquipPayload;
import com.lunkoashtail.avaliproject.network.AvaliOpenTradePayload;
import com.lunkoashtail.avaliproject.network.AvaliSocializeInteractionPayload;
import com.lunkoashtail.avaliproject.network.AvaliTrustSyncPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

public class AvaliInteractionScreen extends Screen {
    private static final float REFERENCE_WIDTH = 400f;
    private static final float REFERENCE_HEIGHT = 320f;
    private static final float MIN_SCALE = 0.5f;
    private static final float MAX_SCALE = 2.0f;

    @Nullable
    private static AvaliInteractionScreen activeInstance;

    private final int entityId;
    private final boolean tamed;
    private final boolean ownedByViewer;

    private int trust = -1;
    private int recruitCost = -1;

    private Button recruitButton;
    private Button equipButton;

    public AvaliInteractionScreen(int entityId, boolean tamed, boolean ownedByViewer) {
        super(Component.translatable("screen.avaliproject.avali_interaction"));
        this.entityId = entityId;
        this.tamed = tamed;
        this.ownedByViewer = ownedByViewer;
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
    protected void init() {
        super.init();
        activeInstance = this;

        float scale = scaleFactor();
        int virtualWidth = (int) (width / scale);
        int virtualHeight = (int) (height / scale);

        int buttonWidth = 90;
        int buttonHeight = 20;
        int spacing = 6;
        int count = 5;
        int totalWidth = count * buttonWidth + (count - 1) * spacing;
        int startX = (virtualWidth - totalWidth) / 2;
        int y = virtualHeight - buttonHeight - 16;

        int x = startX;
        addRenderableWidget(Button.builder(Component.translatable("screen.avaliproject.avali_interaction.socialize"),
                        b -> Minecraft.getInstance().setScreen(new AvaliSocializeScreen(entityId)))
                .bounds(x, y, buttonWidth, buttonHeight).build());
        x += buttonWidth + spacing;

        addRenderableWidget(Button.builder(Component.translatable("screen.avaliproject.avali_interaction.trade"),
                        b -> PacketDistributor.sendToServer(new AvaliOpenTradePayload(entityId)))
                .bounds(x, y, buttonWidth, buttonHeight).build());
        x += buttonWidth + spacing;

        recruitButton = Button.builder(Component.translatable("screen.avaliproject.avali_interaction.recruit"),
                        b -> Minecraft.getInstance().setScreen(new AvaliRecruitConfirmScreen(entityId, recruitCost)))
                .bounds(x, y, buttonWidth, buttonHeight).build();
        recruitButton.active = !tamed;
        addRenderableWidget(recruitButton);
        x += buttonWidth + spacing;

        equipButton = Button.builder(Component.translatable("screen.avaliproject.avali_interaction.equip"),
                        b -> PacketDistributor.sendToServer(new AvaliOpenEquipPayload(entityId)))
                .bounds(x, y, buttonWidth, buttonHeight).build();
        equipButton.active = ownedByViewer;
        addRenderableWidget(equipButton);
        x += buttonWidth + spacing;

        addRenderableWidget(Button.builder(Component.translatable("screen.avaliproject.avali_interaction.hug"),
                        b -> PacketDistributor.sendToServer(new AvaliSocializeInteractionPayload(entityId, AvaliSocializeInteractionPayload.ACTION_HUG)))
                .bounds(x, y, buttonWidth, buttonHeight).build());
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

        String status = trust < 0
                ? Component.translatable("screen.avaliproject.avali_interaction.loading").getString()
                : Component.translatable("screen.avaliproject.avali_interaction.status", trust, recruitCost).getString();
        gfx.drawCenteredString(font, status, virtualWidth / 2, virtualHeight / 2 - 40, 0xFFAAAAAA);

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
    protected void renderBlurredBackground(float partialTick) {
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    public static void onTrustSync(AvaliTrustSyncPayload payload) {
        if (activeInstance == null || activeInstance.entityId != payload.entityId())
            return;
        activeInstance.trust = payload.trust();
        activeInstance.recruitCost = payload.recruitCost();
        if (activeInstance.recruitButton != null)
            activeInstance.recruitButton.active = !payload.tamed();
        if (activeInstance.equipButton != null)
            activeInstance.equipButton.active = payload.ownedByViewer();
    }

    @Override
    public void removed() {
        if (activeInstance == this) {
            activeInstance = null;
        }
        super.removed();
    }
}
