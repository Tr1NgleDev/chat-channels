package dev.tr1ngle.chatchannels;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mojang.authlib.GameProfile;

import dev.xpple.clientarguments.arguments.CGameProfileArgument;

public class ChatChannels implements ClientModInitializer
{
	public static final String MOD_ID = "chat-channels";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static Channel curChannel = Channel.PUBLIC;
	public static List<GameProfile> whisperPlayers = new ArrayList<>();

	@Override
	public void onInitializeClient()
	{
		LOGGER.info("Chat Channels yipeeeee :D - made by Tr1NgleDev");
		ClientPlayConnectionEvents.JOIN.register((handler, sender, client) ->
		{
			whisperPlayers.clear();
			curChannel = Channel.PUBLIC;
		});
		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
		{
			dispatcher.register(ClientCommandManager
				.literal("channel")
				.then(ClientCommandManager.literal("get")
					.executes(context ->
					{
						switch (curChannel)
						{
							case PUBLIC:
								context.getSource().sendFeedback(Text.of("Current Channel: Public"));
								break;
							case TEAM:
								context.getSource().sendFeedback(Text.of("Current Channel: Team"));
								break;
							case PLAYER:
							case GROUP:
								String msg = "Current Channel (" + curChannel.asString() + "): ";
								for (GameProfile profile : whisperPlayers)
								{
									msg += profile.getName() + "; ";
								}
								
								context.getSource().sendFeedback(Text.of(msg));
								break;
						}
						return 1;
					})
				)
				.then(ClientCommandManager.literal("set")
					.then(ClientCommandManager.literal("public")
						.executes(context ->
						{
							whisperPlayers.clear();
							curChannel = Channel.PUBLIC;
							return 1;
						}))
					.then(ClientCommandManager.literal("team")
						.executes(context ->
						{
							whisperPlayers.clear();
							curChannel = Channel.TEAM;
							return 1;
						}))
					.then(ClientCommandManager.argument("player", CGameProfileArgument.gameProfile())
						.executes(context ->
						{
							whisperPlayers.clear();
							curChannel = Channel.PLAYER;

							whisperPlayers.addAll(CGameProfileArgument.getProfileArgument(context, "player"));

							System.out.println(whisperPlayers);

							return 1;
						}))
					.then(ClientCommandManager.literal("group")
						.then(ClientCommandManager.argument("players", ListArgument.of(CGameProfileArgument.gameProfile()))
							.executes(context ->
							{
								whisperPlayers.clear();
								curChannel = Channel.GROUP;

								for (Object arg : ListArgument.getList(context, "players"))
								{
									CGameProfileArgument.Result a = (CGameProfileArgument.Result)arg;
									whisperPlayers.addAll(a.getNames(context.getSource()));
								}

								System.out.println(whisperPlayers);

								return 1;
							})
						)
					)
				)
			);
		});
	}
}
