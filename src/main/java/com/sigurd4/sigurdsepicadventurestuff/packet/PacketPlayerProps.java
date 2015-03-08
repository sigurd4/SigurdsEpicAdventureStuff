package com.sigurd4.sigurdsepicadventurestuff.packet;

import com.sigurd4.sigurdsepicadventurestuff.extended.EntityExtendedPlayer;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.IExtendedEntityProperties;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketPlayerProps implements IMessage
{
	private EntityExtendedPlayer props;
	private ByteBuf buf;

	public PacketPlayerProps() {}

	public PacketPlayerProps(EntityExtendedPlayer props)
	{
		this.props = props;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		props = null;
		this.buf = buf;
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		if(props != null)
		{
			props.writeSpawnData(buf);
			this.buf = buf;
		}
	}

	public static class Handler implements IMessageHandler<PacketPlayerProps, IMessage>
	{
		public Handler(){}

		@Override
		public IMessage onMessage(PacketPlayerProps message, MessageContext context)
		{
			if(context.side == Side.CLIENT)
				return onMessage(message);
			return null;
		}


		@SideOnly(Side.CLIENT)
		public static IMessage onMessage(PacketPlayerProps message)
		{
			EntityPlayer player = Minecraft.getMinecraft().thePlayer;
			EntityExtendedPlayer props = EntityExtendedPlayer.get(player);
			if(props != null && message.buf != null)
			{
				props.readSpawnData(message.buf);
			}
			return null;
		}
	}
}
