package com.lunkoashtail.avaliproject.screen.custom;

import com.lunkoashtail.avaliproject.carry.CarryCandidateEntry;
import com.lunkoashtail.avaliproject.network.CarryRequestPayload;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;

public class CarrySelectionScreen extends Screen {

    private final List<CarryCandidateEntry> candidates;

    public CarrySelectionScreen(List<CarryCandidateEntry> candidates) {
        super(Component.translatable("screen.avaliproject.carry_selection"));
        this.candidates = candidates;
    }

    @Override
    protected void init() {
        super.init();
        int centerX = width / 2;
        int y = height / 2 - (candidates.size() * 22) / 2;

        if (candidates.isEmpty()) return;

        for (CarryCandidateEntry entry : candidates) {
            String kind = entry.isPlayer() ? "" : " (Expie)";
            String label = String.format("%s%s — %.1fm", entry.name(), kind, entry.distance());
            addRenderableWidget(Button.builder(Component.literal(label), b -> {
                        PacketDistributor.sendToServer(new CarryRequestPayload(entry.entityId()));
                        onClose();
                    })
                    .bounds(centerX - 100, y, 200, 20).build());
            y += 22;
        }
    }

    @Override
    public void render(GuiGraphics gfx, int mouseX, int mouseY, float partialTick) {
        renderBackground(gfx, mouseX, mouseY, partialTick);
        super.render(gfx, mouseX, mouseY, partialTick);
        gfx.drawCenteredString(font, title, width / 2, height / 2 - (candidates.size() * 22) / 2 - 20, 0xFFFFFFFF);
        if (candidates.isEmpty()) {
            gfx.drawCenteredString(font,
                    Component.translatable("screen.avaliproject.carry_selection.empty").getString(),
                    width / 2, height / 2, 0xFFAAAAAA);
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
