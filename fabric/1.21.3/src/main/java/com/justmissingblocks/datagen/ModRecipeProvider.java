package com.justmissingblocks.datagen;

import com.justmissingblocks.JustMissingBlocks;
import com.justmissingblocks.ModBlocks;
import com.justmissingblocks.ModBlocks.VariantType;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.SingleItemRecipeBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.Block;

import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends FabricRecipeProvider {

    public ModRecipeProvider(FabricDataOutput output,
                             CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput exporter) {
        return new RecipeProvider(registries, exporter) {
            @Override
            public void buildRecipes() {
                var items = registries.lookupOrThrow(Registries.ITEM);

                for (ModBlocks.BlockEntry entry : ModBlocks.getBlockEntries()) {
                    Block baseBlock = BuiltInRegistries.BLOCK.get(
                        ResourceLocation.fromNamespaceAndPath("minecraft", entry.baseBlockId())).orElseThrow().value();
                    Ingredient baseIngredient = Ingredient.of(baseBlock);

                    for (VariantType variant : entry.variants()) {
                        String id = ModBlocks.variantBlockId(entry.baseBlockId(), variant);
                        Block block = ModBlocks.getRegisteredBlocks().get(id);
                        if (block == null) continue;

                        ResourceKey<Recipe<?>> craftingKey = ResourceKey.create(Registries.RECIPE,
                            ResourceLocation.fromNamespaceAndPath(JustMissingBlocks.MOD_ID, id));
                        ResourceKey<Recipe<?>> stonecuttingKey = ResourceKey.create(Registries.RECIPE,
                            ResourceLocation.fromNamespaceAndPath(JustMissingBlocks.MOD_ID, id + "_stonecutting"));

                        switch (variant) {
                            case STAIRS -> {
                                ShapedRecipeBuilder.shaped(items, RecipeCategory.BUILDING_BLOCKS, block, 4)
                                    .define('#', baseIngredient)
                                    .pattern("#  ")
                                    .pattern("## ")
                                    .pattern("###")
                                    .unlockedBy("has_material", has(baseBlock))
                                    .save(output, craftingKey);
                                SingleItemRecipeBuilder.stonecutting(baseIngredient,
                                        RecipeCategory.BUILDING_BLOCKS, block)
                                    .unlockedBy("has_material", has(baseBlock))
                                    .save(output, stonecuttingKey);
                            }
                            case SLAB -> {
                                ShapedRecipeBuilder.shaped(items, RecipeCategory.BUILDING_BLOCKS, block, 6)
                                    .define('#', baseIngredient)
                                    .pattern("###")
                                    .unlockedBy("has_material", has(baseBlock))
                                    .save(output, craftingKey);
                                SingleItemRecipeBuilder.stonecutting(baseIngredient,
                                        RecipeCategory.BUILDING_BLOCKS, block, 2)
                                    .unlockedBy("has_material", has(baseBlock))
                                    .save(output, stonecuttingKey);
                            }
                            case WALL -> {
                                ShapedRecipeBuilder.shaped(items, RecipeCategory.BUILDING_BLOCKS, block, 6)
                                    .define('#', baseIngredient)
                                    .pattern("###")
                                    .pattern("###")
                                    .unlockedBy("has_material", has(baseBlock))
                                    .save(output, craftingKey);
                                SingleItemRecipeBuilder.stonecutting(baseIngredient,
                                        RecipeCategory.BUILDING_BLOCKS, block)
                                    .unlockedBy("has_material", has(baseBlock))
                                    .save(output, stonecuttingKey);
                            }
                        }
                    }
                }
            }
        };
    }

    @Override
    public String getName() {
        return "JustMissingBlocks Recipes";
    }
}
