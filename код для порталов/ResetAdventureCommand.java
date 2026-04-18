package adventure_telling.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class ResetAdventureCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            Commands.literal("reset_adventure")
                .requires(source -> source.hasPermission(2))
                .executes(ctx -> {
                    CommandSourceStack source = ctx.getSource();
                    Player player = source.getPlayerOrException();

                    CompoundTag persistent = player.getPersistentData().getCompound(Player.PERSISTED_NBT_TAG);
                    persistent.remove("visited_dimensions");
                    player.getPersistentData().put(Player.PERSISTED_NBT_TAG, persistent);

                    player.sendSystemMessage(Component.literal("Прогресс приключения сброшен."));
                    return 1;
                })
        );
    }
}
