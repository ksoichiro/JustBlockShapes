# Dynamic Door Handle Texture Generation - Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Generate door handle textures at runtime by extracting the dominant color from each door's vanilla base block texture and darkening it.

**Architecture:** `DoorHandleTextureGenerator` reads vanilla PNGs from classpath, extracts dominant color via quantized frequency analysis, darkens it, and produces 2x2 PNG bytes. `RuntimeResourceGenerator` calls it for each door block and injects the PNG into `InMemoryResourcePack`. Fallback to fixed texture on failure.

**Tech Stack:** Java AWT (`ImageIO`, `BufferedImage`, `Color`), Minecraft resource pack system

**Spec:** `docs/superpowers/specs/2026-03-21-dynamic-door-handle-texture-design.md`

---

### File Structure

| File | Action | Responsibility |
|------|--------|----------------|
| `common/shared-mc/.../resource/DoorHandleTextureGenerator.java` | Create | Read vanilla texture, extract dominant color, generate darkened 2x2 PNG |
| `common/shared-mc/.../resource/InMemoryResourcePack.java` | Modify | Add `addBinaryResource(PackType, ResourceLocation, byte[])` overload |
| `common/shared-mc/.../resource/RuntimeResourceGenerator.java` | Modify | Call texture generator for doors, update `doorTextures()` and item model |
| `common/1.20.1/.../resource/InMemoryResourcePack.java` | Modify | Add `addBinaryResource(PackType, ResourceLocation, byte[])` overload |
| `common/1.20.1/.../resource/RuntimeResourceGenerator.java` | Modify | Same door changes (uses `new ResourceLocation()` instead of `fromNamespaceAndPath()`) |
| `common/1.21.11/.../resource/InMemoryResourcePack.java` | Modify | Add `addBinaryResource(PackType, Identifier, byte[])` overload |
| `common/1.21.11/.../resource/RuntimeResourceGenerator.java` | Modify | Same door changes (uses `Identifier` instead of `ResourceLocation`) |

---

### Task 1: Add `addBinaryResource()` to `InMemoryResourcePack` (all 3 versions)

**Files:**
- Modify: `common/shared-mc/src/main/java/com/justblockshapes/resource/InMemoryResourcePack.java:35-37`
- Modify: `common/1.20.1/src/main/java/com/justblockshapes/resource/InMemoryResourcePack.java:38-40`
- Modify: `common/1.21.11/src/main/java/com/justblockshapes/resource/InMemoryResourcePack.java:34-36`

- [ ] **Step 1: Add overload to shared-mc `InMemoryResourcePack`**

Add after the existing `addResource(PackType, ResourceLocation, String)` method:

```java
public void addBinaryResource(PackType type, ResourceLocation location, byte[] data) {
    resources.get(type).put(location, data);
}
```

- [ ] **Step 2: Add overload to 1.20.1 `InMemoryResourcePack`**

Same method, same signature (1.20.1 also uses `ResourceLocation`):

```java
public void addBinaryResource(PackType type, ResourceLocation location, byte[] data) {
    resources.get(type).put(location, data);
}
```

- [ ] **Step 3: Add overload to 1.21.11 `InMemoryResourcePack`**

Uses `Identifier` instead of `ResourceLocation`:

```java
public void addBinaryResource(PackType type, Identifier location, byte[] data) {
    resources.get(type).put(location, data);
}
```

- [ ] **Step 4: Build to verify compilation**

Run: `cd /Users/ksoichiro/src/github.com/ksoichiro/JustBlockShapes && ./gradlew build -Ptarget_mc_version=1.21.1`
Expected: BUILD SUCCESSFUL

- [ ] **Step 5: Commit**

```
feat: add addBinaryResource() to InMemoryResourcePack
```

---

### Task 2: Create `DoorHandleTextureGenerator`

**Files:**
- Create: `common/shared-mc/src/main/java/com/justblockshapes/resource/DoorHandleTextureGenerator.java`

- [ ] **Step 1: Create `DoorHandleTextureGenerator.java`**

```java
package com.justblockshapes.resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;

/**
 * Generates door handle textures by extracting the dominant color
 * from a vanilla block texture and darkening it.
 * Relies on java.awt which is available in client environments.
 * Returns null on failure so callers can fall back to the fixed texture.
 */
public class DoorHandleTextureGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger("JustBlockShapes");
    private static final float BRIGHTNESS_FACTOR = 0.5f;

    /**
     * Generates a 2x2 door handle PNG from the given vanilla texture.
     *
     * @param texture ResourceLocation-style texture path (e.g. "minecraft:block/stone")
     * @return PNG bytes, or null if generation fails
     */
    public static byte[] generate(String texture) {
        try {
            String classpathPath = toClasspathPath(texture);
            BufferedImage image = readTexture(classpathPath);
            if (image == null) {
                LOGGER.warn("Failed to read vanilla texture for door handle: {}", texture);
                return null;
            }

            int dominantColor = extractDominantColor(image);
            if (dominantColor == -1) {
                LOGGER.warn("No opaque pixels found in texture for door handle: {}", texture);
                return null;
            }

            int darkened = darken(dominantColor);
            return generatePng(darkened);
        } catch (Exception e) {
            LOGGER.warn("Failed to generate door handle texture for {}: {}", texture, e.getMessage());
            return null;
        }
    }

    /**
     * Converts "minecraft:block/stone" to "/assets/minecraft/textures/block/stone.png".
     */
    static String toClasspathPath(String texture) {
        int colonIndex = texture.indexOf(':');
        String namespace = texture.substring(0, colonIndex);
        String path = texture.substring(colonIndex + 1);
        return "/assets/" + namespace + "/textures/" + path + ".png";
    }

    private static BufferedImage readTexture(String classpathPath) {
        try (InputStream is = DoorHandleTextureGenerator.class.getResourceAsStream(classpathPath)) {
            if (is == null) return null;
            return ImageIO.read(is);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Extracts the dominant color by quantizing RGB to 16 levels and finding
     * the most frequent color group, then averaging original pixels in that group.
     */
    static int extractDominantColor(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();

        // Count quantized color frequencies
        // key: quantized color, value: [count, totalR, totalG, totalB]
        Map<Integer, int[]> colorGroups = new HashMap<>();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int argb = image.getRGB(x, y);
                int alpha = (argb >> 24) & 0xFF;
                if (alpha < 128) continue;

                int r = (argb >> 16) & 0xFF;
                int g = (argb >> 8) & 0xFF;
                int b = argb & 0xFF;

                int qr = r / 16;
                int qg = g / 16;
                int qb = b / 16;
                int quantized = (qr << 16) | (qg << 8) | qb;

                int[] group = colorGroups.computeIfAbsent(quantized, k -> new int[4]);
                group[0]++;
                group[1] += r;
                group[2] += g;
                group[3] += b;
            }
        }

        if (colorGroups.isEmpty()) return -1;

        // Find most frequent group
        int[] best = null;
        for (int[] group : colorGroups.values()) {
            if (best == null || group[0] > best[0]) {
                best = group;
            }
        }

        int avgR = best[1] / best[0];
        int avgG = best[2] / best[0];
        int avgB = best[3] / best[0];
        return (avgR << 16) | (avgG << 8) | avgB;
    }

    /**
     * Darkens a color by reducing its HSB brightness.
     */
    static int darken(int rgb) {
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = rgb & 0xFF;

        float[] hsb = Color.RGBtoHSB(r, g, b, null);
        hsb[2] *= BRIGHTNESS_FACTOR;
        // HSBtoRGB returns 0xFFRRGGBB; strip alpha here, generatePng() adds it back
        return Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]) & 0x00FFFFFF;
    }

    /**
     * Creates a 2x2 single-color PNG.
     */
    private static byte[] generatePng(int rgb) throws Exception {
        BufferedImage img = new BufferedImage(2, 2, BufferedImage.TYPE_INT_ARGB);
        int argb = 0xFF000000 | rgb;
        for (int y = 0; y < 2; y++) {
            for (int x = 0; x < 2; x++) {
                img.setRGB(x, y, argb);
            }
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(img, "png", baos);
        return baos.toByteArray();
    }
}
```

- [ ] **Step 2: Build to verify compilation**

Run: `cd /Users/ksoichiro/src/github.com/ksoichiro/JustBlockShapes && ./gradlew build -Ptarget_mc_version=1.21.1`
Expected: BUILD SUCCESSFUL

- [ ] **Step 3: Commit**

```
feat: add DoorHandleTextureGenerator for runtime handle color extraction
```

---

### Task 3: Update `RuntimeResourceGenerator` (shared-mc) to generate per-door handle textures

**Files:**
- Modify: `common/shared-mc/src/main/java/com/justblockshapes/resource/RuntimeResourceGenerator.java`

- [ ] **Step 1: Add constant and update `doorTextures()` signature**

Add constant:
```java
private static final String DEFAULT_HANDLE_TEXTURE = MOD_ID + ":block/door_handle";
```

Change `doorTextures` from:
```java
private static String doorTextures(String texture) {
    return "\"bottom\":\"%s\",\"top\":\"%s\"".formatted(texture, texture);
}
```
To:
```java
private static String doorTextures(String texture, String handleTexture) {
    String handle = handleTexture != null ? handleTexture : DEFAULT_HANDLE_TEXTURE;
    return "\"bottom\":\"%s\",\"top\":\"%s\",\"handle\":\"%s\"".formatted(texture, texture, handle);
}
```

- [ ] **Step 2: Update `generate()` loop to compute handle texture for doors**

In the loop body, add handle texture generation before `generateBlockModels`:
```java
String handleTexture = null;
if (variant == VariantType.DOOR) {
    byte[] handlePng = DoorHandleTextureGenerator.generate(texture);
    if (handlePng != null) {
        String handleId = "door_handle_" + baseBlockId;
        pack.addBinaryResource(PackType.CLIENT_RESOURCES,
            ResourceLocation.fromNamespaceAndPath(MOD_ID, "textures/block/" + handleId + ".png"), handlePng);
        handleTexture = MOD_ID + ":block/" + handleId;
    }
}
```

Update calls to pass `handleTexture`:
```java
generateBlockModels(pack, blockId, variant, texture, handleTexture);
generateItemModel(pack, blockId, variant, texture, handleTexture);
```

- [ ] **Step 3: Update `generateBlockModels()` signature and DOOR case**

Add `String handleTexture` parameter:
```java
private static void generateBlockModels(InMemoryResourcePack pack, String blockId,
                                         VariantType variant, String texture,
                                         String handleTexture)
```

Update DOOR case to use `doorTextures(texture, handleTexture)`:
```java
case DOOR -> {
    addBlockModel(pack, blockId + "_bottom_left", MOD_ID + ":block/template_door_bottom_left", doorTextures(texture, handleTexture));
    addBlockModel(pack, blockId + "_bottom_left_open", MOD_ID + ":block/template_door_bottom_left_open", doorTextures(texture, handleTexture));
    addBlockModel(pack, blockId + "_bottom_right", MOD_ID + ":block/template_door_bottom_right", doorTextures(texture, handleTexture));
    addBlockModel(pack, blockId + "_bottom_right_open", MOD_ID + ":block/template_door_bottom_right_open", doorTextures(texture, handleTexture));
    addBlockModel(pack, blockId + "_top_left", MOD_ID + ":block/template_door_top_left", doorTextures(texture, handleTexture));
    addBlockModel(pack, blockId + "_top_left_open", MOD_ID + ":block/template_door_top_left_open", doorTextures(texture, handleTexture));
    addBlockModel(pack, blockId + "_top_right", MOD_ID + ":block/template_door_top_right", doorTextures(texture, handleTexture));
    addBlockModel(pack, blockId + "_top_right_open", MOD_ID + ":block/template_door_top_right_open", doorTextures(texture, handleTexture));
}
```

- [ ] **Step 4: Update `generateItemModel()` signature and DOOR case**

Add `String handleTexture` parameter:
```java
private static void generateItemModel(InMemoryResourcePack pack, String blockId,
                                       VariantType variant, String texture,
                                       String handleTexture)
```

Change DOOR case from:
```java
case DOOR ->
    "{\"parent\":\"%s:item/template_door\",\"textures\":{\"texture\":\"%s\"}}".formatted(MOD_ID, texture);
```
To:
```java
case DOOR -> {
    String handle = handleTexture != null ? handleTexture : DEFAULT_HANDLE_TEXTURE;
    yield "{\"parent\":\"%s:item/template_door\",\"textures\":{\"texture\":\"%s\",\"handle\":\"%s\"}}".formatted(MOD_ID, texture, handle);
}
```

- [ ] **Step 5: Build to verify compilation**

Run: `cd /Users/ksoichiro/src/github.com/ksoichiro/JustBlockShapes && ./gradlew build -Ptarget_mc_version=1.21.1`
Expected: BUILD SUCCESSFUL

- [ ] **Step 6: Commit**

```
feat: generate per-door handle textures at runtime (shared-mc)
```

---

### Task 4: Update `RuntimeResourceGenerator` (1.20.1)

**Files:**
- Modify: `common/1.20.1/src/main/java/com/justblockshapes/resource/RuntimeResourceGenerator.java`

- [ ] **Step 1: Apply same changes as Task 3**

Same logic as Task 3 but using `new ResourceLocation(MOD_ID, ...)` instead of `ResourceLocation.fromNamespaceAndPath(MOD_ID, ...)`.

Key difference in handle texture generation:
```java
pack.addBinaryResource(PackType.CLIENT_RESOURCES,
    new ResourceLocation(MOD_ID, "textures/block/" + handleId + ".png"), handlePng);
```

All other logic (constant, `doorTextures()`, method signatures, DOOR cases) is identical.

- [ ] **Step 2: Build to verify compilation**

Run: `cd /Users/ksoichiro/src/github.com/ksoichiro/JustBlockShapes && ./gradlew build -Ptarget_mc_version=1.20.1`
Expected: BUILD SUCCESSFUL (note: 1.20.1 support may not be fully implemented yet)

- [ ] **Step 3: Commit**

```
feat: generate per-door handle textures at runtime (1.20.1)
```

---

### Task 5: Update `RuntimeResourceGenerator` (1.21.11)

**Files:**
- Modify: `common/1.21.11/src/main/java/com/justblockshapes/resource/RuntimeResourceGenerator.java`

- [ ] **Step 1: Apply same changes as Task 3**

Same logic as Task 3 but using `Identifier.fromNamespaceAndPath(MOD_ID, ...)` instead of `ResourceLocation.fromNamespaceAndPath(MOD_ID, ...)`.

Key difference in handle texture generation:
```java
pack.addBinaryResource(PackType.CLIENT_RESOURCES,
    Identifier.fromNamespaceAndPath(MOD_ID, "textures/block/" + handleId + ".png"), handlePng);
```

All other logic (constant, `doorTextures()`, method signatures, DOOR cases) is identical.

- [ ] **Step 2: Build to verify compilation**

Run: `cd /Users/ksoichiro/src/github.com/ksoichiro/JustBlockShapes && ./gradlew build -Ptarget_mc_version=1.21.11`
Expected: BUILD SUCCESSFUL

- [ ] **Step 3: Commit**

```
feat: generate per-door handle textures at runtime (1.21.11)
```

---

### Task 6: Build all versions and manual verification

- [ ] **Step 1: Build all versions**

Run: `cd /Users/ksoichiro/src/github.com/ksoichiro/JustBlockShapes && ./gradlew buildAll`
Expected: BUILD SUCCESSFUL

- [ ] **Step 2: Manual in-game verification**

Run: `cd /Users/ksoichiro/src/github.com/ksoichiro/JustBlockShapes && ./gradlew fabric:runClient -Ptarget_mc_version=1.21.1`

Verify:
- Place a stone door — handle should be a dark gray matching stone
- Place a polished blackstone door — handle should be very dark, matching blackstone
- Check item models in inventory — handles should show correct colors
- No missing texture warnings in game log
