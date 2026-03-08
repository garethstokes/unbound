package com.gareth.unbound.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

public final class YellowEnergyBladeItem extends EnergyBladeItem {
	private static final int SPEED_DURATION = 60; // 3 seconds
	private static final int SPEED_AMPLIFIER = 1; // Speed II

	public YellowEnergyBladeItem(Settings settings) {
		super(settings, BladeConfig.yellow());
	}

	@Override
	protected void applySpecialEffect(LivingEntity target, LivingEntity attacker) {
		// Grant attacker speed boost on hit
		attacker.addStatusEffect(new StatusEffectInstance(
			StatusEffects.SPEED,
			SPEED_DURATION,
			SPEED_AMPLIFIER
		));
		// Slow the target briefly
		target.addStatusEffect(new StatusEffectInstance(
			StatusEffects.SLOWNESS,
			40, // 2 seconds
			0   // Slowness I
		));
	}
}
