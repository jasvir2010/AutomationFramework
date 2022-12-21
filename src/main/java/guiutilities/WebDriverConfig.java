package guiutilities;

import java.net.MalformedURLException;
import java.net.URL;

import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import io.github.bonigarcia.wdm.WebDriverManager;

/**
 * March 4, 2022
 * @author saurabh.shukla
 */

public class WebDriverConfig {

	Proxy proxy = new Proxy();
	
	public String remoteDriverURL;
	
	/**
	 * This method will setup chrome driver for with given capabilities.
	 * @param driverPath path for driver executable
	 * @param incognito enable incognito mode.
	 * @param maximized start browser maximized.
	 * @param enableJS enable Javascript executions.
	 * @param startHeadless start in headless mode.
	 * @param setProxy enable request and response to got through proxy.
	 * @return webdriver instance.
	 */
	public WebDriver setUpChrome(boolean incognito, boolean maximized, boolean enableJS, boolean startHeadless, boolean setProxy, boolean remoteDriver ) {

		ChromeOptions options = new ChromeOptions();
		
		WebDriver driver = null;

		try {

		if(setProxy) {

			options.setProxy(proxy);
		}
		options.setAcceptInsecureCerts(true);

		if(maximized) options.addArguments("start-maximized");

		if(incognito) options.addArguments("--incognito");

		if(enableJS) options.addArguments("--enable-javascript");

		options.setHeadless(startHeadless);
		
		WebDriverManager.chromedriver().setup();
		
		if (remoteDriver) driver = new RemoteWebDriver(new URL(remoteDriverURL), options);
			
		else driver = new ChromeDriver(options);
		
		} catch (MalformedURLException e) {
			
			
			e.printStackTrace();
		}
		return driver;
	}

	/**
	 * This method will setup firefox driver for with given capabilities.
	 * @param driverPath path for driver executable
	 * @param incognito enable incognito mode.
	 * @param maximized start browser maximized.
	 * @param enableJS enable Javascript executions.
	 * @param startHeadless start in headless mode.
	 * @param setProxy enable request and response to got through proxy.
	 * @return webdriver instance.
	 */
	public WebDriver setUpFirefox(boolean incognito, boolean maximized, boolean enableJS,boolean startHeadless, boolean setProxy, boolean remoteDriver) {

		FirefoxOptions options = new FirefoxOptions();
		
		WebDriver driver = null;
		try {

		if(setProxy) {

			options.setProxy(proxy);
		}

		options.setAcceptInsecureCerts(true);

		if(incognito) options.addArguments("--incognito");

		if(maximized) options.addArguments("start-maximized");

		if(enableJS) options.addArguments("--enable-javascript");

		System.setProperty(FirefoxDriver.SystemProperty.BROWSER_LOGFILE,"/dev/null");

		options.setHeadless(startHeadless);
		
		WebDriverManager.firefoxdriver().setup();

		if (remoteDriver) driver = new RemoteWebDriver(new URL(remoteDriverURL), options);
		
		else driver = new FirefoxDriver(options);
		
		} catch (MalformedURLException e) {
			
			e.printStackTrace();
		}

		return driver;
	}
	
	
	
	public void setUpChromium() {
		
		//To be implemented
		
	}

	/**
	 * Method to setup proxy details.
	 * @param proxyHost
	 * @param port
	 * @param type type of proxy like http, https etc default is https.
	 */
	public void proxySetup(String proxyHost, int port, String type) {

		type =type.toUpperCase();

		switch(type) {
		case "HTTPS":
			proxy.setSslProxy(proxyHost+":"+port);
			break;

		case "HTTP":
			proxy.setHttpProxy(proxyHost+":"+port);
			break;

		default:
			proxy.setSslProxy(proxyHost+":"+port);
		}


	}
}
