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
        STAIRS, SLAB, WALL, FENCE, FENCE_GATE, TRAPDOOR, DOOR, PRESSURE_PLATE, BUTTON
    }

    public record BlockEntry(String baseBlockId, Set<VariantType> variants) {}

    public static final String[] COLORS = {
        "white", "orange", "magenta", "light_blue",
        "yellow", "lime", "pink", "gray",
        "light_gray", "cyan", "purple", "blue",
        "brown", "green", "red", "black"
    };

    private static final List<BlockEntry> BLOCK_ENTRIES = new ArrayList<>();

    /**
     * Shorthand for blocks where vanilla has stairs, slab, wall already.
     * Adds only: fence, fence_gate, trapdoor, door, pressure_plate, button.
     */
    private static final Set<VariantType> VANILLA_HAS_STAIRS_SLAB_WALL =
        EnumSet.of(VariantType.FENCE, VariantType.FENCE_GATE, VariantType.TRAPDOOR, VariantType.DOOR, VariantType.PRESSURE_PLATE, VariantType.BUTTON);

    static {
        // --- Cobblestone family (vanilla has stairs, slab, wall) ---
        BLOCK_ENTRIES.add(new BlockEntry("cobblestone", VANILLA_HAS_STAIRS_SLAB_WALL));
        BLOCK_ENTRIES.add(new BlockEntry("mossy_cobblestone", VANILLA_HAS_STAIRS_SLAB_WALL));

        // --- Stone family ---
        // Stone: wall, fence, fence_gate, trapdoor, door only (pressure_plate and button already exist in vanilla)
        BLOCK_ENTRIES.add(new BlockEntry("stone", EnumSet.of(VariantType.WALL, VariantType.FENCE, VariantType.FENCE_GATE, VariantType.TRAPDOOR, VariantType.DOOR)));
        // stone_bricks: vanilla has stairs(stone_brick_), slab(stone_brick_), wall(stone_brick_)
        BLOCK_ENTRIES.add(new BlockEntry("stone_bricks", VANILLA_HAS_STAIRS_SLAB_WALL));
        BLOCK_ENTRIES.add(new BlockEntry("mossy_stone_bricks", VANILLA_HAS_STAIRS_SLAB_WALL));
        // Smooth stone: vanilla has slab only
        stairsAndWall("smooth_stone");

        // --- Natural stone variants (vanilla has stairs, slab, wall) ---
        BLOCK_ENTRIES.add(new BlockEntry("granite", VANILLA_HAS_STAIRS_SLAB_WALL));
        wallOnly("polished_granite");
        BLOCK_ENTRIES.add(new BlockEntry("diorite", VANILLA_HAS_STAIRS_SLAB_WALL));
        wallOnly("polished_diorite");
        BLOCK_ENTRIES.add(new BlockEntry("andesite", VANILLA_HAS_STAIRS_SLAB_WALL));
        wallOnly("polished_andesite");

        // --- Bricks (vanilla has brick_stairs, brick_slab, brick_wall) ---
        BLOCK_ENTRIES.add(new BlockEntry("bricks", VANILLA_HAS_STAIRS_SLAB_WALL));

        // --- Sandstone family ---
        // sandstone: vanilla has stairs, slab, wall
        BLOCK_ENTRIES.add(new BlockEntry("sandstone", VANILLA_HAS_STAIRS_SLAB_WALL));
        stairsAndWall("cut_sandstone");
        wallOnly("smooth_sandstone");
        // red_sandstone: vanilla has stairs, slab, wall
        BLOCK_ENTRIES.add(new BlockEntry("red_sandstone", VANILLA_HAS_STAIRS_SLAB_WALL));
        stairsAndWall("cut_red_sandstone");
        wallOnly("smooth_red_sandstone");

        // --- Quartz family (vanilla has stairs, slab) ---
        wallOnly("quartz_block");
        wallOnly("smooth_quartz");

        // --- Prismarine family ---
        // prismarine: vanilla has stairs, slab, wall
        BLOCK_ENTRIES.add(new BlockEntry("prismarine", VANILLA_HAS_STAIRS_SLAB_WALL));
        wallOnly("dark_prismarine");
        wallOnly("prismarine_bricks");

        // --- Purpur ---
        wallOnly("purpur_block");

        // --- Blackstone family ---
        // blackstone: vanilla has stairs, slab, wall
        BLOCK_ENTRIES.add(new BlockEntry("blackstone", VANILLA_HAS_STAIRS_SLAB_WALL));
        // polished_blackstone: vanilla has stairs, slab, wall, pressure_plate, button
        BLOCK_ENTRIES.add(new BlockEntry("polished_blackstone", EnumSet.of(VariantType.FENCE, VariantType.FENCE_GATE, VariantType.TRAPDOOR, VariantType.DOOR)));
        // polished_blackstone_bricks: vanilla has stairs(polished_blackstone_brick_), slab, wall
        BLOCK_ENTRIES.add(new BlockEntry("polished_blackstone_bricks", VANILLA_HAS_STAIRS_SLAB_WALL));
        allThree("cracked_polished_blackstone_bricks");
        allThree("gilded_blackstone");

        // --- Deepslate family ---
        // cobbled_deepslate: vanilla has stairs, slab, wall
        BLOCK_ENTRIES.add(new BlockEntry("cobbled_deepslate", VANILLA_HAS_STAIRS_SLAB_WALL));
        BLOCK_ENTRIES.add(new BlockEntry("polished_deepslate", VANILLA_HAS_STAIRS_SLAB_WALL));
        BLOCK_ENTRIES.add(new BlockEntry("deepslate_bricks", VANILLA_HAS_STAIRS_SLAB_WALL));
        BLOCK_ENTRIES.add(new BlockEntry("deepslate_tiles", VANILLA_HAS_STAIRS_SLAB_WALL));
        allThree("cracked_deepslate_bricks");
        allThree("cracked_deepslate_tiles");

        // --- Nether family ---
        // nether_bricks: vanilla has stairs(nether_brick_), slab(nether_brick_), wall(nether_brick_), fence(nether_brick_)
        BLOCK_ENTRIES.add(new BlockEntry("nether_bricks", VANILLA_HAS_STAIRS_SLAB_WALL));
        BLOCK_ENTRIES.add(new BlockEntry("red_nether_bricks", VANILLA_HAS_STAIRS_SLAB_WALL));
        allThree("cracked_nether_bricks");

        // --- End family ---
        allThree("end_stone");
        // end_stone_bricks: vanilla has stairs(end_stone_brick_), slab(end_stone_brick_), wall(end_stone_brick_)
        BLOCK_ENTRIES.add(new BlockEntry("end_stone_bricks", VANILLA_HAS_STAIRS_SLAB_WALL));

        // --- Calcite / Basalt ---
        allThree("calcite");
        allThree("smooth_basalt");

        // --- Terracotta ---
        allThree("terracotta");
        for (String color : COLORS) {
            allThree(color + "_terracotta");
        }

        // --- Concrete ---
        for (String color : COLORS) {
            allThree(color + "_concrete");
        }

        // --- Tuff family ---
        allThree("tuff");
        // tuff_bricks, polished_tuff: added in 1.21, skipped on older versions via tryGetBlock
        BLOCK_ENTRIES.add(new BlockEntry("tuff_bricks", VANILLA_HAS_STAIRS_SLAB_WALL));
        BLOCK_ENTRIES.add(new BlockEntry("polished_tuff", VANILLA_HAS_STAIRS_SLAB_WALL));

        // --- Obsidian ---
        allThree("obsidian");
        allThree("crying_obsidian");

        // --- Mud family ---
        allThree("packed_mud");
        // mud_bricks: vanilla has stairs(mud_brick_), slab(mud_brick_), wall(mud_brick_)
        BLOCK_ENTRIES.add(new BlockEntry("mud_bricks", VANILLA_HAS_STAIRS_SLAB_WALL));

        // --- Natural / mineral blocks ---
        allThree("dripstone_block");
        allThree("amethyst_block");
    }

    private static void wallOnly(String baseBlockId) {
        BLOCK_ENTRIES.add(new BlockEntry(baseBlockId, EnumSet.of(VariantType.WALL, VariantType.FENCE, VariantType.FENCE_GATE, VariantType.TRAPDOOR, VariantType.DOOR, VariantType.PRESSURE_PLATE, VariantType.BUTTON)));
    }

    private static void stairsAndWall(String baseBlockId) {
        BLOCK_ENTRIES.add(new BlockEntry(baseBlockId, EnumSet.of(VariantType.STAIRS, VariantType.WALL, VariantType.FENCE, VariantType.FENCE_GATE, VariantType.TRAPDOOR, VariantType.DOOR, VariantType.PRESSURE_PLATE, VariantType.BUTTON)));
    }

    private static void allThree(String baseBlockId) {
        BLOCK_ENTRIES.add(new BlockEntry(baseBlockId, EnumSet.of(VariantType.STAIRS, VariantType.SLAB, VariantType.WALL, VariantType.FENCE, VariantType.FENCE_GATE, VariantType.TRAPDOOR, VariantType.DOOR, VariantType.PRESSURE_PLATE, VariantType.BUTTON)));
    }

    public static List<BlockEntry> getBlockEntries() {
        return BLOCK_ENTRIES;
    }

    public static String variantBlockId(String baseBlockId, VariantType variant) {
        return switch (variant) {
            case STAIRS -> baseBlockId + "_stairs";
            case SLAB -> baseBlockId + "_slab";
            case WALL -> baseBlockId + "_wall";
            case FENCE -> baseBlockId + "_fence";
            case FENCE_GATE -> baseBlockId + "_fence_gate";
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
            case FENCE -> Compat.createFence(props);
            case FENCE_GATE -> Compat.createFenceGate(props);
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
