package com.justblockshapes;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
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
        STAIRS, SLAB, WALL, TRAPDOOR, DOOR, PRESSURE_PLATE, BUTTON
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

        // Walls only (stairs + slab already exist in vanilla)
        wallOnly("smooth_sandstone");
        wallOnly("smooth_red_sandstone");
        // Stone: wall, trapdoor, door only (pressure_plate and button already exist in vanilla)
        BLOCK_ENTRIES.add(new BlockEntry("stone", EnumSet.of(VariantType.WALL, VariantType.TRAPDOOR, VariantType.DOOR)));

        // All three (stairs + slab + wall)
        allThree("calcite");
        allThree("cracked_stone_bricks");
        allThree("smooth_basalt");
        allThree("cracked_deepslate_bricks");
        allThree("cracked_polished_blackstone_bricks");
        allThree("cracked_deepslate_tiles");
        allThree("cracked_nether_bricks");
        allThree("end_stone");
        allThree("gilded_blackstone");
        allThree("terracotta");

        // 16 dyed terracotta
        for (String color : COLORS) {
            allThree(color + "_terracotta");
        }

        // 16 concrete
        for (String color : COLORS) {
            allThree(color + "_concrete");
        }

        // Tuff variants
        allThree("tuff");
        // tuff_bricks, polished_tuff: added in 1.21, skipped on older versions via tryGetBlock
        BLOCK_ENTRIES.add(new BlockEntry("tuff_bricks", EnumSet.of(VariantType.TRAPDOOR, VariantType.DOOR, VariantType.PRESSURE_PLATE, VariantType.BUTTON)));
        BLOCK_ENTRIES.add(new BlockEntry("polished_tuff", EnumSet.of(VariantType.TRAPDOOR, VariantType.DOOR, VariantType.PRESSURE_PLATE, VariantType.BUTTON)));

        // Obsidian variants
        allThree("obsidian");
        allThree("crying_obsidian");

        // End stone bricks (stairs, slab, wall already exist in vanilla)
        BLOCK_ENTRIES.add(new BlockEntry("end_stone_bricks", EnumSet.of(VariantType.TRAPDOOR, VariantType.DOOR, VariantType.PRESSURE_PLATE, VariantType.BUTTON)));

        // Deepslate variants (stairs, slab, wall already exist in vanilla)
        BLOCK_ENTRIES.add(new BlockEntry("deepslate_bricks", EnumSet.of(VariantType.TRAPDOOR, VariantType.DOOR, VariantType.PRESSURE_PLATE, VariantType.BUTTON)));
        BLOCK_ENTRIES.add(new BlockEntry("deepslate_tiles", EnumSet.of(VariantType.TRAPDOOR, VariantType.DOOR, VariantType.PRESSURE_PLATE, VariantType.BUTTON)));
        BLOCK_ENTRIES.add(new BlockEntry("polished_deepslate", EnumSet.of(VariantType.TRAPDOOR, VariantType.DOOR, VariantType.PRESSURE_PLATE, VariantType.BUTTON)));

        // Nether brick variants (stairs, slab, wall already exist in vanilla)
        BLOCK_ENTRIES.add(new BlockEntry("nether_bricks", EnumSet.of(VariantType.TRAPDOOR, VariantType.DOOR, VariantType.PRESSURE_PLATE, VariantType.BUTTON)));
        BLOCK_ENTRIES.add(new BlockEntry("red_nether_bricks", EnumSet.of(VariantType.TRAPDOOR, VariantType.DOOR, VariantType.PRESSURE_PLATE, VariantType.BUTTON)));

        // Natural / mineral blocks
        allThree("packed_mud");
        // mud_bricks: stairs, slab, wall already exist in vanilla
        BLOCK_ENTRIES.add(new BlockEntry("mud_bricks", EnumSet.of(VariantType.TRAPDOOR, VariantType.DOOR, VariantType.PRESSURE_PLATE, VariantType.BUTTON)));
        allThree("dripstone_block");
        allThree("amethyst_block");
    }

    private static void wallOnly(String baseBlockId) {
        BLOCK_ENTRIES.add(new BlockEntry(baseBlockId, EnumSet.of(VariantType.WALL, VariantType.TRAPDOOR, VariantType.DOOR, VariantType.PRESSURE_PLATE, VariantType.BUTTON)));
    }

    private static void stairsAndWall(String baseBlockId) {
        BLOCK_ENTRIES.add(new BlockEntry(baseBlockId, EnumSet.of(VariantType.STAIRS, VariantType.WALL, VariantType.TRAPDOOR, VariantType.DOOR, VariantType.PRESSURE_PLATE, VariantType.BUTTON)));
    }

    private static void allThree(String baseBlockId) {
        BLOCK_ENTRIES.add(new BlockEntry(baseBlockId, EnumSet.of(VariantType.STAIRS, VariantType.SLAB, VariantType.WALL, VariantType.TRAPDOOR, VariantType.DOOR, VariantType.PRESSURE_PLATE, VariantType.BUTTON)));
    }

    public static List<BlockEntry> getBlockEntries() {
        return BLOCK_ENTRIES;
    }

    public static String variantBlockId(String baseBlockId, VariantType variant) {
        return switch (variant) {
            case STAIRS -> baseBlockId + "_stairs";
            case SLAB -> baseBlockId + "_slab";
            case WALL -> baseBlockId + "_wall";
            case TRAPDOOR -> baseBlockId + "_trapdoor";
            case DOOR -> baseBlockId + "_door";
            case PRESSURE_PLATE -> baseBlockId + "_pressure_plate";
            case BUTTON -> baseBlockId + "_button";
        };
    }

    // Base blocks that were skipped because they don't exist in the current MC version
    private static final Set<String> SKIPPED_BASE_BLOCKS = new HashSet<>();

    public static void markSkipped(String baseBlockId) {
        SKIPPED_BASE_BLOCKS.add(baseBlockId);
    }

    public static boolean isSkipped(String baseBlockId) {
        return SKIPPED_BASE_BLOCKS.contains(baseBlockId);
    }

    // Registered block instances, keyed by variant block ID
    private static final Map<String, Block> REGISTERED_BLOCKS = new LinkedHashMap<>();

    public static Map<String, Block> getRegisteredBlocks() {
        return REGISTERED_BLOCKS;
    }

    public static Block createVariantBlock(VariantType variant, Block baseBlock, String blockId) {
        BlockBehaviour.Properties props = Compat.copyProperties(baseBlock);
        props = Compat.withBlockId(props, JustBlockShapes.MOD_ID, blockId);
        return switch (variant) {
            case STAIRS -> new StairBlock(baseBlock.defaultBlockState(), props);
            case SLAB -> new SlabBlock(props);
            case WALL -> new WallBlock(props);
            case TRAPDOOR -> Compat.createTrapDoor(props);
            case DOOR -> Compat.createDoor(props);
            case PRESSURE_PLATE -> Compat.createPressurePlate(props);
            case BUTTON -> Compat.createButton(20, props);
        };
    }

    public static void register(String id, Block block) {
        REGISTERED_BLOCKS.put(id, block);
    }
}
