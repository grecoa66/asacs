package com.gatech.asacs;

import org.json.simple.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by alexgreco on 3/13/17.
 */
@Path("services")
public class ManageServices {

    private DBConnectionService connectionService = new DBConnectionService();
    private Foodbank foodbank = new Foodbank();
    private FoodPantry pantry = new FoodPantry();
    private Shelter shelter = new Shelter();
    private SoupKitchen kitchen = new SoupKitchen();

    /**
     * This method is going to call all the service java
     * classes and query to find this site's services.
     * When if gets all of the results back from the other
     * api classes it will build a final result obj.
     * When this api call returns a result, true means that
     * the that service exists. False would mean that the user
     * can create a service of that category
     * @param parentSite
     * @return
     */
    @Path("/{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getSitesServices(@PathParam("id") int parentSite){
        String result;

        JSONObject jObj = new JSONObject();

        //query for foodbank with parentSite
        String fbResult;
        fbResult = foodbank.getFoodbankById(parentSite);

        if(fbResult == null || fbResult.isEmpty() || fbResult.equals("{}")){
            jObj.put("foodbank", false);
        }else{
            jObj.put("foodbank", true);
        }

        //query for food pantry with parent_site
        String fpResult;
        fpResult = pantry.getPantryById(parentSite);

        if(fpResult == null || fpResult.isEmpty() || fpResult.equals("{}")){
            jObj.put("pantry", false);
        }else{
            jObj.put("pantry", true);
        }

        //query for shelter with parent_site
        String sResult;
        sResult = shelter.getShelterById(parentSite);
        if(sResult == null || sResult.isEmpty() || sResult.equals("{}")){
            jObj.put("shelter", false);
        }else{
            jObj.put("shelter", true);
        }

        //query for soup_kitchen with parent_site
        String skResult;
        skResult = kitchen.getKitchenById(parentSite);
        if(skResult == null || skResult.isEmpty() || skResult.equals("{}")){
            jObj.put("kitchen", false);
        }else{
            jObj.put("kitchen", true);
        }

        result = jObj.toJSONString();
        return result;
    }

    @Path("/add")
    @POST
    public void addService(){

    }

    public static void main(String[] args){
        ManageServices serv = new ManageServices();

        String answer = serv.getSitesServices(5);

        System.out.print(answer);
    }
}
