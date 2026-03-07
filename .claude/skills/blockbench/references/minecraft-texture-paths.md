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
