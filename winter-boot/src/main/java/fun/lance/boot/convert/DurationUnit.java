package fun.lance.boot.convert;

import java.lang.annotation.*;
import java.time.temporal.ChronoUnit;

@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DurationUnit {

    /**
     * The duration unit to use if one is not specified.
     * @return the duration unit
     */
    ChronoUnit value();

}
