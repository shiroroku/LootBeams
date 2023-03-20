package com.lootbeams.compat;

import net.minecraft.world.item.ItemStack;
import shadows.apotheosis.adventure.affix.AffixHelper;
import shadows.apotheosis.adventure.loot.LootRarity;

public class ApotheosisCompat {

    public static String getRarityName(ItemStack stack){
        if(!isApotheosisItem(stack)) return "common";
        LootRarity rarity = AffixHelper.getRarity(stack);
        if(rarity == null) return "common";
        return rarity.id().toLowerCase();
    }

    public static boolean isApotheosisItem(ItemStack stack){
        return AffixHelper.hasAffixes(stack);
    }
}
