package com.lunkoashtail.avaliproject.item.custom;

import com.lunkoashtail.avaliproject.event.PistolProjectileEvent;
import com.lunkoashtail.avaliproject.item.client.BlizzardItemRenderer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemUseAnimation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animatable.processing.AnimationController;
import software.bernie.geckolib.animatable.processing.AnimationTest;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.PlayState;

import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.GeoItem;

import net.minecraft.world.level.Level;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionHand;

import java.util.function.Consumer;

public class BlizzardItem extends Item implements GeoItem {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    public String animationprocedure = "empty";

    public BlizzardItem() {
        super(new Item.Properties().stacksTo(1).rarity(Rarity.EPIC));
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return false;
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private BlizzardItemRenderer renderer;

            @Override
            public @Nullable GeoItemRenderer<BlizzardItem> getGeoItemRenderer() {
                if (this.renderer == null)
                    this.renderer = new BlizzardItemRenderer();
                return this.renderer;
            }
        });
    }

    private PlayState idlePredicate(AnimationTest<BlizzardItem> event) {
        if (this.animationprocedure.equals("empty")) {
            event.controller().setAnimation(RawAnimation.begin().thenLoop("Idle"));
            return PlayState.CONTINUE;
        }
        return PlayState.STOP;
    }

    String prevAnim = "empty";

    private PlayState procedurePredicate(AnimationTest<BlizzardItem> event) {
        if (!this.animationprocedure.equals("empty") && event.controller().getAnimationState() == AnimationController.State.STOPPED || (!this.animationprocedure.equals(prevAnim) && !this.animationprocedure.equals("empty"))) {
            if (!this.animationprocedure.equals(prevAnim))
                event.controller().forceAnimationReset();
            event.controller().setAnimation(RawAnimation.begin().thenPlay(this.animationprocedure));
            if (event.controller().getAnimationState() == AnimationController.State.STOPPED) {
                this.animationprocedure = "empty";
                event.controller().forceAnimationReset();
            }
        } else if (this.animationprocedure.equals("empty")) {
            prevAnim = "empty";
            return PlayState.STOP;
        }
        prevAnim = this.animationprocedure;
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
        AnimationController procedureController = new AnimationController("procedureController", 0, this::procedurePredicate);
        data.add(procedureController);
        AnimationController idleController = new AnimationController("idleController", 0, this::idlePredicate);
        data.add(idleController);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public ItemUseAnimation getUseAnimation(ItemStack itemstack) {
        return ItemUseAnimation.BOW;
    }

    @Override
    public InteractionResult use(Level world, Player entity, InteractionHand hand) {
        InteractionResult ar = super.use(world, entity, hand);
        ItemStack itemstack = entity.getItemInHand(hand);
        PistolProjectileEvent.execute(world, entity.getX(), entity.getY(), entity.getZ(), entity, itemstack);
        return ar;
    }
}