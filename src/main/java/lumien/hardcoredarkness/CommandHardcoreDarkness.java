package lumien.hardcoredarkness;

import java.util.List;

import lumien.hardcoredarkness.config.ConfigHandler;
import lumien.hardcoredarkness.network.PacketHandler;
import lumien.hardcoredarkness.network.messages.MessageConfig;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class CommandHardcoreDarkness extends CommandBase
{

	@Override
	public String getName()
	{
		return "hardcoredarkness";
	}

	@Override
	public String getUsage(ICommandSender sender)
	{
		return "/hardcoredarkness reload";
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos)
	{
		if (args.length == 1)
		{
			return getListOfStringsMatchingLastWord(args, new String[] { "reload" });
		}

		return super.getTabCompletions(server, sender, args, targetPos);
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		if (args.length == 1 && args[0].equals("reload"))
		{
			ConfigHandler configHandler = HardcoreDarkness.INSTANCE.configHandler;

			configHandler.reloadConfig();

			PacketHandler.INSTANCE.sendToAll(new MessageConfig(configHandler.getActiveConfig()));
		}
	}

}
