package com.lootbeams;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextProcessing;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.ModList;

import java.awt.*;
import java.util.List;
import java.util.Optional;

public class LootBeamRenderer extends RenderState {

	/**
	 * ISSUES:
	 * Beam renders behind things like chests/clouds/water/beds.
	 */

	private static final ResourceLocation LOOT_BEAM_TEXTURE = new ResourceLocation(LootBeams.MODID, "textures/entity/loot_beam.png");
	private static final RenderType LOOT_BEAM_RENDERTYPE = createRenderType();

	public LootBeamRenderer(String string, Runnable run, Runnable run2) {
		super(string, run, run2);
	}

	public static void renderLootBeam(MatrixStack stack, IRenderTypeBuffer buffer, float pticks, long worldtime, ItemEntity item) {
		float beamRadius = 0.05f * Configuration.BEAM_RADIUS.get().floatValue();
		float glowRadius = beamRadius + (beamRadius * 0.2f);
		float beamAlpha = Configuration.BEAM_ALPHA.get().floatValue();
		float beamHeight = Configuration.BEAM_HEIGHT.get().floatValue();
		float yOffset = Configuration.BEAM_Y_OFFSET.get().floatValue();

		Color color = getItemColor(item);
		float R = color.getRed() / 255f;
		float G = color.getGreen() / 255f;
		float B = color.getBlue() / 255f;

		stack.pushPose();
		//Render main beam
		stack.pushPose();
		float rotation = (float) Math.floorMod(worldtime, 40L) + pticks;
		stack.mulPose(Vector3f.YP.rotationDegrees(rotation * 2.25F - 45.0F));
		stack.translate(0, yOffset, 0);
		stack.translate(0, 1, 0);
		stack.mulPose(Vector3f.XP.rotationDegrees(180));
		renderPart(stack, buffer.getBuffer(LOOT_BEAM_RENDERTYPE), R, G, B, beamAlpha, beamHeight, 0.0F, beamRadius, beamRadius, 0.0F, -beamRadius, 0.0F, 0.0F, -beamRadius);
		stack.mulPose(Vector3f.XP.rotationDegrees(-180));
		renderPart(stack, buffer.getBuffer(LOOT_BEAM_RENDERTYPE), R, G, B, beamAlpha, beamHeight, 0.0F, beamRadius, beamRadius, 0.0F, -beamRadius, 0.0F, 0.0F, -beamRadius);
		stack.popPose();

		//Render glow around main beam
		stack.translate(0, yOffset, 0);
		stack.translate(0, 1, 0);
		stack.mulPose(Vector3f.XP.rotationDegrees(180));
		renderPart(stack, buffer.getBuffer(LOOT_BEAM_RENDERTYPE), R, G, B, beamAlpha * 0.4f, beamHeight, -glowRadius, -glowRadius, glowRadius, -glowRadius, -beamRadius, glowRadius, glowRadius, glowRadius);
		stack.mulPose(Vector3f.XP.rotationDegrees(-180));
		renderPart(stack, buffer.getBuffer(LOOT_BEAM_RENDERTYPE), R, G, B, beamAlpha * 0.4f, beamHeight, -glowRadius, -glowRadius, glowRadius, -glowRadius, -beamRadius, glowRadius, glowRadius, glowRadius);
		stack.popPose();

		if (Configuration.RENDER_NAMETAGS.get()) {
			renderNameTag(stack, buffer, item);
		}
	}

	private static void renderNameTag(MatrixStack stack, IRenderTypeBuffer buffer, ItemEntity item) {
		ClientPlayerEntity player = Minecraft.getInstance().player;
		boolean isLooking = isLookingAt(player, item, Configuration.NAMETAG_LOOK_SENSITIVITY.get()) && Configuration.RENDER_NAMETAGS_ONLOOK.get();
		if (player.isCrouching() || isLooking) {
			float scale = Configuration.NAMETAG_SCALE.get().floatValue();
			float backgroundAlpha = Configuration.NAMETAG_BACKGROUND_ALPHA.get().floatValue();
			float foregroundAlpha = Configuration.NAMETAG_TEXT_ALPHA.get().floatValue();
			double yOffset = Configuration.NAMETAG_Y_OFFSET.get();

			Color color = getItemColor(item);
			int backgroundColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), (int) (255 * backgroundAlpha)).getRGB();
			int foregroundColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), (int) (255 * foregroundAlpha)).getRGB();

			//Render nametags at heights based on player distance
			stack.pushPose();
			stack.translate(0.0D, Math.min(1D, Minecraft.getInstance().player.distanceToSqr(item) * 0.025D) + yOffset, 0.0D);
			stack.mulPose(Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());
			stack.scale(-0.02F * scale, -0.02F * scale, 0.02F * scale);

			FontRenderer fontrenderer = Minecraft.getInstance().font;
			ITextComponent text = item.getItem().getHoverName();

			boolean override = item.getItem().hasTag() && item.getItem().getTag().contains("lootbeams.color");

			//fontrenderer.drawInBatch(text.getString(), (float) (-fontrenderer.width(text) / 2), 0f, foregroundColor, false, stack.last().pose(), buffer, false, backgroundColor, 15728864);

			String s = StringUtils.stripColor(text.getString());
			RenderText(fontrenderer, stack, buffer, s, foregroundColor, backgroundColor, backgroundAlpha);

			//Smaller tags
			stack.translate(0.0D, 10, 0.0D);
			stack.scale(0.75f, 0.75f, 0.75f);
			boolean textDrawn = false;
			if (Configuration.RPGLOOT_COMPAT_RARITY.get() && ModList.get().isLoaded("rpgloot")) {
				//Render rpgloot rarity small tags
				if (item.getItem().hasTag() && item.getItem().getTag().contains("rpgloot.rarity")) {
					TranslationTextComponent translatedRarity = new TranslationTextComponent("rarity.rpgloot." + item.getItem().getTag().getString("rpgloot.rarity"));
					RenderText(fontrenderer, stack, buffer, translatedRarity.getString(), customDarker(new Color(foregroundColor)).getRGB(), backgroundColor, backgroundAlpha);
					//fontrenderer.drawInBatch(translatedRarity.getString(), (float) (-fontrenderer.width(translatedRarity) / 2), 0f, customDarker(new Color(foregroundColor)).getRGB(), false, stack.last().pose(), buffer, false, backgroundColor, 15728864);
					textDrawn = true;
				}
			}
			if (!textDrawn) {
				//Render small tags based on custom_rarities config
				List<ITextComponent> tooltip = item.getItem().getTooltipLines(null, ITooltipFlag.TooltipFlags.NORMAL);
				if (tooltip.size() > 1) {
					ITextComponent tooltipRarity = tooltip.get(1);
					for (String customrarity : Configuration.CUSTOM_RARITIES.get()) {
						if (tooltipRarity.getString().equals(customrarity)) {
							if (override) {
								RenderText(fontrenderer, stack, buffer, tooltipRarity.getString(), foregroundColor, backgroundColor, backgroundAlpha);
								//fontrenderer.drawInBatch(tooltipRarity.getString(), (float) (-fontrenderer.width(tooltipRarity) / 2), 0f, foregroundColor, false, stack.last().pose(), buffer, false, backgroundColor, 15728864);
							} else {
								Color rarityColor;
								if (tooltip.get(1).getStyle().getColor() == null) {
									rarityColor = new Color(foregroundColor);
								} else {
									rarityColor = new Color(tooltip.get(1).getStyle().getColor().getValue());
								}
								foregroundColor = new Color(rarityColor.getRed(), rarityColor.getGreen(), rarityColor.getBlue(), (int) (255 * foregroundAlpha)).getRGB();
								backgroundColor = new Color(rarityColor.getRed(), rarityColor.getGreen(), rarityColor.getBlue(), (int) (255 * backgroundAlpha)).getRGB();
								//fontrenderer.drawInBatch(tooltipRarity, (float) (-fontrenderer.width(tooltipRarity) / 2), 0f, foregroundColor, false, stack.last().pose(), buffer, false, backgroundColor, 15728864);
								RenderText(fontrenderer, stack, buffer, tooltipRarity.getString(), foregroundColor, backgroundColor, backgroundAlpha);
							}
						}
					}
				}
			}
			stack.popPose();
		}
	}

	private static void RenderText(FontRenderer fontRenderer, MatrixStack stack, IRenderTypeBuffer buffer, String text, int foregroundColor, int backgroundColor, float backgroundAlpha) {
		if (Configuration.BORDERS.get()) {
			float w = -fontRenderer.width(text) / 2f;
			int bg = new Color(0, 0, 0, (int) (255 * backgroundAlpha)).getRGB();
			fontRenderer.draw(stack, text, w + 1f, 0, bg);
			fontRenderer.draw(stack, text, w - 1f, 0, bg);
			fontRenderer.draw(stack, text, w, 1f, bg);
			fontRenderer.draw(stack, text, w, -1f, bg);
			stack.translate(0.0D, 0.0D, -0.01D);
			fontRenderer.draw(stack, text, w, 0, foregroundColor);
			stack.translate(0.0D, 0.0D, 0.01D);
		} else {
			fontRenderer.drawInBatch(text, (float) (-fontRenderer.width(text) / 2), 0f, foregroundColor, false, stack.last().pose(), buffer, false, backgroundColor, 15728864);
		}
	}

	private static Color customDarker(Color color) {
		return new Color(Math.max((int) (color.getRed() * (float) 0.9), 0), Math.max((int) (color.getGreen() * (float) 0.9), 0), Math.max((int) (color.getBlue() * (float) 0.9), 0), color.getAlpha());
	}

	/**
	 * Returns the color from the item's name, rarity, tag, or override.
	 */
	private static Color getItemColor(ItemEntity item) {
		Color override = Configuration.getColorFromItemOverrides(item.getItem().getItem());
		if (override != null) {
			return override;
		}

		if (item.getItem().hasTag() && item.getItem().getTag().contains("lootbeams.color")) {
			return Color.decode(item.getItem().getTag().getString("lootbeams.color"));
		}

		if (Configuration.RENDER_NAME_COLOR.get()) {
			ITextComponent text = item.getItem().getHoverName();
			List<Style> list = Lists.newArrayList();
			text.visit((acceptor, styleIn) -> {
				TextProcessing.iterateFormatted(styleIn, acceptor, (string, style, consumer) -> {
					list.add(style);
					return true;
				});
				return Optional.empty();
			}, Style.EMPTY);
			if (list.get(0).getColor() != null) {
				return new Color(list.get(0).getColor().getValue());
			}
		}

		if (Configuration.RENDER_RARITY_COLOR.get()) {
			return new Color(item.getItem().getRarity().color.getColor());
		} else {
			return Color.WHITE;
		}
	}

	private static void renderPart(MatrixStack stack, IVertexBuilder builder, float red, float green, float blue, float alpha, float height, float radius_1, float radius_2, float radius_3, float radius_4, float radius_5, float radius_6, float radius_7, float radius_8) {
		MatrixStack.Entry matrixentry = stack.last();
		Matrix4f matrixpose = matrixentry.pose();
		Matrix3f matrixnormal = matrixentry.normal();
		renderQuad(matrixpose, matrixnormal, builder, red, green, blue, alpha, height, radius_1, radius_2, radius_3, radius_4);
		renderQuad(matrixpose, matrixnormal, builder, red, green, blue, alpha, height, radius_7, radius_8, radius_5, radius_6);
		renderQuad(matrixpose, matrixnormal, builder, red, green, blue, alpha, height, radius_3, radius_4, radius_7, radius_8);
		renderQuad(matrixpose, matrixnormal, builder, red, green, blue, alpha, height, radius_5, radius_6, radius_1, radius_2);
	}

	private static void renderQuad(Matrix4f pose, Matrix3f normal, IVertexBuilder builder, float red, float green, float blue, float alpha, float y, float z1, float texu1, float z, float texu) {
		addVertex(pose, normal, builder, red, green, blue, alpha, y, z1, texu1, 1f, 0f);
		addVertex(pose, normal, builder, red, green, blue, alpha, 0f, z1, texu1, 1f, 1f);
		addVertex(pose, normal, builder, red, green, blue, alpha, 0f, z, texu, 0f, 1f);
		addVertex(pose, normal, builder, red, green, blue, alpha, y, z, texu, 0f, 0f);
	}

	private static void addVertex(Matrix4f pose, Matrix3f normal, IVertexBuilder builder, float red, float green, float blue, float alpha, float y, float x, float z, float texu, float texv) {
		builder.vertex(pose, x, y, z).color(red, green, blue, alpha).uv(texu, texv).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(normal, 0.0F, 1.0F, 0.0F).endVertex();
	}

	private static RenderType createRenderType() {
		RenderType.State state = RenderType.State.builder().setTextureState(new RenderState.TextureState(LOOT_BEAM_TEXTURE, false, false)).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setWriteMaskState(RenderState.COLOR_WRITE).setFogState(NO_FOG).createCompositeState(false);
		return RenderType.create("rpgloot_loot_beam", DefaultVertexFormats.BLOCK, 7, 256, false, true, state);
	}

	/**
	 * Checks if the player is looking at the given entity, accuracy determines how close the player has to look.
	 */
	private static boolean isLookingAt(ClientPlayerEntity player, Entity target, double accuracy) {
		Vector3d difference = new Vector3d(target.getX() - player.getX(), target.getEyeY() - player.getEyeY(), target.getZ() - player.getZ());
		double length = difference.length();
		double dot = player.getViewVector(1.0F).normalize().dot(difference.normalize());
		return dot > 1.0D - accuracy / length && player.canSee(target);
	}

}
