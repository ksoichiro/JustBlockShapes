package com.justblockshapes.datagen;

import com.justblockshapes.JustBlockShapes;
import com.justblockshapes.ModBlocks;
import com.justblockshapes.ModBlocks.VariantType;
import com.justblockshapes.compat.BiomesOPlentyCompat;
import com.justblockshapes.compat.CompatBlockEntry;
import com.justblockshapes.compat.CreateCompat;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
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
        var trapdoorsTag = getOrCreateTagBuilder(BlockTags.TRAPDOORS);
        var doorsTag = getOrCreateTagBuilder(BlockTags.DOORS);
        var pressurePlatesTag = getOrCreateTagBuilder(BlockTags.PRESSURE_PLATES);
        var buttonsTag = getOrCreateTagBuilder(BlockTags.BUTTONS);
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
                    case TRAPDOOR -> trapdoorsTag.add(block);
                    case DOOR -> doorsTag.add(block);
                    case PRESSURE_PLATE -> pressurePlatesTag.add(block);
                    case BUTTON -> buttonsTag.add(block);
                }

                pickaxeTag.add(block);
            }
        }

        // Compat blocks: added as optional so datagen doesn't require the mod to be present
        java.util.List<CompatBlockEntry> allCompat = new java.util.ArrayList<>();
        allCompat.addAll(BiomesOPlentyCompat.getEntries());
        allCompat.addAll(CreateCompat.getEntries());
        for (CompatBlockEntry entry : allCompat) {
            for (VariantType variant : entry.variants()) {
                String id = ModBlocks.variantBlockId(entry.baseBlockId(), variant);
                ResourceLocation resLoc = ResourceLocation.fromNamespaceAndPath(
                    JustBlockShapes.MOD_ID, id);

                switch (variant) {
                    case STAIRS -> stairsTag.addOptional(resLoc);
                    case SLAB -> slabsTag.addOptional(resLoc);
                    case WALL -> wallsTag.addOptional(resLoc);
                    case TRAPDOOR -> trapdoorsTag.addOptional(resLoc);
                    case DOOR -> doorsTag.addOptional(resLoc);
                    case PRESSURE_PLATE -> pressurePlatesTag.addOptional(resLoc);
                    case BUTTON -> buttonsTag.addOptional(resLoc);
                }

                pickaxeTag.addOptional(resLoc);
            }
        }
    }
}
