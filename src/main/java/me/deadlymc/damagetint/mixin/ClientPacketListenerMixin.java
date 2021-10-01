package me.deadlymc.damagetint.mixin;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import me.deadlymc.damagetint.DamageTint;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundCommandsPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin
{
    @Shadow private CommandDispatcher<SharedSuggestionProvider> commands;

    @SuppressWarnings("unchecked")
    @Inject(method = "<init>", at = @At("RETURN"))
    public void onInit(Minecraft mcIn, Screen previousGuiScreen, Connection networkManagerIn, GameProfile profileIn, CallbackInfo ci)
    {
        DamageTint.registerCommands((CommandDispatcher<CommandSourceStack>) (Object) this.commands);
    }

    @SuppressWarnings("unchecked")
    @Inject(method = "handleCommands", at = @At("TAIL"))
    public void onOnCommandTree(ClientboundCommandsPacket packetIn, CallbackInfo ci)
    {
        DamageTint.registerCommands((CommandDispatcher<CommandSourceStack>) (Object) this.commands);
    }
}
