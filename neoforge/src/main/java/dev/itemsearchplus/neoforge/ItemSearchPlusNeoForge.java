package dev.itemsearchplus.neoforge;

import dev.itemsearchplus.EnglishLanguageCache;
import dev.itemsearchplus.EnglishReloadListener;
import dev.itemsearchplus.ItemSearchPlus;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mod(value = ItemSearchPlus.MOD_ID, dist = Dist.CLIENT)
public final class ItemSearchPlusNeoForge {
    public ItemSearchPlusNeoForge(IEventBus modBus) {
        ItemSearchPlus.LOGGER.info("Initializing {} (NeoForge)", ItemSearchPlus.MOD_ID);
        modBus.addListener(RegisterClientReloadListenersEvent.class, event ->
                event.registerReloadListener(new NeoForgeEnglishReloadListener()));
    }

    /** Bridges our shared listener to NeoForge's PreparableReloadListener via composition. */
    private static final class NeoForgeEnglishReloadListener implements PreparableReloadListener {
        @Override
        public CompletableFuture<Void> reload(
                PreparableReloadListener.PreparationBarrier preparationBarrier,
                ResourceManager resourceManager,
                ProfilerFiller profiler,
                ProfilerFiller profiler2,
                Executor executor,
                Executor executor2) {
            return CompletableFuture.runAsync(() -> EnglishLanguageCache.reload(resourceManager), executor)
                    .thenCompose(preparationBarrier::wait);
        }

        @Override
        public String getName() {
            return EnglishReloadListener.ID.toString();
        }
    }
}
