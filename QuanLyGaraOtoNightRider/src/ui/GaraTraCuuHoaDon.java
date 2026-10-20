package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

import connectDB.ConnectDB;
import dao.ChiTietHoaDonDao;
import dao.HoaDonDao;
import dao.KhachHangDao;
import dao.NhanVienDao;
import dao.XeDao;
import entity.ChiTietHoaDon;
import entity.HoaDon;
import entity.KhachHang;
import entity.NhanVien;
import entity.Xe;

public class GaraTraCuuHoaDon extends JFrame {

    // Các ô hiển thị trên bảng thông tin chi tiết của Hóa đơn đang chọn
    private JTextField txtMaHoaDon;
    private JTextField txtMaPhieuSC;
    private JTextField txtKhachHang;
    private JTextField txtPhuongTien;
    private JTextField txtNhanVien;
    private JTextField txtNgayLap;
    private JTextField txtVAT;
    private JTextField txtTrangThai;
    private JTextField txtTongTien;

    // Thanh tìm kiếm, lọc và Bảng danh sách Hóa đơn
    private JTextField txtTimKiemBang;
    private JComboBox<String> cboLocTrangThai;
    private JTable tableHoaDon;
    private DefaultTableModel tableModel;

    // Các lớp DAO
    private HoaDonDao hoaDonDao;
    private ChiTietHoaDonDao ctHoaDonDao;
    private KhachHangDao khachHangDao;
    private NhanVienDao nhanVienDao;
    private XeDao xeDao;

    // Bộ nhớ đệm hỗ trợ hiển thị nhanh thông tin liên kết
    private Map<String, KhachHang> mapKhachHang = new HashMap<>();
    private Map<String, Xe> mapXe = new HashMap<>();
    private Map<String, NhanVien> mapNhanVien = new HashMap<>();

    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private final String PLACEHOLDER_TIM_KIEM = "Tìm kiếm theo mã HĐ, mã phiếu, tên khách, SĐT hoặc biển số...";

    public GaraTraCuuHoaDon() {
        setTitle("Hệ thống Quản lý Gara Ô Tô");
        setSize(1340, 840);
        setLocationRelativeTo(null);
        setResizable(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        ketNoiCSDL();
        khoiTaoGiaoDien();

        // Nạp dữ liệu danh mục và tải toàn bộ hóa đơn lên JTable
        napDuLieuDanhMuc();
        taiDanhSachHoaDonLenBang("Tất cả");
    }

    private void ketNoiCSDL() {
        try {
            ConnectDB.getInstance().connect();
            hoaDonDao = new HoaDonDao();
            ctHoaDonDao = new ChiTietHoaDonDao();
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

        JLabel lblKhachHang = new JLabel("Khách hàng");
        lblKhachHang.setFont(new Font("Arial", Font.BOLD, 13));
        lblKhachHang.setForeground(Color.DARK_GRAY);
        panelToolbar.add(lblKhachHang);

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

        // ==================== 3. NỘI DUNG CHÍNH: TRA CỨU HÓA ĐƠN ====================
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

        // --- 3.2. Bảng thông tin chi tiết của Hóa đơn được chọn ---
        JLabel lblMoTa = new JLabel("Chọn một hóa đơn từ bảng danh sách bên dưới để xem đầy đủ thông tin chi tiết.");
        lblMoTa.setFont(new Font("Arial", Font.PLAIN, 13));
        lblMoTa.setForeground(Color.GRAY);
        lblMoTa.setBorder(BorderFactory.createEmptyBorder(8, 0, 12, 0));

        txtMaHoaDon = taoTextFieldChiDoc();
        txtMaHoaDon.setFont(new Font("Arial", Font.BOLD, 13));

        txtMaPhieuSC = taoTextFieldChiDoc();
        txtKhachHang = taoTextFieldChiDoc();
        txtPhuongTien = taoTextFieldChiDoc();
        txtNhanVien = taoTextFieldChiDoc();
        txtNgayLap = taoTextFieldChiDoc();
        txtVAT = taoTextFieldChiDoc();
        txtTrangThai = taoTextFieldChiDoc();

        txtTongTien = taoTextFieldChiDoc();
        txtTongTien.setForeground(new Color(180, 30, 40));
        txtTongTien.setFont(new Font("Arial", Font.BOLD, 14));

        JPanel panelGridFields = new JPanel(new GridLayout(3, 3, 25, 10));
        panelGridFields.setBackground(Color.WHITE);
        panelGridFields.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));

        panelGridFields.add(taoOnhapLieu("Mã hóa đơn", txtMaHoaDon));
        panelGridFields.add(taoOnhapLieu("Mã phiếu sửa chữa", txtMaPhieuSC));
        panelGridFields.add(taoOnhapLieu("Khách hàng (Mã - Tên - SĐT)", txtKhachHang));
        panelGridFields.add(taoOnhapLieu("Phương tiện (Biển số - Mẫu xe)", txtPhuongTien));
        panelGridFields.add(taoOnhapLieu("Nhân viên lập", txtNhanVien));
        panelGridFields.add(taoOnhapLieu("Ngày lập hóa đơn", txtNgayLap));
        panelGridFields.add(taoOnhapLieu("Thuế VAT (%)", txtVAT));
        panelGridFields.add(taoOnhapLieu("Trạng thái thanh toán", txtTrangThai));
        panelGridFields.add(taoOnhapLieu("Tổng tiền thanh toán (Gồm VAT)", txtTongTien));

        JPanel panelFieldsContainer = new JPanel(new BorderLayout());
        panelFieldsContainer.setBackground(Color.WHITE);
        panelFieldsContainer.add(lblMoTa, BorderLayout.NORTH);
        panelFieldsContainer.add(panelGridFields, BorderLayout.CENTER);

        // --- 3.3. Thanh tìm kiếm và bộ lọc ---
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

        txtTimKiemBang.addActionListener(e -> thucHienTraCuu());

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

        // --- 3.4. Bảng kết quả toàn bộ Hóa đơn đã lập ---
        String[] columnNames = {
                "Mã Hóa Đơn", "Mã Phiếu SC", "Khách Hàng", "Số Điện Thoại",
                "Biển Số Xe", "Nhân Viên Lập", "Ngày Lập", "VAT (%)", "Tổng Tiền", "Trạng Thái"
        };

        tableModel = new DefaultTableModel(null, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableHoaDon = new JTable(tableModel);
        tableHoaDon.setRowHeight(34);
        tableHoaDon.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tableHoaDon.getTableHeader().setBackground(new Color(240, 243, 246));
        tableHoaDon.setFont(new Font("Arial", Font.PLAIN, 12));
        tableHoaDon.setSelectionBackground(new Color(40, 100, 150));
        tableHoaDon.setSelectionForeground(Color.WHITE);
        tableHoaDon.setShowGrid(false);
        tableHoaDon.setIntercellSpacing(new Dimension(0, 0));

        // Khi click chuột hoặc di chuyển dòng chọn trên bảng -> Đổ thông tin lên bảng thông tin phía trên
        tableHoaDon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                capNhatThongTinTuDongChon();
            }
        });

        tableHoaDon.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                capNhatThongTinTuDongChon();
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

        JButton btnLamMoi = taoNutTrang("Làm mới");
        JButton btnTraCuu = taoNutTrang("Tra cứu");
        JButton btnXemHoaDon = taoNutTrang("Xem hóa đơn");
        JButton btnInHoaDon = taoNutDo("In hóa đơn");

        btnLamMoi.addActionListener(e -> {
            txtTimKiemBang.setText(PLACEHOLDER_TIM_KIEM);
            txtTimKiemBang.setForeground(Color.GRAY);
            cboLocTrangThai.setSelectedIndex(0);
            napDuLieuDanhMuc();
            taiDanhSachHoaDonLenBang("Tất cả");
        });

        btnTraCuu.addActionListener(e -> thucHienTraCuu());
        btnXemHoaDon.addActionListener(e -> xemChiTietPhuTungHoaDon());
        btnInHoaDon.addActionListener(e -> {
            String maHD = txtMaHoaDon.getText().trim();
            if (maHD.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một hóa đơn trong bảng để in!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            JOptionPane.showMessageDialog(this,
                    "Đang chuẩn bị bản in cho hóa đơn: " + maHD + "\nKhách hàng: " + txtKhachHang.getText() + "\nTổng tiền: " + txtTongTien.getText(),
                    "In hóa đơn", JOptionPane.INFORMATION_MESSAGE);
        });

        panelBottomButtons.add(btnLamMoi);
        panelBottomButtons.add(btnTraCuu);
        panelBottomButtons.add(btnXemHoaDon);
        panelBottomButtons.add(btnInHoaDon);

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

    // ==================== CÁC HÀM XỬ LÝ DỮ LIỆU TỪ DATABASE ====================

    private void napDuLieuDanhMuc() {
        if (hoaDonDao == null) return;
        try {
            mapKhachHang.clear();
            mapXe.clear();
            mapNhanVien.clear();

            for (KhachHang kh : khachHangDao.layDanhSachKhachHang()) {
                mapKhachHang.put(kh.getMaKhachHang(), kh);
            }
            for (Xe xe : xeDao.layDanhSachXe()) {
                mapXe.put(xe.getMaPhuongTien(), xe);
            }
            for (NhanVien nv : nhanVienDao.layDanhSachNhanVien()) {
                mapNhanVien.put(nv.getMaNhanVien(), nv);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Tải toàn bộ danh sách Hóa đơn đã lập từ CSDL lên JTable
     */
    private void taiDanhSachHoaDonLenBang(String trangThaiLoc) {
        if (hoaDonDao == null) return;
        try {
            ArrayList<HoaDon> dsHoaDon;
            if (trangThaiLoc == null || trangThaiLoc.equals("Tất cả")) {
                dsHoaDon = hoaDonDao.layDanhSachHoaDon();
            } else {
                dsHoaDon = hoaDonDao.timTheoTrangThai(trangThaiLoc);
            }
            doDuLieuHoaDonVaoBang(dsHoaDon);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi tải danh sách hóa đơn: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Thực hiện tra cứu theo từ khóa và trạng thái lọc
     */
    private void thucHienTraCuu() {
        if (hoaDonDao == null) return;
        String tuKhoa = txtTimKiemBang.getText().trim();
        String trangThaiChon = cboLocTrangThai.getSelectedItem().toString();

        try {
            ArrayList<HoaDon> dsKetQua;
            if (tuKhoa.isEmpty() || tuKhoa.equals(PLACEHOLDER_TIM_KIEM)) {
                if (trangThaiChon.equals("Tất cả")) {
                    dsKetQua = hoaDonDao.layDanhSachHoaDon();
                } else {
                    dsKetQua = hoaDonDao.timTheoTrangThai(trangThaiChon);
                }
            } else {
                ArrayList<HoaDon> dsTim = hoaDonDao.timKiem(tuKhoa);
                if (trangThaiChon.equals("Tất cả")) {
                    dsKetQua = dsTim;
                } else {
                    dsKetQua = new ArrayList<>();
                    for (HoaDon hd : dsTim) {
                        if (trangThaiChon.equals(hd.getTrangThai())) {
                            dsKetQua.add(hd);
                        }
                    }
                }
            }

            doDuLieuHoaDonVaoBang(dsKetQua);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi tra cứu hóa đơn: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void doDuLieuHoaDonVaoBang(ArrayList<HoaDon> dsHoaDon) {
        tableModel.setRowCount(0);
        Map<String, BigDecimal> mapVat = layTatCaVatTuDB();

        for (HoaDon hd : dsHoaDon) {
            KhachHang kh = mapKhachHang.get(hd.getMaKhachHang());
            Xe xe = mapXe.get(hd.getMaPhuongTien());
            NhanVien nv = mapNhanVien.get(hd.getMaNhanVien());

            String tenKH = kh != null ? kh.getHoTen() : hd.getMaKhachHang();
            String sdtKH = kh != null ? kh.getSoDienThoai() : "";
            String bienSo = xe != null ? xe.getBienSo() : hd.getMaPhuongTien();
            String tenNV = nv != null ? nv.getHoTen() : hd.getMaNhanVien();
            String ngayLapStr = hd.getNgayLap() != null ? hd.getNgayLap().format(dtf) : "";

            BigDecimal vat = hd.getVat() != null ? hd.getVat() : mapVat.getOrDefault(hd.getMaHoaDon(), BigDecimal.ZERO);
            BigDecimal tongTien = tinhTongTienHoaDon(hd.getMaHoaDon(), hd.getMaPhieuSuaChua(), vat);

            tableModel.addRow(new Object[]{
                    hd.getMaHoaDon(),
                    hd.getMaPhieuSuaChua(),
                    tenKH,
                    sdtKH,
                    bienSo,
                    tenNV,
                    ngayLapStr,
                    vat.stripTrailingZeros().toPlainString() + "%",
                    String.format("%,.0f đ", tongTien),
                    hd.getTrangThai()
            });
        }

        if (tableHoaDon.getRowCount() > 0) {
            tableHoaDon.setRowSelectionInterval(0, 0);
            capNhatThongTinTuDongChon();
        } else {
            xoaTrangThongTin();
        }
    }

    /**
     * Khi click chọn 1 dòng trên JTable -> Hiển thị chi tiết hóa đơn đó lên bảng thông tin phía trên
     */
    private void capNhatThongTinTuDongChon() {
        int row = tableHoaDon.getSelectedRow();
        if (row < 0) return;

        String maHD = tableModel.getValueAt(row, 0).toString();
        hienThiChiTietHoaDonLenBangThongTin(maHD);
    }

    private void hienThiChiTietHoaDonLenBangThongTin(String maHoaDon) {
        try {
            HoaDon hd = hoaDonDao.timTheoMa(maHoaDon);
            if (hd == null) return;

            txtMaHoaDon.setText(hd.getMaHoaDon());
            txtMaPhieuSC.setText(hd.getMaPhieuSuaChua());
            txtNgayLap.setText(hd.getNgayLap() != null ? hd.getNgayLap().format(dtf) : "");
            txtTrangThai.setText(hd.getTrangThai());

            // Hiển thị thông tin Khách hàng
            KhachHang kh = mapKhachHang.get(hd.getMaKhachHang());
            if (kh == null) {
                kh = khachHangDao.timTheoMa(hd.getMaKhachHang());
            }
            if (kh != null) {
                txtKhachHang.setText(kh.getMaKhachHang() + " - " + kh.getHoTen() + " (" + kh.getSoDienThoai() + ")");
            } else {
                txtKhachHang.setText(hd.getMaKhachHang());
            }

            // Hiển thị thông tin Phương tiện
            Xe xe = mapXe.get(hd.getMaPhuongTien());
            if (xe != null) {
                txtPhuongTien.setText(xe.getBienSo() + " - " + xe.getMauXe());
            } else {
                txtPhuongTien.setText(hd.getMaPhuongTien());
            }

            // Hiển thị thông tin Nhân viên
            NhanVien nv = mapNhanVien.get(hd.getMaNhanVien());
            if (nv == null) {
                nv = nhanVienDao.timTheoMa(hd.getMaNhanVien());
            }
            if (nv != null) {
                txtNhanVien.setText(nv.getMaNhanVien() + " - " + nv.getHoTen());
            } else {
                txtNhanVien.setText(hd.getMaNhanVien());
            }

            // Hiển thị VAT và Tổng tiền
            BigDecimal vat = hd.getVat();
            if (vat == null) {
                vat = layTatCaVatTuDB().getOrDefault(maHoaDon, BigDecimal.ZERO);
            }
            txtVAT.setText(vat.stripTrailingZeros().toPlainString() + "%");

            BigDecimal tongTien = tinhTongTienHoaDon(hd.getMaHoaDon(), hd.getMaPhieuSuaChua(), vat);
            txtTongTien.setText(String.format("%,.0f đ", tongTien));

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Mở hộp thoại xem chi tiết các phụ tùng trong Hóa đơn đang chọn (Nút "Xem hóa đơn")
     */
    private void xemChiTietPhuTungHoaDon() {
        String maHD = txtMaHoaDon.getText().trim();
        if (maHD.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một hóa đơn dưới bảng để xem chi tiết!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            ArrayList<ChiTietHoaDon> dsChiTiet = ctHoaDonDao.timTheoMaHoaDon(maHD);
            String[] cols = {"Mã Phụ Tùng", "Tên Phụ Tùng", "Số Lượng", "Đơn Giá", "Giảm Giá", "Thành Tiền"};
            DefaultTableModel modelCT = new DefaultTableModel(null, cols) {
                @Override
                public boolean isCellEditable(int r, int c) {
                    return false;
                }
            };

            for (ChiTietHoaDon ct : dsChiTiet) {
                String tenPT = layTenPhuTungTuDB(ct.getMaPhuTung());
                BigDecimal sl = new BigDecimal(ct.getSoLuong());
                BigDecimal dg = ct.getDonGia() != null ? ct.getDonGia() : BigDecimal.ZERO;
                BigDecimal gg = ct.getGiamGia() != null ? ct.getGiamGia() : BigDecimal.ZERO;
                BigDecimal thanhTien = dg.multiply(sl).subtract(gg);

                modelCT.addRow(new Object[]{
                        ct.getMaPhuTung(),
                        tenPT,
                        ct.getSoLuong(),
                        String.format("%,.0f đ", dg),
                        String.format("%,.0f đ", gg),
                        String.format("%,.0f đ", thanhTien)
                });
            }

            JTable tblCT = new JTable(modelCT);
            tblCT.setRowHeight(28);
            tblCT.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
            tblCT.setFont(new Font("Arial", Font.PLAIN, 12));

            JScrollPane sp = new JScrollPane(tblCT);
            sp.setPreferredSize(new Dimension(700, 260));

            JPanel pnlPopup = new JPanel(new BorderLayout(0, 10));
            JLabel lblHeader = new JLabel("Chi tiết hóa đơn: " + maHD + " | Khách hàng: " + txtKhachHang.getText() + " | Tổng tiền: " + txtTongTien.getText());
            lblHeader.setFont(new Font("Arial", Font.BOLD, 13));
            pnlPopup.add(lblHeader, BorderLayout.NORTH);
            pnlPopup.add(sp, BorderLayout.CENTER);

            JOptionPane.showMessageDialog(this, pnlPopup, "Chi tiết hóa đơn " + maHD, JOptionPane.PLAIN_MESSAGE);

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi tải chi tiết hóa đơn: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xoaTrangThongTin() {
        txtMaHoaDon.setText("");
        txtMaPhieuSC.setText("");
        txtKhachHang.setText("");
        txtPhuongTien.setText("");
        txtNhanVien.setText("");
        txtNgayLap.setText("");
        txtVAT.setText("");
        txtTrangThai.setText("");
        txtTongTien.setText("0 đ");
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

    private String layTenPhuTungTuDB(String maPhuTung) {
        String sql = "SELECT tenPhuTung FROM dbo.PhuTung WHERE maPhuTung = ?";
        try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql)) {
            ps.setString(1, maPhuTung);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("tenPhuTung");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return maPhuTung;
    }

    // ==================== CÁC HÀM TIỆN ÍCH GIAO DIỆN ====================

    private JTextField taoTextFieldChiDoc() {
        JTextField tf = new JTextField();
        tf.setEditable(false);
        tf.setBackground(new Color(248, 250, 252));
        tf.setForeground(new Color(30, 35, 45));
        return tf;
    }

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
        SwingUtilities.invokeLater(() -> new GaraTraCuuHoaDon().setVisible(true));
    }
}