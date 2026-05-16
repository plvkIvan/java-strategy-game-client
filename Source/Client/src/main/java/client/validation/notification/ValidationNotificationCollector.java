package client.validation.notification;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ValidationNotificationCollector {
    private final List<ValidationNotification> notifications = new ArrayList<>();

    public void addError(String ruleName, String message) {
        notifications.add(new ValidationNotification(
                ValidationSeverity.ERROR,
                ruleName,
                message
        ));
    }

    public void addWarning(String ruleName, String message) {
        notifications.add(new ValidationNotification(
                ValidationSeverity.WARNING,
                ruleName,
                message
        ));
    }

    public void addInfo(String ruleName, String message) {
        notifications.add(new ValidationNotification(
                ValidationSeverity.INFO,
                ruleName,
                message
        ));
    }

    public boolean hasErrors() {
        for (ValidationNotification notification : notifications) {
            if (notification.getSeverity() == ValidationSeverity.ERROR) {
                return true;
            }
        }

        return false;
    }

    public List<ValidationNotification> getNotifications() {
        return Collections.unmodifiableList(notifications);
    }

    public void printErrorsToSystemErr() {
        for (ValidationNotification notification : notifications) {
            if (notification.getSeverity() == ValidationSeverity.ERROR) {
                System.err.println(notification);
            }
        }
    }
}