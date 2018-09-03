package com.EvilNotch.lib.minecraft.content.capabilites.primitive;

import com.EvilNotch.lib.minecraft.content.capabilites.registry.CapContainer;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class CapBoolean<T> extends CapBase<T>{
	
	public boolean value = false;
	public CapBoolean(String key)
	{
		super(key);
	}

	@Override
	public void writeToNBT(T object, NBTTagCompound nbt, CapContainer c) 
	{
		nbt.setBoolean(this.key, this.value);
	}

	@Override
	public void readFromNBT(T object, NBTTagCompound nbt, CapContainer c) 
	{
		this.value = nbt.getBoolean(this.key);
		World w = ((TileEntity)object).getWorld();
		System.out.println(object.getClass() + " " + this.value);
	}

}