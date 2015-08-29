package com.sigurd4.sigurdsEpicAdventureStuff;

import java.awt.Color;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumFacing.AxisDirection;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Timer;
import net.minecraft.util.Vec3;
import net.minecraft.util.Vec3i;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;
import com.mojang.realmsclient.gui.ChatFormatting;
import com.sigurd4.sigurdsEpicAdventureStuff.M.Id;
import com.sigurd4.sigurdsEpicAdventureStuff.item.IItemSubItems;

public class Stuff
{
	public static Random rand = new Random();
	public static Random randSeed = new Random();

	/** Random stuff **/
	//- sigurd4
	public static class Randomization
	{
		public static double r(double i)
		{
			return Randomization.r(i, Stuff.rand);
		}

		public static double r(double i, Random rand)
		{
			return rand.nextDouble() * i * 2 - i;
		}

		public static float r(float i, Random rand)
		{
			return rand.nextFloat() * i * 2 - i;
		}

		public static float r(float i)
		{
			return Randomization.r(i, Stuff.rand);
		}

		public static <T> T getRandom(List<T> es)
		{
			if(es.size() > 0)
			{
				return es.get(es.size() > 1 ? Stuff.rand.nextInt(es.size() - 1) : 0);
			}
			return null;
		}

		public static <T> T getRandom(T[] es)
		{
			return Randomization.getRandom(ArraysAndSuch.arrayToArrayList(es));
		}

		public static Random randSeed(long seed, long ... seeds)
		{
			for(int i = 0; i < seeds.length; ++i)
			{
				seed += Randomization.randSeed(seeds[i]).nextLong();
			}
			Stuff.randSeed.setSeed(seed);
			return Stuff.randSeed;
		}
	}

	/** Stuff with coordinates in a 3D room. */
	//- sigurd4
	public static class Coordinates3D
	{
		public static Vec3 mix(ArrayList<Vec3> a)
		{
			Vec3 pos = new Vec3(0, 0, 0);
			for(int i = 0; i < a.size(); ++i)
			{
				pos = Coordinates3D.add(pos, a.get(i));
			}
			pos = Coordinates3D.divide(pos, a.size());
			return pos;
		}

		public static Vec3 stabilize(Vec3 pos, double w)
		{
			double d = w / Coordinates3D.distance(pos);
			return new Vec3(pos.xCoord * d, pos.yCoord * d, pos.zCoord * d);
		}

		public static double distance(Vec3 pos1, Vec3 pos2)
		{
			return Coordinates3D.distance(Coordinates3D.subtract(pos1, pos2));
		}

		public static Vec3 subtract(Vec3 pos1, Vec3 pos2)
		{
			return new Vec3(pos1.xCoord - pos2.xCoord, pos1.yCoord - pos2.yCoord, pos1.zCoord - pos2.zCoord);
		}

		public static Vec3 add(Vec3 pos1, Vec3 pos2)
		{
			return new Vec3(pos1.xCoord + pos2.xCoord, pos1.yCoord + pos2.yCoord, pos1.zCoord + pos2.zCoord);
		}

		public static Vec3 divide(Vec3 pos, double d)
		{
			return Coordinates3D.multiply(pos, 1 / d);
		}

		public static Vec3 multiply(Vec3 pos, double d)
		{
			return new Vec3(pos.xCoord * d, pos.yCoord * d, pos.zCoord * d);
		}

		public static double distance(Vec3 pos)
		{
			return Math.sqrt(pos.xCoord * pos.xCoord + pos.yCoord * pos.yCoord + pos.zCoord * pos.zCoord);
		}

		public static void throwThing(Entity source, Entity object, double f)
		{
			object.setLocationAndAngles(source.posX, source.posY + source.getEyeHeight(), source.posZ, source.rotationYaw, source.rotationPitch);
			object.posX -= MathHelper.cos(object.rotationYaw / 180.0F * (float)Math.PI) * 0.16F;
			object.posY -= 0.10000000149011612D;
			object.posZ -= MathHelper.sin(object.rotationYaw / 180.0F * (float)Math.PI) * 0.16F;
			object.setPosition(object.posX, object.posY, object.posZ);

			object.motionX = -MathHelper.sin(object.rotationYaw / 180.0F * (float)Math.PI) * MathHelper.cos(object.rotationPitch / 180.0F * (float)Math.PI) * f;
			object.motionZ = MathHelper.cos(object.rotationYaw / 180.0F * (float)Math.PI) * MathHelper.cos(object.rotationPitch / 180.0F * (float)Math.PI) * f;

			object.motionY = -MathHelper.sin(object.rotationPitch / 180.0F * (float)Math.PI) * f;
		}

		public static Vec3 velocity(Entity entity)
		{
			if(entity == null)
			{
				return new Vec3(0, 0, 0);
			}
			return new Vec3(entity.motionX, entity.motionY, entity.motionZ);
		}

		public static void velocity(Entity entity, Vec3 vec)
		{
			if(entity == null || vec == null)
			{
				return;
			}
			entity.motionX = vec.xCoord;
			entity.motionY = vec.yCoord;
			entity.motionZ = vec.zCoord;
		}

		public static void bounce(Entity entity, EnumFacing sideHit, double bouncyness)
		{
			Coordinates3D.velocity(entity, Coordinates3D.bounce(Coordinates3D.velocity(entity), sideHit, bouncyness));
		}

		public static Vec3 bounce(Vec3 m, EnumFacing sideHit, double bouncyness)
		{
			if(sideHit == null)
			{
				return m;
			}
			double xCoord = m.xCoord;
			double yCoord = m.yCoord;
			double zCoord = m.zCoord;
			Axis a = sideHit.getAxis();
			switch(a)
			{
			case X:
			{
				xCoord *= -bouncyness;
				break;
			}
			case Y:
			{
				yCoord *= -bouncyness;
				break;
			}
			case Z:
			{
				zCoord *= -bouncyness;
				break;
			}
			}
			return new Vec3(xCoord, yCoord, zCoord);
		}

		public static Vec3i getVecFromAxis(Axis axis, AxisDirection direction)
		{
			int x = 0;
			int y = 0;
			int z = 0;

			switch(axis)
			{
			case X:
			{
				x = direction.getOffset();
				break;
			}
			case Y:
			{
				y = direction.getOffset();
				break;
			}
			case Z:
			{
				z = direction.getOffset();
				break;
			}
			}

			return new Vec3i(x, y, z);
		}

		public static Vec3 getVectorForRotation(float pitch, float yaw)
		{
			float f2 = MathHelper.cos(-yaw * 0.017453292F - (float)Math.PI);
			float f3 = MathHelper.sin(-yaw * 0.017453292F - (float)Math.PI);
			float f4 = -MathHelper.cos(-pitch * 0.017453292F);
			float f5 = MathHelper.sin(-pitch * 0.017453292F);
			return new Vec3(f3 * f4, f5, f2 * f4);
		}
		
		public static Vec3 middle(BlockPos pos)
		{
			return new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
		}
	}

	/** Compare entities. **/
	//- sigurd4
	public static class EntityComparison
	{
		public static boolean isEntitySmaller(Entity e1, Entity e2, float offset)
		{
			return EntityComparison.isEntityBigger(e2, e1, offset);
		}

		public static boolean isEntityBigger(Entity e1, Entity e2, float offset)
		{
			if(e1 != null && e2 != null)
			{
				float s1 = e1.height;
				if(e1.width > s1)
				{
					s1 = e1.width;
				}
				float s2 = e2.height;
				if(e2.width > s2)
				{
					s2 = e2.width;
				}
				return s1 + offset > s2;
			}
			return false;
		}
	}

	/** Things with arrays and lists etc. **/
	//- sigurd4
	public static class ArraysAndSuch
	{
		public static <T> ArrayList<T> hashMapToArrayList(HashMap<?, T> map)
		{
			ArrayList<T> a = new ArrayList<T>();
			Iterator<T> values = map.values().iterator();
			while(values.hasNext())
			{
				a.add(values.next());
			}
			return a;
		}

		public static <T> ArrayList<T> hashMapKeysToArrayList(HashMap<T, ?> map)
		{
			ArrayList<T> a = new ArrayList<T>();
			Iterator<T> keys = map.keySet().iterator();
			while(keys.hasNext())
			{
				a.add(keys.next());
			}
			return a;
		}

		public static <T> boolean has(T[] a, T o)
		{
			return ArraysAndSuch.has(ArraysAndSuch.arrayToArrayList(a), o);
		}

		public static <T> boolean has(ArrayList<T> a, T o)
		{
			for(int i = 0; i < a.size(); ++i)
			{
				if(a.get(i) == o)
				{
					return true;
				}
			}
			return false;
		}

		public static Object[] arrayListToArray(ArrayList<Object> al)
		{
			return ArraysAndSuch.arrayListToArray2(al, new Object[al.size()]);
		}

		public static <T> T[] arrayListToArray2(ArrayList<T> al, T[] a)
		{
			if(al.size() == a.length)
			{
				for(int i = 0; i < al.size(); ++i)
				{
					a[i] = al.get(i);
				}
			}
			return a;
		}

		public static <T> ArrayList<T> arrayToArrayList(T[] a)
		{
			ArrayList<T> al = new ArrayList<T>();
			for(int i = 0; i < a.length; ++i)
			{
				al.add(a[i]);
			}
			return al;
		}

		public static <T> T[] mixArrays(T[] a1, T[] a2)
		{
			ArrayList<T> al = new ArrayList<T>();
			al.addAll(ArraysAndSuch.arrayToArrayList(a1));
			al.addAll(ArraysAndSuch.arrayToArrayList(a2));
			return (T[])al.toArray();
		}

		public static <T> T[] addToArray(T[] a, T o)
		{
			ArrayList<T> al = new ArrayList<T>();
			al.addAll(ArraysAndSuch.arrayToArrayList(a));
			al.add(o);
			return (T[])al.toArray();
		}

		public static <T> boolean removeFromArrayList(ArrayList<T> a, T o)
		{
			for(int i = 0; i < a.size(); ++i)
			{
				if(a.get(i) == o)
				{
					a.remove(i);
					return true;
				}
			}
			return false;
		}

		public static <T> ArrayList<T> allExtending(ArrayList a, Class<T> c)
		{
			ArrayList<T> at = new ArrayList<T>();
			for(int i = 0; i < a.size(); ++i)
			{
				if(c.isInstance(a.get(i)))
				{
					at.add((T)a.get(i));
				}
			}
			return at;
		}
	}

	/** Get all entities within the area **/
	//- sigurd4
	public static class EntitiesInArea
	{
		public static HashMap<Entity, Vec3> hit = new HashMap<Entity, Vec3>();

		public static List<Entity> getEntitiesWithinRadius(Entity e, double r)
		{
			return EntitiesInArea.getEntitiesWithinRadius(e, r, true);
		}

		public static List<Entity> getEntitiesWithinRadius(Entity e, double r, boolean exclude)
		{
			if(e != null)
			{
				List<Entity> es = EntitiesInArea.getEntitiesWithinRadius(e.worldObj, new Vec3(e.posX, e.posY, e.posZ), r);
				for(int i = 0; i > es.size(); ++i)
				{
					Entity e2 = es.get(i);
					if(e2 == e)
					{
						es.remove(i);
						--i;
					}
				}
				return es;
			}
			return null;
		}

		public static Entity getRandomEntityWithinRadius(Entity e, double r, Random rand)
		{
			List<Entity> es = EntitiesInArea.getEntitiesWithinRadius(e, r);
			Entity e2 = Randomization.getRandom(es);
			return e2;
		}

		public static Entity getRandomEntityWithinCube(Entity e, double m, Random rand)
		{
			List<Entity> es = EntitiesInArea.getEntitiesWithinCube(e, m);
			Entity e2 = Randomization.getRandom(es);
			return e2;
		}

		public static List<Entity> getEntitiesWithinCube(Entity e, double m)
		{
			return EntitiesInArea.getEntitiesWithinCube(e, m, true);
		}

		public static List<Entity> getEntitiesWithinCube(Entity e, double m, boolean exclude)
		{
			if(e != null)
			{
				List<Entity> es = EntitiesInArea.getEntitiesWithinCube(e.worldObj, new Vec3(e.posX, e.posY, e.posZ), m);
				for(int i = 0; i > es.size(); ++i)
				{
					Entity e2 = es.get(i);
					if(e2 == e && exclude)
					{
						es.remove(i);
						--i;
					}
				}
				return es;
			}
			return null;
		}

		public static List<Entity> getEntitiesWithinRadius(World w, Vec3 v, double r)
		{
			List<Entity> es = EntitiesInArea.getEntitiesWithinCube(w, v, r);
			for(int i = 0; i > es.size(); ++i)
			{
				Entity e2 = es.get(i);
				if(e2.getDistance(v.xCoord, v.yCoord, v.zCoord) > r)
				{
					es.remove(i);
					--i;
				}
			}
			return es;
		}

		public static List<Entity> getEntitiesWithinCube(World w, Vec3 v, double m)
		{
			List<Entity> es = w.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(v.xCoord - m, v.yCoord - m, v.zCoord - m, v.xCoord + m, v.yCoord + m, v.zCoord + m));
			return es;
		}

		public static List<Entity> getEntitiesOnAxis(World w, Vec3 pos, Vec3 p2)
		{
			return EntitiesInArea.getEntitiesOnAxis(w, pos, p2, 0.04);
		}

		public static List<Entity> getEntitiesOnAxis(World w, Vec3 pos, Vec3 p2, double r)
		{
			ArrayList<Entity> es = new ArrayList<Entity>();

			double h = Math.max(r / 5, 3);
			double d = Coordinates3D.distance(pos, p2);
			int t = (int)Math.ceil(d / h);
			d = h / t;

			Vec3 axis = Coordinates3D.subtract(p2, pos);
			for(int i = 0; i < t; ++i)
			{
				List<Entity> es2 = EntitiesInArea.getEntitiesWithinRadius(w, Coordinates3D.add(Coordinates3D.multiply(axis, d * i), pos), r);
				for(int i2 = 0; i2 < es2.size(); ++i2)
				{
					EntitiesInArea.hit.put(es2.get(i2), Coordinates3D.add(Coordinates3D.multiply(axis, d * i), pos));
					es.addAll(es2);
				}
			}

			return es;
		}

		public static Entity getClosestEntity(List<Entity> es, Vec3 pos)
		{
			Entity e = null;
			for(int i = 0; i < es.size(); ++i)
			{
				if(e == null || e.getDistance(pos.xCoord, pos.yCoord, pos.zCoord) > es.get(i).getDistance(pos.xCoord, pos.yCoord, pos.zCoord))
				{
					;
				}
				{
					e = es.get(i);
				}
			}
			return e;
		}
	}

	/** Arrays of multiple numbers **/
	//- sigurd4
	public static class MathWithMultiple
	{
		public static double distance(double ... ds)
		{
			for(int i = 0; i < ds.length; ++i)
			{
				ds[i] *= ds[i];
			}
			return Math.sqrt(MathWithMultiple.addAll(ds));
		}

		public static double addAll(double ... ds)
		{
			double d = 0;
			for(int i = 0; i < ds.length; ++i)
			{
				d += ds[i];
			}
			return d;
		}

		public static double max(double ... ds)
		{
			double d = 0;
			for(int i = 0; i < ds.length; ++i)
			{
				if(ds[i] > d)
				{
					d = ds[i];
				}
			}
			return d;
		}

		public static double min(double ... ds)
		{
			double d = 0;
			boolean b = false;
			for(int i = 0; i < ds.length; ++i)
			{
				if(!b || ds[i] < d)
				{
					b = true;
					d = ds[i];
				}
			}
			return d;
		}
	}

	/** fun with hashmaps **/
	//- sigurd4
	public static class HashMapStuff
	{
		public static <K, V> K getKeyFromValue(HashMap<K, V> map, V value)
		{
			Iterator<K> keys = map.keySet().iterator();
			while(keys.hasNext())
			{
				K key = keys.next();
				if(map.get(key) == value)
				{
					return key;
				}
			}
			return null;
		}
	}

	public static class Strings
	{
		public static String UnderscoresToCamelSpaces(String s)
		{
			s = s.toLowerCase();
			ArrayList<Character> cs = Strings.toCharArrayList(s);
			for(int i = 0; i < cs.size(); ++i)
			{
				char c = cs.get(i);
				if(c == '_')
				{
					cs.remove(i);
					cs.set(i, Character.toUpperCase(cs.get(i)));
					--i;
				}
			}
			return Strings.fromCharArrayList(cs);
		}

		public static String removeFormatting(String s)
		{
			if(s == null)
			{
				return s;
			}
			s = new String(s);
			for(ChatFormatting cf : ChatFormatting.values())
			{
				s = s.replaceAll("" + cf, "");
			}
			return s;
		}

		public static String capitalize(String s)
		{
			if(s == null || s.length() <= 0 || StringUtils.isBlank(s))
			{
				return s;
			}
			ArrayList<Character> cs = Strings.toCharArrayList(s);
			for(int i = 0; i < cs.size(); ++i)
			{
				if(!StringUtils.isBlank("" + cs.get(i)))
				{
					cs.set(i, Character.toUpperCase(cs.get(i)));
					break;
				}
			}
			return Strings.fromCharArrayList(cs);
		}

		public static ArrayList<Character> toCharArrayList(String s)
		{
			ArrayList<Character> cs = Lists.newArrayList();
			for(int i = 0; i < s.length(); ++i)
			{
				cs.add(s.charAt(i));
			}
			return cs;
		}

		public static String fromCharArrayList(ArrayList<Character> cs)
		{
			String s = "";
			for(int i = 0; i < cs.size(); ++i)
			{
				char c = cs.get(i);
				s = s + c;
			}
			return s;
		}
	}

	public static class ItemStuff
	{
		public static ArrayList<WeightedRandomChestContent> getChestGens(Item item, ChestGenHooks chest, Random rand)
		{
			ArrayList<WeightedRandomChestContent> loot = new ArrayList<WeightedRandomChestContent>();
			if(item != null)
			{
				Id id = M.getId(item);
				if(id != null && id.visible && id.dungeonLoot && id.dungeonLootChance > 0)
				{
					ItemStack stack = new ItemStack(item);
					ArrayList<ItemStack> variants = Lists.newArrayList();
					if(item instanceof IItemSubItems)
					{
						((IItemSubItems)item).getSubItems2(item, null, variants);
					}
					for(int i = 0; i < variants.size(); ++i)
					{
						stack = variants.get(i);
						if(stack != null)
						{
							loot.add(new WeightedRandomChestContent(stack, id.dungeonLootMin, id.dungeonLootMax, id.dungeonLootChance));
						}
					}
					return loot;
				}
			}
			return loot;
		}
	}

	/** get hidden stuff **/
	public static class Reflection
	{
		public static boolean isPotionBadEffect(Potion potion)
		{
			try
			{
				return potion.isBadEffect();
			}
			catch(Throwable e)
			{
				try
				{
					return ReflectionHelper.getPrivateValue(Potion.class, potion, 36);
				}
				catch(Throwable e2)
				{
					e2.printStackTrace();
				}
				return false;
			}
		}

		@SideOnly(Side.CLIENT)
		public static Timer getTimer()
		{
			try
			{
				return ReflectionHelper.getPrivateValue(Minecraft.class, Minecraft.getMinecraft(), 17);
			}
			catch(Throwable e)
			{
				e.printStackTrace();
			}
			return new Timer(20);
		}

		private static ItemStack itemToRender = null;

		@SideOnly(Side.CLIENT)
		public static void setItemToRender(ItemStack stack)
		{
			Minecraft mc = Minecraft.getMinecraft();
			if(stack == null || mc.thePlayer.getHeldItem() != stack)
			{
				Reflection.itemToRender = stack;
				return;
			}
			return;
		}

		@SideOnly(Side.CLIENT)
		public static void setItemToRender2() throws IllegalArgumentException, IllegalAccessException
		{
			ItemStack stack = Reflection.itemToRender;

			Minecraft mc = Minecraft.getMinecraft();
			ItemRenderer ir = mc.getItemRenderer();

			if(stack == null || mc.thePlayer.getHeldItem() == null || !mc.thePlayer.getHeldItem().getIsItemStackEqual(stack))
			{
				return;
			}
			if(mc.thePlayer.getItemInUse() != null && stack.getIsItemStackEqual(mc.thePlayer.getItemInUse()) && stack != mc.thePlayer.getItemInUse())
			{
				stack = mc.thePlayer.getItemInUse();
			}

			Field[] fs = ItemRenderer.class.getDeclaredFields();
			Field f = null;
			int a = 3;
			if(true)
			{
				String s = fs[a].getGenericType().getTypeName();
				if(s.equals(ItemStack.class.getName()))
				{
					f = fs[a];
				}
			}
			for(int i = 0; i < fs.length; ++i)
			{
				String s = fs[i].getGenericType().getTypeName();
				if(s.equals(ItemStack.class.getName()))
				{
					f = fs[i];
				}
			}
			if(f != null)
			{
				f.setAccessible(true);
				if(f.get(ir) != null && ((ItemStack)f.get(ir)).getItem() == stack.getItem())
				{
					f.set(ir, stack);
				}
				else
				{
					boolean b = true;
				}
				f.setAccessible(false);
				return;
			}
			throw new IllegalArgumentException();
		}
	}

	/** color mixing and such **/
	public static class Colors
	{
		public static double similarity(Color c1, Color c2, boolean hue, boolean saturation, boolean brightness, boolean alpha)
		{
			double h = hue ? (double)(Colors.getHue(c1) - Colors.getHue(c2)) : 0;
			if(Colors.max(c1) - Colors.min(c1) == 0 || Colors.max(c2) - Colors.min(c2) == 0)
			{
				h = 0;
			}
			double s = saturation ? (double)(Colors.getSaturation(c1) - Colors.getSaturation(c2)) : 0;
			double b = brightness ? (double)(Colors.getBrightness(c1) - Colors.getBrightness(c2)) : 0;
			double a = alpha ? (double)(c1.getAlpha() - c2.getAlpha()) / 255 : 0;
			return h * h + s * s + b * b + a * a;
		}

		public static double getHue(Color c)
		{
			double d = Colors.max(c) - Colors.min(c);
			if(d == 0)
			{
				return 0; //undefined, but let's just make it simple, shall we...
			}
			else if(Colors.max(c) == Colors.red(c))
			{
				return Rotary.modularArithmetic(60 * ((Colors.green(c) - Colors.blue(c)) / d), 360) / 360;
			}
			else if(Colors.max(c) == Colors.green(c))
			{
				return Rotary.modularArithmetic(60 * ((Colors.blue(c) - Colors.red(c)) / d) + 120, 360) / 360;
			}
			else if(Colors.max(c) == Colors.blue(c))
			{
				return Rotary.modularArithmetic(60 * ((Colors.red(c) - Colors.green(c)) / d) + 240, 360) / 360;
			}
			return 0;
		}

		public static double getSaturation(Color c)
		{
			if(Colors.max(c) == 0)
			{
				return 0;
			}
			else
			{
				return (Colors.max(c) - Colors.min(c)) / Colors.max(c);
			}
		}

		public static double getBrightness(Color c)
		{
			return Colors.max(c);
		}

		public static double red(Color c)
		{
			return (double)c.getRed() / 255;
		}

		public static double green(Color c)
		{
			return (double)c.getGreen() / 255;
		}

		public static double blue(Color c)
		{
			return (double)c.getBlue() / 255;
		}

		public static double max(Color c)
		{
			return Stuff.MathWithMultiple.max(Colors.red(c), Colors.green(c), Colors.blue(c));
		}

		public static double min(Color c)
		{
			return Stuff.MathWithMultiple.min(Colors.red(c), Colors.green(c), Colors.blue(c));
		}
	}

	public static class Rotary
	{
		public static double modularArithmeticBipolar(double value, double modulus)
		{
			return Rotary.modularArithmetic(value + modulus / 2, modulus) - modulus / 2;
		}

		public static double modularArithmetic(double value, double modulus)
		{
			while(value > modulus)
			{
				value -= modulus;
			}
			while(value < 0)
			{
				value += modulus;
			}
			return value;
		}
	}
}
