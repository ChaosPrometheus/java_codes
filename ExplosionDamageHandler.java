package boomresist;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Fireball;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class ExplosionDamageHandler {

    @SubscribeEvent
    public static void onExplosion(ExplosionEvent.Detonate event) {
        Explosion explosion = event.getExplosion();
        Entity source = explosion.getDirectSourceEntity();
        Level level = event.getLevel();

        boolean isRelevantExplosion = false;

        // Взрывы крипера или TNT
        if (source instanceof Creeper || source instanceof PrimedTnt) {
            isRelevantExplosion = true;
        }
        // Файербол гаста
        else if (source instanceof Fireball) {
            isRelevantExplosion = true;
        }
        // Кристалл края
        else if (source instanceof EndCrystal) {
            isRelevantExplosion = true;
        }
        // Взрыв кровати (в других измерениях)
        else if (source == null) {
            if (level.dimension() != Level.OVERWORLD) {
                isRelevantExplosion = true;
            }
        }

        if (isRelevantExplosion) {
            double radius = 6.0;
            AABB area = new AABB(
                    explosion.getPosition().x - radius,
                    explosion.getPosition().y - radius,
                    explosion.getPosition().z - radius,
                    explosion.getPosition().x + radius,
                    explosion.getPosition().y + radius,
                    explosion.getPosition().z + radius
            );

            for (Entity entity : level.getEntities(null, area)) {
                if (entity instanceof Player player) {

                    // Основные руки
                    applyDurabilityDamage(player, player.getMainHandItem());
                    applyDurabilityDamage(player, player.getOffhandItem());

                    // ВСЯ броня (включая элитру)
                    for (ItemStack armor : player.getArmorSlots()) {
                        applyDurabilityDamage(player, armor);
                    }
                }
            }
        }
    }

    private static void applyDurabilityDamage(Player player, ItemStack stack) {
        if (stack.isEmpty()) return;

        String name = stack.getItem().toString().toLowerCase();
        int baseDamage = 0;

        // -----------------------------
        // Инструменты (Tiered)
        // -----------------------------
        if (stack.getItem() instanceof TieredItem) {

            if (name.contains("gold")) baseDamage = 8;
            else if (name.contains("wood")) baseDamage = 6;
            else if (name.contains("stone")) baseDamage = 4;
            else if (name.contains("iron")) baseDamage = 3;
            else if (name.contains("diamond")) baseDamage = 2;
            else if (name.contains("netherite")) baseDamage = 1;
            else baseDamage = 3;
        }

        // -----------------------------
        // Дополнительные предметы
        // -----------------------------

        // Удочка
        else if (name.contains("fishing_rod")) {
            baseDamage = 4;
        }

        // Элитра
        else if (name.contains("elytra")) {
            baseDamage = 10;
        }

        // Огниво
        else if (name.contains("flint_and_steel")) {
            baseDamage = 2;
        }

        // Панцирь черепахи
        else if (name.contains("turtle_helmet")) {
            baseDamage = 3;
        }

        // Лук
        else if (name.contains("bow")) {
            baseDamage = 4;
        }

        // Арбалет
        else if (name.contains("crossbow")) {
            baseDamage = 4;
        }

        // Остальное пропускаем
        else {
            return;
        }

        // Применение урона предмету
        if (baseDamage > 0 && stack.isDamageableItem()) {
            stack.hurtAndBreak(baseDamage, player, (p) ->
                    p.broadcastBreakEvent(p.getUsedItemHand())
            );
        }
    }
}
