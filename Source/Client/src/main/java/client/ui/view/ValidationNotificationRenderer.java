package client.ui.view;

import client.validation.notification.ValidationNotification;
import client.validation.notification.ValidationNotificationCollector;
import client.validation.notification.ValidationSeverity;

public class ValidationNotificationRenderer {
    private static final String VALIDATION_RULE_PACKAGE = "client.validation.rules.";
    private static final String VALIDATION_METHOD_NAME = "validate";

    public void renderErrors(ValidationNotificationCollector notifications) {
        for (ValidationNotification notification : notifications.getNotifications()) {
            if (notification.getSeverity() == ValidationSeverity.ERROR) {
                System.err.println(formatError(notification));
            }
        }
    }

    private String formatError(ValidationNotification notification) {
        return notification
                + " | Validation logic: "
                + resolveValidationLogicReference(notification.getRuleName());
    }

    private String resolveValidationLogicReference(String ruleName) {
        String className = extractClassName(ruleName);
        return VALIDATION_RULE_PACKAGE + className + "#" + VALIDATION_METHOD_NAME;
    }

    private String extractClassName(String ruleName) {
        int suffixStart = ruleName.indexOf('_');

        if (suffixStart < 0) {
            return ruleName;
        }

        return ruleName.substring(0, suffixStart);
    }
}