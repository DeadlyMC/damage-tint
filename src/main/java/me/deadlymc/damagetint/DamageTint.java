package me.deadlymc.damagetint;

import com.mojang.brigadier.CommandDispatcher;
import me.deadlymc.damagetint.commands.TintCommand;
import net.minecraft.commands.CommandSourceStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fmllegacy.network.FMLNetworkConstants;

@Mod("damagetint")
public class DamageTint
{
    public DamageTint()
    {
        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> FMLNetworkConstants.IGNORESERVERONLY, (ver, remote) -> true));
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> ClientEventHandler::register);
    }
    
    public static void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher)
    {
        TintCommand.register(dispatcher);
    }
}
