package com.justmissingblocks.datagen;

import com.justmissingblocks.ModBlocks;
import com.justmissingblocks.ModBlocks.VariantType;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends FabricTagProvider.BlockTagProvider {

    public ModBlockTagProvider(FabricDataOutput output,
                               CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider wrapperLookup) {
        var stairsTag = getOrCreateTagBuilder(BlockTags.STAIRS);
        var slabsTag = getOrCreateTagBuilder(BlockTags.SLABS);
        var wallsTag = getOrCreateTagBuilder(BlockTags.WALLS);
        var pickaxeTag = getOrCreateTagBuilder(BlockTags.MINEABLE_WITH_PICKAXE);

        for (ModBlocks.BlockEntry entry : ModBlocks.getBlockEntries()) {
            for (VariantType variant : entry.variants()) {
                String id = ModBlocks.variantBlockId(entry.baseBlockId(), variant);
                Block block = ModBlocks.getRegisteredBlocks().get(id);
                if (block == null) continue;

                switch (variant) {
                    case STAIRS -> stairsTag.add(block);
                    case SLAB -> slabsTag.add(block);
                    case WALL -> wallsTag.add(block);
                }

                pickaxeTag.add(block);
            }
        }
    }
}
