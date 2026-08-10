package com.lunkoashtail.avaliproject.entity.custom;

import com.lunkoashtail.avaliproject.entity.ModEntities;
import com.lunkoashtail.avaliproject.limb.Limb;
import com.lunkoashtail.avaliproject.limb.LimbConditions;
import com.lunkoashtail.avaliproject.limb.ModAttachments;
import com.lunkoashtail.avaliproject.network.LimbConditionsSyncPayload;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;

public class GlassShardEntity extends Entity {

    private static final int MAX_AGE_TICKS = 6000;
    private static final int HAZARD_CHECK_INTERVAL = 10;
    private static final float HAZARD_CHANCE = 0.15f;
    private static final int HAZARD_SHRAPNEL_AMOUNT = 12;
    private static final float HAZARD_PAIN_DAMAGE = 1.0f;

    private static final Limb[] CONTACT_LIMBS = { Limb.LEFT_LEG, Limb.RIGHT_LEG };

    private static final double GRAVITY_PER_TICK = 0.04;

    private static final EntityDataAccessor<BlockState> GLASS_STATE =
            SynchedEntityData.defineId(GlassShardEntity.class, EntityDataSerializers.BLOCK_STATE);

    private int age;

    public GlassShardEntity(EntityType<? extends GlassShardEntity> type, Level level) {
        super(type, level);
    }

    public GlassShardEntity(Level level, double x, double y, double z, BlockState glassState) {
        this(ModEntities.GLASS_SHARD.get(), level);
        this.setPos(x, y, z);
        this.setGlassState(glassState);
        this.setYRot(this.random.nextFloat() * 360f);
        this.yRotO = this.getYRot();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(GLASS_STATE, Blocks.GLASS.defaultBlockState());
    }

    public BlockState getGlassState() { return this.entityData.get(GLASS_STATE); }
    public void setGlassState(BlockState state) { this.entityData.set(GLASS_STATE, state); }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {}

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {}

    @Override
    public boolean isPickable() { return true; }

    @Override
    public boolean canBeCollidedWith() { return true; }

    @Override
    public boolean isPushable() { return false; }

    @Override
    public boolean isAttackable() { return true; }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide()) return;

        fallToGround();

        if (++this.age > MAX_AGE_TICKS) {
            this.discard();
            return;
        }

        if (this.age % HAZARD_CHECK_INTERVAL == 0) {
            checkHazard((ServerLevel) this.level());
        }
    }

    private void fallToGround() {
        if (this.onGround()) {
            if (this.getDeltaMovement().y != 0) {
                this.setDeltaMovement(this.getDeltaMovement().x, 0, this.getDeltaMovement().z);
            }
            return;
        }

        Vec3 motion = this.getDeltaMovement();
        this.setDeltaMovement(motion.x, motion.y - GRAVITY_PER_TICK, motion.z);
        this.move(MoverType.SELF, this.getDeltaMovement());
    }

    private void checkHazard(ServerLevel level) {
        AABB shardBox = this.getBoundingBox();
        List<Player> nearby = level.getEntitiesOfClass(Player.class, shardBox.inflate(0.1));
        for (Player player : nearby) {
            if (!(player instanceof ServerPlayer serverPlayer)) continue;
            if (!shardBox.intersects(player.getBoundingBox())) continue;

            LimbConditions conditions = serverPlayer.getData(ModAttachments.LIMB_CONDITIONS);
            boolean embedded = false;
            for (Limb limb : CONTACT_LIMBS) {
                if (this.random.nextFloat() >= HAZARD_CHANCE) continue;
                conditions.addShrapnel(limb, HAZARD_SHRAPNEL_AMOUNT);
                embedded = true;
            }
            if (!embedded) continue;

            PacketDistributor.sendToPlayer(serverPlayer, LimbConditionsSyncPayload.from(conditions));
            serverPlayer.hurt(this.damageSources().generic(), HAZARD_PAIN_DAMAGE);
            level.playSound(null, this.getX(), this.getY(), this.getZ(),
                    SoundEvents.GLASS_HIT, SoundSource.PLAYERS, 0.5f, 1.3f);
        }
    }

    @Override
    public boolean skipAttackInteraction(Entity source) {
        if (source instanceof Player && !this.level().isClientSide()) {
            breakShard((ServerLevel) this.level());
        }
        return true;
    }

    private void breakShard(ServerLevel level) {
        BlockState state = this.getGlassState();
        for (int i = 0; i < 8; i++) {
            double ox = (this.random.nextDouble() - 0.5) * 0.3;
            double oy = this.random.nextDouble() * 0.3;
            double oz = (this.random.nextDouble() - 0.5) * 0.3;
            level.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, state),
                    this.getX(), this.getY() + 0.2, this.getZ(), 1, ox, oy, oz, 0.05);
        }
        level.playSound(null, this.getX(), this.getY(), this.getZ(),
                SoundEvents.GLASS_BREAK, SoundSource.BLOCKS, 1.0f, 1.0f);
        this.discard();
    }
}
