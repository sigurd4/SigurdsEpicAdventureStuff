package com.sigurd4.sigurdsEpicAdventureStuff;

import java.util.HashMap;

import com.google.common.collect.HashMultimap;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.world.World;

public abstract class ItemTagBase<W, T extends NBTBase>
{
	private HashMap<NBTTagCompound, NBTTagCompound> localMap = new HashMap();
	protected T defaultValue;
	public final String key;
	public final boolean local;

	public ItemTagBase(String key, W defaultValue, boolean local)
	{
		this.key = key;
		this.local = local;
	}

	public W get(ItemStack stack)
	{
		return get(stack, true);
	}

	public W get(ItemStack stack, boolean createNew)
	{
		NBTTagCompound compound = getCompound(stack, createNew);
		return get(compound, createNew);
	}

	public W get(NBTTagCompound compound, boolean createNew)
	{
		if(createNew)
		{
			set(compound, get(compound, false));
		}
		if(compound.hasKey(key) && defaultValue.getClass().isInstance(compound.getTag(key)) && compound.getTag(key).getId() == defaultValue.getId())
		{
			return NBTTagToRaw(isValid((T)compound.getTag(key)));
		}
		else
		{
			return NBTTagToRaw(isValid((T)defaultValue.copy()));
		}
	}

	public void set(ItemStack stack, W value)
	{
		NBTTagCompound compound = getCompound(stack, true);
		set(compound, value);
	}

	public void set(NBTTagCompound compound, W value)
	{
		T tag = rawToNBTTag(value);
		compound.setTag(key, tag);
	}

	protected abstract T rawToNBTTag(W value);

	protected abstract W NBTTagToRaw(T value);

	protected T isValid(T original)
	{
		return rawToNBTTag(isValid(NBTTagToRaw(original)));
	}

	protected W isValid(W original)
	{
		return original;
	}

	public String toString()
	{
		return key;
	}

	protected final NBTTagCompound getCompound(ItemStack stack, boolean createNew)
	{
		NBTTagCompound compound = stack.getTagCompound();
		if(local)
		{
			if(!localMap.containsKey(compound))
			{
				if(compound == null)
				{
					stack.setTagCompound(new NBTTagCompound());
					compound = stack.getTagCompound();
				}
				localMap.put(compound, new NBTTagCompound());
			}
			compound = localMap.get(compound);
		}
		else
		{
			if(compound == null)
			{
				compound = new NBTTagCompound();
				if(createNew)
				{
					stack.setTagCompound(compound);
				}
			}
		}
		return compound;
	}

	public static abstract class ItemTagComparable<W extends Comparable, T extends NBTBase> extends ItemTagBase<W, T>
	{
		public final W min;
		public final W max;

		public ItemTagComparable(String key, W defaultValue, W min, W max, boolean local)
		{
			super(key, defaultValue, local);
			this.min = min;
			this.max = max;
			this.defaultValue = rawToNBTTag(isValid(NBTTagToRaw(this.defaultValue)));
		}

		protected W isValid(W original)
		{
			return original;
		}

		public void add(ItemStack stack, W amount)
		{
			NBTTagCompound compound = getCompound(stack, true);
			add(compound, amount);
		}

		public void add(NBTTagCompound compound, W amount)
		{
			W value = get(compound, true);
			set(compound, add(value, amount));
		}

		protected abstract W add(W value1, W value2);
	}

	public static class ItemTagInteger extends ItemTagComparable<Integer, NBTTagInt>
	{
		public ItemTagInteger(String key, Integer defaultValue, Integer min, Integer max, boolean local)
		{
			super(key, defaultValue, min, max, local);
		}

		protected Integer isValid(Integer original)
		{
			if(original > max)
			{
				return max;
			}
			if(original < min)
			{
				return min;
			}
			return original;
		}

		@Override
		protected NBTTagInt rawToNBTTag(Integer value)
		{
			return new NBTTagInt(value);
		}

		@Override
		protected Integer NBTTagToRaw(NBTTagInt value)
		{
			if(value != null)
			{
				return value.getInt();
			}
			return 0;
		}

		protected Integer add(Integer value1, Integer value2)
		{
			return value1 + value2;
		}
	}
}
