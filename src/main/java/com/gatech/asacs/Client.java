package com.gatech.asacs;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.*;
import java.util.Calendar;
import java.util.Date;

@Path("/client")
public class Client {

    CommonDB commonDB = new CommonDB();
    DBConnectionService connectionService = new DBConnectionService();

    /**
     * This function searches for clients that have names similar to
     * the search input
     * @param searchInput
     * @return
     */
    @Path("/searchName/{name}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String clientSearch(@PathParam("name") String searchInput){
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rset;

        JSONObject result = new JSONObject();
        JSONArray jArr = new JSONArray();

        try{
            //get a connection
            conn = connectionService.getConnection();
            //set the query
            pstmt = conn.prepareStatement("SELECT * FROM clients " +
                    "WHERE last_name LIKE ? LIMIT 5");
            //set the parameter
            pstmt.setString(1, searchInput+"%");

            rset = pstmt.executeQuery();


            if(!rset.isBeforeFirst()){
                //If there is no result, return result
                return null;
            }else{
                while(rset.next()){
                    JSONObject jObj = new JSONObject();

                    jObj.put("client_id", rset.getInt("client_id"));
                    jObj.put("id_nbr", rset.getString("id_nbr"));
                    jObj.put("id_desc", rset.getString("id_desc"));
                    jObj.put("first_name", rset.getString("first_name"));
                    jObj.put("last_name", rset.getString("last_name"));
                    jObj.put("phone_number", rset.getString("phone_number"));

                    jArr.add(jObj);
                }
            }

            //wrap the array of results in an obj
            result.put("clients", jArr);

        }catch(SQLException sqlEx){
            sqlEx.printStackTrace();
        }finally{
            commonDB.closeConnections(conn, pstmt);
        }

        //toString the json data
        String finalResult = result.toJSONString();

        return finalResult;
    }

    /**
     * This function search for a client based clientID
     * @param searchInput
     * @return
     */
    @Path("/searchID/{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String clientSearch(@PathParam("id") int searchInput){
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rset;


        JSONObject finaljObj = new JSONObject();
        JSONArray jArr = new JSONArray();

        try{
            //get a connection
            conn = connectionService.getConnection();
            //set the query
            pstmt = conn.prepareStatement("SELECT * FROM clients " +
                    "WHERE id_nbr LIKE ? LIMIT 5");
            //set the parameter
            pstmt.setString(1, searchInput+"%");

            rset = pstmt.executeQuery();


            if(!rset.isBeforeFirst()){
                //If there is no result, return result
                return null;
            }else{
                while(rset.next()) {
                    JSONObject jObj = new JSONObject();

                    jObj.put("client_id", rset.getInt("client_id"));
                    jObj.put("id_nbr", rset.getString("id_nbr"));
                    jObj.put("id_desc", rset.getString("id_desc"));
                    jObj.put("first_name", rset.getString("first_name"));
                    jObj.put("last_name", rset.getString("last_name"));
                    jObj.put("phone_number", rset.getString("phone_number"));

                    jArr.add(jObj);
                }
            }

            //wrap the obj is a wrapper obj
            finaljObj.put("clients", jArr);

        }catch(SQLException sqlEx){
            sqlEx.printStackTrace();
        }finally{
            commonDB.closeConnections(conn, pstmt);
        }

        //toString the json data
        String finalResult = finaljObj.toJSONString();

        return finalResult;
    }

    /**
     * This function will get all audit log entries that correspond
     * to the current client. The clietn ID is passed in by path param.
     * @param clientID
     * @return
     */
    @Path("/auditLog/{id}")
    @GET
    public String getAuditLog(@PathParam("id") int clientID){
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rset;

        JSONObject result = new JSONObject();
        JSONArray jArr = new JSONArray();

        try {
            //get a connection
            conn = connectionService.getConnection();
            //set the query
            pstmt = conn.prepareStatement("SELECT * FROM audit_log " +
                    "WHERE tracking_client = ? ORDER BY date_time DESC");
            pstmt.setInt(1, clientID);
            //trigger query
            rset = pstmt.executeQuery();

            //check if the result is empty or not
            if(!rset.isBeforeFirst()){
                return null;
            }else{
                while(rset.next()){
                    JSONObject jObj = new JSONObject();

                    jObj.put("tracking_client", rset.getInt("tracking_client"));
                    jObj.put("change_description", rset.getString("change_description"));
                    jObj.put("date_time", rset.getDate("date_time").toString());

                    //add result obj to an array of objects
                    jArr.add(jObj);
                }
                //wrap the results in a parent obj
                result.put("audit_log", jArr);
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
     * This function will get all services used entries that correspond
     * to the current client. The clietn ID is passed in by path param.
     * @param clientID
     * @return
     */
    @Path("/serviceLog/{id}")
    @GET
    public String getServiceLog(@PathParam("id") int clientID){
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rset;

        JSONObject result = new JSONObject();
        JSONArray jArr = new JSONArray();

        try {
            //get a connection
            conn = connectionService.getConnection();
            //set the query
            pstmt = conn.prepareStatement("SELECT * FROM services_used " +
                    "WHERE tracking_client = ? ORDER BY date_time DESC ");
            pstmt.setInt(1, clientID);
            //trigger query
            rset = pstmt.executeQuery();

            //check if the result is empty or not
            if(!rset.isBeforeFirst()){
                return null;
            }else{
                while(rset.next()){
                    JSONObject jObj = new JSONObject();

                    jObj.put("tracking_client", rset.getInt("tracking_client"));
                    jObj.put("description", rset.getString("description"));
                    jObj.put("date_time", rset.getDate("date_time").toString());
                    jObj.put("site_number", rset.getInt("site_number"));

                    //add result obj to an array of objects
                    jArr.add(jObj);
                }
                //wrap the results in a parent obj
                result.put("service_log", jArr);
            }

        }catch(SQLException sqlEx){
            sqlEx.printStackTrace();
        }finally{
            commonDB.closeConnections(conn, pstmt);
        }

        String finalResult = result.toJSONString();

        return finalResult;
    }

    @Path("/serviceLog")
    @POST
    public Response addServiceLogEntry(ServicesUsedObject suObj){

        Connection conn = null;
        PreparedStatement pstmt = null;
        java.util.Date date = new Date();
        Timestamp currDate;

        try {
            //get a connection
            conn = connectionService.getConnection();

            currDate = new Timestamp(date.getTime());

            //set the prepared statement
            pstmt = conn.prepareStatement("INSERT INTO " +
                    "services_used( tracking_client, description, date_time, site_number ) " +
                    "VALUES (?, ?, ?, ?)");
            pstmt.setInt(1, suObj.getTracking_client());
            pstmt.setString(2, suObj.getDescription());
            pstmt.setTimestamp(3, currDate);
            pstmt.setInt(4, suObj.getSite_number());

            //excecute the query
            pstmt.executeUpdate();

        }catch(SQLException sqlEx){

            sqlEx.printStackTrace();
            String output = "Bad Request";
            return Response.status(400).entity(output).build();

        }finally{
            commonDB.closeConnections(conn, pstmt);
        }

        String output = suObj.toString();

        return Response.status(200).entity(output).build();
    }

    @Path("/auditLog")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addClientLogEntry(AuditLogObject logObj){

        Connection conn = null;
        PreparedStatement pstmt = null;
        java.util.Date date = new Date();
        Timestamp currDate;

        try {
            //get a connection
            conn = connectionService.getConnection();

            currDate = new Timestamp(date.getTime());
            logObj.setDate_time(currDate);

            //set the prepared statement
            pstmt = conn.prepareStatement("INSERT INTO " +
                    "audit_log( tracking_client, change_description, date_time ) " +
                    "VALUES (?, ?, ?)");
            pstmt.setInt(1, logObj.getTracking_client());
            pstmt.setString(2, logObj.getChange_description());
            pstmt.setObject(3, logObj.getDate_time());

            //excecute the query
            pstmt.executeUpdate();

        }catch(SQLException sqlEx){

            sqlEx.printStackTrace();
            String output = "Bad Request";
            return Response.status(400).entity(output).build();

        }finally{
            commonDB.closeConnections(conn, pstmt);
        }

        String output = logObj.toString();

        return Response.status(200).entity(output).build();
    }

    @Path("checkout/{id}")
    @PUT
    public Response checkClientOut(@PathParam("id") int clientId){

        Bunk bunk = new Bunk();
        Room room = new Room();

        //Call checkout method in bunk
        bunk.checkClientOut(clientId);

        //Call checkout method room
        room.checkClientOut(clientId);

        String output = "Success";

        return Response.status(200).entity(output).build();
    }

    @Path("/add")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addClient(ClientObject clientObj)
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            //get a connection
            conn = connectionService.getConnection();

            //set the prepared statement
            pstmt = conn.prepareStatement("INSERT INTO " +
                    "clients (id_nbr, id_desc, first_name, last_name, phone_number) "
                    + "VALUES (?, ?, ?, ?, ?)");
            pstmt.setString(1, clientObj.getIdNbr());
            pstmt.setString(2, clientObj.getIdDesc());
            pstmt.setString(3, clientObj.getFirstName());
            pstmt.setString(4, clientObj.getLastName());
            pstmt.setString(5, clientObj.getIdPhone());

            //excecute the query
            pstmt.executeUpdate();

        }catch (SQLException sqlEx) {
            sqlEx.printStackTrace();

            //return a bad request if needed
            String output = "Bad Request";
            return Response.status(400).entity(output).build();

        }finally {
            commonDB.closeConnections(conn, pstmt);
        }

        String output = clientObj.toString();
        return Response.status(200).entity(output).build();
    }

    @Path("/update")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateClient(ClientObject clientObj)
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            //get a connection
            conn = connectionService.getConnection();

            //set the prepared statement
            pstmt = conn.prepareStatement("UPDATE clients " +
                    "SET id_nbr=?, id_desc=?, first_name=?, "
                    + "last_name=?, phone_number=? " +
                    "WHERE client_id=? ");
            pstmt.setString(1, clientObj.getIdNbr());
            pstmt.setString(2, clientObj.getIdDesc());
            pstmt.setString(3, clientObj.getFirstName());
            pstmt.setString(4, clientObj.getLastName());
            pstmt.setString(5, clientObj.getIdPhone());
            pstmt.setInt(6, clientObj.getClientId());

            //excecute the query
            pstmt.executeUpdate();

        }catch (SQLException sqlEx) {
            sqlEx.printStackTrace();

            //return a bad request if needed
            String output = "Bad Request";
            return Response.status(400).entity(output).build();

        }finally {
            commonDB.closeConnections(conn, pstmt);
        }

        String output = clientObj.toString();
        return Response.status(200).entity(output).build();
    }

    /* Similar to searchname but with a refined search for Client Mgmt
    setting it up in its own function so as not to break other client
    check in check out scripts. May refactor later.
    Changes, removing limit of 5 and searching for both first and last names
     */
    @Path("/searchNameMgmt/{name}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String clientMgmtSearch(@PathParam("name") String searchInput){
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rset;

        JSONObject result = new JSONObject();
        JSONArray jArr = new JSONArray();

        try{
            //get a connection
            conn = connectionService.getConnection();
            //set the query
            pstmt = conn.prepareStatement("SELECT * FROM clients " +
                    "WHERE first_name LIKE ? OR last_name LIKE ?");
            //set the parameter
            pstmt.setString(1, searchInput+"%");
            pstmt.setString(2, searchInput+"%");

            rset = pstmt.executeQuery();


            if(!rset.isBeforeFirst()){
                //If there is no result, return result
                return null;
            }else{
                while(rset.next()){
                    JSONObject jObj = new JSONObject();

                    jObj.put("client_id", rset.getInt("client_id"));
                    jObj.put("id_nbr", rset.getString("id_nbr"));
                    jObj.put("id_desc", rset.getString("id_desc"));
                    jObj.put("first_name", rset.getString("first_name"));
                    jObj.put("last_name", rset.getString("last_name"));
                    jObj.put("phone_number", rset.getString("phone_number"));

                    jArr.add(jObj);
                }
            }

            //wrap the array of results in an obj
            result.put("clients", jArr);

        }catch(SQLException sqlEx){
            sqlEx.printStackTrace();
        }finally{
            commonDB.closeConnections(conn, pstmt);
        }

        //toString the json data
        String finalResult = result.toJSONString();

        return finalResult;
    }

    /**
     * This function performs an exact search for a client
     * for audit log purposes
     */
    @Path("/searchForAudit")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String clientExactSearch(ClientObject clientObj){
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rset;


        JSONObject finaljObj = new JSONObject();
        JSONArray jArr = new JSONArray();

        try{
            //get a connection
            conn = connectionService.getConnection();
            //set the query
            pstmt = conn.prepareStatement("SELECT * FROM clients " +
                    "WHERE id_nbr = ? " + "AND id_desc = ? " + "AND first_name = ? "
                    + "AND last_name = ? " + "AND phone_number = ? ");
            //set the parameter
            pstmt.setString(1, clientObj.getIdNbr());
            pstmt.setString(2, clientObj.getIdDesc());
            pstmt.setString(3, clientObj.getFirstName());
            pstmt.setString(4, clientObj.getLastName());
            pstmt.setString(5, clientObj.getIdPhone());

            //execute query
            rset = pstmt.executeQuery();


            if(!rset.isBeforeFirst()){
                //If there is no result, return result
                return null;
            }else{
                while(rset.next()) {
                    JSONObject jObj = new JSONObject();

                    jObj.put("client_id", rset.getInt("client_id"));
                    jObj.put("id_nbr", rset.getString("id_nbr"));
                    jObj.put("id_desc", rset.getString("id_desc"));
                    jObj.put("first_name", rset.getString("first_name"));
                    jObj.put("last_name", rset.getString("last_name"));
                    jObj.put("phone_number", rset.getString("phone_number"));

                    jArr.add(jObj);
                }
            }

            //wrap the obj is a wrapper obj
            finaljObj.put("clients", jArr);

        }catch(SQLException sqlEx){
            sqlEx.printStackTrace();
        }finally{
            commonDB.closeConnections(conn, pstmt);
        }

        //toString the json data
        String finalResult = finaljObj.toJSONString();

        return finalResult;
    }

}