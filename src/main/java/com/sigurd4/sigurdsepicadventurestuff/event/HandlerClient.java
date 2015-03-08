package com.sigurd4.sigurdsEpicAdventureStuff.event;

import java.util.ArrayList;

import com.sigurd4.sigurdsEpicAdventureStuff.gui.GuiHud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;

@SideOnly(Side.CLIENT)
public class HandlerClient
{
	@SubscribeEvent
	public void renderGameOverlayEvent(RenderGameOverlayEvent.Post event)
	{
		if(event.type == ElementType.ALL)
		{
			GuiHud hud = new GuiHud(Minecraft.getMinecraft());
			hud.renderGameOverlay(Minecraft.getMinecraft().thePlayer);
		}
	}
}
