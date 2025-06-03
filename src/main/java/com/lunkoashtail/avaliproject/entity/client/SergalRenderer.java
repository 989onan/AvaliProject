package com.lunkoashtail.avaliproject.entity.client;

import com.google.common.collect.Maps;
import com.lunkoashtail.avaliproject.AvaliProject;
import com.lunkoashtail.avaliproject.entity.custom.ProtogenEntity;
import com.lunkoashtail.avaliproject.entity.custom.SergalEntity;
import net.minecraft.Util;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.util.Mth;
import software.bernie.geckolib.animatable.processing.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack;
import software.bernie.geckolib.renderer.base.GeoRenderState;

import java.util.Map;

import static com.lunkoashtail.avaliproject.entity.client.AvaliProjectDataTickets.sergal_variant;
import static com.lunkoashtail.avaliproject.entity.client.AvaliProjectDataTickets.texture;

public class SergalRenderer<R extends EntityRenderState & GeoRenderState>  extends GeoEntityRenderer<SergalEntity, R> {
    public SergalRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new SergalModel());
        this.shadowRadius = 0.5f;
    }

    private static final Map<SergalVariant, String> LOCATION_BY_VARIANT =
            Util.make(Maps.newEnumMap(SergalVariant.class), map -> {
                map.put(SergalVariant.BLACK,"sergal_black");
                map.put(SergalVariant.GREY,"sergal_grey");
                map.put(SergalVariant.BLUE,"sergal_blue");
                map.put(SergalVariant.BROWN,"sergal_brown");
                map.put(SergalVariant.BROWN_ALT,"sergal_brown_alt");
                map.put(SergalVariant.CRIMSON,"sergal_crimson");
                map.put(SergalVariant.FROST,"sergal_frost");
                map.put(SergalVariant.GREEN,"sergal_green");
                map.put(SergalVariant.ORANGE,"sergal_orange");
                map.put(SergalVariant.PEACH,"sergal_peach");
                map.put(SergalVariant.PINK,"sergal_pink");
                map.put(SergalVariant.PURPLE,"sergal_purple");
            });

    @Override
    public RenderType getRenderType(R animatable, ResourceLocation texture) {
        return RenderType.entityTranslucent(getTextureLocation(animatable));
    }

    @Override
    public void addRenderData(SergalEntity animatable, Void relatedObject, R renderState) {
        renderState.addGeckolibData(texture,LOCATION_BY_VARIANT.get(animatable.getVariant()));

    }




    @Override
    public void preRender(R entity, PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, int packedLight, int packedOverlay, int color) {
        float scale = 1f;
        this.scaleHeight = scale;
        this.scaleWidth = scale;
        super.preRender( entity,poseStack, model, bufferSource, buffer, isReRender, packedLight, packedOverlay, color);
    }
}
