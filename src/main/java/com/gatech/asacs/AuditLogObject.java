package com.gatech.asacs;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * Created by Samuel on 4/15/17.
 */
public class AuditLogObject {

    private int tracking_client;
    private String change_description;
    private Timestamp date_time;

    public int getTracking_client() {
        return tracking_client;
    }

    public void setTracking_client(int tracking_client) {
        this.tracking_client = tracking_client;
    }

    public Timestamp getDate_time() {
        return date_time;
    }

    public void setDate_time(Timestamp date_time) {
        this.date_time = date_time;
    }

    public String getChange_description() {
        return change_description;
    }

    public void setChange_description(String change_description) {
        this.change_description = change_description;
    }

}
