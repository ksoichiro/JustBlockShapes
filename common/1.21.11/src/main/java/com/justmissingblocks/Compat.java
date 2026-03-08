package com.justmissingblocks;

import com.justmissingblocks.resource.InMemoryResourcePack;
import net.minecraft.SharedConstants;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.metadata.pack.PackFormat;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.MetadataSectionType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.util.InclusiveRange;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Optional;

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
        return new Item.Properties().setId(key).useBlockDescriptionPrefix();
    }

    public static InMemoryResourcePack createInMemoryResourcePack(PackLocationInfo locationInfo) {
        return new InMemoryResourcePack(locationInfo) {
            @SuppressWarnings("unchecked")
            @Nullable
            @Override
            public <T> T getMetadataSection(MetadataSectionType<T> type) throws IOException {
                if (type == PackMetadataSection.CLIENT_TYPE
                    || type == PackMetadataSection.SERVER_TYPE) {
                    PackFormat format = SharedConstants.getCurrentVersion().packVersion(PackType.CLIENT_RESOURCES);
                    return (T) new PackMetadataSection(
                        Component.literal("JustMissingBlocks compat resources"),
                        new InclusiveRange<>(format)
                    );
                }
                return null;
            }
        };
    }
}
