package dev.itemsearchplus.neoforge;

import dev.itemsearchplus.EnglishReloadListener;
import dev.itemsearchplus.ItemSearchPlus;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;

@Mod(value = ItemSearchPlus.MOD_ID, dist = Dist.CLIENT)
public final class ItemSearchPlusNeoForge {
    public ItemSearchPlusNeoForge(IEventBus modBus) {
        ItemSearchPlus.LOGGER.info("Initializing {} (NeoForge)", ItemSearchPlus.MOD_ID);
        modBus.addListener(RegisterClientReloadListenersEvent.class, event ->
                event.registerReloadListener(new EnglishReloadListener()));
    }
}
