package com.sigurd4.sigurdsEpicAdventureStuff.event;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.sigurd4.sigurdsEpicAdventureStuff.Stuff;
import com.sigurd4.sigurdsEpicAdventureStuff.extended.ExtendedPlayer;
import com.sigurd4.sigurdsEpicAdventureStuff.gui.GuiHud;
import com.sigurd4.sigurdsEpicAdventureStuff.item.ItemSpecialSword;

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
	
	@SubscribeEvent
	public void RenderHandEvent(RenderHandEvent event)
	{
		try
		{
			Stuff.Reflection.setItemToRender2();
		}
		catch(IllegalArgumentException e)
		{
			e.printStackTrace();
		}
		catch(IllegalAccessException e)
		{
			e.printStackTrace();
		}
	}
	
	@SubscribeEvent
	public void MouseEvent(MouseEvent event)
	{
		EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
		WorldClient world = Minecraft.getMinecraft().theWorld;
		
		ItemSpecialSword.pitch = event.dy * 2.4F;
		ItemSpecialSword.yaw = -event.dx * 2.2F;
		if(player != null && world != null)
		{
			ExtendedPlayer props = ExtendedPlayer.get(player);
			float spin = 40 * 4;
			if(props.spin > 0 && ItemSpecialSword.yaw < spin)
			{
				ItemSpecialSword.yaw = spin;
			}
		}
		if(ItemSpecialSword.yaw > 0)
		{
			float max = 10F;
			if(Math.sqrt(ItemSpecialSword.pitch * ItemSpecialSword.pitch + ItemSpecialSword.yaw * ItemSpecialSword.yaw) > max)
			{
				double w = max / Math.sqrt(ItemSpecialSword.pitch * ItemSpecialSword.pitch + ItemSpecialSword.yaw * ItemSpecialSword.yaw);
				ItemSpecialSword.pitch *= w;
				ItemSpecialSword.yaw *= w;
			}
		}
	}
}
