package guiutilities;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;


/**
 * March 4, 2022
 * @author saurabh.shukla
 */

public class WebDriverOperations {
	
	
	public boolean screenShot(WebDriver driver, String outFile) {
		
		try {
		TakesScreenshot scrShot =((TakesScreenshot)driver);

		File srcFile=scrShot.getScreenshotAs(OutputType.FILE);

		File destFile=new File(outFile);

		FileUtils.copyFile(srcFile, destFile);
		
		} catch (IOException e) {
			
			e.printStackTrace();
		}	
		return true;
	}

}
