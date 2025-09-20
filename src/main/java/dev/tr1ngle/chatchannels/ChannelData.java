package dev.tr1ngle.chatchannels;

import java.util.HashSet;
import java.util.Set;

import com.mojang.authlib.GameProfile;

public class ChannelData
{
	public Channel type = Channel.PUBLIC;
	public Set<GameProfile> whisperPlayers = new HashSet<>();
	public int rectWidth = 0;
	
	public ChannelData(Channel type, Set<GameProfile> whisperPlayers)
	{
		this.type = type;
		this.whisperPlayers = whisperPlayers;
	}
}
