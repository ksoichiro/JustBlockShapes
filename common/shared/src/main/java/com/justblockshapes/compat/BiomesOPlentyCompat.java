package com.justblockshapes.compat;

import com.justblockshapes.ModBlocks.VariantType;

import java.util.EnumSet;
import java.util.List;

public class BiomesOPlentyCompat {
    public static final String MOD_ID = "biomesoplenty";

    private static CompatBlockEntry allThree(String baseBlockId) {
        return new CompatBlockEntry(MOD_ID, baseBlockId,
            EnumSet.of(VariantType.STAIRS, VariantType.SLAB, VariantType.WALL,
                VariantType.TRAPDOOR, VariantType.DOOR, VariantType.PRESSURE_PLATE, VariantType.BUTTON));
    }

    private static CompatBlockEntry wallOnly(String baseBlockId) {
        return new CompatBlockEntry(MOD_ID, baseBlockId,
            EnumSet.of(VariantType.WALL,
                VariantType.TRAPDOOR, VariantType.DOOR, VariantType.PRESSURE_PLATE, VariantType.BUTTON));
    }

    private static final List<CompatBlockEntry> ENTRIES = List.of(
        // Full variants (no existing BOP stairs/slab/wall)
        allThree("rose_quartz_block"),
        allThree("brimstone"),
        allThree("flesh"),
        allThree("chiseled_brimstone_bricks"),

        // BOP already has stairs+slab+wall → wall + trapdoor/door/plate/button only
        wallOnly("brimstone_bricks")
    );

    public static List<CompatBlockEntry> getEntries() {
        return ENTRIES;
    }
}
