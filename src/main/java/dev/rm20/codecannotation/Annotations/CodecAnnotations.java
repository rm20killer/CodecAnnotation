package dev.rm20.codecannotation.Annotations;

import com.hypixel.hytale.codec.schema.metadata.ui.UIEditorPreview;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

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

    /**
     * Hides the field from the Hytale Asset Editor UI.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface DisplayMode {
        Mode value();

        enum Mode {
            NORMAL,
            COMPACT,
            HIDDEN
        }
    }

    /**
     * Overrides the display name of the property in the Asset Editor.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface PropertyTitle {
        String value();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface EditorPreview {
        UIEditorPreview.PreviewType value();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface SectionStart {
        String value();
    }

}