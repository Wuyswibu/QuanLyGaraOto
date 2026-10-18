package main;

import java.sql.SQLException;
import connectDB.ConnectDB;

public class Main {
    public static void main(String[] args) {
        try {
            ConnectDB.getInstance().connect();
            System.out.println("Kết nối database thành công");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}