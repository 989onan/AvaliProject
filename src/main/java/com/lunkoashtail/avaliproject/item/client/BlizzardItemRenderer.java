package com.lunkoashtail.avaliproject.item.client;

import com.lunkoashtail.avaliproject.item.custom.BlizzardItem;
import com.lunkoashtail.avaliproject.util.AnimUtils;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.util.RenderUtil;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.cache.object.BakedGeoModel;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.Minecraft;

import java.util.Set;
import java.util.HashSet;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack;

public class BlizzardItemRenderer extends GeoItemRenderer<BlizzardItem> {
    public BlizzardItemRenderer() {
        super(new BlizzardItemModel());
    }

    @Override
    public RenderType getRenderType(GeoRenderState animatable, ResourceLocation texture) {
        return RenderType.entityTranslucent(getTextureLocation(animatable));
    }

    private static final float SCALE_RECIPROCAL = 1.0f / 16.0f;
    protected boolean renderArms = false;
    protected MultiBufferSource currentBuffer;
    protected RenderType renderType;
    public ItemDisplayContext transformType;
    protected BlizzardItem animatable;
    private final Set<String> hiddenBones = new HashSet<>();
    private final Set<String> suppressedBones = new HashSet<>();

    @Override
    public void actuallyRender(GeoRenderState renderState, PoseStack poseStack, BakedGeoModel model, @Nullable RenderType renderType,
                               MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, int packedLight, int packedOverlay, int renderColor) {
        this.currentBuffer = bufferSource;
        this.renderType = renderType;
        super.actuallyRender(renderState,poseStack, model, renderType, bufferSource, buffer, isReRender, packedLight, packedOverlay, renderColor);
        if (this.renderArms) {
            this.renderArms = false;
        }
    }

    @Override
    public void renderRecursively(GeoRenderState renderState, PoseStack stack, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, int packedLightIn, int packedOverlay, int renderColor) {
        Minecraft mc = Minecraft.getInstance();
        String name = bone.getName();
        boolean renderingArms = false;
        if (name.equals("") || name.equals("")) {
            bone.setHidden(true);
            renderingArms = true;
        } else {
            bone.setHidden(this.hiddenBones.contains(name));
        }
        if (renderState.getGeckolibData(DataTickets.ITEM_RENDER_PERSPECTIVE).firstPerson() && renderingArms) {
            AbstractClientPlayer player = mc.player;
            PlayerRenderer playerRenderer = (PlayerRenderer) mc.getEntityRenderDispatcher().getRenderer(player);
            PlayerModel model = playerRenderer.getModel();
            stack.pushPose();
            RenderUtil.translateMatrixToBone(stack, bone);
            RenderUtil.translateToPivotPoint(stack, bone);
            RenderUtil.rotateMatrixAroundBone(stack, bone);
            RenderUtil.scaleMatrixForBone(stack, bone);
            RenderUtil.translateAwayFromPivotPoint(stack, bone);
            ResourceLocation loc = player.getSkin().texture();
            if (name.equals("")) {
                stack.translate(-1.0f * SCALE_RECIPROCAL, 2.0f * SCALE_RECIPROCAL, 0.0f);
                if (!player.isInvisible()) {
                    AnimUtils.renderPartOverBone(model.leftArm, bone, stack, this.currentBuffer.getBuffer(RenderType.entitySolid(loc)), packedLightIn, OverlayTexture.NO_OVERLAY);
                    AnimUtils.renderPartOverBone(model.leftSleeve, bone, stack, this.currentBuffer.getBuffer(RenderType.entityTranslucent(loc)), packedLightIn, OverlayTexture.NO_OVERLAY);
                }
            } else if (name.equals("")) {
                stack.translate(1.0f * SCALE_RECIPROCAL, 2.0f * SCALE_RECIPROCAL, 0.0f);
                if (!player.isInvisible()) {
                    AnimUtils.renderPartOverBone(model.rightArm, bone, stack, this.currentBuffer.getBuffer(RenderType.entitySolid(loc)), packedLightIn, OverlayTexture.NO_OVERLAY);
                    AnimUtils.renderPartOverBone(model.rightSleeve, bone, stack, this.currentBuffer.getBuffer(RenderType.entityTranslucent(loc)), packedLightIn, OverlayTexture.NO_OVERLAY);
                }
            }
            stack.popPose();
        }
        super.renderRecursively(renderState, stack, bone, renderType, bufferSource, buffer, isReRender, packedLightIn, packedOverlay, renderColor);
    }

    @Override
    public ResourceLocation getTextureLocation(GeoRenderState instance) {
        return super.getTextureLocation(instance);
    }
}