package com.gatech.asacs;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

/**
 * Created by shiemstra3 on 3/28/17.
 */
@XmlRootElement
public class ItemObject {
    private int item_id;
    private int units;
    private Date exp_date;
    private String item_name;
    private int parent_site;
    private String storage_type;
    private String supply_type;
    private String food_type;
    private Boolean is_archived;

    public int getItem_id() {
        return item_id;
    }

    public void setItem_id(int item_id) {
        this.item_id = item_id;
    }

    public int getUnits() { return units; }

    public void setUnits(int units) {
        this.units = units;
    }

    public Date getExp_date() { return exp_date; }

    public void setExp_date(Date exp_date) {
        this.exp_date = exp_date;
    }

    public String getItem_name() {
        return item_name;
    }

    public void setItem_name(String item_name) { this.item_name = item_name; }

    public int getParent_site() {
        return parent_site;
    }

    public void setParent_site(int parent_site) {
        this.parent_site = parent_site;
    }

    public String getStorage_type() { return storage_type; }

    public void setStorage_type(String storage_type) { this.storage_type = storage_type; }

    public String getSupply_type() { return supply_type; }

    public void setSupply_type(String supply_type) { this.supply_type = supply_type; }

    public String getFood_type() { return food_type; }

    public void setFood_type(String food_type) { this.food_type = food_type; }

    public Boolean getIs_archived() { return is_archived; }

    public void setIs_archived(Boolean is_archived) { this.is_archived = is_archived; }

    @Override
    public String toString() {
        return "ItemObject{" +
                "item_id=" + item_id +
                ", units=" + units +
                ", exp_date=" + exp_date +
                ", item_name='" + item_name + '\'' +
                ", parent_site=" + parent_site +
                ", storage_type='" + storage_type + '\'' +
                ", supply_type='" + supply_type + '\'' +
                ", food_type='" + food_type + '\'' +
                ", is_archived=" + is_archived +
                '}';
    }
}
