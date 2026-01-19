package unfairweapons.networking;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import unfairweapons.items.block.EldritchSludge;

import static unfairweapons.ItemsRegister.ELDRITCH_SLUDGE_BLOCK;
import static unfairweapons.UnfairWeapons.MOD_ID;

public record SpawnPetrifiedSludgePacket(BlockPos center) implements CustomPacketPayload {

    public static final Type<SpawnPetrifiedSludgePacket> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath(MOD_ID, "place_sludge_grid"));

    public static final StreamCodec<FriendlyByteBuf, SpawnPetrifiedSludgePacket> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC,
                    SpawnPetrifiedSludgePacket::center,
                    SpawnPetrifiedSludgePacket::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(ServerPlayer player) {
        ServerLevel level = (ServerLevel) player.level();

        // Place blocks in a 3x3 grid centered on the player's feet
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                BlockPos targetPos = center.offset(x, 1, z);
                BlockState currentState = level.getBlockState(targetPos);

                // Get your sludge block - adjust this to match your registration
                Block sludgeBlock = ELDRITCH_SLUDGE_BLOCK; // or however you register it

                // If already sludge, add a layer
                if (currentState.getBlock() instanceof EldritchSludge) {
                    int currentLayers = currentState.getValue(EldritchSludge.LAYERS);
                    if (currentLayers < 8) {
                        level.setBlock(targetPos,
                                currentState.setValue(EldritchSludge.LAYERS, currentLayers + 1),
                                3);
                    }
                }
                // If air or replaceable, place new sludge
                else if (currentState.isAir() || canReplace(currentState)) {
                    level.setBlock(targetPos,
                            sludgeBlock.defaultBlockState().setValue(EldritchSludge.LAYERS, 1),
                            3);
                }
            }
        }
    }

    private static boolean canReplace(BlockState state) {
        return state.canBeReplaced() || state.is(net.minecraft.tags.BlockTags.REPLACEABLE);
    }
}