package com.lunkoashtail.avaliproject.screen.custom;

import com.lunkoashtail.avaliproject.item.custom.DrugType;
import com.lunkoashtail.avaliproject.limb.Limb;
import com.lunkoashtail.avaliproject.network.SyringeEffectPayload;
import com.lunkoashtail.avaliproject.sound.ModSounds;
import org.jetbrains.annotations.Nullable;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Random;





























public class SyringeMinigameScreen extends Screen {

    
    
    

    private enum Phase { AIMING, INSERTED, SUCCESS }

    
    
    

     
    private static final int VEIN_RADIUS = 10;

    



    private static final float DRAG_FOR_FULL = 90f;

    
    
    

    
    private static final int ARM_W = 54;
    private static final int ARM_H = 132;

    
    private static final int SYR_W      = 12;
    private static final int SYR_H      = 38;
    private static final int NEEDLE_LEN = 14;
    private static final int PLUNGER_H  = 6;

    
    private static final int COL_PANEL  = 0xBB080812;
    private static final int COL_BORDER = 0xFF334466;

    
    
    

    private final DrugType drugType;
    private final float availableDosage;
     
    @Nullable
    private final Limb targetLimb;
    private final InteractionHand hand;
    private final int targetEntityId;
    private Phase phase = Phase.AIMING;

    
    private int armCX, armCY;
    private int veinX, veinY; 

    
    private int insertX, insertY;

    private float injectionProgress = 0f;

    
    private int closeCountdown = 0;

    
    private float   veinPulse    = 0f;
    private boolean veinPulseUp  = true;

    
    private final Random rng = new Random();
    private float shakeX, shakeY;

    
    private static final int MAX_PARTICLES = 28;
    private final float[][] particles = new float[MAX_PARTICLES][5];
    private int particleCount = 0;

    
    
    

    





    public SyringeMinigameScreen(DrugType drugType, float availableDosage, @Nullable Limb targetLimb, InteractionHand hand, int targetEntityId) {
        super(Component.translatable("screen.avaliproject.syringe_minigame"));
        this.drugType        = drugType;
        this.availableDosage = availableDosage;
        this.targetLimb      = targetLimb;
        this.hand            = hand;
        this.targetEntityId  = targetEntityId;
    }

    
    
    

    @Override
    protected void init() {
        super.init();
        armCX = width  / 2;
        armCY = height / 2 + 5;
        
        veinX = armCX - 7;
        veinY = armCY - 18;
    }

    
    
    

    @Override
    public void renderBackground(GuiGraphics gfx, int mx, int my, float partial) {
        
        
    }

    @Override
    public void render(GuiGraphics gfx, int mx, int my, float partial) {
        gfx.fill(0, 0, width, height, 0xCC000000);

        
        
        int smx = mx + (int) shakeX;
        int smy = my + (int) shakeY;

        drawStatsPanel(gfx);
        drawArm(gfx);
        drawVein(gfx, smx, smy);
        drawParticles(gfx);
        drawSyringe(gfx, smx, smy);
        drawHUD(gfx, smx, smy);

        super.render(gfx, mx, my, partial);
    }

    

    private void drawStatsPanel(GuiGraphics gfx) {
        int pw = 148, ph = 124;
        int px = 8, py = height / 2 - ph / 2;
        gfx.fill(px, py, px + pw, py + ph, COL_PANEL);
        gfx.fill(px,          py,          px + pw,     py + 1,      COL_BORDER);
        gfx.fill(px,          py + ph - 1, px + pw,     py + ph,     COL_BORDER);
        gfx.fill(px,          py,          px + 1,      py + ph,     COL_BORDER);
        gfx.fill(px + pw - 1, py,          px + pw,     py + ph,     COL_BORDER);
    }

    

    private void drawArm(GuiGraphics gfx) {
        int x = armCX - ARM_W / 2;
        int y = armCY - ARM_H / 2;

        final int BASE   = 0xFF0E0E0E; 
        final int MID    = 0xFF181818; 
        final int HILIGHT = 0xFF262626; 
        final int DEEP   = 0xFF050505; 
        final int FUR1   = 0xFF1C1C1C; 
        final int FUR2   = 0xFF282828; 

        
        gfx.fill(x + 2,  y + 5,             x + ARM_W - 2,  y + ARM_H - 5,  BASE);
        gfx.fill(x,      y + 12,            x + ARM_W,       y + ARM_H - 12, BASE);
        
        gfx.fill(x + 5,  y + 2,             x + ARM_W - 5,  y + 5,          BASE);
        gfx.fill(x + 9,  y,                 x + ARM_W - 9,  y + 2,          BASE);
        
        gfx.fill(x + 5,  y + ARM_H - 5,    x + ARM_W - 5,  y + ARM_H - 2,  BASE);
        gfx.fill(x + 9,  y + ARM_H - 2,    x + ARM_W - 9,  y + ARM_H,      BASE);

        
        gfx.fill(x + ARM_W / 2 - 4, y + 4,  x + ARM_W / 2 + 4, y + ARM_H - 4, DEEP);
        
        gfx.fill(x + ARM_W - 10,    y + 8,  x + ARM_W - 3,      y + ARM_H - 8, MID);
        
        gfx.fill(x + ARM_W - 4,     y + 10, x + ARM_W - 2,      y + ARM_H - 10, HILIGHT);

        
        for (int fy = y + 6; fy < y + ARM_H - 6; fy += 3) {
            boolean longStrand = ((fy - y) % 9 == 0);
            int len = longStrand ? 4 : 2;
            int col = longStrand ? FUR2 : FUR1;
            gfx.fill(x - len, fy, x, fy + 1, col);
            if (longStrand) gfx.fill(x - len - 1, fy, x - len, fy + 1, DEEP);
        }

        
        for (int fy = y + 6; fy < y + ARM_H - 6; fy += 3) {
            boolean longStrand = ((fy - y) % 9 == 3);
            int len = longStrand ? 4 : 2;
            int col = longStrand ? FUR2 : FUR1;
            gfx.fill(x + ARM_W, fy, x + ARM_W + len, fy + 1, col);
            if (longStrand) gfx.fill(x + ARM_W + len, fy, x + ARM_W + len + 1, fy + 1, DEEP);
        }

        
        for (int fx = x + 9; fx < x + ARM_W - 9; fx += 4) {
            boolean tall = ((fx - x) % 8 == 1);
            int tuftH = tall ? 3 : 2;
            gfx.fill(fx, y - tuftH, fx + 2, y, tall ? FUR2 : FUR1);
        }

        
        for (int fx = x + 9; fx < x + ARM_W - 9; fx += 4) {
            boolean tall = ((fx - x) % 8 == 1);
            int tuftH = tall ? 3 : 2;
            gfx.fill(fx, y + ARM_H, fx + 2, y + ARM_H + tuftH, tall ? FUR2 : FUR1);
        }
    }

    

    private void drawVein(GuiGraphics gfx, int smx, int smy) {
        boolean near = (phase == Phase.AIMING) && distToVein(smx, smy) <= VEIN_RADIUS * 2.5;

        
        int lineAlpha = near ? (int)(0x65 + veinPulse * 0x55) : 0x48;
        gfx.fill(veinX - 1, armCY - ARM_H / 2 + 14, veinX + 2, armCY + ARM_H / 2 - 14,
                (lineAlpha << 24) | 0x5566EE);

        
        int bulgeAlpha = near ? (int)(0x88 + veinPulse * 0x66) : 0x58;
        gfx.fill(veinX - 4, veinY - 5, veinX + 5, veinY + 5, (bulgeAlpha << 24) | 0x7799FF);
        gfx.fill(veinX - 2, veinY - 3, veinX + 3, veinY + 3,
                (Math.min(0xFF, bulgeAlpha + 0x44) << 24) | 0x99AAFF);

        
        if (phase == Phase.AIMING) {
            int chAlpha = near ? 0xCC : 0x44;
            int chCol   = (chAlpha << 24) | 0xFFEE88;
            gfx.fill(veinX - 9, veinY,     veinX - 6, veinY + 1, chCol);
            gfx.fill(veinX + 6, veinY,     veinX + 9, veinY + 1, chCol);
            gfx.fill(veinX,     veinY - 9, veinX + 1, veinY - 6, chCol);
            gfx.fill(veinX,     veinY + 6, veinX + 1, veinY + 9, chCol);
        }

        
        if (phase == Phase.INSERTED || phase == Phase.SUCCESS) {
            gfx.fill(insertX - 2, insertY - 1, insertX + 3, insertY + 2, 0xCCFF3333);
        }
    }

    

    








    private void drawSyringe(GuiGraphics gfx, int mx, int my) {
        int tipX = (phase == Phase.AIMING) ? mx     : insertX;
        int tipY = (phase == Phase.AIMING) ? my     : insertY;

        
        int needleTopY = tipY - NEEDLE_LEN;
        gfx.fill(tipX,     needleTopY,     tipX + 1, tipY,             0xFFDDDDDD);
        gfx.fill(tipX - 1, needleTopY,     tipX + 2, needleTopY + 3,   0xFFAAAAAA); 

        
        int barX = tipX - SYR_W / 2;
        int barY = needleTopY - SYR_H;

        gfx.fill(barX,              barY,             barX + SYR_W,     barY + SYR_H,     0x44BBBBBB); 
        gfx.fill(barX,              barY,             barX + SYR_W,     barY + 1,          0xFFDDDDDD); 
        gfx.fill(barX,              barY + SYR_H - 1, barX + SYR_W,     barY + SYR_H,     0xFFDDDDDD); 
        gfx.fill(barX,              barY,             barX + 1,          barY + SYR_H,     0xFFDDDDDD); 
        gfx.fill(barX + SYR_W - 1,  barY,             barX + SYR_W,     barY + SYR_H,     0xFFDDDDDD); 

        
        int fluidCol = switch (drugType) {
            case FENTANYL -> 0xCCFFCC44; 
            case HEROIN   -> 0xCCDDBB88; 
        };

        
        int innerH   = SYR_H - 4;
        int fluidPx  = (int)(innerH * (1f - injectionProgress));
        if (fluidPx > 0) {
            int fluidTop = barY + 2 + (innerH - fluidPx);
            gfx.fill(barX + 2, fluidTop, barX + SYR_W - 2, barY + SYR_H - 2, fluidCol);
        }

        
        for (int i = 1; i <= 3; i++) {
            int markY = barY + i * SYR_H / 4;
            gfx.fill(barX + SYR_W - 4, markY, barX + SYR_W - 1, markY + 1, 0x88FFFFFF);
        }
        
        gfx.fill(barX + 2, barY + 2, barX + 4, barY + SYR_H - 2, 0x33FFFFFF);

        
        
        int maxTravel    = SYR_H - PLUNGER_H - 4;
        int plungerOffset = (phase == Phase.AIMING) ? 0 : (int)(maxTravel * injectionProgress);
        int pY           = barY + plungerOffset;

        gfx.fill(barX - 3,     pY,             barX + SYR_W + 3, pY + PLUNGER_H,  0xFFAAAAAA); 
        gfx.fill(barX + 1,     pY + 1,         barX + SYR_W - 1, pY + PLUNGER_H,  0xFF777777); 
        gfx.fill(barX + 2,     pY + 2,         barX + SYR_W - 2, pY + 3,          0xFF555555); 

        
        if (plungerOffset == 0) {
            gfx.fill(tipX - 1, barY - 11, tipX + 2, barY, 0xFF999999);
        }
    }

    

    private void drawParticles(GuiGraphics gfx) {
        for (int i = 0; i < particleCount; i++) {
            int alpha = (int)(particles[i][4] * 210f);
            if (alpha < 6) continue;
            int px = (int) particles[i][0], py = (int) particles[i][1];
            gfx.fill(px, py, px + 2, py + 2, (alpha << 24) | 0xFF3333);
        }
    }

    

    private void drawHUD(GuiGraphics gfx, int mx, int my) {
        int hx = 14;
        int hy = height / 2 - 56;

        
        String drugName = switch (drugType) {
            case FENTANYL -> "Fentanyl";
            case HEROIN   -> "Heroin";
        };
        String limbSuffix = (targetLimb != null) ? " → " + targetLimb.getDisplayName().getString() : "";
        gfx.drawString(font, "Injection: " + drugName + limbSuffix, hx, hy, 0xFFDDDDFF, false);
        gfx.drawString(font, "Loaded: " + (int) availableDosage + " mL", hx, hy - 12, 0xFF88CCFF, false);

        
        String phaseLabel = switch (phase) {
            case AIMING   -> "Step 1: Aim at the glowing vein";
            case INSERTED -> "Step 2: Hold RIGHT-click, move mouse to adjust";
            case SUCCESS  -> "Injection complete!";
        };
        gfx.drawString(font, phaseLabel, hx, hy + 12, 0xAABBCC, false);

        
        if (injectionProgress > 0f || phase != Phase.AIMING) {
            int barW = 130, barH = 9;
            int barX = hx, barY = hy + 30;

            gfx.fill(barX - 1, barY - 1, barX + barW + 1, barY + barH + 1, 0xFF111111); 
            gfx.fill(barX, barY, barX + barW, barY + barH, 0xFF1C1C2A);                  

            int fillW   = (int)(barW * Math.min(injectionProgress, 1f));
            int barCol  = injectionProgress >= 1f ? 0xFF44BB66 : 0xFFAA5555;
            gfx.fill(barX, barY, barX + fillW, barY + barH, barCol);
            gfx.fill(barX, barY, barX + fillW, barY + 3, 0x33FFFFFF); 

            int injectedMl = (int) (availableDosage * Math.min(injectionProgress, 1f));
            gfx.drawString(font, injectedMl + " / " + (int) availableDosage + " mL", barX, barY + barH + 4, 0x99BBEE, false);
        }

        
        float shakeMag = getShakeMagnitude();
        if (shakeMag > 2f) {
            String warn = shakeMag > 5f ? "Hands shaking badly!" : "Hands unsteady";
            gfx.drawString(font, warn, hx, hy + 68, 0xFFFFAA22, false);
        }

        
        if (phase == Phase.AIMING) {
            double dist = distToVein(mx, my);
            if (dist < VEIN_RADIUS * 4) {
                int pct = (int)(100.0 * Math.max(0, 1.0 - dist / (VEIN_RADIUS * 4.0)));
                gfx.drawCenteredString(font, "Aim: " + pct + "%",
                        width / 2, armCY + ARM_H / 2 + 14, 0xFFCCCC44);
            }
        }

        
        if (phase != Phase.SUCCESS) {
            String hint = switch (phase) {
                case AIMING   -> "Click on the vein to insert needle";
                case INSERTED -> "Hold RIGHT-click and move mouse to adjust  |  Release to confirm";
                default       -> "";
            };
            gfx.drawCenteredString(font, hint, width / 2, height - 30, 0xCCCCCC);
            gfx.drawCenteredString(font, "[Esc to cancel]", width / 2, height - 18, 0x666666);
        } else {
            gfx.drawCenteredString(font, "Injected successfully!", width / 2, armCY - ARM_H / 2 - 22, 0xFF55FF88);
        }
    }

    
    
    

    @Override
    public void tick() {
        super.tick();
        updateShake();
        tickParticles();
        tickVeinPulse();
        if (closeCountdown > 0 && --closeCountdown == 0) onClose();
    }

    private void tickVeinPulse() {
        float step = 0.035f;
        if (veinPulseUp) { veinPulse = Math.min(1f, veinPulse + step); if (veinPulse >= 1f) veinPulseUp = false; }
        else             { veinPulse = Math.max(0f, veinPulse - step); if (veinPulse <= 0f) veinPulseUp = true;  }
    }

    private void updateShake() {
        float mag = getShakeMagnitude();
        shakeX = mag > 0f ? (rng.nextFloat() - 0.5f) * mag : 0f;
        shakeY = mag > 0f ? (rng.nextFloat() - 0.5f) * mag : 0f;
    }

    



    private float getShakeMagnitude() {
        Player p = minecraft.player;
        if (p == null) return 0f;
        float s = 0f;
        if (p.hasEffect(MobEffects.POISON))            s += 3.5f;
        if (p.hasEffect(MobEffects.WITHER))            s += 6f;
        if (p.hasEffect(MobEffects.WEAKNESS))          s += 2f;
        if (p.hasEffect(MobEffects.MOVEMENT_SLOWDOWN)) s += 1.5f;
        if (p.hasEffect(MobEffects.CONFUSION))          s += 4f;
        if (p.getHealth() < p.getMaxHealth() * 0.25f)  s += 4.5f;
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
                if (i < particleCount - 1)
                    System.arraycopy(particles[particleCount - 1], 0, particles[i], 0, 5);
                particleCount--;
            }
        }
    }

    private void spawnBloodParticle(int x, int y) {
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
        if (button == 0 && phase == Phase.AIMING) {
            
            
            
            attemptInsertion(mx + shakeX, my + shakeY);
            return true;
        }

        if (button == 1 && phase == Phase.INSERTED) {
            return true;
        }
        return super.mouseClicked(mx, my, button);
    }

    






    private void attemptInsertion(double emx, double emy) {
        if (distToVein((int) emx, (int) emy) <= VEIN_RADIUS) {
            phase   = Phase.INSERTED;
            insertX = veinX;
            insertY = veinY;
            spawnBloodParticle(veinX,     veinY);
            spawnBloodParticle(veinX + 1, veinY + 2);
            playInsertSound();
        } else {
            
            spawnBloodParticle((int) emx, (int) emy);
        }
    }

    




    @Override
    public boolean mouseDragged(double mx, double my, int button, double dx, double dy) {
        if (button != 1 || phase != Phase.INSERTED) {
            return super.mouseDragged(mx, my, button, dx, dy);
        }

        injectionProgress = Math.max(0f, Math.min(1f, injectionProgress + (float) (dy / DRAG_FOR_FULL)));
        if (dy > 0 && rng.nextInt(7) == 0) spawnBloodParticle(insertX, insertY);

        return true;
    }

    @Override
    public boolean mouseReleased(double mx, double my, int button) {
        if (button == 1 && phase == Phase.INSERTED) {
            if (injectionProgress > 0f) {
                triggerSuccess();
            }
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
        phase          = Phase.SUCCESS;
        closeCountdown = 80; 

        float injectedAmount = availableDosage * Math.min(injectionProgress, 1f);

        PacketDistributor.sendToServer(new SyringeEffectPayload(
                hand == InteractionHand.MAIN_HAND ? 0 : 1,
                drugType.ordinal(),
                injectedAmount,
                targetEntityId));

        playInjectSound();
    }

    private void playInsertSound() {
        Player p = minecraft.player;
        if (p == null) return;
        p.level().playLocalSound(p.getX(), p.getY(), p.getZ(),
                ModSounds.SYRINGE_STAB.get(), SoundSource.PLAYERS,
                0.6f, 1.0f + rng.nextFloat() * 0.2f, false);
    }

    private void playInjectSound() {
        Player p = minecraft.player;
        if (p == null) return;
        p.level().playLocalSound(p.getX(), p.getY(), p.getZ(),
                ModSounds.SYRINGE_INJECT.get(), SoundSource.PLAYERS,
                0.9f, 1.0f, false);
    }

    
    
    

    private double distToVein(int mx, int my) {
        double dx = mx - veinX, dy = my - veinY;
        return Math.sqrt(dx * dx + dy * dy);
    }
}
