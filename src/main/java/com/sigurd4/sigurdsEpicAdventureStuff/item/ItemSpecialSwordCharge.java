package com.sigurd4.sigurdsEpicAdventureStuff.item;

import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.sigurd4.sigurdsEpicAdventureStuff.M;
import com.sigurd4.sigurdsEpicAdventureStuff.References;
import com.sigurd4.sigurdsEpicAdventureStuff.extended.ExtendedPlayer;
import com.sigurd4.sigurdsEpicAdventureStuff.itemtags.ItemTagInteger;

public abstract class ItemSpecialSwordCharge extends ItemSpecialSword
{
	
	public final ItemTagInteger CHARGE;
	public final ItemTagInteger COOLDOWN;
	public final ItemTagInteger CHARGEINT = new ItemTagInteger("ChargeInt", 0, 0, 4, true);
	public final ItemTagInteger COOLDOWNINT = new ItemTagInteger("CooldownInt", 0, 0, 4, true);
	
	private final int charge;
	private final int cooldown;

	public ItemSpecialSwordCharge(int attackDamage, float slashMultiplier, int uses, int charge, int cooldown)
	{
		super(attackDamage, slashMultiplier, uses);
		this.charge = charge;
		this.cooldown = cooldown;
		this.CHARGE = new ItemTagInteger("Charge", 0, 0, charge, true);
		this.COOLDOWN = new ItemTagInteger("Cooldown", 0, 0, cooldown, true);
	}

	@Override
	public abstract void spawnSlashParticle(Vec3 look1, Vec3 look);
	
	public abstract void onReleaseEarly(ItemStack stack, World world, EntityPlayer player, int timeLeft);
	
	public abstract void onReleaseCharged(ItemStack stack, World world, EntityPlayer player, int timeLeft);

	@Override
	public final void onUpdate(ItemStack stack, World worldIn, Entity entity, int itemSlot, boolean isSelected)
	{
		if(this.COOLDOWN.get(stack) < this.COOLDOWN.max)
		{
			if(!(entity instanceof EntityPlayer) || ExtendedPlayer.get((EntityPlayer)entity).spin <= 0)
			{
				this.COOLDOWN.add(stack, 1);
			}
			this.CHARGE.set(stack, 0);
		}
		if(!isSelected || entity instanceof EntityPlayer && !((EntityPlayer)entity).isBlocking())
		{
			this.CHARGE.set(stack, 0);
		}
		this.CHARGEINT.set(stack, (int)Math.floor((float)this.CHARGE.get(stack) / this.CHARGE.max * 4));
		this.COOLDOWNINT.set(stack, (int)Math.floor((float)this.COOLDOWN.get(stack) / this.COOLDOWN.max * 4));
	}

	@Override
	public void onPlayerStoppedUsing(ItemStack stack, World world, EntityPlayer player, int timeLeft)
	{
		if(this.CHARGE.get(stack) >= this.CHARGE.max)
		{
			this.onReleaseCharged(stack, world, player, timeLeft);
		}
		else
		{
			this.onReleaseEarly(stack, world, player, timeLeft);
		}
		this.CHARGE.set(stack, 0);
	}

	@Override
	public void onUsingTick(ItemStack stack, EntityPlayer player, int count)
	{
		if(this.COOLDOWN.get(stack) >= this.COOLDOWN.max)
		{
			if(this.CHARGE.get(stack) < this.CHARGE.max)
			{
				this.CHARGE.add(stack, 1);
			}
		}
	}
	
	protected final void doSpin(ItemStack stack, World world, EntityPlayer player, float spin)
	{
		if(world.isRemote)
		{
			ItemSpecialSword.yaw += 179;
			this.attack(stack);
			ExtendedPlayer props = ExtendedPlayer.get(player);
			props.spin = (int)((float)360 / 20 * spin);
			stack.damageItem(2, player);
		}
		this.COOLDOWN.set(stack, 0);
	}

	@SideOnly(Side.CLIENT)
	public int getModelToUse(ItemStack stack, EntityPlayer player, int useRemaining, String[] variants, int texture)
	{
		if(!ItemSpecialSwordCharge.this.getTextureVariants(0).equals(this.getTextureVariants(0)))
		{
			try
			{
				throw new Exception("Sword '" + Item.getIdFromItem(this) + "' overrides 'getTextureVariants()', but not 'getModelToUse()'. This may cause trouble!");
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return texture;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ModelResourceLocation getModel(ItemStack stack, EntityPlayer player, int useRemaining)
	{
		int i = this.CHARGEINT.get(stack) + this.COOLDOWNINT.get(stack);
		String[] variants = this.getTextureVariants(stack.getItemDamage());
		if(i >= variants.length)
		{
			i = variants.length - 1;
		}
		ModelResourceLocation mrl = new ModelResourceLocation(variants[i].toLowerCase(), "inventory");
		return mrl;
	}

	@Override
	public String[] getTextureVariants(int meta)
	{
		return new String[] {References.MODID + ":" + M.getId(this).id + "_0", References.MODID + ":" + M.getId(this).id + "_1", References.MODID + ":" + M.getId(this).id + "_2", References.MODID + ":" + M.getId(this).id + "_3", References.MODID + ":" + M.getId(this).id + "_4", References.MODID + ":" + M.getId(this).id + "_5", References.MODID + ":" + M.getId(this).id + "_6", References.MODID + ":" + M.getId(this).id + "_7", References.MODID + ":" + M.getId(this).id + "_8"};
	}
}
