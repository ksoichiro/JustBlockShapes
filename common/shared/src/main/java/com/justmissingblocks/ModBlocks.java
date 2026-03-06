package com.justmissingblocks;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class ModBlocks {

    public enum VariantType {
        STAIRS, SLAB, WALL
    }

    public record BlockEntry(String baseBlockId, Set<VariantType> variants) {}

    public static final String[] COLORS = {
        "white", "orange", "magenta", "light_blue",
        "yellow", "lime", "pink", "gray",
        "light_gray", "cyan", "purple", "blue",
        "brown", "green", "red", "black"
    };

    private static final List<BlockEntry> BLOCK_ENTRIES = new ArrayList<>();

    static {
        // Walls only (stairs + slab already exist in vanilla)
        wallOnly("quartz_block");
        wallOnly("smooth_quartz");
        wallOnly("polished_andesite");
        wallOnly("polished_granite");
        wallOnly("polished_diorite");
        wallOnly("dark_prismarine");
        wallOnly("prismarine_bricks");
        wallOnly("purpur_block");

        // Stairs + Wall (slab already exists)
        stairsAndWall("smooth_stone");
        stairsAndWall("cut_sandstone");
        stairsAndWall("cut_red_sandstone");

        // All three (stairs + slab + wall)
        allThree("calcite");
        allThree("cracked_stone_bricks");
        allThree("terracotta");

        // 16 dyed terracotta
        for (String color : COLORS) {
            allThree(color + "_terracotta");
        }

        // 16 concrete
        for (String color : COLORS) {
            allThree(color + "_concrete");
        }
    }

    private static void wallOnly(String baseBlockId) {
        BLOCK_ENTRIES.add(new BlockEntry(baseBlockId, EnumSet.of(VariantType.WALL)));
    }

    private static void stairsAndWall(String baseBlockId) {
        BLOCK_ENTRIES.add(new BlockEntry(baseBlockId, EnumSet.of(VariantType.STAIRS, VariantType.WALL)));
    }

    private static void allThree(String baseBlockId) {
        BLOCK_ENTRIES.add(new BlockEntry(baseBlockId, EnumSet.of(VariantType.STAIRS, VariantType.SLAB, VariantType.WALL)));
    }

    public static List<BlockEntry> getBlockEntries() {
        return BLOCK_ENTRIES;
    }

    public static String variantBlockId(String baseBlockId, VariantType variant) {
        return switch (variant) {
            case STAIRS -> baseBlockId + "_stairs";
            case SLAB -> baseBlockId + "_slab";
            case WALL -> baseBlockId + "_wall";
        };
    }

    // Registered block instances, keyed by variant block ID
    private static final Map<String, Block> REGISTERED_BLOCKS = new LinkedHashMap<>();

    public static Map<String, Block> getRegisteredBlocks() {
        return REGISTERED_BLOCKS;
    }

    public static Block createVariantBlock(VariantType variant, Block baseBlock) {
        BlockBehaviour.Properties props = Compat.copyProperties(baseBlock);
        return switch (variant) {
            case STAIRS -> new StairBlock(baseBlock.defaultBlockState(), props);
            case SLAB -> new SlabBlock(props);
            case WALL -> new WallBlock(props);
        };
    }

    public static void register(String id, Block block) {
        REGISTERED_BLOCKS.put(id, block);
    }
}
