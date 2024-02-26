package com.lootbeams.mixin;

import com.lootbeams.ClientSetup;
import net.minecraft.world.entity.item.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public class ItemEntityMixin {
    @Unique
    private boolean lootBeams$hasPlayedSound = false;

    @Inject(method = "tick", at = @At("TAIL"))
    private void onTick(CallbackInfo ci) {
        ItemEntity itemEntity = (ItemEntity) (Object) this;
        if (!lootBeams$hasPlayedSound && (itemEntity.onGround() || (itemEntity.onGround() && (itemEntity.tickCount < 10 && itemEntity.tickCount > 3)))) {
            ClientSetup.playDropSound(itemEntity);
            lootBeams$hasPlayedSound = true;
        }

        if(lootBeams$hasPlayedSound && !itemEntity.onGround()){
            lootBeams$hasPlayedSound = false;
        }
    }
}
