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
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import connectDB.ConnectDB;
import entity.HoaDon;
import entity.KhachHang;
import entity.NhanVien;
import entity.PhieuSuaChua;
import entity.Xe;
import service.HoaDonService;

public class GaraLapHoaDon extends JPanel {
    private static final long serialVersionUID = 1L;
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
    // Lớp Service xử lý nghiệp vụ
    private HoaDonService hoaDonService;
    // Bộ nhớ đệm hỗ trợ hiển thị nhanh tên lên JTable
    private Map<String, String> mapTenKhachHang = new HashMap<>();
    private Map<String, String> mapBienSoXe = new HashMap<>();
    private Map<String, String> mapTenNhanVien = new HashMap<>();
    private boolean dangCapNhatCombo = false;
    private BigDecimal tamTinhHienTai = BigDecimal.ZERO;
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public GaraLapHoaDon() {
        // Khởi tạo tầng Service
        hoaDonService = new HoaDonService();
        khoiTaoGiaoDien();
        // 1. Nạp dữ liệu vào các ComboBox trên form lập hóa đơn
        napDuLieuComboBox();
        // 2. Nạp toàn bộ danh sách Hóa đơn đã lập lên JTable
        taiDanhSachHoaDonLenBang("Tất cả");
        // 3. Khởi tạo form ở chế độ chuẩn bị lập hóa đơn mới
        lamMoiFormLapHoaDon();
    }

    private void khoiTaoGiaoDien() {
        setLayout(new BorderLayout());
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
        JButton btnXacNhan = taoNutDo("Xác nhận thanh toán");
        btnTaoMoi.addActionListener(e -> {
            tableHoaDon.clearSelection();
            lamMoiFormLapHoaDon();
        });
        btnLuuHoaDon.addActionListener(e -> luuHoaDonVaoCSDL());
        btnXacNhan.addActionListener(e -> xacNhanThanhToanHoaDon());
        panelBottomButtons.add(btnTaoMoi);
        panelBottomButtons.add(btnLuuHoaDon);
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
        // Menu, toolbar và footer thuộc GaraManHinhChinh.
        add(panelCenter, BorderLayout.CENTER);
    }

    // ==================== CÁC HÀM GỌI SERVICE & XỬ LÝ NGHIỆP VỤ ====================
    private void napDuLieuComboBox() {
        if (hoaDonService == null) return;
        try {
            dangCapNhatCombo = true;
            cboPhieuSuaChua.removeAllItems();
            cboKhachHang.removeAllItems();
            cboPhuongTien.removeAllItems();
            cboNhanVien.removeAllItems();
            mapTenKhachHang.clear();
            mapBienSoXe.clear();
            mapTenNhanVien.clear();
            for (PhieuSuaChua psc : hoaDonService.layDanhSachPhieuSuaChua()) {
                cboPhieuSuaChua.addItem(psc.getMaPhieuSuaChua() + " - " + psc.getLoaiPhieu() + " (" + psc.getTrangThai() + ")");
            }
            for (KhachHang kh : hoaDonService.layDanhSachKhachHang()) {
                mapTenKhachHang.put(kh.getMaKhachHang(), kh.getHoTen());
                cboKhachHang.addItem(kh.getMaKhachHang() + " - " + kh.getHoTen());
            }
            for (Xe xe : hoaDonService.layDanhSachXe()) {
                mapBienSoXe.put(xe.getMaPhuongTien(), xe.getBienSo() + " (" + xe.getMauXe() + ")");
                cboPhuongTien.addItem(xe.getMaPhuongTien() + " - " + xe.getBienSo() + " (" + xe.getMauXe() + ")");
            }
            for (NhanVien nv : hoaDonService.layDanhSachNhanVien()) {
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
        if (hoaDonService == null) return;
        try {
            tableModel.setRowCount(0);
            ArrayList<HoaDon> dsHoaDon = hoaDonService.layDanhSachHoaDonTheoLoc(trangThaiLoc);
            Map<String, BigDecimal> mapVat = hoaDonService.layTatCaVatTuDB();
            for (HoaDon hd : dsHoaDon) {
                BigDecimal vat = hd.getVat() != null ? hd.getVat() : mapVat.getOrDefault(hd.getMaHoaDon(), BigDecimal.ZERO);
                BigDecimal tongTien = hoaDonService.tinhTongTienHoaDon(hd.getMaHoaDon(), hd.getMaPhieuSuaChua(), vat);
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
            ArrayList<HoaDon> dsHoaDon = hoaDonService.timKiemHoaDon(tuKhoa);
            Map<String, BigDecimal> mapVat = hoaDonService.layTatCaVatTuDB();
            for (HoaDon hd : dsHoaDon) {
                BigDecimal vat = hd.getVat() != null ? hd.getVat() : mapVat.getOrDefault(hd.getMaHoaDon(), BigDecimal.ZERO);
                BigDecimal tongTien = hoaDonService.tinhTongTienHoaDon(hd.getMaHoaDon(), hd.getMaPhieuSuaChua(), vat);
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
            String maMoi = hoaDonService.taoMaHoaDonMoi();
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
            HoaDon hd = hoaDonService.timHoaDonTheoMa(maHoaDon);
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
                vat = hoaDonService.layTatCaVatTuDB().getOrDefault(maHoaDon, BigDecimal.ZERO);
            }
            txtVAT.setText(vat.stripTrailingZeros().toPlainString());
            cboTrangThai.setSelectedItem(hd.getTrangThai());
            tamTinhHienTai = hoaDonService.tinhTamTinh(hd.getMaHoaDon(), hd.getMaPhieuSuaChua());
            capNhatHienThiTongTien();
            dangCapNhatCombo = false;
        } catch (SQLException e) {
            dangCapNhatCombo = false;
            e.printStackTrace();
        }
    }

    private void tuDongDienTheoPhieuSuaChua(String maPSC) {
        String[] thongTin = hoaDonService.layThongTinLienKetTuPhieuSuaChua(maPSC);
        if (thongTin != null) {
            chonItemComboTheoMa(cboKhachHang, thongTin[0]);
            chonItemComboTheoMa(cboPhuongTien, thongTin[1]);
            if (thongTin[2] != null) {
                chonItemComboTheoMa(cboNhanVien, thongTin[2]);
            }
        }
        tamTinhHienTai = hoaDonService.tinhTamTinh(txtMaHoaDon.getText().trim(), maPSC);
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

            HoaDon hdTonTai = hoaDonService.timHoaDonTheoMa(maHD);
            if (hdTonTai == null) {
                // Kiểm tra trùng Phiếu sửa chữa
                HoaDon hdTheoPSC = hoaDonService.timHoaDonTheoMaPhieuSuaChua(maPSC);
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
                if (hoaDonService.themHoaDonMoi(hdMoi)) {
                    JOptionPane.showMessageDialog(this,
                            "Lập hóa đơn mới " + maHD + " thành công!\nThời gian ghi nhận: " + ngayLapTuDong.format(dtf));
                    taiDanhSachHoaDonLenBang(cboLocTrangThai.getSelectedItem().toString());
                    lamMoiFormLapHoaDon();
                } else {
                    JOptionPane.showMessageDialog(this, "Không thể thêm hóa đơn vào CSDL.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                if (hoaDonService.capNhatHoaDon(hdTonTai, maPSC, maKH, maPT, maNV, vat, trangThai)) {
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
            HoaDon hd = hoaDonService.timHoaDonTheoMa(maHD);
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
                if (hoaDonService.xacNhanThanhToan(maHD, hd.getTrangThai())) {
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
    private void capNhatHienThiTongTien() {
        try {
            BigDecimal vat = new BigDecimal(txtVAT.getText().trim());
            BigDecimal tongTien = hoaDonService.tinhTongTienTuTamTinh(tamTinhHienTai, vat);
            txtTongTien.setText(String.format("%,.0f đ", tongTien));
        } catch (Exception e) {
            txtTongTien.setText(String.format("%,.0f đ", tamTinhHienTai));
        }
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
        btn.setContentAreaFilled(false);
        btn.setOpaque(true);
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
        btn.setContentAreaFilled(false);
        btn.setForeground(Color.WHITE);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(9, 18, 9, 18));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // Chạy thử riêng. Khi chạy ứng dụng, dùng main.Main.
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            ConnectDB.getInstance().connect();
            System.out.println("Kết nối CSDL thành công!");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Lỗi kết nối CSDL: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Lập hóa đơn - NIGHT INVADER");
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setContentPane(new GaraLapHoaDon());
            frame.setSize(1340, 840);
            frame.setLocationRelativeTo(null);
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            frame.setVisible(true);
        });
    }
}