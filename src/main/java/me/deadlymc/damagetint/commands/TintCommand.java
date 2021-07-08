package me.deadlymc.damagetint.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.deadlymc.damagetint.TintConfig;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;
import static net.minecraft.command.CommandSource.suggestMatching;

public class TintCommand
{
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher)
    {
        LiteralArgumentBuilder<ServerCommandSource> command = literal("tint").
                executes(TintCommand::status).
                then(argument("health", FloatArgumentType.floatArg(0)).
                        suggests((c, b) -> suggestMatching(new String[]{"0", "10", "20"}, b)).
                        executes(c -> changeThreshold(FloatArgumentType.getFloat(c, "health")))).
                then(literal("dynamic").
                        executes(TintCommand::dynamic_status).
                        then(argument("value", BoolArgumentType.bool()).
                                executes(c -> dynamic(BoolArgumentType.getBool(c, "value"))))).
                then(literal("reset").
                        executes(TintCommand::reset));
        dispatcher.register(command);
    }

    private static int status(CommandContext<ServerCommandSource> context) throws CommandSyntaxException
    {
        boolean dynamic = TintConfig.instance().isDynamic();
        float maxHealth = ((PlayerEntity) context.getSource().getEntityOrThrow()).getMaxHealth();
        float thresholdHealth = TintConfig.instance().getHealth();
        ClientCommandManager.sendFeedback(new LiteralText(Formatting.GRAY + "Predefined health threshold = " + Formatting.RED + "" + Formatting.BOLD + thresholdHealth + " hp" + " (" + thresholdHealth / 2 + " hearts)" + Formatting.GRAY + "."));
        if (dynamic)
            ClientCommandManager.sendFeedback(new LiteralText(Formatting.GRAY + "Current health threshold = " + Formatting.RED + "" + Formatting.BOLD + maxHealth + " hp" + " (" + maxHealth / 2 + " hearts)" + Formatting.GRAY + "."));
        ClientCommandManager.sendFeedback(new LiteralText(dynamic ?
                Formatting.GRAY + "Health threshold is updating dynamically! (Ignoring predefined value)" :
                Formatting.GRAY + "Health threshold is not updating dynamically! (Using predefined value)"));
        return 0;
    }

    private static int changeThreshold(float health)
    {
        TintConfig.instance().update(health, false);
        ClientCommandManager.sendFeedback(new LiteralText(Formatting.GRAY + "Gradually tint screen at " + Formatting.RED + "" + Formatting.BOLD + health + " hp"+ " (" + health / 2 + " hearts)" + Formatting.GRAY + "."));
        return 0;
    }

    private static int dynamic_status(CommandContext<ServerCommandSource> context)
    {
        boolean dynamic = TintConfig.instance().isDynamic();
        ClientCommandManager.sendFeedback(new LiteralText(dynamic ?
                Formatting.GRAY + "Health threshold is updating dynamically! (Ignoring predefined value)" :
                Formatting.GRAY + "Health threshold is not updating dynamically! (Using predefined value)"));
        return 0;
    }

    private static int dynamic(boolean dynamic)
    {
        TintConfig.instance().dynamic(dynamic);
        String msg = "";
        if (TintConfig.instance().isDynamic()) {
            msg = Formatting.GRAY + "Health threshold will update dynamically!";
        } else {
            float health = TintConfig.instance().getHealth();
            msg = Formatting.GRAY + "Health threshold will not update dynamically! Using predefined health threshold = " + Formatting.RED + "" + Formatting.BOLD + health + " hp"+ " (" + health / 2 + " hearts)" + Formatting.GRAY + ".";
        }
        ClientCommandManager.sendFeedback(new LiteralText(msg));
        return 0;
    }

    private static int reset(CommandContext<ServerCommandSource> context)
    {
        TintConfig.instance().update(20F, true);
        ClientCommandManager.sendFeedback(new LiteralText(Formatting.GRAY + "Reset all configs to default."));
        return 0;
    }
}
