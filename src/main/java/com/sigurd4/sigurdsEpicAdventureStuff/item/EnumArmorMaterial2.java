package com.sigurd4.sigurdsEpicAdventureStuff.item;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraftforge.common.util.EnumHelper;

import com.sigurd4.sigurdsEpicAdventureStuff.M;

public enum EnumArmorMaterial2
{
	;

	public static final ArmorMaterial SILVER = EnumArmorMaterial2.addArmorMaterial(M.silver_ingot, "SILVER", "silver", 9, new int[] {2, 5, 4, 1}, 22);
	public static final ArmorMaterial EMERALD = EnumArmorMaterial2.addArmorMaterial(Items.emerald, "EMERALD", "emerald", 2, new int[] {1, 2, 2, 1}, 30);
	
	private static final ArmorMaterial addArmorMaterial(Item customCraftingMaterial, String name, String textureName, int durability, int[] reductionAmounts, int enchantability)
	{
		ArmorMaterial mat = EnumHelper.addArmorMaterial(name, textureName, durability, reductionAmounts, enchantability);
		mat.customCraftingMaterial = customCraftingMaterial;
		return mat;
	}
}
