package com.lootbeams.compat;

import dev.shadowsoffire.apotheosis.adventure.AdventureModule;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixHelper;
import dev.shadowsoffire.apotheosis.adventure.affix.salvaging.SalvageItem;
import dev.shadowsoffire.apotheosis.adventure.affix.socket.gem.GemItem;
import dev.shadowsoffire.apotheosis.adventure.loot.LootRarity;
import dev.shadowsoffire.apotheosis.adventure.loot.RarityRegistry;
import dev.shadowsoffire.placebo.reload.DynamicHolder;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.item.ItemStack;

public class ApotheosisCompat {

    public static String getRarityName(ItemStack stack){
        if(!isApotheosisItem(stack)) return stack.getRarity().name().toLowerCase();
        DynamicHolder<LootRarity> rarity = AffixHelper.getRarity(stack);
        if(stack.getItem() instanceof SalvageItem si) {
            rarity = RarityRegistry.getMaterialRarity(si);
        }
        if(!rarity.isBound()) return stack.getRarity().name().toLowerCase();
        return rarity.get().toComponent().getString().toLowerCase();
    }

    public static TextColor getRarityColor(ItemStack stack){
        if(!isApotheosisItem(stack)) return TextColor.parseColor("#FFFFFF");
        DynamicHolder<LootRarity> rarity = AffixHelper.getRarity(stack);
        if(rarity.get() == null) return TextColor.parseColor(String.valueOf(stack.getRarity().color.getColor()));
        return rarity.get().getColor();
    }

    public static boolean isApotheosisItem(ItemStack stack){
        return AffixHelper.hasAffixes(stack) || stack.getItem() instanceof GemItem || stack.getItem() instanceof SalvageItem;
    }
}
