package lumien.hardcoredarkness;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.List;

import org.apache.logging.log4j.Logger;

import lumien.hardcoredarkness.asm.MCPNames;
import lumien.hardcoredarkness.config.ConfigHandler;
import lumien.hardcoredarkness.config.HardcoreDarknessConfig;
import lumien.hardcoredarkness.network.PacketHandler;
import lumien.hardcoredarkness.network.messages.MessageConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiOptionSlider;
import net.minecraft.client.gui.GuiOptionsRowList;
import net.minecraft.client.gui.GuiOptionsRowList.Row;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiVideoSettings;
import net.minecraft.client.settings.GameSettings;
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
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
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
	public void playerLogout(ClientDisconnectionFromServerEvent event)
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

			float gammaOverride = getActiveConfig().getGammaOverride();

			if (gammaOverride != -1)
			{
				GuiScreen openGUI = Minecraft.getMinecraft().currentScreen;

				if (openGUI instanceof GuiVideoSettings && openGUI != ProxyHelper.fixedGUI.get())
				{
					GuiOptionsRowList rowList = (GuiOptionsRowList) ((GuiVideoSettings) openGUI).optionsRowList;

					ProxyHelper.fixedGUI = new WeakReference<GuiScreen>(openGUI);
					
					for (GuiOptionsRowList.Row row : rowList.options)
					{
						GuiButton buttonA = row.buttonA;
						GuiButton buttonB = row.buttonB;

						if (buttonA instanceof GuiOptionSlider)
						{
							GuiOptionSlider slider = (GuiOptionSlider) buttonA;

							if (slider.options == GameSettings.Options.GAMMA)
							{
								row.buttonA = slider = new GuiOptionSlider(slider.id, slider.xPosition, slider.yPosition, GameSettings.Options.GAMMA, gammaOverride, gammaOverride)
								{
									@Override
									public boolean mousePressed(Minecraft mc, int mouseX, int mouseY)
									{
										return false;
									}

									@Override
									protected void mouseDragged(Minecraft mc, int mouseX, int mouseY)
									{
										return;
									}
								};

								String s = I18n.format(GameSettings.Options.GAMMA.getEnumString(), new Object[0]) + ": ";
								slider.sliderValue = gammaOverride;
								slider.displayString = gammaOverride == 0.0F ? s + I18n.format("options.gamma.min", new Object[0]) : (gammaOverride == 1.0F ? s + I18n.format("options.gamma.max", new Object[0]) : s + "+" + (int) (gammaOverride * 100.0F) + "%");
							}
						}

						if (buttonB instanceof GuiOptionSlider)
						{
							GuiOptionSlider slider = (GuiOptionSlider) buttonB;

							if (slider.options == GameSettings.Options.GAMMA)
							{
								row.buttonB = slider = new GuiOptionSlider(slider.id, slider.xPosition, slider.yPosition, GameSettings.Options.GAMMA, gammaOverride, gammaOverride)
								{
									@Override
									public boolean mousePressed(Minecraft mc, int mouseX, int mouseY)
									{
										return false;
									}

									@Override
									protected void mouseDragged(Minecraft mc, int mouseX, int mouseY)
									{
										return;
									}
								};

								String s = I18n.format(GameSettings.Options.GAMMA.getEnumString(), new Object[0]) + ": ";
								slider.sliderValue = gammaOverride;
								slider.displayString = gammaOverride == 0.0F ? s + I18n.format("options.gamma.min", new Object[0]) : (gammaOverride == 1.0F ? s + I18n.format("options.gamma.max", new Object[0]) : s + "+" + (int) (gammaOverride * 100.0F) + "%");
							}
						}
					}
				}
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
