package com.justmissingblocks.datagen;

import com.justmissingblocks.Compat;
import com.justmissingblocks.ModBlocks;
import com.justmissingblocks.ModBlocks.VariantType;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.model.ModelTemplates;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

public class ModModelProvider extends FabricModelProvider {

    public ModModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators gen) {
        for (ModBlocks.BlockEntry entry : ModBlocks.getBlockEntries()) {
            String baseId = entry.baseBlockId();
            TextureMapping textureMapping = TextureMapping.cube(getBaseTexture(baseId));

            Block stairsBlock = null;
            Block slabBlock = null;
            Block wallBlock = null;

            for (VariantType variant : entry.variants()) {
                String id = ModBlocks.variantBlockId(baseId, variant);
                Block block = ModBlocks.getRegisteredBlocks().get(id);
                if (block == null) continue;

                switch (variant) {
                    case STAIRS -> stairsBlock = block;
                    case SLAB -> slabBlock = block;
                    case WALL -> wallBlock = block;
                }
            }

            if (stairsBlock != null) {
                ResourceLocation inner = ModelTemplates.STAIRS_INNER.create(stairsBlock,
                    textureMapping, gen.modelOutput);
                ResourceLocation straight = ModelTemplates.STAIRS_STRAIGHT.create(stairsBlock,
                    textureMapping, gen.modelOutput);
                ResourceLocation outer = ModelTemplates.STAIRS_OUTER.create(stairsBlock,
                    textureMapping, gen.modelOutput);
                gen.blockStateOutput.accept(
                    BlockModelGenerators.createStairs(stairsBlock, inner, straight, outer));
                gen.delegateItemModel(stairsBlock, straight);
            }

            if (slabBlock != null) {
                ResourceLocation fullBlockModel = getFullBlockModel(baseId);
                ResourceLocation bottom = ModelTemplates.SLAB_BOTTOM.create(slabBlock,
                    textureMapping, gen.modelOutput);
                ResourceLocation top = ModelTemplates.SLAB_TOP.create(slabBlock,
                    textureMapping, gen.modelOutput);
                gen.blockStateOutput.accept(
                    BlockModelGenerators.createSlab(slabBlock, bottom, top, fullBlockModel));
                gen.delegateItemModel(slabBlock, bottom);
            }

            if (wallBlock != null) {
                ResourceLocation post = ModelTemplates.WALL_POST.create(wallBlock,
                    textureMapping, gen.modelOutput);
                ResourceLocation low = ModelTemplates.WALL_LOW_SIDE.create(wallBlock,
                    textureMapping, gen.modelOutput);
                ResourceLocation tall = ModelTemplates.WALL_TALL_SIDE.create(wallBlock,
                    textureMapping, gen.modelOutput);
                gen.blockStateOutput.accept(
                    BlockModelGenerators.createWall(wallBlock, post, low, tall));
                ResourceLocation inventory = ModelTemplates.WALL_INVENTORY.create(wallBlock,
                    textureMapping, gen.modelOutput);
                gen.delegateItemModel(wallBlock, inventory);
            }
        }
    }

    @Override
    public void generateItemModels(ItemModelGenerators itemModelGenerator) {
    }

    private static ResourceLocation getBaseTexture(String baseBlockId) {
        return switch (baseBlockId) {
            case "quartz_block" -> Compat.resourceLocation("block/quartz_block_side");
            case "smooth_quartz" -> Compat.resourceLocation("block/quartz_block_bottom");
            case "prismarine_bricks" -> Compat.resourceLocation("block/prismarine_bricks");
            case "dark_prismarine" -> Compat.resourceLocation("block/dark_prismarine");
            default -> Compat.resourceLocation("block/" + baseBlockId);
        };
    }

    private static ResourceLocation getFullBlockModel(String baseBlockId) {
        return switch (baseBlockId) {
            case "smooth_quartz" -> Compat.resourceLocation("block/smooth_quartz");
            default -> Compat.resourceLocation("block/" + baseBlockId);
        };
    }
}
