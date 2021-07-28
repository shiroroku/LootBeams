package com.lootbeams;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber
public class Configuration {

	public static ForgeConfigSpec CLIENT_CONFIG;

	public static ForgeConfigSpec.BooleanValue ALL_ITEMS;
	public static ForgeConfigSpec.BooleanValue ONLY_EQUIPMENT;
	public static ForgeConfigSpec.ConfigValue<List<String>> WHITELIST;
	public static ForgeConfigSpec.ConfigValue<List<String>> BLACKLIST;
	public static ForgeConfigSpec.ConfigValue<List<String>> COLOR_OVERRIDES;

	public static ForgeConfigSpec.BooleanValue RENDER_NAME_COLOR;
	public static ForgeConfigSpec.BooleanValue RENDER_RARITY_COLOR;
	public static ForgeConfigSpec.DoubleValue BEAM_RADIUS;
	public static ForgeConfigSpec.DoubleValue BEAM_HEIGHT;
	public static ForgeConfigSpec.DoubleValue BEAM_Y_OFFSET;
	public static ForgeConfigSpec.DoubleValue BEAM_ALPHA;

	public static ForgeConfigSpec.BooleanValue BORDERS;
	public static ForgeConfigSpec.BooleanValue RENDER_NAMETAGS;
	public static ForgeConfigSpec.BooleanValue RENDER_NAMETAGS_ONLOOK;
	public static ForgeConfigSpec.DoubleValue NAMETAG_LOOK_SENSITIVITY;
	public static ForgeConfigSpec.DoubleValue NAMETAG_TEXT_ALPHA;
	public static ForgeConfigSpec.DoubleValue NAMETAG_BACKGROUND_ALPHA;
	public static ForgeConfigSpec.DoubleValue NAMETAG_SCALE;
	public static ForgeConfigSpec.DoubleValue NAMETAG_Y_OFFSET;
	public static ForgeConfigSpec.BooleanValue DMCLOOT_COMPAT_RARITY;
	public static ForgeConfigSpec.ConfigValue<List<String>> CUSTOM_RARITIES;

	static {
		ForgeConfigSpec.Builder clientBuilder = new ForgeConfigSpec.Builder();

		clientBuilder.comment("Beam Config").push("Loot Beams");
		RENDER_NAME_COLOR = clientBuilder.comment("If beams should be colored the same as the Items name (excludes name colors from rarity). This has priority over render_rarity_color.").define("render_name_color", true);
		RENDER_RARITY_COLOR = clientBuilder.comment("If beams should be colored the same as the Items rarity.").define("render_rarity_color", true);
		BEAM_RADIUS = clientBuilder.comment("The radius of the Loot Beam.").defineInRange("beam_radius", 1D, 0D, 5D);
		BEAM_HEIGHT = clientBuilder.comment("The height of the Loot Beam.").defineInRange("beam_height", 1D, 0D, 10D);
		BEAM_Y_OFFSET = clientBuilder.comment("The Y-offset of the loot beam.").defineInRange("beam_y_offset", 0D, -30D, 30D);
		BEAM_ALPHA = clientBuilder.comment("Transparency of the Loot Beam.").defineInRange("beam_alpha", 0.85D, 0D, 1D);
		COLOR_OVERRIDES = clientBuilder.comment("Overrides an item's beam color with hex color. Must follow the specific format: (registryname=hexcolor) Or (#tagname=hexcolor). Example: \"minecraft:stone=0xFFFFFF\".").define("color_overrides", new ArrayList<>());

		clientBuilder.comment("Item Config").push("Items");
		ALL_ITEMS = clientBuilder.comment("If all Items Loot Beams should be rendered. Has priority over only_equipment.").define("all_items", true);
		ONLY_EQUIPMENT = clientBuilder.comment("If Loot Beams should only be rendered on equipment. (Equipment includes: Swords, Tools, Armor, Shields, Bows, Crossbows, Tridents, Arrows, and Fishing Rods)").define("only_equipment", false);
		WHITELIST = clientBuilder.comment("Registry names of items that Loot Beams should render on. Example: \"minecraft:stone\", \"minecraft:iron_ingot\"").define("whitelist", new ArrayList<>());
		BLACKLIST = clientBuilder.comment("Registry names of items that Loot Beams should NOT render on. This has priority over everything.").define("blacklist", new ArrayList<>());
		clientBuilder.pop();

		clientBuilder.comment("Item nametags").push("Nametags");
		BORDERS = clientBuilder.comment("Render nametags as bordered. Set to false for flat nametag with background.").define("borders", true);
		RENDER_NAMETAGS = clientBuilder.comment("If Item nametags should be rendered.").define("render_nametags", true);
		RENDER_NAMETAGS_ONLOOK = clientBuilder.comment("If Item nametags should be rendered when looking at items.").define("render_nametags_onlook", true);
		NAMETAG_LOOK_SENSITIVITY = clientBuilder.comment("How close the player has to look at the item to render the nametag.").defineInRange("nametag_look_sensitivity", 0.018D, 0D, 5D);
		NAMETAG_TEXT_ALPHA = clientBuilder.comment("Transparency of the nametag text.").defineInRange("nametag_text_alpha", 1D, 0D, 1D);
		NAMETAG_BACKGROUND_ALPHA = clientBuilder.comment("Transparency of the nametag background/border.").defineInRange("nametag_background_alpha", 0.5D, 0D, 1D);
		NAMETAG_SCALE = clientBuilder.comment("Scale of the nametag.").defineInRange("nametag_scale", 1, -10D, 10D);
		NAMETAG_Y_OFFSET = clientBuilder.comment("The Y-offset of the nametag.").defineInRange("nametag_y_offset", 0.75D, -30D, 30D);
		DMCLOOT_COMPAT_RARITY = clientBuilder.comment("If a smaller tag should be rendered under with DMCLoot rarities.").define("dmcloot_compat_rarity", true);
		CUSTOM_RARITIES = clientBuilder.comment("Define what the smaller tag should render on. Example: \"Exotic\", \"Ancient\". The string supplied has to be the tooltip line below the name. This is really only used for modpacks.").define("custom_rarities", new ArrayList<>());
		clientBuilder.pop();

		clientBuilder.pop();

		CLIENT_CONFIG = clientBuilder.build();
	}

	public static Color getColorFromItemOverrides(Item i) {
		List<String> overrides = COLOR_OVERRIDES.get();
		if (overrides.size() > 0) {
			for (String unparsed : overrides) {
				if (!unparsed.isEmpty()) {
					String[] configValue = unparsed.split("=");
					if (configValue.length == 2) {
						String nameIn = configValue[0];
						String colorIn = configValue[1];
						ResourceLocation registry = ResourceLocation.tryParse(nameIn.replace("#", ""));

						if (nameIn.startsWith("#")) {
							if (i.getTags().contains(registry)) {
								return Color.decode(colorIn);
							}
						} else {
							if (registry != null) {
								Item registryItem = ForgeRegistries.ITEMS.getValue(registry);
								if (registryItem != null && registryItem.getItem() == i) {
									return Color.decode(colorIn);
								}
							}
						}

					}
				}
			}
		}
		return null;
	}
}
