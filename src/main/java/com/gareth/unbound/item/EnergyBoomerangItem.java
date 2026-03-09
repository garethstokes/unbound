package com.gareth.unbound.item;

import com.gareth.unbound.entity.EnergyBoomerangEntity;
import com.gareth.unbound.registry.ModSounds;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Rarity;
import net.minecraft.world.World;

/**
 * Energy Boomerang item.
 *
 * Right-click to throw. The boomerang travels outward, hits enemies,
 * then returns to the player.
 */
public class EnergyBoomerangItem extends Item {
	private static final float THROW_SPEED = 1.5f;
	private static final int COOLDOWN_TICKS = 20; // 1 second

	public EnergyBoomerangItem(Settings settings) {
		super(settings.maxCount(1).rarity(Rarity.RARE));
	}

	@Override
	public ActionResult use(World world, PlayerEntity user, Hand hand) {
		ItemStack stack = user.getStackInHand(hand);

		// Play throw sound
		world.playSound(
			null,
			user.getX(), user.getY(), user.getZ(),
			ModSounds.ENERGY_BLADE_SWING,
			SoundCategory.PLAYERS,
			1.0f,
			0.9f
		);

		if (!world.isClient) {
			// Split off the item to throw (removes from inventory in survival, copies in creative)
			ItemStack thrownStack = stack.splitUnlessCreative(1, user);

			// Create and spawn the boomerang entity
			EnergyBoomerangEntity boomerang = new EnergyBoomerangEntity(world, user, thrownStack);
			boomerang.setVelocity(user, user.getPitch(), user.getYaw(), 0.0f, THROW_SPEED, 1.0f);

			// In creative mode, don't allow pickup (player keeps original)
			if (user.isCreative()) {
				boomerang.pickupType = EnergyBoomerangEntity.PickupPermission.CREATIVE_ONLY;
			}

			world.spawnEntity(boomerang);
		}

		// Apply cooldown
		user.getItemCooldownManager().set(stack, COOLDOWN_TICKS);

		// Stats
		user.incrementStat(Stats.USED.getOrCreateStat(this));

		return ActionResult.SUCCESS;
	}
}
