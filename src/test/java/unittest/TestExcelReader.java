package unittest;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.Test;

import fileutilities.ExcelReader;

public class TestExcelReader {
	
	final Logger LOGGER = LogManager.getLogger();
	
	ExcelReader er=new ExcelReader("File path",null,0);

    @Test(enabled=false)
    public void testReadRowData(){

    	LOGGER.info("Testing 1");
    	LOGGER.error("Error --1");
      System.out.println(  er.readCompleteRowData(0));
      System.out.println(er.readCompleteRowData("2943"));

    }

    @Test(enabled=false)
    public void testReadColumnData(){
    	LOGGER.info("Testing 2");
       System.out.println(er.readCompleteColumnData(0,0));
       System.out.println(er.readCompleteColumnData("Export Id",0));
    }

    @Test(enabled=false)
    public void testReadCellData(){
    	LOGGER.info("Testing 3");
    	LOGGER.error("Error");
    	LOGGER.debug("Debug");
       //System.out.println(er.readCellData(1,0));
    }
    @Test(enabled =false)
    public void readData() {
    	
    	List<List<String>> data = er.readCompleteData(0);
    	System.out.println(data.size());
    }
    
}
