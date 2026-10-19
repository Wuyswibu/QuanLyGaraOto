package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class GaraTraCuuHoaDon extends JFrame {

    private JTextField txtMaHoaDon;
    private JTextField txtBienSoKhach;
    private JTextField txtTuNgay;
    private JTextField txtDenNgay;
    private JTextField txtTimKiemBang;
    private JTable tableHoaDon;

    public GaraTraCuuHoaDon() {
        setTitle("Hệ thống Quản lý Gara Ô Tô");
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

        Font menuFont = new Font("Arial", Font.BOLD, 16); // Font to giống nhập kho

        JMenu menuLogo = new JMenu("NIGHT INVADER");
        menuLogo.setFont(new Font("Arial", Font.BOLD, 15));
        menuLogo.setForeground(new Color(210, 30, 30));
        menuLogo.setMargin(new Insets(5, 10, 5, 10));
        menuBar.add(menuLogo);

        // Đã gỡ bỏ các ký tự \u25BE gây lỗi ô vuông
        String[] tenCacMenu = {
                "Hệ thống", "Khách hàng", "Xe", "Dịch vụ",
                "Phụ tùng", "Kho hàng", "Hóa đơn", "Thống kê", "Nhân viên"
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
            // Highlight menu "Hóa đơn"
            if (tenCacMenu[i].contains("Hóa đơn")) {
                menu.setForeground(new Color(210, 30, 30));
                menu.setOpaque(true);
                menu.setBackground(new Color(230, 235, 240));
            }
            menuBar.add(menu);
        }

        JMenu menuTaiKhoan = new JMenu("Xin chào, Chủ Gara");
        menuTaiKhoan.setFont(menuFont);
        menuTaiKhoan.setMargin(new Insets(5, 10, 5, 10));
        // Khôi phục icon tài khoản
        ImageIcon iconUserMenu = loadIcon("/img/icon_nguoi.jpg", 20, 20);
        if (iconUserMenu != null) {
            menuTaiKhoan.setIcon(iconUserMenu);
        }
        menuBar.add(Box.createHorizontalGlue());
        menuBar.add(menuTaiKhoan);

        setJMenuBar(menuBar);

        // ==================== 2. THANH CÔNG CỤ NHANH (QUICK TOOLBAR) ====================
        // KHÔI PHỤC LẠI TOOLBAR CÓ ICON GIỐNG NHẬP KHO
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
        
        JLabel lblKhachHang = new JLabel("Khách hàng");
        lblKhachHang.setFont(new Font("Arial", Font.BOLD, 13));
        lblKhachHang.setForeground(Color.DARK_GRAY);
        panelToolbar.add(lblKhachHang);

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


        // ==================== 3. NỘI DUNG CHÍNH: FORM TRA CỨU HÓA ĐƠN ====================
        JPanel panelCenter = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 30));
        panelCenter.setBackground(new Color(225, 230, 235));

        JPanel panelCardChinh = new JPanel(new BorderLayout());
        panelCardChinh.setBackground(Color.WHITE);
        panelCardChinh.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 215, 220), 1),
                BorderFactory.createEmptyBorder(25, 30, 25, 30)
        ));
        panelCardChinh.setPreferredSize(new Dimension(1100, 600));

        // --- 3.1. Tiêu đề card ---
        JPanel panelCardTop = new JPanel(new BorderLayout());
        panelCardTop.setBackground(Color.WHITE);

        JLabel lblTieuDeCard = new JLabel("TRA CỨU HÓA ĐƠN");
        lblTieuDeCard.setFont(new Font("Arial", Font.BOLD, 18));
        lblTieuDeCard.setForeground(new Color(30, 35, 45));

        JPanel pnlWindowControls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        pnlWindowControls.setBackground(Color.WHITE);
        JLabel lblMinimize = new JLabel("\u2014");
        JLabel lblMaximize = new JLabel("\u2610");
        pnlWindowControls.add(lblMinimize);
        pnlWindowControls.add(lblMaximize);

        panelCardTop.add(lblTieuDeCard, BorderLayout.WEST);
        panelCardTop.add(pnlWindowControls, BorderLayout.EAST);

        // --- 3.2. Form thông tin tra cứu ---
        JLabel lblMoTa = new JLabel("Tìm theo mã hóa đơn, biển số, khách hàng và trạng thái thanh toán.");
        lblMoTa.setFont(new Font("Arial", Font.PLAIN, 13));
        lblMoTa.setForeground(Color.GRAY);
        lblMoTa.setBorder(BorderFactory.createEmptyBorder(10, 0, 15, 0));

        txtMaHoaDon = new JTextField("Tất cả");
        txtBienSoKhach = new JTextField("Tất cả");
        txtTuNgay = new JTextField("01/10/2026");
        txtDenNgay = new JTextField("05/10/2026");

        JPanel p1 = taoOnhapLieu("Mã hóa đơn", txtMaHoaDon);
        JPanel p2 = taoOnhapLieu("Biển số / Khách", txtBienSoKhach);
        JPanel p3 = taoOnhapLieu("Từ ngày", txtTuNgay);
        JPanel p4 = taoOnhapLieu("Đến ngày", txtDenNgay); 

        JPanel panelFieldsContainer = new JPanel(new BorderLayout());
        panelFieldsContainer.setBackground(Color.WHITE);
        panelFieldsContainer.add(lblMoTa, BorderLayout.NORTH);

        JPanel panelGridFields = new JPanel(new GridLayout(2, 2, 40, 10)); // Đổi thành 2 hàng 2 cột
        panelGridFields.setBackground(Color.WHITE);
        panelGridFields.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        panelGridFields.add(p1);
        panelGridFields.add(p2);
        panelGridFields.add(p3);
        panelGridFields.add(p4);

        panelFieldsContainer.add(panelGridFields, BorderLayout.CENTER);

        // --- 3.3. Thanh tìm kiếm và bộ lọc ---
        JPanel panelSearchFilter = new JPanel(new BorderLayout());
        panelSearchFilter.setBackground(new Color(240, 243, 246));
        panelSearchFilter.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        txtTimKiemBang = new JTextField("Tìm kiếm theo mã, tên hoặc biển số...");
        txtTimKiemBang.setFont(new Font("Arial", Font.PLAIN, 12));
        txtTimKiemBang.setForeground(Color.GRAY);
        txtTimKiemBang.setBorder(null);
        txtTimKiemBang.setOpaque(false);

        JLabel lblTrangThaiLoc = new JLabel("Trạng thái: Tất cả");
        lblTrangThaiLoc.setFont(new Font("Arial", Font.PLAIN, 12));

        panelSearchFilter.add(txtTimKiemBang, BorderLayout.CENTER);
        panelSearchFilter.add(lblTrangThaiLoc, BorderLayout.EAST);

        // --- 3.4. Bảng kết quả tra cứu hóa đơn ---
        String[] columnNames = {"Hóa đơn", "Khách hàng", "Biển số", "Tổng tiền", "Đã thu", "Còn nợ"};
        Object[][] data = {
                {"HD001", "Nguyễn Minh An", "51H-123.45", "2.484.000 đ", "500.000 đ", "1.984.000 đ"},
                {"HD002", "Vũ Thu Hà", "51K-246.80", "950.000 đ", "950.000 đ", "0 đ"}
        };

        DefaultTableModel model = new DefaultTableModel(data, columnNames);
        tableHoaDon = new JTable(model);
        tableHoaDon.setRowHeight(35);
        tableHoaDon.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tableHoaDon.getTableHeader().setBackground(new Color(240, 243, 246));
        tableHoaDon.setFont(new Font("Arial", Font.PLAIN, 12));
        tableHoaDon.setSelectionBackground(new Color(40, 100, 150));
        tableHoaDon.setSelectionForeground(Color.WHITE);
        
        // Cấu hình viền lưới bảng
        tableHoaDon.setShowGrid(false);
        tableHoaDon.setIntercellSpacing(new Dimension(0, 0));

        // Chọn sẵn dòng đầu tiên
        tableHoaDon.setRowSelectionInterval(0, 0);

        JScrollPane scrollPane = new JScrollPane(tableHoaDon);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setPreferredSize(new Dimension(1000, 150));

        JPanel panelTableSection = new JPanel(new BorderLayout());
        panelTableSection.setBackground(Color.WHITE);
        panelTableSection.add(panelSearchFilter, BorderLayout.NORTH);
        panelTableSection.add(scrollPane, BorderLayout.CENTER);

        // --- 3.5. Khu vực nút bấm dưới cùng ---
        JPanel panelBottomButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 15));
        panelBottomButtons.setBackground(Color.WHITE);

        JButton btnTraCuu = taoNutTrang("Tra cứu");
        JButton btnXemHoaDon = taoNutTrang("Xem hóa đơn");
        JButton btnInHoaDon = taoNutDo("In hóa đơn");

        panelBottomButtons.add(btnTraCuu);
        panelBottomButtons.add(btnXemHoaDon);
        panelBottomButtons.add(btnInHoaDon);

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

        JLabel lblTrangThaiFooter = new JLabel("● Đang hoạt động | Kho chính | Chủ Gara");
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

    // --- Các hàm tiện ích ---
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

    // Hàm tạo nút nhanh trên toolbar với Icon
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
    
    // Hàm Load Icon an toàn
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
        SwingUtilities.invokeLater(() -> new GaraTraCuuHoaDon().setVisible(true));
    }
}