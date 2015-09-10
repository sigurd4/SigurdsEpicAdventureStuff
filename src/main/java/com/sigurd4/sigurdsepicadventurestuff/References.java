package com.sigurd4.sigurdsEpicAdventureStuff;

import java.util.Random;
import java.util.UUID;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import scala.Char;

import com.sigurd4.sigurdsEpicAdventureStuff.item.EnumArmorType;
import com.sigurd4.sigurdsEpicAdventureStuff.item.ItemEquipment;
import com.sigurd4.sigurdsEpicAdventureStuff.item.ItemEquipmentArmor;
import com.sigurd4.sigurdsEpicAdventureStuff.item.ItemEquipmentBauble;

public class References
{
	public static final String MODID = "sigurdsEpicAdventureStuff";
	public static final String NAME = "Sigurd's Epic Adventure Stuff";
	public static final String VERSION = "1.0.1";
	public static final String CLIENT_PROXY_CLASS = "com.sigurd4.sigurdsEpicAdventureStuff.proxy.ProxyClient";
	public static final String SERVER_PROXY_CLASS = "com.sigurd4.sigurdsEpicAdventureStuff.proxy.ProxyServer";
	public static final String GUI_FACTORY_CLASS = "com.sigurd4.sigurdsEpicAdventureStuff.gui.GuiFactory";

	public static final long itemEquipmentUUIDMod = 49734985793845l;

	public static final UUID getItemEquipmentUUIDMod(ItemEquipment item, ItemStack stack, EnumAttributeModifier attribute)
	{
		int i = 0;
		if(item instanceof ItemEquipmentArmor)
		{
			i += ((ItemEquipmentArmor)item).armorType.ordinal();
		}
		else if(item instanceof ItemEquipmentBauble)
		{
			i += EnumArmorType.values().length;
			i += ((ItemEquipmentBauble)item).getBaubleType(stack).ordinal();
		}
		char[] name1 = attribute.name.toCharArray();
		long[] name2 = name1 != null ? new long[name1.length] : new long[0];
		for(int i2 = 0; i2 < name2.length && name1 != null && i2 < name1.length; ++i2)
		{
			name2[i2] = Character.getNumericValue(name1[i2]) + (long)i2 * Character.getNumericValue(Char.MaxValue());
		}
		long name3 = Stuff.Randomization.randSeed(53453456643l, name2).nextLong();
		UUID uuid = ItemEquipment.UUID.has(stack) ? ItemEquipment.UUID.get(stack) : new UUID(0, 0);
		return MathHelper.getRandomUuid(Stuff.Randomization.randSeed(References.itemEquipmentUUIDMod, name3, i, uuid.getMostSignificantBits(), uuid.getLeastSignificantBits()));
	}

	public static enum EnumAttributeModifier
	{
		MAX_HEALTH_0(SharedMonsterAttributes.maxHealth, 1, 4, 0),
		KNOCKBACK_RESISTANCE_0(SharedMonsterAttributes.knockbackResistance, 0.1F, 0.5F, 0, 8),
		MOVEMENT_SPEED_2(SharedMonsterAttributes.movementSpeed, 0.5F, 1.5F, 2, 8),
		ATTACK_DAMAGE_0(SharedMonsterAttributes.attackDamage, 1, 6, 0),
		ATTACK_DAMAGE_2(SharedMonsterAttributes.attackDamage, 0.5F, 3F, 2, 4);

		public final IAttribute attribute;
		public final String name;
		public final float minMod;
		public final float maxMod;
		public final int operation;
		public final int complexity;

		EnumAttributeModifier(IAttribute attribute, int minMod, int maxMod, int operation)
		{
			this(attribute, minMod, maxMod, operation, 1);
		}

		EnumAttributeModifier(IAttribute attribute, float minMod, float maxMod, int operation, int complexity)
		{
			this.attribute = attribute;
			this.name = "equipment_mod_" + this.name().toLowerCase();
			this.minMod = minMod;
			this.maxMod = maxMod;
			this.operation = operation;
			this.complexity = Math.max(1, complexity);
		}

		public AttributeModifier getModifier(Random rand, UUID uuid, ItemStack stack)
		{
			double value = this.minMod;
			if(this.maxMod > this.minMod)
			{
				value += rand.nextDouble() * (this.maxMod - this.minMod);
			}
			double value2 = 0;
			for(int i = 0; i < this.complexity; ++i)
			{
				double d0 = (double)i / (double)this.complexity;
				double d1 = Math.floor(value) + d0;
				if(Math.sqrt(Math.pow(value - value2, 2)) > Math.sqrt(Math.pow(value - d1, 2)))
				{
					value2 = d1;
				}
			}
			if(Math.sqrt(Math.pow(value - value2, 2)) > Math.sqrt(Math.pow(value - this.minMod, 2)))
			{
				value2 = this.minMod;
			}
			if(Math.sqrt(Math.pow(value - value2, 2)) > Math.sqrt(Math.pow(value - this.maxMod, 2)))
			{
				value2 = this.maxMod;
			}
			double chanceForNegative = 1F / 6;
			if(((ItemEquipment)stack.getItem()).isCursed(stack))
			{
				chanceForNegative *= 2;
			}
			if(rand.nextDouble() <= chanceForNegative)
			{
				if(this.operation == 2)
				{
					value -= 1;
				}
				value2 *= -1;
				float f = 8F / 10;
				value2 = this.minMod * (1 - f) + value2 * f;
				if(this.operation == 2)
				{
					value += 1;
				}
			}
			return new AttributeModifier(uuid, this.name + "_" + ItemEquipment.UUID.get(stack), value2, this.operation);
		}
	}
}