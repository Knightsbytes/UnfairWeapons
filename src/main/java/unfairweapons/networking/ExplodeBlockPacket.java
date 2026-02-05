package unfairweapons.networking;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import static unfairweapons.UnfairWeapons.MOD_ID;

public record ExplodeBlockPacket() implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ExplodeBlockPacket> TYPE =
            new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(MOD_ID, "explode_block"));

    public static final StreamCodec<FriendlyByteBuf, ExplodeBlockPacket> CODEC =
            StreamCodec.unit(new ExplodeBlockPacket());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
