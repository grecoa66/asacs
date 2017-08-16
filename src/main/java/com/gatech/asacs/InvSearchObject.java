package com.gatech.asacs;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

/**
 * Created by shiemstra3 on 4/14/17.
 */
@XmlRootElement
public class InvSearchObject {
    private String searchName;
    private int site_id;
    private boolean localSearch;
    private Date expDateMin;
    private Date expDateMax;
    private int unitsMin;
    private int unitsMax;
    private String foodSupply;
    private String foodType;
    private String supplyType;
    private String storageType;
    private String parentSite;
    private boolean archived;

    public boolean isArchived() { return archived; }

    public void setArchived(boolean archived) { this.archived = archived; }

    public String getParentSite() { return parentSite; }

    public void setParentSite(String parentSite) { this.parentSite = parentSite; }

    public String getFoodSupply() { return foodSupply; }

    public void setFoodSupply(String foodSupply) { this.foodSupply = foodSupply; }

    public String getFoodType() { return foodType; }

    public void setFoodType(String foodType) { this.foodType = foodType; }

    public String getSupplyType() { return supplyType; }

    public void setSupplyType(String supplyType) { this.supplyType = supplyType; }

    public String getStorageType() { return storageType; }

    public void setStorageType(String storageType) { this.storageType = storageType; }

    public Date getExpDateMax() { return expDateMax; }

    public void setExpDateMax(Date expDateMax) { this.expDateMax = expDateMax; }

    public Date getExpDateMin() { return expDateMin; }

    public void setExpDateMin(Date expDateMin) { this.expDateMin = expDateMin; }

    public int getUnitsMin() { return unitsMin; }

    public void setUnitsMin(int unitsMin) { this.unitsMin = unitsMin; }

    public int getUnitsMax() { return unitsMax; }

    public void setUnitsMax(int unitsMax) { this.unitsMax = unitsMax; }

    public boolean isLocalSearch() { return localSearch; }

    public void setLocalSearch(boolean localSearch) { this.localSearch = localSearch; }

    public int getSite_id() { return site_id; }

    public void setSite_id(int site_id) { this.site_id = site_id; }

    public String getSearchName() { return searchName; }

    public void setSearchName(String searchName) { this.searchName = searchName; }

    @Override
    public String toString() {
        return "InvSearchObject{" +
                "searchName='" + searchName + '\'' +
                ", site_id=" + site_id +
                ", localSearch=" + localSearch +
                ", expDateMin=" + expDateMin +
                ", expDateMax=" + expDateMax +
                ", unitsMin=" + unitsMin +
                ", unitsMax=" + unitsMax +
                ", foodSupply='" + foodSupply + '\'' +
                ", foodType='" + foodType + '\'' +
                ", supplyType='" + supplyType + '\'' +
                ", storageType='" + storageType + '\'' +
                ", parentSite='" + parentSite + '\'' +
                ", archived=" + archived +
                '}';
    }
}
