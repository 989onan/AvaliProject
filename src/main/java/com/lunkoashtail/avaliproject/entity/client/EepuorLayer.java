package com.lunkoashtail.avaliproject.entity.client;

import com.lunkoashtail.avaliproject.entity.custom.EepuorEntity;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.base.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;
import software.bernie.geckolib.cache.object.BakedGeoModel;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack;

public class EepuorLayer<R extends EntityRenderState & GeoRenderState> extends GeoRenderLayer<EepuorEntity, EepuorEntity, R> {
    private static final ResourceLocation LAYER = ResourceLocation.parse("avaliproject:textures/entity/eepuor/eepuorglow.png");

    public EepuorLayer(GeoRenderer<EepuorEntity,EepuorEntity, R> entityRenderer) {
        super(entityRenderer);
    }

    @Override
    public void render(R renderState, PoseStack poseStack, BakedGeoModel bakedModel, @Nullable RenderType renderType, MultiBufferSource bufferSource, @Nullable VertexConsumer buffer,
                       int packedLight, int packedOverlay, int renderColor) {
        RenderType glowRenderType = RenderType.eyes(LAYER);
        getRenderer().reRender(renderState, poseStack, bakedModel, bufferSource, renderType, buffer, packedLight, packedOverlay, -1);
    }
}