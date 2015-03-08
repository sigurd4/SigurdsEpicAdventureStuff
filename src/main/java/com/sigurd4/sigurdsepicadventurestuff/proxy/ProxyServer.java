package com.sigurd4.sigurdsepicadventurestuff.proxy;

import com.sigurd4.sigurdsepicadventurestuff.event.HandlerServer;
import com.sigurd4.sigurdsepicadventurestuff.event.HandlerServerFML;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderEntity;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class ProxyServer extends ProxyCommon
{
	@Override
	public void preInit()
	{
		MinecraftForge.EVENT_BUS.register(new HandlerServer());
		FMLCommonHandler.instance().bus().register(new HandlerServerFML());
		
		super.preInit();
	}
	
	@Override
	public void init()
	{
		super.init();
	}
}
