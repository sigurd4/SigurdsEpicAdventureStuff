package com.sigurd4.sigurdsEpicAdventureStuff.gui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;
import com.sigurd4.sigurdsEpicAdventureStuff.Config;
import com.sigurd4.sigurdsEpicAdventureStuff.References;
import com.sigurd4.sigurdsEpicAdventureStuff.Config.ConfigEntry;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;

public class GuiConfig2 extends GuiConfig
{
	public GuiConfig2(GuiScreen parentScreen)
	{
		super(parentScreen, getAllElements(), References.MODID, false, false, GuiConfig.getAbridgedConfigPath(Config.config.toString()));
	}

	private static List<IConfigElement> getAllElements()
	{
		Iterator<String> categories = Config.config.getCategoryNames().iterator();
		ArrayList<IConfigElement> elements = Lists.newArrayList();
		while(categories.hasNext())
		{
			ConfigCategory category = Config.config.getCategory(categories.next());
			if(category != null)
			{
				elements.addAll(new ConfigElement(category).getChildElements());
			}
		}
		return elements;
	}
}
