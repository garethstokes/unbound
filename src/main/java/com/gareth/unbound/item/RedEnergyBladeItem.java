package com.gareth.unbound.item;

import net.minecraft.entity.LivingEntity;

public final class RedEnergyBladeItem extends EnergyBladeItem {
	private static final int FIRE_DURATION = 5; // 5 seconds of fire

	public RedEnergyBladeItem(Settings settings) {
		super(settings, BladeConfig.red());
	}

	@Override
	protected void applySpecialEffect(LivingEntity target, LivingEntity attacker) {
		target.setOnFireFor(FIRE_DURATION);
	}
}
