package ui;
import java.awt.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import entity.DichVu;
import java.sql.SQLException;
import service.DichVuService;
import service.DichVuService.DuLieuKhongHopLeException;

public class QuanLyDichVuFrame extends JPanel {
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
    private final DichVuService dichVuService = new DichVuService();
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
        txtMaDichVu.setBackground(new Color(240, 240, 240));
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
        thaoTac.add(btnLamMoi);
        noiDung.add(thaoTac, BorderLayout.SOUTH);
        khung.add(noiDung, BorderLayout.CENTER);
        add(khung, BorderLayout.CENTER);
        dangKySuKien();
        
        table.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) {
                return;
            }
            int dong = table.getSelectedRow();
            btnSua.setEnabled(dong >= 0);
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
        });
        btnLamMoi.doClick();
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
    
// ĐĂNG KÝ SỰ KIỆN

    private void dangKySuKien() {
        btnThem.addActionListener(e -> themDichVu());
        btnSua.addActionListener(e -> suaDichVu());
        btnTim.addActionListener(e -> timKiemDichVu());
        txtTimKiem.addActionListener(e -> timKiemDichVu());
        cboLocTrangThai.addActionListener(e -> timKiemDichVu());
        
        btnLamMoi.addActionListener(e -> {
            txtTimKiem.setText("");

            if (cboLocTrangThai.getSelectedIndex() != 0) {
                cboLocTrangThai.setSelectedIndex(0);
            } else {
                timKiemDichVu();
            }
        });
  
    }
   

// XÓA NỘI DUNG FORM 

    private void xoaForm() {
        table.clearSelection();

        txtMaDichVu.setText("");
        txtTenDichVu.setText("");
        txtDonGia.setText("");
        txtMoTa.setText("");
        cboTrangThai.setSelectedIndex(0);
        btnSua.setEnabled(false);

        txtTenDichVu.requestFocusInWindow();
    }

// THAO TÁC GIAO DIỆN

    private void themDichVu() {
        if (!txtMaDichVu.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(
                    this, "Vui lòng làm mới form trước khi thêm dịch vụ mới.");
            return;
        }

        try {
            boolean thanhCong = dichVuService.themDichVu(
                    txtTenDichVu.getText(),
                    txtMoTa.getText(),
                    txtDonGia.getText(),
                    cboTrangThai.getSelectedIndex() == 0);

            JOptionPane.showMessageDialog(
                    this,
                    thanhCong ? "Thêm dịch vụ thành công." : "Không thêm được dịch vụ.");
            
            if (thanhCong) {
                btnLamMoi.doClick();
            }

        } catch (DuLieuKhongHopLeException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    String.join("\n", ex.getLoiTheoTruong().values()));

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                    this,
                    "Không lưu được dữ liệu:\n" + ex.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void suaDichVu() {
        int dong = table.getSelectedRow();

        if (dong < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn dịch vụ cần sửa.");
            return;
        }

        int dongModel = table.convertRowIndexToModel(dong);
        DichVu dichVu = danhSach.get(dongModel);

        try {
            boolean thanhCong = dichVuService.suaDichVu(
                    dichVu.getMaDichVu(),
                    txtTenDichVu.getText(),
                    txtMoTa.getText(),
                    txtDonGia.getText(),
                    cboTrangThai.getSelectedIndex() == 0);

            JOptionPane.showMessageDialog(
                    this,
                    thanhCong ? "Sửa dịch vụ thành công." : "Không cập nhật được dịch vụ.");

            if (thanhCong) {
                btnLamMoi.doClick();
            }

        } catch (DuLieuKhongHopLeException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    String.join("\n", ex.getLoiTheoTruong().values()));

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                    this,
                    "Không cập nhật được dữ liệu:\n" + ex.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void timKiemDichVu() {
        Boolean trangThai = null;

        if (cboLocTrangThai.getSelectedIndex() == 1) {
            trangThai = true;
        } else if (cboLocTrangThai.getSelectedIndex() == 2) {
            trangThai = false;
        }

        try {
            ArrayList<DichVu> ketQua = dichVuService.timKiem(
                    txtTimKiem.getText(), trangThai);

            table.clearSelection();
            boLoc.setRowFilter(null);
            model.setRowCount(0);

            danhSach.clear();
            danhSach.addAll(ketQua);

            for (DichVu dichVu : danhSach) {
                model.addRow(new Object[]{
                    dichVu.getMaDichVu(),
                    dichVu.getTenDichVu(),
                    dichVu.getMoTa(),
                    dichVu.getDonGia(),
                    dichVu.isTrangThai() ? "Đang áp dụng" : "Ngừng áp dụng"
                });
            }

            xoaForm();
            lblSoLuong.setText("Số dịch vụ tìm thấy: " + danhSach.size());

        } catch (SQLException | IllegalArgumentException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                    this,
                    "Không tìm kiếm được dịch vụ:\n" + ex.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
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
