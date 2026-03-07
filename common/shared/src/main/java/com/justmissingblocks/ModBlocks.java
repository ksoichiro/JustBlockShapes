package com.justmissingblocks;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;

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
        wallOnly("stone");

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

    // Registered block instances, keyed by variant block ID
    private static final Map<String, Block> REGISTERED_BLOCKS = new LinkedHashMap<>();

    public static Map<String, Block> getRegisteredBlocks() {
        return REGISTERED_BLOCKS;
    }

    public static Block createVariantBlock(VariantType variant, Block baseBlock, String blockId) {
        BlockBehaviour.Properties props = Compat.copyProperties(baseBlock);
        props = Compat.withBlockId(props, JustMissingBlocks.MOD_ID, blockId);
        return switch (variant) {
            case STAIRS -> new StairBlock(baseBlock.defaultBlockState(), props);
            case SLAB -> new SlabBlock(props);
            case WALL -> new WallBlock(props);
            case TRAPDOOR -> new TrapDoorBlock(BlockSetType.OAK, props);
            case DOOR -> new DoorBlock(BlockSetType.OAK, props);
            case PRESSURE_PLATE -> new PressurePlateBlock(BlockSetType.STONE, props);
            case BUTTON -> new ButtonBlock(BlockSetType.STONE, 20, props);
        };
    }

    public static void register(String id, Block block) {
        REGISTERED_BLOCKS.put(id, block);
    }
}
