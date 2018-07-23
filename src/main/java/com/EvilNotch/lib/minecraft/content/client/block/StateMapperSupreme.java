package com.EvilNotch.lib.minecraft.content.client.block;

import com.EvilNotch.lib.minecraft.content.blocks.IBasicBlock;
import com.EvilNotch.lib.minecraft.content.blocks.property.IPropertyName;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.util.IStringSerializable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class StateMapperSupreme extends StateMapperBase{

	public static final String normal = "normal";
	public static int index = -1;
	
	@Override
	public ModelResourceLocation getModelResourceLocation(IBlockState state) 
	{
		index++;
		Block block = state.getBlock();
		IBasicBlock b = (IBasicBlock)block;
		IProperty p = b.getStateProperty();
		
		ModelResourceLocation model = null;
		
		if(p instanceof PropertyInteger)
		{
			String i = state.getValue(p).toString();
			model = new ModelResourceLocation(block.getRegistryName() ,p.getName() + "=" + i);
		}
		else if(p instanceof PropertyBool)
		{
			String bool = state.getValue(p).toString();
			model = new ModelResourceLocation(block.getRegistryName() ,p.getName() + "=" + bool);
		}
		else if(p instanceof PropertyDirection)
		{
			String dir = state.getValue(p).toString();
			model = new ModelResourceLocation(block.getRegistryName() ,p.getName() + "=" + dir);
		}
		else if(p instanceof PropertyEnum)
		{
			IStringSerializable name = (IStringSerializable) state.getValue(p);
			model = new ModelResourceLocation(block.getRegistryName() ,p.getName() + "=" + name.getName());
		}
//		System.out.println("grabbing Model:" + block.getRegistryName() + " count:" + index + " model:" + model);
		return model;
	}

}
