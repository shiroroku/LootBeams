package com.lootbeams;

import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.*;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderNameplateEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Arrays;
import java.util.List;

@Mod.EventBusSubscriber(modid = LootBeams.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientSetup {

	public static void init(FMLClientSetupEvent e) {
		MinecraftForge.EVENT_BUS.addListener(ClientSetup::onRenderNameplate);
	}

	public static void onRenderNameplate(RenderNameplateEvent event) {
		if (event.getEntity() instanceof ItemEntity) {
			ItemEntity itemEntity = (ItemEntity) event.getEntity();

			boolean shouldRender = false;
			boolean renderAllItems = Configuration.ALL_ITEMS.get();
			boolean renderEquipment = Configuration.ONLY_EQUIPMENT.get();
			List<String> whitelist = Configuration.WHITELIST.get();
			List<String> blacklist = Configuration.BLACKLIST.get();

			if (renderAllItems) {
				shouldRender = true;
			} else {
				if (renderEquipment) {
					List<Class<? extends Item>> equipmentClasses = Arrays.asList(SwordItem.class, ToolItem.class, ArmorItem.class, ShieldItem.class, BowItem.class, CrossbowItem.class, TridentItem.class, ArrowItem.class, FishingRodItem.class);

					for (Class<? extends Item> item : equipmentClasses) {
						if (item.isAssignableFrom(itemEntity.getItem().getItem().getClass())) {
							shouldRender = true;
							break;
						}
					}
				}

				if (isItemInRegistryList(whitelist, itemEntity.getItem().getItem())) {
					shouldRender = true;
				}
			}

			if (isItemInRegistryList(blacklist, itemEntity.getItem().getItem())) {
				shouldRender = false;
			}

			if (shouldRender) {
				LootBeamRenderer.renderLootBeam(event.getMatrixStack(), event.getRenderTypeBuffer(), event.getPartialTicks(), itemEntity.level.getGameTime(), itemEntity);
			}
		}
	}

	/**
	 * Checks if the given item is in the given list of registry names.
	 */
	private static boolean isItemInRegistryList(List<String> registryNames, Item item) {
		if (registryNames.size() > 0) {
			for (String id : registryNames) {
				if (!id.isEmpty()) {
					ResourceLocation itemResource = ResourceLocation.tryParse(id);
					if (itemResource != null && ForgeRegistries.ITEMS.getValue(itemResource).getItem() == item.getItem()) {
						return true;
					}
				}
			}
		}
		return false;
	}

}
