package com.gatech.asacs;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * This is an obj bean file used by jersey to translate
 * json into a java obj.
 * Created by alexgreco on 3/25/17.
 */
@XmlRootElement
public class PantryObject {


    private int parent_site;
    private String description;
    private String hours;
    private String pantry_condition;

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

    public String getPantry_condition() {
        return pantry_condition;
    }

    public void setPantry_condition(String pantry_condition) {
        this.pantry_condition = pantry_condition;
    }

    @Override
    public String toString() {
        return "PantryObject{" +
                "parent_site=" + parent_site +
                ", description='" + description + '\'' +
                ", hours='" + hours + '\'' +
                ", pantry_condition='" + pantry_condition + '\'' +
                '}';
    }

}
