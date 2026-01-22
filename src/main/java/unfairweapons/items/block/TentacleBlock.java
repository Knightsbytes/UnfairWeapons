package unfairweapons.items.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class TentacleBlock extends Block implements SimpleWaterloggedBlock{
    public static final MapCodec<TentacleBlock> CODEC = simpleCodec(TentacleBlock::new);
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final IntegerProperty LEVEL = BlockStateProperties.LEVEL;
    public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;
    public static final Map<Direction, VoxelShape> SHAPES = Shapes.rotateHorizontal(Block.boxZ(16.0, 13.0, 16.0));

    @Override
    public MapCodec<TentacleBlock> codec() {
        return CODEC;
    }

    public TentacleBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(
                this.stateDefinition.any()
                        .setValue(WATERLOGGED, false)
                        .setValue(LEVEL, 0)
                        .setValue(FACING, Direction.NORTH)
        );
    }

    @Override
    protected boolean propagatesSkylightDown(BlockState blockState) {
        return blockState.getFluidState().isEmpty();
    }

    @Override
    protected RenderShape getRenderShape(BlockState blockState) {
        return RenderShape.INVISIBLE;
    }

    @Override
    protected float getShadeBrightness(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
        return 1.0F;
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
        if ((Boolean)blockState.getValue(WATERLOGGED)) {
            scheduledTickAccess.scheduleTick(blockPos, Fluids.WATER, Fluids.WATER.getTickDelay(levelReader));
        }

        return super.updateShape(blockState, levelReader, scheduledTickAccess, blockPos, direction, blockPos2, blockState2, randomSource);
    }

    @Override
    public VoxelShape getCollisionShape(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            CollisionContext context
    ) {
        return Shapes.empty();
    }

    @Override
    protected FluidState getFluidState(BlockState blockState) {
        return blockState.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(blockState);
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return true;
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        AABB box = new AABB(pos).inflate(0.1);
        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, box);

        for (LivingEntity entity : entities) {
            Vec3 velocity = entity.getDeltaMovement();
            double horizontalSpeed = Math.sqrt(velocity.x * velocity.x + velocity.z * velocity.z);
            float damage = (float) (horizontalSpeed * 10.0);

            if (damage > 0.5f) {
                entity.hurt(level.damageSources().generic(), damage);
                entity.setDeltaMovement(velocity.x * 0.1, velocity.y, velocity.z * 0.1);
            }
        }
    }

    @Override
    public VoxelShape getShape(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            CollisionContext context
    ) {
        return Shapes.block();
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
        return this.defaultBlockState()
                .setValue(WATERLOGGED, blockPlaceContext.getLevel().getFluidState(blockPlaceContext.getClickedPos()).getType() == Fluids.WATER);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED, LEVEL, FACING);
    }

    @Override
    public ItemStack pickupBlock(@Nullable LivingEntity livingEntity, LevelAccessor levelAccessor, BlockPos blockPos, BlockState blockState) {
        return livingEntity instanceof Player player && player.isCreative()
                ? SimpleWaterloggedBlock.super.pickupBlock(livingEntity, levelAccessor, blockPos, blockState)
                : ItemStack.EMPTY;
    }

    @Override
    public boolean canPlaceLiquid(@Nullable LivingEntity livingEntity, BlockGetter blockGetter, BlockPos blockPos, BlockState blockState, Fluid fluid) {
        return livingEntity instanceof Player player && player.isCreative()
                ? SimpleWaterloggedBlock.super.canPlaceLiquid(livingEntity, blockGetter, blockPos, blockState, fluid)
                : false;
    }
}
