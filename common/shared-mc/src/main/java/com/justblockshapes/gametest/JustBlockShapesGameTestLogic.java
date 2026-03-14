package com.justblockshapes.gametest;

import com.justblockshapes.ModBlocks;
import com.justblockshapes.ModBlocks.BlockEntry;
import com.justblockshapes.ModBlocks.VariantType;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.WallBlock;

public class JustBlockShapesGameTestLogic {

    public static void allBlocksAreRegistered(GameTestHelper helper) {
        var registered = ModBlocks.getRegisteredBlocks();
        int expectedCount = 0;
        for (BlockEntry entry : ModBlocks.getBlockEntries()) {
            expectedCount += entry.variants().size();
        }
        if (registered.size() != expectedCount) {
            throw new RuntimeException("Expected " + expectedCount + " blocks but found " + registered.size());
        }
        helper.succeed();
    }

    public static void registeredBlocksHaveCorrectTypes(GameTestHelper helper) {
        var registered = ModBlocks.getRegisteredBlocks();
        for (BlockEntry entry : ModBlocks.getBlockEntries()) {
            for (VariantType variant : entry.variants()) {
                String id = ModBlocks.variantBlockId(entry.baseBlockId(), variant);
                Block block = registered.get(id);
                if (block == null) {
                    throw new RuntimeException("Block not registered: " + id);
                }
                Class<? extends Block> expected = switch (variant) {
                    case STAIRS -> StairBlock.class;
                    case SLAB -> SlabBlock.class;
                    case WALL -> WallBlock.class;
                    case TRAPDOOR -> TrapDoorBlock.class;
                    case DOOR -> DoorBlock.class;
                    case PRESSURE_PLATE -> PressurePlateBlock.class;
                    case BUTTON -> Block.class;
                };
                if (variant != VariantType.BUTTON && !expected.isInstance(block)) {
                    throw new RuntimeException(id + " expected " + expected.getSimpleName()
                        + " but was " + block.getClass().getSimpleName());
                }
            }
        }
        helper.succeed();
    }

    public static void variantBlockIdFormat(GameTestHelper helper) {
        for (VariantType variant : VariantType.values()) {
            String id = ModBlocks.variantBlockId("test_block", variant);
            String expectedSuffix = switch (variant) {
                case STAIRS -> "_stairs";
                case SLAB -> "_slab";
                case WALL -> "_wall";
                case TRAPDOOR -> "_trapdoor";
                case DOOR -> "_door";
                case PRESSURE_PLATE -> "_pressure_plate";
                case BUTTON -> "_button";
            };
            if (!id.equals("test_block" + expectedSuffix)) {
                throw new RuntimeException("variantBlockId for " + variant + " returned " + id
                    + " expected test_block" + expectedSuffix);
            }
        }
        helper.succeed();
    }

    public static void blocksCanBePlaced(GameTestHelper helper) {
        var registered = ModBlocks.getRegisteredBlocks();
        BlockPos pos = new BlockPos(0, 1, 0);
        int placed = 0;
        for (var entry : registered.entrySet()) {
            Block block = entry.getValue();
            helper.setBlock(pos, block.defaultBlockState());
            helper.assertBlockPresent(block, pos);
            placed++;
        }
        if (placed == 0) {
            throw new RuntimeException("No blocks were placed");
        }
        helper.succeed();
    }
}
