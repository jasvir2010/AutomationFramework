package fileutilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


/**
 * March 4, 2022
 * @author saurabh.shukla
 */

public class ExcelReader {

	protected XSSFWorkbook workbook;
	XSSFSheet sh;
	Logger logger;

	public ExcelReader(String filePath, String sheetName, int sheetNumber){

		logger= LogManager.getLogger("Excel Reader Logs");	

		try {

			workbook= openFile(filePath);

			if (sheetName!=null) {
				sh = openSheet(sheetName);
			}
			else if(sheetNumber >= 0){
				sh=openSheet(sheetNumber);
			}
			else{
				System.out.println("Provide a valid sheetname/sheetnumber to read.");
			}
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}

	public ExcelReader(){

		logger= LogManager.getLogger("Excel Reader Logs");	
	}

	/**
	 * Class: ExcelFunctions
	 * Method: openFile
	 * To open excel file
	 */
	public XSSFWorkbook openFile(String filepath) {

		File file = new File(filepath);
		XSSFWorkbook workb = null;

		try(FileInputStream fIP = new FileInputStream(file)){

			if(file.isFile() && file.exists())
			{
				workb = new XSSFWorkbook(fIP);

				logger.info("{} file open successfully.", filepath);
			}
			else
			{
				logger.error("Error to open file: {} ",filepath);
			}
		}catch(IOException e){

			e.printStackTrace();

		}
		return workb;
	}

	/**
	 * This method will open and return a instance of a sheet based on the sheet name.
	 * @param sheetName
	 * @return
	 */
	public XSSFSheet openSheet(String sheetName){

		sh = workbook.getSheet(sheetName);

		return sh;
	}

	/**
	 * This method will open and return a instance of a sheet based on the sheet number.
	 * @param sheetNum
	 * @return
	 */
	public XSSFSheet openSheet(int sheetNum){

		sh = workbook.getSheetAt(sheetNum);

		return sh;
	}


	/**
	 * This method will open and return a instance of a sheet based on the sheet name.
	 * @param sheetName
	 * @return
	 */
	public XSSFSheet openSheet(XSSFWorkbook workb, String sheetName){

		XSSFSheet sheet = workb.getSheet(sheetName);

		return sheet;
	}

	/**
	 * This method will open and return a instance of a sheet based on the sheet number.
	 * @param sheetNum
	 * @return
	 */
	public XSSFSheet openSheet(XSSFWorkbook workb, int sheetNum){

		XSSFSheet sheet = workb.getSheetAt(sheetNum);

		return sheet;
	}


	//Need to work on this method not working properly
	/**
	 * This method will return arraylist having values of all the columns in a row.
	 *
	 * @param row (string value of any column of the row)
	 * @return
	 */
	public ArrayList<String> readCompleteRowData(String row){

		int rowNum = getRowNumber(row);

		ArrayList<String> rowData =new ArrayList<>();

		Row rw= sh.getRow(rowNum);
		int totalColumn=rw.getLastCellNum();

		for(int i=0;i<totalColumn;i++){

			rowData.add(getCellValue(rowNum, i));

		}
		return rowData;
	}


	/**
	 * This method will return arraylist having values of all the columns in a row.
	 * @param rowNum
	 * @return
	 */
	public List<String> readCompleteRowData( int rowNum){

		ArrayList<String> rowData =new ArrayList<>();
		if(rowNum<=sh.getLastRowNum() && rowNum >=0)
		{	
			Row row= sh.getRow(rowNum);

			int totalColumn=row.getLastCellNum();

			for(int i=0;i<totalColumn;i++){
				rowData.add(getCellValue(rowNum, i));
			}
		}
		else {
			System.out.println("Value of row number is higher than the rows exists on the sheet OR is less than 0");

		}
		return rowData;
	}

	/**
	 * This method will return arraylist having values of all the columns in a row.
	 * @param rowNum
	 * @return
	 */
	public List<String> readCompleteRowData(XSSFSheet sheet, int rowNum){
		ArrayList<String> rowData =new ArrayList<>();
		
		if(rowNum<=sheet.getLastRowNum() && rowNum>=0)
		{	
			Row row= sheet.getRow(rowNum);

			int totalColumn=row.getLastCellNum();

			for(int i=0;i<totalColumn;i++){
				rowData.add(getCellValue(sheet, rowNum, i));
			}
		}
		else {
			System.out.println("Value of row number is higher than the rows exists on the sheet OR is less than 0");

		}
		return rowData;
	}


	//Need to work on this method current not working as expected
	/**
	 *
	 * This method will return arraylist having all values of that column in all rows.
	 * @param column (columns name)
	 * @return
	 */
	public List<String> readCompleteColumnData(String column, int startRowIndex){
		
		int colNum=getColumnNumber(column);
		ArrayList<String> colData =new ArrayList<>();
		
		int totalRows=sh.getLastRowNum();
		
		for(int i=startRowIndex;i<=totalRows;i++){
			
			colData.add(getCellValue(i, colNum));
			
		}
		return colData;
	}
	/**
	 *
	 * This method will return arraylist having all values of that column in all rows.
	 * @param colNum (columns index)
	 * @return
	 */
	public List<String> readCompleteColumnData(int colNum , int startRowIndex){

		ArrayList<String> colData =new ArrayList<>();

		int totalRows=sh.getLastRowNum();

		for(int i=startRowIndex;i<=totalRows;i++){

			colData.add(getCellValue(i, colNum));
		}
		return colData;
	}

	/**
	 *
	 * This method will return arraylist having all values of that column in all rows.
	 * @param colNum (columns index)
	 * @return
	 */
	public List<String> readCompleteColumnData(XSSFSheet sheet, int colNum, int startRowIndex){
		ArrayList<String> colData =new ArrayList<>();

		int totalRows=sheet.getLastRowNum();

		for(int i=startRowIndex;i<=totalRows;i++){

			colData.add(getCellValue(i, colNum));
		}
		return colData;
	}


	//Need to work on thios method
	/**
	 * This method will return the row number of the row having given value.
	 * @param rowContent
	 * @return
	 */
	public int getRowNumber(String rowContent){
		int rowNum=0;
		int totalRows=sh.getLastRowNum();
		for(int i=1;i<=totalRows;i++){
			Cell c=sh.getRow(i).getCell(0);
			if(c.getStringCellValue().trim().equals(rowContent)){
				rowNum=i;
				break;
			}
		}
		return rowNum;
	}

	//need to work on this method
	/**
	 * This method will return column number of the given column in a sheet.
	 * @param columnHeader
	 * @return
	 */
	public int getColumnNumber(String columnHeader){
		int colNum=0;
		Row row=sh.getRow(0);
		for(Cell c: row){
			if(c.getStringCellValue().trim().equals(columnHeader)){
				colNum=c.getColumnIndex();
				break;
			}
		}
		return colNum;
	}

	/**
	 * This method will close the workbook.
	 */
	public void closeWorkBook(){
		try {
			workbook.close();
		}
		catch (IOException e){
			e.printStackTrace();
		}
	}

	public String getCellValue(int rownum, int colnum) {

		if(rownum <= sh.getLastRowNum() && rownum >=0) {

			Row row = sh.getRow(rownum);

			if(colnum < row.getLastCellNum() && colnum>=0) {

				Cell cell = row.getCell(colnum);

				CellType ct = cell.getCellType();

				if(ct == CellType.STRING) return cell.getStringCellValue();

				if(ct == CellType.NUMERIC) return String.valueOf(cell.getNumericCellValue());

				if(ct == CellType.BOOLEAN) return String.valueOf(cell.getNumericCellValue());

				if(ct == CellType.BLANK) return "";
			}
			else {

				System.out.println("!!!!Value of column number is higher than the columns exists on this row OR less than 0");
			}

		}
		else {		
			System.out.println("!!!!!Value of row number is higher than the rows exists on the sheet OR less than 0");
		}

		return "";		
	}

	public String getCellValue(XSSFSheet sheet, int rownum, int colnum) {

		if(rownum <= sheet.getLastRowNum() && rownum>=0) {

			Row row = sheet.getRow(rownum);

			if(colnum < row.getLastCellNum() && colnum >=0) {

				Cell cell = row.getCell(colnum);

				CellType ct = cell.getCellType();

				if(ct == CellType.STRING) return cell.getStringCellValue();

				if(ct == CellType.NUMERIC) return String.valueOf(cell.getNumericCellValue());

				if(ct == CellType.BOOLEAN) return String.valueOf(cell.getNumericCellValue());

				if(ct == CellType.BLANK) return "";
			}
			else {

				System.out.println("!!!!Value of column number is higher than the columns exists on this row OR less than 0");
			}

		}
		else {		
			System.out.println("!!!!!Value of row number is higher than the rows exists on the sheet OR less than 0");
		}


		return "";		
	}

	public List<List<String>> readCompleteData(int startRowIndex) {

		int totalRows = sh.getLastRowNum();

		List<List<String>> completeData= new ArrayList<>();

		for(int i = startRowIndex ; i <= totalRows; i++) {

			List<String> rowData = readCompleteRowData(i);
			completeData.add(rowData);
		}
		return completeData;
	}

	public List<List<String>> readCompleteData(String file, String sheetName, int sheetNum, int startRowIndex) {

		List<List<String>> completeData= new ArrayList<>();
		try(XSSFWorkbook wb = openFile(file);) {

			XSSFSheet sheet=null;

			if(sheetName!=null) {

				sheet = openSheet(wb,sheetName);

			}
			else if(sheetNum >=0) {

				openSheet(wb,sheetNum);
			}
			else {

				System.out.println("Provide a valid sheetname/sheetnumber to read.");
			}
			int totalRows = sheet.getLastRowNum();

			for(int i = startRowIndex ; i <= totalRows; i++) {

				List<String> rowData = readCompleteRowData(sheet, i);
				completeData.add(rowData);
			}

		} catch (IOException e) {

			e.printStackTrace();

		}
		return completeData;
	}
}

