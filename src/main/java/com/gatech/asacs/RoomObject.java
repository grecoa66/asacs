package com.gatech.asacs;

/**
 * Created by alexgreco on 4/1/17.
 */
public class RoomObject {

    private int room_id;
    private int parent_shelter;
    private int occupying_client;
    private boolean occupied;

    public int getRoom_id() {
        return room_id;
    }

    public void setRoom_id(int room_id) {
        this.room_id = room_id;
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

    public boolean isOccupied() {
        return occupied;
    }

    public void setOccupied(boolean occupied) {
        this.occupied = occupied;
    }

    @Override
    public String toString() {
        return "RoomObject{" +
                "room_id=" + room_id +
                ", parent_shelter=" + parent_shelter +
                ", occupying_client=" + occupying_client +
                ", occupied=" + occupied +
                '}';
    }
}
