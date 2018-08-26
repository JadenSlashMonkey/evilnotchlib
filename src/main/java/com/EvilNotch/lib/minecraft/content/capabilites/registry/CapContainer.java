package com.EvilNotch.lib.minecraft.content.capabilites.registry;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import com.EvilNotch.lib.minecraft.content.capabilites.ICapTick;
import com.EvilNotch.lib.minecraft.content.capabilites.ICapability;
import com.EvilNotch.lib.minecraft.content.capabilites.IListener;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class CapContainer<T extends ICapProvider> {
	
	public HashMap<ResourceLocation,ICapability> caps = new HashMap();
	public HashMap<ResourceLocation,ICapTick> ticks = new HashMap();
	public Set<IListener> listeners = new HashSet();
	
	public void registerCapability(ResourceLocation loc,ICapability cap)
	{
		if(cap instanceof ICapTick)
			ticks.put(loc,(ICapTick) cap);
		caps.put(loc, cap);
	}
	public void removeCapability(ResourceLocation loc)
	{
		caps.remove(loc);
		ticks.remove(loc);
	}
	public void tick(T object)
	{
		for(ICapTick cap : this.ticks.values())
			cap.tick(object,this);
	}
	public void writeToNBT(T object,NBTTagCompound nbt)
	{
		this.preSave(nbt, object);
		for(ICapability cap : this.caps.values())
		{
			cap.writeToNBT(object,nbt,this);
		}
		this.postSave(nbt, object);
	}
	public void readFromNBT(T object,NBTTagCompound nbt)
	{
		this.preRead(nbt, object);
		for(ICapability cap : this.caps.values())
			cap.readFromNBT(object,nbt,this);
		this.postRead(nbt, object);
	}
	
	
	public void preSave(NBTTagCompound nbt, T obj){
		for(IListener li : this.listeners)
			li.preSave(nbt,obj,this);
	}
	public void postSave(NBTTagCompound nbt, T obj){
		for(IListener li : this.listeners)
			li.postSave(nbt,obj,this);
	}
	
	public void preRead(NBTTagCompound nbt, T obj){
		for(IListener li : this.listeners)
			li.preRead(nbt,obj,this);
	}
	public void postRead(NBTTagCompound nbt, T obj){
		for(IListener li : this.listeners)
			li.postRead(nbt,obj,this);
	}

}