package lld.logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.IntStream;

public class LoggerDemo {

    public static void main(String[] args) throws InterruptedException {
//        for (LogLevel logLevel: LogLevel.values()) {
//            System.out.println(logLevel.ordinal() + " " + logLevel);
//        }

        Appender appender = new ConsoleAppender();

        LogManager logManager = LogManager.getInstance();
        Logger logger1 = logManager.getLogger("Test1");
        logger1.addAppender(appender);

        Logger logger2 = logManager.getLogger("Test2");
        logger2.addAppender(appender);

        Logger logger3 = logManager.getLogger("Test3");
        logger3.addAppender(appender);

        var loggers = List.of(logger1, logger2, logger3);

        loggers.parallelStream().forEach(logger -> {
            IntStream.rangeClosed(1, 10000).boxed().forEach(integer ->
                    logger.info("Tests: " + integer));
        });


        Thread.sleep(10000L);
    }
}

enum LogLevel {
    DEBUG, INFO, WARN, ERROR, FATAL
}

interface Appender {
    void append(String message);
}

class ConsoleAppender implements Appender {
    private final BlockingQueue<String> bufferQueue;
    private final ExecutorService consolerPrinter;

    public ConsoleAppender() {
        this.bufferQueue = new LinkedBlockingQueue<>();
        this.consolerPrinter = Executors.newWorkStealingPool();
        this.consolerPrinter.submit(() -> {
            while (true) {
                List<String> drainedLogs = new ArrayList<>();
                bufferQueue.drainTo(drainedLogs, 10);
//                    message = bufferQueue.take();
                drainedLogs.forEach(System.out::println);

            }
        });
    }

    @Override
    public void append(String message) {
        bufferQueue.offer(message);
    }

}

class Logger {
    private final String name;
    private LogLevel logLevel;
    private List<Appender> appenders;

    public Logger(String name, LogLevel logLevel) {
        this.name = name;
        this.logLevel = logLevel;
        this.appenders = new ArrayList<>();
    }

    private void log(String message, LogLevel logLevel) {
        if (this.logLevel.ordinal() <= logLevel.ordinal()) {
            appenders
                    .parallelStream()
                    .forEach(appender -> appender.append(formatMessage(message, logLevel)));
        }
    }

    private String formatMessage(String message, LogLevel logLevel) {
        return String.format(
                "[%s] [%s] [%s] [%s] %s",
                new Date(), logLevel, name, Thread.currentThread().getName(), message
        );
    }

    public void info(String message) {
        log(message, LogLevel.INFO);
    }

    public void debug(String message) {
        log(message, LogLevel.DEBUG);
    }

    public void error(String message) {
        log(message, LogLevel.ERROR);
    }

    public void warn(String message) {
        log(message, LogLevel.WARN);
    }

    public void fatal(String message) {
        log(message, LogLevel.FATAL);
    }

    public void addAppender(Appender appender) {
        this.appenders.add(appender);
    }
}

class LogManager {
    private static volatile LogManager INSTANCE;
    private final Map<String, Logger> loggers;

    private LogManager() {
        this.loggers = new ConcurrentHashMap<>();
    }

    public static LogManager getInstance() {
        if (null == INSTANCE) {
            synchronized (LogManager.class) {
                if (null == INSTANCE) {
                    INSTANCE = new LogManager();
                }
            }
        }
        return INSTANCE;
    }

    public Logger getLogger(String name) {
        return loggers.computeIfAbsent(name, key -> new Logger(name, LogLevel.INFO));
    }
}

class ConfigLoader {

}
