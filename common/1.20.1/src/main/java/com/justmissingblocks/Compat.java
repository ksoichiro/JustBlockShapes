package com.justmissingblocks;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class Compat {
    public static ResourceLocation resourceLocation(String namespace, String path) {
        return new ResourceLocation(namespace, path);
    }

    public static ResourceLocation resourceLocation(String path) {
        return new ResourceLocation(path);
    }

    public static BlockBehaviour.Properties copyProperties(Block block) {
        return BlockBehaviour.Properties.copy(block);
    }

    public static Block getBlock(ResourceLocation id) {
        return BuiltInRegistries.BLOCK.get(id);
    }

    public static BlockBehaviour.Properties withBlockId(BlockBehaviour.Properties props, String namespace, String blockId) {
        return props;
    }

    public static Item.Properties createItemProperties(String namespace, String itemId) {
        return new Item.Properties();
    }
}
