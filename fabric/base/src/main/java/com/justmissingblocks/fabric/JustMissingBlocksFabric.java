package com.justmissingblocks.fabric;

import com.justmissingblocks.JustMissingBlocks;
import com.justmissingblocks.ModBlocks;
import com.justmissingblocks.ModBlocks.VariantType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class JustMissingBlocksFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        for (ModBlocks.BlockEntry entry : ModBlocks.getBlockEntries()) {
            Block baseBlock = BuiltInRegistries.BLOCK.get(
                ResourceLocation.fromNamespaceAndPath("minecraft", entry.baseBlockId()));

            for (VariantType variant : entry.variants()) {
                String id = ModBlocks.variantBlockId(entry.baseBlockId(), variant);
                Block block = ModBlocks.createVariantBlock(variant, baseBlock);

                ResourceLocation resLoc = ResourceLocation.fromNamespaceAndPath(
                    JustMissingBlocks.MOD_ID, id);
                Registry.register(BuiltInRegistries.BLOCK, resLoc, block);
                Registry.register(BuiltInRegistries.ITEM, resLoc,
                    new BlockItem(block, new Item.Properties()));

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
