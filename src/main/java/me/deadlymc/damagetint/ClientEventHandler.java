package me.deadlymc.damagetint;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class ClientEventHandler
{
    public static void register()
    {
        FMLJavaModLoadingContext.get().getModEventBus().register(ClientEventHandler.class);
    }
    
    @SubscribeEvent
    public static void onInitializeClient(FMLClientSetupEvent event)
    {
        if (!TintConfig.instance().getFile().exists())
        {
            TintConfig.instance().init();
        }
        else
        {
            TintConfig.instance().update(null);
        }
    }
}
