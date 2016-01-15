package lumien.hardcoredarkness.network;

import lumien.hardcoredarkness.HardcoreDarkness;
import lumien.hardcoredarkness.network.messages.MessageConfig;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class PacketHandler
{
	public static final SimpleNetworkWrapper INSTANCE = new SimpleNetworkWrapper(HardcoreDarkness.MOD_ID);
	
	public static void init()
	{
		INSTANCE.registerMessage(MessageConfig.class, MessageConfig.class, 0, Side.CLIENT);
	}
}
