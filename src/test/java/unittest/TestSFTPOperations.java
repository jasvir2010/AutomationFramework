package unittest;
import org.testng.annotations.Test;

import fileutilities.SFTPOperations;

public class TestSFTPOperations {
	
	
	@Test(enabled=false)
	public void testConnection() {
		
		SFTPOperations sftpo = new SFTPOperations();
		System.out.println(sftpo);
	}
}
