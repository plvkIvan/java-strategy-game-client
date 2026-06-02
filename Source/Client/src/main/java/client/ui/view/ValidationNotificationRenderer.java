package client.ui.view;

import client.validation.notification.ValidationNotification;
import client.validation.notification.ValidationNotificationCollector;
import client.validation.notification.ValidationSeverity;

public class ValidationNotificationRenderer {

    public void renderErrors(ValidationNotificationCollector notifications) {
        for (ValidationNotification notification : notifications.getNotifications()) {
            if (notification.getSeverity() == ValidationSeverity.ERROR) {
                System.err.println(notification);
            }
        }
    }
}