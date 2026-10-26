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
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;

public class GaraTaiKhoanCaNhan extends JPanel {

    private static final long serialVersionUID = 1L;

    private JTextField txtTenDangNhap;
    private JTextField txtHoTen;
    private JTextField txtVaiTro;
    private JTextField txtEmail;
    private JTextField txtTimKiemBang;

    private JTable tableQuyenHan;

    public GaraTaiKhoanCaNhan() {
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

        JLabel lblTieuDeCard =
                new JLabel("TÀI KHOẢN CÁ NHÂN");

        lblTieuDeCard.setFont(
                new Font("Arial", Font.BOLD, 16)
        );
        lblTieuDeCard.setForeground(new Color(30, 35, 45));

        JLabel lblMoTa = new JLabel(
                "Thông tin tài khoản và quyền truy cập của chủ gara."
        );

        lblMoTa.setFont(new Font("Arial", Font.PLAIN, 12));
        lblMoTa.setForeground(Color.GRAY);

        JPanel panelTieuDe =
                new JPanel(new BorderLayout(0, 8));

        panelTieuDe.setOpaque(false);
        panelTieuDe.add(lblTieuDeCard, BorderLayout.NORTH);
        panelTieuDe.add(lblMoTa, BorderLayout.CENTER);

        // Dữ liệu mẫu của giao diện cũ, chưa lấy từ SQL.
        txtTenDangNhap = new JTextField("chugara");
        txtHoTen = new JTextField("Dương Phú Quý");
        txtVaiTro = new JTextField("Chủ Gara");
        txtEmail = new JTextField(
                "quyskibidiantokbokki@example.com"
        );

        JPanel panelGridFields =
                new JPanel(new GridLayout(2, 2, 40, 10));

        panelGridFields.setBackground(Color.WHITE);

        panelGridFields.setBorder(
                BorderFactory.createEmptyBorder(0, 0, 10, 0)
        );

        panelGridFields.add(
                taoOnhapLieu("Tên đăng nhập", txtTenDangNhap)
        );
        panelGridFields.add(
                taoOnhapLieu("Họ tên", txtHoTen)
        );
        panelGridFields.add(
                taoOnhapLieu("Vai trò", txtVaiTro)
        );
        panelGridFields.add(
                taoOnhapLieu("Email", txtEmail)
        );

        panelPhiaTren.add(panelTieuDe, BorderLayout.NORTH);
        panelPhiaTren.add(panelGridFields, BorderLayout.CENTER);

        // ==================== THANH TÌM KIẾM ====================

        JPanel panelSearchFilter =
                new JPanel(new BorderLayout(10, 0));

        panelSearchFilter.setBackground(
                new Color(245, 247, 250)
        );

        panelSearchFilter.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(220, 225, 230), 1
                        ),
                        BorderFactory.createEmptyBorder(
                                8, 12, 8, 12
                        )
                )
        );

        txtTimKiemBang = new JTextField(
                "Tìm kiếm theo mã, tên hoặc biển số..."
        );

        txtTimKiemBang.setFont(
                new Font("Arial", Font.ITALIC, 12)
        );
        txtTimKiemBang.setForeground(Color.GRAY);
        txtTimKiemBang.setBorder(null);
        txtTimKiemBang.setOpaque(false);

        JComboBox<String> cbTrangThai =
                new JComboBox<>(new String[] {
                        "Tất cả", "Hoạt động", "Khóa"
                });

        cbTrangThai.setFont(
                new Font("Arial", Font.PLAIN, 12)
        );
        cbTrangThai.setFocusable(false);
        cbTrangThai.setBackground(Color.WHITE);

        panelSearchFilter.add(
                txtTimKiemBang, BorderLayout.CENTER
        );
        panelSearchFilter.add(
                cbTrangThai, BorderLayout.EAST
        );

        // ==================== BẢNG QUYỀN HẠN ====================

        String[] columnNames = {
                "Nhóm chức năng", "Quyền", "Phạm vi"
        };

        Object[][] data = {
                {
                        "Khách hàng / Xe",
                        "Xem, thêm, sửa",
                        "Toàn gara"
                },
                {
                        "Dịch vụ / Phụ tùng",
                        "Quản lý danh mục",
                        "Toàn gara"
                },
                {
                        "Hóa đơn / Thống kê",
                        "Duyệt, thu tiền, xem số liệu",
                        "Toàn gara"
                }
        };

        DefaultTableModel model =
                new DefaultTableModel(data, columnNames) {

                    private static final long serialVersionUID = 1L;

                    @Override
                    public boolean isCellEditable(int row, int column) {
                        return false;
                    }
                };

        tableQuyenHan = new JTable(model);

        tableQuyenHan.setRowHeight(32);
        tableQuyenHan.setFont(
                new Font("Arial", Font.PLAIN, 12)
        );

        tableQuyenHan.getTableHeader().setFont(
                new Font("Arial", Font.BOLD, 12)
        );
        tableQuyenHan.getTableHeader().setBackground(
                new Color(230, 235, 240)
        );
        tableQuyenHan.getTableHeader().setReorderingAllowed(false);

        tableQuyenHan.setSelectionBackground(
                new Color(40, 100, 150)
        );
        tableQuyenHan.setSelectionForeground(Color.WHITE);
        tableQuyenHan.setFillsViewportHeight(true);

        JScrollPane scrollPane =
                new JScrollPane(tableQuyenHan);

        scrollPane.getViewport().setBackground(Color.WHITE);

        scrollPane.setBorder(
                BorderFactory.createLineBorder(
                        new Color(220, 225, 230)
                )
        );

        JPanel panelTableSection =
                new JPanel(new BorderLayout(0, 10));

        panelTableSection.setBackground(Color.WHITE);

        panelTableSection.setBorder(
                BorderFactory.createEmptyBorder(0, 0, 15, 0)
        );

        panelTableSection.add(
                panelSearchFilter, BorderLayout.NORTH
        );
        panelTableSection.add(
                scrollPane, BorderLayout.CENTER
        );

        // ==================== CÁC NÚT ====================

        JPanel panelBottomButtons =
                new JPanel(
                        new FlowLayout(FlowLayout.RIGHT, 15, 0)
                );

        panelBottomButtons.setBackground(Color.WHITE);

        JButton btnDoiMatKhau =
                new JButton("Đổi mật khẩu");

        btnDoiMatKhau.setFont(
                new Font("Arial", Font.BOLD, 13)
        );
        btnDoiMatKhau.setBackground(Color.WHITE);
        btnDoiMatKhau.setForeground(new Color(50, 55, 65));
        btnDoiMatKhau.setContentAreaFilled(false);
        btnDoiMatKhau.setOpaque(true);
        btnDoiMatKhau.setFocusPainted(false);

        btnDoiMatKhau.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(200, 205, 210), 1
                        ),
                        BorderFactory.createEmptyBorder(
                                8, 16, 8, 16
                        )
                )
        );

        btnDoiMatKhau.setCursor(
                Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        );

        // Chuyển màn hình trong cùng cửa sổ chính.
        btnDoiMatKhau.addActionListener(e -> {
            Window cuaSo = SwingUtilities.getWindowAncestor(this);

            if (cuaSo instanceof GaraManHinhChinh) {
                GaraManHinhChinh manHinhChinh =
                        (GaraManHinhChinh) cuaSo;

                manHinhChinh.chuyenManHinh("DOI_MAT_KHAU");
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Hãy mở màn hình này từ GaraManHinhChinh."
                );
            }
        });

        JButton btnLuuThongTin =
                new JButton("Lưu thông tin");

        btnLuuThongTin.setFont(
                new Font("Arial", Font.BOLD, 13)
        );
        btnLuuThongTin.setBackground(new Color(180, 30, 40));
        btnLuuThongTin.setForeground(Color.WHITE);
        btnLuuThongTin.setContentAreaFilled(false);
        btnLuuThongTin.setOpaque(true);
        btnLuuThongTin.setBorderPainted(false);
        btnLuuThongTin.setFocusPainted(false);

        btnLuuThongTin.setBorder(
                BorderFactory.createEmptyBorder(
                        9, 18, 9, 18
                )
        );

        btnLuuThongTin.setCursor(
                Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        );

        // Chưa triển khai DAO, chưa lưu dữ liệu.
        btnLuuThongTin.addActionListener(e ->
                JOptionPane.showMessageDialog(
                        this,
                        "Chưa kết nối DAO. Thông tin chưa được lưu.",
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

        panelBottomButtons.add(btnDoiMatKhau);
        panelBottomButtons.add(btnLuuThongTin);
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
            // Giữ cách mở màn hình đăng nhập của code cũ.
            new GaraDangNhap().setVisible(true);

            // Đóng cửa sổ chứa panel, thay cho this.dispose().
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

    private JPanel taoOnhapLieu(
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

    // Chạy thử riêng giao diện, không cần kết nối database.
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
                    new JFrame("Tài khoản cá nhân - NIGHT INVADER");

            frame.setDefaultCloseOperation(
                    JFrame.DISPOSE_ON_CLOSE
            );
            frame.setContentPane(new GaraTaiKhoanCaNhan());
            frame.setSize(1340, 840);
            frame.setLocationRelativeTo(null);
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            frame.setVisible(true);
        });
    }
}