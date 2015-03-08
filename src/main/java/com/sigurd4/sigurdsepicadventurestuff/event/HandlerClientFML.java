package com.sigurd4.sigurdsepicadventurestuff.event;

import java.util.ArrayList;
import java.util.HashMap;

import org.lwjgl.input.Keyboard;

import com.sigurd4.sigurdsepicadventurestuff.M;
import com.sigurd4.sigurdsepicadventurestuff.packet.PacketKey;
import com.sigurd4.sigurdsepicadventurestuff.packet.PacketKey.Key;
import com.sigurd4.sigurdsepicadventurestuff.particles.ParticleHandler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.Item;
import net.minecraft.network.INetHandler;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.registry.GameRegistry;

@SideOnly(Side.CLIENT)
public class HandlerClientFML extends HandlerBase
{
	//fml events for client only here!

	public static KeyBinding WeaponReload;
	public static KeyBinding WeaponZoom;
	public static KeyBinding WeaponEject;

	public static void init()
	{
		WeaponReload = new KeyBinding("key.WeaponReload", Keyboard.KEY_R, "key.categories.spectral.guns");
		WeaponZoom = new KeyBinding("key.WeaponZoom", Keyboard.KEY_Z, "key.categories.spectral.guns");
		WeaponEject = new KeyBinding("key.WeaponEject", Keyboard.KEY_B, "key.categories.spectral.guns");

		ClientRegistry.registerKeyBinding(WeaponReload);
		ClientRegistry.registerKeyBinding(WeaponZoom);
		ClientRegistry.registerKeyBinding(WeaponEject);
	}

	HashMap<KeyBinding, Integer> keyPressed = new HashMap<KeyBinding, Integer>();

	public boolean keypress(KeyBinding k)
	{
		if(!keyPressed.containsKey(k))
		{
			keyPressed.put(k, 0);
		}
		boolean b = true;
		if(!k.isKeyDown() || keyPressed.get(k) > 0)
		{
			b = false;
		}
		keyPressed.put(k, k.isKeyDown() ? keyPressed.get(k)+1 : 0);
		return b;
	}

	public boolean keyhold(KeyBinding k, int rate)
	{
		if(!keyPressed.containsKey(k) || !k.isKeyDown())
		{
			keyPressed.put(k, 0);
			return false;
		}
		if(k.isKeyDown() && keyPressed.get(k) >= rate)
		{
			keyPressed.put(k, 0);
			return true;
		}
		keyPressed.put(k, k.isKeyDown() ? keyPressed.get(k)+1 : 0);
		return false;
	}

	@SubscribeEvent
	public void onKeyInput2(InputEvent.KeyInputEvent event)
	{
		//use for keys to hit once
		/*if(keypress(WeaponReload) && !WeaponEject.isKeyDown())
		{
			M.network.sendToServer(new PacketKey(Key.RELOAD));
		}*/
	}

	@SubscribeEvent
	public void clientTickEvent2(ClientTickEvent event)
	{
		//use for keys to hold down
		/*if(Minecraft.getMinecraft().gameSettings.keyBindUseItem.isKeyDown())
		{
			sendKey(Key.RIGHTCLICK);
			if(Minecraft.getMinecraft().thePlayer != null && EntityExtendedPlayer.get(Minecraft.getMinecraft().thePlayer) != null)
				EntityExtendedPlayer.get(Minecraft.getMinecraft().thePlayer).setRightClick(true);
		}*/
		
		ParticleHandler.update();
	}

	protected void sendKey(Key k)
	{
		PacketKey m = new PacketKey(k);
		M.network.sendToServer(m);
		PacketKey.Handler.onMessage(m, Minecraft.getMinecraft().thePlayer);
	}
}