package overchanter; // поменяй если нужно

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.network.chat.Component;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mod.EventBusSubscriber(modid = "overchanter")
public class OverchanterEvent {

    // ID твоего блока
    private static final ResourceLocation OVERCHANTER_BLOCK_ID = new ResourceLocation("overchanter", "over_chanter");

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        BlockPos pos = event.getPos();
        BlockState blockState = player.level().getBlockState(pos);

        Block block = blockState.getBlock();
        ResourceLocation blockID = ForgeRegistries.BLOCKS.getKey(block);

        // Проверка: является ли блок именно тем, который нужен
        if (!OVERCHANTER_BLOCK_ID.equals(blockID)) {
            return;
        }

        if (player.level().isClientSide()) {
            return; // Только на серверной стороне
        }

        ItemStack itemstack = player.getItemInHand(InteractionHand.MAIN_HAND);
        if (itemstack.isEmpty()) {
            player.displayClientMessage(Component.literal("В руке ничего нет!"), true);
            return;
        }

        if (!itemstack.isEnchanted()) {
            player.displayClientMessage(Component.literal("Этот предмет не зачарован!"), true);
            return;
        }

        Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(itemstack);
        List<Enchantment> notMaxed = new ArrayList<>();
        Enchantment minEnch = null;
        int minLevel = Integer.MAX_VALUE;

        for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
            Enchantment ench = entry.getKey();
            int lvl = entry.getValue();
            if (lvl >= ench.getMaxLevel()) {
                if (lvl < minLevel) {
                    minEnch = ench;
                    minLevel = lvl;
                }
            } else {
                notMaxed.add(ench);
            }
        }

        if (!notMaxed.isEmpty()) {
            player.displayClientMessage(Component.literal("Есть незавершенные зачарования!"), true);
            return;
        }

        if (minEnch != null) {
            int playerXP = player.experienceLevel;
            int cost = 30 + 2 * (minLevel + 1 - minEnch.getMaxLevel());

            if (playerXP >= cost) {
                player.giveExperienceLevels(-cost);

                if (Math.random() < 0.8) { // 80% шанс успеха
                    int increase = (int)(Math.random() * 3) + 1;
                    enchantments.put(minEnch, minLevel + increase);
                    EnchantmentHelper.setEnchantments(enchantments, itemstack);

                    player.displayClientMessage(Component.literal("Уровень зачарования увеличен на " + increase + "!"), true);
                } else {
                    player.displayClientMessage(Component.literal("Неудача! Зачарование не улучшено."), true);
                }
            } else {
                player.displayClientMessage(Component.literal("Недостаточно уровней! Нужно: " + cost + ", у вас: " + playerXP), true);
            }
        } else {
            player.displayClientMessage(Component.literal("Нет доступных зачарований для усиления."), true);
        }

        event.setCanceled(true); // Чтобы клик не прошёл дальше
    }
}
