package com.sigurd4.sigurdsepicadventurestuff.tabs;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class TabGeneric extends CreativeTabs
{
	public TabGeneric(String tabLabel)
	{
		super("sigurdsEpicAdventureStuff." + tabLabel);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public abstract Item getTabIconItem();
}