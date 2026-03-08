package com.justmissingblocks.resource;

import net.minecraft.SharedConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.resources.IoSupplier;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * In-memory resource pack for 1.20.1.
 * Uses packId() instead of location() (no PackLocationInfo in 1.20.1).
 */
public class InMemoryResourcePack implements PackResources {

    private final String id;
    private final Map<PackType, Map<ResourceLocation, byte[]>> resources = new HashMap<>();

    public InMemoryResourcePack(String id) {
        this.id = id;
        for (PackType type : PackType.values()) {
            resources.put(type, new HashMap<>());
        }
    }

    public void addResource(PackType type, ResourceLocation location, String json) {
        resources.get(type).put(location, json.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String packId() {
        return id;
    }

    @Nullable
    @Override
    public IoSupplier<InputStream> getRootResource(String... paths) {
        return null;
    }

    @Nullable
    @Override
    public IoSupplier<InputStream> getResource(PackType type, ResourceLocation location) {
        byte[] data = resources.get(type).get(location);
        if (data != null) {
            return () -> new ByteArrayInputStream(data);
        }
        return null;
    }

    @Override
    public void listResources(PackType type, String namespace, String path,
                              ResourceOutput output) {
        resources.get(type).forEach((location, data) -> {
            if (location.getNamespace().equals(namespace)
                && location.getPath().startsWith(path)) {
                output.accept(location, () -> new ByteArrayInputStream(data));
            }
        });
    }

    @Override
    public Set<String> getNamespaces(PackType type) {
        return resources.get(type).keySet().stream()
            .map(ResourceLocation::getNamespace)
            .collect(Collectors.toSet());
    }

    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    public <T> T getMetadataSection(MetadataSectionSerializer<T> serializer) throws IOException {
        if (serializer == PackMetadataSection.TYPE) {
            int packFormat = SharedConstants.getCurrentVersion().getPackVersion(PackType.CLIENT_RESOURCES);
            return (T) new PackMetadataSection(
                Component.literal("JustMissingBlocks resources"),
                packFormat
            );
        }
        return null;
    }

    @Override
    public void close() {
    }
}
