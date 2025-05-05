package lld.logging;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LogMessage {
    private LogLevel level;
    private String message;
    private LocalDateTime timestamp;

    public LogMessage(LogLevel level, String message) {
        this.level = level;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }
}
