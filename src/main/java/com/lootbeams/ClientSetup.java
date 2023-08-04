package com.lootbeams;

import net.minecraft.client.Minecraft;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderNameTagEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

@Mod.EventBusSubscriber(modid = LootBeams.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientSetup {

	public static void init(FMLClientSetupEvent ignored) {
		ignored.enqueueWork(() -> {
			MinecraftForge.EVENT_BUS.addListener(ClientSetup::onRenderNameplate);
			MinecraftForge.EVENT_BUS.addListener(ClientSetup::onItemCreation);
			MinecraftForge.EVENT_BUS.addListener(ClientSetup::entityRemoval);
		});
	}

	public static void onItemCreation(EntityJoinLevelEvent event){
		if (event.getEntity() instanceof ItemEntity itemEntity) {
			LootBeamRenderer.TOOLTIP_CACHE.computeIfAbsent(itemEntity, entity -> entity.getItem().getTooltipLines(null, TooltipFlag.Default.NORMAL));
			if (!LootBeamRenderer.LIGHT_CACHE.contains(itemEntity)) {
				LootBeamRenderer.LIGHT_CACHE.add(itemEntity);
			}
		}
	}

	public static void entityRemoval(EntityLeaveLevelEvent event) {
		if (event.getEntity() instanceof ItemEntity itemEntity) {
			LootBeamRenderer.TOOLTIP_CACHE.remove(itemEntity);
			LootBeamRenderer.LIGHT_CACHE.remove(itemEntity);
		}
	}

	public static int overrideLight(ItemEntity itemEntity, int light) {
		return shouldRenderBeam(itemEntity.getItem()) ? 15728640 : light;
	}

	public static void playDropSound(ItemEntity itemEntity) {
		if (!shouldPlaySound(itemEntity.getItem())) {
			return;
		}

		WeighedSoundEvents sound = Minecraft.getInstance().getSoundManager().getSoundEvent(LootBeams.LOOT_DROP);
		if (sound != null && Minecraft.getInstance().level != null) {
			Minecraft.getInstance().level.playSound(Minecraft.getInstance().player, itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(), new SoundEvent(LootBeams.LOOT_DROP), SoundSource.AMBIENT, 0.1f * Configuration.SOUND_VOLUME.get().floatValue(), 1.0f);
		}
	}

	public static void onRenderNameplate(RenderNameTagEvent event) {
		if (!(event.getEntity() instanceof ItemEntity itemEntity)) {
			return;
		}

		if (shouldRenderBeam(itemEntity.getItem()) && (!Configuration.REQUIRE_ON_GROUND.get() || itemEntity.isOnGround())) {
			LootBeamRenderer.renderLootBeam(event.getPoseStack(), event.getMultiBufferSource(), event.getPartialTick(), itemEntity.level.getGameTime(), itemEntity);
		}
	}

	public static boolean shouldPlaySound(ItemStack itemStack) {
		// SOUND and SOUND_ONLY_BLACKLIST have the highest Priority
		Item item = itemStack.getItem();
		if (!Configuration.SOUND.get() || isItemInRegistryList(Configuration.SOUND_ONLY_BLACKLIST.get(), item)) {
			return false;
		}

		// If the item must be Rare
		boolean shouldPlay = !Configuration.SOUND_ONLY_RARE.get() || LootBeamRenderer.compatRarityCheck(itemStack);
		// If the item must be an Equipment
		if (Configuration.SOUND_ONLY_EQUIPMENT.get()) {
			shouldPlay = shouldPlay && isEquipmentItem(item);
		}

		// SOUND_ALL_ITEMS and SOUND_ONLY_WHITELIST have priority over SOUND_ONLY_RARE and SOUND_ONLY_EQUIPMENT
		return shouldPlay || Configuration.SOUND_ALL_ITEMS.get() || isItemInRegistryList(Configuration.SOUND_ONLY_WHITELIST.get(), item);
	}

	public static boolean shouldRenderBeam(ItemStack itemStack) {
		Item item = itemStack.getItem();
		// BLACKLIST has the highest Priority
		if (isItemInRegistryList(Configuration.BLACKLIST.get(), item)) {
			return false;
		}

		// If the item must be Rare
		boolean shouldRender = !Configuration.ONLY_RARE.get() || LootBeamRenderer.compatRarityCheck(itemStack);
		// If the item must be an Equipment
		if (Configuration.ONLY_EQUIPMENT.get()) {
			shouldRender = shouldRender && isEquipmentItem(item);
		}

		// ALL_ITEMS and WHITELIST have priority over ONLY_RARE and ONLY_EQUIPMENT
		return shouldRender || Configuration.ALL_ITEMS.get() || isItemInRegistryList(Configuration.WHITELIST.get(), item);
	}

	public static boolean isEquipmentItem(Item item) {
		return item instanceof TieredItem || item instanceof ArmorItem || item instanceof ShieldItem || item instanceof BowItem || item instanceof CrossbowItem;
	}

	private static boolean isItemInRegistryList(List<String> registryNames, Item item) {
		if (registryNames.isEmpty()) {
			return false;
		}

		for (String id : registryNames.stream().filter(s -> !s.isEmpty()).toList()) {
			// Mod id
			if (!id.contains(":") && ForgeRegistries.ITEMS.getKey(item).getNamespace().equals(id)) {
				return true;
			}

			ResourceLocation registry = ResourceLocation.tryParse(id.replace("#", ""));
			if (registry != null) {
				// Tag
				if (id.startsWith("#") && ForgeRegistries.ITEMS.tags().getTag(TagKey.create(Registry.ITEM_REGISTRY, registry)).contains(item)) {
					return true;
				}

				// Item
				Item registryItem = ForgeRegistries.ITEMS.getValue(registry);
				if (registryItem != null && registryItem.asItem() == item) {
					return true;
				}
			}
		}

		return false;
	}

}