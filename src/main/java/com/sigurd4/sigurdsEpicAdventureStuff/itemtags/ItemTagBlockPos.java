package com.sigurd4.sigurdsEpicAdventureStuff.itemtags;

import net.minecraft.nbt.NBTTagInt;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

import com.sigurd4.sigurdsEpicAdventureStuff.Stuff;

public class ItemTagBlockPos extends ItemTagVec<BlockPos, NBTTagInt>
{
	public ItemTagBlockPos(String key, BlockPos defaultValue, BlockPos min, BlockPos max, boolean noWobble)
	{
		super(key, defaultValue, min, max, noWobble);
	}
	
	@Override
	protected Vec3 toVec(BlockPos value)
	{
		return Stuff.Coordinates3D.middle(value);
	}
	
	@Override
	protected BlockPos fromVec(Vec3 vec)
	{
		return new BlockPos(vec);
	}
	
	@Override
	protected NBTTagInt toTag(double value)
	{
		return new NBTTagInt(MathHelper.floor_double(value));
	}
	
	@Override
	protected double fromTag(NBTTagInt tag)
	{
		return tag.getInt();
	}
}