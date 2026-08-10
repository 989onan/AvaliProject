package com.lunkoashtail.avaliproject.screen.custom;

import com.lunkoashtail.avaliproject.item.custom.AugmentItem;
import com.lunkoashtail.avaliproject.limb.ModAttachments;
import com.lunkoashtail.avaliproject.pack.AugmentInventoryData;
import com.lunkoashtail.avaliproject.screen.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class AugmentMenu extends AbstractContainerMenu {
    private static final int AUGMENT_SLOT_COUNT = AugmentInventoryData.SLOT_COUNT;

    private final Player owner;
    private final SimpleContainer container;

    public AugmentMenu(int containerId, Inventory inv, FriendlyByteBuf extraData) {
        this(containerId, inv);
    }

    public AugmentMenu(int containerId, Inventory inv) {
        super(ModMenuTypes.AUGMENT_MENU.get(), containerId);
        this.owner = inv.player;
        this.container = new SimpleContainer(AUGMENT_SLOT_COUNT);
        List<ItemStack> stored = owner.getData(ModAttachments.AUGMENT_INVENTORY).items();
        for (int i = 0; i < AUGMENT_SLOT_COUNT; i++) {
            container.setItem(i, stored.get(i).copy());
        }

        for (int i = 0; i < 4; i++) {
            this.addSlot(new AugmentSlot(container, i, 21, 21 + i * 18));
        }
        for (int i = 0; i < 4; i++) {
            this.addSlot(new AugmentSlot(container, 4 + i, 157, 21 + i * 18));
        }

        int invX = 16;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(inv, col + row * 9 + 9, invX + col * 18, 128 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(inv, col, invX + col * 18, 186));
        }
    }

    private class AugmentSlot extends Slot {
        AugmentSlot(SimpleContainer container, int index, int x, int y) {
            super(container, index, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return stack.isEmpty() || stack.getItem() instanceof AugmentItem;
        }

        @Override
        public void setChanged() {
            super.setChanged();
            List<ItemStack> snapshot = new ArrayList<>(AUGMENT_SLOT_COUNT);
            for (int i = 0; i < AUGMENT_SLOT_COUNT; i++) {
                snapshot.add(container.getItem(i).copy());
            }
            owner.setData(ModAttachments.AUGMENT_INVENTORY, new AugmentInventoryData(snapshot));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot sourceSlot = slots.get(index);
        if (sourceSlot == null || !sourceSlot.hasItem())
            return ItemStack.EMPTY;
        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copy = sourceStack.copy();

        if (index < AUGMENT_SLOT_COUNT) {
            if (!moveItemStackTo(sourceStack, AUGMENT_SLOT_COUNT, slots.size(), true))
                return ItemStack.EMPTY;
        } else {
            if (!(sourceStack.getItem() instanceof AugmentItem) || !moveItemStackTo(sourceStack, 0, AUGMENT_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;
            }
        }

        if (sourceStack.isEmpty())
            sourceSlot.set(ItemStack.EMPTY);
        else
            sourceSlot.setChanged();
        sourceSlot.onTake(player, sourceStack);
        return copy;
    }

    @Override
    public boolean stillValid(Player player) {
        return player == owner;
    }
}
