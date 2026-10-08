package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GaraManHinhChinh extends JFrame {

    public GaraManHinhChinh() {
        // Thiết lập tiêu đề ứng dụng
        setTitle("Hệ thống Quản lý Gara Ô Tô - Night Invader");
        
        setSize(1340, 840);
        
        // Căn giữa màn hình
        setLocationRelativeTo(null);
        
        // Cho phép thay đổi kích thước cửa sổ (resizable = true)
        setResizable(true);
        
        // Thoát ứng dụng khi đóng cửa sổ
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Khởi tạo giao diện chính
        khoiTaoGiaoDien();
    }

    private void khoiTaoGiaoDien() {
        setLayout(new BorderLayout());

        // ==================== 1. THANH MENU BAR (PHÍA TRÊN CÙNG) ====================
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(new Color(245, 247, 250));
        
        JMenu menuLogo = new JMenu("NIGHT INVADER");
        menuLogo.setFont(new Font("Arial", Font.BOLD, 14));
        menuLogo.setForeground(new Color(210, 30, 30));
        menuBar.add(menuLogo);

        // Các menu chức năng dropdown chuẩn Swing
        menuBar.add(taoMenuDropdown("Hệ thống", new String[]{"Tài khoản cá nhân", "Đổi mật khẩu", "-", "Đăng xuất"}));
        menuBar.add(taoMenuDropdown("Khách hàng ", new String[]{"Quản lý khách hàng"}));
        menuBar.add(taoMenuDropdown("Xe ", new String[]{"Quản lý xe", "Tra cứu xe", "Lịch sử sửa chữa"}));
        menuBar.add(taoMenuDropdown("Dịch vụ ", new String[]{"Quản lý dịch vụ", "Thêm dịch vụ", "Sửa dịch vụ", "Xóa dịch vụ", "-", "Lập phiếu tiếp nhận xe", "Lập phiếu sửa chữa / Báo giá", "Cập nhật tình trạng sửa xe"}));
        menuBar.add(taoMenuDropdown("Phụ tùng", new String[]{"Quản lý phụ tùng"}));
        menuBar.add(taoMenuDropdown("Kho hàng ", new String[]{"Lập phiếu nhập kho", "Lập phiếu xuất kho"}));
        menuBar.add(taoMenuDropdown("Hóa đơn ", new String[]{"Lập hóa đơn", "Tra cứu hóa đơn"}));
        menuBar.add(taoMenuDropdown("Thống kê ", new String[]{"Thống kê doanh thu", "Thống kê tồn kho", "Phụ tùng bán chạy", "Phụ tùng bán chậm"}));
        menuBar.add(taoMenuDropdown("Nhân viên ", new String[]{"Quản lý nhân sự", "Phân công kỹ thuật viên"}));

        // Menu tài khoản người dùng ở góc phải thanh menu
        JMenu menuTaiKhoan = new JMenu("Xin chào, Chủ Gara");
        menuTaiKhoan.setFont(new Font("Arial", Font.PLAIN, 12));
        ImageIcon iconUserMenu = loadIcon("/img/icon_nguoi.jpg", 18, 18);
        if (iconUserMenu != null) {
            menuTaiKhoan.setIcon(iconUserMenu);
        }

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

        // ==================== 3. NỘI DUNG CHÍNH (DASHBOARD GRID & TÌM KIẾM) ====================
        JPanel panelCenter = new JPanel();
        panelCenter.setBackground(new Color(240, 243, 246));
        panelCenter.setLayout(new BorderLayout());

        JPanel panelTieuDeContainer = new JPanel(new BorderLayout());
        panelTieuDeContainer.setOpaque(false);
        panelTieuDeContainer.setBorder(BorderFactory.createEmptyBorder(20, 30, 15, 30));

        JPanel panelTieuDeText = new JPanel(new GridLayout(2, 1));
        panelTieuDeText.setOpaque(false);
        JLabel lblManHinhChinh = new JLabel("MÀN HÌNH CHÍNH");
        lblManHinhChinh.setFont(new Font("Arial", Font.BOLD, 22));
        lblManHinhChinh.setForeground(new Color(30, 35, 45));
        
        JLabel lblHuongDan = new JLabel("Chọn công việc bạn muốn thực hiện");
        lblHuongDan.setFont(new Font("Arial", Font.PLAIN, 13));
        lblHuongDan.setForeground(Color.GRAY);
        
        panelTieuDeText.add(lblManHinhChinh);
        panelTieuDeText.add(lblHuongDan);

        // Ô tìm kiếm chức năng kèm icon_tim.png
        JPanel panelTimKiem = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        panelTimKiem.setOpaque(false);
        
        JTextField txtTimKiem = new JTextField("Tìm chức năng...", 20);
        txtTimKiem.setFont(new Font("Arial", Font.ITALIC, 13));
        txtTimKiem.setForeground(Color.GRAY);
        txtTimKiem.setPreferredSize(new Dimension(240, 32));
        
        JLabel lblIconSearch = new JLabel(loadIcon("/img/icon_tim.png", 16, 16));
        lblIconSearch.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));

        JPanel searchWrapper = new JPanel(new BorderLayout());
        searchWrapper.setBackground(Color.WHITE);
        searchWrapper.setBorder(BorderFactory.createLineBorder(new Color(200, 205, 210), 1));
        searchWrapper.add(txtTimKiem, BorderLayout.CENTER);
        searchWrapper.add(lblIconSearch, BorderLayout.EAST);
        
        panelTimKiem.add(searchWrapper);

        panelTieuDeContainer.add(panelTieuDeText, BorderLayout.WEST);
        panelTieuDeContainer.add(panelTimKiem, BorderLayout.EAST);

        // Lưới chứa các nút thẻ chức năng (GridLayout 3x3)
        JPanel panelGridThe = new JPanel(new GridLayout(3, 3, 20, 20));
        panelGridThe.setOpaque(false);
        panelGridThe.setBorder(BorderFactory.createEmptyBorder(0, 30, 30, 30));

        panelGridThe.add(taoNutChucNang("Khách hàng", "Quản lý khách hàng\nThêm, sửa thông tin khách hàng", loadIcon("/img/icon_khachhang.png", 32, 32)));
        panelGridThe.add(taoNutChucNang("Xe", "Quản lý xe - Tra cứu xe\nXem lịch sử sửa chữa theo biển số", loadIcon("/img/icon_xe.png", 32, 32)));
        panelGridThe.add(taoNutChucNang("Dịch vụ", "Thêm - Sửa - Xóa dịch vụ\nLập phiếu tiếp nhận xe - Lập báo giá", loadIcon("/img/icon-vector.png", 32, 32)));
        
        panelGridThe.add(taoNutChucNang("Phụ tùng", "Quản lý danh mục phụ tùng\nThêm, sửa - Ngừng kinh doanh", loadIcon("/img/User_box_light.png", 32, 32)));
        panelGridThe.add(taoNutChucNang("Kho hàng", "Lập phiếu nhập kho\nLập phiếu xuất kho", loadIcon("/img/icon_nhapkho.jpg", 32, 32)));
        panelGridThe.add(taoNutChucNang("Hóa đơn", "Lập hóa đơn và ghi nhận thanh toán\nTra cứu - In hóa đơn", loadIcon("/img/icon_hoadon.png", 32, 32)));
        
        panelGridThe.add(taoNutChucNang("Thống kê", "Doanh thu - Tồn kho\nPhụ tùng bán chạy - Bán chậm", loadIcon("/img/icon_thongke.png", 32, 32)));
        panelGridThe.add(taoNutChucNang("Nhân viên", "Quản lý nhân sự\nPhân công kỹ thuật viên", loadIcon("/img/icon_nguoi.jpg", 32, 32)));
        panelGridThe.add(taoNutChucNang("Hệ thống", "Tài khoản cá nhân - Đổi mật khẩu\nĐăng xuất", loadIcon("/img/icon_caidat.png", 32, 32)));

        panelCenter.add(panelTieuDeContainer, BorderLayout.NORTH);
        panelCenter.add(panelGridThe, BorderLayout.CENTER);

        // ==================== 4. THANH TRẠNG THÁI (FOOTER) ====================
        JPanel panelFooter = new JPanel(new BorderLayout());
        panelFooter.setBackground(new Color(245, 247, 250));
        panelFooter.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        
        JLabel lblTrangThai = new JLabel("Đang hoạt động | Vai trò: Chủ Gara | Chi nhánh: Gara chính");
        lblTrangThai.setFont(new Font("Arial", Font.PLAIN, 12));
        lblTrangThai.setForeground(Color.DARK_GRAY);
        
        JLabel lblBanQuyen = new JLabel("NIGHT INVADER • Gara ô tô");
        lblBanQuyen.setFont(new Font("Arial", Font.PLAIN, 12));
        lblBanQuyen.setForeground(Color.GRAY);

        panelFooter.add(lblTrangThai, BorderLayout.WEST);
        panelFooter.add(lblBanQuyen, BorderLayout.EAST);

        // Gắn tất cả vào Frame chính
        add(panelTopContainer, BorderLayout.NORTH);
        add(panelCenter, BorderLayout.CENTER);
        add(panelFooter, BorderLayout.SOUTH);
    }

    // Hàm tiện ích hỗ trợ tải và scale kích thước icon an toàn từ đường dẫn /img/...
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

    // Hàm hỗ trợ tạo menu dropdown chuẩn Swing
    private JMenu taoMenuDropdown(String tenMenu, String[] cacItem) {
        JMenu menu = new JMenu(tenMenu);
        for (String itemName : cacItem) {
            if (itemName.equals("-")) {
                menu.addSeparator();
            } else {
                JMenuItem item = new JMenuItem(itemName);
                item.addActionListener(e -> JOptionPane.showMessageDialog(this,
                        "Đang mở chức năng: " + itemName,
                        "Thông báo hệ thống", JOptionPane.INFORMATION_MESSAGE));
                menu.add(item);
            }
        }
        return menu;
    }

    // ĐÃ SỬA: Hàm tạo nút bấm nhanh trên Toolbar (Đặt Icon bên trái, Text bên phải, tránh đè chữ)
    private JButton taoNutNhanh(String tieuDe, String phimTat, ImageIcon icon) {
        JButton btn = new JButton();
        btn.setLayout(new BorderLayout(8, 0)); // Khoảng cách giữa icon và chữ
        
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

    // Hàm tạo nút chức năng (Card Button) chuẩn Java Swing thuần, căn giữa nội dung, KHÔNG DÙNG HTML
    private JButton taoNutChucNang(String tenTieuDe, String moTa, ImageIcon icon) {
        JButton btnCard = new JButton();
        btnCard.setLayout(new BorderLayout(5, 5));
        btnCard.setBackground(Color.WHITE);
        btnCard.setFocusPainted(false);
        btnCard.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Tạo viền thẻ card
        btnCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 225, 230), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        // Đặt Icon ở phía TRÊN (NORTH) của Button
        if (icon != null) {
            JLabel lblIcon = new JLabel(icon, JLabel.CENTER);
            btnCard.add(lblIcon, BorderLayout.NORTH);
        }

        // Tạo Panel chứa chữ ở giữa (CENTER) để căn giữa hoàn toàn bằng Swing Layout Managers
        JPanel panelText = new JPanel();
        panelText.setLayout(new BoxLayout(panelText, BoxLayout.Y_AXIS));
        panelText.setOpaque(false);

        JLabel lblTieuDe = new JLabel(tenTieuDe);
        lblTieuDe.setFont(new Font("Arial", Font.BOLD, 15));
        lblTieuDe.setForeground(new Color(30, 35, 45));
        lblTieuDe.setAlignmentX(Component.CENTER_ALIGNMENT);

        panelText.add(Box.createRigidArea(new Dimension(0, 5)));
        panelText.add(lblTieuDe);
        panelText.add(Box.createRigidArea(new Dimension(0, 5)));

        // Tách các dòng mô tả bằng \n và tạo các JLabel riêng biệt
        String[] lines = moTa.split("\n");
        for (String line : lines) {
            JLabel lblLine = new JLabel(line);
            lblLine.setFont(new Font("Arial", Font.PLAIN, 11));
            lblLine.setForeground(Color.GRAY);
            lblLine.setAlignmentX(Component.CENTER_ALIGNMENT);
            panelText.add(lblLine);
            panelText.add(Box.createRigidArea(new Dimension(0, 2)));
        }

        btnCard.add(panelText, BorderLayout.CENTER);

        // Sự kiện khi bấm vào nút thẻ chuyển hướng
        btnCard.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Đang chuyển hướng đến module: " + tenTieuDe,
                "Điều hướng", JOptionPane.INFORMATION_MESSAGE));

        return btnCard;
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> new GaraManHinhChinh().setVisible(true));
    }
}