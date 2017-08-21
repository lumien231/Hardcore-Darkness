package lumien.hardcoredarkness.handler;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import lumien.hardcoredarkness.asm.MCPNames;
import net.minecraft.world.WorldProvider;

public class ReflectionHandler
{
	static Method generateLightBrightnessTable;
	
	static
	{
		try
		{
			generateLightBrightnessTable = WorldProvider.class.getDeclaredMethod(MCPNames.method("func_76556_a"));
		}
		catch (NoSuchMethodException e)
		{
			e.printStackTrace();
		}
		catch (SecurityException e)
		{
			e.printStackTrace();
		}
		generateLightBrightnessTable.setAccessible(true);
	}
	
	public static void refreshLighting(WorldProvider provider)
	{
		try
		{
			generateLightBrightnessTable.invoke(provider);
		}
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
		}
		catch (IllegalArgumentException e)
		{
			e.printStackTrace();
		}
		catch (InvocationTargetException e)
		{
			e.printStackTrace();
		}
	}
}
