package unfairweapons.networking;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import static unfairweapons.UnfairWeapons.MOD_ID;


public record ApplyPetrification3Packet() implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ApplyPetrification3Packet> TYPE =
            new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(MOD_ID, "apply_effect"));

    public static final StreamCodec<FriendlyByteBuf, ApplyPetrification3Packet> CODEC =
            StreamCodec.unit(new ApplyPetrification3Packet());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}