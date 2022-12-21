package unittest;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

import dbutilities.DBConnection;
import dbutilities.DMLOperations;

public class TestDMLOperations
{
    DMLOperations dmlOp = new DMLOperations();
    DBConnection conn = new DBConnection();
    Connection con=conn.getDBConnection("Db Server","user","password");
    @Test(enabled = false)
    public void testInsertOperation()
    {
        List<List<String>> data = new ArrayList();
        List<String> rowData = new ArrayList();
        rowData.add("1Col1");
        rowData.add("1Col2");
        data.add(rowData);
        this.dmlOp.insertData(this.con, data, "DemoDBUtility");
    }

    @Test(enabled=false)
    public void testSelectOperation() throws SQLException
    {
       Statement st = con.createStatement();
       ResultSet rs =st.executeQuery("select top 1 * from test_Stage.[dbo].[calcData]");
      int col= rs.getMetaData().getColumnCount();
      //rs.getMetaData().get

      for(int i=1;i<=col;i++) {

    	  System.out.println(rs.getMetaData().getColumnName(i));

      }
      System.out.println(col);

    }
}

