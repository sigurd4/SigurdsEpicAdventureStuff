package com.sigurd4.sigurdsepicadventurestuff.event;

import java.util.HashMap;
import java.util.List;

import com.sigurd4.sigurdsepicadventurestuff.M;
import com.sigurd4.sigurdsepicadventurestuff.extended.EntityExtendedPlayer;
import com.sigurd4.sigurdsepicadventurestuff.packet.PacketPlayerProps;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityRabbit;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class HandlerCommon extends HandlerBase
{
	//minecraftforge events for both sides here!
	
	public static HashMap<EntityPlayer, NBTTagCompound> playerDeathData = new HashMap<EntityPlayer, NBTTagCompound>();

	@SubscribeEvent
	public void onEntityConstructing(EntityConstructing event)
	{
		if(event.entity instanceof EntityPlayer && EntityExtendedPlayer.get((EntityPlayer) event.entity) == null)
		{
			EntityExtendedPlayer.register((EntityPlayer)event.entity);
		}

		if (event.entity instanceof EntityPlayer && event.entity.getExtendedProperties(EntityExtendedPlayer.PROP) == null)
		{
			event.entity.registerExtendedProperties(EntityExtendedPlayer.PROP, new EntityExtendedPlayer((EntityPlayer) event.entity));
		}
	}

	@SubscribeEvent
	public void livingUpdateEvent(LivingUpdateEvent event)
	{
		if(event.entity instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer)event.entity;
			EntityExtendedPlayer props = EntityExtendedPlayer.get((EntityPlayer)event.entity);
			props.update();

			if(!event.entity.worldObj.isRemote)
			{
				/**makes it so that players keep certain data upon death**/
				if(playerDeathData.get(player) != null && player.getHealth() > 0)
				{
					props.loadNBTData(playerDeathData.get(player), false);
					playerDeathData.remove(player);
				}
				if(playerDeathData.get(player) == null && player.getHealth() <= 0 || player.deathTime > 0)
				{
					NBTTagCompound playerData = new NBTTagCompound();
					props.saveNBTData(playerData, false);
					playerDeathData.put(player, playerData);
				}

				/**give data to client**/
				if(player instanceof EntityPlayerMP)
				{
					NBTTagCompound compound = new NBTTagCompound();
					M.network.sendTo(new PacketPlayerProps(props), (EntityPlayerMP)player);
				}
			}
		}
	}
}
