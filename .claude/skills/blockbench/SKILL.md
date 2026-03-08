---
name: blockbench
description: Use when creating Minecraft item/block sprites, converting AI images to 16x16 pixel-perfect textures, or preparing textures for resource packs (Fabric/Forge) (project)
---

# Minecraft Sprite Creation

Create 16×16 pixel-perfect Minecraft sprites directly using Python.

## Workflow

1. **Design the sprite** — Plan colors and pixel layout for 16×16 grid
2. **Create BMP directly** — Use `scripts/create_sprite.py` to generate the texture
3. **Convert to PNG** — Run `scripts/bmp_to_png.py` for Minecraft compatibility
4. **Validate** — Follow `references/validation-checklist.md`
5. **Place** — Copy to correct mod path per `references/minecraft-texture-paths.md`

## Creating Sprites

Use `scripts/create_sprite.py` to define sprites pixel-by-pixel:

```python
# Define colors as RGB tuples
TRANSPARENT = None  # Will be transparent in final PNG
BLACK = (0, 0, 0)
DARK_GRAY = (64, 64, 64)
BLADE_BLUE = (100, 180, 255)
BLADE_CORE = (200, 230, 255)

# Define 16x16 grid (top-to-bottom, left-to-right)
# Use . for transparent, letters/numbers for colors
pixels = [
    ". . . . . . . . . . . . . B B .",
    ". . . . . . . . . . . . B C B .",
    ". . . . . . . . . . . B C B . .",
    # ... continue for all 16 rows
]
```

Run: `python scripts/create_sprite.py sword output.bmp`

## Critical Rules

- **ALWAYS** create at exactly 16×16 — no resizing
- **NO** gradients or anti-aliasing — hard pixel edges only
- **NO** semi-transparent pixels — use full opacity or full transparent
- Limit to 8-12 colors for authentic Minecraft aesthetic
- Use 1px black outline for item visibility

## Design Guidelines

| Element | Approach |
|---------|----------|
| Outline | 1px black (#000000) around visible edges |
| Highlights | Single lighter shade, top-left bias |
| Shadows | Single darker shade, bottom-right bias |
| Core colors | 2-3 shades per material (light/mid/dark) |
| Transparency | Background pixels = fully transparent |

## Scripts

| Script | Usage |
|--------|-------|
| `scripts/create_sprite.py` | `python create_sprite.py <template> <output.bmp>` |
| `scripts/bmp_to_png.py` | `python bmp_to_png.py <input.bmp> <output.png>` |

## Color Palettes (Common Materials)

```python
# Iron/Metal
IRON_DARK = (96, 96, 96)
IRON_MID = (160, 160, 160)
IRON_LIGHT = (200, 200, 200)

# Wood
WOOD_DARK = (101, 67, 33)
WOOD_MID = (139, 90, 43)
WOOD_LIGHT = (181, 137, 85)

# Gold
GOLD_DARK = (180, 140, 20)
GOLD_MID = (220, 180, 50)
GOLD_LIGHT = (255, 220, 100)

# Blue Energy
BLUE_CORE = (200, 230, 255)
BLUE_MID = (100, 180, 255)
BLUE_EDGE = (50, 120, 200)

# Green Energy
GREEN_CORE = (200, 255, 200)
GREEN_MID = (100, 220, 100)
GREEN_EDGE = (50, 160, 50)

# Red Energy
RED_CORE = (255, 200, 200)
RED_MID = (255, 100, 100)
RED_EDGE = (200, 50, 50)

# Yellow Energy
YELLOW_CORE = (255, 255, 200)
YELLOW_MID = (255, 220, 100)
YELLOW_EDGE = (200, 160, 50)

# Purple Energy
PURPLE_CORE = (230, 200, 255)
PURPLE_MID = (180, 100, 255)
PURPLE_EDGE = (120, 50, 200)

# Rainbow Energy (cycle through for animated or gradient effects)
RAINBOW = [
    (255, 100, 100),   # Red
    (255, 180, 100),   # Orange
    (255, 255, 100),   # Yellow
    (100, 255, 100),   # Green
    (100, 200, 255),   # Blue
    (180, 100, 255),   # Purple
]
```

## References

- `references/validation-checklist.md` — Pre/post export verification
- `references/minecraft-texture-paths.md` — Fabric/Forge file placement
