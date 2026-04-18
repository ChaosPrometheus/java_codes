package adventure;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.network.chat.Component;
import net.minecraft.advancements.Advancement;
import net.minecraft.server.MinecraftServer;

import java.util.HashSet;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = "adventure", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PortalBlocker {

    private static final HashSet<UUID> warnedPlayers = new HashSet<>();

    @SubscribeEvent
    public static void onPortalEnter(EntityTravelToDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            ResourceKey<Level> targetDimension = event.getDimension();
            MinecraftServer server = player.server;

            if (server != null) {
                Advancement diamondsAdvancement = server.getAdvancements().getAdvancement(net.minecraft.resources.ResourceLocation.tryParse("minecraft:story/mine_diamond"));
                boolean hasDiamondsAchievement = diamondsAdvancement != null && player.getAdvancements().getOrStartProgress(diamondsAdvancement).isDone();

                if (!hasDiamondsAchievement) {
                    event.setCanceled(true);

                    if (!warnedPlayers.contains(player.getUUID())) {
                        player.sendSystemMessage(Component.literal("Вы не можете использовать порталы, пока не добудете алмазы!"));
                        warnedPlayers.add(player.getUUID()); 
                    }
                    return;
                } else {
                    warnedPlayers.remove(player.getUUID()); 
                }
            }
        }
    }
}

