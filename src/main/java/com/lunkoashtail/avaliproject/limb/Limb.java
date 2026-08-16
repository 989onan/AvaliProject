package com.lunkoashtail.avaliproject.limb;

import net.minecraft.network.chat.Component;










public enum Limb {
    HEAD      ("head",      "limb.avaliproject.head"),
    LEFT_ARM  ("left_arm",  "limb.avaliproject.left_arm"),
    RIGHT_ARM ("right_arm", "limb.avaliproject.right_arm"),
    BACK      ("back",      "limb.avaliproject.back"),
    LEFT_LEG  ("left_leg",  "limb.avaliproject.left_leg"),
    RIGHT_LEG ("right_leg", "limb.avaliproject.right_leg");

     
    public final String key;
     
    public final String langKey;

    Limb(String key, String langKey) {
        this.key     = key;
        this.langKey = langKey;
    }

    public Component getDisplayName() {
        return Component.translatable(langKey);
    }

     
    public static Limb fromKey(String key) {
        for (Limb l : values()) {
            if (l.key.equals(key)) return l;
        }
        return null;
    }
}
