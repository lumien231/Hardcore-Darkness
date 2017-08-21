package lumien.hardcoredarkness.config;

import java.io.File;

import org.apache.logging.log4j.Level;

import lumien.hardcoredarkness.HardcoreDarkness;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ConfigHandler
{
	HardcoreDarknessConfig localConfig;
	HardcoreDarknessConfig serverConfig;
	
	File configFile;

	public void preInit(FMLPreInitializationEvent event)
	{
		configFile = event.getSuggestedConfigurationFile();
		reloadConfig();
	}
	
	public void reloadConfig()
	{
		localConfig = new HardcoreDarknessConfig();

		Configuration config = new Configuration(configFile);
		config.load();

		localConfig.setMode(config.get("Settings", "Mode", 0, "0: No minimum sky & block light, 1: No minimum block light, 2: Skylight is dependent on moon phase").getInt(0));
		localConfig.setDarkNether(config.getBoolean("Dark Nether", "Settings", true, "Whether the Nether is also supposed to have its minimum light removed"));
		localConfig.setDarkEnd(config.getBoolean("Dark End", "Settings", false, "Whether the End is also supposed to have its minimum light removed"));
		localConfig.setAlternativeNightSkylight(config.getBoolean("AlternativeNightSkyLight", "Settings", false, "Switches the slightly bluish NightSkyLight in mode 1 & 2 with a more greenish version."));
		localConfig.setGammaOverride(config.getFloat("GammaOverride", "Settings", -1.0F, -1.0F, 1.0F, "Setting this to something other than -1 will lock the gamma config settings to that value. (0.0 - 1.0)"));
		
		double[] moonLightList = config.get("Settings", "MoonLightList", new double[] { 0, 0.075, 0.15, 0.225, 0.3 }, "In mode 2 this list defines how much skylight there is when 0%/25%/50%/75%/100% of the moon is visible. (Values go from 0 (Total Darkness) to 1 (Total Brightness)).").getDoubleList();

		if (moonLightList.length == 5)
		{
			float[] floatList = new float[5];

			for (int i = 0; i < moonLightList.length; i++)
			{
				floatList[i] = 1F - (float) moonLightList[i];
			}

			localConfig.setMoonLightList(floatList);
		}

		String blackListString = config.getString("Dimension Blacklist", "Settings", "", "A list of dimension ids in which Hardcore Darkness will be completely disabled\nExample: S:\"Dimension Blacklist\"=-1,1");

		if (!blackListString.isEmpty())
		{
			String[] blackListSplit = blackListString.split(",");

			if (blackListSplit.length > 0)
			{
				for (String s : blackListSplit)
				{
					try
					{
						Integer i = Integer.parseInt(s);

						localConfig.addDimensionToBlacklist(i);
					}
					catch (NumberFormatException exception)
					{
						HardcoreDarkness.INSTANCE.logger.log(Level.ERROR, "Error parsing the dimension blacklist: " + s);
						exception.printStackTrace();
					}
				}
			}
			else
			{
				try
				{
					Integer i = Integer.parseInt(blackListString);

					localConfig.addDimensionToBlacklist(i);
				}
				catch (NumberFormatException exception)
				{
					HardcoreDarkness.INSTANCE.logger.log(Level.ERROR, "Error parsing the dimension blacklist: " + blackListSplit);
					exception.printStackTrace();
				}
			}
		}

		if (config.hasChanged())
		{
			config.save();
		}
	}

	public synchronized void setServerConfig(HardcoreDarknessConfig serverConfig)
	{
		this.serverConfig = serverConfig;
	}

	public synchronized HardcoreDarknessConfig getActiveConfig()
	{
		if (serverConfig != null)
		{
			return serverConfig;
		}
		else
		{
			return localConfig;
		}
	}
}
