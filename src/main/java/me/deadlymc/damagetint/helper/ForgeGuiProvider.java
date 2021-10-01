package me.deadlymc.damagetint.helper;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.level.GameType;

public interface ForgeGuiProvider
{
    void renderDamageTint(LocalPlayer player);

    boolean isGamemodeWithHearts(GameType type);
}
