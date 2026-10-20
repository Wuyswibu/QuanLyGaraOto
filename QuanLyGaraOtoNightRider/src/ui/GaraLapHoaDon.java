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
import java.util.Map;

import connectDB.ConnectDB;
import dao.ChiTietHoaDonDao;
import dao.HoaDonDao;
import dao.KhachHangDao;
import dao.NhanVienDao;
import dao.PhieuSuaChuaDao;
import dao.XeDao;
import entity.ChiTietHoaDon;
import entity.HoaDon;
import entity.KhachHang;
import entity.NhanVien;
import entity.PhieuSuaChua;
import entity.Xe;

public class GaraLapHoaDon extends JFrame {

    // Các ô nhập liệu để Lập Hóa Đơn Mới / Cập Nhật Hóa Đơn
    private JTextField txtMaHoaDon;
    private JComboBox<String> cboPhieuSuaChua;
    private JComboBox<String> cboKhachHang;
    private JComboBox<String> cboPhuongTien;
    private JComboBox<String> cboNhanVien;
    private JTextField txtNgayLap;
    private JTextField txtVAT;
    private JComboBox<String> cboTrangThai;
    private JTextField txtTongTien;

    // Tìm kiếm & Bảng danh sách Hóa Đơn
    private JTextField txtTimKiemBang;
    private JComboBox<String> cboLocTrangThai;
    private JTable tableHoaDon;
    private DefaultTableModel tableModel;

    // Các lớp DAO
    private HoaDonDao hoaDonDao;
    private ChiTietHoaDonDao ctHoaDonDao;
    private PhieuSuaChuaDao phieuSuaChuaDao;
    private KhachHangDao khachHangDao;
    private NhanVienDao nhanVienDao;
    private XeDao xeDao;

    // Bộ nhớ đệm hỗ trợ hiển thị nhanh tên lên JTable
    private Map<String, String> mapTenKhachHang = new HashMap<>();
    private Map<String, String> mapBienSoXe = new HashMap<>();
    private Map<String, String> mapTenNhanVien = new HashMap<>();

    private boolean dangCapNhatCombo = false;
    private BigDecimal tamTinhHienTai = BigDecimal.ZERO;
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public GaraLapHoaDon() {
        setTitle("Hệ thống Quản lý Gara Ô Tô - Night Invader");
        setSize(1340, 840);
        setLocationRelativeTo(null);
        setResizable(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        ketNoiCSDL();
        khoiTaoGiaoDien();

        // 1. Nạp dữ liệu vào các ComboBox trên form lập hóa đơn
        napDuLieuComboBox();
        // 2. Nạp toàn bộ danh sách Hóa đơn đã lập lên JTable
        taiDanhSachHoaDonLenBang("Tất cả");
        // 3. Khởi tạo form ở chế độ chuẩn bị lập hóa đơn mới
        lamMoiFormLapHoaDon();
    }

    private void ketNoiCSDL() {
        try {
            ConnectDB.getInstance().connect();
            hoaDonDao = new HoaDonDao();
            ctHoaDonDao = new ChiTietHoaDonDao();
            phieuSuaChuaDao = new PhieuSuaChuaDao();
            khachHangDao = new KhachHangDao();
            nhanVienDao = new NhanVienDao();
            xeDao = new XeDao();
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

        // ==================== 3. NỘI DUNG CHÍNH: FORM LẬP HÓA ĐƠN & BẢNG HÓA ĐƠN ====================
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

        JLabel lblTieuDeCard = new JLabel("LẬP HÓA ĐƠN");
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

        // --- 3.2. Form điền thông tin lập hóa đơn ---
        JLabel lblMoTa = new JLabel("Điền thông tin bên dưới và nhấn 'Lưu hóa đơn' để lập hóa đơn mới (Ngày lập sẽ tự động ghi nhận khi lưu).");
        lblMoTa.setFont(new Font("Arial", Font.PLAIN, 13));
        lblMoTa.setForeground(Color.GRAY);
        lblMoTa.setBorder(BorderFactory.createEmptyBorder(8, 0, 12, 0));

        txtMaHoaDon = new JTextField();
        txtMaHoaDon.setFont(new Font("Arial", Font.BOLD, 13));

        cboPhieuSuaChua = new JComboBox<>();
        cboKhachHang = new JComboBox<>();
        cboPhuongTien = new JComboBox<>();
        cboNhanVien = new JComboBox<>();

        // Khóa ô Ngày lập, không cho người dùng nhập tay
        txtNgayLap = new JTextField("Tự động ghi nhận khi lưu");
        txtNgayLap.setEditable(false);
        txtNgayLap.setBackground(new Color(245, 247, 250));
        txtNgayLap.setForeground(new Color(90, 95, 105));

        txtVAT = new JTextField("8");

        cboTrangThai = new JComboBox<>(new String[]{
                HoaDon.CHUA_THANH_TOAN, HoaDon.DA_THANH_TOAN, HoaDon.DA_HUY
        });

        txtTongTien = new JTextField("0 đ");
        txtTongTien.setEditable(false);
        txtTongTien.setBackground(new Color(245, 247, 250));
        txtTongTien.setForeground(new Color(180, 30, 40));
        txtTongTien.setFont(new Font("Arial", Font.BOLD, 14));

        // Sự kiện khi chọn Phiếu sửa chữa -> Tự động điền Khách hàng, Xe và tính tiền dự kiến
        cboPhieuSuaChua.addActionListener(e -> {
            if (!dangCapNhatCombo && cboPhieuSuaChua.getSelectedItem() != null) {
                String maPSC = layMaTuCombo(cboPhieuSuaChua);
                tuDongDienTheoPhieuSuaChua(maPSC);
            }
        });

        // Sự kiện khi gõ % VAT -> Tự động tính lại Tổng tiền
        txtVAT.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                capNhatHienThiTongTien();
            }
        });

        JPanel panelGridFields = new JPanel(new GridLayout(3, 3, 25, 10));
        panelGridFields.setBackground(Color.WHITE);
        panelGridFields.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));

        panelGridFields.add(taoOnhapLieu("Mã hóa đơn", txtMaHoaDon));
        panelGridFields.add(taoOCombo("Mã phiếu sửa chữa", cboPhieuSuaChua));
        panelGridFields.add(taoOCombo("Khách hàng", cboKhachHang));
        panelGridFields.add(taoOCombo("Phương tiện (Biển số - Mẫu xe)", cboPhuongTien));
        panelGridFields.add(taoOCombo("Nhân viên lập", cboNhanVien));
        panelGridFields.add(taoOnhapLieu("Ngày lập (Tự động)", txtNgayLap));
        panelGridFields.add(taoOnhapLieu("Thuế VAT (%)", txtVAT));
        panelGridFields.add(taoOCombo("Trạng thái hóa đơn", cboTrangThai));
        panelGridFields.add(taoOnhapLieu("Tổng tiền thanh toán (Gồm VAT)", txtTongTien));

        JPanel panelFieldsContainer = new JPanel(new BorderLayout());
        panelFieldsContainer.setBackground(Color.WHITE);
        panelFieldsContainer.add(lblMoTa, BorderLayout.NORTH);
        panelFieldsContainer.add(panelGridFields, BorderLayout.CENTER);

        // --- 3.3. Thanh tìm kiếm và bộ lọc bảng Hóa Đơn ---
        JPanel panelSearchFilter = new JPanel(new BorderLayout(15, 0));
        panelSearchFilter.setBackground(new Color(240, 243, 246));
        panelSearchFilter.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));

        String placeholder = "Tìm kiếm hóa đơn theo mã HĐ, mã phiếu, tên khách hoặc biển số (Nhấn Enter)...";
        txtTimKiemBang = new JTextField(placeholder);
        txtTimKiemBang.setFont(new Font("Arial", Font.PLAIN, 12));
        txtTimKiemBang.setForeground(Color.GRAY);
        txtTimKiemBang.setBorder(null);
        txtTimKiemBang.setOpaque(false);

        txtTimKiemBang.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (txtTimKiemBang.getText().equals(placeholder)) {
                    txtTimKiemBang.setText("");
                    txtTimKiemBang.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (txtTimKiemBang.getText().trim().isEmpty()) {
                    txtTimKiemBang.setText(placeholder);
                    txtTimKiemBang.setForeground(Color.GRAY);
                }
            }
        });

        txtTimKiemBang.addActionListener(e -> {
            String tuKhoa = txtTimKiemBang.getText().trim();
            if (!tuKhoa.isEmpty() && !tuKhoa.equals(placeholder)) {
                timKiemHoaDonLenBang(tuKhoa);
            } else {
                taiDanhSachHoaDonLenBang(cboLocTrangThai.getSelectedItem().toString());
            }
        });

        JPanel pnlFilterRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        pnlFilterRight.setOpaque(false);
        JLabel lblTrangThaiLoc = new JLabel("Trạng thái:");
        lblTrangThaiLoc.setFont(new Font("Arial", Font.PLAIN, 12));

        cboLocTrangThai = new JComboBox<>(new String[]{
                "Tất cả", HoaDon.CHUA_THANH_TOAN, HoaDon.DA_THANH_TOAN, HoaDon.DA_HUY
        });
        cboLocTrangThai.setFont(new Font("Arial", Font.PLAIN, 12));
        cboLocTrangThai.addActionListener(e -> {
            String trangThaiChon = cboLocTrangThai.getSelectedItem().toString();
            taiDanhSachHoaDonLenBang(trangThaiChon);
        });

        pnlFilterRight.add(lblTrangThaiLoc);
        pnlFilterRight.add(cboLocTrangThai);

        panelSearchFilter.add(txtTimKiemBang, BorderLayout.CENTER);
        panelSearchFilter.add(pnlFilterRight, BorderLayout.EAST);

        // --- 3.4. Bảng Danh Sách Toàn Bộ Hóa Đơn Đã Lập ---
        String[] columnNames = {
                "Mã Hóa Đơn", "Mã Phiếu SC", "Khách Hàng", "Biển Số Xe",
                "Nhân Viên Lập", "Ngày Lập", "VAT (%)", "Tổng Tiền", "Trạng Thái"
        };
        tableModel = new DefaultTableModel(null, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableHoaDon = new JTable(tableModel);
        tableHoaDon.setRowHeight(32);
        tableHoaDon.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tableHoaDon.getTableHeader().setBackground(new Color(240, 243, 246));
        tableHoaDon.setFont(new Font("Arial", Font.PLAIN, 12));
        tableHoaDon.setSelectionBackground(new Color(40, 100, 150));
        tableHoaDon.setSelectionForeground(Color.WHITE);
        tableHoaDon.setShowGrid(false);
        tableHoaDon.setIntercellSpacing(new Dimension(0, 0));

        // Khi click vào 1 dòng Hóa đơn trên bảng -> Đổ thông tin lên Form
        tableHoaDon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tableHoaDon.getSelectedRow();
                if (row >= 0) {
                    String maHD = tableModel.getValueAt(row, 0).toString();
                    hienThiHoaDonLenForm(maHD);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(tableHoaDon);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 233, 236)));

        JPanel panelTableSection = new JPanel(new BorderLayout());
        panelTableSection.setBackground(Color.WHITE);
        panelTableSection.add(panelSearchFilter, BorderLayout.NORTH);
        panelTableSection.add(scrollPane, BorderLayout.CENTER);

        // --- 3.5. Khu vực nút bấm dưới cùng ---
        JPanel panelBottomButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        panelBottomButtons.setBackground(Color.WHITE);

        JButton btnTaoMoi = taoNutTrang("Tạo hóa đơn mới");
        JButton btnLuuHoaDon = taoNutTrang("Lưu hóa đơn");
        JButton btnInHoaDon = taoNutTrang("In hóa đơn");
        JButton btnXacNhan = taoNutDo("Xác nhận thanh toán");

        btnTaoMoi.addActionListener(e -> {
            tableHoaDon.clearSelection();
            lamMoiFormLapHoaDon();
        });

        btnLuuHoaDon.addActionListener(e -> luuHoaDonVaoCSDL());

        btnInHoaDon.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Đang chuẩn bị bản in cho hóa đơn: " + txtMaHoaDon.getText(),
                "In hóa đơn", JOptionPane.INFORMATION_MESSAGE));

        btnXacNhan.addActionListener(e -> xacNhanThanhToanHoaDon());

        panelBottomButtons.add(btnTaoMoi);
        panelBottomButtons.add(btnLuuHoaDon);
        panelBottomButtons.add(btnInHoaDon);
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

        // ==================== 4. THANH TRẠNG THÁI - FOOTER (GIỮ NGUYÊN CODE CŨ) ====================
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
        if (hoaDonDao == null) return;
        try {
            dangCapNhatCombo = true;
            cboPhieuSuaChua.removeAllItems();
            cboKhachHang.removeAllItems();
            cboPhuongTien.removeAllItems();
            cboNhanVien.removeAllItems();

            mapTenKhachHang.clear();
            mapBienSoXe.clear();
            mapTenNhanVien.clear();

            for (PhieuSuaChua psc : phieuSuaChuaDao.layDanhSachPhieuSuaChua()) {
                cboPhieuSuaChua.addItem(psc.getMaPhieuSuaChua() + " - " + psc.getLoaiPhieu() + " (" + psc.getTrangThai() + ")");
            }

            for (KhachHang kh : khachHangDao.layDanhSachKhachHang()) {
                mapTenKhachHang.put(kh.getMaKhachHang(), kh.getHoTen());
                cboKhachHang.addItem(kh.getMaKhachHang() + " - " + kh.getHoTen());
            }

            for (Xe xe : xeDao.layDanhSachXe()) {
                mapBienSoXe.put(xe.getMaPhuongTien(), xe.getBienSo() + " (" + xe.getMauXe() + ")");
                cboPhuongTien.addItem(xe.getMaPhuongTien() + " - " + xe.getBienSo() + " (" + xe.getMauXe() + ")");
            }

            for (NhanVien nv : nhanVienDao.layDanhSachNhanVien()) {
                mapTenNhanVien.put(nv.getMaNhanVien(), nv.getHoTen());
                cboNhanVien.addItem(nv.getMaNhanVien() + " - " + nv.getHoTen());
            }

            dangCapNhatCombo = false;
        } catch (SQLException e) {
            dangCapNhatCombo = false;
            e.printStackTrace();
        }
    }

    private void taiDanhSachHoaDonLenBang(String trangThaiLoc) {
        if (hoaDonDao == null) return;
        try {
            tableModel.setRowCount(0);
            ArrayList<HoaDon> dsHoaDon;

            if (trangThaiLoc == null || trangThaiLoc.equals("Tất cả")) {
                dsHoaDon = hoaDonDao.layDanhSachHoaDon();
            } else {
                dsHoaDon = hoaDonDao.timTheoTrangThai(trangThaiLoc);
            }

            Map<String, BigDecimal> mapVat = layTatCaVatTuDB();

            for (HoaDon hd : dsHoaDon) {
                BigDecimal vat = hd.getVat() != null ? hd.getVat() : mapVat.getOrDefault(hd.getMaHoaDon(), BigDecimal.ZERO);
                BigDecimal tongTien = tinhTongTienHoaDon(hd.getMaHoaDon(), hd.getMaPhieuSuaChua(), vat);

                String tenKH = mapTenKhachHang.getOrDefault(hd.getMaKhachHang(), hd.getMaKhachHang());
                String bienSo = mapBienSoXe.getOrDefault(hd.getMaPhuongTien(), hd.getMaPhuongTien());
                String tenNV = mapTenNhanVien.getOrDefault(hd.getMaNhanVien(), hd.getMaNhanVien());
                String ngayLapStr = hd.getNgayLap() != null ? hd.getNgayLap().format(dtf) : "";

                tableModel.addRow(new Object[]{
                        hd.getMaHoaDon(),
                        hd.getMaPhieuSuaChua(),
                        hd.getMaKhachHang() + " - " + tenKH,
                        bienSo,
                        hd.getMaNhanVien() + " - " + tenNV,
                        ngayLapStr,
                        vat.stripTrailingZeros().toPlainString() + "%",
                        String.format("%,.0f đ", tongTien),
                        hd.getTrangThai()
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi tải danh sách hóa đơn: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void timKiemHoaDonLenBang(String tuKhoa) {
        try {
            tableModel.setRowCount(0);
            ArrayList<HoaDon> dsHoaDon = hoaDonDao.timKiem(tuKhoa);
            Map<String, BigDecimal> mapVat = layTatCaVatTuDB();

            for (HoaDon hd : dsHoaDon) {
                BigDecimal vat = hd.getVat() != null ? hd.getVat() : mapVat.getOrDefault(hd.getMaHoaDon(), BigDecimal.ZERO);
                BigDecimal tongTien = tinhTongTienHoaDon(hd.getMaHoaDon(), hd.getMaPhieuSuaChua(), vat);

                String tenKH = mapTenKhachHang.getOrDefault(hd.getMaKhachHang(), hd.getMaKhachHang());
                String bienSo = mapBienSoXe.getOrDefault(hd.getMaPhuongTien(), hd.getMaPhuongTien());
                String tenNV = mapTenNhanVien.getOrDefault(hd.getMaNhanVien(), hd.getMaNhanVien());
                String ngayLapStr = hd.getNgayLap() != null ? hd.getNgayLap().format(dtf) : "";

                tableModel.addRow(new Object[]{
                        hd.getMaHoaDon(),
                        hd.getMaPhieuSuaChua(),
                        hd.getMaKhachHang() + " - " + tenKH,
                        bienSo,
                        hd.getMaNhanVien() + " - " + tenNV,
                        ngayLapStr,
                        vat.stripTrailingZeros().toPlainString() + "%",
                        String.format("%,.0f đ", tongTien),
                        hd.getTrangThai()
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Chuẩn bị form trống với Mã hóa đơn tự sinh để lập hóa đơn mới
     */
    private void lamMoiFormLapHoaDon() {
        try {
            String maMoi = hoaDonDao.taoMaHoaDon();
            txtMaHoaDon.setText(maMoi);
            txtMaHoaDon.setEditable(true);
            // Để trạng thái chờ tự động ghi nhận thời gian thực khi nhấn Lưu
            txtNgayLap.setText("Tự động ghi nhận khi lưu");
            txtVAT.setText("8");
            cboTrangThai.setSelectedItem(HoaDon.CHUA_THANH_TOAN);

            if (cboPhieuSuaChua.getItemCount() > 0) {
                cboPhieuSuaChua.setSelectedIndex(0);
                tuDongDienTheoPhieuSuaChua(layMaTuCombo(cboPhieuSuaChua));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Khi chọn 1 dòng trên JTable -> Đổ dữ liệu Hóa đơn đó lên Form
     */
    private void hienThiHoaDonLenForm(String maHoaDon) {
        try {
            HoaDon hd = hoaDonDao.timTheoMa(maHoaDon);
            if (hd == null) return;

            dangCapNhatCombo = true;
            txtMaHoaDon.setText(hd.getMaHoaDon());
            txtMaHoaDon.setEditable(false);

            chonItemComboTheoMa(cboPhieuSuaChua, hd.getMaPhieuSuaChua());
            chonItemComboTheoMa(cboKhachHang, hd.getMaKhachHang());
            chonItemComboTheoMa(cboPhuongTien, hd.getMaPhuongTien());
            chonItemComboTheoMa(cboNhanVien, hd.getMaNhanVien());

            txtNgayLap.setText(hd.getNgayLap() != null ? hd.getNgayLap().format(dtf) : "");

            BigDecimal vat = hd.getVat();
            if (vat == null) {
                vat = layTatCaVatTuDB().getOrDefault(maHoaDon, BigDecimal.ZERO);
            }
            txtVAT.setText(vat.stripTrailingZeros().toPlainString());
            cboTrangThai.setSelectedItem(hd.getTrangThai());

            tamTinhHienTai = tinhTamTinh(hd.getMaHoaDon(), hd.getMaPhieuSuaChua());
            capNhatHienThiTongTien();
            dangCapNhatCombo = false;

        } catch (SQLException e) {
            dangCapNhatCombo = false;
            e.printStackTrace();
        }
    }

    private void tuDongDienTheoPhieuSuaChua(String maPSC) {
        String sql = "SELECT p.maKhachHang, p.maPhuongTien, t.maNhanVien "
                + "FROM dbo.PhieuSuaChua s "
                + "JOIN dbo.PhieuTiepNhan t ON s.maTiepNhan = t.maTiepNhan "
                + "JOIN dbo.PhuongTien p ON t.maPhuongTien = p.maPhuongTien "
                + "WHERE s.maPhieuSuaChua = ?";
        try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql)) {
            ps.setString(1, maPSC);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    chonItemComboTheoMa(cboKhachHang, rs.getString("maKhachHang"));
                    chonItemComboTheoMa(cboPhuongTien, rs.getString("maPhuongTien"));
                    if (rs.getString("maNhanVien") != null) {
                        chonItemComboTheoMa(cboNhanVien, rs.getString("maNhanVien"));
                    }
                }
            }
        } catch (SQLException e) {
            // Bỏ qua nếu bảng PhieuTiepNhan không có cột maNhanVien
        }

        tamTinhHienTai = tinhTamTinh(txtMaHoaDon.getText().trim(), maPSC);
        capNhatHienThiTongTien();
    }

    /**
     * Lưu hóa đơn: Tự động ghi nhận thời gian thực tại thời điểm bấm nút Lưu
     */
    private void luuHoaDonVaoCSDL() {
        try {
            String maHD = txtMaHoaDon.getText().trim();
            String maPSC = layMaTuCombo(cboPhieuSuaChua);
            String maKH = layMaTuCombo(cboKhachHang);
            String maPT = layMaTuCombo(cboPhuongTien);
            String maNV = layMaTuCombo(cboNhanVien);
            String trangThai = cboTrangThai.getSelectedItem().toString();
            BigDecimal vat = new BigDecimal(txtVAT.getText().trim());

            HoaDon hdTonTai = hoaDonDao.timTheoMa(maHD);

            if (hdTonTai == null) {
                // Kiểm tra trùng Phiếu sửa chữa
                HoaDon hdTheoPSC = hoaDonDao.timTheoMaPhieuSuaChua(maPSC);
                if (hdTheoPSC != null) {
                    JOptionPane.showMessageDialog(this,
                            "Phiếu sửa chữa " + maPSC + " đã được lập trong hóa đơn " + hdTheoPSC.getMaHoaDon() + "!\n"
                                    + "Vui lòng chọn phiếu sửa chữa khác hoặc chọn hóa đơn " + hdTheoPSC.getMaHoaDon() + " dưới bảng để cập nhật.",
                            "Trùng phiếu sửa chữa", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // Tự động lấy thời gian ngay lúc nhấn Lưu hóa đơn
                LocalDateTime ngayLapTuDong = LocalDateTime.now();
                txtNgayLap.setText(ngayLapTuDong.format(dtf));

                HoaDon hdMoi = new HoaDon(maHD, maPSC, maKH, maPT, maNV, ngayLapTuDong, vat, trangThai);
                hdMoi.setVat(vat);

                if (hoaDonDao.themHoaDon(hdMoi)) {
                    JOptionPane.showMessageDialog(this,
                            "Lập hóa đơn mới " + maHD + " thành công!\nThời gian ghi nhận: " + ngayLapTuDong.format(dtf));
                    taiDanhSachHoaDonLenBang(cboLocTrangThai.getSelectedItem().toString());
                    lamMoiFormLapHoaDon();
                } else {
                    JOptionPane.showMessageDialog(this, "Không thể thêm hóa đơn vào CSDL.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                // Cập nhật Hóa Đơn đã có (Giữ nguyên ngày lập ban đầu của hóa đơn hoặc lấy thời gian hiện tại nếu bị rỗng)
                LocalDateTime ngayLapGoc = hdTonTai.getNgayLap() != null ? hdTonTai.getNgayLap() : LocalDateTime.now();

                HoaDon hdSua = new HoaDon(maHD, maPSC, maKH, maPT, maNV, ngayLapGoc, vat, hdTonTai.getTrangThai());
                hdSua.setVat(vat);

                boolean kqSua = hoaDonDao.suaHoaDon(hdSua);
                boolean kqTrangThai = true;
                if (!hdTonTai.getTrangThai().equals(trangThai)) {
                    kqTrangThai = hoaDonDao.capNhatTrangThai(maHD, hdTonTai.getTrangThai(), trangThai);
                }

                if (kqSua || kqTrangThai) {
                    JOptionPane.showMessageDialog(this, "Cập nhật hóa đơn " + maHD + " thành công!");
                    taiDanhSachHoaDonLenBang(cboLocTrangThai.getSelectedItem().toString());
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Chỉ có thể chỉnh sửa thông tin hóa đơn khi đang ở trạng thái 'Chưa thanh toán'.",
                            "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi lưu hóa đơn: " + ex.getMessage(), "Lỗi dữ liệu", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xacNhanThanhToanHoaDon() {
        String maHD = txtMaHoaDon.getText().trim();
        try {
            HoaDon hd = hoaDonDao.timTheoMa(maHD);
            if (hd == null) {
                JOptionPane.showMessageDialog(this,
                        "Hóa đơn " + maHD + " chưa được lưu vào CSDL. Hãy nhấn 'Lưu hóa đơn' trước hoặc chọn một hóa đơn dưới bảng!",
                        "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (HoaDon.DA_THANH_TOAN.equals(hd.getTrangThai())) {
                JOptionPane.showMessageDialog(this, "Hóa đơn " + maHD + " đã được thanh toán rồi!");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Xác nhận thanh toán cho hóa đơn " + maHD + " với số tiền " + txtTongTien.getText() + "?",
                    "Xác nhận thanh toán", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                if (hoaDonDao.capNhatTrangThai(maHD, hd.getTrangThai(), HoaDon.DA_THANH_TOAN)) {
                    JOptionPane.showMessageDialog(this, "Xác nhận thanh toán hóa đơn " + maHD + " thành công!");
                    cboTrangThai.setSelectedItem(HoaDon.DA_THANH_TOAN);
                    taiDanhSachHoaDonLenBang(cboLocTrangThai.getSelectedItem().toString());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi CSDL: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ==================== CÁC HÀM TÍNH TIỀN & HỖ TRỢ ====================

    private BigDecimal tinhTamTinh(String maHoaDon, String maPSC) {
        BigDecimal tamTinh = BigDecimal.ZERO;
        try {
            ArrayList<ChiTietHoaDon> dsCT = ctHoaDonDao.timTheoMaHoaDon(maHoaDon);
            for (ChiTietHoaDon ct : dsCT) {
                BigDecimal sl = new BigDecimal(ct.getSoLuong());
                BigDecimal dg = ct.getDonGia() != null ? ct.getDonGia() : BigDecimal.ZERO;
                BigDecimal gg = ct.getGiamGia() != null ? ct.getGiamGia() : BigDecimal.ZERO;
                tamTinh = tamTinh.add(dg.multiply(sl).subtract(gg));
            }

            if (tamTinh.compareTo(BigDecimal.ZERO) == 0 && maPSC != null && !maPSC.isEmpty()) {
                String sql = "SELECT ISNULL(SUM(donGia), 0) AS tongPT FROM dbo.PhuTung WHERE maPhieuSuaChua = ?";
                try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql)) {
                    ps.setString(1, maPSC);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next() && rs.getBigDecimal("tongPT") != null) {
                            tamTinh = tamTinh.add(rs.getBigDecimal("tongPT"));
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tamTinh;
    }

    private BigDecimal tinhTongTienHoaDon(String maHoaDon, String maPSC, BigDecimal vat) {
        BigDecimal tamTinh = tinhTamTinh(maHoaDon, maPSC);
        BigDecimal tienVat = tamTinh.multiply(vat).divide(new BigDecimal("100"));
        return tamTinh.add(tienVat);
    }

    private void capNhatHienThiTongTien() {
        try {
            BigDecimal vat = new BigDecimal(txtVAT.getText().trim());
            if (vat.compareTo(BigDecimal.ZERO) < 0) vat = BigDecimal.ZERO;
            BigDecimal tienVat = tamTinhHienTai.multiply(vat).divide(new BigDecimal("100"));
            txtTongTien.setText(String.format("%,.0f đ", tamTinhHienTai.add(tienVat)));
        } catch (Exception e) {
            txtTongTien.setText(String.format("%,.0f đ", tamTinhHienTai));
        }
    }

    private Map<String, BigDecimal> layTatCaVatTuDB() {
        Map<String, BigDecimal> map = new HashMap<>();
        String sql = "SELECT maHoaDon, vat FROM dbo.HoaDon";
        try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                map.put(rs.getString("maHoaDon"), rs.getBigDecimal("vat") != null ? rs.getBigDecimal("vat") : BigDecimal.ZERO);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return map;
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
        SwingUtilities.invokeLater(() -> new GaraLapHoaDon().setVisible(true));
    }
}