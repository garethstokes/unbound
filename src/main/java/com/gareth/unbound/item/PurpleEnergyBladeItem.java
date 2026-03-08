package com.gareth.unbound.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

public final class PurpleEnergyBladeItem extends EnergyBladeItem {
	private static final int WITHER_DURATION = 60; // 3 seconds
	private static final int WITHER_AMPLIFIER = 1; // Wither II

	public PurpleEnergyBladeItem(Settings settings) {
		super(settings, BladeConfig.purple());
	}

	@Override
	protected void applySpecialEffect(LivingEntity target, LivingEntity attacker) {
		// Apply wither effect (dark energy)
		target.addStatusEffect(new StatusEffectInstance(
			StatusEffects.WITHER,
			WITHER_DURATION,
			WITHER_AMPLIFIER
		));
		// Apply darkness briefly (void energy)
		target.addStatusEffect(new StatusEffectInstance(
			StatusEffects.DARKNESS,
			40, // 2 seconds
			0
		));
	}
}
