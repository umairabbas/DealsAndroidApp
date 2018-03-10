package com.regionaldeals.de.entities;

import java.io.Serializable;

/**
 * Created by Umi on 10.03.2018.
 */

public class NotificationsObject implements Serializable {
    private int notificationType;
    private String notificationDetails;
    private GutscheineObject gutscheineObject;

    public int getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(int notificationType) {
        this.notificationType = notificationType;
    }

    public String getNotificationDetails() {
        return notificationDetails;
    }

    public void setNotificationDetails(String notificationDetails) {
        this.notificationDetails = notificationDetails;
    }

    public GutscheineObject getGutscheineObject() {
        return gutscheineObject;
    }

    public void setGutscheineObject(GutscheineObject gutscheineObject) {
        this.gutscheineObject = gutscheineObject;
    }
}
