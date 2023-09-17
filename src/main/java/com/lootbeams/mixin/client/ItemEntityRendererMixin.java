package com.lootbeams.mixin.client;

import com.lootbeams.Configuration;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.OutlineBufferSource;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntityRenderer.class)
public class ItemEntityRendererMixin {
    @Unique
    ItemEntity itemEntity;

    @Inject(method = "render(Lnet/minecraft/world/entity/item/ItemEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At("HEAD"))
    private void renderInject(ItemEntity p_115036_, float p_115037_, float p_115038_, PoseStack p_115039_, MultiBufferSource p_115040_, int p_115041_, CallbackInfo ci) {
        this.itemEntity = p_115036_;
    }

    @Redirect(
            method = "render(Lnet/minecraft/world/entity/item/ItemEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;render(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IILnet/minecraft/client/resources/model/BakedModel;)V")
    )
    private void renderRedirect(ItemRenderer instance, ItemStack flag1, ItemDisplayContext idc, boolean posestack$pose, PoseStack ps, MultiBufferSource vertexconsumer, int light, int rendertype, BakedModel model) {
        // check if player can see item
        if(!Configuration.ITEMS_GLOW.get()) {
            instance.render(flag1, idc, posestack$pose, ps, vertexconsumer, light, rendertype, model);
            return;
        }
        HitResult hitResult = Minecraft.getInstance().level.clip(new ClipContext(Minecraft.getInstance().player.getEyePosition(1.0F), itemEntity.position(), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, Minecraft.getInstance().player));
        if (flag1.isEmpty() || hitResult.getType() == HitResult.Type.BLOCK || Minecraft.getInstance().player.distanceToSqr(this.itemEntity) > Configuration.RENDER_DISTANCE.get()) {
            instance.render(flag1, idc, posestack$pose, ps, vertexconsumer, light, rendertype, model);
        } else {
            OutlineBufferSource outlineProvider = Minecraft.getInstance().renderBuffers().outlineBufferSource();
            int color = flag1.getDisplayName().getStyle().getColor().getValue();
            int r = (color >> 16) & 0xFF;
            int g = (color >> 8) & 0xFF;
            int b = color & 0xFF;
            outlineProvider.setColor(r,g,b,255);
            instance.render(flag1, idc, posestack$pose, ps, outlineProvider, light, rendertype, model);
            Minecraft.getInstance().levelRenderer.renderBuffers.outlineBufferSource().endOutlineBatch();
            Minecraft.getInstance().levelRenderer.entityEffect.process(Minecraft.getInstance().getFrameTime());
            Minecraft.getInstance().getMainRenderTarget().bindWrite(false);
        }
    }

}
