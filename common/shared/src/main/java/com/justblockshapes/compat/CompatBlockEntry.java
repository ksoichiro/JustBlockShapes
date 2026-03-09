package com.justblockshapes.compat;

import com.justblockshapes.ModBlocks.VariantType;

import java.util.Set;

public record CompatBlockEntry(
    String modId,
    String baseBlockId,
    Set<VariantType> variants,
    String texturePath
) {
    public CompatBlockEntry(String modId, String baseBlockId, Set<VariantType> variants) {
        this(modId, baseBlockId, variants, modId + ":block/" + baseBlockId);
    }
}
