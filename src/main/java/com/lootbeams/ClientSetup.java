package com.lootbeams;

import com.lootbeams.compat.ApotheosisCompat;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.TridentItem;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderNameplateEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.EntityLeaveWorldEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(modid = LootBeams.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientSetup {

	public static void init(FMLClientSetupEvent e) {
		MinecraftForge.EVENT_BUS.addListener(ClientSetup::onRenderNameplate);
		MinecraftForge.EVENT_BUS.addListener(ClientSetup::onItemCreation);
		MinecraftForge.EVENT_BUS.addListener(ClientSetup::entityRemoval);
	}
	
	public static void onItemCreation(EntityJoinWorldEvent event) {
		if (event.getEntity() instanceof ItemEntity ie) {
			if (!LootBeamRenderer.TOOLTIP_CACHE.containsKey(ie)) {
				LootBeamRenderer.TOOLTIP_CACHE.put(ie, ie.getItem().getTooltipLines(null, TooltipFlag.Default.NORMAL));
			}
			if (!LootBeamRenderer.LIGHT_CACHE.contains(ie)) {
				LootBeamRenderer.LIGHT_CACHE.add(ie);
			}
		}
	}
	
	public static void entityRemoval(EntityLeaveWorldEvent event) {
		if (event.getEntity() instanceof ItemEntity ie) {
			LootBeamRenderer.TOOLTIP_CACHE.remove(ie);
			LootBeamRenderer.LIGHT_CACHE.remove(ie);
		}
	}
	
	public static int overrideLight(ItemEntity ie, int light) {
		if (Configuration.ALL_ITEMS.get()) {
			light = 15728640;
		} else {
			if (Configuration.ONLY_EQUIPMENT.get()) {
				if (ie.getItem().getItem() instanceof SwordItem || ie.getItem().getItem() instanceof TieredItem || ie.getItem().getItem() instanceof ArmorItem || ie.getItem().getItem() instanceof  ShieldItem || ie.getItem().getItem() instanceof BowItem || ie.getItem().getItem() instanceof CrossbowItem) {
					light = 15728640;
				}
			}
			if (Configuration.ONLY_RARE.get()) {
				if (ModList.get().isLoaded("apotheosis")) {
					if (ApotheosisCompat.isApotheosisItem(ie.getItem())) {
						if (!ApotheosisCompat.getRarityName(ie.getItem()).equals("common") || ie.getItem().getRarity() != Rarity.COMMON) {
							light = 15728640;
						}
					}
				} else {
					if (ie.getItem().getRarity() != Rarity.COMMON) {
						light = 15728640;
					}
				}
			}
			if (isItemInRegistryList(Configuration.SOUND_ONLY_WHITELIST.get(), ie.getItem().getItem())) {
				light = 15728640;
			}
		}
		return light;
	}
	
	public static void playDropSound(ItemEntity itemEntity) {
		if (!Configuration.SOUND.get()) return;
		if (Configuration.SOUND_ALL_ITEMS.get() && !isItemInRegistryList(Configuration.BLACKLIST.get(), itemEntity.getItem().getItem())) {
			itemEntity.level.playSound(null, itemEntity, LootBeams.LOOT_DROP.get(), SoundSource.AMBIENT, Configuration.SOUND_VOLUME.get().floatValue(), ((itemEntity.level.random.nextFloat() - itemEntity.level.random.nextFloat()) * 0.7F + 1.0F) * 2.0F);
		} else {
			if (Configuration.SOUND_ONLY_EQUIPMENT.get()) {
				List<Class<? extends Item>> equipmentClasses = Arrays.asList(SwordItem.class, TieredItem.class, ArmorItem.class, ShieldItem.class, BowItem.class, CrossbowItem.class, TridentItem.class, ArrowItem.class, FishingRodItem.class);
				for (Class<? extends Item> item : equipmentClasses) {
					if (item.isAssignableFrom(itemEntity.getItem().getItem().getClass())) {
						itemEntity.level.playSound(null, itemEntity, LootBeams.LOOT_DROP.get(), SoundSource.AMBIENT, Configuration.SOUND_VOLUME.get().floatValue(), ((itemEntity.level.random.nextFloat() - itemEntity.level.random.nextFloat()) * 0.7F + 1.0F) * 2.0F);
						break;
					}
				}
			}
			if (Configuration.SOUND_ONLY_RARE.get()) {
				if (ModList.get().isLoaded("apotheosis")) {
					if (ApotheosisCompat.isApotheosisItem(itemEntity.getItem())) {
						if (!ApotheosisCompat.getRarityName(itemEntity.getItem()).equals("common") || itemEntity.getItem().getRarity() != Rarity.COMMON) {
							itemEntity.level.playSound(null, itemEntity, LootBeams.LOOT_DROP.get(), SoundSource.AMBIENT, Configuration.SOUND_VOLUME.get().floatValue(), ((itemEntity.level.random.nextFloat() - itemEntity.level.random.nextFloat()) * 0.7F + 1.0F) * 2.0F);
						}
					}
				} else {
					if (itemEntity.getItem().getRarity() != Rarity.COMMON) {
						itemEntity.level.playSound(null, itemEntity, LootBeams.LOOT_DROP.get(), SoundSource.AMBIENT, Configuration.SOUND_VOLUME.get().floatValue(), ((itemEntity.level.random.nextFloat() - itemEntity.level.random.nextFloat()) * 0.7F + 1.0F) * 2.0F);
					}
				}
			}
			if (isItemInRegistryList(Configuration.SOUND_ONLY_WHITELIST.get(), itemEntity.getItem().getItem())) {
				itemEntity.level.playSound(null, itemEntity, LootBeams.LOOT_DROP.get(), SoundSource.AMBIENT, Configuration.SOUND_VOLUME.get().floatValue(), ((itemEntity.level.random.nextFloat() - itemEntity.level.random.nextFloat()) * 0.7F + 1.0F) * 2.0F);
			}
		}
	}

	public static void onRenderNameplate(RenderNameplateEvent event) {
		if (event.getEntity() instanceof ItemEntity) {
			ItemEntity itemEntity = (ItemEntity) event.getEntity();
			if (Minecraft.getInstance().player.distanceToSqr(itemEntity) > Configuration.RENDER_DISTANCE.get() * Configuration.RENDER_DISTANCE.get()) {
				return;
			}


			boolean shouldRender = false;
			if (Configuration.ALL_ITEMS.get()) {
				shouldRender = true;
			} else {
				if (Configuration.ONLY_EQUIPMENT.get()) {
					List<Class<? extends Item>> equipmentClasses = Arrays.asList(SwordItem.class, TieredItem.class, ArmorItem.class, ShieldItem.class, BowItem.class, CrossbowItem.class, TridentItem.class, ArrowItem.class, FishingRodItem.class);
					for (Class<? extends Item> item : equipmentClasses) {
						if (item.isAssignableFrom(itemEntity.getItem().getItem().getClass())) {
							shouldRender = true;
							break;
						}
					}
				}

				if (Configuration.ONLY_RARE.get()) {
					shouldRender = LootBeamRenderer.compatRarityCheck(itemEntity, shouldRender);
				}

				if (isItemInRegistryList(Configuration.WHITELIST.get(), itemEntity.getItem().getItem())) {
					shouldRender = true;
				}
			}
			if (isItemInRegistryList(Configuration.BLACKLIST.get(), itemEntity.getItem().getItem())) {
				shouldRender = false;
			}

			if (shouldRender && itemEntity.isOnGround()) {
				LootBeamRenderer.renderLootBeam(event.getPoseStack(), event.getMultiBufferSource(), event.getPartialTick(), itemEntity.level.getGameTime(), itemEntity);
			}
		}
	}

	/**
	 * Checks if the given item is in the given list of registry names.
	 */
	private static boolean isItemInRegistryList(List<String> registryNames, Item item) {
		if (registryNames.size() > 0) {
			for (String id : registryNames.stream().filter((s) -> (!s.isEmpty())).collect(Collectors.toList())) {
				if (!id.contains(":")) {
					if (item.getRegistryName().getNamespace().equals(id)) {
						return true;
					}
				}
				ResourceLocation itemResource = ResourceLocation.tryParse(id);
				if (itemResource != null && ForgeRegistries.ITEMS.getValue(itemResource).asItem() == item.asItem()) {
					return true;
				}
			}
		}
		return false;
	}

}
