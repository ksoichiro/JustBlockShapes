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


    static {
        // --- Cobblestone family (vanilla has stairs, slab, wall) ---
        withoutStairsSlabWall("cobblestone");
        withoutStairsSlabWall("mossy_cobblestone");

        // --- Stone family ---
        // Stone: wall, fence, fence_gate, trapdoor, door only (pressure_plate and button already exist in vanilla)
        BLOCK_ENTRIES.add(new BlockEntry("stone", EnumSet.of(VariantType.WALL, VariantType.FENCE, VariantType.FENCE_GATE, VariantType.TRAPDOOR, VariantType.DOOR)));
        // stone_bricks: vanilla has stairs(stone_brick_), slab(stone_brick_), wall(stone_brick_)
        withoutStairsSlabWall("stone_bricks");
        withoutStairsSlabWall("mossy_stone_bricks");
        // Smooth stone: vanilla has slab only
        withoutSlab("smooth_stone");

        // --- Natural stone variants (vanilla has stairs, slab, wall) ---
        withoutStairsSlabWall("granite");
        withoutStairsAndSlab("polished_granite");
        withoutStairsSlabWall("diorite");
        withoutStairsAndSlab("polished_diorite");
        withoutStairsSlabWall("andesite");
        withoutStairsAndSlab("polished_andesite");

        // --- Bricks (vanilla has brick_stairs, brick_slab, brick_wall) ---
        withoutStairsSlabWall("bricks");

        // --- Sandstone family ---
        // sandstone: vanilla has stairs, slab, wall
        withoutStairsSlabWall("sandstone");
        withoutSlab("cut_sandstone");
        withoutStairsAndSlab("smooth_sandstone");
        // red_sandstone: vanilla has stairs, slab, wall
        withoutStairsSlabWall("red_sandstone");
        withoutSlab("cut_red_sandstone");
        withoutStairsAndSlab("smooth_red_sandstone");

        // --- Quartz family (vanilla has stairs, slab) ---
        withoutStairsAndSlab("quartz_block");
        withoutStairsAndSlab("smooth_quartz");

        // --- Prismarine family ---
        // prismarine: vanilla has stairs, slab, wall
        withoutStairsSlabWall("prismarine");
        withoutStairsAndSlab("dark_prismarine");
        withoutStairsAndSlab("prismarine_bricks");

        // --- Purpur ---
        withoutStairsAndSlab("purpur_block");

        // --- Blackstone family ---
        // blackstone: vanilla has stairs, slab, wall
        withoutStairsSlabWall("blackstone");
        // polished_blackstone: vanilla has stairs, slab, wall, pressure_plate, button
        BLOCK_ENTRIES.add(new BlockEntry("polished_blackstone", EnumSet.of(VariantType.FENCE, VariantType.FENCE_GATE, VariantType.TRAPDOOR, VariantType.DOOR)));
        // polished_blackstone_bricks: vanilla has stairs(polished_blackstone_brick_), slab, wall
        withoutStairsSlabWall("polished_blackstone_bricks");
        allVariants("cracked_polished_blackstone_bricks");
        allVariants("gilded_blackstone");

        // --- Deepslate family ---
        // cobbled_deepslate: vanilla has stairs, slab, wall
        withoutStairsSlabWall("cobbled_deepslate");
        withoutStairsSlabWall("polished_deepslate");
        withoutStairsSlabWall("deepslate_bricks");
        withoutStairsSlabWall("deepslate_tiles");
        allVariants("cracked_deepslate_bricks");
        allVariants("cracked_deepslate_tiles");

        // --- Nether family ---
        // nether_bricks: vanilla has stairs(nether_brick_), slab(nether_brick_), wall(nether_brick_), fence(nether_brick_)
        withoutStairsSlabWall("nether_bricks");
        withoutStairsSlabWall("red_nether_bricks");
        allVariants("cracked_nether_bricks");

        // --- End family ---
        allVariants("end_stone");
        // end_stone_bricks: vanilla has stairs(end_stone_brick_), slab(end_stone_brick_), wall(end_stone_brick_)
        withoutStairsSlabWall("end_stone_bricks");

        // --- Calcite / Basalt ---
        allVariants("calcite");
        allVariants("smooth_basalt");

        // --- Terracotta ---
        allVariants("terracotta");
        for (String color : COLORS) {
            allVariants(color + "_terracotta");
        }

        // --- Concrete ---
        for (String color : COLORS) {
            allVariants(color + "_concrete");
        }

        // --- Tuff family ---
        allVariants("tuff");
        // tuff_bricks, polished_tuff: added in 1.21, skipped on older versions via tryGetBlock
        withoutStairsSlabWall("tuff_bricks");
        withoutStairsSlabWall("polished_tuff");

        // --- Obsidian ---
        allVariants("obsidian");
        allVariants("crying_obsidian");

        // --- Mud family ---
        allVariants("packed_mud");
        // mud_bricks: vanilla has stairs(mud_brick_), slab(mud_brick_), wall(mud_brick_)
        withoutStairsSlabWall("mud_bricks");

        // --- Natural / mineral blocks ---
        allVariants("dripstone_block");
        allVariants("amethyst_block");
    }

    /** All variant types. For blocks with no existing variants in vanilla. */
    private static void allVariants(String baseBlockId) {
        BLOCK_ENTRIES.add(new BlockEntry(baseBlockId, EnumSet.allOf(VariantType.class)));
    }

    /** All except SLAB. For blocks where vanilla already has a slab. */
    private static void withoutSlab(String baseBlockId) {
        BLOCK_ENTRIES.add(new BlockEntry(baseBlockId, EnumSet.complementOf(EnumSet.of(VariantType.SLAB))));
    }

    /** All except STAIRS and SLAB. For blocks where vanilla already has stairs and slab. */
    private static void withoutStairsAndSlab(String baseBlockId) {
        BLOCK_ENTRIES.add(new BlockEntry(baseBlockId, EnumSet.complementOf(EnumSet.of(VariantType.STAIRS, VariantType.SLAB))));
    }

    /** All except STAIRS, SLAB, and WALL. For blocks where vanilla already has all three. */
    private static void withoutStairsSlabWall(String baseBlockId) {
        BLOCK_ENTRIES.add(new BlockEntry(baseBlockId, EnumSet.complementOf(EnumSet.of(VariantType.STAIRS, VariantType.SLAB, VariantType.WALL))));
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
