package unittest;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.Test;

import dbutilities.DBConnection;
import dbutilities.DDLOperations;

public class TestDDLOperations
{
    DDLOperations ddlOp = new DDLOperations();
    DBConnection conn = new DBConnection();
    Connection con; 

    @Test(priority=1, enabled=false)
    public void testDropTableOperation()
    {
        System.out.println(this.ddlOp.dropTable(this.con, "Table name"));
    }

    @Test(priority=2,enabled=false)
    public void testCreateTableOperation()
    {
        List<String> col = new ArrayList();
        col.add("Col1");
        col.add("Col2");
        System.out.println(this.ddlOp.createTable(this.con, col, "Table name", 10));
    }
}