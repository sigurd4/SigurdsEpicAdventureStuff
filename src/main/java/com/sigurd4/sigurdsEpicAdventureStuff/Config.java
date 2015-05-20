package com.sigurd4.sigurdsEpicAdventureStuff;

import java.util.ArrayList;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import com.google.common.collect.Lists;

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
			return Stuff.Strings.UnderscoresToCamelSpaces(super.toString());
		}
	}
	
	public static final ConfigEntryInt maxEffects = new ConfigEntryInt(3, 1, 100, "maxEffects", ConfigEntryCategory.MYSTERY_POTIONS, "How many effects a mystery potion can have at maximum.")
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
	public static final ConfigEntryInt tries = new ConfigEntryInt(6, 1, 100, "tries", ConfigEntryCategory.MYSTERY_POTIONS, "How many attempts that will be made to give a mystery potion its effects.");
	public static final ConfigEntryBoolean slashMultiple = new ConfigEntryBoolean(true, "slashMultiple", ConfigEntryCategory.SPECIAL_SWORDS, "Wether or not the special swords should be able to slash multiple enemies at once.");
	public static final ConfigEntryFloat slashLenght = new ConfigEntryFloat(4, 0.1F, 20, "slashLenght", ConfigEntryCategory.SPECIAL_SWORDS, "Multiplier for the lenght of the sword-slash.");

	public abstract static class ConfigEntry<T>
	{
		public final T defaultValue;
		public final String name;
		public final ConfigEntryCategory category;
		public final String description;

		protected T value;

		public ConfigEntry(T defaultValue, String name, ConfigEntryCategory category, String description)
		{
			this.defaultValue = this.value = defaultValue;
			this.name = name;
			this.category = category;
			this.description = description;
			entries.add(this);
		}

		public final void set(Configuration config)
		{
			T newValue = load(config);
			if(newValue != null)
			{
				value = newValue;
			}
		}

		public final T get()
		{
			return value != null ? valid(value) : valid(defaultValue);
		}

		protected abstract T load(Configuration config);

		protected abstract T valid(T value);
	}

	public static class ConfigEntryInt extends ConfigEntry<Integer>
	{
		public final int minValue;
		public final int maxValue;

		public ConfigEntryInt(int defaultValue, int minValue, int maxValue, String name, ConfigEntryCategory category, String description)
		{
			super((Integer)defaultValue, name, category, description);
			this.minValue = minValue;
			this.maxValue = maxValue;
		}

		protected Integer load(Configuration config)
		{
			return config.getInt(name, category.toString(), defaultValue, minValue, maxValue, description);
		}

		protected Integer valid(Integer value)
		{
			if(value > maxValue)
			{
				value = maxValue;
			}
			if(value < minValue)
			{
				value = minValue;
			}
			return value;
		}
	}

	public static abstract class ConfigEntryEnum<E extends Enum> extends ConfigEntry<E>
	{
		public ConfigEntryEnum(E defaultValue, String name, ConfigEntryCategory category, String description)
		{
			super(defaultValue, name, category, description);
		}

		protected E load(Configuration config)
		{
			return values()[config.getInt(name, category.toString(), defaultValue.ordinal()-1, 0, values().length-1, description)];
		}

		protected E valid(E value)
		{
			return value;
		}
		
		protected abstract E[] values();
	}

	public static class ConfigEntryBoolean extends ConfigEntry<Boolean>
	{
		public ConfigEntryBoolean(boolean defaultValue, String name, ConfigEntryCategory category, String description)
		{
			super((Boolean)defaultValue, name, category, description);
		}

		protected Boolean load(Configuration config)
		{
			return config.getBoolean(name, category.toString(), defaultValue, description);
		}

		protected Boolean valid(Boolean value)
		{
			return value;
		}
	}

	public static class ConfigEntryFloat extends ConfigEntry<Float>
	{
		public final float minValue;
		public final float maxValue;

		public ConfigEntryFloat(float defaultValue, float minValue, float maxValue, String name, ConfigEntryCategory category, String description)
		{
			super((Float)defaultValue, name, category, description);
			this.minValue = minValue;
			this.maxValue = maxValue;
		}

		protected Float load(Configuration config)
		{
			return config.getFloat(name, category.toString(), defaultValue, minValue, maxValue, description);
		}

		protected Float valid(Float value)
		{
			if(value > maxValue)
			{
				value = maxValue;
			}
			if(value < minValue)
			{
				value = minValue;
			}
			return value;
		}
	}
}
