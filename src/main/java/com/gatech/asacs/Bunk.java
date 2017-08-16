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
 * Created by alexgreco on 4/2/17.
 */
@Path("/bunks")
public class Bunk {

    CommonDB commonDB = new CommonDB();
    DBConnectionService connectionService = new DBConnectionService();

    @Path("/{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getBunk(@PathParam("id") int parentSite){
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
                    "b.bunk_id, b.occupying_client, b.occupied, b.bunk_type, b.parent_shelter, s.site_name " +
                    "FROM bunk b JOIN site s ON b.parent_shelter = s.site_number " +
                    "WHERE parent_shelter = ? ORDER BY bunk_id ASC");
            pstmt.setInt(1, parentSite);
            //trigger query
            rset = pstmt.executeQuery();

            //check if the result is empty or not
            if(!rset.isBeforeFirst()){
                return null;
            }else{
                while(rset.next()){
                    JSONObject jObj = new JSONObject();

                    jObj.put("bunk_id", rset.getInt("bunk_id"));
                    jObj.put("parent_shelter", rset.getInt("parent_shelter"));
                    jObj.put("site_name", rset.getString("site_name"));
                    jObj.put("occupying_client", rset.getInt("occupying_client"));
                    jObj.put("occupied", rset.getBoolean("occupied"));
                    jObj.put("bunk_type", rset.getString("bunk_type"));

                    //add result obj to an array of objects
                    jArr.add(jObj);
                }
                //wrap the results in a parent obj
                result.put("bunks", jArr);
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
            pstmt = conn.prepareStatement("SELECT " +
                    "b.bunk_id, b.occupying_client, b.occupied, b.bunk_type, b.parent_shelter, s.site_name " +
                    "FROM bunk b JOIN site s ON b.parent_shelter = s.site_number " +
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

                    jObj.put("bunk_id", rset.getInt("bunk_id"));
                    jObj.put("parent_shelter", rset.getInt("parent_shelter"));
                    jObj.put("site_name", rset.getString("site_name"));
                    jObj.put("occupying_client", rset.getInt("occupying_client"));
                    jObj.put("occupied", rset.getBoolean("occupied"));
                    jObj.put("bunk_type", rset.getString("bunk_type"));

                    //add result obj to an array of objects
                    jArr.add(jObj);
                }
                //wrap the results in a parent obj
                result.put("bunks", jArr);
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
     * function to update a bunk. Will be used to check a client into
     * a bunk.
     */
    @Path("/update")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response checkClientIntoBunk(BunkObject bunkObj){

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            //get a connection
            conn = connectionService.getConnection();

            //set the prepared statement
            pstmt = conn.prepareStatement("UPDATE bunk " +
                    "SET occupying_client = ?, occupied = ?, bunk_type = ? " +
                    "WHERE bunk_id = ?");

            if(bunkObj.getOccupying_client() == 0){
                pstmt.setNull(1, java.sql.Types.INTEGER);
            }else {
                pstmt.setInt(1, bunkObj.getOccupying_client());
            }
            pstmt.setBoolean(2, bunkObj.isOccupied());
            pstmt.setString(3, bunkObj.getBunk_type());
            pstmt.setInt(4, bunkObj.getBunk_id());

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

        String output = bunkObj.toString();

        return Response.status(200).entity(output).build();
    }



    public Response checkClientOut(int clientId){

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            //get a connection
            conn = connectionService.getConnection();

            //set the prepared statement
            pstmt = conn.prepareStatement("UPDATE bunk " +
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
