package com.EvilNotch.lib.asm;

import static org.objectweb.asm.Opcodes.ALOAD;

import java.io.IOException;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.EvilNotch.lib.Api.MCPSidedString;
import com.EvilNotch.lib.minecraft.content.capabilites.registry.ICapProvider;


public class CapTransformer {

	public static void transFormEntityCaps(String name, ClassNode classNode, boolean obfuscated) throws IOException 
	{
    	implementICapProvider(classNode,name,"Lnet/minecraft/entity/Entity;");
    	
    	//Serialization and ticks
    	String owner = new MCPSidedString("net/minecraft/entity/Entity","vg").toString();
    	String desc_nbt = new MCPSidedString("Lnet/minecraft/nbt/NBTTagCompound;","Lfy;").toString();
    	String readDesc = "(Ljava/lang/Object;" + desc_nbt + ")V";
    	
     	String method_readFromNBT = new MCPSidedString("readFromNBT","f").toString();
    	MethodNode readFromNBT = TestTransformer.getMethodNode(classNode, method_readFromNBT, "(" + desc_nbt + ")V");
    	
    	//readFromNBT
    	InsnList toInsert1 = new InsnList();
    	toInsert1.add(new VarInsnNode(ALOAD,0));
    	toInsert1.add(new FieldInsnNode(Opcodes.GETFIELD, owner, "capContainer", "Lcom/EvilNotch/lib/minecraft/content/capabilites/registry/CapContainer;"));
    	toInsert1.add(new VarInsnNode(ALOAD,0));
    	toInsert1.add(new VarInsnNode(ALOAD,1));
    	toInsert1.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,"com/EvilNotch/lib/minecraft/content/capabilites/registry/CapContainer", "readFromNBT", readDesc, false));
    	AbstractInsnNode spotNode = TestTransformer.getFirstInstruction(readFromNBT, false, Opcodes.ALOAD);
    	readFromNBT.instructions.insertBefore(spotNode,toInsert1);
    	
    	String method_writeToNBT = new MCPSidedString("writeToNBT","f").toString();
        MethodNode writeToNBT = TestTransformer.getMethodNode(classNode,method_writeToNBT, "(" + desc_nbt + ")" + desc_nbt);
		
        //writeToNBT
        InsnList toInsert2 = new InsnList();
        toInsert2.add(new VarInsnNode(ALOAD,0));
        toInsert2.add(new FieldInsnNode(Opcodes.GETFIELD,  owner, "capContainer", "Lcom/EvilNotch/lib/minecraft/content/capabilites/registry/CapContainer;"));
        toInsert2.add(new VarInsnNode(ALOAD,0));
        toInsert2.add(new VarInsnNode(ALOAD,1));
   	    toInsert2.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,"com/EvilNotch/lib/minecraft/content/capabilites/registry/CapContainer", "writeToNBT", readDesc,false));
   	    AbstractInsnNode spotWriteNode = TestTransformer.getFirstInstruction(writeToNBT, false, Opcodes.ALOAD);
   	    writeToNBT.instructions.insertBefore(spotWriteNode,toInsert2);

   	    //tick injection
   	    MethodNode tick = TestTransformer.getMethodNode(classNode, new MCPSidedString("onEntityUpdate","Y").toString(), "()V");
   	    InsnList isntick = new InsnList();
   	    isntick.add(new VarInsnNode(ALOAD,0));
   	    isntick.add(new FieldInsnNode(Opcodes.GETFIELD,owner, "capContainer", "Lcom/EvilNotch/lib/minecraft/content/capabilites/registry/CapContainer;"));
   	    isntick.add(new VarInsnNode(ALOAD,0));
   	    isntick.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,"com/EvilNotch/lib/minecraft/content/capabilites/registry/CapContainer", "tick", "(Ljava/lang/Object;)V", false));
   	    AbstractInsnNode spotTickNode = TestTransformer.getFirstInstruction(tick, true, -1);
 	    tick.instructions.insert(spotTickNode,isntick);
	}

	public static void transFormTileEntityCaps(String name, ClassNode classNode, boolean obfuscated) throws IOException {
    	//add interface and implement methods
    	implementICapProvider(classNode,name,"Lnet/minecraft/tileentity/TileEntity;");
    	
    	//Serialization
    	String owner = new MCPSidedString("net/minecraft/tileentity/TileEntity","avj").toString();
    	String desc_nbt = new MCPSidedString("Lnet/minecraft/nbt/NBTTagCompound;","Lfy;").toString();
    	String readDesc = "(Ljava/lang/Object;" + desc_nbt + ")V";
    	
     	String method_readFromNBT = new MCPSidedString("readFromNBT","a").toString();
    	MethodNode readFromNBT = TestTransformer.getMethodNode(classNode, method_readFromNBT, "(" + desc_nbt + ")V");
    	
    	//readFromNBT
    	InsnList toInsert1 = new InsnList();
    	toInsert1.add(new VarInsnNode(ALOAD,0));
    	toInsert1.add(new FieldInsnNode(Opcodes.GETFIELD, owner, "capContainer", "Lcom/EvilNotch/lib/minecraft/content/capabilites/registry/CapContainer;"));
    	toInsert1.add(new VarInsnNode(ALOAD,0));
    	toInsert1.add(new VarInsnNode(ALOAD,1));
    	toInsert1.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,"com/EvilNotch/lib/minecraft/content/capabilites/registry/CapContainer", "readFromNBT", readDesc, false));
    	AbstractInsnNode spotNode = TestTransformer.getFirstInstruction(readFromNBT, false, Opcodes.ALOAD);
    	readFromNBT.instructions.insertBefore(spotNode,toInsert1);
    	
    	String method_writeToNBT = new MCPSidedString("writeToNBT","f").toString();
        MethodNode writeToNBT = TestTransformer.getMethodNode(classNode,method_writeToNBT, "(" + desc_nbt + ")" + desc_nbt);
		
        //writeToNBT
        InsnList toInsert2 = new InsnList();
        toInsert2.add(new VarInsnNode(ALOAD,0));
        toInsert2.add(new FieldInsnNode(Opcodes.GETFIELD,  owner, "capContainer", "Lcom/EvilNotch/lib/minecraft/content/capabilites/registry/CapContainer;"));
        toInsert2.add(new VarInsnNode(ALOAD,0));
        toInsert2.add(new VarInsnNode(ALOAD,1));
   	    toInsert2.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,"com/EvilNotch/lib/minecraft/content/capabilites/registry/CapContainer", "writeToNBT", readDesc,false));
   	    AbstractInsnNode spotWriteNode = TestTransformer.getFirstInstruction(writeToNBT, false, Opcodes.ALOAD);
   	    writeToNBT.instructions.insertBefore(spotWriteNode,toInsert2);
   	    
   	    //Constructor cap initiation
   	    MethodNode constructor = TestTransformer.getConstructionNode(classNode,"()V");
   	    InsnList toInsert3 = new InsnList();
   		toInsert3.add(new VarInsnNode(ALOAD,0));
   		toInsert3.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/EvilNotch/lib/minecraft/content/capabilites/registry/CapRegHandler", "registerCapsToObj", "(Ljava/lang/Object;)V", false));
   		//find insertion point in the constructor to make capabilities register themselves
   		for(AbstractInsnNode obj : constructor.instructions.toArray())
   		{
   			if(obj.getOpcode() == Opcodes.INVOKESTATIC)
   			{
   				MethodInsnNode node = (MethodInsnNode)obj;
   				if(node.owner.equals("net/minecraftforge/event/ForgeEventFactory") && node.name.equals("gatherCapabilities"))
   				{
   					constructor.instructions.insert(obj.getNext(), toInsert3);
   					break;
   				}
   			}
   		}
   	    //tick injection doesn't occur since a world hook is needed to make tiles tick
	}
	/**
	 * add capabilities into the itemstack
	 * @param name
	 * @param classNode
	 * @param obfuscated
	 * @throws IOException
	 */
	public static void transformItemStack(String name, ClassNode classNode, boolean obfuscated) throws IOException
	{
		String desc_nbt = new MCPSidedString("Lnet/minecraft/nbt/NBTTagCompound;","Lfy;").toString();
		
		implementICapProvider(classNode,name,"Lnet/minecraft/item/ItemStack;");
		
		InsnList toInsert1 = new InsnList();
		//inject CapRegUtil.registerCapsToObject(this);
		toInsert1.add(new VarInsnNode(ALOAD,0));
		toInsert1.add(new MethodInsnNode(Opcodes.INVOKESTATIC,"com/EvilNotch/lib/minecraft/content/capabilites/registry/CapRegHandler", "registerCapsToObj", "(Ljava/lang/Object;)V", false));
		
		//inject this.capContainer.readFromNBT(this,this.getTagCompound());
		toInsert1.add(new VarInsnNode(ALOAD,0));
		toInsert1.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/item/ItemStack", "capContainer", "Lcom/EvilNotch/lib/minecraft/content/capabilites/registry/CapContainer;"));
		toInsert1.add(new VarInsnNode(ALOAD,0));
		toInsert1.add(new VarInsnNode(ALOAD,0));
		toInsert1.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,"net/minecraft/item/ItemStack", "getTagCompound", "()Lnet/minecraft/nbt/NBTTagCompound;", false));
		toInsert1.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,"com/EvilNotch/lib/minecraft/content/capabilites/registry/CapContainer", "readFromNBT", "(Ljava/lang/Object;Lnet/minecraft/nbt/NBTTagCompound;)V", false));
		//inject all instructions
		MethodNode capNode = TestTransformer.getMethodNode(classNode, "forgeInit", "()V");
		AbstractInsnNode spotNode = TestTransformer.getFirstInstruction(capNode, false, Opcodes.ALOAD);
		capNode.instructions.insertBefore(spotNode, toInsert1);
		
		//writeToNBT inject this.capContainer.writeToNBT(this,nbt);
		InsnList toInsert2 = new InsnList();
		toInsert2.add(new VarInsnNode(ALOAD,0));
		toInsert2.add(new FieldInsnNode(Opcodes.GETFIELD,"net/minecraft/item/ItemStack", "capContainer", "Lcom/EvilNotch/lib/minecraft/content/capabilites/registry/CapContainer;"));
		toInsert2.add(new VarInsnNode(ALOAD,0));
		toInsert2.add(new VarInsnNode(ALOAD,1));
		toInsert2.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,"com/EvilNotch/lib/minecraft/content/capabilites/registry/CapContainer", "writeToNBT", "(Ljava/lang/Object;Lnet/minecraft/nbt/NBTTagCompound;)V", false));
		MethodNode write = TestTransformer.getMethodNode(classNode, "writeToNBT", "(" + desc_nbt + ")" + desc_nbt);
		AbstractInsnNode spoteWriteNode = TestTransformer.getLineNumberNode(write.instructions);
		write.instructions.insert(spoteWriteNode,toInsert2);
	}
	/**
	 * add world capabilities
	 * @throws IOException 
	 */
	public static void transformWorld(String name,ClassNode classNode,boolean obfuscated) throws IOException
	{
		implementICapProvider(classNode, name, "Lnet/minecraft/world/World;");
	}
	/**
	 * inject the line to make tile entities caps tick safer then screwing around with other people's classes
	 * @throws IOException 
	 */
	public static void injectWorldTickers(String name, ClassNode classNode, boolean obfuscated) throws IOException
	{
		//add a sided method to tickTileCaps ob/deob to the world.class
		MethodNode tickTileCaps = TestTransformer.addMethod(classNode, "com/EvilNotch/lib/asm/Caps.class", new MCPSidedString("tickTileCapsDeOb","tickTileCapsOb").toString(), "()V");
    	TestTransformer.patchLocals(tickTileCaps, name);
    	TestTransformer.patchInstructions(tickTileCaps, name, "com/EvilNotch/lib/asm/Caps");
    	
		MethodNode method = TestTransformer.getMethodNode(classNode, "updateEntities", "()V");
    	
		//put both world tickers here
		MethodNode tick = TestTransformer.getMethodNode(classNode, "tick", "()V");
		AbstractInsnNode tickPoint = TestTransformer.getFirstInstruction(tick, false, Opcodes.ALOAD);
		System.out.println("found injection for ticking point on WorldInfo");
		
		InsnList toTick = new InsnList();
		
		//inject line this.capContainer.tick(this);
		toTick.add(new VarInsnNode(ALOAD,0));
		toTick.add(new FieldInsnNode(Opcodes.GETFIELD,"net/minecraft/world/World", "capContainer", "Lcom/EvilNotch/lib/minecraft/content/capabilites/registry/CapContainer;"));
		toTick.add(new VarInsnNode(ALOAD,0));
		toTick.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "com/EvilNotch/lib/minecraft/content/capabilites/registry/CapContainer", "tick", "(Ljava/lang/Object;)V", false));
		//inject ((ICapProvider)this.worldInfo).getCapContainer().tick(this.worldInfo); right after the profilers start
		toTick.add(new VarInsnNode(ALOAD,0));
		toTick.add(new FieldInsnNode(Opcodes.GETFIELD,"net/minecraft/world/World", "worldInfo", "Lnet/minecraft/world/storage/WorldInfo;"));
		toTick.add(new TypeInsnNode(Opcodes.CHECKCAST,"com/EvilNotch/lib/minecraft/content/capabilites/registry/ICapProvider"));
		toTick.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE,"com/EvilNotch/lib/minecraft/content/capabilites/registry/ICapProvider", "getCapContainer", "()Lcom/EvilNotch/lib/minecraft/content/capabilites/registry/CapContainer;", true));
		toTick.add(new VarInsnNode(ALOAD,0));
		toTick.add(new FieldInsnNode(Opcodes.GETFIELD,"net/minecraft/world/World", "worldInfo", "Lnet/minecraft/world/storage/WorldInfo;"));
		toTick.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,"com/EvilNotch/lib/minecraft/content/capabilites/registry/CapContainer", "tick", "(Ljava/lang/Object;)V", false));
		
		method.instructions.insert(tickPoint,toTick);
    	
    	//look for first instanceof tickabletiles and the first instance it calls upon an iterator
		String fname = new MCPSidedString("tickableTileEntities","field_175730_i").toString();
		for(AbstractInsnNode obj : method.instructions.toArray())
		{
			if(obj instanceof FieldInsnNode)
			{
				FieldInsnNode isn = (FieldInsnNode)obj;
				if(isn.name.equals(fname))
				{
					AbstractInsnNode point = isn.getNext();
					if(point instanceof MethodInsnNode)
					{
						MethodInsnNode mnode = (MethodInsnNode)point;
						if(mnode.getOpcode() == Opcodes.INVOKEINTERFACE && mnode.name.equals("iterator"))
						{
							System.out.println("found injection point for ticking tile caps:" + point.getClass());
							InsnList toInsert = new InsnList();
							toInsert.add(new VarInsnNode(ALOAD,0));
							toInsert.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/world/World", tickTileCaps.name, "()V", false));
		
							method.instructions.insert(point.getNext(),toInsert);
							break;
						}
					}
				}	
			}
		}
	}
	/**
	 * add world caps to WorldInfo.class yes there is alot of line injections blame there being no default constructor
	 */
	public static void transformWorldInfo(ClassNode classNode,String name, boolean obfuscated) throws IOException
	{
		implementICapProvider(classNode,name,"Lnet/minecraft/world/storage/WorldInfo;");
		
		//inject caps to default constructor
		MethodNode defaultConstruct = TestTransformer.getConstructionNode(classNode, "()V");
		InsnList toInsertDefault = new InsnList();
		//make it visit a line so the decompiler loads right
		LabelNode labelnode = new LabelNode(new Label());
		LineNumberNode lineNode = new LineNumberNode(95,labelnode);
		toInsertDefault.add(labelnode);
		toInsertDefault.add(lineNode);
		toInsertDefault.add(new VarInsnNode(ALOAD,0));
		toInsertDefault.add(new MethodInsnNode(Opcodes.INVOKESTATIC,"com/EvilNotch/lib/minecraft/content/capabilites/registry/CapRegHandler", "registerCapsToObj", "(Ljava/lang/Object;)V", false));
		defaultConstruct.instructions.insert(TestTransformer.getLastPutField(defaultConstruct),toInsertDefault);
		
		//inject readFromNBT constructor
		MethodNode constructNBT = TestTransformer.getConstructionNode(classNode, "(Lnet/minecraft/nbt/NBTTagCompound;)V");
		for(AbstractInsnNode obj : constructNBT.instructions.toArray())
		{
			if(obj instanceof LdcInsnNode)
			{
				LdcInsnNode ldc = (LdcInsnNode)obj;
				if(ldc.cst.toString().equals("Version"))
				{
					System.out.println("found injection point for constructor worldinfo(NBTTagCompound)");
					AbstractInsnNode spot = ldc.getPrevious().getPrevious();
					InsnList toInsert = new InsnList();
					
					//inject CapRegHandler.registerCapsToObj(this)
					toInsert.add(new VarInsnNode(ALOAD,0));
					toInsert.add(new MethodInsnNode(Opcodes.INVOKESTATIC,"com/EvilNotch/lib/minecraft/content/capabilites/registry/CapRegHandler", "registerCapsToObj", "(Ljava/lang/Object;)V", false));
					
					//inject line this.capContainer.readFromNBT(this,nbt);
					toInsert.add(new VarInsnNode(ALOAD,0));
					toInsert.add(new FieldInsnNode(Opcodes.GETFIELD,"net/minecraft/world/storage/WorldInfo", "capContainer", "Lcom/EvilNotch/lib/minecraft/content/capabilites/registry/CapContainer;"));
					toInsert.add(new VarInsnNode(ALOAD,0));
					toInsert.add(new VarInsnNode(ALOAD,1));
					toInsert.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,"com/EvilNotch/lib/minecraft/content/capabilites/registry/CapContainer", "readFromNBT", "(Ljava/lang/Object;Lnet/minecraft/nbt/NBTTagCompound;)V", false));
					
					constructNBT.instructions.insert(spot,toInsert);
					break;
				}
			}
		 }
		
		//writeToNBT
		MethodNode writeToNBT = TestTransformer.getMethodNode(classNode, "updateTagCompound", "(Lnet/minecraft/nbt/NBTTagCompound;Lnet/minecraft/nbt/NBTTagCompound;)V");
		AbstractInsnNode spot = TestTransformer.getFirstInstruction(writeToNBT, true, -1);
		//inject line this.capContainer.writeToNBT(this,nbt);
		InsnList toInsert = new InsnList();
		toInsert.add(new VarInsnNode(ALOAD,0));
		toInsert.add(new FieldInsnNode(Opcodes.GETFIELD,"net/minecraft/world/storage/WorldInfo", "capContainer", "Lcom/EvilNotch/lib/minecraft/content/capabilites/registry/CapContainer;"));
		toInsert.add(new VarInsnNode(ALOAD,0));
		toInsert.add(new VarInsnNode(ALOAD,1));
		toInsert.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,"com/EvilNotch/lib/minecraft/content/capabilites/registry/CapContainer", "writeToNBT", "(Ljava/lang/Object;Lnet/minecraft/nbt/NBTTagCompound;)V", false));
		writeToNBT.instructions.insert(spot,toInsert);
		
		//patch the clone the cap container
		MethodNode constructCopy = TestTransformer.getConstructionNode(classNode, "(Lnet/minecraft/world/storage/WorldInfo;)V");
		AbstractInsnNode injectionPoint = TestTransformer.getLastPutField(constructCopy);
		
		//inject CapRegHandler.registerCapsToObj(this);
		InsnList toCopy = new InsnList();
		toCopy.add(new VarInsnNode(ALOAD,0));
		toCopy.add(new MethodInsnNode(Opcodes.INVOKESTATIC,"com/EvilNotch/lib/minecraft/content/capabilites/registry/CapRegHandler", "registerCapsToObj", "(Ljava/lang/Object;)V", false));
		
		//inject this.capContainer.readFromNBT(worldinfo,worldinfo.cloneNBT(null));
		toCopy.add(new VarInsnNode(ALOAD,0));
		toCopy.add(new FieldInsnNode(Opcodes.GETFIELD,"net/minecraft/world/storage/WorldInfo", "capContainer", "Lcom/EvilNotch/lib/minecraft/content/capabilites/registry/CapContainer;"));
		toCopy.add(new VarInsnNode(ALOAD,1));
		toCopy.add(new VarInsnNode(ALOAD,1));
		toCopy.add(new InsnNode(Opcodes.ACONST_NULL));
		toCopy.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,"net/minecraft/world/storage/WorldInfo", "cloneNBTCompound", "(Lnet/minecraft/nbt/NBTTagCompound;)Lnet/minecraft/nbt/NBTTagCompound;", false));
		toCopy.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,"com/EvilNotch/lib/minecraft/content/capabilites/registry/CapContainer", "readFromNBT", "(Ljava/lang/Object;Lnet/minecraft/nbt/NBTTagCompound;)V", false));
		constructCopy.instructions.insert(injectionPoint,toCopy);
		
		//inject CapRegHandler.registerCapsToObj(this) into the other constructor
		MethodNode construct = TestTransformer.getConstructionNode(classNode, "(Lnet/minecraft/world/WorldSettings;Ljava/lang/String;)V");
		InsnList toConstruct = new InsnList();
		toConstruct.add(new VarInsnNode(ALOAD,0));
		toConstruct.add(new MethodInsnNode(Opcodes.INVOKESTATIC,"com/EvilNotch/lib/minecraft/content/capabilites/registry/CapRegHandler", "registerCapsToObj", "(Ljava/lang/Object;)V", false));
		AbstractInsnNode constructSpot = TestTransformer.getLastPutField(construct);
		construct.instructions.insert(constructSpot, toConstruct);
	}
	/**
	 * injects registering of the caps to chunk and capContainer with interface the rest is handled via forge event since chunks really don't self serialize
	 * @param classNode
	 * @param name
	 * @param obfuscated
	 * @throws IOException
	 */
	public static void transformChunk(ClassNode classNode,String name,boolean obfuscated) throws IOException
	{
		implementICapProvider(classNode, name, "Lnet/minecraft/world/chunk/Chunk;");
		
		MethodNode constructor = TestTransformer.getConstructionNode(classNode, "(Lnet/minecraft/world/World;II)V");
		AbstractInsnNode spot = TestTransformer.getLastPutField(constructor);
		//inject CapRegHandler.registerCapToObj(this);
		InsnList toInsert = new InsnList();
		toInsert.add(new VarInsnNode(ALOAD,0));
		toInsert.add(new MethodInsnNode(Opcodes.INVOKESTATIC,"com/EvilNotch/lib/minecraft/content/capabilites/registry/CapRegHandler", "registerCapsToObj", "(Ljava/lang/Object;)V", false));
		constructor.instructions.insert(spot,toInsert);
		
		//make the caps tick
		MethodNode ticknode = TestTransformer.getMethodNode(classNode, "onTick", "(Z)V");
		InsnList toInsert2 = new InsnList();
		toInsert2.add(new VarInsnNode(ALOAD,0));
		toInsert2.add(new FieldInsnNode(Opcodes.GETFIELD,"net/minecraft/world/chunk/Chunk", "capContainer", "Lcom/EvilNotch/lib/minecraft/content/capabilites/registry/CapContainer;"));
		toInsert2.add(new VarInsnNode(ALOAD,0));
		toInsert2.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,"com/EvilNotch/lib/minecraft/content/capabilites/registry/CapContainer", "tick", "(Ljava/lang/Object;)V", false));
		AbstractInsnNode spotTick = TestTransformer.getFirstInstruction(ticknode, true, -1);
		ticknode.instructions.insert(spotTick,toInsert2);
	}
	
	/**
	 * we are not done patching the chunks until serialization is patched as well
	 */
	public static void transformAnvilChunkLoader(ClassNode classNode, String name, boolean obfuscated) 
	{
		//class org.objectweb.asm.tree.InsnNode
		MethodNode read = TestTransformer.getMethodNode(classNode, "readChunkFromNBT", "(Lnet/minecraft/world/World;Lnet/minecraft/nbt/NBTTagCompound;)Lnet/minecraft/world/chunk/Chunk;");
		//inject line ((ICapProvider)chunk).getCapContainer().readFromNBT(chunk, compound); right before the return statement
		InsnList toInsert = new InsnList();
		toInsert.add(new VarInsnNode(ALOAD,5));
		toInsert.add(new TypeInsnNode(Opcodes.CHECKCAST,"com/EvilNotch/lib/minecraft/content/capabilites/registry/ICapProvider"));
		toInsert.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE,"com/EvilNotch/lib/minecraft/content/capabilites/registry/ICapProvider", "getCapContainer", "()Lcom/EvilNotch/lib/minecraft/content/capabilites/registry/CapContainer;", true));			
		toInsert.add(new VarInsnNode(ALOAD,5));
		toInsert.add(new VarInsnNode(ALOAD,2));
		toInsert.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,"com/EvilNotch/lib/minecraft/content/capabilites/registry/CapContainer", "readFromNBT", "(Ljava/lang/Object;Lnet/minecraft/nbt/NBTTagCompound;)V", false));
		AbstractInsnNode readSpot = null;
		
		//find the last end of an if statement
		AbstractInsnNode[] arr = read.instructions.toArray();
		for(int i=arr.length-1;i>=0;i--)
		{
			AbstractInsnNode node = arr[i];
			if(node.getOpcode() == Opcodes.ALOAD)
			{
				System.out.println("found injection point for AnvilChunk.readChunkFromNBT()");
				readSpot = node;
				break;
			}
		}
		read.instructions.insertBefore(readSpot,toInsert);

		//writeToNBT inject this line ((ICapProvider)chunkIn).getCapContainer().writeToNBT(chunkIn, compound);
		MethodNode write = TestTransformer.getMethodNode(classNode, "writeChunkToNBT", "(Lnet/minecraft/world/chunk/Chunk;Lnet/minecraft/world/World;Lnet/minecraft/nbt/NBTTagCompound;)V");
		InsnList writeIsn = new InsnList();
		writeIsn.add(new VarInsnNode(ALOAD,1));
		writeIsn.add(new TypeInsnNode(Opcodes.CHECKCAST, "com/EvilNotch/lib/minecraft/content/capabilites/registry/ICapProvider"));
		writeIsn.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE,"com/EvilNotch/lib/minecraft/content/capabilites/registry/ICapProvider", "getCapContainer", "()Lcom/EvilNotch/lib/minecraft/content/capabilites/registry/CapContainer;", true));
		writeIsn.add(new VarInsnNode(ALOAD,1));
		writeIsn.add(new VarInsnNode(ALOAD,3));
		writeIsn.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,"com/EvilNotch/lib/minecraft/content/capabilites/registry/CapContainer", "writeToNBT", "(Ljava/lang/Object;Lnet/minecraft/nbt/NBTTagCompound;)V", false));
		
		AbstractInsnNode insertPoint = TestTransformer.getLastInstruction(write, Opcodes.RETURN);
		write.instructions.insertBefore(insertPoint, writeIsn);
	}

	/**
	 * make a class automatically implement ICapProvider without readFromNBT(),writeToNBT(), or tick() implementations
	 * @param classNode
	 * @param name
	 * @throws IOException
	 */
	public static void implementICapProvider(ClassNode classNode, String name,String classType) throws IOException {
		TestTransformer.addFeild(classNode, "capContainer", "Lcom/EvilNotch/lib/minecraft/content/capabilites/registry/CapContainer;","Lcom/EvilNotch/lib/minecraft/content/capabilites/registry/CapContainer<" + classType + ">;");
		
    	TestTransformer.addInterface(classNode, "com/EvilNotch/lib/minecraft/content/capabilites/registry/ICapProvider");
    	MethodNode getCap = TestTransformer.addMethod(classNode,"com/EvilNotch/lib/asm/Caps.class", "getCapContainer", "()Lcom/EvilNotch/lib/minecraft/content/capabilites/registry/CapContainer;");
    	TestTransformer.patchLocals(getCap, name);
    	TestTransformer.patchInstructions(getCap, name, "com/EvilNotch/lib/asm/Caps");
    	
    	MethodNode setCap = TestTransformer.addMethod(classNode,"com/EvilNotch/lib/asm/Caps.class", "setCapContainer", "(Lcom/EvilNotch/lib/minecraft/content/capabilites/registry/CapContainer;)V");
    	TestTransformer.patchLocals(setCap, name);
    	TestTransformer.patchInstructions(setCap, name, "com/EvilNotch/lib/asm/Caps");
	}
}