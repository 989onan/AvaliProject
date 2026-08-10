package com.lunkoashtail.avaliproject.screen.custom;

import com.lunkoashtail.avaliproject.AvaliProject;
import com.lunkoashtail.avaliproject.limb.Limb;
import com.lunkoashtail.avaliproject.limb.ModAttachments;
import com.lunkoashtail.avaliproject.limb.PainData;
import com.lunkoashtail.avaliproject.network.RemoveShrapnelPayload;
import com.lunkoashtail.avaliproject.network.ShrapnelSlipPayload;
import com.lunkoashtail.avaliproject.sound.ModSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Random;

public class ShrapnelMinigameScreen extends Screen {


    private static final float GRIP_RADIUS = 16f;

    private static final float PULL_DISTANCE_FOR_EXTRACTION = 46f;

    private static final float LATERAL_TOLERANCE = 9f;

    private static final float MAX_SAFE_SPEED = 6f;

    private static final float VIOLENT_SPEED = 16f;

    private static final float SLIP_SETBACK = 12f;

    private static final int SLIP_BLEED_PENALTY = 2;

    private static final int DROP_BLEED_PENALTY = 6;

    private static final float MILD_SLIP_PAIN = 3f;
    private static final float MILD_SLIP_DAMAGE = 1.0f;
    private static final float VIOLENT_SLIP_PAIN = 6f;
    private static final float VIOLENT_SLIP_DAMAGE = 2.0f;

    private static final int PANIC_JITTER_TICKS = 12;

    private static final int SEVERITY_PER_PIECE = 25;
    private static final int MAX_PIECES = 5;

    private static final int LIMB_LEN = 150;

    private static final float REFERENCE_WIDTH = 560f;
    private static final float REFERENCE_HEIGHT = 280f;
    private static final float MIN_SCALE = 0.5f;
    private static final float MAX_SCALE = 2.0f;

    private static final int COL_SHADOW = 0xC8000000;

    private static final int COL_PANEL       = 0xDD061008;
    private static final int COL_BAR_BG      = 0xFF050807;
    private static final int COL_BORDER      = 0xFF2FCC66;
    private static final int COL_TEXT_BRIGHT = 0xFF66FF99;
    private static final int COL_TEXT_DIM    = 0xFF2E8850;
    private static final int COL_LIMB_LINE   = 0xFFDCD6C4;

    private static final ResourceLocation TEX_SHRAPNEL =
            ResourceLocation.fromNamespaceAndPath(AvaliProject.MOD_ID, "textures/gui/shrapnel/expie_shrapnel.png");
    private static final ResourceLocation TEX_PAW =
            ResourceLocation.fromNamespaceAndPath(AvaliProject.MOD_ID, "textures/gui/shrapnel/expie_paw.png");
    private static final ResourceLocation TEX_GRIP =
            ResourceLocation.fromNamespaceAndPath(AvaliProject.MOD_ID, "textures/gui/shrapnel/expie_grip.png");

    private static final int SHRAPNEL_TEX_W = 27, SHRAPNEL_TEX_H = 138;
    private static final int PAW_TEX_W = 145, PAW_TEX_H = 211;

    private static final float SHRAPNEL_SCALE = 0.65f;
    private static final int SHRAPNEL_DISPLAY_W = Math.round(SHRAPNEL_TEX_W * SHRAPNEL_SCALE);
    private static final int SHRAPNEL_DISPLAY_H = Math.round(SHRAPNEL_TEX_H * SHRAPNEL_SCALE);

    private static final float SHRAPNEL_HIDDEN_FRACTION = 0.8f;
    private static final float SHRAPNEL_REST_DEPTH = SHRAPNEL_DISPLAY_H * SHRAPNEL_HIDDEN_FRACTION;
    private static final float SHRAPNEL_VISUAL_TRAVEL_SCALE = SHRAPNEL_REST_DEPTH / PULL_DISTANCE_FOR_EXTRACTION;

    private static final float PAW_SCALE = 0.8f;
    private static final int PAW_DISPLAY_W = Math.round(PAW_TEX_W * PAW_SCALE);
    private static final int PAW_DISPLAY_H = Math.round(PAW_TEX_H * PAW_SCALE);
    private static final float PAW_ALPHA = 0.5f;

    private static final float PAW_ANCHOR_Y_FRACTION = 0.2f;


    private static final class Piece {
        final float embedX, embedY;
        final float angleDeg;
        float pulledDistance = 0f;
        boolean extracted = false;

        Piece(float embedX, float embedY, float angleDeg) {
            this.embedX = embedX;
            this.embedY = embedY;
            this.angleDeg = angleDeg;
        }
    }


    private final Limb limb;
    private final int initialShrapnel;

    private Piece[] pieces;
    private int heldIndex = -1;

    private boolean mouseDown = false;

    private int mistakePenalty = 0;

    private int panicTicks = 0;

    private int armCX, armCY;

    private int closeCountdown = 0;
    private boolean succeeded = false;

    private final Random rng = new Random();
    private float shakeX, shakeY;

    private float toolX, toolY;

    private static final int MAX_PARTICLES = 24;
    private final float[][] particles = new float[MAX_PARTICLES][5];
    private int particleCount = 0;

    private int slipSoundCooldown = 0;

    public ShrapnelMinigameScreen(Limb limb, int initialShrapnel) {
        super(Component.translatable("screen.avaliproject.shrapnel_minigame"));
        this.limb = limb;
        this.initialShrapnel = initialShrapnel;
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

        armCX = virtualWidth / 2;
        armCY = virtualHeight / 2 + 5;

        int pieceCount = Math.max(1, Math.min(MAX_PIECES, (initialShrapnel + SEVERITY_PER_PIECE - 1) / SEVERITY_PER_PIECE));
        pieces = new Piece[pieceCount];

        float margin = LIMB_LEN * 0.16f;
        float usable = LIMB_LEN - margin * 2f;
        for (int i = 0; i < pieceCount; i++) {
            float t = pieceCount == 1 ? 0.5f : i / (float) (pieceCount - 1);
            float ex = armCX - LIMB_LEN / 2f + margin + usable * t;
            float ey = armCY;

            float jitter = (rng.nextFloat() - 0.5f) * 16f;
            pieces[i] = new Piece(ex, ey, -90f + jitter);
        }

        toolX = armCX;
        toolY = virtualHeight - 40f;
    }


    @Override
    public void renderBackground(GuiGraphics gfx, int mx, int my, float partial) {
    }

    @Override
    public void render(GuiGraphics gfx, int mx, int my, float partial) {
        gfx.fill(0, 0, width, height, COL_SHADOW);

        float scale = scaleFactor();
        float vmx = mx / scale, vmy = my / scale;
        int vsmx = Math.round(vmx + shakeX);
        int vsmy = Math.round(vmy + shakeY);
        toolX = vmx;
        toolY = vmy;

        gfx.pose().pushPose();
        gfx.pose().scale(scale, scale, 1f);

        drawArm(gfx);
        drawPieces(gfx, vsmx, vsmy, scale);
        drawParticles(gfx);
        drawTool(gfx);
        drawStatsPanel(gfx);
        drawTopBanner(gfx);
        drawBottomHint(gfx);

        super.render(gfx, Math.round(vmx), Math.round(vmy), partial);

        gfx.pose().popPose();
    }

    private void drawStatsPanel(GuiGraphics gfx) {
        int pw = 160, ph = 128;
        int px = 8, py = armCY - ph / 2;
        gfx.fill(px, py, px + pw, py + ph, COL_PANEL);
        gfx.fill(px,          py,          px + pw,     py + 1,      COL_BORDER);
        gfx.fill(px,          py + ph - 1, px + pw,     py + ph,     COL_BORDER);
        gfx.fill(px,          py,          px + 1,      py + ph,     COL_BORDER);
        gfx.fill(px + pw - 1, py,          px + pw,     py + ph,     COL_BORDER);

        int tx = px + 8, ty = py + 8;
        gfx.drawString(font, "SHRAPNEL EXTRACTION", tx, ty, COL_TEXT_BRIGHT, false);
        gfx.fill(px + 6, ty + 10, px + pw - 6, ty + 11, COL_TEXT_DIM);
        ty += 16;

        gfx.drawString(font, "LIMB: " + limb.getDisplayName().getString().toUpperCase(), tx, ty, COL_TEXT_DIM, false);
        ty += 11;
        gfx.drawString(font, "PIECES: " + remainingCount() + " / " + pieces.length, tx, ty, COL_TEXT_BRIGHT, false);
        ty += 13;

        String steadyLabel;
        int steadyCol;
        if (heldIndex < 0) {
            steadyLabel = "NOT GRIPPING";
            steadyCol = COL_TEXT_DIM;
        } else if (panicTicks > 0) {
            steadyLabel = "CAREFUL!";
            steadyCol = 0xFFFF5555;
        } else {
            steadyLabel = "STEADY";
            steadyCol = COL_TEXT_BRIGHT;
        }
        gfx.drawString(font, "GRIP: " + steadyLabel, tx, ty, steadyCol, false);
        ty += 13;

        if (heldIndex >= 0) {
            float frac = pieces[heldIndex].pulledDistance / PULL_DISTANCE_FOR_EXTRACTION;
            int barW = pw - 16, barH = 6;
            gfx.fill(tx - 1, ty - 1, tx + barW + 1, ty + barH + 1, COL_BAR_BG);
            gfx.fill(tx, ty, tx + barW, ty + barH, 0xFF0A1A10);
            gfx.fill(tx, ty, tx + (int) (barW * Math.min(1f, frac)), ty + barH, COL_BORDER);
            ty += barH + 8;
        } else {
            ty += 4;
        }

        Player statsPlayer = minecraft.player;
        float currentPain = statsPlayer != null ? statsPlayer.getData(ModAttachments.PAIN_DATA).get() : 0f;
        gfx.drawString(font, "PAIN: " + Math.round(currentPain) + " / " + (int) PainData.MAX_PAIN,
                tx, ty, painColor(currentPain), false);
        ty += 11;

        if (mistakePenalty > 0) {
            int pendingPain = Math.round(mistakePenalty * 0.5f);
            gfx.drawString(font, "BLEED RISK: +" + mistakePenalty + " (+" + pendingPain + " PAIN)",
                    tx, ty, 0xFFFF8844, false);
            ty += 11;
        }

        float shakeMag = getShakeMagnitude();
        if (shakeMag > 2f) {
            String warn = shakeMag > 5f ? "HANDS SHAKING BADLY" : "HANDS UNSTEADY";
            gfx.drawString(font, warn, tx, ty, 0xFFFFAA22, false);
        }
    }

    private void drawArm(GuiGraphics gfx) {
        int x1 = armCX - LIMB_LEN / 2, x2 = armCX + LIMB_LEN / 2;
        gfx.fill(x1, armCY, x2, armCY + 2, COL_LIMB_LINE);
    }

    private void drawPieces(GuiGraphics gfx, int smx, int smy, float scale) {
        int clipBottomReal = Math.round((armCY + 1) * scale);

        for (int i = 0; i < pieces.length; i++) {
            Piece p = pieces[i];
            boolean held = (i == heldIndex);

            float visualTravel = p.pulledDistance * SHRAPNEL_VISUAL_TRAVEL_SCALE;
            int bottomY = Math.round(p.embedY + SHRAPNEL_REST_DEPTH - visualTravel);
            int drawX = Math.round(p.embedX) - SHRAPNEL_DISPLAY_W / 2;
            int drawY = bottomY - SHRAPNEL_DISPLAY_H;

            if (!p.extracted) {
                gfx.enableScissor(0, 0, width, clipBottomReal);
            }
            float r = 1f, g = held ? 0.85f : 1f, b = held ? 0.6f : 1f, a = 1f;
            gfx.innerBlit(TEX_SHRAPNEL, drawX, drawX + SHRAPNEL_DISPLAY_W, drawY, drawY + SHRAPNEL_DISPLAY_H,
                    0, 0f, 1f, 0f, 1f, r, g, b, a);
            if (!p.extracted) {
                gfx.disableScissor();
            }

            if (held) {
                drawLine(gfx, (int) p.embedX, (int) p.embedY, smx, smy, 0x33FFAA33);
            }
        }
    }

    private void drawTool(GuiGraphics gfx) {
        ResourceLocation tex = mouseDown ? TEX_GRIP : TEX_PAW;
        int drawX = Math.round(toolX) - PAW_DISPLAY_W / 2;
        int drawY = Math.round(toolY) - Math.round(PAW_DISPLAY_H * PAW_ANCHOR_Y_FRACTION);
        gfx.innerBlit(tex, drawX, drawX + PAW_DISPLAY_W, drawY, drawY + PAW_DISPLAY_H,
                0, 0f, 1f, 0f, 1f, 1f, 1f, 1f, PAW_ALPHA);
    }

    private void drawParticles(GuiGraphics gfx) {
        for (int i = 0; i < particleCount; i++) {
            int alpha = (int) (particles[i][4] * 200f);
            if (alpha < 6) continue;
            int px = (int) particles[i][0], py = (int) particles[i][1];
            gfx.fill(px, py, px + 2, py + 2, (alpha << 24) | 0xFF3333);
        }
    }

    private void drawTopBanner(GuiGraphics gfx) {
        String flavor = succeeded
                ? "That should be all of it..."
                : "Let's get this shrapnel out...";
        gfx.drawCenteredString(font, flavor, armCX, 14, 0xFFEFEFE0);
        gfx.drawCenteredString(font,
                "Pull the shrapnel out slowly - going too fast or crooked will hurt.",
                armCX, 26, COL_TEXT_DIM);
    }

    private void drawBottomHint(GuiGraphics gfx) {
        int virtualHeight = (int) (height / scaleFactor());
        if (!succeeded) {
            String hint = (heldIndex < 0)
                    ? "Click a shard to grip it"
                    : "Pull straight and slow along its line";
            gfx.drawCenteredString(font, hint, armCX, virtualHeight - 30, 0xCCCCCC);
            gfx.drawCenteredString(font, "[Right-click or Esc to cancel]", armCX, virtualHeight - 18, 0x666666);
        } else {
            gfx.drawCenteredString(font, "All shrapnel removed!", armCX, armCY - 60, 0xFF55FF88);
        }
    }

    private static int painColor(float pain) {
        if (pain >= 80f) return 0xFFFF5555;
        if (pain >= 50f) return 0xFFFFAA55;
        return 0xFFAACCFF;
    }

    private int remainingCount() {
        int n = 0;
        for (Piece p : pieces) if (!p.extracted) n++;
        return n;
    }


    @Override
    public void tick() {
        super.tick();
        updateShake();
        tickParticles();
        if (panicTicks > 0) panicTicks--;
        if (slipSoundCooldown > 0) slipSoundCooldown--;
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

            PainData pain = p.getData(ModAttachments.PAIN_DATA);
            if (pain.get() > 0f) s += (pain.get() / PainData.MAX_PAIN) * 6f;
        }
        if (panicTicks > 0) s += 5f;
        return s;
    }

    private void tickParticles() {
        for (int i = particleCount - 1; i >= 0; i--) {
            particles[i][0] += particles[i][2];
            particles[i][1] += particles[i][3];
            particles[i][2] *= 0.82f;
            particles[i][3] *= 0.82f;
            particles[i][4] -= 0.04f;
            if (particles[i][4] <= 0f) {
                if (i < particleCount - 1) System.arraycopy(particles[particleCount - 1], 0, particles[i], 0, 5);
                particleCount--;
            }
        }
    }

    private void spawnBloodParticle(float x, float y) {
        if (particleCount >= MAX_PARTICLES) return;
        float[] p = particles[particleCount++];
        p[0] = x + rng.nextInt(5) - 2;
        p[1] = y + rng.nextInt(5) - 2;
        p[2] = (rng.nextFloat() - 0.5f) * 2.5f;
        p[3] = rng.nextFloat() * 1.8f;
        p[4] = 0.5f + rng.nextFloat() * 0.5f;
    }


    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        float scale = scaleFactor();
        double vmx = mx / scale, vmy = my / scale;

        if (button == 1) { onClose(); return true; }

        if (button == 0) {
            mouseDown = true;
            if (heldIndex < 0 && !succeeded) {
                int nearest = findNearestGrippable(vmx + shakeX, vmy + shakeY);
                if (nearest >= 0) heldIndex = nearest;
            }
            return true;
        }
        return super.mouseClicked(vmx, vmy, button);
    }

    private float visibleCenterY(Piece p) {
        float visualTravel = p.pulledDistance * SHRAPNEL_VISUAL_TRAVEL_SCALE;
        float visibleAbove = SHRAPNEL_DISPLAY_H - (SHRAPNEL_REST_DEPTH - visualTravel);
        visibleAbove = Math.max(6f, Math.min(SHRAPNEL_DISPLAY_H, visibleAbove));
        return p.embedY - visibleAbove * 0.5f;
    }

    private int findNearestGrippable(double emx, double emy) {
        int best = -1;
        double bestDist = GRIP_RADIUS;
        for (int i = 0; i < pieces.length; i++) {
            Piece p = pieces[i];
            if (p.extracted) continue;
            double d = Math.hypot(emx - p.embedX, emy - visibleCenterY(p));
            if (d <= bestDist) {
                best = i;
                bestDist = d;
            }
        }
        return best;
    }

    @Override
    public boolean mouseDragged(double mx, double my, int button, double dx, double dy) {
        float scale = scaleFactor();
        double vmx = mx / scale, vmy = my / scale;
        double vdx = dx / scale, vdy = dy / scale;

        if (button != 0 || heldIndex < 0 || succeeded) {
            return super.mouseDragged(vmx, vmy, button, vdx, vdy);
        }

        Piece piece = pieces[heldIndex];
        double speed = Math.hypot(vdx, vdy);

        if (speed > VIOLENT_SPEED) {
            piece.pulledDistance = Math.max(0f, piece.pulledDistance - SLIP_SETBACK * 2f);
            mistakePenalty += DROP_BLEED_PENALTY;
            panicTicks = PANIC_JITTER_TICKS;
            spawnBloodParticle(piece.embedX, piece.embedY);
            playSlipSound();
            PacketDistributor.sendToServer(new ShrapnelSlipPayload(VIOLENT_SLIP_PAIN, VIOLENT_SLIP_DAMAGE));
            heldIndex = -1;
            return true;
        }

        double emx = vmx + shakeX, emy = vmy + shakeY;
        double vx = emx - piece.embedX, vy = emy - piece.embedY;

        double axisX = Math.cos(Math.toRadians(piece.angleDeg));
        double axisY = Math.sin(Math.toRadians(piece.angleDeg));
        double axial = vx * axisX + vy * axisY;
        double lateral = Math.abs(vx * axisY - vy * axisX);

        boolean crooked = lateral > LATERAL_TOLERANCE;
        boolean tooFast = speed > MAX_SAFE_SPEED;

        if (tooFast || crooked) {
            piece.pulledDistance = Math.max(0f, piece.pulledDistance - SLIP_SETBACK);
            mistakePenalty += SLIP_BLEED_PENALTY;
            panicTicks = PANIC_JITTER_TICKS;
            spawnBloodParticle(piece.embedX, piece.embedY);
            playSlipSound();
            PacketDistributor.sendToServer(new ShrapnelSlipPayload(MILD_SLIP_PAIN, MILD_SLIP_DAMAGE));
            return true;
        }

        float candidate = (float) Math.max(0, Math.min(PULL_DISTANCE_FOR_EXTRACTION, axial));
        if (candidate > piece.pulledDistance) {
            piece.pulledDistance = candidate;
        }
        if (piece.pulledDistance >= PULL_DISTANCE_FOR_EXTRACTION) {
            extractPiece(heldIndex);
        }
        return true;
    }

    @Override
    public boolean mouseReleased(double mx, double my, int button) {
        float scale = scaleFactor();
        if (button == 0) {
            mouseDown = false;
            if (heldIndex >= 0) {
                heldIndex = -1;
            }
            return true;
        }
        return super.mouseReleased(mx / scale, my / scale, button);
    }

    private void extractPiece(int index) {
        Piece piece = pieces[index];
        piece.extracted = true;
        heldIndex = -1;
        spawnBloodParticle(piece.embedX, piece.embedY);
        playPullSound();

        if (remainingCount() == 0) {
            triggerSuccess();
        }
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
        closeCountdown = 80;

        PacketDistributor.sendToServer(new RemoveShrapnelPayload(limb.ordinal(), mistakePenalty));
        playCompleteSound();
    }

    private void playPullSound() {
        Player p = minecraft.player;
        if (p == null) return;
        p.level().playLocalSound(p.getX(), p.getY(), p.getZ(),
                ModSounds.SHRAPNEL_PULL.get(), SoundSource.PLAYERS,
                0.6f, 0.9f + rng.nextFloat() * 0.2f, false);
    }

    private void playSlipSound() {
        if (slipSoundCooldown > 0) return;
        slipSoundCooldown = 6;
        Player p = minecraft.player;
        if (p == null) return;
        p.level().playLocalSound(p.getX(), p.getY(), p.getZ(),
                ModSounds.SHRAPNEL_SLIP.get(), SoundSource.PLAYERS,
                0.7f, 1.0f + rng.nextFloat() * 0.2f, false);
    }

    private void playCompleteSound() {
        Player p = minecraft.player;
        if (p == null) return;
        p.level().playLocalSound(p.getX(), p.getY(), p.getZ(),
                ModSounds.SHRAPNEL_COMPLETE.get(), SoundSource.PLAYERS,
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
