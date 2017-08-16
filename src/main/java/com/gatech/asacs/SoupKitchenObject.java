package com.gatech.asacs;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by alexgreco on 3/25/17.
 */
@XmlRootElement
public class SoupKitchenObject {

    private int parent_site;
    private String description;
    private String hours;
    private String kitchen_condition;
    private int seating_capacity;

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

    public String getKitchen_condition() {
        return kitchen_condition;
    }

    public void setKitchen_condition(String kitchen_condition) {
        this.kitchen_condition = kitchen_condition;
    }

    public int getSeating_capacity() {
        return seating_capacity;
    }

    public void setSeating_capacity(int seating_capacity) {
        this.seating_capacity = seating_capacity;
    }

    @Override
    public String toString() {
        return "SoupKitchenObject{" +
                "parent_site=" + parent_site +
                ", description='" + description + '\'' +
                ", hours='" + hours + '\'' +
                ", kitchen_condition='" + kitchen_condition + '\'' +
                ", seating_capacity=" + seating_capacity +
                '}';
    }
}
