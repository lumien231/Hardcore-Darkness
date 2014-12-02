package lumien.hardcoredarkness.asm;

import java.io.File;
import java.util.Iterator;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import static org.objectweb.asm.Opcodes.*;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.common.config.Configuration;

public class ClassTransformer implements IClassTransformer
{
	Logger logger = LogManager.getLogger("HardcoreDarkness");

	int mode;

	public ClassTransformer()
	{
		File f = new File("config/HardcoreDarkness.cfg");
		Configuration config = new Configuration(f);
		config.load();
		mode = config.get("Settings", "Mode", 0, "0: No minimum sky & block light, 1: No minimum block light").getInt(0);

		if (config.hasChanged())
			config.save();
	}

	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass)
	{
		if (transformedName.equals("net.minecraft.client.renderer.EntityRenderer"))
		{
			return patchEntityRendererClass(basicClass);
		}
		else if (transformedName.equals("net.minecraft.world.World") && mode == 0)
		{
			return patchWorldClass(basicClass);
		}

		return basicClass;
	}

	private byte[] patchWorldClass(byte[] basicClass)
	{
		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(basicClass);
		classReader.accept(classNode, 0);
		logger.log(Level.INFO, "Found World Class: " + classNode.name);

		String sunBrightnessName = "getSunBrightnessBody";

		int removeIndex = 0;

		MethodNode getSunBrightnessBody = null;
		MethodNode isBlockIndirectlyGettingPowered = null;

		for (MethodNode mn : classNode.methods)
		{
			if (mn.name.equals(sunBrightnessName))
			{
				getSunBrightnessBody = mn;
			}
		}

		if (getSunBrightnessBody != null)
		{
			for (int i = 0; i < getSunBrightnessBody.instructions.size(); i++)
			{
				AbstractInsnNode an = getSunBrightnessBody.instructions.get(i);
				if (an instanceof LdcInsnNode)
				{
					logger.log(Level.INFO, " - Patched minimal sky light");
					LdcInsnNode lin = (LdcInsnNode) an;

					if (lin.cst.equals(new Float("0.8")))
					{
						removeIndex = i;
					}
				}
			}
			for (int i = 0; i < 4; i++)
			{
				getSunBrightnessBody.instructions.remove(getSunBrightnessBody.instructions.get(removeIndex));
			}

		}

		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
		classNode.accept(writer);

		return writer.toByteArray();
	}

	private byte[] patchEntityRendererClass(byte[] basicClass)
	{
		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(basicClass);
		classReader.accept(classNode, 0);
		logger.log(Level.INFO, "Found EntityRenderer Class: " + classNode.name);

		String methodName = MCPNames.method("func_78472_g");

		int removeIndex = 0;

		MethodNode updateLightmap = null;

		for (MethodNode mn : classNode.methods)
		{
			if (mn.name.equals(methodName))
			{
				updateLightmap = mn;
			}
		}

		Float m0 = new Float("0.95");
		Float m1 = new Float("0.65");
		// Float m2 = new Float("0.69");
		Float m3 = new Float("0.96");

		Float a0 = new Float("0.05");
		Float a1 = new Float("0.35");
		// Float a2 = new Float("0.49");
		Float a3 = new Float("0.03");

		if (updateLightmap != null)
		{
			boolean insertedHook = false;
			Iterator<AbstractInsnNode> iterator = updateLightmap.instructions.iterator();

			for (int i = 0; i < updateLightmap.instructions.size(); i++)
			{
				AbstractInsnNode an = updateLightmap.instructions.get(i);
				if (an instanceof LdcInsnNode)
				{
					LdcInsnNode lin = (LdcInsnNode) an;

					if (lin.cst.equals(m0) || lin.cst.equals(m1) || lin.cst.equals(m3))
					{
						lin.cst = new Float("1.0");
					}
					else if (lin.cst.equals(a0) || lin.cst.equals(a1) || lin.cst.equals(a3))
					{
						lin.cst = new Float("0.0");
					}
				}
			}
		}

		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
		classNode.accept(writer);

		return writer.toByteArray();
	}
}
