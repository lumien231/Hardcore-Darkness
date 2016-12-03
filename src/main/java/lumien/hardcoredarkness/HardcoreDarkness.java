package lumien.hardcoredarkness;

import org.apache.logging.log4j.Logger;

import lumien.hardcoredarkness.config.ConfigHandler;
import lumien.hardcoredarkness.config.HardcoreDarknessConfig;
import lumien.hardcoredarkness.network.PacketHandler;
import lumien.hardcoredarkness.network.messages.MessageConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(modid = HardcoreDarkness.MOD_ID, name = HardcoreDarkness.MOD_NAME, version = HardcoreDarkness.MOD_VERSION)
public class HardcoreDarkness
{
	public static final String MOD_ID = "hardcoredarkness";
	public static final String MOD_NAME = "Hardcore Darkness";
	public static final String MOD_VERSION = "@VERSION@";

	@Instance(MOD_ID)
	public static HardcoreDarkness INSTANCE;

	ConfigHandler configHandler;
	
	public Logger logger;
	
	public boolean enabled;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		logger = event.getModLog();
		
		configHandler = new ConfigHandler();
		configHandler.preInit(event);
		
		FMLCommonHandler.instance().bus().register(this);
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		PacketHandler.init();
	}
	
	@SubscribeEvent
	public void playerLogin(ClientConnectedToServerEvent event)
	{
		configHandler.setServerConfig(null);
	}
	
	@SubscribeEvent
	public void playerLoginServer(PlayerLoggedInEvent event)
	{
		PacketHandler.INSTANCE.sendTo(new MessageConfig(configHandler.getActiveConfig()), (EntityPlayerMP) event.player);
	}
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void clientTick(TickEvent.ClientTickEvent event)
	{
		if (event.phase == Phase.END)
		{
			EntityPlayerSP player = Minecraft.getMinecraft().player;

			if (player != null)
			{
				enabled = !getActiveConfig().isDimensionBlacklisted(player.dimension);
			}
			else
			{
				enabled = false;
			}
		}
	}

	public HardcoreDarknessConfig getActiveConfig()
	{
		return configHandler.getActiveConfig();
	}

	public void setServerConfig(HardcoreDarknessConfig config)
	{
		configHandler.setServerConfig(config);
	}
}
