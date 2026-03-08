# Blockbench Skill Improvement Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Refactor the blockbench skill from a 186-line tutorial into a ~55-line imperative skill with extracted scripts and references.

**Architecture:** Extract deterministic operations (ImageMagick, Python resize) into executable scripts. Move reference material (checklists, paths) into progressive-loading reference files. Rewrite SKILL.md to be imperative and compact.

**Tech Stack:** Bash scripts, Python/Pillow, Markdown

---

## Task 1: Create Scripts Directory and Resize Script (Bash)

**Files:**
- Create: `.claude/skills/blockbench/scripts/resize-sprite.sh`

**Step 1: Create the scripts directory**

```bash
mkdir -p /home/gareth/code/mc/unbound/.claude/skills/blockbench/scripts
```

**Step 2: Create the bash resize script**

Create `.claude/skills/blockbench/scripts/resize-sprite.sh`:

```bash
#!/usr/bin/env bash
# Resize image to 16x16 Minecraft sprite with proper settings
# Usage: ./resize-sprite.sh input.png output.png [colors]

set -euo pipefail

INPUT="${1:?Usage: resize-sprite.sh input.png output.png [colors]}"
OUTPUT="${2:?Usage: resize-sprite.sh input.png output.png [colors]}"
COLORS="${3:-16}"

convert "$INPUT" \
    -resize 16x16 \
    -filter Point \
    -colors "$COLORS" \
    -background none \
    "$OUTPUT"

echo "Created: $OUTPUT ($(identify -format '%wx%h' "$OUTPUT"))"
```

**Step 3: Make script executable**

Run: `chmod +x /home/gareth/code/mc/unbound/.claude/skills/blockbench/scripts/resize-sprite.sh`

**Step 4: Commit**

```bash
git add .claude/skills/blockbench/scripts/resize-sprite.sh
git commit -m "feat(skill): add ImageMagick resize script for blockbench skill"
```

---

## Task 2: Create Python Resize Script

**Files:**
- Create: `.claude/skills/blockbench/scripts/resize_sprite.py`

**Step 1: Create the Python resize script**

Create `.claude/skills/blockbench/scripts/resize_sprite.py`:

```python
#!/usr/bin/env python3
"""Convert image to 16x16 Minecraft sprite using NEAREST interpolation."""
import sys
from pathlib import Path

try:
    from PIL import Image
except ImportError:
    print("Error: Pillow not installed. Run: pip install Pillow")
    sys.exit(1)


def to_minecraft_sprite(input_path: str, output_path: str) -> None:
    """Resize image to 16x16 with NEAREST interpolation (no blur)."""
    img = Image.open(input_path).convert("RGBA")
    sprite = img.resize((16, 16), Image.NEAREST)
    sprite.save(output_path)
    print(f"Created: {output_path} ({sprite.width}x{sprite.height})")


if __name__ == "__main__":
    if len(sys.argv) != 3:
        print("Usage: resize_sprite.py input.png output.png")
        sys.exit(1)

    input_file = Path(sys.argv[1])
    if not input_file.exists():
        print(f"Error: Input file not found: {input_file}")
        sys.exit(1)

    to_minecraft_sprite(sys.argv[1], sys.argv[2])
```

**Step 2: Make script executable**

Run: `chmod +x /home/gareth/code/mc/unbound/.claude/skills/blockbench/scripts/resize_sprite.py`

**Step 3: Commit**

```bash
git add .claude/skills/blockbench/scripts/resize_sprite.py
git commit -m "feat(skill): add Python resize script for blockbench skill"
```

---

## Task 3: Create References Directory and Validation Checklist

**Files:**
- Create: `.claude/skills/blockbench/references/validation-checklist.md`

**Step 1: Create the references directory**

```bash
mkdir -p /home/gareth/code/mc/unbound/.claude/skills/blockbench/references
```

**Step 2: Create the validation checklist**

Create `.claude/skills/blockbench/references/validation-checklist.md`:

```markdown
# Sprite Validation Checklist

## Before Export

- [ ] Dimensions exactly 16×16 (or 32×32 for HD packs)
- [ ] No semi-transparent pixels (hard edges only)
- [ ] 8-12 distinct colors maximum
- [ ] Clear silhouette readable at 1:1 scale
- [ ] No gradient artifacts from resize

## After Export

- [ ] File format is PNG with alpha channel
- [ ] File size < 5KB
- [ ] No blur visible at 400% zoom
- [ ] Transparency renders correctly (checkerboard test)
- [ ] Item recognizable at actual game size

## Quick Verification Commands

```bash
# Check dimensions
identify sprite.png
# Expected: sprite.png PNG 16x16 ...

# Check file size
ls -la sprite.png
# Expected: < 5KB

# Check color count (optional)
identify -verbose sprite.png | grep Colors
```
```

**Step 3: Commit**

```bash
git add .claude/skills/blockbench/references/validation-checklist.md
git commit -m "feat(skill): add validation checklist reference for blockbench skill"
```

---

## Task 4: Create Minecraft Texture Paths Reference

**Files:**
- Create: `.claude/skills/blockbench/references/minecraft-texture-paths.md`

**Step 1: Create the texture paths reference**

Create `.claude/skills/blockbench/references/minecraft-texture-paths.md`:

```markdown
# Minecraft Texture File Placement

## Fabric/Forge Mod Structure

### Item Textures
```
src/main/resources/assets/<modid>/textures/item/<item_name>.png
```

### Block Textures
```
src/main/resources/assets/<modid>/textures/block/<block_name>.png
```

## Model JSON Registration

### Simple Item (item/generated parent)
```json
{
  "parent": "item/generated",
  "textures": {
    "layer0": "<modid>:item/<item_name>"
  }
}
```

Place at: `src/main/resources/assets/<modid>/models/item/<item_name>.json`

### Simple Block
```json
{
  "parent": "block/cube_all",
  "textures": {
    "all": "<modid>:block/<block_name>"
  }
}
```

Place at: `src/main/resources/assets/<modid>/models/block/<block_name>.json`

## Common Mistakes

- Forgetting to create the model JSON (texture won't appear)
- Wrong modid in texture path (case-sensitive)
- Missing `textures/` directory level
- Using `.png` extension in model JSON (don't include it)
```

**Step 2: Commit**

```bash
git add .claude/skills/blockbench/references/minecraft-texture-paths.md
git commit -m "feat(skill): add Minecraft texture paths reference for blockbench skill"
```

---

## Task 5: Rewrite SKILL.md (Core Refactor)

**Files:**
- Modify: `.claude/skills/blockbench/SKILL.md` (full replacement)

**Step 1: Backup original (optional safety)**

Run: `cp /home/gareth/code/mc/unbound/.claude/skills/blockbench/SKILL.md /home/gareth/code/mc/unbound/.claude/skills/blockbench/SKILL.md.bak`

**Step 2: Replace SKILL.md with compact imperative version**

Replace entire contents of `.claude/skills/blockbench/SKILL.md` with:

```markdown
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
```

**Step 3: Remove backup if rewrite is correct**

Run: `rm /home/gareth/code/mc/unbound/.claude/skills/blockbench/SKILL.md.bak`

**Step 4: Commit**

```bash
git add .claude/skills/blockbench/SKILL.md
git commit -m "refactor(skill): rewrite blockbench SKILL.md as compact imperative skill

- Reduced from 186 lines to ~55 lines
- Removed tutorial explanations (why AI fails, etc.)
- Removed graphviz diagram
- Removed redundant 'When to Use' section
- Converted to imperative instructions
- Added script and reference file pointers"
```

---

## Task 6: Verify Skill Structure

**Files:**
- Verify: `.claude/skills/blockbench/` (all files)

**Step 1: Check directory structure**

Run:
```bash
find /home/gareth/code/mc/unbound/.claude/skills/blockbench -type f | sort
```

Expected output:
```
/home/gareth/code/mc/unbound/.claude/skills/blockbench/SKILL.md
/home/gareth/code/mc/unbound/.claude/skills/blockbench/references/minecraft-texture-paths.md
/home/gareth/code/mc/unbound/.claude/skills/blockbench/references/validation-checklist.md
/home/gareth/code/mc/unbound/.claude/skills/blockbench/scripts/resize-sprite.sh
/home/gareth/code/mc/unbound/.claude/skills/blockbench/scripts/resize_sprite.py
```

**Step 2: Check SKILL.md line count**

Run: `wc -l /home/gareth/code/mc/unbound/.claude/skills/blockbench/SKILL.md`

Expected: ~55-60 lines (down from 186)

**Step 3: Check scripts are executable**

Run: `ls -la /home/gareth/code/mc/unbound/.claude/skills/blockbench/scripts/`

Expected: Both scripts have `x` permission

**Step 4: Final commit (if any cleanup needed)**

```bash
git status
# If clean, skip. Otherwise commit fixes.
```

---

## Summary of Changes

| Metric | Before | After |
|--------|--------|-------|
| SKILL.md lines | 186 | ~55 |
| Files | 1 | 5 |
| Scripts | 0 (inline) | 2 (executable) |
| References | 0 (inline) | 2 (progressive) |
| Trigger quality | Good | Good (minor name improvement) |

**Key improvements:**
1. Compact imperative SKILL.md (70% size reduction)
2. Extracted deterministic scripts (reusable, testable)
3. Progressive loading via references
4. Removed tutorial fluff (graphviz, explanations, tool links)
5. Clear skill name reflecting actual scope
