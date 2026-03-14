package com.justblockshapes.forge;

import com.justblockshapes.Compat;
import com.justblockshapes.JustBlockShapes;
import com.justblockshapes.ModBlocks;
import com.justblockshapes.ModBlocks.VariantType;
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
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Optional;

@Mod(JustBlockShapes.MOD_ID)
public class JustBlockShapesForge {

    public static final DeferredRegister<Block> BLOCKS =
        DeferredRegister.create(ForgeRegistries.BLOCKS, JustBlockShapes.MOD_ID);
    public static final DeferredRegister<Item> ITEMS =
        DeferredRegister.create(ForgeRegistries.ITEMS, JustBlockShapes.MOD_ID);

    public JustBlockShapesForge(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();

        for (ModBlocks.BlockEntry entry : ModBlocks.getBlockEntries()) {
            Block baseBlock = Compat.getBlock(
                Compat.resourceLocation("minecraft", entry.baseBlockId()));

            for (VariantType variant : entry.variants()) {
                String id = ModBlocks.variantBlockId(entry.baseBlockId(), variant);

                RegistryObject<Block> blockHolder = BLOCKS.register(id,
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
        modEventBus.addListener(this::addCreative);
        modEventBus.addListener(this::addPackFinders);
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
            for (Block block : ModBlocks.getRegisteredBlocks().values()) {
                event.accept(block);
            }
        }
    }

    private void addPackFinders(AddPackFindersEvent event) {
        if (event.getPackType() == PackType.CLIENT_RESOURCES) {
            String packId = JustBlockShapes.MOD_ID + "_compat";
            PackLocationInfo locationInfo = new PackLocationInfo(
                packId,
                Component.literal("JustBlockShapes Compat"),
                PackSource.BUILT_IN,
                Optional.empty()
            );
            InMemoryResourcePack resourcePack = Compat.createInMemoryResourcePack(locationInfo);
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
                    PackType.CLIENT_RESOURCES,
                    new PackSelectionConfig(true, Pack.Position.TOP, false)
                );
                if (pack != null) {
                    consumer.accept(pack);
                }
            });
        }

        if (event.getPackType() == PackType.SERVER_DATA) {
            String packId = JustBlockShapes.MOD_ID + "_data";
            PackLocationInfo locationInfo = new PackLocationInfo(
                packId,
                Component.literal("JustBlockShapes Data"),
                PackSource.BUILT_IN,
                Optional.empty()
            );
            InMemoryResourcePack resourcePack = Compat.createInMemoryResourcePack(locationInfo);
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
                    PackType.SERVER_DATA,
                    new PackSelectionConfig(true, Pack.Position.TOP, false)
                );
                if (pack != null) {
                    consumer.accept(pack);
                }
            });
        }
    }
}
