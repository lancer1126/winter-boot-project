package fun.lance.boot.convert;

import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.GenericConverter;
import org.springframework.util.ObjectUtils;

import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Set;

final class StringToPeriodConverter implements GenericConverter {

    @Override
    public Set<ConvertiblePair> getConvertibleTypes() {
        return Collections.singleton(new GenericConverter.ConvertiblePair(String.class, Period.class));
    }

    @Override
    public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        if (ObjectUtils.isEmpty(source)) {
            return null;
        }
        return convert(source.toString(), getStyle(targetType), getPeriodUnit(targetType));
    }

    private PeriodStyle getStyle(TypeDescriptor targetType) {
        PeriodFormat annotation = targetType.getAnnotation(PeriodFormat.class);
        return (annotation != null) ? annotation.value() : null;
    }

    private ChronoUnit getPeriodUnit(TypeDescriptor targetType) {
        PeriodUnit annotation = targetType.getAnnotation(PeriodUnit.class);
        return (annotation != null) ? annotation.value() : null;
    }

    private Period convert(String source, PeriodStyle style, ChronoUnit unit) {
        style = (style != null) ? style : PeriodStyle.detect(source);
        return style.parse(source, unit);
    }
}
