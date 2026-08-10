package com.lunkoashtail.avaliproject.command;

import com.lunkoashtail.avaliproject.pack.PackUtil;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;

public class PackCommand {
    public static LiteralArgumentBuilder<CommandSourceStack> build() {
        return Commands.literal("pack")
                .executes(ctx -> {
                    ServerPlayer player = ctx.getSource().getPlayerOrException();
                    PackUtil.sendSync(player);
                    return 1;
                });
    }
}
