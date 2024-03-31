package fun.lance.boot.convert;

import org.springframework.core.convert.converter.Converter;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;

class StringToFileConverter implements Converter<String, File> {

    private static final ResourceLoader resourceLoader = new DefaultResourceLoader(null);

    @Override
    public File convert(String source) {
        if (ResourceUtils.isUrl(source)) {
            return getFile(resourceLoader.getResource(source));
        }
        File file = new File(source);
        if (file.exists()) {
            return file;
        }
        Resource resource = resourceLoader.getResource(source);
        if (resource.exists()) {
            return getFile(resource);
        }
        return file;
    }

    private File getFile(Resource resource) {
        try {
            return resource.getFile();
        }
        catch (IOException ex) {
            throw new IllegalStateException("Could not retrieve file for " + resource + ": " + ex.getMessage());
        }
    }
}
