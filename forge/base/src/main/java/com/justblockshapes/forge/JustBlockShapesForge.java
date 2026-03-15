package com.justblockshapes.forge;

import com.justblockshapes.Compat;
import com.justblockshapes.JustBlockShapes;
import com.justblockshapes.ModBlocks;
import com.justblockshapes.ModBlocks.VariantType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod(JustBlockShapes.MOD_ID)
public class JustBlockShapesForge {

    public static final DeferredRegister<Block> BLOCKS =
        DeferredRegister.create(ForgeRegistries.BLOCKS, JustBlockShapes.MOD_ID);
    public static final DeferredRegister<Item> ITEMS =
        DeferredRegister.create(ForgeRegistries.ITEMS, JustBlockShapes.MOD_ID);

    public JustBlockShapesForge() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        for (ModBlocks.BlockEntry entry : ModBlocks.getBlockEntries()) {
            Block baseBlock = Compat.tryGetBlock(
                Compat.resourceLocation("minecraft", entry.baseBlockId()));
            if (baseBlock == null) {
                ModBlocks.markSkipped(entry.baseBlockId());
                continue;
            }

            for (VariantType variant : entry.variants()) {
                String id = ModBlocks.variantBlockId(entry.baseBlockId(), variant);

                RegistryObject<Block> blockHolder = BLOCKS.register(id,
                    () -> ModBlocks.createVariantBlock(variant, baseBlock, id));
                ITEMS.register(id,
                    () -> new BlockItem(blockHolder.get(), new Item.Properties()));

                modEventBus.addListener((FMLCommonSetupEvent event) -> {
                    ModBlocks.register(id, blockHolder.get());
                });
            }
        }

        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        modEventBus.addListener(this::addCreative);
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
            for (Block block : ModBlocks.getRegisteredBlocks().values()) {
                event.accept(block);
            }
        }
    }
}
