package fun.lance.boot.convert;

import fun.lance.boot.origin.Origin;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.io.InputStreamSource;
import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;

class InputStreamSourceToByteArrayConverter implements Converter<InputStreamSource, byte[]> {

    @Override
    public byte[] convert(InputStreamSource source) {
        try {
            return FileCopyUtils.copyToByteArray(source.getInputStream());
        }
        catch (IOException ex) {
            throw new IllegalStateException("Unable to read from " + getName(source), ex);
        }
    }

    private String getName(InputStreamSource source) {
        Origin origin = Origin.from(source);
        if (origin != null) {
            return origin.toString();
        }
        if (source instanceof Resource resource) {
            return resource.getDescription();
        }
        return "input stream source";
    }
}
