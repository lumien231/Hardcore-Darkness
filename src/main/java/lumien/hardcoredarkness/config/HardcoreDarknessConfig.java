package lumien.hardcoredarkness.config;

import java.util.ArrayList;
import java.util.HashSet;

import net.minecraft.nbt.NBTTagCompound;

public class HardcoreDarknessConfig
{
	int mode;

	boolean darkNether;
	boolean darkEnd;

	boolean alternativeNightSkylight;
	
	HashSet<Integer> dimensionBlacklist;
	
	public HardcoreDarknessConfig()
	{
		dimensionBlacklist = new HashSet<Integer>();
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
		return "HardcoreDarknessConfig [mode=" + mode + ", darkNether=" + darkNether + ", darkEnd=" + darkEnd + ", alternativeNightSkylight=" + alternativeNightSkylight + "]";
	}
}
