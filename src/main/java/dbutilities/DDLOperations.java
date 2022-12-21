package dbutilities;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * March 4, 2022
 * @author saurabh.shukla
 */


public class DDLOperations
{

	Logger logger;
	public DDLOperations() {
		
		logger= LogManager.getLogger("DDL Operation Logs");
	}
	
	/**
	 * Method to create table on DB server
	 * @param conn - SQL Connection
	 * @param columns
	 * @param tblName
	 * @param columnSize
	 * @return
	 */
    public boolean createTable(Connection conn, List<String> columns, String tblName, int columnSize)
    {
    	try(Statement stmt=conn.createStatement()){
			logger.info("Creating table - {} ",tblName);
			
			dropTable(conn, tblName);
			
			StringBuilder query = new StringBuilder();
			
			query.append("create table " +tblName +" (");
			
			for(String col:columns){
				
				query.append(", [" +col +"] varchar("+columnSize+")");
			}
			
			query.append(")");
			
			String insertQuery = query.toString().replaceFirst(",", "");
			
			stmt.executeUpdate(insertQuery);
			
			logger.info("Table created");
			
			return true;
		}
		catch(SQLException e){
			
			logger.error("Failed to create table");
			logger.error("SQL Exception is: {}",e.getMessage());
			if(e.getMessage().contains("There is already an object named")){
				logger.error("Not able to create table");
			}
		}
    	return false;
    }
    
    /**
	 * This method is used to drop table from data base if exists.
	 * @param tblName
	 * @return
	 */
	public boolean dropTable(Connection conn, String tblName){
		
		String dropQuery="If OBJECT_ID(\'"+tblName+"\') is not null drop table " +tblName;
		
		try(Statement stmt=conn.createStatement()){
			
			stmt.execute(dropQuery);
			logger.info("Table {} Dropped", tblName);
			
			return true;
		}
		catch(SQLException e){
			
			if(e.getMessage().contains("Cannot drop the table")){
				
				//logger.error("Unable to drop table, kindly drop table manually or re-run the suite.");
			}
			return false;
		}
	}
}
