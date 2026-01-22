package unfairweapons.networking;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import static unfairweapons.UnfairWeapons.MOD_ID;

public record SummonPetrifyingEyePacket() implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<SummonPetrifyingEyePacket> TYPE =
            new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(MOD_ID, "spawn_entity"));

    public static final StreamCodec<FriendlyByteBuf, SummonPetrifyingEyePacket> CODEC =
            StreamCodec.unit(new SummonPetrifyingEyePacket());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}