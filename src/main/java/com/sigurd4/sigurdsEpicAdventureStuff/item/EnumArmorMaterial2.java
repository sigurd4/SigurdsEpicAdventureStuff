package com.sigurd4.sigurdsEpicAdventureStuff.item;

import java.util.ArrayList;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.EnumHelper;

import com.sigurd4.sigurdsEpicAdventureStuff.M;
import com.sigurd4.sigurdsEpicAdventureStuff.Stuff;

public enum EnumArmorMaterial2
{
	;

	private static ArrayList<ArmorMaterial> values = new ArrayList();
	
	public static final ArmorMaterial EMERALD = EnumArmorMaterial2.addArmorMaterial(Items.emerald, "EMERALD", "emerald", 3, new int[] {2, 4, 3, 1}, 30);
	public static final ArmorMaterial ENDER_PEARL = EnumArmorMaterial2.addArmorMaterial(Items.ender_pearl, "ENDER_PEARL", "ender_pearl", 1, new int[] {1, 1, 1, 1}, 60);
	public static final ArmorMaterial PRISMARINE_CRYSTAL = EnumArmorMaterial2.addArmorMaterial(Items.prismarine_crystals, "PRISMARINE_CRYSTAL", "prismarine_crystal", 4, new int[] {2, 3, 2, 1}, 28);
	public static final ArmorMaterial QUARTZ = EnumArmorMaterial2.addArmorMaterial(Items.quartz, "QUARTZ", "quartz", 2, new int[] {1, 3, 2, 1}, 15);
	public static final ArmorMaterial RUBY = EnumArmorMaterial2.addArmorMaterial(M.ruby, "RUBY", "ruby", 5, new int[] {2, 5, 4, 1}, 27);
	public static final ArmorMaterial SILVER = EnumArmorMaterial2.addArmorMaterial(M.silver_ingot, "SILVER", "silver", 9, new int[] {2, 5, 4, 1}, 22);
	
	private static final ArmorMaterial addArmorMaterial(Item customCraftingMaterial, String name, String textureName, int durability, int[] reductionAmounts, int enchantability)
	{
		ArmorMaterial mat = EnumHelper.addArmorMaterial(name, textureName, durability, reductionAmounts, enchantability);
		mat.customCraftingMaterial = customCraftingMaterial;
		EnumArmorMaterial2.values.add(mat);
		return mat;
	}
	
	public static ArmorMaterial[] values2()
	{
		return Stuff.ArraysAndSuch.arrayListToArray2(EnumArmorMaterial2.values, new ArmorMaterial[EnumArmorMaterial2.values.size()]);
	}
	
	public static String getUnlocalizedName(ArmorMaterial mat)
	{
		return Stuff.Strings.capitalizeEveryWord(Stuff.Strings.underscoresToWhiteSpaces(mat.getName().toLowerCase()));
	}
	
	public static String getDisplayName(ArmorMaterial mat)
	{
		return StatCollector.translateToLocal(EnumArmorMaterial2.getUnlocalizedName(mat)).trim();
	}
}
