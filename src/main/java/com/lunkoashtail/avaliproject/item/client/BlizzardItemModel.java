package com.lunkoashtail.avaliproject.item.client;

import com.lunkoashtail.avaliproject.item.custom.BlizzardItem;
import software.bernie.geckolib.model.GeoModel;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class BlizzardItemModel extends GeoModel<BlizzardItem> {
    @Override
    public ResourceLocation getAnimationResource(BlizzardItem animatable) {
        return ResourceLocation.parse("avaliproject:animations/blizzard.animation.json");
    }

    @Override
    public ResourceLocation getModelResource(GeoRenderState animatable) {
        return ResourceLocation.parse("avaliproject:geo/blizzard.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(GeoRenderState animatable) {
        return ResourceLocation.parse("avaliproject:textures/item/blizzard.png");
    }
}