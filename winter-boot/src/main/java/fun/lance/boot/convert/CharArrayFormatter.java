package fun.lance.boot.convert;

import org.springframework.format.Formatter;

import java.text.ParseException;
import java.util.Locale;

final class CharArrayFormatter implements Formatter<char[]> {

    @Override
    public char[] parse(String text, Locale locale) throws ParseException {
        return text.toCharArray();
    }

    @Override
    public String print(char[] object, Locale locale) {
        return new String(object);
    }
}
