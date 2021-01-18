package me.deadlymc.damagetint;

import com.mojang.brigadier.CommandDispatcher;
import me.deadlymc.damagetint.commands.TintCommand;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.command.CommandSource;
import net.minecraftforge.fml.network.FMLNetworkConstants;
import org.apache.commons.lang3.tuple.Pair;

@Mod("damagetint")
public class DamageTint
{
    public DamageTint()
    {
        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DISPLAYTEST, () -> Pair.of(() -> FMLNetworkConstants.IGNORESERVERONLY, (a, b) -> true));
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> ClientEventHandler::register);
    }
    
    public static void registerCommands(CommandDispatcher<CommandSource> dispatcher)
    {
        TintCommand.register(dispatcher);
    }
}
