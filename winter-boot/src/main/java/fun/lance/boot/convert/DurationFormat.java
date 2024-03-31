package fun.lance.boot.convert;

import java.lang.annotation.*;

@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DurationFormat {

    /**
     * The duration format style.
     * @return the duration format style.
     */
    DurationStyle value();

}
