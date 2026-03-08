# Just Block Shapes

## Overview

**Just Block Shapes** is a simple Minecraft mod that adds missing block shape variants (walls, stairs, slabs) for vanilla materials that lack them.

Vanilla Minecraft has inconsistencies where:

- A full block exists but has **no wall variant**
- Stairs and slabs exist but there is **no fence variant**
- A material is suitable for building but **its shape set is incomplete**

This results in situations like:

> "I want to build a wall with this material, but there's no wall block for it."

Just Block Shapes fills these gaps by adding the **missing shape variants**, making vanilla building materials **more consistently usable**.

## Concept

### 1. Natural vanilla extension

All added blocks use:

- Existing vanilla textures
- Same properties as the base block
- Standard vanilla recipe patterns

The goal is:

> "Every added block should look like it was always part of vanilla."

### 2. Building-focused

Only blocks that are **natural as building materials** are targeted.

Included:

- Stone variants
- Quartz variants
- Terracotta
- Decorative blocks

Excluded:

- Functional blocks
- Redstone-related blocks
- Special-purpose blocks

This prevents block count bloat and unnatural additions.

### 3. Only missing shapes

Shapes that already exist in vanilla are not added.

Example:

| Base Block   | Vanilla has    | Added by this mod |
| ------------ | -------------- | ----------------- |
| Quartz Block | slab / stairs  | **wall**          |
| Smooth Stone | slab           | **stairs / wall** |

Only the **missing variants** are filled in.

### 4. Consistent building experience

The goal is:

> "When you pick a material, you should never have to give up because a shape variant is missing."

This improves freedom in building castles, walls, decorations, and towns.

## Implementation Details

### Extracting missing shapes

During development, a tool checks the vanilla block registry for existing `_wall`, `_stairs`, and `_slab` variants and automatically lists what is missing.

From that list, only blocks suitable for building are manually selected and implemented.

This balances completeness with curation.

### Block properties

All added blocks use:

```java
Properties.ofFullCopy(baseBlock)
```

This copies hardness, blast resistance, mining level, sound, etc. from the base block.

### Shape rules

For each target block, the following shapes are added if missing:

| Shape  | Condition                          |
| ------ | ---------------------------------- |
| wall   | Added if no wall variant exists    |
| stairs | Added if no stairs variant exists  |
| slab   | Added if no slab variant exists    |

Target blocks are limited to materials where walls are architecturally appropriate.

### Wall material tag

A block tag `justblockshapes:wall_materials` determines which blocks are eligible:

```json
{
  "values": [
    "minecraft:quartz_block",
    "minecraft:smooth_quartz",
    "minecraft:smooth_stone",
    "minecraft:calcite"
  ]
}
```

Only blocks in this tag are processed for missing shape addition.

### Data generation

Block states, models, loot tables, and recipes are generated using Minecraft's Data Generator rather than hand-written JSON.

### Creative tab

Added blocks are placed in existing vanilla creative tabs (e.g., "Building Blocks", "Decorations") rather than a custom tab, so they appear naturally alongside vanilla blocks.

## Target Users

- **Builders** — more material freedom
- **Modpack creators** — improved building material consistency
- **Mod developers** — fewer material constraints for structure generation

## Architecture

- Minecraft mod using Architectury
- Compatible with Fabric and NeoForge (1.21.1), Fabric and Forge (1.20.1)
- Initially implemented for Minecraft version 1.21.1
- The project will be configured with Gradle as a multi-project setup

## Directory structure

- common/shared
    - Common code without loader dependencies or version dependencies. Not a Gradle subproject, but incorporated as one of the srcDirs from each version-specific subproject
- common/1.21.1
    - Common code for Minecraft 1.21.1 without loader dependencies. Gradle subproject.
- common/1.20.1
    - Common code for Minecraft 1.20.1 without loader dependencies. Gradle subproject.
- fabric/base
    - Code for Fabric without Minecraft version dependencies. Not a Gradle subproject, but incorporated as one of the srcDirs from each version-specific subproject.
- fabric/1.21.1
    - Code for Fabric and Minecraft 1.21.1. Gradle subproject. Depends on fabric-base.
- fabric/1.20.1
    - Code for Fabric and Minecraft 1.20.1. Gradle subproject. Depends on fabric-base.
- neoforge/base
    - Code for NeoForge without Minecraft version dependencies. Not a Gradle subproject, but incorporated as one of the srcDirs from each version-specific subproject.
- neoforge/1.21.1
    - Code for NeoForge and Minecraft 1.21.1. Gradle subproject. Depends on neoforge-base.
- forge/base
    - Code for Forge without Minecraft version dependencies. Not a Gradle subproject, but incorporated as one of the srcDirs from each version-specific subproject.
- forge/1.20.1
    - Code for Forge and Minecraft 1.20.1. Gradle subproject. Depends on forge-base.

## License

- LGPL-3.0-only
