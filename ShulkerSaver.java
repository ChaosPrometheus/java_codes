package adventure;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.item.ItemExpireEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod("adventure") 
public class ShulkerSaver {

    public ShulkerSaver() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onItemExpire(ItemExpireEvent event) {
        ItemEntity entityItem = event.getEntity(); 
        ItemStack itemStack = entityItem.getItem();

        if (itemStack.getItem() instanceof BlockItem blockItem &&
            blockItem.getBlock() instanceof ShulkerBoxBlock) {

            CompoundTag blockEntityTag = itemStack.getTagElement("BlockEntityTag");
            if (blockEntityTag != null && blockEntityTag.contains("Items", Tag.TAG_LIST)) {
                ListTag itemList = blockEntityTag.getList("Items", Tag.TAG_COMPOUND);

                for (Tag itemTag : itemList) {
                    if (itemTag instanceof CompoundTag tag) {
                        ItemStack containedStack = ItemStack.of(tag);
                        ItemEntity dropped = new ItemEntity(
                            entityItem.level(),
                            entityItem.getX(),
                            entityItem.getY(),
                            entityItem.getZ(),
                            containedStack
                        );
                        entityItem.level().addFreshEntity(dropped);
                    }
                }
            }
        }
    }
}
