package unittest;
import java.sql.Connection;
import java.sql.SQLException;
import org.testng.annotations.Test;
import dbutilities.*;
public class TestDBConnection
{
    DBConnection conn = new DBConnection();
    Connection con;

    @Test(enabled = false)
    public void testSQLAuthenticationConnection()
            throws SQLException
    {
        con = conn.getDBConnection("DB Server", "DB User", "DB Password");
        System.out.println(this.con);
        this.con.close();
    }

    @Test(enabled = false)
    public void testWindowAuthenticationConnection()
            throws SQLException
    {
        this.con = this.conn.getDBConnection("DB Server");
        System.out.println(this.con);
        this.con.close();
    }

    @Test(enabled = false)
    public void testSQLAuthenticationConnectionWithDB()
            throws SQLException
    {
        this.con = this.conn.getDBConnection("DB Server", "Db Table", "Db User", "Db Password");
        System.out.println(this.con);
        this.con.close();
    }

    @Test(enabled = false)
    public void testWindowAuthenticationConnectionWithDB()
            throws SQLException
    {
        this.con = this.conn.getDBConnection("DB User", "DB Table");
        System.out.println(this.con);
        this.con.close();
    }
}
