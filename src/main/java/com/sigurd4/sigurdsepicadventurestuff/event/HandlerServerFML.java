package com.sigurd4.sigurdsEpicAdventureStuff.event;

import java.util.HashMap;

import com.sigurd4.sigurdsEpicAdventureStuff.Config;
import com.sigurd4.sigurdsEpicAdventureStuff.M;
import com.sigurd4.sigurdsEpicAdventureStuff.extended.ExtendedPlayer;
import com.sigurd4.sigurdsEpicAdventureStuff.packet.PacketConfig;
import com.sigurd4.sigurdsEpicAdventureStuff.packet.PacketPlayerProps;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.SERVER)
public class HandlerServerFML
{
	//fml events for server only here!

	@SubscribeEvent
	public void playerUpdateEvent(PlayerTickEvent event)
	{
		if(event.player instanceof EntityPlayerMP)
		{
			for(int i = 0; i < Config.entries.size(); ++i)
			{
				if(Config.entries.get(i) != null && Config.entries.get(i).side == Side.SERVER)
				{
					M.network.sendTo(new PacketConfig(i), (EntityPlayerMP)event.player);
				}
			}
		}
	}
}
