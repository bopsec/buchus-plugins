package com.tzhaarhptracker.attackstyles;

public enum WeaponStyle
{
	MAGIC, RANGE, MELEE, CHINS(AoeStyle.BASIC), TRIDENTS, SCYTHES, DINHS, VENATOR_BOW(AoeStyle.VENATOR);

	AoeStyle aoeStyle;

	WeaponStyle(AoeStyle aoeStyle) {
		this.aoeStyle = aoeStyle;
	}

	WeaponStyle() {
		this.aoeStyle = null;
	}

	public AoeStyle getAoeStyle() {
		return aoeStyle;
	}
}
