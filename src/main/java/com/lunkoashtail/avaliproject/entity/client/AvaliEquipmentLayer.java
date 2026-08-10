package com.lunkoashtail.avaliproject.entity.client;

import com.lunkoashtail.avaliproject.entity.custom.AvaliEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.BlockAndItemGeoLayer;

public class AvaliEquipmentLayer extends BlockAndItemGeoLayer<AvaliEntity> {
    public AvaliEquipmentLayer(GeoRenderer<AvaliEntity> renderer) {
        super(renderer);
    }

    @Override
    @Nullable
    protected ItemStack getStackForBone(GeoBone bone, AvaliEntity animatable) {
        ItemStack stack = switch (bone.getName()) {
            case "rightarm" -> animatable.getItemBySlot(EquipmentSlot.MAINHAND);
            case "leftarm" -> animatable.getItemBySlot(EquipmentSlot.OFFHAND);
            case "head" -> animatable.getItemBySlot(EquipmentSlot.HEAD);
            case "body" -> animatable.getItemBySlot(EquipmentSlot.CHEST);
            case "rightthigh" -> animatable.getItemBySlot(EquipmentSlot.LEGS);
            case "rightfoot" -> animatable.getItemBySlot(EquipmentSlot.FEET);
            default -> ItemStack.EMPTY;
        };
        return stack.isEmpty() ? null : stack;
    }

    @Override
    protected ItemDisplayContext getTransformTypeForStack(GeoBone bone, ItemStack stack, AvaliEntity animatable) {
        return switch (bone.getName()) {
            case "rightarm" -> ItemDisplayContext.THIRD_PERSON_RIGHT_HAND;
            case "leftarm" -> ItemDisplayContext.THIRD_PERSON_LEFT_HAND;
            default -> ItemDisplayContext.FIXED;
        };
    }

    private static final float HAND_OFFSET = 0.85f;

    @Override
    protected void renderStackForBone(PoseStack poseStack, GeoBone bone, ItemStack stack, AvaliEntity animatable,
                                       MultiBufferSource bufferSource, float partialTick, int packedLight, int packedOverlay) {
        boolean isHandBone = bone.getName().equals("rightarm") || bone.getName().equals("leftarm");
        if (isHandBone) {
            poseStack.pushPose();
            poseStack.translate(0, -HAND_OFFSET, 0);
        }

        super.renderStackForBone(poseStack, bone, stack, animatable, bufferSource, partialTick, packedLight, packedOverlay);

        if (isHandBone) {
            poseStack.popPose();
        }
    }
}
