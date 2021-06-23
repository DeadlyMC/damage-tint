package me.deadlymc.damagetint.mixin;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import me.deadlymc.damagetint.TintConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin
{
	@Shadow @Final private MinecraftClient client;
	
	@Shadow private int scaledHeight;
	
	@Shadow private int scaledWidth;
	
	@Shadow @Final private static Identifier VIGNETTE_TEXTURE;
	
	@Inject(method = "render", at = @At(value = "FIELD", ordinal = 0,
			target = "Lnet/minecraft/client/MinecraftClient;interactionManager:Lnet/minecraft/client/network/ClientPlayerInteractionManager;"))
	private void renderTint(MatrixStack matrices, float tickDelta, CallbackInfo ci)
	{
		ClientPlayerEntity player = this.client.player;
		if (player != null && this.client.interactionManager != null)
		{
			if (isGamemodeWithHearts(this.client.interactionManager.getCurrentGameMode()))
				this.renderDamageTint(player);
		}
	}
	
	private boolean isGamemodeWithHearts(GameMode mode)
	{
		return mode == GameMode.SURVIVAL || mode == GameMode.ADVENTURE;
	}
	
	private void renderDamageTint(ClientPlayerEntity player)
	{
		float health = player.getHealth();
		float threshold = TintConfig.instance().getHealth();
		if (health <= threshold)
		{
			float f = (threshold - health) / threshold + 1.0F / threshold * 2.0F;
			RenderSystem.disableDepthTest();
			RenderSystem.depthMask(false);
			RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.ZERO, GlStateManager.DstFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
			RenderSystem.setShaderColor(0.1F, f, f, 1.0F);

			RenderSystem.setShader(GameRenderer::getPositionTexShader);
			RenderSystem.setShaderTexture(0, VIGNETTE_TEXTURE);
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferBuilder = tessellator.getBuffer();
			bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
			bufferBuilder.vertex(0.0D, (double) this.scaledHeight, -90.0D).texture(0.0F, 1.0F).next();
			bufferBuilder.vertex((double) this.scaledWidth, (double) this.scaledHeight, -90.0D).texture(1.0F, 1.0F).next();
			bufferBuilder.vertex((double) this.scaledWidth, 0.0D, -90.0D).texture(1.0F, 0.0F).next();
			bufferBuilder.vertex(0.0D, 0.0D, -90.0D).texture(0.0F, 0.0F).next();
			tessellator.draw();
			RenderSystem.depthMask(true);
			RenderSystem.enableDepthTest();
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
			RenderSystem.defaultBlendFunc();
		}
	}
}
