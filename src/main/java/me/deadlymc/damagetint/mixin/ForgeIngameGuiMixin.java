package me.deadlymc.damagetint.mixin;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import me.deadlymc.damagetint.TintConfig;
import me.deadlymc.damagetint.helper.ForgeGuiProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.level.GameType;
import net.minecraftforge.client.gui.ForgeIngameGui;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ForgeIngameGui.class)
public abstract class ForgeIngameGuiMixin extends Gui implements ForgeGuiProvider
{
	public ForgeIngameGuiMixin(Minecraft mcIn)
	{
		super(mcIn);
	}

	@Override
	public boolean isGamemodeWithHearts(GameType type)
	{
		return type == GameType.SURVIVAL || type == GameType.ADVENTURE;
	}

	@Override
	public void renderDamageTint(LocalPlayer player)
	{
		float health = player.getHealth();
		float threshold = TintConfig.instance().isDynamic() ? player.getMaxHealth() : TintConfig.instance().getHealth();
		if (health <= threshold)
		{
			float f = (threshold - health) / threshold + 1.0F / threshold * 2.0F;
			RenderSystem.disableDepthTest();
			RenderSystem.depthMask(false);
			RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
			RenderSystem.setShaderColor(0.1F, f, f, 1.0F);

			RenderSystem.setShader(GameRenderer::getPositionTexShader);
			RenderSystem.setShaderTexture(0, VIGNETTE_LOCATION);
			Tesselator tessellator = Tesselator.getInstance();
			BufferBuilder bufferBuilder = tessellator.getBuilder();
			bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
			bufferBuilder.vertex(0.0D, (double) this.screenHeight, -90.0D).uv(0.0F, 1.0F).endVertex();
			bufferBuilder.vertex((double) this.screenWidth, (double) this.screenHeight, -90.0D).uv(1.0F, 1.0F).endVertex();
			bufferBuilder.vertex((double) this.screenWidth, 0.0D, -90.0D).uv(1.0F, 0.0F).endVertex();
			bufferBuilder.vertex(0.0D, 0.0D, -90.0D).uv(0.0F, 0.0F).endVertex();
			tessellator.end();
			RenderSystem.depthMask(true);
			RenderSystem.enableDepthTest();
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
			RenderSystem.defaultBlendFunc();
		}
	}
}
