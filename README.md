# Unbound

A Minecraft Fabric mod that adds energy weapons and tools.

## Features

### Energy Blades
Powerful melee weapons with custom swing sounds and fast obsidian mining. Each color has unique effects:

| Blade | Effect |
|-------|--------|
| **Azure** (Blue) | Balanced stats, electric spark particles |
| **Verdant** (Green) | Poisons enemies, life steal |
| **Infernal** (Red) | High damage, sets enemies on fire |
| **Radiant** (Yellow) | Fast attacks, slows enemies |
| **Void** (Purple) | Wither + Darkness effects |
| **Luminous** (White) | Reveals enemies (Glowing), heals wielder, right-click for Invisibility |
| **Prismatic** (Ultimate) | Combines all effects, rainbow particles |

### Fury Double Blade
A double-sided energy staff with blades on both ends.
- **15 base damage** (nearly double a Netherite sword)
- **Lunge-Spin Attack**: Hold right-click for 1.5s, release to dash 9 blocks forward while damaging all enemies in a 4-block radius
- Crafted from 2x Infernal Energy Blade + Nether Star

### Energy Boomerang
Throwable weapon that curves through the air, pierces multiple enemies, and returns to the player.

## Requirements

- Minecraft **1.21.11**
- Fabric Loader **0.18.0** or newer
- Fabric API **0.141.3+1.21.11** or newer

## Installation

### 1. Install Fabric Loader

Download and run the Fabric installer from [fabricmc.net](https://fabricmc.net/use/installer/).

Select Minecraft 1.21.11 and click "Install". This creates a "Fabric" profile in your Minecraft launcher.

### 2. Download Fabric API

Download the Fabric API for Minecraft 1.21.11 from [Modrinth](https://modrinth.com/mod/fabric-api) or [CurseForge](https://www.curseforge.com/minecraft/mc-mods/fabric-api).

### 3. Find Your Mods Folder

| OS | Location |
|----|----------|
| Windows | `%AppData%\.minecraft\mods` |
| macOS | `~/Library/Application Support/minecraft/mods` |
| Linux | `~/.minecraft/mods` |

If the `mods` folder doesn't exist, create it.

### 4. Install the Mods

Place both JAR files in your mods folder:
- `fabric-api-0.141.3+1.21.11.jar` (or newer)
- `unbound-0.2.0.jar`

### 5. Launch the Game

1. Open the Minecraft Launcher
2. Select the "Fabric" profile
3. Click "Play"

## Recipes

All items are crafted at a crafting table. Check the in-game recipe book for details.

## Troubleshooting

**Mod doesn't appear in the mods list**
- Make sure you're using the Fabric profile in the launcher
- Verify you have Minecraft 1.21.11 selected

**Game crashes on launch**
- Check that Fabric API version matches Minecraft 1.21.11
- Make sure you have Java 21 or newer installed

**"Fabric Loader not found" error**
- Run the Fabric installer again for Minecraft 1.21.11

## License

MIT
