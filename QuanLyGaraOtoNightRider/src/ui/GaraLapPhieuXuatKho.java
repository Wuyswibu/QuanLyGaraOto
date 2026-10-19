package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class GaraLapPhieuXuatKho extends JFrame {

    private JTextField txtMaPhieuXuat;
    private JTextField txtPhieuSuaChua;
    private JTextField txtBienSo;
    private JTextField txtNguoiNhan;
    private JTextField txtNguoiXuat;
    private JTextField txtTrangThaiPhieu;
    private JTextField txtTimKiemBang;
    private JTable tablePhuTung;

    public GaraLapPhieuXuatKho() {
        setTitle("Hệ thống Quản lý Gara Ô Tô");
        setSize(1340, 840);
        setLocationRelativeTo(null);
        setResizable(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        khoiTaoGiaoDien();
    }

    private void khoiTaoGiaoDien() {
        setLayout(new BorderLayout());

        // ==================== 1. THANH MENU BAR (PHÍA TRÊN CÙNG) ====================
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(new Color(245, 247, 250));

        // Tăng chiều cao tổng thể và khoảng đệm cho thanh Menu Bar
        menuBar.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        // Font chữ to rõ cho menu
        Font menuFont = new Font("Arial", Font.BOLD, 16);

        JMenu menuLogo = new JMenu("NIGHT INVADER");
        menuLogo.setFont(new Font("Arial", Font.BOLD, 15));
        menuLogo.setForeground(new Color(210, 30, 30));
        menuLogo.setMargin(new Insets(5, 10, 5, 10)); // Tạo khoảng trống xung quanh chữ
        menuBar.add(menuLogo);

        // Mảng tên các menu chức năng
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

        // Vòng lặp thêm các menu vào thanh bar và set font to, margin rộng rãi
        for (int i = 0; i < tenCacMenu.length; i++) {
            JMenu menu = taoMenuDropdown(tenCacMenu[i], cacItemMenu[i]);
            menu.setFont(menuFont);
            menu.setMargin(new Insets(5, 10, 5, 10)); // Làm cho từng mục menu bự và thoáng hơn
            menuBar.add(menu);
        }

        // Menu tài khoản người dùng ở góc phải thanh menu
        JMenu menuTaiKhoan = new JMenu("Xin chào, Chủ Gara");
        menuTaiKhoan.setFont(menuFont);
        menuTaiKhoan.setMargin(new Insets(5, 10, 5, 10));
        ImageIcon iconUserMenu = loadIcon("/img/icon_nguoi.jpg", 20, 20);
        if (iconUserMenu != null) {
            menuTaiKhoan.setIcon(iconUserMenu);
        }
        menuBar.add(Box.createHorizontalGlue()); // Đẩy phần tài khoản sang góc phải nhất
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

        // Thông tin ngày tháng & chi nhánh phía bên phải toolbar
        JPanel panelThongTinPhu = new JPanel(new GridLayout(2, 1));
        panelThongTinPhu.setBackground(Color.WHITE);
        JLabel lblNgay = new JLabel("Thứ Ba, 06/10/2026", JLabel.RIGHT);
        lblNgay.setFont(new Font("Arial", Font.PLAIN, 12));
        JLabel lblChiNhanh = new JLabel("Chi nhánh: Gara chính", JLabel.RIGHT);
        lblChiNhanh.setFont(new Font("Arial", Font.PLAIN, 11));
        lblChiNhanh.setForeground(Color.GRAY);
        panelThongTinPhu.add(lblNgay);
        panelThongTinPhu.add(lblChiNhanh);

        JPanel panelTopContainer = new JPanel(new BorderLayout());
        panelTopContainer.add(panelToolbar, BorderLayout.CENTER);
        panelTopContainer.add(panelThongTinPhu, BorderLayout.EAST);
        panelTopContainer.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 15));


        // ==================== 3. NỘI DUNG CHÍNH: FORM LẬP PHIẾU XUẤT KHO ====================
        // Sử dụng FlowLayout để dễ dàng kiểm soát kích thước của panelCardChinh hơn GridBagLayout
        JPanel panelCenter = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 30));
        panelCenter.setBackground(new Color(225, 230, 235));

        JPanel panelCardChinh = new JPanel(new BorderLayout());
        panelCardChinh.setBackground(Color.WHITE);
        panelCardChinh.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 215, 220), 1),
                BorderFactory.createEmptyBorder(25, 30, 25, 30)
        ));

        // Tăng chiều rộng để nội dung không bị ép lại
        panelCardChinh.setPreferredSize(new Dimension(1100, 600));

        // --- 3.1. Tiêu đề card ---
        JPanel panelCardTop = new JPanel(new BorderLayout());
        panelCardTop.setBackground(Color.WHITE);

        JLabel lblTieuDeCard = new JLabel("LẬP PHIẾU XUẤT KHO");
        lblTieuDeCard.setFont(new Font("Arial", Font.BOLD, 18));
        lblTieuDeCard.setForeground(new Color(30, 35, 45));

        // Thêm nút thu nhỏ/phóng to giả lập
        JPanel pnlWindowControls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        pnlWindowControls.setBackground(Color.WHITE);
        JLabel lblMinimize = new JLabel("\u2014"); // Dấu gạch ngang
        JLabel lblMaximize = new JLabel("\u2610"); // Dấu ô vuông
        pnlWindowControls.add(lblMinimize);
        pnlWindowControls.add(lblMaximize);

        panelCardTop.add(lblTieuDeCard, BorderLayout.WEST);
        panelCardTop.add(pnlWindowControls, BorderLayout.EAST);

        // --- 3.2. Form thông tin xuất kho ---
        JLabel lblMoTa = new JLabel("Xuất theo phiếu sửa chữa; không xuất vượt tồn khả dụng.");
        lblMoTa.setFont(new Font("Arial", Font.PLAIN, 13));
        lblMoTa.setForeground(Color.GRAY);
        lblMoTa.setBorder(BorderFactory.createEmptyBorder(10, 0, 15, 0));

        txtMaPhieuXuat = new JTextField("PX001");
        txtPhieuSuaChua = new JTextField("SC001");
        txtBienSo = new JTextField("51H-123.45");
        txtNguoiNhan = new JTextField("Trần Quốc Huy");
        txtNguoiXuat = new JTextField("Lê Thị Lan");
        txtTrangThaiPhieu = new JTextField("Chờ xuất kho");

        JPanel p1 = taoOnhapLieu("Mã phiếu xuất", txtMaPhieuXuat);
        JPanel p2 = taoOnhapLieu("Phiếu sửa chữa", txtPhieuSuaChua);
        JPanel p3 = taoOnhapLieu("Biển số", txtBienSo);
        JPanel p4 = taoOnhapLieu("Người nhận", txtNguoiNhan);
        JPanel p5 = taoOnhapLieu("Người xuất", txtNguoiXuat);
        JPanel p6 = taoOnhapLieu("Trạng thái", txtTrangThaiPhieu);

        JPanel panelFieldsContainer = new JPanel(new BorderLayout());
        panelFieldsContainer.setBackground(Color.WHITE);
        panelFieldsContainer.add(lblMoTa, BorderLayout.NORTH);

        JPanel panelGridFields = new JPanel(new GridLayout(3, 2, 40, 10)); // 3 hàng, 2 cột
        panelGridFields.setBackground(Color.WHITE);
        panelGridFields.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        panelGridFields.add(p1);
        panelGridFields.add(p2);
        panelGridFields.add(p3);
        panelGridFields.add(p4);
        panelGridFields.add(p5);
        panelGridFields.add(p6);

        panelFieldsContainer.add(panelGridFields, BorderLayout.CENTER);

        // --- 3.3. Thanh tìm kiếm và bộ lọc ---
        JPanel panelSearchFilter = new JPanel(new BorderLayout());
        panelSearchFilter.setBackground(new Color(240, 243, 246)); // Màu nền xám cho thanh tìm kiếm
        panelSearchFilter.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        txtTimKiemBang = new JTextField("Tìm kiếm theo mã, tên hoặc biển số...");
        txtTimKiemBang.setFont(new Font("Arial", Font.PLAIN, 12));
        txtTimKiemBang.setForeground(Color.GRAY);
        txtTimKiemBang.setBorder(null);
        txtTimKiemBang.setOpaque(false);

        JLabel lblTrangThaiLoc = new JLabel("Trạng thái: Tất cả \u25BE");
        lblTrangThaiLoc.setFont(new Font("Arial", Font.PLAIN, 12));

        panelSearchFilter.add(txtTimKiemBang, BorderLayout.CENTER);
        panelSearchFilter.add(lblTrangThaiLoc, BorderLayout.EAST);

        // --- 3.4. Bảng chi tiết phụ tùng xuất ---
        String[] columnNames = {"Mã PT", "Tên phụ tùng", "Tồn khả dụng", "SL xuất", "Còn lại"};
        Object[][] data = {
                {"PT001", "Dầu động cơ 5W-30", "36", "4", "32"},
                {"PT002", "Lọc dầu Toyota", "12", "1", "11"},
                {"PT003", "Má phanh trước", "3", "1", "2"}
        };

        DefaultTableModel model = new DefaultTableModel(data, columnNames);
        tablePhuTung = new JTable(model);
        tablePhuTung.setRowHeight(35);
        tablePhuTung.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tablePhuTung.getTableHeader().setBackground(new Color(240, 243, 246));
        tablePhuTung.setFont(new Font("Arial", Font.PLAIN, 12));
        tablePhuTung.setSelectionBackground(new Color(40, 100, 150)); // Dòng đầu tiên được chọn
        tablePhuTung.setSelectionForeground(Color.WHITE);
        tablePhuTung.setShowGrid(false); // Bỏ viền lưới theo thiết kế

        // Chọn sẵn dòng đầu tiên để khớp ảnh
        tablePhuTung.setRowSelectionInterval(0, 0);

        JScrollPane scrollPane = new JScrollPane(tablePhuTung);
        scrollPane.setBorder(BorderFactory.createEmptyBorder()); // Bỏ viền cuộn

        // Đặt kích thước ưu tiên cho JScrollPane để bảng luôn được hiển thị
        scrollPane.setPreferredSize(new Dimension(1000, 150));

        JPanel panelTableSection = new JPanel(new BorderLayout());
        panelTableSection.setBackground(Color.WHITE);
        panelTableSection.add(panelSearchFilter, BorderLayout.NORTH);
        panelTableSection.add(scrollPane, BorderLayout.CENTER);

        // --- 3.5. Khu vực nút bấm dưới cùng ---
        JPanel panelBottomButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 15));
        panelBottomButtons.setBackground(Color.WHITE);

        JButton btnLuuNhap = taoNutTrang("Lưu nháp");
        JButton btnXacNhan = taoNutDo("Xác nhận xuất kho");

        panelBottomButtons.add(btnLuuNhap);
        panelBottomButtons.add(btnXacNhan);

        // --- Gắn các thành phần vào Card Chính ---
        panelCardChinh.add(panelCardTop, BorderLayout.NORTH);

        JPanel panelCardBody = new JPanel(new BorderLayout(0, 10));
        panelCardBody.setBackground(Color.WHITE);
        panelCardBody.add(panelFieldsContainer, BorderLayout.NORTH);
        panelCardBody.add(panelTableSection, BorderLayout.CENTER);
        panelCardBody.add(panelBottomButtons, BorderLayout.SOUTH);

        panelCardChinh.add(panelCardBody, BorderLayout.CENTER);

        panelCenter.add(panelCardChinh);

        // ==================== 4. THANH TRẠNG THÁI (FOOTER) ====================
        JPanel panelFooter = new JPanel(new BorderLayout());
        panelFooter.setBackground(new Color(245, 247, 250));
        panelFooter.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));

        JLabel lblTrangThaiFooter = new JLabel("Đang hoạt động | Kho chính | Chủ Gara");
        lblTrangThaiFooter.setFont(new Font("Arial", Font.PLAIN, 12));
        lblTrangThaiFooter.setForeground(Color.DARK_GRAY);

        JLabel lblBanQuyen = new JLabel("NIGHT INVADER • 05/10/2026");
        lblBanQuyen.setFont(new Font("Arial", Font.PLAIN, 12));
        lblBanQuyen.setForeground(Color.GRAY);

        panelFooter.add(lblTrangThaiFooter, BorderLayout.WEST);
        panelFooter.add(lblBanQuyen, BorderLayout.EAST);

        // Gắn vào Frame
        add(panelTopContainer, BorderLayout.NORTH);
        add(panelCenter, BorderLayout.CENTER);
        add(panelFooter, BorderLayout.SOUTH);
    }

    // --- Các hàm tiện ích (Helper Methods) ---

    private JPanel taoOnhapLieu(String labelText, JTextField textField) {
        JPanel panel = new JPanel(new BorderLayout(0, 5));
        panel.setBackground(Color.WHITE);

        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("Arial", Font.PLAIN, 12));
        lbl.setForeground(new Color(80, 85, 95));

        textField.setFont(new Font("Arial", Font.PLAIN, 13));
        textField.setPreferredSize(new Dimension(0, 32));
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 215, 220), 1),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));

        panel.add(lbl, BorderLayout.NORTH);
        panel.add(textField, BorderLayout.CENTER);
        return panel;
    }

    private JButton taoNutTrang(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 12));
        btn.setBackground(Color.WHITE);
        btn.setForeground(new Color(50, 55, 65));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 205, 210), 1),
                BorderFactory.createEmptyBorder(8, 16, 8, 16)
        ));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton taoNutDo(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 12));
        btn.setBackground(new Color(180, 30, 40));
        btn.setForeground(Color.WHITE);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(9, 18, 9, 18));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JMenu taoMenuDropdown(String tenMenu, String[] cacItem) {
        JMenu menu = new JMenu(tenMenu);
        for (String itemName : cacItem) {
            if (itemName.equals("-")) {
                menu.addSeparator();
            } else {
                JMenuItem item = new JMenuItem(itemName);
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

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> new GaraLapPhieuXuatKho().setVisible(true));
    }
}