package slimeknights.tconstruct.world.block;

import io.github.fabricators_of_create.porting_lib.util.IPlantable;
import io.github.fabricators_of_create.porting_lib.util.PlantType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import slimeknights.tconstruct.world.TinkerWorld;

public class SlimeDirtBlock extends Block {

  public SlimeDirtBlock(Properties properties) {
    super(properties);
  }

  @Override
  public boolean canSustainPlant(BlockState state, BlockGetter world, BlockPos pos, Direction facing, IPlantable plantable) {
    // can sustain both slimeplants and normal plants
    return plantable.getPlantType(world, pos) == TinkerWorld.SLIME_PLANT_TYPE || plantable.getPlantType(world, pos) == PlantType.PLAINS;
  }
}
