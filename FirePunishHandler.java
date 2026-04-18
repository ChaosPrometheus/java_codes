/**
 * The code of this mod element is always locked.
 *
 * You can register new events in this class too.
 *
 * If you want to make a plain independent class, create it using
 * Project Browser -> New... and make sure to make the class
 * outside alternative as this package is managed by MCreator.
 *
 * If you change workspace package, modid or prefix, you will need
 * to manually adapt this file to these changes or remake it.
 *
 * This class will be added in the mod root package.
*/
package alternative;

import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.damagesource.DamageSource;

@Mod.EventBusSubscriber
public class FirePunishHandler {

    @SubscribeEvent
    public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        Player player = event.getEntity();

        if (player == null) return;

        // Проверяем, что блок — огонь
        if (event.getLevel().getBlockState(event.getPos()).getBlock() == Blocks.FIRE) {

            // Проверяем, что рука пустая
            ItemStack mainHand = player.getMainHandItem();
            if (mainHand.isEmpty()) {

                // Наносим 2 HP урона (1 сердечко = 2 HP)
                player.hurt(player.damageSources().generic(), 2.0F);
            }
        }
    }
}