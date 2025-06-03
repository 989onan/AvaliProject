package com.lunkoashtail.avaliproject.entity.client;

import com.lunkoashtail.avaliproject.entity.custom.AvaliDroneEntity;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.cache.object.BakedGeoModel;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack;
import software.bernie.geckolib.renderer.base.GeoRenderState;

import static com.lunkoashtail.avaliproject.entity.client.AvaliProjectDataTickets.texture;

public class AvaliDroneRenderer<R extends EntityRenderState & GeoRenderState> extends GeoEntityRenderer<AvaliDroneEntity, R> {


    public AvaliDroneRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new AvaliDroneModel());
        this.shadowRadius = 0.5f;
    }

    @Override
    public RenderType getRenderType(R renderState, ResourceLocation texture) {
        return RenderType.entityTranslucent(getTextureLocation(renderState));
    }

    @Override
    public void addRenderData(AvaliDroneEntity animatable, Void relatedObject, R renderState) {
        renderState.addGeckolibData(texture,animatable.getTexture());

    }

    @Override
    public void preRender(R renderState,PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, int packedLight, int packedOverlay, int color) {
        float scale = 1f;
        this.scaleHeight = scale;
        this.scaleWidth = scale;
        super.preRender(renderState,poseStack, model, bufferSource, buffer, isReRender, packedLight, packedOverlay, color);
    }
}
