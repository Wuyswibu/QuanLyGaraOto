package ui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.text.Normalizer;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.Locale;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import dao.PhieuSuaChuaDao;
import entity.PhieuSuaChua;

public class LichSuSuaChuaFrame extends JPanel implements ActionListener {

    private static final long serialVersionUID = 1L;

    private static final Color CHU = new Color(35, 41, 51);
    private static final Color CHU_PHU = new Color(112, 124, 139);
    private static final Color VIEN = new Color(216, 222, 230);
    private static final Color DO = new Color(188, 25, 58);

    private static final DateTimeFormatter DINH_DANG = DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);

    private final JTextField txtBienSo = new JTextField();
    private final JTextField txtChuXe = new JTextField();
    private final JTextField txtMauXe = new JTextField();

    private final JTextField txtTuNgay = new JTextField();
    private final JTextField txtDenNgay = new JTextField();
    private final JTextField txtTimKiem = new JTextField();

    private final JComboBox<String> cboTrangThai = new JComboBox<>(
            new String[]{
                "Tất cả",
                "Chờ sửa chữa",
                "Đang sửa chữa",
                "Đã hoàn thành",
                "Đã hủy"
            });

    private final JButton btnTimXe = taoNut("Tìm xe", false);
    private final JButton btnLoc = taoNut("Lọc lịch sử", false);
    private final JButton btnLamMoi = taoNut("Làm mới", false);
    private final JButton btnXemPhieu = taoNut("Xem phiếu sửa chữa", false);

    private final JLabel lblSoLuong = new JLabel("Số lần sửa: 0");

    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{
                "Ngày lập phiếu",
                "Mã phiếu sửa chữa",
                "Mã tiếp nhận",
                "Chẩn đoán / Nội dung",
                "Loại phiếu",
                "Trạng thái sửa chữa"
            }, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }

        @Override
        public Class<?> getColumnClass(int column) {
            if (column == 0) {
                return LocalDate.class;
            }
            return String.class;
        }
    };

    private final JTable table = new JTable(model);

    // GIỮ NGUYÊN FILE DAO, KHÔNG CẦN CHỈNH SỬA DAO
    private final PhieuSuaChuaDao phieuSuaChuaDao = new PhieuSuaChuaDao();
    private final ArrayList<PhieuSuaChua> danhSachHienThi = new ArrayList<>();

    public LichSuSuaChuaFrame() {
        setLayout(new BorderLayout());
        setBackground(new Color(216, 220, 228));
        setBorder(new EmptyBorder(20, 55, 15, 55));

        txtChuXe.setEditable(false);
        txtMauXe.setEditable(false);

        JPanel khung = new JPanel(new BorderLayout());
        khung.setBackground(Color.WHITE);
        khung.setBorder(BorderFactory.createLineBorder(VIEN));

        JLabel tieuDe = new JLabel("LỊCH SỬ SỬA CHỮA");
        tieuDe.setOpaque(true);
        tieuDe.setBackground(new Color(246, 247, 250));
        tieuDe.setFont(new Font("Arial", Font.BOLD, 16));
        tieuDe.setForeground(CHU);
        tieuDe.setBorder(new EmptyBorder(10, 16, 10, 16));
        khung.add(tieuDe, BorderLayout.NORTH);

        JPanel noiDung = new JPanel(new BorderLayout(0, 18));
        noiDung.setBackground(Color.WHITE);
        noiDung.setBorder(new EmptyBorder(12, 18, 16, 18));

        // ==================== THÔNG TIN XE ====================
        JPanel phanTren = new JPanel(new BorderLayout(0, 12));
        phanTren.setOpaque(false);

        JLabel moTa = new JLabel("Tra cứu các lần sửa chữa của phương tiện theo biển số hoặc mã phiếu.");
        moTa.setFont(new Font("Arial", Font.PLAIN, 13));
        moTa.setForeground(CHU_PHU);
        phanTren.add(moTa, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(2, 3, 20, 10));
        form.setOpaque(false);

        JPanel timXe = new JPanel(new BorderLayout(6, 0));
        timXe.setOpaque(false);
        timXe.add(txtBienSo, BorderLayout.CENTER);
        timXe.add(btnTimXe, BorderLayout.EAST);

        themO(form, "Biển số / Mã phiếu", timXe);
        themO(form, "Chủ xe", txtChuXe);
        themO(form, "Mẫu xe", txtMauXe);
        themO(form, "Từ ngày (dd/MM/yyyy)", txtTuNgay);
        themO(form, "Đến ngày (dd/MM/yyyy)", txtDenNgay);
        themO(form, "Trạng thái sửa chữa", cboTrangThai);

        phanTren.add(form, BorderLayout.CENTER);
        noiDung.add(phanTren, BorderLayout.NORTH);

        // ==================== BẢNG LỊCH SỬ ====================
        JPanel vungBang = new JPanel(new BorderLayout(0, 10));
        vungBang.setOpaque(false);

        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.setRowHeight(36);
        table.setAutoCreateRowSorter(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setSelectionBackground(new Color(53, 105, 139));
        table.setSelectionForeground(Color.WHITE);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));

        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(237, 241, 245));
        table.getTableHeader().setReorderingAllowed(false);

        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable bang, Object value, boolean selected,
                    boolean focus, int row, int column) {
                super.getTableCellRendererComponent(bang, value, selected, focus, row, column);

                setBorder(new EmptyBorder(0, 8, 0, 8));
                setHorizontalAlignment(SwingConstants.LEFT);

                if (value instanceof LocalDate) {
                    setText(((LocalDate) value).format(DINH_DANG));
                }

                if (!selected) {
                    setBackground(row % 2 == 0 ? Color.WHITE : new Color(247, 248, 250));
                    setForeground(CHU);
                }

                return this;
            }
        };

        table.setDefaultRenderer(String.class, renderer);
        table.setDefaultRenderer(LocalDate.class, renderer);

        int[] rong = {130, 160, 140, 350, 140, 150};
        for (int i = 0; i < rong.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(rong[i]);
        }

        JScrollPane cuon = new JScrollPane(table);
        cuon.setBorder(BorderFactory.createLineBorder(VIEN));
        cuon.getViewport().setBackground(Color.WHITE);
        vungBang.add(cuon, BorderLayout.CENTER);

        lblSoLuong.setForeground(CHU_PHU);
        vungBang.add(lblSoLuong, BorderLayout.SOUTH);

        noiDung.add(vungBang, BorderLayout.CENTER);

        JPanel thaoTac = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        thaoTac.setOpaque(false);
        thaoTac.add(btnLamMoi);
        thaoTac.add(btnXemPhieu);

        noiDung.add(thaoTac, BorderLayout.SOUTH);
        khung.add(noiDung, BorderLayout.CENTER);
        add(khung, BorderLayout.CENTER);

        btnTimXe.addActionListener(this);
        txtBienSo.addActionListener(this);
        btnLoc.addActionListener(this);
        txtTimKiem.addActionListener(this);
        txtTuNgay.addActionListener(this);
        txtDenNgay.addActionListener(this);
        btnLamMoi.addActionListener(this);
        btnXemPhieu.addActionListener(this);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                btnXemPhieu.setEnabled(table.getSelectedRow() >= 0);
            }
        });

        // Tự động load toàn bộ phiếu từ CSDL lên bảng ngay khi mở giao diện
        loadDuLieuTuCSDL();
    }

    private void loadDuLieuTuCSDL() {
    	table.setRowSorter(null);
        table.clearSelection();
        
        model.setRowCount(0);
        danhSachHienThi.clear();
        btnXemPhieu.setEnabled(false);

        try {
            ArrayList<PhieuSuaChua> danhSachGoc = phieuSuaChuaDao.layDanhSachPhieuSuaChua();
            if (danhSachGoc == null || danhSachGoc.isEmpty()) {
                lblSoLuong.setText("Số lần sửa: 0");
                return;
            }
            String tuKhoa = chuanHoa(txtBienSo.getText().trim());
            LocalDate tuNgay = txtTuNgay.getText().trim().isEmpty() ? null : LocalDate.parse(txtTuNgay.getText().trim(), DINH_DANG);
            LocalDate denNgay = txtDenNgay.getText().trim().isEmpty() ? null : LocalDate.parse(txtDenNgay.getText().trim(), DINH_DANG);
            String trangThai = (String) cboTrangThai.getSelectedItem();

            for (PhieuSuaChua psc : danhSachGoc) {
                LocalDate ngayLap = psc.getNgayLap() != null ? psc.getNgayLap().toLocalDate() : null;

                // Lọc ngày
                if (tuNgay != null && ngayLap != null && ngayLap.isBefore(tuNgay)) {
                    continue;
                }
                if (denNgay != null && ngayLap != null && ngayLap.isAfter(denNgay)) {
                    continue;
                }

                // Lọc trạng thái
                if (!"Tất cả".equals(trangThai) && !trangThai.equalsIgnoreCase(psc.getTrangThai())) {
                    continue;
                }

                // Lọc từ khóa tìm kiếm
                if (!tuKhoa.isEmpty()) {
                    String chuoi = chuanHoa(psc.getMaPhieuSuaChua() + " " + psc.getMaTiepNhan() + " " + (psc.getChanDoan() != null ? psc.getChanDoan() : ""));
                    if (!chuoi.contains(tuKhoa)) {
                        continue;
                    }
                }

                danhSachHienThi.add(psc);
            }

            // Đổ lên bảng
            for (PhieuSuaChua psc : danhSachHienThi) {
                model.addRow(new Object[]{
                    psc.getNgayLap() != null ? psc.getNgayLap().toLocalDate() : null,
                    psc.getMaPhieuSuaChua(),
                    psc.getMaTiepNhan(),
                    psc.getChanDoan() != null ? psc.getChanDoan() : "-",
                    psc.getLoaiPhieu(),
                    psc.getTrangThai()
                });
            }

            lblSoLuong.setText("Số lần sửa tìm thấy: " + danhSachHienThi.size());

            table.setAutoCreateRowSorter(true);

            if (table.getRowCount() > 0) {
                table.setRowSelectionInterval(0, 0);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi kết nối CSDL: " + ex.getMessage());
        }
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        Object nguon = e.getSource();

        if (nguon == btnLamMoi) {
            txtBienSo.setText("");
            txtChuXe.setText("");
            txtMauXe.setText("");
            txtTuNgay.setText("");
            txtDenNgay.setText("");
            txtTimKiem.setText("");
            cboTrangThai.setSelectedIndex(0);

            loadDuLieuTuCSDL();
            txtBienSo.requestFocusInWindow();

        } else if (nguon == btnTimXe || nguon == txtBienSo
                || nguon == btnLoc || nguon == txtTimKiem
                || nguon == txtTuNgay || nguon == txtDenNgay) {

            if (!validData()) {
                return;
            }

            loadDuLieuTuCSDL();

            if (danhSachHienThi.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Không có lịch sử sửa chữa phù hợp.");
            }

        } else if (nguon == btnXemPhieu) {
            int dong = table.getSelectedRow();
            
            // Thêm điều kiện kiểm tra tránh lệch index của Sorter
            if (dong < 0 || dong >= table.getRowCount()) {
                JOptionPane.showMessageDialog(this, "Chọn một lần sửa trên bảng để xem chi tiết.");
                return;
            }

            int dongThuc = table.convertRowIndexToModel(dong);
            if (dongThuc >= danhSachHienThi.size()) {
                return;
            }

            PhieuSuaChua psc = danhSachHienThi.get(dongThuc);
            String ngayLapStr = (psc.getNgayLap() != null)
                    ? psc.getNgayLap().toLocalDate().format(DINH_DANG)
                    : "—";

            JTextArea chiTiet = new JTextArea(
                    "Mã phiếu sửa chữa: " + psc.getMaPhieuSuaChua()
                    + "\nMã tiếp nhận: " + psc.getMaTiepNhan()
                    + "\nNgày lập phiếu: " + ngayLapStr
                    + "\nLoại phiếu: " + psc.getLoaiPhieu()
                    + "\nTrạng thái: " + psc.getTrangThai()
                    + "\n\nChẩn đoán / Nội dung sửa chữa:\n" + (psc.getChanDoan() != null ? psc.getChanDoan() : "(Không có)")
                    + "\n\nGhi chú:\n" + (psc.getGhiChu() != null ? psc.getGhiChu() : "(Không có)"));

            chiTiet.setEditable(false);
            chiTiet.setLineWrap(true);
            chiTiet.setWrapStyleWord(true);
            chiTiet.setFont(new Font("Arial", Font.PLAIN, 14));
            chiTiet.setCaretPosition(0);

            JScrollPane cuon = new JScrollPane(chiTiet);
            cuon.setPreferredSize(new Dimension(650, 420));

            JOptionPane.showMessageDialog(this, cuon, "Chi tiết phiếu sửa chữa", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private boolean validData() {
        LocalDate tuNgay = null;
        LocalDate denNgay = null;

        if (!txtTuNgay.getText().trim().isEmpty()) {
            try {
                tuNgay = LocalDate.parse(txtTuNgay.getText().trim(), DINH_DANG);
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this, "Từ ngày phải đúng dạng dd/MM/yyyy.");
                txtTuNgay.requestFocusInWindow();
                return false;
            }
        }

        if (!txtDenNgay.getText().trim().isEmpty()) {
            try {
                denNgay = LocalDate.parse(txtDenNgay.getText().trim(), DINH_DANG);
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this, "Đến ngày phải đúng dạng dd/MM/yyyy.");
                txtDenNgay.requestFocusInWindow();
                return false;
            }
        }

        if (tuNgay != null && denNgay != null && tuNgay.isAfter(denNgay)) {
            JOptionPane.showMessageDialog(this, "Từ ngày không được lớn hơn đến ngày.");
            txtTuNgay.requestFocusInWindow();
            return false;
        }

        return true;
    }

    private void themO(JPanel form, String ten, JComponent field) {
        JPanel o = new JPanel(new BorderLayout(0, 5));
        o.setOpaque(false);

        JLabel nhan = new JLabel(ten);
        nhan.setFont(new Font("Arial", Font.BOLD, 12));
        nhan.setForeground(new Color(87, 100, 117));

        field.setFont(new Font("Arial", Font.PLAIN, 13));
        field.setBackground(Color.WHITE);
        field.setForeground(CHU);
        field.setPreferredSize(new Dimension(200, 34));

        o.add(nhan, BorderLayout.NORTH);
        o.add(field, BorderLayout.CENTER);
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

    private static String chuanHoa(String s) {
        return Normalizer.normalize(s, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replace('đ', 'd')
                .replace('Đ', 'D')
                .toLowerCase(Locale.ROOT);
    }

    // ==================== MENU, HEADER, FOOTER ====================

    public JFrame taoCuaSo() {
        JFrame cuaSo = new JFrame("Lịch sử sửa chữa - Night Invader");
        cuaSo.setSize(1340, 840);
        cuaSo.setLocationRelativeTo(null);
        cuaSo.setResizable(true);
        cuaSo.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        cuaSo.setLayout(new BorderLayout());

        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(new Color(245, 247, 250));
        menuBar.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        Font menuFont = new Font("Arial", Font.BOLD, 16);

        JMenu logo = new JMenu("NIGHT INVADER");
        logo.setFont(new Font("Arial", Font.BOLD, 15));
        logo.setForeground(new Color(210, 30, 30));
        logo.setMargin(new Insets(5, 10, 5, 10));
        menuBar.add(logo);

        String[] tenMenu = {
            "Hệ thống", "Khách hàng ", "Xe ", "Dịch vụ ",
            "Phụ tùng", "Kho hàng ", "Hóa đơn ",
            "Thống kê ", "Nhân viên "
        };

        String[][] cacItem = {
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

        for (int i = 0; i < tenMenu.length; i++) {
            JMenu menu = taoMenuDropdown(tenMenu[i], cacItem[i]);
            menu.setFont(menuFont);
            menu.setMargin(new Insets(5, 10, 5, 10));
            menuBar.add(menu);
        }

        JMenu taiKhoan = new JMenu("Xin chào, Chủ Gara");
        taiKhoan.setFont(menuFont);
        taiKhoan.setMargin(new Insets(5, 10, 5, 10));

        ImageIcon iconUser = loadIcon("/img/icon_nguoi.jpg", 20, 20);
        if (iconUser != null) {
            taiKhoan.setIcon(iconUser);
        }

        menuBar.add(Box.createHorizontalGlue());
        menuBar.add(taiKhoan);
        cuaSo.setJMenuBar(menuBar);

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        toolbar.setBackground(Color.WHITE);
        toolbar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 224, 230)));

        toolbar.add(taoNutNhanh("Tiếp nhận xe", "F2", loadIcon("/img/icon_xe.png", 22, 22)));
        toolbar.add(taoNutNhanh("Tình trạng sửa xe", "F5", loadIcon("/img/icon-vector.png", 22, 22)));
        toolbar.add(taoNutNhanh("Lập hóa đơn", "F3", loadIcon("/img/icon_hoadon.png", 22, 22)));
        toolbar.add(taoNutNhanh("Nhập kho", "F4", loadIcon("/img/icon_nhapkho.jpg", 22, 22)));

        JPanel thongTin = new JPanel(new GridLayout(2, 1));
        thongTin.setBackground(Color.WHITE);

        JLabel ngay = new JLabel(LocalDate.now().format(DINH_DANG), JLabel.RIGHT);
        ngay.setFont(new Font("Arial", Font.PLAIN, 12));

        JLabel chiNhanh = new JLabel("Chi nhánh: Gara chính", JLabel.RIGHT);
        chiNhanh.setFont(new Font("Arial", Font.PLAIN, 11));
        chiNhanh.setForeground(Color.GRAY);

        thongTin.add(ngay);
        thongTin.add(chiNhanh);

        JPanel header = new JPanel(new BorderLayout());
        header.add(toolbar, BorderLayout.CENTER);
        header.add(thongTin, BorderLayout.EAST);
        header.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 15));

        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(new Color(245, 247, 250));
        footer.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));

        JLabel trangThai = new JLabel("Đang hoạt động | Vai trò: Chủ Gara | Chi nhánh: Gara chính");
        trangThai.setFont(new Font("Arial", Font.PLAIN, 12));
        trangThai.setForeground(Color.DARK_GRAY);

        JLabel banQuyen = new JLabel("NIGHT INVADER • Gara ô tô");
        banQuyen.setFont(new Font("Arial", Font.PLAIN, 12));
        banQuyen.setForeground(Color.GRAY);

        footer.add(trangThai, BorderLayout.WEST);
        footer.add(banQuyen, BorderLayout.EAST);

        cuaSo.add(header, BorderLayout.NORTH);
        cuaSo.add(this, BorderLayout.CENTER);
        cuaSo.add(footer, BorderLayout.SOUTH);

        return cuaSo;
    }

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
                JMenuItem item = new JMenuItem(ten);
                item.addActionListener(e -> JOptionPane.showMessageDialog(this, "Đang mở chức năng: " + ten));
                menu.add(item);
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
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 205, 210)),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));

        btn.addActionListener(e -> JOptionPane.showMessageDialog(this, "Đang mở nhanh chức năng: " + tieuDe + " (" + phimTat + ")"));

        return btn;
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Kích hoạt kết nối trước khi mở giao diện
        try {
            connectDB.ConnectDB.getInstance().connect();
        } catch (Exception e) {
            System.err.println("Lỗi kết nối CSDL: " + e.getMessage());
        }

        SwingUtilities.invokeLater(() -> new LichSuSuaChuaFrame().taoCuaSo().setVisible(true));
    }
}