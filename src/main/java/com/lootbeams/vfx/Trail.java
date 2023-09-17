package com.lootbeams.vfx;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.function.Function;

public class Trail {
    public enum TilingMode {
        NONE,
        STRETCH,
        REPEAT
    }
    private Vec3[] points;
    private Vec3[] rotations;
    private int color;
    private Function<Float, Float> widthFunction;
    private int length = 100;
    private boolean billboard = true;
    private TilingMode tilingMode = TilingMode.STRETCH;
    private int frequency = 1;
    private float minDistance = 0f;
    private ResourceLocation texture = null;
    private boolean parentRotation = false;

    public Trail(Vec3[] points, int color, Function<Float, Float> widthFunction) {
        this.points = points;
        this.color = color;
        this.widthFunction = widthFunction;
    }

    public Trail(int color, Function<Float, Float> widthFunction) {
        this(new Vec3[]{Vec3.ZERO}, color, widthFunction);
    }

    public void setParentRotation(boolean parentRotation) {
        this.parentRotation = parentRotation;
    }

    public void setTilingMode(TilingMode tilingMode) {
        this.tilingMode = tilingMode;
    }

    public void setTexture(ResourceLocation texture) {
        this.texture = texture;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public void setMinDistance(float minDistance) {
        this.minDistance = minDistance;
    }

    public void setPoints(Vec3[] points) {
        if(points.length > length) {
            Vec3[] newPoints = new Vec3[length];
            System.arraycopy(points, points.length - length, newPoints, 0, length);
            points = newPoints;
        }
        this.points = points;
    }

    public void setColor(int color) {
        this.color = color;
    }
    public void setColor(float r, float g, float b, float a) {
        this.color = ((int) (r * 255) << 16) | ((int) (g * 255) << 8) | ((int) (b * 255)) | ((int) (a * 255) << 24);
    }

    public void setLength(int length) {
        this.length = length;
    }

    public void setBillboard(boolean billboard) {
        this.billboard = billboard;
    }

    public void setWidthFunction(Function<Float, Float> widthFunction) {
        this.widthFunction = widthFunction;
    }

    public ResourceLocation getTexture() {
        return texture;
    }

    public int getLength() {
        return length;
    }

    public void pushPoint(Vec3 point) {
        if(points.length == 0) {
            points = new Vec3[]{point};
            return;
        }
        if (points[points.length - 1].distanceTo(point) < minDistance) {
            return;
        }
        // test if point is same as last point
        if (points[points.length - 1].equals(point)) {
            return;
        }
        // add point to end of array and remove first point if array is longer than length
        if(points[0] == Vec3.ZERO) {
            points[0] = point;
            return;
        }
        Vec3[] newPoints = new Vec3[points.length + 1];
        System.arraycopy(points, 0, newPoints, 0, points.length);
        newPoints[points.length] = point;
        if (newPoints.length > length) {
            Vec3[] newPoints2 = new Vec3[length];
            System.arraycopy(newPoints, 1, newPoints2, 0, length);
            newPoints = newPoints2;
        }
        points = newPoints;
    }

    public void pushRotatedPoint(Vec3 point, Vec3 rotation) {
        if(points.length == 0) {
            points = new Vec3[]{point};
            rotations = new Vec3[]{rotation};
            return;
        }
        if(points[0] == Vec3.ZERO) {
            points[0] = point;
            rotations = new Vec3[]{rotation};
            return;
        }
        if (points[points.length - 1].distanceTo(point) < minDistance) {
            return;
        }
        // test if point is same as last point
        if (points.length > 0 && points[points.length - 1].equals(point)) {
            return;
        }
        if(rotations == null) {
            rotations = new Vec3[]{rotation};
        }
        // add point to end of array and remove first point if array is longer than length
        Vec3[] newPoints = new Vec3[points.length + 1];
        Vec3[] newRotations = new Vec3[points.length + 1];
        System.arraycopy(points, 0, newPoints, 0, points.length);
        System.arraycopy(rotations, 0, newRotations, 0, rotations.length);
        newPoints[points.length] = point;
        newRotations[rotations.length] = rotation;
        if (newPoints.length > length) {
            Vec3[] newPoints2 = new Vec3[length];
            Vec3[] newRotations2 = new Vec3[length];
            System.arraycopy(newPoints, 1, newPoints2, 0, length);
            System.arraycopy(newRotations, 1, newRotations2, 0, length);
            newPoints = newPoints2;
            newRotations = newRotations2;
        }
        points = newPoints;
        rotations = newRotations;
    }

    public void render(PoseStack stack, VertexConsumer consumer, int light) {
        stack.pushPose();
        RenderSystem.disableCull();
        Vector3f[][] corners = new Vector3f[points.length][2];
        for (int i = 0; i < points.length; i++) {
            if(i % frequency != 0) continue;
            float width = widthFunction.apply((float) i / (points.length - 1));
            Vector3f topOffset = new Vector3f(0, (width / 2f),0);
            Vector3f bottomOffset = new Vector3f(0, -(width / 2f), 0);
            if (billboard) {
                Vec3 point = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition().subtract(points[i]).normalize();
                Vector3f cameraDirection = new Vector3f((float) point.x, (float) point.y, (float) point.z);
                Vec3 dirToNext = points[Math.min(i + frequency, points.length - 1)].subtract(points[i]).normalize();
                Vector3f dirToNextPoint = new Vector3f((float) dirToNext.x, (float) dirToNext.y, (float) dirToNext.z);
                Vector3f axis = new Vector3f(cameraDirection);
                // invert the axis
                axis.mul(-1);
                axis.cross(dirToNextPoint);
                topOffset = new Vector3f(axis);
                topOffset.mul(width/2f);
                bottomOffset = new Vector3f(axis);
                bottomOffset.mul(-width/2f);
            }
            topOffset.add((float) points[i].x, (float) points[i].y, (float) points[i].z);
            bottomOffset.add((float) points[i].x, (float) points[i].y, (float) points[i].z);
            corners[i/frequency][0] = topOffset;
            corners[i/frequency][1] = bottomOffset;
        }
        renderPoints(stack, consumer, light, corners, color);
        RenderSystem.enableCull();
        stack.popPose();
    }

    private void renderPoints(PoseStack stack, VertexConsumer consumer, int light, Vector3f[][] corners, int color) {
        stack.pushPose();
        float r = (color >> 16 & 255) / 255f;
        float g = (color >> 8 & 255) / 255f;
        float b = (color & 255) / 255f;
        float a = (color >> 24 & 255) / 255f;
        for (int i = 0; i < corners.length - 1; i++) {
            Vector3f top = corners[i][0];
            Vector3f bottom = corners[i][1];
            Vector3f nextTop = corners[i + 1][0];
            Vector3f nextBottom = corners[i + 1][1];
            if(nextTop == null || nextBottom == null || top == null || bottom == null) continue;
            float u = 0;
            float u1 = 1;
            if(tilingMode == TilingMode.STRETCH) {
                u = (float) i / (corners.length - 1);
                u1 = (float) (i + 1) / (corners.length - 1);
            }
            consumer.vertex(stack.last().pose(), bottom.x(), bottom.y(), bottom.z()).color(r, g, b, a).uv(u, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(0, 1, 0).endVertex();
            consumer.vertex(stack.last().pose(), top.x(), top.y(), top.z()).color(r, g, b, a).uv(u, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(0, 1, 0).endVertex();
            consumer.vertex(stack.last().pose(), nextTop.x(), nextTop.y(), nextTop.z()).color(r, g, b, a).uv(u1, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(0, 1, 0).endVertex();
            consumer.vertex(stack.last().pose(), nextBottom.x(), nextBottom.y(), nextBottom.z()).color(r, g, b, a).uv(u1, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(0, 1, 0).endVertex();
        }
        stack.popPose();
    }
}
