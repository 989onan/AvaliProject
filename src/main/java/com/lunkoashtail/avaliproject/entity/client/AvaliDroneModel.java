package com.lunkoashtail.avaliproject.entity.client;

import com.lunkoashtail.avaliproject.entity.custom.AvaliDroneEntity;
import software.bernie.geckolib.model.GeoModel;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.base.GeoRenderState;

import static com.lunkoashtail.avaliproject.entity.client.AvaliProjectDataTickets.texture;

public class AvaliDroneModel extends GeoModel<AvaliDroneEntity> {
    @Override
    public ResourceLocation getAnimationResource(AvaliDroneEntity entity) {
        return ResourceLocation.parse("avaliproject:animations/avali_drone.animation.json");
    }

    @Override
    public ResourceLocation getModelResource(GeoRenderState entity) {
        return ResourceLocation.parse("avaliproject:geo/avali_drone.json");
    }

    @Override
    public ResourceLocation getTextureResource(GeoRenderState entity) {
        return ResourceLocation.parse("avaliproject:textures/entity/avali_drone/" + entity.getGeckolibData(texture) + ".png");
    }

}