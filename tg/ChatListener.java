package adventure;

import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "adventure", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ChatListener {
    @SubscribeEvent
    public static void onChatMessage(ServerChatEvent event) {
        String playerName = event.getPlayer().getName().getString();
        String message = event.getMessage().getString();
        TelegramBot.sendMessage("💬 " + playerName + ": " + message);
    }
}
