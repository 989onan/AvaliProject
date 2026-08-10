package com.lunkoashtail.avaliproject.screen.custom;

import com.lunkoashtail.avaliproject.creativetab.GalaxyCategory;
import com.lunkoashtail.avaliproject.entity.custom.AvaliEntity;
import com.lunkoashtail.avaliproject.screen.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.SimpleContainer;

public class AvaliEquipMenu extends AbstractContainerMenu {
    private static final int MAINHAND_SLOT = 0;
    private static final int OFFHAND_SLOT = 1;
    private static final int HEAD_SLOT = 2;
    private static final int CHEST_SLOT = 3;
    private static final int LEGS_SLOT = 4;
    private static final int FEET_SLOT = 5;
    private static final int AVALI_SLOT_COUNT = 6;

    public final AvaliEntity avali;

    public AvaliEquipMenu(int containerId, Inventory inv, FriendlyByteBuf extraData) {
        this(containerId, inv, resolveEntity(inv, extraData.readVarInt()));
    }

    public AvaliEquipMenu(int containerId, Inventory inv, AvaliEntity avali) {
        super(ModMenuTypes.AVALI_EQUIP_MENU.get(), containerId);
        this.avali = avali;
        SimpleContainer container = avali.getEquipmentContainer();
        container.startOpen(inv.player);

        this.addSlot(new EquipSlot(container, MAINHAND_SLOT, 21, 21, EquipmentSlot.MAINHAND));
        this.addSlot(new EquipSlot(container, OFFHAND_SLOT, 21, 39, EquipmentSlot.OFFHAND));
        this.addSlot(new EquipSlot(container, HEAD_SLOT, 21, 57, EquipmentSlot.HEAD));
        this.addSlot(new EquipSlot(container, CHEST_SLOT, 157, 21, EquipmentSlot.CHEST));
        this.addSlot(new EquipSlot(container, LEGS_SLOT, 157, 39, EquipmentSlot.LEGS));
        this.addSlot(new EquipSlot(container, FEET_SLOT, 157, 57, EquipmentSlot.FEET));

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

    private static AvaliEntity resolveEntity(Inventory inv, int entityId) {
        if (inv.player.level().getEntity(entityId) instanceof AvaliEntity avali)
            return avali;
        throw new IllegalStateException("Avali entity not found: " + entityId);
    }

    private class EquipSlot extends Slot {
        private final EquipmentSlot equipmentSlot;

        EquipSlot(SimpleContainer container, int index, int x, int y, EquipmentSlot equipmentSlot) {
            super(container, index, x, y);
            this.equipmentSlot = equipmentSlot;
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return stack.isEmpty()
                    || (stack.is(GalaxyCategory.AVALI.tag()) && avali.getEquipmentSlotForItem(stack) == equipmentSlot);
        }

        @Override
        public void setChanged() {
            super.setChanged();
            avali.syncEquipmentFromContainer();
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot sourceSlot = slots.get(index);
        if (sourceSlot == null || !sourceSlot.hasItem())
            return ItemStack.EMPTY;
        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copy = sourceStack.copy();

        if (index < AVALI_SLOT_COUNT) {
            if (!moveItemStackTo(sourceStack, AVALI_SLOT_COUNT, slots.size(), true))
                return ItemStack.EMPTY;
        } else {
            EquipmentSlot targetEquipSlot = avali.getEquipmentSlotForItem(sourceStack);
            int targetIndex = switch (targetEquipSlot) {
                case MAINHAND -> MAINHAND_SLOT;
                case OFFHAND -> OFFHAND_SLOT;
                case HEAD -> HEAD_SLOT;
                case CHEST -> CHEST_SLOT;
                case LEGS -> LEGS_SLOT;
                case FEET -> FEET_SLOT;
                default -> -1;
            };
            if (targetIndex < 0 || !sourceStack.is(GalaxyCategory.AVALI.tag())
                    || !slots.get(targetIndex).getItem().isEmpty()
                    || !moveItemStackTo(sourceStack, targetIndex, targetIndex + 1, false)) {
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
        return avali.isAlive() && avali.isTame() && avali.isOwnedBy(player) && avali.distanceToSqr(player) < 64;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        avali.getEquipmentContainer().stopOpen(player);
    }
}
