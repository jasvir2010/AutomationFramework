package unittest;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.annotations.Test;

import comparison.Comparison;

public class TestComparison {

    @Test(enabled = false)

    public void testFileComaprison() throws SQLException {
        
    	Map<String, String> config =  new HashMap<>();
    	config.put("DBServer", "Pass DB Server here");
    	config.put("DBName", "Pass DB Table here");
    	config.put("Username", "Pass DB Username here");
    	config.put("Password", "Pass DB Password here");
    	config.put("DBTable1", "Pass Symbolic fiirst table name here");
    	config.put("DBTable2", "Pass Symbolic fiirst table name here");
    	config.put("BaseData", "Pass base data(QA/PROD/Common) for comparison  here");
    	
    	
    	Comparison comp = new Comparison(config, null);
        comp.readTolerance("Path to tolerance file", " Name of tolerance sheet ", false);
        
        List<String> key=new ArrayList<String>();
        // add primary keys into this list

        comp.runComparison("Unique Comparison id",
        		"first file",
        		"second file", key);
    }

    @Test(enabled=false)
    public void testTableComaprison() throws SQLException {
        Comparison comp = new Comparison(null, "Configuration properties file");
        comp.readTolerance("Path to tolerance file", " Name of tolerance sheet ", false);

        List<String> key=new ArrayList<String>();

     // add primary keys into this list

        List<String> col=new ArrayList<String>();
     // add column names into this list
 

        comp.runComparison("Unique Comparison id", "table 1", "table 2", col, key, "Comparison name");
    }
}