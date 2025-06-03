package com.lunkoashtail.avaliproject.item.client;

import com.lunkoashtail.avaliproject.item.custom.FirelanceItem;
import software.bernie.geckolib.model.GeoModel;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class FirelanceItemModel extends GeoModel<FirelanceItem> {
    @Override
    public ResourceLocation getAnimationResource(FirelanceItem animatable) {
        return ResourceLocation.parse("avaliproject:animations/firelance.animation.json");
    }

    @Override
    public ResourceLocation getModelResource(GeoRenderState animatable) {
        return ResourceLocation.parse("avaliproject:geo/firelance.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(GeoRenderState animatable) {
        return ResourceLocation.parse("avaliproject:textures/item/firelance.png");
    }
}