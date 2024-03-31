package fun.lance.boot.convert;

import org.springframework.format.Formatter;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.Locale;

public class InetAddressFormatter implements Formatter<InetAddress> {
    @Override
    public InetAddress parse(String text, Locale locale) throws ParseException {
        try {
            return InetAddress.getByName(text);
        }
        catch (UnknownHostException ex) {
            throw new IllegalStateException("Unknown host " + text, ex);
        }
    }

    @Override
    public String print(InetAddress object, Locale locale) {
        return object.getHostAddress();
    }
}
