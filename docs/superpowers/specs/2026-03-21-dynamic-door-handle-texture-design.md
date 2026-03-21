# Dynamic Door Handle Texture Generation

## Problem

Door knobs currently use a fixed dark-colored texture (`door_handle.png`) for all door variants. This looks unnatural because the knob color doesn't match the door's base block material.

## Solution

Generate door handle textures at runtime by extracting the dominant color from each door's vanilla base block texture, darkening it, and injecting the result into the in-memory resource pack.

## Architecture

### New Class: `DoorHandleTextureGenerator`

Location: `common/shared-mc/src/main/java/com/justblockshapes/resource/DoorHandleTextureGenerator.java`

Responsibilities:
- Read vanilla block texture PNG from classpath
- Extract dominant color using quantized frequency analysis
- Darken the color (reduce HSB brightness by 50%)
- Generate a 2x2 single-color PNG as `byte[]` using `ImageIO.write()`
- Return `null` on failure (caller uses fallback)
- Log a warning on failure for debugging

#### Texture Path Resolution

`getVanillaTexture()` returns a ResourceLocation-style string like `minecraft:block/quartz_block_side`. The generator converts this to a classpath path: `/assets/minecraft/textures/block/quartz_block_side.png`.

### Color Extraction Algorithm

1. Read PNG via `ImageIO.read()` from classpath `InputStream`
2. Iterate all pixels; skip pixels with alpha < 128
3. Quantize each pixel's RGB channels to 16 levels (`value / 16`)
4. Count frequency of each quantized color
5. Find the most frequent quantized color group
6. Compute the average RGB of all original pixels belonging to that group
7. Convert to HSB, multiply brightness by 0.5, convert back to RGB

### Modified: `InMemoryResourcePack`

Add `addBinaryResource(PackType, ResourceLocation, byte[])` overload for raw byte data (PNG etc.). The internal storage is already `byte[]`-based, so this simply bypasses the `String.getBytes()` conversion. Applies to all three copies:
- `common/shared-mc/` (abstract base)
- `common/1.20.1/` (standalone concrete class)
- `common/1.21.11/` (standalone concrete class)

### Modified: `RuntimeResourceGenerator`

Changes apply to all three copies (shared-mc, 1.20.1, 1.21.11):

- For each door block, call `DoorHandleTextureGenerator.generate(texture)` where `texture` is the return value of `getVanillaTexture()`
- On success: register generated PNG as `textures/block/door_handle_{blockId}.png`, set handle texture to `justblockshapes:block/door_handle_{blockId}`
- On failure: use existing fixed texture `justblockshapes:block/door_handle`
- Update `doorTextures(String texture)` to `doorTextures(String texture, String handleTexture)` — returns `"bottom":"{texture}","top":"{texture}","handle":"{handleTexture}"`
- Update door item model generation to include `"handle"` key in textures JSON

Note: Top-half door templates (`template_door_top_*`) do not reference `#handle`, so the extra texture key is harmless but unused for those models.

### Fallback Behavior

If texture generation fails (classpath read error, no opaque pixels, ImageIO failure):
- Log a warning with the block name
- Use existing fixed texture `justblockshapes:block/door_handle` (identical to current behavior)

## Files Changed

| File | Change |
|------|--------|
| `common/shared-mc/.../resource/DoorHandleTextureGenerator.java` | New: texture generation logic |
| `common/shared-mc/.../resource/InMemoryResourcePack.java` | Add `addBinaryResource(byte[])` overload |
| `common/shared-mc/.../resource/RuntimeResourceGenerator.java` | Generate per-door handle textures, update model JSON |
| `common/1.20.1/.../resource/InMemoryResourcePack.java` | Add `addBinaryResource(byte[])` overload |
| `common/1.20.1/.../resource/RuntimeResourceGenerator.java` | Same changes as shared-mc (1.20.1-specific API) |
| `common/1.21.11/.../resource/InMemoryResourcePack.java` | Add `addBinaryResource(byte[])` overload |
| `common/1.21.11/.../resource/RuntimeResourceGenerator.java` | Same changes as shared-mc (1.21.11-specific API) |

## No Changes Required

- Template model JSONs (child model textures override parent's `#handle`)
- Platform-specific code (Fabric/NeoForge/Forge)
- Fixed `door_handle.png` (kept as fallback)
