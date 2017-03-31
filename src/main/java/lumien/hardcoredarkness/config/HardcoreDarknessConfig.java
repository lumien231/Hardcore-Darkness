package lumien.hardcoredarkness.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import net.minecraft.nbt.NBTTagCompound;

public class HardcoreDarknessConfig
{
	int mode;

	boolean darkNether;
	boolean darkEnd;

	boolean alternativeNightSkylight;

	HashSet<Integer> dimensionBlacklist;

	float[] moonLightList = new float[5];
	
	float gammaOverride = -1;

	public HardcoreDarknessConfig()
	{
		dimensionBlacklist = new HashSet<Integer>();

		moonLightList[0] = 1F;
		moonLightList[1] = 0.925F;
		moonLightList[2] = 0.85F;
		moonLightList[3] = 0.775F;
		moonLightList[4] = 0.7F;
	}

	public void addDimensionToBlacklist(int dimension)
	{
		dimensionBlacklist.add(dimension);
	}

	public boolean isDimensionBlacklisted(int dimension)
	{
		return dimensionBlacklist.contains(dimension);
	}

	public HashSet<Integer> getDimensionBlackList()
	{
		return dimensionBlacklist;
	}

	public void setMode(int newMode)
	{
		this.mode = newMode;
	}

	public void setDarkNether(boolean darkNether)
	{
		this.darkNether = darkNether;
	}

	public void setDarkEnd(boolean darkEnd)
	{
		this.darkEnd = darkEnd;
	}

	public void setAlternativeNightSkylight(boolean alternativeNightSkylight)
	{
		this.alternativeNightSkylight = alternativeNightSkylight;
	}

	public int getMode()
	{
		return mode;
	}

	public boolean darkNether()
	{
		return darkNether;
	}

	public boolean darkEnd()
	{
		return darkEnd;
	}

	public boolean removeBlue()
	{
		return alternativeNightSkylight;
	}

	@Override
	public String toString()
	{
		return "HardcoreDarknessConfig [mode=" + mode + ", darkNether=" + darkNether + ", darkEnd=" + darkEnd + ", alternativeNightSkylight=" + alternativeNightSkylight + ", dimensionBlacklist=" + dimensionBlacklist + ", moonLightList=" + Arrays.toString(moonLightList) + ", gammaOverride=" + gammaOverride + "]";
	}

	public float[] getMoonLightList()
	{
		return moonLightList;
	}

	public void setMoonLightList(float[] newList)
	{
		this.moonLightList = newList;
	}
	
	public float getGammaOverride()
	{
		return gammaOverride;
	}
	
	public void setGammaOverride(float gammaOverride)
	{
		this.gammaOverride = gammaOverride;
	}
}
