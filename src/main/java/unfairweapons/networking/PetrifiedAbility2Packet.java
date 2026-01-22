package unfairweapons.networking;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import static unfairweapons.UnfairWeapons.MOD_ID;


public record PetrifiedAbility2Packet() implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<PetrifiedAbility2Packet> TYPE =
            new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(MOD_ID, "petrification_ability_2"));

    public static final StreamCodec<FriendlyByteBuf, PetrifiedAbility2Packet> CODEC =
            StreamCodec.unit(new PetrifiedAbility2Packet());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}