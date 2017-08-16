package com.gatech.asacs;

import org.json.simple.JSONObject;

import javax.print.attribute.standard.Media;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by alexgreco on 3/20/17.
 */
@Path("shelter")
public class Shelter {
    DBConnectionService connectionService = new DBConnectionService();
    CommonDB commonDB = new CommonDB();

    /**
     * Get all data for a shelter's based on the parent_site
     * @param parentSite
     * @return json string of the db result
     */
    @Path("/{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getShelterById(@PathParam("id") int parentSite){
        Connection conn = null;
        PreparedStatement pstmt = null;
        JSONObject jObj = new JSONObject();
        ResultSet rset ;
        String result = "";

        try{
            //get a connection
            conn = connectionService.getConnection();

            //set the prepared statement
            pstmt = conn.prepareStatement("SELECT * FROM shelter WHERE parent_site = ?");
            pstmt.setInt(1, parentSite);

            //fire the query and collect the result
            rset = pstmt.executeQuery();

            if(!rset.isBeforeFirst()){
                result = "";
            }else {
                rset.next();
                jObj.put("parent_site", rset.getInt("parent_site"));
                jObj.put("description", rset.getString("description"));
                jObj.put("hours", rset.getString("hours"));
                jObj.put("shelter_condition", rset.getString("shelter_condition"));
            }
            result = jObj.toJSONString();


        }catch(SQLException sqlEx){
            sqlEx.printStackTrace();
        }finally{
            commonDB.closeConnections(conn, pstmt);
        }

        return result;
    }

    /**
     * This is a method that first turns the request from
     * json to a ShelterObject. From there it creates a db connection
     * and creates a query. The query gets its values from the
     * ShelterObj and executes it.
     * @param shelterObj
     * @return
     */
    @Path("/add")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addShelter(ShelterObject shelterObj){

        Connection conn = null;
        Connection conn2 = null;
        Connection conn3 = null;

        PreparedStatement pstmt = null;
        PreparedStatement pstmtBunks = null;
        PreparedStatement pstmtRooms = null;

        try{
            //get a connection
            conn = connectionService.getConnection();

            //set the prepared statement
            pstmt = conn.prepareStatement("INSERT INTO " +
                    "shelter (parent_site, description, hours, shelter_condition)" +
                    "VALUES (?,?,?,?)");
            pstmt.setInt(1, shelterObj.getParent_site());
            pstmt.setString(2, shelterObj.getDescription());
            pstmt.setString(3, shelterObj.getHours());
            pstmt.setString(4, shelterObj.getShelter_condition());

            //fire the query
            pstmt.executeUpdate();

        }catch(SQLException sqlEx){
            sqlEx.printStackTrace();
            //return a bad request if needed
            String output = "Bad Request";
            return Response.status(400).entity(output).build();
        }finally{
            commonDB.closeConnections(conn, pstmt);
        }


        int bunks = shelterObj.getBunks();
        // add bunks to table
        try{

            if(bunks > 0) {
                //get a connection
                conn2 = connectionService.getConnection();
                //conn2.setAutoCommit(false);
                for(int i = 0;i < bunks;i++){
                pstmtBunks = conn2.prepareStatement("INSERT INTO " +
                        "bunk (parent_shelter) VALUE (?)");

                    pstmtBunks.setInt(1, shelterObj.getParent_site());
                    pstmtBunks.executeUpdate();
                }
                //pstmtBunks.executeBatch();
                //conn2.commit();
            }

        }catch(SQLException sqlEx){
            sqlEx.printStackTrace();
            //return a bad request if needed
            String output = "Bad Request";
            return Response.status(400).entity(output).build();
        }finally{
            if(bunks > 0) {
                commonDB.closeConnections(conn2, pstmtBunks);
            }
        }


        int rooms = shelterObj.getRooms();
        // add bunks to table
        try{
            // add rooms to table

            if(rooms > 0) {
                //get a connection
                conn3 = connectionService.getConnection();

                for(int i = 0;i < rooms;i++){
                    pstmtRooms = conn3.prepareStatement("INSERT INTO " +
                        "room (parent_shelter) VALUE (?)");

                    pstmtRooms.setInt(1, shelterObj.getParent_site());
                    //pstmtRooms.addBatch();
                    pstmtRooms.executeUpdate();
                }
                //pstmtRooms.executeBatch();
                //conn3.commit();
            }

        }catch(SQLException sqlEx){
            sqlEx.printStackTrace();
            //return a bad request if needed
            String output = "Bad Request";
            return Response.status(400).entity(output).build();
        }finally{
            if(rooms > 0) {
                commonDB.closeConnections(conn3, pstmtRooms);
            }
        }

        String output = shelterObj.toString();

        return Response.status(200).entity(output).build();
    }

    /**
     * This method is used to update the shelter records in the db.
     * When this route is hit, it first turns the json request
     * into a java ShelterObject. After, it creates a prepared statement
     * and assigns the ShelterObj values to it. Executes the update
     * query and returns an http status.
     *
     * @param shelterObj
     * @return
     */
    @Path("/update")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateShelter(ShelterObject shelterObj){
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            //get a connection
            conn = connectionService.getConnection();

            //set the prepared statement
            pstmt = conn.prepareStatement("UPDATE shelter " +
                    "SET description = ?, hours = ?, shelter_condition = ? " +
                    "WHERE parent_site = ? ");

            pstmt.setString(1, shelterObj.getDescription());
            pstmt.setString(2, shelterObj.getHours());
            pstmt.setString(3, shelterObj.getShelter_condition());
            pstmt.setInt(4, shelterObj.getParent_site());

            pstmt.executeUpdate();

        }catch(SQLException sqlEx){
            sqlEx.printStackTrace();
            //return a bad request if needed
            String output = "Bad Request";
            return Response.status(400).entity(output).build();
        }finally{
            commonDB.closeConnections(conn, pstmt);

        }

        String output = shelterObj.toString();

        return Response.status(200).entity(output).build();
    }

    /**
     * Takes the site id as a param and delete the corresponding shelter
     * @param parentSite
     * @return
     */
    @Path("/delete/{id}")
    @DELETE
    public Response deleteShelter(@PathParam("id") int parentSite){

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            //get a connection
            conn = connectionService.getConnection();

            //set the prepared statement
            pstmt = conn.prepareStatement("DELETE FROM shelter " +
                    "WHERE parent_site = ?");
            pstmt.setInt(1, parentSite);

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
