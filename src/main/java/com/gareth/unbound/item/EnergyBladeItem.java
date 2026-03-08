package com.gareth.unbound.item;

import com.gareth.unbound.registry.ModSounds;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
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

	private static final int HIT_PARTICLE_COLOR = 0x22E6FF;
	private static final float HIT_PARTICLE_SCALE = 1.15f;

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
		double x = target.getX();
		double y = target.getBodyY(0.55);
		double z = target.getZ();

		world.spawnParticles(
			new DustParticleEffect(HIT_PARTICLE_COLOR, HIT_PARTICLE_SCALE),
			true,
			true,
			x,
			y,
			z,
			10,
			0.32,
			0.22,
			0.32,
			0.02
		);

		world.spawnParticles(
			ParticleTypes.ELECTRIC_SPARK,
			true,
			true,
			x,
			y,
			z,
			7,
			0.30,
			0.18,
			0.30,
			0.08
		);

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
			1.05f,
			1.10f + (world.getRandom().nextFloat() * 0.18f)
		);
	}
}
