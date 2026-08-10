package com.lunkoashtail.avaliproject.screen.custom;

import com.lunkoashtail.avaliproject.component.DrugDosage;
import com.lunkoashtail.avaliproject.component.ModDataComponents;
import com.lunkoashtail.avaliproject.component.SyringeContents;
import com.lunkoashtail.avaliproject.item.ModItems;
import com.lunkoashtail.avaliproject.item.custom.DrugType;
import com.lunkoashtail.avaliproject.item.custom.SyringeItem;
import com.lunkoashtail.avaliproject.network.SyringeLoadPayload;
import com.lunkoashtail.avaliproject.sound.ModSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;

public class SyringeDrawScreen extends Screen {

    private enum Phase { SELECT, DRAW }

    private static final float DRAG_FOR_FULL = 90f;

    private static final int ROW_W = 220;
    private static final int ROW_H = 20;

    private static final int COL_BORDER    = 0xFF334466;
    private static final int COL_ROW       = 0xFF15151F;
    private static final int COL_ROW_HOVER = 0xFF223049;

    private record Candidate(int slotIndex, DrugType drugType, float dosage) {}

    private final InteractionHand hand;
    private Phase phase = Phase.SELECT;

    private final List<Candidate> candidates = new ArrayList<>();
    private Candidate selected;

    private float drawnAmount = 0f;
    private float capacityLeft = SyringeItem.CAPACITY;

    public SyringeDrawScreen(InteractionHand hand) {
        super(Component.translatable("screen.avaliproject.syringe_draw"));
        this.hand = hand;
    }


    @Override
    protected void init() {
        super.init();
        candidates.clear();

        Player player = Minecraft.getInstance().player;
        if (player == null) return;

        ItemStack syringeStack = player.getItemInHand(hand);
        SyringeContents contents = syringeStack.get(ModDataComponents.SYRINGE_CONTENTS);
        capacityLeft = SyringeItem.CAPACITY - (contents != null ? contents.dosage() : 0f);

        Inventory inv = player.getInventory();
        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack stack = inv.getItem(i);

            DrugType type;
            if (stack.is(ModItems.FENTANYL.get())) type = DrugType.FENTANYL;
            else if (stack.is(ModItems.HEROIN.get())) type = DrugType.HEROIN;
            else continue;

            if (contents != null && contents.drugType() != type) continue;

            DrugDosage dosage = stack.get(ModDataComponents.DRUG_DOSAGE);
            if (dosage == null || dosage.dosage() <= 0f) continue;

            candidates.add(new Candidate(i, type, dosage.dosage()));
        }
    }


    @Override
    public void renderBackground(GuiGraphics gfx, int mx, int my, float partial) {
    }

    @Override
    public void render(GuiGraphics gfx, int mx, int my, float partial) {
        gfx.fill(0, 0, width, height, 0xCC000000);

        if (phase == Phase.SELECT) {
            renderSelect(gfx, mx, my);
        } else {
            renderDraw(gfx, mx, my);
        }

        super.render(gfx, mx, my, partial);
    }

    private void renderSelect(GuiGraphics gfx, int mx, int my) {
        int top = height / 2 - (candidates.size() * ROW_H) / 2;

        gfx.drawCenteredString(font, "Select a vial to draw from", width / 2, top - 20, 0xFFDDDDFF);

        if (candidates.isEmpty()) {
            gfx.drawCenteredString(font, "No usable fentanyl/heroin in inventory", width / 2, height / 2, 0xFFAA5555);
            gfx.drawCenteredString(font, "[Right-click or Esc to cancel]", width / 2, height - 18, 0x666666);
            return;
        }

        for (int i = 0; i < candidates.size(); i++) {
            Candidate c = candidates.get(i);
            int rx = width / 2 - ROW_W / 2;
            int ry = top + i * ROW_H;
            boolean hovered = mx >= rx && mx <= rx + ROW_W && my >= ry && my <= ry + ROW_H;

            gfx.fill(rx, ry, rx + ROW_W, ry + ROW_H, hovered ? COL_ROW_HOVER : COL_ROW);
            gfx.fill(rx, ry, rx + ROW_W, ry + 1, COL_BORDER);

            String name = c.drugType() == DrugType.FENTANYL ? "Fentanyl" : "Heroin";
            gfx.drawString(font, name, rx + 6, ry + 6, 0xFFEEEEFF, false);
            gfx.drawString(font, (int) c.dosage() + " mL", rx + ROW_W - 60, ry + 6, 0xFF88CCFF, false);
        }

        gfx.drawCenteredString(font, "[Right-click or Esc to cancel]", width / 2, height - 18, 0x666666);
    }

    private void renderDraw(GuiGraphics gfx, int mx, int my) {
        int cx = width / 2, cy = height / 2;
        String drugName = selected.drugType() == DrugType.FENTANYL ? "Fentanyl" : "Heroin";

        gfx.drawCenteredString(font, "Drawing " + drugName, cx, cy - 70, 0xFFDDDDFF);
        gfx.drawCenteredString(font, "Hold RIGHT-click and move mouse to adjust", cx, cy - 58, 0xAABBCC);

        int vialW = 20, vialH = 50;
        int vx = cx - vialW / 2, vy = cy - 10;
        float vialFrac = 1f - (drawnAmount / Math.max(1f, selected.dosage()));
        gfx.fill(vx, vy, vx + vialW, vy + vialH, 0x33BBBBBB);
        int fluidH = (int) (vialH * vialFrac);
        gfx.fill(vx + 2, vy + (vialH - fluidH), vx + vialW - 2, vy + vialH - 2, 0xCCDDBB88);
        gfx.fill(vx, vy, vx + vialW, vy + 1, 0xFFDDDDDD);
        gfx.fill(vx, vy + vialH - 1, vx + vialW, vy + vialH, 0xFFDDDDDD);

        float maxDraw = Math.min(selected.dosage(), capacityLeft);
        float frac = maxDraw <= 0f ? 0f : drawnAmount / maxDraw;

        int barW = 160, barH = 10;
        int barX = cx - barW / 2, barY = cy + 60;
        gfx.fill(barX - 1, barY - 1, barX + barW + 1, barY + barH + 1, 0xFF111111);
        gfx.fill(barX, barY, barX + barW, barY + barH, 0xFF1C1C2A);
        gfx.fill(barX, barY, barX + (int) (barW * Math.min(1f, frac)), barY + barH, 0xFF4488BB);

        gfx.drawCenteredString(font, (int) drawnAmount + " / " + (int) maxDraw + " mL", cx, barY + barH + 6, 0x99BBEE);
        gfx.drawCenteredString(font, "[Hold RIGHT-click to adjust  |  Left-click to confirm  |  Esc to cancel]", cx, height - 18, 0x666666);
    }


    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        if (phase == Phase.SELECT) {
            if (button == 1) { onClose(); return true; }

            if (button == 0 && !candidates.isEmpty()) {
                int top = height / 2 - (candidates.size() * ROW_H) / 2;
                for (int i = 0; i < candidates.size(); i++) {
                    int rx = width / 2 - ROW_W / 2;
                    int ry = top + i * ROW_H;
                    if (mx >= rx && mx <= rx + ROW_W && my >= ry && my <= ry + ROW_H) {
                        selected = candidates.get(i);
                        phase = Phase.DRAW;
                        return true;
                    }
                }
            }
            return super.mouseClicked(mx, my, button);
        }

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
        if (button != 1 || phase != Phase.DRAW) {
            return super.mouseDragged(mx, my, button, dx, dy);
        }

        float maxDraw = Math.min(selected.dosage(), capacityLeft);
        drawnAmount = Math.max(0f, Math.min(maxDraw, drawnAmount - (float) (dy / DRAG_FOR_FULL) * maxDraw));
        return true;
    }

    private void commitDraw() {
        if (selected != null && drawnAmount > 0.5f) {
            PacketDistributor.sendToServer(new SyringeLoadPayload(
                    hand == InteractionHand.MAIN_HAND ? 0 : 1,
                    selected.slotIndex(),
                    selected.drugType().ordinal(),
                    drawnAmount));
            playDrawSound();
        }
        onClose();
    }

    private void playDrawSound() {
        Player p = minecraft.player;
        if (p == null) return;
        p.level().playLocalSound(p.getX(), p.getY(), p.getZ(),
                ModSounds.SYRINGE_STAB.get(), SoundSource.PLAYERS, 0.5f, 1.3f, false);
    }


    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) { onClose(); return true; }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean isPauseScreen() { return false; }
}
