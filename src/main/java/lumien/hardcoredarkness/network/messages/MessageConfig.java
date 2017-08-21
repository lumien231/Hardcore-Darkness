package lumien.hardcoredarkness.network.messages;

import java.util.HashSet;

import org.apache.logging.log4j.Level;

import io.netty.buffer.ByteBuf;
import lumien.hardcoredarkness.HardcoreDarkness;
import lumien.hardcoredarkness.config.HardcoreDarknessConfig;
import lumien.hardcoredarkness.handler.ReflectionHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MessageConfig implements IMessage, IMessageHandler<MessageConfig, IMessage>
{
	HardcoreDarknessConfig config;

	public MessageConfig()
	{

	}

	public MessageConfig(HardcoreDarknessConfig config)
	{
		this.config = config;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IMessage onMessage(final MessageConfig message, MessageContext ctx)
	{
		Minecraft.getMinecraft().addScheduledTask(new Runnable()
		{
			@Override
			public void run()
			{				
				HardcoreDarknessConfig config = message.config;

				HardcoreDarkness.INSTANCE.logger.log(Level.DEBUG, "Received Hardcore Darkness Config from Server: " + config.toString());

				HardcoreDarkness.INSTANCE.setServerConfig(config);

				HardcoreDarkness.INSTANCE.scheduleLightingRefresh();
			}
		});

		return null;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		this.config = new HardcoreDarknessConfig();

		config.setMode(buf.readInt());
		config.setDarkNether(buf.readBoolean());
		config.setDarkEnd(buf.readBoolean());
		config.setAlternativeNightSkylight(buf.readBoolean());
		config.setGammaOverride(buf.readFloat());

		float[] moonLightList = new float[5];
		for (int i = 0; i < 5; i++)
		{
			moonLightList[i] = buf.readFloat();
		}

		config.setMoonLightList(moonLightList);

		int blackListSize = buf.readInt();

		for (int i = 0; i < blackListSize; i++)
		{
			config.addDimensionToBlacklist(buf.readInt());
		}
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(config.getMode());
		buf.writeBoolean(config.darkNether());
		buf.writeBoolean(config.darkEnd());
		buf.writeBoolean(config.removeBlue());
		buf.writeFloat(config.getGammaOverride());

		float[] moonLightList = config.getMoonLightList();
		for (int i = 0; i < 5; i++)
		{
			buf.writeFloat(moonLightList[i]);
		}

		HashSet<Integer> blackList = config.getDimensionBlackList();
		buf.writeInt(blackList.size());
		for (Integer dimension : blackList)
		{
			buf.writeInt(dimension);
		}
	}
}
