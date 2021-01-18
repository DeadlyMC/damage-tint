package me.deadlymc.damagetint.mixin;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import me.deadlymc.damagetint.TintConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.IngameGui;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.world.GameType;
import net.minecraftforge.client.gui.ForgeIngameGui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ForgeIngameGui.class)
public abstract class ForgeIngameGuiMixin extends IngameGui
{
	public ForgeIngameGuiMixin(Minecraft mcIn)
	{
		super(mcIn);
	}
	
	@Inject(method = "renderIngameGui", at = @At(value = "FIELD", ordinal = 0,
			target = "Lnet/minecraft/client/Minecraft;playerController:Lnet/minecraft/client/multiplayer/PlayerController;"))
	private void renderTint(MatrixStack matrices, float partialTicks, CallbackInfo ci)
	{
		ClientPlayerEntity player = this.mc.player;
		if (player != null && this.mc.playerController != null)
		{
			if (isGamemodeWithHearts(this.mc.playerController.getCurrentGameType()))
				this.renderDamageTint(player);
		}
	}
	
	private boolean isGamemodeWithHearts(GameType type)
	{
		return type == GameType.SURVIVAL || type == GameType.ADVENTURE;
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
			RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
			RenderSystem.color4f(0.1F, f, f, 1.0F);
			
			this.mc.getTextureManager().bindTexture(VIGNETTE_TEX_PATH);
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferBuilder = tessellator.getBuffer();
			bufferBuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
			bufferBuilder.pos(0.0D, (double) this.scaledHeight, -90.0D).tex(0.0F, 1.0F).endVertex();
			bufferBuilder.pos((double) this.scaledWidth, (double) this.scaledHeight, -90.0D).tex(1.0F, 1.0F).endVertex();
			bufferBuilder.pos((double) this.scaledWidth, 0.0D, -90.0D).tex(1.0F, 0.0F).endVertex();
			bufferBuilder.pos(0.0D, 0.0D, -90.0D).tex(0.0F, 0.0F).endVertex();
			tessellator.draw();
			RenderSystem.depthMask(true);
			RenderSystem.enableDepthTest();
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			RenderSystem.defaultBlendFunc();
		}
	}
}
