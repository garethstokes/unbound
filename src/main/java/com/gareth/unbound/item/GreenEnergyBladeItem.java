package com.gareth.unbound.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

public final class GreenEnergyBladeItem extends EnergyBladeItem {
	private static final int POISON_DURATION = 100; // 5 seconds
	private static final int POISON_AMPLIFIER = 1;  // Poison II

	public GreenEnergyBladeItem(Settings settings) {
		super(settings, BladeConfig.green());
	}

	@Override
	protected void applySpecialEffect(LivingEntity target, LivingEntity attacker) {
		target.addStatusEffect(new StatusEffectInstance(
			StatusEffects.POISON,
			POISON_DURATION,
			POISON_AMPLIFIER
		));
		// Heal attacker slightly on hit (life steal)
		attacker.heal(1.0f);
	}
}
