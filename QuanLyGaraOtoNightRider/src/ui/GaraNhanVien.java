package ui;
import connectDB.ConnectDB;
import dao.NhanVienDao;
import entity.NhanVien;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;
public class GaraNhanVien extends JPanel {
    private static final long serialVersionUID = 1L;
    // Khai báo tabbedPane làm biến toàn cục của class
    private JTabbedPane tabbedPane;
    // Các Component cho Tab Quản lý nhân sự
    private JTextField txtMaNV, txtHoTen, txtSoDienThoai, txtNgayVaoLam, txtTimKiem;
    private JComboBox<String> cbVaiTro, cbTrangThai;
    private JTable tableNhanVien;
    // Các Component cho Tab Phân công KTV
    private JComboBox<String> cbPhieuSuaChua, cbKyThuatVien;
    private JTextField txtGhiChuPhanCong;
    private JTable tablePhanCong;
    private NhanVienDao nhanVienDao;
    // Model quản lý dữ liệu cho 2 bảng
    private DefaultTableModel tableModelNhanVien;
    private DefaultTableModel tableModelPhanCong;
    // Constructor mặc định (mở tab 0: Hồ sơ nhân viên)
    public GaraNhanVien() {
        this(0);
    }
    // Constructor có tham số để nhảy thẳng vào tab mong muốn
    public GaraNhanVien(int tabMacDinh) {
        // Dùng kết nối database đã mở trong Main.
        nhanVienDao = new NhanVienDao();
        khoiTaoGiaoDien();
        if (tabMacDinh >= 0 && tabbedPane != null && tabMacDinh < tabbedPane.getTabCount()) {
            tabbedPane.setSelectedIndex(tabMacDinh);
        }
        // ĐỔ DỮ LIỆU TỪ DB LÊN BẢNG NHÂN VIÊN
        loadDataToTableNhanVien();
    }
    // ========== HÀM TẢI DỮ LIỆU TỪ SQL LÊN BẢNG NHÂN VIÊN ==========
    private void loadDataToTableNhanVien() {
        if (tableModelNhanVien == null) return;
        tableModelNhanVien.setRowCount(0);
        List<NhanVien> listNV = null;
        try {
            listNV = nhanVienDao.layDanhSachNhanVien();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        for (NhanVien nv : listNV) {
            String ngayVaoStr = (nv.getNgayVaoLam() != null) ? nv.getNgayVaoLam().format(dtf) : "";
            String vaiTro = (nv.getTenChuc() != null) ? nv.getTenChuc() : nv.getMaPhanQuyen();
            tableModelNhanVien.addRow(new Object[]{
                    nv.getMaNhanVien(),
                    nv.getHoTen(),
                    nv.getSoDienThoai(),
                    vaiTro,
                    ngayVaoStr,
                    nv.getTrangThai()
            });
        }
    }
    private void khoiTaoGiaoDien() {
        setLayout(new BorderLayout());
        // ==================== 3. NỘI DUNG CHÍNH: KHUNG CARD MỞ RỘNG TOÀN MÀN HÌNH ====================
        JPanel panelCenter = new JPanel(new BorderLayout());
        panelCenter.setBackground(new Color(240, 243, 246));
        panelCenter.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20)); // Lề xung quanh card
        JPanel panelCardChinh = new JPanel(new BorderLayout());
        panelCardChinh.setBackground(Color.WHITE);
        panelCardChinh.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 215, 220), 1),
                BorderFactory.createEmptyBorder(20, 25, 20, 25)
        ));
        // --- 3.1. Tiêu đề chung của Card ---
        JPanel panelCardTop = new JPanel(new BorderLayout());
        panelCardTop.setBackground(Color.WHITE);
        panelCardTop.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        JLabel lblTieuDeCard = new JLabel("QUẢN LÝ NHÂN SỰ & PHÂN CÔNG KỸ THUẬT VIÊN");
        lblTieuDeCard.setFont(new Font("Arial", Font.BOLD, 18));
        lblTieuDeCard.setForeground(new Color(30, 35, 45));
        JLabel lblMoTa = new JLabel("Cập nhật hồ sơ nhân viên và điều phối công việc sửa chữa cho xưởng.");
        lblMoTa.setFont(new Font("Arial", Font.PLAIN, 13));
        lblMoTa.setForeground(Color.GRAY);
        JPanel pnlTitleWrap = new JPanel(new GridLayout(2, 1));
        pnlTitleWrap.setBackground(Color.WHITE);
        pnlTitleWrap.add(lblTieuDeCard);
        pnlTitleWrap.add(lblMoTa);
        panelCardTop.add(pnlTitleWrap, BorderLayout.WEST);
        // --- 3.2. JTabbedPane chứa 2 chức năng ---
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 14));
        tabbedPane.setBackground(new Color(245, 247, 250));
        tabbedPane.addTab("Hồ Sơ Nhân Viên", taoTabQuanLyNhanSu());
        tabbedPane.addTab("Phân Công Kỹ Thuật Viên", taoTabPhanCongKTV());
        // Gắn vào Card Chính
        panelCardChinh.add(panelCardTop, BorderLayout.NORTH);
        panelCardChinh.add(tabbedPane, BorderLayout.CENTER);
        panelCenter.add(panelCardChinh, BorderLayout.CENTER);
        // Menu, toolbar và footer dùng chung của GaraManHinhChinh.
        add(panelCenter, BorderLayout.CENTER);
    }
    // ==================== TAB 1: QUẢN LÝ NHÂN SỰ ====================
    private JPanel taoTabQuanLyNhanSu() {
        JPanel panelTab = new JPanel(new BorderLayout());
        panelTab.setBackground(Color.WHITE);
        panelTab.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        // 1. Khối nhập liệu
        txtMaNV = new JTextField(); txtMaNV.setEditable(false); txtMaNV.setBackground(new Color(245, 247, 250));
        txtHoTen = new JTextField();
        txtSoDienThoai = new JTextField();
        txtNgayVaoLam = new JTextField();
        cbVaiTro = new JComboBox<>(new String[]{"Tiếp nhận", "Kỹ thuật viên", "Nhân viên Kho", "Thu ngân", "Quản lý"});
        cbVaiTro.setFont(new Font("Arial", Font.PLAIN, 13)); cbVaiTro.setBackground(Color.WHITE);
        cbTrangThai = new JComboBox<>(new String[]{"Đang làm việc", "Đã nghỉ / Khóa"});
        cbTrangThai.setFont(new Font("Arial", Font.PLAIN, 13)); cbTrangThai.setBackground(Color.WHITE);
        JPanel panelGridFields = new JPanel(new GridLayout(2, 3, 30, 15));
        panelGridFields.setBackground(Color.WHITE);
        panelGridFields.setBorder(BorderFactory.createEmptyBorder(10, 5, 15, 5));
        panelGridFields.add(taoOnhapLieu("Mã nhân viên (Tự động)", txtMaNV));
        panelGridFields.add(taoOnhapLieu("Họ và Tên (*)", txtHoTen));
        panelGridFields.add(taoOnhapLieu("Số điện thoại (*)", txtSoDienThoai));
        panelGridFields.add(taoOnhapLieu("Vai trò / Chức vụ", cbVaiTro));
        panelGridFields.add(taoOnhapLieu("Ngày vào làm", txtNgayVaoLam));
        panelGridFields.add(taoOnhapLieu("Trạng thái", cbTrangThai));
        // 2. Thanh bộ lọc & Bảng dữ liệu
        JPanel panelSearchFilter = new JPanel(new BorderLayout());
        panelSearchFilter.setBackground(new Color(245, 247, 250));
        panelSearchFilter.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 225, 230), 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        txtTimKiem = new JTextField(" Tìm kiếm nhân viên...");
        txtTimKiem.setFont(new Font("Arial", Font.ITALIC, 13));
        txtTimKiem.setForeground(Color.GRAY);
        txtTimKiem.setBorder(null);
        txtTimKiem.setOpaque(false);
        JComboBox<String> cbLocVaiTro = new JComboBox<>(new String[]{"Tất cả vai trò", "Tiếp nhận", "Kỹ thuật viên", "Nhân viên Kho", "Thu ngân"});
        cbLocVaiTro.setFont(new Font("Arial", Font.PLAIN, 12));
        cbLocVaiTro.setBackground(Color.WHITE);
        ImageIcon iconTim = loadIcon("/img/icon_tim.png", 16, 16);
        if (iconTim != null) {
            panelSearchFilter.add(new JLabel(iconTim), BorderLayout.WEST);
        }
        panelSearchFilter.add(txtTimKiem, BorderLayout.CENTER);
        panelSearchFilter.add(cbLocVaiTro, BorderLayout.EAST);
        String[] columnNames = {"Mã NV", "Họ Tên", "SĐT", "Vai Trò", "Ngày Vào Làm", "Trạng Thái"};
        tableModelNhanVien = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tableNhanVien = new JTable(tableModelNhanVien);
        // Custom giao diện bảng
        tableNhanVien.setRowHeight(32);
        tableNhanVien.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        tableNhanVien.getTableHeader().setBackground(new Color(230, 235, 240));
        tableNhanVien.setFont(new Font("Arial", Font.PLAIN, 13));
        tableNhanVien.setSelectionBackground(new Color(40, 100, 150));
        tableNhanVien.setSelectionForeground(Color.WHITE);
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        tableNhanVien.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        JPanel panelTableSection = new JPanel(new BorderLayout(0, 10));
        panelTableSection.setBackground(Color.WHITE);
        panelTableSection.add(panelSearchFilter, BorderLayout.NORTH);
        panelTableSection.add(new JScrollPane(tableNhanVien), BorderLayout.CENTER);
        // 3. Khối nút bấm
        JPanel panelBottomButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelBottomButtons.setBackground(Color.WHITE);
        panelBottomButtons.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        JButton btnLamMoi = taoNutChucNang("Làm mới", Color.WHITE, new Color(50, 55, 65));
        JButton btnKhoa = taoNutChucNang("Khóa tài khoản", Color.WHITE, new Color(210, 30, 30));
        JButton btnCapNhat = taoNutChucNang("Cập nhật", new Color(40, 100, 150), Color.WHITE);
        JButton btnThemMoi = taoNutChucNang("Thêm mới", new Color(25, 135, 84), Color.WHITE);
        panelBottomButtons.add(btnLamMoi);
        panelBottomButtons.add(btnKhoa);
        panelBottomButtons.add(btnCapNhat);
        panelBottomButtons.add(btnThemMoi);
        panelTab.add(panelGridFields, BorderLayout.NORTH);
        panelTab.add(panelTableSection, BorderLayout.CENTER);
        panelTab.add(panelBottomButtons, BorderLayout.SOUTH);
        return panelTab;
    }
    // ==================== TAB 2: PHÂN CÔNG KỸ THUẬT VIÊN ====================
    private JPanel taoTabPhanCongKTV() {
        JPanel panelTab = new JPanel(new BorderLayout());
        panelTab.setBackground(Color.WHITE);
        panelTab.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        // 1. Khối nhập liệu phân công
        cbPhieuSuaChua = new JComboBox<>(new String[]{"[SC00215] Xe Honda Civic - 51G-123.45 (Chờ sửa)", "[SC00216] Xe Mazda CX-5 - 60A-999.99 (Chờ sửa)"});
        cbPhieuSuaChua.setFont(new Font("Arial", Font.BOLD, 13));
        cbPhieuSuaChua.setBackground(Color.WHITE);
        cbKyThuatVien = new JComboBox<>(new String[]{"[NV002] Trần Ngọc Huy (Đang rảnh)", "[NV005] Lê Văn Tám (Đang sửa 1 xe)"});
        cbKyThuatVien.setFont(new Font("Arial", Font.BOLD, 13));
        cbKyThuatVien.setBackground(Color.WHITE);
        txtGhiChuPhanCong = new JTextField("Ưu tiên xử lý lỗi lốc lạnh trước.");
        JPanel panelGridFields = new JPanel(new GridLayout(2, 2, 30, 15));
        panelGridFields.setBackground(Color.WHITE);
        panelGridFields.setBorder(BorderFactory.createEmptyBorder(10, 5, 15, 5));
        panelGridFields.add(taoOnhapLieu("Chọn Phiếu Sửa Chữa (Xe)", cbPhieuSuaChua));
        panelGridFields.add(taoOnhapLieu("Chọn Kỹ Thuật Viên Phụ Trách", cbKyThuatVien));
        panelGridFields.add(taoOnhapLieu("Ghi chú / Nhắc nhở cho KTV", txtGhiChuPhanCong));
        panelGridFields.add(new JLabel());
        // 2. Bảng danh sách đang phân công
        JLabel lblDanhSach = new JLabel("DANH SÁCH XE ĐANG SỬA TẠI XƯỞNG");
        lblDanhSach.setFont(new Font("Arial", Font.BOLD, 14));
        lblDanhSach.setForeground(new Color(210, 30, 30));
        JPanel pnlTitleTable = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlTitleTable.setBackground(Color.WHITE);
        pnlTitleTable.add(lblDanhSach);
        String[] columnNames = {"Mã Phân Công", "Mã Phiếu SC", "Biển Số Xe", "Kỹ Thuật Viên", "Ngày Phân Công", "Tiến Độ"};
        Object[][] data = {
                {"PC001", "SC00212", "51H-555.55", "Trần Ngọc Huy", "12/10/2026", "Đang sửa chữa (50%)"},
                {"PC002", "SC00214", "61A-123.88", "Lê Văn Tám", "12/10/2026", "Chờ phụ tùng"}
        };
        tableModelPhanCong = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tablePhanCong = new JTable(tableModelPhanCong);
        tablePhanCong.setRowHeight(32);
        tablePhanCong.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        tablePhanCong.getTableHeader().setBackground(new Color(230, 235, 240));
        tablePhanCong.setFont(new Font("Arial", Font.PLAIN, 13));
        tablePhanCong.setSelectionBackground(new Color(40, 100, 150));
        tablePhanCong.setSelectionForeground(Color.WHITE);
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        tablePhanCong.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        JPanel panelTableSection = new JPanel(new BorderLayout(0, 5));
        panelTableSection.setBackground(Color.WHITE);
        panelTableSection.add(pnlTitleTable, BorderLayout.NORTH);
        panelTableSection.add(new JScrollPane(tablePhanCong), BorderLayout.CENTER);
        // 3. Khối nút bấm phân công
        JPanel panelBottomButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelBottomButtons.setBackground(Color.WHITE);
        panelBottomButtons.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        JButton btnHuyPC = taoNutChucNang("Hủy phân công", Color.WHITE, new Color(210, 30, 30));
        JButton btnCapNhatPC = taoNutChucNang("Cập nhật lại", new Color(40, 100, 150), Color.WHITE);
        JButton btnPhanCong = taoNutChucNang("Xác nhận Phân Công", new Color(25, 135, 84), Color.WHITE);
        btnPhanCong.addActionListener(e -> JOptionPane.showMessageDialog(this, "Đã giao xe cho Kỹ thuật viên thành công!", "Phân công", JOptionPane.INFORMATION_MESSAGE));
        panelBottomButtons.add(btnHuyPC);
        panelBottomButtons.add(btnCapNhatPC);
        panelBottomButtons.add(btnPhanCong);
        panelTab.add(panelGridFields, BorderLayout.NORTH);
        panelTab.add(panelTableSection, BorderLayout.CENTER);
        panelTab.add(panelBottomButtons, BorderLayout.SOUTH);
        return panelTab;
    }
    // ==================== HÀM TIỆN ÍCH ====================
    private JPanel taoOnhapLieu(String labelText, JComponent inputComponent) {
        JPanel panel = new JPanel(new BorderLayout(0, 5));
        panel.setBackground(Color.WHITE);
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("Arial", Font.BOLD, 12));
        lbl.setForeground(new Color(80, 85, 95));
        inputComponent.setFont(new Font("Arial", Font.PLAIN, 14));
        inputComponent.setPreferredSize(new Dimension(250, 35));
        if (inputComponent instanceof JTextField) {
            inputComponent.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(200, 205, 210), 1),
                    BorderFactory.createEmptyBorder(4, 10, 4, 10)
            ));
        }
        panel.add(lbl, BorderLayout.NORTH);
        panel.add(inputComponent, BorderLayout.CENTER);
        return panel;
    }
    private JButton taoNutChucNang(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 13));
        btn.setBackground(bg);
        btn.setContentAreaFilled(false);
        btn.setOpaque(true);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        if (bg.equals(Color.WHITE)) {
            btn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(fg, 1),
                    BorderFactory.createEmptyBorder(8, 20, 8, 20)
            ));
        } else {
            btn.setOpaque(true);
            btn.setBorderPainted(false);
            btn.setBorder(BorderFactory.createEmptyBorder(9, 20, 9, 20));
        }
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
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
            System.out.println("Không thể tải ảnh: " + path);
        }
        return null;
    }
    public void chonTab(int chiSo) {
        if (chiSo >= 0 && chiSo < tabbedPane.getTabCount()) {
            tabbedPane.setSelectedIndex(chiSo);
        }
    }
    // Chạy thử riêng; ứng dụng chính vẫn chạy từ Main.
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        try {
            ConnectDB.getInstance().connect();
            System.out.println("Kết nối database thành công!");
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Không thể kết nối database: " + ex.getMessage());
            return;
        }
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("GaraNhanVien - NIGHT INVADER");
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setContentPane(new GaraNhanVien());
            frame.setSize(1340, 840);
            frame.setLocationRelativeTo(null);
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            frame.setVisible(true);
        });
    }
}
