package com.justmissingblocks.resource;

import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.IoSupplier;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Abstract in-memory resource pack for 1.21.11 (Identifier API).
 * Use {@link com.justmissingblocks.Compat#createInMemoryResourcePack(PackLocationInfo)} to create instances.
 */
public abstract class InMemoryResourcePack implements PackResources {

    private final PackLocationInfo locationInfo;
    private final Map<PackType, Map<Identifier, byte[]>> resources = new HashMap<>();

    protected InMemoryResourcePack(PackLocationInfo locationInfo) {
        this.locationInfo = locationInfo;
        for (PackType type : PackType.values()) {
            resources.put(type, new HashMap<>());
        }
    }

    public void addResource(PackType type, Identifier location, String json) {
        resources.get(type).put(location, json.getBytes(StandardCharsets.UTF_8));
    }

    @Nullable
    @Override
    public IoSupplier<InputStream> getRootResource(String... paths) {
        return null;
    }

    @Nullable
    @Override
    public IoSupplier<InputStream> getResource(PackType type, Identifier location) {
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
            .map(Identifier::getNamespace)
            .collect(Collectors.toSet());
    }

    @Override
    public PackLocationInfo location() {
        return locationInfo;
    }

    @Override
    public void close() {
    }
}
