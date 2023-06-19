package birsy.shadedcitadels.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SculkSpeckParticle extends BaseAshSmokeParticle {
    protected SculkSpeckParticle(ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed, float pQuadSizeMultiplier, SpriteSet pSprites) {
        super(pLevel, pX, pY, pZ, 0.0F, -0.0F, 0.0F, pXSpeed, pYSpeed, pZSpeed, pQuadSizeMultiplier, pSprites, 0.0F, 20, 0.005F, false);
        this.rCol = 0;
        this.gCol = 0;
        this.bCol = 0;
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            this.remove();
        } else {
            this.alpha = (float)this.age / (float)this.lifetime;
            double friction = 0.9;
            this.xd *= friction;
            this.yd *= friction;
            this.zd *= friction;

            this.yd -= this.gravity * 0.1;

            this.x += this.xd;
            this.y += this.yd;
            this.z += this.zd;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet pSprites) {
            this.sprites = pSprites;
        }

        public Particle createParticle(SimpleParticleType pType, ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
            RandomSource randomsource = pLevel.random;
            return new SculkSpeckParticle(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed, randomsource.nextFloat() + randomsource.nextFloat(), this.sprites);
        }
    }
}
