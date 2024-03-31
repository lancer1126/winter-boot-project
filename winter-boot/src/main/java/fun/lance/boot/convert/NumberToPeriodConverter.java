package fun.lance.boot.convert;

import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.GenericConverter;

import java.time.Period;
import java.util.Collections;
import java.util.Set;

final class NumberToPeriodConverter implements GenericConverter {

    private final StringToPeriodConverter delegate = new StringToPeriodConverter();

    @Override
    public Set<ConvertiblePair> getConvertibleTypes() {
        return Collections.singleton(new ConvertiblePair(Number.class, Period.class));
    }

    @Override
    public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        return this.delegate.convert((source != null) ? source.toString() : null, TypeDescriptor.valueOf(String.class),
                targetType);
    }
}
