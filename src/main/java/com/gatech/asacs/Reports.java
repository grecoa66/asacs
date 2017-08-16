package com.gatech.asacs;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by eren on 4/5/17.
 */

@Path("/reports")
public class Reports {
    CommonDB commonDB = new CommonDB();
    DBConnectionService connectionService = new DBConnectionService();

    /* The available bunks/rooms report provides a list of all shelters that have available bunks or rooms.
    (shelters with zero availability are not shown). For each shelter, it displays the name, site location,
    phone number, hours of operation and conditions, as well as the number of male/female/mixed bunks
    and rooms available. If no bunks/rooms are available throughout the entire ASA system, a message
    “Sorry, all shelters are currently at maximum capacity.” should be shown instead
    */
    @Path("/availableBeds")
    @GET
    public String getAvailableBeds() {
        JSONObject result = new JSONObject();
        JSONArray jArr = new JSONArray();
        Connection conn = null;
        PreparedStatement pstmt = null, pstmt1 = null;
        ResultSet rset, rset1;
        int curSite = 0, curRooms = 0, curMale = 0, curFemale = 0, curMixed = 0;
        String curName, curLocation, curPhone, curHours, curCondition, bunkType;

        try {
            //get a connection
            conn = connectionService.getConnection();

            //set the query for the sites
            pstmt = conn.prepareStatement("SELECT * FROM site ");
            //trigger query
            rset = pstmt.executeQuery();

            //check if the result is empty or not
            if (!rset.isBeforeFirst()) {
                return null;
            } else {
                while (rset.next()) {
                    // get the site details set
                    curSite = rset.getInt("site_number");
                    curName = rset.getString("site_name");
                    curLocation = rset.getString("street_address") + ", " +
                            rset.getString("city") + ", " +
                            rset.getString("state") + " " +
                            rset.getString("zip_code");
                    curPhone = rset.getString("primary_phone");

                    //set the query for shelter details
                    pstmt1 = conn.prepareStatement("SELECT hours, shelter_condition " +
                            "FROM shelter where parent_site = ?");
                    pstmt1.setInt(1, curSite);
                    //trigger query
                    curHours = "";
                    curCondition = "";
                    rset1 = pstmt1.executeQuery();
                    while (rset1.next()) {
                        curHours = rset1.getString("hours");
                        curCondition = rset1.getString("shelter_condition");
                    }

                    //set the query for rooms
                    pstmt1 = conn.prepareStatement("SELECT count(*) as rooms FROM room " +
                            "WHERE parent_shelter = ? AND occupied = FALSE");
                    pstmt1.setInt(1, curSite);
                    //trigger query
                    rset1 = pstmt1.executeQuery();
                    while (rset1.next()) {
                        curRooms = rset1.getInt("rooms");
                    }

                    //set the query for bunks
                    curMale = 0;
                    curFemale = 0;
                    curMixed = 0;
                    pstmt1 = conn.prepareStatement("SELECT bunk_type, count(*) as bunks " +
                            "FROM bunk WHERE parent_shelter = ? AND occupied = FALSE GROUP BY bunk_type");
                    pstmt1.setInt(1, curSite);
                    //trigger query
                    rset1 = pstmt1.executeQuery();
                    while (rset1.next()) {
                        bunkType = rset1.getString("bunk_type");
                        if (bunkType.equalsIgnoreCase("female only")) {
                            curFemale = rset1.getInt("bunks");
                        } else if (bunkType.equalsIgnoreCase("male only")) {
                            curMale = rset1.getInt("bunks");
                        } else if (bunkType.equalsIgnoreCase("mixed")) {
                            curMixed = rset1.getInt("bunks");
                        }
                    }

                    // now let's see if all or some have zero
                    if ((curRooms == 0) && (curFemale == 0) && (curMale == 0) && (curMixed == 0)) {
                        // nothing available, do not include site in final report
                    } else {
                        // add site to the final result
                        JSONObject jObj = new JSONObject();
                        jObj.put("site_number", curSite);
                        jObj.put("name", curName);
                        jObj.put("location", curLocation);
                        jObj.put("phone", curPhone);
                        jObj.put("hours", curHours);
                        jObj.put("condition", curCondition);
                        jObj.put("rooms", curRooms);
                        jObj.put("male_only", curMale);
                        jObj.put("female_only", curFemale);
                        jObj.put("mixed", curMixed);

                        //add result obj to an array of objects
                        jArr.add(jObj);
                    }
                }
                //wrap the results in a parent obj
                result.put("beds", jArr);
            }

        } catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
        } finally {
            commonDB.closeConnections(conn, pstmt);
            commonDB.closeConnections(conn, pstmt1);
        }

        String finalResult = result.toJSONString();

        return finalResult;
    }

    /* This report shows a simple number of the meals remaining in inventory in all food banks in the ASA
    system. (It does not consider food already at soup kitchens or food pantries and no longer in the food
    bank inventory system.) In addition to listing the maximum number of meals that can be served using
    current food bank inventory, the report explains what type of donations are most needed to provide
    more meals (Either vegetables, nuts/grains/beans, or one of Meat/seafood or Dairy/eggs.). This report
    is used for fund-raising and donation solicitation purposes, and is made available to local newscasts,
    government officials, and the general public via the web.
    The ASA defines a “meal” as one unit each of a Vegetable, nuts/grains/beans, and Meat/seafood OR
    Dairy/eggs. So a meal could consist of “Carrots, Hamburger Bun, Ground Beef” or “Potatoes, Biscuit,
    Eggs”.
    */
    @Path("/mealsRemaining")
    @GET
    public String getMealsRemaining() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rset;
        String result;
        JSONObject jObj = new JSONObject();
        int veggies = 0, nuts = 0, meats = 0, dairy = 0, protein = 0, meals = 0;
        String foodType, mostNeeded = "";

        try {
            //get a connection
            conn = connectionService.getConnection();

            //set the query for items
            // do not exclude expired items per piazza post 1230
            pstmt = conn.prepareStatement("SELECT food_type, sum(units) as items FROM item where is_archived = 0 GROUP BY food_type");
            //trigger query
            rset = pstmt.executeQuery();
            while (rset.next()) {
                foodType = rset.getString("food_type");
                if (foodType != null) {
                    if (foodType.equalsIgnoreCase("vegetables")) {
                        veggies = rset.getInt("items");
                    } else if (foodType.equalsIgnoreCase("nuts/grains/beans")) {
                        nuts = rset.getInt("items");
                    } else if (foodType.equalsIgnoreCase("meat/seafood")) {
                        meats = rset.getInt("items");
                    } else if (foodType.equalsIgnoreCase("dairy/eggs")) {
                        dairy = rset.getInt("items");
                    }
                }
            }
            protein = meats + dairy;

            if (veggies < nuts) {
                if (veggies < protein) {
                    mostNeeded = "Vegetables";
                    meals = veggies;
                } else {
                    mostNeeded = "Proteins";
                }
            } else {
                if (nuts < protein) {
                    mostNeeded = "Nuts/Grains/Beans";
                    meals = nuts;
                } else {
                    mostNeeded = "Proteins";
                }
            }

            if (mostNeeded.equalsIgnoreCase("Proteins")) {
                if (meats < dairy) {
                    mostNeeded = "Meat/Seafood";
                    meals = meats;
                } else {
                    mostNeeded = "Dairy/Eggs";
                    meals = dairy;
                }
            }

            jObj.put("meals", meals);
            jObj.put("most_needed", mostNeeded);

        } catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
        } finally {
            commonDB.closeConnections(conn, pstmt);
        }

        //format the jsonObj
        result = jObj.toJSONString();

        return result;
    }
}
