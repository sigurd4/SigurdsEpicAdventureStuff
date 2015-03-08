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
		MYSTERY_POTIONS;
		
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
}
