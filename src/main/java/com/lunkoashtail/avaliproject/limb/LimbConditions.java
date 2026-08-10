package com.lunkoashtail.avaliproject.limb;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Arrays;

public class LimbConditions {

    public static final int MAX_SHRAPNEL = 100;

    private final int[] shrapnelValues = new int[Limb.values().length];
    private final boolean[] dislocated = new boolean[Limb.values().length];

    public LimbConditions() {}

    public LimbConditions(int headShrapnel, int leftArmShrapnel, int rightArmShrapnel,
                           int backShrapnel, int leftLegShrapnel, int rightLegShrapnel,
                           boolean headDislocated, boolean leftArmDislocated, boolean rightArmDislocated,
                           boolean backDislocated, boolean leftLegDislocated, boolean rightLegDislocated) {
        shrapnelValues[Limb.HEAD.ordinal()]      = clampShrapnel(headShrapnel);
        shrapnelValues[Limb.LEFT_ARM.ordinal()]  = clampShrapnel(leftArmShrapnel);
        shrapnelValues[Limb.RIGHT_ARM.ordinal()] = clampShrapnel(rightArmShrapnel);
        shrapnelValues[Limb.BACK.ordinal()]      = clampShrapnel(backShrapnel);
        shrapnelValues[Limb.LEFT_LEG.ordinal()]  = clampShrapnel(leftLegShrapnel);
        shrapnelValues[Limb.RIGHT_LEG.ordinal()] = clampShrapnel(rightLegShrapnel);

        dislocated[Limb.HEAD.ordinal()]      = headDislocated;
        dislocated[Limb.LEFT_ARM.ordinal()]  = leftArmDislocated;
        dislocated[Limb.RIGHT_ARM.ordinal()] = rightArmDislocated;
        dislocated[Limb.BACK.ordinal()]      = backDislocated;
        dislocated[Limb.LEFT_LEG.ordinal()]  = leftLegDislocated;
        dislocated[Limb.RIGHT_LEG.ordinal()] = rightLegDislocated;
    }


    public int getShrapnel(Limb limb) {
        return shrapnelValues[limb.ordinal()];
    }

    public void setShrapnel(Limb limb, int value) {
        shrapnelValues[limb.ordinal()] = clampShrapnel(value);
    }

    public void addShrapnel(Limb limb, int amount) {
        setShrapnel(limb, getShrapnel(limb) + amount);
    }

    public void removeShrapnel(Limb limb, int amount) {
        setShrapnel(limb, getShrapnel(limb) - amount);
    }

    public boolean hasShrapnel(Limb limb) {
        return getShrapnel(limb) > 0;
    }


    public boolean isDislocated(Limb limb) {
        return dislocated[limb.ordinal()];
    }

    public void setDislocated(Limb limb, boolean value) {
        dislocated[limb.ordinal()] = value;
    }


    public static final Codec<LimbConditions> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            Codec.INT.optionalFieldOf("head_shrapnel",      0).forGetter(d -> d.getShrapnel(Limb.HEAD)),
            Codec.INT.optionalFieldOf("left_arm_shrapnel",  0).forGetter(d -> d.getShrapnel(Limb.LEFT_ARM)),
            Codec.INT.optionalFieldOf("right_arm_shrapnel", 0).forGetter(d -> d.getShrapnel(Limb.RIGHT_ARM)),
            Codec.INT.optionalFieldOf("back_shrapnel",      0).forGetter(d -> d.getShrapnel(Limb.BACK)),
            Codec.INT.optionalFieldOf("left_leg_shrapnel",  0).forGetter(d -> d.getShrapnel(Limb.LEFT_LEG)),
            Codec.INT.optionalFieldOf("right_leg_shrapnel", 0).forGetter(d -> d.getShrapnel(Limb.RIGHT_LEG)),
            Codec.BOOL.optionalFieldOf("head_dislocated",      false).forGetter(d -> d.isDislocated(Limb.HEAD)),
            Codec.BOOL.optionalFieldOf("left_arm_dislocated",  false).forGetter(d -> d.isDislocated(Limb.LEFT_ARM)),
            Codec.BOOL.optionalFieldOf("right_arm_dislocated", false).forGetter(d -> d.isDislocated(Limb.RIGHT_ARM)),
            Codec.BOOL.optionalFieldOf("back_dislocated",      false).forGetter(d -> d.isDislocated(Limb.BACK)),
            Codec.BOOL.optionalFieldOf("left_leg_dislocated",  false).forGetter(d -> d.isDislocated(Limb.LEFT_LEG)),
            Codec.BOOL.optionalFieldOf("right_leg_dislocated", false).forGetter(d -> d.isDislocated(Limb.RIGHT_LEG))
        ).apply(instance, LimbConditions::new)
    );


    private static int clampShrapnel(int v) {
        return Math.max(0, Math.min(MAX_SHRAPNEL, v));
    }

    @Override
    public String toString() {
        return "LimbConditions{shrapnel=" + Arrays.toString(shrapnelValues) + ", dislocated=" + Arrays.toString(dislocated) + "}";
    }
}
