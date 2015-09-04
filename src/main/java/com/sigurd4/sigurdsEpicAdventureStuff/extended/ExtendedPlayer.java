package com.sigurd4.sigurdsEpicAdventureStuff.extended;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import baubles.api.BaubleType;

import com.google.common.collect.Lists;
import com.sigurd4.sigurdsEpicAdventureStuff.References;

public class ExtendedPlayer implements IExtendedEntityProperties, IEntityAdditionalSpawnData
{
	public final ItemStack[] previousBaubles = new ItemStack[BaubleType.values().length + 1];
	public boolean isRightClickHeldDown = false;

	public void setRightClick(boolean b)
	{
		this.isRightClickHeldDownLast = this.isRightClickHeldDown;
		this.isRightClickHeldDown = b;
	}

	public boolean isRightClickHeldDownLast = false;
	public ArrayList<Integer> mysteryPotionsDrunk = Lists.newArrayList();
	public int spin = 0;
	public int floating = 0;

	public final static String PROP = References.MODID;
	public final static String RIGHTCLICK = "RightClick";
	public final static String RIGHTCLICKLAST = "RightClickLast";
	public final static String KNOWNMYSTERYPOTIONS = "KnownMysteryPotions";

	private final EntityPlayer player;

	public ExtendedPlayer(EntityPlayer player)
	{
		this.player = player;
	}

	/**
	 * Returns ExtendedPlayer properties for player This method is for
	 * convenience only; it will make your code look nicer
	 */
	public static final ExtendedPlayer get(EntityPlayer player)
	{
		return (ExtendedPlayer)player.getExtendedProperties(ExtendedPlayer.PROP);
	}

	/**
	 * Used to register these extended properties for the player during
	 * EntityConstructing event This method is for convenience only; it makes my
	 * code look nicer.
	 */
	public static final void register(EntityPlayer player)
	{
		player.registerExtendedProperties(ExtendedPlayer.PROP, new ExtendedPlayer(player));
	}

	@Override
	public void loadNBTData(NBTTagCompound compound)
	{
		this.loadNBTData(compound, true);
	}

	public void loadNBTData(NBTTagCompound compound, boolean b)
	{
		NBTTagCompound properties = (NBTTagCompound)compound.getTag(ExtendedPlayer.PROP);
		if(properties == null)
		{
			return;
		}
		this.isRightClickHeldDown = properties.getBoolean(ExtendedPlayer.RIGHTCLICK);
		this.isRightClickHeldDownLast = properties.getBoolean(ExtendedPlayer.RIGHTCLICKLAST);
		//mysteryPotionsDrunk.clear();
		NBTTagList potions = properties.getTagList(ExtendedPlayer.KNOWNMYSTERYPOTIONS, new NBTTagInt(0).getId());
		if(potions != null)
		{
			for(int i = 0; i < potions.tagCount(); ++i)
			{
				NBTBase tag = potions.get(i);
				if(tag instanceof NBTTagInt)
				{
					this.mysteryPotionsDrunk.add(((NBTTagInt)tag).getInt());
				}
			}
		}

		if(b)
		{
			b = true;
		}
	}

	@Override
	public void saveNBTData(NBTTagCompound compound)
	{
		this.saveNBTData(compound, true);
	}

	public void saveNBTData(NBTTagCompound compound, boolean b)
	{
		NBTTagCompound properties = new NBTTagCompound();
		compound.setTag(ExtendedPlayer.PROP, properties);

		properties.setBoolean(ExtendedPlayer.RIGHTCLICK, this.isRightClickHeldDown);
		properties.setBoolean(ExtendedPlayer.RIGHTCLICKLAST, this.isRightClickHeldDownLast);
		NBTTagList potions = new NBTTagList();
		for(int i = 0; i < this.mysteryPotionsDrunk.size(); ++i)
		{
			NBTTagInt tag = new NBTTagInt(this.mysteryPotionsDrunk.get(i));
			potions.appendTag(tag);
		}
		properties.setTag(ExtendedPlayer.KNOWNMYSTERYPOTIONS, potions);

		if(b)
		{

		}
	}

	public void update()
	{

	}

	public boolean knowsPotion(int meta)
	{
		return this.mysteryPotionsDrunk.contains(meta);
	}

	public void drinkPotion(int meta)
	{
		if(!this.mysteryPotionsDrunk.contains(meta))
		{
			this.mysteryPotionsDrunk.add(meta);
		}
	}

	@Override
	public void init(Entity entity, World world)
	{
	}

	/**
	 * Called by the server when constructing the spawn packet. Data should be
	 * added to the provided stream.
	 *
	 * @param buffer
	 *            The packet data stream
	 */
	@Override
	public void writeSpawnData(ByteBuf buf)
	{
		NBTTagCompound compound = new NBTTagCompound();
		this.saveNBTData(compound);
		ByteBufUtils.writeTag(buf, compound);
	}

	/**
	 * Called by the client when it receives a Entity spawn packet. Data should
	 * be read out of the stream in the same way as it was written.
	 *
	 * @param data
	 *            The packet data stream
	 */
	@Override
	public void readSpawnData(ByteBuf buf)
	{
		NBTTagCompound compound = ByteBufUtils.readTag(buf);
		if(compound != null)
		{
			this.loadNBTData(compound);
		}
	}
}