package com.lunkoashtail.avaliproject.screen.custom;

import com.lunkoashtail.avaliproject.network.AvaliRecruitProposalAcceptPayload;
import com.lunkoashtail.avaliproject.network.AvaliRecruitProposalDeclinePayload;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;

public class AvaliRecruitProposalScreen extends Screen {
    private final int entityId;

    public AvaliRecruitProposalScreen(int entityId) {
        super(Component.translatable("screen.avaliproject.avali_recruit_proposal"));
        this.entityId = entityId;
    }

    @Override
    protected void init() {
        super.init();
        int centerX = width / 2;
        int y = height / 2 + 10;

        addRenderableWidget(Button.builder(Component.translatable("screen.avaliproject.avali_recruit_proposal.accept"),
                        b -> {
                            PacketDistributor.sendToServer(new AvaliRecruitProposalAcceptPayload(entityId));
                            onClose();
                        })
                .bounds(centerX - 105, y, 100, 20).build());

        addRenderableWidget(Button.builder(Component.translatable("screen.avaliproject.avali_recruit_proposal.decline"),
                        b -> {
                            PacketDistributor.sendToServer(new AvaliRecruitProposalDeclinePayload(entityId));
                            onClose();
                        })
                .bounds(centerX + 5, y, 100, 20).build());
    }

    @Override
    public void render(GuiGraphics gfx, int mouseX, int mouseY, float partialTick) {
        renderBackground(gfx, mouseX, mouseY, partialTick);
        super.render(gfx, mouseX, mouseY, partialTick);
        gfx.drawCenteredString(font, title, width / 2, height / 2 - 20, 0xFFFFFFFF);
    }

    @Override
    protected void renderBlurredBackground(float partialTick) {
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
