package com.gatech.asacs;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by alexgreco on 3/25/17.
 */
@XmlRootElement
public class FoodbankObject {
    private int parent_site;

    public int getParent_site() {
        return parent_site;
    }

    public void setParent_site(int parent_site) {
        this.parent_site = parent_site;
    }

    @Override
    public String toString() {
        return "FoodbankObject{" +
                "parent_site=" + parent_site +
                '}';
    }
}
