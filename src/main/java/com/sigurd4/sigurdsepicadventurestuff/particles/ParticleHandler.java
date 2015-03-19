package com.sigurd4.sigurdsEpicAdventureStuff.particles;

import java.util.ArrayList;

import com.sigurd4.sigurdsEpicAdventureStuff.M;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityCrit2FX;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.particle.EntityReddustFX;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

public class ParticleHandler
{
	private static ArrayList<P> particles = new ArrayList<P>();
	private static class P
	{
		public final EntityFX particle;
		public final EnumParticleTypes2 type;
		public int[] par;

		public P(EntityFX particle, EnumParticleTypes2 type, int[] par)
		{
			this.particle = particle;
			this.type = type;
			this.par = par;
		}
	}

	public static enum EnumParticleTypes2
	{
		CRIT_COLOURED()
		{
			@Override
			public EntityFX get(World world, double x, double y, double z, double mx, double my, double mz, int... par)
			{
				float r = 1;
				float g = 1;
				float b = 1;
				if(par.length >= 3)
				{
					r = (float)par[0]/100;
					g = (float)par[1]/100;
					b = (float)par[2]/100;
				}
				EntityCritColouredFX fx = new EntityCritColouredFX(world, x, y, z, mx, my, mz, r, g, b);
				return fx;
			}

			@Override
			public void update(P p)
			{

			}
		};

		private EnumParticleTypes2()
		{

		}

		public abstract EntityFX get(World world, double x, double y, double z, double mx, double my, double mz, int ... par);

		public abstract void update(P p);
	}

	@SideOnly(Side.CLIENT)
	public static EntityFX particle(EnumParticleTypes2 particleEnum, World world, boolean ignoreDistance, double x, double y, double z, double mx, double my, double mz, int ... par)
	{
		EntityFX particle = particleEnum.get(world, x, y, z, mx, my, mz, par);
		if(particle != null)
		{
			particle.prevPosX = particle.posX;
			particle.prevPosY = particle.posY;
			particle.prevPosZ = particle.posZ;
			particles.add(new P(particle, particleEnum, par));
			return spawnEntityFX(particle, ignoreDistance, x, y, z, mx, my, mz, par);
		}
		return null;
	}

	@SideOnly(Side.CLIENT)
	public static EntityFX spawnEntityFX(EntityFX particle, boolean ignoreDistance, double x, double y, double z, double mx, double my, double mz, int ... par)
	{
		Minecraft mc = Minecraft.getMinecraft();
		World world = M.proxy.world(0);
		if(mc != null && mc.getRenderViewEntity() != null && mc.effectRenderer != null)
		{
			int k = mc.gameSettings.particleSetting;

			if(k == 1 && world.rand.nextInt(3) == 0)
			{
				k = 2;
			}

			double d6 = mc.getRenderViewEntity().posX - x;
			double d7 = mc.getRenderViewEntity().posY - y;
			double d8 = mc.getRenderViewEntity().posZ - z;

			double r = 32;
			if(!(ignoreDistance || (d6 * d6 + d7 * d7 + d8 * d8 <= r*r && k <= 1)))
			{
				particle = null;
			}
			if(particle != null)
			{
				mc.effectRenderer.addEffect(particle);
				return particle;
			}
		}
		return null;
	}

	public static void update()
	{
		for(int i = 0; i < particles.size(); ++i)
		{
			P p = particles.get(i);
			if(p.particle == null || p.particle.isDead)
			{
				particles.remove(i);
				--i;
			}
			else
			{
				p.type.update(p);
			}
		}
	}
	
	public static void spawnCritColoured(World world, boolean ignoreDistance, double x, double y, double z, double mx, double my, double mz, float r, float g, float b)
	{
		if(world.isRemote)
		{
			particle(EnumParticleTypes2.CRIT_COLOURED, world, ignoreDistance, x, y, z, mx, my, mz, new int[]{(int)(r*100), (int)(g*100), (int)(b*100)});
		}
	}
}
