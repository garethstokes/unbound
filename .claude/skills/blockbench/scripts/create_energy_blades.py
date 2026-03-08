#!/usr/bin/env python3
"""Generate 32x32 pixel-art energy blade sprites in different colors.

Follows Minecraft pixel art conventions:
- Hard pixel edges, no anti-aliasing
- Limited color palette (8-12 colors)
- 1px black outline for visibility
"""
import sys
from pathlib import Path

try:
    from PIL import Image
except ImportError:
    print("Error: Pillow not installed. Run: pip install Pillow")
    sys.exit(1)

# Color palettes for each energy type (EDGE, MID, CORE)
PALETTES = {
    "blue": {
        "core": (200, 230, 255),
        "mid": (100, 180, 255),
        "edge": (50, 120, 200),
    },
    "green": {
        "core": (200, 255, 200),
        "mid": (100, 220, 100),
        "edge": (50, 160, 50),
    },
    "red": {
        "core": (255, 200, 200),
        "mid": (255, 100, 100),
        "edge": (200, 50, 50),
    },
    "yellow": {
        "core": (255, 255, 200),
        "mid": (255, 220, 100),
        "edge": (200, 160, 50),
    },
    "purple": {
        "core": (230, 200, 255),
        "mid": (180, 100, 255),
        "edge": (120, 50, 200),
    },
}

# Handle colors
HANDLE_DARK = (40, 40, 40)
HANDLE_MID = (64, 64, 64)
HANDLE_LIGHT = (96, 96, 96)
BLACK = (0, 0, 0)

# Transparent
T = None


def create_energy_blade(color_name: str, output_path: str) -> None:
    """Create a 32x32 energy blade sprite with the given color palette."""
    if color_name not in PALETTES:
        print(f"Error: Unknown color '{color_name}'. Available: {list(PALETTES.keys())}")
        sys.exit(1)

    palette = PALETTES[color_name]
    C = palette["core"]   # Core (brightest, center)
    M = palette["mid"]    # Mid (main blade color)
    E = palette["edge"]   # Edge (darkest, outline)

    H1 = HANDLE_DARK
    H2 = HANDLE_MID
    H3 = HANDLE_LIGHT
    B = BLACK

    # 32x32 pixel grid - energy blade diagonal from bottom-left to top-right
    # T = transparent, B = black outline, E = edge, M = mid, C = core
    # H1/H2/H3 = handle shades
    # Design: 3-4px wide blade with core highlight, proper handle with crossguard hint
    pixels = [
        # Row 0 (top) - blade tip
        [T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  B,  B,  T ],
        # Row 1
        [T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  B,  E,  B,  T ],
        # Row 2
        [T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  B,  E,  M,  B,  T ],
        # Row 3
        [T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  B,  E,  M,  M,  B,  T ],
        # Row 4
        [T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  B,  E,  M,  C,  M,  B,  T ],
        # Row 5
        [T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  B,  E,  M,  C,  M,  B,  T,  T ],
        # Row 6
        [T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  B,  E,  M,  C,  M,  B,  T,  T,  T ],
        # Row 7
        [T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  B,  E,  M,  C,  M,  B,  T,  T,  T,  T ],
        # Row 8
        [T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  B,  E,  M,  C,  M,  B,  T,  T,  T,  T,  T ],
        # Row 9
        [T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  B,  E,  M,  C,  M,  B,  T,  T,  T,  T,  T,  T ],
        # Row 10
        [T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  B,  E,  M,  C,  M,  B,  T,  T,  T,  T,  T,  T,  T ],
        # Row 11
        [T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  B,  E,  M,  C,  M,  B,  T,  T,  T,  T,  T,  T,  T,  T ],
        # Row 12
        [T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  B,  E,  M,  C,  M,  B,  T,  T,  T,  T,  T,  T,  T,  T,  T ],
        # Row 13
        [T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  B,  E,  M,  C,  M,  B,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T ],
        # Row 14
        [T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  B,  E,  M,  C,  M,  B,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T ],
        # Row 15
        [T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  B,  E,  M,  C,  M,  B,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T ],
        # Row 16
        [T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  B,  E,  M,  C,  M,  B,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T ],
        # Row 17
        [T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  B,  E,  M,  C,  M,  B,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T ],
        # Row 18
        [T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  B,  E,  M,  C,  M,  B,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T ],
        # Row 19
        [T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  B,  E,  M,  C,  M,  B,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T ],
        # Row 20
        [T,  T,  T,  T,  T,  T,  T,  T,  T,  B,  E,  M,  C,  M,  B,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T ],
        # Row 21
        [T,  T,  T,  T,  T,  T,  T,  T,  B,  E,  M,  C,  M,  B,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T ],
        # Row 22 - blade meets handle
        [T,  T,  T,  T,  T,  T,  T,  B,  E,  M,  C,  M,  B,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T ],
        # Row 23 - handle (diagonal, continues blade line)
        [T,  T,  T,  T,  T,  T,  B, H3, H2, H3,  B,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T ],
        # Row 24 - handle (diagonal)
        [T,  T,  T,  T,  T,  B, H3, H2, H1,  B,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T ],
        # Row 25 - handle (diagonal)
        [T,  T,  T,  T,  B, H3, H2, H1,  B,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T ],
        # Row 26 - handle (diagonal)
        [T,  T,  T,  B, H3, H2, H1,  B,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T ],
        # Row 27 - handle (diagonal)
        [T,  T,  B, H3, H2, H1,  B,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T ],
        # Row 28 - handle (diagonal)
        [T,  B, H3, H2, H1,  B,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T ],
        # Row 29 - pommel
        [T,  B, H2, H1,  B,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T ],
        # Row 30 - pommel
        [B, H2, H1,  B,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T ],
        # Row 31 (bottom) - pommel tip
        [B,  B,  B,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T ],
    ]

    # Create RGBA image
    img = Image.new("RGBA", (32, 32), (0, 0, 0, 0))

    for y, row in enumerate(pixels):
        for x, color in enumerate(row):
            if color is not None:
                img.putpixel((x, y), (*color, 255))

    img.save(output_path)
    print(f"Created: {output_path}")


def main():
    if len(sys.argv) < 2:
        print("Usage: create_energy_blades.py <color|all> [output_dir]")
        print(f"Colors: {', '.join(PALETTES.keys())}, all")
        sys.exit(1)

    color_arg = sys.argv[1].lower()
    output_dir = Path(sys.argv[2]) if len(sys.argv) > 2 else Path(".")
    output_dir.mkdir(parents=True, exist_ok=True)

    if color_arg == "all":
        for color in PALETTES:
            output_path = output_dir / f"energy_blade_{color}.png"
            create_energy_blade(color, str(output_path))
    else:
        output_path = output_dir / f"energy_blade_{color_arg}.png"
        create_energy_blade(color_arg, str(output_path))


if __name__ == "__main__":
    main()
