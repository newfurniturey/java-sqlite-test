import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Sample {
    private static String DB_NAME = "sample.db";
    private static Connection _connection = null;
    
    public void connect() throws SQLException {
        _connection = DriverManager.getConnection("jdbc:sqlite:" + DB_NAME);
    }
    
    public void createTable() throws SQLException {
        Statement statement = _connection.createStatement();
        statement.executeUpdate("DROP TABLE IF EXISTS person;");
        statement.executeUpdate("CREATE TABLE IF NOT EXISTS person (id integer, name string);");
        addSampleData();
    }
    
    public void addSampleData() throws SQLException {
        Statement statement = _connection.createStatement();
        statement.executeUpdate("INSERT INTO person VALUES (1, 'leo');");
        statement.executeUpdate("INSERT INTO person VALUES (2, 'geordie');");
        statement.executeUpdate("INSERT INTO person VALUES (3, 'yui');");
    }
    
    public ResultSet query() throws SQLException {
        Statement statement = _connection.createStatement();
        return statement.executeQuery("SELECT id, name FROM person;");
    }
    
    public static void main(String[] args) throws ClassNotFoundException {
        // load the sqlite-JDBC driver using the current class loader
        Class.forName("org.sqlite.JDBC");

        Sample testDb = new Sample();
        try {
            // create a database connection
            testDb.connect();
            testDb.createTable();
            
            ResultSet rs = testDb.query();
            if (!rs.isBeforeFirst()) {
                System.out.println("no data found.");
            } else {
                int numRows = 0;
                System.out.println("id name\n-- ----");
                while (rs.next()) {
                    // read the result set
                    System.out.printf("%-2d %s\n", rs.getInt("id"), rs.getString("name"));
                    numRows++;
                }
                System.out.printf("\nAffected Rows: %d\n", numRows);
            }
        } catch (SQLException e) {
            // if the error message is "out of memory", 
            // it probably means no database file is found
            System.err.println(e.getMessage());
        } finally {
            try {
                if (_connection != null) {
                    _connection.close();
                }
            } catch (SQLException e) {
                // connection close failed.
                System.err.println(e);
            }
        }
    }
    
}
