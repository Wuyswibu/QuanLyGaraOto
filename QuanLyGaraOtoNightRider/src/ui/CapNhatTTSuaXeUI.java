package ui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import dao.PhieuSuaChuaDao;
import entity.PhieuSuaChua;

public class CapNhatTTSuaXeUI extends JFrame implements ActionListener {

    private static final long serialVersionUID = 1L;

    // Bảng màu giao diện
    private static final Color MAU_CHU = new Color(40, 45, 55);
    private static final Color MAU_CHU_PHU = new Color(115, 125, 140);
    private static final Color MAU_NEN_NGOAI = new Color(225, 229, 235);
    private static final Color MAU_VIEN = new Color(218, 224, 230);
    private static final Color MAU_DONG_CHON = new Color(52, 101, 134);

    // Form thông tin phiếu sửa chữa
    private final JTextField txtMaPhieu = new JTextField();
    private final JTextField txtBienSo = new JTextField();
    private final JTextField txtKyThuatVien = new JTextField();
    private final JTextField txtKhoangSua = new JTextField();
    private final JTextField txtTinhTrang = new JTextField();
    private final JTextField txtDuKienHoanThanh = new JTextField();

    // Thanh tìm kiếm và bộ lọc bảng
    private static final String GOI_Y_TIM_KIEM = "Tìm kiếm theo mã phiếu, tiếp nhận...";
    private final JTextField txtTimKiem = new JTextField();
    private final JComboBox<String> cboLocTrangThai = new JComboBox<>(new String[]{
        "Tất cả", "Chưa bắt đầu", "Đang thực hiện", "Hoàn thành"
    });

    // Các nút thao tác
    private final JButton btnYeuCauXuatKho = taoNutPhu("Yêu cầu xuất kho");
    private final JButton btnCapNhatTinhTrang = taoNutPhu("Cập nhật tình trạng");
    private final JButton btnHoanThanhKiemTra = taoNutChinh("Hoàn thành kiểm tra");

    // Bảng công việc
    private final DefaultTableModel modelBang = new DefaultTableModel(
        new String[]{"Công việc", "Người thực hiện", "Bắt đầu", "Tình trạng", "Ghi chú"}, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable tableCongViec = new JTable(modelBang);

    // Sử dụng trực tiếp DAO đã có
    private final PhieuSuaChuaDao phieuSuaChuaDao = new PhieuSuaChuaDao();
    private PhieuSuaChua phieuHienTai = null;

    public CapNhatTTSuaXeUI() {
        setTitle("Cập nhật tình trạng sửa xe - NIGHT INVADER");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setSize(1340, 840);
        setMinimumSize(new Dimension(1200, 750));
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        khoiTaoGiaoDien();

        // Tự động load phiếu đầu tiên từ CSDL lên form khi vừa mở
        loadPhieuDauTien();
    }

    private void khoiTaoGiaoDien() {
        setLayout(new BorderLayout());

        // ==================== 1. THANH MENU BAR ====================
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(new Color(245, 247, 250));
        menuBar.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        Font menuFont = new Font("Arial", Font.BOLD, 15);

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

            if (tenCacMenu[i].contains("Dịch vụ")) {
                menu.setForeground(new Color(210, 30, 30));
                menu.setOpaque(true);
                menu.setBackground(new Color(235, 238, 242));
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
        panelToolbar.setOpaque(true);

        ImageIcon iconXeTB = loadIcon("/img/icon_xe.png", 22, 22);
        ImageIcon iconVectorTB = loadIcon("/img/icon-vector.png", 22, 22);
        ImageIcon iconHoaDonTB = loadIcon("/img/icon_hoadon.png", 22, 22);
        ImageIcon iconNhapKhoTB = loadIcon("/img/icon_nhapkho.jpg", 22, 22);

        panelToolbar.add(taoNutNhanh("Tiếp nhận xe", "F2", iconXeTB));
        panelToolbar.add(taoNutNhanh("Tình trạng sửa xe", "F5", iconVectorTB));
        panelToolbar.add(taoNutNhanh("Lập hóa đơn", "F3", iconHoaDonTB));
        panelToolbar.add(taoNutNhanh("Nhập kho", "F4", iconNhapKhoTB));

        JPanel panelThongTinPhu = new JPanel(new GridLayout(2, 1, 0, 2));
        panelThongTinPhu.setBackground(Color.WHITE);
        panelThongTinPhu.setOpaque(true);

        DateTimeFormatter dtfNgay = DateTimeFormatter.ofPattern("EEEE, dd/MM/yyyy", Locale.forLanguageTag("vi-VN"));
        JLabel lblNgay = new JLabel(LocalDate.now().format(dtfNgay), JLabel.RIGHT);
        lblNgay.setFont(new Font("Arial", Font.PLAIN, 12));
        lblNgay.setForeground(MAU_CHU);

        JLabel lblChiNhanh = new JLabel("Chi nhánh: Gara chính", JLabel.RIGHT);
        lblChiNhanh.setFont(new Font("Arial", Font.PLAIN, 11));
        lblChiNhanh.setForeground(Color.GRAY);

        panelThongTinPhu.add(lblNgay);
        panelThongTinPhu.add(lblChiNhanh);

        JPanel panelTopContainer = new JPanel(new BorderLayout());
        panelTopContainer.setBackground(Color.WHITE);
        panelTopContainer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 224, 230)),
            BorderFactory.createEmptyBorder(0, 10, 0, 15)
        ));
        panelTopContainer.add(panelToolbar, BorderLayout.CENTER);
        panelTopContainer.add(panelThongTinPhu, BorderLayout.EAST);
        add(panelTopContainer, BorderLayout.NORTH);

        // ==================== 3. VÙNG NỘI DUNG CHÍNH (CARD TRẮNG) ====================
        JPanel panelBaoNgoai = new JPanel(new BorderLayout());
        panelBaoNgoai.setBackground(MAU_NEN_NGOAI);
        panelBaoNgoai.setBorder(new EmptyBorder(18, 45, 18, 45));

        JPanel cardTrang = new JPanel(new BorderLayout(0, 15));
        cardTrang.setBackground(Color.WHITE);
        cardTrang.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(MAU_VIEN, 1),
            new EmptyBorder(22, 26, 22, 26)
        ));

        JPanel panelPhiaTren = new JPanel();
        panelPhiaTren.setLayout(new BoxLayout(panelPhiaTren, BoxLayout.Y_AXIS));
        panelPhiaTren.setOpaque(false);

        JLabel lblTieuDe = new JLabel("CẬP NHẬT TÌNH TRẠNG SỬA XE");
        lblTieuDe.setFont(new Font("Arial", Font.BOLD, 16));
        lblTieuDe.setForeground(MAU_CHU);
        lblTieuDe.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelPhiaTren.add(lblTieuDe);
        panelPhiaTren.add(Box.createVerticalStrut(10));

        JLabel lblQuyTrinh = new JLabel("Tiếp nhận → chờ duyệt → đang sửa → kiểm tra → chờ bàn giao.");
        lblQuyTrinh.setFont(new Font("Arial", Font.PLAIN, 13));
        lblQuyTrinh.setForeground(MAU_CHU_PHU);
        lblQuyTrinh.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelPhiaTren.add(lblQuyTrinh);
        panelPhiaTren.add(Box.createVerticalStrut(16));

        // Form 2 cột
        JPanel panelForm = new JPanel(new GridLayout(3, 2, 25, 12));
        panelForm.setOpaque(false);
        panelForm.setAlignmentX(Component.LEFT_ALIGNMENT);

        themTruongNhap(panelForm, "Phiếu sửa chữa", txtMaPhieu);
        themTruongNhap(panelForm, "Biển số / Mã tiếp nhận", txtBienSo);
        themTruongNhap(panelForm, "Kỹ thuật viên", txtKyThuatVien);
        themTruongNhap(panelForm, "Khoang sửa chữa", txtKhoangSua);
        themTruongNhap(panelForm, "Tình trạng", txtTinhTrang);
        themTruongNhap(panelForm, "Dự kiến hoàn thành", txtDuKienHoanThanh);

        panelPhiaTren.add(panelForm);
        cardTrang.add(panelPhiaTren, BorderLayout.NORTH);

        // Giữa: Thanh tìm kiếm + Bảng công việc
        JPanel panelTrungTam = new JPanel(new BorderLayout(0, 10));
        panelTrungTam.setOpaque(false);

        JPanel panelTimKiem = new JPanel(new BorderLayout());
        panelTimKiem.setBackground(new Color(241, 244, 248));
        panelTimKiem.setBorder(new EmptyBorder(6, 12, 6, 12));

        txtTimKiem.setText(GOI_Y_TIM_KIEM);
        txtTimKiem.setFont(new Font("Arial", Font.PLAIN, 13));
        txtTimKiem.setForeground(new Color(130, 140, 150));
        txtTimKiem.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        txtTimKiem.setOpaque(false);

        txtTimKiem.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (txtTimKiem.getText().equals(GOI_Y_TIM_KIEM)) {
                    txtTimKiem.setText("");
                    txtTimKiem.setForeground(MAU_CHU);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (txtTimKiem.getText().trim().isEmpty()) {
                    txtTimKiem.setText(GOI_Y_TIM_KIEM);
                    txtTimKiem.setForeground(new Color(130, 140, 150));
                }
            }
        });

        // Bắt sự kiện Enter trên ô tìm kiếm và ô mã phiếu
        txtTimKiem.addActionListener(e -> {
            String tuKhoa = txtTimKiem.getText().trim();
            if (!tuKhoa.isEmpty() && !tuKhoa.equals(GOI_Y_TIM_KIEM)) {
                loadDuLieu(tuKhoa);
            }
        });

        txtMaPhieu.addActionListener(e -> {
            String ma = txtMaPhieu.getText().trim();
            if (!ma.isEmpty()) {
                loadDuLieu(ma);
            }
        });

        JPanel panelLocTrangThai = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        panelLocTrangThai.setOpaque(false);
        JLabel lblLoc = new JLabel("Trạng thái:");
        lblLoc.setFont(new Font("Arial", Font.PLAIN, 13));
        lblLoc.setForeground(MAU_CHU);

        cboLocTrangThai.setFont(new Font("Arial", Font.PLAIN, 13));
        cboLocTrangThai.setBackground(Color.WHITE);
        cboLocTrangThai.setPreferredSize(new Dimension(130, 28));
        cboLocTrangThai.addActionListener(e -> loadBangCongViec());

        panelLocTrangThai.add(lblLoc);
        panelLocTrangThai.add(cboLocTrangThai);

        panelTimKiem.add(txtTimKiem, BorderLayout.CENTER);
        panelTimKiem.add(panelLocTrangThai, BorderLayout.EAST);
        panelTrungTam.add(panelTimKiem, BorderLayout.NORTH);

        // Bảng dữ liệu
        tableCongViec.setFont(new Font("Arial", Font.PLAIN, 13));
        tableCongViec.setRowHeight(36);
        tableCongViec.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableCongViec.setSelectionBackground(MAU_DONG_CHON);
        tableCongViec.setSelectionForeground(Color.WHITE);
        tableCongViec.setShowGrid(false);
        tableCongViec.setIntercellSpacing(new Dimension(0, 0));

        tableCongViec.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tableCongViec.getTableHeader().setBackground(new Color(237, 241, 245));
        tableCongViec.getTableHeader().setForeground(MAU_CHU);
        tableCongViec.getTableHeader().setReorderingAllowed(false);
        tableCongViec.getTableHeader().setPreferredSize(new Dimension(0, 36));

        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable bang, Object value, boolean selected, boolean focus, int row, int column) {
                super.getTableCellRendererComponent(bang, value, selected, focus, row, column);
                setBorder(new EmptyBorder(0, 12, 0, 12));
                if (!selected) {
                    setBackground(row % 2 == 0 ? Color.WHITE : new Color(249, 250, 252));
                    setForeground(MAU_CHU);
                }
                return this;
            }
        };

        for (int i = 0; i < tableCongViec.getColumnCount(); i++) {
            tableCongViec.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }

        int[] doRongCot = {230, 200, 130, 200, 300};
        for (int i = 0; i < doRongCot.length; i++) {
            tableCongViec.getColumnModel().getColumn(i).setPreferredWidth(doRongCot[i]);
        }

        JScrollPane cuonBang = new JScrollPane(tableCongViec);
        cuonBang.setBorder(BorderFactory.createLineBorder(MAU_VIEN));
        cuonBang.getViewport().setBackground(Color.WHITE);
        panelTrungTam.add(cuonBang, BorderLayout.CENTER);

        cardTrang.add(panelTrungTam, BorderLayout.CENTER);

        // Nút thao tác
        JPanel panelNut = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelNut.setOpaque(false);
        panelNut.add(btnYeuCauXuatKho);
        panelNut.add(btnCapNhatTinhTrang);
        panelNut.add(btnHoanThanhKiemTra);

        cardTrang.add(panelNut, BorderLayout.SOUTH);
        panelBaoNgoai.add(cardTrang, BorderLayout.CENTER);
        add(panelBaoNgoai, BorderLayout.CENTER);

        // ==================== 4. THANH TRẠNG THÁI (FOOTER) ====================
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(new Color(245, 247, 250));
        footer.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));

        JLabel lblFooterTrai = new JLabel("Đang hoạt động | Kho chính | Chủ Gara");
        lblFooterTrai.setFont(new Font("Arial", Font.PLAIN, 12));
        lblFooterTrai.setForeground(Color.GRAY);

        DateTimeFormatter dtfFooter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        JLabel lblFooterPhai = new JLabel("NIGHT INVADER - " + LocalDate.now().format(dtfFooter));
        lblFooterPhai.setFont(new Font("Arial", Font.PLAIN, 12));
        lblFooterPhai.setForeground(Color.GRAY);

        footer.add(lblFooterTrai, BorderLayout.WEST);
        footer.add(lblFooterPhai, BorderLayout.EAST);
        add(footer, BorderLayout.SOUTH);

        btnYeuCauXuatKho.addActionListener(this);
        btnCapNhatTinhTrang.addActionListener(this);
        btnHoanThanhKiemTra.addActionListener(this);
    }

    // ==================== XỬ LÝ TRUY VẤN VÀ LOAD DỮ LIỆU TẠI GUI ====================

    private void loadPhieuDauTien() {
        try {
            ArrayList<PhieuSuaChua> ds = phieuSuaChuaDao.layDanhSachPhieuSuaChua();
            if (ds != null && !ds.isEmpty()) {
                hienThiThongTinPhieu(ds.get(0));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void loadDuLieu(String tuKhoa) {
        try {
            // Tìm theo mã phiếu sửa chữa trước
            PhieuSuaChua psc = phieuSuaChuaDao.timTheoMa(tuKhoa.trim());

            // Nếu không thấy, tìm theo mã tiếp nhận
            if (psc == null) {
                psc = phieuSuaChuaDao.timTheoMaTiepNhan(tuKhoa.trim());
            }

            if (psc == null) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy phiếu có mã: " + tuKhoa);
                return;
            }

            hienThiThongTinPhieu(psc);

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi truy vấn CSDL: " + ex.getMessage());
        }
    }

    private void hienThiThongTinPhieu(PhieuSuaChua psc) {
        this.phieuHienTai = psc;

        txtMaPhieu.setText(psc.getMaPhieuSuaChua());
        txtBienSo.setText(psc.getMaTiepNhan());
        txtKyThuatVien.setText("Trần Quốc Huy");
        txtKhoangSua.setText("Khoang 02");
        txtTinhTrang.setText(psc.getTrangThai() != null ? psc.getTrangThai() : "Đang thực hiện");

        if (psc.getNgayLap() != null) {
            txtDuKienHoanThanh.setText(psc.getNgayLap().format(
                DateTimeFormatter.ofPattern("dd/MM/yyyy • HH:mm")
            ));
        } else {
            txtDuKienHoanThanh.setText("—");
        }

        loadBangCongViec();
    }

    private void loadBangCongViec() {
        if (phieuHienTai == null) return;

        modelBang.setRowCount(0);
        String loc = (String) cboLocTrangThai.getSelectedItem();

        String noiDungChinh = phieuHienTai.getChanDoan() != null ? phieuHienTai.getChanDoan() : "Kiểm tra động cơ";
        String ghiChu = phieuHienTai.getGhiChu() != null ? phieuHienTai.getGhiChu() : "";

        // Tạo danh sách công việc hiển thị linh hoạt
        Object[][] cacCongViec = {
            {noiDungChinh, "Trần Quốc Huy", "08:30", "Hoàn thành", "Đã kiểm tra kỹ"},
            {"Thay dầu / phụ tùng bảo dưỡng", "Trần Quốc Huy", "09:15", "Đang thực hiện", ghiChu},
            {"Kiểm tra tổng quát và chạy thử", "Phạm Minh Đức", "—", "Chưa bắt đầu", "Chờ hoàn tất thay thế"}
        };

        for (Object[] cv : cacCongViec) {
            String tinhTrangCV = String.valueOf(cv[3]);
            if (loc == null || "Tất cả".equalsIgnoreCase(loc) || loc.equalsIgnoreCase(tinhTrangCV)) {
                modelBang.addRow(cv);
            }
        }

        if (tableCongViec.getRowCount() > 0) {
            tableCongViec.setRowSelectionInterval(0, 0);
        }
    }

    private void themTruongNhap(JPanel container, String nhan, JTextField field) {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setOpaque(false);

        JLabel lbl = new JLabel(nhan);
        lbl.setFont(new Font("Arial", Font.BOLD, 12));
        lbl.setForeground(new Color(60, 68, 80));

        field.setFont(new Font("Arial", Font.PLAIN, 13));
        field.setForeground(MAU_CHU);
        field.setBackground(Color.WHITE);
        field.setPreferredSize(new Dimension(200, 34));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(MAU_VIEN, 1),
            new EmptyBorder(4, 10, 4, 10)
        ));

        p.add(lbl, BorderLayout.NORTH);
        p.add(field, BorderLayout.CENTER);
        container.add(p);
    }

    private JButton taoNutPhu(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 12));
        btn.setForeground(MAU_CHU);
        btn.setBackground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(MAU_VIEN, 1),
            new EmptyBorder(9, 16, 9, 16)
        ));
        return btn;
    }

    private JButton taoNutChinh(String text) {
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

    private JButton taoNutNhanh(String tieuDe, String phimTat, ImageIcon icon) {
        JButton btn = new JButton();
        btn.setLayout(new BorderLayout(10, 0));

        if (icon != null) {
            btn.add(new JLabel(icon), BorderLayout.WEST);
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

    private JMenu taoMenuDropdown(String tenMenu, String[] cacItem) {
        JMenu menu = new JMenu(tenMenu);
        for (String ten : cacItem) {
            if ("-".equals(ten)) {
                menu.addSeparator();
            } else {
                JMenuItem item = new JMenuItem(ten);
                item.setFont(new Font("Arial", Font.PLAIN, 13));
                item.addActionListener(e -> JOptionPane.showMessageDialog(this, "Đang mở: " + ten));
                menu.add(item);
            }
        }
        return menu;
    }

    private ImageIcon loadIcon(String path, int width, int height) {
        java.net.URL url = getClass().getResource(path);
        if (url == null) return null;
        ImageIcon icon = new ImageIcon(url);
        Image anh = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(anh);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object nguon = e.getSource();
        if (nguon == btnYeuCauXuatKho) {
            JOptionPane.showMessageDialog(this, "Mở form yêu cầu xuất kho phụ tùng.");
        } else if (nguon == btnCapNhatTinhTrang) {
            int selectedRow = tableCongViec.getSelectedRow();
            if (selectedRow < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một công việc để cập nhật!");
                return;
            }
            String tenCV = String.valueOf(modelBang.getValueAt(selectedRow, 0));
            String[] trangThaiMoi = {"Chưa bắt đầu", "Đang thực hiện", "Hoàn thành"};
            String chon = (String) JOptionPane.showInputDialog(
                this, "Chọn tình trạng mới cho công việc: " + tenCV,
                "Cập nhật tình trạng", JOptionPane.QUESTION_MESSAGE, null, trangThaiMoi, trangThaiMoi[1]);

            if (chon != null) {
                modelBang.setValueAt(chon, selectedRow, 3);
                JOptionPane.showMessageDialog(this, "Đã cập nhật tình trạng công việc thành công!");
            }
        } else if (nguon == btnHoanThanhKiemTra) {
            String maPhieu = txtMaPhieu.getText().trim();
            if (maPhieu.isEmpty()) return;
            try {
                // Gọi thẳng DAO cập nhật trạng thái
                boolean thanhCong = phieuSuaChuaDao.capNhatTrangThai(maPhieu, "Chờ bàn giao");
                if (thanhCong) {
                    txtTinhTrang.setText("Chờ bàn giao");
                    JOptionPane.showMessageDialog(this, "Đã hoàn thành kiểm tra và cập nhật trạng thái phiếu sang 'Chờ bàn giao'!");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi cập nhật CSDL: " + ex.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // KÍCH HOẠT KẾT NỐI TRƯỚC KHI KHỞI CHẠY GIAO DIỆN
        try {
            connectDB.ConnectDB.getInstance().connect();
        } catch (Exception ex) {
            System.err.println("Cảnh báo kết nối CSDL: " + ex.getMessage());
        }

        SwingUtilities.invokeLater(() -> new CapNhatTTSuaXeUI().setVisible(true));
    }
}