package com.sigurd4.sigurdsEpicAdventureStuff.item;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public interface IItemTextureVariants
{
    public String[] getTextureVariants(int meta);
}
