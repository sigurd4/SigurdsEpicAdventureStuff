package com.sigurd4.sigurdsEpicAdventureStuff.item;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemStack;
import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import baubles.api.IBauble;

@AItemForMod(modids = {"Baubles"})
public class ItemEquipmentBauble extends ItemEquipment implements IBauble
{
	public final BaubleType baubleType;
	
	public ItemEquipmentBauble(ArmorMaterial[] matsBase, ArmorMaterial[] matsDeco, float damageReduceMod, float durabilityMod, float enchantabilityMod, BaubleType baubleType)
	{
		super(matsBase, matsDeco, damageReduceMod, durabilityMod, enchantabilityMod);
		this.baubleType = baubleType;
	}

	@Override
	protected boolean isEquipped(ItemStack stack, EntityLivingBase entity)
	{
		ItemStack heldStack = entity.getHeldItem();
		if(heldStack != null && ItemStack.areItemStacksEqual(stack, heldStack))
		{
			return false;
		}
		if(entity instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer)entity;
			IInventory baubles = BaublesApi.getBaubles(player);
			
			for(int i = 0; i < baubles.getSizeInventory(); i++)
			{
				ItemStack stack2 = baubles.getStackInSlot(i);
				if(stack2 != null && ItemStack.areItemStacksEqual(stack, stack2))
				{
					return true;
				}
			}
		}
		return false;
	}
	
	@Override
	protected boolean equipEquipment(ItemStack stack, EntityLivingBase entity)
	{
		if(entity instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer)entity;
			IInventory baubles = BaublesApi.getBaubles(player);
			
			for(int i = 0; i < baubles.getSizeInventory(); i++)
			{
				if(baubles.getStackInSlot(i) == null && baubles.isItemValidForSlot(i, stack.copy()))
				{
					if(!player.worldObj.isRemote)
					{
						ItemStack itemstack1 = stack.copy();
						itemstack1.stackSize = 1;
						baubles.setInventorySlotContents(i, itemstack1);
						
						((ItemEquipmentBauble)stack.getItem()).onEquipped(stack, player);
						
						--stack.stackSize;
					}
					return true;
				}
			}
		}
		return false;
	}
	
	@Override
	public BaubleType getBaubleType(ItemStack itemstack)
	{
		return this.baubleType;
	}
	
	@Override
	public final void onWornTick(ItemStack stack, EntityLivingBase player)
	{
		this.onWornTick2(stack, player);
		if(player instanceof EntityPlayer)
		{
			this.onArmorTick(player.worldObj, (EntityPlayer)player, stack);
		}
	}
	
	public void onWornTick2(ItemStack stack, EntityLivingBase player)
	{
		
	}
	
	@Override
	public final void onEquipped(ItemStack stack, EntityLivingBase player)
	{

	}
	
	@Override
	public void onUnequipped(ItemStack stack, EntityLivingBase player)
	{
		
	}
	
	@Override
	public boolean canEquip(ItemStack stack, EntityLivingBase player)
	{
		return true;
	}
	
	@Override
	public final boolean canUnequip(ItemStack stack, EntityLivingBase player)
	{
		return (!this.isCursed(stack) || player instanceof EntityPlayer && ((EntityPlayer)player).capabilities.isCreativeMode) && this.canUnequip2(stack, player);
	}
	
	public boolean canUnequip2(ItemStack stack, EntityLivingBase player)
	{
		return true;
	}
}
