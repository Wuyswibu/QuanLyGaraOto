package ui;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.text.Normalizer;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import entity.DichVu;
public class QuanLyDichVuFrame extends JPanel
        implements ActionListener {
    private static final long serialVersionUID = 1L;
    private static final Color CHU = new Color(35, 41, 51);
    private static final Color CHU_PHU = new Color(112, 124, 139);
    private static final Color VIEN = new Color(216, 222, 230);
    private static final Color DO = new Color(188, 25, 58);
    private final JTextField txtMaDichVu = new JTextField();
    private final JTextField txtTenDichVu = new JTextField();
    private final JTextField txtDonGia = new JTextField();
    private final JTextArea txtMoTa = new JTextArea(3, 20);
    private final JComboBox<String> cboTrangThai = new JComboBox<>(
            new String[]{"Đang áp dụng", "Ngừng áp dụng"});
    private final JTextField txtTimKiem = new JTextField();
    private final JComboBox<String> cboLocTrangThai = new JComboBox<>(
            new String[]{"Tất cả", "Đang áp dụng", "Ngừng áp dụng"});
    private final JButton btnThem = taoNut("Thêm dịch vụ", false);
    private final JButton btnSua = taoNut("Sửa dịch vụ", false);
    private final JButton btnNgung = taoNut("Ngừng áp dụng", true);
    private final JButton btnLamMoi = taoNut("Làm mới", false);
    private final JButton btnTim = taoNut("Tìm kiếm", false);
    private final JLabel lblSoLuong = new JLabel();
    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{
                "Mã dịch vụ", "Tên dịch vụ", "Mô tả",
                "Đơn giá", "Trạng thái"
            }, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
        @Override
        public Class<?> getColumnClass(int column) {
            return column == 3 ? BigDecimal.class : String.class;
        }
    };
    private final JTable table = new JTable(model);
    private final TableRowSorter<DefaultTableModel> boLoc =
            new TableRowSorter<>(model);
    private final ArrayList<DichVu> danhSach = new ArrayList<>();
    private int soThuTu = 5;
    public QuanLyDichVuFrame() {
        setLayout(new BorderLayout());
        setBackground(new Color(216, 220, 228));
        setBorder(new EmptyBorder(20, 65, 15, 65));
        JPanel khung = new JPanel(new BorderLayout());
        khung.setBackground(Color.WHITE);
        khung.setBorder(BorderFactory.createLineBorder(VIEN));
        JLabel tieuDe = new JLabel("QUẢN LÝ DỊCH VỤ");
        tieuDe.setFont(new Font("Arial", Font.BOLD, 16));
        tieuDe.setForeground(CHU);
        tieuDe.setOpaque(true);
        tieuDe.setBackground(new Color(246, 247, 250));
        tieuDe.setBorder(new EmptyBorder(10, 16, 10, 16));
        khung.add(tieuDe, BorderLayout.NORTH);
        JPanel noiDung = new JPanel(new BorderLayout(0, 20));
        noiDung.setBackground(Color.WHITE);
        noiDung.setBorder(new EmptyBorder(12, 18, 16, 18));
        // ==================== FORM ====================
        JPanel phanTren = new JPanel(new BorderLayout(0, 12));
        phanTren.setOpaque(false);
        JLabel moTa = new JLabel(
                "Quản lý danh mục dịch vụ và đơn giá áp dụng.");
        moTa.setFont(new Font("Arial", Font.PLAIN, 13));
        moTa.setForeground(CHU_PHU);
        phanTren.add(moTa, BorderLayout.NORTH);
        JPanel form = new JPanel(new GridLayout(2, 2, 24, 12));
        form.setOpaque(false);
        txtMaDichVu.setEditable(false);
        themO(form, "Mã dịch vụ", txtMaDichVu);
        themO(form, "Tên dịch vụ", txtTenDichVu);
        themO(form, "Đơn giá dịch vụ (VNĐ)", txtDonGia);
        themO(form, "Trạng thái", cboTrangThai);
        phanTren.add(form, BorderLayout.CENTER);
        txtMoTa.setLineWrap(true);
        txtMoTa.setWrapStyleWord(true);
        txtMoTa.setFont(new Font("Arial", Font.PLAIN, 13));
        txtMoTa.setBorder(new EmptyBorder(6, 8, 6, 8));
        JPanel vungMoTa = new JPanel(new BorderLayout(0, 5));
        vungMoTa.setOpaque(false);
        JLabel lblMoTa = new JLabel("Mô tả");
        lblMoTa.setFont(new Font("Arial", Font.BOLD, 12));
        lblMoTa.setForeground(new Color(87, 100, 117));
        JScrollPane cuonMoTa = new JScrollPane(txtMoTa);
        cuonMoTa.setBorder(BorderFactory.createLineBorder(VIEN));
        vungMoTa.add(lblMoTa, BorderLayout.NORTH);
        vungMoTa.add(cuonMoTa, BorderLayout.CENTER);
        phanTren.add(vungMoTa, BorderLayout.SOUTH);
        noiDung.add(phanTren, BorderLayout.NORTH);
        // ==================== DANH SÁCH ====================
        JPanel phanBang = new JPanel(new BorderLayout(0, 10));
        phanBang.setOpaque(false);
        JPanel thanhTim = new JPanel(new BorderLayout(16, 0));
        thanhTim.setBackground(new Color(237, 241, 245));
        thanhTim.setBorder(new EmptyBorder(6, 10, 6, 10));
        JPanel tim = new JPanel(new BorderLayout(8, 0));
        tim.setOpaque(false);
        tim.add(new JLabel("Tìm theo mã hoặc tên dịch vụ:"),
                BorderLayout.WEST);
        tim.add(txtTimKiem, BorderLayout.CENTER);
        tim.add(btnTim, BorderLayout.EAST);
        JPanel loc = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 5, 0));
        loc.setOpaque(false);
        loc.add(new JLabel("Trạng thái:"));
        loc.add(cboLocTrangThai);
        thanhTim.add(tim, BorderLayout.CENTER);
        thanhTim.add(loc, BorderLayout.EAST);
        phanBang.add(thanhTim, BorderLayout.NORTH);
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.setRowHeight(36);
        table.setRowSorter(boLoc);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setSelectionBackground(new Color(53, 105, 139));
        table.setSelectionForeground(Color.WHITE);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.getTableHeader().setFont(
                new Font("Arial", Font.BOLD, 12));
        table.getTableHeader().setBackground(
                new Color(237, 241, 245));
        table.getTableHeader().setReorderingAllowed(false);
        DefaultTableCellRenderer hienThi =
                new DefaultTableCellRenderer() {
            private final NumberFormat tien =
                    NumberFormat.getNumberInstance(
                            Locale.forLanguageTag("vi-VN"));
            @Override
            public Component getTableCellRendererComponent(
                    JTable bang, Object value, boolean selected,
                    boolean focus, int row, int column) {
                super.getTableCellRendererComponent(
                        bang, value, selected, focus, row, column);
                setBorder(new EmptyBorder(0, 8, 0, 8));
                setHorizontalAlignment(SwingConstants.LEFT);
                if (value instanceof BigDecimal) {
                    tien.setMinimumFractionDigits(0);
                    tien.setMaximumFractionDigits(2);
                    setText(tien.format(value) + " đ");
                    setHorizontalAlignment(SwingConstants.RIGHT);
                }
                if (!selected) {
                    setBackground(row % 2 == 0
                            ? Color.WHITE
                            : new Color(247, 248, 250));
                    setForeground(CHU);
                }
                return this;
            }
        };
        table.setDefaultRenderer(String.class, hienThi);
        table.setDefaultRenderer(BigDecimal.class, hienThi);
        int[] rong = {120, 230, 350, 140, 160};
        for (int i = 0; i < rong.length; i++) {
            table.getColumnModel().getColumn(i)
                    .setPreferredWidth(rong[i]);
        }
        JScrollPane cuonBang = new JScrollPane(table);
        cuonBang.setBorder(BorderFactory.createLineBorder(VIEN));
        cuonBang.getViewport().setBackground(Color.WHITE);
        phanBang.add(cuonBang, BorderLayout.CENTER);
        lblSoLuong.setForeground(CHU_PHU);
        phanBang.add(lblSoLuong, BorderLayout.SOUTH);
        noiDung.add(phanBang, BorderLayout.CENTER);
        // ==================== NÚT ====================
        JPanel thaoTac = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 8, 0));
        thaoTac.setOpaque(false);
        thaoTac.add(btnThem);
        thaoTac.add(btnSua);
        thaoTac.add(btnNgung);
        thaoTac.add(btnLamMoi);
        noiDung.add(thaoTac, BorderLayout.SOUTH);
        khung.add(noiDung, BorderLayout.CENTER);
        add(khung, BorderLayout.CENTER);
        btnThem.addActionListener(this);
        btnSua.addActionListener(this);
        btnNgung.addActionListener(this);
        btnLamMoi.addActionListener(this);
        btnTim.addActionListener(this);
        txtTimKiem.addActionListener(this);
        cboLocTrangThai.addActionListener(this);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) {
                return;
            }
            int dong = table.getSelectedRow();
            btnSua.setEnabled(dong >= 0);
            btnNgung.setEnabled(false);
            if (dong < 0) {
                return;
            }
            DichVu dv = danhSach.get(
                    table.convertRowIndexToModel(dong));
            txtMaDichVu.setText(dv.getMaDichVu());
            txtTenDichVu.setText(dv.getTenDichVu());
            txtMoTa.setText(dv.getMoTa());
            txtDonGia.setText(dv.getDonGia().toPlainString());
            cboTrangThai.setSelectedIndex(
                    dv.isTrangThai() ? 0 : 1);
            btnNgung.setEnabled(dv.isTrangThai());
        });
        danhSach.add(new DichVu(
                "DV001", "Thay dầu động cơ",
                "Thay dầu và kiểm tra mức dầu động cơ.",
                new BigDecimal("150000"), true));
        danhSach.add(new DichVu(
                "DV002", "Thay má phanh trước",
                "Kiểm tra và thay má phanh trước.",
                new BigDecimal("300000"), true));
        danhSach.add(new DichVu(
                "DV003", "Kiểm tra điều hòa",
                "Kiểm tra khả năng làm lạnh và hệ thống điều hòa.",
                new BigDecimal("200000"), true));
        danhSach.add(new DichVu(
                "DV004", "Cân bằng động bánh xe",
                "Kiểm tra và cân bằng động bánh xe.",
                new BigDecimal("250000"), false));
        danhSach.add(new DichVu(
                "DV005", "Vệ sinh lọc gió",
                "Vệ sinh lọc gió động cơ.",
                new BigDecimal("80000"), true));
        btnLamMoi.doClick();
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        Object nguon = e.getSource();
        if (nguon == btnThem) {
            if (!validData()) {
                return;
            }
            try {
                String ma = String.format(
                        Locale.ROOT, "DV%03d", soThuTu + 1);
                DichVu dv = new DichVu(
                        ma,
                        txtTenDichVu.getText().trim(),
                        txtMoTa.getText().trim(),
                        new BigDecimal(txtDonGia.getText().trim()),
                        cboTrangThai.getSelectedIndex() == 0);
                danhSach.add(dv);
                soThuTu++;
                JOptionPane.showMessageDialog(
                        this, "Đã thêm dịch vụ mẫu: " + ma);
                btnLamMoi.doClick();
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(
                        this, ex.getMessage());
            }
        } else if (nguon == btnSua) {
            int dong = table.getSelectedRow();
            if (dong < 0) {
                JOptionPane.showMessageDialog(
                        this, "Chọn dịch vụ cần sửa.");
                return;
            }
            if (!validData()) {
                return;
            }
            try {
                int dongModel = table.convertRowIndexToModel(dong);
                DichVu cu = danhSach.get(dongModel);
                DichVu moi = new DichVu(
                        cu.getMaDichVu(),
                        txtTenDichVu.getText().trim(),
                        txtMoTa.getText().trim(),
                        new BigDecimal(txtDonGia.getText().trim()),
                        cboTrangThai.getSelectedIndex() == 0);
                danhSach.set(dongModel, moi);
                JOptionPane.showMessageDialog(
                        this, "Đã cập nhật dịch vụ mẫu.");
                btnLamMoi.doClick();
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(
                        this, ex.getMessage());
            }
        } else if (nguon == btnNgung) {
            int dong = table.getSelectedRow();
            if (dong < 0) {
                JOptionPane.showMessageDialog(
                        this, "Chọn dịch vụ cần ngừng áp dụng.");
                return;
            }
            int dongModel = table.convertRowIndexToModel(dong);
            DichVu dv = danhSach.get(dongModel);
            int chon = JOptionPane.showConfirmDialog(
                    this,
                    "Ngừng áp dụng dịch vụ: "
                            + dv.getTenDichVu() + "?",
                    "Xác nhận",
                    JOptionPane.YES_NO_OPTION);
            if (chon == JOptionPane.YES_OPTION) {
                dv.setTrangThai(false);
                JOptionPane.showMessageDialog(
                        this, "Đã ngừng áp dụng dịch vụ mẫu.");
                btnLamMoi.doClick();
            }
        } else if (nguon == btnLamMoi) {
            table.clearSelection();
            boLoc.setRowFilter(null);
            model.setRowCount(0);
            for (DichVu dv : danhSach) {
                model.addRow(new Object[]{
                    dv.getMaDichVu(),
                    dv.getTenDichVu(),
                    dv.getMoTa(),
                    dv.getDonGia(),
                    dv.isTrangThai()
                            ? "Đang áp dụng" : "Ngừng áp dụng"
                });
            }
            txtMaDichVu.setText("");
            txtTenDichVu.setText("");
            txtMoTa.setText("");
            txtDonGia.setText("");
            cboTrangThai.setSelectedIndex(0);
            txtTimKiem.setText("");
            cboLocTrangThai.setSelectedIndex(0);
            table.clearSelection();
            btnSua.setEnabled(false);
            btnNgung.setEnabled(false);
            lblSoLuong.setText(
                    "Hiển thị: " + table.getRowCount()
                            + " / " + model.getRowCount()
                            + " dịch vụ");
            txtTenDichVu.requestFocusInWindow();
        } else if (nguon == btnTim
                || nguon == txtTimKiem
                || nguon == cboLocTrangThai) {
            String tuKhoa = chuanHoa(
                    txtTimKiem.getText().trim());
            String trangThai =
                    (String) cboLocTrangThai.getSelectedItem();
            boLoc.setRowFilter(
                    new RowFilter<DefaultTableModel, Integer>() {
                @Override
                public boolean include(
                        Entry<? extends DefaultTableModel,
                                ? extends Integer> entry) {
                    String noiDung = entry.getStringValue(0)
                            + " " + entry.getStringValue(1);
                    boolean dungTrangThai =
                            "Tất cả".equals(trangThai)
                            || entry.getStringValue(4)
                                    .equals(trangThai);
                    return dungTrangThai
                            && chuanHoa(noiDung).contains(tuKhoa);
                }
            });
            lblSoLuong.setText(
                    "Hiển thị: " + table.getRowCount()
                            + " / " + model.getRowCount()
                            + " dịch vụ");
        }
    }
    private boolean validData() {
        String ten = txtTenDichVu.getText().trim();
        String moTa = txtMoTa.getText().trim();
        String gia = txtDonGia.getText().trim();
        if (ten.isEmpty() || ten.length() > 150) {
            JOptionPane.showMessageDialog(
                    this,
                    "Tên dịch vụ không được rỗng,"
                            + " tối đa 150 ký tự.");
            txtTenDichVu.requestFocusInWindow();
            return false;
        }
        if (moTa.length() > 1000) {
            JOptionPane.showMessageDialog(
                    this, "Mô tả tối đa 1000 ký tự.");
            txtMoTa.requestFocusInWindow();
            return false;
        }
        // Nhập giá dạng 150000 hoặc 150000.50.
        if (!gia.matches("[0-9]+(\\.[0-9]{1,2})?")) {
            JOptionPane.showMessageDialog(
                    this,
                    "Đơn giá phải là số không âm,"
                            + " tối đa 2 chữ số thập phân."
                            + "\nVí dụ: 150000 hoặc 150000.50"
                            + "\nKhông nhập dấu phân cách hàng nghìn.");
            txtDonGia.requestFocusInWindow();
            return false;
        }
        try {
            BigDecimal donGia = new BigDecimal(gia);
            if (donGia.compareTo(
                    new BigDecimal("9999999999999999.99")) > 0) {
                JOptionPane.showMessageDialog(
                        this, "Đơn giá vượt giới hạn DECIMAL(18,2).");
                txtDonGia.requestFocusInWindow();
                return false;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                    this, "Đơn giá không hợp lệ.");
            txtDonGia.requestFocusInWindow();
            return false;
        }
        if (cboTrangThai.getSelectedIndex() < 0) {
            JOptionPane.showMessageDialog(
                    this, "Vui lòng chọn trạng thái.");
            return false;
        }
        return true;
    }
    private void themO(
            JPanel form, String ten, JComponent field) {
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
    // Cửa sổ để chạy thử riêng; khi gắn vào màn hình chính,
    // thêm trực tiếp panel, không gọi taoCuaSo().
    public JFrame taoCuaSo() {
        JFrame cuaSo = new JFrame("QuanLyDichVuFrame - NIGHT INVADER");
        cuaSo.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        cuaSo.setContentPane(this);
        cuaSo.setSize(1340, 840);
        cuaSo.setLocationRelativeTo(null);
        cuaSo.setExtendedState(JFrame.MAXIMIZED_BOTH);
        return cuaSo;
    }
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        SwingUtilities.invokeLater(() ->
                new QuanLyDichVuFrame().taoCuaSo().setVisible(true));
    }
}
