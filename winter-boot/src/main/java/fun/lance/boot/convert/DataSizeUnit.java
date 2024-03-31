package fun.lance.boot.convert;

import org.springframework.util.unit.DataUnit;

import java.lang.annotation.*;

@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataSizeUnit {

    /**
     * The {@link DataUnit} to use if one is not specified.
     * @return the data unit
     */
    DataUnit value();

}
