package com.justmissingblocks;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
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

    public static Block getBlock(ResourceLocation id) {
        return BuiltInRegistries.BLOCK.get(id).orElseThrow().value();
    }

    public static BlockBehaviour.Properties withBlockId(BlockBehaviour.Properties props, String namespace, String blockId) {
        ResourceKey<Block> key = ResourceKey.create(Registries.BLOCK,
            ResourceLocation.fromNamespaceAndPath(namespace, blockId));
        return props.setId(key);
    }

    public static Item.Properties createItemProperties(String namespace, String itemId) {
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM,
            ResourceLocation.fromNamespaceAndPath(namespace, itemId));
        return new Item.Properties().setId(key);
    }
}
