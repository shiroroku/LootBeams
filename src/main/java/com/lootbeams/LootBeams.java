package com.lootbeams;

import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(LootBeams.MODID)
public class LootBeams {

	public static final String MODID = "lootbeams";

	public LootBeams() {
		ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Configuration.CLIENT_CONFIG);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientSetup::init);
	}
}
