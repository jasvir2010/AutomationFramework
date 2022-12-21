package dbutilities;

import java.io.FileWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.opencsv.CSVWriter;

/**
 * March 4, 2022
 * @author saurabh.shukla
 */


public class DMLOperations
{
	Logger logger;
	public DMLOperations(){

		logger= LogManager.getLogger("DML Operations Logs");	
	}
	
	public List<List<String>> select(Connection conn, String query)
	{
		List<String> rowData = null;
		List<List<String>> dataOutput = new ArrayList<>();
		try( Statement stmt = conn.createStatement())
		{

			ResultSet rs = stmt.executeQuery(query);
			ResultSetMetaData rsmd = rs.getMetaData();
			int columnsNumber = rsmd.getColumnCount();
			while (rs.next())
			{
				rowData = new ArrayList<>();
				for (int i = 0; i < columnsNumber; i++)
				{
					rowData.add(rs.getString(i + 1));
				}
				dataOutput.add(rowData);
			}
		}
		catch (Exception e)
		{
			logger.error(e.getMessage());
		}
		return dataOutput;
	}

	public void insertData(Connection conn, List<List<String>> values, String tblName)
	{
		String query = "insert into " + tblName + " values ";
		try(Statement stmt = conn.createStatement())
		{
			for (int i = 0; i < values.size(); i++)
			{
				String rowData = "";
				query = query + ",(";
				List<String> rowValues = values.get(i);
				for (String a : rowValues)
				{
					a = a.replace("'", "");
					rowData = rowData + ", '" + a + "'";
				}
				rowData = rowData.replaceFirst(",", "");
				query = query + rowData;
				query = query + ")";
			}
			query = query.replaceFirst(",", "");

			stmt.executeUpdate(query);
		}
		catch (Exception e)
		{
			logger.error(e.getMessage());
		}
	}
	
	public boolean bulkInsert(Connection conn, List<List<String>> values, List<String>columns, String tblName, int batchSize) {
		int count = 0;

		logger.info("Inserting {} into table {}", values.size(), tblName);
		int noOfCol=columns.size();
		String sql = "insert into "+ tblName;
        values.remove(0);
		sql=sql.concat(" values (?");

		for(int i=1;i<noOfCol;i++) {
			sql=sql.concat(",?");
		}
		sql=sql.concat(")");
		try(PreparedStatement pstmt = conn.prepareStatement(sql)) {
			for(List<String> val: values) {
				for(int j=0;j<val.size();j++) {
					pstmt.setString(j+1, val.get(j));
				}
				pstmt.addBatch();

				if(++count % batchSize == 0) {
					pstmt.executeBatch();
				}
			}
			pstmt.executeBatch();

			logger.info("{} Records inserted into table {}", values.size(),tblName);
			
			conn.commit();
			
			return true;

		}
		catch(Exception e) {
			logger.error(e.getMessage());
			return false;
		}
	}
	

	public int update(Connection conn, String query)
	{
		int affetctedRows = 0;
		try(Statement stmt = conn.createStatement())
		{

			affetctedRows = stmt.executeUpdate(query);
		}
		catch (Exception e)
		{
			logger.error(e.toString());
		}
		return affetctedRows;
	}

	public boolean executeQuery(Connection conn, String query)
	{
		boolean flag = false;
		try(Statement stmt = conn.createStatement())
		{
			flag = stmt.execute(query);
		}
		catch (Exception e)
		{
			logger.error(e.toString());
		}
		return flag;
	}
	
	public Map<String,String> columnDataTypeMap(Connection conn, String table) {

		Map<String,String> typeMap =new HashMap<>();
		try(Statement stmt = conn.createStatement();) {

			ResultSet rs = stmt.executeQuery("SELECT column_name , data_type FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME=\'"+table+"\'");

			while(rs.next()) {
				typeMap.put(rs.getString("column_name"), rs.getString("data_type"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return typeMap;
	}
	
	/**
	 * 
	 * @param conn
	 * @param query
	 * @param file filename or filename with full path
	 */
	public void selectAndSaveToFile(Connection conn, String query, String file)
	{
		try( Statement stmt = conn.createStatement();
				CSVWriter csvWriter = new CSVWriter(new FileWriter(file)))
		{
			ResultSet rs = stmt.executeQuery(query);
			
			csvWriter.writeAll(rs, true);
		}
		catch (Exception e)
		{
			logger.error(e.getMessage());
		}
	}
}

