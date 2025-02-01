package alexthw.ars_elemental.util;

import com.hollingsworth.arsnouveau.client.particle.ColorParticleTypeData;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleLineData;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Random;

import static com.hollingsworth.arsnouveau.client.particle.ParticleColor.defaultParticleColor;

public class ParticleUtil {

    public static ParticleColor fireColor = new ParticleColor(250, 15, 15);
    public static ParticleColor waterColor = new ParticleColor(50, 50, 250);
    public static ParticleColor earthColor = new ParticleColor(50, 250, 55);
    public static ParticleColor airColor = new ParticleColor(255, 255, 26);
    public static ParticleColor soulColor = new ParticleColor(200, 200, 200);


    public static ParticleColor schoolToColor(String school) {
        return switch (school) {

            case "fire" -> fireColor;

            case "water" -> waterColor;

            case "earth" -> earthColor;

            case "air" -> airColor;

            case "necromancy" -> soulColor;

            default -> defaultParticleColor();
        };
    }

    public static ParticleColor schoolToColor2(String school) {
        return switch (school) {

            case "fire", "water" -> airColor;

            case "earth" -> waterColor;

            case "air", "necromancy" -> fireColor;

            default -> defaultParticleColor();
        };
    }

    public static void gravityParticles(BlockPos pos, Level world, RandomSource rand, ParticleColor color, int range, int chance, int numParticles) {
        BlockPos.withinManhattanStream(pos, range, range, range).forEach(blockPos -> {
            if (rand.nextInt(chance) == 0) {
                for (int i = 0; i < rand.nextInt(numParticles); i++) {
                    double x = blockPos.getX() + com.hollingsworth.arsnouveau.client.particle.ParticleUtil.inRange(-0.5, 0.5) + 0.5;
                    double y = blockPos.getY() + com.hollingsworth.arsnouveau.client.particle.ParticleUtil.inRange(0.5, 1);
                    double z = blockPos.getZ() + com.hollingsworth.arsnouveau.client.particle.ParticleUtil.inRange(-0.5, 0.5) + 0.5;

                    // Add the particle to the world
                    world.addParticle(ParticleLineData.createData(color),
                            x, y, z,
                            pos.getX() + 0.5, pos.getY() + 1 + com.hollingsworth.arsnouveau.client.particle.ParticleUtil.inRange(0.25, 0.75), pos.getZ() + 0.5);
                }
            }
        });
    }

    public static class ParticleBuilder {
        Random random = new Random();

        ParticleColor color;
        float scale = 1.0F;
        float alpha = 1.0F;
        int lifetime = 20;
        double vx = 0, vy = 0, vz = 0;
        double dx = 0, dy = 0, dz = 0;
        double maxXSpeed = 0, maxYSpeed = 0, maxZSpeed = 0;
        double maxXDist = 0, maxYDist = 0, maxZDist = 0;

        public ParticleBuilder(ParticleColor color) {
            this.color = color;
        }

        public ParticleBuilder(float r, float g, float b) {
            this(new ParticleColor(r, b, g));
        }

        public ParticleBuilder scale(float scale) {
            this.scale = scale;
            return this;
        }

        public ParticleBuilder alpha(float alpha) {
            this.alpha = alpha;
            return this;
        }

        public ParticleBuilder setLifetime(int lifetime) {
            this.lifetime = lifetime;
            return this;
        }

        public ParticleBuilder randomVelocity(double maxSpeed) {
            return randomVelocity(maxSpeed, maxSpeed, maxSpeed);
        }

        public ParticleBuilder randomVelocity(double maxHSpeed, double maxVSpeed) {
            return randomVelocity(maxHSpeed, maxVSpeed, maxHSpeed);
        }

        public ParticleBuilder randomVelocity(double maxXSpeed, double maxYSpeed, double maxZSpeed) {
            this.maxXSpeed = maxXSpeed;
            this.maxYSpeed = maxYSpeed;
            this.maxZSpeed = maxZSpeed;
            return this;
        }

        public ParticleBuilder addVelocity(double vx, double vy, double vz) {
            this.vx += vx;
            this.vy += vy;
            this.vz += vz;
            return this;
        }

        public ParticleBuilder setVelocity(Vec3 speed) {
            return setVelocity(speed.x(), speed.y(), speed.z());
        }

        public ParticleBuilder setVelocity(double vx, double vy, double vz) {
            this.vx = vx;
            this.vy = vy;
            this.vz = vz;
            return this;
        }

        public ParticleBuilder randomOffset(double maxDistance) {
            return randomOffset(maxDistance, maxDistance, maxDistance);
        }

        public ParticleBuilder randomOffset(double maxHDist, double maxVDist) {
            return randomOffset(maxHDist, maxVDist, maxHDist);
        }

        public ParticleBuilder randomOffset(double maxXDist, double maxYDist, double maxZDist) {
            this.maxXDist = maxXDist;
            this.maxYDist = maxYDist;
            this.maxZDist = maxZDist;
            return this;
        }

        public void spawn(Level world, BlockPos pos) {
            spawn(world, pos.getX(), pos.getY(), pos.getZ());
        }

        public void spawn(Level world, double x, double y, double z) {
            double yaw = random.nextFloat() * Math.PI * 2,
                    pitch = random.nextFloat() * Math.PI - Math.PI / 2,
                    xSpeed = random.nextFloat() * maxXSpeed,
                    ySpeed = random.nextFloat() * maxYSpeed,
                    zSpeed = random.nextFloat() * maxZSpeed;
            this.vx += Math.sin(yaw) * Math.cos(pitch) * xSpeed;
            this.vy += Math.sin(pitch) * ySpeed;
            this.vz += Math.cos(yaw) * Math.cos(pitch) * zSpeed;
            double yaw2 = random.nextFloat() * Math.PI * 2, pitch2 = random.nextFloat() * Math.PI - Math.PI / 2,
                    xDist = random.nextFloat() * maxXDist, yDist = random.nextFloat() * maxYDist, zDist = random.nextFloat() * maxZDist;
            this.dx = Math.sin(yaw2) * Math.cos(pitch2) * xDist;
            this.dy = Math.sin(pitch2) * yDist;
            this.dz = Math.cos(yaw2) * Math.cos(pitch2) * zDist;

            world.addParticle(this.build(), x + dx, y + dy, z + dz, vx, vy, vz);
        }

        ColorParticleTypeData build(){
           return new ColorParticleTypeData(color, false, scale, alpha, lifetime);
        }


    }

    public void makeCouple(ParticleColor color1, ParticleColor color2, float size1, float size2, int age, BlockPos pos, Vec3 speed, Level world){

        ParticleBuilder p1 = new ParticleBuilder(color1).scale(size1).setLifetime(age).setVelocity(speed);
        ParticleBuilder p2 = new ParticleBuilder(color2).scale(size2).setLifetime(age).setVelocity(speed);

        p1.spawn(world, pos);
        p2.spawn(world, pos);

    }

}
