package com.lunkoashtail.avaliproject.limb;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Arrays;











public class LimbData {

     
    public static final int MAX_BLEED = 100;

    
    private final int[] bleedValues = new int[Limb.values().length];

     
    public LimbData() {}

    



    public LimbData(int head, int leftArm, int rightArm, int back, int leftLeg, int rightLeg) {
        bleedValues[Limb.HEAD.ordinal()]      = clamp(head);
        bleedValues[Limb.LEFT_ARM.ordinal()]  = clamp(leftArm);
        bleedValues[Limb.RIGHT_ARM.ordinal()] = clamp(rightArm);
        bleedValues[Limb.BACK.ordinal()]      = clamp(back);
        bleedValues[Limb.LEFT_LEG.ordinal()]  = clamp(leftLeg);
        bleedValues[Limb.RIGHT_LEG.ordinal()] = clamp(rightLeg);
    }

    
    
    

    public int getBleed(Limb limb) {
        return bleedValues[limb.ordinal()];
    }

    public void setBleed(Limb limb, int value) {
        bleedValues[limb.ordinal()] = clamp(value);
    }

     
    public void addBleed(Limb limb, int amount) {
        setBleed(limb, getBleed(limb) + amount);
    }

     
    public void reduceBleed(Limb limb, int amount) {
        setBleed(limb, getBleed(limb) - amount);
    }

     
    public int getTotalBleed() {
        int total = 0;
        for (int v : bleedValues) total += v;
        return total;
    }

     
    public boolean isAnyBleeding() {
        for (int v : bleedValues) if (v > 0) return true;
        return false;
    }

    public void clear() {
        Arrays.fill(bleedValues, 0);
    }

    
    
    

    



    public static final Codec<LimbData> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            Codec.INT.optionalFieldOf("head",      0).forGetter(d -> d.getBleed(Limb.HEAD)),
            Codec.INT.optionalFieldOf("left_arm",  0).forGetter(d -> d.getBleed(Limb.LEFT_ARM)),
            Codec.INT.optionalFieldOf("right_arm", 0).forGetter(d -> d.getBleed(Limb.RIGHT_ARM)),
            Codec.INT.optionalFieldOf("back",      0).forGetter(d -> d.getBleed(Limb.BACK)),
            Codec.INT.optionalFieldOf("left_leg",  0).forGetter(d -> d.getBleed(Limb.LEFT_LEG)),
            Codec.INT.optionalFieldOf("right_leg", 0).forGetter(d -> d.getBleed(Limb.RIGHT_LEG))
        ).apply(instance, LimbData::new)
    );

    
    
    

    private static int clamp(int v) {
        return Math.max(0, Math.min(MAX_BLEED, v));
    }

    @Override
    public String toString() {
        return "LimbData" + Arrays.toString(bleedValues);
    }
}
