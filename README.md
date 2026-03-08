# Just Block Shapes

A Minecraft mod that adds missing wall, stairs, and slab variants for vanilla building blocks.

- **1.21.1, 1.21.3–1.21.11**: Fabric + NeoForge

## Added Blocks

### Walls only (stairs + slab already exist in vanilla)
Quartz, Smooth Quartz, Polished Andesite, Polished Granite, Polished Diorite, Dark Prismarine, Prismarine Bricks, Purpur

### Stairs + Wall (slab already exists)
Smooth Stone, Cut Sandstone, Cut Red Sandstone

### Stairs + Slab + Wall
Calcite, Cracked Stone Bricks, Terracotta (plain), 16 Dyed Terracotta, 16 Concrete

**Total: ~119 new blocks**

## Installation

1. Install [Fabric Loader](https://fabricmc.net/) or [NeoForge](https://neoforged.net/)
2. Install [Fabric API](https://modrinth.com/mod/fabric-api) (Fabric only)
3. Place the mod JAR in your `mods/` folder

## Building from Source

```bash
# Build
./gradlew build

# Run datagen (regenerate resources)
./gradlew fabric:runDatagen -Ptarget_mc_version=1.21.1

# Run client for testing
./gradlew fabric:runClient -Ptarget_mc_version=1.21.1
./gradlew neoforge:runClient -Ptarget_mc_version=1.21.1
```

## License

LGPL-3.0-only
