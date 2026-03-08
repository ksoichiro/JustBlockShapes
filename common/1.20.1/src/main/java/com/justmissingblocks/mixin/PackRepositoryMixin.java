package com.justmissingblocks.mixin;

import com.justmissingblocks.JustMissingBlocks;
import com.justmissingblocks.resource.InMemoryResourcePack;
import com.justmissingblocks.resource.RuntimeResourceGenerator;
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
    private InMemoryResourcePack justmissingblocks_resourcePack;

    @Inject(method = "discoverAvailable", at = @At("RETURN"), cancellable = true)
    private void justmissingblocks_addResourcePack(CallbackInfoReturnable<Map<String, Pack>> cir) {
        String packId = JustMissingBlocks.MOD_ID + "_resources";

        if (justmissingblocks_resourcePack == null) {
            justmissingblocks_resourcePack = new InMemoryResourcePack(packId);
            RuntimeResourceGenerator.generate(justmissingblocks_resourcePack);
        }

        Pack pack = Pack.readMetaAndCreate(
            packId,
            Component.literal("JustMissingBlocks Resources"),
            true,
            id -> justmissingblocks_resourcePack,
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
