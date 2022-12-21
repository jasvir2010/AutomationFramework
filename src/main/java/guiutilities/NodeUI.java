package guiutilities;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.ScriptTimeoutException;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import com.paulhammant.ngwebdriver.NgWebDriver;

/**
 * March 4, 2022
 * @author saurabh.shukla
 */

public class NodeUI {

	private WebDriver driver;
	public NgWebDriver ngd;

	/**
	 * Construct an instance with login using webdriver and credentials provided as parameters
	 * @param webdriver  Object of webdriver.
	 * @param username  Analyst portal username/email
	 * @param password  Analyst portal password.
	 */
	public NodeUI(WebDriver webdriver, String username, String password) {

		driver =webdriver;
		try {
			ngd = new NgWebDriver((JavascriptExecutor)driver).withRootSelector("app-root");
			System.out.println("-Waiting for angular page to be loaded --");
			ngd.waitForAngularRequestsToFinish();
			login(username, password);

			Thread.sleep(30000);
		} catch (InterruptedException e) {

			e.printStackTrace();
		}
		catch(ScriptTimeoutException e) {

			System.out.println("Angular Not loaded");
		}
	}
	/**
	 * Construct an instance navigate to login url(provided as parameter) and login using webdriver and credentials provided as parameters
	 * @param webdriver  Object of webdriver.
	 * @param username  Analyst portal username/email
	 * @param password  Analyst portal password.
	 * @param url - URL of login page
	 */
	public NodeUI(WebDriver webdriver, String username, String password, String url) {

		driver =webdriver;
		try {
			driver.get(url);
			Thread.sleep(20000);
			ngd = new NgWebDriver((JavascriptExecutor)driver);
			login(username, password);

			Thread.sleep(30000);
		} catch (InterruptedException e) {

			e.printStackTrace();
		}
	}

	/**
	 * Login to native ui using credentials passed as parameter.
	 * @param username  Analyst portal username/email
	 * @param password  Analyst portal password.
	 */
	public void login(String username, String password) {
		try {
			driver.findElement(By.id("username")).sendKeys(username);
			System.out.println("--Username entered--");
			driver.findElement(By.id("password")).sendKeys(password);
			System.out.println("--Password entered--");
			driver.findElement(By.id("submit")).click();
			System.out.println("--Clicked on submit button--");

			Thread.sleep(30000);
		} catch (InterruptedException e) {

			e.printStackTrace();
		}
		catch (NoSuchElementException e) {
			String time=new Timestamp(System.currentTimeMillis()).toString().replace(" ", "_");
			File img = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
			File destImg = new File(System.getProperty("user.dir")+File.separator+"Screenshots"+File.separator+time+".png");
			try {
				FileUtils.copyFile(img, destImg);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

	}

	/**
	 * Navigate to rebalancing page by clicking on rebalancing link on Indices page.
	 */
	public void navigateToRebalancing() {
		try
		{
			driver.findElement(By.xpath("//span[@class='icon submenu-text' and text()='Rebalancing']")).click();
			System.out.println("--Clicked on Rebalancing link--");
		}
		catch (NoSuchElementException e) {
			String time=new Timestamp(System.currentTimeMillis()).toString().replace(" ", "_");
			File img = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
			File destImg = new File(System.getProperty("user.dir")+File.separator+"Screenshots"+File.separator+time+".png");
			try {
				FileUtils.copyFile(img, destImg);
			} catch (IOException e1) {
				e1.printStackTrace();
			}

		}
	}
	/**
	 * Navigate to Indices page by clicking on Indices link.
	 * @param linkText text appearing on the page for Indices.
	 */
	public void navigateToIndices(String linkText) {
		try {
			System.out.println("--Waiting for page to load--");
			driver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
			System.out.println("--Page loaded--");
			// link text for Indices is  Indices(prefix with space)
			driver.findElement(By.xpath("//span[@class='menu-text' and text()='"+linkText+"']")).click();
			System.out.println("--Clicked on Indices link--");

			Thread.sleep(20000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		catch (NoSuchElementException e) {
			String time=new Timestamp(System.currentTimeMillis()).toString().replace(" ", "_");
			File img = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
			File destImg = new File(System.getProperty("user.dir")+File.separator+"Screenshots"+File.separator+time+".png");
			try {
				FileUtils.copyFile(img, destImg);
			} catch (IOException e1) {
				e1.printStackTrace();
			}

		}
	}

	/**
	 * Navigate to AGG UI page by clicking on Aggregate UI link.
	 */
	public void navigateToAggregateUI() {
		try {
			driver.findElement(By.xpath("//span[@class='icon submenu-text' and text()='Aggregate UI']")).click();
			System.out.println("--Clicked on Aggregate UI link--");
			Thread.sleep(20000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
