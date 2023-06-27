package com.lootbeams;

import net.minecraft.client.Minecraft;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.Item;
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
		if (event.getEntity() instanceof ItemEntity ie) {
			LootBeamRenderer.TOOLTIP_CACHE.computeIfAbsent(ie, itemEntity -> itemEntity.getItem().getTooltipLines(null, TooltipFlag.Default.NORMAL));
			if (!LootBeamRenderer.LIGHT_CACHE.contains(ie)) {
				LootBeamRenderer.LIGHT_CACHE.add(ie);
			}
		}
	}

	public static void entityRemoval(EntityLeaveLevelEvent event) {
		if (event.getEntity() instanceof ItemEntity ie) {
			LootBeamRenderer.TOOLTIP_CACHE.remove(ie);
			LootBeamRenderer.LIGHT_CACHE.remove(ie);
		}
	}

	public static int overrideLight(ItemEntity ie, int light) {
		if (Configuration.ALL_ITEMS.get()
				|| (Configuration.ONLY_EQUIPMENT.get() && isEquipmentItem(ie.getItem().getItem()))
				|| (Configuration.ONLY_RARE.get() && LootBeamRenderer.compatRarityCheck(ie, false))
				|| (isItemInRegistryList(Configuration.WHITELIST.get(), ie.getItem().getItem()))) {
			light = 15728640;
		}

		return light;
	}

	public static void playDropSound(ItemEntity itemEntity) {
		if (!Configuration.SOUND.get()) {
			return;
		}

		Item item = itemEntity.getItem().getItem();
		if ((Configuration.SOUND_ALL_ITEMS.get() && !isItemInRegistryList(Configuration.BLACKLIST.get(), item))
				|| (Configuration.SOUND_ONLY_EQUIPMENT.get() && isEquipmentItem(item))
				|| (Configuration.SOUND_ONLY_RARE.get() && LootBeamRenderer.compatRarityCheck(itemEntity, false))
				|| isItemInRegistryList(Configuration.SOUND_ONLY_WHITELIST.get(), item)) {
			WeighedSoundEvents sound = Minecraft.getInstance().getSoundManager().getSoundEvent(LootBeams.LOOT_DROP);
			if(sound != null && Minecraft.getInstance().level != null) {
				Minecraft.getInstance().level.playSound(Minecraft.getInstance().player, itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(), SoundEvent.createVariableRangeEvent(LootBeams.LOOT_DROP), SoundSource.AMBIENT, 0.1f * Configuration.SOUND_VOLUME.get().floatValue(), 1.0f);
			}
		}
	}

	public static void onRenderNameplate(RenderNameTagEvent event) {
		if (!(event.getEntity() instanceof ItemEntity itemEntity)
				|| Minecraft.getInstance().player.distanceToSqr(itemEntity) > Math.pow(Configuration.RENDER_DISTANCE.get(), 2)) {
			return;
		}

		Item item = itemEntity.getItem().getItem();
		boolean shouldRender = (Configuration.ALL_ITEMS.get()
				|| (Configuration.ONLY_EQUIPMENT.get() && isEquipmentItem(item))
				|| (Configuration.ONLY_RARE.get())
				|| (isItemInRegistryList(Configuration.WHITELIST.get(), itemEntity.getItem().getItem())))
				&& !isItemInRegistryList(Configuration.BLACKLIST.get(), itemEntity.getItem().getItem());

		if (shouldRender && (!Configuration.REQUIRE_ON_GROUND.get() || itemEntity.onGround())) {
			LootBeamRenderer.renderLootBeam(event.getPoseStack(), event.getMultiBufferSource(), event.getPartialTick(), itemEntity.level().getGameTime(), itemEntity);
		}
	}

	public static boolean isEquipmentItem(Item item) {
		return item instanceof TieredItem || item instanceof ArmorItem || item instanceof ShieldItem || item instanceof BowItem || item instanceof CrossbowItem;
	}

	private static boolean isItemInRegistryList(List<String> registryNames, Item item) {
		if (registryNames.isEmpty()) {
			return false;
		}

		for (String id : registryNames.stream().filter(s -> !s.isEmpty()).toList()) {
			if (!id.contains(":") && ForgeRegistries.ITEMS.getKey(item).getNamespace().equals(id)) {
				return true;
			}

			ResourceLocation itemResource = ResourceLocation.tryParse(id);
			if (itemResource != null && ForgeRegistries.ITEMS.getValue(itemResource).asItem() == item.asItem()) {
				return true;
			}
		}

		return false;
	}

}
