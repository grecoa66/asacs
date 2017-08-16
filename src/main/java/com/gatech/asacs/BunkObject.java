package com.gatech.asacs;

/**
 * Created by alexgreco on 4/2/17.
 */
public class BunkObject {
    private int bunk_id;
    private int parent_shelter;
    private int occupying_client;
    private Boolean occupied;
    private String bunk_type;

    public int getBunk_id() {
        return bunk_id;
    }

    public void setBunk_id(int bunk_id) {
        this.bunk_id = bunk_id;
    }

    public int getParent_shelter() {
        return parent_shelter;
    }

    public void setParent_shelter(int parent_shelter) {
        this.parent_shelter = parent_shelter;
    }

    public int getOccupying_client() {
        return occupying_client;
    }

    public void setOccupying_client(int occupying_client) {
        this.occupying_client = occupying_client;
    }

    public Boolean isOccupied() {
        return occupied;
    }

    public void setOccupied(Boolean occupied) {
        this.occupied = occupied;
    }

    public String getBunk_type() {
        return bunk_type;
    }

    public void setBunk_type(String bunk_type) {
        this.bunk_type = bunk_type;
    }

    @Override
    public String toString() {
        return "BunkObject{" +
                "bunk_id=" + bunk_id +
                ", parent_shelter=" + parent_shelter +
                ", occupying_client=" + occupying_client +
                ", occupied=" + occupied +
                ", bunk_type='" + bunk_type + '\'' +
                '}';
    }
}
