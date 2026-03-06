package com.justmissingblocks.forge;

import com.justmissingblocks.Compat;
import com.justmissingblocks.JustMissingBlocks;
import com.justmissingblocks.ModBlocks;
import com.justmissingblocks.ModBlocks.VariantType;
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

@Mod(JustMissingBlocks.MOD_ID)
public class JustMissingBlocksForge {

    public static final DeferredRegister<Block> BLOCKS =
        DeferredRegister.create(ForgeRegistries.BLOCKS, JustMissingBlocks.MOD_ID);
    public static final DeferredRegister<Item> ITEMS =
        DeferredRegister.create(ForgeRegistries.ITEMS, JustMissingBlocks.MOD_ID);

    public JustMissingBlocksForge() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        for (ModBlocks.BlockEntry entry : ModBlocks.getBlockEntries()) {
            Block baseBlock = BuiltInRegistries.BLOCK.get(
                Compat.resourceLocation("minecraft", entry.baseBlockId()));

            for (VariantType variant : entry.variants()) {
                String id = ModBlocks.variantBlockId(entry.baseBlockId(), variant);

                RegistryObject<Block> blockHolder = BLOCKS.register(id,
                    () -> ModBlocks.createVariantBlock(variant, baseBlock));
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
