package com.lunkoashtail.avaliproject.entity.client;

import com.google.common.collect.Maps;
import com.lunkoashtail.avaliproject.AvaliProject;
import com.lunkoashtail.avaliproject.entity.custom.PrimagenEntity;
import com.lunkoashtail.avaliproject.entity.custom.ProtogenEntity;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.Util;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack;
import software.bernie.geckolib.renderer.base.GeoRenderState;

import static com.lunkoashtail.avaliproject.entity.client.AvaliProjectDataTickets.texture;

public class ProtogenRenderer<R extends EntityRenderState & GeoRenderState> extends GeoEntityRenderer<ProtogenEntity, R> {
    public ProtogenRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ProtogenModel());
        this.shadowRadius = 0.5f;
    }


    private static final Map<ProtogenVariant, String> LOCATION_BY_VARIANT =
            Util.make(Maps.newEnumMap(ProtogenVariant.class), map -> {
                map.put(ProtogenVariant.BLUE,"protogenblue");
                map.put(ProtogenVariant.PURPLE,"protogenpurple");
                map.put(ProtogenVariant.GREEN,"protogengreen");
            });


    @Override
    public RenderType getRenderType(R animatable, ResourceLocation texture) {
        return RenderType.entityTranslucent(getTextureLocation(animatable));
    }

    @Override
    public void addRenderData(ProtogenEntity animatable, Void relatedObject, R renderState) {
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

