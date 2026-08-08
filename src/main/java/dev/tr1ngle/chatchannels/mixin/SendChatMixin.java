package dev.tr1ngle.chatchannels.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.authlib.GameProfile;

import dev.tr1ngle.chatchannels.ChannelData;
import dev.tr1ngle.chatchannels.ChatChannels;
import net.minecraft.client.multiplayer.ClientPacketListener;

@Mixin(value = ClientPacketListener.class, priority = 1)
public abstract class SendChatMixin
{
	@Shadow
	public abstract void sendCommand(String command);

	@Inject(method = "sendChat", at = @At("HEAD"), cancellable = true)
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
				sendCommand(tm);
				break;
			case WHISPER:
				for (GameProfile profile : curChannel.whisperPlayers)
				{
					String w = "w " + profile.name() + " " + content;
					w = w.trim();
					sendCommand(w);
				}
				break;
		}

		ci.cancel();
	}
}
