package me.deadlymc.damagetint.commands;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.command.CommandException;
import net.minecraft.util.text.*;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

public class ClientCommandManager
{
    public static void sendError(ITextComponent error)
    {
        sendFeedback(new StringTextComponent("").append(error).mergeStyle(TextFormatting.RED));
    }
    
    public static void sendFeedback(ITextComponent message)
    {
        Minecraft.getInstance().ingameGUI.getChatGUI().printChatMessage(message);
    }
    
    // Credits: Earthcomputer (clientcommands)
    public static int executeCommand(StringReader reader, String command)
    {
        ClientPlayerEntity player = Minecraft.getInstance().player;
        try
        {
            return player.connection.getCommandDispatcher().execute(reader, player.getCommandSource());
        }
        catch (CommandException e)
        {
            ClientCommandManager.sendError(e.getComponent());
        }
        catch (CommandSyntaxException e)
        {
            ClientCommandManager.sendError(TextComponentUtils.toTextComponent(e.getRawMessage()));
            if (e.getInput() != null && e.getCursor() >= 0)
            {
                int cursor = Math.min(e.getCursor(), e.getInput().length());
                IFormattableTextComponent text = new StringTextComponent("").mergeStyle(TextFormatting.GRAY).modifyStyle(style -> style.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command)));
                if (cursor > 10)
                    text.appendString("...");
                
                text.appendString(e.getInput().substring(Math.max(0, cursor - 10), cursor));
                if (cursor < e.getInput().length())
                {
                    text.append(new StringTextComponent(e.getInput().substring(cursor)).mergeStyle(TextFormatting.RED, TextFormatting.UNDERLINE));
                }
                
                text.append(new TranslationTextComponent("command.context.here").mergeStyle(TextFormatting.RED, TextFormatting.ITALIC));
                ClientCommandManager.sendError(text);
            }
        }
        catch (Exception e)
        {
            StringTextComponent error = new StringTextComponent(e.getMessage() == null ? e.getClass().getName() : e.getMessage());
            ClientCommandManager.sendError(new TranslationTextComponent("command.failed").modifyStyle(style -> style.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, error))));
            e.printStackTrace();
        }
        return 1;
    }
}
