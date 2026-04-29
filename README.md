# Item Search Plus

A small client-side Minecraft utility mod that augments the **Creative inventory search tab**
so it also matches items by their **English display name** and **registry id**, regardless of
the game's current language.

Useful when you play in Chinese / Japanese / Korean / etc. and still want to type
`diamond`, `diamond_sword`, or `minecraft:stick` to find the item you want.

- Match modes (always all three, *union*):
  - Current-language display name (vanilla behavior, unchanged)
  - English (`en_us`) display name
  - Registry id, both as `path` (`stick`) and `namespace:path` (`minecraft:stick`)
- Scope: vanilla creative-mode search tab only.
- Side: **client only**. Safe to install on the client without server changes.

## Supported platforms

| Platform | Minecraft | Loader / Engine |
|----------|-----------|-----------------|
| Fabric   | 1.21.1    | Fabric Loader 0.16+, Fabric API |
| NeoForge | 1.21.1    | NeoForge 21.1.x |

The same source compiles for newer 1.21.x patch versions by editing
`gradle.properties` (`minecraft_version`, `neoforge_version`, `fabric_*`, `parchment_*`).

## Build

Requirements: JDK 21.

First-time setup (generates the Gradle wrapper):

```bash
gradle wrapper --gradle-version 8.10
```

Then build either or both jars:

```bash
# Fabric jar -> fabric/build/libs/
./gradlew :fabric:build

# NeoForge jar -> neoforge/build/libs/
./gradlew :neoforge:build

# Both at once
./gradlew build
```

### Building for a specific Minecraft version

Version overrides live under `versions/`. Pass `-PversionProfile=<mc>` to apply
one (or set the `VERSION_PROFILE` env var):

```bash
./gradlew :fabric:build   -PversionProfile=1.21.4
./gradlew :neoforge:build -PversionProfile=1.21.5
```

Available profiles: `1.21.1`, `1.21.4`, `1.21.5` (extend by adding files to
`versions/`). Without a profile flag, the defaults in `gradle.properties`
(currently 1.21.1) are used.

### CI

GitHub Actions builds the full matrix (`fabric` × `neoforge` × every profile)
on every push and on tags `v*`. Tags additionally publish a GitHub Release with
all jars attached. See `.github/workflows/build.yml`.

Drop the platform-appropriate jar into your `mods/` folder.

## Project layout

```
common/      shared sources (no platform deps) — included via srcDir
fabric/      Fabric Loom module
neoforge/    NeoForge ModDevGradle module
```

## How it works (brief)

A Mixin appends to `Minecraft#createSearchTrees` and re-registers
`SearchRegistry.CREATIVE_NAMES` with an extended text-token extractor that yields:

1. The vanilla tooltip lines (current language).
2. The English display name, looked up via a cache built from every namespace's
   `assets/<ns>/lang/en_us.json` file.
3. The item's registry id (`path` and `namespace:path`).

The English cache is rebuilt on every client resource reload (language change, resource
pack swap, `F3+T`, etc.). The creative search tree itself is rebuilt by vanilla whenever
the creative inventory screen opens, so changes are picked up immediately.

## License

MIT.
