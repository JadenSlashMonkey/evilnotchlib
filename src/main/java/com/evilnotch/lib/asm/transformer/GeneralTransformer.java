package com.evilnotch.lib.asm.transformer;

import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
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
         toInsert.add(new VarInsnNode(ALOAD,1));
         toInsert.add(new MethodInsnNode(INVOKESTATIC, "com/evilnotch/lib/minecraft/util/EntityUtil", "patchUUID", "(Lcom/mojang/authlib/GameProfile;)V", false));
         method.instructions.insertBefore(ASMHelper.getFirstInstruction(method, Opcodes.ALOAD),toInsert);
	}

}