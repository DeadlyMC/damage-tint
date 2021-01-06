package me.deadlymc.damagetint.commands;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.command.CommandException;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;

public class ClientCommandManager
{
    public static void sendError(Text error)
    {
        sendFeedback(new LiteralText("").append(error).formatted(Formatting.RED));
    }
    
    public static void sendFeedback(Text message)
    {
        MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(message);
    }
    
    // Credits: Earthcomputer (clientcommands)
    public static int executeCommand(StringReader reader, String command)
    {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        try
        {
            return player.networkHandler.getCommandDispatcher().execute(reader, player.getCommandSource());
        }
        catch (CommandException e)
        {
            ClientCommandManager.sendError(e.getTextMessage());
        }
        catch (CommandSyntaxException e)
        {
            ClientCommandManager.sendError(Texts.toText(e.getRawMessage()));
            if (e.getInput() != null && e.getCursor() >= 0)
            {
                int cursor = Math.min(e.getCursor(), e.getInput().length());
                MutableText text = new LiteralText("").formatted(Formatting.GRAY).styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command)));
                if (cursor > 10)
                    text.append("...");
                
                text.append(e.getInput().substring(Math.max(0, cursor - 10), cursor));
                if (cursor < e.getInput().length())
                {
                    text.append(new LiteralText(e.getInput().substring(cursor)).formatted(Formatting.RED, Formatting.UNDERLINE));
                }
                
                text.append(new TranslatableText("command.context.here").formatted(Formatting.RED, Formatting.ITALIC));
                ClientCommandManager.sendError(text);
            }
        }
        catch (Exception e)
        {
            LiteralText error = new LiteralText(e.getMessage() == null ? e.getClass().getName() : e.getMessage());
            ClientCommandManager.sendError(new TranslatableText("command.failed").styled(style -> style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, error))));
            e.printStackTrace();
        }
        return 1;
    }
}
