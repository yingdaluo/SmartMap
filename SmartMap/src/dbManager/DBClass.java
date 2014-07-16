package dbManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class DBClass {
	private HashMap<String, Connection> DBConns = new HashMap<String, Connection>(); 

	public ResultSet Select(String sqlStmt, String dbName) throws Exception
    {            
       ResultSet executionResult = null;
       java.sql.Statement s = null;     
       Boolean connectError = false;   // Error flag
       if(!DBConns.containsKey(dbName)){
    	   try
           { 
              Class.forName("com.mysql.jdbc.Driver");
              
              String sourceURL = "jdbc:mysql://localhost:3306/" +dbName;
              
              ObtainDbConn(sourceURL, dbName);
               
            } catch (Exception e) {

                connectError = true;    
                throw e;

            } // end try-catch
       }
        if (!connectError)
        {
        	Connection DBConn = DBConns.get(dbName);
            try
            {
                s = DBConn.createStatement();
                executionResult = s.executeQuery(sqlStmt);

            } catch (Exception e) {

                throw e;

            } // try

        } //execute SQL check
       
        return executionResult;
    }

	public int Update(String sqlStmt, String dbName) throws Exception
    {            
       int executionResult = 0 ;
       java.sql.Statement s = null;     
       Boolean connectError = false;   // Error flag
       
       if(!DBConns.containsKey(dbName)){
    	   try
           { 
              Class.forName("com.mysql.jdbc.Driver");
              
              String sourceURL = "jdbc:mysql://localhost:3306/" +dbName;
              
              ObtainDbConn(sourceURL, dbName);
               
            } catch (Exception e) {

                connectError = true;    
                throw e;

            } // end try-catch
       }
        if (!connectError)
        {
            try
            {
            	Connection DBConn = DBConns.get(dbName);
                s = DBConn.createStatement();
                executionResult = s.executeUpdate(sqlStmt);

            } catch (Exception e) {

                throw e;

            } // try

        } //execute SQL check
       
        return executionResult;
    }
    
	private void ObtainDbConn(String sourceURL, String dbName) throws SQLException {
         Connection  DBConn = DriverManager.getConnection(sourceURL,"smartmap","smartmap_pass");
         DBConns.put(dbName, DBConn);
    }
	
	
//	public static void main(String[] args) {
//		DBClass dbclass = new DBClass();
//		// String sqlStmt = "INSERT INTO userlocations (user_name,longitude,latitude) VALUE ('tester2', 20.0, 20.0)";
//		String sqlStmt = "UPDATE userlocations SET longitude=15.0,latitude=15.0 WHERE user_name='tester2'";
//		String dbName = "smartmap_node1";
//		try {
//			dbclass.Update(sqlStmt, dbName);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		System.out.println("Update done.");
//
//	}

}
