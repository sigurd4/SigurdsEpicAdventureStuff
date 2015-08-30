package com.sigurd4.sigurdsEpicAdventureStuff.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.common.ISpecialArmor;

public class ItemEquipmentArmor extends ItemEquipment implements ISpecialArmor
{
	public final EnumArmorType armorType;
	
	public ItemEquipmentArmor(ArmorMaterial[] matsBase, ArmorMaterial[] matsDeco, float damageReduceMod, float durabilityMod, float enchantabilityMod, EnumArmorType armorType)
	{
		super(matsBase, matsDeco, damageReduceMod, durabilityMod, enchantabilityMod);
		this.armorType = armorType;
	}
	
	@Override
	protected boolean equipEquipment(ItemStack stack, EntityLivingBase entitylivingbase)
	{
		int i1 = EntityLiving.getArmorPosition(stack);
		if(i1 <= 0)
		{
			i1 = this.armorType.getSlot();
		}
		if(i1 > 0)
		{
			if(!entitylivingbase.worldObj.isRemote)
			{
				ItemStack itemstack1 = stack.copy();
				itemstack1.stackSize = 1;
				entitylivingbase.setCurrentItemOrArmor(i1, itemstack1);
				
				if(entitylivingbase instanceof EntityLiving)
				{
					((EntityLiving)entitylivingbase).setEquipmentDropChance(i1, 2.0F);
				}
				
				--stack.stackSize;
			}
			return true;
		}
		return false;
	}
	
	@Override
	public boolean isValidArmor(ItemStack stack, int armorType, Entity entity)
	{
		return armorType == this.armorType.getId();
	}
	
	@Override
	public ArmorProperties getProperties(EntityLivingBase player, ItemStack armor, DamageSource source, double damage, int slot)
	{
		return new ArmorProperties(0, this.getArmorDisplay(armor, slot) / 25D, Integer.MAX_VALUE);
	}
	
	@Override
	public int getArmorDisplay(EntityPlayer player, ItemStack armor, int slot)
	{
		return this.getArmorDisplay(armor, slot);
	}
	
	private int getArmorDisplay(ItemStack armor, int slot)
	{
		return 10 * Math.round(this.getMaterial(armor).getDamageReductionAmount(1) * this.damageReduceMod / 40);
	}
	
	@Override
	public void damageArmor(EntityLivingBase entity, ItemStack stack, DamageSource source, int damage, int slot)
	{
		stack.damageItem(damage, entity);
	}

	@Override
	public void onArmorTick(World world, EntityPlayer player, ItemStack stack)
	{
		this.setKnown(stack, player);
		ItemEquipment.CURSED.add(stack, -1);
	}
}
