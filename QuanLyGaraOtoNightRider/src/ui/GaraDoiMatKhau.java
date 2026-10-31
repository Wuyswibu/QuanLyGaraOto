package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Window;
import java.sql.SQLException;

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

import connectDB.ConnectDB;
import dao.TaiKhoanDao;
import entity.TaiKhoan;

public class GaraDoiMatKhau extends JPanel {

    private static final long serialVersionUID = 1L;

    private JTextField txtTaiKhoan;
    private JPasswordField txtMatKhauHienTai;
    private JPasswordField txtMatKhauMoi;
    private JPasswordField txtXacNhanMatKhau;

    private JTable tableDieuKien;

    private final TaiKhoanDao taiKhoanDao;
    private String tenDangNhapHienTai;

    public GaraDoiMatKhau() {
        this("chugara");
    }

    public GaraDoiMatKhau(String tenDangNhapHienTai) {
        this.tenDangNhapHienTai = (tenDangNhapHienTai != null && !tenDangNhapHienTai.trim().isEmpty()) 
                ? tenDangNhapHienTai.trim() : "";
        
        damBaoKetNoiCoSoDuLieu();
        this.taiKhoanDao = new TaiKhoanDao();

        khoiTaoGiaoDien();
        
        // Đặt đúng tên tài khoản được truyền từ màn hình trước
        txtTaiKhoan.setText(this.tenDangNhapHienTai);
    }

    private void damBaoKetNoiCoSoDuLieu() {
        try {
            if (ConnectDB.getConnection() == null || ConnectDB.getConnection().isClosed()) {
                ConnectDB.getInstance().connect();
            }
        } catch (Exception ex) {
            System.err.println("Cảnh báo: Không thể kết nối DB");
        }
    }

    /**
     * Cho phép đổi tài khoản cần cập nhật mật khẩu nếu màn hình chính tái sử dụng panel
     */
    public void setTenDangNhap(String tenDangNhap) {
        this.tenDangNhapHienTai = tenDangNhap;
        if (txtTaiKhoan != null) {
            txtTaiKhoan.setText(tenDangNhap);
        }
        xoaCacOmatKhau();
    }

    private void khoiTaoGiaoDien() {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 243, 246));
        setBorder(BorderFactory.createEmptyBorder(20, 45, 20, 45));

        JPanel panelCardChinh = new JPanel(new BorderLayout(0, 12));
        panelCardChinh.setBackground(Color.WHITE);
        panelCardChinh.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(210, 215, 220), 1),
                        BorderFactory.createEmptyBorder(25, 30, 25, 30)
                )
        );

        // ==================== TIÊU ĐỀ VÀ FORM ====================
        JPanel panelPhiaTren = new JPanel(new BorderLayout(0, 12));
        panelPhiaTren.setOpaque(false);

        JLabel lblTieuDe = new JLabel("ĐỔI MẬT KHẨU");
        lblTieuDe.setFont(new Font("Arial", Font.BOLD, 16));
        lblTieuDe.setForeground(new Color(30, 35, 45));

        JLabel lblMoTa = new JLabel("Nhập chính xác mật khẩu hiện tại và mật khẩu mới bạn muốn đặt.");
        lblMoTa.setFont(new Font("Arial", Font.PLAIN, 12));
        lblMoTa.setForeground(Color.GRAY);

        JPanel panelTieuDe = new JPanel(new BorderLayout(0, 8));
        panelTieuDe.setOpaque(false);
        panelTieuDe.add(lblTieuDe, BorderLayout.NORTH);
        panelTieuDe.add(lblMoTa, BorderLayout.CENTER);

        txtTaiKhoan = new JTextField();
        txtTaiKhoan.setEditable(false); // Không cho sửa tên tài khoản

        txtMatKhauHienTai = new JPasswordField();
        txtMatKhauMoi = new JPasswordField();
        txtXacNhanMatKhau = new JPasswordField();

        JPanel panelGridFields = new JPanel(new GridLayout(2, 2, 40, 10));
        panelGridFields.setBackground(Color.WHITE);
        panelGridFields.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        panelGridFields.add(taoTruongNhap("Tên đăng nhập", txtTaiKhoan));
        panelGridFields.add(taoTruongNhap("Mật khẩu hiện tại (*)", txtMatKhauHienTai));
        panelGridFields.add(taoTruongNhap("Mật khẩu mới", txtMatKhauMoi));
        panelGridFields.add(taoTruongNhap("Xác nhận mật khẩu mới", txtXacNhanMatKhau));

        panelPhiaTren.add(panelTieuDe, BorderLayout.NORTH);
        panelPhiaTren.add(panelGridFields, BorderLayout.CENTER);

        // ==================== BẢNG THÔNG TIN QUY TẮC ====================
        String[] columnNames = {"Mục", "Yêu cầu thực hiện"};
        Object[][] data = {
                {"Mật khẩu hiện tại", "Khớp chính xác mật khẩu đang dùng của tài khoản này"},
                {"Xác nhận mật khẩu", "Ô xác nhận phải nhập trùng khớp với ô mật khẩu mới"}
        };

        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableDieuKien = new JTable(model);
        tableDieuKien.setRowHeight(32);
        tableDieuKien.setFont(new Font("Arial", Font.PLAIN, 12));
        tableDieuKien.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tableDieuKien.getTableHeader().setBackground(new Color(230, 235, 240));
        tableDieuKien.getTableHeader().setReorderingAllowed(false);
        tableDieuKien.setSelectionBackground(new Color(40, 100, 150));
        tableDieuKien.setSelectionForeground(Color.WHITE);
        tableDieuKien.setFillsViewportHeight(true);

        JScrollPane scrollPane = new JScrollPane(tableDieuKien);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 225, 230)));

        JPanel panelTableSection = new JPanel(new BorderLayout());
        panelTableSection.setBackground(Color.WHITE);
        panelTableSection.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        panelTableSection.add(scrollPane, BorderLayout.CENTER);

        // ==================== CÁC NÚT ĐIỀU KHIỂN ====================
        JPanel panelBottomButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        panelBottomButtons.setBackground(Color.WHITE);

        JButton btnHuy = new JButton("Quay lại");
        btnHuy.setFont(new Font("Arial", Font.BOLD, 13));
        btnHuy.setBackground(Color.WHITE);
        btnHuy.setForeground(new Color(50, 55, 65));
        btnHuy.setContentAreaFilled(false);
        btnHuy.setOpaque(true);
        btnHuy.setFocusPainted(false);
        btnHuy.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 205, 210), 1),
                BorderFactory.createEmptyBorder(8, 20, 8, 20)
        ));
        btnHuy.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnHuy.addActionListener(e -> quayVeTaiKhoan());

        JButton btnXacNhan = new JButton("Xác nhận đổi mật khẩu");
        btnXacNhan.setFont(new Font("Arial", Font.BOLD, 13));
        btnXacNhan.setBackground(new Color(180, 30, 40));
        btnXacNhan.setForeground(Color.WHITE);
        btnXacNhan.setContentAreaFilled(false);
        btnXacNhan.setOpaque(true);
        btnXacNhan.setBorderPainted(false);
        btnXacNhan.setFocusPainted(false);
        btnXacNhan.setBorder(BorderFactory.createEmptyBorder(9, 18, 9, 18));
        btnXacNhan.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Xử lý đổi mật khẩu
        btnXacNhan.addActionListener(e -> xuLyDoiMatKhau());

        JButton btnDangXuat = new JButton("Đăng Xuất →");
        btnDangXuat.setFont(new Font("Arial", Font.BOLD, 14));
        btnDangXuat.setForeground(new Color(180, 30, 40));
        btnDangXuat.setBackground(Color.WHITE);
        btnDangXuat.setFocusPainted(false);
        btnDangXuat.setBorderPainted(false);
        btnDangXuat.setContentAreaFilled(false);
        btnDangXuat.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnDangXuat.addActionListener(e -> xuLyDangXuat());

        panelBottomButtons.add(btnHuy);
        panelBottomButtons.add(btnXacNhan);
        panelBottomButtons.add(Box.createHorizontalStrut(10));
        panelBottomButtons.add(btnDangXuat);

        panelCardChinh.add(panelPhiaTren, BorderLayout.NORTH);
        panelCardChinh.add(panelTableSection, BorderLayout.CENTER);
        panelCardChinh.add(panelBottomButtons, BorderLayout.SOUTH);

        add(panelCardChinh, BorderLayout.CENTER);
    }

    /**
     * XỬ LÝ ĐỔI MẬT KHẨU LINH HOẠT:
     * - Chỉ cần mật khẩu cũ nhập vào khớp với mật khẩu trong DB.
     * - Hai ô mật khẩu mới trùng nhau.
     * - Không bắt buộc độ dài hay ký tự đặc biệt.
     */
    private void xuLyDoiMatKhau() {
        String taiKhoan = txtTaiKhoan.getText().trim();
        String mkHienTai = new String(txtMatKhauHienTai.getPassword()).trim();
        String mkMoi = new String(txtMatKhauMoi.getPassword()).trim();
        String xacNhanMk = new String(txtXacNhanMatKhau.getPassword()).trim();

        if (taiKhoan.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy tên tài khoản!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (mkHienTai.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập mật khẩu hiện tại!", "Nhắc nhở", JOptionPane.WARNING_MESSAGE);
            txtMatKhauHienTai.requestFocus();
            return;
        }

        if (mkMoi.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập mật khẩu mới!", "Nhắc nhở", JOptionPane.WARNING_MESSAGE);
            txtMatKhauMoi.requestFocus();
            return;
        }

        if (!mkMoi.equals(xacNhanMk)) {
            JOptionPane.showMessageDialog(this, "Mật khẩu mới và xác nhận mật khẩu không khớp nhau!", "Nhắc nhở", JOptionPane.WARNING_MESSAGE);
            txtXacNhanMatKhau.requestFocus();
            return;
        }

        try {
            damBaoKetNoiCoSoDuLieu();

            // Tìm tài khoản trong SQL
            TaiKhoan tk = taiKhoanDao.timTheoTenDangNhap(taiKhoan);
            if (tk == null) {
                JOptionPane.showMessageDialog(this, "Tài khoản [" + taiKhoan + "] không tồn tại trên hệ thống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // SO KHỚP MẬT KHẨU HIỆN TẠI VỚI DATABASE
            if (!tk.getMatKhau().equals(mkHienTai)) {
                JOptionPane.showMessageDialog(this, "Mật khẩu hiện tại không đúng nha! Vui lòng thử lại.", "Sai mật khẩu", JOptionPane.ERROR_MESSAGE);
                txtMatKhauHienTai.requestFocus();
                return;
            }

            // Cập nhật mật khẩu mới xuống SQL Server
            boolean thanhCong = taiKhoanDao.doiMatKhau(tk.getMaTaiKhoan(), mkMoi);
            if (thanhCong) {
                JOptionPane.showMessageDialog(this, "Đổi mật khẩu cho tài khoản [" + taiKhoan + "] thành công rồi nhé!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                xoaCacOmatKhau();
                quayVeTaiKhoan();
            } else {
                JOptionPane.showMessageDialog(this, "Chưa cập nhật được mật khẩu, hãy kiểm tra lại nhé!", "Thông báo", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi kết nối CSDL: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void quayVeTaiKhoan() {
        Window cuaSo = SwingUtilities.getWindowAncestor(this);
        if (cuaSo instanceof GaraManHinhChinh) {
            xoaCacOmatKhau();
            GaraManHinhChinh manHinhChinh = (GaraManHinhChinh) cuaSo;
            manHinhChinh.chuyenManHinh("TAI_KHOAN");
        } else {
            xoaCacOmatKhau();
            if (cuaSo != null) {
                cuaSo.dispose();
            }
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

            Window cuaSo = SwingUtilities.getWindowAncestor(this);
            if (cuaSo != null) {
                cuaSo.dispose();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                    this,
                    "Không mở được màn hình đăng nhập:\n" + ex.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private JPanel taoTruongNhap(String labelText, JTextField textField) {
        JPanel panel = new JPanel(new BorderLayout(0, 5));
        panel.setBackground(Color.WHITE);

        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("Arial", Font.PLAIN, 12));
        lbl.setForeground(new Color(80, 85, 95));

        textField.setFont(new Font("Arial", Font.PLAIN, 13));
        textField.setPreferredSize(new Dimension(250, 32));
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 205, 210), 1),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));

        panel.add(lbl, BorderLayout.NORTH);
        panel.add(textField, BorderLayout.CENTER);

        return panel;
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        try {
            ConnectDB.getInstance().connect();
        } catch (SQLException e) {
            System.err.println("Lỗi mở kết nối DB: " + e.getMessage());
        }

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Đổi mật khẩu - NIGHT INVADER");
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setContentPane(new GaraDoiMatKhau("nv01")); // Chạy thử với tài khoản nv01
            frame.setSize(1340, 840);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}