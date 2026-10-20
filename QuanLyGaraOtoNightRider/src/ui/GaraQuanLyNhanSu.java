package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class GaraQuanLyNhanSu extends JFrame {

    private JTextField txtMaNV, txtHoTen, txtSoDienThoai, txtNgayVaoLam, txtTimKiem;
    private JComboBox<String> cbVaiTro, cbTrangThai;
    private JTable tableNhanVien;

    public GaraQuanLyNhanSu() {
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

        String[] tenCacMenu = {"Hệ thống", "Khách hàng ", "Xe ", "Dịch vụ ", "Phụ tùng", "Kho hàng ", "Hóa đơn ", "Thống kê ", "Nhân viên "};
        String[][] cacItemMenu = {
                {"Màn hình chính", "Tài khoản cá nhân", "Đổi mật khẩu", "-", "Đăng xuất"},
                {"Quản lý khách hàng"}, {"Quản lý xe", "Tra cứu xe", "Lịch sử sửa chữa"},
                {"Quản lý dịch vụ", "Thêm dịch vụ", "Sửa dịch vụ", "-", "Lập phiếu tiếp nhận xe", "Lập phiếu sửa chữa / Báo giá", "Cập nhật tình trạng sửa xe"},
                {"Quản lý phụ tùng"}, {"Lập phiếu nhập kho", "Lập phiếu xuất kho"},
                {"Lập hóa đơn", "Tra cứu hóa đơn"}, {"Thống kê doanh thu", "Thống kê tồn kho", "Phụ tùng bán chạy", "Phụ tùng bán chậm"},
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
        ImageIcon iconUser = loadIcon("/img/icon_nguoi.jpg", 20, 20);
        if (iconUser != null) menuTaiKhoan.setIcon(iconUser);
        menuBar.add(Box.createHorizontalGlue());
        menuBar.add(menuTaiKhoan);
        setJMenuBar(menuBar);

        // ==================== 2. THANH CÔNG CỤ NHANH ====================
        JPanel panelToolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        panelToolbar.setBackground(Color.WHITE);
        panelToolbar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 224, 230)));

        panelToolbar.add(taoNutNhanh("Tiếp nhận xe", "F2", loadIcon("/img/icon_xe.png", 22, 22)));
        panelToolbar.add(taoNutNhanh("Tình trạng sửa xe", "F5", loadIcon("/img/icon-vector.png", 22, 22)));
        panelToolbar.add(taoNutNhanh("Lập hóa đơn", "F3", loadIcon("/img/icon_hoadon.png", 22, 22)));
        panelToolbar.add(taoNutNhanh("Nhập kho", "F4", loadIcon("/img/icon_nhapkho.jpg", 22, 22)));

        JLabel lblModuleHienTai = new JLabel("Phân hệ Quản Lý Nhân Sự");
        lblModuleHienTai.setFont(new Font("Arial", Font.BOLD, 13));
        lblModuleHienTai.setForeground(new Color(150, 30, 40));

        JPanel panelLabelTieuDe = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 12));
        panelLabelTieuDe.setBackground(Color.WHITE);
        panelLabelTieuDe.add(lblModuleHienTai);

        JPanel panelToolbarWrapper = new JPanel(new BorderLayout());
        panelToolbarWrapper.setBackground(Color.WHITE);
        panelToolbarWrapper.add(panelToolbar, BorderLayout.CENTER);
        panelToolbarWrapper.add(panelLabelTieuDe, BorderLayout.EAST);

        // ==================== 3. NỘI DUNG CHÍNH (TỰ ĐỘNG GIÃN RỘNG MÀN HÌNH) ====================
        JPanel panelCenter = new JPanel(new BorderLayout());
        panelCenter.setBackground(new Color(240, 243, 246));
        panelCenter.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20)); // Lề xung quanh card

        JPanel panelCardChinh = new JPanel(new BorderLayout(0, 15));
        panelCardChinh.setBackground(Color.WHITE);
        panelCardChinh.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 215, 220), 1),
                BorderFactory.createEmptyBorder(20, 25, 20, 25)
        ));

        // Tiêu đề card
        JLabel lblTieuDeCard = new JLabel("HỒ SƠ NHÂN VIÊN");
        lblTieuDeCard.setFont(new Font("Arial", Font.BOLD, 18));
        JLabel lblMoTa = new JLabel("Thêm mới, cập nhật thông tin và phân quyền vai trò cho nhân sự trong gara.");
        lblMoTa.setForeground(Color.GRAY);
        JPanel pnlTitleWrap = new JPanel(new GridLayout(2, 1));
        pnlTitleWrap.setBackground(Color.WHITE);
        pnlTitleWrap.add(lblTieuDeCard);
        pnlTitleWrap.add(lblMoTa);

        // Form Nhập Liệu (Grid 2 dòng 3 cột)
        txtMaNV = new JTextField("NV004"); txtMaNV.setEditable(false); txtMaNV.setBackground(new Color(245, 247, 250));
        txtHoTen = new JTextField();
        txtSoDienThoai = new JTextField();
        txtNgayVaoLam = new JTextField("12/10/2026");
        cbVaiTro = new JComboBox<>(new String[]{"Tiếp nhận", "Kỹ thuật viên", "Nhân viên Kho", "Thu ngân", "Quản lý"});
        cbVaiTro.setBackground(Color.WHITE);
        cbTrangThai = new JComboBox<>(new String[]{"Đang làm việc", "Đã nghỉ / Khóa"});
        cbTrangThai.setBackground(Color.WHITE);

        JPanel panelGridFields = new JPanel(new GridLayout(2, 3, 30, 15));
        panelGridFields.setBackground(Color.WHITE);
        panelGridFields.add(taoOnhapLieu("Mã nhân viên (Tự động)", txtMaNV));
        panelGridFields.add(taoOnhapLieu("Họ và Tên (*)", txtHoTen));
        panelGridFields.add(taoOnhapLieu("Số điện thoại (*)", txtSoDienThoai));
        panelGridFields.add(taoOnhapLieu("Vai trò / Chức vụ", cbVaiTro));
        panelGridFields.add(taoOnhapLieu("Ngày vào làm", txtNgayVaoLam));
        panelGridFields.add(taoOnhapLieu("Trạng thái", cbTrangThai));

        // Bộ lọc & Bảng dữ liệu
        JPanel panelSearchFilter = new JPanel(new BorderLayout());
        panelSearchFilter.setBackground(new Color(245, 247, 250));
        panelSearchFilter.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 225, 230), 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        txtTimKiem = new JTextField(" Tìm kiếm nhân viên...");
        txtTimKiem.setFont(new Font("Arial", Font.ITALIC, 13));
        txtTimKiem.setForeground(Color.GRAY);
        txtTimKiem.setOpaque(false); txtTimKiem.setBorder(null);
        JComboBox<String> cbLocVaiTro = new JComboBox<>(new String[]{"Tất cả vai trò", "Tiếp nhận", "Kỹ thuật viên", "Kho", "Thu ngân"});
        cbLocVaiTro.setBackground(Color.WHITE);

        ImageIcon iconTim = loadIcon("/img/icon_tim.png", 16, 16);
        if (iconTim != null) panelSearchFilter.add(new JLabel(iconTim), BorderLayout.WEST);
        panelSearchFilter.add(txtTimKiem, BorderLayout.CENTER);
        panelSearchFilter.add(cbLocVaiTro, BorderLayout.EAST);

        String[] cols = {"Mã NV", "Họ Tên", "SĐT", "Vai Trò", "Ngày Vào Làm", "Trạng Thái"};
        Object[][] data = {
                {"NV001", "Võ Thị Bích Trân", "0987654321", "Thu ngân", "10/08/2026", "Đang làm việc"},
                {"NV002", "Trần Ngọc Huy", "0912345678", "Kỹ thuật viên", "12/08/2026", "Đang làm việc"}
        };
        tableNhanVien = taoBangDuLieu(cols, data);

        JPanel panelTableSection = new JPanel(new BorderLayout(0, 10));
        panelTableSection.setBackground(Color.WHITE);
        panelTableSection.add(panelSearchFilter, BorderLayout.NORTH);
        panelTableSection.add(new JScrollPane(tableNhanVien), BorderLayout.CENTER);

        // Các nút CRUD
        JPanel panelBottomButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelBottomButtons.setBackground(Color.WHITE);
        JButton btnLamMoi = taoNutChucNang("Làm mới (Xóa rỗng)", Color.WHITE, new Color(50, 55, 65));
        JButton btnKhoa = taoNutChucNang("Khóa tài khoản (Xóa)", Color.WHITE, new Color(210, 30, 30));
        JButton btnCapNhat = taoNutChucNang("Cập nhật (Sửa)", new Color(40, 100, 150), Color.WHITE);
        JButton btnThemMoi = taoNutChucNang("Thêm mới", new Color(25, 135, 84), Color.WHITE);

        btnLamMoi.addActionListener(e -> { txtHoTen.setText(""); txtSoDienThoai.setText(""); cbVaiTro.setSelectedIndex(0); });

        panelBottomButtons.add(btnLamMoi); panelBottomButtons.add(btnKhoa);
        panelBottomButtons.add(btnCapNhat); panelBottomButtons.add(btnThemMoi);

        panelCardChinh.add(pnlTitleWrap, BorderLayout.NORTH);
        JPanel pnlBody = new JPanel(new BorderLayout(0, 15));
        pnlBody.setBackground(Color.WHITE);
        pnlBody.add(panelGridFields, BorderLayout.NORTH);
        pnlBody.add(panelTableSection, BorderLayout.CENTER);
        pnlBody.add(panelBottomButtons, BorderLayout.SOUTH);
        panelCardChinh.add(pnlBody, BorderLayout.CENTER);

        panelCenter.add(panelCardChinh, BorderLayout.CENTER);

        // ==================== 4. THANH TRẠNG THÁI ====================
        JPanel panelFooter = new JPanel(new BorderLayout());
        panelFooter.setBackground(new Color(245, 247, 250));
        panelFooter.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        panelFooter.add(new JLabel("Đang hoạt động | Quản trị viên"), BorderLayout.WEST);
        panelFooter.add(new JLabel("NIGHT INVADER - HỆ THỐNG QUẢN LÝ GARA"), BorderLayout.EAST);

        add(panelToolbarWrapper, BorderLayout.NORTH);
        add(panelCenter, BorderLayout.CENTER);
        add(panelFooter, BorderLayout.SOUTH);
    }

    private JPanel taoOnhapLieu(String lbl, JComponent c) {
        JPanel p = new JPanel(new BorderLayout(0, 5)); p.setBackground(Color.WHITE);
        JLabel l = new JLabel(lbl); l.setFont(new Font("Arial", Font.BOLD, 12));
        c.setPreferredSize(new Dimension(250, 35));
        if (c instanceof JTextField) c.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(200, 205, 210)), BorderFactory.createEmptyBorder(4, 10, 4, 10)));
        p.add(l, BorderLayout.NORTH); p.add(c, BorderLayout.CENTER); return p;
    }

    private JTable taoBangDuLieu(String[] cols, Object[][] data) {
        JTable table = new JTable(new DefaultTableModel(data, cols) { public boolean isCellEditable(int r, int c) { return false; } });
        table.setRowHeight(32); table.getTableHeader().setBackground(new Color(230, 235, 240));
        return table;
    }

    private JButton taoNutChucNang(String text, Color bg, Color fg) {
        JButton btn = new JButton(text); btn.setBackground(bg); btn.setForeground(fg);
        if (bg.equals(Color.WHITE)) btn.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(fg), BorderFactory.createEmptyBorder(8, 20, 8, 20)));
        else { btn.setOpaque(true); btn.setBorderPainted(false); btn.setBorder(BorderFactory.createEmptyBorder(9, 20, 9, 20)); }
        return btn;
    }

    private ImageIcon loadIcon(String p, int w, int h) {
        try { java.net.URL url = getClass().getResource(p); if(url != null) return new ImageIcon(new ImageIcon(url).getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH)); } catch(Exception ignored){} return null;
    }

    private JButton taoNutNhanh(String tieuDe, String phimTat, ImageIcon icon) {
        JButton btn = new JButton(); btn.setLayout(new BorderLayout(10, 0));
        if (icon != null) btn.add(new JLabel(icon), BorderLayout.WEST);
        JPanel p = new JPanel(new GridLayout(2, 1)); p.setOpaque(false);
        p.add(new JLabel(tieuDe)); p.add(new JLabel(phimTat)); btn.add(p, BorderLayout.CENTER);
        btn.setBackground(Color.WHITE); btn.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        return btn;
    }

    private JMenu taoMenuDropdown(String tenMenu, String[] cacItem) {
        JMenu menu = new JMenu(tenMenu);
        for (String itemName : cacItem) {
            if (itemName.equals("-")) menu.addSeparator();
            else {
                JMenuItem item = new JMenuItem(itemName);
                item.addActionListener(e -> {

                    if (itemName.equals("Thống kê doanh thu") ) {
                        new GaraThongKe(0).setVisible(true);
                        this.dispose();
                    } else if (itemName.equals("Thống kê tồn kho")) {
                        new GaraThongKe(1).setVisible(true);
                        this.dispose();
                    } else if (itemName.equals("Phụ tùng bán chạy")) {
                        new GaraThongKe(2).setVisible(true);
                        this.dispose();
                    } else if (itemName.equals("Phụ tùng bán chậm")) {
                        new  GaraThongKe(3).setVisible(true);
                        this.dispose();
                    } else if (itemName.equals("Quản lý nhân sự")) {
                        JOptionPane.showMessageDialog(this, "Bạn đang ở màn hình quản lý nhân sự rồi!");
                    } else if (itemName.equals("Phân công kỹ thuật viên")) {
                        new GaraPhanCongKTV().setVisible(true);
                        this.dispose();
                    } else if (itemName.equals("Tài khoản cá nhân")) {
                        new GaraTaiKhoanCaNhan().setVisible(true);
                        this.dispose();
                    }  else if (itemName.equals("Đăng xuất")) {
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

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}
        SwingUtilities.invokeLater(() -> new GaraQuanLyNhanSu().setVisible(true));
    }
}