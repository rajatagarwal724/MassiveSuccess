package lld.logging;

import java.util.ArrayList;
import java.util.List;

public class LogManager {
    private LogLevel currentLevel;
    private final List<Logger> loggers;

    public LogManager(LogLevel currentLevel) {
        this.currentLevel = currentLevel;
        this.loggers = new ArrayList<>();
    }

    public void log(LogLevel level, String message) {
        if (level.ordinal() < currentLevel.ordinal()) {
            return;
        }

        LogMessage logMessage = new LogMessage(level, message);
        loggers.forEach(logger -> logger.log(logMessage));
    }

    public void setCurrentLevel(LogLevel logLevel) {
        this.currentLevel = logLevel;
    }

    public void addLogger(Logger logger) {
        loggers.add(logger);
    }
}
