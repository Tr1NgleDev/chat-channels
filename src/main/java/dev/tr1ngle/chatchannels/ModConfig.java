package dev.tr1ngle.chatchannels;

import org.lwjgl.glfw.GLFW;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

@Config(name = "chat-channels")
public class ModConfig implements ConfigData
{
	public Keybind switchChannelKey = Keybind.LeftAlt;
	
	public static enum Keybind
	{
		LeftAlt("Left Alt", GLFW.GLFW_KEY_LEFT_ALT),
		RightAlt("Right Alt", GLFW.GLFW_KEY_RIGHT_ALT),
		LeftCtrl("Left Ctrl", GLFW.GLFW_KEY_LEFT_CONTROL),
		RightCtrl("Right Ctrl", GLFW.GLFW_KEY_RIGHT_CONTROL),
		RightShift("Right Shift", GLFW.GLFW_KEY_RIGHT_SHIFT),
		;

		private final String name;
		private final int keyCode;

		Keybind(String name, int keyCode)
		{
			this.name = name;
			this.keyCode = keyCode;
		}

		public String asString()
		{
			return name;
		}

		public int getKeyCode()
		{
			return keyCode;
		}
	}
}
