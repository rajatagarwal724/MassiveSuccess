package lld.logging;

public class ConsoleLogger extends Logger {
    public ConsoleLogger(Formatter formatter) {
        super(formatter);
    }

    @Override
    void log(LogMessage message) {
        System.out.println(formatMessage(message));
    }
}
