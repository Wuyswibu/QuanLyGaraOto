package connectDB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectDB {
    public static Connection conn = null;
    public static ConnectDB instance = new ConnectDB();

    public static ConnectDB getInstance() {
        return instance;
    }

    public Connection connect() throws SQLException {
    	String url = "jdbc:sqlserver://localhost:1433;databaseName=QuanLyBanCaPhe;encrypt=false;trustServerCertificate=true";
    	String user = "sa";
    	String password = "sapassword";
        conn = DriverManager.getConnection(url, user, password);
        return conn;
    }
  
    public void disconnect() {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                // TODO: handle exception
                e.printStackTrace();
            }
        }
    }

    public static Connection getConnection() {
        return conn;
    }

}
