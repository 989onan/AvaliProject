package com.lunkoashtail.avaliproject.item.custom;

import com.lunkoashtail.avaliproject.util.HitscanUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.phys.Vec3;

public class RailgunItem extends Item implements HitscanWeapon {
    private static final double TARGET_KNOCKBACK = 6.0;
    private static final double SHOOTER_KNOCKBACK = 1.4;
    private static final double SHOOTER_KNOCKBACK_SNEAKING_MULTIPLIER = 0.25;
    private static final double RANGE = 96;
    private static final double DAMAGE = 12;

    public RailgunItem() {
        super(new Item.Properties().stacksTo(1).rarity(Rarity.EPIC));
    }


    @Override
    public void fire(ServerLevel level, ServerPlayer player, ItemStack stack) {
        HitscanUtil.fire(level, player, DAMAGE, TARGET_KNOCKBACK, RANGE);

        Vec3 recoil = player.getLookAngle().scale(-1);
        double shooterKnockback = SHOOTER_KNOCKBACK * (player.isShiftKeyDown() ? SHOOTER_KNOCKBACK_SNEAKING_MULTIPLIER : 1.0);
        player.push(recoil.x * shooterKnockback, 0.1 * shooterKnockback, recoil.z * shooterKnockback);

        player.getCooldowns().addCooldown(this, 20);
    }
}
