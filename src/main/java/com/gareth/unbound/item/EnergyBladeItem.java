package com.gareth.unbound.item;

import com.gareth.unbound.registry.ModSounds;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Rarity;
import net.minecraft.util.math.Box;

import java.util.List;

public class EnergyBladeItem extends Item {
	protected final BladeConfig config;

	// Energy blades cut through obsidian like butter
	private static final float OBSIDIAN_MINING_SPEED = 50.0f;

	public EnergyBladeItem(Item.Settings settings, BladeConfig config) {
		super(config.material().applySwordSettings(
			settings.rarity(config.rarity()),
			config.attackDamage(),
			config.attackSpeed()
		));
		this.config = config;
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
		if (attacker.getEntityWorld() instanceof ServerWorld serverWorld) {
			applyExtraKnockback(target, attacker);
			applyShockwave(serverWorld, target, attacker);
			applySpecialEffect(target, attacker);
			spawnHitEffects(serverWorld, target);
		}

		super.postHit(stack, target, attacker);
	}

	protected void applySpecialEffect(LivingEntity target, LivingEntity attacker) {
		// Override in subclasses for special effects
	}

	private void applyExtraKnockback(LivingEntity target, LivingEntity attacker) {
		double x = attacker.getX() - target.getX();
		double z = attacker.getZ() - target.getZ();
		target.takeKnockback(config.knockbackStrength(), x, z);
	}

	private void applyShockwave(ServerWorld world, LivingEntity target, LivingEntity attacker) {
		double radius = config.shockwaveRadius();
		Box box = target.getBoundingBox().expand(radius, 0.75, radius);
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
		double radiusSq = radius * radius;
		double maxStrength = config.shockwaveMaxStrength();

		for (LivingEntity entity : entities) {
			double dx = entity.getX() - centerX;
			double dz = entity.getZ() - centerZ;
			double distSq = dx * dx + dz * dz;
			if (distSq < 1.0e-6 || distSq > radiusSq) {
				continue;
			}

			double distance = Math.sqrt(distSq);
			double strength = maxStrength * (1.0 - (distance / radius));
			entity.takeKnockback(strength, centerX - entity.getX(), centerZ - entity.getZ());
		}
	}

	private void spawnHitEffects(ServerWorld world, LivingEntity target) {
		double x = target.getX();
		double y = target.getBodyY(0.55);
		double z = target.getZ();

		world.spawnParticles(
			new DustParticleEffect(config.particleColor(), config.particleScale()),
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

		ParticleEffect secondaryParticle = config.secondaryParticle();
		if (secondaryParticle != null) {
			world.spawnParticles(
				secondaryParticle,
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
		}

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
			config.soundPitch() + (world.getRandom().nextFloat() * 0.18f)
		);
	}

	public record BladeConfig(
		ToolMaterial material,
		float attackDamage,
		float attackSpeed,
		double knockbackStrength,
		double shockwaveRadius,
		double shockwaveMaxStrength,
		int particleColor,
		float particleScale,
		ParticleEffect secondaryParticle,
		float soundPitch,
		Rarity rarity
	) {
		public static BladeConfig blue() {
			return new BladeConfig(
				ToolMaterial.DIAMOND,
				5.0f,          // Balanced damage
				-2.5f,         // Balanced speed
				1.15,          // Standard knockback
				2.75,          // Standard shockwave
				0.7,
				0x22E6FF,      // Cyan
				1.15f,
				ParticleTypes.ELECTRIC_SPARK,
				1.10f,
				Rarity.RARE
			);
		}

		public static BladeConfig green() {
			return new BladeConfig(
				ToolMaterial.DIAMOND,
				4.0f,          // Lower damage (poison compensates)
				-2.4f,         // Slightly faster
				0.8,           // Less knockback (keep them close for poison)
				2.0,           // Smaller shockwave
				0.4,
				0x22FF44,      // Green
				1.0f,
				ParticleTypes.HAPPY_VILLAGER,
				1.20f,
				Rarity.RARE
			);
		}

		public static BladeConfig red() {
			return new BladeConfig(
				ToolMaterial.DIAMOND,
				7.0f,          // High damage
				-2.8f,         // Slower (heavy hits)
				1.5,           // Strong knockback
				3.5,           // Large shockwave
				0.9,
				0xFF4422,      // Red/orange
				1.3f,
				ParticleTypes.FLAME,
				0.90f,         // Lower pitch (powerful)
				Rarity.EPIC
			);
		}

		public static BladeConfig yellow() {
			return new BladeConfig(
				ToolMaterial.DIAMOND,
				3.5f,          // Low damage per hit
				-1.8f,         // Very fast attacks
				0.6,           // Light knockback
				2.0,           // Small shockwave
				0.5,
				0xFFEE22,      // Yellow
				0.9f,
				ParticleTypes.ELECTRIC_SPARK,
				1.40f,         // High pitch (fast)
				Rarity.RARE
			);
		}

		public static BladeConfig purple() {
			return new BladeConfig(
				ToolMaterial.NETHERITE,  // Stronger material
				6.0f,          // Good damage
				-2.6f,         // Moderate speed
				1.0,           // Standard knockback
				3.0,           // Medium shockwave
				0.8,
				0xAA22FF,      // Purple
				1.2f,
				ParticleTypes.WITCH,
				1.00f,
				Rarity.EPIC
			);
		}

		public static BladeConfig white() {
			return new BladeConfig(
				ToolMaterial.NETHERITE,  // Strong material (like purple)
				6.0f,          // Good damage (matches purple)
				-2.5f,         // Slightly faster than purple
				1.2,           // Good knockback
				3.0,           // Medium shockwave
				0.8,
				0xFFFFFF,      // Pure white
				1.3f,
				ParticleTypes.END_ROD,  // Radiant light particles
				1.15f,         // Higher pitch (pure/holy sound)
				Rarity.EPIC
			);
		}
	}
}
