import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseExample {


	private static Connection conn = null;
	private Statement stmt = null;
	private String dbUser = null;
	private String dbPass = null;

	public static void main(String[] args) {

		//Connection String
		//Server name:  52.206.157.109
		//Database name:  U03QIu
		//Username:  U03QIu
		//Password:  53688051379
		// JDBC driver name and database URL
		final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
		final String DB_URL = "jdbc:mysql://52.206.157.109/U03QIu";

		//  Database credentials
		final String DBUSER = "U03QIu";
		final String DBPASS = "53688051379";

		boolean res = false;

		try {
			//STEP 2: Register JDBC driver
			Class.forName("com.mysql.jdbc.Driver");

			//STEP 3: Open a connection
			System.out.println("Connecting to database...");
			conn = DriverManager.getConnection(DB_URL, DBUSER, DBPASS);
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		} catch (SQLException ex) {
			ex.printStackTrace();

		}
		Statement stmt;

		ResultSet rs = null;
		try {

			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT country FROM country");

			while (rs.next()) {
				String country = rs.getString(1);
				System.out.println(country);
			}
		} catch (SQLException ex) {
			ex.printStackTrace();

		}

	}
}
