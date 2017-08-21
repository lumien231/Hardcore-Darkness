package lumien.hardcoredarkness.asm;

import static org.objectweb.asm.Opcodes.FADD;
import static org.objectweb.asm.Opcodes.FLOAD;
import static org.objectweb.asm.Opcodes.FMUL;
import static org.objectweb.asm.Opcodes.FSTORE;

import java.io.File;
import java.util.Iterator;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.common.config.Configuration;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import scala.actors.threadpool.Arrays;

public class ClassTransformer implements IClassTransformer
{
	Logger logger = LogManager.getLogger("HardcoreDarknessCore");

	public ClassTransformer()
	{

	}

	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass)
	{
		if (transformedName.equals("net.minecraft.client.renderer.EntityRenderer"))
		{
			return patchEntityRendererClass(basicClass);
		}
		else if (transformedName.equals("net.minecraft.world.World"))
		{
			return patchWorldClass(basicClass);
		}
		else if (transformedName.equals("net.minecraft.world.WorldProviderHell"))
		{
			return patchHellProvider(basicClass);
		}

		return basicClass;
	}

	private byte[] patchHellProvider(byte[] basicClass)
	{
		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(basicClass);
		classReader.accept(classNode, 0);
		logger.log(Level.DEBUG, "Found WorldProviderHell Class: " + classNode.name);

		MethodNode generateLightBrightnessTable = null;

		for (MethodNode mn : classNode.methods)
		{
			if (mn.name.equals(MCPNames.method("func_76556_a")))
			{
				generateLightBrightnessTable = mn;
				break;
			}
		}

		if (generateLightBrightnessTable != null)
		{
			logger.log(Level.DEBUG, " - Patched generateLightBrightnessTable");

			InsnList toInsert = new InsnList();

			LabelNode l1 = new LabelNode(new Label());

			toInsert.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "lumien/hardcoredarkness/handler/AsmHandler", "stopNetherLight", "()Z", false));
			toInsert.add(new JumpInsnNode(Opcodes.IFEQ, l1));
			toInsert.add(new VarInsnNode(Opcodes.ALOAD, 0));
			toInsert.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "net/minecraft/world/WorldProvider", MCPNames.method("func_76556_a"), "()V", false));
			toInsert.add(new InsnNode(Opcodes.RETURN));
			toInsert.add(l1);

			generateLightBrightnessTable.instructions.insert(toInsert);
		}

		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
		classNode.accept(writer);

		return writer.toByteArray();
	}

	private byte[] patchWorldClass(byte[] basicClass)
	{
		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(basicClass);
		classReader.accept(classNode, 0);
		logger.log(Level.DEBUG, "Found World Class: " + classNode.name);

		String sunBrightnessName = "getSunBrightnessBody";

		int removeIndex = 0;

		MethodNode getSunBrightnessBody = null;

		for (MethodNode mn : classNode.methods)
		{
			if (mn.name.equals(sunBrightnessName))
			{
				getSunBrightnessBody = mn;
			}
		}

		boolean activate2Power = false;

		Float add = new Float("0.2");
		Float mult = new Float("0.8");

		if (getSunBrightnessBody != null)
		{
			for (int i = 0; i < getSunBrightnessBody.instructions.size(); i++)
			{
				AbstractInsnNode an = getSunBrightnessBody.instructions.get(i);
				if (an instanceof LdcInsnNode)
				{
					LdcInsnNode lin = (LdcInsnNode) an;

					if (lin.cst.equals(mult))
					{
						logger.log(Level.DEBUG, " - Patched minimal sky light (1/2)");
						getSunBrightnessBody.instructions.set(lin, new MethodInsnNode(Opcodes.INVOKESTATIC, "lumien/hardcoredarkness/handler/AsmHandler", "sky1", "()F", false));
						activate2Power = true;
					}
					else if (activate2Power && lin.cst.equals(add))
					{
						logger.log(Level.DEBUG, " - Patched minimal sky light (2/2)");
						getSunBrightnessBody.instructions.set(lin, new MethodInsnNode(Opcodes.INVOKESTATIC, "lumien/hardcoredarkness/handler/AsmHandler", "sky2", "()F", false));
					}
				}
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
		logger.log(Level.DEBUG, "Found EntityRenderer Class: " + classNode.name);

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
		Float m3 = new Float("0.96");

		Float a0 = new Float("0.05");
		Float a3 = new Float("0.03");

		if (updateLightmap != null)
		{
			logger.log(Level.DEBUG, " - Patched updateLightmap");
			boolean insertedHook = false;

			boolean potion = false;
			for (int i = 0; i < updateLightmap.instructions.size(); i++)
			{
				AbstractInsnNode an = updateLightmap.instructions.get(i);
				if (an instanceof LdcInsnNode)
				{
					LdcInsnNode lin = (LdcInsnNode) an;

					if (!potion)
					{
						if (lin.cst.equals(m0) || lin.cst.equals(m3))
						{
							updateLightmap.instructions.insert(lin, new MethodInsnNode(Opcodes.INVOKESTATIC, "lumien/hardcoredarkness/handler/AsmHandler", "up", "(F)F", false));
						}
						else if (lin.cst.equals(a0) || lin.cst.equals(a3))
						{
							updateLightmap.instructions.insert(lin, new MethodInsnNode(Opcodes.INVOKESTATIC, "lumien/hardcoredarkness/handler/AsmHandler", "down", "(F)F", false));
						}
					}
				}
				else if (an instanceof MethodInsnNode)
				{
					MethodInsnNode min = (MethodInsnNode) an;
					if (min.name.equals(MCPNames.method("func_70644_a")))
					{
						if (updateLightmap.instructions.get(i + 1) instanceof JumpInsnNode)
						{
							float mod1 = 0.9f;
							float mod2 = 1f - mod1;
							logger.log(Level.DEBUG, " - Patched Nightvision Potion");
							AbstractInsnNode insertAfter = updateLightmap.instructions.get(i + 1);
							InsnList instructions = new InsnList();
							instructions.add(new VarInsnNode(FLOAD, 11));
							instructions.add(new LdcInsnNode(mod1));
							instructions.add(new InsnNode(FMUL));
							instructions.add(new LdcInsnNode(mod2));
							instructions.add(new InsnNode(FADD));
							instructions.add(new VarInsnNode(FSTORE, 11));

							instructions.add(new VarInsnNode(FLOAD, 12));
							instructions.add(new LdcInsnNode(mod1));
							instructions.add(new InsnNode(FMUL));
							instructions.add(new LdcInsnNode(mod2));
							instructions.add(new InsnNode(FADD));
							instructions.add(new VarInsnNode(FSTORE, 12));

							instructions.add(new VarInsnNode(FLOAD, 13));
							instructions.add(new LdcInsnNode(mod1));
							instructions.add(new InsnNode(FMUL));
							instructions.add(new LdcInsnNode(mod2));
							instructions.add(new InsnNode(FADD));
							instructions.add(new VarInsnNode(FSTORE, 13));

							updateLightmap.instructions.insert(insertAfter, instructions);
							i += 18;
						}
					}
					else if (min.name.equals(MCPNames.method("func_186068_a")))
					{
						logger.log(Level.DEBUG, " - Patched End Light Removal");
						InsnList toInsert = new InsnList();

						LabelNode l0 = new LabelNode(new Label());
						toInsert.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "lumien/hardcoredarkness/handler/AsmHandler", "stopEndLight", "()Z", false));
						toInsert.add(new JumpInsnNode(Opcodes.IFEQ, l0));
						toInsert.add(new InsnNode(Opcodes.POP));
						toInsert.add(new InsnNode(Opcodes.ICONST_0));
						toInsert.add(l0);

						updateLightmap.instructions.insert(updateLightmap.instructions.get(i), toInsert);
					}
					else if (min.name.equals(MCPNames.method("func_110564_a")))
					{
						logger.log(Level.DEBUG, " - Patched Lightmap Manipulation");
						InsnList toInsert = new InsnList();

						toInsert.add(new VarInsnNode(Opcodes.ALOAD, 0));
						toInsert.add(new VarInsnNode(Opcodes.ALOAD, 0));
						toInsert.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/renderer/EntityRenderer", MCPNames.field("field_78504_Q"), "[I"));
						toInsert.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "lumien/hardcoredarkness/handler/AsmHandler", "modifyLightmap", "([I)[I", false));
						toInsert.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraft/client/renderer/EntityRenderer", MCPNames.field("field_78504_Q"), "[I"));

						updateLightmap.instructions.insertBefore(min, toInsert);
						i += 5;
					}
				}
				else if (an instanceof FieldInsnNode)
				{
					FieldInsnNode fin = (FieldInsnNode) an;

					if (fin.name.equals(MCPNames.field("field_74333_Y")))
					{
						updateLightmap.instructions.insert(fin, new MethodInsnNode(Opcodes.INVOKESTATIC, "lumien/hardcoredarkness/handler/AsmHandler", "overrideGamma", "(F)F", false));
					}
				}
			}
		}

		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
		classNode.accept(writer);

		return writer.toByteArray();
	}
}
