package com.gatech.asacs;

import java.io.IOException;
import java.sql.*;

/**
 * This class will make a connection to our database.
 * Call the getConnection(context) method from other api classes
 * to get a connection instance. Make sure to close the
 * connection when you are done!!!!
 *
 * Created by alexgreco on 3/12/17.
 */
public class DBConnectionService {
    //define all the constants
    public String databaseName = "ASACS39";
    public String instanceConnectionName = "asacs-cloud-sql:us-central1:asacs-6400";
    public String username = "team39";
    public String password = "team39";

    public Connection getConnection(){
        Connection conn = null;

        try{
            //form the connection string
            String connString = String.format(
                    "jdbc:mysql://google/%s?cloudSqlInstance=%s&"
                            + "socketFactory=com.google.cloud.sql.mysql.SocketFactory",
                    databaseName,
                    instanceConnectionName);

            //We need to define the DriverManager before we use it
            Class.forName("com.mysql.jdbc.Driver").newInstance();

            //Create the connection
            conn = DriverManager.getConnection(connString, username, password);
        }
        catch(SQLException sqlEx ){

            sqlEx.printStackTrace();
        }
        catch (IllegalAccessException e) {

            e.printStackTrace();
        }
        catch (InstantiationException e) {

            e.printStackTrace();
        }
        catch (ClassNotFoundException e) {

            e.printStackTrace();
        }

        return conn;
    }

    /**
     * Method to test that our DB connections are working.
     * @param args
     * @throws IOException
     * @throws SQLException
     */
    public static void main(String[] args) throws IOException, SQLException {
        DBConnectionService connectionService = new DBConnectionService();
        Connection conn = null;

        try{
            //form the connection string
            String connString = String.format(
                    "jdbc:mysql://google/%s?cloudSqlInstance=%s&"
                            + "socketFactory=com.google.cloud.sql.mysql.SocketFactory",
                    connectionService.databaseName,
                    connectionService.instanceConnectionName);

            //Create the connection
            conn = DriverManager.getConnection(connString, connectionService.username, connectionService.password);
        }
        catch(SQLException sqlEx ){

            sqlEx.printStackTrace();
        }

        try (Statement statement = conn.createStatement()) {
            ResultSet resultSet = statement.executeQuery("SHOW TABLES");
            while (resultSet.next()) {
                System.out.println("This is the database table: " + resultSet.getString(1));
            }
        }
        catch(SQLException sqlEx ){

            sqlEx.printStackTrace();
        }
    }
}