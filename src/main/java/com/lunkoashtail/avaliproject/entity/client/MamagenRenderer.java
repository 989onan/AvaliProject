package com.lunkoashtail.avaliproject.entity.client;

import com.lunkoashtail.avaliproject.entity.custom.MaleNevreanEntity;
import com.lunkoashtail.avaliproject.entity.custom.MamagenEntity;
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

public class MamagenRenderer<R extends EntityRenderState & GeoRenderState> extends GeoEntityRenderer<MamagenEntity,R> {
    public MamagenRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new MamagenModel());
        this.shadowRadius = 0.5f;
    }

    @Override
    public void addRenderData(MamagenEntity animatable, Void relatedObject, R renderState) {
        renderState.addGeckolibData(texture,animatable.getTexture());

    }

    @Override
    public RenderType getRenderType(R animatable, ResourceLocation texture) {
        return RenderType.entityTranslucent(getTextureLocation(animatable));
    }

    @Override
    public void preRender(R entity, PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, int packedLight, int packedOverlay, int color) {
        float scale = 1f;
        this.scaleHeight = scale;
        this.scaleWidth = scale;
        super.preRender( entity,poseStack, model, bufferSource, buffer, isReRender, packedLight, packedOverlay, color);
    }
}
