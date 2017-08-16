package com.gatech.asacs;

/**
 * Created by alexgreco on 4/2/17.
 */
public class WaitlistObject {
    private int parent_shelter;
    private int client_id;

    public int getParent_shelter() {
        return parent_shelter;
    }

    public void setParent_shelter(int parent_shelter) {
        this.parent_shelter = parent_shelter;
    }

    public int getClient_id() {
        return client_id;
    }

    public void setClient_id(int client_id) {
        this.client_id = client_id;
    }

    @Override
    public String toString() {
        return "WaitlistObject{" +
                "parent_shelter=" + parent_shelter +
                ", client_id=" + client_id +
                '}';
    }
}
