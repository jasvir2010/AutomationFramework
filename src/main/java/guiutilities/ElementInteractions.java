package guiutilities;

import java.time.Duration;
import java.util.function.Function;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * March 7, 2022
 * @author saurabh.shukla
 *
 */
public class ElementInteractions {
	Logger logger = LogManager.getLogger();

	public void click(WebDriver driver, By by, String elementLabel, String waitStrategy, int waitTime) {
		try {
			if(checkElementPresent(driver, by, waitStrategy, waitTime)) {
				driver.findElement(by).click();
				logger.info("Web Element {} clicked", elementLabel);
			}
			else {
				logger.error("Failed to click on {}", elementLabel);
			}
		}
		catch(Exception e) {
			logger.error("Failed to click on {} exception occured {}", elementLabel, e.getMessage());
		}
	}

	public void enterText(WebDriver driver, By by, String elementLabel, String text, String waitStrategy, int waitTime) {
		WebElement element;
		try {
			if(waitStrategy.equalsIgnoreCase("Fluent")) {
				Wait<WebDriver> wait = new FluentWait<WebDriver>(driver)
						.withTimeout(Duration.ofSeconds(waitTime))
						.pollingEvery(Duration.ofSeconds(5))
						.ignoring(NoSuchElementException.class)
						.ignoring(ElementNotInteractableException.class);

				element = wait.until(new Function<WebDriver, WebElement>() {
					public WebElement apply(WebDriver driver) {
						return driver.findElement(by);
					}
				});			
			}
			else {
				WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(waitTime));
				element = wait
						.until(ExpectedConditions.visibilityOfElementLocated(by));
			}
			if(element!=null) {
				element.clear();
				element.sendKeys(text);
				logger.info("Text Entered in {} ", elementLabel);
			}
			else {
				logger.error("Failed to enter text in {}", elementLabel);
			}
		}
		catch(Exception e) {
			logger.error("Failed to enter text in {} exception occured {}", elementLabel, e.getMessage());
		}
	}

	public void selectFromDropDown(WebDriver driver, By by, String elementLabel, String optionToSelect, String waitStrategy, int waitTime) {
		WebElement element;
		try {
			if(waitStrategy.equalsIgnoreCase("Fluent")) {
				Wait<WebDriver> wait = new FluentWait<WebDriver>(driver)
						.withTimeout(Duration.ofSeconds(waitTime))
						.pollingEvery(Duration.ofSeconds(5))
						.ignoring(NoSuchElementException.class)
						.ignoring(ElementNotInteractableException.class);

				element = wait.until(new Function<WebDriver, WebElement>() {
					public WebElement apply(WebDriver driver) {
						return driver.findElement(by);
					}
				});			
			}
			else {
				WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(waitTime));
				element = wait
						.until(ExpectedConditions.visibilityOfElementLocated(by));
			}
			if(element!=null) {

				new Select(element).selectByValue(optionToSelect);

				logger.info("Option {} Selected in drop down {} ",optionToSelect, elementLabel);
			}
			else {
				logger.error("Failed to select option {} in drop down {}",optionToSelect, elementLabel);
			}
		}
		catch(Exception e) {
			logger.error("Failed to select option {} in drop down {} exception occured {}",optionToSelect, elementLabel, e.getMessage());
		}
	}
	
	private boolean checkElementPresent(WebDriver driver, By by, String waitStrategy,  int waitTime) {
		
		WebElement element;
		
		try {
			if(waitStrategy.equalsIgnoreCase("Fluent")) {
				Wait<WebDriver> wait = new FluentWait<WebDriver>(driver)
						.withTimeout(Duration.ofSeconds(waitTime))
						.pollingEvery(Duration.ofSeconds(5))
						.ignoring(NoSuchElementException.class)
						.ignoring(ElementNotInteractableException.class);

				element = wait.until(new Function<WebDriver, WebElement>() {
					public WebElement apply(WebDriver driver) {
						return driver.findElement(by);
					}
				});			
			}
			else {
				WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(waitTime));
				element = wait
						.until(ExpectedConditions.elementToBeClickable(by));
			}
			return element !=null? true: false;
		}
		catch(Exception e) {
			
			return false;
		}
	}
}
