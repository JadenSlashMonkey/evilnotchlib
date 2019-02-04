package com.evilnotch.iitemrender.asm;

import static org.objectweb.asm.Opcodes.ALOAD;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.evilnotch.iitemrender.asm.compat.JEI;
import com.evilnotch.iitemrender.handlers.IItemRendererHandler;
import com.evilnotch.lib.api.mcp.MCPSidedString;
import com.evilnotch.lib.asm.ConfigCore;
import com.evilnotch.lib.asm.FMLCorePlugin;
import com.evilnotch.lib.asm.util.ASMHelper;
import com.evilnotch.lib.asm.util.MCWriter;
import com.evilnotch.lib.util.JavaUtil;

import it.unimi.dsi.fastutil.bytes.Byte2BooleanSortedMaps.SynchronizedSortedMap;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.launchwrapper.IClassTransformer;

public class RenderTransformer  implements IClassTransformer{
	
    public static final List<String> classesBeingTransformed = JavaUtil.<String>asArray(new Object[]
    {
		"net.minecraftforge.client.ForgeHooksClient",
		"net.minecraft.client.Minecraft",
		"mezz.jei.render.IngredientListBatchRenderer"
    });

	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass) 
	{
		if(basicClass == null)
			return null;
		int index = classesBeingTransformed.indexOf(transformedName);
	    return index != -1 ? transform(index, basicClass, FMLCorePlugin.isObf) : basicClass;
	}
	/**
	 * f**k jei
	 */
	public byte[] transform(int index, byte[] basicClass, boolean obfuscated) 
	{
		try 
		{
			ClassNode classNode = ASMHelper.getClassNode(basicClass);
			switch (index)
			{
			  case 0:
				  patchCameraTransform(classNode);
			  break;
			  
			  case 1:
				  patchRenderItem(classNode);
		      break;
			  
			  case 2:
				  JEI.patchJEI(classNode);
		   	  break;
			  
			}
			ASMHelper.clearCacheNodes();
	   		ClassWriter classWriter = new MCWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        	classNode.accept(classWriter);
        	
        	int origin = index;
        	index += 14;
        	
            if(index == ConfigCore.cfgIndex || ConfigCore.cfgIndex == -2)
            {
          	  String[] a = classesBeingTransformed.get(origin).split("\\.");
          	  File f = new File(System.getProperty("user.home") + "/Desktop/" + a[a.length-1] + ".class");
          	  FileUtils.writeByteArrayToFile(f, classWriter.toByteArray());
            }
            
        	return classWriter.toByteArray();
		}
		catch (Throwable e) 
		{
			e.printStackTrace();
			return basicClass;
		}
	}
	
	public static void patchCameraTransform(ClassNode classNode) 
	{
		  System.out.println("patching ForgeHooksClient#handleCameraTransforms");
		  MethodNode camera = ASMHelper.getMethodNode(classNode, "handleCameraTransforms", "(Lnet/minecraft/client/renderer/block/model/IBakedModel;Lnet/minecraft/client/renderer/block/model/ItemCameraTransforms$TransformType;Z)Lnet/minecraft/client/renderer/block/model/IBakedModel;");
		 
		  InsnList toInsert0 = new InsnList();
	      toInsert0.add(new VarInsnNode(ALOAD,1));
	      toInsert0.add(new MethodInsnNode(Opcodes.INVOKESTATIC,"com/evilnotch/iitemrender/handlers/IItemRendererHandler", "handleCameraTransforms", "(Lnet/minecraft/client/renderer/block/model/ItemCameraTransforms$TransformType;)V", false));
	      camera.instructions.insertBefore(ASMHelper.getFirstInstruction(camera, Opcodes.ALOAD), toInsert0);
	}
	
	public static void patchRenderItem(ClassNode classNode) 
	{
		  System.out.println("patching Minecraft.class for IITemRenderer");
		  MethodNode method = ASMHelper.getMethodNode(classNode, new MCPSidedString("init","func_71384_a").toString(), "()V");
		  AbstractInsnNode start = null;
		  AbstractInsnNode end = null;
		  
		  AbstractInsnNode[] nodes = method.instructions.toArray();
		  for(AbstractInsnNode ab : nodes)
		  {
			  if(ab.getOpcode() == Opcodes.NEW)
			  {
				  TypeInsnNode node = (TypeInsnNode)ab;
				  if(node.desc.equals("net/minecraft/client/renderer/RenderItem"))
				  {
					  start = node.getPrevious();
				  }
			  }
			  else if(start != null && ab.getOpcode() == Opcodes.PUTFIELD)
			  {
				  end = ab.getPrevious();
				  break;
			  }
		  }
		 
		  InsnList toInsert = new InsnList();
		  toInsert.add(new TypeInsnNode(Opcodes.NEW, "com/evilnotch/iitemrender/handlers/RenderItemObj"));
		  toInsert.add(new InsnNode(Opcodes.DUP));
		  method.instructions.insert(start,toInsert);
		  method.instructions.insert(end,new MethodInsnNode(Opcodes.INVOKESPECIAL,"com/evilnotch/iitemrender/handlers/RenderItemObj", "<init>", "(Lnet/minecraft/client/renderer/RenderItem;)V", false));
	}

}