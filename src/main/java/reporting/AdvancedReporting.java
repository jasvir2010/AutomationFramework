package reporting;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.Level;

/**
 * March 9, 2022
 * @author saurabh.shukla
 */

public class AdvancedReporting {

	public static ExtentReports er;

	public String projectName, env, user, elkServer;

	boolean extentTest, elkLog;

	Logger logger;

	public AdvancedReporting() {

		logger = LogManager.getLogger("Test Logger");
	}

	public AdvancedReporting(String project, String environment, String username, boolean extentTest, boolean elkLog, String elkServer) {

		projectName = project;

		env = environment;

		user = username;

		this.extentTest = extentTest;

		this.elkLog = elkLog;

		this.elkServer = elkServer;

		logger = LogManager.getLogger(project);

	}

	public ExtentReports initalizeReporting(String outputPath){

		ExtentSparkReporter spark = new ExtentSparkReporter(outputPath);
		spark.config().setTheme(Theme.DARK);
		spark.config().setDocumentTitle(projectName);

		er = new ExtentReports();
		er.setSystemInfo("Name", projectName);
		er.setSystemInfo("Host Name", "Indices QA");
		er.setSystemInfo("Environment", env);
		er.setSystemInfo("User Name", user);
		er.attachReporter(spark);

		return er;
	}


	public ExtentReports initalizeReporting(String project, String environment, String user, String outputPath){

		ExtentSparkReporter spark = new ExtentSparkReporter(outputPath);
		spark.config().setTheme(Theme.DARK);
		spark.config().setDocumentTitle(project);

		er = new ExtentReports();
		er.setSystemInfo("Name", project);
		er.setSystemInfo("Host Name", "Indices QA");
		er.setSystemInfo("Environment", environment);
		er.setSystemInfo("User Name", user);
		er.attachReporter(spark);

		return er;
	}

	public void generateReport(){
		if(er!=null){
			er.flush();
		}
	}

	public void log(Level logLevel, String logMessage, ExtentTest test, Status testStatus) {

		log(logLevel, logMessage, extentTest, test, testStatus, elkLog);

	}
	
	public void log(Level logLevel, String logMessage, boolean logToExtent, ExtentTest test, Status testStatus, boolean logToElk) {

		StackTraceElement ste = Thread.currentThread().getStackTrace()[2];

		logger.log(logLevel, "{} {} - {}", ste.getClassName(), ste.getLineNumber(), logMessage);

		if (logToExtent) test.log(testStatus, logMessage);

		if(logToElk) postElkLog(logMessage, testStatus.toString());

	}

	private void postElkLog(String logMessage, String logLevel) {

		
		Map<String,String> map = new HashMap<>();
		map.put("testName", projectName);
		map.put("status" , logLevel);
		map.put("Log", logMessage);
		map.put("executionTime", LocalDateTime.now().toString());

		Response response = given().header("Content-Type","application/json")
				.log()
				.all()
				.body(map)
				.post(elkServer);

		Assert.assertEquals(response.statusCode(), 201);

		response.prettyPrint();
		
	}
}

