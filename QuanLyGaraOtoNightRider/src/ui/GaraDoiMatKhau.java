package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GaraDoiMatKhau extends JFrame {

    private JPasswordField txtMatKhauHienTai;
    private JPasswordField txtMatKhauMoi;
    private JPasswordField txtXacNhanMatKhau;
    private JTextField txtTimKiemBang;
    private JTable tableDieuKien;

    public GaraDoiMatKhau() {
        setTitle("Hệ thống Quản lý Gara Ô Tô - Night Invader");
        setSize(1340, 840);
        setLocationRelativeTo(null);
        setResizable(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        khoiTaoGiaoDien();
    }

    private void khoiTaoGiaoDien() {
        setLayout(new BorderLayout());

        // ==================== 1. THANH MENU BAR ====================
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(new Color(245, 247, 250));
        menuBar.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        Font menuFont = new Font("Arial", Font.BOLD, 16);

        JMenu menuLogo = new JMenu("NIGHT INVADER");
        menuLogo.setFont(new Font("Arial", Font.BOLD, 15));
        menuLogo.setForeground(new Color(210, 30, 30));
        menuLogo.setMargin(new Insets(5, 10, 5, 10));
        menuBar.add(menuLogo);

        String[] tenCacMenu = {
            "Hệ thống", "Khách hàng ", "Xe ", "Dịch vụ ", 
            "Phụ tùng", "Kho hàng ", "Hóa đơn ", "Thống kê ", "Nhân viên "
        };
        
        String[][] cacItemMenu = {
            {"Tài khoản cá nhân", "Đổi mật khẩu", "-", "Đăng xuất"},
            {"Quản lý khách hàng"},
            {"Quản lý xe", "Tra cứu xe", "Lịch sử sửa chữa"},
            {"Quản lý dịch vụ", "Thêm dịch vụ", "Sửa dịch vụ", "Xóa dịch vụ", "-", "Lập phiếu tiếp nhận xe", "Lập phiếu sửa chữa / Báo giá", "Cập nhật tình trạng sửa xe"},
            {"Quản lý phụ tùng"},
            {"Lập phiếu nhập kho", "Lập phiếu xuất kho"},
            {"Lập hóa đơn", "Tra cứu hóa đơn"},
            {"Thống kê doanh thu", "Thống kê tồn kho", "Phụ tùng bán chạy", "Phụ tùng bán chậm"},
            {"Quản lý nhân sự", "Phân công kỹ thuật viên"}
        };

        for (int i = 0; i < tenCacMenu.length; i++) {
            JMenu menu = taoMenuDropdown(tenCacMenu[i], cacItemMenu[i]);
            menu.setFont(menuFont);
            menu.setMargin(new Insets(5, 10, 5, 10));
            menuBar.add(menu);
        }

        JMenu menuTaiKhoan = new JMenu("Xin chào, Chủ Gara");
        menuTaiKhoan.setFont(menuFont);
        menuTaiKhoan.setMargin(new Insets(5, 10, 5, 10));
        ImageIcon iconUserMenu = loadIcon("/img/icon_nguoi.jpg", 20, 20);
        if (iconUserMenu != null) {
            menuTaiKhoan.setIcon(iconUserMenu);
        }
        menuBar.add(Box.createHorizontalGlue());
        menuBar.add(menuTaiKhoan);

        setJMenuBar(menuBar);

        // ==================== 2. THANH CÔNG CỤ NHANH (QUICK TOOLBAR) ====================
        JPanel panelToolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        panelToolbar.setBackground(Color.WHITE);
        panelToolbar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 224, 230)));

        ImageIcon iconXeTB = loadIcon("/img/icon_xe.png", 22, 22);
        ImageIcon iconHoaDonTB = loadIcon("/img/icon_hoadon.png", 22, 22);
        ImageIcon iconNhapKhoTB = loadIcon("/img/icon_nhapkho.jpg", 22, 22);
        ImageIcon iconVectorTB = loadIcon("/img/icon-vector.png", 22, 22);

        panelToolbar.add(taoNutNhanh("Tiếp nhận xe", "F2", iconXeTB));
        panelToolbar.add(taoNutNhanh("Tình trạng sửa xe", "F5", iconVectorTB));
        panelToolbar.add(taoNutNhanh("Lập hóa đơn", "F3", iconHoaDonTB));
        panelToolbar.add(taoNutNhanh("Nhập kho", "F4", iconNhapKhoTB));

        JLabel lblModuleHienTai = new JLabel("Đổi mật khẩu");
        lblModuleHienTai.setFont(new Font("Arial", Font.BOLD, 13));
        lblModuleHienTai.setForeground(new Color(150, 30, 40));

        JPanel panelLabelTieuDe = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 12));
        panelLabelTieuDe.setBackground(Color.WHITE);
        panelLabelTieuDe.add(lblModuleHienTai);

        JPanel panelToolbarWrapper = new JPanel(new BorderLayout());
        panelToolbarWrapper.setBackground(Color.WHITE);
        panelToolbarWrapper.add(panelToolbar, BorderLayout.CENTER);
        panelToolbarWrapper.add(panelLabelTieuDe, BorderLayout.EAST);

        // ==================== 3. NỘI DUNG CHÍNH: FORM ĐỔI MẬT KHẨU ====================
        JPanel panelCenter = new JPanel();
        panelCenter.setBackground(new Color(240, 243, 246));
        panelCenter.setLayout(new GridBagLayout());

        JPanel panelCardChinh = new JPanel(new BorderLayout());
        panelCardChinh.setBackground(Color.WHITE);
        panelCardChinh.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 215, 220), 1),
                BorderFactory.createEmptyBorder(25, 30, 25, 30)
        ));
        panelCardChinh.setPreferredSize(new Dimension(1220, 660));

        // --- 3.1. Tiêu đề card ---
        JPanel panelCardTop = new JPanel(new BorderLayout());
        panelCardTop.setBackground(Color.WHITE);
        
        JLabel lblTieuDeCard = new JLabel("ĐỔI MẬT KHẨU");
        lblTieuDeCard.setFont(new Font("Arial", Font.BOLD, 16));
        lblTieuDeCard.setForeground(new Color(30, 35, 45));

        panelCardTop.add(lblTieuDeCard, BorderLayout.WEST);

        // --- 3.2. Form thông tin các trường mật khẩu ---
        JLabel lblMoTa = new JLabel("Nhập mật khẩu hiện tại và xác nhận mật khẩu mới.");
        lblMoTa.setFont(new Font("Arial", Font.PLAIN, 12));
        lblMoTa.setForeground(Color.GRAY);

        JTextField txtTaiKhoan = new JTextField("chugara");
        txtTaiKhoan.setEditable(false);
        txtMatKhauHienTai = new JPasswordField("");
        txtMatKhauMoi = new JPasswordField("");
        txtXacNhanMatKhau = new JPasswordField("");

        JPanel p1 = taoOnhapLieuText("Tài khoản", txtTaiKhoan);
        JPanel p2 = taoOnhapLieuPassword("Mật khẩu hiện tại", txtMatKhauHienTai);
        JPanel p3 = taoOnhapLieuPassword("Mật khẩu mới", txtMatKhauMoi);
        JPanel p4 = taoOnhapLieuPassword("Xác nhận mật khẩu", txtXacNhanMatKhau);

        JPanel panelFieldsContainer = new JPanel(new BorderLayout());
        panelFieldsContainer.setBackground(Color.WHITE);
        panelFieldsContainer.add(lblMoTa, BorderLayout.NORTH);
        
        JPanel panelGridFields = new JPanel(new GridLayout(2, 2, 40, 10));
        panelGridFields.setBackground(Color.WHITE);
        panelGridFields.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        panelGridFields.add(p1);
        panelGridFields.add(p2);
        panelGridFields.add(p3);
        panelGridFields.add(p4);
        panelFieldsContainer.add(panelGridFields, BorderLayout.CENTER);

        // --- 3.3. Thanh tìm kiếm và bộ lọc trạng thái ---
        JPanel panelSearchFilter = new JPanel(new BorderLayout());
        panelSearchFilter.setBackground(new Color(245, 247, 250));
        panelSearchFilter.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 225, 230), 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));

        txtTimKiemBang = new JTextField("Tìm kiếm theo mã, tên hoặc biển số...");
        txtTimKiemBang.setFont(new Font("Arial", Font.ITALIC, 12));
        txtTimKiemBang.setForeground(Color.GRAY);
        txtTimKiemBang.setBorder(null);
        txtTimKiemBang.setOpaque(false);

        String[] trangThaiOptions = {"Tất cả", "Hoạt động", "Khóa"};
        JComboBox<String> cbTrangThai = new JComboBox<>(trangThaiOptions);
        cbTrangThai.setFont(new Font("Arial", Font.PLAIN, 12));
        cbTrangThai.setFocusable(false);

        panelSearchFilter.add(txtTimKiemBang, BorderLayout.CENTER);
        panelSearchFilter.add(cbTrangThai, BorderLayout.EAST);

        // --- 3.4. Bảng điều kiện mật khẩu ---
        String[] columnNames = {"Điều kiện", "Yêu cầu"};
        Object[][] data = {
            {"Độ dài", "Tối thiểu 8 ký tự"},
            {"Xác nhận", "Hai mật khẩu mới phải trùng nhau"}
        };

        DefaultTableModel model = new DefaultTableModel(data, columnNames);
        tableDieuKien = new JTable(model);
        tableDieuKien.setRowHeight(32);
        tableDieuKien.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tableDieuKien.getTableHeader().setBackground(new Color(230, 235, 240));
        tableDieuKien.setFont(new Font("Arial", Font.PLAIN, 12));
        tableDieuKien.setSelectionBackground(new Color(40, 100, 150));
        tableDieuKien.setSelectionForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(tableDieuKien);
        scrollPane.setPreferredSize(new Dimension(1160, 130));

        JPanel panelTableSection = new JPanel(new BorderLayout(0, 10));
        panelTableSection.setBackground(Color.WHITE);
        panelTableSection.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        panelTableSection.add(scrollPane, BorderLayout.CENTER);

        // --- 3.5. Khu vực nút bấm dưới cùng: Hủy, Xác nhận đổi mật khẩu & Đăng xuất ---
        JPanel panelBottomButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        panelBottomButtons.setBackground(Color.WHITE);

        JButton btnHuy = new JButton("Hủy");
        btnHuy.setFont(new Font("Arial", Font.BOLD, 13));
        btnHuy.setBackground(Color.WHITE);
        btnHuy.setForeground(new Color(50, 55, 65));
        btnHuy.setFocusPainted(false);
        btnHuy.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 205, 210), 1),
                BorderFactory.createEmptyBorder(8, 20, 8, 20)
        ));
        btnHuy.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnHuy.addActionListener(e -> {
            new GaraTaiKhoanCaNhan().setVisible(true);
            this.dispose();
        });

        JButton btnXacNhan = new JButton("Xác nhận đổi mật khẩu");
        btnXacNhan.setFont(new Font("Arial", Font.BOLD, 13));
        btnXacNhan.setBackground(new Color(180, 30, 40));
        btnXacNhan.setForeground(Color.WHITE);
        btnXacNhan.setOpaque(true);
        btnXacNhan.setBorderPainted(false);
        btnXacNhan.setFocusPainted(false);
        btnXacNhan.setBorder(BorderFactory.createEmptyBorder(9, 18, 9, 18));
        btnXacNhan.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnXacNhan.addActionListener(e -> {
            String mkHienTai = new String(txtMatKhauHienTai.getPassword());
            String mkMoi = new String(txtMatKhauMoi.getPassword());
            String xacNhan = new String(txtXacNhanMatKhau.getPassword());

            if (mkHienTai.isEmpty() || mkMoi.isEmpty() || xacNhan.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            } else if (mkMoi.length() < 8) {
                JOptionPane.showMessageDialog(this, "Mật khẩu mới phải có tối thiểu 8 ký tự!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            } else if (!mkMoi.equals(xacNhan)) {
                JOptionPane.showMessageDialog(this, "Hai mật khẩu mới không trùng nhau!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Đổi mật khẩu thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                new GaraTaiKhoanCaNhan().setVisible(true);
                this.dispose();
            }
        });

        // Nút Đăng Xuất dạng chữ đỏ có mũi tên khớp mẫu hình ảnh
        JButton btnDangXuat = new JButton("Đăng Xuất →");
        btnDangXuat.setFont(new Font("Arial", Font.BOLD, 14));
        btnDangXuat.setForeground(new Color(180, 30, 40));
        btnDangXuat.setBackground(Color.WHITE);
        btnDangXuat.setFocusPainted(false);
        btnDangXuat.setBorderPainted(false);
        btnDangXuat.setContentAreaFilled(false);
        btnDangXuat.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btnDangXuat.addActionListener(e -> xuLyDangXuat());

        panelBottomButtons.add(btnHuy);
        panelBottomButtons.add(btnXacNhan);
        panelBottomButtons.add(Box.createHorizontalStrut(10));
        panelBottomButtons.add(btnDangXuat);

        panelCardChinh.add(panelCardTop, BorderLayout.NORTH);
        
        JPanel panelCardBody = new JPanel(new BorderLayout());
        panelCardBody.setBackground(Color.WHITE);
        panelCardBody.add(panelFieldsContainer, BorderLayout.NORTH);
        panelCardBody.add(panelTableSection, BorderLayout.CENTER);
        panelCardBody.add(panelBottomButtons, BorderLayout.SOUTH);
        
        panelCardChinh.add(panelCardBody, BorderLayout.CENTER);

        panelCenter.add(panelCardChinh, new GridBagConstraints());

        // ==================== 4. THANH TRẠNG THÁI (FOOTER) ====================
        JPanel panelFooter = new JPanel(new BorderLayout());
        panelFooter.setBackground(new Color(245, 247, 250));
        panelFooter.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        
        JLabel lblTrangThai = new JLabel("Đang hoạt động | Kho chính | Chủ Gara");
        lblTrangThai.setFont(new Font("Arial", Font.PLAIN, 12));
        lblTrangThai.setForeground(Color.DARK_GRAY);
        
        JLabel lblBanQuyen = new JLabel("NIGHT INVADER • 05/10/2026");
        lblBanQuyen.setFont(new Font("Arial", Font.PLAIN, 12));
        lblBanQuyen.setForeground(Color.GRAY);

        panelFooter.add(lblTrangThai, BorderLayout.WEST);
        panelFooter.add(lblBanQuyen, BorderLayout.EAST);

        add(panelToolbarWrapper, BorderLayout.NORTH);
        add(panelCenter, BorderLayout.CENTER);
        add(panelFooter, BorderLayout.SOUTH);
    }

    private void xuLyDangXuat() {
        int luaChon = JOptionPane.showConfirmDialog(
            this,
            "Bạn có chắc chắn muốn đăng xuất khỏi hệ thống?",
            "Xác nhận đăng xuất",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );

        if (luaChon == JOptionPane.YES_OPTION) {
            this.dispose();
            try {
                new GaraDangNhap().setVisible(true);
            } catch (Exception e) {
                System.exit(0);
            }
        }
    }

    private JPanel taoOnhapLieuText(String labelText, JTextField textField) {
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

    private JPanel taoOnhapLieuPassword(String labelText, JPasswordField passwordField) {
        JPanel panel = new JPanel(new BorderLayout(0, 5));
        panel.setBackground(Color.WHITE);

        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("Arial", Font.PLAIN, 12));
        lbl.setForeground(new Color(80, 85, 95));

        passwordField.setFont(new Font("Arial", Font.PLAIN, 13));
        passwordField.setPreferredSize(new Dimension(250, 32));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 205, 210), 1),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));

        panel.add(lbl, BorderLayout.NORTH);
        panel.add(passwordField, BorderLayout.CENTER);
        return panel;
    }

    private ImageIcon loadIcon(String path, int width, int height) {
        try {
            java.net.URL imgURL = getClass().getResource(path);
            if (imgURL != null) {
                ImageIcon icon = new ImageIcon(imgURL);
                Image scaledImg = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
                return new ImageIcon(scaledImg);
            }
        } catch (Exception e) {
            System.out.println("Không thể tải ảnh từ đường dẫn: " + path);
        }
        return null;
    }

    private JMenu taoMenuDropdown(String tenMenu, String[] cacItem) {
        JMenu menu = new JMenu(tenMenu);
        for (String itemName : cacItem) {
            if (itemName.equals("-")) {
                menu.addSeparator();
            } else {
                JMenuItem item = new JMenuItem(itemName);
                item.addActionListener(e -> {
                    if (itemName.equals("Thống kê doanh thu") ) {
                        new GaraThongKe(0).setVisible(true);
                        this.dispose(); // Đóng màn hình chính
                    } else if (itemName.equals("Thống kê tồn kho")) {
                        new GaraThongKe(1).setVisible(true);
                        this.dispose();
                    } else if (itemName.equals("Phụ tùng bán chạy")) {
                        new GaraThongKe(2).setVisible(true);
                        this.dispose();
                    } else if (itemName.equals("Phụ tùng bán chậm")) {
                        new GaraThongKe(3).setVisible(true);
                        this.dispose();
                    } else if (itemName.equals("Quản lý nhân sự") || itemName.equals("Phân công kỹ thuật viên")) {
                        new GaraNhanVien().setVisible(true);
                        this.dispose();
                    } else if (itemName.equals("Tài khoản cá nhân")) {
                        new GaraTaiKhoanCaNhan().setVisible(true);
                        this.dispose();
                    } else if (itemName.equals("Đổi mật khẩu")) {
                        // Đang ở màn hình này rồi
                    } else {
                        JOptionPane.showMessageDialog(this, "Đang mở chức năng: " + itemName, "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    }
                });
                menu.add(item);
            }
        }
        return menu;
    }

    private JButton taoNutNhanh(String tieuDe, String phimTat, ImageIcon icon) {
        JButton btn = new JButton();
        btn.setLayout(new BorderLayout(10, 0));
        
        if (icon != null) {
            JLabel lblIcon = new JLabel(icon);
            btn.add(lblIcon, BorderLayout.WEST);
        }
        
        JPanel pText = new JPanel(new GridLayout(2, 1));
        pText.setOpaque(false);
        
        JLabel lblTieuDe = new JLabel(tieuDe);
        lblTieuDe.setFont(new Font("Arial", Font.BOLD, 12));
        
        JLabel lblPhimTat = new JLabel(phimTat);
        lblPhimTat.setFont(new Font("Arial", Font.PLAIN, 11));
        lblPhimTat.setForeground(Color.GRAY);
        
        pText.add(lblTieuDe);
        pText.add(lblPhimTat);
        
        btn.add(pText, BorderLayout.CENTER);
        
        btn.setBackground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 205, 210), 1),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        
        btn.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Đang mở nhanh chức năng: " + tieuDe + " (" + phimTat + ")",
                "Phím tắt kích hoạt", JOptionPane.INFORMATION_MESSAGE));
        return btn;
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> new GaraDoiMatKhau().setVisible(true));
    }
}