package com.lunkoashtail.avaliproject.entity.client;

import com.lunkoashtail.avaliproject.entity.custom.TalxleechEntity;
import com.lunkoashtail.avaliproject.entity.custom.TalxweaselEntity;
import software.bernie.geckolib.animatable.processing.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.cache.object.GeoBone;

import net.minecraft.util.Mth;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.base.GeoRenderState;

import static com.lunkoashtail.avaliproject.entity.client.AvaliProjectDataTickets.texture;

public class TalxweaselModel extends GeoModel<TalxweaselEntity> {
    @Override
    public ResourceLocation getAnimationResource(TalxweaselEntity entity) {
        return ResourceLocation.parse("avaliproject:animations/talxweasel.animation.json");
    }

    @Override
    public ResourceLocation getModelResource(GeoRenderState entity) {
        return ResourceLocation.parse("avaliproject:geo/talxweasel.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(GeoRenderState entity) {
        return ResourceLocation.parse("avaliproject:textures/entity/talxweasel/" + entity.getGeckolibData(texture)+ ".png");
    }

    @Override
    public void setCustomAnimations(AnimationState<TalxweaselEntity> animatable) {
        GeoBone head = getAnimationProcessor().getBone("head");
        if (head != null) {
            head.setRotX(animatable.renderState().getGeckolibData(DataTickets.ENTITY_PITCH)  * Mth.DEG_TO_RAD);
            head.setRotY(animatable.renderState().getGeckolibData(DataTickets.ENTITY_YAW)  * Mth.DEG_TO_RAD);
        }

    }
}
