package com.justblockshapes.resource;

import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.IoSupplier;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Map;

/**
 * Dynamic compat pack that generates door handle textures on demand.
 * In NeoForge 26.1, vanilla textures may not be on the classpath at pack
 * construction time, so textures are generated lazily when first requested.
 */
public class DynamicCompatPack extends InMemoryResourcePack {

    private final Map<Identifier, byte[]> dynamicTextureCache = new java.util.HashMap<>();

    public DynamicCompatPack(PackLocationInfo locationInfo) {
        super(locationInfo);
    }

    @Override
    @Nullable
    public IoSupplier<InputStream> getResource(PackType type, Identifier location) {
        IoSupplier<InputStream> staticResource = super.getResource(type, location);
        if (staticResource != null) {
            return staticResource;
        }

        if (type == PackType.CLIENT_RESOURCES && location.getNamespace().equals(com.justblockshapes.JustBlockShapes.MOD_ID)) {
            String path = location.getPath();
            if (path.startsWith("textures/block/door_handle_") && path.endsWith(".png")) {
                String fileName = path.substring(path.lastIndexOf('/') + 1, path.length() - 4);
                byte[] textureData = dynamicTextureCache.get(location);
                if (textureData == null) {
                    textureData = generateDoorHandleTexture(fileName);
                    if (textureData != null) {
                        dynamicTextureCache.put(location, textureData);
                    }
                }
                final byte[] finalTextureData = textureData;
                if (finalTextureData != null) {
                    return () -> new ByteArrayInputStream(finalTextureData);
                }
            }
        }

        return null;
    }

    private byte[] generateDoorHandleTexture(String handleId) {
        if (!handleId.startsWith("door_handle_")) {
            return null;
        }
        String baseBlockId = handleId.substring("door_handle_".length());
        String texture = RuntimeResourceGenerator.getVanillaTexture(baseBlockId);
        return DoorHandleTextureGenerator.generate(texture);
    }
}
