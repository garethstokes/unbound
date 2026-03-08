package com.gareth.unbound.registry;

import com.gareth.unbound.UnboundMod;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public final class ModSounds {
	public static final SoundEvent ENERGY_BLADE_SWING = register("energy_blade_swing");
	public static final SoundEvent ENERGY_BLADE_HIT = register("energy_blade_hit");

	public static void init() {
		// Static init is enough; this exists to make initialization explicit.
	}

	private static SoundEvent register(String path) {
		Identifier id = Identifier.of(UnboundMod.MOD_ID, path);
		return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
	}

	private ModSounds() {}
}

