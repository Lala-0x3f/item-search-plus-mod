package dev.itemsearchplus.mixin;

import dev.itemsearchplus.ItemSearchPlus;
import net.minecraft.Util;
import net.minecraft.client.multiplayer.SessionSearchTrees;
import net.minecraft.client.searchtree.FullTextSearchTree;
import net.minecraft.client.searchtree.SearchTree;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

/**
 * After {@link SessionSearchTrees#updateCreativeTooltips} finishes building the vanilla
 * creative-name search tree, replace the tree with one whose text extractor also yields
 * the English display name and the registry id of each stack.
 *
 * <p>The replacement is computed asynchronously, mirroring vanilla's pattern so the UI
 * thread is never blocked.
 */
@Mixin(SessionSearchTrees.class)
public abstract class SessionSearchTreesMixin {
    @Shadow
    private CompletableFuture<SearchTree<ItemStack>> creativeByNameSearch;

    @Inject(method = "updateCreativeTooltips", at = @At("TAIL"))
    private void itemSearchPlus$replaceCreativeNamesTree(HolderLookup.Provider provider,
                                                          List<ItemStack> stacks,
                                                          CallbackInfo ci) {
        Item.TooltipContext ctx = Item.TooltipContext.of(provider);
        TooltipFlag flag = TooltipFlag.Default.NORMAL.asCreative();
        this.creativeByNameSearch = CompletableFuture.supplyAsync(
            () -> new FullTextSearchTree<ItemStack>(
                stack -> ItemSearchPlus.buildTextTokens(ctx, flag, stack),
                stack -> Stream.of(BuiltInRegistries.ITEM.getKey(stack.getItem())),
                stacks
            ),
            Util.backgroundExecutor()
        );
    }
}
