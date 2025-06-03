package com.lunkoashtail.avaliproject.entity.client;

import com.lunkoashtail.avaliproject.AvaliProject;
import com.lunkoashtail.avaliproject.entity.custom.MizoleEntity;
import com.lunkoashtail.avaliproject.entity.custom.PrimagenEntity;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import net.minecraft.Util;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.VertexConsumer;

import com.mojang.blaze3d.vertex.PoseStack;
import software.bernie.geckolib.renderer.base.GeoRenderState;

import static com.lunkoashtail.avaliproject.entity.client.AvaliProjectDataTickets.texture;

public class PrimagenRenderer<R extends EntityRenderState & GeoRenderState>  extends GeoEntityRenderer<PrimagenEntity, R> {
    public PrimagenRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new PrimagenModel());
        this.shadowRadius = 0.5f;
    }

    private static final Map<PrimagenVariant, String> LOCATION_BY_VARIANT =
            Util.make(Maps.newEnumMap(PrimagenVariant.class), map -> {
                map.put(PrimagenVariant.BLUE,"primagenblue");
                map.put(PrimagenVariant.PINK,"primagenpink");
                map.put(PrimagenVariant.BROWN,"primagenbrown");
            });

    @Override
    public void addRenderData(PrimagenEntity animatable, Void relatedObject, R renderState) {
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

