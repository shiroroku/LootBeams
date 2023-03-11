package com.lootbeams.mixin;

import com.lootbeams.ClientSetup;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(Entity.class)
public class EntityMixin {
    @Inject(method = "checkFallDamage", at = @At("TAIL"))
    private void onCheckFallDamage(double p_19911_, boolean p_19912_, BlockState p_19913_, BlockPos p_19914_, CallbackInfo ci) {
        Entity _this = (Entity) (Object) this;
        if(_this instanceof ItemEntity){
            ClientSetup.playDropSound((ItemEntity) _this);
        }
    }
}
