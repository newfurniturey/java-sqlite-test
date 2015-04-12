import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Sample {
    private static String DB_NAME = "sample.db";
    
    public Connection connect() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:" + DB_NAME);
    }
    
    public void createTable(Statement stmt) throws SQLException {
        stmt.executeUpdate("DROP TABLE IF EXISTS person;");
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS person (id integer, name string);");
    }
    
    public static void main(String[] args) throws ClassNotFoundException {
        // load the sqlite-JDBC driver using the current class loader
        Class.forName("org.sqlite.JDBC");

        Sample testDb = new Sample();
        Connection connection = null;
        try {
            // create a database connection
            connection = testDb.connect();
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);  // set timeout to 30 sec.

            testDb.createTable(statement);
            statement.executeUpdate("insert into person values(1, 'leo');");
            statement.executeUpdate("insert into person values(2, 'yui');");
            
            ResultSet rs = statement.executeQuery("select id, name from person;");
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
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                // connection close failed.
                System.err.println(e);
            }
        }
    }
    
}
