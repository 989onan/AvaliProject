package com.lunkoashtail.avaliproject.screen.custom;

import com.lunkoashtail.avaliproject.component.BloodContents;
import com.lunkoashtail.avaliproject.network.DrawBloodPayload;
import com.lunkoashtail.avaliproject.sound.ModSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;

public class BloodDrawScreen extends Screen {

    private static final float DRAG_FOR_FULL = 90f;

    private float drawnAmount = 0f;
    private final float maxDrawMl;

    public BloodDrawScreen() {
        super(Component.translatable("screen.avaliproject.blood_draw"));

        Player player = Minecraft.getInstance().player;
        float safeByHealth = player != null
                ? Math.max(0f, (player.getHealth() - 1f) / 15f * BloodContents.CAPACITY_ML)
                : 0f;
        this.maxDrawMl = Math.min(BloodContents.CAPACITY_ML, safeByHealth);
    }

    @Override
    public void renderBackground(GuiGraphics gfx, int mx, int my, float partial) {
    }

    @Override
    public void render(GuiGraphics gfx, int mx, int my, float partial) {
        gfx.fill(0, 0, width, height, 0xCC000000);

        int cx = width / 2, cy = height / 2;

        gfx.drawCenteredString(font, "Drawing blood", cx, cy - 70, 0xFFDDDDFF);
        gfx.drawCenteredString(font, "Hold RIGHT-click and move mouse to adjust", cx, cy - 58, 0xAABBCC);

        int vialW = 20, vialH = 50;
        int vx = cx - vialW / 2, vy = cy - 10;
        float frac = maxDrawMl <= 0f ? 0f : drawnAmount / maxDrawMl;
        gfx.fill(vx, vy, vx + vialW, vy + vialH, 0x33BBBBBB);
        int fluidH = (int) (vialH * frac);
        gfx.fill(vx + 2, vy + (vialH - fluidH), vx + vialW - 2, vy + vialH - 2, 0xCCBB2222);
        gfx.fill(vx, vy, vx + vialW, vy + 1, 0xFFDDDDDD);
        gfx.fill(vx, vy + vialH - 1, vx + vialW, vy + vialH, 0xFFDDDDDD);

        int barW = 160, barH = 10;
        int barX = cx - barW / 2, barY = cy + 60;
        gfx.fill(barX - 1, barY - 1, barX + barW + 1, barY + barH + 1, 0xFF111111);
        gfx.fill(barX, barY, barX + barW, barY + barH, 0xFF1C1C2A);
        gfx.fill(barX, barY, barX + (int) (barW * Math.min(1f, frac)), barY + barH, 0xFFBB4444);

        float hearts = drawnAmount / BloodContents.CAPACITY_ML * 7.5f;
        gfx.drawCenteredString(font, (int) drawnAmount + " / " + (int) maxDrawMl + " mL  (" + String.format("%.1f", hearts) + " hearts)",
                cx, barY + barH + 6, 0x99BBEE);
        gfx.drawCenteredString(font, "[Hold RIGHT-click to adjust  |  Left-click to confirm  |  Esc to cancel]", cx, height - 18, 0x666666);

        super.render(gfx, mx, my, partial);
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        if (button == 0) {
            commitDraw();
            return true;
        }
        if (button == 1) {
            return true;
        }
        return super.mouseClicked(mx, my, button);
    }

    @Override
    public boolean mouseDragged(double mx, double my, int button, double dx, double dy) {
        if (button != 1) {
            return super.mouseDragged(mx, my, button, dx, dy);
        }
        drawnAmount = Math.max(0f, Math.min(maxDrawMl, drawnAmount - (float) (dy / DRAG_FOR_FULL) * maxDrawMl));
        return true;
    }

    private void commitDraw() {
        if (drawnAmount > 0.5f) {
            PacketDistributor.sendToServer(new DrawBloodPayload(drawnAmount));
            playDrawSound();
        }
        onClose();
    }

    private void playDrawSound() {
        Player p = minecraft.player;
        if (p == null) return;
        p.level().playLocalSound(p.getX(), p.getY(), p.getZ(),
                ModSounds.SYRINGE_STAB.get(), SoundSource.PLAYERS, 0.5f, 0.8f, false);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) { onClose(); return true; }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean isPauseScreen() { return false; }
}
