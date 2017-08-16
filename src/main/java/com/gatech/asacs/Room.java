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

/**
 * Created by alexgreco on 4/1/17.
 */
@Path("/rooms")
public class Room {
    CommonDB commonDB = new CommonDB();
    DBConnectionService connectionService = new DBConnectionService();

    /**
     * function to get all open rooms for a specific site number
     * @param parentSite
     * @return
     */
    @Path("/available/{id}")
    @GET
    public String getAvailableRooms(@PathParam("id") int parentSite){
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rset;

        JSONObject result = new JSONObject();
        JSONArray jArr = new JSONArray();

        try {
            //get a connection
            conn = connectionService.getConnection();
            //set the query
            pstmt = conn.prepareStatement("SELECT r.room_id, r.parent_shelter, r.occupying_client, r.occupied, s.site_name " +
                    "FROM room r JOIN site s ON r.parent_shelter = s.site_number " +
                    "WHERE parent_shelter = ? AND occupied = FALSE");
            pstmt.setInt(1, parentSite);
            //trigger query
            rset = pstmt.executeQuery();

            //check if the result is empty or not
            if(!rset.isBeforeFirst()){
                return null;
            }else{
                while(rset.next()){
                    JSONObject jObj = new JSONObject();

                    jObj.put("room_id", rset.getInt("room_id"));
                    jObj.put("parent_shelter", rset.getInt("parent_shelter"));
                    jObj.put("occupying_client", rset.getInt("occupying_client"));
                    jObj.put("site_name", rset.getString("site_name"));
                    jObj.put("occupied", rset.getBoolean("occupied"));

                    //add result obj to an array of objects
                    jArr.add(jObj);
                }
                //wrap the results in a parent obj
                result.put("rooms", jArr);
            }

        }catch(SQLException sqlEx){
            sqlEx.printStackTrace();
        }finally{
            commonDB.closeConnections(conn, pstmt);
        }

        String finalResult = result.toJSONString();

        return finalResult;
    }

    /**
     * function to update a room. Will be used to check a client into
     * a room.
     */
    @Path("/update")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateRoom(RoomObject roomObj){

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            //get a connection
            conn = connectionService.getConnection();

            // This if is to stop Null pointer exceptions
            if(roomObj.getOccupying_client() == 0) {
                //set the prepared statement
                pstmt = conn.prepareStatement("UPDATE room " +
                        "SET occupying_client = null, occupied = ? " +
                        "WHERE room_id = ?");

                pstmt.setBoolean(1, roomObj.isOccupied());
                pstmt.setInt(2, roomObj.getRoom_id());

            }else{
                //set the prepared statement
                pstmt = conn.prepareStatement("UPDATE room " +
                        "SET occupying_client = ?, occupied = ? " +
                        "WHERE room_id = ?");

                pstmt.setInt(1, roomObj.getOccupying_client());
                pstmt.setBoolean(2, roomObj.isOccupied());
                pstmt.setInt(3, roomObj.getRoom_id());
            }

            //excecute the query
            pstmt.executeUpdate();

        }catch(SQLException sqlEx){
            sqlEx.printStackTrace();
            //return a bad request if needed
            String output = "Bad Request";
            return Response.status(400).entity(output).build();
        }finally{
            commonDB.closeConnections(conn, pstmt);
        }

        String output = roomObj.toString();

        return Response.status(200).entity(output).build();
    }

    /**
     * this method gets all room for a site.
     * @param parentSite
     * @return
     */
    @Path("/{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getRooms(@PathParam("id") int parentSite){
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rset;

        JSONObject result = new JSONObject();
        JSONArray jArr = new JSONArray();

        try {
            //get a connection
            conn = connectionService.getConnection();
            //set the query
            pstmt = conn.prepareStatement("SELECT r.room_id, r.parent_shelter, r.occupying_client, r.occupied, s.site_name " +
                    "FROM room r JOIN site s ON r.parent_shelter = s.site_number " +
                    "WHERE parent_shelter = ? ");
            pstmt.setInt(1, parentSite);
            //trigger query
            rset = pstmt.executeQuery();

            //check if the result is empty or not
            if(!rset.isBeforeFirst()){
                return null;
            }else{
                while(rset.next()){
                    JSONObject jObj = new JSONObject();

                    jObj.put("room_id", rset.getInt("room_id"));
                    jObj.put("parent_shelter", rset.getInt("parent_shelter"));
                    jObj.put("occupying_client", rset.getInt("occupying_client"));
                    jObj.put("site_name", rset.getString("site_name"));
                    jObj.put("occupied", rset.getBoolean("occupied"));

                    //add result obj to an array of objects
                    jArr.add(jObj);
                }
                //wrap the results in a parent obj
                result.put("rooms", jArr);
            }

        }catch(SQLException sqlEx){
            sqlEx.printStackTrace();
        }finally{
            commonDB.closeConnections(conn, pstmt);
        }

        String finalResult = result.toJSONString();

        return finalResult;
    }

    public Response checkClientOut(int clientId){

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            //get a connection
            conn = connectionService.getConnection();

            //set the prepared statement
            pstmt = conn.prepareStatement("UPDATE room " +
                    "SET occupying_client = null, occupied = false " +
                    "WHERE occupying_client = ?");
            pstmt.setInt(1, clientId);

            pstmt.executeUpdate();


        }catch(SQLException sqlEx){
            sqlEx.printStackTrace();
            //return a bad request if needed
            String output = "Bad Request";
            return Response.status(400).entity(output).build();
        }finally{
            commonDB.closeConnections(conn, pstmt);
        }

        String output = "Success";

        return Response.status(200).entity(output).build();
    }
}
