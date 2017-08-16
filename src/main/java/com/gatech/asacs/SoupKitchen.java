package com.gatech.asacs;

import org.json.simple.JSONObject;

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
@Path("kitchen")
public class SoupKitchen {
    DBConnectionService connectionService = new DBConnectionService();
    CommonDB commonDB = new CommonDB();

    /**
     * Get all data for a kitchens based on the parent_site
     * @param parentSite
     * @return json string of the db result
     */
    @Path("/{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getKitchenById(@PathParam("id") int parentSite) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        JSONObject jObj = new JSONObject();
        ResultSet rset;
        String result = "";

        try{
            //get a connection
            conn = connectionService.getConnection();

            //set the prepared statement
            pstmt = conn.prepareStatement("SELECT * FROM soup_kitchen WHERE parent_site = ?");
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
                jObj.put("kitchen_condition", rset.getString("kitchen_condition"));
                jObj.put("seating_capacity", rset.getInt("seating_capacity"));
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
     * json to a SoupKitchenObject. From there it creates a db connection
     * and creates a query. The query gets its values from the
     * SoupKitchenObj and executes it.
     * @param kitchenObj
     * @return
     */
    @Path("/add")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addKitchen(SoupKitchenObject kitchenObj){

        Connection conn = null;
        PreparedStatement pstmt = null;

        try{
            //get a connection
            conn = connectionService.getConnection();

            //set the prepared statement
            pstmt = conn.prepareStatement("INSERT INTO " +
                    "soup_kitchen(parent_site, description, hours, kitchen_condition, seating_capacity)" +
                    "VALUES (?,?,?,?,?)");

            //set the prepared statement
            pstmt.setInt(1, kitchenObj.getParent_site());
            pstmt.setString(2, kitchenObj.getDescription());
            pstmt.setString(3, kitchenObj.getHours());
            pstmt.setString(4, kitchenObj.getKitchen_condition());
            pstmt.setInt(5, kitchenObj.getSeating_capacity());

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

        String output = kitchenObj.toString();

        return Response.status(200).entity(output).build();
    }

    /**
     * This method is used to update the kitchen records in the db.
     * When this route is hit, it first turns the json request
     * into a java KitchenObject. After, it creates a prepared statement
     * and assigns the KitchenObj values to it. Executes the update
     * query and returns an http status.
     *
     * @param kitchenObj
     * @return
     */
    @Path("/update")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateKitchen(SoupKitchenObject kitchenObj){
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            //get a connection
            conn = connectionService.getConnection();

            //set the prepared statement
            pstmt = conn.prepareStatement("UPDATE soup_kitchen " +
                    "SET description = ?, hours = ?, kitchen_condition = ?, seating_capacity = ? " +
                    "WHERE parent_site = ? ");

            pstmt.setString(1, kitchenObj.getDescription());
            pstmt.setString(2, kitchenObj.getHours());
            pstmt.setString(3, kitchenObj.getKitchen_condition());
            pstmt.setInt(4, kitchenObj.getSeating_capacity());
            pstmt.setInt(5, kitchenObj.getParent_site());

            pstmt.executeUpdate();

        }catch(SQLException sqlEx){
            sqlEx.printStackTrace();
            //return a bad request if needed
            String output = "Bad Request";
            return Response.status(400).entity(output).build();
        }finally{
            commonDB.closeConnections(conn, pstmt);
        }

        String output = kitchenObj.toString();

        return Response.status(200).entity(output).build();
    }

    /**
     * Takes the site id as a param and delete the corresponding soup kitchen
     * @param parentSite
     * @return
     */
    @Path("/delete/{id}")
    @DELETE
    public Response deleteKitchen(@PathParam("id") int parentSite){

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            //get a connection
            conn = connectionService.getConnection();

            //set the prepared statement
            pstmt = conn.prepareStatement("DELETE FROM soup_kitchen " +
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
