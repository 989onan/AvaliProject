package com.lunkoashtail.avaliproject.command;

import com.lunkoashtail.avaliproject.limb.Limb;
import com.lunkoashtail.avaliproject.limb.LimbConditions;
import com.lunkoashtail.avaliproject.screen.custom.DislocationMinigameScreen;
import com.lunkoashtail.avaliproject.screen.custom.ShrapnelMinigameScreen;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class TestMinigameCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> build() {
        return Commands.literal("avalitest")
                .then(Commands.literal("shrapnel")
                        .then(Commands.argument("limb", StringArgumentType.word())
                                .suggests((ctx, b) -> suggestLimbs(b))
                                .executes(ctx -> runShrapnel(ctx, LimbConditions.MAX_SHRAPNEL / 2))
                                .then(Commands.argument("severity",
                                                IntegerArgumentType.integer(1, LimbConditions.MAX_SHRAPNEL))
                                        .executes(ctx -> runShrapnel(ctx,
                                                IntegerArgumentType.getInteger(ctx, "severity"))))))

                .then(Commands.literal("dislocation")
                        .then(Commands.argument("limb", StringArgumentType.word())
                                .suggests((ctx, b) -> suggestLimbs(b))
                                .executes(TestMinigameCommand::runDislocation)));
    }

    private static int runShrapnel(CommandContext<CommandSourceStack> ctx, int severity) {
        Limb limb = resolveLimb(ctx.getSource(), StringArgumentType.getString(ctx, "limb"));
        if (limb == null) return 0;

        Minecraft.getInstance().setScreen(new ShrapnelMinigameScreen(limb, severity));
        ctx.getSource().sendSuccess(() -> Component.literal(
                "Opened shrapnel minigame on " + limb.getDisplayName().getString()
                        + " (severity " + severity + ")"), false);
        return 1;
    }

    private static int runDislocation(CommandContext<CommandSourceStack> ctx) {
        Limb limb = resolveLimb(ctx.getSource(), StringArgumentType.getString(ctx, "limb"));
        if (limb == null) return 0;

        Minecraft.getInstance().setScreen(new DislocationMinigameScreen(limb));
        ctx.getSource().sendSuccess(() -> Component.literal(
                "Opened dislocation minigame on " + limb.getDisplayName().getString()), false);
        return 1;
    }


    private static Limb resolveLimb(CommandSourceStack src, String key) {
        Limb limb = Limb.fromKey(key);
        if (limb == null) {
            src.sendFailure(Component.literal("Unknown limb '" + key + "'. Valid: " + validLimbs()));
        }
        return limb;
    }

    private static String validLimbs() {
        StringBuilder sb = new StringBuilder();
        for (Limb l : Limb.values()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(l.key);
        }
        return sb.toString();
    }

    private static java.util.concurrent.CompletableFuture<com.mojang.brigadier.suggestion.Suggestions>
    suggestLimbs(com.mojang.brigadier.suggestion.SuggestionsBuilder builder) {
        for (Limb l : Limb.values()) builder.suggest(l.key);
        return builder.buildFuture();
    }
}
