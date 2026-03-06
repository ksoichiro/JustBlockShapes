package com.justmissingblocks.datagen;

import com.justmissingblocks.Compat;
import com.justmissingblocks.JustMissingBlocks;
import com.justmissingblocks.ModBlocks;
import com.justmissingblocks.ModBlocks.VariantType;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.SingleItemRecipeBuilder;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;

import java.util.function.Consumer;

public class ModRecipeProvider extends FabricRecipeProvider {

    public ModRecipeProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void buildRecipes(Consumer<FinishedRecipe> exporter) {
        for (ModBlocks.BlockEntry entry : ModBlocks.getBlockEntries()) {
            Block baseBlock = BuiltInRegistries.BLOCK.get(
                Compat.resourceLocation("minecraft", entry.baseBlockId()));
            Ingredient baseIngredient = Ingredient.of(baseBlock);

            for (VariantType variant : entry.variants()) {
                String id = ModBlocks.variantBlockId(entry.baseBlockId(), variant);
                Block block = ModBlocks.getRegisteredBlocks().get(id);
                if (block == null) continue;

                var craftingId = Compat.resourceLocation(JustMissingBlocks.MOD_ID, id);
                var stonecuttingId = Compat.resourceLocation(
                    JustMissingBlocks.MOD_ID, id + "_stonecutting");

                switch (variant) {
                    case STAIRS -> {
                        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, block, 4)
                            .define('#', baseIngredient)
                            .pattern("#  ")
                            .pattern("## ")
                            .pattern("###")
                            .unlockedBy("has_material", has(baseBlock))
                            .save(exporter, craftingId);
                        SingleItemRecipeBuilder.stonecutting(baseIngredient,
                                RecipeCategory.BUILDING_BLOCKS, block)
                            .unlockedBy("has_material", has(baseBlock))
                            .save(exporter, stonecuttingId);
                    }
                    case SLAB -> {
                        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, block, 6)
                            .define('#', baseIngredient)
                            .pattern("###")
                            .unlockedBy("has_material", has(baseBlock))
                            .save(exporter, craftingId);
                        SingleItemRecipeBuilder.stonecutting(baseIngredient,
                                RecipeCategory.BUILDING_BLOCKS, block, 2)
                            .unlockedBy("has_material", has(baseBlock))
                            .save(exporter, stonecuttingId);
                    }
                    case WALL -> {
                        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, block, 6)
                            .define('#', baseIngredient)
                            .pattern("###")
                            .pattern("###")
                            .unlockedBy("has_material", has(baseBlock))
                            .save(exporter, craftingId);
                        SingleItemRecipeBuilder.stonecutting(baseIngredient,
                                RecipeCategory.BUILDING_BLOCKS, block)
                            .unlockedBy("has_material", has(baseBlock))
                            .save(exporter, stonecuttingId);
                    }
                }
            }
        }
    }
}
