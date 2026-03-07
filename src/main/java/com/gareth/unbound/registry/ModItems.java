package com.gareth.unbound.registry;

import com.gareth.unbound.UnboundMod;
import com.gareth.unbound.item.EnergyBladeItem;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.function.Function;

public final class ModItems {
	public static final Item ENERGY_BLADE = register("energy_blade", key -> new EnergyBladeItem(new Item.Settings().registryKey(key)));

	public static void init() {
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(entries -> entries.addAfter(Items.NETHERITE_SWORD, ENERGY_BLADE));
	}

	private static Item register(String path, Function<RegistryKey<Item>, Item> factory) {
		Identifier id = Identifier.of(UnboundMod.MOD_ID, path);
		RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, id);
		return Registry.register(Registries.ITEM, key, factory.apply(key));
	}

	private ModItems() {}
}
