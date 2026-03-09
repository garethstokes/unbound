#!/usr/bin/env python3
"""Generate 32x32 pixel-art energy boomerang sprite.

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

# Blue energy palette (matching energy blades)
CORE = (200, 230, 255)   # Brightest center
MID = (100, 180, 255)    # Main color
EDGE = (50, 120, 200)    # Darkest edge
BLACK = (0, 0, 0)

# Transparent
T = None
B = BLACK
C = CORE
M = MID
E = EDGE


def create_energy_boomerang(output_path: str) -> None:
    """Create a 32x32 energy boomerang sprite.

    Design: A classic boomerang shape - like a bent < or V rotated diagonally.
    The key is that from the center bend, one arm goes UP-LEFT and the other goes DOWN-LEFT.
    Both arms curve AWAY from each other, creating that classic boomerang silhouette.
    """

    # 32x32 pixel grid
    # Classic boomerang: two arms meeting at a ~110 degree angle
    # Upper arm goes toward upper-left, lower arm goes toward lower-left
    # The vertex/bend point is on the right side of the sprite
    pixels = [
        # Row 0
        [T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T ],
        # Row 1
        [T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T ],
        # Row 2 - Upper arm tip (far left, upper portion)
        [T,  T,  T,  B,  B,  B,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T ],
        # Row 3
        [T,  T,  B,  E,  M,  E,  B,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T ],
        # Row 4
        [T,  T,  B,  M,  C,  M,  B,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T ],
        # Row 5 - Upper arm moves right as it goes down
        [T,  T,  T,  B,  M,  C,  M,  B,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T ],
        # Row 6
        [T,  T,  T,  T,  B,  M,  C,  M,  B,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T ],
        # Row 7
        [T,  T,  T,  T,  T,  B,  M,  C,  M,  B,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T ],
        # Row 8
        [T,  T,  T,  T,  T,  T,  B,  M,  C,  M,  B,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T ],
        # Row 9
        [T,  T,  T,  T,  T,  T,  T,  B,  M,  C,  M,  B,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T ],
        # Row 10
        [T,  T,  T,  T,  T,  T,  T,  T,  B,  M,  C,  M,  B,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T ],
        # Row 11
        [T,  T,  T,  T,  T,  T,  T,  T,  T,  B,  M,  C,  M,  B,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T ],
        # Row 12 - Approaching center
        [T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  B,  M,  C,  M,  B,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T ],
        # Row 13
        [T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  B,  M,  C,  M,  B,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T ],
        # Row 14 - CENTER VERTEX (rightmost point of the V)
        [T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  B,  M,  C,  C,  M,  B,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T ],
        # Row 15 - CENTER - brightest point
        [T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  B,  E,  C,  C,  E,  B,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T ],
        # Row 16 - Lower arm starts going down-left from center
        [T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  B,  M,  C,  C,  M,  B,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T ],
        # Row 17
        [T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  B,  M,  C,  M,  B,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T ],
        # Row 18
        [T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  B,  M,  C,  M,  B,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T ],
        # Row 19
        [T,  T,  T,  T,  T,  T,  T,  T,  T,  B,  M,  C,  M,  B,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T ],
        # Row 20
        [T,  T,  T,  T,  T,  T,  T,  T,  B,  M,  C,  M,  B,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T ],
        # Row 21
        [T,  T,  T,  T,  T,  T,  T,  B,  M,  C,  M,  B,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T ],
        # Row 22
        [T,  T,  T,  T,  T,  T,  B,  M,  C,  M,  B,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T ],
        # Row 23
        [T,  T,  T,  T,  T,  B,  M,  C,  M,  B,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T ],
        # Row 24
        [T,  T,  T,  T,  B,  M,  C,  M,  B,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T ],
        # Row 25
        [T,  T,  T,  B,  M,  C,  M,  B,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T ],
        # Row 26 - Lower arm tip
        [T,  T,  B,  M,  C,  M,  B,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T ],
        # Row 27
        [T,  T,  B,  E,  M,  E,  B,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T ],
        # Row 28 - Lower arm tip end
        [T,  T,  T,  B,  B,  B,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T ],
        # Row 29-31 - empty
        [T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T ],
        [T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T ],
        [T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T ],
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
    output_dir = Path(sys.argv[1]) if len(sys.argv) > 1 else Path(".")
    output_dir.mkdir(parents=True, exist_ok=True)

    output_path = output_dir / "energy_boomerang.png"
    create_energy_boomerang(str(output_path))


if __name__ == "__main__":
    main()
