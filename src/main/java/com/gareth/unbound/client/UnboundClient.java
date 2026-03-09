package com.gareth.unbound.client;

import com.gareth.unbound.item.EnergyBladeItem;
import com.gareth.unbound.item.UltimateEnergyBladeItem;
import com.gareth.unbound.registry.ModEntities;
import com.gareth.unbound.registry.ModSounds;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.fabricmc.fabric.api.event.client.player.ClientPreAttackCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.random.Random;

public final class UnboundClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ClientPreAttackCallback.EVENT.register(UnboundClient::onPreAttack);

		// Register entity renderers
		EntityRendererRegistry.register(ModEntities.ENERGY_BOOMERANG, FlyingItemEntityRenderer::new);
	}

	private static boolean onPreAttack(MinecraftClient client, ClientPlayerEntity player, int clickCount) {
		if (clickCount == 0 || client.world == null) {
			return false;
		}

		var item = player.getMainHandStack().getItem();
		if (!(item instanceof EnergyBladeItem || item instanceof UltimateEnergyBladeItem)) {
			return false;
		}

		Random random = client.world.getRandom();
		player.playSound(ModSounds.ENERGY_BLADE_SWING, 0.9f, 1.30f + (random.nextFloat() * 0.25f));
		return false;
	}
}

