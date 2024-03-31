package fun.lance.boot.convert;

import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalGenericConverter;

import java.util.Collections;
import java.util.Set;

final class CharSequenceToObjectConverter implements ConditionalGenericConverter {

    private static final TypeDescriptor STRING = TypeDescriptor.valueOf(String.class);

    private static final TypeDescriptor BYTE_ARRAY = TypeDescriptor.valueOf(byte[].class);

    private static final Set<ConvertiblePair> TYPES;

    private final ThreadLocal<Boolean> disable = new ThreadLocal<>();

    static {
        TYPES = Collections.singleton(new ConvertiblePair(CharSequence.class, Object.class));
    }

    private final ConversionService conversionService;

    CharSequenceToObjectConverter(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    @Override
    public Set<ConvertiblePair> getConvertibleTypes() {
        return TYPES;
    }

    @Override
    public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
        if (sourceType.getType() == String.class || this.disable.get() == Boolean.TRUE) {
            return false;
        }
        this.disable.set(Boolean.TRUE);
        try {
            boolean canDirectlyConvertCharSequence = this.conversionService.canConvert(sourceType, targetType);
            if (canDirectlyConvertCharSequence && !isStringConversionBetter(sourceType, targetType)) {
                return false;
            }
            return this.conversionService.canConvert(STRING, targetType);
        }
        finally {
            this.disable.remove();
        }
    }

    /**
     * Return if String based conversion is better based on the target type. This is
     * required when ObjectTo... conversion produces incorrect results.
     * @param sourceType the source type to test
     * @param targetType the target type to test
     * @return if string conversion is better
     */
    private boolean isStringConversionBetter(TypeDescriptor sourceType, TypeDescriptor targetType) {
        if (this.conversionService instanceof ApplicationConversionService applicationConversionService) {
            if (applicationConversionService.isConvertViaObjectSourceType(sourceType, targetType)) {
                // If an ObjectTo... converter is being used then there might be a better
                // StringTo... version
                return true;
            }
        }
        if ((targetType.isArray() || targetType.isCollection()) && !targetType.equals(BYTE_ARRAY)) {
            // StringToArrayConverter / StringToCollectionConverter are better than
            // ObjectToArrayConverter / ObjectToCollectionConverter
            return true;
        }
        return false;
    }

    @Override
    public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        return this.conversionService.convert(source.toString(), STRING, targetType);
    }
}
