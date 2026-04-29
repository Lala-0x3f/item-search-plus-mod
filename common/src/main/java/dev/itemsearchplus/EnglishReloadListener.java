package dev.itemsearchplus;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

/**
 * Synchronous client-side reload listener that rebuilds {@link EnglishLanguageCache}.
 * Both Fabric and NeoForge entrypoints register an instance of this class.
 */
public class EnglishReloadListener implements ResourceManagerReloadListener {
    public static final ResourceLocation ID =
            ResourceLocation.fromNamespaceAndPath(ItemSearchPlus.MOD_ID, "english_lang_cache");

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {
        EnglishLanguageCache.reload(resourceManager);
    }
}
