package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Window;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;

public class GaraDoiMatKhau extends JPanel {

    private static final long serialVersionUID = 1L;

    private JTextField txtTaiKhoan;
    private JPasswordField txtMatKhauHienTai;
    private JPasswordField txtMatKhauMoi;
    private JPasswordField txtXacNhanMatKhau;

    private JTable tableDieuKien;

    public GaraDoiMatKhau() {
        khoiTaoGiaoDien();
    }

    private void khoiTaoGiaoDien() {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 243, 246));

        setBorder(
                BorderFactory.createEmptyBorder(20, 45, 20, 45)
        );

        // Menu, toolbar và footer dùng chung của màn hình chính.
        JPanel panelCardChinh =
                new JPanel(new BorderLayout(0, 12));

        panelCardChinh.setBackground(Color.WHITE);

        panelCardChinh.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(210, 215, 220), 1
                        ),
                        BorderFactory.createEmptyBorder(
                                25, 30, 25, 30
                        )
                )
        );

        // ==================== TIÊU ĐỀ VÀ FORM ====================

        JPanel panelPhiaTren =
                new JPanel(new BorderLayout(0, 12));

        panelPhiaTren.setOpaque(false);

        JLabel lblTieuDe = new JLabel("ĐỔI MẬT KHẨU");
        lblTieuDe.setFont(new Font("Arial", Font.BOLD, 16));
        lblTieuDe.setForeground(new Color(30, 35, 45));

        JLabel lblMoTa = new JLabel(
                "Nhập mật khẩu hiện tại và xác nhận mật khẩu mới."
        );

        lblMoTa.setFont(new Font("Arial", Font.PLAIN, 12));
        lblMoTa.setForeground(Color.GRAY);

        JPanel panelTieuDe =
                new JPanel(new BorderLayout(0, 8));

        panelTieuDe.setOpaque(false);
        panelTieuDe.add(lblTieuDe, BorderLayout.NORTH);
        panelTieuDe.add(lblMoTa, BorderLayout.CENTER);

        // Dữ liệu mẫu, chưa lấy từ tài khoản đăng nhập.
        txtTaiKhoan = new JTextField("chugara");
        txtTaiKhoan.setEditable(false);

        txtMatKhauHienTai = new JPasswordField();
        txtMatKhauMoi = new JPasswordField();
        txtXacNhanMatKhau = new JPasswordField();

        JPanel panelGridFields =
                new JPanel(new GridLayout(2, 2, 40, 10));

        panelGridFields.setBackground(Color.WHITE);

        panelGridFields.setBorder(
                BorderFactory.createEmptyBorder(0, 0, 10, 0)
        );

        panelGridFields.add(
                taoTruongNhap("Tài khoản", txtTaiKhoan)
        );
        panelGridFields.add(
                taoTruongNhap(
                        "Mật khẩu hiện tại", txtMatKhauHienTai
                )
        );
        panelGridFields.add(
                taoTruongNhap("Mật khẩu mới", txtMatKhauMoi)
        );
        panelGridFields.add(
                taoTruongNhap(
                        "Xác nhận mật khẩu", txtXacNhanMatKhau
                )
        );

        panelPhiaTren.add(panelTieuDe, BorderLayout.NORTH);
        panelPhiaTren.add(panelGridFields, BorderLayout.CENTER);

        // ==================== BẢNG ĐIỀU KIỆN ====================

        String[] columnNames = {
                "Điều kiện", "Yêu cầu"
        };

        Object[][] data = {
                {"Độ dài", "Tối thiểu 8 ký tự"},
                {"Xác nhận", "Hai mật khẩu mới phải trùng nhau"}
        };

        DefaultTableModel model =
                new DefaultTableModel(data, columnNames) {

                    private static final long serialVersionUID = 1L;

                    @Override
                    public boolean isCellEditable(int row, int column) {
                        return false;
                    }
                };

        tableDieuKien = new JTable(model);

        tableDieuKien.setRowHeight(32);
        tableDieuKien.setFont(
                new Font("Arial", Font.PLAIN, 12)
        );

        tableDieuKien.getTableHeader().setFont(
                new Font("Arial", Font.BOLD, 12)
        );
        tableDieuKien.getTableHeader().setBackground(
                new Color(230, 235, 240)
        );
        tableDieuKien.getTableHeader().setReorderingAllowed(false);

        tableDieuKien.setSelectionBackground(
                new Color(40, 100, 150)
        );
        tableDieuKien.setSelectionForeground(Color.WHITE);
        tableDieuKien.setFillsViewportHeight(true);

        JScrollPane scrollPane =
                new JScrollPane(tableDieuKien);

        scrollPane.getViewport().setBackground(Color.WHITE);

        scrollPane.setBorder(
                BorderFactory.createLineBorder(
                        new Color(220, 225, 230)
                )
        );

        JPanel panelTableSection =
                new JPanel(new BorderLayout());

        panelTableSection.setBackground(Color.WHITE);

        panelTableSection.setBorder(
                BorderFactory.createEmptyBorder(0, 0, 15, 0)
        );

        panelTableSection.add(scrollPane, BorderLayout.CENTER);

        // ==================== CÁC NÚT ====================

        JPanel panelBottomButtons =
                new JPanel(
                        new FlowLayout(FlowLayout.RIGHT, 15, 0)
                );

        panelBottomButtons.setBackground(Color.WHITE);

        JButton btnHuy = new JButton("Hủy");

        btnHuy.setFont(new Font("Arial", Font.BOLD, 13));
        btnHuy.setBackground(Color.WHITE);
        btnHuy.setForeground(new Color(50, 55, 65));
        btnHuy.setContentAreaFilled(false);
        btnHuy.setOpaque(true);
        btnHuy.setFocusPainted(false);

        btnHuy.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(200, 205, 210), 1
                        ),
                        BorderFactory.createEmptyBorder(
                                8, 20, 8, 20
                        )
                )
        );

        btnHuy.setCursor(
                Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        );

        btnHuy.addActionListener(e -> quayVeTaiKhoan());

        JButton btnXacNhan =
                new JButton("Xác nhận đổi mật khẩu");

        btnXacNhan.setFont(
                new Font("Arial", Font.BOLD, 13)
        );
        btnXacNhan.setBackground(new Color(180, 30, 40));
        btnXacNhan.setForeground(Color.WHITE);
        btnXacNhan.setContentAreaFilled(false);
        btnXacNhan.setOpaque(true);
        btnXacNhan.setBorderPainted(false);
        btnXacNhan.setFocusPainted(false);

        btnXacNhan.setBorder(
                BorderFactory.createEmptyBorder(
                        9, 18, 9, 18
                )
        );

        btnXacNhan.setCursor(
                Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        );

        // Chỉ hiển thị thông báo, chưa xử lý đổi mật khẩu.
        btnXacNhan.addActionListener(e ->
                JOptionPane.showMessageDialog(
                        this,
                        "Chức năng đổi mật khẩu chưa kết nối DAO.",
                        "Thông báo",
                        JOptionPane.INFORMATION_MESSAGE
                )
        );

        JButton btnDangXuat =
                new JButton("Đăng Xuất →");

        btnDangXuat.setFont(
                new Font("Arial", Font.BOLD, 14)
        );
        btnDangXuat.setForeground(new Color(180, 30, 40));
        btnDangXuat.setBackground(Color.WHITE);
        btnDangXuat.setFocusPainted(false);
        btnDangXuat.setBorderPainted(false);
        btnDangXuat.setContentAreaFilled(false);

        btnDangXuat.setCursor(
                Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        );

        btnDangXuat.addActionListener(e -> xuLyDangXuat());

        panelBottomButtons.add(btnHuy);
        panelBottomButtons.add(btnXacNhan);
        panelBottomButtons.add(Box.createHorizontalStrut(10));
        panelBottomButtons.add(btnDangXuat);

        // ==================== GẮN NỘI DUNG ====================

        panelCardChinh.add(
                panelPhiaTren, BorderLayout.NORTH
        );
        panelCardChinh.add(
                panelTableSection, BorderLayout.CENTER
        );
        panelCardChinh.add(
                panelBottomButtons, BorderLayout.SOUTH
        );

        add(panelCardChinh, BorderLayout.CENTER);
    }

    private void quayVeTaiKhoan() {
        Window cuaSo =
                SwingUtilities.getWindowAncestor(this);

        if (cuaSo instanceof GaraManHinhChinh) {
            xoaCacOmatKhau();

            GaraManHinhChinh manHinhChinh =
                    (GaraManHinhChinh) cuaSo;

            manHinhChinh.chuyenManHinh("TAI_KHOAN");

        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "Hãy mở giao diện này từ GaraManHinhChinh."
            );
        }
    }

    private void xoaCacOmatKhau() {
        txtMatKhauHienTai.setText("");
        txtMatKhauMoi.setText("");
        txtXacNhanMatKhau.setText("");
    }

    private void xuLyDangXuat() {
        int luaChon = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc chắn muốn đăng xuất khỏi hệ thống?",
                "Xác nhận đăng xuất",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (luaChon != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            new GaraDangNhap().setVisible(true);
            xoaCacOmatKhau();

            Window cuaSo =
                    SwingUtilities.getWindowAncestor(this);

            if (cuaSo != null) {
                cuaSo.dispose();
            }

        } catch (Exception ex) {
            ex.printStackTrace();

            JOptionPane.showMessageDialog(
                    this,
                    "Không mở được màn hình đăng nhập:\n"
                            + ex.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private JPanel taoTruongNhap(
            String labelText,
            JTextField textField) {

        JPanel panel =
                new JPanel(new BorderLayout(0, 5));

        panel.setBackground(Color.WHITE);

        JLabel lbl = new JLabel(labelText);

        lbl.setFont(new Font("Arial", Font.PLAIN, 12));
        lbl.setForeground(new Color(80, 85, 95));

        textField.setFont(
                new Font("Arial", Font.PLAIN, 13)
        );
        textField.setPreferredSize(new Dimension(250, 32));

        textField.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(200, 205, 210), 1
                        ),
                        BorderFactory.createEmptyBorder(
                                4, 8, 4, 8
                        )
                )
        );

        panel.add(lbl, BorderLayout.NORTH);
        panel.add(textField, BorderLayout.CENTER);

        return panel;
    }

    // Chạy thử riêng giao diện, không cần database.
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName()
            );
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            JFrame frame =
                    new JFrame("Đổi mật khẩu - NIGHT INVADER");

            frame.setDefaultCloseOperation(
                    JFrame.DISPOSE_ON_CLOSE
            );
            frame.setContentPane(new GaraDoiMatKhau());
            frame.setSize(1340, 840);
            frame.setLocationRelativeTo(null);
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            frame.setVisible(true);
        });
    }
}