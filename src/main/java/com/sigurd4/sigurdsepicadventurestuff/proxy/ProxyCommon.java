package com.sigurd4.sigurdsepicadventurestuff.proxy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.sigurd4.sigurdsepicadventurestuff.M;
import com.sigurd4.sigurdsepicadventurestuff.M.Id;
import com.sigurd4.sigurdsepicadventurestuff.References;
import com.sigurd4.sigurdsepicadventurestuff.event.HandlerCommon;
import com.sigurd4.sigurdsepicadventurestuff.event.HandlerCommonFML;
import com.sigurd4.sigurdsepicadventurestuff.packet.PacketKey;
import com.sigurd4.sigurdsepicadventurestuff.packet.PacketPlayerProps;

import net.minecraft.block.Block;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.oredict.OreDictionary;

public class ProxyCommon
{
	public void preInit()
	{
		MinecraftForge.EVENT_BUS.register(new HandlerCommon());
		FMLCommonHandler.instance().bus().register(new HandlerCommonFML());

		registerItems();
		oreDictionary();
		packets();
		entities();
		M.idsToBeRegistered.clear();
	}

	public void init()
	{
		recipes();
	}

	public void postInit()
	{

	}

	private void packets()
	{
		M.network = NetworkRegistry.INSTANCE.newSimpleChannel(References.MODID + "Packets");
		M.network.registerMessage(PacketKey.Handler.class, PacketKey.class, 0, Side.SERVER);
		M.network.registerMessage(PacketPlayerProps.Handler.class, PacketPlayerProps.class, 1, Side.CLIENT);
	}

	private void recipes()
	{
	}

	private void registerNugget(Item nugget, Item bar)
	{
		if(M.visible(nugget))
		{
			GameRegistry.addShapedRecipe(new ItemStack(bar, 1), new Object[]{"NNN", "NNN", "NNN", 'N', nugget});
			GameRegistry.addShapedRecipe(new ItemStack(nugget, 9), new Object[]{"B", 'B', bar});
		}
	}

	private void registerGear(Item gear, Item bar)
	{
		if(M.visible(gear))
		{
			GameRegistry.addShapedRecipe(new ItemStack(gear, 6), new Object[]{" B ", "BbB", " B ", 'B', bar, 'b', Item.getItemFromBlock(Blocks.stone_button)});
		}
	}

	private void oreDictionary()
	{
		Iterator<Id> ids = M.idsToBeRegistered.iterator();
		while(ids.hasNext())
		{
			Id id = ids.next();
			if(id != null)
			{
				Object item = M.getItem(id);
				for(int i = 0; i < id.oreDictNames.length; ++i)
				{
					if(item != null && item instanceof Block)
					{
						OreDictionary.registerOre(id.oreDictNames[i], (Block)item);
					}
					if(item != null && item instanceof Item)
					{
						OreDictionary.registerOre(id.oreDictNames[i], (Item)item);
					}
				}
			}
		}
	}

	private void entities()
	{
		/*M.registerEntityNoEgg(EntityFood.class, "foodProjectile", 0);
		M.registerEntityNoEgg(EntityShuriken.class, "shuriken", 1);
		M.registerEntityNoEgg(EntityLaser.class, "laser", 2);*/
	}

	private void registerItems()
	{
		Iterator<Id> ids = M.idsToBeRegistered.iterator();
		while(ids.hasNext())
		{
			Id id = ids.next();
			if(id != null)
			{
				Object item = M.getItem(id);
				if(item != null && item instanceof Block)
				{
					GameRegistry.registerBlock((Block)item, id.id);
				}
				if(item != null && item instanceof Item)
				{
					GameRegistry.registerItem((Item)item, id.id);
				}

				if(id.replacedIfAlreadyAnOreDict)
				{
					id.visible = false;
					for(int i2 = 0; i2 < id.oreDictNames.length; ++i2)
					{
						List<ItemStack> oreDicts = OreDictionary.getOres(id.oreDictNames[i2]);
						if(oreDicts != null)
						{
							for(int i3 = 0; i3 < oreDicts.size(); ++i3)
							{
								if(oreDicts.get(i3) != null && oreDicts.get(i3).getItem() == item)
								{
									oreDicts.remove(i3);
									--i3;
								}
							}
						}
						if(oreDicts == null || oreDicts.size() <= 0)
						{
							id.visible = true;
						}
					}
				}
				else
				{
					id.visible = true;
				}
			}
		}
	}
}
