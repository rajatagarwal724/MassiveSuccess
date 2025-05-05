package lld.logging;

public class SimpleFormatter implements Formatter {
    @Override
    public String format(LogMessage message) {
        return String.format("[%s] [%s]: %s",
                    message.getTimestamp(),
                    message.getLevel(),
                    message.getMessage()
                );
    }
}
