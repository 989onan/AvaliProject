package com.lunkoashtail.avaliproject.screen.custom;

import com.lunkoashtail.avaliproject.AvaliProject;
import com.lunkoashtail.avaliproject.client.TargetDataCache;
import com.lunkoashtail.avaliproject.limb.BleedingTier;
import com.lunkoashtail.avaliproject.limb.Limb;
import com.lunkoashtail.avaliproject.limb.LimbConditions;
import com.lunkoashtail.avaliproject.limb.LimbData;
import com.lunkoashtail.avaliproject.limb.ModAttachments;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;













































public class LimbSelectionScreen extends Screen {

    
    
    

     
    private static final int WHEEL_RADIUS = 90;
     
    private static final int BTN_R        = 22;
     
    private static final int ICON_SIZE    = 16;

    private static final ResourceLocation SHRAPNEL_ICON =
            ResourceLocation.fromNamespaceAndPath(AvaliProject.MOD_ID, "textures/gui/shrapnel/expie_shrapnel.png");
    private static final int SHRAPNEL_ICON_TEX_W = 27, SHRAPNEL_ICON_TEX_H = 138;
    private static final int SHRAPNEL_BADGE_W = 8, SHRAPNEL_BADGE_H = 14;

    
    
    
    private static final int COL_OVERLAY      = 0xBB060612;
    private static final int COL_BTN_IDLE     = 0xFF1A1A2E;
    private static final int COL_BTN_HOVER    = 0xFF22334C;
    private static final int COL_BORDER_IDLE  = 0xFF4466AA;
    private static final int COL_BORDER_HOVER = 0xFF88AADD;
    private static final int COL_TEXT_IDLE    = 0xFFAABBCC;
    private static final int COL_TEXT_HOVER   = 0xFFFFFFFF;
    private static final int COL_TITLE        = 0xFFCCCCFF;
    private static final int COL_HINT         = 0xFF666666;
    private static final int COL_HEALTHY      = 0xFF44BB66;
    private static final int COL_BORDER_SHRAPNEL = 0xFFFFAA33;

    
    
    
    private static final Limb[] WHEEL_ORDER = {
            Limb.HEAD,      
            Limb.RIGHT_ARM, 
            Limb.RIGHT_LEG, 
            Limb.BACK,      
            Limb.LEFT_LEG,  
            Limb.LEFT_ARM   
    };

    
    
    

    @Nullable private final Consumer<Limb> onLimbSelected;
    @Nullable private Limb hoveredLimb = null;

    private final int targetEntityId;

     
    private final int[] btnX = new int[6];
    private final int[] btnY = new int[6];
    private int centerX, centerY;

    
    
    

    


    public LimbSelectionScreen(@Nullable Consumer<Limb> onLimbSelected) {
        this(onLimbSelected, localPlayerId());
    }

    public LimbSelectionScreen(@Nullable Consumer<Limb> onLimbSelected, int targetEntityId) {
        super(Component.translatable("screen.avaliproject.limb_selection"));
        this.onLimbSelected = onLimbSelected;
        this.targetEntityId = targetEntityId;
    }

    private static int localPlayerId() {
        Player player = Minecraft.getInstance().player;
        return player != null ? player.getId() : -1;
    }

    private boolean isSelf() {
        Player local = Minecraft.getInstance().player;
        return local != null && local.getId() == targetEntityId;
    }

    private LimbData resolveLimbData() {
        Player local = Minecraft.getInstance().player;
        if (isSelf()) return local != null ? local.getData(ModAttachments.LIMB_DATA) : new LimbData();
        LimbData data = new LimbData();
        for (Limb limb : Limb.values()) data.setBleed(limb, TargetDataCache.getBleed(targetEntityId, limb));
        return data;
    }

    private LimbConditions resolveLimbConditions() {
        Player local = Minecraft.getInstance().player;
        if (isSelf() && local != null) return local.getData(ModAttachments.LIMB_CONDITIONS);
        return new LimbConditions();
    }

    
    
    

    @Override
    protected void init() {
        super.init();
        centerX = width  / 2;
        centerY = height / 2;

        for (int i = 0; i < WHEEL_ORDER.length; i++) {
            double angleRad = Math.toRadians(-90.0 + i * 60.0); 
            btnX[i] = centerX + (int) (Math.cos(angleRad) * WHEEL_RADIUS);
            btnY[i] = centerY + (int) (Math.sin(angleRad) * WHEEL_RADIUS);
        }
    }

    
    
    

    @Override
    public void renderBackground(GuiGraphics gfx, int mx, int my, float partial) {
        
    }

    @Override
    public void render(GuiGraphics gfx, int mx, int my, float partial) {
        gfx.fill(0, 0, width, height, COL_OVERLAY);

        updateHoveredLimb(mx, my);

        drawWheelSpokes(gfx);
        drawPlayerModel(gfx, mx, my);
        drawLimbButtons(gfx);
        drawTitle(gfx);
        drawTooltip(gfx);

        super.render(gfx, mx, my, partial);
    }

     
    private void drawWheelSpokes(GuiGraphics gfx) {
        for (int i = 0; i < 6; i++) {
            drawLine(gfx, btnX[i], btnY[i], btnX[(i + 1) % 6], btnY[(i + 1) % 6], 0x33334466);
            drawLine(gfx, centerX, centerY, btnX[i], btnY[i], 0x22334466);
        }
    }

    



    private void drawPlayerModel(GuiGraphics gfx, int mx, int my) {
        Player local = Minecraft.getInstance().player;
        if (local == null) return;

        Player renderTarget = local;
        if (!isSelf() && Minecraft.getInstance().level != null) {
            Entity entity = Minecraft.getInstance().level.getEntity(targetEntityId);
            if (entity instanceof Player targetPlayer) renderTarget = targetPlayer;
        }

        InventoryScreen.renderEntityInInventoryFollowsMouse(
                gfx,
                centerX - 28, centerY - 46,
                centerX + 28, centerY + 46,
                22, 0.0f, (float) mx, (float) my, renderTarget
        );
    }

    private void drawLimbButtons(GuiGraphics gfx) {
        LimbData data = resolveLimbData();
        LimbConditions conditions = resolveLimbConditions();

        for (int i = 0; i < WHEEL_ORDER.length; i++) {
            Limb limb  = WHEEL_ORDER[i];
            int  bleed = data.getBleed(limb);
            boolean hasShrapnel = conditions.getShrapnel(limb) > 0;
            drawButton(gfx, btnX[i], btnY[i], limb, bleed, hasShrapnel, limb == hoveredLimb);
        }
    }

    











    private void drawButton(GuiGraphics gfx, int cx, int cy, Limb limb, int bleed, boolean hasShrapnel, boolean hovered) {
        int x = cx - BTN_R, y = cy - BTN_R;
        int w = BTN_R * 2,  h = BTN_R * 2;   

        gfx.fill(x, y, x + w, y + h, hovered ? COL_BTN_HOVER : COL_BTN_IDLE);
        int bc = hasShrapnel ? COL_BORDER_SHRAPNEL : (hovered ? COL_BORDER_HOVER : COL_BORDER_IDLE);
        gfx.fill(x,         y,         x + w, y + 1,     bc);
        gfx.fill(x,         y + h - 1, x + w, y + h,     bc);
        gfx.fill(x,         y,         x + 1, y + h,     bc);
        gfx.fill(x + w - 1, y,         x + w, y + h,     bc);

        
        
        BleedingTier tier = BleedingTier.fromBleedValue(bleed);
        if (tier != null) {
            int iconX = x + w - ICON_SIZE - 2; 
            int iconY = y + 2;                  
            drawTierIcon(gfx, tier, iconX, iconY);
        }

        if (hasShrapnel) {
            gfx.blit(SHRAPNEL_ICON, x + 2, y + 2, SHRAPNEL_BADGE_W, SHRAPNEL_BADGE_H,
                    0f, 0f, SHRAPNEL_ICON_TEX_W, SHRAPNEL_ICON_TEX_H, SHRAPNEL_ICON_TEX_W, SHRAPNEL_ICON_TEX_H);
        }

        
        int tc = hovered ? COL_TEXT_HOVER : COL_TEXT_IDLE;
        String[] parts = limb.getDisplayName().getString().split(" ", 2);
        
        int textCX = (tier != null) ? cx - 4 : cx;
        int textY  = (parts.length == 2) ? cy - 8 : cy - 4;
        for (String part : parts) {
            gfx.drawCenteredString(font, part, textCX, textY, tc);
            textY += font.lineHeight + 1;
        }

        
        int barW = w - 8, barH = 4;
        int barX = x + 4, barY = y + h - barH - 4;
        gfx.fill(barX, barY, barX + barW, barY + barH, 0xFF111111); 
        if (bleed > 0 && tier != null) {
            int fillW = Math.max(2, (int) ((float) bleed / LimbData.MAX_BLEED * barW));
            gfx.fill(barX, barY, barX + fillW, barY + barH, tier.getColor());
        }
    }

    private void drawTierIcon(GuiGraphics gfx, BleedingTier tier, int iconX, int iconY) {
        
        gfx.blit(tier.icon, iconX, iconY, 0, 0, ICON_SIZE, ICON_SIZE);
    }

    private void drawTitle(GuiGraphics gfx) {
        String title = (onLimbSelected != null)
                ? Component.translatable("screen.avaliproject.limb_selection.choose").getString()
                : Component.translatable("screen.avaliproject.limb_selection").getString();
        gfx.drawCenteredString(font, title, width / 2, 10, COL_TITLE);
        gfx.drawCenteredString(font, "[Esc / Right-click to close]", width / 2, height - 14, COL_HINT);
    }

    



    private void drawTooltip(GuiGraphics gfx) {
        if (hoveredLimb == null) return;

        LimbData data = resolveLimbData();
        LimbConditions conditions = resolveLimbConditions();
        int bleed = data.getBleed(hoveredLimb);
        boolean hasShrapnel = conditions.getShrapnel(hoveredLimb) > 0;

        BleedingTier tier = BleedingTier.fromBleedValue(bleed);

        String statusStr;
        int statusCol;
        if (tier == null) {
            statusStr = hasShrapnel ? "Shrapnel embedded" : "Healthy";
            statusCol = hasShrapnel ? COL_BORDER_SHRAPNEL : COL_HEALTHY;
        } else {
            statusStr = tier.getDisplayName().getString() + " (" + bleed + "%)" + (hasShrapnel ? " + Shrapnel" : "");
            statusCol = hasShrapnel ? COL_BORDER_SHRAPNEL : tier.getColor();
        }

        String label = hoveredLimb.getDisplayName().getString() + "  —  " + statusStr;
        gfx.drawCenteredString(font, label, width / 2, height - 28, statusCol);

        if (onLimbSelected != null) {
            gfx.drawCenteredString(font, "Click to treat this limb", width / 2, height - 16, 0x88FFFFFF);
        } else if (hasShrapnel) {
            gfx.drawCenteredString(font, "Click to remove the shrapnel", width / 2, height - 16, 0xFFFFAA33);
        }
    }

    
    
    

    private void updateHoveredLimb(int mx, int my) {
        hoveredLimb = null;
        for (int i = 0; i < WHEEL_ORDER.length; i++) {
            if (Math.abs(mx - btnX[i]) <= BTN_R && Math.abs(my - btnY[i]) <= BTN_R) {
                hoveredLimb = WHEEL_ORDER[i];
                return;
            }
        }
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        if (button == 1) { onClose(); return true; } 

        if (button == 0 && hoveredLimb != null) {
            if (onLimbSelected != null) {
                onLimbSelected.accept(hoveredLimb);
            } else {
                LimbConditions conditions = resolveLimbConditions();
                int shrapnel = conditions.getShrapnel(hoveredLimb);
                if (shrapnel > 0) {
                    Minecraft.getInstance().setScreen(new ShrapnelMinigameScreen(hoveredLimb, shrapnel));
                } else {
                    onClose();
                }
            }
            return true;
        }
        return super.mouseClicked(mx, my, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) { onClose(); return true; } 
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean isPauseScreen() { return false; }

    
    
    

     
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
