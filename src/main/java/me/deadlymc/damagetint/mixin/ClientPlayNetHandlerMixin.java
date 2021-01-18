package me.deadlymc.damagetint.mixin;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import me.deadlymc.damagetint.DamageTint;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SCommandListPacket;
import net.minecraft.command.CommandSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetHandler.class)
public abstract class ClientPlayNetHandlerMixin
{
    @Shadow private CommandDispatcher<ISuggestionProvider> commandDispatcher;
    
    @SuppressWarnings("unchecked")
    @Inject(method = "<init>", at = @At("RETURN"))
    public void onInit(Minecraft mcIn, Screen previousGuiScreen, NetworkManager networkManagerIn, GameProfile profileIn, CallbackInfo ci)
    {
        DamageTint.registerCommands((CommandDispatcher<CommandSource>) (Object) this.commandDispatcher);
    }
    
    @SuppressWarnings("unchecked")
    @Inject(method = "handleCommandList", at = @At("TAIL"))
    public void onOnCommandTree(SCommandListPacket packetIn, CallbackInfo ci)
    {
        DamageTint.registerCommands((CommandDispatcher<CommandSource>) (Object) this.commandDispatcher);
    }
}
