package com.gatech.asacs;

import org.json.simple.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by alexgreco on 3/15/17.
 */
public class CommonDB {

    /**
     * Method to close out db connection and prepared statements.
     * @param conn
     * @param pstmt
     */
    public void closeConnections(Connection conn, PreparedStatement pstmt){
        try{
            conn.close();
            pstmt.close();
        }catch(SQLException sqlEx){
            sqlEx.printStackTrace();
        }
    }

}
