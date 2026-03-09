package com.gareth.unbound;

import com.gareth.unbound.registry.ModEntities;
import com.gareth.unbound.registry.ModItems;
import com.gareth.unbound.registry.ModSounds;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class UnboundMod implements ModInitializer {
	public static final String MOD_ID = "unbound";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Unbound initializing");
		ModSounds.init();
		ModEntities.init();
		ModItems.init();
	}
}
