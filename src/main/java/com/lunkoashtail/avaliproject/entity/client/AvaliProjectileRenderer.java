package com.lunkoashtail.avaliproject.entity.client;

import com.lunkoashtail.avaliproject.entity.custom.AvaliExplosiveEntity;
import com.lunkoashtail.avaliproject.entity.custom.AvaliProjectileEntity;
import net.minecraft.util.Mth;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;

import com.mojang.math.Axis;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack;

public class AvaliProjectileRenderer extends EntityRenderer<AvaliProjectileEntity, AvaliProjectileRenderState> {
    private static final ResourceLocation texture = ResourceLocation.parse("avaliproject:textures/entity/avali_projectile.png");
    private final Modelavali_projectile_Converted<AvaliProjectileEntity> model;

    public AvaliProjectileRenderer(EntityRendererProvider.Context context) {
        super(context);
        model = new Modelavali_projectile_Converted<>(context.bakeLayer(Modelavali_projectile_Converted.LAYER_LOCATION));
    }

    @Override
    public AvaliProjectileRenderState createRenderState() {
        AvaliProjectileRenderState state = new AvaliProjectileRenderState();
        return state;
    }

    @Override
    public void render(AvaliProjectileRenderState entityIn, PoseStack poseStack, MultiBufferSource bufferIn, int packedLight) {
        VertexConsumer vb = bufferIn.getBuffer(RenderType.entityCutout(this.getTextureLocation(entityIn)));
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(entityIn.partialTick, entityIn.yRotO, entityIn.getYRot0) - 90));
        poseStack.mulPose(Axis.ZP.rotationDegrees(90 + Mth.lerp(entityIn.partialTick, entityIn.xRotO, entityIn.getXRot0)));
        model.renderToBuffer(poseStack, vb, packedLight, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
        super.render(entityIn, poseStack, bufferIn, packedLight);
    }

    @Override
    public void extractRenderState(AvaliProjectileEntity entity, AvaliProjectileRenderState state, float tick_progress) {
        super.extractRenderState(entity, state, tick_progress);
        state.yRotO = entity.yRotO;
        state.xRotO = entity.xRotO;
        state.getXRot0 = entity.getXRot();
        state.getYRot0 = entity.getYRot();
    }

    public ResourceLocation getTextureLocation(AvaliProjectileRenderState entity) {
        return texture;
    }
}
