package com.lunkoashtail.avaliproject.entity.client;

import com.lunkoashtail.avaliproject.entity.custom.CaklerahEntity;
import software.bernie.geckolib.model.GeoModel;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.base.GeoRenderState;

import static com.lunkoashtail.avaliproject.entity.client.AvaliProjectDataTickets.texture;

public class CaklerahModel extends GeoModel<CaklerahEntity> {

    @Override
    public ResourceLocation getAnimationResource(CaklerahEntity entity) {
        return ResourceLocation.parse("avaliproject:animations/caklerah.animation.json");
    }

    @Override
    public ResourceLocation getModelResource(GeoRenderState entity) {
        return ResourceLocation.parse("avaliproject:geo/caklerah.json");
    }

    @Override
    public ResourceLocation getTextureResource(GeoRenderState entity) {
        return ResourceLocation.parse("avaliproject:textures/entity/caklerah/" + entity.getGeckolibData(texture) + ".png");
    }

}
