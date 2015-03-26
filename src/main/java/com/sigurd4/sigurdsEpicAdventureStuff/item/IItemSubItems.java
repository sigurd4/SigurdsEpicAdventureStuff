package com.sigurd4.sigurdsEpicAdventureStuff.item;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public interface IItemSubItems
{
    public void getSubItems2(Item itemIn, CreativeTabs tab, ArrayList<ItemStack> subItems);
}
