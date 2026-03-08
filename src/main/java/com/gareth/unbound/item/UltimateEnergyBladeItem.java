package com.gareth.unbound.item;

import com.gareth.unbound.registry.ModSounds;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Rarity;
import net.minecraft.util.math.Box;

import java.util.List;

/**
 * The Ultimate Energy Blade combines all elemental powers:
 * - High damage with fast attack speed
 * - Poison + Life steal (Green)
 * - Fire damage (Red)
 * - Speed boost + Slow (Yellow)
 * - Wither + Darkness (Purple)
 * - Electric shockwave (Blue)
 */
public final class UltimateEnergyBladeItem extends Item {
	private static final ToolMaterial MATERIAL = ToolMaterial.NETHERITE;
	private static final float BASE_ATTACK_DAMAGE = 8.0f;
	private static final float ATTACK_SPEED = -2.0f;

	private static final double KNOCKBACK_STRENGTH = 1.5;
	private static final double SHOCKWAVE_RADIUS = 4.0;
	private static final double SHOCKWAVE_MAX_STRENGTH = 1.0;

	// Ultimate blade cuts through obsidian even faster
	private static final float OBSIDIAN_MINING_SPEED = 100.0f;

	// Particle colors for rainbow effect
	private static final int[] PARTICLE_COLORS = {
		0x22E6FF, // Blue
		0x22FF44, // Green
		0xFF4422, // Red
		0xFFEE22, // Yellow
		0xAA22FF  // Purple
	};

	public UltimateEnergyBladeItem(Item.Settings settings) {
		super(MATERIAL.applySwordSettings(settings.rarity(Rarity.EPIC), BASE_ATTACK_DAMAGE, ATTACK_SPEED));
	}

	@Override
	public boolean hasGlint(ItemStack stack) {
		return true; // Always show enchantment glint
	}

	@Override
	public float getMiningSpeed(ItemStack stack, BlockState state) {
		if (isObsidianLike(state)) {
			return OBSIDIAN_MINING_SPEED;
		}
		return super.getMiningSpeed(stack, state);
	}

	@Override
	public boolean isCorrectForDrops(ItemStack stack, BlockState state) {
		if (isObsidianLike(state)) {
			return true;
		}
		return super.isCorrectForDrops(stack, state);
	}

	private static boolean isObsidianLike(BlockState state) {
		return state.isOf(Blocks.OBSIDIAN)
			|| state.isOf(Blocks.CRYING_OBSIDIAN)
			|| state.isOf(Blocks.RESPAWN_ANCHOR);
	}

	@Override
	public void postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		if (attacker.getWorld() instanceof ServerWorld serverWorld) {
			applyAllEffects(target, attacker);
			applyShockwave(serverWorld, target, attacker);
			spawnRainbowHitEffects(serverWorld, target, attacker);
		}

		super.postHit(stack, target, attacker);
	}

	private void applyAllEffects(LivingEntity target, LivingEntity attacker) {
		// Green: Poison + Life steal
		target.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, 60, 1));
		attacker.heal(2.0f);

		// Red: Fire
		target.setOnFireFor(3);

		// Yellow: Speed (self) + Slow (target)
		attacker.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 40, 1));
		target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 40, 1));

		// Purple: Wither + Darkness
		target.addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, 40, 1));
		target.addStatusEffect(new StatusEffectInstance(StatusEffects.DARKNESS, 30, 0));

		// Blue: Extra knockback (handled separately)
		double x = attacker.getX() - target.getX();
		double z = attacker.getZ() - target.getZ();
		target.takeKnockback(KNOCKBACK_STRENGTH, x, z);
	}

	private void applyShockwave(ServerWorld world, LivingEntity target, LivingEntity attacker) {
		Box box = target.getBoundingBox().expand(SHOCKWAVE_RADIUS, 1.0, SHOCKWAVE_RADIUS);
		List<LivingEntity> entities = world.getEntitiesByClass(
			LivingEntity.class,
			box,
			entity -> entity.isAlive() && entity != target && entity != attacker
		);

		if (entities.isEmpty()) {
			return;
		}

		double centerX = target.getX();
		double centerZ = target.getZ();
		double radiusSq = SHOCKWAVE_RADIUS * SHOCKWAVE_RADIUS;

		for (LivingEntity entity : entities) {
			double dx = entity.getX() - centerX;
			double dz = entity.getZ() - centerZ;
			double distSq = dx * dx + dz * dz;
			if (distSq < 1.0e-6 || distSq > radiusSq) {
				continue;
			}

			double distance = Math.sqrt(distSq);
			double strength = SHOCKWAVE_MAX_STRENGTH * (1.0 - (distance / SHOCKWAVE_RADIUS));
			entity.takeKnockback(strength, centerX - entity.getX(), centerZ - entity.getZ());

			// Also apply minor effects to shockwave targets
			entity.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 20, 0));
		}
	}

	private void spawnRainbowHitEffects(ServerWorld world, LivingEntity target, LivingEntity attacker) {
		double x = target.getX();
		double y = target.getBodyY(0.55);
		double z = target.getZ();

		// Spawn particles in all colors
		for (int color : PARTICLE_COLORS) {
			world.spawnParticles(
				new DustParticleEffect(color, 1.0f),
				true,
				true,
				x,
				y,
				z,
				3,
				0.4,
				0.3,
				0.4,
				0.02
			);
		}

		// Electric sparks
		world.spawnParticles(
			ParticleTypes.ELECTRIC_SPARK,
			true,
			true,
			x,
			y,
			z,
			10,
			0.35,
			0.25,
			0.35,
			0.1
		);

		// Flame particles
		world.spawnParticles(
			ParticleTypes.FLAME,
			true,
			true,
			x,
			y,
			z,
			5,
			0.3,
			0.2,
			0.3,
			0.05
		);

		// Witch particles (purple magic)
		world.spawnParticles(
			ParticleTypes.WITCH,
			true,
			true,
			x,
			y,
			z,
			5,
			0.3,
			0.2,
			0.3,
			0.02
		);

		// Sweep attack
		world.spawnParticles(
			ParticleTypes.SWEEP_ATTACK,
			true,
			true,
			x,
			y,
			z,
			1,
			0.0,
			0.0,
			0.0,
			0.0
		);

		world.playSound(
			null,
			x,
			target.getY(),
			z,
			ModSounds.ENERGY_BLADE_HIT,
			SoundCategory.PLAYERS,
			1.2f,
			0.8f + (world.getRandom().nextFloat() * 0.4f)
		);

		// Backup sound in case custom sound fails
		world.playSound(
			null,
			x,
			target.getY(),
			z,
			SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP,
			SoundCategory.PLAYERS,
			1.0f,
			1.0f
		);
	}
}
