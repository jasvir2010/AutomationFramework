package reporting;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;

import dbutilities.DBConnection;
import dbutilities.DMLOperations;

/**
 * March 4, 2022
 * @author saurabh.shukla
 */

public class AuditResults
{

	private DMLOperations dmlop = new DMLOperations();
	private static Connection conn;
	private String database;
	
	public AuditResults(String dbServer, String dbname, String userName, String password)
	{
		DBConnection connection = new DBConnection();
		
		database = dbname;
		
		conn = connection.getDBConnection(dbServer, userName, password);
	}

	public void auditResultsInTable(String testSuite, int totalTCs, int passedTCs, int failedTCs, int skippedTCs,
			
			String runBy, String runThrough, boolean auditFlag)
	{
		if(auditFlag) {
		LocalDate today = LocalDate.now();
		int day = today.getDayOfMonth();
		int month = today.getMonthValue();
		int year = today.getYear();
		int totalExecutedTCs = totalTCs - skippedTCs;
		String insertQuery = "insert into ["+database+"].[dbo].[TestRunAuditTable](TestSuite, TotalTestCases, TotalTestCasesExecuted, PassedTestCases, FailedTestCases, SkippedTestCases, Day, Month, Year, RunBy, RunThrough, Creation_Timestamp) values ("
				+"'" + testSuite + "','" + totalTCs + "','" + totalExecutedTCs + "','" + passedTCs + "','"
				+ failedTCs + "','" + skippedTCs + "','" + day + "','" + month + "','" + year + "','" + runBy + "','"
				+ runThrough + "',  getutcdate()  )";
		dmlop.update(conn, insertQuery);
		}
	}

	public int fetchRunIdFromTable()
	{
		int runId = -1;
		List<List<String>> lastetRunId = dmlop.select(conn,
				"select top 1 runId from ["+database+"].dbo.[TestRunAuditTable] order by Creation_Timestamp desc");
		
		runId = lastetRunId.isEmpty()? 1: Integer.parseInt(lastetRunId.get(0).get(0)) + 1;

		return runId;
	}
}

