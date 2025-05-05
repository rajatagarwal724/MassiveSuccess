package lld.logging;

public class Main {

    public static void main(String[] args) {
        LogManager manager = new LogManager(LogLevel.INFO);
        manager.addLogger(new ConsoleLogger(new AdvancedFormatter()));

        manager.log(LogLevel.INFO, "Log an Info Level Message");
        manager.log(LogLevel.DEBUG, "Log an Error Level Message");
        manager.log(LogLevel.WARNING, "Log an Warning Level Message");
        manager.log(LogLevel.ERROR, "Log an Error Level Message");
    }
}
