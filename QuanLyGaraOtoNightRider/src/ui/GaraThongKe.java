package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class GaraThongKe extends JFrame {

    private JTabbedPane tabbedPane;

    public GaraThongKe() {
        this(0);
    }

    public GaraThongKe(int tabMacDinh) {
        setTitle("Hệ thống Quản lý Gara Ô Tô - Night Invader");
        setSize(1340, 840);
        setLocationRelativeTo(null);
        setResizable(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        khoiTaoGiaoDien();

        if (tabMacDinh >= 0 && tabMacDinh < tabbedPane.getTabCount()) {
            tabbedPane.setSelectedIndex(tabMacDinh);
        }
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

        JLabel lblModuleHienTai = new JLabel("Thống kê & Báo cáo");
        lblModuleHienTai.setFont(new Font("Arial", Font.BOLD, 13));
        lblModuleHienTai.setForeground(new Color(150, 30, 40));

        JPanel panelLabelTieuDe = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 12));
        panelLabelTieuDe.setBackground(Color.WHITE);
        panelLabelTieuDe.add(lblModuleHienTai);

        JPanel panelToolbarWrapper = new JPanel(new BorderLayout());
        panelToolbarWrapper.setBackground(Color.WHITE);
        panelToolbarWrapper.add(panelToolbar, BorderLayout.CENTER);
        panelToolbarWrapper.add(panelLabelTieuDe, BorderLayout.EAST);

        // ==================== 3. NỘI DUNG CHÍNH: FORM THỐNG KÊ ====================
        JPanel panelCenter = new JPanel();
        panelCenter.setBackground(new Color(240, 243, 246));
        panelCenter.setLayout(new GridBagLayout()); // Căn giữa Card chính

        JPanel panelCardChinh = new JPanel(new BorderLayout());
        panelCardChinh.setBackground(Color.WHITE);
        panelCardChinh.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 215, 220), 1),
                BorderFactory.createEmptyBorder(25, 30, 25, 30)
        ));
        panelCardChinh.setPreferredSize(new Dimension(1220, 660)); // Kích thước cố định giống TaiKhoanCaNhan

        // --- 3.1. Tiêu đề card ---
        JPanel panelCardTop = new JPanel(new BorderLayout());
        panelCardTop.setBackground(Color.WHITE);
        panelCardTop.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        JLabel lblTieuDeCard = new JLabel("BÁO CÁO THỐNG KÊ TỔNG HỢP");
        lblTieuDeCard.setFont(new Font("Arial", Font.BOLD, 18));
        lblTieuDeCard.setForeground(new Color(30, 35, 45));

        JLabel lblMoTa = new JLabel("Tổng hợp dữ liệu doanh thu, tồn kho và phân tích kinh doanh.");
        lblMoTa.setFont(new Font("Arial", Font.PLAIN, 12));
        lblMoTa.setForeground(Color.GRAY);

        JPanel pnlTitleWrap = new JPanel(new GridLayout(2, 1));
        pnlTitleWrap.setBackground(Color.WHITE);
        pnlTitleWrap.add(lblTieuDeCard);
        pnlTitleWrap.add(lblMoTa);

        panelCardTop.add(pnlTitleWrap, BorderLayout.WEST);

        // --- 3.2. Form Thống Kê (Tabs) ---
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 14));
        tabbedPane.setBackground(new Color(245, 247, 250));

        tabbedPane.addTab("Thống kê doanh thu", taoTabDoanhThu());
        tabbedPane.addTab("Thống kê tồn kho", taoTabTonKho());
        tabbedPane.addTab("Phụ tùng bán chạy", taoTabBanChay());
        tabbedPane.addTab("Phụ tùng bán chậm", taoTabBanCham());

        // --- 3.3. Khu vực nút bấm dưới cùng ---
        JPanel panelBottomButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        panelBottomButtons.setBackground(Color.WHITE);
        panelBottomButtons.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        JButton btnXuatExcel = new JButton("Xuất Excel");
        btnXuatExcel.setFont(new Font("Arial", Font.BOLD, 13));
        btnXuatExcel.setBackground(new Color(25, 135, 84)); // Xanh lá mượt
        btnXuatExcel.setForeground(Color.WHITE);
        btnXuatExcel.setOpaque(true);
        btnXuatExcel.setBorderPainted(false);
        btnXuatExcel.setFocusPainted(false);
        btnXuatExcel.setBorder(BorderFactory.createEmptyBorder(9, 18, 9, 18));
        btnXuatExcel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnXuatExcel.addActionListener(e -> JOptionPane.showMessageDialog(this, "Đang xuất dữ liệu tab hiện tại ra file Excel...", "Thông báo", JOptionPane.INFORMATION_MESSAGE));

        JButton btnQuayLai = new JButton("Trở về Màn Hình Chính");
        btnQuayLai.setFont(new Font("Arial", Font.BOLD, 13));
        btnQuayLai.setBackground(Color.WHITE);
        btnQuayLai.setForeground(new Color(50, 55, 65));
        btnQuayLai.setFocusPainted(false);
        btnQuayLai.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 205, 210), 1),
                BorderFactory.createEmptyBorder(8, 16, 8, 16)
        ));
        btnQuayLai.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnQuayLai.addActionListener(e -> {
            new GaraManHinhChinh().setVisible(true);
            this.dispose();
        });

        panelBottomButtons.add(btnQuayLai);
        panelBottomButtons.add(btnXuatExcel);

        // Gắn vào Card Chính
        panelCardChinh.add(panelCardTop, BorderLayout.NORTH);
        panelCardChinh.add(tabbedPane, BorderLayout.CENTER);
        panelCardChinh.add(panelBottomButtons, BorderLayout.SOUTH);

        panelCenter.add(panelCardChinh, new GridBagConstraints());

        // ==================== 4. THANH TRẠNG THÁI (FOOTER) ====================
        JPanel panelFooter = new JPanel(new BorderLayout());
        panelFooter.setBackground(new Color(245, 247, 250));
        panelFooter.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));

        JLabel lblTrangThai = new JLabel("Đang hoạt động | Kho chính | Chủ Gara");
        lblTrangThai.setFont(new Font("Arial", Font.PLAIN, 12));
        lblTrangThai.setForeground(Color.DARK_GRAY);

        JLabel lblBanQuyen = new JLabel("NIGHT INVADER - 05/10/2026");
        lblBanQuyen.setFont(new Font("Arial", Font.PLAIN, 12));
        lblBanQuyen.setForeground(Color.GRAY);

        panelFooter.add(lblTrangThai, BorderLayout.WEST);
        panelFooter.add(lblBanQuyen, BorderLayout.EAST);

        add(panelToolbarWrapper, BorderLayout.NORTH);
        add(panelCenter, BorderLayout.CENTER);
        add(panelFooter, BorderLayout.SOUTH);
    }

    // ==================== CÁC TAB THỐNG KÊ ====================
    private JPanel taoTabDoanhThu() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));

        // Lọc
        JPanel panelLoc = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        panelLoc.setBackground(new Color(250, 250, 252));
        panelLoc.setBorder(BorderFactory.createLineBorder(new Color(220, 225, 230)));

        panelLoc.add(new JLabel("Từ ngày:"));
        panelLoc.add(new JTextField("01/10/2026", 10));
        panelLoc.add(new JLabel("Đến ngày:"));
        panelLoc.add(new JTextField("31/10/2026", 10));

        JButton btnLoc = new JButton("Lọc Dữ Liệu");
        btnLoc.setBackground(new Color(30, 35, 45));
        btnLoc.setForeground(Color.WHITE);
        btnLoc.setFocusPainted(false);
        panelLoc.add(btnLoc);

        // Tổng quan
        JPanel panelTongQuan = new JPanel(new GridLayout(1, 3, 20, 0));
        panelTongQuan.setOpaque(false);
        panelTongQuan.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        panelTongQuan.add(taoCardThongKe("TỔNG DOANH THU", "85,450,000 đ", new Color(25, 135, 84)));
        panelTongQuan.add(taoCardThongKe("TỔNG CHI PHÍ PHỤ TÙNG", "32,100,000 đ", new Color(210, 30, 30)));
        panelTongQuan.add(taoCardThongKe("LỢI NHUẬN GỘP", "53,350,000 đ", new Color(13, 110, 253)));

        JPanel panelTop = new JPanel(new BorderLayout(0, 5));
        panelTop.setOpaque(false);
        panelTop.add(panelLoc, BorderLayout.NORTH);
        panelTop.add(panelTongQuan, BorderLayout.CENTER);

        // Bảng
        String[] columns = {"Mã HĐ", "Ngày Lập", "Biển Số Xe", "Khách Hàng", "Tiền Công", "Tiền Phụ Tùng", "Tổng Thu"};
        Object[][] data = {
                {"HD00101", "15/10/2026", "51G-123.45", "Trần Ngọc Huy", "500,000", "1,200,000", "1,700,000"},
                {"HD00102", "15/10/2026", "60A-888.99", "Nguyễn Văn A", "1,500,000", "3,000,000", "4,500,000"}
        };
        JTable table = taoBangDuLieu(columns, data);

        panel.add(panelTop, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private JPanel taoTabTonKho() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));

        JPanel panelTongQuan = new JPanel(new GridLayout(1, 2, 20, 0));
        panelTongQuan.setOpaque(false);
        panelTongQuan.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        panelTongQuan.add(taoCardThongKe("TỔNG SỐ LƯỢNG PHỤ TÙNG", "1,245 CHIẾC", Color.DARK_GRAY));
        panelTongQuan.add(taoCardThongKe("TỔNG GIÁ TRỊ TỒN KHO HIỆN TẠI", "145,000,000 VNĐ", new Color(210, 30, 30)));

        String[] columns = {"Mã PT", "Tên Phụ Tùng / Vật Tư", "ĐVT", "SL Tồn", "Đơn Giá Nhập", "Thành Tiền", "Trạng Thái"};
        Object[][] data = {
                {"PT001", "Nhớt Castrol GTX 10W-40", "Can", "15", "350,000", "5,250,000", "Bình thường"},
                {"PT002", "Lọc nhớt Honda Civic 2020", "Cái", "2", "150,000", "300,000", "Cảnh báo Min-stock"}
        };
        JTable table = taoBangDuLieu(columns, data);

        panel.add(panelTongQuan, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private JPanel taoTabBanChay() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));

        JLabel lblTop = new JLabel("TOP 10 PHỤ TÙNG XUẤT KHO NHIỀU NHẤT KỲ");
        lblTop.setFont(new Font("Arial", Font.BOLD, 15));

        String[] columns = {"Xếp Hạng", "Mã PT", "Tên Phụ Tùng", "Số Lượng Đã Xuất", "Doanh Thu Mang Lại (VNĐ)"};
        Object[][] data = {
                {"1", "PT001", "Nhớt Castrol GTX 10W-40", "45", "20,250,000"},
                {"2", "PT005", "Bugi Iridium NGK", "30", "12,000,000"}
        };
        JTable table = taoBangDuLieu(columns, data);

        panel.add(lblTop, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private JPanel taoTabBanCham() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));

        JLabel lblTop = new JLabel("DANH SÁCH PHỤ TÙNG ÍT GIAO DỊCH (TỒN ĐỌNG LÂU)");
        lblTop.setFont(new Font("Arial", Font.BOLD, 15));

        String[] columns = {"Xếp Hạng", "Mã PT", "Tên Phụ Tùng", "Số Lượng Tồn", "Số Ngày Tồn", "Lần Xuất Gần Nhất"};
        Object[][] data = {
                {"1", "PT088", "Cản trước Toyota Vios 2018", "2", "120 ngày", "01/06/2026"},
                {"2", "PT091", "Gương chiếu hậu Mazda 3", "3", "85 ngày", "15/07/2026"}
        };
        JTable table = taoBangDuLieu(columns, data);

        panel.add(lblTop, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    // ==================== HÀM TIỆN ÍCH ====================

    private JPanel taoCardThongKe(String tieuDe, String giaTri, Color mauNhan) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(new Color(250, 252, 254)); // Nền hơi sáng xám nhạt để tách khỏi nền trắng của panel
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 225, 230), 1),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 5, 0, 0, mauNhan),
                        BorderFactory.createEmptyBorder(15, 20, 15, 20)
                )
        ));

        JLabel lblTieuDe = new JLabel(tieuDe);
        lblTieuDe.setFont(new Font("Arial", Font.BOLD, 12));
        lblTieuDe.setForeground(Color.GRAY);

        JLabel lblGiaTri = new JLabel(giaTri);
        lblGiaTri.setFont(new Font("Arial", Font.BOLD, 22));
        lblGiaTri.setForeground(mauNhan);

        card.add(lblTieuDe);
        card.add(Box.createRigidArea(new Dimension(0, 8)));
        card.add(lblGiaTri);
        return card;
    }

    private JTable taoBangDuLieu(String[] columns, Object[][] data) {
        DefaultTableModel model = new DefaultTableModel(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(model);
        table.setRowHeight(32);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(230, 235, 240));
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.setSelectionBackground(new Color(40, 100, 150));
        table.setSelectionForeground(Color.WHITE);

        // Căn phải cho cột tiền (VD cột cuối)
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        table.getColumnModel().getColumn(table.getColumnCount() - 1).setCellRenderer(rightRenderer);

        return table;
    }

    private void xuLyDangXuat() {
        int luaChon = JOptionPane.showConfirmDialog(
                this, "Bạn có chắc chắn muốn đăng xuất?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (luaChon == JOptionPane.YES_OPTION) {
            this.dispose();
            System.exit(0);
        }
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
            System.out.println("Không thể tải ảnh: " + path);
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
                        tabbedPane.setSelectedIndex(0);
                    } else if (itemName.equals("Thống kê tồn kho")) {
                        tabbedPane.setSelectedIndex(1);
                    } else if (itemName.equals("Phụ tùng bán chạy")) {
                        tabbedPane.setSelectedIndex(2);
                    } else if (itemName.equals("Phụ tùng bán chậm")) {
                        tabbedPane.setSelectedIndex(3);
                    } else if (itemName.equals("Quản lý nhân sự")) {
                        new GaraQuanLyNhanSu().setVisible(true);
                        this.dispose();
                    } else if (itemName.equals("Phân công kỹ thuật viên")) {
                        new GaraPhanCongKTV().setVisible(true);
                        this.dispose();
                    } else if (itemName.equals("Tài khoản cá nhân")) {
                        new GaraTaiKhoanCaNhan().setVisible(true);
                        this.dispose();
                    } else if (itemName.equals("Quản lý nhân sự") ||
                            itemName.equals("Phân công kỹ thuật viên")) {
                        new GaraNhanVien().setVisible(true);
                        this.dispose();
                    } else if (itemName.equals("Đăng xuất")) {
                        System.exit(0);
                    } else {
                        JOptionPane.showMessageDialog(this,
                                "Đang mở chức năng: " + itemName,
                                "Thông báo hệ thống", JOptionPane.INFORMATION_MESSAGE);


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
        return btn;
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {}
        SwingUtilities.invokeLater(() -> new GaraThongKe().setVisible(true));
    }
}