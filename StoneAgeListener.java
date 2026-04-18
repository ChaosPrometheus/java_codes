package adventure.event;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "adventure")
public class StoneAgeListener {

    private static final ResourceLocation STONE_AGE_ID = new ResourceLocation("minecraft", "story/mine_stone");
    private static final ResourceLocation DIAMOND_AGE_ID = new ResourceLocation("minecraft", "story/mine_diamond");
    private static final ResourceLocation NETHER_AGE_ID = new ResourceLocation("minecraft", "story/enter_the_nether");
    private static final ResourceLocation BASTION_ID = new ResourceLocation("minecraft", "nether/find_bastion");
    private static final ResourceLocation END_AGE_ID = new ResourceLocation("minecraft", "story/enter_the_end");

    @SubscribeEvent
    public static void onAdvancement(AdvancementEvent.AdvancementEarnEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        ResourceLocation advancementId = event.getAdvancement().getId();

        if (STONE_AGE_ID.equals(advancementId)) {
            MutableComponent message = Component.literal("[???] ")
                    .setStyle(Style.EMPTY.withColor(TextColor.parseColor("#ff0000"))) 
                    .append(Component.literal("Я и не знал, что в этом мире кто-то еще жив.")
                            .setStyle(Style.EMPTY.withColor(TextColor.parseColor("#FFFFFF"))));
            player.sendSystemMessage(message, false);

        } else if (DIAMOND_AGE_ID.equals(advancementId)) {
            MutableComponent message = Component.literal("[???] ")
                    .setStyle(Style.EMPTY.withColor(TextColor.parseColor("#ff0000"))) 
                    .append(Component.literal("Алмазы… Ты думаешь, что этого достаточно?")
                            .setStyle(Style.EMPTY.withColor(TextColor.parseColor("#FFFFFF")))); 
            player.sendSystemMessage(message, false);

        } else if (NETHER_AGE_ID.equals(advancementId)) {
            MutableComponent message = Component.literal("[???] ")
                    .setStyle(Style.EMPTY.withColor(TextColor.parseColor("#ff0000"))) 
                    .append(Component.literal("Ты пересёк границу миров. Здесь твои ошибки могут стоить тебе всего.")
                            .setStyle(Style.EMPTY.withColor(TextColor.parseColor("#FFFFFF"))));
            player.sendSystemMessage(message, false);

        } else if (BASTION_ID.equals(advancementId)) {
            MutableComponent message = Component.literal("[???] ")
                    .setStyle(Style.EMPTY.withColor(TextColor.parseColor("#ff0000"))) 
                    .append(Component.literal("Ты идёшь по следам тех, кого уже нет… Они тоже думали, что смогут всё.")
                            .setStyle(Style.EMPTY.withColor(TextColor.parseColor("#FFFFFF")))); 
            player.sendSystemMessage(message, false);

        } else if (END_AGE_ID.equals(advancementId)) {
            MutableComponent message = Component.literal("[???] ")
                    .setStyle(Style.EMPTY.withColor(TextColor.parseColor("#ff0000"))) 
                    .append(Component.literal("Ты приблизился к разгадке… или к ловушке?")
                            .setStyle(Style.EMPTY.withColor(TextColor.parseColor("#FFFFFF")))); 
            player.sendSystemMessage(message, false);
            
        }
    }
}
