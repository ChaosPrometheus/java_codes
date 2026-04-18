package alternative;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.UUID;
import java.util.Random;

@Mod.EventBusSubscriber
public class RealisticWeightSystem {

    private static final UUID SPEED_UUID =
            UUID.fromString("77777777-7777-7777-7777-777777777777");

    private static final double MAX_KG = 120.0;
    private static final Random random = new Random();

    // ===== ТИК ИГРОКА =====
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {

        if (event.phase != TickEvent.Phase.END) return;

        Player player = event.player;
        if (player.level().isClientSide()) return;

        double kg = calculateTotalKg(player);
        double percent = (kg / MAX_KG) * 100.0;

        player.getAttribute(Attributes.MOVEMENT_SPEED)
                .removeModifier(SPEED_UUID);

        // Плавное замедление
        double slow = - (percent / 100.0) * 0.85;

        if (percent > 5) {
            player.getAttribute(Attributes.MOVEMENT_SPEED)
                    .addPermanentModifier(new AttributeModifier(
                            SPEED_UUID,
                            "kg_weight_slow",
                            slow,
                            AttributeModifier.Operation.MULTIPLY_TOTAL
                    ));
        }

        // Блок спринта
        if (percent >= 100) {
            player.setSprinting(false);
        }

        // Снижение прыжка
        if (percent >= 85 && !player.onGround()) {
            player.setDeltaMovement(
                    player.getDeltaMovement().x,
                    player.getDeltaMovement().y * 0.5,
                    player.getDeltaMovement().z
            );
        }

        // Критический перегруз
        if (percent >= 120) {
            player.setDeltaMovement(0, player.getDeltaMovement().y, 0);
        }
    }

    // ===== ТРЯСКА КАМЕРЫ =====
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        double kg = calculateTotalKg(mc.player);
        double percent = (kg / MAX_KG) * 100.0;

        if (percent >= 80) {

            float power = (float)((percent - 80) / 100.0);

            mc.player.setYRot(
                    mc.player.getYRot() + (random.nextFloat() - 0.5f) * power * 3f
            );

            mc.player.setXRot(
                    mc.player.getXRot() + (random.nextFloat() - 0.5f) * power * 3f
            );
        }
    }

    // ===== HUD =====
    @SubscribeEvent
    public static void onRenderOverlay(RenderGuiOverlayEvent.Post event) {

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        double kg = calculateTotalKg(mc.player);
        double percent = (kg / MAX_KG) * 100.0;

        String text = String.format("Вес: %.1f кг (%.0f%%)", kg, percent);

        int y = mc.getWindow().getGuiScaledHeight() - 20;

        int color = 0x00FF00;
        if (percent > 70) color = 0xFFFF00;
        if (percent > 95) color = 0xFF0000;

        GuiGraphics gui = event.getGuiGraphics();
        gui.drawString(mc.font, text, 10, y, color);
    }

    // ===== РАСЧЁТ КГ =====
    private static double calculateTotalKg(Player player) {

        double total = 0;

        for (ItemStack stack : player.getInventory().items) {
            if (!stack.isEmpty()) {
                total += getItemKg(stack) * stack.getCount();
            }
        }

        return total;
    }

    // ===== ВЕС ПРЕДМЕТОВ =====
    private static double getItemKg(ItemStack stack) {

    	if (stack.getItem() == Items.FEATHER) return 0.01;
    	if (stack.getItem() == Items.PAPER) return 0.02;

    	if (stack.getItem() == Items.OAK_LOG) return 1.0;       // было 8
    	if (stack.getItem() == Items.COBBLESTONE) return 2.0;  // было 12
    	if (stack.getItem() == Items.IRON_INGOT) return 3.0;
    	if (stack.getItem() == Items.IRON_BLOCK) return 15.0;  // было 60
    	if (stack.getItem() == Items.NETHERITE_BLOCK) return 25.0;

    	return 0.5;
	}
}