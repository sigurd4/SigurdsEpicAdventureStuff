package com.sigurd4.sigurdsEpicAdventureStuff.item;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.sigurd4.sigurdsEpicAdventureStuff.Config;
import com.sigurd4.sigurdsEpicAdventureStuff.M;
import com.sigurd4.sigurdsEpicAdventureStuff.References;
import com.sigurd4.sigurdsEpicAdventureStuff.Stuff;
import com.sigurd4.sigurdsEpicAdventureStuff.extended.ExtendedPlayer;

public abstract class ItemSpecialSword extends ItemSword implements IItemSubItems, IItemTextureVariants
{
	public HashMap<EntityPlayer, Boolean> playerReachAffected = new HashMap<EntityPlayer, Boolean>();
	public static float pitch = 0;
	public static float yaw = 0;
	
	public float attackDamage;
	public float moveMultiplier;
	public float slashMultiplier;
	
	public ItemSpecialSword(int attackDamage, float slashMultiplier, int uses)
	{
		super(Item.ToolMaterial.IRON);
		this.maxStackSize = 1;
		this.setMaxDamage(uses);
		this.setCreativeTab(M.tabCore);
		this.attackDamage = attackDamage;
		this.slashMultiplier = slashMultiplier;
	}
	
	/**
	 * Returns the amount of damage this item will deal. One heart of damage is
	 * equal to 2 damage points.
	 */
	@Override
	public float getDamageVsEntity()
	{
		return this.attackDamage;
	}
	
	@Override
	public float getStrVsBlock(ItemStack stack, Block block)
	{
		if(block == Blocks.web)
		{
			return 15.0F;
		}
		else
		{
			Material material = block.getMaterial();
			return material != Material.plants && material != Material.vine && material != Material.coral && material != Material.leaves && material != Material.gourd ? 1.0F : 1.5F;
		}
	}
	
	/**
	 * Called each tick as long the item is on a player inventory. Uses by maps
	 * to check if is on a player hand and update it's contents.
	 */
	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected)
	{
		super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected);
	}
	
	/**
	 * Called when the player stops using an Item (stops holding the right mouse
	 * button).
	 * 
	 * @param timeLeft
	 *            The amount of ticks left before the using would have been
	 *            complete
	 */
	@Override
	public void onPlayerStoppedUsing(ItemStack stack, World world, EntityPlayer player, int timeLeft)
	{
		
	}
	
	/**
	 * Called each tick while using an item.
	 * 
	 * @param stack
	 *            The Item being used
	 * @param player
	 *            The Player using the item
	 * @param count
	 *            The amount of time in tick the item has been used for
	 *            continuously
	 */
	@Override
	public void onUsingTick(ItemStack stack, EntityPlayer player, int count)
	{
		
	}
	
	/**
	 * Called when a Block is destroyed using this Item. Return true to trigger
	 * the "Use Item" statistic.
	 */
	@Override
	public boolean onBlockDestroyed(ItemStack stack, World worldIn, Block blockIn, BlockPos pos, EntityLivingBase playerIn)
	{
		if(blockIn.getBlockHardness(worldIn, pos) != 0.0D)
		{
			stack.damageItem(2, playerIn);
		}
		
		return true;
	}
	
	/**
	 * Returns True is the item is renderer in full 3D when hold.
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public boolean isFull3D()
	{
		return true;
	}
	
	/**
	 * returns the action that specifies what animation to play when the items
	 * is being used
	 */
	@Override
	public EnumAction getItemUseAction(ItemStack stack)
	{
		return EnumAction.BLOCK;
	}
	
	/**
	 * How long it takes to use or consume an item
	 */
	@Override
	public int getMaxItemUseDuration(ItemStack stack)
	{
		return 72000;
	}
	
	/**
	 * Called whenever this item is equipped and the right mouse button is
	 * pressed. Args: itemStack, world, entityPlayer
	 */
	@Override
	public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn)
	{
		playerIn.setItemInUse(itemStackIn, this.getMaxItemUseDuration(itemStackIn));
		return itemStackIn;
	}
	
	/**
	 * Check whether this Item can harvest the given Block
	 */
	@Override
	public boolean canHarvestBlock(Block blockIn)
	{
		return blockIn == Blocks.web;
	}
	
	/**
	 * Return the enchantability factor of the item, most of the time is based
	 * on material.
	 */
	@Override
	public int getItemEnchantability()
	{
		return 0;
	}
	
	/**
	 * Return whether this item is repairable in an anvil.
	 * 
	 * @param toRepair
	 *            The ItemStack to be repaired
	 * @param repair
	 *            The ItemStack that should repair this Item (leather for
	 *            leather armor, etc.)
	 */
	@Override
	public boolean getIsRepairable(ItemStack toRepair, ItemStack repair)
	{
		return false;
	}
	
	/**
	 * Gets a map of item attribute modifiers, used by ItemSword to increase hit
	 * damage.
	 */
	@Override
	public Multimap getAttributeModifiers(ItemStack stack)
	{
		Multimap multimap = HashMultimap.create();
		multimap.put(SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName(), new AttributeModifier(Item.itemModifierUUID, "Weapon modifier", this.attackDamage, 0));
		//multimap.put(SharedMonsterAttributes.movementSpeed.getAttributeUnlocalizedName(), new AttributeModifier(itemModifierUUID, "Weapon modifier", (double)this.moveMultiplier, 0));
		return multimap;
	}
	
	/**
	 * Called when a entity tries to play the 'swing' animation.
	 *
	 * @param entityLiving
	 *            The entity swinging the item.
	 * @param stack
	 *            The Item stack
	 * @return True to cancel any further processing by EntityLiving
	 */
	@Override
	public boolean onEntitySwing(EntityLivingBase entityLiving, ItemStack stack)
	{
		if(entityLiving.worldObj.isRemote)
		{
			this.attack(stack);
		}
		return false;
	}
	
	@Override
	public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker)
	{
		if(attacker.motionX == 0 || attacker.getLookVec().xCoord > 0 == attacker.motionX > 0)
		{
			if(attacker.motionY == 0 || attacker.getLookVec().yCoord > 0 == attacker.motionY > 0)
			{
				if(attacker.motionZ == 0 || attacker.getLookVec().zCoord > 0 == attacker.motionZ > 0)
				{
					target.addVelocity(attacker.motionX / 2, attacker.motionY / 2, attacker.motionZ / 2);
				}
			}
		}
		return super.hitEntity(stack, target, attacker);
	}
	
	@SideOnly(Side.CLIENT)
	public void attack(ItemStack stack)
	{
		Minecraft mc = Minecraft.getMinecraft();
		
		if(mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectType.BLOCK)
		{
			return;
		}
		float reach = 3;
		Vec3 look1 = Stuff.Coordinates3D.getVectorForRotation(mc.thePlayer.rotationPitch, mc.thePlayer.rotationYaw);
		look1 = new Vec3(look1.xCoord * reach, look1.yCoord * reach, look1.zCoord * reach);
		float pt = Stuff.Reflection.getTimer().renderPartialTicks;
		final float orp = mc.thePlayer.rotationPitch;
		final float ory = mc.thePlayer.rotationYaw;
		final float oryh = mc.thePlayer.rotationYawHead;
		final float porp = mc.thePlayer.prevRotationPitch;
		final float pory = mc.thePlayer.prevRotationYaw;
		final float poryh = mc.thePlayer.prevRotationYawHead;
		
		float rp = -ItemSpecialSword.pitch;
		float ry = -ItemSpecialSword.yaw;
		rp /= 2;
		ry /= 2;
		if(!mc.thePlayer.onGround && !mc.thePlayer.isAirBorne)
		{
			rp -= mc.thePlayer.motionY * 200;
		}
		double w = Math.sqrt(rp * rp + ry * ry);
		if(w > 0.1)
		{
			rp *= this.slashMultiplier;
			ry *= this.slashMultiplier;
			if(ExtendedPlayer.get(mc.thePlayer).spin <= 0)
			{
				rp *= Config.slashLenght.get();
				ry *= Config.slashLenght.get();
			}
			w /= w / 100;
			float a = 90;
			if(w > a)
			{
				rp /= w / a;
				ry /= w / a;
			}
			int times = (int)Math.ceil(w / 2);
			for(int i = 0; i < times; ++i)
			{
				float f = Stuff.rand.nextFloat() * Stuff.rand.nextFloat() * Stuff.rand.nextFloat();
				f *= Stuff.rand.nextBoolean() ? -1 : 1;
				mc.thePlayer.rotationPitch = orp + rp * f;
				mc.thePlayer.prevRotationPitch = porp + ItemSpecialSword.pitch + rp * f;
				mc.thePlayer.rotationYawHead = mc.thePlayer.rotationYaw = ory + ry * f;
				mc.thePlayer.prevRotationYawHead = mc.thePlayer.prevRotationYaw = pory + ItemSpecialSword.yaw + ry * f;
				
				mc.entityRenderer.getMouseOver(pt);
				
				Vec3 look = Stuff.Coordinates3D.getVectorForRotation(mc.thePlayer.rotationPitch, mc.thePlayer.rotationYaw);
				look = new Vec3(look.xCoord * reach, look.yCoord * reach, look.zCoord * reach);
				
				if(mc.thePlayer != null && mc.objectMouseOver != null)
				{
					Vec3 hitVec = new Vec3(mc.objectMouseOver.hitVec.xCoord - mc.thePlayer.posX, mc.objectMouseOver.hitVec.yCoord - mc.thePlayer.posY - mc.thePlayer.getEyeHeight(), mc.objectMouseOver.hitVec.zCoord - mc.thePlayer.posZ);
					this.attackDamage /= 2;
					if(mc.thePlayer != null && mc.objectMouseOver != null && mc.objectMouseOver.entityHit != null)
					{
						mc.playerController.attackEntity(mc.thePlayer, mc.objectMouseOver.entityHit);
						//leftClick();
					}
					if(mc.thePlayer != null && mc.objectMouseOver != null && mc.objectMouseOver.entityHit instanceof EntityLivingBase && !Config.slashMultiple.get())
					{
						break;
					}
					this.attackDamage *= 2;
					if(hitVec.distanceTo(mc.thePlayer.getPositionEyes(pt)) >= look.distanceTo(mc.thePlayer.getPositionEyes(pt)))
					{
						if(Math.sqrt(look.xCoord * look.xCoord + look.zCoord * look.zCoord) > 1)
						{
							this.spawnSlashParticle(look1, look);
						}
					}
				}
			}
			ItemSpecialSword.pitch = 0;
			ItemSpecialSword.yaw = 0;
		}
		if(true)
		{
			mc.entityRenderer.getMouseOver(Stuff.Reflection.getTimer().renderPartialTicks);
			if(mc.thePlayer != null && mc.objectMouseOver != null && mc.objectMouseOver.entityHit != null)
			{
				mc.playerController.attackEntity(mc.thePlayer, mc.objectMouseOver.entityHit);
				//leftClick();
			}
			
			Vec3 look = Stuff.Coordinates3D.getVectorForRotation(mc.thePlayer.rotationPitch, mc.thePlayer.rotationYaw);
			look = new Vec3(look.xCoord * reach, look.yCoord * reach, look.zCoord * reach);
			//mc.thePlayer.worldObj.spawnParticle(EnumParticleTypes.CRIT, mc.thePlayer.posX+look.xCoord, mc.thePlayer.posY+look.yCoord+mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ+look.zCoord, 0, 0, 0, new int[]{});
		}
		
		mc.thePlayer.rotationPitch = orp;
		mc.thePlayer.rotationYaw = ory;
		mc.thePlayer.rotationYawHead = oryh;
		mc.thePlayer.prevRotationPitch = porp;
		mc.thePlayer.prevRotationYaw = pory;
		mc.thePlayer.prevRotationYawHead = poryh;
		mc.entityRenderer.getMouseOver(pt);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item item, CreativeTabs tab, List stacks)
	{
		if(stacks instanceof ArrayList<?>)
		{
			this.getSubItems2(item, tab, (ArrayList<ItemStack>)stacks);
		}
	}
	
	@Override
	public void getSubItems2(Item item, CreativeTabs tab, ArrayList<ItemStack> stacks)
	{
		stacks.add(new ItemStack(item));
	}
	
	@SideOnly(Side.CLIENT)
	public abstract void spawnSlashParticle(Vec3 look1, Vec3 look);
	
	@SideOnly(Side.CLIENT)
	public void leftClick()
	{
		try
		{
			Method method = Minecraft.class.getDeclaredMethod("clickMouse");
			method.setAccessible(true);
			method.invoke(Minecraft.getMinecraft());
			Minecraft.getMinecraft().entityRenderer.getMouseOver(Stuff.Reflection.getTimer().renderPartialTicks);
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public ModelResourceLocation getModel(ItemStack stack, EntityPlayer player, int useRemaining)
	{
		return new ModelResourceLocation(References.MODID + ":" + M.getId(this).id, "inventory");
	}
}