package com.sigurd4.sigurdsEpicAdventureStuff.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import baubles.api.BaublesApi;

public class PacketBaublesEquipment implements IMessage
{
	private int entityId;
	private int slot;
	private ItemStack stack;
	
	public PacketBaublesEquipment()
	{
	}
	
	public PacketBaublesEquipment(int entityId, int slot, ItemStack stack)
	{
		this.entityId = entityId;
		this.slot = slot;
		this.stack = stack;
	}
	
	@Override
	public void fromBytes(ByteBuf buf)
	{
		this.entityId = buf.readInt();
		this.slot = buf.readInt();
		this.stack = ByteBufUtils.readItemStack(buf);
	}
	
	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(this.entityId);
		buf.writeInt(this.slot);
		ByteBufUtils.writeItemStack(buf, this.stack);
	}
	
	public static class Handler implements IMessageHandler<PacketBaublesEquipment, IMessage>
	{
		public Handler()
		{
		}
		
		@Override
		public IMessage onMessage(PacketBaublesEquipment message, MessageContext context)
		{
			if(context.side == Side.CLIENT)
			{
				return Handler.onMessage(message);
			}
			return null;
		}
		
		@SideOnly(Side.CLIENT)
		public static IMessage onMessage(PacketBaublesEquipment message)
		{
			Entity entity = Minecraft.getMinecraft().theWorld.getEntityByID(message.entityId);
			if(entity instanceof EntityPlayer)
			{
				IInventory baubles = BaublesApi.getBaubles((EntityPlayer)entity);
				if(message.slot >= 0 && message.slot < baubles.getSizeInventory())
				{
					baubles.setInventorySlotContents(message.slot, message.stack);
				}
			}
			return null;
		}
	}
}
