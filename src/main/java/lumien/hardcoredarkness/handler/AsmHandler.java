package lumien.hardcoredarkness.handler;

import java.awt.Color;

import lumien.hardcoredarkness.HardcoreDarkness;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;

public class AsmHandler
{
	public static boolean stopNetherLight()
	{
		if (!enabled())
		{
			return false;
		}

		return HardcoreDarkness.INSTANCE.getActiveConfig().darkNether();
	}

	public static boolean stopEndLight()
	{
		if (!enabled())
		{
			return false;
		}

		return HardcoreDarkness.INSTANCE.getActiveConfig().darkEnd();
	}

	public static boolean darkTwilightForest()
	{
		if (!enabled())
		{
			return false;
		}

		return true;
	}

	static float value2 = 0;

	static float maxBright = 0.7F;
	static float step = (1F - maxBright) / 10F;

	static int skyModeCache;

	public static float sky1()
	{
		if (!enabled())
		{
			return 0.8f;
		}

		skyModeCache = HardcoreDarkness.INSTANCE.getActiveConfig().getMode();

		switch (skyModeCache)
		{
			case 0:
				return 1;
			case 1:
				return 0.8F;
			case 2:
				float value;
				float moon = Minecraft.getMinecraft().theWorld.getCurrentMoonPhaseFactorBody();
				int phase = (int) (moon * 10);

				value = maxBright + (10 - phase) * step;
				value2 = 1 - value;
				return value;
			default:
				return 0.8F;
		}
	}

	public static float sky2()
	{
		if (!enabled())
		{
			return 0.2f;
		}

		switch (skyModeCache)
		{
			case 0:
				return 0;
			case 1:
				return 0.2F;
			case 2:
				return value2;
			default:
				return 0.2F;
		}
	}

	static int colorModeCache;
	static float savedRed;
	static float savedGreen;
	static float savedBlue;

	public static float modRed(float red)
	{
		if (!enabled())
		{
			return red;
		}

		if (colorModeCache == 1)
		{
			red = savedRed = Math.min(1, red + 0.1f);
		}
		savedRed = red;
		return red;
	}

	public static float modGreen(float green)
	{
		if (!enabled())
		{
			return green;
		}

		savedGreen = green;

		if (savedRed > green)
		{
			return Math.max(savedRed, green);
		}
		else if (savedRed < green)
		{
			return Math.min(savedRed, green);
		}
		else
		{
			return green;
		}
	}

	public static float modBlue(float blue)
	{
		if (!enabled())
		{
			return blue;
		}

		savedBlue = blue;

		if (savedRed > blue)
		{
			return Math.max(savedRed, blue);
		}
		else if (savedRed < blue)
		{
			return Math.min(savedRed, blue);
		}
		else
		{
			return blue;
		}
	}

	public static int[] modifyLightmap(int[] original)
	{
		if (!enabled())
		{
			return original;
		}

		colorModeCache = HardcoreDarkness.INSTANCE.getActiveConfig().getMode();
		if (colorModeCache > 0 && HardcoreDarkness.INSTANCE.getActiveConfig().removeBlue())
		{
			for (int i = 0; i < original.length; i++)
			{
				int height = i / 16;

				if (height != 0 && height < 16)
				{
					Color color = new Color(original[i]);

					Color newColor = new Color(modRed(1F / 255F * color.getRed()), modGreen(1F / 255F * color.getGreen()), modBlue(1F / 255F * color.getBlue()));

					original[i] = newColor.getRGB();
				}
			}
		}
		return original;
	}

	public static float up(float f)
	{
		if (enabled())
		{
			return 1;
		}
		else
		{
			return f;
		}
	}
	
	public static float down(float f)
	{
		if (enabled())
		{
			return 0;
		}
		else
		{
			return f;
		}
	}

	public static boolean enabled()
	{
		return HardcoreDarkness.INSTANCE.enabled;
	}
}
