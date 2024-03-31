package fun.lance.boot.convert;

import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConverterRegistry;
import org.springframework.core.convert.converter.GenericConverter;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.util.StringValueResolver;

import java.util.Set;

/**
 * ApplicationConversionService 自动注册了以下类型的转换器和格式化器：
 * 标准的Spring MVC转换器，例如将字符串转换为数字、日期等。
 * Jackson ObjectMapper 转换器，用于处理JSON数据绑定。
 * Joda-Time和Java 8日期和时间转换器（如果相应的库在类路径上）。
 * 货币转换器。
 * 邮件转换器，例如将字符串转换为InternetAddress。
 * 集合转换器，用于集合和数组之间的转换。 -等等。
 */
public class ApplicationConversionService extends FormattingConversionService {

    private static volatile ApplicationConversionService sharedInstance;

    private final boolean unmodifiable;

    public ApplicationConversionService() {
        this(null);
    }

    public ApplicationConversionService(StringValueResolver embeddedValueResolver) {
        this(embeddedValueResolver, false);
    }

    public ApplicationConversionService(StringValueResolver stringValueResolver, boolean unmodifiable) {
        if (stringValueResolver != null) {
            setEmbeddedValueResolver(stringValueResolver);
        }
        configure(this);
        this.unmodifiable = unmodifiable;
    }

    public boolean isConvertViaObjectSourceType(TypeDescriptor sourceType, TypeDescriptor targetType) {
        GenericConverter converter = getConverter(sourceType, targetType);
        Set<GenericConverter.ConvertiblePair> pairs = (converter != null) ? converter.getConvertibleTypes() : null;
        if (pairs != null) {
            for (GenericConverter.ConvertiblePair pair : pairs) {
                if (Object.class.equals(pair.getSourceType())) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void configure(FormatterRegistry registry) {
        DefaultConversionService.addDefaultConverters(registry);
        DefaultFormattingConversionService.addDefaultFormatters(registry);
        addApplicationFormatters(registry);
        addApplicationConverters(registry);
    }

    public static void addApplicationFormatters(FormatterRegistry registry) {
        registry.addFormatter(new CharArrayFormatter());
        registry.addFormatter(new InetAddressFormatter());
        registry.addFormatter(new IsoOffsetFormatter());
    }

    public static void addApplicationConverters(FormatterRegistry registry) {
        addDelimitedStringConverters(registry);
        registry.addConverter(new StringToDurationConverter());
        registry.addConverter(new DurationToStringConverter());
        registry.addConverter(new NumberToDurationConverter());
        registry.addConverter(new DurationToNumberConverter());
        registry.addConverter(new StringToPeriodConverter());
        registry.addConverter(new PeriodToStringConverter());
        registry.addConverter(new NumberToPeriodConverter());
        registry.addConverter(new StringToDataSizeConverter());
        registry.addConverter(new NumberToDataSizeConverter());
        registry.addConverter(new StringToFileConverter());
        registry.addConverter(new InputStreamSourceToByteArrayConverter());
        registry.addConverterFactory(new LenientStringToEnumConverterFactory());
        registry.addConverterFactory(new LenientBooleanToEnumConverterFactory());
        if (registry instanceof ConversionService conversionService) {
            addApplicationConverters(registry, conversionService);
        }
    }

    private static void addApplicationConverters(ConverterRegistry registry, ConversionService conversionService) {
        registry.addConverter(new CharSequenceToObjectConverter(conversionService));
    }

    public static void addDelimitedStringConverters(ConverterRegistry registry) {
        ConversionService service = (ConversionService) registry;
        registry.addConverter(new ArrayToDelimitedStringConverter(service));
        registry.addConverter(new CollectionToDelimitedStringConverter(service));
        registry.addConverter(new DelimitedStringToArrayConverter(service));
        registry.addConverter(new DelimitedStringToCollectionConverter(service));
    }
}
