package com.gatech.asacs;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by shiemstra3 on 3/28/17.
 */
@XmlRootElement
public class RequestObject {
    private int request_id;
    private String requesting_user;
    private String approving_user;
    private int fulfilling_foodbank;
    private int requested_item_id;
    private int requested_units;
    private int fulfilled_units;
    private String request_status;
    private int fill_units;
    private int available_units;

    //Getter Setter functions

    public int getRequest_id() {
        return request_id;
    }

    public void setRequest_id(int request_id) {
        this.request_id = request_id;
    }

    public String getRequesting_user() {
        return requesting_user;
    }

    public void setRequesting_user(String requesting_user) {
        this.requesting_user = requesting_user;
    }

    public String getApproving_user() {
        return approving_user;
    }

    public void setApproving_user(String approving_user) {
        this.approving_user = approving_user;
    }

    public int getFulfilling_foodbank() {
        return fulfilling_foodbank;
    }

    public void setFulfilling_foodbank(int fulfilling_foodbank) { this.fulfilling_foodbank = fulfilling_foodbank; }

    public int getRequested_item_id() { return requested_item_id; }

    public void setRequested_item_id(int requested_item_id) { this.requested_item_id = requested_item_id; }

    public int getRequested_units() { return requested_units; }

    public void setRequested_units(int requested_units) { this.requested_units = requested_units; }

    public String getRequest_status() { return request_status; }

    public void setRequest_status(String request_status) { this.request_status = request_status; }

    public int getFulfilled_units() { return fulfilled_units; }

    public void setFulfilled_units(int fulfilled_units) { this.fulfilled_units = fulfilled_units; }

    public int getFill_units() { return fill_units; }

    public void setFill_units(int fill_units) { this.fill_units = fill_units; }

    public int getAvailable_units() { return available_units; }

    public void setAvailable_units(int available_units) { this.available_units = available_units; }

    //to String for JSON
    @Override
    public String toString() {
        return "RequestObject{" +
                "request_id=" + request_id +
                ", requesting_user='" + requesting_user + '\'' +
                ", approving_user='" + approving_user + '\'' +
                ", fulfilling_foodbank=" + fulfilling_foodbank +
                ", requested_item_id=" + requested_item_id +
                ", requested_units=" + requested_units +
                ", fulfilled_units=" + fulfilled_units +
                ", request_status='" + request_status + '\'' +
                ", fill_units=" + fill_units +
                ", available_units=" + available_units +
                '}';
    }
}
