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

    public static Block createTrapDoor(BlockBehaviour.Properties props) {
        return new net.minecraft.world.level.block.TrapDoorBlock(props,
            net.minecraft.world.level.block.state.properties.BlockSetType.OAK);
    }

    public static Block createDoor(BlockBehaviour.Properties props) {
        return new net.minecraft.world.level.block.DoorBlock(props,
            net.minecraft.world.level.block.state.properties.BlockSetType.OAK);
    }

    public static Block createPressurePlate(BlockBehaviour.Properties props) {
        return new net.minecraft.world.level.block.PressurePlateBlock(
            net.minecraft.world.level.block.PressurePlateBlock.Sensitivity.MOBS, props,
            net.minecraft.world.level.block.state.properties.BlockSetType.STONE) {};
    }

    public static Block createButton(int ticksToStayPressed, BlockBehaviour.Properties props) {
        return new net.minecraft.world.level.block.ButtonBlock(props,
            net.minecraft.world.level.block.state.properties.BlockSetType.STONE,
            ticksToStayPressed, false) {};
    }
}
