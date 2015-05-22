package com.sigurd4.sigurdsEpicAdventureStuff.packet;

import com.sigurd4.sigurdsEpicAdventureStuff.Config;
import com.sigurd4.sigurdsEpicAdventureStuff.extended.ExtendedPlayer;

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

public class PacketConfig implements IMessage
{
	private int configOption;

	public PacketConfig() {}

	public PacketConfig(int configOption)
	{
		this.configOption = configOption;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		try
		{
			Config.entries.get(configOption).fromBytes(buf);
		}
		catch(Exception e)
		{
			//e.printStackTrace();
		}
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		Config.entries.get(configOption).toBytes(buf);
	}

	public static class Handler implements IMessageHandler<PacketConfig, IMessage>
	{
		public Handler(){}

		@Override
		public IMessage onMessage(PacketConfig message, MessageContext context)
		{
			return null;
		}
	}
}
