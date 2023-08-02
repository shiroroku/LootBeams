package com.lootbeams.compat;

import net.minecraft.world.item.ItemStack;
import shadows.apotheosis.adventure.AdventureModule;
import shadows.apotheosis.adventure.affix.AffixHelper;
import shadows.apotheosis.adventure.affix.salvaging.SalvageItem;
import shadows.apotheosis.adventure.affix.socket.gem.GemItem;
import shadows.apotheosis.adventure.loot.LootRarity;

public class ApotheosisCompat {

    public static String getRarityName(ItemStack stack){
        if (!isApotheosisItem(stack)) return null;
        if (stack.getItem() instanceof SalvageItem salvageItem) {
            return AdventureModule.RARITY_MATERIALS.inverse().get(salvageItem).id().toLowerCase();
        }
        if (stack.getItem() instanceof GemItem) {
            return GemItem.getLootRarity(stack).id().toLowerCase();
        }
        LootRarity rarity = AffixHelper.getRarity(stack);
        if (rarity == null) return stack.getRarity().name().toLowerCase();
        return rarity.id().toLowerCase();
    }

    public static boolean isApotheosisItem(ItemStack stack){
        return AffixHelper.hasAffixes(stack) || stack.getItem() instanceof SalvageItem || stack.getItem() instanceof GemItem;
    }
}
