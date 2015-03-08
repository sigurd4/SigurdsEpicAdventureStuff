package com.sigurd4.sigurdsEpicAdventureStuff.proxy;

import java.util.Iterator;

import com.sigurd4.sigurdsEpicAdventureStuff.M;
import com.sigurd4.sigurdsEpicAdventureStuff.M.Id;
import com.sigurd4.sigurdsEpicAdventureStuff.event.HandlerClient;
import com.sigurd4.sigurdsEpicAdventureStuff.event.HandlerClientFML;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderEntity;
import net.minecraft.client.renderer.entity.RenderFireball;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ProxyClient extends ProxyCommon
{
	@Override
	public void preInit(FMLPreInitializationEvent event)
	{
		MinecraftForge.EVENT_BUS.register(new HandlerClient());
		FMLCommonHandler.instance().bus().register(new HandlerClientFML());
		HandlerClientFML.init();

		super.preInit(event);
	}

	@Override
	public void init(FMLInitializationEvent event)
	{
		super.init(event);

		RenderItem ri = Minecraft.getMinecraft().getRenderItem();
		RenderManager rm = Minecraft.getMinecraft().getRenderManager();
		registerItemModels(ri);
		entityRender(rm, ri);
	}

	private void entityRender(RenderManager rm, RenderItem ri)
	{
		//RenderingRegistry.registerEntityRenderingHandler(EntitySmallFireball2.class, new RenderFireball(rm, 1.3F));
	}

	private void registerItemModels(RenderItem ri)
	{
		Iterator<Id> ids = M.getIds();
		while(ids.hasNext())
		{
			Id id = ids.next();
			if(id != null)
			{
				Object item = M.getItem(id);
				if(item != null && item instanceof Block)
				{
					ri.getItemModelMesher().register(Item.getItemFromBlock((Block)item), 0, new ModelResourceLocation(id.mod.toLowerCase() + ":" + id.id.toLowerCase(), "inventory"));
				}
				else if(item != null && item instanceof Item)
				{
					String[] s = M.getTypes((Item)item);
					for(int i = 0; i <= ((Item)item).getMaxDamage(); ++i)
					{
						int i2 = i;
						while(i2 >= s.length)
						{
							i2 -= s.length;
						}
						ri.getItemModelMesher().register((Item)item, i, new ModelResourceLocation(s[i2], "inventory"));
					}
				}
			}
		}
	}
}