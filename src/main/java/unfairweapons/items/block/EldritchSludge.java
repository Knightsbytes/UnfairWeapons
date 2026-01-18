package unfairweapons.items.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

public class EldritchSludge extends FallingBlock {
    public static final MapCodec<EldritchSludge> CODEC = simpleCodec(EldritchSludge::new);
    public static final int MAX_HEIGHT = 8;
    public static final IntegerProperty LAYERS = BlockStateProperties.LAYERS;
    private static final VoxelShape[] SHAPES = Block.boxes(8, i -> Block.column(16.0, 0.0, i * 2));
    public static final int HEIGHT_IMPASSABLE = 5;

    public EldritchSludge(Properties properties){
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(LAYERS, 1));
    }

    @Override
    public MapCodec<EldritchSludge> codec() {
        return CODEC;
    }

    @Override
    protected boolean isPathfindable(BlockState blockState, PathComputationType pathComputationType) {
        return pathComputationType == PathComputationType.LAND ? (Integer)blockState.getValue(LAYERS) < 5 : false;
    }

    @Override
    protected VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        return SHAPES[blockState.getValue(LAYERS)];
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        return SHAPES[blockState.getValue(LAYERS) - 1];
    }

    @Override
    protected VoxelShape getBlockSupportShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
        return SHAPES[blockState.getValue(LAYERS)];
    }

    @Override
    protected VoxelShape getVisualShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        return SHAPES[blockState.getValue(LAYERS)];
    }

    @Override
    protected boolean useShapeForLightOcclusion(BlockState blockState) {
        return true;
    }

    @Override
    protected float getShadeBrightness(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
        return blockState.getValue(LAYERS) == 8 ? 0.2F : 1.0F;
    }

    @Override
    protected boolean canSurvive(BlockState blockState, LevelReader levelReader, BlockPos blockPos) {
        BlockState blockState2 = levelReader.getBlockState(blockPos.below());
        // Check if the block below can support this sludge
        return blockState2.is(BlockTags.SNOW_LAYER_CAN_SURVIVE_ON)
                || Block.isFaceFull(blockState2.getCollisionShape(levelReader, blockPos.below()), Direction.UP)
                || blockState2.is(this) && (Integer) blockState2.getValue(LAYERS) == 8;
    }

    @Override
    protected BlockState updateShape(
            BlockState blockState,
            LevelReader levelReader,
            ScheduledTickAccess scheduledTickAccess,
            BlockPos blockPos,
            Direction direction,
            BlockPos blockPos2,
            BlockState blockState2,
            RandomSource randomSource
    ) {
        // Schedule a tick to check for falling
        if (direction == Direction.DOWN && !this.canSurvive(blockState, levelReader, blockPos)) {
            scheduledTickAccess.scheduleTick(blockPos, this, this.getDelayAfterPlace());
        }

        return super.updateShape(blockState, levelReader, scheduledTickAccess, blockPos, direction, blockPos2, blockState2, randomSource);
    }

    @Override
    protected void tick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, RandomSource randomSource) {
        // Check if block should fall
        if (!FallingBlock.isFree(serverLevel.getBlockState(blockPos.below())) || blockPos.getY() < serverLevel.getMinY()) {
            return;
        }

        // Create falling block entity with the layer count
        FallingBlockEntity fallingBlockEntity = FallingBlockEntity.fall(serverLevel, blockPos, blockState);
        this.falling(fallingBlockEntity);
    }

    @Override
    public int getDustColor(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
        // Return the color for falling dust particles (brown/sludge color)
        return 0x8B4513; // Saddle brown color for eldritch sludge
    }

    @Override
    public void onLand(Level level, BlockPos blockPos, BlockState fallingState, BlockState hitState, FallingBlockEntity fallingBlockEntity) {
        int fallingLayers = fallingState.getValue(LAYERS);

        // The blockPos is where the entity landed (the space it's trying to occupy)
        // hitState is the block that was at that position

        // Check if we can replace the block at landing position (grass, snow, air)
        if (canReplace(hitState)) {
            level.setBlock(blockPos, fallingState, 3);
        }
        // Check if landing on same block type (stack layers)
        else if (hitState.is(this)) {
            int existingLayers = hitState.getValue(LAYERS);
            int totalLayers = Math.min(8, existingLayers + fallingLayers);
            level.setBlock(blockPos, hitState.setValue(LAYERS, totalLayers), 3);

            // If there are leftover layers, place them above
            int remainingLayers = (existingLayers + fallingLayers) - totalLayers;
            if (remainingLayers > 0) {
                BlockPos abovePos = blockPos.above();
                BlockState aboveState = level.getBlockState(abovePos);
                if (aboveState.isAir() || canReplace(aboveState)) {
                    level.setBlock(abovePos, this.defaultBlockState().setValue(LAYERS, remainingLayers), 3);
                }
            }
        }
        // Otherwise it's a solid block, try to place on top
        else {
            BlockPos abovePos = blockPos.above();
            BlockState aboveState = level.getBlockState(abovePos);
            if (aboveState.isAir() || canReplace(aboveState)) {
                level.setBlock(abovePos, fallingState, 3);
            } else {
                // Can't place anywhere, drop as item
                super.onLand(level, blockPos, fallingState, hitState, fallingBlockEntity);
            }
        }
    }

    // Helper method to check if a block can be replaced by falling sludge
    private boolean canReplace(BlockState state) {
        return state.isAir()
                || state.is(Blocks.TALL_GRASS)
                || state.is(Blocks.SHORT_GRASS)
                || state.is(Blocks.SNOW)
                || state.is(BlockTags.REPLACEABLE);
    }

    @Override
    protected void randomTick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, RandomSource randomSource) {
        if (serverLevel.getBrightness(LightLayer.BLOCK, blockPos) > 11) {
            dropResources(blockState, serverLevel, blockPos);
            serverLevel.removeBlock(blockPos, false);
        }
    }

    @Override
    protected boolean canBeReplaced(BlockState blockState, BlockPlaceContext blockPlaceContext) {
        int i = (Integer)blockState.getValue(LAYERS);
        if (!blockPlaceContext.getItemInHand().is(this.asItem()) || i >= 8) {
            return i == 1;
        } else {
            return blockPlaceContext.replacingClickedOnBlock() ? blockPlaceContext.getClickedFace() == Direction.UP : true;
        }
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
        BlockState blockState = blockPlaceContext.getLevel().getBlockState(blockPlaceContext.getClickedPos());
        if (blockState.is(this)) {
            int i = (Integer)blockState.getValue(LAYERS);
            return blockState.setValue(LAYERS, Math.min(8, i + 1));
        } else {
            return super.getStateForPlacement(blockPlaceContext);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LAYERS);
    }

    // Override to get delay before falling
    protected int getDelayAfterPlace() {
        return 2;
    }
}