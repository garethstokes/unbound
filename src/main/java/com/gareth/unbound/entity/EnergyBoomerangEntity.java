package com.gareth.unbound.entity;

import com.gareth.unbound.registry.ModEntities;
import com.gareth.unbound.registry.ModItems;
import com.gareth.unbound.registry.ModSounds;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FlyingItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Energy Boomerang projectile entity.
 *
 * Behavior:
 * - Thrown by player, travels outward
 * - Hits multiple entities along its path (piercing)
 * - After hitting a block or reaching max distance, returns to thrower
 * - Phases through blocks during return
 * - Returns to player inventory when caught
 */
public class EnergyBoomerangEntity extends PersistentProjectileEntity implements FlyingItemEntity {
	private static final float BASE_DAMAGE = 5.0f;
	private static final double MAX_DISTANCE = 20.0;
	private static final double RETURN_SPEED = 1.5;
	private static final int PARTICLE_COLOR = 0x22E6FF;
	private static final float CURVE_DEGREES_PER_TICK = 3.75f;

	private boolean returning = false;
	private Vec3d startPos;
	private final Set<UUID> hitEntities = new HashSet<>();

	public EnergyBoomerangEntity(EntityType<? extends EnergyBoomerangEntity> entityType, World world) {
		super(entityType, world);
		this.pickupType = PickupPermission.ALLOWED;
	}

	public EnergyBoomerangEntity(World world, LivingEntity owner, ItemStack stack) {
		super(ModEntities.ENERGY_BOOMERANG, owner, world, stack, null);
		this.startPos = owner.getPos();
		this.pickupType = PickupPermission.ALLOWED;
	}

	@Override
	protected void initDataTracker(net.minecraft.entity.data.DataTracker.Builder builder) {
		super.initDataTracker(builder);
	}

	@Override
	public void tick() {
		super.tick();

		if (getWorld().isClient) {
			return;
		}

		Entity owner = getOwner();

		// Spawn trail particles on server
		if (getWorld() instanceof ServerWorld serverWorld) {
			spawnTrailParticles(serverWorld);
		}

		// Check if we should start returning (max distance reached)
		if (!returning && startPos != null) {
			double distanceTraveled = getPos().distanceTo(startPos);
			if (distanceTraveled >= MAX_DISTANCE) {
				startReturning();
			}
		}

		// Apply curve to outward flight
		if (!returning) {
			applyCurve();
			updateFacingRotation();
		}

		// Handle return movement
		if (returning) {
			if (owner == null || !owner.isAlive()) {
				// Owner gone - drop as item
				dropAsItem();
				return;
			}

			// Calculate direction to owner
			Vec3d toOwner = owner.getEyePos().subtract(getPos());
			double distance = toOwner.length();

			// Parent's pickup system handles collection when player collides

			// Move toward owner
			Vec3d velocity = toOwner.normalize().multiply(RETURN_SPEED);
			setVelocity(velocity);

			// Update rotation to face direction of travel
			updateFacingRotation();

			// No block collision during return
			noClip = true;
		} else {
			noClip = false;
		}
	}

	private void startReturning() {
		returning = true;
		hitEntities.clear(); // Allow hitting entities again on return path

		if (getWorld() instanceof ServerWorld serverWorld) {
			// Play return sound
			serverWorld.playSound(
				null,
				getX(), getY(), getZ(),
				SoundEvents.ENTITY_ARROW_SHOOT,
				SoundCategory.PLAYERS,
				1.0f,
				1.5f
			);
		}
	}

	@Override
	protected void onEntityHit(EntityHitResult entityHitResult) {
		Entity target = entityHitResult.getEntity();

		// Don't hit owner or already-hit entities
		if (target == getOwner() || hitEntities.contains(target.getUuid())) {
			return;
		}

		hitEntities.add(target.getUuid());

		if (getWorld() instanceof ServerWorld serverWorld && target instanceof LivingEntity livingTarget) {
			// Deal damage
			DamageSource damageSource = getDamageSources().thrown(this, getOwner());
			livingTarget.damage(serverWorld, damageSource, BASE_DAMAGE);

			// Knockback perpendicular to flight path (feels like getting hit by spinning blade)
			Vec3d velocity = getVelocity().normalize();
			livingTarget.takeKnockback(0.5, -velocity.x, -velocity.z);

			// Spawn hit effects
			spawnHitParticles(serverWorld, livingTarget);
			serverWorld.playSound(
				null,
				livingTarget.getX(), livingTarget.getY(), livingTarget.getZ(),
				ModSounds.ENERGY_BLADE_HIT,
				SoundCategory.PLAYERS,
				1.0f,
				1.2f
			);
		}

		// Don't stop - pierce through (don't call super)
	}

	@Override
	protected void onBlockHit(BlockHitResult blockHitResult) {
		if (!returning) {
			// Hit a block while going out - start returning
			startReturning();

			if (getWorld() instanceof ServerWorld serverWorld) {
				// Spawn impact particles
				Vec3d pos = blockHitResult.getPos();
				serverWorld.spawnParticles(
					ParticleTypes.ELECTRIC_SPARK,
					pos.x, pos.y, pos.z,
					10,
					0.2, 0.2, 0.2,
					0.1
				);
			}
		}
		// During return, we're noClip so this shouldn't be called
	}

	private void dropAsItem() {
		discard();
	}

	private void spawnTrailParticles(ServerWorld serverWorld) {
		serverWorld.spawnParticles(
			new DustParticleEffect(PARTICLE_COLOR, 0.8f),
			getX(), getY(), getZ(),
			2,
			0.1, 0.1, 0.1,
			0.01
		);
	}

	private void spawnHitParticles(ServerWorld world, LivingEntity target) {
		world.spawnParticles(
			new DustParticleEffect(PARTICLE_COLOR, 1.0f),
			target.getX(), target.getBodyY(0.5), target.getZ(),
			8,
			0.3, 0.2, 0.3,
			0.02
		);
		world.spawnParticles(
			ParticleTypes.ELECTRIC_SPARK,
			target.getX(), target.getBodyY(0.5), target.getZ(),
			5,
			0.2, 0.15, 0.2,
			0.08
		);
	}

	private void updateFacingRotation() {
		Vec3d velocity = getVelocity();
		double horizontalSpeed = velocity.horizontalLength();
		this.setPitch((float) (MathHelper.atan2(velocity.y, horizontalSpeed) * (180.0 / Math.PI)));
		this.setYaw((float) (MathHelper.atan2(velocity.x, velocity.z) * (180.0 / Math.PI)));
	}

	/**
	 * Rotates the horizontal velocity clockwise to create a curved flight path.
	 */
	private void applyCurve() {
		Vec3d velocity = getVelocity();

		// Convert curve rate to radians
		double theta = Math.toRadians(CURVE_DEGREES_PER_TICK);
		double cos = Math.cos(theta);
		double sin = Math.sin(theta);

		// Rotate horizontal components clockwise (rightward from player's view)
		double x = velocity.x;
		double z = velocity.z;
		double newX = x * cos + z * sin;
		double newZ = -x * sin + z * cos;

		// Set new velocity, preserving vertical component
		setVelocity(newX, velocity.y, newZ);
	}

	@Override
	protected ItemStack getDefaultItemStack() {
		return new ItemStack(ModItems.ENERGY_BOOMERANG);
	}

	@Override
	public ItemStack getStack() {
		return getDefaultItemStack();
	}

	@Override
	protected SoundEvent getHitSound() {
		return ModSounds.ENERGY_BLADE_HIT;
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putBoolean("Returning", returning);
		if (startPos != null) {
			nbt.putDouble("StartX", startPos.x);
			nbt.putDouble("StartY", startPos.y);
			nbt.putDouble("StartZ", startPos.z);
		}
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		returning = nbt.getBoolean("Returning", false);
		if (nbt.contains("StartX")) {
			startPos = new Vec3d(
				nbt.getDouble("StartX", 0),
				nbt.getDouble("StartY", 0),
				nbt.getDouble("StartZ", 0)
			);
		}
	}

	public boolean isReturning() {
		return returning;
	}
}
