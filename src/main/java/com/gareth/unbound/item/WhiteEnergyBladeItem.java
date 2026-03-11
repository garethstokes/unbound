package com.gareth.unbound.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

/**
 * White Energy Blade - Radiance themed.
 *
 * Special effects:
 * - Applies Glowing to targets (reveals enemies through walls)
 * - Grants brief Regeneration to the attacker (holy healing)
 * - Right-click: Grants Invisibility for a short time
 */
public final class WhiteEnergyBladeItem extends EnergyBladeItem {
	private static final int GLOWING_DURATION = 100;      // 5 seconds
	private static final int REGEN_DURATION = 40;         // 2 seconds
	private static final int REGEN_AMPLIFIER = 0;         // Regeneration I
	private static final int INVISIBILITY_DURATION = 100; // 5 seconds
	private static final int COOLDOWN_TICKS = 200;        // 10 second cooldown

	public WhiteEnergyBladeItem(Settings settings) {
		super(settings, BladeConfig.white());
	}

	@Override
	public ActionResult use(World world, PlayerEntity user, Hand hand) {
		ItemStack stack = user.getStackInHand(hand);

		// Apply invisibility
		user.addStatusEffect(new StatusEffectInstance(
			StatusEffects.INVISIBILITY,
			INVISIBILITY_DURATION,
			0
		));

		// Effects
		if (world instanceof ServerWorld serverWorld) {
			// Spawn particles where player was
			serverWorld.spawnParticles(
				ParticleTypes.END_ROD,
				user.getX(), user.getBodyY(0.5), user.getZ(),
				20,
				0.5, 0.5, 0.5,
				0.1
			);
		}

		// Play sound
		world.playSound(
			null,
			user.getX(), user.getY(), user.getZ(),
			SoundEvents.ENTITY_ILLUSIONER_CAST_SPELL,
			SoundCategory.PLAYERS,
			1.0f,
			1.2f
		);

		// Apply cooldown
		user.getItemCooldownManager().set(stack, COOLDOWN_TICKS);

		return ActionResult.SUCCESS;
	}

	@Override
	protected void applySpecialEffect(LivingEntity target, LivingEntity attacker) {
		// Apply Glowing to target (radiant light reveals enemies)
		target.addStatusEffect(new StatusEffectInstance(
			StatusEffects.GLOWING,
			GLOWING_DURATION,
			0
		));
		// Grant Regeneration to attacker (holy energy heals)
		attacker.addStatusEffect(new StatusEffectInstance(
			StatusEffects.REGENERATION,
			REGEN_DURATION,
			REGEN_AMPLIFIER
		));
	}
}
