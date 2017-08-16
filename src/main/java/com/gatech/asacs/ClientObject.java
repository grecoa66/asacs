package com.gatech.asacs;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Samuel on 4/2/17.
 */
@XmlRootElement
public class ClientObject
{

    private int clientId;
    private String idNbr;
    private String idDesc;
    private String firstName;
    private String lastName;
    private String idPhone;

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public String getIdNbr() {
        return idNbr;
    }

    public void setIdNbr(String idNbr) {
        this.idNbr = idNbr;
    }

    public String getIdDesc() {
        return idDesc;
    }

    public void setIdDesc(String idDesc) {
        this.idDesc = idDesc;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getIdPhone() {
        return idPhone;
    }

    public void setIdPhone(String idPhone) {
        this.idPhone = idPhone;
    }
}
