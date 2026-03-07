package com.gareth.unbound.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Rarity;
import net.minecraft.util.math.Box;

import java.util.List;

public final class EnergyBladeItem extends Item {
	private static final ToolMaterial MATERIAL = ToolMaterial.DIAMOND;

	private static final float BASE_ATTACK_DAMAGE = 5.0f;
	private static final float ATTACK_SPEED = -2.5f;

	private static final double EXTRA_KNOCKBACK_STRENGTH = 1.15;
	private static final double SHOCKWAVE_RADIUS = 2.75;
	private static final double SHOCKWAVE_MAX_STRENGTH = 0.7;

	public EnergyBladeItem(Item.Settings settings) {
		super(MATERIAL.applySwordSettings(settings.rarity(Rarity.RARE), BASE_ATTACK_DAMAGE, ATTACK_SPEED));
	}

	@Override
	public void postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		if (attacker.getWorld() instanceof ServerWorld serverWorld) {
			applyExtraKnockback(target, attacker);
			applyShockwave(serverWorld, target, attacker);
			spawnHitEffects(serverWorld, target);
		}

		super.postHit(stack, target, attacker);
	}

	private static void applyExtraKnockback(LivingEntity target, LivingEntity attacker) {
		double x = attacker.getX() - target.getX();
		double z = attacker.getZ() - target.getZ();
		target.takeKnockback(EXTRA_KNOCKBACK_STRENGTH, x, z);
	}

	private static void applyShockwave(ServerWorld world, LivingEntity target, LivingEntity attacker) {
		Box box = target.getBoundingBox().expand(SHOCKWAVE_RADIUS, 0.75, SHOCKWAVE_RADIUS);
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
		}
	}

	private static void spawnHitEffects(ServerWorld world, LivingEntity target) {
		world.spawnParticles(
			ParticleTypes.ELECTRIC_SPARK,
			target.getX(),
			target.getBodyY(0.5),
			target.getZ(),
			22,
			0.35,
			0.25,
			0.35,
			0.10
		);

		world.spawnParticles(
			ParticleTypes.SWEEP_ATTACK,
			target.getX(),
			target.getBodyY(0.5),
			target.getZ(),
			1,
			0.0,
			0.0,
			0.0,
			0.0
		);

		world.playSound(
			null,
			target.getX(),
			target.getY(),
			target.getZ(),
			SoundEvents.BLOCK_BEACON_POWER_SELECT,
			SoundCategory.PLAYERS,
			0.7f,
			1.85f
		);
	}
}
