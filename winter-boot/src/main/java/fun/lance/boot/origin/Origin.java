package fun.lance.boot.origin;

public interface Origin {
    static Origin from(Object source) {
        if (source instanceof Origin origin) {
            return origin;
        }
        Origin origin = null;
        if (source instanceof OriginProvider originProvider) {
            origin = originProvider.getOrigin();
        }
        if (origin == null && source instanceof Throwable throwable) {
            return from(throwable.getCause());
        }
        return origin;
    }
}
