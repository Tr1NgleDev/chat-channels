package dev.tr1ngle.chatchannels;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

public enum Channel implements StringRepresentable
{
	PUBLIC("public"),
	TEAM("team"),
	WHISPER("whisper"),
	;

	public static final Codec<Channel> CODEC = StringRepresentable.fromEnum(Channel::values);

	private final String name;

	Channel(String name)
	{
		this.name = name;
	}

	@Override
	public String getSerializedName()
	{
		return name;
	}
}
