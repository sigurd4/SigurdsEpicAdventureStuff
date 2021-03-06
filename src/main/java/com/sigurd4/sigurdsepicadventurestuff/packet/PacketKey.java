package com.sigurd4.sigurdsEpicAdventureStuff.packet;

import com.sigurd4.sigurdsEpicAdventureStuff.extended.ExtendedPlayer;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketKey implements IMessage
{
	public static enum Key
	{
		RIGHTCLICK(0),
		NOTRIGHTCLICK(1);

		public final int id;

		private Key(int id)
		{
			this.id = id;
		}

		public static Key get(int id)
		{
			for(int i = 0; i < Key.values().length; ++i)
			{
				if(Key.values()[i].id == id)
				{
					return Key.values()[i];
				}
			}
			return null;
		}
	}

	private int i;

	public PacketKey() {}

	public PacketKey(Key k)
	{
		this.i = k.id;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		i = ByteBufUtils.readVarInt(buf, 3);
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		ByteBufUtils.writeVarInt(buf, i, 3);
	}

	public static class Handler implements IMessageHandler<PacketKey, IMessage>
	{
		public Handler(){}

		@Override
		public IMessage onMessage(PacketKey message, MessageContext context)
		{
			EntityPlayer player = context.getServerHandler().playerEntity;
			return onMessage(message, player);
		}

		public static IMessage onMessage(PacketKey message, EntityPlayer player)
		{
			if(player == null)
			{
				return null;
			}
			ExtendedPlayer props = ExtendedPlayer.get(player);
			Key k = Key.get(message.i);
			switch(k)
			{
			case RIGHTCLICK:
			{
				if(props != null)
				{
					props.setRightClick(true);
				}
				break;
			}
			case NOTRIGHTCLICK:
			{
				if(props != null)
				{
					props.setRightClick(false);
				}
				break;
			}
			}
			return null;
		}
	}
}
