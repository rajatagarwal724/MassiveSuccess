package lld.logging;

public abstract class Logger {
    private final Formatter formatter;

    public Logger(Formatter formatter) {
        this.formatter = formatter;
    }

    abstract void log(LogMessage message);

    protected String formatMessage(LogMessage message) {
        return formatter.format(message);
    }
}
