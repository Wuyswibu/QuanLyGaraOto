package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import connectDB.ConnectDB;
import dao.ChiTietPhieuNhapDao;
import dao.NhanVienDao;
import dao.PhieuNhapKhoDao;
import dao.PhuTungDao;
import entity.ChiTietPhieuNhap;
import entity.NhanVien;
import entity.PhieuNhapKho;
import entity.PhuTung;

public class GaraLapPhieuNhapKho extends JFrame {

    // Các trường thông tin trên Form Lập Phiếu Nhập Kho
    private JTextField txtMaPhieuNhap;
    private JTextField txtNgayNhap;
    private JComboBox<String> cboTrangThai;
    private JComboBox<String> cboPhuTung;
    private JTextField txtSoLuong;
    private JTextField txtDonGia;
    private JComboBox<String> cboNhanVienKho;
    private JTextField txtGhiChu;
    private JTextField txtTongGiaTri;

    // Thanh tìm kiếm, lọc và Bảng danh sách Phiếu Nhập Kho
    private JTextField txtTimKiemBang;
    private JComboBox<String> cboLocTrangThai;
    private JTable tablePhieuNhap;
    private DefaultTableModel tableModel;

    // Các lớp DAO
    private PhieuNhapKhoDao phieuNhapKhoDao;
    private ChiTietPhieuNhapDao ctPhieuNhapDao;
    private PhuTungDao phuTungDao;
    private NhanVienDao nhanVienDao;

    // Bộ nhớ đệm Phụ tùng để tra cứu nhanh tên và giá
    private Map<String, PhuTung> mapPhuTung = new HashMap<>();
    private boolean dangCapNhatCombo = false;

    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private final String PLACEHOLDER_TIM_KIEM = "Tìm kiếm theo mã phiếu nhập hoặc ghi chú (Nhấn Enter)...";
	private JTable tablePhuTung;

    public GaraLapPhieuNhapKho() {
        setTitle("Hệ thống Quản lý Gara Ô Tô - Night Invader");
        setSize(1340, 840);
        setLocationRelativeTo(null);
        setResizable(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        ketNoiCSDL();
        khoiTaoGiaoDien();

        // 1. Nạp dữ liệu Phụ tùng, Nhân viên, Trạng thái vào ComboBox
        napDuLieuComboBox();
        // 2. Tải toàn bộ danh sách Phiếu nhập kho đã lập lên JTable
        taiDanhSachPhieuNhapLenBang("Tất cả");
        // 3. Khởi tạo form ở chế độ lập phiếu nhập mới
        lamMoiFormLapPhieu();
    }

    private void ketNoiCSDL() {
        try {
            ConnectDB.getInstance().connect();
            phieuNhapKhoDao = new PhieuNhapKhoDao();
            ctPhieuNhapDao = new ChiTietPhieuNhapDao();
            phuTungDao = new PhuTungDao();
            nhanVienDao = new NhanVienDao();
            System.out.println("Kết nối CSDL thành công!");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi kết nối CSDL: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void khoiTaoGiaoDien() {
        setLayout(new BorderLayout());

        // ==================== 1. THANH MENU BAR (GIỮ NGUYÊN CODE CŨ) ====================
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
            if (tenCacMenu[i].contains("Kho hàng")) {
                menu.setForeground(new Color(210, 30, 30));
                menu.setOpaque(true);
                menu.setBackground(new Color(230, 235, 240));
            }
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

        // ==================== 2. THANH CÔNG CỤ NHANH (GIỮ NGUYÊN CODE CŨ) ====================
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

        // ==================== 3. NỘI DUNG CHÍNH: FORM LẬP PHIẾU NHẬP KHO ====================
        JPanel panelCenter = new JPanel(new BorderLayout());
        panelCenter.setBackground(new Color(225, 230, 235));
        panelCenter.setBorder(BorderFactory.createEmptyBorder(20, 70, 20, 70));

        JPanel panelCardChinh = new JPanel(new BorderLayout());
        panelCardChinh.setBackground(Color.WHITE);
        panelCardChinh.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 215, 220), 1),
                BorderFactory.createEmptyBorder(20, 30, 15, 30)
        ));

        // --- 3.1. Tiêu đề card ---
        JPanel panelCardTop = new JPanel(new BorderLayout());
        panelCardTop.setBackground(Color.WHITE);

        JLabel lblTieuDeCard = new JLabel("LẬP PHIẾU NHẬP KHO");
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

        // --- 3.2. Form thông tin nhập kho (Bám sát Entity PhieuNhapKho & ChiTietPhieuNhap) ---
        JLabel lblMoTa = new JLabel("Điền thông tin nhập phụ tùng bên dưới (Ngày nhập tự động ghi nhận khi lưu) hoặc chọn phiếu dưới bảng để xem/thêm mặt hàng.");
        lblMoTa.setFont(new Font("Arial", Font.PLAIN, 13));
        lblMoTa.setForeground(Color.GRAY);
        lblMoTa.setBorder(BorderFactory.createEmptyBorder(8, 0, 12, 0));

        txtMaPhieuNhap = new JTextField();
        txtMaPhieuNhap.setFont(new Font("Arial", Font.BOLD, 13));

        txtNgayNhap = new JTextField("Tự động ghi nhận khi lưu");
        txtNgayNhap.setEditable(false);
        txtNgayNhap.setBackground(new Color(245, 247, 250));
        txtNgayNhap.setForeground(new Color(90, 95, 105));

        cboTrangThai = new JComboBox<>();
        cboPhuTung = new JComboBox<>();
        txtSoLuong = new JTextField("1");
        txtDonGia = new JTextField("0");
        cboNhanVienKho = new JComboBox<>();
        txtGhiChu = new JTextField();

        txtTongGiaTri = new JTextField("0 đ");
        txtTongGiaTri.setEditable(false);
        txtTongGiaTri.setBackground(new Color(245, 247, 250));
        txtTongGiaTri.setForeground(new Color(180, 30, 40));
        txtTongGiaTri.setFont(new Font("Arial", Font.BOLD, 14));

        // Khi chọn Phụ tùng -> Tự động gợi ý đơn giá từ bảng PhuTung
        cboPhuTung.addActionListener(e -> {
            if (!dangCapNhatCombo && cboPhuTung.getSelectedItem() != null) {
                String maPT = layMaTuCombo(cboPhuTung);
                PhuTung pt = mapPhuTung.get(maPT);
                if (pt != null && pt.getDonGia() != null) {
                    txtDonGia.setText(pt.getDonGia().stripTrailingZeros().toPlainString());
                    tinhTamThanhTienTrenForm();
                }
            }
        });

        KeyAdapter tinhTienListener = new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                tinhTamThanhTienTrenForm();
            }
        };
        txtSoLuong.addKeyListener(tinhTienListener);
        txtDonGia.addKeyListener(tinhTienListener);

        JPanel panelGridFields = new JPanel(new GridLayout(3, 3, 25, 10));
        panelGridFields.setBackground(Color.WHITE);
        panelGridFields.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));

        panelGridFields.add(taoOnhapLieu("Mã phiếu nhập", txtMaPhieuNhap));
        panelGridFields.add(taoOnhapLieu("Ngày nhập (Tự động)", txtNgayNhap));
        panelGridFields.add(taoOCombo("Trạng thái", cboTrangThai));
        panelGridFields.add(taoOCombo("Phụ tùng nhập", cboPhuTung));
        panelGridFields.add(taoOnhapLieu("Số lượng nhập", txtSoLuong));
        panelGridFields.add(taoOnhapLieu("Đơn giá nhập (VNĐ)", txtDonGia));
        panelGridFields.add(taoOCombo("Nhân viên kho", cboNhanVienKho));
        panelGridFields.add(taoOnhapLieu("Ghi chú / Nhà cung cấp", txtGhiChu));
        panelGridFields.add(taoOnhapLieu("Tổng giá trị phiếu nhập", txtTongGiaTri));

        JPanel panelFieldsContainer = new JPanel(new BorderLayout());
        panelFieldsContainer.setBackground(Color.WHITE);
        panelFieldsContainer.add(lblMoTa, BorderLayout.NORTH);
        panelFieldsContainer.add(panelGridFields, BorderLayout.CENTER);

        // --- 3.3. Thanh tìm kiếm và lọc trạng thái ---
        JPanel panelSearchFilter = new JPanel(new BorderLayout(15, 0));
        panelSearchFilter.setBackground(new Color(240, 243, 246));
        panelSearchFilter.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));

        txtTimKiemBang = new JTextField(PLACEHOLDER_TIM_KIEM);
        txtTimKiemBang.setFont(new Font("Arial", Font.PLAIN, 12));
        txtTimKiemBang.setForeground(Color.GRAY);
        txtTimKiemBang.setBorder(null);
        txtTimKiemBang.setOpaque(false);

        txtTimKiemBang.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (txtTimKiemBang.getText().equals(PLACEHOLDER_TIM_KIEM)) {
                    txtTimKiemBang.setText("");
                    txtTimKiemBang.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (txtTimKiemBang.getText().trim().isEmpty()) {
                    txtTimKiemBang.setText(PLACEHOLDER_TIM_KIEM);
                    txtTimKiemBang.setForeground(Color.GRAY);
                }
            }
        });

        txtTimKiemBang.addActionListener(e -> {
            String tuKhoa = txtTimKiemBang.getText().trim();
            if (!tuKhoa.isEmpty() && !tuKhoa.equals(PLACEHOLDER_TIM_KIEM)) {
                timKiemPhieuNhapLenBang(tuKhoa);
            } else {
                taiDanhSachPhieuNhapLenBang(cboLocTrangThai.getSelectedItem().toString());
            }
        });

        JPanel pnlFilterRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        pnlFilterRight.setOpaque(false);
        JLabel lblTrangThai = new JLabel("Trạng thái:");
        lblTrangThai.setFont(new Font("Arial", Font.PLAIN, 12));

        cboLocTrangThai = new JComboBox<>(new String[]{"Tất cả"});
        cboLocTrangThai.setFont(new Font("Arial", Font.PLAIN, 12));
        cboLocTrangThai.addActionListener(e -> {
            if (!dangCapNhatCombo && cboLocTrangThai.getSelectedItem() != null) {
                taiDanhSachPhieuNhapLenBang(cboLocTrangThai.getSelectedItem().toString());
            }
        });

        pnlFilterRight.add(lblTrangThai);
        pnlFilterRight.add(cboLocTrangThai);

        panelSearchFilter.add(txtTimKiemBang, BorderLayout.CENTER);
        panelSearchFilter.add(pnlFilterRight, BorderLayout.EAST);

        // --- 3.4. Bảng Danh Sách Toàn Bộ Phiếu Nhập Kho Đã Lập ---
        String[] columnNames = {
                "Mã Phiếu Nhập", "Ngày Nhập", "Phụ Tùng Nhập",
                "Tổng SL", "Tổng Giá Trị", "Ghi Chú / Nhà Cung Cấp", "Trạng Thái"
        };

        tableModel = new DefaultTableModel(null, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablePhuTung = new JTable(tableModel);
        tablePhieuNhap = tablePhuTung;
        tablePhieuNhap.setRowHeight(34);
        tablePhieuNhap.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tablePhieuNhap.getTableHeader().setBackground(new Color(240, 243, 246));
        tablePhieuNhap.setFont(new Font("Arial", Font.PLAIN, 12));
        tablePhieuNhap.setSelectionBackground(new Color(40, 100, 150));
        tablePhieuNhap.setSelectionForeground(Color.WHITE);
        tablePhieuNhap.setShowGrid(false);
        tablePhieuNhap.setIntercellSpacing(new Dimension(0, 0));

        // Khi click vào 1 dòng Phiếu nhập dưới bảng -> Đổ thông tin lên Form phía trên
        tablePhieuNhap.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tablePhieuNhap.getSelectedRow();
                if (row >= 0) {
                    String maPN = tableModel.getValueAt(row, 0).toString();
                    hienThiPhieuNhapLenForm(maPN);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(tablePhieuNhap);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 233, 236)));

        JPanel panelTableSection = new JPanel(new BorderLayout());
        panelTableSection.setBackground(Color.WHITE);
        panelTableSection.add(panelSearchFilter, BorderLayout.NORTH);
        panelTableSection.add(scrollPane, BorderLayout.CENTER);

        // --- 3.5. Khu vực nút bấm dưới cùng ---
        JPanel panelBottomButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        panelBottomButtons.setBackground(Color.WHITE);

        JButton btnTaoMoi = taoNutTrang("Tạo phiếu mới");
        JButton btnXemChiTiet = taoNutTrang("Xem chi tiết phụ tùng");
        JButton btnThemDong = taoNutTrang("Thêm phụ tùng vào phiếu");
        JButton btnXacNhan = taoNutDo("Xác nhận nhập kho");

        btnTaoMoi.addActionListener(e -> {
            tablePhieuNhap.clearSelection();
            lamMoiFormLapPhieu();
        });

        btnXemChiTiet.addActionListener(e -> xemChiTietPhieuNhapPopup());
        btnThemDong.addActionListener(e -> luuHoacThemPhuTungVaoPhieu(false));
        btnXacNhan.addActionListener(e -> luuHoacThemPhuTungVaoPhieu(true));

        panelBottomButtons.add(btnTaoMoi);
        panelBottomButtons.add(btnXemChiTiet);
        panelBottomButtons.add(btnThemDong);
        panelBottomButtons.add(btnXacNhan);

        // --- Gắn các thành phần vào Card Chính ---
        panelCardChinh.add(panelCardTop, BorderLayout.NORTH);

        JPanel panelCardBody = new JPanel(new BorderLayout(0, 8));
        panelCardBody.setBackground(Color.WHITE);
        panelCardBody.add(panelFieldsContainer, BorderLayout.NORTH);
        panelCardBody.add(panelTableSection, BorderLayout.CENTER);
        panelCardBody.add(panelBottomButtons, BorderLayout.SOUTH);

        panelCardChinh.add(panelCardBody, BorderLayout.CENTER);
        panelCenter.add(panelCardChinh, BorderLayout.CENTER);

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

    // ==================== CÁC HÀM XỬ LÝ DATABASE & NGHIỆP VỤ ====================

    private void napDuLieuComboBox() {
        if (phieuNhapKhoDao == null) return;
        try {
            dangCapNhatCombo = true;
            cboPhuTung.removeAllItems();
            cboNhanVienKho.removeAllItems();
            cboTrangThai.removeAllItems();
            cboLocTrangThai.removeAllItems();
            cboLocTrangThai.addItem("Tất cả");

            mapPhuTung.clear();

            // 1. Nạp danh sách Phụ tùng
            for (PhuTung pt : phuTungDao.layDanhSachPhuTung()) {
                mapPhuTung.put(pt.getMaPhuTung(), pt);
                cboPhuTung.addItem(pt.getMaPhuTung() + " - " + pt.getTenPhuTung() + " (Tồn: " + pt.getSoLuongTon() + ")");
            }

            // 2. Nạp danh sách Nhân viên
            for (NhanVien nv : nhanVienDao.layDanhSachNhanVien()) {
                cboNhanVienKho.addItem(nv.getMaNhanVien() + " - " + nv.getHoTen());
            }

            // 3. Nạp các trạng thái thực tế từ bảng PhieuNhapKho để đảm bảo khớp CHECK constraint
            Set<String> dsTrangThai = new LinkedHashSet<>();
            String sqlTT = "SELECT DISTINCT trangThai FROM dbo.PhieuNhapKho WHERE trangThai IS NOT NULL";
            try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sqlTT);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    dsTrangThai.add(rs.getString("trangThai"));
                }
            }
            if (dsTrangThai.isEmpty()) {
                dsTrangThai.add("Đã nhập kho");
                dsTrangThai.add("Chờ xử lý");
                dsTrangThai.add("Đã hủy");
            }

            for (String tt : dsTrangThai) {
                cboTrangThai.addItem(tt);
                cboLocTrangThai.addItem(tt);
            }

            dangCapNhatCombo = false;
        } catch (SQLException e) {
            dangCapNhatCombo = false;
            e.printStackTrace();
        }
    }

    /**
     * Tải toàn bộ danh sách Phiếu nhập kho đã lập lên JTable
     */
    private void taiDanhSachPhieuNhapLenBang(String trangThaiLoc) {
        if (phieuNhapKhoDao == null) return;
        try {
            ArrayList<PhieuNhapKho> dsPhieu;
            if (trangThaiLoc == null || trangThaiLoc.equals("Tất cả")) {
                dsPhieu = phieuNhapKhoDao.layDanhSachPhieuNhap();
            } else {
                dsPhieu = phieuNhapKhoDao.timTheoTrangThai(trangThaiLoc);
            }
            doDuLieuPhieuNhapVaoBang(dsPhieu);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi tải danh sách phiếu nhập: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void timKiemPhieuNhapLenBang(String tuKhoa) {
        if (phieuNhapKhoDao == null) return;
        try {
            ArrayList<PhieuNhapKho> dsPhieu = phieuNhapKhoDao.timKiem(tuKhoa);
            doDuLieuPhieuNhapVaoBang(dsPhieu);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void doDuLieuPhieuNhapVaoBang(ArrayList<PhieuNhapKho> dsPhieu) throws SQLException {
        tableModel.setRowCount(0);

        for (PhieuNhapKho pnk : dsPhieu) {
            ArrayList<ChiTietPhieuNhap> dsCT = ctPhieuNhapDao.timTheoMaPhieuNhap(pnk.getMaPhieuNhap());

            int tongSL = 0;
            BigDecimal tongTien = BigDecimal.ZERO;
            StringBuilder tómTatPhuTung = new StringBuilder();

            for (ChiTietPhieuNhap ct : dsCT) {
                tongSL += ct.getSoLuong();
                BigDecimal thanhTien = ct.getDonGia().multiply(new BigDecimal(ct.getSoLuong()));
                tongTien = tongTien.add(thanhTien);

                PhuTung pt = mapPhuTung.get(ct.getMaPhuTung());
                String tenPT = pt != null ? pt.getTenPhuTung() : ct.getMaPhuTung();
                if (tómTatPhuTung.length() > 0) tómTatPhuTung.append(", ");
                tómTatPhuTung.append(tenPT).append(" (x").append(ct.getSoLuong()).append(")");
            }

            if (tómTatPhuTung.length() == 0) {
                tómTatPhuTung.append("(Chưa có chi tiết)");
            }

            String ngayNhapStr = pnk.getNgayNhap() != null ? pnk.getNgayNhap().format(dtf) : "";

            tableModel.addRow(new Object[]{
                    pnk.getMaPhieuNhap(),
                    ngayNhapStr,
                    tómTatPhuTung.toString(),
                    tongSL,
                    String.format("%,.0f đ", tongTien),
                    pnk.getGhiChu() != null ? pnk.getGhiChu() : "",
                    pnk.getTrangThai()
            });
        }
    }

    /**
     * Khởi tạo form trống với Mã phiếu nhập tự sinh để lập phiếu mới
     */
    private void lamMoiFormLapPhieu() {
        if (phieuNhapKhoDao == null) return;
        try {
            String maMoi = phieuNhapKhoDao.taoMaPhieuNhap();
            txtMaPhieuNhap.setText(maMoi);
            txtMaPhieuNhap.setEditable(true);
            txtNgayNhap.setText("Tự động ghi nhận khi lưu");
            txtSoLuong.setText("1");
            txtGhiChu.setText("");

            if (cboTrangThai.getItemCount() > 0) {
                cboTrangThai.setSelectedIndex(0);
            }
            if (cboPhuTung.getItemCount() > 0) {
                cboPhuTung.setSelectedIndex(0);
                String maPT = layMaTuCombo(cboPhuTung);
                PhuTung pt = mapPhuTung.get(maPT);
                if (pt != null && pt.getDonGia() != null) {
                    txtDonGia.setText(pt.getDonGia().stripTrailingZeros().toPlainString());
                }
            }
            tinhTamThanhTienTrenForm();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Khi click vào 1 dòng Phiếu nhập trên JTable -> Đổ thông tin lên Form phía trên
     */
    private void hienThiPhieuNhapLenForm(String maPhieuNhap) {
        try {
            PhieuNhapKho pnk = phieuNhapKhoDao.timTheoMa(maPhieuNhap);
            if (pnk == null) return;

            dangCapNhatCombo = true;
            txtMaPhieuNhap.setText(pnk.getMaPhieuNhap());
            txtMaPhieuNhap.setEditable(false);
            txtNgayNhap.setText(pnk.getNgayNhap() != null ? pnk.getNgayNhap().format(dtf) : "");
            cboTrangThai.setSelectedItem(pnk.getTrangThai());
            txtGhiChu.setText(pnk.getGhiChu() != null ? pnk.getGhiChu() : "");

            ArrayList<ChiTietPhieuNhap> dsCT = ctPhieuNhapDao.timTheoMaPhieuNhap(maPhieuNhap);
            BigDecimal tongGiaTri = BigDecimal.ZERO;

            for (ChiTietPhieuNhap ct : dsCT) {
                BigDecimal thanhTien = ct.getDonGia().multiply(new BigDecimal(ct.getSoLuong()));
                tongGiaTri = tongGiaTri.add(thanhTien);
            }

            // Hiển thị mặt hàng đầu tiên của phiếu lên ô Phụ tùng, Số lượng, Đơn giá
            if (!dsCT.isEmpty()) {
                ChiTietPhieuNhap ctDau = dsCT.get(0);
                chonItemComboTheoMa(cboPhuTung, ctDau.getMaPhuTung());
                txtSoLuong.setText(String.valueOf(ctDau.getSoLuong()));
                txtDonGia.setText(ctDau.getDonGia().stripTrailingZeros().toPlainString());
            }

            txtTongGiaTri.setText(String.format("%,.0f đ", tongGiaTri));
            dangCapNhatCombo = false;

        } catch (SQLException e) {
            dangCapNhatCombo = false;
            e.printStackTrace();
        }
    }

    /**
     * Lưu phiếu nhập kho mới hoặc thêm phụ tùng vào phiếu nhập đã chọn
     */
    private void luuHoacThemPhuTungVaoPhieu(boolean lamMoiSauKhiLuu) {
        try {
            String maPN = txtMaPhieuNhap.getText().trim();
            String trangThai = cboTrangThai.getSelectedItem() != null ? cboTrangThai.getSelectedItem().toString() : "Đã nhập kho";
            String maPT = layMaTuCombo(cboPhuTung);
            int soLuong = Integer.parseInt(txtSoLuong.getText().trim());
            BigDecimal donGia = parseSoTien(txtDonGia.getText().trim());

            String ghiChu = txtGhiChu.getText().trim();
            String tenNV = cboNhanVienKho.getSelectedItem() != null ? cboNhanVienKho.getSelectedItem().toString() : "";
            if (ghiChu.isEmpty() && !tenNV.isEmpty()) {
                ghiChu = "NV nhập: " + tenNV;
            }

            PhieuNhapKho pnkTonTai = phieuNhapKhoDao.timTheoMa(maPN);

            if (pnkTonTai == null) {
                // 1. Tạo mới Phiếu nhập kho với thời gian thực ngay lúc nhấn Lưu
                LocalDateTime ngayNhapTuDong = LocalDateTime.now();
                txtNgayNhap.setText(ngayNhapTuDong.format(dtf));

                PhieuNhapKho pnkMoi = new PhieuNhapKho(maPN, ngayNhapTuDong, trangThai, ghiChu.isEmpty() ? null : ghiChu);
                if (!phieuNhapKhoDao.themPhieuNhap(pnkMoi)) {
                    JOptionPane.showMessageDialog(this, "Không thể tạo phiếu nhập kho mới!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // 2. Thêm Chi tiết phiếu nhập & Tăng tồn kho Phụ tùng
                ChiTietPhieuNhap ctMoi = new ChiTietPhieuNhap(maPN, maPT, soLuong, donGia);
                ctPhieuNhapDao.themChiTiet(ctMoi);
                phuTungDao.tangTonKho(maPT, soLuong);

                JOptionPane.showMessageDialog(this,
                        "Đã lập phiếu nhập kho " + maPN + " và cộng +" + soLuong + " vào tồn kho phụ tùng " + maPT + "!\n"
                                + "Thời gian ghi nhận: " + ngayNhapTuDong.format(dtf));
            } else {
                // Cập nhật thông tin chung của Phiếu nhập kho đã có
                pnkTonTai.setTrangThai(trangThai);
                pnkTonTai.setGhiChu(ghiChu.isEmpty() ? null : ghiChu);
                phieuNhapKhoDao.suaPhieuNhap(pnkTonTai);

                // Kiểm tra phụ tùng đã có trong phiếu nhập này chưa
                ChiTietPhieuNhap ctCu = ctPhieuNhapDao.timTheoKhoa(maPN, maPT);
                if (ctCu == null) {
                    ChiTietPhieuNhap ctMoi = new ChiTietPhieuNhap(maPN, maPT, soLuong, donGia);
                    ctPhieuNhapDao.themChiTiet(ctMoi);
                    phuTungDao.tangTonKho(maPT, soLuong);
                    JOptionPane.showMessageDialog(this,
                            "Đã thêm phụ tùng " + maPT + " (SL: " + soLuong + ") vào phiếu " + maPN + " và cập nhật tồn kho!");
                } else {
                    int chenhLech = soLuong - ctCu.getSoLuong();
                    ChiTietPhieuNhap ctSua = new ChiTietPhieuNhap(maPN, maPT, soLuong, donGia);
                    ctPhieuNhapDao.suaChiTiet(ctSua);

                    if (chenhLech > 0) {
                        phuTungDao.tangTonKho(maPT, chenhLech);
                    } else if (chenhLech < 0) {
                        phuTungDao.giamTonKho(maPT, Math.abs(chenhLech));
                    }
                    JOptionPane.showMessageDialog(this, "Đã cập nhật phiếu nhập kho " + maPN + " thành công!");
                }
            }

            // Tải lại danh sách Phụ tùng (để cập nhật số tồn mới) và bảng Phiếu nhập
            napDuLieuComboBox();
            taiDanhSachPhieuNhapLenBang(cboLocTrangThai.getSelectedItem().toString());

            if (lamMoiSauKhiLuu) {
                lamMoiFormLapPhieu();
            } else {
                hienThiPhieuNhapLenForm(maPN);
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Số lượng và đơn giá phải là số hợp lệ!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi lưu phiếu nhập kho: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Xem bảng chi tiết các phụ tùng trong một Phiếu nhập kho
     */
    private void xemChiTietPhieuNhapPopup() {
        String maPN = txtMaPhieuNhap.getText().trim();
        try {
            PhieuNhapKho pnk = phieuNhapKhoDao.timTheoMa(maPN);
            if (pnk == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một phiếu nhập đã lưu dưới bảng để xem chi tiết!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            ArrayList<ChiTietPhieuNhap> dsCT = ctPhieuNhapDao.timTheoMaPhieuNhap(maPN);
            String[] cols = {"Mã Phụ Tùng", "Tên Phụ Tùng", "Đơn Vị Tính", "Số Lượng Nhập", "Đơn Giá Nhập", "Thành Tiền"};
            DefaultTableModel modelCT = new DefaultTableModel(null, cols) {
                @Override
                public boolean isCellEditable(int r, int c) {
                    return false;
                }
            };

            for (ChiTietPhieuNhap ct : dsCT) {
                PhuTung pt = mapPhuTung.get(ct.getMaPhuTung());
                String tenPT = pt != null ? pt.getTenPhuTung() : ct.getMaPhuTung();
                String dvt = pt != null ? pt.getDonViTinh() : "Cái";
                BigDecimal thanhTien = ct.getDonGia().multiply(new BigDecimal(ct.getSoLuong()));

                modelCT.addRow(new Object[]{
                        ct.getMaPhuTung(),
                        tenPT,
                        dvt,
                        ct.getSoLuong(),
                        String.format("%,.0f đ", ct.getDonGia()),
                        String.format("%,.0f đ", thanhTien)
                });
            }

            JTable tblCT = new JTable(modelCT);
            tblCT.setRowHeight(28);
            tblCT.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
            tblCT.setFont(new Font("Arial", Font.PLAIN, 12));

            JScrollPane sp = new JScrollPane(tblCT);
            sp.setPreferredSize(new Dimension(680, 240));

            JOptionPane.showMessageDialog(this, sp, "Chi tiết phiếu nhập kho: " + maPN, JOptionPane.PLAIN_MESSAGE);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void tinhTamThanhTienTrenForm() {
        try {
            int sl = Integer.parseInt(txtSoLuong.getText().trim());
            BigDecimal dg = parseSoTien(txtDonGia.getText().trim());
            if (sl < 0) sl = 0;
            BigDecimal thanhTien = dg.multiply(new BigDecimal(sl));
            txtTongGiaTri.setText(String.format("%,.0f đ", thanhTien));
        } catch (Exception e) {
            txtTongGiaTri.setText("0 đ");
        }
    }

    private BigDecimal parseSoTien(String text) {
        String clean = text.replaceAll("[^0-9.]", "");
        if (clean.isEmpty()) return BigDecimal.ZERO;
        return new BigDecimal(clean);
    }

    private String layMaTuCombo(JComboBox<String> combo) {
        if (combo.getSelectedItem() == null) return "";
        return combo.getSelectedItem().toString().split(" - ")[0].trim();
    }

    private void chonItemComboTheoMa(JComboBox<String> combo, String maCanChon) {
        if (maCanChon == null) return;
        for (int i = 0; i < combo.getItemCount(); i++) {
            if (combo.getItemAt(i).startsWith(maCanChon + " -") || combo.getItemAt(i).equals(maCanChon)) {
                combo.setSelectedIndex(i);
                return;
            }
        }
    }

    // ==================== CÁC HÀM TIỆN ÍCH GIAO DIỆN ====================

    private JPanel taoOnhapLieu(String labelText, JTextField textField) {
        JPanel panel = new JPanel(new BorderLayout(0, 4));
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

    private JPanel taoOCombo(String labelText, JComboBox<String> comboBox) {
        JPanel panel = new JPanel(new BorderLayout(0, 4));
        panel.setBackground(Color.WHITE);

        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("Arial", Font.PLAIN, 12));
        lbl.setForeground(new Color(80, 85, 95));

        comboBox.setFont(new Font("Arial", Font.PLAIN, 13));
        comboBox.setBackground(Color.WHITE);
        comboBox.setPreferredSize(new Dimension(0, 32));

        panel.add(lbl, BorderLayout.NORTH);
        panel.add(comboBox, BorderLayout.CENTER);
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
        SwingUtilities.invokeLater(() -> new GaraLapPhieuNhapKho().setVisible(true));
    }
}