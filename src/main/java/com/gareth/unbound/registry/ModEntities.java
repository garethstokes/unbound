package com.gareth.unbound.registry;

import com.gareth.unbound.UnboundMod;
import com.gareth.unbound.entity.EnergyBoomerangEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public final class ModEntities {
	public static final EntityType<EnergyBoomerangEntity> ENERGY_BOOMERANG = register(
		"energy_boomerang",
		EntityType.Builder.<EnergyBoomerangEntity>create(EnergyBoomerangEntity::new, SpawnGroup.MISC)
			.dimensions(1.0f, 0.5f)
			.maxTrackingRange(64)
			.trackingTickInterval(10)
	);

	public static void init() {
		// Static init is enough; this exists to make initialization explicit.
	}

	private static <T extends net.minecraft.entity.Entity> EntityType<T> register(
			String path,
			EntityType.Builder<T> builder) {
		Identifier id = Identifier.of(UnboundMod.MOD_ID, path);
		RegistryKey<EntityType<?>> key = RegistryKey.of(RegistryKeys.ENTITY_TYPE, id);
		return Registry.register(Registries.ENTITY_TYPE, key, builder.build(key));
	}

	private ModEntities() {}
}
