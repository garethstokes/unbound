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
