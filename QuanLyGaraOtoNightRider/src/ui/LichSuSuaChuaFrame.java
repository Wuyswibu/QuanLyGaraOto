package ui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.text.Normalizer;
import java.text.NumberFormat;
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
    private final JButton btnXemPhieu =
            taoNut("Xem phiếu sửa chữa", false);

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

    private final ArrayList<XeMau> danhSachXe = new ArrayList<>();
    private final ArrayList<LanSuaMau> danhSachLichSu = new ArrayList<>();
    private final ArrayList<LanSuaMau> danhSachHienThi = new ArrayList<>();

    private XeMau xeDangXem;

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

        JLabel moTa = new JLabel("Tra cứu các lần sửa chữa và hóa đơn của phương tiện.");
        moTa.setFont(new Font("Arial", Font.PLAIN, 13));
        moTa.setForeground(CHU_PHU);
        phanTren.add(moTa, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(2, 3, 20, 10));
        form.setOpaque(false);

        JPanel timXe = new JPanel(new BorderLayout(6, 0));
        timXe.setOpaque(false);
        timXe.add(txtBienSo, BorderLayout.CENTER);
        timXe.add(btnTimXe, BorderLayout.EAST);

        themO(form, "Biển số", timXe);
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

        DefaultTableCellRenderer renderer =
                new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable bang, Object value, boolean selected,
                    boolean focus, int row, int column) {
                super.getTableCellRendererComponent(
                        bang, value, selected, focus, row, column);

                setBorder(new EmptyBorder(0, 8, 0, 8));
                setHorizontalAlignment(SwingConstants.LEFT);

                if (value instanceof LocalDate) {
                    setText(((LocalDate) value).format(DINH_DANG));
                } else if (column == 2 && value instanceof Integer) {
                    setText(NumberFormat.getIntegerInstance(
                            Locale.forLanguageTag("vi-VN"))
                            .format(value));
                    setHorizontalAlignment(SwingConstants.RIGHT);
                } else if (column == 4) {
                    setText(value == null
                            ? "Chưa lập hóa đơn"
                            : dinhDangTien((BigDecimal) value));
                    setHorizontalAlignment(SwingConstants.RIGHT);
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
        table.setDefaultRenderer(Integer.class, renderer);
        table.setDefaultRenderer(BigDecimal.class, renderer);

        int[] rong = {120, 150, 145, 300, 165, 160};
        for (int i = 0; i < rong.length; i++) {
            table.getColumnModel().getColumn(i)
                    .setPreferredWidth(rong[i]);
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
            if (e.getValueIsAdjusting()) {
                return;
            }
        });

        taoDuLieuMau();
        btnLamMoi.doClick();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object nguon = e.getSource();

        if (nguon == btnLamMoi) {
            table.clearSelection();
            model.setRowCount(0);
            danhSachHienThi.clear();
            xeDangXem = null;

            txtBienSo.setText("");
            txtChuXe.setText("");
            txtMauXe.setText("");
            txtTuNgay.setText("");
            txtDenNgay.setText("");
            txtTimKiem.setText("");
            cboTrangThai.setSelectedIndex(0);

            btnXemPhieu.setEnabled(false);
            lblSoLuong.setText("Số lần sửa: 0");

            txtBienSo.requestFocusInWindow();

        } else if (nguon == btnTimXe || nguon == txtBienSo
                || nguon == btnLoc || nguon == txtTimKiem
                || nguon == txtTuNgay || nguon == txtDenNgay) {

            if (!validData()) {
                return;
            }

            String bienSo = chuanHoaBienSo(txtBienSo.getText());
            XeMau xeTim = null;

            for (XeMau xe : danhSachXe) {
                if (chuanHoaBienSo(xe.bienSo).equals(bienSo)) {
                    xeTim = xe;
                    break;
                }
            }

            table.clearSelection();
            model.setRowCount(0);
            danhSachHienThi.clear();

            btnXemPhieu.setEnabled(false);

            if (xeTim == null) {
                xeDangXem = null;
                txtChuXe.setText("");
                txtMauXe.setText("");
                lblSoLuong.setText("Số lần sửa: 0");

                JOptionPane.showMessageDialog(
                        this, "Không tìm thấy xe có biển số này.");
                return;
            }

            xeDangXem = xeTim;
            txtBienSo.setText(xeTim.bienSo);
            txtChuXe.setText(xeTim.chuXe);
            txtMauXe.setText(xeTim.mauXe);

            LocalDate tuNgay = txtTuNgay.getText().trim().isEmpty()
                    ? null
                    : LocalDate.parse(
                            txtTuNgay.getText().trim(), DINH_DANG);

            LocalDate denNgay = txtDenNgay.getText().trim().isEmpty()
                    ? null
                    : LocalDate.parse(
                            txtDenNgay.getText().trim(), DINH_DANG);

            String tuKhoa = chuanHoa(txtTimKiem.getText().trim());
            String trangThai =
                    (String) cboTrangThai.getSelectedItem();

            for (LanSuaMau lanSua : danhSachLichSu) {
                if (!lanSua.maXe.equals(xeTim.ma)) {
                    continue;
                }

                if (tuNgay != null && lanSua.ngayLap.isBefore(tuNgay)) {
                    continue;
                }

                if (denNgay != null && lanSua.ngayLap.isAfter(denNgay)) {
                    continue;
                }

                if (!"Tất cả".equals(trangThai)
                        && !lanSua.trangThai.equals(trangThai)) {
                    continue;
                }

                String noiDung = chuanHoa(
                        lanSua.maPhieu + " " + lanSua.noiDung);

                if (!noiDung.contains(tuKhoa)) {
                    continue;
                }

                danhSachHienThi.add(lanSua);
            }

            danhSachHienThi.sort((a, b) -> b.ngayLap.compareTo(a.ngayLap));

            for (LanSuaMau lanSua : danhSachHienThi) {
                model.addRow(new Object[]{
                    lanSua.ngayLap,
                    lanSua.maPhieu,
                    lanSua.soKm,
                    lanSua.noiDung,
                    lanSua.tongTien(),
                    lanSua.trangThai
                });
            }

            lblSoLuong.setText("Số lần sửa tìm thấy: " + danhSachHienThi.size());

            if (danhSachHienThi.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Không có lịch sử phù hợp với bộ lọc.");
            }

        } else if (nguon == btnXemPhieu) {
            int dong = table.getSelectedRow();

            if (dong < 0 || xeDangXem == null) {
                JOptionPane.showMessageDialog(
                        this, "Chọn một lần sửa để xem.");
                return;
            }

            LanSuaMau lanSua = danhSachHienThi.get( table.convertRowIndexToModel(dong));

            JTextArea chiTiet = new JTextArea(
                    "Mã phiếu sửa chữa: " + lanSua.maPhieu
                    + "\nMã tiếp nhận: " + lanSua.maTiepNhan
                    + "\nNgày lập: " + lanSua.ngayLap.format(DINH_DANG)
                    + "\nBiển số: " + xeDangXem.bienSo
                    + "\nMẫu xe: " + xeDangXem.mauXe
                    + "\nChủ xe: " + xeDangXem.chuXe
                    + "\nSố km lúc tiếp nhận: " + lanSua.soKm
                    + "\nTrạng thái: " + lanSua.trangThai
                    + "\n\nYêu cầu khách hàng:\n" + lanSua.yeuCau
                    + "\n\nChẩn đoán:\n" + lanSua.chanDoan
                    + "\n\nHạng mục dịch vụ:\n" + lanSua.hangMuc
                    + "\n\nKết quả thực hiện:\n" + lanSua.ketQua
                    + "\n\nGhi chú:\n" + lanSua.ghiChu);

            chiTiet.setEditable(false);
            chiTiet.setLineWrap(true);
            chiTiet.setWrapStyleWord(true);
            chiTiet.setFont(new Font("Arial", Font.PLAIN, 14));
            chiTiet.setCaretPosition(0);

            JScrollPane cuon = new JScrollPane(chiTiet);
            cuon.setPreferredSize(new Dimension(650, 450));

            JOptionPane.showMessageDialog(
                    this, cuon, "Chi tiết phiếu sửa chữa",
                    JOptionPane.INFORMATION_MESSAGE);

        } 
    }

    private boolean validData() {
        if (chuanHoaBienSo(txtBienSo.getText()).isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập biển số.");
            txtBienSo.requestFocusInWindow();
            return false;
        }

        if (txtBienSo.getText().trim().length() > 20) {
            JOptionPane.showMessageDialog( this, "Biển số tối đa 20 ký tự.");
            txtBienSo.requestFocusInWindow();
            return false;
        }

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
                denNgay = LocalDate.parse(
                        txtDenNgay.getText().trim(), DINH_DANG);
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this, "Đến ngày phải đúng dạng dd/MM/yyyy.");
                txtDenNgay.requestFocusInWindow();
                return false;
            }
        }

        if (tuNgay != null && denNgay != null
                && tuNgay.isAfter(denNgay)) {
            JOptionPane.showMessageDialog(this, "Từ ngày không được lớn hơn đến ngày.");
            txtTuNgay.requestFocusInWindow();
            return false;
        }

        return true;
    }

    private void taoDuLieuMau() {
        danhSachXe.add(new XeMau(
                "PT001", "51H-123.45",
                "Toyota Vios", "Nguyễn Minh An"));

        danhSachXe.add(new XeMau(
                "PT002", "51K-678.90",
                "Honda City", "Trần Thị Bình"));

        danhSachXe.add(new XeMau(
                "PT003", "50H-456.78",
                "Mazda 3", "Nguyễn Minh An"));

        danhSachLichSu.add(new LanSuaMau(
                "PSC001", "PTN001", "PT001",
                LocalDate.of(2026, 6, 10), 35100,
                "Thay dầu động cơ và lọc dầu",
                "Bảo dưỡng định kỳ.",
                "Dầu động cơ và lọc dầu cần thay.",
                "- Thay dầu động cơ\n- Thay lọc dầu",
                "Xe vận hành ổn định sau bảo dưỡng.",
                "Khách đã nhận xe.",
                "Đã hoàn thành",
                "HD0001",
                "- Dầu động cơ: 4 lít\n- Lọc dầu: 1 cái",
                new BigDecimal("150000"),
                new BigDecimal("800000"),
                BigDecimal.ZERO,
                new BigDecimal("8"),
                "Đã thanh toán"));

        danhSachLichSu.add(new LanSuaMau(
                "PSC002", "PTN002", "PT001",
                LocalDate.of(2026, 10, 5), 40250,
                "Bảo dưỡng và thay má phanh trước",
                "Kiểm tra tiếng kêu khi phanh, bảo dưỡng.",
                "Má phanh trước mòn, dầu động cơ đến hạn thay.",
                "- Thay dầu động cơ\n- Thay má phanh trước",
                "Đã thay phụ tùng và kiểm tra phanh.",
                "Hướng dẫn khách theo dõi sau sửa chữa.",
                "Đã hoàn thành",
                "HD0002",
                "- Dầu động cơ: 4 lít\n- Lọc dầu: 1 cái"
                        + "\n- Má phanh trước: 1 bộ",
                new BigDecimal("450000"),
                new BigDecimal("1850000"),
                BigDecimal.ZERO,
                new BigDecimal("8"),
                "Đã thanh toán"));

        danhSachLichSu.add(new LanSuaMau(
                "PSC003", "PTN003", "PT001",
                LocalDate.of(2026, 10, 18), 42500,
                "Kiểm tra điều hòa",
                "Điều hòa làm lạnh yếu.",
                "Đang kiểm tra nguyên nhân.",
                "- Kiểm tra hệ thống điều hòa",
                "Chưa hoàn thành.",
                "Chờ kết quả kiểm tra.",
                "Đang sửa chữa",
                null,
                "",
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                ""));

        danhSachLichSu.add(new LanSuaMau(
                "PSC004", "PTN004", "PT002",
                LocalDate.of(2026, 9, 12), 28000,
                "Vệ sinh lọc gió",
                "Kiểm tra và vệ sinh lọc gió.",
                "Lọc gió bám nhiều bụi.",
                "- Vệ sinh lọc gió",
                "Đã vệ sinh và lắp lại.",
                "",
                "Đã hoàn thành",
                "HD0003",
                "Không thay phụ tùng.",
                new BigDecimal("80000"),
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                new BigDecimal("8"),
                "Đã thanh toán"));

        danhSachLichSu.add(new LanSuaMau(
                "PSC005", "PTN005", "PT003",
                LocalDate.of(2026, 10, 19), 53000,
                "Kiểm tra đèn cảnh báo động cơ",
                "Đèn cảnh báo động cơ sáng.",
                "Chưa có chẩn đoán.",
                "- Kiểm tra lỗi động cơ",
                "Chưa thực hiện.",
                "",
                "Chờ sửa chữa",
                null,
                "",
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                ""));
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
        nut.setCursor(
                Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
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

    private static String chuanHoaBienSo(String s) {
        return s.replaceAll("[\\s.\\-]", "")
                .toUpperCase(Locale.ROOT);
    }

    private static String dinhDangTien(BigDecimal tien) {
        NumberFormat format = NumberFormat.getNumberInstance(
                Locale.forLanguageTag("vi-VN"));
        format.setMinimumFractionDigits(0);
        format.setMaximumFractionDigits(2);
        return format.format(tien) + " đ";
    }

    // ==================== DỮ LIỆU TẠM ====================

    private static class XeMau {
        String ma, bienSo, mauXe, chuXe;

        XeMau(String ma, String bienSo,
                String mauXe, String chuXe) {
            this.ma = ma;
            this.bienSo = bienSo;
            this.mauXe = mauXe;
            this.chuXe = chuXe;
        }
    }

    private static class LanSuaMau {
        String maPhieu, maTiepNhan, maXe;
        LocalDate ngayLap;
        int soKm;
        String noiDung, yeuCau, chanDoan;
        String hangMuc, ketQua, ghiChu, trangThai;
        String maHoaDon, phuTung, trangThaiHoaDon;
        BigDecimal tienDichVu, tienPhuTung, giamGia, vat;

        LanSuaMau(String maPhieu, String maTiepNhan, String maXe,
                LocalDate ngayLap, int soKm, String noiDung,
                String yeuCau, String chanDoan, String hangMuc,
                String ketQua, String ghiChu, String trangThai,
                String maHoaDon, String phuTung,
                BigDecimal tienDichVu, BigDecimal tienPhuTung,
                BigDecimal giamGia, BigDecimal vat,
                String trangThaiHoaDon) {
            this.maPhieu = maPhieu;
            this.maTiepNhan = maTiepNhan;
            this.maXe = maXe;
            this.ngayLap = ngayLap;
            this.soKm = soKm;
            this.noiDung = noiDung;
            this.yeuCau = yeuCau;
            this.chanDoan = chanDoan;
            this.hangMuc = hangMuc;
            this.ketQua = ketQua;
            this.ghiChu = ghiChu;
            this.trangThai = trangThai;
            this.maHoaDon = maHoaDon;
            this.phuTung = phuTung;
            this.tienDichVu = tienDichVu;
            this.tienPhuTung = tienPhuTung;
            this.giamGia = giamGia;
            this.vat = vat;
            this.trangThaiHoaDon = trangThaiHoaDon;
        }

        BigDecimal tongTien() {
            if (maHoaDon == null) {
                return null;
            }

            BigDecimal truocThue =
                    tienDichVu.add(tienPhuTung).subtract(giamGia);

            BigDecimal tienVAT = truocThue.multiply(vat)
                    .divide(new BigDecimal("100"), 2,
                            java.math.RoundingMode.HALF_UP);

            return truocThue.add(tienVAT);
        }
    }

    // ==================== MENU, HEADER, FOOTER ====================

    public JFrame taoCuaSo() {
        JFrame cuaSo = new JFrame( "Lịch sử sửa chữa - Night Invader");
        cuaSo.setSize(1340, 840);
        cuaSo.setLocationRelativeTo(null);
        cuaSo.setResizable(true);
        cuaSo.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        cuaSo.setLayout(new BorderLayout());

        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(new Color(245, 247, 250));
        menuBar.setBorder(
                BorderFactory.createEmptyBorder(10, 15, 10, 15));

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
            {
                "Tài khoản cá nhân", "Đổi mật khẩu",
                "-", "Đăng xuất"
            },
            {
                "Quản lý khách hàng"
            },
            {
                "Quản lý xe", "Tra cứu xe", "Lịch sử sửa chữa"
            },
            {
                "Quản lý dịch vụ", "Thêm dịch vụ",
                "Sửa dịch vụ", "Xóa dịch vụ", "-",
                "Lập phiếu tiếp nhận xe",
                "Lập phiếu sửa chữa / Báo giá",
                "Cập nhật tình trạng sửa xe"
            },
            {
                "Quản lý phụ tùng"
            },
            {
                "Lập phiếu nhập kho", "Lập phiếu xuất kho"
            },
            {
                "Lập hóa đơn", "Tra cứu hóa đơn"
            },
            {
                "Thống kê doanh thu", "Thống kê tồn kho",
                "Phụ tùng bán chạy", "Phụ tùng bán chậm"
            },
            {
                "Quản lý nhân sự", "Phân công kỹ thuật viên"
            }
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
        toolbar.setBorder(BorderFactory.createMatteBorder(
                0, 0, 1, 0, new Color(220, 224, 230)));

        toolbar.add(taoNutNhanh("Tiếp nhận xe", "F2",loadIcon("/img/icon_xe.png", 22, 22)));
        toolbar.add(taoNutNhanh( "Tình trạng sửa xe", "F5",loadIcon("/img/icon-vector.png", 22, 22)));
        toolbar.add(taoNutNhanh("Lập hóa đơn", "F3",loadIcon("/img/icon_hoadon.png", 22, 22)));
        toolbar.add(taoNutNhanh( "Nhập kho", "F4",loadIcon("/img/icon_nhapkho.jpg", 22, 22)));

        JPanel thongTin = new JPanel(new GridLayout(2, 1));
        thongTin.setBackground(Color.WHITE);

        JLabel ngay = new JLabel(LocalDate.now().format(DINH_DANG), JLabel.RIGHT);
        ngay.setFont(new Font("Arial", Font.PLAIN, 12));

        JLabel chiNhanh = new JLabel( "Chi nhánh: Gara chính", JLabel.RIGHT);
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

        JLabel trangThai = new JLabel( "Đang hoạt động | Vai trò: Chủ Gara | Chi nhánh: Gara chính");
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
                item.addActionListener(e ->JOptionPane.showMessageDialog(
                                this, "Đang mở chức năng: " + ten));
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
        btn.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(200, 205, 210)),BorderFactory.createEmptyBorder(6, 10, 6, 10)));

        btn.addActionListener(e ->JOptionPane.showMessageDialog(this,"Đang mở nhanh chức năng: "+ tieuDe + " (" + phimTat + ")"));

        return btn;
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() ->new LichSuSuaChuaFrame().taoCuaSo().setVisible(true));
    }
}