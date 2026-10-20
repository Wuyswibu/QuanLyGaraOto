package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import connectDB.ConnectDB;
import dao.ChiTietPhieuXuatDao;
import dao.PhieuSuaChuaDao;
import dao.PhieuXuatKhoDao;
import dao.PhuTungDao;
import entity.ChiTietPhieuXuat;
import entity.PhieuSuaChua;
import entity.PhieuXuatKho;
import entity.PhuTung;

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

public class GaraLapPhieuXuatKho extends JFrame {

    // Các trường thông tin trên Form Lập Phiếu Xuất Kho
    private JTextField txtMaPhieuXuat;
    private JTextField txtNgayXuat;
    private JComboBox<String> cboTrangThai;
    private JComboBox<String> cboPhuTung;
    private JTextField txtSoLuong;
    private JTextField txtDonGia;
    private JComboBox<String> cboLyDoXuat; // Cho phép chọn theo Phiếu sửa chữa hoặc tự nhập lý do
    private JTextField txtGhiChu;
    private JTextField txtTongGiaTri;

    // Thanh tìm kiếm, bộ lọc và Bảng danh sách Phiếu Xuất Kho
    private JTextField txtTimKiemBang;
    private JComboBox<String> cboLocTrangThai;
    private JTable tablePhuTung;
    private JTable tablePhieuXuat;
    private DefaultTableModel tableModel;

    // Các lớp DAO
    private PhieuXuatKhoDao phieuXuatKhoDao;
    private ChiTietPhieuXuatDao ctPhieuXuatDao;
    private PhuTungDao phuTungDao;
    private PhieuSuaChuaDao phieuSuaChuaDao;

    // Bộ nhớ đệm Phụ tùng để kiểm tra nhanh tồn kho và đơn giá
    private Map<String, PhuTung> mapPhuTung = new HashMap<>();
    private boolean dangCapNhatCombo = false;

    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private final String PLACEHOLDER_TIM_KIEM = "Tìm kiếm theo mã phiếu xuất, lý do xuất hoặc ghi chú (Nhấn Enter)...";

    public GaraLapPhieuXuatKho() {
        setTitle("Hệ thống Quản lý Gara Ô Tô");
        setSize(1340, 840);
        setLocationRelativeTo(null);
        setResizable(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        ketNoiCSDL();
        khoiTaoGiaoDien();

        // 1. Nạp dữ liệu Phụ tùng, Phiếu sửa chữa, Trạng thái vào ComboBox
        napDuLieuComboBox();
        // 2. Tải toàn bộ danh sách Phiếu xuất kho đã lập lên JTable
        taiDanhSachPhieuXuatLenBang("Tất cả");
        // 3. Khởi tạo form ở chế độ chuẩn bị lập phiếu xuất mới
        lamMoiFormLapPhieu();
    }

    private void ketNoiCSDL() {
        try {
            ConnectDB.getInstance().connect();
            phieuXuatKhoDao = new PhieuXuatKhoDao();
            ctPhieuXuatDao = new ChiTietPhieuXuatDao();
            phuTungDao = new PhuTungDao();
            phieuSuaChuaDao = new PhieuSuaChuaDao();
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

        // ==================== 3. NỘI DUNG CHÍNH: FORM LẬP PHIẾU XUẤT KHO ====================
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

        JLabel lblTieuDeCard = new JLabel("LẬP PHIẾU XUẤT KHO");
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

        // --- 3.2. Form thông tin xuất kho (Bám sát Entity PhieuXuatKho & ChiTietPhieuXuat) ---
        JLabel lblMoTa = new JLabel("Xuất phụ tùng theo phiếu sửa chữa (không vượt tồn khả dụng, ngày xuất tự động ghi nhận khi lưu).");
        lblMoTa.setFont(new Font("Arial", Font.PLAIN, 13));
        lblMoTa.setForeground(Color.GRAY);
        lblMoTa.setBorder(BorderFactory.createEmptyBorder(8, 0, 12, 0));

        txtMaPhieuXuat = new JTextField();
        txtMaPhieuXuat.setFont(new Font("Arial", Font.BOLD, 13));

        txtNgayXuat = new JTextField("Tự động ghi nhận khi lưu");
        txtNgayXuat.setEditable(false);
        txtNgayXuat.setBackground(new Color(245, 247, 250));
        txtNgayXuat.setForeground(new Color(90, 95, 105));

        cboTrangThai = new JComboBox<>();
        cboPhuTung = new JComboBox<>();
        txtSoLuong = new JTextField("1");
        txtDonGia = new JTextField("0");

        cboLyDoXuat = new JComboBox<>();
        cboLyDoXuat.setEditable(true); // Cho phép chọn phiếu sửa chữa hoặc tự gõ lý do xuất

        txtGhiChu = new JTextField();

        txtTongGiaTri = new JTextField("0 đ");
        txtTongGiaTri.setEditable(false);
        txtTongGiaTri.setBackground(new Color(245, 247, 250));
        txtTongGiaTri.setForeground(new Color(180, 30, 40));
        txtTongGiaTri.setFont(new Font("Arial", Font.BOLD, 14));

        // Khi chọn Phụ tùng -> Tự động điền đơn giá của phụ tùng đó
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

        panelGridFields.add(taoOnhapLieu("Mã phiếu xuất", txtMaPhieuXuat));
        panelGridFields.add(taoOnhapLieu("Ngày xuất (Tự động)", txtNgayXuat));
        panelGridFields.add(taoOCombo("Trạng thái", cboTrangThai));
        panelGridFields.add(taoOCombo("Phụ tùng xuất (Kèm tồn khả dụng)", cboPhuTung));
        panelGridFields.add(taoOnhapLieu("Số lượng xuất", txtSoLuong));
        panelGridFields.add(taoOnhapLieu("Đơn giá xuất (VNĐ)", txtDonGia));
        panelGridFields.add(taoOCombo("Lý do xuất / Phiếu sửa chữa", cboLyDoXuat));
        panelGridFields.add(taoOnhapLieu("Ghi chú (Người nhận / Người xuất)", txtGhiChu));
        panelGridFields.add(taoOnhapLieu("Tổng giá trị phiếu xuất", txtTongGiaTri));

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

        txtTimKiemBang.addActionListener(e -> {
            String tuKhoa = txtTimKiemBang.getText().trim();
            if (!tuKhoa.isEmpty() && !tuKhoa.equals(PLACEHOLDER_TIM_KIEM)) {
                timKiemPhieuXuatLenBang(tuKhoa);
            } else {
                taiDanhSachPhieuXuatLenBang(cboLocTrangThai.getSelectedItem().toString());
            }
        });

        JPanel pnlFilterRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        pnlFilterRight.setOpaque(false);
        JLabel lblTrangThaiLoc = new JLabel("Trạng thái:");
        lblTrangThaiLoc.setFont(new Font("Arial", Font.PLAIN, 12));

        cboLocTrangThai = new JComboBox<>(new String[]{"Tất cả"});
        cboLocTrangThai.setFont(new Font("Arial", Font.PLAIN, 12));
        cboLocTrangThai.addActionListener(e -> {
            if (!dangCapNhatCombo && cboLocTrangThai.getSelectedItem() != null) {
                taiDanhSachPhieuXuatLenBang(cboLocTrangThai.getSelectedItem().toString());
            }
        });

        pnlFilterRight.add(lblTrangThaiLoc);
        pnlFilterRight.add(cboLocTrangThai);

        panelSearchFilter.add(txtTimKiemBang, BorderLayout.CENTER);
        panelSearchFilter.add(pnlFilterRight, BorderLayout.EAST);

        // --- 3.4. Bảng Danh Sách Toàn Bộ Phiếu Xuất Kho Đã Lập ---
        String[] columnNames = {
                "Mã Phiếu Xuất", "Ngày Xuất", "Lý Do Xuất", "Phụ Tùng Xuất",
                "Tổng SL Xuất", "Tổng Giá Trị", "Ghi Chú", "Trạng Thái"
        };

        tableModel = new DefaultTableModel(null, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablePhuTung = new JTable(tableModel);
        tablePhieuXuat = tablePhuTung;
        tablePhieuXuat.setRowHeight(34);
        tablePhieuXuat.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tablePhieuXuat.getTableHeader().setBackground(new Color(240, 243, 246));
        tablePhieuXuat.setFont(new Font("Arial", Font.PLAIN, 12));
        tablePhieuXuat.setSelectionBackground(new Color(40, 100, 150));
        tablePhieuXuat.setSelectionForeground(Color.WHITE);
        tablePhieuXuat.setShowGrid(false);
        tablePhieuXuat.setIntercellSpacing(new Dimension(0, 0));

        // Khi click vào 1 dòng Phiếu xuất trên JTable -> Đổ dữ liệu lên Form phía trên
        tablePhieuXuat.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tablePhieuXuat.getSelectedRow();
                if (row >= 0) {
                    String maPX = tableModel.getValueAt(row, 0).toString();
                    hienThiPhieuXuatLenForm(maPX);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(tablePhieuXuat);
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
        JButton btnThemPhuTung = taoNutTrang("Thêm phụ tùng vào phiếu");
        JButton btnXacNhan = taoNutDo("Xác nhận xuất kho");

        btnTaoMoi.addActionListener(e -> {
            tablePhieuXuat.clearSelection();
            lamMoiFormLapPhieu();
        });

        btnXemChiTiet.addActionListener(e -> xemChiTietPhieuXuatPopup());
        btnThemPhuTung.addActionListener(e -> luuHoacThemPhuTungXuat(false));
        btnXacNhan.addActionListener(e -> luuHoacThemPhuTungXuat(true));

        panelBottomButtons.add(btnTaoMoi);
        panelBottomButtons.add(btnXemChiTiet);
        panelBottomButtons.add(btnThemPhuTung);
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
        if (phieuXuatKhoDao == null) return;
        try {
            dangCapNhatCombo = true;
            cboPhuTung.removeAllItems();
            cboLyDoXuat.removeAllItems();
            cboTrangThai.removeAllItems();
            cboLocTrangThai.removeAllItems();
            cboLocTrangThai.addItem("Tất cả");

            mapPhuTung.clear();

            // 1. Nạp danh sách Phụ tùng kèm số lượng tồn khả dụng
            for (PhuTung pt : phuTungDao.layDanhSachPhuTung()) {
                mapPhuTung.put(pt.getMaPhuTung(), pt);
                cboPhuTung.addItem(pt.getMaPhuTung() + " - " + pt.getTenPhuTung() + " (Tồn: " + pt.getSoLuongTon() + ")");
            }

            // 2. Nạp danh sách Phiếu sửa chữa vào ô Lý do xuất để chọn nhanh
            for (PhieuSuaChua psc : phieuSuaChuaDao.layDanhSachPhieuSuaChua()) {
                cboLyDoXuat.addItem("Xuất sửa chữa theo phiếu " + psc.getMaPhieuSuaChua());
            }

            // 3. Nạp các trạng thái thực tế từ bảng PhieuXuatKho trong CSDL
            Set<String> dsTrangThai = new LinkedHashSet<>();
            String sqlTT = "SELECT DISTINCT trangThai FROM dbo.PhieuXuatKho WHERE trangThai IS NOT NULL";
            try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sqlTT);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    dsTrangThai.add(rs.getString("trangThai"));
                }
            }
            if (dsTrangThai.isEmpty()) {
                dsTrangThai.add("Đã xuất kho");
                dsTrangThai.add("Chờ xuất kho");
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
     * Tải toàn bộ danh sách Phiếu xuất kho đã lập lên JTable
     */
    private void taiDanhSachPhieuXuatLenBang(String trangThaiLoc) {
        if (phieuXuatKhoDao == null) return;
        try {
            ArrayList<PhieuXuatKho> dsPhieu;
            if (trangThaiLoc == null || trangThaiLoc.equals("Tất cả")) {
                dsPhieu = phieuXuatKhoDao.layDanhSachPhieuXuat();
            } else {
                dsPhieu = phieuXuatKhoDao.timTheoTrangThai(trangThaiLoc);
            }
            doDuLieuPhieuXuatVaoBang(dsPhieu);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi tải danh sách phiếu xuất: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void timKiemPhieuXuatLenBang(String tuKhoa) {
        if (phieuXuatKhoDao == null) return;
        try {
            ArrayList<PhieuXuatKho> dsPhieu = phieuXuatKhoDao.timKiem(tuKhoa);
            doDuLieuPhieuXuatVaoBang(dsPhieu);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void doDuLieuPhieuXuatVaoBang(ArrayList<PhieuXuatKho> dsPhieu) throws SQLException {
        tableModel.setRowCount(0);

        for (PhieuXuatKho pxk : dsPhieu) {
            ArrayList<ChiTietPhieuXuat> dsCT = ctPhieuXuatDao.timTheoMaPhieuXuat(pxk.getMaPhieuXuat());

            int tongSL = 0;
            BigDecimal tongTien = BigDecimal.ZERO;
            StringBuilder tomTatPhuTung = new StringBuilder();

            for (ChiTietPhieuXuat ct : dsCT) {
                tongSL += ct.getSoLuong();
                BigDecimal thanhTien = ct.getDonGia().multiply(new BigDecimal(ct.getSoLuong()));
                tongTien = tongTien.add(thanhTien);

                PhuTung pt = mapPhuTung.get(ct.getMaPhuTung());
                String tenPT = pt != null ? pt.getTenPhuTung() : ct.getMaPhuTung();
                if (tomTatPhuTung.length() > 0) tomTatPhuTung.append(", ");
                tomTatPhuTung.append(tenPT).append(" (x").append(ct.getSoLuong()).append(")");
            }

            if (tomTatPhuTung.length() == 0) {
                tomTatPhuTung.append("(Chưa có chi tiết)");
            }

            String ngayXuatStr = pxk.getNgayXuat() != null ? pxk.getNgayXuat().format(dtf) : "";

            tableModel.addRow(new Object[]{
                    pxk.getMaPhieuXuat(),
                    ngayXuatStr,
                    pxk.getLyDoXuat() != null ? pxk.getLyDoXuat() : "",
                    tomTatPhuTung.toString(),
                    tongSL,
                    String.format("%,.0f đ", tongTien),
                    pxk.getGhiChu() != null ? pxk.getGhiChu() : "",
                    pxk.getTrangThai()
            });
        }
    }

    /**
     * Khởi tạo form trống với Mã phiếu xuất tự sinh để lập phiếu mới
     */
    private void lamMoiFormLapPhieu() {
        if (phieuXuatKhoDao == null) return;
        try {
            String maMoi = phieuXuatKhoDao.taoMaPhieuXuat();
            txtMaPhieuXuat.setText(maMoi);
            txtMaPhieuXuat.setEditable(true);
            txtNgayXuat.setText("Tự động ghi nhận khi lưu");
            txtSoLuong.setText("1");
            txtGhiChu.setText("");

            if (cboTrangThai.getItemCount() > 0) {
                cboTrangThai.setSelectedIndex(0);
            }
            if (cboLyDoXuat.getItemCount() > 0) {
                cboLyDoXuat.setSelectedIndex(0);
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
     * Khi click chọn 1 dòng trên JTable -> Đổ dữ liệu Phiếu xuất đó lên Form phía trên
     */
    private void hienThiPhieuXuatLenForm(String maPhieuXuat) {
        try {
            PhieuXuatKho pxk = phieuXuatKhoDao.timTheoMa(maPhieuXuat);
            if (pxk == null) return;

            dangCapNhatCombo = true;
            txtMaPhieuXuat.setText(pxk.getMaPhieuXuat());
            txtMaPhieuXuat.setEditable(false);
            txtNgayXuat.setText(pxk.getNgayXuat() != null ? pxk.getNgayXuat().format(dtf) : "");
            cboTrangThai.setSelectedItem(pxk.getTrangThai());
            cboLyDoXuat.setSelectedItem(pxk.getLyDoXuat() != null ? pxk.getLyDoXuat() : "");
            txtGhiChu.setText(pxk.getGhiChu() != null ? pxk.getGhiChu() : "");

            ArrayList<ChiTietPhieuXuat> dsCT = ctPhieuXuatDao.timTheoMaPhieuXuat(maPhieuXuat);
            BigDecimal tongGiaTri = BigDecimal.ZERO;

            for (ChiTietPhieuXuat ct : dsCT) {
                BigDecimal thanhTien = ct.getDonGia().multiply(new BigDecimal(ct.getSoLuong()));
                tongGiaTri = tongGiaTri.add(thanhTien);
            }

            if (!dsCT.isEmpty()) {
                ChiTietPhieuXuat ctDau = dsCT.get(0);
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
     * Lưu phiếu xuất kho mới hoặc thêm phụ tùng vào phiếu xuất đã chọn (kèm kiểm tra & trừ tồn kho)
     */
    private void luuHoacThemPhuTungXuat(boolean lamMoiSauKhiLuu) {
        try {
            String maPX = txtMaPhieuXuat.getText().trim();
            String trangThai = cboTrangThai.getSelectedItem() != null ? cboTrangThai.getSelectedItem().toString() : "Đã xuất kho";
            String maPT = layMaTuCombo(cboPhuTung);
            int soLuongXuat = Integer.parseInt(txtSoLuong.getText().trim());
            BigDecimal donGia = parseSoTien(txtDonGia.getText().trim());

            String lyDoXuat = cboLyDoXuat.getSelectedItem() != null ? cboLyDoXuat.getSelectedItem().toString().trim() : "";
            String ghiChu = txtGhiChu.getText().trim();

            if (soLuongXuat <= 0) {
                JOptionPane.showMessageDialog(this, "Số lượng xuất phải lớn hơn 0!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            PhuTung ptHienTai = phuTungDao.timTheoMa(maPT);
            if (ptHienTai == null) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy thông tin phụ tùng: " + maPT, "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            PhieuXuatKho pxkTonTai = phieuXuatKhoDao.timTheoMa(maPX);

            if (pxkTonTai == null) {
                // Kiểm tra tồn khả dụng trước khi tạo phiếu xuất mới
                if (soLuongXuat > ptHienTai.getSoLuongTon()) {
                    JOptionPane.showMessageDialog(this,
                            "Không thể xuất vượt tồn khả dụng!\n"
                                    + "Phụ tùng " + ptHienTai.getTenPhuTung() + " chỉ còn tồn: " + ptHienTai.getSoLuongTon() + " " + ptHienTai.getDonViTinh(),
                            "Vượt tồn khả dụng", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // 1. Tự động ghi nhận thời gian thực ngay lúc nhấn Lưu
                LocalDateTime ngayXuatTuDong = LocalDateTime.now();
                txtNgayXuat.setText(ngayXuatTuDong.format(dtf));

                PhieuXuatKho pxkMoi = new PhieuXuatKho(
                        maPX,
                        ngayXuatTuDong,
                        lyDoXuat.isEmpty() ? null : lyDoXuat,
                        trangThai,
                        ghiChu.isEmpty() ? null : ghiChu
                );

                if (!phieuXuatKhoDao.themPhieuXuat(pxkMoi)) {
                    JOptionPane.showMessageDialog(this, "Không thể tạo phiếu xuất kho mới!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // 2. Thêm Chi tiết phiếu xuất & Trừ tồn kho Phụ tùng
                ChiTietPhieuXuat ctMoi = new ChiTietPhieuXuat(maPX, maPT, soLuongXuat, donGia);
                ctPhieuXuatDao.themChiTiet(ctMoi);
                phuTungDao.giamTonKho(maPT, soLuongXuat);

                JOptionPane.showMessageDialog(this,
                        "Đã lập phiếu xuất kho " + maPX + " và trừ -" + soLuongXuat + " khỏi tồn kho phụ tùng " + maPT + "!\n"
                                + "Thời gian ghi nhận: " + ngayXuatTuDong.format(dtf));
            } else {
                // Cập nhật thông tin chung của Phiếu xuất kho đã tồn tại
                pxkTonTai.setLyDoXuat(lyDoXuat.isEmpty() ? null : lyDoXuat);
                pxkTonTai.setTrangThai(trangThai);
                pxkTonTai.setGhiChu(ghiChu.isEmpty() ? null : ghiChu);
                phieuXuatKhoDao.suaPhieuXuat(pxkTonTai);

                ChiTietPhieuXuat ctCu = ctPhieuXuatDao.timTheoKhoa(maPX, maPT);
                if (ctCu == null) {
                    if (soLuongXuat > ptHienTai.getSoLuongTon()) {
                        JOptionPane.showMessageDialog(this,
                                "Không thể xuất vượt tồn khả dụng! Tồn hiện tại của " + ptHienTai.getTenPhuTung() + ": " + ptHienTai.getSoLuongTon(),
                                "Vượt tồn khả dụng", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    ChiTietPhieuXuat ctMoi = new ChiTietPhieuXuat(maPX, maPT, soLuongXuat, donGia);
                    ctPhieuXuatDao.themChiTiet(ctMoi);
                    phuTungDao.giamTonKho(maPT, soLuongXuat);

                    JOptionPane.showMessageDialog(this,
                            "Đã thêm phụ tùng " + maPT + " (SL xuất: " + soLuongXuat + ") vào phiếu " + maPX + " và trừ tồn kho!");
                } else {
                    int chenhLech = soLuongXuat - ctCu.getSoLuong();
                    if (chenhLech > 0 && chenhLech > ptHienTai.getSoLuongTon()) {
                        JOptionPane.showMessageDialog(this,
                                "Số lượng xuất tăng thêm (" + chenhLech + ") vượt quá tồn khả dụng hiện tại (" + ptHienTai.getSoLuongTon() + ")!",
                                "Vượt tồn khả dụng", JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    ChiTietPhieuXuat ctSua = new ChiTietPhieuXuat(maPX, maPT, soLuongXuat, donGia);
                    ctPhieuXuatDao.suaChiTiet(ctSua);

                    if (chenhLech > 0) {
                        phuTungDao.giamTonKho(maPT, chenhLech);
                    } else if (chenhLech < 0) {
                        phuTungDao.tangTonKho(maPT, Math.abs(chenhLech));
                    }
                    JOptionPane.showMessageDialog(this, "Đã cập nhật phiếu xuất kho " + maPX + " thành công!");
                }
            }

            // Nạp lại danh sách Phụ tùng (cập nhật số tồn mới) và bảng Phiếu xuất
            napDuLieuComboBox();
            taiDanhSachPhieuXuatLenBang(cboLocTrangThai.getSelectedItem().toString());

            if (lamMoiSauKhiLuu) {
                lamMoiFormLapPhieu();
            } else {
                hienThiPhieuXuatLenForm(maPX);
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Số lượng và đơn giá phải là số hợp lệ!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi lưu phiếu xuất kho: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Mở hộp thoại xem chi tiết các phụ tùng trong một Phiếu xuất kho
     */
    private void xemChiTietPhieuXuatPopup() {
        String maPX = txtMaPhieuXuat.getText().trim();
        try {
            PhieuXuatKho pxk = phieuXuatKhoDao.timTheoMa(maPX);
            if (pxk == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một phiếu xuất đã lưu dưới bảng để xem chi tiết!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            ArrayList<ChiTietPhieuXuat> dsCT = ctPhieuXuatDao.timTheoMaPhieuXuat(maPX);
            String[] cols = {"Mã Phụ Tùng", "Tên Phụ Tùng", "Đơn Vị Tính", "Số Lượng Xuất", "Tồn Hiện Tại", "Đơn Giá Xuất", "Thành Tiền"};
            DefaultTableModel modelCT = new DefaultTableModel(null, cols) {
                @Override
                public boolean isCellEditable(int r, int c) {
                    return false;
                }
            };

            for (ChiTietPhieuXuat ct : dsCT) {
                PhuTung pt = mapPhuTung.get(ct.getMaPhuTung());
                String tenPT = pt != null ? pt.getTenPhuTung() : ct.getMaPhuTung();
                String dvt = pt != null ? pt.getDonViTinh() : "Cái";
                int tonHienTai = pt != null ? pt.getSoLuongTon() : 0;
                BigDecimal thanhTien = ct.getDonGia().multiply(new BigDecimal(ct.getSoLuong()));

                modelCT.addRow(new Object[]{
                        ct.getMaPhuTung(),
                        tenPT,
                        dvt,
                        ct.getSoLuong(),
                        tonHienTai,
                        String.format("%,.0f đ", ct.getDonGia()),
                        String.format("%,.0f đ", thanhTien)
                });
            }

            JTable tblCT = new JTable(modelCT);
            tblCT.setRowHeight(28);
            tblCT.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
            tblCT.setFont(new Font("Arial", Font.PLAIN, 12));

            JScrollPane sp = new JScrollPane(tblCT);
            sp.setPreferredSize(new Dimension(720, 240));

            JOptionPane.showMessageDialog(this, sp, "Chi tiết phiếu xuất kho: " + maPX, JOptionPane.PLAIN_MESSAGE);

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
        SwingUtilities.invokeLater(() -> new GaraLapPhieuXuatKho().setVisible(true));
    }
}