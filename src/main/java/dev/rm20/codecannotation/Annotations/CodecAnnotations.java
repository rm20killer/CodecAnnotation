package dev.rm20.codecannotation.Annotations;

import java.lang.annotation.*;

public final class CodecAnnotations {
    private CodecAnnotations() {}

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Field {
        String value() default "";
        String doc() default "";
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Min {
        int value();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Max {
        int value();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface UniqueArray {}

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface NonNull {}

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface NonEmpty {}

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface CustomValidator {
        String type() default "json";
        String value();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface ValidateAssetKey {

        /**
         * The class that holds the static VALIDATOR_CACHE field (e.g., DamageCause.class or ElementAsset.class).
         */
        Class<?> target();

        /**
         * The field name on the target class. Defaults to "VALIDATOR_CACHE".
         */
        String fieldName() default "VALIDATOR_CACHE";

        /**
         * Target target type for maps.
         */
        TargetType type() default TargetType.VALUE;

        enum TargetType {
            VALUE,
            MAP_KEY,
            MAP_VALUE
        }
    }

}