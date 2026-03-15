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
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Mod(JustBlockShapes.MOD_ID)
public class JustBlockShapesForge {

    public static final DeferredRegister<Block> BLOCKS =
        DeferredRegister.create(ForgeRegistries.BLOCKS, JustBlockShapes.MOD_ID);
    public static final DeferredRegister<Item> ITEMS =
        DeferredRegister.create(ForgeRegistries.ITEMS, JustBlockShapes.MOD_ID);

    static final List<BlockRegistration> BLOCK_REGISTRATIONS = new ArrayList<>();

    record BlockRegistration(String id, RegistryObject<Block> holder) {}

    public JustBlockShapesForge(FMLJavaModLoadingContext context) {
        var modBusGroup = context.getModBusGroup();

        for (ModBlocks.BlockEntry entry : ModBlocks.getBlockEntries()) {
            Block baseBlock = Compat.tryGetBlock(
                Compat.resourceLocation("minecraft", entry.baseBlockId()));
            if (baseBlock == null) continue;

            for (VariantType variant : entry.variants()) {
                String id = ModBlocks.variantBlockId(entry.baseBlockId(), variant);

                RegistryObject<Block> blockHolder = BLOCKS.register(id,
                    () -> ModBlocks.createVariantBlock(variant, baseBlock, id));
                ITEMS.register(id,
                    () -> new BlockItem(blockHolder.get(), Compat.createItemProperties(JustBlockShapes.MOD_ID, id)));

                BLOCK_REGISTRATIONS.add(new BlockRegistration(id, blockHolder));
            }
        }

        BLOCKS.register(modBusGroup);
        ITEMS.register(modBusGroup);
    }

    // MOD bus events (lifecycle)
    @Mod.EventBusSubscriber(modid = JustBlockShapes.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModBusEvents {
        @SubscribeEvent
        public static void onCommonSetup(FMLCommonSetupEvent event) {
            for (BlockRegistration reg : BLOCK_REGISTRATIONS) {
                ModBlocks.register(reg.id(), reg.holder().get());
            }
        }
    }

    // FORGE bus events (game events, resource packs, creative tabs)
    @Mod.EventBusSubscriber(modid = JustBlockShapes.MOD_ID)
    public static class ForgeBusEvents {
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
                    "JustBlockShapes Compat", PackType.CLIENT_RESOURCES);
            }

            if (event.getPackType() == PackType.SERVER_DATA) {
                ResourcePackHelper.register(event, JustBlockShapes.MOD_ID + "_data",
                    "JustBlockShapes Data", PackType.SERVER_DATA);
            }
        }
    }

    // Helper class outside @EventBusSubscriber to avoid Forge 56+ scanning it as an event method
    private static class ResourcePackHelper {
        static void register(AddPackFindersEvent event, String packId,
                             String displayName, PackType packType) {
            PackLocationInfo locationInfo = new PackLocationInfo(
                packId,
                Component.literal(displayName),
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
