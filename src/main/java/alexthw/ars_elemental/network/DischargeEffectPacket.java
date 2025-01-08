package alexthw.ars_elemental.network;

import alexthw.ars_elemental.registry.ModParticles;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.client.particle.ColoredDynamicTypeData;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.common.network.AbstractPacket;
import com.hollingsworth.arsnouveau.common.network.Networking;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.fml.ModList;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

import static com.hollingsworth.arsnouveau.client.registry.ModParticles.SPARKLE_TYPE;

public class DischargeEffectPacket extends AbstractPacket {

    public Vec3 from;
    public Vec3 to;
    public ParticleColor color;

    public DischargeEffectPacket(Vec3 from, Vec3 to, ParticleColor colors) {
        this.from = from;
        this.to = to;
        this.color = colors;
    }

    public static void encode(DischargeEffectPacket msg, FriendlyByteBuf buf) {
        encodePos(buf, msg.from);
        encodePos(buf, msg.to);
        buf.writeInt(msg.color.getColor());
    }

    public static void encodePos(@Nonnull FriendlyByteBuf buf, @Nonnull Vec3 item) {
        buf.writeDouble(item.x);
        buf.writeDouble(item.y);
        buf.writeDouble(item.z);
    }

    public static DischargeEffectPacket decode(FriendlyByteBuf buf) {
        Vec3 from = decodeVector3d(buf);
        Vec3 to = decodeVector3d(buf);
        int colors = buf.readInt();
        return new DischargeEffectPacket(from, to, ParticleColor.fromInt(colors));
    }

    public static Vec3 decodeVector3d(@Nonnull FriendlyByteBuf buf) {
        double x = buf.readDouble();
        double y = buf.readDouble();
        double z = buf.readDouble();
        return new Vec3(x, y, z);
    }


    public static void send(@Nonnull Level level, @Nonnull ParticleColor spellColor, @Nonnull Vec3 fromPoint, @Nonnull Vec3 hitPoint) {
        Vec3 midpoint = fromPoint.add(hitPoint).scale(0.5);
        double radius = 64.0 + fromPoint.distanceTo(midpoint);
        //double radiusSqr = radius * radius;

        if (level instanceof ServerLevel serverLevel) {
            DischargeEffectPacket fx = new DischargeEffectPacket(fromPoint, hitPoint, spellColor);

            Networking.sendToNearbyClient(serverLevel, BlockPos.containing(hitPoint), fx);
        }
    }

    @Override
    public void onClientReceived(Minecraft minecraft, Player player) {
        if (player != null) {
            Level level = player.level();

                double distance = from.distanceTo(to);
                double start = 0.0, increment = 1.0 / 4.0;
                if (player.position().distanceToSqr(from) < 4.0 && to.subtract(from).normalize().dot(player.getViewVector(1f)) > Mth.SQRT_OF_TWO / 2) {
                    start = Math.min(2.0, distance / 2.0);
                    increment = 1.0 / 8.0;
                }
                for (double d = start; d < distance; d += increment) {
                    double fractionalDistance = d / distance;
                    double speedCoefficient = Mth.lerp(fractionalDistance, 0.2, 0.001);
                    if (true || ModList.get().isLoaded("cofh_core")) {
                        level.addParticle(
                                new ColoredDynamicTypeData(SPARKLE_TYPE.get(), color, 0.5F, 10),
                                Mth.lerp(fractionalDistance, from.x, to.x),
                                Mth.lerp(fractionalDistance, from.y, to.y) + 0.5,
                                Mth.lerp(fractionalDistance, from.z, to.z),
                                (level.random.nextFloat() - 0.25) * speedCoefficient,
                                (level.random.nextFloat() - 0.25) * speedCoefficient,
                                (level.random.nextFloat() - 0.25) * speedCoefficient);
                        level.addParticle(ModParticles.SPARK.get(), Mth.lerp(fractionalDistance, from.x, to.x),
                                Mth.lerp(fractionalDistance, from.y, to.y) + 0.5,
                                Mth.lerp(fractionalDistance, from.z, to.z),
                                (level.random.nextFloat() - 0.5) * speedCoefficient,
                                (level.random.nextFloat() - 0.5) * speedCoefficient,
                                (level.random.nextFloat() - 0.5) * speedCoefficient);
                    } else
                        level.addParticle(
                                new ColoredDynamicTypeData(SPARKLE_TYPE.get(), color, 0.5F, 10),
                                Mth.lerp(fractionalDistance, from.x, to.x),
                                Mth.lerp(fractionalDistance, from.y, to.y) + 0.5,
                                Mth.lerp(fractionalDistance, from.z, to.z),
                                (level.random.nextFloat() - 0.5) * speedCoefficient,
                                (level.random.nextFloat() - 0.5) * speedCoefficient,
                                (level.random.nextFloat() - 0.5) * speedCoefficient);
                }


            }
    }

    public static final CustomPacketPayload.Type<DischargeEffectPacket> TYPE = new CustomPacketPayload.Type<>(ArsNouveau.prefix("discharge_effect"));
    public static final StreamCodec<RegistryFriendlyByteBuf, DischargeEffectPacket> CODEC = StreamCodec.ofMember(DischargeEffectPacket::encode, DischargeEffectPacket::decode);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

