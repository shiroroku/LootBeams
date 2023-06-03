package com.lootbeams.mixin.client;

import com.lootbeams.ClientSetup;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin<T extends Entity> {
    @Unique
    ItemEntity entity;

    @Inject(at = @At("HEAD"), method = "render", locals = LocalCapture.CAPTURE_FAILHARD)
    public void render(T p_114485_, float p_114486_, float p_114487_, PoseStack p_114488_, MultiBufferSource p_114489_, int p_114490_, CallbackInfo ci) {
        if (p_114485_ instanceof ItemEntity ie) {
            entity = ie;
        }
    }

    @ModifyVariable(at = @At("HEAD"), method = "render", ordinal = 0, argsOnly = true)
    public int render(int light) {
        return entity != null ? ClientSetup.overrideLight(entity, light) : light;
    }
}
