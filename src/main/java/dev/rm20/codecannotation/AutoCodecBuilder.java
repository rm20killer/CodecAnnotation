package dev.rm20.codecannotation;

import com.hypixel.hytale.assetstore.AssetExtraInfo;
import com.hypixel.hytale.assetstore.JsonAsset;
import com.hypixel.hytale.assetstore.codec.AssetBuilderCodec;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.builder.BuilderField;
import com.hypixel.hytale.codec.codecs.EnumCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.codec.schema.metadata.ui.UIDisplayMode;
import com.hypixel.hytale.codec.schema.metadata.ui.UIEditorPreview;
import com.hypixel.hytale.codec.schema.metadata.ui.UIEditorSectionStart;
import com.hypixel.hytale.codec.schema.metadata.ui.UIPropertyTitle;
import com.hypixel.hytale.codec.validation.Validator;
import com.hypixel.hytale.codec.validation.ValidatorCache;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.math.shape.*;
import com.hypixel.hytale.protocol.*;
import com.hypixel.hytale.server.core.asset.common.CommonAssetValidator;
import com.hypixel.hytale.server.core.codec.LayerEntryCodec;
import com.hypixel.hytale.server.core.codec.PairCodec;
import com.hypixel.hytale.server.core.codec.ProtocolCodecs;
import com.hypixel.hytale.server.core.codec.ShapeCodecs;
import dev.rm20.codecannotation.Annotations.CodecAnnotations;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class AutoCodecBuilder {

    private static final Map<Class<?>, Codec<?>> REGISTRY = new ConcurrentHashMap<>();
    private static final Map<Class<?>, Codec<?>> PRIMITIVE_CODECS = new HashMap<>();

    static {
        // Built-in Primitives
        PRIMITIVE_CODECS.put(String.class, Codec.STRING);
        PRIMITIVE_CODECS.put(Integer.TYPE, Codec.INTEGER);
        PRIMITIVE_CODECS.put(Integer.class, Codec.INTEGER);
        PRIMITIVE_CODECS.put(Boolean.TYPE, Codec.BOOLEAN);
        PRIMITIVE_CODECS.put(Boolean.class, Codec.BOOLEAN);
        PRIMITIVE_CODECS.put(Float.TYPE, Codec.FLOAT);
        PRIMITIVE_CODECS.put(Float.class, Codec.FLOAT);
        PRIMITIVE_CODECS.put(Double.TYPE, Codec.DOUBLE);
        PRIMITIVE_CODECS.put(Double.class, Codec.DOUBLE);
        PRIMITIVE_CODECS.put(Long.TYPE, Codec.LONG);
        PRIMITIVE_CODECS.put(Long.class, Codec.LONG);
        PRIMITIVE_CODECS.put(Byte.TYPE, Codec.BYTE);
        PRIMITIVE_CODECS.put(Byte.class, Codec.BYTE);
        PRIMITIVE_CODECS.put(Short.TYPE, Codec.SHORT);
        PRIMITIVE_CODECS.put(Short.class, Codec.SHORT);

        // Standard Utility & BSON Types
        PRIMITIVE_CODECS.put(org.bson.BsonDocument.class, Codec.BSON_DOCUMENT);
        PRIMITIVE_CODECS.put(UUID.class, Codec.UUID_STRING);
        PRIMITIVE_CODECS.put(java.nio.file.Path.class, Codec.PATH);
        PRIMITIVE_CODECS.put(java.time.Instant.class, Codec.INSTANT);
        PRIMITIVE_CODECS.put(java.time.Duration.class, Codec.DURATION);
        PRIMITIVE_CODECS.put(java.util.logging.Level.class, Codec.LOG_LEVEL);

        // Protocol Codecs
        PRIMITIVE_CODECS.put(Color.class, ProtocolCodecs.COLOR);
        PRIMITIVE_CODECS.put(Direction.class, ProtocolCodecs.DIRECTION);
        PRIMITIVE_CODECS.put(ColorLight.class, ProtocolCodecs.COLOR_LIGHT);
        PRIMITIVE_CODECS.put(Color[].class, ProtocolCodecs.COLOR_ARRAY);
        PRIMITIVE_CODECS.put(ColorAlpha.class, ProtocolCodecs.COLOR_ALPHA);
        PRIMITIVE_CODECS.put(GameMode.class, ProtocolCodecs.GAMEMODE);
        PRIMITIVE_CODECS.put(Size.class, ProtocolCodecs.SIZE);
        PRIMITIVE_CODECS.put(Range.class, ProtocolCodecs.RANGE);
        PRIMITIVE_CODECS.put(Rangeb.class, ProtocolCodecs.RANGEB);
        PRIMITIVE_CODECS.put(Rangef.class, ProtocolCodecs.RANGEF);
        PRIMITIVE_CODECS.put(RangeVector2f.class, ProtocolCodecs.RANGE_VECTOR2F);
        PRIMITIVE_CODECS.put(RangeVector3f.class, ProtocolCodecs.RANGE_VECTOR3F);
        PRIMITIVE_CODECS.put(InitialVelocity.class, ProtocolCodecs.INITIAL_VELOCITY);
        PRIMITIVE_CODECS.put(UVMotion.class, ProtocolCodecs.UV_MOTION);
        PRIMITIVE_CODECS.put(IntersectionHighlight.class, ProtocolCodecs.INTERSECTION_HIGHLIGHT);
        PRIMITIVE_CODECS.put(SavedMovementStates.class, ProtocolCodecs.SAVED_MOVEMENT_STATES);
        PRIMITIVE_CODECS.put(ItemAnimation.class, ProtocolCodecs.ITEM_ANIMATION_CODEC);
        PRIMITIVE_CODECS.put(ChangeStatBehaviour.class, ProtocolCodecs.CHANGE_STAT_BEHAVIOUR_CODEC);
        PRIMITIVE_CODECS.put(AccumulationMode.class, ProtocolCodecs.ACCUMULATION_MODE_CODEC);
        PRIMITIVE_CODECS.put(EasingType.class, ProtocolCodecs.EASING_TYPE_CODEC);
        PRIMITIVE_CODECS.put(ChangeVelocityType.class, ProtocolCodecs.CHANGE_VELOCITY_TYPE_CODEC);
        PRIMITIVE_CODECS.put(RailPoint.class, ProtocolCodecs.RAIL_POINT_CODEC);
        PRIMITIVE_CODECS.put(RailConfig.class, ProtocolCodecs.RAIL_CONFIG_CODEC);

        PRIMITIVE_CODECS.put(Shape.class, ShapeCodecs.SHAPE);
        PRIMITIVE_CODECS.put(Box.class, ShapeCodecs.BOX);
        PRIMITIVE_CODECS.put(Ellipsoid.class, ShapeCodecs.ELLIPSOID);
        PRIMITIVE_CODECS.put(Cylinder.class, ShapeCodecs.CYLINDER);
        PRIMITIVE_CODECS.put(OriginShape.class, ShapeCodecs.ORIGIN_SHAPE);
        
        // Primitive & Object Array Codecs
        PRIMITIVE_CODECS.put(String[].class, Codec.STRING_ARRAY);
        PRIMITIVE_CODECS.put(byte[].class, Codec.BYTE_ARRAY);
        PRIMITIVE_CODECS.put(int[].class, Codec.INT_ARRAY);
        PRIMITIVE_CODECS.put(float[].class, Codec.FLOAT_ARRAY);
        PRIMITIVE_CODECS.put(double[].class, Codec.DOUBLE_ARRAY);
        PRIMITIVE_CODECS.put(long[].class, Codec.LONG_ARRAY);

        PRIMITIVE_CODECS.put(LayerEntryCodec.class, LayerEntryCodec.CODEC);
        PRIMITIVE_CODECS.put(PairCodec.IntegerPair.class, PairCodec.IntegerPair.CODEC);
        PRIMITIVE_CODECS.put(PairCodec.IntegerStringPair.class, PairCodec.IntegerStringPair.CODEC);

        // Boxed Primitive Array Fallbacks
        PRIMITIVE_CODECS.put(Integer[].class, new ArrayCodec<>(Codec.INTEGER, Integer[]::new));
        PRIMITIVE_CODECS.put(Float[].class, new ArrayCodec<>(Codec.FLOAT, Float[]::new));
        PRIMITIVE_CODECS.put(Double[].class, new ArrayCodec<>(Codec.DOUBLE, Double[]::new));
        PRIMITIVE_CODECS.put(Long[].class, new ArrayCodec<>(Codec.LONG, Long[]::new));
    }

    public static <T> void register(Class<T> clazz, Codec<T> codec) {
        REGISTRY.put(clazz, codec);
    }

    /**
     * Builds a standard object BuilderCodec.
     */
    public static <T> BuilderCodec<T> create(Class<T> clazz, Supplier<T> creator) {
        BuilderCodec.Builder<T> builder = BuilderCodec.builder(clazz, creator);

        for (Field field : getDeclaredFieldsUpToSuper(clazz)) {
            if (!field.isAnnotationPresent(CodecAnnotations.Field.class)) continue;

            CodecAnnotations.Field meta = field.getAnnotation(CodecAnnotations.Field.class);
            String key = meta.value().isEmpty() ? field.getName() : meta.value();
            Codec<?> baseCodec = resolveCodecForType(field.getType());

            MethodHandle getter = unreflectGetter(field);
            MethodHandle setter = unreflectSetter(field);

            BuilderField.FieldBuilder<T, Object, BuilderCodec.Builder<T>> fieldBuilder = builder.append(
                    new KeyedCodec<>(key, (Codec<Object>) baseCodec),
                    (instance, value) -> invokeSetter(setter, instance, value),
                    (instance) -> invokeGetter(getter, instance)
            );

            applyFieldValidatorsAndDoc(fieldBuilder, field, meta);
            builder = fieldBuilder.add();
        }

        return builder.build();
    }

    /**
     * Builds a JsonAsset AssetBuilderCodec
     */
    public static <T extends JsonAsset<String>> AssetBuilderCodec<String, T> createAsset(
            Class<T> clazz,
            Supplier<T> creator,
            Field idField,
            Field dataField
    ) {
        MethodHandle idGetter = unreflectGetter(idField);
        MethodHandle idSetter = unreflectSetter(idField);
        MethodHandle dataGetter = unreflectGetter(dataField);
        MethodHandle dataSetter = unreflectSetter(dataField);

        var builder = AssetBuilderCodec.builder(
                clazz,
                creator,
                Codec.STRING,
                (instance, id) -> invokeSetter(idSetter, instance, id),
                (instance) -> (String) invokeGetter(idGetter, instance),
                (instance, data) -> invokeSetter(dataSetter, instance, data),
                (instance) -> (AssetExtraInfo.Data) invokeGetter(dataGetter, instance)
        );

        for (Field field : getDeclaredFieldsUpToSuper(clazz)) {
            if (!field.isAnnotationPresent(CodecAnnotations.Field.class)) continue;

            CodecAnnotations.Field meta = field.getAnnotation(CodecAnnotations.Field.class);
            String key = meta.value().isEmpty() ? field.getName() : meta.value();
            Codec<Object> baseCodec = (Codec<Object>) resolveCodecForType(field.getType());

            MethodHandle getter = unreflectGetter(field);
            MethodHandle setter = unreflectSetter(field);

            var context = builder.appendInherited(
                    new KeyedCodec<>(key, baseCodec),
                    (instance, value) -> invokeSetter(setter, instance, value),
                    (instance) -> invokeGetter(getter, instance),
                    (instance, parent) -> invokeSetter(setter, instance, invokeGetter(getter, parent))
            );

            applyFieldValidatorsAndDoc(context, field, meta);
            builder = context.add();
        }

        return builder.build();
    }

    /**
     * Shared logic to process annotations (documentation + validators) on fields.
     */
    @SuppressWarnings("unchecked")
    private static <B extends BuilderCodec.BuilderBase<?, B>> void applyFieldValidatorsAndDoc(
            BuilderField.FieldBuilder<?, Object, B> context,
            Field field,
            CodecAnnotations.Field meta
    ) {
        if (!meta.doc().isEmpty()) {
            context.documentation(meta.doc());
        }

        // Meta data

        if (field.isAnnotationPresent(CodecAnnotations.PropertyTitle.class)) {
            CodecAnnotations.PropertyTitle titleMeta = field.getAnnotation(CodecAnnotations.PropertyTitle.class);
            context.metadata(new UIPropertyTitle(titleMeta.value()));
        }

        if (field.isAnnotationPresent(CodecAnnotations.DisplayMode.class)) {
            CodecAnnotations.DisplayMode modeMeta = field.getAnnotation(CodecAnnotations.DisplayMode.class);
            switch (modeMeta.value()) {
                case NORMAL -> context.metadata(UIDisplayMode.NORMAL);
                case COMPACT -> context.metadata(UIDisplayMode.COMPACT);
                case HIDDEN -> context.metadata(UIDisplayMode.HIDDEN);
            }
        }


        if (field.isAnnotationPresent(CodecAnnotations.EditorPreview.class)) {
            CodecAnnotations.EditorPreview previewMeta = field.getAnnotation(CodecAnnotations.EditorPreview.class);
            context.metadata(new UIEditorPreview(previewMeta.value()));
        }

        if (field.isAnnotationPresent(CodecAnnotations.SectionStart.class)) {
            CodecAnnotations.SectionStart sectionMeta = field.getAnnotation(CodecAnnotations.SectionStart.class);
            context.metadata(new UIEditorSectionStart(sectionMeta.value()));
        }

        // Validator
        if (field.isAnnotationPresent(CodecAnnotations.Min.class)) {
            int minVal = field.getAnnotation(CodecAnnotations.Min.class).value();
            context.addValidator((Validator<Object>) (Validator<?>) Validators.min(minVal));
        }

        if (field.isAnnotationPresent(CodecAnnotations.Max.class)) {
            int maxVal = field.getAnnotation(CodecAnnotations.Max.class).value();
            context.addValidator((Validator<Object>) (Validator<?>) Validators.max(maxVal));
        }

        if (field.isAnnotationPresent(CodecAnnotations.UniqueArray.class)) {
            context.addValidator((Validator<Object>) (Validator<?>) Validators.uniqueInArray());
        }

        if (field.isAnnotationPresent(CodecAnnotations.NonNull.class)) {
            context.addValidator(Validators.nonNull());
        }

        if (field.isAnnotationPresent(CodecAnnotations.NonEmpty.class)) {
            context.addValidator((Validator<Object>) (Validator<?>) Validators.nonEmptyString());
        }

        if (field.isAnnotationPresent(CodecAnnotations.CustomValidator.class)) {
            CodecAnnotations.CustomValidator validatorMeta = field.getAnnotation(CodecAnnotations.CustomValidator.class);
            try {
                Validator<?> dynamicValidator = new CommonAssetValidator(validatorMeta.type(), validatorMeta.value());
                context.addValidator((Validator<Object>) dynamicValidator);
            } catch (Exception e) {
                throw new RuntimeException("Failed to dynamically create CommonAssetValidator for path: " + validatorMeta.value(), e);
            }
        }

        if (field.isAnnotationPresent(CodecAnnotations.ValidateAssetKey.class)) {
            CodecAnnotations.ValidateAssetKey assetKeyMeta = field.getAnnotation(CodecAnnotations.ValidateAssetKey.class);

            try {
                Class<?> targetClass = assetKeyMeta.target();
                Field cacheField = targetClass.getDeclaredField(assetKeyMeta.fieldName());
                cacheField.setAccessible(true);

                Object cacheObj = cacheField.get(null);

                if (cacheObj instanceof ValidatorCache<?> validatorCache) {
                    Validator<?> validator = switch (assetKeyMeta.type()) {
                        case MAP_KEY -> validatorCache.getMapKeyValidator();
                        case MAP_VALUE -> validatorCache.getMapValueValidator();
                        case VALUE -> validatorCache.getValidator();
                    };

                    context.addValidator((Validator<Object>) validator);
                }
            } catch (Exception e) {
                throw new RuntimeException("Failed to attach AssetKeyValidator from " + assetKeyMeta.target().getName(), e);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static Codec<?> resolveCodecForType(Class<?> type) {
        Codec<?> primitiveCodec = PRIMITIVE_CODECS.get(type);
        if (primitiveCodec != null) return primitiveCodec;

        // Custom Registered Codecs
        if (REGISTRY.containsKey(type)) {
            return REGISTRY.get(type);
        }

        // Auto-detect Enums
        if (type.isEnum()) {
            return new EnumCodec<>((Class<? extends Enum>) type);
        }

        // Auto-detect Objects mapping Enum Arrays
        if (type.isArray() && type.getComponentType().isEnum()) {
            Class<? extends Enum> enumClass = (Class<? extends Enum>) type.getComponentType();
            return new ArrayCodec<>(new EnumCodec<>(enumClass), size -> (Enum[]) java.lang.reflect.Array.newInstance(enumClass, size));
        }

        // Fallback for custom objects
        if (type.isArray()) {
            Class<?> componentType = type.getComponentType();
            if (REGISTRY.containsKey(componentType)) {
                Codec<Object> elementCodec = (Codec<Object>) REGISTRY.get(componentType);
                return new ArrayCodec<>(
                        elementCodec,
                        size -> (Object[]) java.lang.reflect.Array.newInstance(componentType, size)
                );
            }
        }

        throw new IllegalArgumentException("Registry missing mapped conversion format for Type: " + type.getName());
    }

    private static List<Field> getDeclaredFieldsUpToSuper(Class<?> startClass) {
        List<Field> allFields = new ArrayList<>();
        Class<?> currentClass = startClass;
        while (currentClass != null && currentClass != Object.class) {
            for (Field field : currentClass.getDeclaredFields()) {
                if (!Modifier.isStatic(field.getModifiers())) {
                    allFields.add(field);
                }
            }
            currentClass = currentClass.getSuperclass();
        }
        return allFields;
    }

    private static MethodHandle unreflectGetter(Field field) {
        try {
            field.setAccessible(true);
            return MethodHandles.lookup().unreflectGetter(field);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to create getter handle for field: " + field.getName(), e);
        }
    }

    private static MethodHandle unreflectSetter(Field field) {
        try {
            field.setAccessible(true);
            return MethodHandles.lookup().unreflectSetter(field);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to create setter handle for field: " + field.getName(), e);
        }
    }

    private static Object invokeGetter(MethodHandle handle, Object instance) {
        try {
            return handle.invoke(instance);
        } catch (Throwable e) {
            throw new RuntimeException("Failed invoking getter for instance: " + instance, e);
        }
    }

    private static void invokeSetter(MethodHandle handle, Object instance, Object value) {
        try {
            handle.invoke(instance, value);
        } catch (Throwable e) {
            throw new RuntimeException("Failed setting value: " + value + " on instance: " + instance, e);
        }
    }
}