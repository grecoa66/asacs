package com.gatech.asacs;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * Created by alexgreco on 3/9/17.
 */

@Path("foodbank")
public class Foodbank {

    DBConnectionService connectionService = new DBConnectionService();
    CommonDB commonDB = new CommonDB();

    /**
     * This is an example of how the api will work.
     * This will be deleted when everyone understands.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getAllFoodbanks(){

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rset;

        JSONObject result = new JSONObject();
        JSONArray jArr = new JSONArray();
        Collection<JSONObject> foodBanks = new ArrayList<>();

        try {
            //get a connection
             conn = connectionService.getConnection();

            //set the prepared statement
            pstmt = conn.prepareStatement("SELECT * FROM foodbank");

            //fire the query and collect the result
            rset = pstmt.executeQuery();

            //build json obj from rset
            while(rset.next()){
                //create the obj we will write to
                JSONObject jObj = new JSONObject();
                //write the data to obj
                jObj.put("parent_site", rset.getInt("parent_site"));
                //store that obj in an array
                jArr.add(jObj);
            }
            //wrap the array in an obj and return this below
            result.put("foodbanks", jArr);

        }
        catch(SQLException sqlEx){
            sqlEx.printStackTrace();
        }
        finally{
           commonDB.closeConnections(conn, pstmt);
        }

        return result.toJSONString();
    }


    @Path("/{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getFoodbankById(@PathParam("id") int parentSite){
        Connection conn = null;
        PreparedStatement pstmt = null;
        JSONObject jObj = new JSONObject();
        ResultSet rset ;
        String result = "";

        try{
            //get a connection
            conn = connectionService.getConnection();

            //set the prepared statement
            pstmt = conn.prepareStatement("SELECT * FROM foodbank WHERE parent_site = ?");
            pstmt.setInt(1, parentSite);

            //fire the query and collect the result
            rset = pstmt.executeQuery();

            if(!rset.isBeforeFirst()){
                result = "";
            }else {
                rset.next();
                jObj.put("parent_site", rset.getInt("parent_site"));
            }
            result = jObj.toJSONString();

        }catch(SQLException sqlEx){
            sqlEx.printStackTrace();
        }finally{
            commonDB.closeConnections(conn, pstmt);
        }

        return result;
    }

    /*
    * Gets all other foodbank sites for inventory search 'site' search
    */
    @Path("/sites/{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getOtherFoodbanks(@PathParam("id") int parentSite){
        Connection conn = null;
        PreparedStatement pstmt = null;
        JSONObject start = new JSONObject();
        JSONArray jArr = new JSONArray();
        ResultSet rset ;
        String output = "";

        try{
            //get a connection
            conn = connectionService.getConnection();

            //set the prepared statement
            pstmt = conn.prepareStatement("SELECT a.parent_site, b.site_name " +
                    "FROM foodbank a " +
                    "INNER JOIN site b ON a.parent_site = b.site_number " +
                    "WHERE a.parent_site != ?");
            pstmt.setInt(1, parentSite);

            //fire the query and collect the result
            rset = pstmt.executeQuery();

            //first option of ""
            start.put("value", "");
            start.put("label", "Site Name");
            jArr.add(start);

            while(rset.next()){
                //create the obj we will write to
                JSONObject jObj = new JSONObject();
                //write the data to obj
                jObj.put("value", rset.getInt("parent_site"));
                jObj.put("label", rset.getString("site_name"));

                //store that obj in an array
                jArr.add(jObj);
            }

        }catch(SQLException sqlEx){
            sqlEx.printStackTrace();
        }finally{
            commonDB.closeConnections(conn, pstmt);
        }
        output = jArr.toJSONString();

        return output;
    }

    /**
     * This is a method that first turns the request from
     * json to a FoodbankObject. From there it creates a db connection
     * and creates a query. The query gets its values from the
     * foodbankObj and executes it.
     * @param foodbankObj
     * @return
     */
    @Path("/add")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addFoodbank(FoodbankObject foodbankObj){

        Connection conn = null;
        PreparedStatement pstmt = null;

        try{
            //get a connection
            conn = connectionService.getConnection();

            //set the prepared statement
            pstmt = conn.prepareStatement("INSERT INTO " +
                    "foodbank(parent_site) " +
                    "VALUES (?)");
            pstmt.setInt(1, foodbankObj.getParent_site());

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

        String output = foodbankObj.toString();

        return Response.status(200).entity(output).build();
    }

    /**
     * Takes the site id as a param and delete the corresponding foodbank
     * @param parentSite
     * @return
     */
    @Path("/delete/{id}")
    @DELETE
    public Response deleteFoodbank(@PathParam("id") int parentSite){

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            //get a connection
            conn = connectionService.getConnection();

            //set the prepared statement
            pstmt = conn.prepareStatement("DELETE FROM foodbank " +
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

    /**
     * This is a method that first turns the request from
     * json to a RequestObject. From there it creates a db connection
     * and creates a query. The query gets its values from the
     * requestObj and executes it.
     * @param requestObj
     * @return
     */
    @Path("/request/create")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createRequest(RequestObject requestObj){

        Connection conn = null;
        PreparedStatement pstmt = null;

        try{
            //get a connection
            conn = connectionService.getConnection();

            //set the prepared statement
            pstmt = conn.prepareStatement("INSERT INTO " +
                    " request(requesting_user, request_item_id, requested_units)" +
                    " VALUES (?, ?, ?)");
            pstmt.setString(1, requestObj.getRequesting_user());
            pstmt.setInt(2, requestObj.getRequested_item_id());
            pstmt.setInt(3, requestObj.getRequested_units());

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

        String output = requestObj.toString();

        return Response.status(200).entity(output).build();
    }

    /**
     * This is a method that retrieves requests and returns json/jsonArray
     * @param site
     * @return
     */
    @Path("/request/status/{site}")
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    public String getRequestStatus(@PathParam("site") String site) {

        Connection conn = null;
        PreparedStatement pstmt = null;
        JSONObject result = new JSONObject();
        JSONArray jArr = new JSONArray();
        ResultSet rset;
        String output = "";

        try{
            //get a connection
            conn = connectionService.getConnection();

            //set the prepared statement
            pstmt = conn.prepareStatement("SELECT a.request_id, b.item_name, a.requested_units, a.requesting_user, " +
                    "a.fulfilled_units, c.site_name, a.approving_user, a.request_status, b.parent_foodbank " +
                    "FROM request a JOIN item b ON a.request_item_id=b.item_id " +
                    "JOIN site c ON b.parent_foodbank=c.site_number " +
                    "JOIN users d ON a.requesting_user=d.user_name " +
                    "WHERE site_managing = ? ");
            pstmt.setString(1, site);

            //excecute the query
            rset = pstmt.executeQuery();

            //build json obj from rset
            while(rset.next()){
                //create the obj we will write to
                JSONObject jObj = new JSONObject();
                //write the data to obj
                jObj.put("request_id", rset.getInt("request_id"));
                jObj.put("item_name", rset.getString("item_name"));
                jObj.put("requested_units", rset.getInt("requested_units"));
                jObj.put("fulfilled_units", rset.getInt("fulfilled_units"));
                jObj.put("fulfilling_foodbank", rset.getString("site_name"));
                jObj.put("requesting_user", rset.getString("requesting_user"));
                jObj.put("approving_user", rset.getString("approving_user"));
                jObj.put("request_status", rset.getString("request_status"));
                jObj.put("parent_foodbank", rset.getInt("parent_foodbank"));

                // Generate Cancellable variable based on status
                if(jObj.get("request_status").equals("unable") || jObj.get("request_status").equals("full") || jObj.get("request_status").equals("partial")){
                    jObj.put("cancellable", false);
                } else {
                    jObj.put("cancellable", true);
                }

                //store that obj in an array
                jArr.add(jObj);
            }
            //wrap the array in an obj and return this below
            result.put("requests", jArr);

        }catch(SQLException sqlEx){
            sqlEx.printStackTrace();
        }finally{
            commonDB.closeConnections(conn, pstmt);
        }

        output = result.toJSONString();

        return output;
    }

    /**
     * This is a method that retrieves requests and returns json/jsonArray
     * @param parent_site
     * @return
     */
    @Path("/request/outstanding/{parent_site}")
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    public String getOutstandingRequests(@PathParam("parent_site") String parent_site) {

        Connection conn = null;
        PreparedStatement pstmt = null;
        JSONObject result = new JSONObject();
        JSONArray jArr = new JSONArray();
        ResultSet rset;
        String output = "";

        try{
            //get a connection
            conn = connectionService.getConnection();

            //set the prepared statement
            pstmt = conn.prepareStatement("SELECT a.request_id, b.item_name, a.requested_units, " +
                    "a.fulfilled_units, d.site_name, b.units, a.request_status " +
                    "FROM request a JOIN item b ON a.request_item_id=b.item_id " +
                    "JOIN users c ON a.requesting_user=c.user_name " +
                    "JOIN site d ON c.site_managing=d.site_number " +
                    "WHERE a.request_status = 'pending' " +
                    "AND b.parent_foodbank = ? ");
            pstmt.setString(1, parent_site);

            //excecute the query
            rset = pstmt.executeQuery();

            //build json obj from rset
            while(rset.next()){
                //create the obj we will write to
                JSONObject jObj = new JSONObject();
                //write the data to obj
                jObj.put("request_id", rset.getInt("request_id"));
                jObj.put("item_name", rset.getString("item_name"));
                jObj.put("requested_units", rset.getInt("requested_units"));
                jObj.put("fulfilled_units", rset.getInt("fulfilled_units"));
                jObj.put("requesting_foodbank", rset.getString("site_name"));
                jObj.put("available_units", rset.getInt("units"));
                jObj.put("request_status", rset.getString("request_status"));

                //store that obj in an array
                jArr.add(jObj);
            }
            //wrap the array in an obj and return this below
            result.put("requests", jArr);

        }catch(SQLException sqlEx){
            sqlEx.printStackTrace();
        }finally{
            commonDB.closeConnections(conn, pstmt);
        }

        output = result.toJSONString();

        return output;
    }

    /**
     * This is a method that retrieves requests and returns json/jsonArray
     * @param request_id
     * @return
     */
    @Path("/request/fill/{request_id}")
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    public String getFillRequest(@PathParam("request_id") String request_id) {

        Connection conn = null;
        PreparedStatement pstmt = null;
        //create the obj we will write to
        JSONObject jObj = new JSONObject();
        ResultSet rset;
        String output = "";

        try{
            //get a connection
            conn = connectionService.getConnection();

            //set the prepared statement
            pstmt = conn.prepareStatement("SELECT a.request_id, b.item_name, a.requested_units, " +
                    "b.item_id, a.fulfilled_units, d.site_name, b.units, a.request_status " +
                    "FROM request a JOIN item b ON a.request_item_id=b.item_id " +
                    "JOIN users c ON a.requesting_user=c.user_name " +
                    "JOIN site d ON c.site_managing=d.site_number " +
                    "WHERE a.request_status = 'pending' " +
                    "AND a.request_id = ? ");
            pstmt.setString(1, request_id);

            //excecute the query
            rset = pstmt.executeQuery();

            if(!rset.isBeforeFirst()){
                output = "";
            }else {
                rset.next();
                //write the data to obj
                jObj.put("request_id", rset.getInt("request_id"));
                jObj.put("item_name", rset.getString("item_name"));
                jObj.put("requested_units", rset.getInt("requested_units"));
                jObj.put("item_id", rset.getInt("item_id"));
                jObj.put("fulfilled_units", rset.getInt("fulfilled_units"));
                jObj.put("requesting_foodbank", rset.getString("site_name"));
                jObj.put("available_units", rset.getInt("units"));
                jObj.put("request_status", rset.getString("request_status"));
            }

        }catch(SQLException sqlEx){
            sqlEx.printStackTrace();
        }finally{
            commonDB.closeConnections(conn, pstmt);
        }

        output = jObj.toJSONString();

        return output;
    }

    /**
     * This is a method that updates Request upon fill
     * And if makes inventory level zero,
     * @param requestObj
     * @return
     */
    @Path("/request/update")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateRequest(RequestObject requestObj){
        String status = "";
        boolean delete = false;
        int units = requestObj.getAvailable_units();

        //determine what to enter in DB

        // generate status
        int filledUnitTotal = requestObj.getFulfilled_units() + requestObj.getFill_units();
        if(filledUnitTotal == requestObj.getRequested_units()){
            status = "full";
        } else
        if(filledUnitTotal < requestObj.getRequested_units() && filledUnitTotal > 0){
            status = "partial";
        } else
        if(filledUnitTotal == 0) {
            status = "unable";
        }

        // determine if item remains
        if(requestObj.getFill_units() == units) {
            delete = true;
        }

        Connection conn = null;
        PreparedStatement pstmt = null;

        try{
            //get a connection
            conn = connectionService.getConnection();

            // statement to update request
            pstmt = conn.prepareStatement("UPDATE request " +
                    "SET fulfilled_units = fulfilled_units + ?, approving_user = ?, request_status = ? " +
                    "WHERE request_id = ? ; ");
            pstmt.setInt(1, requestObj.getFill_units());
            pstmt.setString(2, requestObj.getApproving_user());
            pstmt.setString(3, status);
            pstmt.setInt(4, requestObj.getRequest_id());

            //execute the query
            pstmt.executeUpdate();

            if (delete) {
                pstmt = conn.prepareStatement("UPDATE item " +
                        "SET is_archived = 1, units = units - ? " +
                        "WHERE item_id = ? and units >= 0 ");
                pstmt.setInt(1, requestObj.getFill_units());
                pstmt.setInt(2, requestObj.getRequested_item_id());
            } else {
                //statement to run if decrementing units
                pstmt = conn.prepareStatement("UPDATE item " +
                        "SET units = units - ? " +
                        "WHERE item_id = ? and units >= 0 ");
                pstmt.setInt(1, requestObj.getFill_units());
                pstmt.setInt(2, requestObj.getRequested_item_id());
            }
            //execute the query
            pstmt.executeUpdate();

        }catch(SQLException sqlEx){
            sqlEx.printStackTrace();
            //return a bad request if needed
            String output = "Bad Request";
            return Response.status(400).entity(output).build();
        } finally{
            commonDB.closeConnections(conn, pstmt);
        }

        String output = requestObj.toString();

        return Response.status(200).entity(output).build();
    }

    /* Update item quantity
    * @param requestObj
    * @return
    */
    @Path("/request/delete")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response cancelRequest(RequestObject requestObj){

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            //get a connection
            conn = connectionService.getConnection();

            //set the prepared statement

            pstmt = conn.prepareStatement("DELETE FROM request " +
                    "WHERE request_id = ? ");
            pstmt.setInt(1, requestObj.getRequest_id());
            pstmt.executeUpdate();

        }catch(SQLException sqlEx){
            sqlEx.printStackTrace();
            //return a bad request if needed
            String output = "Bad Request";
            return Response.status(400).entity(output).build();
        }finally{
            commonDB.closeConnections(conn, pstmt);
        }

        String output = requestObj.toString();

        return Response.status(200).entity(output).build();
    }

    /* Update item quantity
    * @param requestObj
    * @return
    */
    @Path("/request/reject")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response rejectRequest(RequestObject requestObj){

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            //get a connection
            conn = connectionService.getConnection();

            //set the prepared statement
            pstmt = conn.prepareStatement("UPDATE request " +
                    "SET request_status = 'unable', approving_user = ? " +
                    "WHERE request_id = ? ");

            pstmt.setString(1, requestObj.getApproving_user());
            pstmt.setInt(2, requestObj.getRequest_id());

            pstmt.executeUpdate();

        }catch(SQLException sqlEx){
            sqlEx.printStackTrace();
            //return a bad request if needed
            String output = "Bad Request";
            return Response.status(400).entity(output).build();
        }finally{
            commonDB.closeConnections(conn, pstmt);
        }

        String output = requestObj.toString();

        return Response.status(200).entity(output).build();
    }

    /**
     * This is a method that retrieves the current inventory and returns json/jsonArray
     * @param parentSite
     * @return
     */
    @Path("/inventory/local/{id}")
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    public String getLocalInventory(@PathParam("id") int parentSite) {

        Connection conn = null;
        PreparedStatement pstmt = null;
        JSONObject result = new JSONObject();
        JSONArray jArr = new JSONArray();
        ResultSet rset;
        String output = "";

        try{
            //get a connection
            conn = connectionService.getConnection();

            //set the prepared statement
            pstmt = conn.prepareStatement("SELECT a.*, b.site_name, " +
                    "CASE WHEN a.supply_type <=> null THEN 'food' ELSE 'supply' END as foodorsupply " +
                    "FROM item a JOIN site b ON a.parent_foodbank=b.site_number " +
                    "WHERE parent_foodbank = ? and a.is_archived = 0");
            pstmt.setInt(1, parentSite);

            //excecute the query
            rset = pstmt.executeQuery();

            //build json obj from rset
            while(rset.next()){
                //create the obj we will write to
                JSONObject jObj = new JSONObject();
                //write the data to obj
                jObj.put("item_id", rset.getInt("item_id"));
                jObj.put("units", rset.getInt("units"));
                jObj.put("exp_date", rset.getString("exp_date"));
                jObj.put("item_name", rset.getString("item_name"));
                jObj.put("parent_foodbank", rset.getInt("parent_foodbank"));
                jObj.put("storage_type", rset.getString("storage_type"));
                jObj.put("supply_type", rset.getString("supply_type"));
                jObj.put("food_type", rset.getString("food_type"));
                jObj.put("site_name", rset.getString("site_name"));
                jObj.put("foodorsupply", rset.getString("foodorsupply"));

                //store that obj in an array
                jArr.add(jObj);
            }
            //wrap the array in an obj and return this below
            result.put("inventory", jArr);

        }catch(SQLException sqlEx){
            sqlEx.printStackTrace();
        }finally{
            commonDB.closeConnections(conn, pstmt);
        }

        output = result.toJSONString();

        return output;
    }
    /**
     * This is a method that retrieves the current inventory and returns json/jsonArray
     * @param parentSite
     * @return
     */
    @Path("/inventory/external/{id}")
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    public String getExternalInventory(@PathParam("id") int parentSite) {

        Connection conn = null;
        PreparedStatement pstmt = null;
        JSONObject result = new JSONObject();
        JSONArray jArr = new JSONArray();
        ResultSet rset;
        String output = "";

        try{
            //get a connection
            conn = connectionService.getConnection();

            //set the prepared statement
            pstmt = conn.prepareStatement("SELECT a.*, b.site_name, " +
                    "CASE WHEN a.supply_type <=> null THEN 'food' ELSE 'supply' END as foodorsupply " +
                    "FROM item a JOIN site b ON a.parent_foodbank=b.site_number " +
                    "WHERE parent_foodbank != ? and a.is_archived = 0");
            pstmt.setInt(1, parentSite);

            //excecute the query
            rset = pstmt.executeQuery();

            //build json obj from rset
            while(rset.next()){
                //create the obj we will write to
                JSONObject jObj = new JSONObject();
                //write the data to obj
                jObj.put("item_id", rset.getInt("item_id"));
                jObj.put("units", rset.getInt("units"));
                jObj.put("exp_date", rset.getString("exp_date"));
                jObj.put("item_name", rset.getString("item_name"));
                jObj.put("parent_foodbank", rset.getInt("parent_foodbank"));
                jObj.put("storage_type", rset.getString("storage_type"));
                jObj.put("supply_type", rset.getString("supply_type"));
                jObj.put("food_type", rset.getString("food_type"));
                jObj.put("site_name", rset.getString("site_name"));
                jObj.put("foodorsupply", rset.getString("foodorsupply"));

                //store that obj in an array
                jArr.add(jObj);
            }
            //wrap the array in an obj and return this below
            result.put("inventory", jArr);
            System.out.println(result);

        }catch(SQLException sqlEx){
            sqlEx.printStackTrace();
        }finally{
            commonDB.closeConnections(conn, pstmt);
        }

        output = result.toString();

        return output;
    }
    /**
     * This is a method that retrieves inventory based on search content
     * @param searchObj
     * @return
     */
    @Path("/inventory/search")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public String getSearchInventory(InvSearchObject searchObj) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        JSONObject result = new JSONObject();
        JSONArray jArr = new JSONArray();
        ResultSet rset;
        String output = "";
        StringBuilder sb = new StringBuilder();
        boolean stor_type = false;
        boolean footype = false;
        boolean suptype = false;
        boolean mindate = false;
        boolean maxdate = false;
        java.sql.Date expminsql = null;
        java.sql.Date expmaxsql = null;

        String searchText = searchObj.getSearchName();

        try{
            //get a connection
            conn = connectionService.getConnection();

            int i = 2;
            sb.append("SELECT a.*, b.site_name, ");
            sb.append("CASE WHEN a.supply_type <=> null THEN 'food' ELSE 'supply' END as foodorsupply ");
            sb.append("FROM item a JOIN site b ON a.parent_foodbank=b.site_number ");
            // Determine if local or external search
            if(searchObj.isLocalSearch()){
                sb.append("WHERE a.parent_foodbank = ? ");
            } else {
                sb.append("WHERE a.parent_foodbank != ? ");
            }
            // archived?
            sb.append("and a.is_archived = ? ");
            // keyword search
            sb.append("and a.item_name LIKE ? ");
            // storage type
            if(searchObj.getStorageType().length() > 0) {
                sb.append("and a.storage_type LIKE ? ");
                stor_type = true;
            }
            // food or supply
            if(Objects.equals(searchObj.getFoodSupply(),"food")) {
                sb.append("and a.supply_type <=> null ");
            } else
                if(Objects.equals(searchObj.getFoodSupply(),"supply")) {
                sb.append("and a.food_type <=> null ");
            }
            // supply category
            String str1 = searchObj.getSupplyType();
            if(str1.length() > 0) {
                sb.append("and a.supply_type LIKE ? ");
            }
            // food category
            String str2 = searchObj.getFoodType();
            if(str2.length() > 0) {
                sb.append("and a.food_type LIKE ? ");
            }
            // parent site
            String str3 = searchObj.getParentSite();
            if(str3.length() > 0 && searchObj.getParentSite() != null) {
                sb.append("and a.parent_foodbank LIKE ? ");
            }
            // min date
            if (searchObj.getExpDateMin() != null) {
                java.util.Date expminjs = searchObj.getExpDateMin();
                expminsql = convUtiltoSql(expminjs);
                sb.append("and a.exp_date >= ? ");
                mindate = true;
            }
            // max date
            if (searchObj.getExpDateMax() != null) {
                java.util.Date expmaxjs = searchObj.getExpDateMax();
                expmaxsql = convUtiltoSql(expmaxjs);
                sb.append("and a.exp_date <= ? ");
                maxdate = true;
            }

            String compiledstatement = sb.toString();
            pstmt = conn.prepareStatement(compiledstatement);
            // Site
            pstmt.setInt(1, searchObj.getSite_id());
            // archived?
            pstmt.setBoolean(i++, searchObj.isArchived());
            // Search text
            pstmt.setString(i++,"%" + searchText + "%");
            // Storage type
            if(stor_type) {
                pstmt.setString(i++, searchObj.getStorageType());
            }
            // Supply type
            if(str1.length() > 0) {
                pstmt.setString(i++, searchObj.getSupplyType());
            }
            // Food type
            if(str2.length() > 0) {
                pstmt.setString(i++, searchObj.getFoodType());
            }
            // Parent Site
            if(str3.length() > 0 && searchObj.getParentSite() != null) {
                pstmt.setString(i++, searchObj.getParentSite());
            }
            // Search Dates
            if(mindate) {
                pstmt.setDate(i++, expminsql);
            }
            if(maxdate) {
                pstmt.setDate(i, expmaxsql);
            }

            //excecute the query
            rset = pstmt.executeQuery();

            //build json obj from rset
            while(rset.next()){
                //create the obj we will write to
                JSONObject jObj = new JSONObject();
                //write the data to obj
                jObj.put("item_id", rset.getInt("item_id"));
                jObj.put("units", rset.getInt("units"));
                jObj.put("exp_date", rset.getString("exp_date"));
                jObj.put("item_name", rset.getString("item_name"));
                jObj.put("parent_foodbank", rset.getInt("parent_foodbank"));
                jObj.put("storage_type", rset.getString("storage_type"));
                jObj.put("supply_type", rset.getString("supply_type"));
                jObj.put("food_type", rset.getString("food_type"));
                jObj.put("site_name", rset.getString("site_name"));
                jObj.put("foodorsupply", rset.getString("foodorsupply"));

                //store that obj in an array
                jArr.add(jObj);
            }
            //wrap the array in an obj and return this below
            result.put("inventory", jArr);

        }catch(SQLException sqlEx) {
            sqlEx.printStackTrace();
        }finally{
            commonDB.closeConnections(conn, pstmt);
        }

        output = result.toString();

        return output;
    }

    /**
     * This is a method that retrieves the current inventory and returns json/jsonArray
     * @param itemID
     * @return
     */
    @Path("/inventory/item/{id}")
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    public String getInventoryItem(@PathParam("id") int itemID) {

        Connection conn = null;
        PreparedStatement pstmt = null;
        JSONObject jObj = new JSONObject();
        ResultSet rset;
        String output = "";

        try{
            //get a connection
            conn = connectionService.getConnection();

            //set the prepared statement
            pstmt = conn.prepareStatement("SELECT a.*, b.site_name, " +
                    "CASE WHEN a.supply_type <=> null THEN 'food' ELSE 'supply' END as foodorsupply " +
                    "FROM item a JOIN site b ON a.parent_foodbank=b.site_number " +
                    "WHERE a.item_id = ?");
            pstmt.setInt(1, itemID);

            //excecute the query
            rset = pstmt.executeQuery();

            if(!rset.isBeforeFirst()){
                output = "";
            }else {
                rset.next();
                //write the data to obj
                jObj.put("item_id", rset.getInt("item_id"));
                jObj.put("units", rset.getInt("units"));
                jObj.put("exp_date", rset.getString("exp_date"));
                jObj.put("item_name", rset.getString("item_name"));
                jObj.put("parent_foodbank", rset.getInt("parent_foodbank"));
                jObj.put("storage_type", rset.getString("storage_type"));
                jObj.put("supply_type", rset.getString("supply_type"));
                jObj.put("food_type", rset.getString("food_type"));
                jObj.put("site_name", rset.getString("site_name"));
                jObj.put("foodorsupply", rset.getString("foodorsupply"));
            }
            //wrap the array in an obj and return this below

        }catch(SQLException sqlEx){
            sqlEx.printStackTrace();
        }finally{
            commonDB.closeConnections(conn, pstmt);
        }

        output = jObj.toString();

        return output;
    }

    /**
     * This is a method that first turns the request from
     * json to a RequestObject. From there it creates a db connection
     * and creates a query. The query gets its values from the
     * requestObj and executes it.
     * @param requestObj
     * @return
     */
    @Path("/inventory/insert")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response insertItem(ItemObject requestObj){

        Connection conn = null;
        PreparedStatement pstmt = null;

        // convert Java.util.Date object from Json to java.sql.date object fo insertion
        java.util.Date Jdate = requestObj.getExp_date();
        java.sql.Date Sdate = convUtiltoSql(Jdate);

        try{
            //get a connection
            conn = connectionService.getConnection();

            //set the prepared statement
            pstmt = conn.prepareStatement("INSERT INTO " +
                    "item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)");
            pstmt.setInt(1, requestObj.getUnits());
            pstmt.setDate(2, Sdate);
            pstmt.setString(3, requestObj.getItem_name());
            pstmt.setInt(4, requestObj.getParent_site());
            pstmt.setString(5, requestObj.getStorage_type());
            pstmt.setString(6, requestObj.getSupply_type());
            pstmt.setString(7, requestObj.getFood_type());

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

        String output = requestObj.toString();

        return Response.status(200).entity(output).build();
    }

    /* Update item quantity
     * @param ItemObj
     * @return
     */

    @Path("/inventory/update")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateItem(ItemObject itemObj){

        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean archivebit;

        if(itemObj.getUnits() > 0){
            archivebit = false;
        } else {
            archivebit = true;
        }

        try {
            //get a connection
            conn = connectionService.getConnection();

            //set the prepared statement
            pstmt = conn.prepareStatement("UPDATE item " +
                    "SET units = ?, is_archived = ? " +
                    "WHERE item_id = ? ");

            pstmt.setInt(1, itemObj.getUnits());
            pstmt.setBoolean(2, archivebit);
            pstmt.setInt(3, itemObj.getItem_id());

            pstmt.executeUpdate();

        }catch(SQLException sqlEx){
            sqlEx.printStackTrace();
            //return a bad request if needed
            String output = "Bad Request";
            return Response.status(400).entity(output).build();
        }finally{
            commonDB.closeConnections(conn, pstmt);
        }

        String output = itemObj.toString();

        return Response.status(200).entity(output).build();
    }

    /* Update item quantity
    * @param ItemObj
    * @return
    */
    @Path("/inventory/delete")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response deleteItem(ItemObject itemObj){

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            //get a connection
            conn = connectionService.getConnection();

            //set the prepared statement
            pstmt = conn.prepareStatement("UPDATE item " +
                    "SET is_archived = 1 " +
                    "WHERE item_id = ? ");

            pstmt.setInt(1, itemObj.getItem_id());

            pstmt.executeUpdate();

        }catch(SQLException sqlEx){
            sqlEx.printStackTrace();
            //return a bad request if needed
            String output = "Bad Request";
            return Response.status(400).entity(output).build();
        }finally{
            commonDB.closeConnections(conn, pstmt);
        }

        String output = itemObj.toString();

        return Response.status(200).entity(output).build();
    }

    private static java.sql.Date convUtiltoSql(java.util.Date cDate){
        java.sql.Date sDate = new java.sql.Date(cDate.getTime());
        return sDate;
    }
}
