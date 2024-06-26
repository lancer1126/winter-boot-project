package fun.lance.boot.convert;

import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.GenericConverter;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Set;

final class DurationToStringConverter implements GenericConverter {

    @Override
    public Set<ConvertiblePair> getConvertibleTypes() {
        return Collections.singleton(new ConvertiblePair(Duration.class, String.class));
    }

    @Override
    public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        if (source == null) {
            return null;
        }
        return convert((Duration) source, getDurationStyle(sourceType), getDurationUnit(sourceType));
    }

    private ChronoUnit getDurationUnit(TypeDescriptor sourceType) {
        DurationUnit annotation = sourceType.getAnnotation(DurationUnit.class);
        return (annotation != null) ? annotation.value() : null;
    }

    private DurationStyle getDurationStyle(TypeDescriptor sourceType) {
        DurationFormat annotation = sourceType.getAnnotation(DurationFormat.class);
        return (annotation != null) ? annotation.value() : null;
    }

    private String convert(Duration source, DurationStyle style, ChronoUnit unit) {
        style = (style != null) ? style : DurationStyle.ISO8601;
        return style.print(source, unit);
    }
}
