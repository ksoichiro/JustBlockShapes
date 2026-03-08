package com.justblockshapes.compat;

import com.justblockshapes.ModBlocks.VariantType;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public class BiomesOPlentyCompat {
    public static final String MOD_ID = "biomesoplenty";

    public record CompatBlockEntry(
        String modId,
        String baseBlockId,
        Set<VariantType> variants
    ) {}

    private static final List<CompatBlockEntry> ENTRIES = List.of(
        new CompatBlockEntry(MOD_ID, "rose_quartz_block",
            EnumSet.of(VariantType.STAIRS, VariantType.SLAB, VariantType.WALL, VariantType.TRAPDOOR, VariantType.DOOR, VariantType.PRESSURE_PLATE, VariantType.BUTTON)),
        new CompatBlockEntry(MOD_ID, "brimstone",
            EnumSet.of(VariantType.STAIRS, VariantType.SLAB, VariantType.WALL, VariantType.TRAPDOOR, VariantType.DOOR, VariantType.PRESSURE_PLATE, VariantType.BUTTON)),
        new CompatBlockEntry(MOD_ID, "flesh",
            EnumSet.of(VariantType.STAIRS, VariantType.SLAB, VariantType.WALL, VariantType.TRAPDOOR, VariantType.DOOR, VariantType.PRESSURE_PLATE, VariantType.BUTTON))
    );

    public static List<CompatBlockEntry> getEntries() {
        return ENTRIES;
    }
}
