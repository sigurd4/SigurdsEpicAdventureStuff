package com.sigurd4.sigurdsEpicAdventureStuff.tabs;

import java.util.ArrayList;
import java.util.Iterator;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.google.common.collect.Lists;
import com.sigurd4.sigurdsEpicAdventureStuff.M;
import com.sigurd4.sigurdsEpicAdventureStuff.M.Id;
import com.sigurd4.sigurdsEpicAdventureStuff.Stuff;

public class TabGeneric extends CreativeTabs
{
	public TabGeneric(String tabLabel)
	{
		super("sigurdsEpicAdventureStuff." + tabLabel);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Item getTabIconItem()
	{
		World world = M.proxy.world(0);
		long time = world.getTotalWorldTime();
		ArrayList<Item> items = this.getItems();
		if(items.size() > 0)
		{
			return items.get((int)(time % items.size()));
		}
		else
		{
			return Item.getItemFromBlock(Blocks.stone);
		}
	}

	private ArrayList<ItemStack> iconItemstacks = null;
	private int timer = 0;
	private final int frequency = 50;
	
	@Override
	@SideOnly(Side.CLIENT)
	public ItemStack getIconItemStack()
	{
		++this.timer;
		int timer2 = (int)((float)this.timer / this.frequency);
		if(this.iconItemstacks == null)
		{
			World world = M.proxy.world(0);
			long time = world.getTotalWorldTime();
			ArrayList<Item> items = this.getItems();
			this.iconItemstacks = Lists.newArrayList();
			for(int i = 0; i < items.size(); ++i)
			{
				ArrayList<ItemStack> variants = Lists.newArrayList();
				items.get(i).getSubItems(items.get(i), this, variants);
				//itemstacks.addAll(variants);
				if(variants.size() > 0)
				{
					this.iconItemstacks.add(variants.get(Stuff.Randomization.randSeed(world.getSeed(), world.getTotalWorldTime() / 10).nextInt(variants.size())));
				}
			}
		}
		ArrayList<ItemStack> itemstacks = this.iconItemstacks;
		while(itemstacks.size() > 0 && timer2 >= itemstacks.size())
		{
			this.iconItemstacks = null;
			this.timer -= itemstacks.size() * this.frequency;
			timer2 -= itemstacks.size();
		}
		if(itemstacks.size() > 0)
		{
			return itemstacks.get(timer2);
		}
		else
		{
			return new ItemStack(Item.getItemFromBlock(Blocks.stone));
		}
	}

	public ArrayList<Item> getItems()
	{
		Iterator iterator = Item.itemRegistry.iterator();
		ArrayList<Item> items = Lists.newArrayList();
		while(iterator.hasNext())
		{
			Item item = (Item)iterator.next();
			if(item == null)
			{
				continue;
			}
			if(M.hasItem(item))
			{
				Id id = M.getId(item);
				if(!id.visible)
				{
					continue;
				}
			}
			boolean contains = false;
			CreativeTabs[] itemTabs = item.getCreativeTabs();
			for(int i = 0; i < itemTabs.length; ++i)
			{
				if(this == itemTabs[i])
				{
					items.add(item);
				}
			}
		}
		return items;
	}
}