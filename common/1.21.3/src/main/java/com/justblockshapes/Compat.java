package com.justblockshapes;

import com.justblockshapes.resource.InMemoryResourcePack;
import net.minecraft.SharedConstants;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
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
        return BuiltInRegistries.BLOCK.get(id).orElseThrow().value();
    }

    @Nullable
    public static Block tryGetBlock(ResourceLocation id) {
        try {
            return getBlock(id);
        } catch (Exception e) {
            return null;
        }
    }

    public static BlockBehaviour.Properties withBlockId(BlockBehaviour.Properties props, String namespace, String blockId) {
        ResourceKey<Block> key = ResourceKey.create(Registries.BLOCK,
            ResourceLocation.fromNamespaceAndPath(namespace, blockId));
        return props.setId(key);
    }

    public static Item.Properties createItemProperties(String namespace, String itemId) {
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM,
            ResourceLocation.fromNamespaceAndPath(namespace, itemId));
        return new Item.Properties().setId(key).useBlockDescriptionPrefix();
    }

    public static Block createTrapDoor(BlockBehaviour.Properties props) {
        return new net.minecraft.world.level.block.TrapDoorBlock(
            net.minecraft.world.level.block.state.properties.BlockSetType.OAK, props);
    }

    public static Block createDoor(BlockBehaviour.Properties props) {
        return new net.minecraft.world.level.block.DoorBlock(
            net.minecraft.world.level.block.state.properties.BlockSetType.OAK, props);
    }

    public static Block createPressurePlate(BlockBehaviour.Properties props) {
        return new net.minecraft.world.level.block.PressurePlateBlock(
            net.minecraft.world.level.block.state.properties.BlockSetType.STONE, props);
    }

    public static Block createButton(int ticksToStayPressed, BlockBehaviour.Properties props) {
        return new net.minecraft.world.level.block.ButtonBlock(
            net.minecraft.world.level.block.state.properties.BlockSetType.STONE, ticksToStayPressed, props);
    }

    public static Block createFence(BlockBehaviour.Properties props) {
        return new net.minecraft.world.level.block.FenceBlock(props);
    }

    public static Block createFenceGate(BlockBehaviour.Properties props) {
        return new net.minecraft.world.level.block.FenceGateBlock(
            net.minecraft.world.level.block.state.properties.WoodType.OAK, props);
    }

    /**
     * Returns an ingredient JSON fragment for the given item.
     * 1.21.3+ uses the plain string format: "minecraft:foo"
     */
    public static String ingredientJson(String item) {
        return "\"%s\"".formatted(item);
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
                        Component.literal("JustBlockShapes compat resources"),
                        packFormat,
                        Optional.empty()
                    );
                }
                return null;
            }
        };
    }
}
