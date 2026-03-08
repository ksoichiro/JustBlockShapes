package com.justblockshapes.mixin;

import com.justblockshapes.JustBlockShapes;
import com.justblockshapes.resource.InMemoryResourcePack;
import com.justblockshapes.resource.RuntimeResourceGenerator;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackResources;
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

@Mixin(PackRepository.class)
public class PackRepositoryMixin {

    @Unique
    private InMemoryResourcePack justblockshapes_resourcePack;

    @Inject(method = "discoverAvailable", at = @At("RETURN"), cancellable = true)
    private void justblockshapes_addResourcePack(CallbackInfoReturnable<Map<String, Pack>> cir) {
        String packId = JustBlockShapes.MOD_ID + "_resources";

        if (justblockshapes_resourcePack == null) {
            justblockshapes_resourcePack = new InMemoryResourcePack(packId);
            RuntimeResourceGenerator.generate(justblockshapes_resourcePack);
        }

        Pack pack = Pack.readMetaAndCreate(
            packId,
            Component.literal("JustBlockShapes Resources"),
            true,
            id -> justblockshapes_resourcePack,
            PackType.CLIENT_RESOURCES,
            Pack.Position.TOP,
            PackSource.BUILT_IN
        );

        if (pack != null) {
            Map<String, Pack> result = new HashMap<>(cir.getReturnValue());
            result.put(packId, pack);
            cir.setReturnValue(result);
        }
    }
}
