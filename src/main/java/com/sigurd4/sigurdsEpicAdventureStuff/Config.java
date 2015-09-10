package com.sigurd4.sigurdsEpicAdventureStuff;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;

import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.relauncher.Side;

import com.google.common.collect.Lists;
import com.sigurd4.sigurdsEpicAdventureStuff.item.ItemMysteryPotion.EnumPotionColorMethod;

public class Config
{
	public static Configuration config = null;
	public static final ArrayList<ConfigEntry> entries = Lists.newArrayList();
	
	public enum ConfigEntryCategory
	{
		GENERAL
		{
			@Override
			public String toString()
			{
				return Configuration.CATEGORY_GENERAL;
			}
		},
		MYSTERY_POTIONS,
		SPECIAL_SWORDS;
		
		@Override
		public String toString()
		{
			return Stuff.Strings.underscoresToCamelSpaces(super.toString());
		}
	}
	
	public static final ConfigEntryInt maxEffects = new ConfigEntryInt(Side.SERVER, 3, 1, 100, "maxEffects", ConfigEntryCategory.MYSTERY_POTIONS, "How many effects a mystery potion can have at maximum.")
	{
		@Override
		protected Integer valid(Integer value)
		{
			int tries = Config.tries.get();
			if(value > tries)
			{
				value = tries;
			}
			return super.valid(value);
		}
	};
	public static final ConfigEntryInt tries = new ConfigEntryInt(Side.SERVER, 6, 1, 100, "tries", ConfigEntryCategory.MYSTERY_POTIONS, "How many attempts that will be made to give a mystery potion its effects.");
	public static final ConfigEntryEnum<EnumPotionColorMethod> potionColor = new ConfigEntryEnum<EnumPotionColorMethod>(Side.CLIENT, EnumPotionColorMethod.RANDOMIZED_ALL, "potionColor", ConfigEntryCategory.MYSTERY_POTIONS, "Determines what the potions should look like. [0=animated rainbow, 1=rainbow, 2=randomized tint, 3=all randomized]")
	{
		@Override
		protected EnumPotionColorMethod[] values()
		{
			return EnumPotionColorMethod.values();
		}
	};
	public static final ConfigEntryFloat potionColorSimilarityThreshold = new ConfigEntryFloat(Side.CLIENT, 0.5F, 0.0F, 1.0F, "potionColorSimilarityThreshold", ConfigEntryCategory.MYSTERY_POTIONS, "No colors will be any more similar to each other than this value is to 1.");
	public static final ConfigEntryBoolean slashMultiple = new ConfigEntryBoolean(Side.SERVER, true, "slashMultiple", ConfigEntryCategory.SPECIAL_SWORDS, "Wether or not the special swords should be able to slash multiple enemies at once.");
	public static final ConfigEntryFloat slashLenght = new ConfigEntryFloat(Side.SERVER, 4, 0.1F, 10, "slashLenght", ConfigEntryCategory.SPECIAL_SWORDS, "Multiplier for the lenght of the sword-slash.");
	
	public abstract static class ConfigEntry <T>
	{
		public final T defaultValue;
		public final String name;
		public final String unlocalizedName;
		public final ConfigEntryCategory category;
		public final String description;
		public final Side side;
		
		protected T value;
		protected T serverValue;
		
		public ConfigEntry(Side side, T defaultValue, String name, ConfigEntryCategory category, String description)
		{
			this.defaultValue = this.value = defaultValue;
			this.name = name;
			this.unlocalizedName = (side == Side.SERVER ? EnumChatFormatting.AQUA : EnumChatFormatting.GREEN) + "configEntry." + name + ".name";
			this.category = category;
			this.side = side;
			this.description = description + " [SIDE: " + side.name().toUpperCase() + "]";
			Config.entries.add(this);
		}
		
		public final void set(Configuration config)
		{
			if(this.side != Side.SERVER && M.proxy.side() == Side.SERVER)
			{
				return;
			}
			T newValue = this.load(config);
			if(newValue != null)
			{
				this.value = newValue;
			}
		}
		
		public final T get()
		{
			if(this.side == Side.SERVER && M.proxy.side() != Side.SERVER && this.serverValue != null)
			{
				return this.serverValue;
			}
			return this.value != null ? this.valid(this.value) : this.valid(this.defaultValue);
		}
		
		protected abstract T load(Configuration config);
		
		protected abstract T valid(T value);
		
		public abstract void fromBytes(ByteBuf buf);
		
		public abstract void toBytes(ByteBuf buf);
	}
	
	public static class ConfigEntryInt extends ConfigEntry<Integer>
	{
		public final int minValue;
		public final int maxValue;
		
		public ConfigEntryInt(Side side, int defaultValue, int minValue, int maxValue, String name, ConfigEntryCategory category, String description)
		{
			super(side, defaultValue, name, category, description);
			this.minValue = minValue;
			this.maxValue = maxValue;
		}
		
		@Override
		protected Integer load(Configuration config)
		{
			return config.getInt(this.name, this.category.toString(), this.defaultValue, this.minValue, this.maxValue, this.description, this.unlocalizedName);
		}
		
		@Override
		protected Integer valid(Integer value)
		{
			if(value > this.maxValue)
			{
				value = this.maxValue;
			}
			if(value < this.minValue)
			{
				value = this.minValue;
			}
			return value;
		}
		
		@Override
		public void fromBytes(ByteBuf buf)
		{
			int i = buf.readInt();
			this.serverValue = this.valid(i);
		}
		
		@Override
		public void toBytes(ByteBuf buf)
		{
			buf.writeInt(this.valid(this.value));
		}
	}
	
	public static abstract class ConfigEntryEnum <E extends Enum> extends ConfigEntry<E>
	{
		public ConfigEntryEnum(Side side, E defaultValue, String name, ConfigEntryCategory category, String description)
		{
			super(side, defaultValue, name, category, description);
		}
		
		@Override
		protected E load(Configuration config)
		{
			return this.values()[config.getInt(this.name, this.category.toString(), this.defaultValue.ordinal(), 0, this.values().length - 1, this.description, this.unlocalizedName)];
		}
		
		@Override
		protected E valid(E value)
		{
			return value;
		}
		
		@Override
		public void fromBytes(ByteBuf buf)
		{
			if(buf.readInt() >= 0 && buf.readInt() < this.values().length)
			{
				this.serverValue = this.valid(this.values()[buf.readInt()]);
			}
		}
		
		@Override
		public void toBytes(ByteBuf buf)
		{
			buf.writeInt(this.valid(this.value).ordinal());
		}
		
		protected abstract E[] values();
	}
	
	public static class ConfigEntryBoolean extends ConfigEntry<Boolean>
	{
		public ConfigEntryBoolean(Side side, boolean defaultValue, String name, ConfigEntryCategory category, String description)
		{
			super(side, defaultValue, name, category, description);
		}
		
		@Override
		protected Boolean load(Configuration config)
		{
			return config.getBoolean(this.name, this.category.toString(), this.defaultValue, this.description, this.unlocalizedName);
		}
		
		@Override
		protected Boolean valid(Boolean value)
		{
			return value;
		}
		
		@Override
		public void fromBytes(ByteBuf buf)
		{
			this.serverValue = this.valid(buf.readByte() > 0);
		}
		
		@Override
		public void toBytes(ByteBuf buf)
		{
			buf.writeByte(this.valid(this.value) ? (byte)1 : (byte)0);
		}
	}
	
	public static class ConfigEntryFloat extends ConfigEntry<Float>
	{
		public final float minValue;
		public final float maxValue;
		
		public ConfigEntryFloat(Side side, float defaultValue, float minValue, float maxValue, String name, ConfigEntryCategory category, String description)
		{
			super(side, defaultValue, name, category, description);
			this.minValue = minValue;
			this.maxValue = maxValue;
		}
		
		@Override
		protected Float load(Configuration config)
		{
			return config.getFloat(this.name, this.category.toString(), this.defaultValue, this.minValue, this.maxValue, this.description, this.unlocalizedName);
		}
		
		@Override
		protected Float valid(Float value)
		{
			if(value > this.maxValue)
			{
				value = this.maxValue;
			}
			if(value < this.minValue)
			{
				value = this.minValue;
			}
			return value;
		}
		
		@Override
		public void fromBytes(ByteBuf buf)
		{
			this.serverValue = this.valid(buf.readFloat());
		}
		
		@Override
		public void toBytes(ByteBuf buf)
		{
			buf.writeFloat(this.valid(this.value));
		}
	}
}
