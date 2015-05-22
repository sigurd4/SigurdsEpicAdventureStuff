package com.sigurd4.sigurdsEpicAdventureStuff.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sigurd4.sigurdsEpicAdventureStuff.Config;
import com.sigurd4.sigurdsEpicAdventureStuff.M;
import com.sigurd4.sigurdsEpicAdventureStuff.M.Id;
import com.sigurd4.sigurdsEpicAdventureStuff.Stuff;
import com.sigurd4.sigurdsEpicAdventureStuff.extended.ExtendedPlayer;
import com.sun.xml.internal.stream.Entity;

import java.awt.Color;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;

import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionHelper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.stats.StatList;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.server.FMLServerHandler;

public class ItemMysteryPotion extends Item implements IItemSubItems
{
	@SideOnly(Side.CLIENT)
	public enum EnumPotionColorMethod
	{
		RAINBOW_ANIMATED,
		RAINBOW,
		RANDOMIZED_TINT,
		RANDOMIZED_ALL
	}
	/**
	 * Contains a map from integers to the list of potion effects that potions with that damage value confer (to prevent
	 * recalculating it).
	 */
	private static final Map SUB_ITEMS_CACHE = Maps.newLinkedHashMap();

	public ItemMysteryPotion()
	{
		this.setMaxStackSize(1);
		this.setHasSubtypes(true);
		this.setNoRepair();
		String[] potionPrefixes = getPotionPrefixes();
		if(potionPrefixes != null)
		{
			this.setMaxDamage(potionPrefixes.length-1);
		}
	}

	public boolean isDamageable()
	{
		return false;
	}

	public boolean isDamaged(ItemStack stack)
	{
		return false;
	}

	/**
	 * Returns a list of potion effects for the specified itemstack.
	 */
	public ArrayList<PotionEffect> getEffects(ItemStack stack)
	{
		return getEffects(stack.getItemDamage());
	}

	/**
	 * Returns a list of effects for the specified potion damage value.
	 */
	public ArrayList<PotionEffect> getEffects(int meta)
	{
		World world = M.proxy.world(0);
		long seed = world.getSeed();

		ArrayList<Potion> possibleEffects = Lists.newArrayList();
		for(int i = 0; i < Potion.potionTypes.length; ++i)
		{
			Potion p = Potion.potionTypes[i];
			if(p != null && !possibleEffects.contains(p))
			{
				possibleEffects.add(p);
			}
		}

		ArrayList<PotionEffect> effects = Lists.newArrayList();
		int maxSize = Config.maxEffects.get();
		int tries = Config.tries.get();
		for(int i = 0; i < tries && possibleEffects.size() > 0; ++i)
		{
			Potion p = null;
			for(int i2 = 0; i2 < 100; ++i2)
			{
				p = possibleEffects.get(Stuff.Randomization.randSeed(seed, meta, i, 45235).nextInt(possibleEffects.size()));
			}
			if(p != null)
			{
				PotionEffect contains = null;
				int i2 = 0;
				for(; i2 < effects.size(); ++i2)
				{
					if(effects.get(i2) != null && effects.get(i2).getPotionID() == p.getId())
					{
						contains = effects.get(i2);
						break;
					}
				}
				if(contains == null)
				{
					if(effects.size() < maxSize)
					{
						effects.add(new PotionEffect(p.getId(), !p.isInstant() ? 30*20 : 0, 0));
					}
					else
					{
						if(Stuff.Randomization.randSeed(seed, meta, i, 12421).nextFloat() < 5/6)
						{
							++tries;
						}
					}
				}
				else
				{
					effects.remove(i2);
					if(Stuff.Randomization.randSeed(seed, meta, i, 35242).nextBoolean() && !contains.getIsPotionDurationMax() && !p.isInstant())
					{
						effects.add(i2, new PotionEffect(p.getId(), contains.getDuration(), contains.getAmplifier()+1));
					}
					else
					{
						effects.add(i2, new PotionEffect(p.getId(), contains.getDuration()+30*20, contains.getAmplifier()));
					}
				}
			}
		}
		for(int i = 0; i < effects.size(); ++i)
		{
			PotionEffect effect = effects.get(i);
			Potion potion = Potion.potionTypes[effect.getPotionID()];
			int duration = effect.getDuration();
			int amplifier = effect.getAmplifier();
			effects.remove(i);
			if(potion.isInstant())
			{
				duration = 0;
			}
			if(amplifier > 3)
			{
				amplifier = 3;
			}
			if(Stuff.Randomization.randSeed(seed, meta, i*i, 4352453).nextBoolean() || Stuff.Randomization.randSeed(seed, meta, i, 98320).nextBoolean())
			{
				if(Stuff.Randomization.randSeed(seed, meta, i*i, 543234).nextBoolean() && Stuff.Randomization.randSeed(seed, meta, i*i, 663452).nextBoolean())
				{
					duration /= Stuff.Randomization.randSeed(seed, meta, i*i, 753432).nextInt(2)+2;
				}
				else
				{
					if(potion.isBadEffect())
					{
						duration *= Stuff.Randomization.randSeed(seed, meta, i*i, 745426).nextInt(2)+1;
					}
					else
					{
						duration *= Stuff.Randomization.randSeed(seed, meta, i*i, 745426).nextInt(4)+1;
					}
				}
			}
			effects.add(i, new PotionEffect(potion.getId(), duration, amplifier));
		}

		return effects;
	}

	/**
	 * Called when the player finishes using this Item (E.g. finishes eating.). Not called when the player stops using
	 * the Item before the action is complete.
	 */
	public ItemStack onItemUseFinish(ItemStack stack, World world, EntityPlayer player)
	{
		if(!player.capabilities.isCreativeMode || stack.stackSize > 1)
		{
			--stack.stackSize;
		}

		ExtendedPlayer props = ExtendedPlayer.get(player);
		if(!world.isRemote)
		{
			List list = this.getEffects(stack);

			if(list != null)
			{
				Iterator iterator = list.iterator();

				while(iterator.hasNext())
				{
					PotionEffect potioneffect = (PotionEffect)iterator.next();
					player.addPotionEffect(new PotionEffect(potioneffect));
				}
			}
		}
		props.drinkPotion(stack.getItemDamage());

		player.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this)]);

		if(!player.capabilities.isCreativeMode)
		{
			if(stack.stackSize <= 0)
			{
				return new ItemStack(Items.glass_bottle);
			}

			player.inventory.addItemStackToInventory(new ItemStack(Items.glass_bottle));
		}

		return stack;
	}

	/**
	 * How long it takes to use or consume an item
	 */
	public int getMaxItemUseDuration(ItemStack stack)
	{
		return 32;
	}

	/**
	 * returns the action that specifies what animation to play when the items is being used
	 */
	public EnumAction getItemUseAction(ItemStack stack)
	{
		return EnumAction.DRINK;
	}

	/**
	 * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
	 */
	public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn)
	{
		playerIn.setItemInUse(itemStackIn, this.getMaxItemUseDuration(itemStackIn));
		return itemStackIn;
	}

	private HashMap<EnumPotionColorMethod, HashMap<Float, HashMap<World, HashMap<Integer, Color>>>> potionColorMap = new HashMap();
	@SideOnly(Side.CLIENT)
	public int getColorFromDamage(int meta)
	{
		World world = M.proxy.world(0);
		Color c = this.getColorFromDamage2(meta);
		if(!potionColorMap.containsKey(Config.potionColor.get()) || !potionColorMap.get(Config.potionColor.get()).containsKey((Float)Config.potionColorSimilarityThreshold.get()))
		{
			potionColorMap = new HashMap();
			potionColorMap.put(Config.potionColor.get(), new HashMap());
			potionColorMap.get(Config.potionColor.get()).put((Float)Config.potionColorSimilarityThreshold.get(), new HashMap());
		}
		if(!potionColorMap.get(Config.potionColor.get()).get((Float)Config.potionColorSimilarityThreshold.get()).containsKey(world))
		{
			potionColorMap.get(Config.potionColor.get()).get((Float)Config.potionColorSimilarityThreshold.get()).put(world, new HashMap());
		}
		potionColorMap.get(Config.potionColor.get()).get((Float)Config.potionColorSimilarityThreshold.get()).get(world).put((Integer)meta, c);
		return c.getRGB();
	}
	@SideOnly(Side.CLIENT)
	public Color getColorFromDamage2(int meta)
	{
		World world = M.proxy.world(0);
		long worldSeed = world.getSeed();
		long time = world.getTotalWorldTime();

		Color[] cs = new Color[20];
		if(Config.potionColor.get() == EnumPotionColorMethod.RANDOMIZED_TINT || Config.potionColor.get() == EnumPotionColorMethod.RANDOMIZED_ALL)
		{
			if(potionColorMap.containsKey(Config.potionColor.get()))
			{
				if(potionColorMap.get(Config.potionColor.get()).containsKey((Float)Config.potionColorSimilarityThreshold.get()))
				{
					if(potionColorMap.get(Config.potionColor.get()).get((Float)Config.potionColorSimilarityThreshold.get()).containsKey(world))
					{
						if(potionColorMap.get(Config.potionColor.get()).get((Float)Config.potionColorSimilarityThreshold.get()).get(world).containsKey((Integer)meta))
						{
							return potionColorMap.get(Config.potionColor.get()).get((Float)Config.potionColorSimilarityThreshold.get()).get(world).get((Integer)meta);
						}
					}
				}
			}

			Color oldC = null;
			loop:for(int seed = 0; ; ++seed)
			{
				Color c = getColorFromDamage(meta, Stuff.Randomization.randSeed(seed, meta, 35234324l, seed).nextLong());
				if(seed < cs.length)
				{
					cs[seed] = c;
				}
				if(oldC != null && oldC.equals(c))
				{
					break loop;
				}
				else
				{
					oldC = c;
				}
				lowerMetas:for(int i = 1; i < meta; ++i)
				{
					ArrayList<ItemStack> stacks = Lists.newArrayList();
					this.getSubItems2(this, this.getCreativeTab(), stacks);
					for(ItemStack stack : stacks)
					{
						if(stack.getItemDamage() == i)
						{
							if(Stuff.Colors.similarity(c, new Color(getColorFromDamage(i)), true, Config.potionColor.get() == EnumPotionColorMethod.RANDOMIZED_ALL, Config.potionColor.get() == EnumPotionColorMethod.RANDOMIZED_ALL, false) < 0.9)
							{
								continue loop;
							}
						}
						continue lowerMetas;
					}
				}
				if(Config.potionColor.get() == EnumPotionColorMethod.RANDOMIZED_ALL && (Stuff.Colors.getSaturation(c) < 0.3 || Stuff.Colors.getBrightness(c) < 0.3))
				{
					continue loop;
				}
				return c;
			}
		}
		Color c = cs.length > 0 ? cs[Stuff.Randomization.randSeed(meta, worldSeed, 39758234905l).nextInt(cs.length)] : null;
		if(c != null)
		{
			return c;
		}
		else
		{
			return this.getColorFromDamage(meta, 0);
		}
	}

	@SideOnly(Side.CLIENT)
	public Color getColorFromDamage(int meta, long seed)
	{
		World world = M.proxy.world(0);
		long worldSeed = world.getSeed();
		long time = world.getTotalWorldTime();

		Color c = null;
		switch(Config.potionColor.get())
		{
		case RAINBOW_ANIMATED:
		{
			c = Color.getHSBColor(((float)meta/this.getMaxDamage()/2)+((float)time/80), 1F, 1F);
			break;
		}
		case RAINBOW:
		{
			c = Color.getHSBColor(((float)meta/this.getMaxDamage()/2)+Stuff.Randomization.randSeed(worldSeed, 6576474524l, seed).nextFloat(), 1F, 1F);
			break;
		}
		case RANDOMIZED_TINT:
		{
			c = Color.getHSBColor(Stuff.Randomization.randSeed(worldSeed, meta, 5435436l, seed).nextFloat(), 1F, 1F);
			break;
		}
		case RANDOMIZED_ALL:
		{
			c = new Color(Stuff.Randomization.randSeed(worldSeed, meta, 4324324l, seed).nextFloat(), Stuff.Randomization.randSeed(worldSeed, meta, 547654734l, seed).nextFloat(), Stuff.Randomization.randSeed(worldSeed, meta, 3254234l, seed).nextFloat());
			break;
		}
		}
		return c;
	}



	@SideOnly(Side.CLIENT)
	public int getColorFromItemStack(ItemStack stack, int renderPass)
	{
		return renderPass > 0 ? 16777215 : this.getColorFromDamage(stack.getMetadata());
	}

	public String getItemStackDisplayName(ItemStack stack)
	{
		String s = "";

		List list = getEffects(stack);
		String s1;

		s1 = PotionHelper.getPotionPrefix(stack.getMetadata());
		return StatCollector.translateToLocal(s1).trim() + " " + super.getItemStackDisplayName(stack);
	}

	@SideOnly(Side.CLIENT)
	public boolean isEffectInstant(int meta)
	{
		List list = this.getEffects(meta);

		if(list != null && !list.isEmpty())
		{
			Iterator iterator = list.iterator();
			PotionEffect potioneffect;

			do
			{
				if(!iterator.hasNext())
				{
					return false;
				}

				potioneffect = (PotionEffect)iterator.next();
			}
			while(!Potion.potionTypes[potioneffect.getPotionID()].isInstant());

			return true;
		}
		else
		{
			return false;
		}
	}

	/**
	 * allows items to add custom lines of information to the mouseover description
	 *  
	 * @param tooltip All lines to display in the Item's tooltip. This is a List of Strings.
	 * @param advanced Whether the setting "Advanced tooltips" is enabled
	 */
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean advanced)
	{
		ExtendedPlayer props = ExtendedPlayer.get(player);
		if(props.knowsPotion(stack.getItemDamage()))
		{
			List list1 = getEffects(stack);
			HashMultimap hashmultimap = HashMultimap.create();
			Iterator iterator1;

			if(list1 != null && !list1.isEmpty())
			{
				iterator1 = list1.iterator();

				while(iterator1.hasNext())
				{
					PotionEffect potioneffect = (PotionEffect)iterator1.next();
					String s1 = StatCollector.translateToLocal(potioneffect.getEffectName()).trim();
					Potion potion = Potion.potionTypes[potioneffect.getPotionID()];
					Map map = potion.getAttributeModifierMap();

					if(map != null && map.size() > 0)
					{
						Iterator iterator = map.entrySet().iterator();

						while(iterator.hasNext())
						{
							Entry entry = (Entry)iterator.next();
							AttributeModifier attributemodifier = (AttributeModifier)entry.getValue();
							AttributeModifier attributemodifier1 = new AttributeModifier(attributemodifier.getName(), potion.getAttributeModifierAmount(potioneffect.getAmplifier(), attributemodifier), attributemodifier.getOperation());
							hashmultimap.put(((IAttribute)entry.getKey()).getAttributeUnlocalizedName(), attributemodifier1);
						}
					}

					if(potioneffect.getAmplifier() > 0)
					{
						s1 = s1 + " " + StatCollector.translateToLocal("potion.potency." + potioneffect.getAmplifier()).trim();
					}

					if(potioneffect.getDuration() > 20)
					{
						s1 = s1 + " (" + Potion.getDurationString(potioneffect) + ")";
					}

					if(potion.isBadEffect())
					{
						tooltip.add(EnumChatFormatting.RED + s1);
					}
					else
					{
						tooltip.add(EnumChatFormatting.GRAY + s1);
					}
				}
			}
			else
			{
				String s = StatCollector.translateToLocal("potion.empty").trim();
				tooltip.add(EnumChatFormatting.GRAY + s);
			}

			if(!hashmultimap.isEmpty())
			{
				tooltip.add("");
				tooltip.add(EnumChatFormatting.DARK_PURPLE + StatCollector.translateToLocal("potion.effects.whenDrank"));
				iterator1 = hashmultimap.entries().iterator();

				while (iterator1.hasNext())
				{
					Entry entry1 = (Entry)iterator1.next();
					AttributeModifier attributemodifier2 = (AttributeModifier)entry1.getValue();
					double d0 = attributemodifier2.getAmount();
					double d1;

					if (attributemodifier2.getOperation() != 1 && attributemodifier2.getOperation() != 2)
					{
						d1 = attributemodifier2.getAmount();
					}
					else
					{
						d1 = attributemodifier2.getAmount() * 100.0D;
					}

					if (d0 > 0.0D)
					{
						tooltip.add(EnumChatFormatting.BLUE + StatCollector.translateToLocalFormatted("attribute.modifier.plus." + attributemodifier2.getOperation(), new Object[] {ItemStack.DECIMALFORMAT.format(d1), StatCollector.translateToLocal("attribute.name." + (String)entry1.getKey())}));
					}
					else if (d0 < 0.0D)
					{
						d1 *= -1.0D;
						tooltip.add(EnumChatFormatting.RED + StatCollector.translateToLocalFormatted("attribute.modifier.take." + attributemodifier2.getOperation(), new Object[] {ItemStack.DECIMALFORMAT.format(d1), StatCollector.translateToLocal("attribute.name." + (String)entry1.getKey())}));
					}
				}
			}
		}
		else
		{
			tooltip.add(EnumChatFormatting.GRAY + "???");
		}
	}

	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack stack)
	{
		List list = this.getEffects(stack);
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		return false;
		/*if(player != null)
		{
			EntityExtendedPlayer props = EntityExtendedPlayer.get(player);
			return props.knowsPotion(stack.getItemDamage()) || true;
		}
		return true;*/
	}

	/**
	 * returns a list of items with the same ID, but different meta (eg: dye returns 16 items)
	 *  
	 * @param subItems The List of sub-items. This is a List of ItemStacks.
	 */
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item item, CreativeTabs tab, List stacks)
	{
		if(stacks instanceof ArrayList<?>)
		{
			getSubItems2(item, tab, (ArrayList<ItemStack>)stacks);
		}
	}
	public void getSubItems2(Item item, CreativeTabs tab, ArrayList<ItemStack> stacks)
	{
		ArrayList<Integer> types = Lists.newArrayList();

		String[] potionPrefixes = getPotionPrefixes();
		if(potionPrefixes != null)
		{
			for(int i = 0; i < potionPrefixes.length; ++i)
			{
				boolean contains = false;
				if(potionPrefixes[i] != null)
				{
					for(int i2 = 0; i2 < types.size(); ++i2)
					{
						if(potionPrefixes[(int)types.get(i2)] != null)
						{
							if(potionPrefixes[i].contains(potionPrefixes[(int)types.get(i2)]) || i == (int)types.get(i2))
							{
								contains = true;
								break;
							}
						}
					}
					if(!contains)
					{
						types.add(i);
					}
				}
			}
		}
		for(int i = 0; i < types.size(); ++i)
		{
			stacks.add(new ItemStack(item, 1, types.get(i)*2));
		}
		if(stacks.size() <= 0)
		{
			stacks.add(new ItemStack(item, 1, 0));
		}
	}

	public String[] getPotionPrefixes()
	{
		String[] potionPrefixes = null;
		try
		{
			potionPrefixes = (String[])ReflectionHelper.getPrivateValue(PotionHelper.class, null, 17);
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}
		return potionPrefixes;
	}
}