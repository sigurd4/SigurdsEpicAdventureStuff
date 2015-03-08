package com.sigurd4.sigurdsEpicAdventureStuff.tabs;

import java.util.ArrayList;
import java.util.Iterator;

import com.google.common.collect.Lists;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
		long time = MinecraftServer.getServer().worldServers[0].getTotalWorldTime();
		ArrayList<Item> items = getItems();
		if(items.size() > 0)
		{
			return items.get((int)(time % items.size()));
		}
		else
		{
			return Item.getItemFromBlock(Blocks.stone);
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public ItemStack getIconItemStack()
	{
		long time = MinecraftServer.getServer().worldServers[0].getTotalWorldTime();
		ArrayList<Item> items = getItems();
		ArrayList<ItemStack> itemstacks = Lists.newArrayList();
		for(int i = 0; i < items.size(); ++i)
		{
			ArrayList<ItemStack> variants = Lists.newArrayList();
			items.get(i).getSubItems(items.get(i), this, variants);
			itemstacks.addAll(variants);
		}
		if(itemstacks.size() > 0)
		{
			return itemstacks.get((int)(time % itemstacks.size()));
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
            if (item == null)
            {
                continue;
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