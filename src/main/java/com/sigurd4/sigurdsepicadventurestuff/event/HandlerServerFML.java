package com.sigurd4.sigurdsepicadventurestuff.event;

import java.util.HashMap;

import com.sigurd4.sigurdsepicadventurestuff.M;
import com.sigurd4.sigurdsepicadventurestuff.extended.EntityExtendedPlayer;
import com.sigurd4.sigurdsepicadventurestuff.packet.PacketPlayerProps;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.SERVER)
public class HandlerServerFML extends HandlerBase
{
	//fml events for server only here!

	@SubscribeEvent
	public void playerUpdateEvent2(PlayerTickEvent event)
	{
		
	}
}
