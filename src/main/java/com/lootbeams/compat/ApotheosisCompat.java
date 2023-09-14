package com.lootbeams.compat;

import dev.shadowsoffire.apotheosis.adventure.AdventureModule;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixHelper;
import dev.shadowsoffire.apotheosis.adventure.affix.salvaging.SalvageItem;
import dev.shadowsoffire.apotheosis.adventure.affix.socket.gem.GemItem;
import dev.shadowsoffire.apotheosis.adventure.loot.LootRarity;
import dev.shadowsoffire.placebo.reload.DynamicHolder;
import net.minecraft.world.item.ItemStack;

public class ApotheosisCompat {

    public static String getRarityName(ItemStack stack){
        if(!isApotheosisItem(stack)) return null;
        DynamicHolder<LootRarity> rarity = AffixHelper.getRarity(stack);
        if(rarity.get() == null) return stack.getRarity().name().toLowerCase();
        return rarity.get().toComponent().getString().toLowerCase();
    }

    public static boolean isApotheosisItem(ItemStack stack){
        return AffixHelper.hasAffixes(stack);// || stack.getItem() instanceof SalvageItem || stack.getItem() instanceof GemItem;
    }
}
