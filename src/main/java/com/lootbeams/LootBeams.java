package com.lootbeams;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

@Mod(LootBeams.MODID)
public class LootBeams {

	public static final String MODID = "lootbeams";
	public static final Logger LOGGER = LogManager.getLogger();
	public static final List<ItemStack> CRASH_BLACKLIST = new ArrayList<>();
	public static final ResourceLocation LOOT_DROP = new ResourceLocation(MODID, "loot_drop");

	public LootBeams() {
		//ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DISPLAYTEST, () -> Pair.of(() -> FMLNetworkConstants.IGNORESERVERONLY, (a, b) -> true));
		ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, com.lootbeams.Configuration.CLIENT_CONFIG);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(com.lootbeams.ClientSetup::init);
	}
}
