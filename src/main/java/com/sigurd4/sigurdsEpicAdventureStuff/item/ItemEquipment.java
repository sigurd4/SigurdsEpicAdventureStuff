package com.sigurd4.sigurdsEpicAdventureStuff.item;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.BlockDispenser;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.command.IEntitySelector;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

import com.google.common.base.Predicates;
import com.sigurd4.sigurdsEpicAdventureStuff.M;
import com.sigurd4.sigurdsEpicAdventureStuff.References;
import com.sigurd4.sigurdsEpicAdventureStuff.Stuff;
import com.sigurd4.sigurdsEpicAdventureStuff.itemtags.ItemTagBoolean;
import com.sigurd4.sigurdsEpicAdventureStuff.itemtags.ItemTagEnum;
import com.sigurd4.sigurdsEpicAdventureStuff.itemtags.ItemTagInteger;
import com.sigurd4.sigurdsEpicAdventureStuff.itemtags.ItemTagMap;

public abstract class ItemEquipment extends Item implements IItemTextureVariants, IItemDynamicModel
{
	private static final IBehaviorDispenseItem dispenserBehavior = new BehaviorDefaultDispenseItem()
	{
		/**
		 * Dispense the specified stack, play the dispense sound and spawn
		 * particles.
		 */
		@Override
		protected ItemStack dispenseStack(IBlockSource source, ItemStack stack)
		{
			BlockPos blockpos = source.getBlockPos().offset(BlockDispenser.getFacing(source.getBlockMetadata()));
			int i = blockpos.getX();
			int j = blockpos.getY();
			int k = blockpos.getZ();
			AxisAlignedBB axisalignedbb = new AxisAlignedBB(i, j, k, i + 1, j + 1, k + 1);
			List list = source.getWorld().getEntitiesWithinAABB(EntityLivingBase.class, axisalignedbb, Predicates.and(IEntitySelector.NOT_SPECTATING, new IEntitySelector.ArmoredMob(stack)));

			if(list.size() > 0)
			{
				EntityLivingBase entitylivingbase = (EntityLivingBase)list.get(0);
				if(((ItemEquipment)stack.getItem()).equipEquipment(stack, entitylivingbase))
				{
					return stack;
				}
			}
			return super.dispenseStack(source, stack);
		}
	};
	public final float damageReduceMod;
	public final float durabilityMod;
	public final float enchantabilityMod;
	private final ArmorMaterial[] materialsBase;
	private final ArmorMaterial[] materialsDeco;
	private final int cursedTime = 400;

	//nbt
	private static final ItemTagMap<Boolean, ItemTagBoolean, EntityPlayer> KNOWN_BY_PLAYERS = new ItemTagMap<Boolean, ItemTagBoolean, EntityPlayer>("KnownByPlayers", new ItemTagBoolean("", false, true), true)
	{
		@Override
		protected String getKey(EntityPlayer key)
		{
			return key.getGameProfile().getId().toString();
		}

		@Override
		protected ItemTagBoolean newEntryTag(ItemTagBoolean defaultEntry, String key)
		{
			return new ItemTagBoolean(key, defaultEntry.getDefault(), defaultEntry.noWobble);
		}
	};
	
	public static final ItemTagInteger CURSED = new ItemTagInteger("Cursed", 0, 0, Integer.MAX_VALUE, false);
	
	public final ItemTagEnum<ArmorMaterial> MATERIAL_BASE = new ItemTagEnum<ArmorMaterial>("MaterialBase", ArmorMaterial.IRON, false)
	{
		@Override
		protected NBTTagInt rawToNBTTag(ArmorMaterial value)
		{
			if(ItemEquipment.this.materialsBase != null)
			{
				for(int i = 0; i < ItemEquipment.this.materialsBase.length; ++i)
				{
					ArmorMaterial mat2 = ItemEquipment.this.materialsBase[i];
					if(mat2 == value)
					{
						return new NBTTagInt(i);
					}
				}
			}
			return new NBTTagInt(0);
		}
		
		@Override
		protected ArmorMaterial NBTTagToRaw(NBTTagInt value)
		{
			int i = value.getInt();
			if(ItemEquipment.this.materialsBase != null)
			{
				if(i < ItemEquipment.this.materialsBase.length && i >= 0)
				{
					return ItemEquipment.this.materialsBase[i];
				}
				return ItemEquipment.this.materialsBase[0];
			}
			return null;
		}
	};
	
	public final ItemTagEnum<ArmorMaterial> MATERIAL_DECO = new ItemTagEnum<ArmorMaterial>("MaterialDeco", ArmorMaterial.IRON, false)
	{
		@Override
		protected NBTTagInt rawToNBTTag(ArmorMaterial value)
		{
			if(ItemEquipment.this.materialsBase != null)
			{
				for(int i = 0; i < ItemEquipment.this.materialsDeco.length; ++i)
				{
					ArmorMaterial mat2 = ItemEquipment.this.materialsDeco[i];
					if(mat2 == value)
					{
						return new NBTTagInt(i);
					}
				}
			}
			return new NBTTagInt(0);
		}
		
		@Override
		protected ArmorMaterial NBTTagToRaw(NBTTagInt value)
		{
			int i = value.getInt();
			if(ItemEquipment.this.materialsBase != null)
			{
				if(i < ItemEquipment.this.materialsDeco.length && i >= 0)
				{
					return ItemEquipment.this.materialsDeco[i];
				}
				return ItemEquipment.this.materialsDeco[0];
			}
			return null;
		}
	};
	
	public ItemEquipment(ArmorMaterial[] matsBase, ArmorMaterial[] matsDeco, float damageReduceMod, float durabilityMod, float enchantabilityMod)
	{
		this.materialsBase = matsBase;
		this.materialsDeco = matsDeco;
		this.damageReduceMod = damageReduceMod;
		this.durabilityMod = durabilityMod;
		this.enchantabilityMod = enchantabilityMod;
		this.setMaxDamage(255);
		this.maxStackSize = 1;
		this.setCreativeTab(M.tabCore);
		BlockDispenser.dispenseBehaviorRegistry.putObject(this, ItemEquipment.dispenserBehavior);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean advanced)
	{
		if(this.isKnown(stack, player))
		{
			if(this.isCursed(stack))
			{
				tooltip.add(EnumChatFormatting.DARK_RED + "Curse: " + ItemEquipment.CURSED.get(stack));
			}
		}
		else
		{
			tooltip.add(EnumChatFormatting.GRAY + "???");
		}
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void getSubItems(Item item, CreativeTabs tab, List list)
	{
		ArrayList<ItemStack> stacks = new ArrayList<ItemStack>();
		for(ArmorMaterial materialBase : this.materialsBase)
		{
			ItemStack stack = new ItemStack(item);
			this.MATERIAL_BASE.set(stack, materialBase);
			this.MATERIAL_DECO.remove(stack);
			//this.setKnown(stack, Minecraft.getMinecraft().thePlayer);
			this.setToHide(stack, Minecraft.getMinecraft().thePlayer);
			stacks.add(stack);
			for(ArmorMaterial materialDeco : this.materialsDeco)
			{
				ItemStack stack2 = stack.copy();
				this.MATERIAL_DECO.set(stack2, materialDeco);
				stacks.add(stack2);
			}
		}
		for(ItemStack stack : stacks)
		{
			while(!stack.isItemEnchanted())
			{
				EnchantmentHelper.addRandomEnchantment(Item.itemRand, stack, 1);
			}
		}
		ArrayList<ItemStack> stacks2 = new ArrayList<ItemStack>();
		while(stacks.size() > 0)
		{
			int i2 = 0;
			for(int i = 0; i < stacks.size(); ++i)
			{
				int rarity1 = this.getRarityInt(stacks.get(i));
				int rarity2 = this.getRarityInt(stacks.get(i2));
				if(rarity1 > rarity2 || rarity1 == rarity2 && stacks.get(i).getItem().getItemEnchantability(stacks.get(i)) > stacks.get(i2).getItem().getItemEnchantability(stacks.get(i2)))
				{
					i2 = i;
				}
			}
			stacks2.add(stacks.get(i2));
			stacks.remove(i2);
		}
		for(ItemStack stack : stacks2)
		{
			if(Item.itemRand.nextFloat() <= 1F / 8F)
			{
				ItemEquipment.CURSED.set(stack, (int)(this.cursedTime * (1F + Item.itemRand.nextFloat() * 2F + (Item.itemRand.nextFloat() / 4 + 0.1F) * this.getRarityInt(stack))));
			}
		}
		list.addAll(stacks2);
	}
	
	public final void setToHide(ItemStack stack, EntityPlayer player)
	{
		if(!this.isKnown(stack, player))
		{
			stack.getTagCompound().setInteger("HideFlags", Stuff.ItemStacks.getHideFlagsInt(true, true, false, false, false, false));
		}
		else
		{
			stack.getTagCompound().removeTag("HideFlags");
		}
	}
	
	protected abstract boolean equipEquipment(ItemStack stack, EntityLivingBase entitylivingbase);
	
	/**
	 * Return the enchantability factor of the item, most of the time is based
	 * on material.
	 */
	@Override
	public int getItemEnchantability(ItemStack stack)
	{
		return this.getMaterial(stack).getEnchantability() + (this.getMaterial2(stack) != null ? this.getMaterial2(stack).getEnchantability() * 4 : 0);
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
		return this.getMaterial(toRepair).getRepairItem() == repair.getItem() || OreDictionary.itemMatches(new ItemStack(this.getMaterial(toRepair).getRepairItem()), repair, false) || super.getIsRepairable(toRepair, repair);
	}
	
	/**
	 * Called whenever this item is equipped and the right mouse button is
	 * pressed. Args: itemStack, world, entityPlayer
	 */
	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
	{
		this.equipEquipment(stack, player);
		
		return stack;
	}
	
	public final ArmorMaterial getMaterial(ItemStack stack)
	{
		return this.MATERIAL_BASE.get(stack);
	}
	
	public final ArmorMaterial getMaterial2(ItemStack stack)
	{
		if(this.MATERIAL_DECO.has(stack))
		{
			return this.MATERIAL_DECO.get(stack);
		}
		return null;
	}
	
	@Override
	public int getMaxDamage(ItemStack stack)
	{
		return 10 * Math.round(this.getMaterial(stack).getDurability(1) * this.durabilityMod / 40);
	}
	
	public int getRarityInt(ItemStack stack)
	{
		int rarity = 0;
		NBTTagList list = stack.getEnchantmentTagList();
		for(int i = 0; i < list.tagCount(); ++i)
		{
			NBTTagCompound compound = list.getCompoundTagAt(i);
			short id = compound.getShort("id");
			short lvl = compound.getShort("lvl");
			Enchantment ench = Enchantment.getEnchantmentById(id);
			rarity += this.getEnchRarity(ench, lvl);
		}
		if(this.isCursed(stack))
		{
			rarity *= 1.5F;
		}
		return rarity;
	}
	
	public final int getMaxEnchantmentRarity()
	{
		float maxRarity = 0;
		
		for(int i = 0; i < Short.MAX_VALUE; ++i)
		{
			Enchantment ench = Enchantment.getEnchantmentById(i);
			if(ench != null)
			{
				float rarity = this.getEnchRarity(ench, ench.getMaxLevel() + 1 - ench.getMinLevel());
				if(rarity > maxRarity)
				{
					maxRarity = rarity;
				}
			}
		}
		
		return (int)maxRarity;
	}
	
	public final float getEnchRarity(Enchantment ench, int lvl)
	{
		return (int)(100 * (float)lvl / ench.getWeight() / (ench.getMaxLevel() + 1 - ench.getMinLevel()));
	}
	
	public final int getMaxEnchantmentsRarity()
	{
		int maxRarity = this.getMaxEnchantmentRarity();
		return maxRarity / 5;
	}
	
	@Override
	public EnumRarity getRarity(ItemStack stack)
	{
		int maxRarity = this.getMaxEnchantmentsRarity();
		int rarity = this.getRarityInt(stack);
		float f = (float)rarity / (float)maxRarity;
		int i = Math.max(0, Math.min(EnumRarity.values().length - 1, (int)Math.floor(Math.sqrt(f) * (EnumRarity.values().length - 1))));
		return EnumRarity.values()[i];
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public ModelResourceLocation getModel(ItemStack stack, EntityPlayer player, int useRemaining)
	{
		if(this.getMaterial2(stack) != null)
		{
			return new ModelResourceLocation(References.MODID + ":" + M.getId(this).id + "_" + this.getMaterial(stack).getName().toLowerCase() + "_" + this.getMaterial2(stack).getName().toLowerCase(), "inventory");
		}
		return new ModelResourceLocation(References.MODID + ":" + M.getId(this).id + "_" + this.getMaterial(stack).getName().toLowerCase(), "inventory");
	}
	
	@Override
	public String[] getTextureVariants(int meta)
	{
		ArrayList<String> texs = new ArrayList<String>();
		for(ArmorMaterial mat : this.materialsBase)
		{
			texs.add(References.MODID + ":" + M.getId(this).id + "_" + mat.getName().toLowerCase());
			for(ArmorMaterial mat2 : this.materialsDeco)
			{
				texs.add(References.MODID + ":" + M.getId(this).id + "_" + mat.getName().toLowerCase() + "_" + mat2.getName().toLowerCase());
			}
		}
		return texs.toArray(new String[texs.size()]);
	}
	
	@Override
	public String getItemStackDisplayName(ItemStack stack)
	{
		if(M.proxy.side() == Side.CLIENT)
		{
			this.setToHide(stack, Minecraft.getMinecraft().thePlayer);
		}
		String s = "";
		s += Stuff.Strings.capitalize(this.getMaterial(stack).getName()) + " ";
		s += super.getItemStackDisplayName(stack);
		if(this.getMaterial2(stack) != null)
		{
			s += " (" + Stuff.Strings.capitalize(this.getMaterial2(stack).getName()) + ")";
		}
		if(M.proxy.side() == Side.CLIENT)
		{
			if(this.isCursed(stack) && this.isKnown(stack, Minecraft.getMinecraft().thePlayer))
			{
				s = EnumChatFormatting.DARK_RED + "Cursed " + s;
			}
		}
		return s;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public ModelResourceLocation getModelLocation(ItemStack stack)
	{
		return this.getModel(stack, Minecraft.getMinecraft().thePlayer, Minecraft.getMinecraft().thePlayer.getItemInUseCount());
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack stack)
	{
		return false;
	}
	
	public final void setKnown(ItemStack stack, EntityPlayer player)
	{
		ItemEquipment.KNOWN_BY_PLAYERS.set(stack, player, true);
	}
	
	public final boolean isKnown(ItemStack stack, EntityPlayer player)
	{
		return ItemEquipment.KNOWN_BY_PLAYERS.get(stack, player);
	}
	
	public final boolean isCursed(ItemStack stack)
	{
		return ItemEquipment.CURSED.get(stack) > 0;
	}
}