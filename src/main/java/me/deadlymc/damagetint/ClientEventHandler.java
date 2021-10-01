package me.deadlymc.damagetint;

import me.deadlymc.damagetint.helper.ForgeGuiProvider;
import me.deadlymc.damagetint.mixin.GuiAccessor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.client.gui.OverlayRegistry;
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
        if (!config.getFile().exists()) {
            TintConfig.instance().init();
        } else {
            TintConfig.instance().update();
        }

        // Add Overlay
        OverlayRegistry.registerOverlayTop("Dynamic Tint", (gui, mStack, partialTicks, width, height) -> {
            gui.setupOverlayRenderState(true, false);
            LocalPlayer player = ((GuiAccessor) gui).getMinecraft().player;
            ForgeGuiProvider forgeGui = ((ForgeGuiProvider) gui);
            if (player != null && ((GuiAccessor) gui).getMinecraft().gameMode != null)
            {
                if (forgeGui.isGamemodeWithHearts(((GuiAccessor) gui).getMinecraft().gameMode.getPlayerMode()))
                    forgeGui.renderDamageTint(player);
            }
        });
    }
}
