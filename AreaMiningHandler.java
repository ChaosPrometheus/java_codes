package adventure_telling;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "adventure_telling", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class AreaMiningHandler {

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (!(event.getPlayer() instanceof ServerPlayer player)) return;

        // Проверим, что предмет в руке — кирка (TieredItem)
        ItemStack heldItem = player.getMainHandItem();
        if (!(heldItem.getItem() instanceof TieredItem)) return;

        Level level = player.level();
        if (level.isClientSide) return;

        BlockPos origin = event.getPos();
        Vec3 viewVec = player.getLookAngle();

        Direction direction = Direction.getNearest(viewVec.x, viewVec.y, viewVec.z);

        int[][] offsets;

        switch (direction) {
            case DOWN, UP -> offsets = new int[][] {
                {-1, 0, -1}, {0, 0, -1}, {1, 0, -1},
                {-1, 0,  0}, {0, 0,  0}, {1, 0,  0},
                {-1, 0,  1}, {0, 0,  1}, {1, 0,  1},
            };
            case NORTH, SOUTH -> offsets = new int[][] {
                {-1, -1, 0}, {0, -1, 0}, {1, -1, 0},
                {-1,  0, 0}, {0,  0, 0}, {1,  0, 0},
                {-1,  1, 0}, {0,  1, 0}, {1,  1, 0},
            };
            case WEST, EAST -> offsets = new int[][] {
                {0, -1, -1}, {0, -1, 0}, {0, -1, 1},
                {0,  0, -1}, {0,  0, 0}, {0,  0, 1},
                {0,  1, -1}, {0,  1, 0}, {0,  1, 1},
            };
            default -> offsets = new int[0][0];
        }

        for (int[] offset : offsets) {
            BlockPos targetPos = origin.offset(offset[0], offset[1], offset[2]);

            if (targetPos.equals(origin)) continue;

            BlockState targetState = level.getBlockState(targetPos);
            Block block = targetState.getBlock();

            // Проверим, можно ли разрушить блок и не является ли он воздухом
            if (!targetState.isAir() && targetState.getDestroySpeed(level, targetPos) >= 0) {
                level.destroyBlock(targetPos, true, player);
            }
        }
    }
}
