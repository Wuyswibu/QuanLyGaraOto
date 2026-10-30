package ui;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import entity.*;
import service.PhieuTiepNhanService;
import service.PhieuTiepNhanService.*;

/**
 * Hai trang độc lập trong cùng panel: lập phiếu và danh sách; không tạo dữ liệu mẫu. 
 */
public class LapPhieuTiepNhanUI extends JPanel {
    // ==================== HẰNG SỐ VÀ THÀNH PHẦN GIAO DIỆN ====================

    private static final long serialVersionUID = 1L;
    private static final Color RED = new Color(190, 22, 58);
    private static final Color BG = new Color(240, 243, 246);
    private static final Color LINE = new Color(220, 224, 230);
    private final PhieuTiepNhanService service;
    private final PhieuTiepNhanUI danhSachPhieu;
    private final JTabbedPane tabs = new JTabbedPane();
    private final Map<String, JLabel> errors = new LinkedHashMap<>();
    private final List<JButton> buttons = new ArrayList<>();
    private final JTextField timKH = new JTextField();
    private final JTextField timXe = new JTextField();
    private final JTextField maKH = new JTextField();
    private final JTextField ten = new JTextField();
    private final JTextField sdt = new JTextField();
    private final JTextField email = new JTextField();
    private final JTextField diaChi = new JTextField();
    private final JTextField maXe = new JTextField();
    private final JTextField bienSo = new JTextField();
    private final JComboBox<String> mauXe = new JComboBox<>();
    private final JComboBox<XeItem> xeCuaKhach = new JComboBox<>();
    private final JTextField maPhieu = new JTextField();
    private final JTextField nhanVien = new JTextField();
    private final JTextField ngayNhan = new JTextField();
    private final JTextField henTra = new JTextField();
    private final JTextField odo = new JTextField();
    private final JComboBox<String> nhienLieu = new JComboBox<>(new String[]{"-- Chọn --", "Cạn", "1/4", "1/2", "3/4", "Đầy"});
    private final JTextArea tinhTrang = area(3);
    private final JTextArea yeuCau = area(3);
    private final JTextArea ghiChu = area(2);
    private final JTextField tuKhoa = new JTextField();
    private final JComboBox<String> locTrangThai = new JComboBox<>(new String[]{"Tất cả"});
    private final JLabel soLuong = new JLabel("Chọn tab danh sách để tải dữ liệu từ SQL.");
    private final JLabel thongBao = new JLabel(" ");
    private final DefaultTableModel model = new DefaultTableModel(new String[]{"Mã tiếp nhận", "Mã phương tiện", "Nhân viên", "Ngày tiếp nhận", "Hẹn trả", "Trạng thái"}, 0){
        private static final long serialVersionUID = 1L;
        
        @Override
        public boolean isCellEditable(int r, int c) {
            return false;
        }
    };
    private final JTable table = new JTable(model);
    private List<PhieuTiepNhan> ds = new ArrayList<>();
    private String maNhanVien = "";
    private boolean daLuu;
    private boolean busy;
    private boolean changing;
    
    // ==================== KHỞI TẠO MÀN HÌNH ====================

    public LapPhieuTiepNhanUI() {
        this(new PhieuTiepNhanService(), null);
    }
    
    public LapPhieuTiepNhanUI(Runnable veTrangChu) {
        this(new PhieuTiepNhanService(), veTrangChu);
    }
    
    public LapPhieuTiepNhanUI(String maNV, String hoTenNV, Runnable veTrangChu) {
        this(new PhieuTiepNhanService(), veTrangChu);
        setNguoiTiepNhan(maNV, hoTenNV);
    }
    
    /**
     * Constructor tiêm Service dùng cho kiểm thử hoặc nguồn kết nối riêng. 
     */
    public LapPhieuTiepNhanUI(PhieuTiepNhanService service, Runnable veTrangChu) {
        this.service = Objects.requireNonNull(service);
        this.danhSachPhieu = new PhieuTiepNhanUI(service);
        setLayout(new BorderLayout(0, 10));
        setBackground(BG);
        setBorder(new EmptyBorder(10, 14, 10, 14));
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = new JLabel("TIẾP NHẬN XE");
        title.setFont(new Font("SansSerif", Font.BOLD, 19));
        header.add(title);
        if (veTrangChu != null) header.add(button("Về màn hình chính", veTrangChu), BorderLayout.EAST);
        add(header, BorderLayout.NORTH);
        mauXe.setEditable(true);
        maKH.setEditable(false);
        maXe.setEditable(false);
        maPhieu.setEditable(false);
        nhanVien.setEditable(false);
        ngayNhan.setEditable(false);
        henTra.setEditable(false);
        tabs.addTab("Lập phiếu tiếp nhận", taoTrangLap());
        tabs.addTab("Danh sách phiếu tiếp nhận", danhSachPhieu);
        tabs.setFont(new Font("SansSerif", Font.BOLD, 14));
        add(tabs);
        thongBao.setForeground(new Color(85, 96, 111));
        add(thongBao, BorderLayout.SOUTH);
        if (tabs.getSelectedIndex() == 1 && !busy) {
            danhSachPhieu.taiDanhSach();
        }
        xeCuaKhach.addActionListener((e)->{
            if (!changing && !busy && xeCuaKhach.getSelectedItem() instanceof XeItem) {
                Xe x = ((XeItem)xeCuaKhach.getSelectedItem()).xe;
                if (x != null) {
                    if ("Hoạt động".equals(x.getTrangThai())) ganXe(x); else {
                        moiXe();
                        JOptionPane.showMessageDialog(this, "Xe đã ngừng theo dõi. Khôi phục trước.");
                    }
                } else moiXe();
            }
        });
        table.setRowHeight(28);
        table.setAutoCreateRowSorter(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFont(new Font("SansSerif", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        tuKhoa.addActionListener((e)->{
            if (!busy) taiDanhSach();
        });
        timKH.addActionListener((e)->{
            if (!busy) timKhach();
        });
        timXe.addActionListener((e)->{
            if (!busy) timBienSo();
        });
        moiPhieu();
        capNhatMauXe();
    }
    
    // ==================== LIÊN KẾT ĐĂNG NHẬP VÀ ĐIỀU HƯỚNG ====================

    public void setNguoiTiepNhan(String maNV, String hoTenNV) {
        maNhanVien = PhieuTiepNhanService.trim(maNV);
        nhanVien.setText(maNhanVien + (PhieuTiepNhanService.trim(hoTenNV).isEmpty() ? "" : " • " + hoTenNV));
        nhanVien.setToolTipText("Lấy từ tài khoản đang đăng nhập");
    }
    
    public void hienThiDanhSachPhieuTiepNhan() {
        if (busy) {
            return;
        }

        if (tabs.getSelectedIndex() == 1) {
            danhSachPhieu.taiDanhSach();
        } else {
            tabs.setSelectedIndex(1);
        }
    }
    
    public void hienThiLapPhieuTiepNhan() {
        if (!busy) tabs.setSelectedIndex(0);
    }
    
    private static class FormContent extends JPanel implements Scrollable {
        private static final long serialVersionUID = 1L;
        
        public Dimension getPreferredScrollableViewportSize() {
            return getPreferredSize();
        }
        
        public int getScrollableUnitIncrement(Rectangle r, int o, int d) {
            return 20;
        }
        
        public int getScrollableBlockIncrement(Rectangle r, int o, int d) {
            return 160;
        }
        
        public boolean getScrollableTracksViewportWidth() {
            return true;
        }
        
        public boolean getScrollableTracksViewportHeight() {
            return false;
        }
    }
    
    private static JTextArea area(int rows) {
        JTextArea a = new JTextArea(rows, 30);
        a.setLineWrap(true);
        a.setWrapStyleWord(true);
        a.setFont(new Font("SansSerif", Font.PLAIN, 13));
        a.setMargin(new Insets(9, 10, 9, 10));
        return a;
    }
    
    // ==================== XÂY DỰNG GIAO DIỆN ====================

    private JPanel taoTrangLap() {
        JPanel content = new FormContent();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(BG);
        content.setBorder(new EmptyBorder(10, 0, 10, 0));
        JPanel kh = card("01   KHÁCH HÀNG", new BorderLayout(0, 6));
        JPanel search = new JPanel(new BorderLayout(8, 0));
        search.setOpaque(false);
        search.add(new JLabel("Số điện thoại:"), BorderLayout.WEST);
        search.add(timKH);
        timKH.setToolTipText("Nhập số điện thoại đầy đủ để tìm khách hàng");
        JPanel kb = flow();
        kb.add(button("Tìm khách", this::timKhach));
        kb.add(button("Khách mới", ()->{
            moiKhach();
            sdt.requestFocusInWindow();
        }));
        search.add(kb, BorderLayout.EAST);
        kh.add(search, BorderLayout.NORTH);
        JPanel kgrid = grid(2, 3);
        kgrid.add(field("Mã khách hàng · tự sinh", maKH, null));
        kgrid.add(field("Họ tên *", ten, "hoTen"));
        kgrid.add(field("Số điện thoại *", sdt, "soDienThoai"));
        kgrid.add(field("Email", email, "email"));
        kgrid.add(field("Địa chỉ", diaChi, "diaChi"));
        kgrid.add(field("Xe của khách · chọn để điền", xeCuaKhach, null));
        kh.add(kgrid);
        content.add(kh);
        content.add(Box.createVerticalStrut(10));
        JPanel xe = card("02   PHƯƠNG TIỆN", new BorderLayout(0, 6));
        JPanel xs = new JPanel(new BorderLayout(8, 0));
        xs.setOpaque(false);
        xs.add(new JLabel("Biển số:"), BorderLayout.WEST);
        xs.add(timXe);
        timXe.setToolTipText("Nhập biển số để tìm xe, có thể tìm trước khách");
        JPanel xb = flow();
        xb.add(button("Tìm xe", this::timBienSo));
        xb.add(button("Xe mới", this::moiXe));
        xs.add(xb, BorderLayout.EAST);
        xe.add(xs, BorderLayout.NORTH);
        JPanel xgrid = grid(1, 3);
        xgrid.add(field("Mã phương tiện · tự sinh", maXe, null));
        xgrid.add(field("Biển số *", bienSo, "bienSo"));
        xgrid.add(field("Mẫu xe * · chọn hoặc nhập", mauXe, "mauXe"));
        xe.add(xgrid);
        content.add(xe);
        content.add(Box.createVerticalStrut(10));
        JPanel phieu = card("03   THÔNG TIN TIẾP NHẬN", new BorderLayout(0, 8));
        JPanel pgrid = grid(2, 3);
        pgrid.add(field("Mã phiếu · tự sinh khi lưu", maPhieu, null));
        pgrid.add(field("Nhân viên tiếp nhận", nhanVien, "nhanVien"));
        pgrid.add(field("Ngày nhận · tự cập nhật lúc lưu", ngayNhan, null));
        pgrid.add(field("Ngày giờ hẹn trả", taoOHenTra(), "henTra"));
        pgrid.add(field("Số km đã đi *", odo, "odo"));
        pgrid.add(field("Mức nhiên liệu *", nhienLieu, "nhienLieu"));
        phieu.add(pgrid, BorderLayout.NORTH);
        JPanel notes = new JPanel(new BorderLayout(0, 8));
        notes.setOpaque(false);
        JPanel pair = grid(1, 2);
        pair.add(field("Hiện trạng xe * · tối đa 2000 ký tự", new JScrollPane(tinhTrang), "tinhTrang"));
        pair.add(field("Yêu cầu khách hàng * · tối đa 2000 ký tự", new JScrollPane(yeuCau), "yeuCau"));
        notes.add(pair, BorderLayout.NORTH);
        notes.add(field("Ghi chú thêm · không bắt buộc, tối đa 1000 ký tự", new JScrollPane(ghiChu), "ghiChu"));
        phieu.add(notes);
        content.add(phieu);
        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(20);
        JPanel page = new JPanel(new BorderLayout(0, 8));
        page.setBackground(BG);
        page.add(scroll);
        JPanel footer = flow();
        footer.add(new JLabel("* Bắt buộc  |  Phiếu mới: Đã tiếp nhận"));
        footer.add(button("Làm mới", this::moiPhieu));
        JButton save = button("Lưu phiếu tiếp nhận", () -> luu(false));

        save.setBackground(RED);
        save.setForeground(Color.WHITE);
        save.setOpaque(true);
        save.setContentAreaFilled(true);
        save.setBorderPainted(false);
        save.setBackground(RED);
        save.setForeground(Color.WHITE);
        footer.add(save);
        page.add(footer, BorderLayout.SOUTH);
        return page;
    }
    
    private JPanel taoTrangDanhSach() {
        JPanel page = card("DANH SÁCH PHIẾU TIẾP NHẬN", new BorderLayout(0, 10));
        JPanel search = new JPanel(new BorderLayout(8, 0));
        search.setOpaque(false);
        tuKhoa.setToolTipText("Tìm theo mã phiếu, biển số, tên khách hoặc điện thoại");
        search.add(new JLabel("Tìm phiếu:"), BorderLayout.WEST);
        search.add(tuKhoa);
        JPanel tools = flow();
        tools.add(locTrangThai);
        tools.add(button("Tìm kiếm", this::taiDanhSach));
        tools.add(button("Tải lại", ()->{
            tuKhoa.setText("");
            locTrangThai.setSelectedIndex(0);
            taiDanhSach();
        }));
        search.add(tools, BorderLayout.EAST);
        page.add(search, BorderLayout.NORTH);
        JScrollPane bang = new JScrollPane(table);
        bang.setColumnHeaderView(table.getTableHeader());
        page.add(bang);
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        footer.add(soLuong);
        footer.add(button("Xem chi tiết phiếu", this::xemChiTiet), BorderLayout.EAST);
        page.add(footer, BorderLayout.SOUTH);
        return page;
    }
    
    private JPanel card(String title, LayoutManager layout) {
        JPanel p = new JPanel(layout);
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(LINE), title, 0, 0, new Font("SansSerif", Font.BOLD, 13)), new EmptyBorder(6, 10, 6, 10)));
        return p;
    }
    
    private static JPanel flow() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        p.setOpaque(false);
        return p;
    }
    
    private static JPanel grid(int r, int c) {
        JPanel p = new JPanel(new GridLayout(r, c, 12, 4));
        p.setOpaque(false);   
        return p;
    }
    
    private JPanel field(String label, JComponent input, String key) {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setOpaque(false);
        JLabel caption = new JLabel(label);
        caption.setFont(new Font("SansSerif", Font.PLAIN, 12));
        p.add(caption, BorderLayout.NORTH);
        if (!(input instanceof JScrollPane)) {
            input.setPreferredSize(new Dimension(120, 30));
            input.setFont(new Font("SansSerif", Font.PLAIN, 13));
        }
        p.add(input);
        JLabel error = new JLabel(" ");
        error.setForeground(RED);
        error.setFont(new Font("SansSerif", Font.PLAIN, 11));
        if (key != null) errors.put(key, error);
        p.add(error, BorderLayout.SOUTH);
        return p;
    }
    
    private JButton button(String title, Runnable run) {
        JButton b = new JButton(title);
        b.setFont(new Font("SansSerif", Font.PLAIN, 13));
        b.setFocusPainted(false);
        b.setPreferredSize(new Dimension(Math.max(105, b.getPreferredSize().width), 30));
        b.addActionListener((e)->{
            if (!busy) run.run();
        });
        buttons.add(b);
        return b;
    }
    
    // ==================== HIỂN THỊ LỖI VÀ LÀM MỚI FORM ====================

    private void clearErrors() {
        for (JLabel l : errors.values()) {
            l.setText(" ");
            l.setToolTipText(null);
        }
    }
    
    private void showErrors(Map<String, String> es) {
        clearErrors();
        es.forEach((k,v)->{
            JLabel l = errors.get(k);
            if (l != null) {
                l.setText(v);
                l.setToolTipText(v);
            }
        });
        thongBao.setText("Thông tin chưa hợp lệ. Kiểm tra các dòng màu đỏ.");
    }
    
    private void lockKhach(boolean lock) {
        ten.setEditable(!lock);
        sdt.setEditable(!lock);
        email.setEditable(!lock);
        diaChi.setEditable(!lock);
    }
    
    private void moiKhach() {
        maKH.setText("");
        ten.setText("");
        sdt.setText("");
        email.setText("");
        diaChi.setText("");
        timKH.setText("");
        lockKhach(false);
        changing = true;
        xeCuaKhach.removeAllItems();
        changing = false;
        moiXe();
    }
    
    private void moiXe() {
        maXe.setText("");
        bienSo.setText("");
        bienSo.setEditable(true);
        mauXe.setEnabled(true);
        mauXe.setSelectedItem("");
        timXe.setText("");
        changing = true;
        xeCuaKhach.setSelectedIndex(-1);
        changing = false;
    }
    
    private JPanel taoOHenTra() {
        JPanel panel = new JPanel(new BorderLayout(5, 0));
        panel.setOpaque(false);

        henTra.setFont(new Font("SansSerif", Font.PLAIN, 13));
        henTra.setMargin(new Insets(2, 4, 2, 4));

        JButton btnChonNgay = button("...", this::chonNgayHenTra);
        btnChonNgay.setPreferredSize(new Dimension(34, 28));
        btnChonNgay.setToolTipText("Chọn ngày hẹn trả");

        panel.add(henTra, BorderLayout.CENTER);
        panel.add(btnChonNgay, BorderLayout.EAST);
        panel.setPreferredSize(new Dimension(120, 28));

        return panel;
    }
    
    private void chonNgayHenTra() {
        java.time.LocalDateTime banDau =
                java.time.LocalDateTime.now().plusDays(1);

        if (!henTra.getText().trim().isEmpty()) {
            try {
                banDau = java.time.LocalDateTime.parse(
                        henTra.getText().trim(),
                        PhieuTiepNhanService.NGAY
                );
            } catch (java.time.format.DateTimeParseException ex) {
                // Dùng ngày mai nếu giá trị cũ không hợp lệ.
            }
        }

        JComboBox<Integer> cboNgay = new JComboBox<>();
        JComboBox<Integer> cboThang = new JComboBox<>();
        JComboBox<Integer> cboNam = new JComboBox<>();
        JComboBox<Integer> cboGio = new JComboBox<>();
        JComboBox<Integer> cboPhut = new JComboBox<>();

        int namHienTai = java.time.LocalDate.now().getYear();
        int namDau = Math.min(namHienTai, banDau.getYear());
        int namCuoi = Math.max(namHienTai + 10, banDau.getYear());

        for (int nam = namDau; nam <= namCuoi; nam++) {
            cboNam.addItem(nam);
        }

        for (int thang = 1; thang <= 12; thang++) {
            cboThang.addItem(thang);
        }

        for (int gio = 0; gio < 24; gio++) {
            cboGio.addItem(gio);
        }

        for (int phut = 0; phut < 60; phut++) {
            cboPhut.addItem(phut);
        }

        cboNam.setSelectedItem(banDau.getYear());
        cboThang.setSelectedItem(banDau.getMonthValue());
        cboGio.setSelectedItem(banDau.getHour());
        cboPhut.setSelectedItem(banDau.getMinute());

        Runnable capNhatNgay = () -> {
            Integer ngayCu = (Integer) cboNgay.getSelectedItem();
            int nam = (Integer) cboNam.getSelectedItem();
            int thang = (Integer) cboThang.getSelectedItem();

            int soNgay = java.time.YearMonth.of(nam, thang).lengthOfMonth();

            cboNgay.removeAllItems();

            for (int ngay = 1; ngay <= soNgay; ngay++) {
                cboNgay.addItem(ngay);
            }

            if (ngayCu != null) {
                cboNgay.setSelectedItem(Math.min(ngayCu, soNgay));
            }
        };

        capNhatNgay.run();
        cboNgay.setSelectedItem(banDau.getDayOfMonth());

        cboThang.addActionListener(e -> capNhatNgay.run());
        cboNam.addActionListener(e -> capNhatNgay.run());

        JPanel form = new JPanel(new GridLayout(2, 5, 8, 6));
        form.setPreferredSize(new Dimension(480, 100));
        form.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        form.add(new JLabel("Ngày"));
        form.add(new JLabel("Tháng"));
        form.add(new JLabel("Năm"));
        form.add(new JLabel("Giờ"));
        form.add(new JLabel("Phút"));

        form.add(cboNgay);
        form.add(cboThang);
        form.add(cboNam);
        form.add(cboGio);
        form.add(cboPhut);

        Object[] nut = {"Chọn", "Bỏ hẹn trả", "Hủy"};

        int luaChon = JOptionPane.showOptionDialog(
                this,
                form,
                "Chọn ngày giờ hẹn trả",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                nut,
                nut[0]
        );

        if (luaChon == 0) {
            java.time.LocalDateTime ngayHen = java.time.LocalDateTime.of(
                    (Integer) cboNam.getSelectedItem(),
                    (Integer) cboThang.getSelectedItem(),
                    (Integer) cboNgay.getSelectedItem(),
                    (Integer) cboGio.getSelectedItem(),
                    (Integer) cboPhut.getSelectedItem()
            );

            if (ngayHen.isBefore(java.time.LocalDateTime.now())) {
                JOptionPane.showMessageDialog(
                        this,
                        "Ngày giờ hẹn trả phải sau thời điểm hiện tại."
                );
                return;
            }

            henTra.setText(ngayHen.format(PhieuTiepNhanService.NGAY));

        } else if (luaChon == 1) {
            henTra.setText("");
        }
    }
    
    private void moiPhieu() {
        daLuu = false;
        if (tabs.getTabCount() > 0) khoaNhap((Container)tabs.getComponentAt(0), false);
        clearErrors();
        moiKhach();
        maPhieu.setText("Tự sinh khi lưu");
        ngayNhan.setText(java.time.LocalDateTime.now().format(PhieuTiepNhanService.NGAY));
        henTra.setText("");
        odo.setText("");
        nhienLieu.setSelectedIndex(0);
        tinhTrang.setText("");
        yeuCau.setText("");
        ghiChu.setText("");
        thongBao.setText("Tìm hồ sơ có sẵn hoặc nhập khách và xe mới. Hẹn trả có thể để trống.");
    }
    
    private void ganKhach(KhachHang k) {
        maKH.setText(k.getMaKhachHang());
        ten.setText(k.getHoTen());
        sdt.setText(k.getSoDienThoai());
        email.setText(k.getEmail());
        diaChi.setText(k.getDiaChi());
        lockKhach(true);
    }
    
    private void ganXe(Xe x) {
        if (!"Hoạt động".equals(x.getTrangThai())) {
            JOptionPane.showMessageDialog(this, "Xe đã ngừng theo dõi. Khôi phục ở màn hình Xe trước.");
            return;
        }
        maXe.setText(x.getMaPhuongTien());
        bienSo.setText(x.getBienSo());
        bienSo.setEditable(false);
        mauXe.setSelectedItem(x.getMauXe());
        mauXe.setEnabled(false);
    }
    
    private static class HoSo {
        KhachHang kh;
        Xe xe;
        List<Xe> ds;
        
        HoSo(KhachHang k, Xe x, List<Xe> ds) {
            this.kh = k;
            this.xe = x;
            this.ds = ds;
        }
    }
    
    private void ganDanhSachXe(List<Xe> ds) {
        changing = true;
        try {
            xeCuaKhach.removeAllItems();
            xeCuaKhach.addItem(new XeItem(null));
            for (Xe x : ds) xeCuaKhach.addItem(new XeItem(x));
        } finally {
            changing = false;
        }
    }
    
    // ==================== TÌM KHÁCH HÀNG VÀ PHƯƠNG TIỆN ====================

    private void timKhach() {
        String so = timKH.getText().trim();
        if (so.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nhập số điện thoại vào ô tìm khách.");
            return;
        }
        run(()->{
            KhachHang k = service.timKhach(so);
            return new HoSo(k, null, k == null ? Collections.emptyList() : service.xeCuaKhach(k.getMaKhachHang()));
        }, (h)->{
            if (h.kh == null) {
                moiKhach();
                sdt.setText(PhieuTiepNhanService.chuanHoaSo(so));
                timKH.setText(so);
                thongBao.setText("Chưa có khách. Nhập thông tin để thêm mới khi lưu phiếu.");
                return;
            }
            if (!h.kh.isTrangThai()) {
                JOptionPane.showMessageDialog(this, "Khách đã ngừng sử dụng. Khôi phục ở màn hình Khách hàng trước.");
                return;
            }
            moiXe();
            ganKhach(h.kh);
            ganDanhSachXe(h.ds);
            clearErrors();
            thongBao.setText("Đã chọn khách. Chọn xe của khách hoặc nhập xe mới.");
        });
    }
    
    private void timBienSo() {
        String bs = timXe.getText().trim();
        String selected = maKH.getText();
        if (bs.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nhập biển số vào ô tìm xe.");
            return;
        }
        run(()->{
            Xe x = service.timXe(bs);
            KhachHang k = x == null ? null : service.khachTheoMa(x.getMaKhachHang());
            return new HoSo(k, x, k == null ? Collections.emptyList() : service.xeCuaKhach(k.getMaKhachHang()));
        }, (h)->{
            if (h.xe == null) {
                moiXe();
                bienSo.setText(bs);
                timXe.setText(bs);
                thongBao.setText("Chưa có xe. Nhập mẫu xe để thêm mới khi lưu.");
                return;
            }
            if (h.kh == null || !h.kh.isTrangThai()) {
                JOptionPane.showMessageDialog(this, "Chủ xe không tồn tại hoặc đã ngừng sử dụng.");
                return;
            }
            if (!"Hoạt động".equals(h.xe.getTrangThai())) {
                JOptionPane.showMessageDialog(this, "Xe đã ngừng theo dõi. Khôi phục trước.");
                return;
            }
            if (!selected.isEmpty() && !selected.equals(h.kh.getMaKhachHang())) {
                JOptionPane.showMessageDialog(this, "Xe thuộc khách " + h.kh.getMaKhachHang() + ". Bấm Khách mới để bỏ lựa chọn cũ rồi tìm lại xe.");
                return;
            }
            ganKhach(h.kh);
            ganDanhSachXe(h.ds);
            ganXe(h.xe);
            clearErrors();
            thongBao.setText("Đã chọn xe và đúng chủ xe từ SQL.");
        });
    }
    
    // ==================== LẤY DỮ LIỆU VÀ LƯU TIẾP NHẬN ====================

    private DuLieu duLieu(boolean confirm) {
        DuLieu d = new DuLieu();
        d.maKH = maKH.getText();
        d.hoTen = ten.getText();
        d.soDienThoai = sdt.getText();
        d.email = email.getText();
        d.diaChi = diaChi.getText();
        d.maXe = maXe.getText();
        d.bienSo = bienSo.getText();
        d.mauXe = String.valueOf(mauXe.getEditor().getItem());
        d.maNhanVien = maNhanVien;
        d.odo = odo.getText();
        d.nhienLieu = String.valueOf(nhienLieu.getSelectedItem());
        d.henTra = henTra.getText();
        d.tinhTrang = tinhTrang.getText();
        d.yeuCau = yeuCau.getText();
        d.ghiChu = ghiChu.getText();
        d.xacNhanOdoThap = confirm;
        return d;
    }
    
    private void luu(boolean confirm) {
        if (daLuu) {
            JOptionPane.showMessageDialog(this, "Phiếu đã lưu. Bấm Làm mới để lập phiếu tiếp theo.");
            return;
        }
        DuLieu d = duLieu(confirm);
        Map<String, String> e = service.kiemTra(d);
        if (!e.isEmpty()) {
            showErrors(e);
            return;
        }
        clearErrors();
        run(()->service.luu(d), (p)->{
            maPhieu.setText(p.getMaTiepNhan());
            ngayNhan.setText(p.getNgayGioTiepNhan().format(PhieuTiepNhanService.NGAY));
            daLuu = true;
            khoaFormDaLuu();
            for (JButton b : buttons) if ("Làm mới".equals(b.getText()) || "Về màn hình chính".equals(b.getText())) b.setEnabled(true);
            JOptionPane.showMessageDialog(this, "Đã lưu phiếu " + p.getMaTiepNhan() + " thành công.");
            thongBao.setText("Đã lưu " + p.getMaTiepNhan() + ". Bấm Làm mới để tiếp nhận xe tiếp theo.");
        });
    }
    
    private void capNhatMauXe() {
        run(service::mauXeDaCo, (list)->{
            Object old = mauXe.getEditor().getItem();
            mauXe.removeAllItems();
            for (String s : list) mauXe.addItem(s);
            mauXe.setSelectedItem(old == null ? "" : old);
        });
    }
    
    // ==================== DANH SÁCH VÀ CHI TIẾT PHIẾU ====================

    private void taiDanhSach() {
        String q = tuKhoa.getText();
        String state = String.valueOf(locTrangThai.getSelectedItem());
        run(()->service.danhSach(q, state), (list)->{
            ds = list;
            model.setRowCount(0);
            java.util.TreeSet<String> states = new java.util.TreeSet<>();
            for (PhieuTiepNhan p : list) {
                states.add(p.getTrangThai());
                model.addRow(new Object[]{p.getMaTiepNhan(), p.getMaPhuongTien(), p.getMaNhanVien(), fmt(p.getNgayGioTiepNhan()), fmt(p.getNgayGioHenTra()), p.getTrangThai()});
            }
            for (int i = 1; i < locTrangThai.getItemCount(); i++) states.add(locTrangThai.getItemAt(i));
            locTrangThai.removeAllItems();
            locTrangThai.addItem("Tất cả");
            for (String s : states) locTrangThai.addItem(s);
            locTrangThai.setSelectedItem(state);
            soLuong.setText("Hiển thị: " + list.size() + " phiếu");
        });
    }
    
    private static String fmt(java.time.LocalDateTime d) {
        return d == null ? "" : d.format(PhieuTiepNhanService.NGAY);
    }
    
    private void xemChiTiet() {
        int v = table.getSelectedRow();
        if (v < 0) {
            JOptionPane.showMessageDialog(this, "Chọn một phiếu để xem.");
            return;
        }
        PhieuTiepNhan p = ds.get(table.convertRowIndexToModel(v));
        JPanel detail = new JPanel(new BorderLayout(0, 10));
        JTextArea info = area(7);
        info.setEditable(false);
        info.setText("Phiếu: " + p.getMaTiepNhan() + "\nPhương tiện: " + p.getMaPhuongTien() + " • Nhân viên: " + p.getMaNhanVien() + "\nNgày nhận: " + fmt(p.getNgayGioTiepNhan()) + "\nHẹn trả: " + fmt(p.getNgayGioHenTra()) + "\nSố km: " + p.getOdo() + " • Nhiên liệu: " + p.getMucNhienLieu() + "\nTrạng thái: " + p.getTrangThai());
        detail.add(info, BorderLayout.NORTH);
        JTextArea notes = area(16);
        notes.setEditable(false);
        notes.setText("HIỆN TRẠNG BAN ĐẦU\n" + p.getTinhTrangBanDau() + "\n\nYÊU CẦU KHÁCH HÀNG\n" + p.getYeuCauKhachHang() + "\n\nGHI CHÚ\n" + PhieuTiepNhanService.trim(p.getGhiChu()));
        notes.setCaretPosition(0);
        detail.add(new JScrollPane(notes));
        detail.setPreferredSize(new Dimension(680, 510));
        JOptionPane.showMessageDialog(this, detail, "Chi tiết phiếu tiếp nhận", JOptionPane.PLAIN_MESSAGE);
    }
    
    private static class XeItem {
        final Xe xe;
        
        XeItem(Xe xe) {
            this.xe = xe;
        }
        
        @Override
        public String toString() {
            return xe == null ? "-- Chọn xe của khách --" : xe.getBienSo() + " • " + xe.getMauXe() + " • " + xe.getTrangThai();
        }
    }
    
    // ==================== KHÓA GIAO DIỆN VÀ XỬ LÝ NỀN ====================

    private void khoaNhap(Container parent, boolean lock) {
        for (Component c : parent.getComponents()) {
            // Danh sách tự quản lý trạng thái tải riêng.
            if (c instanceof PhieuTiepNhanUI) {
                continue;
            }

            if (c instanceof JTextField
                    || c instanceof JTextArea
                    || c instanceof JComboBox
                    || c instanceof JButton) {
                c.setEnabled(!lock);
            }

            if (c instanceof Container) {
                khoaNhap((Container) c, lock);
            }
        }
    }
    
    private void khoaFormDaLuu() {
        khoaNhap((Container)tabs.getComponentAt(0), true);
        for (JButton b : buttons) if ("Làm mới".equals(b.getText())) b.setEnabled(true);
    }
    
    private void setBusy(boolean value) {
        busy = value;
        khoaNhap(this, value);
        tabs.setEnabled(!value);
        if (!value) {
            lockKhach(!maKH.getText().isEmpty());
            bienSo.setEditable(maXe.getText().isEmpty());
            mauXe.setEnabled(maXe.getText().isEmpty());
            if (daLuu) khoaFormDaLuu();
        }
    }
    
    private <T>void run(Callable<T> task, Consumer<T> success) {
        if (busy) return;
        setBusy(true);
        thongBao.setText("Đang xử lý…");
        new SwingWorker<T, Void>(){
            
            @Override
            protected T doInBackground() throws Exception {
                return task.call();
            }
            
            @Override
            protected void done() {
                setBusy(false);
                try {
                    success.accept(get());
                } catch (Exception ex) {
                    Throwable cause = ex instanceof java.util.concurrent.ExecutionException ? ex.getCause() : ex;
                    if (cause instanceof LoiDuLieu) showErrors(((LoiDuLieu)cause).getLoi()); else if (cause instanceof CanXacNhanOdo) {
                        if (JOptionPane.showConfirmDialog(LapPhieuTiepNhanUI.this, cause.getMessage(), "Đối chiếu số km", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) luu(true);
                    } else {
                        thongBao.setText("Không thực hiện được thao tác. Kiểm tra thông báo lỗi.");
                        JOptionPane.showMessageDialog(LapPhieuTiepNhanUI.this, cause.getMessage(), "Không thực hiện được", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        }.execute();
    }
}