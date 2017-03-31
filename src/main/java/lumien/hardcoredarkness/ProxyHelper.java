package lumien.hardcoredarkness;

import java.lang.ref.WeakReference;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ProxyHelper
{
	public static WeakReference<GuiScreen> fixedGUI = new WeakReference<GuiScreen>(null);
}
