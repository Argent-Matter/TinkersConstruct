package slimeknights.tconstruct.shared.block;

import io.github.fabricators_of_create.porting_lib.tags.ToolTags;
import lombok.Getter;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.shared.TinkerCommons;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.Random;

public class WeatheringPlatformBlock extends PlatformBlock implements WeatheringCopper {
  @Getter
  private final WeatherState age;
  public WeatheringPlatformBlock(WeatherState age, Properties props) {
    super(props);
    this.age = age;
    UseBlockCallback.EVENT.register(this::getToolModifiedState);
  }

  @Override
  protected boolean verticalConnect(BlockState state) {
    return state.is(TinkerTags.Blocks.COPPER_PLATFORMS);
  }

  @SuppressWarnings("deprecation")
  @Deprecated
  @Override
  public void randomTick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
    this.onRandomTick(pState, pLevel, pPos, pRandom);
  }

  /** Gets the next state for weathering */
  @Nullable
  private static WeatherState getNext(WeatherState original) {
    return switch (original) {
      case UNAFFECTED -> WeatherState.EXPOSED;
      case EXPOSED -> WeatherState.WEATHERED;
      case WEATHERED -> WeatherState.OXIDIZED;
      default -> null;
    };
  }

  @Override
  public boolean isRandomlyTicking(BlockState pState) {
    return getNext(age) != null;
  }

  @Override
  public Optional<BlockState> getNext(BlockState state) {
    return Optional.ofNullable(getNext(age))
                   .map(next -> TinkerCommons.copperPlatform.get(next).withPropertiesOf(state));
  }

  /** Gets the next state for weathering */
  @Nullable
  private static WeatherState getPrevious(WeatherState original) {
    return switch (original) {
      case EXPOSED -> WeatherState.UNAFFECTED;
      case WEATHERED -> WeatherState.EXPOSED;
      case OXIDIZED -> WeatherState.WEATHERED;
      default -> null;
    };
  }

  public InteractionResult getToolModifiedState(Player player, Level world, InteractionHand hand, BlockHitResult hitResult) {
    BlockState state = world.getBlockState(hitResult.getBlockPos());
    if (player.getItemInHand(hand).is(ToolTags.AXES) && state.is(this)) {
      WeatherState prev = getPrevious(age);
      if (prev != null) {
        world.setBlockAndUpdate(hitResult.getBlockPos(), TinkerCommons.copperPlatform.get(prev).withPropertiesOf(state));
        return InteractionResult.SUCCESS;
      }
    }
    return InteractionResult.PASS;
  }

  @Override
  public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
    ItemStack stack = player.getItemInHand(hand);
    if (stack.getItem() == Items.HONEYCOMB) {
      if (player instanceof ServerPlayer serverPlayer) {
        CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger(serverPlayer, pos, stack);
      }
      if (!player.isCreative()) {
        stack.shrink(1);
      }
      level.setBlock(pos, TinkerCommons.waxedCopperPlatform.get(age).withPropertiesOf(state), 11);
      level.levelEvent(player, 3003, pos, 0);
      return InteractionResult.sidedSuccess(level.isClientSide);
    }
    return InteractionResult.PASS;
  }
}
