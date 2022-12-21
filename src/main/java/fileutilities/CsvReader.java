package fileutilities;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

/**
 * March 4, 2022
 * @author saurabh.shukla
 */


public class CsvReader {
	
	public int maxColWidth;
	
	Logger logger = LogManager.getLogger("CSV Reader");
	
	/**
	 * This method is used to read input file and store their data into list of lists
	 * @param filePath
	 * @return
	 */
	public List<List<String>> readFiles(String filePath){
		
		List<List<String>>completeData=new ArrayList<>();
		
		try(CSVReader reader = new CSVReader(new FileReader(filePath))){
			
			logger.info("Reading data from file {}", filePath);
			
			String [] nextLine;
			while ((nextLine = reader.readNext()) != null) {
				
				List<String> al=new ArrayList<>(Arrays.asList(nextLine));
				
				completeData.add(al);
				
				for(String rowData: al){
					
					if(maxColWidth<=rowData.length()){
						
						maxColWidth=rowData.length()+1;
					}
				}
			}
			logger.info("Successfully read data from file {} ",filePath);
			
		}
		catch(IOException | CsvValidationException e){
			
			logger.error("Failed to read data from file {} ",filePath);

			logger.error("IO Exception Occured: {}", e.getMessage());
		}
		
		return completeData;
	}
}
