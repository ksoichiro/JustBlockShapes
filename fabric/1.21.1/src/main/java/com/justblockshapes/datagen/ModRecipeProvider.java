package com.justblockshapes.datagen;

import com.justblockshapes.JustBlockShapes;
import com.justblockshapes.ModBlocks;
import com.justblockshapes.ModBlocks.VariantType;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SingleItemRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;

import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends FabricRecipeProvider {

    public ModRecipeProvider(FabricDataOutput output,
                             CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public void buildRecipes(RecipeOutput exporter) {
        for (ModBlocks.BlockEntry entry : ModBlocks.getBlockEntries()) {
            Block baseBlock = BuiltInRegistries.BLOCK.get(
                ResourceLocation.fromNamespaceAndPath("minecraft", entry.baseBlockId()));
            Ingredient baseIngredient = Ingredient.of(baseBlock);

            for (VariantType variant : entry.variants()) {
                String id = ModBlocks.variantBlockId(entry.baseBlockId(), variant);
                Block block = ModBlocks.getRegisteredBlocks().get(id);
                if (block == null) continue;

                ResourceLocation craftingId = ResourceLocation.fromNamespaceAndPath(
                    JustBlockShapes.MOD_ID, id);
                ResourceLocation stonecuttingId = ResourceLocation.fromNamespaceAndPath(
                    JustBlockShapes.MOD_ID, id + "_stonecutting");

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
                    case TRAPDOOR -> {
                        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, block, 2)
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
                    case DOOR -> {
                        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, block, 3)
                            .define('#', baseIngredient)
                            .pattern("##")
                            .pattern("##")
                            .pattern("##")
                            .unlockedBy("has_material", has(baseBlock))
                            .save(exporter, craftingId);
                        SingleItemRecipeBuilder.stonecutting(baseIngredient,
                                RecipeCategory.BUILDING_BLOCKS, block)
                            .unlockedBy("has_material", has(baseBlock))
                            .save(exporter, stonecuttingId);
                    }
                    case PRESSURE_PLATE -> {
                        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, block, 1)
                            .define('#', baseIngredient)
                            .pattern("##")
                            .unlockedBy("has_material", has(baseBlock))
                            .save(exporter, craftingId);
                        SingleItemRecipeBuilder.stonecutting(baseIngredient,
                                RecipeCategory.BUILDING_BLOCKS, block)
                            .unlockedBy("has_material", has(baseBlock))
                            .save(exporter, stonecuttingId);
                    }
                    case BUTTON -> {
                        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, block)
                            .requires(baseIngredient)
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
