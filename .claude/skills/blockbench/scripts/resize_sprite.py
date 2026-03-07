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
