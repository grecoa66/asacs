package com.gatech.asacs;

import java.sql.Date;

/**
 * Created by alexgreco on 4/2/17.
 */

public class ServicesUsedObject {
    private int tracking_client;
    private String description;
    private Date date_time;
    private int site_number;

    public int getTracking_client() {
        return tracking_client;
    }

    public void setTracking_client(int tracking_client) {
        this.tracking_client = tracking_client;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDate_time() {
        return date_time;
    }

    public void setDate_time(Date date_time) {
        this.date_time = date_time;
    }

    public int getSite_number() {
        return site_number;
    }

    public void setSite_number(int site_number) {
        this.site_number = site_number;
    }

    @Override
    public String toString() {
        return "ServicesUsedObject{" +
                "tracking_client=" + tracking_client +
                ", description='" + description + '\'' +
                ", date_time=" + date_time +
                ", site_number=" + site_number +
                '}';
    }
}
