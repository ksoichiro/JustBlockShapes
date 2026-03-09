package com.justblockshapes.compat;

import com.justblockshapes.ModBlocks.VariantType;

import java.util.EnumSet;
import java.util.List;

public class CreateCompat {
    public static final String MOD_ID = "create";

    private static CompatBlockEntry allVariants(String baseBlockId) {
        return new CompatBlockEntry(MOD_ID, baseBlockId,
            EnumSet.of(VariantType.STAIRS, VariantType.SLAB, VariantType.WALL,
                VariantType.TRAPDOOR, VariantType.DOOR, VariantType.PRESSURE_PLATE, VariantType.BUTTON));
    }

    private static CompatBlockEntry allVariants(String baseBlockId, String texturePath) {
        return new CompatBlockEntry(MOD_ID, baseBlockId,
            EnumSet.of(VariantType.STAIRS, VariantType.SLAB, VariantType.WALL,
                VariantType.TRAPDOOR, VariantType.DOOR, VariantType.PRESSURE_PLATE, VariantType.BUTTON),
            texturePath);
    }

    private static CompatBlockEntry noTrapdoorDoor(String baseBlockId) {
        return new CompatBlockEntry(MOD_ID, baseBlockId,
            EnumSet.of(VariantType.STAIRS, VariantType.SLAB, VariantType.WALL,
                VariantType.PRESSURE_PLATE, VariantType.BUTTON));
    }

    private static final List<CompatBlockEntry> ENTRIES = List.of(
        // Metal blocks: no trapdoor/door
        noTrapdoorDoor("industrial_iron_block"),
        noTrapdoorDoor("zinc_block"),
        noTrapdoorDoor("brass_block"),

        // Stone blocks: all variants (textures under palettes/stone_types/)
        allVariants("limestone", "create:block/palettes/stone_types/limestone"),
        allVariants("scoria", "create:block/palettes/stone_types/scoria")
    );

    public static List<CompatBlockEntry> getEntries() {
        return ENTRIES;
    }
}
