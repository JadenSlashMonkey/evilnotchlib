package com.EvilNotch.lib.minecraft.content.capabilites.primitive;

import com.EvilNotch.lib.minecraft.content.capabilites.ICapability;
import com.EvilNotch.lib.minecraft.content.capabilites.registry.CapContainer;

import net.minecraft.nbt.NBTTagCompound;

public class CapString<T> extends CapBase<T>{
	
	public String str = "";
	public CapString(String key)
	{
		super(key);
	}

	@Override
	public void writeToNBT(T object, NBTTagCompound nbt, CapContainer c) {
		nbt.setString(this.key, this.str);
	}

	@Override
	public void readFromNBT(T object, NBTTagCompound nbt, CapContainer c) 
	{
		this.str = nbt.getString(this.key);
	}
	@Override
	public String toString(){return this.key + "=" + this.str;}

}