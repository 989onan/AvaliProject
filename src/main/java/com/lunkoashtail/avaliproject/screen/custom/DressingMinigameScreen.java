package com.lunkoashtail.avaliproject.screen.custom;

import com.lunkoashtail.avaliproject.AvaliProject;
import com.lunkoashtail.avaliproject.client.TargetDataCache;
import com.lunkoashtail.avaliproject.item.ModItems;
import com.lunkoashtail.avaliproject.limb.BleedingTier;
import com.lunkoashtail.avaliproject.limb.Limb;
import com.lunkoashtail.avaliproject.limb.LimbData;
import com.lunkoashtail.avaliproject.limb.ModAttachments;
import com.lunkoashtail.avaliproject.network.ReduceBleedPayload;
import com.lunkoashtail.avaliproject.sound.ModSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Random;

public class DressingMinigameScreen extends Screen {


    private static final float REVOLUTIONS_PER_FULL_DRESSING = 24f;

    private static final float DURABILITY_PER_REVOLUTION = (2f * LimbData.MAX_BLEED) / REVOLUTIONS_PER_FULL_DRESSING;

    private static final float BLEED_PER_REVOLUTION = 2.5f;

    private static final float DURABILITY_PER_RADIAN = DURABILITY_PER_REVOLUTION / (float) (2.0 * Math.PI);
    private static final float BLEED_PER_RADIAN = BLEED_PER_REVOLUTION / (float) (2.0 * Math.PI);

    private static final float MIN_WRAP_RADIUS = 20f;

    private static final float MAX_WRAP_RADIUS = 140f;

    private static final double MIN_ANGLE_DELTA = 0.003;
    private static final double MAX_ANGLE_DELTA = 1.6;

    private static final int POINTS_PER_SOUND = 8;

    private static final int COL_SHADOW      = 0xC8000000;
    private static final int COL_PANEL       = 0xDD061008;
    private static final int COL_BAR_BG      = 0xFF050807;
    private static final int COL_BORDER      = 0xFF2FCC66;
    private static final int COL_TEXT_BRIGHT = 0xFF66FF99;
    private static final int COL_TEXT_DIM    = 0xFF2E8850;
    private static final int COL_ROLL         = 0xFFEDE0CE;
    private static final int COL_ROLL_SHADOW  = 0xFF8A7A5A;

    private static final ResourceLocation TEX_LIMB =
            ResourceLocation.fromNamespaceAndPath(AvaliProject.MOD_ID, "textures/gui/dressing/expie_limb.png");
    private static final int LIMB_TEX_DISPLAY = 110;
    private static final float LIMB_RING_RADIUS_FRACTION = 0.475f;
    private static final int LIMB_RING_RADIUS = Math.round(LIMB_TEX_DISPLAY * LIMB_RING_RADIUS_FRACTION);
    private static final int COVERAGE_GAP = 3;
    private static final int MAX_COVERAGE_BAND_THICKNESS = 14;

    private static final int MAX_ROLL_RADIUS = 28;

    private static final int ROLL_ORBIT_RADIUS = LIMB_RING_RADIUS + MAX_ROLL_RADIUS;

    private static final float REFERENCE_WIDTH = 560f;
    private static final float REFERENCE_HEIGHT = 280f;
    private static final float MIN_SCALE = 0.5f;
    private static final float MAX_SCALE = 2.0f;

    private static final ResourceLocation TEX_PAW =
            ResourceLocation.fromNamespaceAndPath(AvaliProject.MOD_ID, "textures/gui/shrapnel/expie_paw.png");
    private static final ResourceLocation TEX_GRIP =
            ResourceLocation.fromNamespaceAndPath(AvaliProject.MOD_ID, "textures/gui/shrapnel/expie_grip.png");
    private static final int PAW_TEX_W = 145, PAW_TEX_H = 211;
    private static final float PAW_SCALE = 0.8f;
    private static final int PAW_DISPLAY_W = Math.round(PAW_TEX_W * PAW_SCALE);
    private static final int PAW_DISPLAY_H = Math.round(PAW_TEX_H * PAW_SCALE);
    private static final float PAW_ALPHA = 0.5f;
    private static final float PAW_ANCHOR_Y_FRACTION = 0.2f;

    private final Limb limb;
    private final InteractionHand hand;
    private final int targetEntityId;

    private boolean lmbHeld = false;
    private double lastAngle = Double.NaN;

    private float rollAngleRad = (float) Math.toRadians(-90);

    private float bleedAccumulator = 0f;
    private float durabilityAccumulator = 0f;

    private float lapRadians = 0f;

    private int lapsCompleted = 0;

    private int pointsSinceSound = 0;

    private boolean healedNotified;

    private final Random rng = new Random();

    private int limbCX, limbCY;

    private float toolX, toolY;

    private boolean depleted = false;
    private int closeCountdown = 0;

    private static final int MAX_PARTICLES = 32;
    private final float[][] particles = new float[MAX_PARTICLES][4];
    private int particleCount = 0;

    public DressingMinigameScreen(Limb limb, int initialBleed, InteractionHand hand, int targetEntityId) {
        super(Component.translatable("screen.avaliproject.dressing_minigame"));
        this.limb = limb;
        this.hand = hand;
        this.targetEntityId = targetEntityId;
        this.healedNotified = initialBleed <= 0;
    }

    private float rollX() { return limbCX + (float) Math.cos(rollAngleRad) * ROLL_ORBIT_RADIUS; }
    private float rollY() { return limbCY + (float) Math.sin(rollAngleRad) * ROLL_ORBIT_RADIUS; }

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

        limbCX = virtualWidth / 2;
        limbCY = virtualHeight / 2 + 5;

        toolX = limbCX;
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

        toolX = lmbHeld ? rollX() : vmx;
        toolY = lmbHeld ? rollY() : vmy;

        gfx.pose().pushPose();
        gfx.pose().scale(scale, scale, 1f);

        drawLimb(gfx);
        drawParticles(gfx);
        drawTool(gfx);
        drawDressingRoll(gfx);
        drawStatsPanel(gfx);
        drawTopBanner(gfx);
        drawBottomHint(gfx);

        super.render(gfx, Math.round(vmx), Math.round(vmy), partial);

        gfx.pose().popPose();
    }

    private void drawLimb(GuiGraphics gfx) {
        int drawX = limbCX - LIMB_TEX_DISPLAY / 2;
        int drawY = limbCY - LIMB_TEX_DISPLAY / 2;
        gfx.innerBlit(TEX_LIMB, drawX, drawX + LIMB_TEX_DISPLAY, drawY, drawY + LIMB_TEX_DISPLAY,
                0, 0f, 1f, 0f, 1f, 1f, 1f, 1f, 1f);

        drawCoverage(gfx);
    }

    private void drawCoverage(GuiGraphics gfx) {
        float frac = coverageFraction();
        if (frac <= 0f) return;

        int innerRadius = LIMB_RING_RADIUS + COVERAGE_GAP;
        int outerRadius = innerRadius + Math.round(MAX_COVERAGE_BAND_THICKNESS * frac);

        int alpha = 0x60 + Math.round(0x9F * frac);
        fillAnnulus(gfx, limbCX, limbCY, outerRadius, innerRadius, (alpha << 24) | 0xFFFFFF);
    }

    private float coverageFraction() {
        float totalRevolutions = lapsCompleted + lapRadians / (float) (2.0 * Math.PI);
        return Mth.clamp(totalRevolutions / REVOLUTIONS_PER_FULL_DRESSING, 0f, 1f);
    }

    private static void fillCircle(GuiGraphics gfx, int cx, int cy, int radius, int color) {
        if (radius <= 0) return;
        for (int y = -radius; y <= radius; y++) {
            int dx = (int) Math.sqrt((double) radius * radius - (double) y * y);
            gfx.fill(cx - dx, cy + y, cx + dx + 1, cy + y + 1, color);
        }
    }

    private static void fillAnnulus(GuiGraphics gfx, int cx, int cy, int outerRadius, int innerRadius, int color) {
        if (outerRadius <= 0 || outerRadius <= innerRadius) return;
        for (int y = -outerRadius; y <= outerRadius; y++) {
            int outerDx = (int) Math.sqrt((double) outerRadius * outerRadius - (double) y * y);
            double innerSq = (double) innerRadius * innerRadius - (double) y * y;
            if (innerSq <= 0) {
                gfx.fill(cx - outerDx, cy + y, cx + outerDx + 1, cy + y + 1, color);
            } else {
                int innerDx = (int) Math.sqrt(innerSq);
                gfx.fill(cx - outerDx, cy + y, cx - innerDx, cy + y + 1, color);
                gfx.fill(cx + innerDx + 1, cy + y, cx + outerDx + 1, cy + y + 1, color);
            }
        }
    }

    private void drawParticles(GuiGraphics gfx) {
        for (int i = 0; i < particleCount; i++) {
            float spd = Math.abs(particles[i][2]) + Math.abs(particles[i][3]);
            int alpha = (int) Math.min(180, spd * 90);
            if (alpha < 5) continue;
            int px = (int) particles[i][0];
            int py = (int) particles[i][1];
            gfx.fill(px, py, px + 2, py + 2, (alpha << 24) | 0xE8D8A8);
        }
    }

    private void drawTool(GuiGraphics gfx) {
        ResourceLocation tex = lmbHeld ? TEX_GRIP : TEX_PAW;
        int drawX = Math.round(toolX) - PAW_DISPLAY_W / 2;
        int drawY = Math.round(toolY) - Math.round(PAW_DISPLAY_H * PAW_ANCHOR_Y_FRACTION);
        gfx.innerBlit(tex, drawX, drawX + PAW_DISPLAY_W, drawY, drawY + PAW_DISPLAY_H,
                0, 0f, 1f, 0f, 1f, 1f, 1f, 1f, PAW_ALPHA);
    }

    private void drawDressingRoll(GuiGraphics gfx) {
        if (findDressingStack().isEmpty()) return;

        int radius = Math.round(MAX_ROLL_RADIUS * remainingDurabilityFraction());
        if (radius <= 0) return;

        int cx = Math.round(rollX());
        int cy = Math.round(rollY());
        fillCircle(gfx, cx, cy, radius, COL_ROLL);
        if (radius > 3) fillCircle(gfx, cx, cy, radius - 3, COL_ROLL_SHADOW);
        if (radius > 5) fillCircle(gfx, cx, cy, radius - 5, COL_ROLL);
    }

    private ItemStack findDressingStack() {
        Player p = minecraft.player;
        if (p == null) return ItemStack.EMPTY;
        ItemStack stack = hand == InteractionHand.MAIN_HAND ? p.getMainHandItem() : p.getOffhandItem();
        return isDressingStack(stack) ? stack : ItemStack.EMPTY;
    }

    private static boolean isDressingStack(ItemStack stack) {
        return stack.is(ModItems.DRESSING.get()) || stack.is(ModItems.STERILIZED_DRESSING.get());
    }

    private float remainingDurabilityFraction() {
        ItemStack stack = findDressingStack();
        if (stack.isEmpty()) return 0f;
        return 1f - (float) stack.getDamageValue() / stack.getMaxDamage();
    }

    private void drawStatsPanel(GuiGraphics gfx) {
        int pw = 160, ph = 128;
        int px = 8, py = limbCY - ph / 2;
        gfx.fill(px, py, px + pw, py + ph, COL_PANEL);
        gfx.fill(px,          py,          px + pw,     py + 1,      COL_BORDER);
        gfx.fill(px,          py + ph - 1, px + pw,     py + ph,     COL_BORDER);
        gfx.fill(px,          py,          px + 1,      py + ph,     COL_BORDER);
        gfx.fill(px + pw - 1, py,          px + pw,     py + ph,     COL_BORDER);

        int tx = px + 8, ty = py + 8;
        gfx.drawString(font, "WOUND TREATMENT", tx, ty, COL_TEXT_BRIGHT, false);
        gfx.fill(px + 6, ty + 10, px + pw - 6, ty + 11, COL_TEXT_DIM);
        ty += 16;

        gfx.drawString(font, "LIMB: " + limb.getDisplayName().getString().toUpperCase(), tx, ty, COL_TEXT_DIM, false);
        ty += 11;

        int bleed = currentBleed();
        BleedingTier tier = BleedingTier.fromBleedValue(bleed);
        int bleedCol = tier != null ? tier.getColor() : COL_TEXT_BRIGHT;
        gfx.drawString(font, "BLEED: " + bleed + " / " + LimbData.MAX_BLEED, tx, ty, bleedCol, false);
        ty += 11;

        int dressingPct = Math.round(remainingDurabilityFraction() * 100);
        int dressingCol = dressingPct <= 20 ? 0xFFFF5555 : COL_TEXT_DIM;
        gfx.drawString(font, "DRESSING: " + dressingPct + "%", tx, ty, dressingCol, false);
        ty += 13;

        int barW = pw - 16, barH = 6;
        float frac = lapRadians / (float) (2.0 * Math.PI);
        gfx.fill(tx - 1, ty - 1, tx + barW + 1, ty + barH + 1, COL_BAR_BG);
        gfx.fill(tx, ty, tx + barW, ty + barH, 0xFF0A1A10);
        gfx.fill(tx, ty, tx + (int) (barW * Math.min(1f, frac)), ty + barH, COL_BORDER);
        ty += barH + 10;

        if (depleted) {
            gfx.drawString(font, "OUT OF DRESSINGS", tx, ty, 0xFFFF5555, false);
        }
    }

    private void drawTopBanner(GuiGraphics gfx) {
        String flavor = depleted
                ? "Out of dressing material..."
                : "Wrap the wound clockwise, nice and steady...";
        gfx.drawCenteredString(font, flavor, limbCX, 14, 0xFFEFEFE0);
        gfx.drawCenteredString(font,
                "Hold [LMB] and circle clockwise around the limb - reversing loses your current wrap.",
                limbCX, 26, COL_TEXT_DIM);
    }

    private void drawBottomHint(GuiGraphics gfx) {
        int virtualHeight = (int) (height / scaleFactor());
        if (depleted) {
            gfx.drawCenteredString(font, "No dressings left - closing...", limbCX, virtualHeight - 30, 0xFFFF8844);
        } else {
            String hint = lmbHeld ? "Keep circling clockwise!" : "Hold [LMB] and circle the wound";
            gfx.drawCenteredString(font, hint, limbCX, virtualHeight - 30, 0xCCCCCC);
            gfx.drawCenteredString(font, "[Right-click or Esc to stop]", limbCX, virtualHeight - 18, 0x666666);
        }
    }

    private int currentBleed() {
        Player p = minecraft.player;
        if (p == null) return 0;
        return (p.getId() == targetEntityId)
                ? p.getData(ModAttachments.LIMB_DATA).getBleed(limb)
                : TargetDataCache.getBleed(targetEntityId, limb);
    }


    @Override
    public void tick() {
        super.tick();
        tickParticles();
        checkHealed();
        if (closeCountdown > 0 && --closeCountdown == 0) onClose();
    }

    private void checkHealed() {
        if (healedNotified) return;
        if (currentBleed() <= 0) {
            healedNotified = true;
            playHealedSound();
        }
    }

    private void tickParticles() {
        for (int i = particleCount - 1; i >= 0; i--) {
            particles[i][0] += particles[i][2];
            particles[i][1] += particles[i][3];
            particles[i][2] *= 0.82f;
            particles[i][3] *= 0.82f;
            float spd = Math.abs(particles[i][2]) + Math.abs(particles[i][3]);
            if (spd < 0.04f) {
                if (i < particleCount - 1) particles[i] = particles[particleCount - 1];
                particleCount--;
            }
        }
    }

    private void spawnParticle(double px, double py) {
        if (particleCount >= MAX_PARTICLES) return;
        float vx = (rng.nextFloat() - 0.5f) * 2.5f;
        float vy = (rng.nextFloat() - 0.8f) * 2.5f;
        particles[particleCount][0] = (float) px;
        particles[particleCount][1] = (float) py;
        particles[particleCount][2] = vx;
        particles[particleCount][3] = vy;
        particleCount++;
    }


    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        float scale = scaleFactor();
        double vmx = mx / scale, vmy = my / scale;

        if (button == 1) { onClose(); return true; }

        if (button == 0 && !depleted) {
            lmbHeld = true;
            lastAngle = Double.NaN;
            return true;
        }
        return super.mouseClicked(vmx, vmy, button);
    }

    @Override
    public boolean mouseDragged(double mx, double my, int button, double dx, double dy) {
        float scale = scaleFactor();
        double vmx = mx / scale, vmy = my / scale;
        double vdx = dx / scale, vdy = dy / scale;

        if (button != 0 || !lmbHeld || depleted) {
            return super.mouseDragged(vmx, vmy, button, vdx, vdy);
        }

        double emx = vmx, emy = vmy;
        double currAngle = Math.atan2(emy - limbCY, emx - limbCX);

        if (Double.isNaN(lastAngle)) {
            lastAngle = currAngle;
            return true;
        }

        double delta = currAngle - lastAngle;
        while (delta >  Math.PI) delta -= 2 * Math.PI;
        while (delta < -Math.PI) delta += 2 * Math.PI;

        if (Math.abs(delta) < MIN_ANGLE_DELTA || Math.abs(delta) > MAX_ANGLE_DELTA) {
            lastAngle = currAngle;
            return true;
        }

        double ddx = emx - limbCX, ddy = emy - limbCY;
        double dist = Math.sqrt(ddx * ddx + ddy * ddy);
        if (dist < MIN_WRAP_RADIUS || dist > MAX_WRAP_RADIUS) {
            lastAngle = currAngle;
            return true;
        }

        rollAngleRad = (float) currAngle;

        if (delta > 0) {
            advanceWrap((float) delta, emx, emy);
        } else {
            bleedAccumulator = 0f;
            durabilityAccumulator = 0f;
            lapRadians = 0f;
        }

        lastAngle = currAngle;
        return true;
    }

    private void advanceWrap(float delta, double emx, double emy) {
        lapRadians += delta;
        while (lapRadians >= 2.0 * Math.PI) {
            lapRadians -= (float) (2.0 * Math.PI);
            lapsCompleted++;
        }

        bleedAccumulator += delta * BLEED_PER_RADIAN;
        durabilityAccumulator += delta * DURABILITY_PER_RADIAN;
        int bleedWhole = (int) bleedAccumulator;
        int durabilityWhole = (int) durabilityAccumulator;
        if (bleedWhole > 0 || durabilityWhole > 0) {
            bleedAccumulator -= bleedWhole;
            durabilityAccumulator -= durabilityWhole;
            PacketDistributor.sendToServer(new ReduceBleedPayload(
                    limb.ordinal(), bleedWhole, durabilityWhole, hand == InteractionHand.MAIN_HAND, targetEntityId));

            pointsSinceSound += durabilityWhole;
            if (pointsSinceSound >= POINTS_PER_SOUND) {
                pointsSinceSound -= POINTS_PER_SOUND;
                playWrapSound();
            }
        }

        if (rng.nextFloat() < 0.15f) {
            spawnParticle(emx + (rng.nextFloat() - 0.5f) * 20, emy + (rng.nextFloat() - 0.5f) * 20);
        }
    }

    @Override
    public boolean mouseReleased(double mx, double my, int button) {
        float scale = scaleFactor();
        if (button == 0) {
            lmbHeld = false;
            return true;
        }
        return super.mouseReleased(mx / scale, my / scale, button);
    }


    public void onDressingDepleted() {
        if (depleted) return;
        depleted = true;
        lmbHeld = false;
        closeCountdown = 60;
    }


    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) { onClose(); return true; }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean isPauseScreen() { return false; }


    private void playWrapSound() {
        Player p = minecraft.player;
        if (p == null) return;
        p.level().playLocalSound(p.getX(), p.getY(), p.getZ(),
                ModSounds.BANDAGE_WRAP.get(), SoundSource.PLAYERS,
                0.35f, 0.85f + rng.nextFloat() * 0.3f, false);
    }

    private void playHealedSound() {
        Player p = minecraft.player;
        if (p == null) return;
        p.level().playLocalSound(p.getX(), p.getY(), p.getZ(),
                ModSounds.BANDAGE_SUCCESS.get(), SoundSource.PLAYERS,
                0.9f, 1.0f, false);
    }
}
