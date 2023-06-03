package com.lootbeams.compat;

import net.minecraft.world.item.ItemStack;
import shadows.apotheosis.adventure.affix.AffixHelper;

public class ApotheosisCompat {

    public static String getRarityName(ItemStack stack){
        if (!isApotheosisItem(stack) || AffixHelper.getRarity(stack) == null) {
            return "common";
        }

        return AffixHelper.getRarity(stack).id().toLowerCase();
    }

    public static boolean isApotheosisItem(ItemStack stack){
        return AffixHelper.hasAffixes(stack);
    }
}
