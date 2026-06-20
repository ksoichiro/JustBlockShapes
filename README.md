# Just Block Shapes

Completes the vanilla building palette with missing block shape variants.

A Minecraft mod that adds missing wall, stairs, slab, fence, fence gate, trapdoor, door, pressure plate, and button variants for vanilla building blocks.

![overview](docs/screenshots/05_many-types-of-blocks.png)

- **1.21.1, 1.21.3–1.21.11**: Fabric + NeoForge + Forge
- **1.20.1**: Fabric + Forge
- **26.1**: NeoForge (Fabric/Forge blocked by upstream issues)
- **26.1.1, 26.1.2, 26.2**: Fabric + NeoForge + Forge

## Added Blocks

Each base block receives the variant types listed below. Variants that already exist in vanilla are not duplicated.

### Fence + Fence Gate + Trapdoor + Door + Pressure Plate + Button (stairs + slab + wall already exist)
Cobblestone, Mossy Cobblestone, Stone Bricks, Mossy Stone Bricks, Granite, Diorite, Andesite, Bricks, Sandstone, Red Sandstone, Prismarine, Blackstone, Polished Blackstone Bricks, Cobbled Deepslate, Polished Deepslate, Deepslate Bricks, Deepslate Tiles, Red Nether Bricks, End Stone Bricks, Mud Bricks, Tuff Bricks, Polished Tuff

### Fence Gate + Trapdoor + Door + Pressure Plate + Button (stairs + slab + wall + fence already exist)
Nether Bricks

### Fence + Fence Gate + Trapdoor + Door (stairs + slab + wall + pressure plate + button already exist)
Polished Blackstone

### Wall + Fence + Fence Gate + Trapdoor + Door + Pressure Plate + Button (stairs + slab already exist)
Polished Granite, Polished Diorite, Polished Andesite, Dark Prismarine, Prismarine Bricks, Purpur, Smooth Sandstone, Smooth Red Sandstone, Quartz, Smooth Quartz

### Stairs + Wall + Fence + Fence Gate + Trapdoor + Door + Pressure Plate + Button (slab already exists)
Smooth Stone, Cut Sandstone, Cut Red Sandstone

### Wall + Fence + Fence Gate + Trapdoor + Door (pressure plate + button already exist)
Stone

### All variants (stairs + slab + wall + fence + fence gate + trapdoor + door + pressure plate + button)
Calcite, Cracked Stone Bricks, Smooth Basalt, Cracked Deepslate Bricks, Cracked Deepslate Tiles, Cracked Polished Blackstone Bricks, Cracked Nether Bricks, End Stone, Gilded Blackstone, Tuff, Obsidian, Crying Obsidian, Packed Mud, Dripstone Block, Amethyst Block, Terracotta (plain), 16 Dyed Terracotta, 16 Concrete

## Installation

1. Install [Fabric Loader](https://fabricmc.net/), [NeoForge](https://neoforged.net/), or [Forge](https://files.minecraftforge.net/)
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
