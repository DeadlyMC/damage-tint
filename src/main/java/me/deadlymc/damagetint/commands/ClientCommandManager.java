package me.deadlymc.damagetint.commands;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.CommandRuntimeException;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

public class ClientCommandManager
{
    public static void sendError(Component error)
    {
        sendFeedback(new TextComponent("").append(error).withStyle(ChatFormatting.RED));
    }
    
    public static void sendFeedback(Component message)
    {
        Minecraft.getInstance().gui.getChat().addMessage(message);
    }
    
    // Credits: Earthcomputer (clientcommands)
    public static int executeCommand(StringReader reader, String command)
    {
        LocalPlayer player = Minecraft.getInstance().player;
        try
        {
            return player.connection.getCommands().execute(reader, player.createCommandSourceStack());
        }
        catch (CommandRuntimeException e)
        {
            ClientCommandManager.sendError(e.getComponent());
        }
        catch (CommandSyntaxException e)
        {
            ClientCommandManager.sendError(ComponentUtils.fromMessage(e.getRawMessage()));
            if (e.getInput() != null && e.getCursor() >= 0)
            {
                int cursor = Math.min(e.getCursor(), e.getInput().length());
                MutableComponent text = new TextComponent("").withStyle(ChatFormatting.GRAY).withStyle(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command)));
                if (cursor > 10)
                    text.append("...");
                
                text.append(e.getInput().substring(Math.max(0, cursor - 10), cursor));
                if (cursor < e.getInput().length())
                {
                    text.append(new TextComponent(e.getInput().substring(cursor)).withStyle(ChatFormatting.RED, ChatFormatting.UNDERLINE));
                }
                
                text.append(new TranslatableComponent("command.context.here").withStyle(ChatFormatting.RED, ChatFormatting.ITALIC));
                ClientCommandManager.sendError(text);
            }
        }
        catch (Exception e)
        {
            TextComponent error = new TextComponent(e.getMessage() == null ? e.getClass().getName() : e.getMessage());
            ClientCommandManager.sendError(new TranslatableComponent("command.failed").withStyle(style -> style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, error))));
            e.printStackTrace();
        }
        return 1;
    }
}
