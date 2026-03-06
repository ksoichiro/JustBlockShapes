package com.justmissingblocks.datagen;

import com.justmissingblocks.ModBlocks;
import com.justmissingblocks.ModBlocks.VariantType;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.world.level.block.Block;

public class ModLootTableProvider extends FabricBlockLootTableProvider {

    public ModLootTableProvider(FabricDataOutput dataOutput) {
        super(dataOutput);
    }

    @Override
    public void generate() {
        for (ModBlocks.BlockEntry entry : ModBlocks.getBlockEntries()) {
            for (VariantType variant : entry.variants()) {
                String id = ModBlocks.variantBlockId(entry.baseBlockId(), variant);
                Block block = ModBlocks.getRegisteredBlocks().get(id);
                if (block == null) continue;

                if (variant == VariantType.SLAB) {
                    this.add(block, this::createSlabItemTable);
                } else {
                    this.dropSelf(block);
                }
            }
        }
    }
}
