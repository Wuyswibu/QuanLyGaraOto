package main;

import connectDB.ConnectDB;

import ui.GaraManHinhChinh;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.sql.SQLException;

public class main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                ConnectDB.getInstance().connect();
                
                new GaraManHinhChinh().setVisible(true);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(
                        null,
                        "Không thể kết nối database!",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE
                );
                e.printStackTrace();
            }
        });
    }
}