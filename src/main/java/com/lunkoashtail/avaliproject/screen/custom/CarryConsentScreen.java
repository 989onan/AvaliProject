package com.lunkoashtail.avaliproject.screen.custom;

import com.lunkoashtail.avaliproject.network.CarryConsentAcceptPayload;
import com.lunkoashtail.avaliproject.network.CarryConsentDeclinePayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;

public class CarryConsentScreen extends Screen {

    private final int requesterEntityId;
    private final String requesterName;

    public CarryConsentScreen(int requesterEntityId) {
        super(Component.translatable("screen.avaliproject.carry_consent"));
        this.requesterEntityId = requesterEntityId;

        var level = Minecraft.getInstance().level;
        var entity = level != null ? level.getEntity(requesterEntityId) : null;
        this.requesterName = entity != null ? entity.getName().getString() : "Someone";
    }

    @Override
    protected void init() {
        super.init();
        int centerX = width / 2;
        int y = height / 2 + 10;

        addRenderableWidget(Button.builder(Component.translatable("screen.avaliproject.carry_consent.accept"),
                        b -> {
                            PacketDistributor.sendToServer(new CarryConsentAcceptPayload(requesterEntityId));
                            onClose();
                        })
                .bounds(centerX - 105, y, 100, 20).build());

        addRenderableWidget(Button.builder(Component.translatable("screen.avaliproject.carry_consent.decline"),
                        b -> {
                            PacketDistributor.sendToServer(new CarryConsentDeclinePayload(requesterEntityId));
                            onClose();
                        })
                .bounds(centerX + 5, y, 100, 20).build());
    }

    @Override
    public void render(GuiGraphics gfx, int mouseX, int mouseY, float partialTick) {
        renderBackground(gfx, mouseX, mouseY, partialTick);
        super.render(gfx, mouseX, mouseY, partialTick);
        String message = requesterName + " wants to carry you.";
        gfx.drawCenteredString(font, message, width / 2, height / 2 - 20, 0xFFFFFFFF);
    }

    @Override
    protected void renderBlurredBackground(float partialTick) {
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
