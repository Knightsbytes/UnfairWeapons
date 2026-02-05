package unfairweapons.networking;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import static unfairweapons.UnfairWeapons.MOD_ID;

public record UnboundChainsPacket() implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<UnboundChainsPacket> TYPE =
            new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(MOD_ID, "unbound_chains"));

    public static final StreamCodec<FriendlyByteBuf, UnboundChainsPacket> CODEC =
            StreamCodec.unit(new UnboundChainsPacket());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}