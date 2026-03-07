package com.justmissingblocks.neoforge;

import com.justmissingblocks.Compat;
import com.justmissingblocks.JustMissingBlocks;
import com.justmissingblocks.ModBlocks;
import com.justmissingblocks.ModBlocks.VariantType;
import com.justmissingblocks.compat.BiomesOPlentyCompat;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(JustMissingBlocks.MOD_ID)
public class JustMissingBlocksNeoForge {

    public static final DeferredRegister.Blocks BLOCKS =
        DeferredRegister.createBlocks(JustMissingBlocks.MOD_ID);
    public static final DeferredRegister.Items ITEMS =
        DeferredRegister.createItems(JustMissingBlocks.MOD_ID);

    public JustMissingBlocksNeoForge(IEventBus modEventBus) {
        for (ModBlocks.BlockEntry entry : ModBlocks.getBlockEntries()) {
            Block baseBlock = Compat.getBlock(
                Compat.resourceLocation("minecraft", entry.baseBlockId()));

            for (VariantType variant : entry.variants()) {
                String id = ModBlocks.variantBlockId(entry.baseBlockId(), variant);

                var blockHolder = BLOCKS.register(id,
                    () -> ModBlocks.createVariantBlock(variant, baseBlock, id));
                ITEMS.register(id,
                    () -> new BlockItem(blockHolder.get(), Compat.createItemProperties(JustMissingBlocks.MOD_ID, id)));

                // Register in common map after deferred registration resolves
                modEventBus.addListener((net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent event) -> {
                    ModBlocks.register(id, blockHolder.get());
                });
            }
        }

        if (ModList.get().isLoaded(BiomesOPlentyCompat.MOD_ID)) {
            for (BiomesOPlentyCompat.CompatBlockEntry entry : BiomesOPlentyCompat.getEntries()) {
                Block baseBlock = Compat.getBlock(
                    Compat.resourceLocation(entry.modId(), entry.baseBlockId()));

                for (VariantType variant : entry.variants()) {
                    String id = ModBlocks.variantBlockId(entry.baseBlockId(), variant);

                    var blockHolder = BLOCKS.register(id,
                        () -> ModBlocks.createVariantBlock(variant, baseBlock, id));
                    ITEMS.register(id,
                        () -> new BlockItem(blockHolder.get(), Compat.createItemProperties(JustMissingBlocks.MOD_ID, id)));

                    modEventBus.addListener((net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent event) -> {
                        ModBlocks.register(id, blockHolder.get());
                    });
                }
            }
        }

        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        modEventBus.addListener(this::addCreative);
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
            for (Block block : ModBlocks.getRegisteredBlocks().values()) {
                event.accept(block);
            }
        }
    }
}
