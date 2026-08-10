package com.lunkoashtail.avaliproject.screen.custom;

import com.lunkoashtail.avaliproject.network.AvaliRecruitPayload;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;

public class AvaliRecruitConfirmScreen extends Screen {
    private final int entityId;
    private final int cost;

    public AvaliRecruitConfirmScreen(int entityId, int cost) {
        super(Component.translatable("screen.avaliproject.avali_recruit_confirm"));
        this.entityId = entityId;
        this.cost = cost;
    }

    @Override
    protected void init() {
        super.init();
        int centerX = width / 2;
        int y = height / 2 + 10;

        addRenderableWidget(Button.builder(Component.translatable("screen.avaliproject.avali_recruit_confirm.accept"),
                        b -> {
                            PacketDistributor.sendToServer(new AvaliRecruitPayload(entityId));
                            onClose();
                        })
                .bounds(centerX - 105, y, 100, 20).build());

        addRenderableWidget(Button.builder(Component.translatable("screen.avaliproject.avali_recruit_confirm.cancel"),
                        b -> onClose())
                .bounds(centerX + 5, y, 100, 20).build());
    }

    @Override
    public void render(GuiGraphics gfx, int mouseX, int mouseY, float partialTick) {
        renderBackground(gfx, mouseX, mouseY, partialTick);
        super.render(gfx, mouseX, mouseY, partialTick);
        gfx.drawCenteredString(font, title, width / 2, height / 2 - 30, 0xFFFFFFFF);
        String costLine = cost >= 0
                ? Component.translatable("screen.avaliproject.avali_recruit_confirm.cost", cost).getString()
                : "";
        gfx.drawCenteredString(font, costLine, width / 2, height / 2 - 10, 0xFF55FFFF);
    }

    @Override
    protected void renderBlurredBackground(float partialTick) {
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
