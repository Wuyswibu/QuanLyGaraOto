package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;

import connectDB.ConnectDB;
import dao.TaiKhoanDao;
import entity.TaiKhoan;

public class GaraTaiKhoanCaNhan extends JPanel {

    private static final long serialVersionUID = 1L;

    // Component giao diện
    private JTextField txtTenDangNhap;
    private JTextField txtHoTen;
    private JTextField txtVaiTro;
    private JTextField txtEmail;
    private JTextField txtTimKiemBang;
    private JComboBox<String> cbTrangThai;

    private JTable tableQuyenHan;
    private DefaultTableModel modelTable;

    // DAO quản lý dữ liệu
    private TaiKhoanDao taiKhoanDao;
    private String tenDangNhapHienTai;

    public GaraTaiKhoanCaNhan() {
        this("chugara");
    }

    public GaraTaiKhoanCaNhan(String tenDangNhapHienTai) {
        this.tenDangNhapHienTai = tenDangNhapHienTai;

        // Đảm bảo mở kết nối cơ sở dữ liệu trước khi DAO được gọi
        damBaoKetNoiCoSoDuLieu();

        this.taiKhoanDao = new TaiKhoanDao();

        khoiTaoGiaoDien();

        // Tải dữ liệu lên giao diện
        loadThongTinCaNhan();
        loadDuLieuLenBang();
    }

    /**
     * Mở kết nối Database an toàn mà không cần chỉnh sửa TaiKhoanDao
     */
    private void damBaoKetNoiCoSoDuLieu() {
        try {
            if (ConnectDB.getConnection() == null || ConnectDB.getConnection().isClosed()) {
                ConnectDB.getInstance().connect();
            }
        } catch (Exception ex) {
            System.err.println("Cảnh báo: Không thể kết nối DB");
        }
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

        JLabel lblTieuDeCard = new JLabel("TÀI KHOẢN CÁ NHÂN");
        lblTieuDeCard.setFont(new Font("Arial", Font.BOLD, 16));
        lblTieuDeCard.setForeground(new Color(30, 35, 45));

        JLabel lblMoTa = new JLabel("Thông tin tài khoản và danh sách người dùng trong hệ thống gara.");
        lblMoTa.setFont(new Font("Arial", Font.PLAIN, 12));
        lblMoTa.setForeground(Color.GRAY);

        JPanel panelTieuDe = new JPanel(new BorderLayout(0, 8));
        panelTieuDe.setOpaque(false);
        panelTieuDe.add(lblTieuDeCard, BorderLayout.NORTH);
        panelTieuDe.add(lblMoTa, BorderLayout.CENTER);

        txtTenDangNhap = new JTextField();
        txtTenDangNhap.setEditable(false);
        txtHoTen = new JTextField();
        txtVaiTro = new JTextField();
        txtEmail = new JTextField();

        JPanel panelGridFields = new JPanel(new GridLayout(2, 2, 40, 10));
        panelGridFields.setBackground(Color.WHITE);
        panelGridFields.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        panelGridFields.add(taoOnhapLieu("Tên đăng nhập", txtTenDangNhap));
        panelGridFields.add(taoOnhapLieu("Mã nhân viên", txtHoTen));
        panelGridFields.add(taoOnhapLieu("Trạng thái", txtVaiTro));
        panelGridFields.add(taoOnhapLieu("Mã tài khoản", txtEmail));

        panelPhiaTren.add(panelTieuDe, BorderLayout.NORTH);
        panelPhiaTren.add(panelGridFields, BorderLayout.CENTER);

        // ==================== THANH TÌM KIẾM VÀ LỌC ====================
        JPanel panelSearchFilter = new JPanel(new BorderLayout(10, 0));
        panelSearchFilter.setBackground(new Color(245, 247, 250));
        panelSearchFilter.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(220, 225, 230), 1),
                        BorderFactory.createEmptyBorder(8, 12, 8, 12)
                )
        );

        txtTimKiemBang = new JTextField();
        txtTimKiemBang.setFont(new Font("Arial", Font.PLAIN, 12));
        txtTimKiemBang.setBorder(null);
        txtTimKiemBang.setOpaque(false);
        txtTimKiemBang.setToolTipText("Nhập mã tài khoản, mã nhân viên hoặc tên đăng nhập...");

        txtTimKiemBang.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                xuLyTimKiemVaLoc();
            }
        });

        cbTrangThai = new JComboBox<>(new String[] { "Tất cả", "Hoạt động", "Khóa" });
        cbTrangThai.setFont(new Font("Arial", Font.PLAIN, 12));
        cbTrangThai.setFocusable(false);
        cbTrangThai.setBackground(Color.WHITE);
        cbTrangThai.addActionListener(e -> xuLyTimKiemVaLoc());

        panelSearchFilter.add(txtTimKiemBang, BorderLayout.CENTER);
        panelSearchFilter.add(cbTrangThai, BorderLayout.EAST);

        // ==================== BẢNG TÀI KHOẢN ====================
        String[] columnNames = {
                "Mã tài khoản", "Mã nhân viên", "Tên đăng nhập", "Mật khẩu", "Trạng thái"
        };

        modelTable = new DefaultTableModel(columnNames, 0) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableQuyenHan = new JTable(modelTable);
        tableQuyenHan.setRowHeight(32);
        tableQuyenHan.setFont(new Font("Arial", Font.PLAIN, 12));
        tableQuyenHan.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tableQuyenHan.getTableHeader().setBackground(new Color(230, 235, 240));
        tableQuyenHan.getTableHeader().setReorderingAllowed(false);
        tableQuyenHan.setSelectionBackground(new Color(40, 100, 150));
        tableQuyenHan.setSelectionForeground(Color.WHITE);
        tableQuyenHan.setFillsViewportHeight(true);

        // Click chọn dòng trên bảng để đẩy dữ liệu lên các ô nhập
        tableQuyenHan.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tableQuyenHan.getSelectedRow() != -1) {
                int row = tableQuyenHan.getSelectedRow();
                txtEmail.setText(String.valueOf(modelTable.getValueAt(row, 0)));
                txtHoTen.setText(String.valueOf(modelTable.getValueAt(row, 1)));
                txtTenDangNhap.setText(String.valueOf(modelTable.getValueAt(row, 2)));
                txtVaiTro.setText(String.valueOf(modelTable.getValueAt(row, 4)));
            }
        });

        JScrollPane scrollPane = new JScrollPane(tableQuyenHan);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 225, 230)));

        JPanel panelTableSection = new JPanel(new BorderLayout(0, 10));
        panelTableSection.setBackground(Color.WHITE);
        panelTableSection.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        panelTableSection.add(panelSearchFilter, BorderLayout.NORTH);
        panelTableSection.add(scrollPane, BorderLayout.CENTER);

        // ==================== CÁC NÚT ĐIỀU HƯỚNG ====================
        JPanel panelBottomButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        panelBottomButtons.setBackground(Color.WHITE);

        JButton btnDoiMatKhau = new JButton("Đổi mật khẩu");
        btnDoiMatKhau.setFont(new Font("Arial", Font.BOLD, 13));
        btnDoiMatKhau.setBackground(Color.WHITE);
        btnDoiMatKhau.setForeground(new Color(50, 55, 65));
        btnDoiMatKhau.setContentAreaFilled(false);
        btnDoiMatKhau.setOpaque(true);
        btnDoiMatKhau.setFocusPainted(false);
        btnDoiMatKhau.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(200, 205, 210), 1),
                        BorderFactory.createEmptyBorder(8, 16, 8, 16)
                )
        );
        btnDoiMatKhau.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btnDoiMatKhau.addActionListener(e -> {
            Window cuaSo = SwingUtilities.getWindowAncestor(this);
            if (cuaSo instanceof GaraManHinhChinh) {
                GaraManHinhChinh manHinhChinh = (GaraManHinhChinh) cuaSo;
                manHinhChinh.chuyenManHinh("DOI_MAT_KHAU");
            } else {
                JOptionPane.showMessageDialog(this, "Hãy mở màn hình này từ GaraManHinhChinh.");
            }
        });

        JButton btnLuuThongTin = new JButton("Lưu thông tin");
        btnLuuThongTin.setFont(new Font("Arial", Font.BOLD, 13));
        btnLuuThongTin.setBackground(new Color(180, 30, 40));
        btnLuuThongTin.setForeground(Color.WHITE);
        btnLuuThongTin.setContentAreaFilled(false);
        btnLuuThongTin.setOpaque(true);
        btnLuuThongTin.setBorderPainted(false);
        btnLuuThongTin.setFocusPainted(false);
        btnLuuThongTin.setBorder(BorderFactory.createEmptyBorder(9, 18, 9, 18));
        btnLuuThongTin.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btnLuuThongTin.addActionListener(e -> xuLyCapNhat());

        JButton btnDangXuat = new JButton("Đăng Xuất →");
        btnDangXuat.setFont(new Font("Arial", Font.BOLD, 14));
        btnDangXuat.setForeground(new Color(180, 30, 40));
        btnDangXuat.setBackground(Color.WHITE);
        btnDangXuat.setFocusPainted(false);
        btnDangXuat.setBorderPainted(false);
        btnDangXuat.setContentAreaFilled(false);
        btnDangXuat.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btnDangXuat.addActionListener(e -> xuLyDangXuat());

        panelBottomButtons.add(btnDoiMatKhau);
        panelBottomButtons.add(btnLuuThongTin);
        panelBottomButtons.add(Box.createHorizontalStrut(10));
        panelBottomButtons.add(btnDangXuat);

        // ==================== GẮN PANEL VÀO GIAO DIỆN ====================
        panelCardChinh.add(panelPhiaTren, BorderLayout.NORTH);
        panelCardChinh.add(panelTableSection, BorderLayout.CENTER);
        panelCardChinh.add(panelBottomButtons, BorderLayout.SOUTH);

        add(panelCardChinh, BorderLayout.CENTER);
    }

    // ==================== LOAD & CẬP NHẬT DỮ LIỆU TỪ DAO ====================

    private void loadThongTinCaNhan() {
        if (tenDangNhapHienTai == null || tenDangNhapHienTai.trim().isEmpty()) {
            return;
        }

        try {
            TaiKhoan tk = taiKhoanDao.timTheoTenDangNhap(tenDangNhapHienTai);
            if (tk != null) {
                txtEmail.setText(tk.getMaTaiKhoan());
                txtHoTen.setText(tk.getMaNhanVien());
                txtTenDangNhap.setText(tk.getTenDangNhap());
                txtVaiTro.setText(tk.getTrangThai());
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải thông tin cá nhân: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void loadDuLieuLenBang() {
        try {
            ArrayList<TaiKhoan> danhSach = taiKhoanDao.layDanhSachTaiKhoan();
            hienThiLenBang(danhSach);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Không thể tải danh sách tài khoản:\n" + e.getMessage(), "Lỗi kết nối", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void hienThiLenBang(ArrayList<TaiKhoan> danhSach) {
        modelTable.setRowCount(0);
        for (TaiKhoan tk : danhSach) {
            modelTable.addRow(new Object[] {
                    tk.getMaTaiKhoan(),
                    tk.getMaNhanVien(),
                    tk.getTenDangNhap(),
                    "******",
                    tk.getTrangThai()
            });
        }
    }

    private void xuLyTimKiemVaLoc() {
        String tuKhoa = txtTimKiemBang.getText().trim();
        String trangThaiLoc = cbTrangThai.getSelectedItem().toString();

        try {
            ArrayList<TaiKhoan> danhSach;
            if (tuKhoa.isEmpty()) {
                danhSach = taiKhoanDao.layDanhSachTaiKhoan();
            } else {
                danhSach = taiKhoanDao.timKiem(tuKhoa);
            }

            ArrayList<TaiKhoan> ketQuaLoc = new ArrayList<>();
            for (TaiKhoan tk : danhSach) {
                if (trangThaiLoc.equals("Tất cả") || tk.getTrangThai().equalsIgnoreCase(trangThaiLoc)) {
                    ketQuaLoc.add(tk);
                }
            }

            hienThiLenBang(ketQuaLoc);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void xuLyCapNhat() {
        String maTK = txtEmail.getText().trim();
        String maNV = txtHoTen.getText().trim();
        String tenDN = txtTenDangNhap.getText().trim();
        String trangThai = txtVaiTro.getText().trim();

        if (maTK.isEmpty() || tenDN.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn tài khoản cần sửa từ danh sách!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            TaiKhoan tk = new TaiKhoan(maTK, maNV, tenDN, "", trangThai);
            boolean thanhCong = taiKhoanDao.suaTaiKhoan(tk);

            if (thanhCong) {
                JOptionPane.showMessageDialog(this, "Cập nhật tài khoản thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                loadDuLieuLenBang();
            } else {
                JOptionPane.showMessageDialog(this, "Cập nhật không thành công!", "Thông báo", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi cập nhật: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
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

    private JPanel taoOnhapLieu(String labelText, JTextField textField) {
        JPanel panel = new JPanel(new BorderLayout(0, 5));
        panel.setBackground(Color.WHITE);

        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("Arial", Font.PLAIN, 12));
        lbl.setForeground(new Color(80, 85, 95));

        textField.setFont(new Font("Arial", Font.PLAIN, 13));
        textField.setPreferredSize(new Dimension(250, 32));
        textField.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(200, 205, 210), 1),
                        BorderFactory.createEmptyBorder(4, 8, 4, 8)
                )
        );

        panel.add(lbl, BorderLayout.NORTH);
        panel.add(textField, BorderLayout.CENTER);

        return panel;
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName()
            );
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // Mở kết nối Database trước khi giao diện được vẽ
        try {
            ConnectDB.getInstance().connect();
        } catch (Exception e) {
            System.err.println("Không thể kết nối SQL Server trước khi chạy UI: " + e.getMessage());
        }

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Tài khoản cá nhân - NIGHT INVADER");
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setContentPane(new GaraTaiKhoanCaNhan("chugara"));
            frame.setSize(1340, 840);
            frame.setLocationRelativeTo(null);
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            frame.setVisible(true);
        });
    }
}