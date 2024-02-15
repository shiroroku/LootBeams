package com.lootbeams;

import com.lootbeams.vfx.Trail;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.List;

public class VFXParticle extends TextureSheetParticle {
    private final boolean fullbright;

    private boolean stoppedByCollision;
    private boolean hasTrail = false;

    public VFXParticle(ClientLevel clientWorld, TextureAtlasSprite sprite, float r, float g, float b, float a, int lifetime, float size,
                       Vec3 pos, Vec3 motion, float gravity, boolean collision, boolean fullbright) {
        super(clientWorld, pos.x, pos.y, pos.z);
        this.setSprite(sprite);
        this.rCol = r;
        this.gCol = g;
        this.bCol = b;
        this.alpha = Math.min(a,1   );
        this.lifetime = lifetime + 5;
        this.setSize(size);
        this.xd = motion.x;
        this.yd = motion.y;
        this.zd = motion.z;
        this.gravity = gravity;
        this.hasPhysics = collision;
        this.fullbright = fullbright;
        this.hasTrail = Math.random() < Configuration.TRAIL_CHANCE.get();
        if(hasTrail && Configuration.TRAIL_PARTICLES_INVISIBLE.get()){
            this.setSize(0.00001f);
        }
        if (Configuration.TRAILS.get() && hasTrail) {
            trail = new Trail(
                    FastColor.ARGB32.color((int) (r * 255), (int) (g * 255), (int) (b * 255), (int) (a * 255)),
                    (width) -> (float) (((float) Math.sin(width * 3.15) / 2f) * (0.3f * 0.3f) * Configuration.TRAIL_WIDTH.get() * (1+Math.random()))
            );
            trail.setColor(r, g, b, a);
            trail.setBillboard(true);
            trail.setLength((int) (Configuration.TRAIL_LENGTH.get() * (1+Math.random())));
            trail.setFrequency(Configuration.TRAIL_FREQUENCY.get());
            trail.pushPoint(new Vec3(this.x, this.y, this.z));
        }
    }

    Trail trail;

    @Override
    public boolean shouldCull() {
        return hasTrail;
    }

    @Override
    public void render(VertexConsumer p_107678_, Camera p_107679_, float p_107680_) {
        Vec3 vec3 = p_107679_.getPosition();
        float f = (float) (Mth.lerp(p_107680_, this.xo, this.x) - vec3.x());
        float f1 = (float) (Mth.lerp(p_107680_, this.yo, this.y) - vec3.y());
        float f2 = (float) (Mth.lerp(p_107680_, this.zo, this.z) - vec3.z());
        float lX = (float) (Mth.lerp(p_107680_, this.xo, this.x));
        float lY = (float) (Mth.lerp(p_107680_, this.yo, this.y));
        float lZ = (float) (Mth.lerp(p_107680_, this.zo, this.z));
        Quaternionf quaternionf;
        if (this.roll == 0.0F) {
            quaternionf = p_107679_.rotation();
        } else {
            quaternionf = new Quaternionf(p_107679_.rotation());
            quaternionf.rotateZ(Mth.lerp(p_107680_, this.oRoll, this.roll));
        }

        Vector3f[] avector3f = new Vector3f[]{new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)};
        float f3 = this.getQuadSize(p_107680_);

        for (int i = 0; i < 4; ++i) {
            Vector3f vector3f = avector3f[i];
            vector3f.rotate(quaternionf);
            vector3f.mul(f3);
            vector3f.add(f, f1, f2);
        }

        float f6 = this.getU0();
        float f7 = this.getU1();
        float f4 = this.getV0();
        float f5 = this.getV1();
        int j = this.getLightColor(p_107680_);
        p_107678_.vertex(avector3f[0].x(), avector3f[0].y(), avector3f[0].z()).uv(f7, f5).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
        p_107678_.vertex(avector3f[1].x(), avector3f[1].y(), avector3f[1].z()).uv(f7, f4).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
        p_107678_.vertex(avector3f[2].x(), avector3f[2].y(), avector3f[2].z()).uv(f6, f4).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
        p_107678_.vertex(avector3f[3].x(), avector3f[3].y(), avector3f[3].z()).uv(f6, f5).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
        if (Configuration.TRAILS.get() && hasTrail) {
            ClientSetup.delayedRenders.add(ps -> {
                trail.pushPoint(new Vec3(lX, lY, lZ));
                trail.setColor(this.rCol, this.gCol, this.bCol, this.alpha);
                trail.render(ps, Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(LootBeams.translucentNoCull(TEXTURE)), j);
            });
        }
    }

    private static final ResourceLocation TEXTURE = new ResourceLocation("lootbeams:textures/entity/white.png");

    @Override
    protected int getLightColor(float pPartialTick) {
        if (this.fullbright) {
            return LightTexture.pack(15, 15);
        } else {
            return super.getLightColor(pPartialTick);
        }
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
        if (Configuration.SPIN_AROUND_BEAM.get()) {
            applyForce();
        }
        if(this.age > this.lifetime-5) {
            this.alpha = 1 - ((float) (this.age - (this.lifetime-5)) / 5f);
        }
        if (this.age++ >= this.lifetime) {
            this.remove();
        } else {
            this.move(this.xd, this.yd, this.zd);
        }
    }

    Vec3 particleCenter = Vec3.ZERO;
    Vec3 axis = new Vec3(0, Configuration.BEAM_HEIGHT.get(), 0);

    public void setParticleCenter(Vec3 particleCenter) {
        this.particleCenter = particleCenter;
    }

    private void applyForce() {
        Vec3 particleToCenter = particleCenter.subtract(this.getPosition());
        Vec3 particleToCenterOnAxis = particleToCenter.subtract(axis.scale(particleToCenter.dot(axis)));
        Vec3 particleToCenterOnAxisUnit = particleToCenterOnAxis.normalize();
        Vec3 particleToCenterOnAxisUnitCrossVortexAxis = particleToCenterOnAxisUnit.cross(axis);
        Vec3 particleToCenterOnAxisUnitCrossVortexAxisUnit = particleToCenterOnAxisUnitCrossVortexAxis.normalize();
        Vec3 particleToCenterOnAxisUnitCrossVortexAxisUnitScaled = particleToCenterOnAxisUnitCrossVortexAxisUnit.scale(0.01f);
        this.xd += particleToCenterOnAxisUnitCrossVortexAxisUnitScaled.x * 0.65;
        this.yd += particleToCenterOnAxisUnitCrossVortexAxisUnitScaled.y;
        this.zd += particleToCenterOnAxisUnitCrossVortexAxisUnitScaled.z * 0.65;

        Vec3 target = particleCenter.add(axis.add(0,Configuration.BEAM_Y_OFFSET.get(),0));
        Vec3 particleToTarget = target.subtract(this.getPosition());
        Vec3 particleToTargetUnit = particleToTarget.normalize();
        int mod = this.y > target.y ? 0 : 1;
        Vec3 particleToTargetUnitScaled = particleToTargetUnit.scale(Configuration.PARTICLE_SPEED.get() * mod);
        this.xd += particleToTargetUnitScaled.x;
        this.yd += particleToTargetUnitScaled.y;
        this.zd += particleToTargetUnitScaled.z;
    }

    private Vec3 getPosition() {
        return new Vec3(this.x, this.y, this.z);
    }

    @Override
    public void move(double x, double y, double z) {
        if (!stoppedByCollision) {
            double dX = x;
            double dY = y;
            double dZ = z;
            if (this.hasPhysics && (x != 0.0D || y != 0.0D || z != 0.0D)) {
                Vec3 vector3d = Entity.collideBoundingBox(null, new Vec3(x, y, z), this.getBoundingBox(), this.level,
                        List.of()
                );
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

    private static final ParticleRenderType RENDER_TYPE = new LootBeamsParticleRenderType();

    @Override
    public ParticleRenderType getRenderType() {
        return RENDER_TYPE;
    }
}