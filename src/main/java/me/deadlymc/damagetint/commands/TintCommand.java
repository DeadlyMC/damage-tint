package me.deadlymc.damagetint.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.deadlymc.damagetint.TintConfig;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

import static net.minecraft.command.Commands.argument;
import static net.minecraft.command.Commands.literal;
import static net.minecraft.command.ISuggestionProvider.suggest;

public class TintCommand
{
    public static void register(CommandDispatcher<CommandSource> dispatcher)
    {
        LiteralArgumentBuilder<CommandSource> command = literal("tint").
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
    
    private static int status(CommandContext<CommandSource> context) throws CommandSyntaxException
    {
        boolean dynamic = TintConfig.instance().isDynamic();
        float maxHealth = ((PlayerEntity) context.getSource().assertIsEntity()).getMaxHealth();
        float thresholdHealth = TintConfig.instance().getHealth();
        ClientCommandManager.sendFeedback(new StringTextComponent(TextFormatting.GRAY + "Predefined health threshold = " + TextFormatting.RED + "" + TextFormatting.BOLD + thresholdHealth + " hp" + " (" + thresholdHealth / 2 + " hearts)" + TextFormatting.GRAY + "."));
        if (dynamic)
            ClientCommandManager.sendFeedback(new StringTextComponent(TextFormatting.GRAY + "Current health threshold = " + TextFormatting.RED + "" + TextFormatting.BOLD + maxHealth + " hp" + " (" + maxHealth / 2 + " hearts)" + TextFormatting.GRAY + "."));
        ClientCommandManager.sendFeedback(new StringTextComponent(dynamic ?
                TextFormatting.GRAY + "Health threshold is updating dynamically! (Ignoring predefined value)" :
                TextFormatting.GRAY + "Health threshold is not updating dynamically! (Using predefined value)"));
        return 0;
    }
    
    private static int changeThreshold(float health)
    {
        TintConfig.instance().update(health, false);
        ClientCommandManager.sendFeedback(new StringTextComponent(TextFormatting.GRAY + "Gradually tint screen at " + TextFormatting.RED + "" + TextFormatting.BOLD + health + " hp"+ " (" + health / 2 + " hearts)" + TextFormatting.GRAY + "."));
        return 0;
    }

    private static int dynamic_status(CommandContext<CommandSource> context)
    {
        boolean dynamic = TintConfig.instance().isDynamic();
        ClientCommandManager.sendFeedback(new StringTextComponent(dynamic ?
                TextFormatting.GRAY + "Health threshold is updating dynamically! (Ignoring predefined value)" :
                TextFormatting.GRAY + "Health threshold is not updating dynamically! (Using predefined value)"));
        return 0;
    }
    
    private static int dynamic(boolean dynamic)
    {
        TintConfig.instance().dynamic(dynamic);
        String msg = "";
        if (TintConfig.instance().isDynamic()) {
            msg = TextFormatting.GRAY + "Health threshold will update dynamically!";
        } else {
            float health = TintConfig.instance().getHealth();
            msg = TextFormatting.GRAY + "Health threshold will not update dynamically! Using predefined health threshold = " + TextFormatting.RED + "" + TextFormatting.BOLD + health + " hp"+ " (" + health / 2 + " hearts)" + TextFormatting.GRAY + ".";
        }
        ClientCommandManager.sendFeedback(new StringTextComponent(msg));
        return 0;
    }

    private static int reset(CommandContext<CommandSource> context)
    {
        TintConfig.instance().update(20F, true);
        ClientCommandManager.sendFeedback(new StringTextComponent(TextFormatting.GRAY + "Reset all configs to default."));
        return 0;
    }
}
