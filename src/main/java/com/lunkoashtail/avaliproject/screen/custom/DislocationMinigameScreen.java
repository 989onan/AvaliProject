package com.lunkoashtail.avaliproject.screen.custom;

import com.lunkoashtail.avaliproject.limb.Limb;
import com.lunkoashtail.avaliproject.network.ResetDislocationPayload;
import com.lunkoashtail.avaliproject.sound.ModSounds;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Random;

public class DislocationMinigameScreen extends Screen {


    private static final float SOCKET_WANDER_RADIUS = 40f;
    private static final int SOCKET_RETARGET_MIN_TICKS = 35;
    private static final int SOCKET_RETARGET_MAX_TICKS = 80;
    private static final float SOCKET_EASE = 0.045f;

    private static final float ALIGN_RADIUS = 13f;
    private static final int HOLD_TICKS_FOR_SUCCESS = 25;

    private static final float GRAB_RADIUS = 11f;

    private static final float VIOLENT_SPEED = 20f;
    private static final int VIOLENT_PAIN_PENALTY = 3;
    private static final int PANIC_JITTER_TICKS = 12;

    private static final int SOCKET_R = 9;
    private static final int BONE_R   = 8;
    private static final int COL_PANEL  = 0xBB080812;
    private static final int COL_BORDER = 0xFF334466;


    private final Limb limb;

    private int anchorCX, anchorCY;

    private float socketX, socketY;
    private float socketTargetX, socketTargetY;
    private int retargetCooldown;

    private float boneX, boneY;
    private boolean heldBone = false;

    private int holdTicks = 0;
    private int painPenalty = 0;
    private int panicTicks = 0;

    private boolean succeeded = false;
    private int closeCountdown = 0;

    private final Random rng = new Random();
    private float shakeX, shakeY;

    public DislocationMinigameScreen(Limb limb) {
        super(Component.translatable("screen.avaliproject.dislocation_minigame"));
        this.limb = limb;
    }


    @Override
    protected void init() {
        super.init();
        anchorCX = width / 2;
        anchorCY = height / 2 + 5;

        socketX = anchorCX;
        socketY = anchorCY;
        pickNewSocketTarget();
        retargetCooldown = randomRetargetTicks();

        double angle = rng.nextDouble() * Math.PI * 2;
        float dist = 55f + rng.nextFloat() * 15f;
        boneX = anchorCX + (float) (Math.cos(angle) * dist);
        boneY = anchorCY + (float) (Math.sin(angle) * dist);
    }

    private void pickNewSocketTarget() {
        double angle = rng.nextDouble() * Math.PI * 2;
        float dist = rng.nextFloat() * SOCKET_WANDER_RADIUS;
        socketTargetX = anchorCX + (float) (Math.cos(angle) * dist);
        socketTargetY = anchorCY + (float) (Math.sin(angle) * dist);
    }

    private int randomRetargetTicks() {
        return SOCKET_RETARGET_MIN_TICKS + rng.nextInt(SOCKET_RETARGET_MAX_TICKS - SOCKET_RETARGET_MIN_TICKS);
    }


    @Override
    public void renderBackground(GuiGraphics gfx, int mx, int my, float partial) {
    }

    @Override
    public void render(GuiGraphics gfx, int mx, int my, float partial) {
        gfx.fill(0, 0, width, height, 0xCC000000);

        drawStatsPanel(gfx);
        drawLinkLine(gfx);
        drawSocket(gfx);
        drawBone(gfx);
        drawHUD(gfx);

        super.render(gfx, mx, my, partial);
    }

    private void drawStatsPanel(GuiGraphics gfx) {
        int pw = 150, ph = 112;
        int px = 8, py = height / 2 - ph / 2;
        gfx.fill(px, py, px + pw, py + ph, COL_PANEL);
        gfx.fill(px,          py,          px + pw,     py + 1,      COL_BORDER);
        gfx.fill(px,          py + ph - 1, px + pw,     py + ph,     COL_BORDER);
        gfx.fill(px,          py,          px + 1,      py + ph,     COL_BORDER);
        gfx.fill(px + pw - 1, py,          px + pw,     py + ph,     COL_BORDER);
    }

    private void drawLinkLine(GuiGraphics gfx) {
        double dist = Math.hypot(boneX - socketX, boneY - socketY);
        int col;
        if (dist <= ALIGN_RADIUS)        col = 0xFF66DD88;
        else if (dist <= ALIGN_RADIUS * 3) col = 0xFFEEDD66;
        else                              col = 0xFF995555;
        drawLine(gfx, (int) socketX, (int) socketY, (int) boneX, (int) boneY, col);
    }

    private void drawSocket(GuiGraphics gfx) {
        int cx = (int) socketX, cy = (int) socketY;
        gfx.fill(cx - SOCKET_R, cy - SOCKET_R, cx + SOCKET_R, cy + SOCKET_R, 0xFF141414);
        gfx.fill(cx - SOCKET_R + 3, cy - SOCKET_R + 3, cx + SOCKET_R - 3, cy + SOCKET_R - 3, 0xFF050505);
        gfx.fill(cx - 2, cy - 2, cx + 2, cy + 2, 0xFF221010);
    }

    private void drawBone(GuiGraphics gfx) {
        int cx = (int) boneX, cy = (int) boneY;
        int base = heldBone ? 0xFFDDD8C8 : 0xFFBBB6A6;
        gfx.fill(cx - BONE_R, cy - BONE_R, cx + BONE_R, cy + BONE_R, 0xFF1C1C1C);
        gfx.fill(cx - BONE_R + 2, cy - BONE_R + 2, cx + BONE_R - 2, cy + BONE_R - 2, base);
        gfx.fill(cx - 3, cy - 3, cx, cy, 0xFFFFF6E0);
    }

    private void drawHUD(GuiGraphics gfx) {
        int hx = 14;
        int hy = height / 2 - 48;

        gfx.drawString(font, "Reduction: " + limb.getDisplayName().getString(), hx, hy, 0xFFDDDDFF, false);

        String stateLabel;
        int stateCol;
        if (!heldBone) {
            stateLabel = "Grab the bone";
            stateCol = 0xFFCCCCCC;
        } else if (panicTicks > 0) {
            stateLabel = "Yanked too hard!";
            stateCol = 0xFFFF4444;
        } else if (Math.hypot(boneX - socketX, boneY - socketY) <= ALIGN_RADIUS) {
            stateLabel = "Holding in socket...";
            stateCol = 0xFF66DD88;
        } else {
            stateLabel = "Chase the socket!";
            stateCol = 0xFFEEEE66;
        }
        gfx.drawString(font, stateLabel, hx, hy + 14, stateCol, false);

        int barW = 130, barH = 8;
        int barX = hx, barY = hy + 28;
        gfx.fill(barX - 1, barY - 1, barX + barW + 1, barY + barH + 1, 0xFF111111);
        gfx.fill(barX, barY, barX + barW, barY + barH, 0xFF1C1C2A);
        float holdFrac = holdTicks / (float) HOLD_TICKS_FOR_SUCCESS;
        gfx.fill(barX, barY, barX + (int) (barW * Math.min(1f, holdFrac)), barY + barH, 0xFF4488BB);
        gfx.drawString(font, (int) (holdFrac * 100) + "% held steady", barX, barY + barH + 4, 0x99BBEE, false);

        if (painPenalty > 0) {
            gfx.drawString(font, "Extra pain risked: " + painPenalty, hx, hy + 60, 0xFFFF8844, false);
        }

        float shakeMag = getShakeMagnitude();
        if (shakeMag > 2f) {
            String warn = shakeMag > 5f ? "Hands shaking badly!" : "Hands unsteady";
            gfx.drawString(font, warn, hx, hy + 74, 0xFFFFAA22, false);
        }

        if (!succeeded) {
            String hint = heldBone
                    ? "Follow the drifting socket and hold it there"
                    : "Click the bone, then drag it into the socket";
            gfx.drawCenteredString(font, hint, width / 2, height - 30, 0xCCCCCC);
            gfx.drawCenteredString(font, "[Right-click or Esc to cancel]", width / 2, height - 18, 0x666666);
        } else {
            gfx.drawCenteredString(font, "Joint reset!", width / 2, anchorCY - 70, 0xFF55FF88);
        }
    }


    @Override
    public void tick() {
        super.tick();
        updateShake();

        if (panicTicks > 0) panicTicks--;

        socketX += (socketTargetX - socketX) * SOCKET_EASE;
        socketY += (socketTargetY - socketY) * SOCKET_EASE;
        if (--retargetCooldown <= 0) {
            pickNewSocketTarget();
            retargetCooldown = randomRetargetTicks();
        }

        if (!succeeded) {
            double dist = Math.hypot(boneX - socketX, boneY - socketY);
            boolean aligned = dist <= ALIGN_RADIUS;
            if (heldBone && aligned) {
                holdTicks++;
                if (holdTicks >= HOLD_TICKS_FOR_SUCCESS) triggerSuccess();
            } else {
                holdTicks = 0;
            }
        }

        if (closeCountdown > 0 && --closeCountdown == 0) onClose();
    }

    private void updateShake() {
        float mag = getShakeMagnitude();
        shakeX = mag > 0f ? (rng.nextFloat() - 0.5f) * mag : 0f;
        shakeY = mag > 0f ? (rng.nextFloat() - 0.5f) * mag : 0f;
    }

    private float getShakeMagnitude() {
        Player p = minecraft.player;
        float s = 0f;
        if (p != null) {
            if (p.hasEffect(MobEffects.POISON))            s += 3.5f;
            if (p.hasEffect(MobEffects.WITHER))             s += 6f;
            if (p.hasEffect(MobEffects.WEAKNESS))           s += 2f;
            if (p.hasEffect(MobEffects.MOVEMENT_SLOWDOWN))  s += 1.5f;
            if (p.getHealth() < p.getMaxHealth() * 0.25f)   s += 4.5f;
        }
        if (panicTicks > 0) s += 5f;
        return s;
    }


    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        if (button == 1) { onClose(); return true; }

        if (button == 0 && !heldBone && !succeeded) {
            double dist = Math.hypot(mx - boneX, my - boneY);
            if (dist <= GRAB_RADIUS) {
                heldBone = true;
                holdTicks = 0;
            }
            return true;
        }
        return super.mouseClicked(mx, my, button);
    }

    @Override
    public boolean mouseDragged(double mx, double my, int button, double dx, double dy) {
        if (button != 0 || !heldBone || succeeded) {
            return super.mouseDragged(mx, my, button, dx, dy);
        }

        double speed = Math.hypot(dx, dy);
        if (speed > VIOLENT_SPEED) {
            painPenalty += VIOLENT_PAIN_PENALTY;
            panicTicks = PANIC_JITTER_TICKS;
            playStrainSound();
            heldBone = false;
            holdTicks = 0;
            return true;
        }

        boneX = (float) (mx + shakeX);
        boneY = (float) (my + shakeY);
        return true;
    }

    @Override
    public boolean mouseReleased(double mx, double my, int button) {
        if (button == 0 && heldBone) {
            heldBone = false;
            holdTicks = 0;
            return true;
        }
        return super.mouseReleased(mx, my, button);
    }


    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) { onClose(); return true; }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean isPauseScreen() { return false; }


    private void triggerSuccess() {
        succeeded = true;
        heldBone = false;
        closeCountdown = 80;

        PacketDistributor.sendToServer(new ResetDislocationPayload(limb.ordinal(), painPenalty));
        playPopSound();
    }

    private void playStrainSound() {
        Player p = minecraft.player;
        if (p == null) return;
        p.level().playLocalSound(p.getX(), p.getY(), p.getZ(),
                ModSounds.DISLOCATION_STRAIN.get(), SoundSource.PLAYERS,
                0.6f, 0.9f + rng.nextFloat() * 0.2f, false);
    }

    private void playPopSound() {
        Player p = minecraft.player;
        if (p == null) return;
        p.level().playLocalSound(p.getX(), p.getY(), p.getZ(),
                ModSounds.DISLOCATION_POP.get(), SoundSource.PLAYERS,
                0.9f, 1.0f, false);
    }


    private static void drawLine(GuiGraphics gfx, int x1, int y1, int x2, int y2, int color) {
        int dx = x2 - x1, dy = y2 - y1;
        int steps = Math.max(Math.abs(dx), Math.abs(dy));
        if (steps == 0) return;
        float sx = dx / (float) steps, sy = dy / (float) steps;
        float cx = x1, cy = y1;
        for (int i = 0; i <= steps; i++) {
            gfx.fill((int) cx, (int) cy, (int) cx + 1, (int) cy + 1, color);
            cx += sx; cy += sy;
        }
    }
}
