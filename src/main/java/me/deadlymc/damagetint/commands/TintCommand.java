package me.deadlymc.damagetint.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import me.deadlymc.damagetint.TintConfig;
import net.minecraft.command.CommandSource;
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
                executes(TintCommand::currentThreshold).
                then(argument("health", IntegerArgumentType.integer(0, 20)).
                        suggests((c, b) -> suggest(new String[]{"0", "10", "20"}, b)).
                        executes(c -> changeThreshold(IntegerArgumentType.getInteger(c, "health")))).
                then(literal("reset").
                        executes(TintCommand::reset));
        dispatcher.register(command);
    }
    
    private static int currentThreshold(CommandContext<CommandSource> context)
    {
        int health = TintConfig.instance().getHealth();
        ClientCommandManager.sendFeedback(new StringTextComponent(TextFormatting.GRAY + "Damage tint starts at " + TextFormatting.RED + "" + TextFormatting.BOLD + health + " hp" + " (" + (double) health / 2 + " hearts)" + TextFormatting.GRAY + "."));
        return 0;
    }
    
    private static int changeThreshold(Integer health)
    {
        TintConfig.instance().update(health);
        ClientCommandManager.sendFeedback(new StringTextComponent(TextFormatting.GRAY + "Gradually tint screen at " + TextFormatting.RED + "" + TextFormatting.BOLD + health + " hp"+ " (" + (double) health / 2 + " hearts)" + TextFormatting.GRAY + "."));
        return 0;
    }
    
    private static int reset(CommandContext<CommandSource> context)
    {
        TintConfig.instance().update(20);
        ClientCommandManager.sendFeedback(new StringTextComponent(TextFormatting.GRAY + "Health threshold reset to " + TextFormatting.RED + "" + TextFormatting.BOLD + "20 hp"+ " (" + 20 / 2 + " hearts)" + TextFormatting.GRAY + "."));
        return 0;
    }
}
