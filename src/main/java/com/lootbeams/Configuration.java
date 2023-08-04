package com.lootbeams;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber
public class Configuration {

	public static final ForgeConfigSpec CLIENT_CONFIG;

	public static final ForgeConfigSpec.BooleanValue RENDER_NAME_COLOR;
	public static final ForgeConfigSpec.BooleanValue RENDER_RARITY_COLOR;
	public static final ForgeConfigSpec.DoubleValue RENDER_DISTANCE;
	public static final ForgeConfigSpec.ConfigValue<List<String>> COLOR_OVERRIDES;

	public static final ForgeConfigSpec.DoubleValue BEAM_RADIUS;
	public static final ForgeConfigSpec.DoubleValue BEAM_HEIGHT;
	public static final ForgeConfigSpec.DoubleValue BEAM_Y_OFFSET;
	public static final ForgeConfigSpec.BooleanValue COMMON_SHORTER_BEAM;
	public static final ForgeConfigSpec.DoubleValue BEAM_ALPHA;
	public static final ForgeConfigSpec.BooleanValue SOLID_BEAM;
	public static final ForgeConfigSpec.BooleanValue WHITE_CENTER;
	public static final ForgeConfigSpec.BooleanValue GLOWING_BEAM;
	public static final ForgeConfigSpec.BooleanValue GLOW_EFFECT;
	public static final ForgeConfigSpec.DoubleValue GLOW_EFFECT_RADIUS;
	public static final ForgeConfigSpec.BooleanValue ANIMATE_GLOW;
	public static final ForgeConfigSpec.BooleanValue REQUIRE_ON_GROUND;

	public static final ForgeConfigSpec.BooleanValue PARTICLES;
	public static final ForgeConfigSpec.DoubleValue PARTICLE_SIZE;
	public static final ForgeConfigSpec.DoubleValue PARTICLE_SPEED;
	public static final ForgeConfigSpec.DoubleValue PARTICLE_RADIUS;
	public static final ForgeConfigSpec.DoubleValue PARTICLE_COUNT;
	public static final ForgeConfigSpec.IntValue PARTICLE_LIFETIME;
	public static final ForgeConfigSpec.BooleanValue PARTICLE_RARE_ONLY;

	public static final ForgeConfigSpec.BooleanValue ALL_ITEMS;
	public static final ForgeConfigSpec.BooleanValue ONLY_RARE;
	public static final ForgeConfigSpec.BooleanValue ONLY_EQUIPMENT;
	public static final ForgeConfigSpec.ConfigValue<List<String>> WHITELIST;
	public static final ForgeConfigSpec.ConfigValue<List<String>> BLACKLIST;

	public static final ForgeConfigSpec.BooleanValue BORDERS;
	public static final ForgeConfigSpec.BooleanValue RENDER_NAMETAGS;
	public static final ForgeConfigSpec.BooleanValue RENDER_NAMETAGS_ONLOOK;
	public static final ForgeConfigSpec.BooleanValue RENDER_STACKCOUNT;
	public static final ForgeConfigSpec.DoubleValue NAMETAG_LOOK_SENSITIVITY;
	public static final ForgeConfigSpec.DoubleValue NAMETAG_TEXT_ALPHA;
	public static final ForgeConfigSpec.DoubleValue NAMETAG_BACKGROUND_ALPHA;
	public static final ForgeConfigSpec.DoubleValue NAMETAG_SCALE;
	public static final ForgeConfigSpec.DoubleValue NAMETAG_Y_OFFSET;
	public static final ForgeConfigSpec.BooleanValue DMCLOOT_COMPAT_RARITY;
	public static final ForgeConfigSpec.ConfigValue<List<String>> CUSTOM_RARITIES;
	public static final ForgeConfigSpec.BooleanValue WHITE_RARITIES;
	public static final ForgeConfigSpec.BooleanValue VANILLA_RARITIES;

	public static final ForgeConfigSpec.BooleanValue SOUND;
	public static final ForgeConfigSpec.DoubleValue SOUND_VOLUME;
	public static final ForgeConfigSpec.BooleanValue SOUND_ALL_ITEMS;
	public static final ForgeConfigSpec.BooleanValue SOUND_ONLY_RARE;
	public static final ForgeConfigSpec.BooleanValue SOUND_ONLY_EQUIPMENT;
	public static final ForgeConfigSpec.ConfigValue<List<String>> SOUND_ONLY_WHITELIST;
	public static final ForgeConfigSpec.ConfigValue<List<String>> SOUND_ONLY_BLACKLIST;

	public static ForgeConfigSpec.BooleanValue GLOWING_BEAM;

	public static ForgeConfigSpec.BooleanValue VANILLA_RARITIES;
	public static ForgeConfigSpec.BooleanValue WHITE_CENTER;
	public static ForgeConfigSpec.DoubleValue PARTICLE_SIZE;
	public static ForgeConfigSpec.DoubleValue PARTICLE_SPEED;
	public static ForgeConfigSpec.DoubleValue PARTICLE_RADIUS;
	public static ForgeConfigSpec.DoubleValue PARTICLE_COUNT;
	public static ForgeConfigSpec.IntValue PARTICLE_LIFETIME;
	public static ForgeConfigSpec.BooleanValue PARTICLE_RARE_ONLY;

	public static ForgeConfigSpec.BooleanValue SOUND;
	public static ForgeConfigSpec.DoubleValue SOUND_VOLUME;
	public static ForgeConfigSpec.BooleanValue SOUND_ONLY_RARE;
	public static ForgeConfigSpec.BooleanValue SOUND_ONLY_EQUIPMENT;
	public static ForgeConfigSpec.ConfigValue<List<String>> SOUND_ONLY_WHITELIST;
	public static ForgeConfigSpec.ConfigValue<List<String>> SOUND_ONLY_BLACKLIST;
	public static ForgeConfigSpec.BooleanValue SOUND_ALL_ITEMS;

	static {
		ForgeConfigSpec.Builder clientBuilder = new ForgeConfigSpec.Builder();

		clientBuilder.comment("Beam Config").push("Loot Beams");
		RENDER_NAME_COLOR = clientBuilder.comment("If beams should be colored the same as the Items name (excludes name colors from rarity). This has priority over render_rarity_color.").define("render_name_color", true);
		RENDER_RARITY_COLOR = clientBuilder.comment("If beams should be colored the same as the Items rarity.").define("render_rarity_color", true);
		RENDER_DISTANCE = clientBuilder.comment("How close the player has to be to see the beam. (note: ItemEntities stop rendering at 24 blocks, so that is the limit for beams)").defineInRange("render_distance", 24D, 0D, 24D);
		COLOR_OVERRIDES = clientBuilder.comment("Overrides an item's beam color with hex color. Must follow the specific format: (registryname=hexcolor) Or (#tagname=hexcolor). Example: \"minecraft:stone=0xFFFFFF\". This also accepts modids.").define("color_overrides", new ArrayList<>());

		clientBuilder.comment("Beam Configuration").push("Beam");
		BEAM_RADIUS = clientBuilder.comment("The radius of the Loot Beam.").defineInRange("beam_radius", 0.55D, 0D, 5D);
		BEAM_HEIGHT = clientBuilder.comment("The height of the Loot Beam.").defineInRange("beam_height", 1.5D, 0D, 10D);
		BEAM_Y_OFFSET = clientBuilder.comment("The Y-offset of the loot beam.").defineInRange("beam_y_offset", 0.5D, -30D, 30D);
		COMMON_SHORTER_BEAM = clientBuilder.comment("If the Loot Beam should be shorter for common items.").define("common_shorter_beam", true);
		BEAM_ALPHA = clientBuilder.comment("Transparency of the Loot Beam.").defineInRange("beam_alpha", 0.75D, 0D, 1D);
		SOLID_BEAM = clientBuilder.comment("If the Loot Beam should use a solid texture or the beacon style texture.").define("solid_beam", true);
		WHITE_CENTER = clientBuilder.comment("If the Loot Beam should have a white center.").define("white_center", true);
		GLOWING_BEAM = clientBuilder.comment("If the Loot Beam should be glowing.").define("glowing_beam", true);
		GLOW_EFFECT = clientBuilder.comment("If the Loot Beam should have a glow effect around the base of the item.").define("glow_effect", true);
		GLOW_EFFECT_RADIUS = clientBuilder.comment("The radius of the glow effect.").defineInRange("glow_effect_radius", 0.5D, 0.00001D, 1D);
		ANIMATE_GLOW = clientBuilder.comment("If the glow effect should be animated.").define("animate_glow", true);
		REQUIRE_ON_GROUND = clientBuilder.comment("If the item must be on the ground to render a beam.").define("require_on_ground", true);
		clientBuilder.pop();

		clientBuilder.comment("Particle Config").push("Particles");
		PARTICLES = clientBuilder.comment("If particles should appear around the item.").define("particles", true);
		PARTICLE_SIZE = clientBuilder.comment("The size of the particles.").defineInRange("particle_size", 0.25D, 0.00001D, 10D);
		PARTICLE_SPEED = clientBuilder.comment("The speed of the particles.").defineInRange("particle_speed", 0.1D, 0.00001D, 10D);
		PARTICLE_RADIUS = clientBuilder.comment("The radius of the particles.").defineInRange("particle_radius", 0.05D, 0.00001D, 10D);
		PARTICLE_COUNT = clientBuilder.comment("The amount of particles to spawn per second.").defineInRange("particle_count", 19D, 1D, 20D);
		PARTICLE_LIFETIME = clientBuilder.comment("The lifetime of the particles in ticks.").defineInRange("particle_lifetime", 20, 1, 100);
		PARTICLE_RARE_ONLY = clientBuilder.comment("If particles should only appear on rare items.").define("particle_rare_only", true);
		clientBuilder.pop();

		clientBuilder.comment("Item Config").push("Items");
		ALL_ITEMS = clientBuilder.comment("If all Items Loot Beams should be rendered. Has priority over only_equipment and only_rare.").define("all_items", true);
		ONLY_RARE = clientBuilder.comment("If Loot Beams should only be rendered on items with rarity.").define("only_rare", false);
		ONLY_EQUIPMENT = clientBuilder.comment("If Loot Beams should only be rendered on equipment. (Equipment includes: Swords, Tools, Armor, Shields, Bows, Crossbows, Tridents, Arrows, and Fishing Rods)").define("only_equipment", false);
		WHITELIST = clientBuilder.comment("Registry names of items that Loot Beams should render on. Example: \"minecraft:stone\", \"minecraft:iron_ingot\", You can also specify modids for a whole mod's items.").define("whitelist", new ArrayList<>());
		BLACKLIST = clientBuilder.comment("Registry names of items that Loot Beams should NOT render on. This has priority over everything. You can also specify modids for a whole mod's items.").define("blacklist", new ArrayList<>());
		clientBuilder.pop();

		clientBuilder.comment("Item nametags").push("Nametags");
		BORDERS = clientBuilder.comment("Render nametags as bordered. Set to false for flat nametag with background.").define("borders", true);
		RENDER_NAMETAGS = clientBuilder.comment("If Item nametags should be rendered.").define("render_nametags", true);
		RENDER_NAMETAGS_ONLOOK = clientBuilder.comment("If Item nametags should be rendered when looking at items.").define("render_nametags_onlook", true);
		RENDER_STACKCOUNT = clientBuilder.comment("If the count of item's should also be shown in the nametag.").define("render_stackcount", true);
		NAMETAG_LOOK_SENSITIVITY = clientBuilder.comment("How close the player has to look at the item to render the nametag.").defineInRange("nametag_look_sensitivity", 0.018D, 0D, 5D);
		NAMETAG_TEXT_ALPHA = clientBuilder.comment("Transparency of the nametag text.").defineInRange("nametag_text_alpha", 1D, 0D, 1D);
		NAMETAG_BACKGROUND_ALPHA = clientBuilder.comment("Transparency of the nametag background/border.").defineInRange("nametag_background_alpha", 0.5D, 0D, 1D);
		NAMETAG_SCALE = clientBuilder.comment("Scale of the nametag.").defineInRange("nametag_scale", 1, -10D, 10D);
		NAMETAG_Y_OFFSET = clientBuilder.comment("The Y-offset of the nametag.").defineInRange("nametag_y_offset", 0.75D, -30D, 30D);
		DMCLOOT_COMPAT_RARITY = clientBuilder.comment("If a smaller tag should be rendered under with DMCLoot rarities.").define("dmcloot_compat_rarity", true);
		CUSTOM_RARITIES = clientBuilder.comment("Define what the smaller tag should render on. Example: \"Exotic\", \"Ancient\". The string supplied has to be the tooltip line below the name. This is really only used for modpacks.").define("custom_rarities", new ArrayList<>());
		WHITE_RARITIES = clientBuilder.comment("If rarities should ignore color and render as white (This is really only used for modpacks)").define("white_rarities", false);
		VANILLA_RARITIES = clientBuilder.comment("If vanilla rarities should be rendered.").define("vanilla_rarities", true);
		clientBuilder.pop();

		clientBuilder.comment("Sounds").push("Sounds");
		SOUND = clientBuilder.comment("If sounds should be played when items are dropped up.").define("play_sounds", false);
		SOUND_VOLUME = clientBuilder.comment("The volume of the sound.").defineInRange("sound_volume", 1D, 0D, 1D);
		SOUND_ALL_ITEMS = clientBuilder.comment("If sounds should play on all items. Has priority over sound_only_equipment and sound_only_rare.").define("sound_all_items", false);
		SOUND_ONLY_RARE = clientBuilder.comment("If sounds should only be played on items with rarity.").define("sound_only_rare", true);
		SOUND_ONLY_EQUIPMENT = clientBuilder.comment("If sounds should only be played on equipment. (Equipment includes: Swords, Tools, Armor, Shields, Bows, Crossbows, Tridents, Arrows, and Fishing Rods)").define("sound_only_equipment", false);
		SOUND_ONLY_WHITELIST = clientBuilder.comment("Registry names of items that sounds should play on. This has priority over everything except sound_only_blacklist. Example: \"minecraft:stone\", \"minecraft:iron_ingot\", You can also specify modids for a whole mod's items.").define("sound_whitelist", new ArrayList<>());
		SOUND_ONLY_BLACKLIST = clientBuilder.comment("Registry names of items that sounds should NOT play on. This has priority over everything. You can also specify modids for a whole mod's items.").define("sound_blacklist", new ArrayList<>());
		clientBuilder.pop();



		clientBuilder.pop();

		CLIENT_CONFIG = clientBuilder.build();
	}

	public static Color getColorFromItemOverrides(Item item) {
		List<String> overrides = COLOR_OVERRIDES.get();
		if (overrides.isEmpty()) {
			return null;
		}

		for (String unparsed : overrides.stream().filter(s -> !s.isEmpty()).toList()) {
			String[] configValue = unparsed.split("=");
			if (configValue.length != 2) {
				continue;
			}

			String nameIn = configValue[0];
			ResourceLocation registry = ResourceLocation.tryParse(nameIn.replace("#", ""));
			Color colorIn;
			try {
				colorIn = Color.decode(configValue[1]);
			} catch (Exception e) {
				LootBeams.LOGGER.error(String.format("Color overrides error! \"%s\" is not a valid hex color for \"%s\"", configValue[1], nameIn));
				return null;
			}

			// Mod id
			if (!nameIn.contains(":") && ForgeRegistries.ITEMS.getKey(item).getNamespace().equals(nameIn)) {
				return colorIn;
			}

			if (registry != null) {
				// Tag
				if (nameIn.startsWith("#") && ForgeRegistries.ITEMS.tags().getTag(TagKey.create(Registry.ITEM_REGISTRY, registry)).contains(item)) {
					return colorIn;
				}

				// Item
				Item registryItem = ForgeRegistries.ITEMS.getValue(registry);
				if (registryItem != null && registryItem.asItem() == item) {
					return colorIn;
				}

			}
		}

		return null;
	}
}
