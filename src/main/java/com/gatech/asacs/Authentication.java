package com.gatech.asacs;

import org.json.simple.JSONObject;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by alexgreco on 3/29/17.
 */
@Path("/authenticate")
public class Authentication {

    private DBConnectionService connectionService = new DBConnectionService();
    private CommonDB commonDB = new CommonDB();

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public String authenticateUser(AuthenticationObject authObj){
        String result;
        JSONObject jObj = new JSONObject();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rset;

        try {
            //get a connection
            conn = connectionService.getConnection();

            //set the prepared statement
            pstmt = conn.prepareStatement("SELECT * FROM users " +
                    "WHERE user_name = ? AND user_password = ?");
            pstmt.setString(1, authObj.getUser_name());
            pstmt.setString(2, authObj.getUser_password());

            //capture result in a set
            rset = pstmt.executeQuery();

            //if no result returned, return null to caller
            if(!rset.isBeforeFirst()){
                return null;
            }else{
                //if result, package it as json and return to caller
                rset.next();
                jObj.put("first_name", rset.getString("first_name"));
                jObj.put("last_name", rset.getString("last_name"));
                jObj.put("site_managing", rset.getString("site_managing"));
                jObj.put("user_name", rset.getString("user_name"));
            }

        }catch(SQLException sqlEx){
            sqlEx.printStackTrace();
        }finally{
            commonDB.closeConnections(conn, pstmt);
        }

        //format the jsonObj
        result = jObj.toJSONString();

        return result;
    }

}
