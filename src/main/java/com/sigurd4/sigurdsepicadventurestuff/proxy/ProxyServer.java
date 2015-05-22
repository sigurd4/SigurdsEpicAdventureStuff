package com.sigurd4.sigurdsEpicAdventureStuff.proxy;

import com.sigurd4.sigurdsEpicAdventureStuff.event.HandlerServer;
import com.sigurd4.sigurdsEpicAdventureStuff.event.HandlerServerFML;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderEntity;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;

public class ProxyServer extends ProxyCommon
{
	@Override
	public Side side()
	{
		return Side.SERVER;
	}
	
	@Override
	public void preInit(FMLPreInitializationEvent event)
	{
		MinecraftForge.EVENT_BUS.register(new HandlerServer());
		FMLCommonHandler.instance().bus().register(new HandlerServerFML());

		super.preInit(event);
	}

	@Override
	public void init(FMLInitializationEvent event)
	{
		super.init(event);
	}

	public World world(int dimension)
	{
		return MinecraftServer.getServer().worldServers[dimension];
	}
}
