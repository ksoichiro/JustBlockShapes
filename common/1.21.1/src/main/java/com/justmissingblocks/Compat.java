package com.justmissingblocks;

import com.justmissingblocks.resource.InMemoryResourcePack;
import net.minecraft.SharedConstants;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Optional;

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
        return BuiltInRegistries.BLOCK.get(id);
    }

    public static BlockBehaviour.Properties withBlockId(BlockBehaviour.Properties props, String namespace, String blockId) {
        return props;
    }

    public static Item.Properties createItemProperties(String namespace, String itemId) {
        return new Item.Properties();
    }

    public static InMemoryResourcePack createInMemoryResourcePack(PackLocationInfo locationInfo) {
        return new InMemoryResourcePack(locationInfo) {
            @SuppressWarnings("unchecked")
            @Nullable
            @Override
            public <T> T getMetadataSection(MetadataSectionSerializer<T> serializer) throws IOException {
                if (serializer == PackMetadataSection.TYPE) {
                    int packFormat = SharedConstants.getCurrentVersion().getPackVersion(PackType.CLIENT_RESOURCES);
                    return (T) new PackMetadataSection(
                        Component.literal("JustMissingBlocks compat resources"),
                        packFormat,
                        Optional.empty()
                    );
                }
                return null;
            }
        };
    }
}
