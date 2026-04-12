# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [0.3.0] - 2026-04-12

### Added

- Minecraft 26.1.1 and 26.1.2 support (Fabric, NeoForge, Forge)

### Fixed

- Forge runClient failing on macOS due to missing `-XstartOnFirstThread` JVM argument
- Runtime resource pack not loading on Forge 1.20.1 (registered via `AddPackFindersEvent` instead of Mixin)

## [0.2.0] - 2026-04-05

### Added

- Minecraft 26.1 support for NeoForge (Fabric/Forge blocked by upstream issues; door handle color generation not yet working)
- Fence and fence gate variants for all supported blocks
- New block families: tuff, polished tuff, tuff bricks, deepslate variants, nether brick variants, packed mud, mud bricks, dripstone, amethyst, obsidian, crying obsidian, end stone bricks, and more
- Per-door handle textures generated at runtime from the dominant color of each block's texture

### Changed

- Upgraded Gradle wrapper from 8.14 to 9.4.0
- Migrated build system from architectury-plugin to fabric-loom + ModDevGradle (Gradle 9 compatibility)
- Migrated all Forge 1.21.x builds from ForgeGradle 6.x to 7.x

### Fixed

- Door item model display transforms now match vanilla

## [0.1.0] - 2026-03-23

- Initial release for Minecraft 1.21.1, 1.21.3–1.21.11 (Fabric, NeoForge, Forge)
