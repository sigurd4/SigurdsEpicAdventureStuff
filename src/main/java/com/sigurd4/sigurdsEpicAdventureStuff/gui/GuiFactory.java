package com.sigurd4.sigurdsEpicAdventureStuff.gui;

import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.network.play.server.S2DPacketOpenWindow;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.IModGuiFactory;

public class GuiFactory implements IModGuiFactory
{
	@Override
	public void initialize(Minecraft minecraftInstance)
	{
		
	}

	@Override
	public Class<? extends GuiScreen> mainConfigGuiClass()
	{
		return GuiConfig2.class;
	}

	@Override
	public Set<RuntimeOptionCategoryElement> runtimeGuiCategories()
	{
		return null;
	}

	@Override
	public RuntimeOptionGuiHandler getHandlerFor(RuntimeOptionCategoryElement element)
	{
		return null;
	}
	
}