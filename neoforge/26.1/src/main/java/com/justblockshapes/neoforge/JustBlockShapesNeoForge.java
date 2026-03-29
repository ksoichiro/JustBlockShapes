package com.justblockshapes.neoforge;

import com.justblockshapes.Compat;
import com.justblockshapes.JustBlockShapes;
import com.justblockshapes.ModBlocks;
import com.justblockshapes.ModBlocks.VariantType;
import com.justblockshapes.resource.InMemoryResourcePack;
import com.justblockshapes.resource.RuntimeResourceGenerator;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.server.ServerLifecycleEvents;

@Mod(JustBlockShapes.MOD_ID)
public class JustBlockShapesNeoForge {

    public static final DeferredRegister.Blocks BLOCKS =
        DeferredRegister.createBlocks(JustBlockShapes.MOD_ID);
    public static final DeferredRegister.Items ITEMS =
        DeferredRegister.createItems(JustBlockShapes.MOD_ID);

    public JustBlockShapesNeoForge(IEventBus modEventBus) {
        for (ModBlocks.BlockEntry entry : ModBlocks.getBlockEntries()) {
            Block baseBlock = Compat.tryGetBlock(
                Compat.resourceLocation("minecraft", entry.baseBlockId()));
            if (baseBlock == null) {
                ModBlocks.markSkipped(entry.baseBlockId());
                continue;
            }

            for (VariantType variant : entry.variants()) {
                String id = ModBlocks.variantBlockId(entry.baseBlockId(), variant);

                var blockHolder = BLOCKS.register(id,
                    () -> ModBlocks.createVariantBlock(variant, baseBlock, id));
                ITEMS.register(id,
                    () -> new BlockItem(blockHolder.get(), Compat.createItemProperties(JustBlockShapes.MOD_ID, id)));

                // Register in common map after deferred registration resolves
                modEventBus.addListener((FMLCommonSetupEvent event) -> {
                    ModBlocks.register(id, blockHolder.get());
                });
            }
        }

        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        modEventBus.addListener(this::addCreative);
        modEventBus.addListener(this::addCompatPack);
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
            for (Block block : ModBlocks.getRegisteredBlocks().values()) {
                event.accept(block);
            }
        }
    }

    private void addCompatPack(FMLCommonSetupEvent event) {
        // Add compat pack via client-side resource loading
        // This is called on both client and server, but we only need it on client
        // The compat pack will be loaded through the resource system
    }
}
