package test;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "test")
public class FullArmorEffectHandler {

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        if (!(event.player instanceof ServerPlayer player)) return;
        boolean fullSet =
                isWearing(player.getInventory().getArmor(3), Items.IRON_HELMET) &&
                isWearing(player.getInventory().getArmor(2), Items.IRON_CHESTPLATE) &&
                isWearing(player.getInventory().getArmor(1), Items.IRON_LEGGINGS) &&
                isWearing(player.getInventory().getArmor(0), Items.IRON_BOOTS);

        if (fullSet) {
            player.removeEffect(MobEffects.POISON);
        } else {
            if (!player.hasEffect(MobEffects.POISON)) {
                player.addEffect(new MobEffectInstance(MobEffects.POISON, 60, 0, true, true));
            }
        }
    }

    private static boolean isWearing(ItemStack stack, ItemStack target) {
        return !stack.isEmpty() && stack.getItem() == target.getItem();
    }

    private static boolean isWearing(ItemStack stack, net.minecraft.world.item.Item targetItem) {
        return !stack.isEmpty() && stack.getItem() == targetItem;
    }
}
