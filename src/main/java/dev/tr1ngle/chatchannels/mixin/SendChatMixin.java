package dev.tr1ngle.chatchannels.mixin;

import net.minecraft.client.network.ClientPlayNetworkHandler;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.authlib.GameProfile;

import dev.tr1ngle.chatchannels.ChannelData;
import dev.tr1ngle.chatchannels.ChatChannels;

@Mixin(value = ClientPlayNetworkHandler.class, priority = 1)
public abstract class SendChatMixin
{
	@Shadow
	public abstract void sendChatCommand(String command);

	@Inject(method = "sendChatMessage", at = @At("HEAD"), cancellable = true)
	private void onSendChat(String content, CallbackInfo ci)
	{
		if (content.startsWith("/"))
		{
			return;
		}

		ChannelData curChannel = ChatChannels.getCurrentChannel();
		switch (curChannel.type)
		{
			case PUBLIC:
				return;
			case TEAM:
				String tm = "tm " + content;
				tm = tm.trim();
				sendChatCommand(tm);
				break;
			case WHISPER:
				for (GameProfile profile : curChannel.whisperPlayers)
				{
					String w = "w " + profile.getName() + " " + content;
					w = w.trim();
					sendChatCommand(w);
				}
				break;
		}

		ci.cancel();
	}
}
