---
name: minecraft-sprite-workflow
description: Use when creating Minecraft item/block sprites, converting AI images to 16x16 pixel-perfect textures, or preparing textures for resource packs (Fabric/Forge)
---

# Minecraft Sprite Workflow

Convert AI-generated concepts into valid 16×16 Minecraft sprites.

## Workflow

1. **Generate concept** — Use AI at 64×64+ with prompt below
2. **Resize to 16×16** — Run `scripts/resize-sprite.sh` or `scripts/resize_sprite.py` (NEAREST interpolation)
3. **Clean up** — Open in Blockbench Paint mode: remove artifacts, add 1px outline, fix transparency
4. **Validate** — Follow `references/validation-checklist.md`
5. **Place** — Copy to correct mod path per `references/minecraft-texture-paths.md`

## Critical Rules

- **NEVER** use bilinear/bicubic resize — creates blur
- **ALWAYS** use NEAREST/Point filter
- **DELETE** semi-transparent pixels — Minecraft needs hard transparency
- Limit to 8-12 colors for authentic aesthetic

## AI Prompt Template

```
[item description], pixel art style, Minecraft aesthetic,
simple flat colors, no gradients, black outline,
centered on transparent background, 64x64
```

## Scripts

| Script | Usage |
|--------|-------|
| `scripts/resize-sprite.sh` | `./resize-sprite.sh input.png output.png [colors]` |
| `scripts/resize_sprite.py` | `python resize_sprite.py input.png output.png` |

## Common Mistakes

| Mistake | Fix |
|---------|-----|
| Bilinear resize | Use NEAREST filter via scripts |
| Gradients remain | Posterize or manual flatten |
| Too much detail | Simplify BEFORE resize |
| Semi-transparent edges | Delete in Blockbench |
| JPEG export | Use PNG with alpha |

## References

- `references/validation-checklist.md` — Pre/post export verification
- `references/minecraft-texture-paths.md` — Fabric/Forge file placement
