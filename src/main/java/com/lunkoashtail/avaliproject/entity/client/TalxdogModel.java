package com.lunkoashtail.avaliproject.entity.client;

import com.lunkoashtail.avaliproject.entity.custom.PrimagenEntity;
import com.lunkoashtail.avaliproject.entity.custom.TalxdogEntity;
import software.bernie.geckolib.animatable.processing.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.cache.object.GeoBone;

import net.minecraft.util.Mth;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.base.GeoRenderState;

import static com.lunkoashtail.avaliproject.entity.client.AvaliProjectDataTickets.texture;

public class TalxdogModel extends GeoModel<TalxdogEntity> {
    @Override
    public ResourceLocation getAnimationResource(TalxdogEntity entity) {
        return ResourceLocation.parse("avaliproject:animations/talxdog.animation.json");
    }

    @Override
    public ResourceLocation getModelResource(GeoRenderState entity) {
        return ResourceLocation.parse("avaliproject:geo/talxdog.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(GeoRenderState entity) {
        return ResourceLocation.parse("avaliproject:textures/entity/talxdog/" + entity.getGeckolibData(texture) + ".png");
    }

    @Override
    public void setCustomAnimations(AnimationState<TalxdogEntity> animatable) {
        GeoBone head = getAnimationProcessor().getBone("head");
        if (head != null) {
            head.setRotX(animatable.renderState().getGeckolibData(DataTickets.ENTITY_PITCH)  * Mth.DEG_TO_RAD);
            head.setRotY(animatable.renderState().getGeckolibData(DataTickets.ENTITY_YAW)  * Mth.DEG_TO_RAD);
        }

    }
}
