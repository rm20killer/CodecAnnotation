# CodecAnnotation

CodecAnnotation reflection-based library designed for Hytale mod development. It removes the manual writing of Hytale Codec and AssetBuilderCodec structures by replacing them with Java annotations.

## Installation
### 1. Add Repositories
Add the JitPack repository

```Gradle
repositories {
  //
  maven { url 'https://jitpack.io' } 
}
```

### 2. Add Dependencies
Add the CodecAnnotation library to your dependencies.

```Gradle
dependencies {
  compileOnly "com.hypixel.hytale:Server:$hytale_build" 
  // Other dependencies
  implementation "com.github.rm20killer:CodecAnnotation:1.0.0"
}
```
## How To Use
### Step 1: Annotate Your Data Models
Apply the `@CodecAnnotations.Field` annotation to your properties. 
You can also enforce file path and type constraints using `@CodecAnnotations.CustomValidator`.
You can also enforce Validators with `@CodecAnnotations.Min(0)`, `@CodecAnnotations.UniqueArray` and [more...](https://github.com/rm20killer/CodecAnnotation/blob/main/src/main/java/dev/rm20/codecannotation/Annotations/CodecAnnotations.java)

```Java
import dev.rm20.codecannotation.Annotations.CodecAnnotations;
import com.hypixel.hytale.protocol.Color;

public class BookAssetData implements JsonAssetWithMap<String, DefaultAssetMap<String, BookAssetData>> {
  //... 
  public static class ZoneInfo {
    
    @CodecAnnotations.Field("DisplayName") 
    public String displayName;

    @CodecAnnotations.Field("ZoneDescription") 
    public String zoneDescription;

    // Restricts assets to PNGs located inside the specified directory
    @CodecAnnotations.Field("ZoneImage")
    @CodecAnnotations.CustomValidator(type = "png", value = "UI/Custom/Almanac/Fish/Assets")
    public String zoneImage;

    // Supports native Hytale types like Color
    @CodecAnnotations.Field("TabColour") 
    public Color tabColour;
  }
  //...
}
```
### Step 2: Generate and Register Your Codecs

```Java
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.data.AssetBuilderCodec;
import dev.rm20.codecannotation.AutoCodecBuilder;

public class BookAssetData implements JsonAssetWithMap<String, DefaultAssetMap<String, BookAssetData>> {

  public static final BuilderCodec<ZoneInfo> ZONE_INFO_CODEC;
  public static final BuilderCodec<SpreadTemplate> SPREAD_CODEC;
  public static final BuilderCodec<HabitatInfo> HABITAT_INFO_CODEC;
  public static final AssetBuilderCodec<String, BookAssetData> BOOK_ASSET_CODEC;

  static {
    // 1. Create and Register
    ZONE_INFO_CODEC = AutoCodecBuilder.create(ZoneInfo.class, ZoneInfo::new); 
    AutoCodecBuilder.register(ZoneInfo.class, ZONE_INFO_CODEC);

    SPREAD_CODEC = AutoCodecBuilder.create(SpreadTemplate.class, SpreadTemplate::new);
    AutoCodecBuilder.register(SpreadTemplate.class, SPREAD_CODEC);

    HABITAT_INFO_CODEC = AutoCodecBuilder.create(HabitatInfo.class, HabitatInfo::new);
    AutoCodecBuilder.register(HabitatInfo.class, HABITAT_INFO_CODEC);

    // 2. Create AssetBuilderCodec
    try {
      BOOK_ASSET_CODEC = AutoCodecBuilder.createAsset(
        BookAssetData.class,
        BookAssetData::new,
        BookAssetData.class.getDeclaredField("id"),
        BookAssetData.class.getDeclaredField("data")
      );
    } catch (NoSuchFieldException e) {
      throw new ExceptionInInitializerError("Failed to initialize BookAssetData fields for Codec", e);
      }
    }
    private String id;
    private AssetExtraInfo.Data data;

    // Nested data structures
    public static class ZoneInfo {...}
    public static class SpreadTemplate {...}
    public static class HABITAT_INFO_CODEC {...}

    @CodecAnnotations.Field("Habitats")
    private habitatsInfo[] habitats;
    
    private static AssetStore<String, BookAssetData, DefaultAssetMap<String, BookAssetData>> ASSET_STORE;

}
```
## Example: Config File
Here is an example of a configuration class from [Anglers-Almanac](https://github.com/rm20killer/Anglers-Almanac).

```Java
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import dev.rm20.codecannotation.AutoCodecBuilder;
import dev.rm20.codecannotation.Annotations.CodecAnnotations;

public class AnglersAlmanacConfig {
    public static final String KEY = "Config";

    public static final BuilderCodec<AnglersAlmanacConfig> CODEC =
            AutoCodecBuilder.create(AnglersAlmanacConfig.class, AnglersAlmanacConfig::new);

    public static final KeyedCodec<AnglersAlmanacConfig> KEYED_CODEC = new KeyedCodec<>(KEY, CODEC);

    @CodecAnnotations.Field(value = "MinigameToUse", doc = "The name of the minigame logic to use for fishing.")
    private String minigameToUse = "TensionBar";

    @CodecAnnotations.Field(value = "UseBait", doc = "If fishing should use bait when casting")
    private Boolean ShouldUseBait = false;

    @CodecAnnotations.Field(value = "ShouldHabCheck", doc = "If the loot table should check habitat info")
    private Boolean ShouldHabCheck = true;

    @CodecAnnotations.Field(value = "ShouldEnvironmentCheck", doc = "If the loot table should check Environment info like y level, depth, time of day etc")
    private Boolean ShouldEnvironmentCheck = true;

    @CodecAnnotations.Field( value = "ShouldHookEntities", doc = "If entities can be hooked (including players)")
    private Boolean HookEntities = true;
    public AnglersAlmanacConfig() {
    }
}
```
