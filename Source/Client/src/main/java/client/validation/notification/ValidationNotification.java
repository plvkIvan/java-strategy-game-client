package client.validation.notification;

public class ValidationNotification {
    private final ValidationSeverity severity;
    private final String ruleName;
    private final String message;

    public ValidationNotification(ValidationSeverity severity, String ruleName, String message) {
        this.severity = severity;
        this.ruleName = ruleName;
        this.message = message;
    }

    public ValidationSeverity getSeverity() {
        return severity;
    }

    public String getRuleName() {
        return ruleName;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "[" + severity + "] " + ruleName + ": " + message;
    }
}