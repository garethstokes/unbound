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
