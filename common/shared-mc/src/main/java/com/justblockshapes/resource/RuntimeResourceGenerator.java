package com.justblockshapes.resource;

import com.justblockshapes.JustBlockShapes;
import com.justblockshapes.ModBlocks;
import com.justblockshapes.ModBlocks.VariantType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;

/**
 * Generates JSON resources at runtime,
 * eliminating the need for static JSON files.
 */
public class RuntimeResourceGenerator {

    private static final String MOD_ID = JustBlockShapes.MOD_ID;

    public static void generate(InMemoryResourcePack pack) {
        java.util.Map<String, java.util.List<String>> tagEntries = new java.util.LinkedHashMap<>();
        for (ModBlocks.BlockEntry entry : ModBlocks.getBlockEntries()) {
            String baseBlockId = entry.baseBlockId();
            String texture = getVanillaTexture(baseBlockId);
            String baseItem = "minecraft:" + baseBlockId;

            for (VariantType variant : entry.variants()) {
                String blockId = ModBlocks.variantBlockId(baseBlockId, variant);

                generateBlockstate(pack, blockId, variant);
                generateBlockModels(pack, blockId, variant, texture);
                generateItemModel(pack, blockId, variant, texture);
                generateItemDefinition(pack, blockId);
                generateLootTable(pack, blockId, variant);
                generateVanillaRecipes(pack, blockId, variant, baseItem);
                generateAdvancement(pack, blockId, baseItem);
                generateAdvancement(pack, blockId + "_stonecutting", baseItem);

                // Collect for tags
                String tagName = variantTagName(variant);
                tagEntries.computeIfAbsent(tagName, k -> new java.util.ArrayList<>())
                    .add(MOD_ID + ":" + blockId);
                tagEntries.computeIfAbsent("mineable/pickaxe", k -> new java.util.ArrayList<>())
                    .add(MOD_ID + ":" + blockId);
            }
        }

        // Generate tag files
        for (var tagEntry : tagEntries.entrySet()) {
            generateTag(pack, tagEntry.getKey(), tagEntry.getValue());
        }
    }

    /**
     * Resolves the base texture for vanilla blocks.
     * Some vanilla blocks use a different texture than their block ID would suggest.
     */
    private static String getVanillaTexture(String baseBlockId) {
        return switch (baseBlockId) {
            case "quartz_block" -> "minecraft:block/quartz_block_side";
            case "smooth_quartz" -> "minecraft:block/quartz_block_bottom";
            case "smooth_sandstone" -> "minecraft:block/sandstone_top";
            case "smooth_red_sandstone" -> "minecraft:block/red_sandstone_top";
            default -> "minecraft:block/" + baseBlockId;
        };
    }

    // ---- Blockstate generation ----

    private static void generateBlockstate(InMemoryResourcePack pack, String blockId, VariantType variant) {
        String json = switch (variant) {
            case STAIRS -> stairsBlockstate(blockId);
            case SLAB -> slabBlockstate(blockId);
            case WALL -> wallBlockstate(blockId);
            case TRAPDOOR -> trapdoorBlockstate(blockId);
            case DOOR -> doorBlockstate(blockId);
            case BUTTON -> buttonBlockstate(blockId);
            case PRESSURE_PLATE -> pressurePlateBlockstate(blockId);
        };
        pack.addResource(PackType.CLIENT_RESOURCES,
            ResourceLocation.fromNamespaceAndPath(MOD_ID, "blockstates/" + blockId + ".json"), json);
    }

    private static String stairsBlockstate(String id) {
        String m = MOD_ID + ":block/" + id;
        String mi = m + "_inner";
        String mo = m + "_outer";
        return """
            {"variants":{
            "facing=east,half=bottom,shape=inner_left":{"model":"%s","uvlock":true,"y":270},
            "facing=east,half=bottom,shape=inner_right":{"model":"%s"},
            "facing=east,half=bottom,shape=outer_left":{"model":"%s","uvlock":true,"y":270},
            "facing=east,half=bottom,shape=outer_right":{"model":"%s"},
            "facing=east,half=bottom,shape=straight":{"model":"%s"},
            "facing=east,half=top,shape=inner_left":{"model":"%s","uvlock":true,"x":180},
            "facing=east,half=top,shape=inner_right":{"model":"%s","uvlock":true,"x":180,"y":90},
            "facing=east,half=top,shape=outer_left":{"model":"%s","uvlock":true,"x":180},
            "facing=east,half=top,shape=outer_right":{"model":"%s","uvlock":true,"x":180,"y":90},
            "facing=east,half=top,shape=straight":{"model":"%s","uvlock":true,"x":180},
            "facing=north,half=bottom,shape=inner_left":{"model":"%s","uvlock":true,"y":180},
            "facing=north,half=bottom,shape=inner_right":{"model":"%s","uvlock":true,"y":270},
            "facing=north,half=bottom,shape=outer_left":{"model":"%s","uvlock":true,"y":180},
            "facing=north,half=bottom,shape=outer_right":{"model":"%s","uvlock":true,"y":270},
            "facing=north,half=bottom,shape=straight":{"model":"%s","uvlock":true,"y":270},
            "facing=north,half=top,shape=inner_left":{"model":"%s","uvlock":true,"x":180,"y":270},
            "facing=north,half=top,shape=inner_right":{"model":"%s","uvlock":true,"x":180},
            "facing=north,half=top,shape=outer_left":{"model":"%s","uvlock":true,"x":180,"y":270},
            "facing=north,half=top,shape=outer_right":{"model":"%s","uvlock":true,"x":180},
            "facing=north,half=top,shape=straight":{"model":"%s","uvlock":true,"x":180,"y":270},
            "facing=south,half=bottom,shape=inner_left":{"model":"%s"},
            "facing=south,half=bottom,shape=inner_right":{"model":"%s","uvlock":true,"y":90},
            "facing=south,half=bottom,shape=outer_left":{"model":"%s"},
            "facing=south,half=bottom,shape=outer_right":{"model":"%s","uvlock":true,"y":90},
            "facing=south,half=bottom,shape=straight":{"model":"%s","uvlock":true,"y":90},
            "facing=south,half=top,shape=inner_left":{"model":"%s","uvlock":true,"x":180,"y":90},
            "facing=south,half=top,shape=inner_right":{"model":"%s","uvlock":true,"x":180,"y":180},
            "facing=south,half=top,shape=outer_left":{"model":"%s","uvlock":true,"x":180,"y":90},
            "facing=south,half=top,shape=outer_right":{"model":"%s","uvlock":true,"x":180,"y":180},
            "facing=south,half=top,shape=straight":{"model":"%s","uvlock":true,"x":180,"y":90},
            "facing=west,half=bottom,shape=inner_left":{"model":"%s","uvlock":true,"y":90},
            "facing=west,half=bottom,shape=inner_right":{"model":"%s","uvlock":true,"y":180},
            "facing=west,half=bottom,shape=outer_left":{"model":"%s","uvlock":true,"y":90},
            "facing=west,half=bottom,shape=outer_right":{"model":"%s","uvlock":true,"y":180},
            "facing=west,half=bottom,shape=straight":{"model":"%s","uvlock":true,"y":180},
            "facing=west,half=top,shape=inner_left":{"model":"%s","uvlock":true,"x":180,"y":180},
            "facing=west,half=top,shape=inner_right":{"model":"%s","uvlock":true,"x":180,"y":270},
            "facing=west,half=top,shape=outer_left":{"model":"%s","uvlock":true,"x":180,"y":180},
            "facing=west,half=top,shape=outer_right":{"model":"%s","uvlock":true,"x":180,"y":270},
            "facing=west,half=top,shape=straight":{"model":"%s","uvlock":true,"x":180,"y":180}
            }}""".formatted(
            mi, mi, mo, mo, m,
            mi, mi, mo, mo, m,
            mi, mi, mo, mo, m,
            mi, mi, mo, mo, m,
            mi, mi, mo, mo, m,
            mi, mi, mo, mo, m,
            mi, mi, mo, mo, m,
            mi, mi, mo, mo, m);
    }

    private static String slabBlockstate(String id) {
        // For double slab, reference the original vanilla full block model
        String baseBlockId = id.substring(0, id.length() - "_slab".length());
        return """
            {"variants":{
            "type=bottom":{"model":"%s:block/%s"},
            "type=double":{"model":"minecraft:block/%s"},
            "type=top":{"model":"%s:block/%s_top"}
            }}""".formatted(MOD_ID, id, baseBlockId, MOD_ID, id);
    }

    private static String wallBlockstate(String id) {
        String m = MOD_ID + ":block/" + id;
        return """
            {"multipart":[
            {"apply":{"model":"%s_post"},"when":{"up":"true"}},
            {"apply":{"model":"%s_side","uvlock":true},"when":{"north":"low"}},
            {"apply":{"model":"%s_side","uvlock":true,"y":90},"when":{"east":"low"}},
            {"apply":{"model":"%s_side","uvlock":true,"y":180},"when":{"south":"low"}},
            {"apply":{"model":"%s_side","uvlock":true,"y":270},"when":{"west":"low"}},
            {"apply":{"model":"%s_side_tall","uvlock":true},"when":{"north":"tall"}},
            {"apply":{"model":"%s_side_tall","uvlock":true,"y":90},"when":{"east":"tall"}},
            {"apply":{"model":"%s_side_tall","uvlock":true,"y":180},"when":{"south":"tall"}},
            {"apply":{"model":"%s_side_tall","uvlock":true,"y":270},"when":{"west":"tall"}}
            ]}""".formatted(m, m, m, m, m, m, m, m, m);
    }

    private static String doorBlockstate(String id) {
        String m = MOD_ID + ":block/" + id;
        return """
            {"variants":{
            "facing=east,half=lower,hinge=left,open=false":{"model":"%s_bottom_left"},
            "facing=east,half=lower,hinge=left,open=true":{"model":"%s_bottom_left_open","y":90},
            "facing=east,half=lower,hinge=right,open=false":{"model":"%s_bottom_right"},
            "facing=east,half=lower,hinge=right,open=true":{"model":"%s_bottom_right_open","y":270},
            "facing=east,half=upper,hinge=left,open=false":{"model":"%s_top_left"},
            "facing=east,half=upper,hinge=left,open=true":{"model":"%s_top_left_open","y":90},
            "facing=east,half=upper,hinge=right,open=false":{"model":"%s_top_right"},
            "facing=east,half=upper,hinge=right,open=true":{"model":"%s_top_right_open","y":270},
            "facing=north,half=lower,hinge=left,open=false":{"model":"%s_bottom_left","y":270},
            "facing=north,half=lower,hinge=left,open=true":{"model":"%s_bottom_left_open"},
            "facing=north,half=lower,hinge=right,open=false":{"model":"%s_bottom_right","y":270},
            "facing=north,half=lower,hinge=right,open=true":{"model":"%s_bottom_right_open","y":180},
            "facing=north,half=upper,hinge=left,open=false":{"model":"%s_top_left","y":270},
            "facing=north,half=upper,hinge=left,open=true":{"model":"%s_top_left_open"},
            "facing=north,half=upper,hinge=right,open=false":{"model":"%s_top_right","y":270},
            "facing=north,half=upper,hinge=right,open=true":{"model":"%s_top_right_open","y":180},
            "facing=south,half=lower,hinge=left,open=false":{"model":"%s_bottom_left","y":90},
            "facing=south,half=lower,hinge=left,open=true":{"model":"%s_bottom_left_open","y":180},
            "facing=south,half=lower,hinge=right,open=false":{"model":"%s_bottom_right","y":90},
            "facing=south,half=lower,hinge=right,open=true":{"model":"%s_bottom_right_open"},
            "facing=south,half=upper,hinge=left,open=false":{"model":"%s_top_left","y":90},
            "facing=south,half=upper,hinge=left,open=true":{"model":"%s_top_left_open","y":180},
            "facing=south,half=upper,hinge=right,open=false":{"model":"%s_top_right","y":90},
            "facing=south,half=upper,hinge=right,open=true":{"model":"%s_top_right_open"},
            "facing=west,half=lower,hinge=left,open=false":{"model":"%s_bottom_left","y":180},
            "facing=west,half=lower,hinge=left,open=true":{"model":"%s_bottom_left_open","y":270},
            "facing=west,half=lower,hinge=right,open=false":{"model":"%s_bottom_right","y":180},
            "facing=west,half=lower,hinge=right,open=true":{"model":"%s_bottom_right_open","y":90},
            "facing=west,half=upper,hinge=left,open=false":{"model":"%s_top_left","y":180},
            "facing=west,half=upper,hinge=left,open=true":{"model":"%s_top_left_open","y":270},
            "facing=west,half=upper,hinge=right,open=false":{"model":"%s_top_right","y":180},
            "facing=west,half=upper,hinge=right,open=true":{"model":"%s_top_right_open","y":90}
            }}""".formatted(
            m, m, m, m, m, m, m, m,
            m, m, m, m, m, m, m, m,
            m, m, m, m, m, m, m, m,
            m, m, m, m, m, m, m, m);
    }

    private static String trapdoorBlockstate(String id) {
        String m = MOD_ID + ":block/" + id;
        return """
            {"variants":{
            "facing=east,half=bottom,open=false":{"model":"%s_bottom"},
            "facing=east,half=bottom,open=true":{"model":"%s_open","y":90},
            "facing=east,half=top,open=false":{"model":"%s_top"},
            "facing=east,half=top,open=true":{"model":"%s_open","y":90},
            "facing=north,half=bottom,open=false":{"model":"%s_bottom"},
            "facing=north,half=bottom,open=true":{"model":"%s_open"},
            "facing=north,half=top,open=false":{"model":"%s_top"},
            "facing=north,half=top,open=true":{"model":"%s_open"},
            "facing=south,half=bottom,open=false":{"model":"%s_bottom"},
            "facing=south,half=bottom,open=true":{"model":"%s_open","y":180},
            "facing=south,half=top,open=false":{"model":"%s_top"},
            "facing=south,half=top,open=true":{"model":"%s_open","y":180},
            "facing=west,half=bottom,open=false":{"model":"%s_bottom"},
            "facing=west,half=bottom,open=true":{"model":"%s_open","y":270},
            "facing=west,half=top,open=false":{"model":"%s_top"},
            "facing=west,half=top,open=true":{"model":"%s_open","y":270}
            }}""".formatted(m, m, m, m, m, m, m, m, m, m, m, m, m, m, m, m);
    }

    private static String buttonBlockstate(String id) {
        String m = MOD_ID + ":block/" + id;
        String mp = m + "_pressed";
        return """
            {"variants":{
            "face=ceiling,facing=east,powered=false":{"model":"%s","x":180,"y":270},
            "face=ceiling,facing=east,powered=true":{"model":"%s","x":180,"y":270},
            "face=ceiling,facing=north,powered=false":{"model":"%s","x":180,"y":180},
            "face=ceiling,facing=north,powered=true":{"model":"%s","x":180,"y":180},
            "face=ceiling,facing=south,powered=false":{"model":"%s","x":180},
            "face=ceiling,facing=south,powered=true":{"model":"%s","x":180},
            "face=ceiling,facing=west,powered=false":{"model":"%s","x":180,"y":90},
            "face=ceiling,facing=west,powered=true":{"model":"%s","x":180,"y":90},
            "face=floor,facing=east,powered=false":{"model":"%s","y":90},
            "face=floor,facing=east,powered=true":{"model":"%s","y":90},
            "face=floor,facing=north,powered=false":{"model":"%s"},
            "face=floor,facing=north,powered=true":{"model":"%s"},
            "face=floor,facing=south,powered=false":{"model":"%s","y":180},
            "face=floor,facing=south,powered=true":{"model":"%s","y":180},
            "face=floor,facing=west,powered=false":{"model":"%s","y":270},
            "face=floor,facing=west,powered=true":{"model":"%s","y":270},
            "face=wall,facing=east,powered=false":{"model":"%s","uvlock":true,"x":90,"y":90},
            "face=wall,facing=east,powered=true":{"model":"%s","uvlock":true,"x":90,"y":90},
            "face=wall,facing=north,powered=false":{"model":"%s","uvlock":true,"x":90},
            "face=wall,facing=north,powered=true":{"model":"%s","uvlock":true,"x":90},
            "face=wall,facing=south,powered=false":{"model":"%s","uvlock":true,"x":90,"y":180},
            "face=wall,facing=south,powered=true":{"model":"%s","uvlock":true,"x":90,"y":180},
            "face=wall,facing=west,powered=false":{"model":"%s","uvlock":true,"x":90,"y":270},
            "face=wall,facing=west,powered=true":{"model":"%s","uvlock":true,"x":90,"y":270}
            }}""".formatted(m, mp, m, mp, m, mp, m, mp, m, mp, m, mp, m, mp, m, mp, m, mp, m, mp, m, mp, m, mp);
    }

    private static String pressurePlateBlockstate(String id) {
        String m = MOD_ID + ":block/" + id;
        return """
            {"variants":{
            "powered=false":{"model":"%s"},
            "powered=true":{"model":"%s_down"}
            }}""".formatted(m, m);
    }

    // ---- Block model generation ----

    private static void generateBlockModels(InMemoryResourcePack pack, String blockId,
                                            VariantType variant, String texture) {
        switch (variant) {
            case STAIRS -> {
                addBlockModel(pack, blockId, "minecraft:block/stairs", threeTextures(texture));
                addBlockModel(pack, blockId + "_inner", "minecraft:block/inner_stairs", threeTextures(texture));
                addBlockModel(pack, blockId + "_outer", "minecraft:block/outer_stairs", threeTextures(texture));
            }
            case SLAB -> {
                addBlockModel(pack, blockId, "minecraft:block/slab", threeTextures(texture));
                addBlockModel(pack, blockId + "_top", "minecraft:block/slab_top", threeTextures(texture));
            }
            case WALL -> {
                addBlockModel(pack, blockId + "_inventory", "minecraft:block/wall_inventory", wallTexture(texture));
                addBlockModel(pack, blockId + "_post", "minecraft:block/template_wall_post", wallTexture(texture));
                addBlockModel(pack, blockId + "_side", "minecraft:block/template_wall_side", wallTexture(texture));
                addBlockModel(pack, blockId + "_side_tall", "minecraft:block/template_wall_side_tall", wallTexture(texture));
            }
            case TRAPDOOR -> {
                addBlockModel(pack, blockId + "_bottom", "minecraft:block/template_trapdoor_bottom", singleTexture(texture));
                addBlockModel(pack, blockId + "_open", "minecraft:block/template_trapdoor_open", singleTexture(texture));
                addBlockModel(pack, blockId + "_top", "minecraft:block/template_trapdoor_top", singleTexture(texture));
            }
            case DOOR -> {
                // Both halves use custom templates (with ambient occlusion enabled)
                addBlockModel(pack, blockId + "_bottom_left", MOD_ID + ":block/template_door_bottom_left", doorTextures(texture));
                addBlockModel(pack, blockId + "_bottom_left_open", MOD_ID + ":block/template_door_bottom_left_open", doorTextures(texture));
                addBlockModel(pack, blockId + "_bottom_right", MOD_ID + ":block/template_door_bottom_right", doorTextures(texture));
                addBlockModel(pack, blockId + "_bottom_right_open", MOD_ID + ":block/template_door_bottom_right_open", doorTextures(texture));
                addBlockModel(pack, blockId + "_top_left", MOD_ID + ":block/template_door_top_left", doorTextures(texture));
                addBlockModel(pack, blockId + "_top_left_open", MOD_ID + ":block/template_door_top_left_open", doorTextures(texture));
                addBlockModel(pack, blockId + "_top_right", MOD_ID + ":block/template_door_top_right", doorTextures(texture));
                addBlockModel(pack, blockId + "_top_right_open", MOD_ID + ":block/template_door_top_right_open", doorTextures(texture));
            }
            case BUTTON -> {
                addBlockModel(pack, blockId, "minecraft:block/button", singleTexture(texture));
                addBlockModel(pack, blockId + "_inventory", "minecraft:block/button_inventory", singleTexture(texture));
                addBlockModel(pack, blockId + "_pressed", "minecraft:block/button_pressed", singleTexture(texture));
            }
            case PRESSURE_PLATE -> {
                addBlockModel(pack, blockId, "minecraft:block/pressure_plate_up", singleTexture(texture));
                addBlockModel(pack, blockId + "_down", "minecraft:block/pressure_plate_down", singleTexture(texture));
            }
        }
    }

    private static void addBlockModel(InMemoryResourcePack pack, String modelId,
                                      String parent, String textures) {
        String json = "{\"parent\":\"%s\",\"textures\":{%s}}".formatted(parent, textures);
        pack.addResource(PackType.CLIENT_RESOURCES,
            ResourceLocation.fromNamespaceAndPath(MOD_ID, "models/block/" + modelId + ".json"), json);
    }

    private static String threeTextures(String texture) {
        return "\"bottom\":\"%s\",\"side\":\"%s\",\"top\":\"%s\"".formatted(texture, texture, texture);
    }

    private static String wallTexture(String texture) {
        return "\"wall\":\"%s\"".formatted(texture);
    }

    private static String singleTexture(String texture) {
        return "\"texture\":\"%s\"".formatted(texture);
    }

    private static String doorTextures(String texture) {
        return "\"bottom\":\"%s\",\"top\":\"%s\"".formatted(texture, texture);
    }

    // ---- Item model generation ----

    private static void generateItemModel(InMemoryResourcePack pack, String blockId,
                                          VariantType variant, String texture) {
        String json = switch (variant) {
            case STAIRS, SLAB, PRESSURE_PLATE ->
                "{\"parent\":\"%s:block/%s\"}".formatted(MOD_ID, blockId);
            case TRAPDOOR ->
                "{\"parent\":\"%s:block/%s_bottom\"}".formatted(MOD_ID, blockId);
            case WALL ->
                "{\"parent\":\"%s:block/%s_inventory\"}".formatted(MOD_ID, blockId);
            case DOOR ->
                "{\"parent\":\"%s:item/template_door\",\"textures\":{\"texture\":\"%s\"}}".formatted(MOD_ID, texture);
            case BUTTON ->
                "{\"parent\":\"%s:block/%s_inventory\"}".formatted(MOD_ID, blockId);
        };
        pack.addResource(PackType.CLIENT_RESOURCES,
            ResourceLocation.fromNamespaceAndPath(MOD_ID, "models/item/" + blockId + ".json"), json);
    }

    // ---- Item definition generation (1.21.4+ items/ directory format) ----

    private static void generateItemDefinition(InMemoryResourcePack pack, String blockId) {
        String json = "{\"model\":{\"type\":\"minecraft:model\",\"model\":\"%s:item/%s\"}}".formatted(MOD_ID, blockId);
        pack.addResource(PackType.CLIENT_RESOURCES,
            ResourceLocation.fromNamespaceAndPath(MOD_ID, "items/" + blockId + ".json"), json);
    }

    // ---- Loot table generation ----

    private static void generateLootTable(InMemoryResourcePack pack, String blockId, VariantType variant) {
        String json = switch (variant) {
            case SLAB -> slabLootTable(blockId);
            case DOOR -> doorLootTable(blockId);
            default -> simpleLootTable(blockId);
        };
        pack.addResource(PackType.SERVER_DATA,
            ResourceLocation.fromNamespaceAndPath(MOD_ID, "loot_table/blocks/" + blockId + ".json"), json);
    }

    private static String simpleLootTable(String blockId) {
        return """
            {"type":"minecraft:block","pools":[{"bonus_rolls":0.0,"conditions":[{"condition":"minecraft:survives_explosion"}],"entries":[{"type":"minecraft:item","name":"%s:%s"}],"rolls":1.0}]}""".formatted(MOD_ID, blockId);
    }

    private static String slabLootTable(String blockId) {
        return """
            {"type":"minecraft:block","pools":[{"bonus_rolls":0.0,"entries":[{"type":"minecraft:item","functions":[{"add":false,"conditions":[{"block":"%s:%s","condition":"minecraft:block_state_property","properties":{"type":"double"}}],"count":2.0,"function":"minecraft:set_count"},{"function":"minecraft:explosion_decay"}],"name":"%s:%s"}],"rolls":1.0}]}""".formatted(MOD_ID, blockId, MOD_ID, blockId);
    }

    private static String doorLootTable(String blockId) {
        return """
            {"type":"minecraft:block","pools":[{"bonus_rolls":0.0,"conditions":[{"condition":"minecraft:survives_explosion"}],"entries":[{"type":"minecraft:item","conditions":[{"block":"%s:%s","condition":"minecraft:block_state_property","properties":{"half":"lower"}}],"name":"%s:%s"}],"rolls":1.0}]}""".formatted(MOD_ID, blockId, MOD_ID, blockId);
    }

    // ---- Recipe generation ----

    private static void generateVanillaRecipes(InMemoryResourcePack pack, String blockId,
                                                VariantType variant, String baseItem) {
        String craftingJson = switch (variant) {
            case STAIRS ->
                "{\"type\":\"minecraft:crafting_shaped\",\"category\":\"building\",\"key\":{\"#\":{\"item\":\"%s\"}},\"pattern\":[\"#  \",\"## \",\"###\"],\"result\":{\"count\":4,\"id\":\"%s:%s\"}}".formatted(
                    baseItem, MOD_ID, blockId);
            case SLAB ->
                "{\"type\":\"minecraft:crafting_shaped\",\"category\":\"building\",\"key\":{\"#\":{\"item\":\"%s\"}},\"pattern\":[\"###\"],\"result\":{\"count\":6,\"id\":\"%s:%s\"}}".formatted(
                    baseItem, MOD_ID, blockId);
            case WALL ->
                "{\"type\":\"minecraft:crafting_shaped\",\"category\":\"building\",\"key\":{\"#\":{\"item\":\"%s\"}},\"pattern\":[\"###\",\"###\"],\"result\":{\"count\":6,\"id\":\"%s:%s\"}}".formatted(
                    baseItem, MOD_ID, blockId);
            case TRAPDOOR ->
                "{\"type\":\"minecraft:crafting_shaped\",\"category\":\"building\",\"key\":{\"#\":{\"item\":\"%s\"}},\"pattern\":[\"###\",\"###\"],\"result\":{\"count\":2,\"id\":\"%s:%s\"}}".formatted(
                    baseItem, MOD_ID, blockId);
            case DOOR ->
                "{\"type\":\"minecraft:crafting_shaped\",\"category\":\"building\",\"key\":{\"#\":{\"item\":\"%s\"}},\"pattern\":[\"##\",\"##\",\"##\"],\"result\":{\"count\":3,\"id\":\"%s:%s\"}}".formatted(
                    baseItem, MOD_ID, blockId);
            case PRESSURE_PLATE ->
                "{\"type\":\"minecraft:crafting_shaped\",\"category\":\"building\",\"key\":{\"#\":{\"item\":\"%s\"}},\"pattern\":[\"##\"],\"result\":{\"count\":1,\"id\":\"%s:%s\"}}".formatted(
                    baseItem, MOD_ID, blockId);
            case BUTTON ->
                "{\"type\":\"minecraft:crafting_shapeless\",\"category\":\"building\",\"ingredients\":[{\"item\":\"%s\"}],\"result\":{\"count\":1,\"id\":\"%s:%s\"}}".formatted(
                    baseItem, MOD_ID, blockId);
        };
        addRecipe(pack, blockId, craftingJson);

        // Stonecutting recipe for all variant types
        int count = variant == VariantType.SLAB ? 2 : 1;
        String scJson = "{\"type\":\"minecraft:stonecutting\",\"ingredient\":{\"item\":\"%s\"},\"result\":{\"count\":%d,\"id\":\"%s:%s\"}}".formatted(
            baseItem, count, MOD_ID, blockId);
        addRecipe(pack, blockId + "_stonecutting", scJson);
    }

    private static void addRecipe(InMemoryResourcePack pack, String recipeId, String json) {
        pack.addResource(PackType.SERVER_DATA,
            ResourceLocation.fromNamespaceAndPath(MOD_ID, "recipe/" + recipeId + ".json"), json);
    }

    // ---- Advancement generation ----

    private static void generateAdvancement(InMemoryResourcePack pack, String recipeId, String baseItem) {
        String json = "{\"parent\":\"minecraft:recipes/root\",\"criteria\":{\"has_material\":{\"conditions\":{\"items\":[{\"items\":\"%s\"}]},\"trigger\":\"minecraft:inventory_changed\"},\"has_the_recipe\":{\"conditions\":{\"recipe\":\"%s:%s\"},\"trigger\":\"minecraft:recipe_unlocked\"}},\"requirements\":[[\"has_the_recipe\",\"has_material\"]],\"rewards\":{\"recipes\":[\"%s:%s\"]}}".formatted(
            baseItem, MOD_ID, recipeId, MOD_ID, recipeId);
        pack.addResource(PackType.SERVER_DATA,
            ResourceLocation.fromNamespaceAndPath(MOD_ID, "advancement/recipes/building_blocks/" + recipeId + ".json"), json);
    }

    // ---- Tag generation ----

    private static String variantTagName(VariantType variant) {
        return switch (variant) {
            case STAIRS -> "stairs";
            case SLAB -> "slabs";
            case WALL -> "walls";
            case TRAPDOOR -> "trapdoors";
            case DOOR -> "doors";
            case PRESSURE_PLATE -> "pressure_plates";
            case BUTTON -> "buttons";
        };
    }

    private static void generateTag(InMemoryResourcePack pack, String tagName,
                                     java.util.List<String> ids) {
        StringBuilder sb = new StringBuilder("{\"values\":[");
        boolean first = true;
        for (String id : ids) {
            if (!first) sb.append(",");
            sb.append("\"").append(id).append("\"");
            first = false;
        }
        sb.append("]}");
        pack.addResource(PackType.SERVER_DATA,
            ResourceLocation.fromNamespaceAndPath("minecraft", "tags/block/" + tagName + ".json"), sb.toString());
    }
}
