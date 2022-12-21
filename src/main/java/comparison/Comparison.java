package comparison;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.opencsv.CSVWriter;
import dbutilities.DBConnection;
import dbutilities.DDLOperations;
import dbutilities.DMLOperations;
import fileutilities.CsvReader;
import fileutilities.ExcelReader;
import fileutilities.PropertyReader;

/**
 * March 4, 2022
 * @author saurabh.shukla
 * @version 2.0.0
 *
 */

public class Comparison {

	public List<String>tbl1Columns;
	public List<String>tbl2Columns;
	private Map<String,Integer> tolerance;
	private Map<String,Float> difference;
	private List<String>columnsToIgnore;
	private Map<String, String> configMap;

	private Connection conn;

	public List<String> failedColumns;

	public final Logger logger;

	public int dataSize1;
	public int dataSize2;
	public int qaOnlyData;
	public int prodOnlyData;

	private Map<String, String> qaTypeMap;
	private Map<String, String> prodTypeMap;

	DBConnection dbc=new DBConnection();
	DMLOperations dmlOp =new DMLOperations();
	DDLOperations ddlOp =new DDLOperations();
	public CsvReader csv;

	{
		logger=LogManager.getLogger("Comparison Logging");
		failedColumns= new ArrayList<>();
		csv = new CsvReader();
	}

	public Comparison(Map<String,String>config, String configFile){

		if (config !=null)  configMap = config;
		else configMap = new PropertyReader(configFile).readAllProperty();
	}

	/**
	 *
	 * @param exportid
	 * @param file1Path
	 * @param file2Path
	 * @param primaryKeys
	 */
	public void runComparison(String exportid, String file1Path, String file2Path,
			List<String> primaryKeys) {
		List<List<String>>fileData;
		String qaTable = "";
		String prodTable = "";
		String comparisonName="";
		String comparisonQuery="";

		conn = dbc.getDBConnection(configMap.get("DBServer") , configMap.get("DBName"),configMap.get("Username"), configMap.get("Password"));

		if (primaryKeys!=null && primaryKeys.size() > 0) {
			//Reading fist file
			fileData = csv.readFiles(file1Path);

			qaTable = "QA"+ "_" + exportid;

			tbl1Columns = fileData.get(0);

			//Creating fist table
			boolean falg = ddlOp.createTable(conn, tbl1Columns, qaTable, csv.maxColWidth) ;
			//Inserting data into first table
			if (dmlOp.bulkInsert(conn, fileData, tbl1Columns, qaTable, 100000)) {

				dataSize1=fileData.size();

				logger.info("Successfully Entered data into table {}",qaTable);

				prodTable ="Prod"+ "_" + exportid;
				//Reading second file
				fileData = csv.readFiles(file2Path);

				tbl2Columns = fileData.get(0);
				//Creating second table
				ddlOp.createTable(conn, tbl2Columns, prodTable, csv.maxColWidth);
				//Inserting data into second table
				if (dmlOp.bulkInsert(conn, fileData, tbl2Columns, prodTable, 100000)) {
					dataSize2=fileData.size();
					logger.info("Successfully Entered Prod Data into Table");
					//Comparing columns of both tables
					if (checkColumnsAndPrimaryKeys(primaryKeys)) {
						comparisonName="comparison" + exportid;
						//Creating and storing comparison query
						comparisonQuery = comparisonQueries(qaTable, prodTable, comparisonName, tbl1Columns, primaryKeys, tolerance, 
								difference, configMap.getOrDefault("BaseData", "Common"));

						logger.info("Primary keys: {}", primaryKeys);
						//Writing status in individual comparison files
						if ((writeStatus(comparisonQuery, exportid, comparisonName, qaTable, prodTable, tbl1Columns, primaryKeys))) {
							logger.info("-- Comparison Completed --");
							//Dropping first table
							ddlOp.dropTable(conn,qaTable);
							//Dropping second table
							ddlOp.dropTable(conn,prodTable);
						} else {
							logger.error("Status Not Written on Comparison File");

						}
					} else {
						logger.error("######### Column Mismatch ###########");
					}
				} else {
					logger.error("Failed to Enter Data Into Table {}",prodTable);
				}

			} else {
				logger.error("Failed to Enter Data Into Table {}",qaTable);
			}
		} else {
			logger.error("Compariosn keys does not provided");
		}
	}

	/**
	 *
	 * @param exportid
	 * @param file1Path
	 * @param file2Path
	 * @param primaryKeys
	 * @throws SQLException
	 */
	public void runComparison(String uniqueCompId, String table1, String table2, List<String> tblColums,
			List<String> primaryKeys, String comparisonName)
	{

		ResultSet rs;
		String comparisonQuery = "";

		conn = dbc.getDBConnection(configMap.get("DBServer") , configMap.get("DBName"),
				configMap.get("Username"), configMap.get("Password"));

		try (Statement stmt= conn.createStatement()){
			logger.info("Checking count for table {}", table1);
			rs = stmt.executeQuery("Select count(*) \"count\" from " + table1);
			if (rs.next())
			{
				dataSize1 = Integer.parseInt(rs.getString("count"));
			}
			logger.info("Checking count for table {}", table2);
			rs = stmt.executeQuery("Select count(*) \"count\" from " + table2);
			if (rs.next())
			{
				dataSize2 = Integer.parseInt(rs.getString("count"));
			}
			if (primaryKeys!=null && !primaryKeys.isEmpty())
			{

				qaTypeMap = dmlOp.columnDataTypeMap(conn, table1);
				prodTypeMap = dmlOp.columnDataTypeMap(conn, table2);

				// Creating and storing comparison query
				comparisonQuery = comparisonQueriesNew(table1, table2, comparisonName, tblColums, primaryKeys, tolerance, configMap.getOrDefault("BaseData", "Common"));
				logger.info("Primary keys: {}", primaryKeys);

				// Writing status in individual comparison files
				if (Boolean.TRUE.equals(writeStatus(comparisonQuery, uniqueCompId, comparisonName, table1, table2, tblColums, primaryKeys)))
				{
					logger.info("-- Comparison Completed --");

					// Dropping first table
					ddlOp.dropTable(conn, table1);
					// Dropping second table
					ddlOp.dropTable(conn, table2);
				} else
				{
					logger.error("Status Not Written on Comparison File");
				}
			} else
			{
				logger.error("Compariosn keys does not provided");
			}
		}
		catch(SQLException e) {
			logger.error(e.getMessage());
		}
	}

	/**
	 * This method is used to create a comparison query based on the primary keys , tolerant keys and columns from input files.
	 * Table to Table Comparison
	 * @param tbl1
	 * @param tbl2
	 * @param comparisonTable
	 * @param col
	 * @param pKey
	 * @param tolerance
	 * @return
	 */
	public String comparisonQueries (String tbl1, String tbl2,String comparisonTable,List<String> col, List<String> pKey, 
			Map<String,Integer> tolerance, Map<String,Float> difference, String baseData ){
		String query="";
		String select="";
		col.removeAll(pKey);

		select=select.concat("select distinct ");

		String from = createBaseQuery(tbl1, tbl2, comparisonTable, baseData);

		for(String key: pKey){
			select=select.concat(" "+tbl1+".["+key+"],");

			from=from.concat(" and "+tbl1+".["+key+"]="+tbl2+".["+key+"] ");
		}

		if( columnsToIgnore!=null) {

			col.removeAll(columnsToIgnore);

		}

		for(String column:col){
			query=query.concat(", "+tbl1+".["+column+"] as "+"\"" +tbl1+" "+column+"\", "+ tbl2+".["+column+"] as "+"\"" +tbl2+" "+column+"\", ");
			if(tolerance.containsKey(column)){
				if(tolerance.get(column) <= 0 ) {

					logger.info("Ignoring Decimal Value for Column - {}", column);

					query=query.concat("CASE WHEN ( CASE WHEN "+tbl1+".["+column+"] != '' THEN  CASE WHEN CHARINDEX ('.',"+tbl1+".["+column+"]) != 0 Then Substring("+tbl1+".["+column+"],1,CHARINDEX('.',"+tbl1+".["+column+"]) -1) ELSE "+tbl1+".["+column+"] END ELSE '0.0' END ) = (CASE WHEN "+tbl2+".["+column+"] != '' THEN  CASE WHEN CHARINDEX ('.',"+tbl2+".["+column+"]) != 0 Then Substring("+tbl2+".["+column+"],1,CHARINDEX('.',"+tbl2+".["+column+"]) -1) ELSE "+tbl2+".["+column+"] END ELSE '0.0' END) THEN \'Pass\' ELSE \'Fail\' END AS ["+ column+"ComparisonStatus]\n");
				}

				else {
					if(difference.get(column)!=null) {

						query=query.concat("CASE WHEN( abs (( CASE WHEN "+tbl1+".["+column+"] != '' THEN cast( cast ("+tbl1+".["+column+"] as float) as decimal (38, "+tolerance.get(column)+")) ELSE '0.0' END ) - (CASE WHEN "+tbl2+".["+column+"] != '' THEN cast( cast ("+tbl2+".["+column+"] as float) as decimal (38, "+tolerance.get(column)+")) ELSE '0.0' END)) < "+difference.get(column)+" ) THEN \'Pass\' ELSE \'Fail\' END AS ["+ column+"ComparisonStatus]\n");
					}
					else {
						query = query.concat("CASE WHEN ( CASE WHEN " + tbl1 + ".[" + column + "] != '' THEN cast( cast ("+tbl1+".["+column+"] as float) as decimal (38, "+tolerance.get(column)
						+ ")) ELSE '0.0' END ) = (CASE WHEN " + tbl2 + ".[" + column + "] != '' THEN cast( cast ("+tbl2+".["+column+"] as float) as decimal (38, "+tolerance.get(column)
						+ ")) ELSE '0.0' END) THEN \'Pass\' ELSE \'Fail\' END AS [" + column + "ComparisonStatus]\n");

					}
				}
			}
			else {
				if(tolerance.containsKey("All")){
					query=query.concat("CASE WHEN ( CASE WHEN "+tbl1+".["+column+"] != '' THEN Substring("+tbl1+".["+column+"],1,CHARINDEX('.',"+tbl1+".["+column+"])+ "+tolerance.get(column)+") ELSE '0.0' END ) = (CASE WHEN "+tbl2+".["+column+"] != '' THEN Substring("+tbl2+".["+column+"],1,CHARINDEX('.',"+tbl2+".["+column+"])+ "+tolerance.get(column)+") ELSE '0.0' END) THEN \'Pass\' ELSE \'Fail\' END AS ["+ column+"ComparisonStatus]\n");

				}
				else{
					query=query.concat("CASE WHEN "+tbl1+".["+column+"] ="+tbl2+".["+column+"] THEN \'Pass\' ELSE \'Fail\' END AS ["+ column+"ComparisonStatus]\n");
				}
			}
		}
		from=from.replaceFirst("and", "");
		query=query.replaceFirst(",", "");
		return select.concat(query).concat(from);
	}


	public String createBaseQuery(String tbl1, String tbl2, String comparisonTable, String baseData) {

		String from = "";

		switch (baseData) {
		case "Prod":
			from=from.concat(" into "+comparisonTable+" from "+tbl1+ " right join "+tbl2+" on ");
			break;

		case "QA":
			from=from.concat(" into "+comparisonTable+" from "+tbl1+ " left join "+tbl2+" on ");
			break;

		case "Common":
			from=from.concat(" into "+comparisonTable+" from "+tbl1+ " inner join "+tbl2+" on ");
			break;

		default:
			from=from.concat(" into "+comparisonTable+" from "+tbl1+ " full outer join "+tbl2+" on ");
			break;
		}
		return from;
	}

	/**
	 * This method is used to create a comparison query based on the primary
	 * keys , tolerant keys and columns from input files.
	 *
	 * @param tbl1
	 * @param tbl2
	 * @param comparisonTable
	 * @param col
	 * @param pKey
	 * @param tolerance
	 * @return
	 */
	public String comparisonQueriesNew(String tbl1, String tbl2, String comparisonTable, List<String> col,
			List<String> pKey, Map<String, Integer> tolerance, String baseData)
	{
		String query = "";
		String select = "";
		col.removeAll(pKey);
		select = select.concat("select distinct ");

		String from = createBaseQuery(tbl1, tbl2, comparisonTable, baseData);

		for (String key : pKey)
		{
			select = select.concat(" " + tbl1 + ".[" + key + "],");

			from = from.concat(" and " + tbl1 + ".[" + key + "]=" + tbl2 + ".[" + key + "] ");
		}
		if( columnsToIgnore!=null) {

			col.removeAll(columnsToIgnore);

		}

		for (String column : col)
		{
			query = query.concat(", " + tbl1 + ".[" + column + "] as " + "\"" + tbl1 + " " + column + "\", " + tbl2
					+ ".[" + column + "] as " + "\"" + tbl2 + " " + column + "\", ");
			if (tolerance.containsKey(column))
			{
				String qaTol= applyTolerance(tbl1, qaTypeMap, column, tolerance.get(column));

				String prodTol=applyTolerance(tbl2, prodTypeMap, column, tolerance.get(column));

				query = query.concat("CASE WHEN "+qaTol+" = "+prodTol+" THEN \'Pass\'" + "WHEN " + tbl1 + ".[" + column + "] IS NULL AND " + tbl2 + ".[" +column + "] IS NULL THEN \'Pass\' ELSE \'Fail\' END AS [" + column + "ComparisonStatus]");

			}
			else
			{
				query = query.concat("CASE WHEN " + tbl1 + ".[" + column + "] =" + tbl2 + ".[" + column
						+ "] THEN \'Pass\' " + "WHEN " + tbl1 + ".[" + column + "] IS NULL AND " + tbl2 + ".["
						+ column + " ] IS NULL THEN \'Pass\'"
						+ " ELSE \'Fail\' END AS [" + column + "ComparisonStatus]\n");
			}

		}
		from = from.replaceFirst("and", "");
		query = query.replaceFirst(",", "");
		return select.concat(query).concat(from);
	}


	public void readTolerance(String filePath, String sheetName, boolean readDifference){

		logger.info("Reading tolerance and allowed difference from sheet {}", sheetName);

		ExcelReader er = new ExcelReader(filePath, sheetName, 0);
		List<List<String>> data = er.readCompleteData(1);

		tolerance = data.stream().collect(Collectors.toMap(x->x.get(0), x->Float.valueOf(x.get(1)).intValue()));

		if(readDifference) {
			difference = data.stream().collect(Collectors.toMap(x->x.get(0), x->Float.valueOf(x.get(2))));
		}

		logger.info("Tolerance Map: {}",tolerance);
		er.closeWorkBook();
	}

	public void readColumnsToIgnore(String filePath, String sheetName){

		logger.info("Reading columns to ignore from sheet {}", sheetName);

		ExcelReader er = new ExcelReader(filePath, sheetName, 0);

		columnsToIgnore =	er.readCompleteColumnData(0,1);
		logger.info("Columns to Ignore: {}",columnsToIgnore);

		er.closeWorkBook();
	}

	/**
	 * This method is used to execute comparison query and then write columns wise comparison status in a comparison file.
	 * @param comparisonQuery
	 * @param exportId
	 * @param compTable
	 * @param tbl1
	 * @param tbl2
	 * @param columns
	 * @param primaryKeys
	 * @return
	 */
	public Boolean writeStatus(String comparisonQuery,String exportId, String compTable,String tbl1,String tbl2,
			List<String>columns,List<String>primaryKeys){

		Boolean flag=false;
		int i=1;
		String select="select ";
		try(
				XSSFWorkbook wb=new XSSFWorkbook(); Statement stmt=conn.createStatement();				
				){
			if(failedColumns!=null){
				failedColumns.clear();
			}
			if(dataSize1!=dataSize2){

				logger.error("Data size mismatch!! {} file has {} rows but {} file has {} rows", tbl1, dataSize1,tbl2, dataSize2 );

			}

			for(String key:primaryKeys){
				select=select.concat(" , ["+key+"]");
			}
			select =select.replaceFirst(",", "");

			XSSFSheet sh=wb.createSheet("Comparison Status");
			Row headers=sh.createRow(0);
			headers.createCell(0).setCellValue("Column");
			headers.createCell(1).setCellValue("Comparison Status");
			headers.createCell(2).setCellValue("Query");

			ddlOp.dropTable(conn, compTable);
			stmt.execute(comparisonQuery);

			for(String col:columns){
				String query=", ["+ tbl1 +" "+col+ "], ["+ tbl2 +" "+col+  "] from "+compTable+ " where ["+ col+"ComparisonStatus]"+" = "+ "\'Fail\'";
				query=select.concat(query);

				String passQuery=", ["+ tbl1 +" "+col+ "], ["+ tbl2 +" "+col+  "] from "+compTable+ " where ["+ col+"ComparisonStatus]"+" = "+ "\'Pass\'";

				passQuery=select.concat(passQuery);

				ResultSet rs=stmt.executeQuery(query);

				Row row=sh.createRow(i);

				CellStyle style = wb.createCellStyle(); //Create new style

				style.setWrapText(true); //Set wordwrap
				row.setRowStyle(style);
				if(!rs.isBeforeFirst() && !rs.next()){
					row.createCell(0).setCellValue(col);
					row.createCell(1).setCellValue("Pass");
					row.createCell(2).setCellValue(passQuery);
				}
				else{
					failedColumns.add(col);
					row.createCell(0).setCellValue(col);
					row.createCell(1).setCellValue("Fail");
					row.createCell(2).setCellValue(query);
				}
				i=i+1;
			}

			logger.error("Total Columns Failed: {}", failedColumns.size());
			logger.error("Columns Failed: {}", failedColumns);

			Date d=new Date();
			SimpleDateFormat smf=new SimpleDateFormat("dd-MM-yyyy");
			String dateStamp=smf.format(d);
			dateStamp="."+File.separator+"ComparisonReports"+File.separator+dateStamp+File.separator+exportId;
			String filename="Comparison_"+exportId+".xlsx";
			File f=new File(dateStamp);
			if(f.mkdirs())
			{
				createComparisonFile(f.getAbsolutePath()+File.separator+exportId+"ComparisonData.csv", compTable);

				if(dataSize1!=dataSize2){
					getQAOnlyData(tbl1, tbl2, primaryKeys, f.getAbsolutePath()+File.separator+exportId+"QAOnlyData.csv");
					getProdOnlyData(tbl1, tbl2, primaryKeys, f.getAbsolutePath()+File.separator+exportId+"ProdOnlyData.csv");
				}
			}
			else{
				if(f.exists()){

					createComparisonFile(f.getAbsolutePath()+File.separator+exportId+"ComparisonData.csv", compTable);
					if(dataSize1!=dataSize2){
						getQAOnlyData(tbl1, tbl2, primaryKeys, f.getAbsolutePath()+File.separator+exportId+"QAOnlyData.csv");
						getProdOnlyData(tbl1, tbl2, primaryKeys, f.getAbsolutePath()+File.separator+exportId+"ProdOnlyData.csv");
					}
				}
				else{
					logger.error("Unable to Create Folder or File for comparison");
				}
			}	
			FileOutputStream fos=new FileOutputStream(f.getAbsolutePath()+File.separator+filename);
			flag=true;
			wb.write(fos);
			fos.close();
		}
		catch(SQLException e){
			flag=false;
			logger.error("SQL Exception: {}",e.getMessage());
		}
		catch(FileNotFoundException e){
			flag=false;
			logger.error("File doesn't exists {}",e.getMessage());
		}
		catch(IOException e){
			flag=false;
			logger.error("IO Exception: {}",e.getMessage());
		}

		return flag;
	}

	public void createComparisonFile(String path, String tblName){

		try (
				Statement stmt=conn.createStatement();

				CSVWriter wr1 = new CSVWriter(new FileWriter(path));
				){
			ResultSet rs=stmt.executeQuery("Select * from "+tblName);

			wr1.writeAll(rs, true, true);
			wr1.flush();
		}
		catch(SQLException  | IOException e) {
			logger.error(e.getMessage());
		}
	}

	public void getQAOnlyData(String qaTable,String prodTable, List<String> keys, String path){

		String query="select %primaryKeys% from %table1% except select %primaryKeys% from %table2%";
	    query=query.replace("%table1%", qaTable);
		String primarySelect="";
		try (
				Statement stmt=conn.createStatement();

				CSVWriter wr1 = new CSVWriter(new FileWriter(path));
				){
			for(String key:keys){
				primarySelect=primarySelect.concat(",["+key+"]");
			}
			primarySelect=primarySelect.replaceFirst(",", "");

			query=query.replace("%table2%", prodTable);
			query=query.replace("%primaryKeys%", primarySelect);

			ResultSet rs=stmt.executeQuery(query);

			wr1.writeAll(rs, true, true);
			wr1.flush();
		}
		catch(SQLException | IOException e) {
			logger.error(e.getMessage());
		}
	}

	public void getProdOnlyData(String qaTable,String prodTable, List<String> keys, String path) throws SQLException, IOException{

		String query="select %primaryKeys% from %table2% except select %primaryKeys% from %table1%";
		query=query.replace("%table1%", qaTable);

		String primarySelect="";
		try (
				Statement stmt=conn.createStatement();

				CSVWriter wr1 = new CSVWriter(new FileWriter(path));
				){
			for(String key:keys){
				primarySelect=primarySelect.concat(",["+key+"]");
			}
			primarySelect=primarySelect.replaceFirst(",", "");

			query=query.replace("%table2%", prodTable);
			query=query.replace("%primaryKeys%", primarySelect);

			ResultSet rs=stmt.executeQuery(query);

			wr1.writeAll(rs, true, true);
			wr1.flush();
		}
		catch(SQLException | IOException e) {
			logger.error(e.getMessage());
		}
	}

	private boolean checkColumnsAndPrimaryKeys(List<String> pKeys) {

		boolean flag=false;

		List<String>col1 = tbl1Columns.stream().map(String::toLowerCase).collect(Collectors.toList());

		List<String>col2 = tbl2Columns.stream().map(String::toLowerCase).collect(Collectors.toList());

		List<String> pK = pKeys.stream().map(String::toLowerCase).collect(Collectors.toList());

		if(col1.equals(col2) ) {

			if(col1.containsAll(pK)) flag=true;

			else {
				for (String key: pK) {
					if(!col1.contains(key)) logger.error("Primary key {} is not in the files, please remove and run again", key);
				}
			}

		}
		else {

			if(columnsToIgnore!=null && !columnsToIgnore.isEmpty()) {

				col1.removeAll(columnsToIgnore);

				col2.removeAll(columnsToIgnore);

			}

			List<String>qaOnlyCol=new ArrayList<>();

			for(String qaCol:col1) {
				if(!col2.remove(qaCol)) {
					qaOnlyCol.add(qaCol);
				}
			}

			if(qaOnlyCol.isEmpty() && col2.isEmpty()) {

				flag=true;
			}
			else {
				if(!qaOnlyCol.isEmpty()) {

					logger.error("There are few extra columns in QA: {}",qaOnlyCol);

				}
				if(!col2.isEmpty()) {
					logger.error("There are few extra columns in Prod: {}",col2);
				}
			}
		}

		return flag;
	}

	public String applyTolerance(String table, Map<String,String> typeMap , String column, int tolerance) {

		//fetch column type
		//apply covert method based on the column type
		String toleranceString="";
		String dataType = typeMap.get(column).toLowerCase();
		logger.info("Column Data Type Map: {}",typeMap);
		if(dataType!=null) {
			switch (dataType) {

			case "nvarchar":
				toleranceString= "round("+table+".["+column+"]," +tolerance+", 1)";

				break;

			case "varchar":
				toleranceString= "round("+table+".["+column+"]," +tolerance+", 1)";

				break;

			case "int":
				toleranceString= "round("+table+".["+column+"]," +tolerance+", 1)";

				break;

			case "float":

				toleranceString= "round("+table+".["+column+"]," +tolerance+", 1)";

				break;

			case "decimal":

				toleranceString= "round("+table+".["+column+"]," +tolerance+", 1)";

				break;

			case "numeric":

				toleranceString= "round("+table+".["+column+"]," +tolerance+", 1)";

				break;

			case "smallint":

				toleranceString= "round("+table+".["+column+"]," +tolerance+", 1)";

				break;

			default:

				logger.error("Datatype isnt convertible");

				break;
			}
		}
		else {
			logger.error("Datatype for column {} is null", column);
			toleranceString = table+".["+column+"]";
		}
		return toleranceString;
	}

}

