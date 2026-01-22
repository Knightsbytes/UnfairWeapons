package unfairweapons.items.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.player.Player;
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
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

import static net.minecraft.world.effect.MobEffects.*;
import static unfairweapons.UnfairWeapons.MOD_ID;
import static unfairweapons.UnfairWeapons.PETRIFICATION_EFFECT;

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
        BlockPos belowPos = blockPos.below();
        BlockState belowState = serverLevel.getBlockState(belowPos);

        if (blockPos.getY() < serverLevel.getMinY()) {
            return;
        }

        // Check if the space below is free or replaceable
        if (!FallingBlock.isFree(belowState)) {
            return;
        }

        // Create falling block entity with the layer count
        FallingBlockEntity fallingBlockEntity = FallingBlockEntity.fall(serverLevel, blockPos, blockState);
        this.falling(fallingBlockEntity);
    }

    @Override
    protected void falling(FallingBlockEntity fallingBlockEntity) {
        fallingBlockEntity.setHurtsEntities(0.0f, 0);
        fallingBlockEntity.dropItem = false;
        super.falling(fallingBlockEntity);
    }

    @Override
    public void onBrokenAfterFall(Level level, BlockPos blockPos, FallingBlockEntity fallingBlockEntity) {
        // This is called when the falling entity lands!
        BlockState fallingState = fallingBlockEntity.getBlockState();
        int fallingLayers = fallingState.getValue(LAYERS);


        BlockState hitState = level.getBlockState(blockPos);

        // Check if we can replace the block at landing position (grass, snow, air)
        if (canReplace(hitState)) {
            level.setBlock(blockPos, fallingState, 3);
        }
        // Check if landing on same block type (stack layers)
        else if (hitState.getBlock() instanceof EldritchSludge) {
            int existingLayers = hitState.getValue(LAYERS);
            int totalLayers = Math.min(8, existingLayers + fallingLayers);
            System.out.println("Stacking! Existing: " + existingLayers + " + Falling: " + fallingLayers + " = " + totalLayers);
            level.setBlock(blockPos, hitState.setValue(LAYERS, totalLayers), 3);

            // If there are leftover layers, place them above
            int remainingLayers = (existingLayers + fallingLayers) - totalLayers;
            if (remainingLayers > 0) {
                System.out.println("Overflow! Placing " + remainingLayers + " layers above");
                BlockPos abovePos = blockPos.above();
                BlockState aboveState = level.getBlockState(abovePos);
                if (aboveState.isAir() || canReplace(aboveState)) {
                    level.setBlock(abovePos, this.defaultBlockState().setValue(LAYERS, remainingLayers), 3);
                }
            }
        }
        // Otherwise it's a solid block, try to place on top
        else {
            System.out.println("Solid block, placing on top");
            BlockPos abovePos = blockPos.above();
            BlockState aboveState = level.getBlockState(abovePos);
            if (aboveState.isAir() || canReplace(aboveState)) {
                level.setBlock(abovePos, fallingState, 3);
            } else {
                System.out.println("Can't place anywhere, calling super");
                // Can't place anywhere, use default behavior
                super.onBrokenAfterFall(level, blockPos, fallingBlockEntity);
            }
        }
        System.out.println("===========================");
    }



    @Override
    public int getDustColor(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
        // Return the color for falling dust particles (brown/sludge color)
        return 0x8B4513; // Saddle brown color for eldritch sludge
    }

    // Helper method to check if a block can be replaced by falling sludge
    private boolean canReplace(BlockState state) {
        return state.isAir()
                || state.is(Blocks.GRASS_BLOCK)
                || state.is(Blocks.TALL_GRASS)
                || state.is(Blocks.SHORT_GRASS)
                || state.is(Blocks.SNOW)
                || state.is(BlockTags.REPLACEABLE);
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

    @Override
    public float getSpeedFactor() {
        return this.speedFactor;
    }

    @Override
    protected void entityInside(
            BlockState blockState, Level level, BlockPos blockPos, Entity entity, InsideBlockEffectApplier insideBlockEffectApplier, boolean bl
    ) {
        if (!level.isClientSide() && entity instanceof Player player) {
            if (!player.hasEffect(PETRIFICATION_EFFECT)) {
                player.addEffect(new MobEffectInstance(
                        SLOWNESS,
                        1,
                        1
                ));

                player.addEffect(new MobEffectInstance(
                        WEAKNESS,
                        1,
                        1
                ));

                boolean onFire = player.isOnFire();
                onFire = true;
            }
            else {
                player.addEffect(new MobEffectInstance(
                        SPEED,
                        1,
                        1
                ));

                player.addEffect(new MobEffectInstance(
                        STRENGTH,
                        1,
                        1
                ));
            }

        }
    }
}