package com.gatech.asacs;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by alexgreco on 3/29/17.
 */
@XmlRootElement
public class AuthenticationObject {

    private String user_name;
    private String user_password;

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_password() {
        return user_password;
    }

    public void setUser_password(String user_password) {
        this.user_password = user_password;
    }

    @Override
    public String toString() {
        return "AuthenticationObject{" +
                "user_name='" + user_name + '\'' +
                ", user_password='" + user_password + '\'' +
                '}';
    }
}
