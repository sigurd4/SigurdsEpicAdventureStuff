package com.sigurd4.sigurdsEpicAdventureStuff.gui;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.potion.Potion;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL41;
import org.lwjgl.opengl.GLContext;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;

public class GuiHud extends Gui
{
	protected final Random rand = new Random();
	protected final Minecraft mc;

	public GuiHud(Minecraft mc)
	{
		this.mc = mc;
	}

	/**
	 * Render ingame text in the corner, ...
	 */
	public void renderGameOverlay(EntityPlayer player)
	{   
		ScaledResolution scaledresolution = new ScaledResolution(this.mc, this.mc.displayWidth, this.mc.displayHeight);
		int w = scaledresolution.getScaledWidth();
		int h = scaledresolution.getScaledHeight();
		FontRenderer fontrenderer = this.mc.fontRendererObj;

		GL11.glPushMatrix();
		if(mc.inGameHasFocus)
		{
			//mc.mcProfiler.startSection("something");
			//mc.mcProfiler.endSection();
		}
		GL11.glColor4f(1, 1, 1, 1);
		GL11.glPopMatrix();
	}
}