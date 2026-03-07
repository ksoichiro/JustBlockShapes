package com.justmissingblocks;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class Compat {
    public static Identifier resourceLocation(String namespace, String path) {
        return Identifier.fromNamespaceAndPath(namespace, path);
    }

    public static Identifier resourceLocation(String path) {
        return Identifier.withDefaultNamespace(path);
    }

    public static BlockBehaviour.Properties copyProperties(Block block) {
        return BlockBehaviour.Properties.ofFullCopy(block);
    }

    public static Block getBlock(Identifier id) {
        return BuiltInRegistries.BLOCK.get(id).orElseThrow().value();
    }

    public static BlockBehaviour.Properties withBlockId(BlockBehaviour.Properties props, String namespace, String blockId) {
        ResourceKey<Block> key = ResourceKey.create(Registries.BLOCK,
            Identifier.fromNamespaceAndPath(namespace, blockId));
        return props.setId(key);
    }

    public static Item.Properties createItemProperties(String namespace, String itemId) {
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM,
            Identifier.fromNamespaceAndPath(namespace, itemId));
        return new Item.Properties().setId(key);
    }
}
