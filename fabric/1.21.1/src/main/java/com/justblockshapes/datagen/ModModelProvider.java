package com.justblockshapes.datagen;

import com.justblockshapes.JustBlockShapes;
import com.justblockshapes.ModBlocks;
import com.justblockshapes.ModBlocks.VariantType;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.blockstates.BlockStateGenerator;
import net.minecraft.data.models.model.ModelTemplate;
import net.minecraft.data.models.model.ModelTemplates;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.data.models.model.TextureSlot;
import net.minecraft.data.models.model.TexturedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

import java.util.Optional;

public class ModModelProvider extends FabricModelProvider {

    private static final ModelTemplate DOOR_ITEM = new ModelTemplate(
        Optional.of(ResourceLocation.fromNamespaceAndPath(JustBlockShapes.MOD_ID, "item/template_door")),
        Optional.empty(),
        TextureSlot.TEXTURE);

    private static final ModelTemplate CUSTOM_DOOR_BOTTOM_LEFT = new ModelTemplate(
        Optional.of(ResourceLocation.fromNamespaceAndPath(JustBlockShapes.MOD_ID, "block/template_door_bottom_left")),
        Optional.of("_bottom_left"),
        TextureSlot.TOP, TextureSlot.BOTTOM);
    private static final ModelTemplate CUSTOM_DOOR_BOTTOM_LEFT_OPEN = new ModelTemplate(
        Optional.of(ResourceLocation.fromNamespaceAndPath(JustBlockShapes.MOD_ID, "block/template_door_bottom_left_open")),
        Optional.of("_bottom_left_open"),
        TextureSlot.TOP, TextureSlot.BOTTOM);
    private static final ModelTemplate CUSTOM_DOOR_BOTTOM_RIGHT = new ModelTemplate(
        Optional.of(ResourceLocation.fromNamespaceAndPath(JustBlockShapes.MOD_ID, "block/template_door_bottom_right")),
        Optional.of("_bottom_right"),
        TextureSlot.TOP, TextureSlot.BOTTOM);
    private static final ModelTemplate CUSTOM_DOOR_BOTTOM_RIGHT_OPEN = new ModelTemplate(
        Optional.of(ResourceLocation.fromNamespaceAndPath(JustBlockShapes.MOD_ID, "block/template_door_bottom_right_open")),
        Optional.of("_bottom_right_open"),
        TextureSlot.TOP, TextureSlot.BOTTOM);

    public ModModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators gen) {
        for (ModBlocks.BlockEntry entry : ModBlocks.getBlockEntries()) {
            String baseId = entry.baseBlockId();
            ResourceLocation baseTexture = getBaseTexture(baseId);
            TextureMapping textureMapping = TextureMapping.cube(baseTexture);

            Block stairsBlock = null;
            Block slabBlock = null;
            Block wallBlock = null;
            Block trapdoorBlock = null;
            Block doorBlock = null;
            Block pressurePlateBlock = null;
            Block fenceBlock = null;
            Block fenceGateBlock = null;
            Block buttonBlock = null;

            for (VariantType variant : entry.variants()) {
                String id = ModBlocks.variantBlockId(baseId, variant);
                Block block = ModBlocks.getRegisteredBlocks().get(id);
                if (block == null) continue;

                switch (variant) {
                    case STAIRS -> stairsBlock = block;
                    case SLAB -> slabBlock = block;
                    case WALL -> wallBlock = block;
                    case FENCE -> fenceBlock = block;
                    case FENCE_GATE -> fenceGateBlock = block;
                    case TRAPDOOR -> trapdoorBlock = block;
                    case DOOR -> doorBlock = block;
                    case PRESSURE_PLATE -> pressurePlateBlock = block;
                    case BUTTON -> buttonBlock = block;
                }
            }

            if (stairsBlock != null) {
                ResourceLocation inner = ModelTemplates.STAIRS_INNER.create(stairsBlock,
                    textureMapping, gen.modelOutput);
                ResourceLocation straight = ModelTemplates.STAIRS_STRAIGHT.create(stairsBlock,
                    textureMapping, gen.modelOutput);
                ResourceLocation outer = ModelTemplates.STAIRS_OUTER.create(stairsBlock,
                    textureMapping, gen.modelOutput);
                gen.blockStateOutput.accept(
                    BlockModelGenerators.createStairs(stairsBlock, inner, straight, outer));
                gen.delegateItemModel(stairsBlock, straight);
            }

            if (slabBlock != null) {
                ResourceLocation fullBlockModel = getFullBlockModel(baseId);
                ResourceLocation bottom = ModelTemplates.SLAB_BOTTOM.create(slabBlock,
                    textureMapping, gen.modelOutput);
                ResourceLocation top = ModelTemplates.SLAB_TOP.create(slabBlock,
                    textureMapping, gen.modelOutput);
                gen.blockStateOutput.accept(
                    BlockModelGenerators.createSlab(slabBlock, bottom, top, fullBlockModel));
                gen.delegateItemModel(slabBlock, bottom);
            }

            if (wallBlock != null) {
                ResourceLocation post = ModelTemplates.WALL_POST.create(wallBlock,
                    textureMapping, gen.modelOutput);
                ResourceLocation low = ModelTemplates.WALL_LOW_SIDE.create(wallBlock,
                    textureMapping, gen.modelOutput);
                ResourceLocation tall = ModelTemplates.WALL_TALL_SIDE.create(wallBlock,
                    textureMapping, gen.modelOutput);
                gen.blockStateOutput.accept(
                    BlockModelGenerators.createWall(wallBlock, post, low, tall));
                ResourceLocation inventory = ModelTemplates.WALL_INVENTORY.create(wallBlock,
                    textureMapping, gen.modelOutput);
                gen.delegateItemModel(wallBlock, inventory);
            }

            if (fenceBlock != null) {
                TextureMapping singleTexture = new TextureMapping().put(TextureSlot.TEXTURE, baseTexture);
                ResourceLocation post = ModelTemplates.FENCE_POST.create(fenceBlock, singleTexture, gen.modelOutput);
                ResourceLocation side = ModelTemplates.FENCE_SIDE.create(fenceBlock, singleTexture, gen.modelOutput);
                gen.blockStateOutput.accept(
                    BlockModelGenerators.createFence(fenceBlock, post, side));
                ResourceLocation inventory = ModelTemplates.FENCE_INVENTORY.create(fenceBlock, singleTexture, gen.modelOutput);
                gen.delegateItemModel(fenceBlock, inventory);
            }

            if (fenceGateBlock != null) {
                TextureMapping singleTexture = new TextureMapping().put(TextureSlot.TEXTURE, baseTexture);
                ResourceLocation gate = ModelTemplates.FENCE_GATE_CLOSED.create(fenceGateBlock, singleTexture, gen.modelOutput);
                ResourceLocation gateOpen = ModelTemplates.FENCE_GATE_OPEN.create(fenceGateBlock, singleTexture, gen.modelOutput);
                ResourceLocation gateWall = ModelTemplates.FENCE_GATE_WALL_CLOSED.create(fenceGateBlock, singleTexture, gen.modelOutput);
                ResourceLocation gateWallOpen = ModelTemplates.FENCE_GATE_WALL_OPEN.create(fenceGateBlock, singleTexture, gen.modelOutput);
                gen.blockStateOutput.accept(
                    BlockModelGenerators.createFenceGate(fenceGateBlock, gate, gateOpen, gateWall, gateWallOpen, false));
                gen.delegateItemModel(fenceGateBlock, gate);
            }

            if (trapdoorBlock != null) {
                ResourceLocation top = ModelTemplates.TRAPDOOR_TOP.create(trapdoorBlock,
                    textureMapping, gen.modelOutput);
                ResourceLocation bottom = ModelTemplates.TRAPDOOR_BOTTOM.create(trapdoorBlock,
                    textureMapping, gen.modelOutput);
                ResourceLocation open = ModelTemplates.TRAPDOOR_OPEN.create(trapdoorBlock,
                    textureMapping, gen.modelOutput);
                gen.blockStateOutput.accept(
                    BlockModelGenerators.createTrapdoor(trapdoorBlock, top, bottom, open));
                gen.delegateItemModel(trapdoorBlock, bottom);
            }

            if (doorBlock != null) {
                TextureMapping doorTexture = new TextureMapping()
                    .put(TextureSlot.TOP, baseTexture)
                    .put(TextureSlot.BOTTOM, baseTexture);
                ResourceLocation bottomLeft = CUSTOM_DOOR_BOTTOM_LEFT.create(doorBlock, doorTexture, gen.modelOutput);
                ResourceLocation bottomLeftOpen = CUSTOM_DOOR_BOTTOM_LEFT_OPEN.create(doorBlock, doorTexture, gen.modelOutput);
                ResourceLocation bottomRight = CUSTOM_DOOR_BOTTOM_RIGHT.create(doorBlock, doorTexture, gen.modelOutput);
                ResourceLocation bottomRightOpen = CUSTOM_DOOR_BOTTOM_RIGHT_OPEN.create(doorBlock, doorTexture, gen.modelOutput);
                ResourceLocation topLeft = ModelTemplates.DOOR_TOP_LEFT.create(doorBlock, doorTexture, gen.modelOutput);
                ResourceLocation topLeftOpen = ModelTemplates.DOOR_TOP_LEFT_OPEN.create(doorBlock, doorTexture, gen.modelOutput);
                ResourceLocation topRight = ModelTemplates.DOOR_TOP_RIGHT.create(doorBlock, doorTexture, gen.modelOutput);
                ResourceLocation topRightOpen = ModelTemplates.DOOR_TOP_RIGHT_OPEN.create(doorBlock, doorTexture, gen.modelOutput);
                gen.blockStateOutput.accept(
                    BlockModelGenerators.createDoor(doorBlock, bottomLeft, bottomLeftOpen,
                        bottomRight, bottomRightOpen, topLeft, topLeftOpen, topRight, topRightOpen));
                // Item model using template_door parent
                String doorId = ModBlocks.variantBlockId(baseId, VariantType.DOOR);
                TextureMapping itemTexture = new TextureMapping().put(TextureSlot.TEXTURE, baseTexture);
                DOOR_ITEM.create(
                    ResourceLocation.fromNamespaceAndPath(JustBlockShapes.MOD_ID, "item/" + doorId),
                    itemTexture, gen.modelOutput);
            }

            if (pressurePlateBlock != null) {
                TextureMapping singleTexture = new TextureMapping().put(TextureSlot.TEXTURE, baseTexture);
                ResourceLocation up = ModelTemplates.PRESSURE_PLATE_UP.create(pressurePlateBlock, singleTexture, gen.modelOutput);
                ResourceLocation down = ModelTemplates.PRESSURE_PLATE_DOWN.create(pressurePlateBlock, singleTexture, gen.modelOutput);
                gen.blockStateOutput.accept(
                    BlockModelGenerators.createPressurePlate(pressurePlateBlock, up, down));
                gen.delegateItemModel(pressurePlateBlock, up);
            }

            if (buttonBlock != null) {
                TextureMapping singleTexture = new TextureMapping().put(TextureSlot.TEXTURE, baseTexture);
                ResourceLocation button = ModelTemplates.BUTTON.create(buttonBlock, singleTexture, gen.modelOutput);
                ResourceLocation pressed = ModelTemplates.BUTTON_PRESSED.create(buttonBlock, singleTexture, gen.modelOutput);
                gen.blockStateOutput.accept(
                    BlockModelGenerators.createButton(buttonBlock, button, pressed));
                ResourceLocation inventory = ModelTemplates.BUTTON_INVENTORY.create(buttonBlock, singleTexture, gen.modelOutput);
                gen.delegateItemModel(buttonBlock, inventory);
            }
        }
    }

    @Override
    public void generateItemModels(ItemModelGenerators itemModelGenerator) {
    }

    private static ResourceLocation getBaseTexture(String baseBlockId) {
        return switch (baseBlockId) {
            case "quartz_block" -> ResourceLocation.withDefaultNamespace("block/quartz_block_side");
            case "smooth_quartz" -> ResourceLocation.withDefaultNamespace("block/quartz_block_bottom");
            case "smooth_sandstone" -> ResourceLocation.withDefaultNamespace("block/sandstone_top");
            case "smooth_red_sandstone" -> ResourceLocation.withDefaultNamespace("block/red_sandstone_top");
            case "prismarine_bricks" -> ResourceLocation.withDefaultNamespace("block/prismarine_bricks");
            case "dark_prismarine" -> ResourceLocation.withDefaultNamespace("block/dark_prismarine");
            default -> ResourceLocation.withDefaultNamespace("block/" + baseBlockId);
        };
    }

    private static ResourceLocation getFullBlockModel(String baseBlockId) {
        return switch (baseBlockId) {
            case "smooth_quartz" -> ResourceLocation.withDefaultNamespace("block/smooth_quartz");
            default -> ResourceLocation.withDefaultNamespace("block/" + baseBlockId);
        };
    }
}
