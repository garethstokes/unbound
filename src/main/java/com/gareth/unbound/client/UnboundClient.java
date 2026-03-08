package com.gareth.unbound.client;

import com.gareth.unbound.item.EnergyBladeItem;
import com.gareth.unbound.registry.ModSounds;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.event.client.player.ClientPreAttackCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.random.Random;

public final class UnboundClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ClientPreAttackCallback.EVENT.register(UnboundClient::onPreAttack);
	}

	private static boolean onPreAttack(MinecraftClient client, ClientPlayerEntity player, int clickCount) {
		if (clickCount == 0 || client.world == null) {
			return false;
		}

		if (!(player.getMainHandStack().getItem() instanceof EnergyBladeItem)) {
			return false;
		}

		Random random = client.world.getRandom();
		player.playSound(ModSounds.ENERGY_BLADE_SWING, 0.9f, 1.30f + (random.nextFloat() * 0.25f));
		return false;
	}
}

