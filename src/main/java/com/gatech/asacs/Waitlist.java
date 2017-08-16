package com.gatech.asacs;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by alexgreco on 4/2/17.
 */
@Path("/waitlist")
public class Waitlist {

    CommonDB commonDB = new CommonDB();
    DBConnectionService connectionService = new DBConnectionService();

    /**
     * Add a client to the waitlist
     * @param waitObj
     * @return
     */
    @POST
    public Response addClientToWaitlist(WaitlistObject waitObj){

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rset;

        try {
            //get a connection
            conn = connectionService.getConnection();

            pstmt = conn.prepareStatement("SELECT (MAX(wait_position) + 1) AS `position` FROM room_waitlist ");

            rset = pstmt.executeQuery();

            rset.next();
            int position = rset.getInt("position");

            //set the prepared statement
            pstmt = conn.prepareStatement("INSERT INTO room_waitlist(parent_shelter, client_id, wait_position) " +
                    "VALUES( ?, ? ,?) ");
            pstmt.setInt(1, waitObj.getParent_shelter());
            pstmt.setInt(2, waitObj.getClient_id());
            pstmt.setInt(3, position);

            //excecute the query
            pstmt.executeUpdate();

        }catch(SQLException sqlEx){

            sqlEx.printStackTrace();
            String output = "Bad Request";
            return Response.status(400).entity(output).build();

        }finally{
            commonDB.closeConnections(conn, pstmt);
        }

        String output = waitObj.toString();

        return Response.status(200).entity(output).build();
    }

    @Path("/remove/{id}")
    @DELETE
    public Response removeClient(@PathParam("id") int clientId){
        Connection conn = null;
        PreparedStatement pstmt = null;

        try{
            //get a connection
            conn = connectionService.getConnection();

            //get the position of the client
            pstmt = conn.prepareStatement("DELETE FROM room_waitlist " +
                    "WHERE client_id = ?");
            pstmt.setInt(1, clientId);
            //fire query
            pstmt.executeUpdate();

        }catch(SQLException sqlEx){
            sqlEx.printStackTrace();
        }finally{
            commonDB.closeConnections(conn, pstmt);
        }

        String output = "success";

        return Response.status(200).entity(output).build();
    }

    @Path("/{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String viewWaitList(@PathParam("id") int parentSite){

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rset;

        JSONObject result = new JSONObject();
        JSONArray jArr = new JSONArray();

        try {
            //get a connection
            conn = connectionService.getConnection();
            //set the query
            pstmt = conn.prepareStatement("SELECT " +
                    "r.client_id, r.wait_position, s.site_number, s.site_name, c.first_name, c.last_name  " +
                    "FROM room_waitlist r " +
                    "JOIN site s ON r.parent_shelter = s.site_number " +
                    "JOIN clients c ON r.client_id = c.client_id " +
                    "WHERE parent_shelter = ? ORDER BY wait_position ASC");
            pstmt.setInt(1, parentSite);
            //trigger query
            rset = pstmt.executeQuery();

            //check if the result is empty or not
            if(!rset.isBeforeFirst()){
                return null;
            }else{
                while(rset.next()){
                    JSONObject jObj = new JSONObject();

                    jObj.put("parent_shelter", rset.getInt("site_number"));
                    jObj.put("client_id", rset.getInt("client_id"));
                    jObj.put("client_name", (rset.getString("first_name") + " " + rset.getString("last_name")));
                    jObj.put("client_id", rset.getInt("client_id"));
                    jObj.put("wait_position", rset.getInt("wait_position"));

                    //add result obj to an array of objects
                    jArr.add(jObj);
                }
                //wrap the results in a parent obj
                result.put("waitList", jArr);
            }

        }catch(SQLException sqlEx){
            sqlEx.printStackTrace();
        }finally{
            commonDB.closeConnections(conn, pstmt);
        }

        String finalResult = result.toJSONString();

        return finalResult;

    }

    @Path("/move/up/{client_id}/{site_id}")
    @PUT
    public Response moveClientUp(@PathParam("client_id") int client_id, @PathParam("site_id") int site_id){
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rset;
        Map map = new HashMap();
        JSONArray firstArr = new JSONArray();

        try {
            //get a connection
            conn = connectionService.getConnection();

            //get the wait position of the client in question
            pstmt = conn.prepareStatement("SELECT wait_position " +
                    "FROM room_waitlist " +
                    "WHERE client_id = ? AND parent_shelter = ?");
            pstmt.setInt(1, client_id);
            pstmt.setInt(2, site_id);

            rset = pstmt.executeQuery();

            rset.next();

            int clientPosition = rset.getInt("wait_position");

            //Get all the ids and positions for the clients in the current waitlist

            pstmt = conn.prepareStatement("SELECT client_id, wait_position " +
                    "FROM room_waitlist WHERE parent_shelter = ? AND client_id != ?");
            pstmt.setInt(1, site_id);
            pstmt.setInt(2, client_id);

            rset = pstmt.executeQuery();

            //make a make with the position as the key and client_id as value
            if(!rset.isBeforeFirst()){
                return null;
            }else {
                while (rset.next()) {
                    map.put(rset.getInt("wait_position"), rset.getInt("client_id"));
                }
            }

            //temp values for client we will swtich
            int switchPos = 0;
            int switchClient = 0;

            //find the closest client based on position, count toward 0
            for(int i = (clientPosition - 1); i > 0; i--){
                if(map.containsKey(i)){
                    switchPos = i;
                    Object key = i;
                    switchClient = Integer.parseInt(map.get(key).toString());
                    break;
                }
            }

            //if we didn't find a client to swap, return null
            if(switchPos == 0 && switchClient == 0){
                return null;
            }

            //run updates to switch their position
            //first update the client moving up
            pstmt = conn.prepareStatement("UPDATE room_waitlist " +
                    "SET wait_position = ? " +
                    "WHERE client_id = ? " +
                    "AND parent_shelter = ?");

            pstmt.setInt(1, switchPos);
            pstmt.setInt(2, client_id);
            pstmt.setInt(3, site_id);

            pstmt.executeUpdate();

            //update the client we moved down
            pstmt = conn.prepareStatement("UPDATE room_waitlist " +
                    "SET wait_position = ? " +
                    "WHERE client_id = ? " +
                    "AND parent_shelter = ?");

            pstmt.setInt(1, clientPosition);
            pstmt.setInt(2, switchClient);
            pstmt.setInt(3, site_id);

            pstmt.executeUpdate();

        }catch(SQLException sqlEx){

            sqlEx.printStackTrace();
            String output = "Bad Request";
            return Response.status(400).entity(output).build();

        }finally{
            commonDB.closeConnections(conn, pstmt);
        }

        String output;

        return Response.status(200).entity("request").build();
    }

    @Path("/move/down/{client_id}/{site_id}")
    @PUT
    public Response moveClientDown(@PathParam("client_id") int client_id, @PathParam("site_id") int site_id){
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rset;
        Map map = new HashMap();
        JSONArray firstArr = new JSONArray();

        try {
            //get a connection
            conn = connectionService.getConnection();

            //get the wait position of the client in question
            pstmt = conn.prepareStatement("SELECT wait_position " +
                    "FROM room_waitlist " +
                    "WHERE client_id = ? AND parent_shelter = ?");
            pstmt.setInt(1, client_id);
            pstmt.setInt(2, site_id);

            rset = pstmt.executeQuery();

            rset.next();

            //get position of client in waitlist
            int clientPosition = rset.getInt("wait_position");

            // Get all the ids and positions for the clients in the current waitlist
            pstmt = conn.prepareStatement("SELECT client_id, wait_position " +
                    "FROM room_waitlist WHERE parent_shelter = ? AND client_id != ?");
            pstmt.setInt(1, site_id);
            pstmt.setInt(2, client_id);

            rset = pstmt.executeQuery();

            //make a make with the position as the key and client_id as value
            if(!rset.isBeforeFirst()){
                return null;
            }else {
                while (rset.next()) {
                    map.put(rset.getInt("wait_position"), rset.getInt("client_id"));
                }
            }

            //get the highest position number from the list
            int highestPos = 0;

            pstmt = conn.prepareStatement("SELECT max(wait_position) " +
                    "AS max_pos FROM room_waitlist " +
                    "WHERE parent_shelter = ? AND client_id != ?");
            pstmt.setInt(1, site_id);
            pstmt.setInt(2, client_id);

            rset = pstmt.executeQuery();
            //increment the rset to first result
            rset.next();
            //assign the highest wait position from result
            highestPos = rset.getInt("max_pos");

            //temp values for client we will swtich
            int switchPos = 0;
            int switchClient = 0;

            //find the closest client based on position, count toward 0
            for(int i = clientPosition; i <= highestPos; i++){
                if(map.containsKey(i)){
                    switchPos = i;
                    Object key = i;
                    switchClient = Integer.parseInt(map.get(key).toString());
                    break;
                }
            }

            //if we didn't find a client to swap, return null
            if(switchPos == 0 && switchClient == 0){
                return null;
            }

            //run updates to switch their position
            //first update the client moving up
            pstmt = conn.prepareStatement("UPDATE room_waitlist " +
                    "SET wait_position = ? " +
                    "WHERE client_id = ? " +
                    "AND parent_shelter = ?");

            pstmt.setInt(1, switchPos);
            pstmt.setInt(2, client_id);
            pstmt.setInt(3, site_id);

            pstmt.executeUpdate();

            //update the client we moved down
            pstmt = conn.prepareStatement("UPDATE room_waitlist " +
                    "SET wait_position = ? " +
                    "WHERE client_id = ? " +
                    "AND parent_shelter = ?");

            pstmt.setInt(1, clientPosition);
            pstmt.setInt(2, switchClient);
            pstmt.setInt(3, site_id);

            pstmt.executeUpdate();

        }catch(SQLException sqlEx){

            sqlEx.printStackTrace();
            String output = "Bad Request";
            return Response.status(400).entity(output).build();

        }finally{
            commonDB.closeConnections(conn, pstmt);
        }

        String output;

        return Response.status(200).entity("request").build();
    }

    /**
     * get all records for one client in all waitlists.
     */
    @Path("/client/{id}")
    @GET
    public String getClientWaitlist(@PathParam("id") int clientID){
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rset;

        JSONObject result = new JSONObject();
        JSONArray jArr = new JSONArray();

        try {
            //get a connection
            conn = connectionService.getConnection();
            //set the query
            pstmt = conn.prepareStatement("SELECT * FROM room_waitlist " +
                    "WHERE client_id = ?");
            pstmt.setInt(1, clientID);
            //trigger query
            rset = pstmt.executeQuery();

            //check if the result is empty or not
            if(!rset.isBeforeFirst()){
                return null;
            }else{
                while(rset.next()){
                    JSONObject jObj = new JSONObject();

                    jObj.put("parent_shelter", rset.getInt("parent_shelter"));
                    jObj.put("client_id", rset.getInt("client_id"));
                    jObj.put("wait_position", rset.getInt("wait_position"));

                    //add result obj to an array of objects
                    jArr.add(jObj);
                }
                //wrap the results in a parent obj
                result.put("waitList", jArr);
            }

        }catch(SQLException sqlEx){
            sqlEx.printStackTrace();
        }finally{
            commonDB.closeConnections(conn, pstmt);
        }

        String finalResult = result.toJSONString();

        return finalResult;
    }

}
