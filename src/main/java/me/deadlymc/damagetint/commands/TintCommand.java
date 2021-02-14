package me.deadlymc.damagetint.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import me.deadlymc.damagetint.TintConfig;
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
                executes(TintCommand::currentThreshold).
                then(argument("health", IntegerArgumentType.integer(0, 20)).
                        suggests((c, b) -> suggestMatching(new String[]{"0", "10", "20"}, b)).
                        executes(c -> changeThreshold(IntegerArgumentType.getInteger(c, "health")))).
                then(literal("reset").
                        executes(TintCommand::reset));
        dispatcher.register(command);
    }
    
    private static int currentThreshold(CommandContext<ServerCommandSource> context)
    {
        int health = TintConfig.instance().getHealth();
        ClientCommandManager.sendFeedback(new LiteralText(Formatting.GRAY + "Damage tint starts at " + Formatting.RED + "" + Formatting.BOLD + health + " hp" + " (" + (double) health / 2 + " hearts)" + Formatting.GRAY + "."));
        return 0;
    }
    
    private static int changeThreshold(Integer health)
    {
        TintConfig.instance().update(health);
        ClientCommandManager.sendFeedback(new LiteralText(Formatting.GRAY + "Gradually tint screen at " + Formatting.RED + "" + Formatting.BOLD + health + " hp"+ " (" + (double) health / 2 + " hearts)" + Formatting.GRAY + "."));
        return 0;
    }
    
    private static int reset(CommandContext<ServerCommandSource> context)
    {
        TintConfig.instance().update(20);
        ClientCommandManager.sendFeedback(new LiteralText(Formatting.GRAY + "Health threshold reset to " + Formatting.RED + "" + Formatting.BOLD + "20 hp"+ " (" + 20 / 2 + " hearts)" + Formatting.GRAY + "."));
        return 0;
    }
}
