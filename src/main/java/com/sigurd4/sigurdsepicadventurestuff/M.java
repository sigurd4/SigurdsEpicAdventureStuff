package com.sigurd4.sigurdsEpicAdventureStuff;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import baubles.api.BaubleType;

import com.sigurd4.sigurdsEpicAdventureStuff.Stuff.HashMapStuff;
import com.sigurd4.sigurdsEpicAdventureStuff.item.AItemForMod;
import com.sigurd4.sigurdsEpicAdventureStuff.item.EnumArmorMaterial2;
import com.sigurd4.sigurdsEpicAdventureStuff.item.IItemIdFrom;
import com.sigurd4.sigurdsEpicAdventureStuff.item.IItemTextureVariants;
import com.sigurd4.sigurdsEpicAdventureStuff.item.ItemEquipmentBauble;
import com.sigurd4.sigurdsEpicAdventureStuff.item.ItemMysteryPotion;
import com.sigurd4.sigurdsEpicAdventureStuff.item.ItemSpecialSwordCharge;
import com.sigurd4.sigurdsEpicAdventureStuff.particles.ParticleHandler;
import com.sigurd4.sigurdsEpicAdventureStuff.proxy.ProxyCommon;
import com.sigurd4.sigurdsEpicAdventureStuff.tabs.TabGeneric;

@Mod(modid = References.MODID, name = References.NAME, version = References.VERSION, guiFactory = References.GUI_FACTORY_CLASS)
public class M
{
	@Instance(References.MODID)
	public static M instance;

	public static SimpleNetworkWrapper network;

	private static final HashMap<Object, Id> ids = new HashMap<Object, Id>();
	public static final ArrayList<Id> idsToBeRegistered = new ArrayList<Id>();
	public static final HashMap<Object, CreativeTabs[]> creativeTabs = new HashMap<Object, CreativeTabs[]>();
	private static final HashMap<String, Boolean> isModLoaded = new HashMap<String, Boolean>();

	/** tabs **/
	public static TabGeneric tabCore = new TabGeneric("core");

	////ITEMS:
	
	//crafting ingredients
	public static final Item ruby = M.registerItem("ruby", new Item().setUnlocalizedName("ruby").setCreativeTab(CreativeTabs.tabMaterials), true, new String[] {"gemRuby"});
	public static final Item silver_ingot = M.registerItem("silver_ingot", new Item().setUnlocalizedName("ingotSilver").setCreativeTab(CreativeTabs.tabMaterials), true, new String[] {"ingotSilver"});
	
	//swords
	public static final ItemSpecialSwordCharge adventure_sword = M.registerItem("adventure_sword", (ItemSpecialSwordCharge)new ItemSpecialSwordCharge(7, 2.3F, 100, 2 * 20, 11 * 20)
	{
		@Override
		@SideOnly(Side.CLIENT)
		public void spawnSlashParticle(Vec3 look1, Vec3 look)
		{
			Minecraft mc = Minecraft.getMinecraft();
			mc.thePlayer.worldObj.spawnParticle(EnumParticleTypes.CRIT, true, mc.thePlayer.posX + look.xCoord, mc.thePlayer.posY + look.yCoord + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ + look.zCoord, look.xCoord / 10, look.yCoord / 10, look.zCoord / 10, new int[] {});
		}

		@Override
		public void onReleaseEarly(ItemStack stack, World world, EntityPlayer player, int timeLeft)
		{

		}

		@Override
		public void onReleaseCharged(ItemStack stack, World world, EntityPlayer player, int timeLeft)
		{
			this.doSpin(stack, world, player, 1F);
		}
	}.setUnlocalizedName("adventureSword").setCreativeTab(M.tabCore), false, new String[] {}, 1, 1, 1);
	public static final ItemSpecialSwordCharge sky_sword = M.registerItem("sky_sword", (ItemSpecialSwordCharge)new ItemSpecialSwordCharge(4, 3.5F, 100, 15, 8 * 20)
	{
		@Override
		@SideOnly(Side.CLIENT)
		public void spawnSlashParticle(Vec3 look1, Vec3 look)
		{
			Minecraft mc = Minecraft.getMinecraft();
			ParticleHandler.spawnCritColoured(mc.thePlayer.worldObj, true, mc.thePlayer.posX + look.xCoord, mc.thePlayer.posY + look.yCoord + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ + look.zCoord, look.xCoord / 10, look.yCoord / 10, look.zCoord / 10, 0.8F, 0.95F, 1.15F);
		}

		@Override
		public void onReleaseEarly(ItemStack stack, World world, EntityPlayer player, int timeLeft)
		{

		}

		@Override
		public void onReleaseCharged(ItemStack stack, World world, EntityPlayer player, int timeLeft)
		{
			this.doSpin(stack, world, player, 0.5F);
			if(player.motionY < 0)
			{
				player.motionY = 0;
			}
			player.addVelocity(0, 1.2F, 0);
			player.fallDistance = 0;
		}

		@Override
		public boolean onEntitySwing(EntityLivingBase entity, ItemStack stack)
		{
			float reach = 1.5F;
			Vec3 look = Stuff.Coordinates3D.getVectorForRotation(entity.rotationPitch, entity.rotationYaw);
			look = new Vec3(look.xCoord * reach, look.yCoord * reach, look.zCoord * reach);

			for(int i = 0; i < 2 && entity.worldObj.isRemote; ++i)
			{
				entity.worldObj.spawnParticle(EnumParticleTypes.CLOUD, true, entity.posX + look.xCoord * 2 + Stuff.Randomization.r(0.9), entity.posY + look.yCoord * 2 + entity.getEyeHeight() + Stuff.Randomization.r(0.9), entity.posZ + look.zCoord * 2 + Stuff.Randomization.r(0.9), 4 * look.xCoord / (10 + 20 * Stuff.rand.nextFloat()) + Stuff.Randomization.r(0.1), 4 * look.yCoord / (10 + 20 * Stuff.rand.nextFloat()) + Stuff.Randomization.r(0.1), 4 * look.zCoord / (10 + 20 * Stuff.rand.nextFloat()) + Stuff.Randomization.r(0.1), new int[] {});
			}
			return super.onEntitySwing(entity, stack);
		}
	}.setUnlocalizedName("skySword").setCreativeTab(M.tabCore), false, new String[] {}, 1, 1, 1);
	
	//generated loot
	public static final ItemEquipmentBauble equipment_ring = M.registerItem("ring", (ItemEquipmentBauble)new ItemEquipmentBauble(new ArmorMaterial[] {ArmorMaterial.IRON, ArmorMaterial.GOLD, EnumArmorMaterial2.SILVER}, new ArmorMaterial[] {ArmorMaterial.DIAMOND, EnumArmorMaterial2.EMERALD, EnumArmorMaterial2.ENDER_PEARL, EnumArmorMaterial2.PRISMARINE_CRYSTAL, EnumArmorMaterial2.QUARTZ, EnumArmorMaterial2.RUBY}, 0, 1F / 6F, 6, BaubleType.RING).setUnlocalizedName("generatedRing").setCreativeTab(M.tabCore), false, new String[] {}, 1, 1, 1);
	public static final ItemMysteryPotion mystery_potion = M.registerItem("mystery_potion", (ItemMysteryPotion)new ItemMysteryPotion().setUnlocalizedName("mysteryPotion").setCreativeTab(M.tabCore), false, new String[] {}, 1, 1, 1);

	////BLOCKS:

	public M()
	{
		
	}

	@SidedProxy(clientSide = References.CLIENT_PROXY_CLASS, serverSide = References.SERVER_PROXY_CLASS)
	public static ProxyCommon proxy;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		M.proxy.preInit(event);
	}

	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		M.proxy.init(event);
	}

	@EventHandler
	public void init(FMLPostInitializationEvent event)
	{
		M.proxy.postInit(event);
	}

	/** Register entity with egg **/
	/*public static void registerEntity(Class<? extends Entity> entityClass, String name, int entityID, int primaryColor, int secondaryColor)
	{
		EntityRegistry.registerGlobalEntityID(entityClass, name, entityID);
		ItemMonsterPlacer2.EntityList2.registerEntity(entityClass, entityID, name, primaryColor, secondaryColor);
	}*/

	/** Register entity without egg **/
	public static void registerEntityNoEgg(Class<? extends Entity> entityClass, String name, int entityID)
	{
		EntityRegistry.registerModEntity(entityClass, name, entityID, M.instance, 64, 1, true);
	}

	public static Iterator<Id> getIds()
	{
		return ((HashMap<Object, Id>)M.ids.clone()).values().iterator();
	}

	public static Id getId(Item item)
	{
		if(M.ids.containsKey(item))
		{
			return M.ids.get(item);
		}
		return null;
	}

	public static Id getId(Block block)
	{
		if(M.ids.containsKey(block))
		{
			return M.ids.get(block);
		}
		return null;
	}

	public static Object getItem(Id id)
	{
		if(M.ids.containsValue(id))
		{
			Object v = HashMapStuff.getKeyFromValue((HashMap<Object, Id>)M.ids.clone(), id);
			if(v instanceof Item || v instanceof Block)
			{
				return v;
			}
		}
		return null;
	}

	public static boolean hasId(Id id)
	{
		return M.ids.containsValue(id);
	}

	public static boolean hasItem(Item item)
	{
		return M.ids.containsKey(item);
	}

	public static boolean hasItem(Block block)
	{
		return M.ids.containsKey(block);
	}

	public static HashMap<Integer, ArrayList<String>> getTypes(Item item)
	{
		HashMap<Integer, ArrayList<String>> types = new HashMap();
		for(int meta = 0; meta < item.getMaxDamage() || meta == 0; ++meta)
		{
			types.put(meta, new ArrayList());
			if(item instanceof IItemTextureVariants)
			{
				String[] variants = ((IItemTextureVariants)item).getTextureVariants(meta);
				for(int i = 0; i < variants.length; ++i)
				{
					types.get(meta).add(variants[i].toLowerCase());
				}
			}
			if(types.get(meta).size() <= 0)
			{
				if(M.getId(item) != null)
				{
					Id id = M.getId(item);
					types.get(meta).add(id.mod + ":" + id.id);
				}
				else
				{
					types.get(meta).add("" + Item.itemRegistry.getNameForObject(item));
				}
			}
		}
		return types;
	}

	public static class Id
	{
		public final String id;
		public final String mod;
		public final String[] oreDictNames;
		public final boolean replacedIfAlreadyAnOreDict;

		public boolean shouldBeReplaced()
		{
			return this.oreDictNames.length <= 0 || !this.replacedIfAlreadyAnOreDict;
		};

		public boolean visible;
		public final boolean dungeonLoot;
		public final int dungeonLootMin;
		public final int dungeonLootMax;
		public final int dungeonLootChance;

		public Id(String id, String mod, boolean replacedIfAlreadyAnOreDict, String[] oreDictNames)
		{
			this.id = id;
			this.mod = mod;
			this.replacedIfAlreadyAnOreDict = replacedIfAlreadyAnOreDict;
			this.oreDictNames = oreDictNames;

			this.dungeonLoot = false;
			this.dungeonLootMin = 0;
			this.dungeonLootMax = 0;
			this.dungeonLootChance = 0;
		}

		private Id(String id, String mod, boolean replacedIfAlreadyAnOreDict, String[] oreDictNames, int dungeonLootMin, int dungeonLootMax, int dungeonLootChance)
		{
			this.id = id;
			this.mod = mod;
			this.replacedIfAlreadyAnOreDict = replacedIfAlreadyAnOreDict;
			this.oreDictNames = oreDictNames;

			dungeonLootMin = dungeonLootMin >= 1 ? dungeonLootMin : 1;
			dungeonLootMax = dungeonLootMax <= 64 ? dungeonLootMax : 64;
			dungeonLootMin = dungeonLootMin <= dungeonLootMax ? dungeonLootMin : dungeonLootMax;
			dungeonLootMax = dungeonLootMax >= dungeonLootMin ? dungeonLootMax : dungeonLootMin;

			this.dungeonLoot = true;
			this.dungeonLootMin = dungeonLootMin;
			this.dungeonLootMax = dungeonLootMax;
			this.dungeonLootChance = dungeonLootChance;
		}

		public Id(String id, boolean replacedIfAlreadyAnOreDict, String[] oreDictNames)
		{
			this(id, References.MODID, replacedIfAlreadyAnOreDict, oreDictNames);
		}
	}

	public static <T extends Item & IItemIdFrom> T registerItem(T item, boolean replacedIfAlreadyAnOreDict, String[] oreDictNames)
	{
		return M.registerItem(item.getId(), References.MODID, item, replacedIfAlreadyAnOreDict, oreDictNames);
	}

	public static <T extends Item> T registerItem(String id, T item, boolean replacedIfAlreadyAnOreDict, String[] oreDictNames)
	{
		return M.registerItem(id, References.MODID, item, replacedIfAlreadyAnOreDict, oreDictNames);
	}

	public static <T extends Item> T registerItem(String id, String modid, T item, boolean replacedIfAlreadyAnOreDict, String[] oreDictNames)
	{
		return M.registerItem(id, modid, item, replacedIfAlreadyAnOreDict, oreDictNames, 0, 0, 0);
	}

	public static <T extends Item & IItemIdFrom> T registerItem(T item, boolean replacedIfAlreadyAnOreDict, String[] oreDictNames, int min, int max, int chance)
	{
		return M.registerItem(item.getId(), References.MODID, item, replacedIfAlreadyAnOreDict, oreDictNames, min, max, chance);
	}

	public static <T extends Item> T registerItem(String id, T item, boolean replacedIfAlreadyAnOreDict, String[] oreDictNames, int min, int max, int chance)
	{
		return M.registerItem(id, References.MODID, item, replacedIfAlreadyAnOreDict, oreDictNames, min, max, chance);
	}

	public static <T extends Item> T registerItem(String id, String modid, T item, boolean replacedIfAlreadyAnOreDict, String[] oreDictNames, int min, int max, int chance)
	{
		if(!M.ids.containsKey(item) && !M.ids.containsValue(id))
		{
			Id ID = new Id(id, modid, replacedIfAlreadyAnOreDict, oreDictNames);
			if(chance > 0)
			{
				ID = new Id(id, modid, replacedIfAlreadyAnOreDict, oreDictNames, min, max, chance);
			}
			M.ids.put(item, ID);
			M.idsToBeRegistered.add(ID);
		}
		return item;
	}

	public static <T extends Block> T registerBlock(String id, T block, boolean replacedIfAlreadyAnOreDict, String[] oreDictNames)
	{
		return M.registerBlock(id, References.MODID, block, replacedIfAlreadyAnOreDict, oreDictNames);
	}

	public static <T extends Block> T registerBlock(String id, String modid, T block, boolean replacedIfAlreadyAnOreDict, String[] oreDictNames)
	{
		if(!M.ids.containsKey(block))
		{
			Id ID = new Id(id, modid, replacedIfAlreadyAnOreDict, oreDictNames);
			M.ids.put(block, ID);
			M.idsToBeRegistered.add(ID);
		}
		return block;
	}

	public static boolean visible(Item item)
	{
		Id id = M.getId(item);
		return M.visible(id);
	}

	public static boolean visible(Block block)
	{
		Id id = M.getId(block);
		return M.visible(id);
	}

	public static boolean visible(Id id)
	{
		if(id != null)
		{
			return id.visible;
		}
		return true;
	}

	public static boolean isModForItemLoaded(Object item)
	{
		if(item instanceof Block)
		{
			item = Item.getItemFromBlock((Block)item);
		}
		boolean b1 = false;
		Field field = null;
		try
		{
			Field[] fields = M.class.getDeclaredFields();
			for(Field field2 : fields)
			{
				boolean b = false;
				if(!field2.isAccessible())
				{
					b = true;
					field2.setAccessible(true);
				}
				Object item2 = field2.get(M.instance);
				if(item2 != null)
				{
					Class clazz = item.getClass();
					Class clazz2 = item2.getClass();
					if(clazz2.equals(clazz))
					{
						if(item2.equals(item))
						{
							field = field2;
							b1 = b;
						}
					}
				}
				if(b && !b1)
				{
					field2.setAccessible(false);
				}
				if(field != null)
				{
					break;
				}
			}
		}
		catch(Exception e)
		{
		}
		
		AItemForMod annotation = null;
		if(field != null)
		{
			if(field.isAnnotationPresent(AItemForMod.class))
			{
				annotation = field.getAnnotation(AItemForMod.class);
			}
		}
		if(annotation == null)
		{
			Class clazz = item.getClass();
			if(clazz.isAnnotationPresent(AItemForMod.class))
			{
				annotation = (AItemForMod)clazz.getAnnotation(AItemForMod.class);
			}
		}
		if(b1)
		{
			field.setAccessible(false);
		}
		if(annotation != null)
		{
			return M.isModForItemLoaded2(annotation);
		}
		else
		{
			return true;
		}
	}

	public static boolean isModForItemLoaded2(AItemForMod item)
	{
		for(String modid : item.modids())
		{
			if(!M.isModLoaded(modid))
			{
				return false;
			}
		}
		return true;
	}

	public static boolean isModLoaded(String modid)
	{
		if(M.isModLoaded.containsKey(modid))
		{
			return M.isModLoaded.get(modid);
		}
		boolean b = Loader.isModLoaded(modid);
		M.isModLoaded.put(modid, b);
		return b;
	}
}