package com.sigurd4.sigurdsEpicAdventureStuff.particles;

import net.minecraft.client.particle.EntityCrit2FX;
import net.minecraft.world.World;

public class EntityCritColouredFX extends EntityCrit2FX
{
	protected EntityCritColouredFX(World worldIn, double x, double y, double z, double mx, double my, double mz, float r, float g, float b)
	{
		this(worldIn, x, y, z, mx, my, mz, r, g, b, 1.0F);
	}

	protected EntityCritColouredFX(World worldIn, double x, double y, double z, double mx, double my, double mz, float r, float g, float b, float f)
	{
		super(worldIn, x, y, z, mx, my, mz, f);
		this.setRBGColorF(r, g, b);
	}
}
