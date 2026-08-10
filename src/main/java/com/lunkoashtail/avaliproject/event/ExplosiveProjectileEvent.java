package com.lunkoashtail.avaliproject.event;

import com.lunkoashtail.avaliproject.util.HitscanUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

public class ExplosiveProjectileEvent {
    public static void execute(LevelAccessor world, double x, double y, double z, Entity entity, ItemStack itemstack) {
        if (entity == null)
            return;
        if (world instanceof ServerLevel level) {
            HitscanUtil.HitscanResult result = HitscanUtil.fire(level, entity, 20, 0, 48);
            level.explode(null, result.hitPos().x, result.hitPos().y, result.hitPos().z, 5, Level.ExplosionInteraction.NONE);
        }
        if (entity instanceof Player _player)
            _player.getCooldowns().addCooldown(itemstack.getItem(), 150);
    }
}
