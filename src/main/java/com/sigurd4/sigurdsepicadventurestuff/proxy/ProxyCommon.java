package com.sigurd4.sigurdsEpicAdventureStuff.proxy;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.sigurd4.sigurdsEpicAdventureStuff.Config;
import com.sigurd4.sigurdsEpicAdventureStuff.M;
import com.sigurd4.sigurdsEpicAdventureStuff.References;
import com.sigurd4.sigurdsEpicAdventureStuff.M.Id;
import com.sigurd4.sigurdsEpicAdventureStuff.Stuff;
import com.sigurd4.sigurdsEpicAdventureStuff.event.HandlerCommon;
import com.sigurd4.sigurdsEpicAdventureStuff.event.HandlerCommonFML;
import com.sigurd4.sigurdsEpicAdventureStuff.packet.PacketKey;
import com.sigurd4.sigurdsEpicAdventureStuff.packet.PacketPlayerProps;

import net.minecraft.block.Block;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionHelper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.oredict.OreDictionary;

public class ProxyCommon
{
	public void preInit(FMLPreInitializationEvent event)
	{
		MinecraftForge.EVENT_BUS.register(new HandlerCommon());
		FMLCommonHandler.instance().bus().register(new HandlerCommonFML());

		registerItems();
		oreDictionary();
		packets();
		entities();
		M.idsToBeRegistered.clear();
		registerConfig(event.getSuggestedConfigurationFile());
	}

	public void init(FMLInitializationEvent event)
	{
		recipes();
		dungeonLoot();
	}

	public void postInit(FMLPostInitializationEvent event)
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

	private void registerConfig(File file)
	{
		if(Config.config == null)
		{
			Config.config = new Configuration(file);
		}
	}

	private void dungeonLoot()
	{
		Iterator<Id> ids = M.getIds();
		while(ids.hasNext())
		{
			Id id = ids.next();
			if(id != null)
			{
				if(M.getItem(id) instanceof Item)
				{
					Item item = (Item)M.getItem(id);

					HashMap<String, ChestGenHooks> categories = null;
					try
					{
						Field field = ChestGenHooks.class.getDeclaredField("chestInfo");
						field.setAccessible(true);
						if(field != null)
						{
							categories = (HashMap<String, ChestGenHooks>)field.get(null);
						}
					}
					catch(Throwable e)
					{
						e.printStackTrace();
					}

					if(categories != null)
					{
						String[] categoriesS = categories.keySet().toArray(new String[categories.keySet().size()]);
						for(int i = 0; i < categoriesS.length; ++i)
						{
							ArrayList<WeightedRandomChestContent> loot = Stuff.ItemStuff.getChestGens(item, ChestGenHooks.getInfo(categoriesS[i]), Stuff.rand);
							for(int i2 = 0; i2 < loot.size(); ++i2)
							{
								ChestGenHooks.addItem(categoriesS[i], loot.get(i2));
							}
						}
					}
				}
			}
		}
	}
}
