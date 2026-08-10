package com.lunkoashtail.avaliproject.event;

import com.lunkoashtail.avaliproject.AvaliProject;
import com.lunkoashtail.avaliproject.limb.ModAttachments;
import com.lunkoashtail.avaliproject.pack.PackMembership;
import com.lunkoashtail.avaliproject.pack.PackRecord;
import com.lunkoashtail.avaliproject.pack.PackSavedData;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@EventBusSubscriber(modid = AvaliProject.MOD_ID)
public class PackChatHandler {
    @SubscribeEvent
    public static void onNameFormat(PlayerEvent.NameFormat event) {
        Player player = event.getEntity();
        if (player.level().isClientSide() || !(player.level() instanceof ServerLevel serverLevel))
            return;

        PackMembership membership = player.getData(ModAttachments.PACK_MEMBERSHIP);
        if (membership.leaderUUID().isEmpty())
            return;

        PackRecord record = PackSavedData.get(serverLevel).get(membership.leaderUUID().get());
        if (record == null)
            return;

        event.setDisplayname(Component.literal("[" + record.getName() + "] ")
                .setStyle(Style.EMPTY.withColor(ChatFormatting.GOLD))
                .append(event.getDisplayname()));
    }
}
