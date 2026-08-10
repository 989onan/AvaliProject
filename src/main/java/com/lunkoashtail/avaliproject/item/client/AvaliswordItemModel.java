package com.lunkoashtail.avaliproject.item.client;

import com.lunkoashtail.avaliproject.item.custom.AvaliswordItem;
import software.bernie.geckolib.model.GeoModel;

import net.minecraft.resources.ResourceLocation;

public class AvaliswordItemModel extends GeoModel<AvaliswordItem> {
    public static final ResourceLocation TEXTURE = ResourceLocation.parse("avaliproject:textures/item/avali_sword.png");
    public static final ResourceLocation SHATTERED_TEXTURE = ResourceLocation.parse("avaliproject:textures/item/avali_sword_shattered.png");

    @Override
    public ResourceLocation getAnimationResource(AvaliswordItem animatable) {
        return ResourceLocation.parse("avaliproject:animations/avali_sword.animation.json");
    }

    @Override
    public ResourceLocation getModelResource(AvaliswordItem animatable) {
        return ResourceLocation.parse("avaliproject:geo/avali_sword.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(AvaliswordItem animatable) {
        return TEXTURE;
    }
}
