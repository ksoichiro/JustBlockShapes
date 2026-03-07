# Just Missing Blocks

## About This Project

A Minecraft mod that adds missing wall, stairs, and slab variants for vanilla building blocks. Uses Architectury Loom for multi-loader build tooling but does NOT depend on Architectury API at runtime.

## Project Structure

- `common/shared/` - Version-agnostic shared code (block definitions, mod ID)
- `common/{version}/` - Version-specific common code and generated resources
- `common/{version}/src/main/generated/` - Datagen output (block states, models, recipes, tags, loot tables)
- `fabric/{version}/` - Fabric platform implementation
- `neoforge/{version}/` - NeoForge platform implementation (1.21+)
- `forge/{version}/` - Forge platform implementation (1.20.x)
- `fabric/base/`, `neoforge/base/`, `forge/base/` - Base platform code (included as srcDirs)
- `fabric/{version}/src/main/java/.../datagen/` - Data generation providers (Fabric only)
- `props/` - Version-specific properties files

Supported versions:
- 1.21.1, 1.21.3–1.21.11: Fabric, NeoForge
- 1.20.1: Fabric, Forge (not yet implemented)

## Build Commands

```bash
# Full build for default version (1.21.1)
./gradlew build

# Build for specific version
./gradlew build -Ptarget_mc_version=1.21.1

# Run datagen (generates resources; available for 1.21.1 and 1.21.3)
./gradlew fabric:runDatagen -Ptarget_mc_version=1.21.1

# Run client for testing
./gradlew fabric:runClient -Ptarget_mc_version=1.21.1
./gradlew neoforge:runClient -Ptarget_mc_version=1.21.1

# Clean build
./gradlew clean build -Ptarget_mc_version=1.21.1

# Build all versions
./gradlew buildAll

# Release (clean, build all, collect JARs)
./gradlew release
```

## Key Files

- `gradle.properties` - Mod version, target MC version
- `props/{version}.properties` - Version-specific dependencies
- `common/shared/.../JustMissingBlocks.java` - MOD_ID holder
- `common/shared/.../ModBlocks.java` - Block definitions (data-driven list of base blocks and variant types)
- `fabric/base/.../JustMissingBlocksFabric.java` - Fabric registration
- `neoforge/base/.../JustMissingBlocksNeoForge.java` - NeoForge registration

## Architecture

- **No Architectury API runtime dependency**: Common module holds block definitions (data only). Each platform registers using native APIs.
- **Data-driven**: Adding/removing blocks means editing `ModBlocks.java`. All datagen providers consume this list.
- **`Properties.ofFullCopy()`**: Copies all properties from vanilla base block automatically.
- **StairBlock access widener**: `StairBlock` constructor is protected; access widener in `justmissingblocks.accesswidener` makes it accessible.
- **Generated resources in common**: Datagen outputs to `common/{version}/src/main/generated/` so both platforms share the same generated resources. Versions 1.21.4+ share generated resources from 1.21.3 (identical JSON format within 1.21.x).

## Development Notes

- Platform-specific code goes in `fabric/base/`, `neoforge/base/`
- After changing `ModBlocks.java`, re-run datagen to regenerate resources
- Update `en_us.json` manually when adding new blocks (lang file is hand-maintained)

### Version-specific API differences

**1.21.3+ vs 1.21.1:**
- `BuiltInRegistries.BLOCK.get()` returns `Optional<Reference<Block>>` (1.21.1 returns `Block` directly)
- `BlockBehaviour.Properties` requires block ID via `setId(ResourceKey)` before construction
- `Item.Properties` requires item ID via `setId(ResourceKey)` before construction
- `FabricRecipeProvider` uses `createRecipeProvider()` pattern instead of `buildRecipes()`
- `ShapedRecipeBuilder.shaped()` takes `HolderGetter<Item>` as first param
- Recipe `save()` uses `ResourceKey<Recipe<?>>` instead of `ResourceLocation`

**1.21.4+:**
- Model datagen API was reorganized (`net.minecraft.data.models` package removed)
- Datagen code exists only for 1.21.1 and 1.21.3; later versions share generated resources

**1.21.10+:**
- `pack.mcmeta` uses `min_format`/`max_format` instead of `pack_format`

**1.21.11:**
- `ResourceLocation` renamed to `Identifier`

**1.21.1 vs 1.20.1:**
- ResourceLocation: 1.21 uses `fromNamespaceAndPath()`, 1.20.1 uses constructor
- StairBlock: 1.21 constructor is protected (needs access widener)

All version differences are abstracted via `Compat.java` in each `common/{version}/` module.

### Forge 1.20.1 with Architectury Loom

When using Architectury Loom 1.11+ with Forge 1.20.1, `loom.platform = forge` must be set in `forge/{version}/gradle.properties`.

## Scripts

- `scripts/release.sh` - Upload a single JAR to Modrinth (requires `.envrc` with `MODRINTH_TOKEN`)
- `scripts/release-all.sh` - Upload all JARs in `build/release/` to Modrinth

## Resources Location

- Assets: `common/{version}/src/main/resources/assets/justmissingblocks/`
- Generated assets: `common/{version}/src/main/generated/assets/justmissingblocks/`
- Generated data: `common/{version}/src/main/generated/data/`
- Mixin config: `common/{version}/src/main/resources/justmissingblocks.mixins.json`
- Access widener: `common/{version}/src/main/resources/justmissingblocks.accesswidener`
