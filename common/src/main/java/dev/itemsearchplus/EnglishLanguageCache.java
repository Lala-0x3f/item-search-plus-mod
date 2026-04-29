package dev.itemsearchplus;

import com.google.common.collect.ImmutableMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.locale.Language;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Loads {@code lang/en_us.json} from every namespace in the active resource pack stack
 * and exposes a quick lookup by translation key. Refreshed via the platform-specific
 * client resource reload listener.
 */
public final class EnglishLanguageCache {
    private static volatile Map<String, String> englishMap = Map.of();

    private EnglishLanguageCache() {}

    /** Re-scan en_us.json files. Safe to call from the resource reload thread. */
    public static void reload(ResourceManager resourceManager) {
        Map<String, String> out = new HashMap<>();
        for (String namespace : resourceManager.getNamespaces()) {
            ResourceLocation rl;
            try {
                rl = ResourceLocation.fromNamespaceAndPath(namespace, "lang/en_us.json");
            } catch (Exception e) {
                continue;
            }
            List<Resource> stack = resourceManager.getResourceStack(rl);
            for (Resource res : stack) {
                try (InputStream is = res.open()) {
                    Language.loadFromJson(is, out::put);
                } catch (Exception e) {
                    ItemSearchPlus.LOGGER.debug("Skipping unreadable lang file {} from {}", rl, res.sourcePackId(), e);
                }
            }
        }
        englishMap = ImmutableMap.copyOf(out);
        ItemSearchPlus.LOGGER.info("Loaded {} English translation entries", englishMap.size());
    }

    /** @return the English string for the given translation key, or {@code null} if absent. */
    public static String getEnglish(String descriptionId) {
        return englishMap.get(descriptionId);
    }

    /**
     * Extra search tokens for an item stack: lower-cased English name, registry path,
     * and full {@code namespace:path} id. All are substring-indexable by SuffixArray.
     */
    public static Stream<String> extraTokens(ItemStack stack) {
        Item item = stack.getItem();
        ResourceLocation key = BuiltInRegistries.ITEM.getKey(item);
        String english = englishMap.get(item.getDescriptionId());

        Stream.Builder<String> b = Stream.builder();
        if (english != null && !english.isEmpty()) {
            b.add(english.toLowerCase(Locale.ROOT));
        }
        if (key != null) {
            b.add(key.getPath());
            b.add(key.toString());
        }
        return b.build();
    }
}
