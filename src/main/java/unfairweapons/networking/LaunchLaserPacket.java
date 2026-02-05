package unfairweapons.networking;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import static unfairweapons.UnfairWeapons.MOD_ID;

public class LaunchLaserPacket implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<LaunchLaserPacket> TYPE =
            new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(MOD_ID, "spawn_entity"));

    public static final StreamCodec<FriendlyByteBuf, LaunchLaserPacket> CODEC =
            StreamCodec.unit(new LaunchLaserPacket());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
