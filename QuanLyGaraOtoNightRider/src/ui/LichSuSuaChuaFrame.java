package ui;

import java.awt.*;
import java.sql.SQLException;
import java.text.Normalizer;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import dao.PhieuSuaChuaDao;
import entity.PhieuSuaChua;

public class LichSuSuaChuaFrame extends JPanel {

    private static final long serialVersionUID = 1L;

    private static final Color DO = new Color(188, 25, 58);
    private static final Color CHU = new Color(35, 41, 51);
    private static final Color CHU_PHU = new Color(112, 124, 139);
    private static final Color VIEN = new Color(216, 222, 230);
    private static final Color NEN = new Color(239, 243, 246);

    private static final DateTimeFormatter NGAY =
            DateTimeFormatter.ofPattern("dd/MM/uuuu")
                    .withResolverStyle(ResolverStyle.STRICT);

    private static final DateTimeFormatter NGAY_GIO =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final PhieuSuaChuaDao dao = new PhieuSuaChuaDao();

    private final List<PhieuSuaChua> danhSachGoc = new ArrayList<>();
    private final List<PhieuSuaChua> danhSachHienThi = new ArrayList<>();

    /*
     * Mỗi phần tử:
     * [0] Biển số
     * [1] Chủ xe
     * [2] Mẫu xe
     * [3] Ngày giờ hẹn trả
     * [4] Ngày giờ bàn giao thực tế
     */
    private Map<String, String[]> thongTinLichSu = new HashMap<>();

    private final JTextField txtTimKiem = new JTextField();
    private final JTextField txtTuNgay = new JTextField();
    private final JTextField txtDenNgay = new JTextField();

    private final JTextField txtBienSo = new JTextField();
    private final JTextField txtChuXe = new JTextField();
    private final JTextField txtMauXe = new JTextField();

    private final JComboBox<String> cboTrangThai = new JComboBox<>(
            new String[]{
                "Tất cả",
                "Chờ sửa chữa",
                "Đang sửa chữa",
                "Đã hoàn thành",
                "Đã hủy"
            });

    private final JButton btnTimKiem = taoNut("Tìm kiếm", true);
    private final JButton btnLoc = taoNut("Lọc lịch sử", false);
    private final JButton btnLamMoi = taoNut("Làm mới", false);
    private final JButton btnXemPhieu = taoNut(
            "Xem phiếu sửa chữa", true);

    private final JLabel lblSoLuong = new JLabel("Số phiếu: 0");

    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{
                "Ngày lập phiếu",
                "Mã phiếu sửa chữa",
                "Mã tiếp nhận",
                "Biển số",
                "Chủ xe",
                "Loại phiếu",
                "Nội dung sửa chữa",
                "Trạng thái sửa chữa",
                "Ngày giờ hẹn trả",
                "Ngày giờ bàn giao thực tế",
                "Kỹ thuật viên sửa chữa"
            }, 0) {

        private static final long serialVersionUID = 1L;

        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }

        @Override
        public Class<?> getColumnClass(int column) {
            return column == 0 ? LocalDate.class : String.class;
        }
    };

    private final JTable table = new JTable(model);

    public LichSuSuaChuaFrame() {
        setLayout(new BorderLayout());
        setBackground(NEN);
        setBorder(new EmptyBorder(12, 20, 12, 20));

        JPanel khung = new JPanel(new BorderLayout(0, 14));
        khung.setBackground(Color.WHITE);
        khung.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(VIEN),
                new EmptyBorder(16, 18, 16, 18)));

        khung.add(taoPhanTren(), BorderLayout.NORTH);
        khung.add(taoBang(), BorderLayout.CENTER);
        khung.add(taoThanhDuoi(), BorderLayout.SOUTH);

        add(khung, BorderLayout.CENTER);

        ganSuKien();
        taiDuLieu();
    }

    private JPanel taoPhanTren() {
        JPanel panel = new JPanel(new BorderLayout(0, 14));
        panel.setOpaque(false);

        JPanel tieuDePanel = new JPanel(new BorderLayout(0, 5));
        tieuDePanel.setOpaque(false);

        JLabel tieuDe = new JLabel("LỊCH SỬ SỬA CHỮA");
        tieuDe.setFont(new Font("SansSerif", Font.BOLD, 22));
        tieuDe.setForeground(CHU);

        JLabel moTa = new JLabel(
                "Tra cứu phiếu sửa chữa, ngày hẹn trả "
                + "và thời gian bàn giao xe thực tế.");
        moTa.setFont(new Font("SansSerif", Font.PLAIN, 13));
        moTa.setForeground(CHU_PHU);

        tieuDePanel.add(tieuDe, BorderLayout.NORTH);
        tieuDePanel.add(moTa, BorderLayout.SOUTH);

        panel.add(tieuDePanel, BorderLayout.NORTH);

        JPanel cacBoLoc = new JPanel(new BorderLayout(0, 12));
        cacBoLoc.setOpaque(false);

        JPanel timKiemPanel = new JPanel(new BorderLayout(8, 0));
        timKiemPanel.setOpaque(false);

        txtTimKiem.setToolTipText(
                "Nhập biển số, tên khách, mã phiếu sửa chữa "
                + "hoặc mã tiếp nhận");

        dinhDangO(txtTimKiem);

        timKiemPanel.add(txtTimKiem, BorderLayout.CENTER);
        timKiemPanel.add(btnTimKiem, BorderLayout.EAST);

        cacBoLoc.add(
                taoO("Tìm kiếm biển số / Tên khách / Mã phiếu",
                        timKiemPanel),
                BorderLayout.NORTH);

        JPanel locPanel = new JPanel(new GridLayout(1, 4, 12, 0));
        locPanel.setOpaque(false);

        locPanel.add(taoO(
                "Ngày lập từ (dd/MM/yyyy)", txtTuNgay));
        locPanel.add(taoO(
                "Ngày lập đến (dd/MM/yyyy)", txtDenNgay));
        locPanel.add(taoO(
                "Trạng thái sửa chữa", cboTrangThai));

        JPanel nutLocPanel = new JPanel(new BorderLayout());
        nutLocPanel.setOpaque(false);
        nutLocPanel.setBorder(new EmptyBorder(22, 0, 0, 0));
        nutLocPanel.add(btnLoc, BorderLayout.CENTER);
        locPanel.add(nutLocPanel);

        cacBoLoc.add(locPanel, BorderLayout.CENTER);

        JPanel thongTinXe = new JPanel(
                new GridLayout(1, 3, 12, 0));
        thongTinXe.setOpaque(false);

        txtBienSo.setEditable(false);
        txtChuXe.setEditable(false);
        txtMauXe.setEditable(false);

        thongTinXe.add(taoO("Biển số xe đang chọn", txtBienSo));
        thongTinXe.add(taoO("Chủ xe", txtChuXe));
        thongTinXe.add(taoO("Mẫu xe", txtMauXe));

        Color nenThongTin = new Color(245, 247, 250);
        txtBienSo.setBackground(nenThongTin);
        txtChuXe.setBackground(nenThongTin);
        txtMauXe.setBackground(nenThongTin);

        cacBoLoc.add(thongTinXe, BorderLayout.SOUTH);
        panel.add(cacBoLoc, BorderLayout.CENTER);

        return panel;
    }

    private JScrollPane taoBang() {
        table.setFont(new Font("SansSerif", Font.PLAIN, 14));
        table.setRowHeight(38);
        table.setAutoCreateRowSorter(true);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setSelectionBackground(new Color(43, 99, 143));
        table.setSelectionForeground(Color.WHITE);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setFillsViewportHeight(true);

        table.getTableHeader().setFont(
                new Font("SansSerif", Font.BOLD, 13));
        table.getTableHeader().setPreferredSize(
                new Dimension(0, 40));
        table.getTableHeader().setReorderingAllowed(false);

        DefaultTableCellRenderer renderer =
                new DefaultTableCellRenderer() {

            private static final long serialVersionUID = 1L;

            @Override
            public Component getTableCellRendererComponent(
                    JTable bang, Object value, boolean selected,
                    boolean focus, int row, int column) {

                super.getTableCellRendererComponent(
                        bang, value, selected, focus, row, column);

                setBorder(new EmptyBorder(0, 10, 0, 10));
                setHorizontalAlignment(SwingConstants.LEFT);

                if (value instanceof LocalDate) {
                    setText(((LocalDate) value).format(NGAY));
                }

                setToolTipText(
                        value == null ? null : getText());

                if (!selected) {
                    setBackground(row % 2 == 0
                            ? Color.WHITE
                            : new Color(247, 249, 251));
                    setForeground(CHU);
                }

                return this;
            }
        };

        table.setDefaultRenderer(String.class, renderer);
        table.setDefaultRenderer(LocalDate.class, renderer);

        int[] doRong = {
        	    125, 155, 125, 125, 185,
        	    135, 260, 165, 180, 215, 230
        	};

        for (int i = 0; i < doRong.length; i++) {
            table.getColumnModel().getColumn(i)
                    .setPreferredWidth(doRong[i]);
        }

        JScrollPane cuon = new JScrollPane(table);
        cuon.setBorder(BorderFactory.createLineBorder(VIEN));
        cuon.getViewport().setBackground(Color.WHITE);

        return cuon;
    }

    private JPanel taoThanhDuoi() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        lblSoLuong.setFont(
                new Font("SansSerif", Font.PLAIN, 13));
        lblSoLuong.setForeground(CHU_PHU);

        JPanel nutPanel = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 8, 0));
        nutPanel.setOpaque(false);
        nutPanel.add(btnLamMoi);
        nutPanel.add(btnXemPhieu);

        btnXemPhieu.setEnabled(false);

        panel.add(lblSoLuong, BorderLayout.WEST);
        panel.add(nutPanel, BorderLayout.EAST);

        return panel;
    }

    private void ganSuKien() {
        btnTimKiem.addActionListener(e -> timKiem());
        btnLoc.addActionListener(e -> timKiem());

        txtTimKiem.addActionListener(e -> timKiem());
        txtTuNgay.addActionListener(e -> timKiem());
        txtDenNgay.addActionListener(e -> timKiem());

        cboTrangThai.addActionListener(e -> {
            if (kiemTraNgay()) {
                locDuLieu();
            }
        });

        btnLamMoi.addActionListener(e -> {
            txtTimKiem.setText("");
            txtTuNgay.setText("");
            txtDenNgay.setText("");
            cboTrangThai.setSelectedIndex(0);

            taiDuLieu();
            txtTimKiem.requestFocusInWindow();
        });

        btnXemPhieu.addActionListener(e -> xemChiTiet());

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                hienThongTinXe();
            }
        });
    }

    private void timKiem() {
        if (kiemTraNgay()) {
            // Tải lại để thấy ngày bàn giao mới nhất từ SQL.
            taiDuLieu();
        }
    }

    private void taiDuLieu() {
        if (!kiemTraNgay()) {
            return;
        }

        try {
            List<PhieuSuaChua> phieuMoi =
                    dao.layDanhSachPhieuSuaChua();

            Map<String, String[]> thongTinMoi =
                    dao.layThongTinLichSu();

            danhSachGoc.clear();

            if (phieuMoi != null) {
                danhSachGoc.addAll(phieuMoi);
            }

            thongTinLichSu = thongTinMoi == null
                    ? new HashMap<>()
                    : thongTinMoi;

            locDuLieu();

        } catch (SQLException ex) {
            ex.printStackTrace();

            JOptionPane.showMessageDialog(
                    this,
                    "Không tải được lịch sử sửa chữa:\n"
                    + ex.getMessage(),
                    "Lỗi tải dữ liệu",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void locDuLieu() {
        String tuKhoa = chuanHoa(txtTimKiem.getText());

        LocalDate tuNgay = docNgay(txtTuNgay);
        LocalDate denNgay = docNgay(txtDenNgay);

        String trangThai =
                Objects.toString(cboTrangThai.getSelectedItem(), "");

        table.clearSelection();
        model.setRowCount(0);
        danhSachHienThi.clear();

        for (PhieuSuaChua phieu : danhSachGoc) {
            LocalDate ngayLap = phieu.getNgayLap() == null
                    ? null
                    : phieu.getNgayLap().toLocalDate();

            if (tuNgay != null
                    && (ngayLap == null || ngayLap.isBefore(tuNgay))) {
                continue;
            }

            if (denNgay != null
                    && (ngayLap == null || ngayLap.isAfter(denNgay))) {
                continue;
            }

            if (!"Tất cả".equals(trangThai)
                    && !trangThai.equalsIgnoreCase(
                            phieu.getTrangThai())) {
                continue;
            }

            String[] thongTin = thongTinCua(phieu);

            String noiDungTim = chuanHoa(
                    anToan(phieu.getMaPhieuSuaChua()) + " "
                    + anToan(phieu.getMaTiepNhan()) + " "
                    + anToan(thongTin[0]) + " "
                    + anToan(thongTin[1]) + " "
                    + anToan(thongTin[2]) + " "
                    + anToan(phieu.getChanDoan()));

            if (!tuKhoa.isEmpty()
                    && !noiDungTim.contains(tuKhoa)) {
                continue;
            }

            danhSachHienThi.add(phieu);

            model.addRow(new Object[]{
                ngayLap,
                phieu.getMaPhieuSuaChua(),
                phieu.getMaTiepNhan(),
                hienThi(thongTin[0]),
                hienThi(thongTin[1]),
                hienThi(phieu.getLoaiPhieu()),
                hienThi(phieu.getChanDoan()),
                hienThi(phieu.getTrangThai()),
                hienThi(thongTin[3]),
                hienThi(thongTin[4]),
                hienThi(thongTin[5])
            });
        }

        lblSoLuong.setText(
                "Tìm thấy " + danhSachHienThi.size()
                + " phiếu sửa chữa");

        if (table.getRowCount() > 0) {
            table.setRowSelectionInterval(0, 0);
        }

        hienThongTinXe();
    }

    private PhieuSuaChua layPhieuDangChon() {
        int dong = table.getSelectedRow();

        if (dong < 0) {
            return null;
        }

        int dongModel = table.convertRowIndexToModel(dong);

        if (dongModel < 0
                || dongModel >= danhSachHienThi.size()) {
            return null;
        }

        return danhSachHienThi.get(dongModel);
    }

    private void hienThongTinXe() {
        PhieuSuaChua phieu = layPhieuDangChon();
        btnXemPhieu.setEnabled(phieu != null);

        if (phieu == null) {
            txtBienSo.setText("");
            txtChuXe.setText("");
            txtMauXe.setText("");
            return;
        }

        String[] thongTin = thongTinCua(phieu);

        txtBienSo.setText(anToan(thongTin[0]));
        txtChuXe.setText(anToan(thongTin[1]));
        txtMauXe.setText(anToan(thongTin[2]));
    }

    private void xemChiTiet() {
        PhieuSuaChua phieu = layPhieuDangChon();

        if (phieu == null) {
            JOptionPane.showMessageDialog(
                    this, "Vui lòng chọn phiếu sửa chữa.");
            return;
        }

        String[] thongTin = thongTinCua(phieu);

        String ngayLap = phieu.getNgayLap() == null
                ? "Chưa có"
                : phieu.getNgayLap().format(NGAY_GIO);

        JTextArea noiDung = new JTextArea(
                "THÔNG TIN PHIẾU SỬA CHỮA"
                + "\n\nMã phiếu: " + hienThi(phieu.getMaPhieuSuaChua())
                + "\nMã tiếp nhận: " + hienThi(phieu.getMaTiepNhan())
                + "\nNgày lập: " + ngayLap
                + "\nLoại phiếu: " + hienThi(phieu.getLoaiPhieu())
                + "\nKỹ thuật viên sửa chữa: " + hienThi(thongTin[5])
                + "\nTrạng thái sửa chữa: "
                + hienThi(phieu.getTrangThai())
                + "\n\nTHÔNG TIN PHƯƠNG TIỆN"
                + "\n\nBiển số: " + hienThi(thongTin[0])
                + "\nChủ xe: " + hienThi(thongTin[1])
                + "\nMẫu xe: " + hienThi(thongTin[2])
                + "\n\nTHỜI GIAN TRẢ XE"
                + "\n\nNgày giờ hẹn trả: " + hienThi(thongTin[3])
                + "\nNgày giờ bàn giao thực tế: "
                + hienThi(thongTin[4])
                + "\n\nCHẨN ĐOÁN / NỘI DUNG SỬA CHỮA"
                + "\n\n" + hienThi(phieu.getChanDoan())
                + "\n\nGHI CHÚ"
                + "\n\n" + hienThi(phieu.getGhiChu()));

        noiDung.setEditable(false);
        noiDung.setLineWrap(true);
        noiDung.setWrapStyleWord(true);
        noiDung.setFont(new Font("SansSerif", Font.PLAIN, 14));
        noiDung.setMargin(new Insets(14, 14, 14, 14));
        noiDung.setCaretPosition(0);

        JScrollPane cuon = new JScrollPane(noiDung);
        cuon.setPreferredSize(new Dimension(720, 520));

        JOptionPane.showMessageDialog(
                this,
                cuon,
                "Chi tiết lịch sử sửa chữa",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private String[] thongTinCua(PhieuSuaChua phieu) {
        return thongTinLichSu.getOrDefault(
                phieu.getMaPhieuSuaChua(),
                new String[]{
                	    "", "", "", "Chưa hẹn", "Chưa bàn giao", "Chưa ghi nhận"
                	});
    }

    private LocalDate docNgay(JTextField field) {
        String giaTri = field.getText().trim();

        return giaTri.isEmpty()
                ? null
                : LocalDate.parse(giaTri, NGAY);
    }

    private boolean kiemTraNgay() {
        LocalDate tuNgay;
        LocalDate denNgay;

        try {
            tuNgay = docNgay(txtTuNgay);
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(
                    this, "Từ ngày phải đúng dạng dd/MM/yyyy.");
            txtTuNgay.requestFocusInWindow();
            return false;
        }

        try {
            denNgay = docNgay(txtDenNgay);
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(
                    this, "Đến ngày phải đúng dạng dd/MM/yyyy.");
            txtDenNgay.requestFocusInWindow();
            return false;
        }

        if (tuNgay != null && denNgay != null
                && tuNgay.isAfter(denNgay)) {

            JOptionPane.showMessageDialog(
                    this, "Từ ngày không được lớn hơn đến ngày.");
            txtTuNgay.requestFocusInWindow();
            return false;
        }

        return true;
    }

    private JPanel taoO(String ten, JComponent thanhPhan) {
        JPanel panel = new JPanel(new BorderLayout(0, 5));
        panel.setOpaque(false);

        JLabel nhan = new JLabel(ten);
        nhan.setFont(new Font("SansSerif", Font.BOLD, 12));
        nhan.setForeground(CHU_PHU);

        dinhDangO(thanhPhan);

        panel.add(nhan, BorderLayout.NORTH);
        panel.add(thanhPhan, BorderLayout.CENTER);

        return panel;
    }

    private void dinhDangO(JComponent thanhPhan) {
        thanhPhan.setFont(
                new Font("SansSerif", Font.PLAIN, 14));
        thanhPhan.setForeground(CHU);
        thanhPhan.setBackground(Color.WHITE);
        thanhPhan.setPreferredSize(new Dimension(180, 36));

        if (thanhPhan instanceof JTextField) {
            ((JTextField) thanhPhan).setMargin(
                    new Insets(4, 8, 4, 8));
        }
    }

    private static JButton taoNut(String ten, boolean chinh) {
        JButton nut = new JButton(ten);

        // Dùng UI này để màu nền nút hiển thị trên Windows.
        nut.setUI(new javax.swing.plaf.basic.BasicButtonUI());

        nut.setFont(new Font("SansSerif", Font.BOLD, 13));
        nut.setBackground(chinh ? DO : Color.WHITE);
        nut.setForeground(chinh ? Color.WHITE : CHU);
        nut.setOpaque(true);
        nut.setContentAreaFilled(true);
        nut.setFocusPainted(false);
        nut.setCursor(Cursor.getPredefinedCursor(
                Cursor.HAND_CURSOR));

        nut.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                        chinh ? DO : VIEN),
                new EmptyBorder(9, 16, 9, 16)));

        return nut;
    }

    private static String anToan(String giaTri) {
        return Objects.toString(giaTri, "");
    }

    private static String hienThi(String giaTri) {
        return giaTri == null || giaTri.trim().isEmpty()
                ? "—"
                : giaTri;
    }

    private static String chuanHoa(String giaTri) {
        return Normalizer.normalize(
                anToan(giaTri), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replace('đ', 'd')
                .replace('Đ', 'D')
                .toLowerCase(Locale.ROOT)
                .trim()
                .replaceAll("\\s+", " ");
    }

    public JFrame taoCuaSo() {
        JFrame cuaSo = new JFrame(
                "Lịch sử sửa chữa - NIGHT INVADER");

        cuaSo.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        cuaSo.setContentPane(this);
        cuaSo.setSize(1400, 850);
        cuaSo.setLocationRelativeTo(null);
        cuaSo.setExtendedState(JFrame.MAXIMIZED_BOTH);

        return cuaSo;
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());

            connectDB.ConnectDB.getInstance().connect();

            System.out.println("Kết nối database thành công!");

        } catch (Exception ex) {
            ex.printStackTrace();

            JOptionPane.showMessageDialog(
                    null,
                    "Không thể khởi động:\n" + ex.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);

            return;
        }

        SwingUtilities.invokeLater(() ->
                new LichSuSuaChuaFrame()
                        .taoCuaSo()
                        .setVisible(true));
    }
}