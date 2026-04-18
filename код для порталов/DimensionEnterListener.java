package adventure_telling.event;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "adventure_telling")
public class DimensionEnterListener {

    private static final String NBT_KEY = "visited_dimensions";
    private static final ResourceLocation CUSTOM_PERSISTED_KEY = new ResourceLocation("adventure_telling", "persistent_data");

    private static final ResourceLocation AETHER_ID = new ResourceLocation("aether", "the_aether");
    private static final ResourceLocation TWILIGHT_ID = new ResourceLocation("twilightforest", "twilight_forest");

    @SubscribeEvent
    public static void onDimensionChange(PlayerChangedDimensionEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        ResourceLocation toDimension = player.level().dimension().location();

        if (hasVisited(player, toDimension)) return;
        markVisited(player, toDimension);


        } else if (AETHER_ID.equals(toDimension)) {
            sendMysteryMessage(player, "Ты поднялся выше облаков… Но чем выше ты поднимаешься, тем больнее падать.");
        } else if (TWILIGHT_ID.equals(toDimension)) {
            sendMysteryMessage(player, "Сумерки сгущаются... Этот лес дышит магией и древним страхом.");
        }
    }

    private static boolean hasVisited(ServerPlayer player, ResourceLocation dimension) {
        CompoundTag persisted = player.getPersistentData();
        CompoundTag custom = persisted.getCompound(CUSTOM_PERSISTED_KEY.toString());
        CompoundTag visited = custom.getCompound(NBT_KEY);
        return visited.getBoolean(dimension.toString());
    }

    private static void markVisited(ServerPlayer player, ResourceLocation dimension) {
        CompoundTag persisted = player.getPersistentData();
        CompoundTag custom = persisted.getCompound(CUSTOM_PERSISTED_KEY.toString());
        CompoundTag visited = custom.getCompound(NBT_KEY);

        visited.putBoolean(dimension.toString(), true);
        custom.put(NBT_KEY, visited);
        persisted.put(CUSTOM_PERSISTED_KEY.toString(), custom);
    }

    private static void sendMysteryMessage(ServerPlayer player, String messageText) {
        MutableComponent message = Component.literal("[???] ")
            .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xFF0000))) // Красный
            .append(Component.literal(messageText)
            .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xFFFFFF)))); // Белый
        player.sendSystemMessage(message, false);
    }
}
