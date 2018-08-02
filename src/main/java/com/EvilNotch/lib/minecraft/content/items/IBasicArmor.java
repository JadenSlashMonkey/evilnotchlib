package com.EvilNotch.lib.minecraft.content.items;

import com.EvilNotch.lib.minecraft.content.ArmorSet;

import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;

public interface IBasicArmor {
	
	public ArmorSet getArmorSet();
	public void setArmorSet(ArmorSet set);
	public boolean hasFullArmorSet(ItemStack boots,ItemStack leggings,ItemStack chestplate,ItemStack helmet);
	default public boolean hasPotionEffect(){
		return getPotionEffect() != null;
	}
	public PotionEffect getPotionEffect();
}