package ui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Locale;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import connectDB.ConnectDB;
import dao.KhachHangDao;
import dao.NhanVienDao;
import dao.PhieuTiepNhanDao;
import dao.XeDao;
import entity.KhachHang;
import entity.NhanVien;
import entity.PhieuTiepNhan;
import entity.Xe;

public class TiepNhanXeFrame extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;
    private static final Color CHU = new Color(35, 41, 51);
    private static final Color CHU_PHU = new Color(112, 124, 139);
    private static final Color VIEN = new Color(216, 222, 230);
    private static final Color DO = new Color(188, 25, 58);

    private static final String DA_TIEP_NHAN = "Đã tiếp nhận";
    private static final DateTimeFormatter DINH_DANG = DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm").withResolverStyle(ResolverStyle.STRICT);

    // ==================== DAO VÀ DỮ LIỆU ====================

    private final KhachHangDao khachHangDao = new KhachHangDao();
    private final XeDao xeDao = new XeDao();
    private final NhanVienDao nhanVienDao = new NhanVienDao();
    private final PhieuTiepNhanDao phieuDao = new PhieuTiepNhanDao();

    private ArrayList<PhieuTiepNhan> danhSachPhieu = new ArrayList<>();
    private ArrayList<NhanVien> danhSachNhanVien = new ArrayList<>();

    private final LinkedHashMap<String, KhachHang> khachTheoMa = new LinkedHashMap<>();
    private final LinkedHashMap<String, Xe> xeTheoMa = new LinkedHashMap<>();

    private final String maNhanVienDangNhap;
    private boolean dangTaiDuLieu;
    private boolean dangXemPhieu;

    // ==================== CÁC THÀNH PHẦN GIAO DIỆN ====================

    private final JTextField txtMaKH = new JTextField();
    private final JTextField txtHoTen = new JTextField();
    private final JTextField txtSDT = new JTextField();
    private final JTextField txtEmail = new JTextField();
    private final JTextField txtDiaChi = new JTextField();

    private final JTextField txtMaXe = new JTextField();
    private final JTextField txtBienSo = new JTextField();
    private final JTextField txtMauXe = new JTextField();

    private final JTextField txtMaTiepNhan = new JTextField();
    private final JComboBox<String> cboNhanVien = new JComboBox<>();
    private final JTextField txtNgayNhan = new JTextField();
    private final JTextField txtHenTra = new JTextField();
    private final JTextField txtSoKm = new JTextField();
    private final JTextField txtTrangThai = new JTextField();

    private final JComboBox<String> cboNhienLieu = new JComboBox<>(new String[]{
        "-- Chọn --", "Cạn", "1/4", "1/2", "3/4", "Đầy"
    });

    private final JTextArea txtTinhTrang = new JTextArea(3, 20);
    private final JTextArea txtYeuCau = new JTextArea(3, 20);
    private final JTextArea txtGhiChu = new JTextArea(3, 20);

    private final JTextField txtTimKiem = new JTextField();
    private final JComboBox<String> cboLocTrangThai = new JComboBox<>();
    private final JLabel lblSoLuong = new JLabel("Số phiếu: 0");

    private final JButton btnTimKH = taoNut("Tìm khách", false);
    private final JButton btnTimXe = taoNut("Tìm xe", false);
    private final JButton btnTim = taoNut("Tìm kiếm", false);
    private final JButton btnMoi = taoNut("Làm mới", false);
    private final JButton btnLuu = taoNut("Lưu tiếp nhận", true);

    private final DefaultTableModel model = new DefaultTableModel(new String[]{
        "Mã tiếp nhận", "Biển số", "Chủ xe", "Số điện thoại",
        "Ngày tiếp nhận", "Hẹn trả", "Trạng thái"
    }, 0) {
        private static final long serialVersionUID = 1L;

        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private final JTable table = new JTable(model);
    private final TableRowSorter<DefaultTableModel> boLoc = new TableRowSorter<>(model);

    // ==================== CONSTRUCTOR ====================

    public TiepNhanXeFrame() {
        this(null);
    }

    public TiepNhanXeFrame(String maNhanVienDangNhap) {
        this.maNhanVienDangNhap = maNhanVienDangNhap;

        setTitle("Tiếp nhận xe - Night Invader");
        setSize(1340, 960);
        setLocationRelativeTo(null);
        setResizable(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        khoiTaoGiaoDien();
    }

    // ==================== KHỞI TẠO GIAO DIỆN ====================

    private void khoiTaoGiaoDien() {
        setLayout(new BorderLayout());

        // ==================== 1. MENU BAR ====================

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

            if (tenCacMenu[i].trim().equals("Dịch vụ")) {
                menu.setForeground(DO);
                menu.setBorder(BorderFactory.createMatteBorder(0, 0, 3, 0, DO));
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

        // ==================== 3. NỘI DUNG TIẾP NHẬN XE ====================

        JPanel panelCenter = new JPanel(new BorderLayout());
        panelCenter.setBackground(new Color(216, 220, 228));
        panelCenter.setBorder(new EmptyBorder(18, 45, 15, 45));

        JPanel khung = new JPanel(new BorderLayout());
        khung.setBackground(Color.WHITE);
        khung.setBorder(BorderFactory.createLineBorder(VIEN));

        JPanel tieuDe = new JPanel(new BorderLayout());
        tieuDe.setBackground(new Color(246, 247, 250));
        tieuDe.setBorder(new EmptyBorder(8, 16, 8, 16));

        JLabel lblTieuDe = new JLabel("LẬP PHIẾU TIẾP NHẬN XE");
        lblTieuDe.setFont(new Font("Arial", Font.BOLD, 16));
        lblTieuDe.setForeground(CHU);
        tieuDe.add(lblTieuDe, BorderLayout.WEST);
        khung.add(tieuDe, BorderLayout.NORTH);

        JPanel noiDung = new JPanel(new BorderLayout(0, 12));
        noiDung.setBackground(Color.WHITE);
        noiDung.setBorder(new EmptyBorder(12, 18, 16, 18));

        JPanel cacForm = new JPanel();
        cacForm.setLayout(new BoxLayout(cacForm, BoxLayout.Y_AXIS));
        cacForm.setBackground(Color.WHITE);

        JLabel moTa = new JLabel("Tìm hồ sơ đã có hoặc nhập khách hàng và xe mới ngay tại đây.");
        moTa.setForeground(CHU_PHU);
        moTa.setFont(new Font("Arial", Font.PLAIN, 13));
        moTa.setAlignmentX(Component.LEFT_ALIGNMENT);
        cacForm.add(moTa);
        cacForm.add(Box.createVerticalStrut(10));

        txtMaKH.setEditable(false);
        txtMaXe.setEditable(false);
        txtMaTiepNhan.setEditable(false);
        txtTrangThai.setEditable(false);

        // ==================== FORM KHÁCH HÀNG ====================

        JPanel formKH = taoNhom("1. Thông tin khách hàng", 2, 3);
        themO(formKH, "Số điện thoại", ghepNut(txtSDT, btnTimKH));
        themO(formKH, "Mã khách hàng", txtMaKH);
        themO(formKH, "Họ tên", txtHoTen);
        themO(formKH, "Email", txtEmail);
        themO(formKH, "Địa chỉ", txtDiaChi);

        JLabel huongDanKH = new JLabel("Khách mới: nhập thông tin, mã tự sinh khi lưu.");
        huongDanKH.setFont(new Font("Arial", Font.ITALIC, 12));
        huongDanKH.setForeground(CHU_PHU);
        formKH.add(huongDanKH);

        cacForm.add(formKH);
        cacForm.add(Box.createVerticalStrut(8));

        // ==================== FORM THÔNG TIN XE ====================

        JPanel formXe = taoNhom("2. Thông tin xe", 1, 3);
        themO(formXe, "Biển số", ghepNut(txtBienSo, btnTimXe));
        themO(formXe, "Mã phương tiện", txtMaXe);
        themO(formXe, "Mẫu xe", txtMauXe);

        cacForm.add(formXe);
        cacForm.add(Box.createVerticalStrut(8));

        // ==================== FORM TIẾP NHẬN ====================

        JPanel formTN = taoNhom("3. Thông tin tiếp nhận", 2, 4);
        themO(formTN, "Mã tiếp nhận", txtMaTiepNhan);
        themO(formTN, "Nhân viên tiếp nhận", cboNhanVien);
        themO(formTN, "Ngày nhận (dd/MM/yyyy HH:mm)", txtNgayNhan);
        themO(formTN, "Hẹn trả (dd/MM/yyyy HH:mm)", txtHenTra);
        themO(formTN, "Số km đã đi", txtSoKm);
        themO(formTN, "Mức nhiên liệu", cboNhienLieu);
        themO(formTN, "Trạng thái phiếu", txtTrangThai);

        JPanel oTrong = new JPanel();
        oTrong.setOpaque(false);
        formTN.add(oTrong);

        cacForm.add(formTN);
        cacForm.add(Box.createVerticalStrut(8));

        JPanel ghiNhan = new JPanel(new GridLayout(1, 3, 16, 0));
        ghiNhan.setOpaque(false);
        ghiNhan.setAlignmentX(Component.LEFT_ALIGNMENT);

        themVanBan(ghiNhan, "Tình trạng ban đầu", txtTinhTrang);
        themVanBan(ghiNhan, "Yêu cầu khách hàng", txtYeuCau);
        themVanBan(ghiNhan, "Ghi chú", txtGhiChu);
        cacForm.add(ghiNhan);

        JScrollPane cuonForm = new JScrollPane(cacForm);
        cuonForm.setBorder(BorderFactory.createEmptyBorder());
        cuonForm.getVerticalScrollBar().setUnitIncrement(16);
        cuonForm.setPreferredSize(new Dimension(1100, 405));
        noiDung.add(cuonForm, BorderLayout.NORTH);

        // ==================== BẢNG DANH SÁCH TIẾP NHẬN ====================

        JPanel vungBang = new JPanel(new BorderLayout(0, 10));
        vungBang.setOpaque(false);

        JPanel thanhLoc = new JPanel(new BorderLayout(12, 0));
        thanhLoc.setBackground(new Color(237, 241, 245));
        thanhLoc.setBorder(new EmptyBorder(6, 10, 6, 10));

        JPanel tim = new JPanel(new BorderLayout(8, 0));
        tim.setOpaque(false);

        JLabel lblTim = new JLabel("Tìm phiếu, biển số, khách hàng:");
        lblTim.setFont(new Font("Arial", Font.PLAIN, 12));
        lblTim.setForeground(CHU_PHU);
        tim.add(lblTim, BorderLayout.WEST);
        tim.add(txtTimKiem, BorderLayout.CENTER);
        tim.add(btnTim, BorderLayout.EAST);

        JPanel loc = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        loc.setOpaque(false);
        loc.add(new JLabel("Trạng thái:"));
        loc.add(cboLocTrangThai);

        thanhLoc.add(tim, BorderLayout.CENTER);
        thanhLoc.add(loc, BorderLayout.EAST);
        vungBang.add(thanhLoc, BorderLayout.NORTH);

        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.setRowHeight(36);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowSorter(boLoc);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(53, 105, 139));
        table.setSelectionForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(237, 241, 245));
        table.getTableHeader().setPreferredSize(new Dimension(0, 30));
        table.getTableHeader().setReorderingAllowed(false);

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            private static final long serialVersionUID = 1L;

            @Override
            public Component getTableCellRendererComponent(JTable bang, Object value, boolean selected, boolean focus, int row, int column) {
                super.getTableCellRendererComponent(bang, value, selected, focus, row, column);
                setBorder(new EmptyBorder(0, 8, 0, 8));

                if (!selected) {
                    setBackground(row % 2 == 0 ? Color.WHITE : new Color(247, 248, 250));
                    setForeground(CHU);
                }
                return this;
            }
        });

        int[] rong = {115, 125, 180, 125, 155, 155, 145};
        for (int i = 0; i < rong.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(rong[i]);
        }

        JScrollPane cuonBang = new JScrollPane(table);
        cuonBang.setBorder(BorderFactory.createEmptyBorder());
        cuonBang.getViewport().setBackground(Color.WHITE);
        vungBang.add(cuonBang, BorderLayout.CENTER);

        lblSoLuong.setForeground(CHU_PHU);
        vungBang.add(lblSoLuong, BorderLayout.SOUTH);
        noiDung.add(vungBang, BorderLayout.CENTER);

        // ==================== CÁC NÚT CHỨC NĂNG ====================

        JPanel thaoTac = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        thaoTac.setOpaque(false);
        thaoTac.add(btnMoi);
        thaoTac.add(btnLuu);
        noiDung.add(thaoTac, BorderLayout.SOUTH);

        khung.add(noiDung, BorderLayout.CENTER);
        panelCenter.add(khung, BorderLayout.CENTER);

        // ==================== 4. FOOTER ====================

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

        // ==================== GẮN VÀO FRAME ====================

        add(panelTopContainer, BorderLayout.NORTH);
        add(panelCenter, BorderLayout.CENTER);
        add(panelFooter, BorderLayout.SOUTH);

        // ==================== ĐĂNG KÝ SỰ KIỆN ====================

        btnTimKH.addActionListener(this);
        txtSDT.addActionListener(this);
        btnTimXe.addActionListener(this);
        txtBienSo.addActionListener(this);
        btnMoi.addActionListener(this);
        btnLuu.addActionListener(this);
        btnTim.addActionListener(this);
        txtTimKiem.addActionListener(this);
        cboLocTrangThai.addActionListener(this);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting() || dangTaiDuLieu) return;

            int dong = table.getSelectedRow();
            if (dong < 0) return;

            PhieuTiepNhan phieu = danhSachPhieu.get(table.convertRowIndexToModel(dong));
            Xe xe = xeTheoMa.get(phieu.getMaPhuongTien());
            KhachHang khach = xe == null ? null : khachTheoMa.get(xe.getMaKhachHang());

            if (xe == null || khach == null) {
                JOptionPane.showMessageDialog(this, "Thiếu hồ sơ khách hàng hoặc xe. Bấm Làm mới để tải lại.");
                return;
            }

            dangXemPhieu = true;

            hienKhachHang(khach);
            hienXe(xe);

            txtMaTiepNhan.setText(phieu.getMaTiepNhan());
            txtNgayNhan.setText(phieu.getNgayGioTiepNhan().format(DINH_DANG));
            txtHenTra.setText(phieu.getNgayGioHenTra() == null ? "" : phieu.getNgayGioHenTra().format(DINH_DANG));
            txtSoKm.setText(String.valueOf(phieu.getOdo()));
            cboNhienLieu.setSelectedItem(phieu.getMucNhienLieu());
            txtTinhTrang.setText(phieu.getTinhTrangBanDau());
            txtYeuCau.setText(phieu.getYeuCauKhachHang());
            txtGhiChu.setText(phieu.getGhiChu());
            txtTrangThai.setText(phieu.getTrangThai());

            chonNhanVien(phieu.getMaNhanVien());
            choNhap(false);
        });

        // Tải dữ liệu ban đầu từ Database.
        btnMoi.doClick();
    }

    // ==================== XỬ LÝ SỰ KIỆN ====================

    @Override
    public void actionPerformed(ActionEvent e) {
        Object nguon = e.getSource();

        // ==================== LÀM MỚI ====================

        if (nguon == btnMoi) {
            try {
                taiDuLieu();
                xoaTrangForm();
            } catch (SQLException | IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, "Không tải được dữ liệu: " + ex.getMessage());
            }

        // ==================== TÌM KHÁCH HÀNG ====================

        } else if (nguon == btnTimKH || nguon == txtSDT) {
            if (dangXemPhieu) return;

            String sdt = txtSDT.getText().trim();
            if (sdt.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nhập số điện thoại để tìm khách hàng.");
                txtSDT.requestFocusInWindow();
                return;
            }

            try {
                ArrayList<KhachHang> ketQua = khachHangDao.timTheoSoDienThoai(sdt);

                if (ketQua.isEmpty()) {
                    txtMaKH.setText("");
                    txtHoTen.setText("");
                    txtEmail.setText("");
                    txtDiaChi.setText("");
                    txtMaXe.setText("");
                    txtBienSo.setText("");
                    txtMauXe.setText("");

                    choNhap(true);

                    JOptionPane.showMessageDialog(this, "Chưa có khách hàng. Nhập thông tin khách mới.");
                    txtHoTen.requestFocusInWindow();
                    return;
                }

                KhachHang khach = ketQua.get(0);

                if (ketQua.size() > 1) {
                    String[] luaChon = new String[ketQua.size()];
                    for (int i = 0; i < ketQua.size(); i++) {
                        luaChon[i] = ketQua.get(i).getMaKhachHang() + " • " + ketQua.get(i).getHoTen();
                    }

                    String chon = (String) JOptionPane.showInputDialog(this, "Chọn khách hàng:", "Kết quả tìm", JOptionPane.QUESTION_MESSAGE, null, luaChon, luaChon[0]);
                    if (chon == null) return;

                    for (int i = 0; i < luaChon.length; i++) {
                        if (chon.equals(luaChon[i])) khach = ketQua.get(i);
                    }
                }

                hienKhachHang(khach);
                txtMaXe.setText("");
                txtBienSo.setText("");
                txtMauXe.setText("");
                choNhap(true);

            } catch (SQLException | IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, "Không tìm được khách hàng: " + ex.getMessage());
            }

        // ==================== TÌM XE ====================

        } else if (nguon == btnTimXe || nguon == txtBienSo) {
            if (dangXemPhieu) return;

            if (chuanHoaBienSo(txtBienSo.getText()).isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nhập biển số để tìm xe.");
                txtBienSo.requestFocusInWindow();
                return;
            }

            try {
                Xe xe = timXeTheoBienSo(txtBienSo.getText());

                if (xe == null) {
                    txtMaXe.setText("");
                    txtMauXe.setText("");
                    choNhap(true);

                    JOptionPane.showMessageDialog(this, "Chưa có xe. Nhập mẫu xe và thông tin khách hàng để thêm mới.");
                    txtMauXe.requestFocusInWindow();
                    return;
                }

                if (!txtMaKH.getText().isEmpty() && !txtMaKH.getText().equals(xe.getMaKhachHang())) {
                    int chon = JOptionPane.showConfirmDialog(this, "Xe thuộc khách hàng khác.\nHiển thị chủ xe đã lưu?", "Đối chiếu chủ xe", JOptionPane.YES_NO_OPTION);
                    if (chon != JOptionPane.YES_OPTION) {
                        txtMaXe.setText("");
                        choNhap(true);
                        return;
                    }
                }

                KhachHang khach = khachHangDao.timTheoMa(xe.getMaKhachHang());
                if (khach == null) {
                    JOptionPane.showMessageDialog(this, "Không tìm thấy chủ xe.");
                    return;
                }

                hienXe(xe);
                hienKhachHang(khach);
                choNhap(true);

            } catch (SQLException | IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, "Không tìm được xe: " + ex.getMessage());
            }

        // ==================== TÌM VÀ LỌC PHIẾU ====================

        } else if (nguon == btnTim || nguon == txtTimKiem || nguon == cboLocTrangThai) {
            if (dangTaiDuLieu) return;

            String tuKhoa = chuanHoa(txtTimKiem.getText().trim());
            String trangThai = (String) cboLocTrangThai.getSelectedItem();

            boLoc.setRowFilter(new RowFilter<DefaultTableModel, Integer>() {
                @Override
                public boolean include(Entry<? extends DefaultTableModel, ? extends Integer> entry) {
                    String noiDung = "";
                    for (int i = 0; i < 4; i++) {
                        noiDung += " " + entry.getStringValue(i);
                    }

                    boolean dungTrangThai = "Tất cả".equals(trangThai) || entry.getStringValue(6).equals(trangThai);
                    return dungTrangThai && chuanHoa(noiDung).contains(tuKhoa);
                }
            });

            lblSoLuong.setText("Hiển thị: " + table.getRowCount() + " / " + model.getRowCount() + " phiếu");

        // ==================== LƯU TIẾP NHẬN ====================

        } else if (nguon == btnLuu) {
            if (dangXemPhieu || !validData()) return;

            String maDaLuu = null;
            Connection conn = null;
            boolean daMoGiaoDich = false;
            Exception loi = null;

            try {
                conn = ConnectDB.getConnection();

                if (conn == null || conn.isClosed()) {
                    throw new SQLException("Chưa kết nối database.");
                }

                if (!conn.getAutoCommit()) {
                    throw new SQLException("Kết nối đang có giao dịch khác.");
                }

                conn.setAutoCommit(false);
                daMoGiaoDich = true;

                String sdt = txtSDT.getText().trim();
                String maKH = txtMaKH.getText().trim();
                KhachHang khach = null;

                if (!maKH.isEmpty()) {
                    khach = khachHangDao.timTheoMa(maKH);

                    if (khach == null) {
                        throw new IllegalArgumentException("Khách hàng không còn tồn tại.");
                    }

                    if (!sdt.equals(khach.getSoDienThoai())) {
                        throw new IllegalArgumentException("Số điện thoại đã thay đổi. Bấm Tìm khách để chọn lại hồ sơ.");
                    }

                    if (!khach.isTrangThai()) {
                        throw new IllegalArgumentException("Khách hàng đang ngừng theo dõi.");
                    }
                } else if (!khachHangDao.timTheoSoDienThoai(sdt).isEmpty()) {
                    throw new IllegalArgumentException("Số điện thoại đã có hồ sơ. Bấm Tìm khách để chọn khách hàng.");
                }

                Xe xe = timXeTheoBienSo(txtBienSo.getText());

                if (xe != null) {
                    if (khach == null || !xe.getMaKhachHang().equals(khach.getMaKhachHang())) {
                        throw new IllegalArgumentException("Xe không thuộc khách hàng đang nhập. Bấm Tìm xe để đối chiếu.");
                    }

                    if (!"Hoạt động".equals(xe.getTrangThai())) {
                        throw new IllegalArgumentException("Xe đang ngừng theo dõi.");
                    }

                    for (PhieuTiepNhan phieuCu : phieuDao.timTheoMaPhuongTien(xe.getMaPhuongTien())) {
                        if (!"Đã giao xe".equals(phieuCu.getTrangThai()) && !"Đã hủy".equals(phieuCu.getTrangThai())) {
                            throw new IllegalArgumentException("Xe đang có phiếu chưa hoàn tất: " + phieuCu.getMaTiepNhan());
                        }
                    }
                }

                int viTriNhanVien = cboNhanVien.getSelectedIndex() - 1;
                String maNV = danhSachNhanVien.get(viTriNhanVien).getMaNhanVien();

                if (nhanVienDao.timTheoMa(maNV) == null) {
                    throw new IllegalArgumentException("Nhân viên không còn tồn tại.");
                }

                // Thêm khách hàng mới nếu chưa có.
                if (khach == null) {
                    khach = new KhachHang(
                            khachHangDao.taoMaKhachHang(),
                            txtHoTen.getText().trim(),
                            sdt,
                            txtEmail.getText().trim(),
                            txtDiaChi.getText().trim(),
                            true);

                    if (!khachHangDao.themKhachHang(khach)) {
                        throw new SQLException("Không thêm được khách hàng.");
                    }
                }

                // Thêm xe mới nếu chưa có.
                if (xe == null) {
                    xe = new Xe(
                            xeDao.taoMaXe(),
                            khach.getMaKhachHang(),
                            txtBienSo.getText().replaceAll("\\s+", "").toUpperCase(Locale.ROOT),
                            txtMauXe.getText().trim(),
                            "Hoạt động");

                    if (!xeDao.themXe(xe)) {
                        throw new SQLException("Không thêm được xe.");
                    }
                }

                LocalDateTime ngayNhan = LocalDateTime.parse(txtNgayNhan.getText().trim(), DINH_DANG);
                LocalDateTime henTra = txtHenTra.getText().trim().isEmpty()
                        ? null : LocalDateTime.parse(txtHenTra.getText().trim(), DINH_DANG);

                String maPhieu = phieuDao.taoMaTiepNhan();

                PhieuTiepNhan phieu = new PhieuTiepNhan(
                        maPhieu,
                        xe.getMaPhuongTien(),
                        maNV,
                        ngayNhan,
                        henTra,
                        Integer.parseInt(txtSoKm.getText().trim()),
                        (String) cboNhienLieu.getSelectedItem(),
                        txtTinhTrang.getText().trim(),
                        txtYeuCau.getText().trim(),
                        txtGhiChu.getText().trim(),
                        DA_TIEP_NHAN);

                if (!phieuDao.themPhieuTiepNhan(phieu)) {
                    throw new SQLException("Không lưu được phiếu tiếp nhận.");
                }

                conn.commit();
                maDaLuu = maPhieu;

            } catch (Exception ex) {
                loi = ex;

                if (daMoGiaoDich) {
                    try {
                        conn.rollback();
                    } catch (SQLException rollbackError) {
                        ex.addSuppressed(rollbackError);
                        try {
                            conn.close();
                        } catch (SQLException closeError) {
                            ex.addSuppressed(closeError);
                        }
                    }
                }
            } finally {
                if (daMoGiaoDich) {
                    try {
                        if (!conn.isClosed()) conn.setAutoCommit(true);
                    } catch (SQLException ex) {
                        if (loi == null) {
                            loi = ex;
                        } else {
                            loi.addSuppressed(ex);
                        }
                    }
                }
            }

            if (maDaLuu == null) {
                JOptionPane.showMessageDialog(this, "Không lưu được tiếp nhận: " + (loi == null ? "Lỗi không xác định." : loi.getMessage()));
                return;
            }

            JOptionPane.showMessageDialog(this, "Đã lưu phiếu tiếp nhận: " + maDaLuu);

            if (loi != null) {
                JOptionPane.showMessageDialog(this, "Phiếu đã lưu nhưng kết nối gặp lỗi: " + loi.getMessage());
                return;
            }

            try {
                taiDuLieu();
                xoaTrangForm();
            } catch (SQLException | IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, "Phiếu đã lưu nhưng chưa tải lại được danh sách: " + ex.getMessage());
            }
        }
    }

    // ==================== TẢI DỮ LIỆU SQL ====================

    private void taiDuLieu() throws SQLException {
        ArrayList<KhachHang> dsKhach = khachHangDao.layDanhSachKhachHang();
        ArrayList<Xe> dsXe = xeDao.layDanhSachXe();
        ArrayList<NhanVien> dsNhanVien = nhanVienDao.layDanhSachNhanVien();
        ArrayList<PhieuTiepNhan> dsPhieu = phieuDao.layDanhSachPhieuTiepNhan();

        String maNVCu = null;
        int viTriCu = cboNhanVien.getSelectedIndex() - 1;
        if (viTriCu >= 0 && viTriCu < danhSachNhanVien.size()) {
            maNVCu = danhSachNhanVien.get(viTriCu).getMaNhanVien();
        }

        dangTaiDuLieu = true;

        try {
            table.clearSelection();
            boLoc.setRowFilter(null);
            model.setRowCount(0);

            khachTheoMa.clear();
            for (KhachHang khach : dsKhach) {
                khachTheoMa.put(khach.getMaKhachHang(), khach);
            }

            xeTheoMa.clear();
            for (Xe xe : dsXe) {
                xeTheoMa.put(xe.getMaPhuongTien(), xe);
            }

            danhSachNhanVien = dsNhanVien;
            cboNhanVien.removeAllItems();
            cboNhanVien.addItem("-- Chọn nhân viên --");

            for (NhanVien nv : danhSachNhanVien) {
                cboNhanVien.addItem(nv.getMaNhanVien() + " • " + nv.getHoTen());
            }

            chonNhanVien(maNhanVienDangNhap == null ? maNVCu : maNhanVienDangNhap);

            danhSachPhieu = dsPhieu;

            cboLocTrangThai.removeAllItems();
            cboLocTrangThai.addItem("Tất cả");
            LinkedHashSet<String> cacTrangThai = new LinkedHashSet<>();
            cacTrangThai.add(DA_TIEP_NHAN);

            for (PhieuTiepNhan phieu : danhSachPhieu) {
                Xe xe = xeTheoMa.get(phieu.getMaPhuongTien());
                KhachHang khach = xe == null ? null : khachTheoMa.get(xe.getMaKhachHang());

                model.addRow(new Object[]{
                    phieu.getMaTiepNhan(),
                    xe == null ? phieu.getMaPhuongTien() : xe.getBienSo(),
                    khach == null ? "" : khach.getHoTen(),
                    khach == null ? "" : khach.getSoDienThoai(),
                    phieu.getNgayGioTiepNhan().format(DINH_DANG),
                    phieu.getNgayGioHenTra() == null ? "" : phieu.getNgayGioHenTra().format(DINH_DANG),
                    phieu.getTrangThai()
                });

                cacTrangThai.add(phieu.getTrangThai());
            }

            for (String trangThai : cacTrangThai) {
                cboLocTrangThai.addItem(trangThai);
            }

            txtTimKiem.setText("");
            lblSoLuong.setText("Hiển thị: " + model.getRowCount() + " / " + model.getRowCount() + " phiếu");

        } finally {
            dangTaiDuLieu = false;
        }
    }

    // ==================== KIỂM TRA DỮ LIỆU ====================

    private boolean validData() {
        String hoTen = txtHoTen.getText().trim();
        String sdt = txtSDT.getText().trim();
        String email = txtEmail.getText().trim();
        String bienSo = txtBienSo.getText().trim();
        String mauXe = txtMauXe.getText().trim();

        if (hoTen.isEmpty() || hoTen.length() > 120) {
            JOptionPane.showMessageDialog(this, "Họ tên không được rỗng, tối đa 120 ký tự.");
            txtHoTen.requestFocusInWindow();
            return false;
        }

        if (!sdt.matches("[0-9]{10,15}")) {
            JOptionPane.showMessageDialog(this, "Số điện thoại phải gồm 10 đến 15 chữ số.");
            txtSDT.requestFocusInWindow();
            return false;
        }

        if (!email.isEmpty() && (email.length() > 254 || !email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$"))) {
            JOptionPane.showMessageDialog(this, "Email không hợp lệ.");
            txtEmail.requestFocusInWindow();
            return false;
        }

        if (txtDiaChi.getText().trim().length() > 255) {
            JOptionPane.showMessageDialog(this, "Địa chỉ tối đa 255 ký tự.");
            txtDiaChi.requestFocusInWindow();
            return false;
        }

        if (chuanHoaBienSo(bienSo).isEmpty() || bienSo.length() > 20) {
            JOptionPane.showMessageDialog(this, "Biển số không được rỗng, tối đa 20 ký tự.");
            txtBienSo.requestFocusInWindow();
            return false;
        }

        if (mauXe.isEmpty() || mauXe.length() > 100) {
            JOptionPane.showMessageDialog(this, "Mẫu xe không được rỗng, tối đa 100 ký tự.");
            txtMauXe.requestFocusInWindow();
            return false;
        }

        if (cboNhanVien.getSelectedIndex() <= 0) {
            JOptionPane.showMessageDialog(this, "Chọn nhân viên tiếp nhận.");
            cboNhanVien.requestFocusInWindow();
            return false;
        }

        try {
            LocalDateTime ngayNhan = LocalDateTime.parse(txtNgayNhan.getText().trim(), DINH_DANG);

            if (ngayNhan.isAfter(LocalDateTime.now())) {
                JOptionPane.showMessageDialog(this, "Ngày nhận không được ở tương lai.");
                txtNgayNhan.requestFocusInWindow();
                return false;
            }

            if (!txtHenTra.getText().trim().isEmpty()) {
                LocalDateTime henTra = LocalDateTime.parse(txtHenTra.getText().trim(), DINH_DANG);

                if (henTra.isBefore(ngayNhan)) {
                    JOptionPane.showMessageDialog(this, "Hẹn trả không được trước ngày nhận.");
                    txtHenTra.requestFocusInWindow();
                    return false;
                }
            }
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Ngày giờ phải đúng dạng dd/MM/yyyy HH:mm.");
            return false;
        }

        try {
            int soKm = Integer.parseInt(txtSoKm.getText().trim());
            if (soKm < 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Số km phải là số nguyên không âm.");
            txtSoKm.requestFocusInWindow();
            return false;
        }

        if (cboNhienLieu.getSelectedIndex() <= 0) {
            JOptionPane.showMessageDialog(this, "Chọn mức nhiên liệu.");
            cboNhienLieu.requestFocusInWindow();
            return false;
        }

        if (txtTinhTrang.getText().trim().isEmpty() || txtTinhTrang.getText().trim().length() > 2000) {
            JOptionPane.showMessageDialog(this, "Tình trạng ban đầu không được rỗng, tối đa 2000 ký tự.");
            txtTinhTrang.requestFocusInWindow();
            return false;
        }

        if (txtYeuCau.getText().trim().isEmpty() || txtYeuCau.getText().trim().length() > 2000) {
            JOptionPane.showMessageDialog(this, "Yêu cầu khách hàng không được rỗng, tối đa 2000 ký tự.");
            txtYeuCau.requestFocusInWindow();
            return false;
        }

        if (txtGhiChu.getText().trim().length() > 1000) {
            JOptionPane.showMessageDialog(this, "Ghi chú tối đa 1000 ký tự.");
            txtGhiChu.requestFocusInWindow();
            return false;
        }

        return true;
    }

    // ==================== TÌM XE THEO BIỂN SỐ ====================

    private Xe timXeTheoBienSo(String bienSo) throws SQLException {
        String bienSoChuan = chuanHoaBienSo(bienSo);

        for (Xe xe : xeDao.layDanhSachXe()) {
            if (chuanHoaBienSo(xe.getBienSo()).equals(bienSoChuan)) {
                return xe;
            }
        }

        return null;
    }

    // ==================== HIỂN THỊ KHÁCH HÀNG VÀ XE ====================

    private void hienKhachHang(KhachHang khach) {
        txtMaKH.setText(khach.getMaKhachHang());
        txtHoTen.setText(khach.getHoTen());
        txtSDT.setText(khach.getSoDienThoai());
        txtEmail.setText(khach.getEmail());
        txtDiaChi.setText(khach.getDiaChi());
    }

    private void hienXe(Xe xe) {
        txtMaXe.setText(xe.getMaPhuongTien());
        txtBienSo.setText(xe.getBienSo());
        txtMauXe.setText(xe.getMauXe());
    }

    private void chonNhanVien(String maNhanVien) {
        cboNhanVien.setSelectedIndex(0);

        if (maNhanVien == null) return;

        for (int i = 0; i < danhSachNhanVien.size(); i++) {
            if (maNhanVien.equals(danhSachNhanVien.get(i).getMaNhanVien())) {
                cboNhanVien.setSelectedIndex(i + 1);
                return;
            }
        }
    }

    // ==================== CHO PHÉP NHẬP LIỆU ====================

    private void choNhap(boolean choPhep) {
        txtSDT.setEditable(choPhep);
        txtBienSo.setEditable(choPhep);

        // Hồ sơ cũ chỉ dùng lại. Sửa hồ sơ tại màn hình quản lý.
        txtHoTen.setEditable(choPhep && txtMaKH.getText().isEmpty());
        txtEmail.setEditable(choPhep && txtMaKH.getText().isEmpty());
        txtDiaChi.setEditable(choPhep && txtMaKH.getText().isEmpty());
        txtMauXe.setEditable(choPhep && txtMaXe.getText().isEmpty());

        txtNgayNhan.setEditable(choPhep);
        txtHenTra.setEditable(choPhep);
        txtSoKm.setEditable(choPhep);
        txtTinhTrang.setEditable(choPhep);
        txtYeuCau.setEditable(choPhep);
        txtGhiChu.setEditable(choPhep);

        cboNhienLieu.setEnabled(choPhep);
        cboNhanVien.setEnabled(choPhep && maNhanVienDangNhap == null);

        btnTimKH.setEnabled(choPhep);
        btnTimXe.setEnabled(choPhep);
        btnLuu.setEnabled(choPhep);
    }

    // ==================== XÓA TRẮNG FORM ====================

    private void xoaTrangForm() {
        dangTaiDuLieu = true;

        try {
            table.clearSelection();

            txtMaKH.setText("");
            txtHoTen.setText("");
            txtSDT.setText("");
            txtEmail.setText("");
            txtDiaChi.setText("");

            txtMaXe.setText("");
            txtBienSo.setText("");
            txtMauXe.setText("");

            txtMaTiepNhan.setText("");
            txtNgayNhan.setText(LocalDateTime.now().format(DINH_DANG));
            txtHenTra.setText("");
            txtSoKm.setText("");
            cboNhienLieu.setSelectedIndex(0);

            txtTinhTrang.setText("");
            txtYeuCau.setText("");
            txtGhiChu.setText("");
            txtTrangThai.setText(DA_TIEP_NHAN);

            if (maNhanVienDangNhap != null) {
                chonNhanVien(maNhanVienDangNhap);
            }

            dangXemPhieu = false;
            choNhap(true);
            txtSDT.requestFocusInWindow();

        } finally {
            dangTaiDuLieu = false;
        }
    }

    // ==================== TẠO NHÓM THÔNG TIN ====================

    private JPanel taoNhom(String ten, int dong, int cot) {
        JPanel panel = new JPanel(new GridLayout(dong, cot, 16, 8));
        panel.setOpaque(false);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(VIEN), ten, 0, 0, new Font("Arial", Font.BOLD, 12), CHU));
        return panel;
    }

    // ==================== TẠO Ô NHẬP LIỆU ====================

    private void themO(JPanel form, String ten, JComponent field) {
        JPanel o = new JPanel(new BorderLayout(0, 4));
        o.setOpaque(false);

        JLabel nhan = new JLabel(ten);
        nhan.setFont(new Font("Arial", Font.BOLD, 12));
        nhan.setForeground(new Color(87, 100, 117));

        field.setFont(new Font("Arial", Font.PLAIN, 13));
        field.setBackground(Color.WHITE);
        field.setForeground(CHU);
        field.setPreferredSize(new Dimension(150, 32));

        if (field instanceof JTextField) {
            field.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(VIEN), new EmptyBorder(4, 8, 4, 8)));
        }

        nhan.setLabelFor(field);
        o.add(nhan, BorderLayout.NORTH);
        o.add(field, BorderLayout.CENTER);
        form.add(o);
    }

    private JPanel ghepNut(JTextField field, JButton nut) {
        JPanel panel = new JPanel(new BorderLayout(5, 0));
        panel.setOpaque(false);

        field.setFont(new Font("Arial", Font.PLAIN, 13));
        field.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(VIEN), new EmptyBorder(4, 8, 4, 8)));

        panel.add(field, BorderLayout.CENTER);
        panel.add(nut, BorderLayout.EAST);
        return panel;
    }

    private void themVanBan(JPanel form, String ten, JTextArea area) {
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setFont(new Font("Arial", Font.PLAIN, 13));
        area.setBorder(new EmptyBorder(5, 7, 5, 7));

        JScrollPane cuon = new JScrollPane(area);
        cuon.setBorder(BorderFactory.createLineBorder(VIEN));

        themO(form, ten, cuon);
        cuon.setPreferredSize(new Dimension(200, 65));
    }

    // ==================== TẠO NÚT ====================

    private static JButton taoNut(String ten, boolean chinh) {
        JButton nut = new JButton(ten);
        nut.setFont(new Font("Arial", Font.BOLD, 12));
        nut.setBackground(chinh ? DO : Color.WHITE);
        nut.setForeground(chinh ? Color.WHITE : CHU);
        nut.setContentAreaFilled(false);
        nut.setOpaque(true);
        nut.setFocusPainted(false);
        nut.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        nut.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(chinh ? DO : VIEN), new EmptyBorder(7, 12, 7, 12)));
        return nut;
    }

    // ==================== CHUẨN HÓA TỪ KHÓA ====================

    private static String chuanHoa(String s) {
        return Normalizer.normalize(s, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replace('đ', 'd')
                .replace('Đ', 'D')
                .toLowerCase(Locale.ROOT);
    }

    private static String chuanHoaBienSo(String s) {
        return s.replaceAll("[\\s.\\-]", "").toUpperCase(Locale.ROOT);
    }

    // ==================== TẢI ICON ====================

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

    // ==================== TẠO MENU DROPDOWN ====================

    private JMenu taoMenuDropdown(String tenMenu, String[] cacItem) {
        JMenu menu = new JMenu(tenMenu);

        for (String itemName : cacItem) {
            if (itemName.equals("-")) {
                menu.addSeparator();
            } else {
                JMenuItem item = new JMenuItem(itemName);
                item.addActionListener(e -> JOptionPane.showMessageDialog(this, "Đang mở chức năng: " + itemName, "Thông báo hệ thống", JOptionPane.INFORMATION_MESSAGE));
                menu.add(item);
            }
        }

        return menu;
    }

    // ==================== TẠO NÚT NHANH TOOLBAR ====================

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
        btn.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(200, 205, 210), 1), BorderFactory.createEmptyBorder(6, 10, 6, 10)));

        btn.addActionListener(e -> JOptionPane.showMessageDialog(this, "Đang mở nhanh chức năng: " + tieuDe + " (" + phimTat + ")", "Phím tắt kích hoạt", JOptionPane.INFORMATION_MESSAGE));

        return btn;
    }

    // ==================== CHẠY THỬ ====================

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        try {
            Connection conn = ConnectDB.getConnection();
            if (conn == null || conn.isClosed()) {
                ConnectDB.getInstance().connect();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Không kết nối được database: " + ex.getMessage());
            return;
        }

        SwingUtilities.invokeLater(() -> new TiepNhanXeFrame().setVisible(true));
    }
}