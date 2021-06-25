package me.deadlymc.damagetint.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.deadlymc.damagetint.TintConfig;
import net.minecraft.client.Minecraft;
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
                executes(TintCommand::currentThreshold).
                then(argument("health", FloatArgumentType.floatArg(0)).
                        suggests((c, b) -> suggest(new String[]{"0", "10", "20"}, b)).
                        executes(c -> changeThreshold(FloatArgumentType.getFloat(c, "health")))).
                then(literal("dynamic").
                        executes(TintCommand::dynamic));
        dispatcher.register(command);
    }
    
    private static int currentThreshold(CommandContext<CommandSource> context) throws CommandSyntaxException
    {
        float health = TintConfig.instance().isDynamic() ? ((PlayerEntity) context.getSource().assertIsEntity()).getMaxHealth() : TintConfig.instance().getHealth();
        ClientCommandManager.sendFeedback(new StringTextComponent(TextFormatting.GRAY + "Damage tint starts at " + TextFormatting.RED + "" + TextFormatting.BOLD + health + " hp" + " (" + health / 2 + " hearts)" + TextFormatting.GRAY + "."));
        return 0;
    }
    
    private static int changeThreshold(Float health)
    {
        TintConfig.instance().update(health);
        ClientCommandManager.sendFeedback(new StringTextComponent(TextFormatting.GRAY + "Gradually tint screen at " + TextFormatting.RED + "" + TextFormatting.BOLD + health + " hp"+ " (" + health / 2 + " hearts)" + TextFormatting.GRAY + "."));
        return 0;
    }
    
    private static int dynamic(CommandContext<CommandSource> context)
    {
        TintConfig.instance().dynamic();
        String msg = "";
        if (TintConfig.instance().isDynamic()) {
            msg = TextFormatting.GRAY + "Health threshold will update dynamically!";
        } else {
            float health = TintConfig.instance().getHealth();
            msg = TextFormatting.GRAY + "Health threshold set to " + TextFormatting.RED + "" + TextFormatting.BOLD + health + " hp"+ " (" + health / 2 + " hearts)" + TextFormatting.GRAY + ".";
        }
        ClientCommandManager.sendFeedback(new StringTextComponent(msg));
        return 0;
    }
}
