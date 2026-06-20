package com.justblockshapes.neoforge;

import com.justblockshapes.Compat;
import com.justblockshapes.JustBlockShapes;
import com.justblockshapes.ModBlocks;
import com.justblockshapes.ModBlocks.VariantType;
import com.justblockshapes.resource.DynamicCompatPack;
import com.justblockshapes.resource.InMemoryResourcePack;
import com.justblockshapes.resource.RuntimeResourceGenerator;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Optional;

@Mod(JustBlockShapes.MOD_ID)
public class JustBlockShapesNeoForge {

    public static final DeferredRegister.Blocks BLOCKS =
        DeferredRegister.createBlocks(JustBlockShapes.MOD_ID);
    public static final DeferredRegister.Items ITEMS =
        DeferredRegister.createItems(JustBlockShapes.MOD_ID);

    public JustBlockShapesNeoForge(IEventBus modEventBus) {
        for (ModBlocks.BlockEntry entry : ModBlocks.getBlockEntries()) {
            Block baseBlock = Compat.tryGetBlock(
                Compat.resourceLocation("minecraft", entry.baseBlockId()));
            if (baseBlock == null) {
                ModBlocks.markSkipped(entry.baseBlockId());
                continue;
            }

            for (VariantType variant : entry.variants()) {
                String id = ModBlocks.variantBlockId(entry.baseBlockId(), variant);

                var blockHolder = BLOCKS.register(id,
                    () -> ModBlocks.createVariantBlock(variant, baseBlock, id));
                ITEMS.register(id,
                    () -> new BlockItem(blockHolder.get(), Compat.createItemProperties(JustBlockShapes.MOD_ID, id)));

                modEventBus.addListener((FMLCommonSetupEvent event) -> {
                    ModBlocks.register(id, blockHolder.get());
                });
            }
        }

        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        modEventBus.register(ModBusEvents.class);
    }

    public static class ModBusEvents {
        @SubscribeEvent
        public static void addCreative(BuildCreativeModeTabContentsEvent event) {
            if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
                for (Block block : ModBlocks.getRegisteredBlocks().values()) {
                    event.accept(block);
                }
            }
        }

        @SubscribeEvent
        public static void addPackFinders(AddPackFindersEvent event) {
            if (event.getPackType() == PackType.CLIENT_RESOURCES) {
                ResourcePackHelper.register(event, JustBlockShapes.MOD_ID + "_compat",
                    "JustBlockShapes Compat", PackType.CLIENT_RESOURCES, true);
            }

            if (event.getPackType() == PackType.SERVER_DATA) {
                ResourcePackHelper.register(event, JustBlockShapes.MOD_ID + "_data",
                    "JustBlockShapes Data", PackType.SERVER_DATA, false);
            }
        }
    }

    private static class ResourcePackHelper {
        static void register(AddPackFindersEvent event, String packId,
                             String displayName, PackType packType, boolean useDynamic) {
            PackLocationInfo locationInfo = new PackLocationInfo(
                packId,
                Component.literal(displayName),
                PackSource.BUILT_IN,
                Optional.empty()
            );
            InMemoryResourcePack resourcePack;
            if (useDynamic) {
                resourcePack = new DynamicCompatPack(locationInfo);
            } else {
                resourcePack = Compat.createInMemoryResourcePack(locationInfo);
            }
            RuntimeResourceGenerator.generate(resourcePack);

            event.addRepositorySource(consumer -> {
                Pack pack = Pack.readMetaAndCreate(
                    locationInfo,
                    new Pack.ResourcesSupplier() {
                        @Override
                        public PackResources openPrimary(PackLocationInfo info) {
                            return resourcePack;
                        }

                        @Override
                        public PackResources openFull(PackLocationInfo info, Pack.Metadata metadata) {
                            return resourcePack;
                        }
                    },
                    packType,
                    new PackSelectionConfig(true, Pack.Position.TOP, false)
                );
                if (pack != null) {
                    consumer.accept(pack);
                }
            });
        }
    }
}
