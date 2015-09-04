package com.sigurd4.sigurdsEpicAdventureStuff.itemtags;

import java.util.UUID;

import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;

public class ItemTagUUID extends ItemTagBase<UUID, NBTTagList>
{
	public ItemTagUUID(String key, UUID uuid, boolean noWobble)
	{
		super(key, uuid, noWobble);
	}

	public ItemTagUUID(String key, boolean noWobble)
	{
		this(key, new UUID(0, 0), noWobble);
	}

	@Override
	protected NBTTagList rawToNBTTag(UUID value)
	{
		NBTTagList list = new NBTTagList();
		list.appendTag(new NBTTagLong(value.getMostSignificantBits()));
		list.appendTag(new NBTTagLong(value.getLeastSignificantBits()));
		return list;
	}

	@Override
	protected UUID NBTTagToRaw(NBTTagList value)
	{
		if(value.tagCount() >= 2 && value.getTagType() == new NBTTagLong(0).getId())
		{
			return new UUID(((NBTTagLong)value.get(0)).getLong(), ((NBTTagLong)value.get(1)).getLong());
		}
		return this.getDefault();
	}
}