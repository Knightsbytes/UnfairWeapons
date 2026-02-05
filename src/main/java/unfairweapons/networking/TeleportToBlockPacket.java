package unfairweapons.networking;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import static unfairweapons.UnfairWeapons.MOD_ID;

public record TeleportToBlockPacket() implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<TeleportToBlockPacket> TYPE =
            new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(MOD_ID, "teleport_to_block"));

    public static final StreamCodec<FriendlyByteBuf, TeleportToBlockPacket> CODEC =
            StreamCodec.unit(new TeleportToBlockPacket());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
