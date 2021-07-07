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
        TintConfig config = TintConfig.instance();
        if (!config.getFile().exists())
        {
            TintConfig.instance().init();
        }
        else
        {
            TintConfig.instance().update();
        }
    }
}
