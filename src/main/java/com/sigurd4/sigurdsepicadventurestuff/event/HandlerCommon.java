package com.sigurd4.sigurdsEpicAdventureStuff.event;

import java.util.HashMap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import baubles.api.BaublesApi;

import com.sigurd4.sigurdsEpicAdventureStuff.M;
import com.sigurd4.sigurdsEpicAdventureStuff.extended.ExtendedPlayer;
import com.sigurd4.sigurdsEpicAdventureStuff.packet.PacketBaublesEquipment;
import com.sigurd4.sigurdsEpicAdventureStuff.packet.PacketPlayerProps;

public class HandlerCommon
{
	//minecraftforge events for both sides here!
	public static HashMap<EntityPlayer, NBTTagCompound> playerDeathData = new HashMap<EntityPlayer, NBTTagCompound>();

	@SubscribeEvent
	public void onEntityConstructing(EntityConstructing event)
	{
		if(event.entity instanceof EntityPlayer && ExtendedPlayer.get((EntityPlayer)event.entity) == null)
		{
			ExtendedPlayer.register((EntityPlayer)event.entity);
		}

		if(event.entity instanceof EntityPlayer && event.entity.getExtendedProperties(ExtendedPlayer.PROP) == null)
		{
			event.entity.registerExtendedProperties(ExtendedPlayer.PROP, new ExtendedPlayer((EntityPlayer)event.entity));
		}
	}

	@SubscribeEvent
	public void livingUpdateEvent(LivingUpdateEvent event)
	{
		if(event.entity instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer)event.entity;
			ExtendedPlayer props = ExtendedPlayer.get((EntityPlayer)event.entity);
			props.update();

			if(!event.entity.worldObj.isRemote)
			{
				/** makes it so that players keep certain data upon death **/
				if(HandlerCommon.playerDeathData.get(player) != null && player.getHealth() > 0)
				{
					props.loadNBTData(HandlerCommon.playerDeathData.get(player), false);
					HandlerCommon.playerDeathData.remove(player);
				}
				if(HandlerCommon.playerDeathData.get(player) == null && player.getHealth() <= 0 || player.deathTime > 0)
				{
					NBTTagCompound playerData = new NBTTagCompound();
					props.saveNBTData(playerData, false);
					HandlerCommon.playerDeathData.put(player, playerData);
				}

				/** give data to client **/
				if(player instanceof EntityPlayerMP)
				{
					NBTTagCompound compound = new NBTTagCompound();
					M.network.sendTo(new PacketPlayerProps(props), (EntityPlayerMP)player);
				}
			}
			
			IInventory baublesInv = BaublesApi.getBaubles(player);
			for(int i = 0; i < baublesInv.getSizeInventory(); ++i)
			{
				ItemStack itemstack = props.previousBaubles[i];
				ItemStack itemstack1 = baublesInv.getStackInSlot(i);

				if(!ItemStack.areItemStacksEqual(itemstack1, itemstack))
				{
					if(player instanceof EntityPlayerMP)
					{
						M.network.sendTo(new PacketBaublesEquipment(player.getEntityId(), i, itemstack), (EntityPlayerMP)player);
					}

					if(itemstack != null && M.hasItem(itemstack.getItem()))
					{
						event.entityLiving.getAttributeMap().removeAttributeModifiers(itemstack.getAttributeModifiers());
					}

					if(itemstack1 != null && M.hasItem(itemstack1.getItem()))
					{
						event.entityLiving.getAttributeMap().applyAttributeModifiers(itemstack1.getAttributeModifiers());
					}

					props.previousBaubles[i] = itemstack1 == null ? null : itemstack1.copy();
				}
			}
		}
	}
}
