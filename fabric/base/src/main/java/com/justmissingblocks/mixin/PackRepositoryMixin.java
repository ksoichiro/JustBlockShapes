package com.justmissingblocks.mixin;

import com.justmissingblocks.JustMissingBlocks;
import com.justmissingblocks.fabric.resource.InMemoryResourcePack;
import com.justmissingblocks.fabric.resource.RuntimeResourceGenerator;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.PackRepository;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Mixin(PackRepository.class)
public class PackRepositoryMixin {

    @Unique
    private InMemoryResourcePack justmissingblocks_compatPack;

    @Inject(method = "discoverAvailable", at = @At("RETURN"), cancellable = true)
    private void justmissingblocks_addCompatPack(CallbackInfoReturnable<Map<String, Pack>> cir) {
        String packId = JustMissingBlocks.MOD_ID + "_compat";

        if (justmissingblocks_compatPack == null) {
            PackLocationInfo locationInfo = new PackLocationInfo(
                packId,
                Component.literal("JustMissingBlocks Compat"),
                PackSource.BUILT_IN,
                Optional.empty()
            );
            justmissingblocks_compatPack = new InMemoryResourcePack(locationInfo);
            RuntimeResourceGenerator.generate(justmissingblocks_compatPack);
        }

        PackLocationInfo locationInfo = justmissingblocks_compatPack.location();
        Pack pack = Pack.readMetaAndCreate(
            locationInfo,
            new Pack.ResourcesSupplier() {
                @Override
                public PackResources openPrimary(PackLocationInfo info) {
                    return justmissingblocks_compatPack;
                }

                @Override
                public PackResources openFull(PackLocationInfo info, Pack.Metadata metadata) {
                    return justmissingblocks_compatPack;
                }
            },
            PackType.CLIENT_RESOURCES,
            new PackSelectionConfig(true, Pack.Position.TOP, false)
        );

        if (pack != null) {
            // The return value is ImmutableMap, so we need a mutable copy
            Map<String, Pack> result = new HashMap<>(cir.getReturnValue());
            result.put(packId, pack);
            cir.setReturnValue(result);
        }
    }
}
