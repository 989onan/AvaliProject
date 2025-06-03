package com.lunkoashtail.avaliproject.item.client;

import com.lunkoashtail.avaliproject.item.custom.AvaliswordItem;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.cache.object.BakedGeoModel;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;

import java.util.Set;
import java.util.HashSet;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class AvaliswordItemRenderer extends GeoItemRenderer<AvaliswordItem> {
    public AvaliswordItemRenderer() {
        super(new AvaliswordItemModel());
    }

    @Override
    public RenderType getRenderType(GeoRenderState animatable, ResourceLocation texture) {
        return RenderType.entityTranslucent(getTextureLocation(animatable));
    }

    private static final float SCALE_RECIPROCAL = 1.0f / 16.0f;
    protected boolean renderArms = false;
    protected MultiBufferSource currentBuffer;
    protected RenderType renderType;
    //protected AvaliswordItem animatable;
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
    public ResourceLocation getTextureLocation(GeoRenderState instance) {
        return super.getTextureLocation(instance);
    }
}