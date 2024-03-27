package fun.lance;

import org.springframework.core.env.Environment;

import java.io.PrintStream;

public interface Banner {
    void printBanner(Environment environment, Class<?> sourceClass, PrintStream out);

    public static enum Mode {
        OFF,
        CONSOLE,
        LOG;

        private Mode() {}
    }
}
