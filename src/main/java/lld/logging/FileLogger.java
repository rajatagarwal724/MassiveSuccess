package lld.logging;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileLogger extends Logger {
    private final String filePath;

    public FileLogger(Formatter formatter, String filePath) {
        super(formatter);
        this.filePath = filePath;
    }

    @Override
    void log(LogMessage message) {
        try(FileWriter fileWriter = new FileWriter(filePath, true)) {
            fileWriter.write(formatMessage(message));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
