package fun.lance.boot.convert;

import java.lang.annotation.*;
import java.time.Period;

@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PeriodFormat {

    /**
     * The {@link Period} format style.
     * @return the period format style.
     */
    PeriodStyle value();

}
