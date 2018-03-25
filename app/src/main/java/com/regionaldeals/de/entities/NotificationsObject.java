package com.regionaldeals.de.entities;

import java.io.Serializable;

/**
 * Created by Umi on 10.03.2018.
 */

public class NotificationsObject implements Serializable {
    private int notificationType;
    private String notificationDetails;
    private GutscheineObject gutscheineObject;
    private long notificationDate = 0;
    private String  notificationText1;
    private String  notificationText2;

    public String getNotificationText2() {
        return notificationText2;
    }

    public void setNotificationText2(String notificationText2) {
        this.notificationText2 = notificationText2;
    }

    public long getNotificationDate() {
        return notificationDate;
    }

    public void setNotificationDate(long notificationDate) {
        this.notificationDate = notificationDate;
    }

    public String getNotificationText1() {
        return notificationText1;
    }

    public void setNotificationText1(String notificationText1) {
        this.notificationText1 = notificationText1;
    }

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
