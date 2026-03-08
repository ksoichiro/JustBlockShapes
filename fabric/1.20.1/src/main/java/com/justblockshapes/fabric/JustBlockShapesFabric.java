package com.justblockshapes.fabric;

import com.justblockshapes.Compat;
import com.justblockshapes.JustBlockShapes;
import com.justblockshapes.ModBlocks;
import com.justblockshapes.ModBlocks.VariantType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class JustBlockShapesFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        for (ModBlocks.BlockEntry entry : ModBlocks.getBlockEntries()) {
            Block baseBlock = Compat.getBlock(
                Compat.resourceLocation("minecraft", entry.baseBlockId()));

            for (VariantType variant : entry.variants()) {
                String id = ModBlocks.variantBlockId(entry.baseBlockId(), variant);
                Block block = ModBlocks.createVariantBlock(variant, baseBlock, id);

                var resLoc = Compat.resourceLocation(
                    JustBlockShapes.MOD_ID, id);
                Registry.register(BuiltInRegistries.BLOCK, resLoc, block);
                Registry.register(BuiltInRegistries.ITEM, resLoc,
                    new BlockItem(block, Compat.createItemProperties(JustBlockShapes.MOD_ID, id)));

                ModBlocks.register(id, block);
            }
        }

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.BUILDING_BLOCKS).register(entries -> {
            for (Block block : ModBlocks.getRegisteredBlocks().values()) {
                entries.accept(block);
            }
        });
    }
}
