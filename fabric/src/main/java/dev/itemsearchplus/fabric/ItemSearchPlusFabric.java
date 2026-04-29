package dev.itemsearchplus.fabric;

import dev.itemsearchplus.EnglishLanguageCache;
import dev.itemsearchplus.EnglishReloadListener;
import dev.itemsearchplus.ItemSearchPlus;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

public final class ItemSearchPlusFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ItemSearchPlus.LOGGER.info("Initializing {} (Fabric)", ItemSearchPlus.MOD_ID);

        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES)
                .registerReloadListener(new FabricListener());
    }

    /** Bridges our shared listener to Fabric's identifiable variant via composition. */
    private static final class FabricListener
            implements IdentifiableResourceReloadListener, ResourceManagerReloadListener {
        @Override
        public ResourceLocation getFabricId() {
            return EnglishReloadListener.ID;
        }

        @Override
        public void onResourceManagerReload(ResourceManager resourceManager) {
            EnglishLanguageCache.reload(resourceManager);
        }
    }
}
