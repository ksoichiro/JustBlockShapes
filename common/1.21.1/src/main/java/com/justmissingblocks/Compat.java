package com.justmissingblocks;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class Compat {
    public static ResourceLocation resourceLocation(String namespace, String path) {
        return ResourceLocation.fromNamespaceAndPath(namespace, path);
    }

    public static ResourceLocation resourceLocation(String path) {
        return ResourceLocation.withDefaultNamespace(path);
    }

    public static BlockBehaviour.Properties copyProperties(Block block) {
        return BlockBehaviour.Properties.ofFullCopy(block);
    }
}
