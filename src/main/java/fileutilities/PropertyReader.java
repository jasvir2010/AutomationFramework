package fileutilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * March 4, 2022
 * @author saurabh.shukla
 */

public class PropertyReader {

	private Properties pro;
	public Map<String,String> propertiesMap=new HashMap<>();

	Logger logger;

	public PropertyReader(String filePath) {

		logger= LogManager.getLogger("Property File Reader Logs");	

		try(FileInputStream fis = new FileInputStream(new File(filePath))) {

			pro=new Properties();
			pro.load(fis);
		}
		catch (FileNotFoundException e){

			logger.error("Property File Not Found, Please check the filepath and try again.");
		}
		catch (IOException e){
			logger.error("IO Exception"+ e.getMessage());
		}
	}
	public String getProperty(String key){
		return pro.getProperty(key);
	}

	public Map<String, String> readAllProperty(){
		logger.info("Reading All properties from properties file");
		Enumeration<?> e = pro.propertyNames();
		while (e.hasMoreElements()) {
			String key = (String) e.nextElement();
			String value = pro.getProperty(key);
			propertiesMap.put(key,value);
		}
		
		return propertiesMap;
	}
}
