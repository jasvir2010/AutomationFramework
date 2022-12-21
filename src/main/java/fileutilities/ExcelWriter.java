package fileutilities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.List;

/**
 * March 4, 2022
 * @author saurabh.shukla
 */


public class ExcelWriter {

	private XSSFWorkbook workbook;
	private FileInputStream fis;
	public Logger logger;

	/*
	 * 
	 * Parameterized constructor to initialize workbook for given file.
	 */
	public ExcelWriter(String filePath) {

		try {
			logger= LogManager.getLogger("Excel Writer Logs");
			
			fis=new FileInputStream(filePath);
			workbook = new XSSFWorkbook(fis);
		}
		catch(FileNotFoundException e){
			logger.error("Not able to find given file : {}",e.getMessage());
		}
		catch (IOException e){
			logger.error("IO Exception Occured: {}",e.getMessage());
		}
	}
	/*
	 * Default constructor
	 * 
	 */
	public ExcelWriter() {
		logger= LogManager.getLogger("Excel Writer Logs");	
	}

	/*
	 * Method to create a sheet in current workbook instance.
	 * 
	 */
	public void createSheet(String sheetName){

		if(workbook.getSheet(sheetName)==null) {
			workbook.createSheet(sheetName);
			logger.info("Sheet {} created", sheetName);
		}
		else {
			logger.error("Sheet {} Already exists", sheetName);
		}
	}

	/*
	 * Method to create a sheet in a workbook.
	 * 
	 */
	public void createSheet(String file, String sheetName){

		try(
				FileInputStream fis = new FileInputStream(file); 
				XSSFWorkbook workbook = new XSSFWorkbook(fis)){

			if(workbook.getSheet(sheetName)==null) {
				workbook.createSheet(sheetName);
				logger.info("Sheet {} created", sheetName);
			}
			else {
				logger.error("Sheet {} Already exists", sheetName);
			}

			fis.close();
			FileOutputStream fos = new FileOutputStream(file);
			workbook.write(fos);
			workbook.close();
			fos.close();
		}
		catch(Exception e) {
			logger.error("IO Exception Occured: {}",e.getMessage());
		}
	}

	/*
	 * Method to create a cell in a sheet of current workbook instance.
	 * 
	 */
	public void setCellValue(String sheet, boolean createSheetIfNotPresent, int rowNum, boolean createRowIfNotPresent, int colNum, boolean createColIfNotPresent, String text) {
		try{

			Sheet sh = workbook.getSheet(sheet);

			if(sh!=null || createSheetIfNotPresent) {

				if(sh==null) sh = workbook.createSheet(sheet);

				Row row = sh.getRow(rowNum);

				if(row!=null || createRowIfNotPresent) {

					if(row==null) row = sh.createRow(rowNum);

					Cell cell = row.getCell(colNum);

					if(cell !=null || createColIfNotPresent) {

						if(cell==null) cell = row.createCell(colNum);

						cell.setCellValue(text);
						logger.info("Value inserted in Cell at row {} and column {}", rowNum, colNum);
					}
					else {
						logger.error("Column {} doesn't exists", colNum);
					}
				}
				else {
					logger.error("Row {} doesn't exists", rowNum);
				}
			}
			else {
				logger.error("Sheet {} doesn't exists", sheet);
			}

		}
		catch(Exception e) {
			logger.error("IO Exception Occured: {}",e.getMessage());
		}
	}

	/*
	 * Method to create a cell in sheet of a given file.
	 * 
	 */
	public void setCellValue(String fileName, String sheet, boolean createSheetIfNotPresent, int rowNum, boolean createRowIfNotPresent, int colNum, boolean createColIfNotPresent, String text) {

		try(
				FileInputStream fis = new FileInputStream(fileName); 
				XSSFWorkbook workbook = new XSSFWorkbook(fis)){


			Sheet sh = workbook.getSheet(sheet);
			if(sh!=null || createSheetIfNotPresent) {

				if(sh==null) sh = workbook.createSheet(sheet);

				Row row = sh.getRow(rowNum);

				if(row!=null || createRowIfNotPresent) {

					if(row==null) row = sh.createRow(rowNum);

					Cell cell = row.getCell(colNum);

					if(cell !=null || createColIfNotPresent) {

						if(cell==null) cell = row.createCell(colNum);

						cell.setCellValue(text);
						logger.info("Value inserted in Cell at row {} and column {}", rowNum, colNum);
					}
					else {
						logger.error("Column {} doesn't exists", colNum);
					}
				}
				else {
					logger.error("Row {} doesn't exists", rowNum);
				}
			}
			else {
				logger.error("Sheet {} doesn't exists", sheet);
			}
			fis.close();
			FileOutputStream fos = new FileOutputStream(fileName);
			workbook.write(fos);
			workbook.close();
		}

		catch(IOException e) {
			logger.error("IO Exception Occured: {}",e.getMessage());
		}
	}

	/*
	 * 
	 * Add a row in current workbook instance. If sheet is not there it will create a new sheet.
	 */
	public void addNewRow(String sheet, boolean createSheetIfNotPresent, List<String>rowVal) {
		try{

			Sheet sh = workbook.getSheet(sheet);
			if(sh!=null || createSheetIfNotPresent) {

				if(sh ==null) sh = workbook.createSheet(sheet);

				Row row = sh.createRow(sh.getLastRowNum()+1);
				for(int col = 0; col < rowVal.size(); col++) {
					row.createCell(col).setCellValue(rowVal.get(col));
				}
				logger.info("Values inserted in new row");
			}
			else {
				logger.error("Sheet {} doesn't exists", sheet);
			}
		}
		catch(Exception e) {
			logger.error("Exception Occured: {}",e.getMessage());
		}
	}

	/*
	 * Add a row inside a sheet in given file. If sheet is not there it will create a new sheet.
	 * 
	 */
	public void addNewRow( String file, String sheet, boolean createSheetIfNotPresent, List<String>rowVal) {
		try(
				FileInputStream fis = new FileInputStream(file); 
				XSSFWorkbook workbook = new XSSFWorkbook(fis)
				){

			Sheet sh = workbook.getSheet(sheet);
			if(sh!=null || createSheetIfNotPresent) {

				if(sh ==null) sh = workbook.createSheet(sheet);
				Row row = sh.createRow(sh.getLastRowNum()+1);
				for(int col = 0; col < rowVal.size(); col++) {
					row.createCell(col).setCellValue(rowVal.get(col));
				}
				logger.info("Values inserted in new row");
			}
			else {
				logger.error("Sheet {} doesn't exists", sheet);
			}
			fis.close();
			FileOutputStream fos = new FileOutputStream(file);
			workbook.write(fos);
			workbook.close();
			fos.close();
		}
		catch(Exception e) {
			logger.error("Exception Occured: {}",e.getMessage());
		}
	}

	/*
	 * 
	 * Update values of a row in current workbook instance. If sheet is not there it will create a new sheet.
	 */
	public void updateRow(String sheet, boolean createSheetIfNotPresent, int rowNum, List<String>rowVal) {
		try{

			Sheet sh = workbook.getSheet(sheet);
			if(sh!=null || createSheetIfNotPresent) {

				if(sh ==null) sh = workbook.createSheet(sheet);

				Row row = sh.getRow(rowNum);

				if(row!=null) {

					for(int col = 0; col < rowVal.size(); col++) {
						row.getCell(col).setCellValue(rowVal.get(col));
					}
					logger.info("Values updated in row at {}", rowNum);
				}
				else {
					logger.error("Row at index {} doesnt exists", rowNum);
				}
			}
			else {
				logger.error("Sheet {} doesn't exists", sheet);
			}
		}
		catch(Exception e) {
			logger.error("Exception Occured: {}",e.getMessage());
		}
	}

	/*
	 * Update values of a row inside a sheet in given file. If sheet is not there it will create a new sheet.
	 * 
	 */
	public void updateRow( String file, String sheet, boolean createSheetIfNotPresent,int rowNum, List<String>rowVal) {

		try(
				FileInputStream fis = new FileInputStream(file); 
				XSSFWorkbook workbook = new XSSFWorkbook(fis)
				){

			Sheet sh = workbook.getSheet(sheet);
			if(sh!=null || createSheetIfNotPresent) {

				if(sh ==null) sh = workbook.createSheet(sheet);
				Row row = sh.getRow(rowNum);

				if(row!=null) {

					for(int col = 0; col < rowVal.size(); col++) {
						row.getCell(col).setCellValue(rowVal.get(col));
					}
					logger.info("Values updated in row at {}", rowNum);
				}
				else {
					logger.error("Row at index {} doesnt exists", rowNum);
				}
			}
			else {
				logger.error("Sheet {} doesn't exists", sheet);
			}
			fis.close();
			FileOutputStream fos = new FileOutputStream(file);
			workbook.write(fos);
			workbook.close();
			fos.close();
		}
		catch(Exception e) {
			logger.error("Exception Occured: {}",e.getMessage());
		}
	}

	/*
	 * Add a column inside a sheet in given file. If sheet is not there it will create a new sheet.
	 * 
	 */
	public void addColumn( String file, String sheet, boolean createSheetIfNotPresent, boolean createRowIfNotPresent, int colNum, List<String>colVal) {
		try(
				FileInputStream fis = new FileInputStream(file); 
				XSSFWorkbook workbook = new XSSFWorkbook(fis)
				){

			Sheet sh = workbook.getSheet(sheet);
			if(sh!=null || createSheetIfNotPresent) {

				if(sh ==null) sh = workbook.createSheet(sheet);

				for(int i=0;i<colVal.size(); i++) {

					Row row = sh.getRow(i+1);

					if(row==null) {
						if(createRowIfNotPresent) {
							row = sh.createRow(i+1);
							Cell cell = row.createCell(colNum);
							cell.setCellValue(colVal.get(i));
						}
						else {

							logger.error("Last row at {} filled", i+1);
						}
					}
				}
			}
			else {
				logger.error("Sheet {} doesn't exists", sheet);
			}
			fis.close();
			FileOutputStream fos = new FileOutputStream(file);
			workbook.write(fos);
			workbook.close();
		}
		catch(Exception e) {
			logger.error("Exception Occured: {}",e.getMessage());
		}
	}

	/*
	 * 
	 * Add a column in current workbook instance. If sheet is not there it will create a new sheet.
	 */
	public void addColumn(String sheet, boolean createSheetIfNotPresent,boolean createRowIfNotPresent, int colNum, List<String>colVal) {
		try{

			Sheet sh = workbook.getSheet(sheet);
			if(sh!=null || createSheetIfNotPresent) {

				if(sh ==null) sh = workbook.createSheet(sheet);

				for(int i=0;i<colVal.size(); i++) {

					Row row = sh.getRow(i+1);

					if(row==null) {
						if(createRowIfNotPresent) {
							row = sh.createRow(i+1);
							Cell cell = row.createCell(colNum);
							cell.setCellValue(colVal.get(i));
						}
						else {
							logger.error("Last row at {} filled", i+1);
						}
					}
				}
			}
			else {
				logger.error("Sheet {} doesn't exists", sheet);
			}
		}
		catch(Exception e) {
			logger.error("Exception Occured: {}",e.getMessage());
		}
	}

	/*
	 * Method to write all the changes made in the file.
	 * 
	 */
	public void writeAllChanges(String file) {
		try (FileOutputStream fos = new FileOutputStream(file)){
			fis.close();
			workbook.write(fos);
			workbook.close();
			fos.close();
		}
		catch(IOException e) {
			logger.error("IO Exception Occured: {}",e.getMessage());
		}
	}
}
