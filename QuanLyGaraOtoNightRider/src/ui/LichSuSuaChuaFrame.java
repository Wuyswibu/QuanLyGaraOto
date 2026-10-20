package ui;

import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class LichSuSuaChuaFrame extends JFrame {
    private static final long serialVersionUID = 1L;

    private static final Color CHU = new Color(35, 41, 51);
    private static final Color CHU_PHU = new Color(112, 124, 139);
    private static final Color VIEN = new Color(216, 222, 230);
    private static final Color DO = new Color(188, 25, 58);

    // ==================== CÁC THÀNH PHẦN GIAO DIỆN ====================

    private final JTextField txtBienSo = new JTextField();
    private final JTextField txtChuXe = new JTextField();
    private final JTextField txtMauXe = new JTextField();

    private final JTextField txtTuNgay = new JTextField();
    private final JTextField txtDenNgay = new JTextField();
    private final JTextField txtTimKiem = new JTextField(25);

    private final JComboBox<String> cboTrangThai = new JComboBox<>(
            new String[]{
                "Tất cả",
                "Chờ sửa chữa",
                "Đang sửa chữa",
                "Đã hoàn thành",
                "Đã hủy"
            });

    private final JButton btnTimXe = taoNut("Tìm xe", true);
    private final JButton btnTimKiem = taoNut("Tìm kiếm", true);
    private final JButton btnLoc = taoNut("Lọc lịch sử", false);
    private final JButton btnLamMoi = taoNut("Làm mới", false);
    private final JButton btnXemPhieu = taoNut("Xem phiếu sửa chữa", true);

    private final JLabel lblSoLuong = new JLabel("Số lần sửa: 0");

    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{
                "Ngày lập phiếu",
                "Mã phiếu sửa chữa",
                "Số km lúc tiếp nhận",
                "Nội dung sửa chữa",
                "Tổng tiền hóa đơn",
                "Trạng thái sửa chữa"
            }, 0) {
        private static final long serialVersionUID = 1L;

        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }

        @Override
        public Class<?> getColumnClass(int column) {
            if (column == 0) {
                return LocalDate.class;
            }

            if (column == 2) {
                return Integer.class;
            }

            if (column == 4) {
                return BigDecimal.class;
            }

            return String.class;
        }
    };

    private final JTable table = new JTable(model);

    // ==================== CONSTRUCTOR ====================

    public LichSuSuaChuaFrame() {
        setTitle("Lịch sử sửa chữa - Night Invader");
        setSize(1340, 900);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        setResizable(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        khoiTaoGiaoDien();
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

            if (i == 2) {
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

        // 3. NỘI DUNG LỊCH SỬ SỬA CHỮA

        JPanel panelCenter = new JPanel(new BorderLayout());
        panelCenter.setBackground(new Color(216, 220, 228));
        panelCenter.setBorder(new EmptyBorder(20, 55, 15, 55));

        JPanel khung = new JPanel(new BorderLayout());
        khung.setBackground(Color.WHITE);
        khung.setBorder(BorderFactory.createLineBorder(VIEN));

        JLabel tieuDe = new JLabel("LỊCH SỬ SỬA CHỮA");
        tieuDe.setOpaque(true);
        tieuDe.setBackground(new Color(246, 247, 250));
        tieuDe.setForeground(CHU);
        tieuDe.setFont(new Font("Arial", Font.BOLD, 16));
        tieuDe.setBorder(new EmptyBorder(10, 16, 10, 16));
        khung.add(tieuDe, BorderLayout.NORTH);

        JPanel noiDung = new JPanel(new BorderLayout(0, 16));
        noiDung.setBackground(Color.WHITE);
        noiDung.setBorder(new EmptyBorder(16, 18, 16, 18));

        // FORM THÔNG TIN XE VÀ BỘ LỌC

        JPanel phanTren = new JPanel(new BorderLayout(0, 12));
        phanTren.setOpaque(false);

        JLabel moTa = new JLabel("Tra cứu các lần sửa chữa và hóa đơn của phương tiện.");
        moTa.setFont(new Font("Arial", Font.PLAIN, 13));
        moTa.setForeground(CHU_PHU);
        phanTren.add(moTa, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(2, 3, 20, 12));
        form.setOpaque(false);

        JPanel panelTimXe = new JPanel(new BorderLayout(6, 0));
        panelTimXe.setOpaque(false);

        dinhDangOText(txtBienSo);

        panelTimXe.add(txtBienSo, BorderLayout.CENTER);
        panelTimXe.add(btnTimXe, BorderLayout.EAST);

        themO(form, "Biển số", panelTimXe);
        themO(form, "Chủ xe", txtChuXe);
        themO(form, "Mẫu xe", txtMauXe);
        themO(form, "Từ ngày (dd/MM/yyyy)", txtTuNgay);
        themO(form, "Đến ngày (dd/MM/yyyy)", txtDenNgay);
        themO(form, "Trạng thái sửa chữa", cboTrangThai);

        txtChuXe.setEditable(false);
        txtMauXe.setEditable(false);

        phanTren.add(form, BorderLayout.CENTER);

        JPanel panelLoc = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        panelLoc.setOpaque(false);
        panelLoc.add(btnLoc);

        phanTren.add(panelLoc, BorderLayout.SOUTH);
        noiDung.add(phanTren, BorderLayout.NORTH);

        // BẢNG LỊCH SỬ SỬA CHỮA

        JPanel panelDanhSach = new JPanel(new BorderLayout(0, 10));
        panelDanhSach.setBackground(Color.WHITE);

        JPanel thanhDanhSach = new JPanel(new BorderLayout(10, 0));
        thanhDanhSach.setBackground(new Color(237, 241, 245));
        thanhDanhSach.setBorder(new EmptyBorder(6, 10, 6, 10));

        JLabel lblDanhSach = new JLabel("DANH SÁCH LỊCH SỬ SỬA CHỮA");
        lblDanhSach.setFont(new Font("Arial", Font.BOLD, 12));
        lblDanhSach.setForeground(CHU);

        JPanel panelTimKiem = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        panelTimKiem.setOpaque(false);

        dinhDangOText(txtTimKiem);
        txtTimKiem.setPreferredSize(new Dimension(250, 30));
        txtTimKiem.setToolTipText("Nhập mã phiếu hoặc nội dung sửa chữa");

        panelTimKiem.add(txtTimKiem);
        panelTimKiem.add(btnTimKiem);

        thanhDanhSach.add(lblDanhSach, BorderLayout.WEST);
        thanhDanhSach.add(panelTimKiem, BorderLayout.EAST);

        table.setRowHeight(34);
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setSelectionBackground(new Color(53, 105, 139));
        table.setSelectionForeground(Color.WHITE);
        table.setGridColor(VIEN);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(237, 241, 245));
        table.getTableHeader().setReorderingAllowed(false);

        int[] rong = {140, 160, 160, 350, 180, 180};
        for (int i = 0; i < rong.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(rong[i]);
        }

        JScrollPane cuonBang = new JScrollPane(table);
        cuonBang.setBorder(BorderFactory.createLineBorder(VIEN));
        cuonBang.getViewport().setBackground(Color.WHITE);

        lblSoLuong.setFont(new Font("Arial", Font.PLAIN, 12));
        lblSoLuong.setForeground(CHU_PHU);

        panelDanhSach.add(thanhDanhSach, BorderLayout.NORTH);
        panelDanhSach.add(cuonBang, BorderLayout.CENTER);
        panelDanhSach.add(lblSoLuong, BorderLayout.SOUTH);

        noiDung.add(panelDanhSach, BorderLayout.CENTER);

        // CÁC NÚT CHỨC NĂNG: CHƯA GẮN XỬ LÝ

        JPanel thaoTac = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        thaoTac.setOpaque(false);
        thaoTac.add(btnLamMoi);
        thaoTac.add(btnXemPhieu);

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
    }

    // ==================== CÁC HÀM HỖ TRỢ GIAO DIỆN ====================

    private void themO(JPanel form, String ten, JComponent field) {
        JPanel o = new JPanel(new BorderLayout(0, 4));
        o.setOpaque(false);

        JLabel nhan = new JLabel(ten);
        nhan.setFont(new Font("Arial", Font.BOLD, 12));
        nhan.setForeground(new Color(87, 100, 117));

        field.setFont(new Font("Arial", Font.PLAIN, 13));
        field.setBackground(Color.WHITE);
        field.setForeground(CHU);
        field.setPreferredSize(new Dimension(200, 34));

        if (field instanceof JTextField) {
            dinhDangOText((JTextField) field);
        }

        o.add(nhan, BorderLayout.NORTH);
        o.add(field, BorderLayout.CENTER);
        form.add(o);
    }

    private void dinhDangOText(JTextField field) {
        field.setFont(new Font("Arial", Font.PLAIN, 13));
        field.setBackground(Color.WHITE);
        field.setForeground(CHU);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(VIEN),
                new EmptyBorder(5, 7, 5, 7)));
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

    // ==================== MAIN CHẠY THỬ GIAO DIỆN ====================

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> new LichSuSuaChuaFrame().setVisible(true));
    }
}