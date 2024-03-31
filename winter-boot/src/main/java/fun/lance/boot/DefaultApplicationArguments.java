package fun.lance.boot;

import org.springframework.core.env.SimpleCommandLinePropertySource;
import org.springframework.util.Assert;

import java.util.List;

public class DefaultApplicationArguments implements ApplicationArguments {

    private final Source source;
    private final String[] args;

    public DefaultApplicationArguments(String[] args) {
        Assert.notNull(args, "Args must not be null");
        this.source = new Source(args);
        this.args = args;
    }

    @Override
    public String[] getSourceArgs() {
        return this.args;
    }

    private static class Source extends SimpleCommandLinePropertySource {
        Source(String[] args) {super(args);}

        @Override
        public List<String> getNonOptionArgs() {
            return super.getNonOptionArgs();
        }

        @Override
        public List<String> getOptionValues(String name) {
            return super.getOptionValues(name);
        }
    }
}
