package ui;

import connectDB.ConnectDB;
import dao.NhanVienDao;
import entity.NhanVien;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class GaraNhanVien extends JFrame {

    // Khai báo tabbedPane làm biến toàn cục của class
    private JTabbedPane tabbedPane;

    // Các Component cho Tab Quản lý nhân sự
    private JTextField txtMaNV, txtHoTen, txtSoDienThoai, txtNgayVaoLam, txtTimKiem;
    private JComboBox<String> cbVaiTro, cbTrangThai;
    private JTable tableNhanVien;

    // Các Component cho Tab Phân công KTV
    private JComboBox<String> cbPhieuSuaChua, cbKyThuatVien;
    private JTextField txtGhiChuPhanCong;
    private JTable tablePhanCong;

    private NhanVienDao nhanVienDao;

    // Model quản lý dữ liệu cho 2 bảng
    private DefaultTableModel tableModelNhanVien;
    private DefaultTableModel tableModelPhanCong;

    // Constructor mặc định (mở tab 0: Hồ sơ nhân viên)
    public GaraNhanVien() {
        this(0);
    }

    // Constructor có tham số để nhảy thẳng vào tab mong muốn
    public GaraNhanVien(int tabMacDinh) {
        try {
            ConnectDB.getInstance().connect();
            System.out.println("Kết nối CSDL NightInvader thành công!");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi kết nối Cơ sở dữ liệu: " + e.getMessage());
        }

        nhanVienDao = new NhanVienDao();

        setTitle("Hệ thống Quản lý Gara Ô Tô - Night Invader");
        setSize(1340, 840);
        setLocationRelativeTo(null);
        setResizable(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        khoiTaoGiaoDien();

        if (tabMacDinh >= 0 && tabbedPane != null && tabMacDinh < tabbedPane.getTabCount()) {
            tabbedPane.setSelectedIndex(tabMacDinh);
        }

        // ĐỔ DỮ LIỆU TỪ DB LÊN BẢNG NHÂN VIÊN
        loadDataToTableNhanVien();
    }

    // ========== HÀM TẢI DỮ LIỆU TỪ SQL LÊN BẢNG NHÂN VIÊN ==========
    private void loadDataToTableNhanVien() {
        if (tableModelNhanVien == null) return;
        tableModelNhanVien.setRowCount(0);

        List<NhanVien> listNV = null;
        try {
            listNV = nhanVienDao.layDanhSachNhanVien();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (NhanVien nv : listNV) {
            String ngayVaoStr = (nv.getNgayVaoLam() != null) ? nv.getNgayVaoLam().format(dtf) : "";
            String vaiTro = (nv.getTenChuc() != null) ? nv.getTenChuc() : nv.getMaPhanQuyen();

            tableModelNhanVien.addRow(new Object[]{
                    nv.getMaNhanVien(),
                    nv.getHoTen(),
                    nv.getSoDienThoai(),
                    vaiTro,
                    ngayVaoStr,
                    nv.getTrangThai()
            });
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
                {"Màn hình chính", "Tài khoản cá nhân", "Đổi mật khẩu", "-", "Đăng xuất"},
                {"Quản lý khách hàng"},
                {"Quản lý xe", "Tra cứu xe", "Lịch sử sửa chữa"},
                {"Quản lý dịch vụ", "Thêm dịch vụ", "Sửa dịch vụ", "-", "Lập phiếu tiếp nhận xe", "Lập phiếu sửa chữa / Báo giá", "Cập nhật tình trạng sửa xe"},
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

        // ==================== 2. THANH CÔNG CỤ NHANH ====================
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

        JLabel lblModuleHienTai = new JLabel("Phân hệ Nhân Sự & Công Việc");
        lblModuleHienTai.setFont(new Font("Arial", Font.BOLD, 13));
        lblModuleHienTai.setForeground(new Color(150, 30, 40));

        JPanel panelLabelTieuDe = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 12));
        panelLabelTieuDe.setBackground(Color.WHITE);
        panelLabelTieuDe.add(lblModuleHienTai);

        JPanel panelToolbarWrapper = new JPanel(new BorderLayout());
        panelToolbarWrapper.setBackground(Color.WHITE);
        panelToolbarWrapper.add(panelToolbar, BorderLayout.CENTER);
        panelToolbarWrapper.add(panelLabelTieuDe, BorderLayout.EAST);

        // ==================== 3. NỘI DUNG CHÍNH: KHUNG CARD MỞ RỘNG TOÀN MÀN HÌNH ====================
        JPanel panelCenter = new JPanel(new BorderLayout());
        panelCenter.setBackground(new Color(240, 243, 246));
        panelCenter.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20)); // Lề xung quanh card

        JPanel panelCardChinh = new JPanel(new BorderLayout());
        panelCardChinh.setBackground(Color.WHITE);
        panelCardChinh.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 215, 220), 1),
                BorderFactory.createEmptyBorder(20, 25, 20, 25)
        ));

        // --- 3.1. Tiêu đề chung của Card ---
        JPanel panelCardTop = new JPanel(new BorderLayout());
        panelCardTop.setBackground(Color.WHITE);
        panelCardTop.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        JLabel lblTieuDeCard = new JLabel("QUẢN LÝ NHÂN SỰ & PHÂN CÔNG KỸ THUẬT VIÊN");
        lblTieuDeCard.setFont(new Font("Arial", Font.BOLD, 18));
        lblTieuDeCard.setForeground(new Color(30, 35, 45));

        JLabel lblMoTa = new JLabel("Cập nhật hồ sơ nhân viên và điều phối công việc sửa chữa cho xưởng.");
        lblMoTa.setFont(new Font("Arial", Font.PLAIN, 13));
        lblMoTa.setForeground(Color.GRAY);

        JPanel pnlTitleWrap = new JPanel(new GridLayout(2, 1));
        pnlTitleWrap.setBackground(Color.WHITE);
        pnlTitleWrap.add(lblTieuDeCard);
        pnlTitleWrap.add(lblMoTa);

        panelCardTop.add(pnlTitleWrap, BorderLayout.WEST);

        // --- 3.2. JTabbedPane chứa 2 chức năng ---
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 14));
        tabbedPane.setBackground(new Color(245, 247, 250));

        tabbedPane.addTab("Hồ Sơ Nhân Viên", taoTabQuanLyNhanSu());
        tabbedPane.addTab("Phân Công Kỹ Thuật Viên", taoTabPhanCongKTV());

        // Gắn vào Card Chính
        panelCardChinh.add(panelCardTop, BorderLayout.NORTH);
        panelCardChinh.add(tabbedPane, BorderLayout.CENTER);

        panelCenter.add(panelCardChinh, BorderLayout.CENTER);

        // ==================== 4. THANH TRẠNG THÁI (FOOTER) ====================
        JPanel panelFooter = new JPanel(new BorderLayout());
        panelFooter.setBackground(new Color(245, 247, 250));
        panelFooter.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));

        JLabel lblTrangThai = new JLabel("Đang hoạt động | Kho chính | Quản trị viên");
        lblTrangThai.setFont(new Font("Arial", Font.PLAIN, 12));
        lblTrangThai.setForeground(Color.DARK_GRAY);

        JLabel lblBanQuyen = new JLabel("NIGHT INVADER - HỆ THỐNG QUẢN LÝ GARA");
        lblBanQuyen.setFont(new Font("Arial", Font.PLAIN, 12));
        lblBanQuyen.setForeground(Color.GRAY);

        panelFooter.add(lblTrangThai, BorderLayout.WEST);
        panelFooter.add(lblBanQuyen, BorderLayout.EAST);

        add(panelToolbarWrapper, BorderLayout.NORTH);
        add(panelCenter, BorderLayout.CENTER);
        add(panelFooter, BorderLayout.SOUTH);
    }

    // ==================== TAB 1: QUẢN LÝ NHÂN SỰ ====================
    private JPanel taoTabQuanLyNhanSu() {
        JPanel panelTab = new JPanel(new BorderLayout());
        panelTab.setBackground(Color.WHITE);
        panelTab.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        // 1. Khối nhập liệu
        txtMaNV = new JTextField(); txtMaNV.setEditable(false); txtMaNV.setBackground(new Color(245, 247, 250));
        txtHoTen = new JTextField();
        txtSoDienThoai = new JTextField();
        txtNgayVaoLam = new JTextField();
        cbVaiTro = new JComboBox<>(new String[]{"Tiếp nhận", "Kỹ thuật viên", "Nhân viên Kho", "Thu ngân", "Quản lý"});
        cbVaiTro.setFont(new Font("Arial", Font.PLAIN, 13)); cbVaiTro.setBackground(Color.WHITE);
        cbTrangThai = new JComboBox<>(new String[]{"Đang làm việc", "Đã nghỉ / Khóa"});
        cbTrangThai.setFont(new Font("Arial", Font.PLAIN, 13)); cbTrangThai.setBackground(Color.WHITE);

        JPanel panelGridFields = new JPanel(new GridLayout(2, 3, 30, 15));
        panelGridFields.setBackground(Color.WHITE);
        panelGridFields.setBorder(BorderFactory.createEmptyBorder(10, 5, 15, 5));
        panelGridFields.add(taoOnhapLieu("Mã nhân viên (Tự động)", txtMaNV));
        panelGridFields.add(taoOnhapLieu("Họ và Tên (*)", txtHoTen));
        panelGridFields.add(taoOnhapLieu("Số điện thoại (*)", txtSoDienThoai));
        panelGridFields.add(taoOnhapLieu("Vai trò / Chức vụ", cbVaiTro));
        panelGridFields.add(taoOnhapLieu("Ngày vào làm", txtNgayVaoLam));
        panelGridFields.add(taoOnhapLieu("Trạng thái", cbTrangThai));

        // 2. Thanh bộ lọc & Bảng dữ liệu
        JPanel panelSearchFilter = new JPanel(new BorderLayout());
        panelSearchFilter.setBackground(new Color(245, 247, 250));
        panelSearchFilter.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 225, 230), 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));

        txtTimKiem = new JTextField(" Tìm kiếm nhân viên...");
        txtTimKiem.setFont(new Font("Arial", Font.ITALIC, 13));
        txtTimKiem.setForeground(Color.GRAY);
        txtTimKiem.setBorder(null);
        txtTimKiem.setOpaque(false);

        JComboBox<String> cbLocVaiTro = new JComboBox<>(new String[]{"Tất cả vai trò", "Tiếp nhận", "Kỹ thuật viên", "Nhân viên Kho", "Thu ngân"});
        cbLocVaiTro.setFont(new Font("Arial", Font.PLAIN, 12));
        cbLocVaiTro.setBackground(Color.WHITE);

        ImageIcon iconTim = loadIcon("/img/icon_tim.png", 16, 16);
        if (iconTim != null) {
            panelSearchFilter.add(new JLabel(iconTim), BorderLayout.WEST);
        }
        panelSearchFilter.add(txtTimKiem, BorderLayout.CENTER);
        panelSearchFilter.add(cbLocVaiTro, BorderLayout.EAST);

        String[] columnNames = {"Mã NV", "Họ Tên", "SĐT", "Vai Trò", "Ngày Vào Làm", "Trạng Thái"};
        tableModelNhanVien = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tableNhanVien = new JTable(tableModelNhanVien);

        // Custom giao diện bảng
        tableNhanVien.setRowHeight(32);
        tableNhanVien.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        tableNhanVien.getTableHeader().setBackground(new Color(230, 235, 240));
        tableNhanVien.setFont(new Font("Arial", Font.PLAIN, 13));
        tableNhanVien.setSelectionBackground(new Color(40, 100, 150));
        tableNhanVien.setSelectionForeground(Color.WHITE);
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        tableNhanVien.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);

        JPanel panelTableSection = new JPanel(new BorderLayout(0, 10));
        panelTableSection.setBackground(Color.WHITE);
        panelTableSection.add(panelSearchFilter, BorderLayout.NORTH);
        panelTableSection.add(new JScrollPane(tableNhanVien), BorderLayout.CENTER);

        // 3. Khối nút bấm
        JPanel panelBottomButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelBottomButtons.setBackground(Color.WHITE);
        panelBottomButtons.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        JButton btnLamMoi = taoNutChucNang("Làm mới", Color.WHITE, new Color(50, 55, 65));
        JButton btnKhoa = taoNutChucNang("Khóa tài khoản", Color.WHITE, new Color(210, 30, 30));
        JButton btnCapNhat = taoNutChucNang("Cập nhật", new Color(40, 100, 150), Color.WHITE);
        JButton btnThemMoi = taoNutChucNang("Thêm mới", new Color(25, 135, 84), Color.WHITE);

        panelBottomButtons.add(btnLamMoi);
        panelBottomButtons.add(btnKhoa);
        panelBottomButtons.add(btnCapNhat);
        panelBottomButtons.add(btnThemMoi);

        panelTab.add(panelGridFields, BorderLayout.NORTH);
        panelTab.add(panelTableSection, BorderLayout.CENTER);
        panelTab.add(panelBottomButtons, BorderLayout.SOUTH);

        return panelTab;
    }

    // ==================== TAB 2: PHÂN CÔNG KỸ THUẬT VIÊN ====================
    private JPanel taoTabPhanCongKTV() {
        JPanel panelTab = new JPanel(new BorderLayout());
        panelTab.setBackground(Color.WHITE);
        panelTab.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        // 1. Khối nhập liệu phân công
        cbPhieuSuaChua = new JComboBox<>(new String[]{"[SC00215] Xe Honda Civic - 51G-123.45 (Chờ sửa)", "[SC00216] Xe Mazda CX-5 - 60A-999.99 (Chờ sửa)"});
        cbPhieuSuaChua.setFont(new Font("Arial", Font.BOLD, 13));
        cbPhieuSuaChua.setBackground(Color.WHITE);

        cbKyThuatVien = new JComboBox<>(new String[]{"[NV002] Trần Ngọc Huy (Đang rảnh)", "[NV005] Lê Văn Tám (Đang sửa 1 xe)"});
        cbKyThuatVien.setFont(new Font("Arial", Font.BOLD, 13));
        cbKyThuatVien.setBackground(Color.WHITE);

        txtGhiChuPhanCong = new JTextField("Ưu tiên xử lý lỗi lốc lạnh trước.");

        JPanel panelGridFields = new JPanel(new GridLayout(2, 2, 30, 15));
        panelGridFields.setBackground(Color.WHITE);
        panelGridFields.setBorder(BorderFactory.createEmptyBorder(10, 5, 15, 5));

        panelGridFields.add(taoOnhapLieu("Chọn Phiếu Sửa Chữa (Xe)", cbPhieuSuaChua));
        panelGridFields.add(taoOnhapLieu("Chọn Kỹ Thuật Viên Phụ Trách", cbKyThuatVien));
        panelGridFields.add(taoOnhapLieu("Ghi chú / Nhắc nhở cho KTV", txtGhiChuPhanCong));
        panelGridFields.add(new JLabel());

        // 2. Bảng danh sách đang phân công
        JLabel lblDanhSach = new JLabel("DANH SÁCH XE ĐANG SỬA TẠI XƯỞNG");
        lblDanhSach.setFont(new Font("Arial", Font.BOLD, 14));
        lblDanhSach.setForeground(new Color(210, 30, 30));

        JPanel pnlTitleTable = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlTitleTable.setBackground(Color.WHITE);
        pnlTitleTable.add(lblDanhSach);

        String[] columnNames = {"Mã Phân Công", "Mã Phiếu SC", "Biển Số Xe", "Kỹ Thuật Viên", "Ngày Phân Công", "Tiến Độ"};
        Object[][] data = {
                {"PC001", "SC00212", "51H-555.55", "Trần Ngọc Huy", "12/10/2026", "Đang sửa chữa (50%)"},
                {"PC002", "SC00214", "61A-123.88", "Lê Văn Tám", "12/10/2026", "Chờ phụ tùng"}
        };

        tableModelPhanCong = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tablePhanCong = new JTable(tableModelPhanCong);
        tablePhanCong.setRowHeight(32);
        tablePhanCong.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        tablePhanCong.getTableHeader().setBackground(new Color(230, 235, 240));
        tablePhanCong.setFont(new Font("Arial", Font.PLAIN, 13));
        tablePhanCong.setSelectionBackground(new Color(40, 100, 150));
        tablePhanCong.setSelectionForeground(Color.WHITE);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        tablePhanCong.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);

        JPanel panelTableSection = new JPanel(new BorderLayout(0, 5));
        panelTableSection.setBackground(Color.WHITE);
        panelTableSection.add(pnlTitleTable, BorderLayout.NORTH);
        panelTableSection.add(new JScrollPane(tablePhanCong), BorderLayout.CENTER);

        // 3. Khối nút bấm phân công
        JPanel panelBottomButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelBottomButtons.setBackground(Color.WHITE);
        panelBottomButtons.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        JButton btnHuyPC = taoNutChucNang("Hủy phân công", Color.WHITE, new Color(210, 30, 30));
        JButton btnCapNhatPC = taoNutChucNang("Cập nhật lại", new Color(40, 100, 150), Color.WHITE);
        JButton btnPhanCong = taoNutChucNang("Xác nhận Phân Công", new Color(25, 135, 84), Color.WHITE);

        btnPhanCong.addActionListener(e -> JOptionPane.showMessageDialog(this, "Đã giao xe cho Kỹ thuật viên thành công!", "Phân công", JOptionPane.INFORMATION_MESSAGE));

        panelBottomButtons.add(btnHuyPC);
        panelBottomButtons.add(btnCapNhatPC);
        panelBottomButtons.add(btnPhanCong);

        panelTab.add(panelGridFields, BorderLayout.NORTH);
        panelTab.add(panelTableSection, BorderLayout.CENTER);
        panelTab.add(panelBottomButtons, BorderLayout.SOUTH);

        return panelTab;
    }


    // ==================== HÀM TIỆN ÍCH ====================
    private JPanel taoOnhapLieu(String labelText, JComponent inputComponent) {
        JPanel panel = new JPanel(new BorderLayout(0, 5));
        panel.setBackground(Color.WHITE);

        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("Arial", Font.BOLD, 12));
        lbl.setForeground(new Color(80, 85, 95));

        inputComponent.setFont(new Font("Arial", Font.PLAIN, 14));
        inputComponent.setPreferredSize(new Dimension(250, 35));

        if (inputComponent instanceof JTextField) {
            inputComponent.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(200, 205, 210), 1),
                    BorderFactory.createEmptyBorder(4, 10, 4, 10)
            ));
        }

        panel.add(lbl, BorderLayout.NORTH);
        panel.add(inputComponent, BorderLayout.CENTER);
        return panel;
    }

    private JButton taoNutChucNang(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 13));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        if (bg.equals(Color.WHITE)) {
            btn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(fg, 1),
                    BorderFactory.createEmptyBorder(8, 20, 8, 20)
            ));
        } else {
            btn.setOpaque(true);
            btn.setBorderPainted(false);
            btn.setBorder(BorderFactory.createEmptyBorder(9, 20, 9, 20));
        }
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
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
                    if (itemName.equals("Quản lý nhân sự")) {
                        tabbedPane.setSelectedIndex(0);
                    } else if (itemName.equals("Phân công kỹ thuật viên")) {
                        tabbedPane.setSelectedIndex(1);
                    }
                    else if (itemName.equals("Thống kê doanh thu")) {
                        new GaraThongKe(0).setVisible(true);
                        this.dispose();
                    } else if (itemName.equals("Thống kê tồn kho")) {
                        new GaraThongKe(1).setVisible(true);
                        this.dispose();
                    } else if (itemName.equals("Phụ tùng bán chạy")) {
                        new GaraThongKe(2).setVisible(true);
                        this.dispose();
                    } else if (itemName.equals("Phụ tùng bán chậm")) {
                        new GaraThongKe(3).setVisible(true);
                        this.dispose();
                    }
                    // CÁC MÀN HÌNH KHÁC
                    else if (itemName.equals("Màn hình chính")) {
                        new GaraManHinhChinh().setVisible(true);
                        this.dispose();
                    } else if (itemName.equals("Tài khoản cá nhân")) {
                        new GaraTaiKhoanCaNhan().setVisible(true);
                        this.dispose();
                    } else if (itemName.equals("Đăng xuất")) {
                        xuLyDangXuat();
                    } else if (itemName.equals("Quản lý khách hàng")) {
                        new KhachHangFrame().setVisible(true);
                        this.dispose();
                    } else if (itemName.equals("Lịch sử sửa chữa")) {
                        new LichSuSuaChuaFrame().setVisible(true);
                        this.dispose();
                    } else if (itemName.equals("Quản lý xe") || itemName.equals("Tra cứu xe")) {
                        new QuanLyXeFrame().setVisible(true);
                        this.dispose();
                    } else if (itemName.equals("Thêm dịch vụ")) {

                    } else if (itemName.equals("Quản lý dịch vụ")) {

                    } else if (itemName.equals("Lập phiếu tiếp nhận xe")) {

                    } else if (itemName.equals("")) {

                    } else if (itemName.equals("")) {

                    } else if (itemName.equals("")) {

                    } else if (itemName.equals("")) {

                    } else if (itemName.equals("")) {

                    } else if (itemName.equals("")) {

                    } else if (itemName.equals("")) {

                    } else if (itemName.equals("")) {

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
        SwingUtilities.invokeLater(() -> new GaraNhanVien().setVisible(true));
    }
}