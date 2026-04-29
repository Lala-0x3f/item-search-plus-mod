package dev.itemsearchplus;

import net.minecraft.ChatFormatting;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Stream;

/**
 * Shared (loader-agnostic) entrypoint helpers.
 *
 * <p>The actual platform mod entrypoints (Fabric / NeoForge) call into here so we keep
 * the bulk of the logic in one place. The Mixin in
 * {@link dev.itemsearchplus.mixin.MinecraftMixin} re-registers the
 * {@code SearchRegistry.CREATIVE_NAMES} tree with {@link #buildTextTokens} as the
 * text extractor, which yields vanilla tooltip lines plus our extra tokens
 * (English display name + registry id path + full id).
 */
public final class ItemSearchPlus {
    public static final String MOD_ID = "itemsearchplus";
    public static final Logger LOGGER = LoggerFactory.getLogger("ItemSearchPlus");

    private ItemSearchPlus() {}

    /**
     * Returns the tokens that should be indexed (substring-searchable) for a creative-tab
     * stack: the vanilla tooltip lines (current language) plus the en_us display name and
     * the registry id (path-only and full {@code namespace:path}).
     */
    public static Stream<String> buildTextTokens(Item.TooltipContext ctx, TooltipFlag flag, ItemStack stack) {
        Stream<String> tooltipTokens;
        try {
            tooltipTokens = stack.getTooltipLines(ctx, null, flag).stream()
                .map(component -> ChatFormatting.stripFormatting(component.getString()))
                .filter(s -> s != null)
                .map(String::trim)
                .filter(s -> !s.isEmpty());
        } catch (Throwable t) {
            // Some mods throw when generating tooltips outside their expected context;
            // fall back to extras only so the index still builds.
            LOGGER.debug("Tooltip extraction failed for {}, falling back to extras only", stack, t);
            tooltipTokens = Stream.empty();
        }
        return Stream.concat(tooltipTokens, EnglishLanguageCache.extraTokens(stack));
    }
}
