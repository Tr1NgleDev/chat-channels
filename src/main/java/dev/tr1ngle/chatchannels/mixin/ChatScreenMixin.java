package dev.tr1ngle.chatchannels.mixin;

import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import dev.tr1ngle.chatchannels.ChannelData;
import dev.tr1ngle.chatchannels.ChatChannels;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.util.math.Rect2i;

@Mixin(ChatScreen.class)
public class ChatScreenMixin
{
	@Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
	public void keyPressed(KeyInput input, CallbackInfoReturnable<Boolean> cir)
	{
		if (input.key() == ChatChannels.config.switchChannelKey.getKeyCode())
		{
			++ChatChannels.selectedChannel;
			if (ChatChannels.selectedChannel >= ChatChannels.channels.size())
			{
				ChatChannels.selectedChannel = 0;
			}

			cir.setReturnValue(true);
			return;
		}
	}

	@Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
	public void mouseClicked(Click click, boolean doubled, CallbackInfoReturnable<Boolean> cir)
	{
		if (click.button() != GLFW.GLFW_MOUSE_BUTTON_LEFT)
		{
			return;
		}

		int height = ((ChatScreen)(Object)this).height;

		int rectX = 2;
		int rectY = height - 16 - 12;
		int ind = 0;
		for (ChannelData channelData : ChatChannels.channels)
		{
			int rectWidth = channelData.rectWidth;

			if (new Rect2i(rectX, rectY, rectWidth, 12).contains((int)click.x(), (int)click.y()))
			{
				ChatChannels.selectedChannel = ind;

				cir.setReturnValue(true);
				return;
			}

			rectX += rectWidth + 1;
			
			++ind;
		}
	}

	@Inject(method = "render", at = @At("HEAD"))
	public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks, CallbackInfo ci)
	{
		int width = ((ChatScreen)(Object)this).width;
		int height = ((ChatScreen)(Object)this).height;

		ChatChannels.channelsRender(context, mouseX, mouseY, deltaTicks, width, height);
	}
}
