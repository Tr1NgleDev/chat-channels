package dev.tr1ngle.chatchannels;

import com.mojang.serialization.Codec;

import net.minecraft.util.StringIdentifiable;

public enum Channel implements StringIdentifiable
{
	PUBLIC("public"),
	TEAM("team"),
	WHISPER("whisper"),
	;

	public static final Codec<Channel> CODEC = StringIdentifiable.createCodec(Channel::values);

	private final String name;

	Channel(String name)
	{
		this.name = name;
	}

	@Override
	public String asString()
	{
		return name;
	}
}
