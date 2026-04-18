package adventure;

import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class BotStartup {
    @SubscribeEvent
    public static void onServerStart(ServerStartedEvent event) {
        TelegramBot.startBot(event.getServer());
    }
}
