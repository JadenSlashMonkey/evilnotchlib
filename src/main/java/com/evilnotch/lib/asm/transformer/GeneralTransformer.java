package com.evilnotch.lib.asm.transformer;

import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;

import java.io.IOException;
import java.util.List;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.evilnotch.lib.api.mcp.MCPSidedString;
import com.evilnotch.lib.asm.util.ASMHelper;

public class GeneralTransformer {
	
	/**
	 * changes the execution of where the invoking static method takes the middle click to my method
	 */
	public static void transformMC(ClassNode classNode) 
	{
    	MethodNode mnode = ASMHelper.getMethodNode(classNode, new MCPSidedString("middleClickMouse","func_147112_ai").toString(), "()V");
    	for(AbstractInsnNode node : mnode.instructions.toArray())
    	{
    		if(node.getOpcode() == Opcodes.INVOKESTATIC && node instanceof MethodInsnNode)
    		{
    			MethodInsnNode n = (MethodInsnNode)node;
    			if(n.owner.equals("net/minecraftforge/common/ForgeHooks"))
    			{
    				n.name = "pickBlock";
    				n.owner = "com/evilnotch/lib/main/eventhandler/PickBlock";
    				System.out.println("patched Minecraft#middleClickMouse()");
    				break;
    			}
    		}
    	}
	}
	
	/**
	 * injects line EntityUtil.patchUUID(profile); into the classNode
	 */
	public static void injectUUIDPatcher(ClassNode playerList, boolean obfuscated) 
	{
		 final String method_name = obfuscated ? "func_148545_a" : "createPlayerForUser";
		 final String method_desc = "(Lcom/mojang/authlib/GameProfile;)Lnet/minecraft/entity/player/EntityPlayerMP;";
		 
		 MethodNode method = ASMHelper.getMethodNode(playerList, method_name, method_desc);
		 InsnList toInsert = new InsnList();
         toInsert.add(new VarInsnNode(ALOAD, 1));
         toInsert.add(new MethodInsnNode(INVOKESTATIC, "com/evilnotch/lib/minecraft/util/PlayerUtil", "patchUUID", "(Lcom/mojang/authlib/GameProfile;)V", false));
         method.instructions.insertBefore(ASMHelper.getFirstInstruction(method, Opcodes.ALOAD), toInsert);
	}
    
    /**
     * patch the bug when opening to lan where the host cannot open command blocks even though cheats are enabled for him but, not everybody else
     */
	public static void patchOpenToLan(ClassNode classNode) 
	{
		MethodNode method = ASMHelper.getMethodNode(classNode, new MCPSidedString("shareToLAN","func_71206_a").toString(), "(Lnet/minecraft/world/GameType;Z)Ljava/lang/String;");
		AbstractInsnNode spot = null;
		AbstractInsnNode[] arr = method.instructions.toArray();
		for(int i=arr.length-1;i>=0;i--)
		{
			AbstractInsnNode ab = arr[i];
			if(ab.getOpcode() == Opcodes.ICONST_0 && ab.getPrevious() instanceof FrameNode)
			{
				List<Object> objs = ((FrameNode)ab.getPrevious()).stack;
				if(objs != null && objs.contains("net/minecraft/client/entity/EntityPlayerSP"))
				{
					spot = ab;
					break;
				}
			}
		}
		
		InsnList toInsert = new InsnList();
	    toInsert.add(new VarInsnNode(ALOAD, 0));
	   	toInsert.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/server/integrated/IntegratedServer", new MCPSidedString("mc","field_71349_l").toString(), "Lnet/minecraft/client/Minecraft;"));
	   	toInsert.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/Minecraft", new MCPSidedString("player","field_71439_g").toString(), "Lnet/minecraft/client/entity/EntityPlayerSP;"));
	   	toInsert.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/client/entity/EntityPlayerSP", new MCPSidedString("getPermissionLevel","func_184840_I").toString(), "()I", false));
	   	method.instructions.insert(spot, toInsert);
	   
	   	method.instructions.remove(spot);
	}
	/**
	 * adds a method call fixNBT() via constructor and adds the method
	 * @throws IOException 
	 */
	public static void patchWeightedSpawner(ClassNode classNode, String inputBase, String className) throws IOException 
	{
		MethodNode fixId = ASMHelper.addMethod(classNode, inputBase + "Methods", "fixIdLib", "(Lnet/minecraft/nbt/NBTTagCompound;)V");
		ASMHelper.patchMethod(fixId, className, "com/evilnotch/lib/asm/gen/Methods");
		
		MethodNode construct = ASMHelper.getConstructionNode(classNode, "(ILnet/minecraft/nbt/NBTTagCompound;)V");
		AbstractInsnNode point = ASMHelper.getLastPutField(construct);
		
		InsnList list = new InsnList();
		list.add(new VarInsnNode(ALOAD, 0));
		list.add(new VarInsnNode(ALOAD, 2));
		list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/util/WeightedSpawnerEntity", "fixIdLib", "(Lnet/minecraft/nbt/NBTTagCompound;)V", false));
		construct.instructions.insert(point, list);
	}

}
