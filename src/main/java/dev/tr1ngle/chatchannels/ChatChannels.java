package dev.tr1ngle.chatchannels;

import me.shedaniel.autoconfig.AutoConfigClient;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mojang.authlib.GameProfile;

import dev.xpple.clientarguments.arguments.CGameProfileArgument;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;

public class ChatChannels implements ClientModInitializer
{
	public static final String MOD_ID = "chat-channels";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static List<ChannelData> channels = new ArrayList<>();
	public static int selectedChannel = 0;
	
	public static ModConfig config;

	public static ChannelData getCurrentChannel()
	{
		selectedChannel = Math.clamp(selectedChannel, 0, channels.size() - 1);
		return channels.get(selectedChannel);
	}

	public static void drawRect(final GuiGraphicsExtractor graphics, int x, int y, int width, int height, int color)
	{
		graphics.fill(x, y, width + x, height + y, color);
	}

	public static void channelsRender(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a, int width, int height)
	{
		Minecraft client = Minecraft.getInstance();
		Font textRenderer = client.font;

		int rectX = 2;
		int rectY = height - 16 - 12;
		int ind = 0;
		for (ChannelData channelData : channels)
		{
			boolean selected = ind == selectedChannel;

			String channel = "";
			switch (channelData.type)
			{
				case PUBLIC:
					channel = Component.translatable("chat-channels.public").getString();
					break;
				case TEAM:
					channel = Component.translatable("chat-channels.team").getString();
					break;
				case WHISPER:
					for (GameProfile profile : channelData.whisperPlayers)
					{
						channel += profile.name() + ",";
					}
					channel = channel.substring(0, channel.length() - 1);
					break;
			}
			if (channel.length() > 18)
			{
				channel = channel.substring(0, 18);
				channel += "...";
			}

			int channelWidth = textRenderer.width(channel);
			int rectWidth = 2 + channelWidth + 2;

			if (selected)
			{
				drawRect(graphics, rectX - 1, rectY - 1, rectWidth + 2, 12 + 2, ARGB.color(0xAF, 0xFFFFFF));
			}
			drawRect(graphics, rectX, rectY, rectWidth, 12, selected ? ARGB.color(0xFF, 0x000000) : ARGB.color(0x7F, 0x000000));
			graphics.text(textRenderer, Component.nullToEmpty(channel), rectX + 2, rectY + 2, selected ? ARGB.color(0xFF, 0xFFFFFF) : ARGB.color(0x7F, 0xFFFFFF), true);

			rectX += rectWidth + 1;
			channelData.rectWidth = rectWidth;

			++ind;
		}

		if (config.showHelpText)
		{
			graphics.text(textRenderer, Component.translatable("chat-channels.switchChannelText", config.switchChannelKey.asString()), 2, rectY - 10, ARGB.color(0x7F, 0xFFFFFF), true);
		}
	}

	public static void clearChannels()
	{
		channels.clear();
		channels.add(new ChannelData(Channel.PUBLIC, new HashSet<>()));
		selectedChannel = 0;
	}

	public static int findChannel(ChannelData channel)
	{
		int ind = 0;
		for (ChannelData channelData : channels)
		{
			if (channel.type == channelData.type)
			{
				if (channel.type == Channel.WHISPER)
				{
					if (channel.whisperPlayers.equals(channelData.whisperPlayers))
					{
						return ind;
					}
				}
				else
				{
					return ind;
				}
			}
			++ind;
		}
		return -1;
	}

	public static boolean setChannel(ChannelData channel)
	{
		int prevSelected = selectedChannel;
		selectedChannel = findChannel(channel);
		if (selectedChannel == -1)
		{
			if (channels.size() >= 7)
			{
				selectedChannel = prevSelected;
				return false;
			}
			channels.add(channel);
			selectedChannel = channels.size() - 1;
		}
		return true;
	}

	@Override
	public void onInitializeClient()
	{
		LOGGER.info("Chat Channels yipeeeee :D - made by Tr1NgleDev");

		AutoConfig.register(ModConfig.class, JanksonConfigSerializer::new);
		
		config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
		if (config.switchChannelKey == null)
		{
			config.switchChannelKey = ModConfig.Keybind.LeftAlt;
		}

		ClientPlayConnectionEvents.JOIN.register((handler, sender, client) ->
		{
			clearChannels();
		});
		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
		{
			dispatcher.register(ClientCommands
				.literal("channel")
				.then(ClientCommands.literal("config")
					.executes(context ->
					{
						Minecraft.getInstance().execute(() -> {
							Minecraft.getInstance().setScreenAndShow(AutoConfigClient.getConfigScreen(ModConfig.class, Minecraft.getInstance().gui.screen()).get());
						});
						return 1;
					})
				)
				.then(ClientCommands.literal("clear")
					.executes(context ->
					{
						clearChannels();
						return 1;
					})
				)
				.then(ClientCommands.literal("get")
					.executes(context ->
					{
						ChannelData curChannel = getCurrentChannel();
						switch (curChannel.type)
						{
							case PUBLIC:
								context.getSource().sendFeedback(Component.translatable("chat-channels.currentChannel", Component.translatable("chat-channels.public")));
								break;
							case TEAM:
								context.getSource().sendFeedback(Component.translatable("chat-channels.currentChannel", Component.translatable("chat-channels.team")));
								break;
							case WHISPER:
								String msg = "";
								for (GameProfile profile : curChannel.whisperPlayers)
								{
									msg += profile.name() + "; ";
								}
								msg = msg.substring(0, msg.length() - 2);

								context.getSource().sendFeedback(Component.translatable("chat-channels.currentChannel", Component.translatable("chat-channels.whisper").append(": " + msg)));
								break;
						}
						return 1;
					})
				)
				.then(ClientCommands.literal("set")
					.then(ClientCommands.literal("public")
						.executes(context ->
						{
							setChannel(new ChannelData(Channel.PUBLIC, new HashSet<>()));
							return 1;
						}))
					.then(ClientCommands.literal("team")
						.executes(context ->
						{
							if (!setChannel(new ChannelData(Channel.TEAM, new HashSet<>())))
							{
								context.getSource().sendFeedback(Component.translatable("chat-channels.tooManyChannels"));
							}
							return 1;
						}))
					.then(ClientCommands.literal("whisper")
						.then(ClientCommands.argument("players", ListArgument.of(CGameProfileArgument.gameProfile()))
							.executes(context ->
							{
								Set<GameProfile> whisperPlayers = new HashSet<>();

								for (Object arg : ListArgument.getList(context, "players"))
								{
									CGameProfileArgument.Result a = (CGameProfileArgument.Result)arg;
									whisperPlayers.addAll(a.getNames(context.getSource()));
								}

								if (!setChannel(new ChannelData(Channel.WHISPER, whisperPlayers)))
								{
									context.getSource().sendFeedback(Component.translatable("chat-channels.tooManyChannels"));
								}

								return 1;
							})
						)
					)
				)
			);
		});
	}
}
