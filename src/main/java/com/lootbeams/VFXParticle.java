package com.lootbeams;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class VFXParticle extends TextureSheetParticle {
    private final boolean fullBright;
    private boolean stoppedByCollision;

    public VFXParticle(ClientLevel clientWorld, TextureAtlasSprite sprite, float r, float g, float b, float a, int lifetime, float size,
                       Vec3 pos, Vec3 motion, float gravity, boolean collision, boolean fullBright) {
        super(clientWorld, pos.x, pos.y, pos.z);
        this.setSprite(sprite);
        this.rCol = r;
        this.gCol = g;
        this.bCol = b;
        this.alpha = a;
        this.lifetime = lifetime;
        this.setSize(size);
        this.xd = motion.x;
        this.yd = motion.y;
        this.zd = motion.z;
        this.gravity = gravity;
        this.hasPhysics = collision;
        this.fullBright = fullBright;
    }

    @Override
    protected int getLightColor(float pPartialTick) {
        return this.fullBright ? LightTexture.pack(15, 15) : super.getLightColor(pPartialTick);
    }

    public void setSize(float size) {
        this.quadSize = size / 10;
        this.setSize(size / 10, size / 10);
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        this.alpha -= (float) 1 / this.lifetime;
        if (this.age++ >= this.lifetime) {
            this.remove();
        } else {
            this.yd -= 0.02D * (double) this.gravity;
            this.move(this.xd, this.yd, this.zd);
        }
    }

    @Override
    public void move(double x, double y, double z) {
        if (!stoppedByCollision) {
            double dX = x;
            double dY = y;
            double dZ = z;
            if (this.hasPhysics && (x != 0.0D || y != 0.0D || z != 0.0D)) {
                Vec3 vector3d = Entity.collideBoundingBox(null, new Vec3(x, y, z), this.getBoundingBox(), this.level, List.of());
                x = vector3d.x;
                y = vector3d.y;
                z = vector3d.z;
            }

            if (x != 0.0D || y != 0.0D || z != 0.0D) {
                this.setBoundingBox(this.getBoundingBox().move(x, y, z));
                this.setLocationFromBoundingbox();
            } else {
                this.stoppedByCollision = true;
            }

            if (dX != x) {
                this.xd = 0.0D;
            }

            if (dY != y) {
                this.yd = 0.0D;
            }

            if (dZ != z) {
                this.zd = 0.0D;
            }
        }
    }

    @Override
    @NotNull
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }
}