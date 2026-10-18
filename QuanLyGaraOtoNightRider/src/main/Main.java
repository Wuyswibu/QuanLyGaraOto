package main;

import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import connectDB.ConnectDB;
import ui.QuanLyXeFrame;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName()
            );
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            ConnectDB.getInstance().connect();
            System.out.println("Kết nối database thành công");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                    null,
                    "Không kết nối được database: " + e.getMessage()
            );
            return;
        }

        SwingUtilities.invokeLater(() -> {
            QuanLyXeFrame giaoDien = new QuanLyXeFrame();
            giaoDien.taoCuaSo().setVisible(true);
        });
    }
}