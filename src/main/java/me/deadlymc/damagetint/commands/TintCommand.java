package me.deadlymc.damagetint.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.deadlymc.damagetint.TintConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Player;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;
import static net.minecraft.commands.SharedSuggestionProvider.suggest;

public class TintCommand
{
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher)
    {
        LiteralArgumentBuilder<CommandSourceStack> command = literal("tint").
                executes(TintCommand::status).
                then(argument("health", FloatArgumentType.floatArg(0)).
                        suggests((c, b) -> suggest(new String[]{"0", "10", "20"}, b)).
                        executes(c -> changeThreshold(FloatArgumentType.getFloat(c, "health")))).
                then(literal("dynamic").
                        executes(TintCommand::dynamic_status).
                        then(argument("value", BoolArgumentType.bool()).
                                executes(c -> dynamic(BoolArgumentType.getBool(c, "value"))))).
                then(literal("reset").
                        executes(TintCommand::reset));
        dispatcher.register(command);
    }
    
    private static int status(CommandContext<CommandSourceStack> context) throws CommandSyntaxException
    {
        boolean dynamic = TintConfig.instance().isDynamic();
        float maxHealth = ((Player) context.getSource().getEntityOrException()).getMaxHealth();
        float thresholdHealth = TintConfig.instance().getHealth();
        ClientCommandManager.sendFeedback(new TextComponent(ChatFormatting.GRAY + "Predefined health threshold = " + ChatFormatting.RED + "" + ChatFormatting.BOLD + thresholdHealth + " hp" + " (" + thresholdHealth / 2 + " hearts)" + ChatFormatting.GRAY + "."));
        if (dynamic)
            ClientCommandManager.sendFeedback(new TextComponent(ChatFormatting.GRAY + "Current health threshold = " + ChatFormatting.RED + "" + ChatFormatting.BOLD + maxHealth + " hp" + " (" + maxHealth / 2 + " hearts)" + ChatFormatting.GRAY + "."));
        ClientCommandManager.sendFeedback(new TextComponent(dynamic ?
                ChatFormatting.GRAY + "Health threshold is updating dynamically! (Ignoring predefined value)" :
                ChatFormatting.GRAY + "Health threshold is not updating dynamically! (Using predefined value)"));
        return 0;
    }
    
    private static int changeThreshold(float health)
    {
        TintConfig.instance().update(health, false);
        ClientCommandManager.sendFeedback(new TextComponent(ChatFormatting.GRAY + "Gradually tint screen at " + ChatFormatting.RED + "" + ChatFormatting.BOLD + health + " hp"+ " (" + health / 2 + " hearts)" + ChatFormatting.GRAY + "."));
        return 0;
    }

    private static int dynamic_status(CommandContext<CommandSourceStack> context)
    {
        boolean dynamic = TintConfig.instance().isDynamic();
        ClientCommandManager.sendFeedback(new TextComponent(dynamic ?
                ChatFormatting.GRAY + "Health threshold is updating dynamically! (Ignoring predefined value)" :
                ChatFormatting.GRAY + "Health threshold is not updating dynamically! (Using predefined value)"));
        return 0;
    }
    
    private static int dynamic(boolean dynamic)
    {
        TintConfig.instance().dynamic(dynamic);
        String msg = "";
        if (TintConfig.instance().isDynamic()) {
            msg = ChatFormatting.GRAY + "Health threshold will update dynamically!";
        } else {
            float health = TintConfig.instance().getHealth();
            msg = ChatFormatting.GRAY + "Health threshold will not update dynamically! Using predefined health threshold = " + ChatFormatting.RED + "" + ChatFormatting.BOLD + health + " hp"+ " (" + health / 2 + " hearts)" + ChatFormatting.GRAY + ".";
        }
        ClientCommandManager.sendFeedback(new TextComponent(msg));
        return 0;
    }

    private static int reset(CommandContext<CommandSourceStack> context)
    {
        TintConfig.instance().update(20F, true);
        ClientCommandManager.sendFeedback(new TextComponent(ChatFormatting.GRAY + "Reset all configs to default."));
        return 0;
    }
}
