package com.EvilNotch.lib.minecraft.network.packets.handlers;

import com.EvilNotch.lib.minecraft.network.MessegeBase;
import com.EvilNotch.lib.minecraft.network.packets.PacketYawOffset;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

public class PacketYawOffsetHandler extends MessegeBase<PacketYawOffset>{

	@Override
	public void handleClientSide(PacketYawOffset message, EntityPlayer p) 
	{
		Minecraft.getMinecraft().addScheduledTask(() -> 
		{
			Entity e = Minecraft.getMinecraft().world.getEntityByID(message.id);
			if(!(e instanceof EntityPlayer))
			{
				System.out.println("invalid packet recieved for player:" + message.id);
				return;
			}
			EntityPlayer player = (EntityPlayer)e;
			player.renderYawOffset = message.yawOffsetRender;
			player.prevRenderYawOffset = message.yawOffsetRender;
		});
	}

	@Override
	public void handleServerSide(PacketYawOffset message, EntityPlayer player) {}

}