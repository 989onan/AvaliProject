package com.lunkoashtail.avaliproject.entity.client;

import com.lunkoashtail.avaliproject.entity.custom.ProtogenEntity;
import com.lunkoashtail.avaliproject.entity.custom.SksceegehkjaEntity;
import software.bernie.geckolib.animatable.processing.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.cache.object.GeoBone;

import net.minecraft.util.Mth;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.base.GeoRenderState;

import static com.lunkoashtail.avaliproject.entity.client.AvaliProjectDataTickets.texture;

public class SksceegehkjaModel extends GeoModel<SksceegehkjaEntity> {
    @Override
    public ResourceLocation getAnimationResource(SksceegehkjaEntity entity) {
        return ResourceLocation.parse("avaliproject:animations/sksceegehkja.animation.json");
    }

    @Override
    public ResourceLocation getModelResource(GeoRenderState entity) {
        return ResourceLocation.parse("avaliproject:geo/sksceegehkja.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(GeoRenderState entity) {
        return ResourceLocation.parse("avaliproject:textures/entity/" + entity.getGeckolibData(texture) + ".png");
    }

    @Override
    public void setCustomAnimations(AnimationState<SksceegehkjaEntity> animatable) {
        GeoBone head = getAnimationProcessor().getBone("head");
        if (head != null) {
            head.setRotX(animatable.renderState().getGeckolibData(DataTickets.ENTITY_PITCH)  * Mth.DEG_TO_RAD);
            head.setRotY(animatable.renderState().getGeckolibData(DataTickets.ENTITY_YAW)  * Mth.DEG_TO_RAD);
        }

    }
}

