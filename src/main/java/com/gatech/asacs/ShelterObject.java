package com.gatech.asacs;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by alexgreco on 3/25/17.
 */
@XmlRootElement
public class ShelterObject {
    private int parent_site;
    private String description;
    private String hours;
    private String shelter_condition;
    private int rooms;
    private int bunks;

    public int getBunks() { return bunks; }

    public void setBunks(int bunks) { this.bunks = bunks; }

    public int getRooms() { return rooms; }

    public void setRooms(int rooms) { this.rooms = rooms; }

    public int getParent_site() {
        return parent_site;
    }

    public void setParent_site(int parent_site) {
        this.parent_site = parent_site;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getHours() {
        return hours;
    }

    public void setHours(String hours) {
        this.hours = hours;
    }

    public String getShelter_condition() {
        return shelter_condition;
    }

    public void setShelter_condition(String shelter_condition) {
        this.shelter_condition = shelter_condition;
    }

    @Override
    public String toString() {
        return "ShelterObject{" +
                "parent_site=" + parent_site +
                ", description='" + description + '\'' +
                ", hours='" + hours + '\'' +
                ", shelter_condition='" + shelter_condition + '\'' +
                ", rooms=" + rooms +
                ", bunks=" + bunks +
                '}';
    }
}
