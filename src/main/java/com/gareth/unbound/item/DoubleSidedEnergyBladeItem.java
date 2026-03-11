package com.gareth.unbound.item;

import com.gareth.unbound.registry.ModSounds;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.item.consume.UseAction;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Rarity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Double-Sided Energy Blade - A staff with blades on both ends.
 *
 * Features:
 * - High base damage (15) but slower attack speed
 * - Right-click: Hold to charge, release to perform lunge-spin attack
 * - Lunge-spin: Dash 8-10 blocks forward, damaging all enemies within 4 block radius
 */
public class DoubleSidedEnergyBladeItem extends EnergyBladeItem {
	// Lunge-spin parameters
	private static final int CHARGE_TIME_TICKS = 30;      // 1.5 seconds to full charge
	private static final int MAX_USE_TIME = 72000;        // Standard max (same as bow)
	private static final double LUNGE_DISTANCE = 9.0;     // ~8-10 blocks
	private static final double LUNGE_SPEED = 1.5;        // Blocks per tick during lunge
	private static final double DAMAGE_RADIUS = 4.0;      // 4 block damage radius
	private static final float LUNGE_DAMAGE = 10.0f;      // Damage during lunge
	private static final int COOLDOWN_TICKS = 100;        // 5 second cooldown

	public DoubleSidedEnergyBladeItem(Settings settings, BladeConfig config) {
		super(settings, config);
	}

	@Override
	public ActionResult use(World world, PlayerEntity user, Hand hand) {
		ItemStack stack = user.getStackInHand(hand);
		user.setCurrentHand(hand);

		// Play charge start sound
		world.playSound(
			null,
			user.getX(), user.getY(), user.getZ(),
			SoundEvents.BLOCK_BEACON_ACTIVATE,
			SoundCategory.PLAYERS,
			0.5f,
			1.5f
		);

		return ActionResult.CONSUME;
	}

	@Override
	public boolean onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
		if (!(user instanceof PlayerEntity player)) {
			return false;
		}

		int chargeTime = getMaxUseTime(stack, user) - remainingUseTicks;

		// Must charge for minimum time
		if (chargeTime < CHARGE_TIME_TICKS) {
			// Play fizzle sound - not enough charge
			world.playSound(
				null,
				user.getX(), user.getY(), user.getZ(),
				SoundEvents.BLOCK_FIRE_EXTINGUISH,
				SoundCategory.PLAYERS,
				0.5f,
				1.2f
			);
			return false;
		}

		// Execute lunge-spin!
		if (world instanceof ServerWorld serverWorld) {
			executeLungeSpin(serverWorld, player, stack);
		}

		// Apply cooldown
		player.getItemCooldownManager().set(stack, COOLDOWN_TICKS);
		return true;
	}

	private void executeLungeSpin(ServerWorld world, PlayerEntity player, ItemStack stack) {
		Vec3d startPos = player.getEntityPos();
		Vec3d lookVec = player.getRotationVec(1.0f);
		Vec3d horizontalLook = new Vec3d(lookVec.x, 0, lookVec.z).normalize();

		// If looking straight up/down, use facing direction
		if (horizontalLook.lengthSquared() < 0.01) {
			float yaw = player.getYaw() * (float) (Math.PI / 180.0);
			horizontalLook = new Vec3d(-Math.sin(yaw), 0, Math.cos(yaw));
		}

		// Track entities we've already hit to avoid double-damage
		Set<UUID> hitEntities = new HashSet<>();

		// Play lunge sound
		world.playSound(
			null,
			player.getX(), player.getY(), player.getZ(),
			ModSounds.ENERGY_BLADE_SWING,
			SoundCategory.PLAYERS,
			1.5f,
			0.8f
		);

		// Execute lunge in steps
		double distanceTraveled = 0;
		Vec3d currentPos = startPos;
		int steps = (int) (LUNGE_DISTANCE / LUNGE_SPEED);

		for (int i = 0; i < steps && distanceTraveled < LUNGE_DISTANCE; i++) {
			// Calculate next position
			Vec3d nextPos = currentPos.add(horizontalLook.multiply(LUNGE_SPEED));

			// Check for wall collision
			if (world.getBlockState(player.getBlockPos().offset(player.getHorizontalFacing())).isSolidBlock(world, player.getBlockPos())) {
				break;
			}

			// Move player
			player.setPosition(nextPos.x, nextPos.y, nextPos.z);
			currentPos = nextPos;
			distanceTraveled += LUNGE_SPEED;

			// Damage entities in radius
			damageEntitiesInRadius(world, player, currentPos, hitEntities);

			// Spawn trail particles
			spawnLungeParticles(world, currentPos);
		}

		// Final position adjustments - apply some velocity for smooth finish
		player.setVelocity(horizontalLook.multiply(0.5));

		// Spawn impact particles at final position
		spawnImpactParticles(world, player);

		// Play impact sound
		world.playSound(
			null,
			player.getX(), player.getY(), player.getZ(),
			SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP,
			SoundCategory.PLAYERS,
			1.2f,
			0.9f
		);
	}

	private void damageEntitiesInRadius(ServerWorld world, PlayerEntity player, Vec3d center, Set<UUID> hitEntities) {
		Box damageBox = new Box(
			center.x - DAMAGE_RADIUS, center.y - 1, center.z - DAMAGE_RADIUS,
			center.x + DAMAGE_RADIUS, center.y + 2, center.z + DAMAGE_RADIUS
		);

		List<LivingEntity> entities = world.getEntitiesByClass(
			LivingEntity.class,
			damageBox,
			entity -> entity.isAlive() && entity != player && !hitEntities.contains(entity.getUuid())
		);

		for (LivingEntity entity : entities) {
			double dist = entity.getEntityPos().distanceTo(center);
			if (dist <= DAMAGE_RADIUS) {
				hitEntities.add(entity.getUuid());

				// Deal damage
				entity.damage(world, player.getDamageSources().playerAttack(player), LUNGE_DAMAGE);

				// Knockback perpendicular to lunge direction (swept aside)
				Vec3d toEntity = entity.getEntityPos().subtract(center).normalize();
				entity.takeKnockback(1.2, -toEntity.x, -toEntity.z);

				// Hit particles
				world.spawnParticles(
					new DustParticleEffect(config.particleColor(), 1.2f),
					entity.getX(), entity.getBodyY(0.5), entity.getZ(),
					8,
					0.3, 0.2, 0.3,
					0.05
				);

				// Hit sound
				world.playSound(
					null,
					entity.getX(), entity.getY(), entity.getZ(),
					ModSounds.ENERGY_BLADE_HIT,
					SoundCategory.PLAYERS,
					0.8f,
					1.1f
				);
			}
		}
	}

	private void spawnLungeParticles(ServerWorld world, Vec3d pos) {
		// Energy trail
		world.spawnParticles(
			new DustParticleEffect(config.particleColor(), 1.0f),
			pos.x, pos.y + 0.5, pos.z,
			5,
			0.5, 0.3, 0.5,
			0.02
		);

		// Spin effect particles in a circle
		for (int j = 0; j < 4; j++) {
			double angle = (j / 4.0) * Math.PI * 2;
			double px = pos.x + Math.cos(angle) * 1.5;
			double pz = pos.z + Math.sin(angle) * 1.5;
			world.spawnParticles(
				ParticleTypes.SWEEP_ATTACK,
				px, pos.y + 0.8, pz,
				1,
				0, 0, 0,
				0
			);
		}
	}

	private void spawnImpactParticles(ServerWorld world, PlayerEntity player) {
		world.spawnParticles(
			new DustParticleEffect(config.particleColor(), 1.5f),
			player.getX(), player.getY() + 0.5, player.getZ(),
			20,
			1.0, 0.5, 1.0,
			0.1
		);

		world.spawnParticles(
			ParticleTypes.FLAME,
			player.getX(), player.getY() + 0.5, player.getZ(),
			10,
			0.8, 0.3, 0.8,
			0.05
		);
	}

	@Override
	public int getMaxUseTime(ItemStack stack, LivingEntity user) {
		return MAX_USE_TIME;
	}

	@Override
	public UseAction getUseAction(ItemStack stack) {
		return UseAction.SPEAR; // Shows charging animation like trident
	}

	@Override
	public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
		// Spawn charging particles while holding
		if (world instanceof ServerWorld serverWorld) {
			int chargeTime = getMaxUseTime(stack, user) - remainingUseTicks;
			if (chargeTime > 0 && chargeTime % 5 == 0) {
				// Particles gather around player as charge builds
				double progress = Math.min(1.0, (double) chargeTime / CHARGE_TIME_TICKS);
				int particleCount = (int) (progress * 8) + 1;

				serverWorld.spawnParticles(
					new DustParticleEffect(config.particleColor(), 0.8f),
					user.getX(), user.getY() + 1, user.getZ(),
					particleCount,
					0.5, 0.5, 0.5,
					0.02
				);

				// Play charging sound at intervals
				if (chargeTime == CHARGE_TIME_TICKS) {
					// Fully charged sound
					world.playSound(
						null,
						user.getX(), user.getY(), user.getZ(),
						SoundEvents.BLOCK_BEACON_POWER_SELECT,
						SoundCategory.PLAYERS,
						1.0f,
						1.5f
					);
				}
			}
		}
	}

	/**
	 * Configuration for double-sided blades.
	 */
	public static BladeConfig redDouble() {
		return new BladeConfig(
			ToolMaterial.NETHERITE,
			15.0f,         // High damage
			-2.9f,         // Slower (staff is unwieldy)
			1.3,           // Strong knockback
			3.5,           // Large shockwave
			0.9,
			0xFF4422,      // Red/orange
			1.4f,
			ParticleTypes.FLAME,
			0.85f,         // Low pitch (powerful)
			Rarity.EPIC
		);
	}
}
