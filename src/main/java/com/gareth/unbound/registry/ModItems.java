package com.gareth.unbound.registry;

import com.gareth.unbound.UnboundMod;
import com.gareth.unbound.item.EnergyBladeItem;
import com.gareth.unbound.item.EnergyBoomerangItem;
import com.gareth.unbound.item.GreenEnergyBladeItem;
import com.gareth.unbound.item.PurpleEnergyBladeItem;
import com.gareth.unbound.item.RedEnergyBladeItem;
import com.gareth.unbound.item.UltimateEnergyBladeItem;
import com.gareth.unbound.item.WhiteEnergyBladeItem;
import com.gareth.unbound.item.YellowEnergyBladeItem;
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
	public static final Item ENERGY_BLADE_BLUE = register("energy_blade_blue",
		key -> new EnergyBladeItem(new Item.Settings().registryKey(key), EnergyBladeItem.BladeConfig.blue()));
	public static final Item ENERGY_BLADE_GREEN = register("energy_blade_green",
		key -> new GreenEnergyBladeItem(new Item.Settings().registryKey(key)));
	public static final Item ENERGY_BLADE_RED = register("energy_blade_red",
		key -> new RedEnergyBladeItem(new Item.Settings().registryKey(key)));
	public static final Item ENERGY_BLADE_YELLOW = register("energy_blade_yellow",
		key -> new YellowEnergyBladeItem(new Item.Settings().registryKey(key)));
	public static final Item ENERGY_BLADE_PURPLE = register("energy_blade_purple",
		key -> new PurpleEnergyBladeItem(new Item.Settings().registryKey(key)));
	public static final Item ENERGY_BLADE_WHITE = register("energy_blade_white",
		key -> new WhiteEnergyBladeItem(new Item.Settings().registryKey(key)));
	public static final Item ENERGY_BLADE_ULTIMATE = register("energy_blade_ultimate",
		key -> new UltimateEnergyBladeItem(new Item.Settings().registryKey(key)));
	public static final Item ENERGY_BOOMERANG = register("energy_boomerang",
		key -> new EnergyBoomerangItem(new Item.Settings().registryKey(key)));

	public static void init() {
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(entries -> {
			entries.addAfter(Items.NETHERITE_SWORD,
				ENERGY_BLADE_BLUE,
				ENERGY_BLADE_GREEN,
				ENERGY_BLADE_RED,
				ENERGY_BLADE_YELLOW,
				ENERGY_BLADE_PURPLE,
				ENERGY_BLADE_WHITE,
				ENERGY_BLADE_ULTIMATE,
				ENERGY_BOOMERANG
			);
		});
	}

	private static Item register(String path, Function<RegistryKey<Item>, Item> factory) {
		Identifier id = Identifier.of(UnboundMod.MOD_ID, path);
		RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, id);
		return Registry.register(Registries.ITEM, key, factory.apply(key));
	}

	private ModItems() {}
}
