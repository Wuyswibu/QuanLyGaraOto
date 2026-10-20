package ui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import dao.HangMucDichVuDao;
import dao.KhachHangDao;
import dao.PhieuSuaChuaDao;
import dao.PhieuTiepNhanDao;
import dao.XeDao;
import entity.HangMucDichVu;
import entity.KhachHang;
import entity.PhieuSuaChua;
import entity.PhieuTiepNhan;
import entity.Xe;

public class LapPhieuSuaChuaFrame extends JFrame implements ActionListener {
    private static final long serialVersionUID = 1L;

    private static final Color CHU = new Color(35, 41, 51);
    private static final Color CHU_PHU = new Color(112, 124, 139);
    private static final Color VIEN = new Color(216, 222, 230);
    private static final Color DO = new Color(188, 25, 58);

    private static final DateTimeFormatter DINH_DANG = DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm");

    // ==================== DAO VÀ DỮ LIỆU ====================

    private final PhieuSuaChuaDao phieuSuaChuaDao = new PhieuSuaChuaDao();
    private final PhieuTiepNhanDao phieuTiepNhanDao = new PhieuTiepNhanDao();
    private final HangMucDichVuDao hangMucDichVuDao = new HangMucDichVuDao();
    private final XeDao xeDao = new XeDao();
    private final KhachHangDao khachHangDao = new KhachHangDao();

    private final ArrayList<PhieuSuaChua> danhSachPhieu = new ArrayList<>();
    private final ArrayList<PhieuTiepNhan> danhSachTiepNhan = new ArrayList<>();

    private final Map<String, PhieuTiepNhan> tiepNhanTheoMa = new LinkedHashMap<>();
    private final Map<String, Xe> xeTheoMa = new LinkedHashMap<>();
    private final Map<String, KhachHang> khachHangTheoMa = new LinkedHashMap<>();

    private boolean dangHienThi;

    // ==================== CÁC THÀNH PHẦN GIAO DIỆN ====================

    private final JTextField txtMaPhieu = new JTextField();
    private final JComboBox<String> cboTiepNhan = new JComboBox<>();
    private final JTextField txtBienSo = new JTextField();
    private final JTextField txtMauXe = new JTextField();
    private final JTextField txtKhachHang = new JTextField();
    private final JTextField txtNgayLap = new JTextField();
    private final JTextField txtTrangThai = new JTextField();

    private final JComboBox<String> cboLoaiPhieu = new JComboBox<>(
            new String[]{"Sửa chữa", "Bảo dưỡng", "Bảo hành"});

    private final JTextArea txtYeuCau = new JTextArea(3, 20);
    private final JTextArea txtChanDoan = new JTextArea(3, 20);
    private final JTextArea txtGhiChu = new JTextArea(3, 20);

    private final JTextField txtTimKiem = new JTextField(24);

    private final JButton btnTimKiem = taoNut("Tìm kiếm", true);
    private final JButton btnThem = taoNut("Thêm", true);
    private final JButton btnSua = taoNut("Sửa", false);
    private final JButton btnLamMoi = taoNut("Làm mới", false);

    private final JLabel lblSoPhieu = new JLabel("Số phiếu: 0");
    private final JLabel lblTong = new JLabel("Tổng chi phí dự kiến: 0 đ");

    private final DefaultTableModel modelPhieu = new DefaultTableModel(
            new String[]{
                "Mã phiếu sửa chữa", "Mã tiếp nhận", "Biển số",
                "Khách hàng", "Ngày lập", "Loại phiếu", "Trạng thái"
            }, 0) {
        private static final long serialVersionUID = 1L;

        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private final DefaultTableModel modelHangMuc = new DefaultTableModel(
            new String[]{
                "Mã hạng mục", "Mã dịch vụ", "Tên hạng mục", "Mô tả",
                "Chi phí dự kiến", "Chi phí thực tế", "Trạng thái"
            }, 0) {
        private static final long serialVersionUID = 1L;

        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }

        @Override
        public Class<?> getColumnClass(int column) {
            return column == 4 || column == 5 ? BigDecimal.class : String.class;
        }
    };

    private final JTable tablePhieu = new JTable(modelPhieu);
    private final JTable tableHangMuc = new JTable(modelHangMuc);

    private final TableRowSorter<DefaultTableModel> sorterPhieu = new TableRowSorter<>(modelPhieu);

    // ==================== CONSTRUCTOR ====================

    public LapPhieuSuaChuaFrame() {
        setTitle("Lập phiếu sửa chữa - Night Invader");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        setResizable(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        khoiTaoGiaoDien();

        try {
            taiDuLieuTuDatabase();
        } catch (SQLException | IllegalArgumentException ex) {
            baoLoi("Không tải được dữ liệu.", ex);
        }
    }

    // ==================== KHỞI TẠO GIAO DIỆN ====================

    private void khoiTaoGiaoDien() {
        setLayout(new BorderLayout());

        // 1. MENU BAR

        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(new Color(245, 247, 250));
        menuBar.setBorder(new EmptyBorder(10, 15, 10, 15));

        Font menuFont = new Font("Arial", Font.BOLD, 16);

        JMenu logo = new JMenu("NIGHT INVADER");
        logo.setFont(new Font("Arial", Font.BOLD, 15));
        logo.setForeground(new Color(210, 30, 30));
        logo.setMargin(new Insets(5, 10, 5, 10));
        menuBar.add(logo);

        String[] tenMenu = {
            "Hệ thống", "Khách hàng ", "Xe ", "Dịch vụ ",
            "Phụ tùng", "Kho hàng ", "Hóa đơn ", "Thống kê ", "Nhân viên "
        };

        String[][] cacItem = {
            {"Tài khoản cá nhân", "Đổi mật khẩu", "-", "Đăng xuất"},
            {"Quản lý khách hàng"},
            {"Quản lý xe", "Tra cứu xe", "Lịch sử sửa chữa"},
            {
                "Quản lý dịch vụ", "Thêm dịch vụ", "Sửa dịch vụ", "Xóa dịch vụ",
                "-", "Lập phiếu tiếp nhận xe", "Lập phiếu sửa chữa / Báo giá",
                "Cập nhật tình trạng sửa xe"
            },
            {"Quản lý phụ tùng"},
            {"Lập phiếu nhập kho", "Lập phiếu xuất kho"},
            {"Lập hóa đơn", "Tra cứu hóa đơn"},
            {
                "Thống kê doanh thu", "Thống kê tồn kho",
                "Phụ tùng bán chạy", "Phụ tùng bán chậm"
            },
            {"Quản lý nhân sự", "Phân công kỹ thuật viên"}
        };

        for (int i = 0; i < tenMenu.length; i++) {
            JMenu menu = taoMenuDropdown(tenMenu[i], cacItem[i]);
            menu.setFont(menuFont);
            menu.setMargin(new Insets(5, 10, 5, 10));

            if (i == 3) {
                menu.setForeground(DO);
                menu.setBorder(BorderFactory.createMatteBorder(0, 0, 3, 0, DO));
            }

            menuBar.add(menu);
        }

        JMenu taiKhoan = new JMenu("Xin chào, Chủ Gara");
        taiKhoan.setFont(menuFont);
        taiKhoan.setMargin(new Insets(5, 10, 5, 10));
        taiKhoan.setIcon(loadIcon("/img/icon_nguoi.jpg", 20, 20));

        menuBar.add(Box.createHorizontalGlue());
        menuBar.add(taiKhoan);
        setJMenuBar(menuBar);

        // 2. THANH CÔNG CỤ NHANH

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        toolbar.setBackground(Color.WHITE);
        toolbar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 224, 230)));

        toolbar.add(taoNutNhanh("Tiếp nhận xe", "F2", loadIcon("/img/icon_xe.png", 22, 22)));
        toolbar.add(taoNutNhanh("Tình trạng sửa xe", "F5", loadIcon("/img/icon-vector.png", 22, 22)));
        toolbar.add(taoNutNhanh("Lập hóa đơn", "F3", loadIcon("/img/icon_hoadon.png", 22, 22)));
        toolbar.add(taoNutNhanh("Nhập kho", "F4", loadIcon("/img/icon_nhapkho.jpg", 22, 22)));

        JPanel thongTinPhu = new JPanel(new GridLayout(2, 1));
        thongTinPhu.setBackground(Color.WHITE);

        DateTimeFormatter dinhDangNgay = DateTimeFormatter.ofPattern(
                "EEEE, dd/MM/uuuu", Locale.forLanguageTag("vi-VN"));

        JLabel ngay = new JLabel(LocalDate.now().format(dinhDangNgay), JLabel.RIGHT);
        ngay.setFont(new Font("Arial", Font.PLAIN, 12));

        JLabel chiNhanh = new JLabel("Chi nhánh: Gara chính", JLabel.RIGHT);
        chiNhanh.setFont(new Font("Arial", Font.PLAIN, 11));
        chiNhanh.setForeground(Color.GRAY);

        thongTinPhu.add(ngay);
        thongTinPhu.add(chiNhanh);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(new EmptyBorder(0, 10, 0, 15));
        header.add(toolbar, BorderLayout.CENTER);
        header.add(thongTinPhu, BorderLayout.EAST);

        // 3. NỘI DUNG LẬP PHIẾU SỬA CHỮA

        JPanel panelCenter = new JPanel(new BorderLayout());
        panelCenter.setBackground(new Color(216, 220, 228));
        panelCenter.setBorder(new EmptyBorder(20, 55, 15, 55));

        JPanel khung = new JPanel(new BorderLayout());
        khung.setBackground(Color.WHITE);
        khung.setBorder(BorderFactory.createLineBorder(VIEN));

        JLabel tieuDe = new JLabel("LẬP PHIẾU SỬA CHỮA");
        tieuDe.setOpaque(true);
        tieuDe.setBackground(new Color(246, 247, 250));
        tieuDe.setForeground(CHU);
        tieuDe.setFont(new Font("Arial", Font.BOLD, 16));
        tieuDe.setBorder(new EmptyBorder(10, 16, 10, 16));
        khung.add(tieuDe, BorderLayout.NORTH);

        JPanel noiDung = new JPanel(new BorderLayout(0, 14));
        noiDung.setBackground(Color.WHITE);
        noiDung.setBorder(new EmptyBorder(12, 18, 16, 18));

        // FORM THÔNG TIN

        JPanel phanTren = new JPanel(new BorderLayout(0, 12));
        phanTren.setOpaque(false);

        JPanel form = new JPanel(new GridLayout(4, 2, 24, 8));
        form.setOpaque(false);

        themO(form, "Mã phiếu sửa chữa", txtMaPhieu);
        themO(form, "Phiếu tiếp nhận", cboTiepNhan);
        themO(form, "Biển số", txtBienSo);
        themO(form, "Mẫu xe", txtMauXe);
        themO(form, "Khách hàng", txtKhachHang);
        themO(form, "Ngày lập", txtNgayLap);
        themO(form, "Loại phiếu", cboLoaiPhieu);
        themO(form, "Trạng thái", txtTrangThai);

        txtMaPhieu.setEditable(false);
        txtBienSo.setEditable(false);
        txtMauXe.setEditable(false);
        txtKhachHang.setEditable(false);
        txtNgayLap.setEditable(false);

        phanTren.add(form, BorderLayout.CENTER);

        JPanel ghiNhan = new JPanel(new GridLayout(1, 3, 16, 0));
        ghiNhan.setOpaque(false);

        themVanBan(ghiNhan, "Yêu cầu khách hàng", txtYeuCau);
        themVanBan(ghiNhan, "Chẩn đoán", txtChanDoan);
        themVanBan(ghiNhan, "Ghi chú", txtGhiChu);

        txtYeuCau.setEditable(false);

        phanTren.add(ghiNhan, BorderLayout.SOUTH);
        noiDung.add(phanTren, BorderLayout.NORTH);

        // DANH SÁCH PHIẾU

        JPanel panelDanhSach = new JPanel(new BorderLayout(0, 8));
        panelDanhSach.setBackground(Color.WHITE);

        JPanel thanhDanhSach = new JPanel(new BorderLayout(10, 0));
        thanhDanhSach.setBackground(new Color(237, 241, 245));
        thanhDanhSach.setBorder(new EmptyBorder(6, 10, 6, 10));

        JLabel lblDanhSach = new JLabel("DANH SÁCH PHIẾU SỬA CHỮA");
        lblDanhSach.setFont(new Font("Arial", Font.BOLD, 12));
        lblDanhSach.setForeground(CHU);

        JPanel panelTimKiem = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        panelTimKiem.setOpaque(false);

        txtTimKiem.setFont(new Font("Arial", Font.PLAIN, 13));
        txtTimKiem.setPreferredSize(new Dimension(250, 30));
        txtTimKiem.setToolTipText("Nhập mã phiếu, mã tiếp nhận, biển số hoặc tên khách hàng");

        panelTimKiem.add(txtTimKiem);
        panelTimKiem.add(btnTimKiem);

        thanhDanhSach.add(lblDanhSach, BorderLayout.WEST);
        thanhDanhSach.add(panelTimKiem, BorderLayout.EAST);

        caiDatBang(tablePhieu);
        tablePhieu.setRowSorter(sorterPhieu);

        int[] rongPhieu = {150, 120, 110, 210, 150, 110, 140};
        for (int i = 0; i < rongPhieu.length; i++) {
            tablePhieu.getColumnModel().getColumn(i).setPreferredWidth(rongPhieu[i]);
        }

        JScrollPane cuonPhieu = new JScrollPane(tablePhieu);
        cuonPhieu.setBorder(BorderFactory.createLineBorder(VIEN));
        cuonPhieu.getViewport().setBackground(Color.WHITE);

        lblSoPhieu.setFont(new Font("Arial", Font.PLAIN, 12));
        lblSoPhieu.setForeground(CHU_PHU);

        panelDanhSach.add(thanhDanhSach, BorderLayout.NORTH);
        panelDanhSach.add(cuonPhieu, BorderLayout.CENTER);
        panelDanhSach.add(lblSoPhieu, BorderLayout.SOUTH);

        // HẠNG MỤC CỦA PHIẾU ĐANG CHỌN

        JPanel panelHangMuc = new JPanel(new BorderLayout(0, 8));
        panelHangMuc.setBackground(Color.WHITE);

        JLabel lblHangMuc = new JLabel("HẠNG MỤC DỊCH VỤ CỦA PHIẾU ĐANG CHỌN");
        lblHangMuc.setOpaque(true);
        lblHangMuc.setBackground(new Color(237, 241, 245));
        lblHangMuc.setForeground(CHU);
        lblHangMuc.setFont(new Font("Arial", Font.BOLD, 12));
        lblHangMuc.setBorder(new EmptyBorder(10, 10, 10, 10));

        caiDatBang(tableHangMuc);

        int[] rongHangMuc = {120, 100, 190, 230, 140, 140, 130};
        for (int i = 0; i < rongHangMuc.length; i++) {
            tableHangMuc.getColumnModel().getColumn(i).setPreferredWidth(rongHangMuc[i]);
        }

        JScrollPane cuonHangMuc = new JScrollPane(tableHangMuc);
        cuonHangMuc.setBorder(BorderFactory.createLineBorder(VIEN));
        cuonHangMuc.getViewport().setBackground(Color.WHITE);

        lblTong.setFont(new Font("Arial", Font.BOLD, 13));
        lblTong.setForeground(CHU);
        lblTong.setHorizontalAlignment(SwingConstants.RIGHT);

        panelHangMuc.add(lblHangMuc, BorderLayout.NORTH);
        panelHangMuc.add(cuonHangMuc, BorderLayout.CENTER);
        panelHangMuc.add(lblTong, BorderLayout.SOUTH);

        JSplitPane chiaBang = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT, panelDanhSach, panelHangMuc);
        chiaBang.setBorder(null);
        chiaBang.setResizeWeight(0.55);
        chiaBang.setDividerSize(8);
        chiaBang.setContinuousLayout(true);

        panelDanhSach.setMinimumSize(new Dimension(0, 130));
        panelHangMuc.setMinimumSize(new Dimension(0, 130));

        noiDung.add(chiaBang, BorderLayout.CENTER);

        // CÁC NÚT CHỨC NĂNG

        JPanel thaoTac = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        thaoTac.setOpaque(false);
        thaoTac.add(btnThem);
        thaoTac.add(btnSua);
        thaoTac.add(btnLamMoi);

        noiDung.add(thaoTac, BorderLayout.SOUTH);
        khung.add(noiDung, BorderLayout.CENTER);
        panelCenter.add(khung, BorderLayout.CENTER);

        // 4. FOOTER

        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(new Color(245, 247, 250));
        footer.setBorder(new EmptyBorder(8, 15, 8, 15));

        JLabel trangThai = new JLabel("Đang hoạt động | Vai trò: Chủ Gara | Chi nhánh: Gara chính");
        trangThai.setFont(new Font("Arial", Font.PLAIN, 12));
        trangThai.setForeground(Color.DARK_GRAY);

        JLabel banQuyen = new JLabel("NIGHT INVADER • Gara ô tô");
        banQuyen.setFont(new Font("Arial", Font.PLAIN, 12));
        banQuyen.setForeground(Color.GRAY);

        footer.add(trangThai, BorderLayout.WEST);
        footer.add(banQuyen, BorderLayout.EAST);

        // GẮN VÀO FRAME

        add(header, BorderLayout.NORTH);
        add(panelCenter, BorderLayout.CENTER);
        add(footer, BorderLayout.SOUTH);

        // ĐĂNG KÝ SỰ KIỆN

        btnThem.addActionListener(this);
        btnSua.addActionListener(this);
        btnLamMoi.addActionListener(this);
        btnTimKiem.addActionListener(this);
        txtTimKiem.addActionListener(this);
        cboTiepNhan.addActionListener(this);

        tablePhieu.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && !dangHienThi) {
                hienThiPhieuDangChon();
            }
        });

        xoaThongTin();
        capNhatTrangThaiForm();
    }

    // ==================== XỬ LÝ SỰ KIỆN ====================

    @Override
    public void actionPerformed(ActionEvent e) {
        Object nguon = e.getSource();

        if (nguon == cboTiepNhan) {
            if (dangHienThi || layPhieuDangChon() != null) {
                return;
            }

            PhieuTiepNhan tiepNhan = layTiepNhanDangChon();
            hienThiThongTinXe(tiepNhan);

        } else if (nguon == btnThem || nguon == btnSua) {
            boolean them = nguon == btnThem;
            PhieuSuaChua phieuDangChon = layPhieuDangChon();

            if (them && phieuDangChon != null) {
                JOptionPane.showMessageDialog(this, "Bấm Làm mới trước khi nhập phiếu mới.");
                return;
            }

            if (!them && phieuDangChon == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn phiếu cần sửa.");
                return;
            }

            if (!validData()) {
                return;
            }

            String maDaLuu;

            try {
                String maTiepNhan;
                String maPhieu;
                String trangThai;
                LocalDateTime ngayLap;

                if (them) {
                    PhieuTiepNhan tiepNhan = layTiepNhanDangChon();
                    maTiepNhan = tiepNhan.getMaTiepNhan();

                    PhieuTiepNhan tiepNhanMoiNhat = phieuTiepNhanDao.timTheoMa(maTiepNhan);

                    if (tiepNhanMoiNhat == null) {
                        JOptionPane.showMessageDialog(this, "Phiếu tiếp nhận không còn tồn tại.");
                        return;
                    }

                    if (!"Đã tiếp nhận".equals(tiepNhanMoiNhat.getTrangThai())) {
                        JOptionPane.showMessageDialog(
                                this, "Chỉ lập phiếu sửa chữa cho phiếu có trạng thái Đã tiếp nhận.");
                        return;
                    }

                    PhieuSuaChua phieuCu = phieuSuaChuaDao.timTheoMaTiepNhan(maTiepNhan);

                    if (phieuCu != null) {
                        JOptionPane.showMessageDialog(
                                this,
                                "Phiếu tiếp nhận này đã có phiếu sửa chữa: "
                                        + phieuCu.getMaPhieuSuaChua());
                        return;
                    }

                    maPhieu = phieuSuaChuaDao.taoMaPhieuSuaChua();
                    ngayLap = LocalDateTime.now();
                    trangThai = "Chờ sửa chữa";

                } else {
                    // Giữ nguyên liên kết tiếp nhận và ngày lập khi sửa.
                    maTiepNhan = phieuDangChon.getMaTiepNhan();
                    maPhieu = phieuDangChon.getMaPhieuSuaChua();
                    ngayLap = phieuDangChon.getNgayLap();
                    trangThai = txtTrangThai.getText().trim();
                }

                PhieuSuaChua phieu = new PhieuSuaChua(
                        maPhieu,
                        maTiepNhan,
                        ngayLap,
                        chuoiHoacNull(txtChanDoan.getText()),
                        (String) cboLoaiPhieu.getSelectedItem(),
                        trangThai,
                        chuoiHoacNull(txtGhiChu.getText()));

                boolean thanhCong;

                if (them) {
                    thanhCong = phieuSuaChuaDao.themPhieuSuaChua(phieu);
                } else {
                    thanhCong = phieuSuaChuaDao.suaPhieuSuaChua(phieu);
                }

                if (!thanhCong) {
                    JOptionPane.showMessageDialog(this, "Không lưu được phiếu sửa chữa.");
                    return;
                }

                maDaLuu = maPhieu;

            } catch (SQLException | IllegalArgumentException ex) {
                baoLoi("Không lưu được phiếu sửa chữa.", ex);
                return;
            }

            JOptionPane.showMessageDialog(
                    this, them ? "Thêm phiếu sửa chữa thành công." : "Cập nhật phiếu sửa chữa thành công.");

            try {
                txtTimKiem.setText("");
                sorterPhieu.setRowFilter(null);
                taiDuLieuTuDatabase();
                chonPhieuTheoMa(maDaLuu);
            } catch (SQLException | IllegalArgumentException ex) {
                baoLoi("Phiếu đã lưu nhưng chưa tải lại được danh sách. Bấm Làm mới để thử lại.", ex);
            }

        } else if (nguon == btnLamMoi) {
            try {
                taiDuLieuTuDatabase();
                txtTimKiem.setText("");
                sorterPhieu.setRowFilter(null);
                tablePhieu.clearSelection();
                xoaThongTin();
                capNhatTrangThaiForm();
                capNhatSoLuong();
                cboTiepNhan.requestFocusInWindow();
            } catch (SQLException | IllegalArgumentException ex) {
                baoLoi("Không tải lại được dữ liệu.", ex);
            }

        } else if (nguon == btnTimKiem || nguon == txtTimKiem) {
            String tuKhoa = txtTimKiem.getText().trim();

            tablePhieu.clearSelection();

            if (tuKhoa.isEmpty()) {
                sorterPhieu.setRowFilter(null);
            } else {
                sorterPhieu.setRowFilter(RowFilter.regexFilter(
                        "(?iu)" + Pattern.quote(tuKhoa), 0, 1, 2, 3));
            }

            xoaThongTin();
            capNhatTrangThaiForm();
            capNhatSoLuong();
        }
    }

    // ==================== KIỂM TRA DỮ LIỆU ====================

    private boolean validData() {
        PhieuSuaChua phieuDangChon = layPhieuDangChon();
        PhieuTiepNhan tiepNhan = layTiepNhanDangChon();

        if (phieuDangChon == null) {
            if (tiepNhan == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn phiếu tiếp nhận.");
                cboTiepNhan.requestFocusInWindow();
                return false;
            }

            if (!"Đã tiếp nhận".equals(tiepNhan.getTrangThai())) {
                JOptionPane.showMessageDialog(
                        this, "Chỉ lập phiếu sửa chữa cho phiếu có trạng thái Đã tiếp nhận.");
                return false;
            }

            Xe xe = xeTheoMa.get(tiepNhan.getMaPhuongTien());

            if (xe == null || !khachHangTheoMa.containsKey(xe.getMaKhachHang())) {
                JOptionPane.showMessageDialog(
                        this, "Không tìm thấy thông tin xe hoặc khách hàng. Hãy bấm Làm mới.");
                return false;
            }
        }

        if (cboLoaiPhieu.getSelectedIndex() < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn loại phiếu.");
            cboLoaiPhieu.requestFocusInWindow();
            return false;
        }

        if (txtChanDoan.getText().trim().length() > 2000) {
            JOptionPane.showMessageDialog(this, "Chẩn đoán tối đa 2000 ký tự.");
            txtChanDoan.requestFocusInWindow();
            return false;
        }

        if (txtGhiChu.getText().trim().length() > 1000) {
            JOptionPane.showMessageDialog(this, "Ghi chú tối đa 1000 ký tự.");
            txtGhiChu.requestFocusInWindow();
            return false;
        }

        if (phieuDangChon != null) {
            String trangThai = txtTrangThai.getText().trim();

            if (trangThai.isEmpty() || trangThai.length() > 40) {
                JOptionPane.showMessageDialog(this, "Trạng thái không được rỗng và tối đa 40 ký tự.");
                txtTrangThai.requestFocusInWindow();
                return false;
            }
        }

        return true;
    }

    // ==================== TẢI DỮ LIỆU TỪ SQL ====================

    private void taiDuLieuTuDatabase() throws SQLException {
        ArrayList<PhieuSuaChua> dsPhieu = phieuSuaChuaDao.layDanhSachPhieuSuaChua();
        ArrayList<PhieuTiepNhan> dsTiepNhan = phieuTiepNhanDao.layDanhSachPhieuTiepNhan();
        ArrayList<Xe> dsXe = xeDao.layDanhSachXe();
        ArrayList<KhachHang> dsKhachHang = khachHangDao.layDanhSachKhachHang();

        dangHienThi = true;

        try {
            tablePhieu.clearSelection();
            modelPhieu.setRowCount(0);

            danhSachPhieu.clear();
            danhSachPhieu.addAll(dsPhieu);

            danhSachTiepNhan.clear();
            danhSachTiepNhan.addAll(dsTiepNhan);

            tiepNhanTheoMa.clear();
            for (PhieuTiepNhan phieu : dsTiepNhan) {
                tiepNhanTheoMa.put(phieu.getMaTiepNhan(), phieu);
            }

            xeTheoMa.clear();
            for (Xe xe : dsXe) {
                xeTheoMa.put(xe.getMaPhuongTien(), xe);
            }

            khachHangTheoMa.clear();
            for (KhachHang khachHang : dsKhachHang) {
                khachHangTheoMa.put(khachHang.getMaKhachHang(), khachHang);
            }

            cboTiepNhan.removeAllItems();
            cboTiepNhan.addItem("-- Chọn phiếu tiếp nhận --");

            for (PhieuTiepNhan tiepNhan : danhSachTiepNhan) {
                Xe xe = xeTheoMa.get(tiepNhan.getMaPhuongTien());

                cboTiepNhan.addItem(
                        tiepNhan.getMaTiepNhan()
                                + " • "
                                + (xe == null ? tiepNhan.getMaPhuongTien() : xe.getBienSo()));
            }

            for (PhieuSuaChua phieu : danhSachPhieu) {
                PhieuTiepNhan tiepNhan = tiepNhanTheoMa.get(phieu.getMaTiepNhan());

                Xe xe = tiepNhan == null
                        ? null
                        : xeTheoMa.get(tiepNhan.getMaPhuongTien());

                KhachHang khachHang = xe == null
                        ? null
                        : khachHangTheoMa.get(xe.getMaKhachHang());

                modelPhieu.addRow(new Object[]{
                    phieu.getMaPhieuSuaChua(),
                    phieu.getMaTiepNhan(),
                    xe == null ? "" : xe.getBienSo(),
                    khachHang == null ? "" : khachHang.getHoTen(),
                    phieu.getNgayLap() == null ? "" : phieu.getNgayLap().format(DINH_DANG),
                    phieu.getLoaiPhieu(),
                    phieu.getTrangThai()
                });
            }

            xoaThongTin();

        } finally {
            dangHienThi = false;
        }

        capNhatSoLuong();
        capNhatTrangThaiForm();
    }

    // ==================== HIỂN THỊ DỮ LIỆU ====================

    private PhieuSuaChua layPhieuDangChon() {
        int dong = tablePhieu.getSelectedRow();

        if (dong < 0) {
            return null;
        }

        int dongModel = tablePhieu.convertRowIndexToModel(dong);

        if (dongModel >= danhSachPhieu.size()) {
            return null;
        }

        return danhSachPhieu.get(dongModel);
    }

    private PhieuTiepNhan layTiepNhanDangChon() {
        int viTri = cboTiepNhan.getSelectedIndex() - 1;

        if (viTri < 0 || viTri >= danhSachTiepNhan.size()) {
            return null;
        }

        return danhSachTiepNhan.get(viTri);
    }

    private void hienThiPhieuDangChon() {
        PhieuSuaChua phieu = layPhieuDangChon();

        if (phieu == null) {
            xoaThongTin();
            capNhatTrangThaiForm();
            return;
        }

        dangHienThi = true;

        try {
            xoaThongTin();

            txtMaPhieu.setText(phieu.getMaPhieuSuaChua());
            txtNgayLap.setText(phieu.getNgayLap() == null ? "" : phieu.getNgayLap().format(DINH_DANG));
            cboLoaiPhieu.setSelectedItem(phieu.getLoaiPhieu());
            txtTrangThai.setText(phieu.getTrangThai());
            txtChanDoan.setText(phieu.getChanDoan());
            txtGhiChu.setText(phieu.getGhiChu());

            for (int i = 0; i < danhSachTiepNhan.size(); i++) {
                if (danhSachTiepNhan.get(i).getMaTiepNhan().equals(phieu.getMaTiepNhan())) {
                    cboTiepNhan.setSelectedIndex(i + 1);
                    break;
                }
            }

            hienThiThongTinXe(tiepNhanTheoMa.get(phieu.getMaTiepNhan()));

            txtChanDoan.setCaretPosition(0);
            txtGhiChu.setCaretPosition(0);

        } finally {
            dangHienThi = false;
        }

        capNhatTrangThaiForm();
        taiHangMucTheoPhieu(phieu.getMaPhieuSuaChua());
    }

    private void hienThiThongTinXe(PhieuTiepNhan tiepNhan) {
        txtBienSo.setText("");
        txtMauXe.setText("");
        txtKhachHang.setText("");
        txtYeuCau.setText("");

        if (tiepNhan == null) {
            return;
        }

        txtYeuCau.setText(tiepNhan.getYeuCauKhachHang());
        txtYeuCau.setCaretPosition(0);

        Xe xe = xeTheoMa.get(tiepNhan.getMaPhuongTien());

        if (xe == null) {
            return;
        }

        txtBienSo.setText(xe.getBienSo());
        txtMauXe.setText(xe.getMauXe());

        KhachHang khachHang = khachHangTheoMa.get(xe.getMaKhachHang());

        if (khachHang != null) {
            txtKhachHang.setText(khachHang.getMaKhachHang() + " • " + khachHang.getHoTen());
        }
    }

    private void taiHangMucTheoPhieu(String maPhieuSuaChua) {
        modelHangMuc.setRowCount(0);
        lblTong.setText("Tổng chi phí dự kiến: 0 đ");

        try {
            ArrayList<HangMucDichVu> danhSach = hangMucDichVuDao.timTheoMaPhieuSuaChua(maPhieuSuaChua);
            BigDecimal tong = BigDecimal.ZERO;

            for (HangMucDichVu hangMuc : danhSach) {
                modelHangMuc.addRow(new Object[]{
                    hangMuc.getMaHangMuc(),
                    hangMuc.getMaDichVu(),
                    hangMuc.getTenHangMuc(),
                    hangMuc.getMoTa(),
                    hangMuc.getChiPhiDuKien(),
                    hangMuc.getChiPhiThucTe(),
                    hangMuc.getTrangThai()
                });

                if (hangMuc.getChiPhiDuKien() != null) {
                    tong = tong.add(hangMuc.getChiPhiDuKien());
                }
            }

            lblTong.setText("Tổng chi phí dự kiến: "
                    + String.format(Locale.forLanguageTag("vi-VN"), "%,.2f đ", tong));

        } catch (SQLException | IllegalArgumentException ex) {
            lblTong.setText("Chưa tải được chi phí dự kiến");
            baoLoi("Không tải được hạng mục của phiếu " + maPhieuSuaChua + ".", ex);
        }
    }

    private void chonPhieuTheoMa(String ma) {
        for (int i = 0; i < danhSachPhieu.size(); i++) {
            if (danhSachPhieu.get(i).getMaPhieuSuaChua().equals(ma)) {
                int dongHienThi = tablePhieu.convertRowIndexToView(i);

                if (dongHienThi >= 0) {
                    tablePhieu.setRowSelectionInterval(dongHienThi, dongHienThi);
                    tablePhieu.scrollRectToVisible(tablePhieu.getCellRect(dongHienThi, 0, true));
                }

                return;
            }
        }
    }

    // ==================== CÁC HÀM HỖ TRỢ ====================

    private void xoaThongTin() {
        boolean trangThaiCu = dangHienThi;
        dangHienThi = true;

        try {
            txtMaPhieu.setText("");
            txtBienSo.setText("");
            txtMauXe.setText("");
            txtKhachHang.setText("");
            txtNgayLap.setText("");
            txtTrangThai.setText("Chờ sửa chữa");
            txtYeuCau.setText("");
            txtChanDoan.setText("");
            txtGhiChu.setText("");

            cboLoaiPhieu.setSelectedIndex(0);

            if (cboTiepNhan.getItemCount() > 0) {
                cboTiepNhan.setSelectedIndex(0);
            }

            modelHangMuc.setRowCount(0);
            lblTong.setText("Tổng chi phí dự kiến: 0 đ");

        } finally {
            dangHienThi = trangThaiCu;
        }
    }

    private void capNhatTrangThaiForm() {
        boolean daChonPhieu = layPhieuDangChon() != null;

        btnThem.setEnabled(!daChonPhieu);
        btnSua.setEnabled(daChonPhieu);

        // Phiếu đã lập giữ nguyên liên kết với phiếu tiếp nhận.
        cboTiepNhan.setEnabled(!daChonPhieu);

        txtChanDoan.setEditable(true);
        txtGhiChu.setEditable(true);
        cboLoaiPhieu.setEnabled(true);

        // Phiếu mới mặc định Chờ sửa chữa.
        txtTrangThai.setEditable(daChonPhieu);
    }

    private void capNhatSoLuong() {
        lblSoPhieu.setText(
                "Hiển thị: " + tablePhieu.getRowCount() + " / " + danhSachPhieu.size() + " phiếu");
    }

    private String chuoiHoacNull(String chuoi) {
        if (chuoi == null || chuoi.trim().isEmpty()) {
            return null;
        }
        return chuoi.trim();
    }

    private void baoLoi(String noiDung, Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(
                this,
                noiDung + "\n" + ex.getMessage(),
                "Thông báo",
                JOptionPane.ERROR_MESSAGE);
    }

    private void caiDatBang(JTable table) {
        table.setRowHeight(32);
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.setAutoCreateRowSorter(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setSelectionBackground(new Color(53, 105, 139));
        table.setSelectionForeground(Color.WHITE);
        table.setGridColor(VIEN);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        table.getTableHeader().setReorderingAllowed(false);
    }

    private void themO(JPanel form, String ten, JComponent field) {
        JPanel o = new JPanel(new BorderLayout(0, 4));
        o.setOpaque(false);

        JLabel nhan = new JLabel(ten);
        nhan.setFont(new Font("Arial", Font.BOLD, 12));
        nhan.setForeground(new Color(87, 100, 117));

        field.setFont(new Font("Arial", Font.PLAIN, 13));
        field.setBackground(Color.WHITE);
        field.setForeground(CHU);
        field.setPreferredSize(new Dimension(200, 32));

        if (field instanceof JTextField) {
            field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(VIEN),
                    new EmptyBorder(5, 7, 5, 7)));
        }

        o.add(nhan, BorderLayout.NORTH);
        o.add(field, BorderLayout.CENTER);
        form.add(o);
    }

    private void themVanBan(JPanel form, String ten, JTextArea area) {
        JPanel o = new JPanel(new BorderLayout(0, 4));
        o.setOpaque(false);

        JLabel nhan = new JLabel(ten);
        nhan.setFont(new Font("Arial", Font.BOLD, 12));
        nhan.setForeground(new Color(87, 100, 117));

        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setFont(new Font("Arial", Font.PLAIN, 13));
        area.setForeground(CHU);
        area.setBackground(Color.WHITE);
        area.setBorder(new EmptyBorder(5, 7, 5, 7));

        JScrollPane cuon = new JScrollPane(area);
        cuon.setPreferredSize(new Dimension(200, 75));
        cuon.setBorder(BorderFactory.createLineBorder(VIEN));

        o.add(nhan, BorderLayout.NORTH);
        o.add(cuon, BorderLayout.CENTER);
        form.add(o);
    }

    private static JButton taoNut(String ten, boolean chinh) {
        JButton nut = new JButton(ten);
        nut.setFont(new Font("Arial", Font.BOLD, 12));
        nut.setBackground(chinh ? DO : Color.WHITE);
        nut.setForeground(chinh ? Color.WHITE : CHU);
        nut.setContentAreaFilled(false);
        nut.setOpaque(true);
        nut.setFocusPainted(false);
        nut.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        nut.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(chinh ? DO : VIEN),
                new EmptyBorder(8, 14, 8, 14)));
        return nut;
    }

    // ==================== MENU VÀ THANH CÔNG CỤ ====================

    private ImageIcon loadIcon(String path, int width, int height) {
        java.net.URL url = getClass().getResource(path);

        if (url == null) {
            return null;
        }

        ImageIcon icon = new ImageIcon(url);
        Image anh = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(anh);
    }

    private JMenu taoMenuDropdown(String tenMenu, String[] cacItem) {
        JMenu menu = new JMenu(tenMenu);

        for (String ten : cacItem) {
            if ("-".equals(ten)) {
                menu.addSeparator();
            } else {
                menu.add(new JMenuItem(ten));
            }
        }

        return menu;
    }

    private JButton taoNutNhanh(String tieuDe, String phimTat, ImageIcon icon) {
        JButton btn = new JButton();
        btn.setLayout(new BorderLayout(10, 0));

        if (icon != null) {
            btn.add(new JLabel(icon), BorderLayout.WEST);
        }

        JPanel pText = new JPanel(new GridLayout(2, 1));
        pText.setOpaque(false);

        JLabel lblTen = new JLabel(tieuDe);
        lblTen.setFont(new Font("Arial", Font.BOLD, 12));

        JLabel lblPhim = new JLabel(phimTat);
        lblPhim.setFont(new Font("Arial", Font.PLAIN, 11));
        lblPhim.setForeground(Color.GRAY);

        pText.add(lblTen);
        pText.add(lblPhim);
        btn.add(pText, BorderLayout.CENTER);

        btn.setBackground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 205, 210)),
                new EmptyBorder(6, 10, 6, 10)));

        return btn;
    }
}