package com.sigurd4.sigurdsEpicAdventureStuff.event;

import com.sigurd4.sigurdsEpicAdventureStuff.Config;
import com.sigurd4.sigurdsEpicAdventureStuff.References;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;

public class HandlerCommonFML
{
	//fml events for both sides here!

	@SubscribeEvent
	public void playerUpdateEvent(PlayerTickEvent event)
	{

	}

	@SubscribeEvent
	public void OnConfigChangedEvent(ConfigChangedEvent.OnConfigChangedEvent event)
	{
		if(event.modID.equals(References.MODID))
		{
			for(int i = 0; i < Config.entries.size(); ++i)
			{
				Config.entries.get(i).set(Config.config);
			}
			if(Config.config.hasChanged())
			{
				Config.config.save();
			}
		}
	}
}
