package unittest;
import fileutilities.ExcelWriter;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.Test;

public class TestExcelWriter {
	@Test(enabled=false)
	public void testAddRow() {
		ExcelWriter ew = new ExcelWriter();
		List<String> val = new ArrayList<>();
		val.add("Value1");
		val.add("Value2");
		val.add("Value3");
		ew.addNewRow("File path","Test2", false, val);	
		System.out.println("----------Done------");
	}
	
	@Test(enabled=false)
	public void testAddSheet() {
		ExcelWriter ew = new ExcelWriter();
		ew.createSheet("file path","Test4");
	}
	
	@Test(enabled=false)
	public void testEditRow() {
		ExcelWriter ew = new ExcelWriter();
		List<String> val = new ArrayList<>();
		val.add("Value1");
		val.add("Value2");
		val.add("Value3");
		ew.updateRow("file path","Test1", false, 2, val);
	}
	
	@Test(enabled=false)
	public void setCellValue() {
		ExcelWriter ew = new ExcelWriter("file path");
		ew.setCellValue("file path","Test3", true, 2, true, 1, true, "SetCell");
		ew.setCellValue("file path","Test1", false, 1, false, 1, false, "SetCell");
	}
	
	@Test(enabled=false)
	public void testFileUpdateFlow() {
		ExcelWriter ew = new ExcelWriter("file path");
		ew.createSheet("Test4");
		ew.setCellValue("Test1", false, 1, false, 1, false, "SetCell");
		ew.setCellValue("Test3", true, 2, true, 1, true, "SetCell");
		List<String> val = new ArrayList<>();
		val.add("Val1");
		val.add("Val2");
		val.add("Val3");
		ew.addNewRow("Test2", false, val);
		ew.addNewRow("Test1", false, val);
		ew.updateRow("Test1", false, 2, val);
		ew.writeAllChanges("file path");		
	}
	
	
}