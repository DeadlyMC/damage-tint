package me.deadlymc.damagetint;

import com.mojang.brigadier.CommandDispatcher;
import me.deadlymc.damagetint.commands.TintCommand;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.server.command.ServerCommandSource;

public class DamageTint implements ClientModInitializer
{
    @Override
    public void onInitializeClient()
    {
        if (!TintConfig.instance().getFile().exists())
        {
            TintConfig.instance().init();
        }
        else
        {
            TintConfig.instance().update();
        }
    }
    
    public static void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher)
    {
        TintCommand.register(dispatcher);
    }
}
