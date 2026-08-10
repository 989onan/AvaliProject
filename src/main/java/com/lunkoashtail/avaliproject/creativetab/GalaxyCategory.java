package com.lunkoashtail.avaliproject.creativetab;

import com.lunkoashtail.avaliproject.AvaliProject;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public enum GalaxyCategory {
    ALL("all_items", null),
    WICKERBEASTS("wickerbeasts", tag("wickerbeasts")),
    FELKIN("felkin", tag("felkin")),
    AVALI("avali", tag("avali")),
    SERGAL("sergal", tag("sergal")),
    PROTOGEN("protogen", tag("protogen")),
    DUTCH_ANGEL_DRAGON("dutch_angel_dragon", tag("dutch_angel_dragon")),
    EXPIE("expie", tag("expie")),
    MANOKIT("manokit", tag("manokit")),
    YINGLET("yinglet", tag("yinglet")),
    ZORGOIA("zorgoia", tag("zorgoia")),
    SYNTH("synth", tag("synth"));

    private final String id;
    private final TagKey<Item> tag;
    private final ResourceLocation icon;

    GalaxyCategory(String id, TagKey<Item> tag) {
        this.id = id;
        this.tag = tag;
        this.icon = ResourceLocation.fromNamespaceAndPath(AvaliProject.MOD_ID, "creativetab/" + id);
    }

    private static TagKey<Item> tag(String path) {
        return ItemTags.create(ResourceLocation.fromNamespaceAndPath(AvaliProject.MOD_ID, path));
    }

    public String id() {
        return id;
    }

    public TagKey<Item> tag() {
        return tag;
    }

    public ResourceLocation icon() {
        return icon;
    }

    public Component displayName() {
        return Component.translatable("galaxycomesalive.category." + id);
    }

    public boolean matches(ItemStack stack) {
        return tag == null || stack.is(tag);
    }
}
