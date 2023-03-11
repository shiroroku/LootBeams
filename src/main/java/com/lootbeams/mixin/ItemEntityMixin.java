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
    private boolean hasPlayedSound = false;
    @Inject(method = "tick", at = @At("TAIL"))
    private void onTick(CallbackInfo ci) {
        ItemEntity _this = (ItemEntity) (Object) this;
        if (!hasPlayedSound && (_this.isOnGround() || (_this.isOnGround() && (_this.tickCount < 10 && _this.tickCount > 3)))) {
            ClientSetup.playDropSound(_this);
            hasPlayedSound = true;
        }
        if(hasPlayedSound && !_this.isOnGround()){
            hasPlayedSound = false;
        }
    }
}
