package lld.logging;

public class AdvancedFormatter implements Formatter {
    @Override
    public String format(LogMessage message) {
        return String.format("[%s] [Thread: %s] [%s]: %s",
                message.getTimestamp(),
                Thread.currentThread().getName(),
                message.getLevel(),
                message.getMessage()
        );
    }
}
