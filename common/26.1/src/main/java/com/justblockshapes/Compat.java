package com.justblockshapes;

import com.justblockshapes.resource.InMemoryResourcePack;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.lang.reflect.Constructor;
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

    @Nullable
    public static Block tryGetBlock(Identifier id) {
        try {
            return getBlock(id);
        } catch (Exception e) {
            return null;
        }
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

    public static Block createTrapDoor(BlockBehaviour.Properties props) {
        try {
            Constructor<net.minecraft.world.level.block.TrapDoorBlock> ctor =
                net.minecraft.world.level.block.TrapDoorBlock.class.getDeclaredConstructor(
                    BlockSetType.class, BlockBehaviour.Properties.class);
            ctor.setAccessible(true);
            return ctor.newInstance(BlockSetType.OAK, props);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Failed to create TrapDoorBlock", e);
        }
    }

    public static Block createDoor(BlockBehaviour.Properties props) {
        try {
            Constructor<net.minecraft.world.level.block.DoorBlock> ctor =
                net.minecraft.world.level.block.DoorBlock.class.getDeclaredConstructor(
                    BlockSetType.class, BlockBehaviour.Properties.class);
            ctor.setAccessible(true);
            return ctor.newInstance(BlockSetType.OAK, props);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Failed to create DoorBlock", e);
        }
    }

    public static Block createPressurePlate(BlockBehaviour.Properties props) {
        try {
            Constructor<net.minecraft.world.level.block.PressurePlateBlock> ctor =
                net.minecraft.world.level.block.PressurePlateBlock.class.getDeclaredConstructor(
                    BlockSetType.class, BlockBehaviour.Properties.class);
            ctor.setAccessible(true);
            return ctor.newInstance(BlockSetType.STONE, props);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Failed to create PressurePlateBlock", e);
        }
    }

    public static Block createButton(int ticksToStayPressed, BlockBehaviour.Properties props) {
        try {
            Constructor<net.minecraft.world.level.block.ButtonBlock> ctor =
                net.minecraft.world.level.block.ButtonBlock.class.getDeclaredConstructor(
                    BlockSetType.class, int.class, BlockBehaviour.Properties.class);
            ctor.setAccessible(true);
            return ctor.newInstance(BlockSetType.STONE, ticksToStayPressed, props);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Failed to create ButtonBlock", e);
        }
    }

    public static Block createStairs(BlockState baseBlockState, BlockBehaviour.Properties props) {
        try {
            Constructor<net.minecraft.world.level.block.StairBlock> ctor =
                net.minecraft.world.level.block.StairBlock.class.getDeclaredConstructor(
                    BlockState.class, BlockBehaviour.Properties.class);
            ctor.setAccessible(true);
            return ctor.newInstance(baseBlockState, props);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Failed to create StairBlock", e);
        }
    }

    public static Block createFence(BlockBehaviour.Properties props) {
        return new net.minecraft.world.level.block.FenceBlock(props);
    }

    public static Block createFenceGate(BlockBehaviour.Properties props) {
        return new net.minecraft.world.level.block.FenceGateBlock(
            net.minecraft.world.level.block.state.properties.WoodType.OAK, props);
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
                        Component.literal("JustBlockShapes compat resources"),
                        new InclusiveRange<>(format)
                    );
                }
                return null;
            }
        };
    }
}
