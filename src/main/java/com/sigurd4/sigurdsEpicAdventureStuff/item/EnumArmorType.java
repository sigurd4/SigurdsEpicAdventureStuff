package com.sigurd4.sigurdsEpicAdventureStuff.item;

public enum EnumArmorType
{
	HELMET,
	CHEST_PIECE,
	LEGGINGS,
	BOOTS;
	
	private EnumArmorType()
	{
		
	}
	
	public int getId()
	{
		return this.ordinal();
	}

	public int getSlot()
	{
		switch(this.getId())
		{
		case 0:
			return 4;
		case 1:
			return 3;
		case 2:
			return 2;
		case 3:
			return 1;
		}
		return 0;
	}
}
