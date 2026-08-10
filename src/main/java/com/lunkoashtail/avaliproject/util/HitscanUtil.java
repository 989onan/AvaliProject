package com.lunkoashtail.avaliproject.util;

import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import java.util.Optional;

public class HitscanUtil {

    public record HitscanResult(Vec3 origin, Vec3 hitPos, @Nullable LivingEntity target) {
        public boolean hitEntity() {
            return target != null;
        }
    }

    public static HitscanResult fire(ServerLevel level, Entity shooter, double damage, double knockback, double range) {
        return fire(level, shooter, shooter.getEyePosition(), shooter.getLookAngle(), damage, knockback, range);
    }

    public static HitscanResult fire(ServerLevel level, Entity shooter, Vec3 origin, Vec3 look, double damage, double knockback, double range) {
        Vec3 end = origin.add(look.scale(range));

        BlockHitResult blockHit = level.clip(new ClipContext(origin, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, shooter));
        double closestDistSqr = blockHit.getType() == HitResult.Type.MISS ? range * range : origin.distanceToSqr(blockHit.getLocation());
        Vec3 hitPos = blockHit.getType() == HitResult.Type.MISS ? end : blockHit.getLocation();
        LivingEntity hitEntity = null;

        AABB searchBox = shooter.getBoundingBox().expandTowards(look.scale(range)).inflate(1.0);
        for (Entity candidate : level.getEntities(shooter, searchBox, e -> e instanceof LivingEntity living && living.isAlive() && e.isPickable())) {
            AABB box = candidate.getBoundingBox().inflate(0.3);
            Optional<Vec3> clip = box.clip(origin, end);
            if (clip.isEmpty())
                continue;
            double distSqr = origin.distanceToSqr(clip.get());
            if (distSqr < closestDistSqr) {
                closestDistSqr = distSqr;
                hitPos = clip.get();
                hitEntity = (LivingEntity) candidate;
            }
        }

        if (hitEntity != null) {
            DamageSource damageSource = shooter instanceof Player player
                    ? level.damageSources().playerAttack(player)
                    : shooter instanceof LivingEntity living ? level.damageSources().mobAttack(living) : level.damageSources().generic();
            hitEntity.hurt(damageSource, (float) damage);
            applyKnockback(hitEntity, look, knockback);
        }

        spawnTrail(level, origin, hitPos);
        return new HitscanResult(origin, hitPos, hitEntity);
    }

    public static void applyKnockback(LivingEntity target, Vec3 direction, double knockback) {
        if (knockback <= 0)
            return;
        double resistance = Math.max(0.0, 1.0 - target.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
        Vec3 push = direction.multiply(1.0, 0.0, 1.0).normalize().scale(knockback * 0.6 * resistance);
        if (push.lengthSqr() > 0.0) {
            target.push(push.x, 0.1, push.z);
        }
    }

    private static final DustParticleOptions BEAM_CORE = new DustParticleOptions(new Vector3f(1.0f, 0.62f, 0.05f), 1.4f);
    private static final DustParticleOptions BEAM_GLOW = new DustParticleOptions(new Vector3f(1.0f, 0.35f, 0.02f), 2.6f);

    private static final double BEAM_START_OFFSET = 0.75;

    private static void spawnTrail(ServerLevel level, Vec3 from, Vec3 to) {
        double length = from.distanceTo(to);
        if (length <= BEAM_START_OFFSET)
            return;
        Vec3 beamStart = from.add(to.subtract(from).normalize().scale(BEAM_START_OFFSET));
        double beamLength = beamStart.distanceTo(to);
        int steps = (int) Math.max(1, beamLength * 3);
        RandomSource random = level.getRandom();
        for (int i = 0; i <= steps; i++) {
            Vec3 point = beamStart.lerp(to, (double) i / steps);
            level.sendParticles(BEAM_GLOW, point.x, point.y, point.z, 1, 0, 0, 0, 0);
            level.sendParticles(BEAM_CORE, point.x, point.y, point.z, 1, 0, 0, 0, 0);
            if (random.nextFloat() < 0.15f) {
                level.sendParticles(ParticleTypes.FLAME, point.x, point.y, point.z, 1,
                        Mth.nextDouble(random, -0.02, 0.02), Mth.nextDouble(random, -0.02, 0.02), Mth.nextDouble(random, -0.02, 0.02), 0.001);
            }
        }
        level.sendParticles(BEAM_CORE, to.x, to.y, to.z, 6, 0.15, 0.15, 0.15, 0.01);
        level.playSound(null, from.x, from.y, from.z, SoundEvents.TRIDENT_THROW, SoundSource.PLAYERS, 0.6f, 1.6f);
    }
}
