package com.lunkoashtail.avaliproject.limb;

import com.lunkoashtail.avaliproject.AvaliProject;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;



























public enum BleedingTier {

    
    MINOR_BLEEDING        (1,   25,  0.5f,  "minor_bleeding_effect"),
    BLEEDING              (26,  50,  1.5f,  "bleeding_effect"),
    HEAVY_BLEEDING        (51,  75,  3.0f,  "heavy_bleeding_effect"),
    CATASTROPHIC_BLEEDING (76, 100,  5.0f,  "catastrophic_bleeding_effect");

     
    public final int minBleed;
     
    public final int maxBleed;
    



    public final float damagePerInterval;
    



    public final ResourceLocation icon;

    BleedingTier(int minBleed, int maxBleed, float damagePerInterval, String iconStem) {
        this.minBleed           = minBleed;
        this.maxBleed           = maxBleed;
        this.damagePerInterval  = damagePerInterval;
        this.icon = ResourceLocation.fromNamespaceAndPath(
                AvaliProject.MOD_ID, "textures/gui/effect/" + iconStem + ".png");
    }

    
    
    

    






    @Nullable
    public static BleedingTier fromBleedValue(int bleed) {
        if (bleed <= 0) return null;
        for (BleedingTier tier : values()) {
            if (bleed <= tier.maxBleed) return tier;
        }
        
        return CATASTROPHIC_BLEEDING;
    }

    
    
    

    public Component getDisplayName() {
        return Component.translatable("bleeding_tier.avaliproject." + name().toLowerCase());
    }

    



    public int getColor() {
        return switch (this) {
            case MINOR_BLEEDING        -> 0xFFFFDD44; 
            case BLEEDING              -> 0xFFFF8833; 
            case HEAVY_BLEEDING        -> 0xFFFF3333; 
            case CATASTROPHIC_BLEEDING -> 0xFF990000; 
        };
    }
}
