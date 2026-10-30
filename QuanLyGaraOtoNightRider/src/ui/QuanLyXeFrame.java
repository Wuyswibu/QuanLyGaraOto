package ui;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import service.XeService;
import entity.Xe;
public class QuanLyXeFrame extends JPanel implements ActionListener {
    private static final long serialVersionUID = 1L;
    private static final Color CHU = new Color(35, 41, 51);
    private static final Color CHU_PHU = new Color(112, 124, 139);
    private static final Color VIEN = new Color(216, 222, 230);
    private static final Color DO = new Color(188, 25, 58);
    private static final String[] TRANG_THAI = {"Hoạt động", "Ngừng theo dõi"};
    private final XeService xeService;
    private String maKhachHangLoc;
    private ArrayList<Xe> dsTatCaXe = new ArrayList<>();
    private final JButton btnTatCaXe = taoNut("Tất cả xe", false);
    
    private boolean dangTaiDuLieu;
    private ArrayList<Xe> danhSachXe = new ArrayList<>();
    private LinkedHashMap<String, String> khachHang = new LinkedHashMap<>();
    private final JTextField txtMaXe = new JTextField();
    private final JTextField txtBienSo = new JTextField();
    private final JComboBox<String> cboMauXe = new JComboBox<>();
    private final JComboBox<String> cboChuXe = new JComboBox<>();
    private final JComboBox<String> cboTrangThaiXe =new JComboBox<>(TRANG_THAI);
    private final JTextField txtTimKiem = new JTextField();
    private final JComboBox<String> cboTrangThai = new JComboBox<>(new String[]{"Tất cả", "Hoạt động", "Ngừng theo dõi"});
    private final JLabel lblSoLuong = new JLabel("Số xe: 0");
    private final JButton btnThem = taoNut("Thêm xe", false);
    private final JButton btnSua = taoNut("Sửa thông tin xe", false);
    private final JButton btnLamMoi = taoNut("Làm mới", false);
    private final JButton btnTimKiem = taoNut("Tìm kiếm", true);
    private final DefaultTableModel mdlXe = new DefaultTableModel(new String[]{"Mã phương tiện", "Biển số", "Mẫu xe","Chủ xe", "Trạng thái"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable tblXe = new JTable(mdlXe);
    private final TableRowSorter<DefaultTableModel> boLoc = new TableRowSorter<>(mdlXe);
    public QuanLyXeFrame() {
        this(null);
    }
    public QuanLyXeFrame(Runnable veManHinhChinh) {
        this(veManHinhChinh, new XeService());
    }
    public QuanLyXeFrame(Runnable veManHinhChinh, XeService xeService) {
        this.xeService = java.util.Objects.requireNonNull(xeService);
        setLayout(new BorderLayout());
        setBackground(new Color(216, 220, 228));
        setBorder(new EmptyBorder(10, 14, 10, 14));
        JPanel khung = new JPanel(new BorderLayout());
        khung.setBackground(Color.WHITE);
        khung.setBorder(BorderFactory.createLineBorder(VIEN));
        JPanel tieuDe = new JPanel(new BorderLayout());
        tieuDe.setBackground(new Color(246, 247, 250));
        tieuDe.setBorder(new EmptyBorder(8, 16, 8, 16));
        JLabel lbl = new JLabel("QUẢN LÝ XE");
        lbl.setFont(new Font("Arial", Font.BOLD, 18));
        lbl.setForeground(CHU);
        tieuDe.add(lbl, BorderLayout.WEST);
        if (veManHinhChinh != null) {
            JButton btn = taoNut("Màn hình chính", false);
            btn.addActionListener(e -> veManHinhChinh.run());
            tieuDe.add(btn, BorderLayout.EAST);
        }
        khung.add(tieuDe, BorderLayout.NORTH);
        JPanel noiDung = new JPanel(new BorderLayout(0, 10));
        noiDung.setBackground(Color.WHITE);
        noiDung.setBorder(new EmptyBorder(12, 14, 12, 14));
        JPanel dau = new JPanel(new BorderLayout(0, 12));
        dau.setOpaque(false);
        JLabel moTa = new JLabel("Mỗi biển số gắn với chủ xe và lịch sử sửa chữa.");
        moTa.setForeground(CHU_PHU);
        moTa.setFont(new Font("Arial", Font.PLAIN, 13));
        dau.add(moTa, BorderLayout.NORTH);
        JPanel form = new JPanel(new GridLayout(2, 3, 12, 8));
        form.setOpaque(false);
        txtMaXe.setEditable(false);
        txtMaXe.setFocusable(false);
        txtMaXe.setToolTipText("Mã tự động sinh khi lưu xe mới.");
        cboMauXe.setEditable(true);
        cboChuXe.setEditable(false);
        themO(form, "Mã phương tiện", txtMaXe);
        themO(form, "Chủ xe", cboChuXe);
        themO(form, "Biển số", txtBienSo);
        themO(form, "Mẫu xe", cboMauXe);
        themO(form, "Trạng thái", cboTrangThaiXe);
        JPanel oTrong = new JPanel();
        oTrong.setOpaque(false);
        form.add(oTrong);
        dau.add(form, BorderLayout.CENTER);
        noiDung.add(dau, BorderLayout.NORTH);
        JPanel vungBang = new JPanel(new BorderLayout(0, 12));
        vungBang.setOpaque(false);
        JPanel thanhLoc = new JPanel(new BorderLayout(24, 0));
        thanhLoc.setBackground(new Color(237, 241, 245));
        thanhLoc.setBorder(new EmptyBorder(6, 10, 6, 10));
        JPanel tim = new JPanel(new BorderLayout(10, 0));
        tim.setOpaque(false);
        JLabel lblTim = new JLabel("Mã phương tiện / Mã khách hàng:");
        lblTim.setFont(new Font("Arial", Font.PLAIN, 12));
        lblTim.setForeground(CHU_PHU);
        txtTimKiem.setFont(new Font("Arial", Font.PLAIN, 13));
        tim.add(lblTim, BorderLayout.WEST);
        tim.add(txtTimKiem, BorderLayout.CENTER);
        tim.add(btnTimKiem, BorderLayout.EAST);
        txtTimKiem.setToolTipText("Nhập đầy đủ mã phương tiện hoặc mã khách hàng; để trống để xem tất cả trong phạm vi hiện tại.");
        thanhLoc.add(tim, BorderLayout.CENTER);
        JPanel loc = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        loc.setOpaque(false);
        loc.add(new JLabel("Trạng thái:"));
        cboTrangThai.setBackground(Color.WHITE);
        loc.add(cboTrangThai);
        thanhLoc.add(loc, BorderLayout.EAST);
        vungBang.add(thanhLoc, BorderLayout.NORTH);
        tblXe.setFont(new Font("Arial", Font.PLAIN, 13));
        tblXe.setRowHeight(28);
        tblXe.setFillsViewportHeight(true);
        tblXe.setPreferredScrollableViewportSize(new Dimension(1100, 8 * 28));
        tblXe.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblXe.setRowSorter(boLoc);
        tblXe.setShowGrid(false);
        tblXe.setIntercellSpacing(new Dimension(0, 0));
        tblXe.setSelectionBackground(new Color(53, 105, 139));
        tblXe.setSelectionForeground(Color.WHITE);
        tblXe.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        tblXe.getTableHeader().setBackground(new Color(237, 241, 245));
        tblXe.getTableHeader().setPreferredSize(new Dimension(0, 28));
        tblXe.getTableHeader().setReorderingAllowed(false);
        tblXe.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean selected, boolean focus, int row, int column) {
                super.getTableCellRendererComponent(
                        table, value, selected, focus, row, column);
                setBorder(new EmptyBorder(0, 8, 0, 8));
                if (!selected) {
                    setBackground(row % 2 == 0
                            ? Color.WHITE
                            : new Color(247, 248, 250));
                    setForeground(CHU);
                }
                return this;
            }
        });
        int[] rong = {140, 160, 180, 280, 160};
        for (int i = 0; i < rong.length; i++) {
            tblXe.getColumnModel().getColumn(i)
                    .setPreferredWidth(rong[i]);
        }
        JScrollPane cuon = new JScrollPane(tblXe);
        cuon.setBorder(BorderFactory.createLineBorder(VIEN));
        cuon.getViewport().setBackground(Color.WHITE);
        vungBang.add(cuon, BorderLayout.CENTER);
        lblSoLuong.setForeground(CHU_PHU);
        vungBang.add(lblSoLuong, BorderLayout.SOUTH);
        noiDung.add(vungBang, BorderLayout.CENTER);
        JPanel thaoTac = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        thaoTac.setOpaque(false);
        btnTimKiem.setContentAreaFilled(false);
        btnTimKiem.setOpaque(true);
        btnTimKiem.setBackground(DO);
        btnTimKiem.setForeground(Color.WHITE);
        thaoTac.add(btnTatCaXe);
        btnTatCaXe.addActionListener(e -> hienThiTatCaXe());
        thaoTac.add(btnThem);
        btnSua.setPreferredSize(new Dimension(175, 30));
        thaoTac.add(btnSua);
        thaoTac.add(btnLamMoi);
        noiDung.add(thaoTac, BorderLayout.SOUTH);
        khung.add(noiDung, BorderLayout.CENTER);
        add(khung, BorderLayout.CENTER);
        btnThem.addActionListener(this);
        btnSua.addActionListener(this);
        btnLamMoi.addActionListener(this);
        btnTimKiem.addActionListener(this);
        cboTrangThai.addActionListener(this);
        txtTimKiem.addActionListener(this);
        tblXe.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting() || dangTaiDuLieu) {
                return;
            }
            int dong = tblXe.getSelectedRow();
            btnSua.setEnabled(dong >= 0);
            if (dong < 0) {
                return;
            }
            int dongModel = tblXe.convertRowIndexToModel(dong);
            Xe xe = danhSachXe.get(dongModel);
            txtMaXe.setText(xe.getMaPhuongTien());
            txtBienSo.setText(xe.getBienSo());
            cboMauXe.setSelectedItem(xe.getMauXe());
            cboChuXe.setSelectedItem(xe.getMaKhachHang() + " • " + khachHang.get(xe.getMaKhachHang()));
            cboTrangThaiXe.setSelectedItem(xe.getTrangThai());
        });
        btnLamMoi.doClick();
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if (dangTaiDuLieu) return;
        Object nguon = e.getSource();
        if (nguon == btnThem || nguon == btnSua) {
            boolean sua = nguon == btnSua;
            if (sua && tblXe.getSelectedRow() < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn xe cần sửa.");
                return;
            }
            String chu = (String) cboChuXe.getSelectedItem();
            String maKH = cboChuXe.getSelectedIndex() <= 0 ? ""
                    : chu.substring(0, chu.indexOf(" • "));
            String ma = txtMaXe.getText();
            String bienSo = txtBienSo.getText();
            Object mauNhap = cboMauXe.getEditor().getItem();
            String mau = mauNhap == null ? "" : mauNhap.toString();
            String tt = (String) cboTrangThaiXe.getSelectedItem();
            chayNen(() -> {
                if (sua) xeService.suaXe(ma, maKH, bienSo, mau, tt);
                else xeService.themXe(maKH, bienSo, mau, tt);
                return null;
            }, () -> {
                JOptionPane.showMessageDialog(this, sua ? "Cập nhật xe thành công." : "Thêm xe thành công.");
                taiDuLieu();
            });
        } else if (nguon == btnLamMoi) {
            taiDuLieu();
        } else {
            locDanhSach();
        }
    }

    private interface TacVu { Object thucHien() throws Exception; }

    private void chayNen(TacVu tacVu, Runnable thanhCong) {
        datTrangThaiBan(true);
        new SwingWorker<Object, Void>() {
            @Override protected Object doInBackground() throws Exception {
                return tacVu.thucHien();
            }
            @Override protected void done() {
                datTrangThaiBan(false);
                try {
                    Object ketQua = get();
                    if (ketQua instanceof XeService.DuLieuXe) {
                        XeService.DuLieuXe duLieu = (XeService.DuLieuXe) ketQua;
                        dsTatCaXe = duLieu.getDanhSachXe();
                        khachHang = duLieu.getKhachHang();
                        cboMauXe.removeAllItems();
                        for (String mau : xeService.layMauXeDaCo(dsTatCaXe)) cboMauXe.addItem(mau);
                        cboChuXe.removeAllItems();
                        cboChuXe.addItem("-- Chọn khách hàng --");
                        khachHang.forEach((ma, ten) -> cboChuXe.addItem(ma + " • " + ten));
                        xoaForm();
                        locDanhSach();
                    }
                    if (thanhCong != null) thanhCong.run();
                } catch (Exception ex) {
                    Throwable loi = ex instanceof java.util.concurrent.ExecutionException ? ex.getCause() : ex;
                    JOptionPane.showMessageDialog(QuanLyXeFrame.this, loi.getMessage(),
                            "Không thực hiện được", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void datTrangThaiBan(boolean ban) {
        dangTaiDuLieu = ban;
        btnThem.setEnabled(!ban);
        btnSua.setEnabled(!ban && tblXe.getSelectedRow() >= 0);
        btnLamMoi.setEnabled(!ban);
        btnTimKiem.setEnabled(!ban);
        txtBienSo.setEnabled(!ban);
        cboMauXe.setEnabled(!ban);
        cboChuXe.setEnabled(!ban);
        cboTrangThaiXe.setEnabled(!ban);
    }

    private void taiDuLieu() {
        if (!dangTaiDuLieu) chayNen(() -> xeService.taiDuLieu(), null);
    }

    private void xoaForm() {
        tblXe.clearSelection();
        txtMaXe.setText("");
        txtBienSo.setText("");
        cboMauXe.setSelectedItem("");
        cboChuXe.setSelectedIndex(0);
        if (maKhachHangLoc != null && khachHang.containsKey(maKhachHangLoc)) {
            cboChuXe.setSelectedItem(maKhachHangLoc + " • " + khachHang.get(maKhachHangLoc));
        }
        cboTrangThaiXe.setSelectedIndex(0);
        btnSua.setEnabled(false);
    }

    private void locDanhSach() {
        if (dangTaiDuLieu) return;
        dangTaiDuLieu = true;
        try {
            tblXe.clearSelection();
            boLoc.setRowFilter(null);
            danhSachXe = xeService.locXe(dsTatCaXe, khachHang, maKhachHangLoc,
                    txtTimKiem.getText(), (String) cboTrangThai.getSelectedItem());
            mdlXe.setRowCount(0);
            for (Xe xe : danhSachXe) {
                mdlXe.addRow(new Object[]{xe.getMaPhuongTien(), xe.getBienSo(), xe.getMauXe(),
                        xe.getMaKhachHang() + " • " + khachHang.getOrDefault(xe.getMaKhachHang(), ""),
                        xe.getTrangThai()});
            }
            lblSoLuong.setText((maKhachHangLoc == null ? "Tất cả khách hàng" : "Khách hàng: " + maKhachHangLoc)
                    + " — Số xe: " + danhSachXe.size());
            xoaForm();
        } finally {
            dangTaiDuLieu = false;
        }
    }

    public void hienThiXeCuaKhachHang(String maKhachHang) {
        if (maKhachHang == null || maKhachHang.trim().isEmpty())
            throw new IllegalArgumentException("Vui lòng chọn khách hàng.");
        maKhachHangLoc = maKhachHang.trim();
        txtTimKiem.setText("");
        cboTrangThai.setSelectedIndex(0);
        locDanhSach();
        taiDuLieu();
    }

    public void hienThiTatCaXe() {
        maKhachHangLoc = null;
        txtTimKiem.setText("");
        cboTrangThai.setSelectedIndex(0);
        locDanhSach();
        taiDuLieu();
    }

    private void themO(JPanel form, String ten, JComponent field) {
        JPanel o = new JPanel(new BorderLayout(0, 4));
        o.setOpaque(false);
        JLabel nhan = new JLabel(ten);
        nhan.setFont(new Font("Arial", Font.PLAIN, 13));
        nhan.setForeground(new Color(87, 100, 117));
        field.setFont(new Font("Arial", Font.PLAIN, 13));
        field.setBackground(Color.WHITE);
        field.setForeground(CHU);
        field.setPreferredSize(new Dimension(200, 30));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(VIEN),
                new EmptyBorder(4, 8, 4, 8)));
        nhan.setLabelFor(field);
        o.add(nhan, BorderLayout.NORTH);
        o.add(field, BorderLayout.CENTER);
        form.add(o);
    }
    private static JButton taoNut(String ten, boolean chinh) {
        JButton nut = new JButton(ten);
        nut.setFont(new Font("Arial", Font.BOLD, 12));
        nut.setPreferredSize(new Dimension(140, 30));
        nut.setBackground(chinh ? DO : Color.WHITE);
        nut.setForeground(chinh ? Color.WHITE : CHU);
        nut.setContentAreaFilled(false);
        nut.setOpaque(true);
        nut.setFocusPainted(false);
        nut.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        nut.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(chinh ? DO : VIEN),
                new EmptyBorder(5, 14, 5, 14)));
        return nut;
    }
    public void chuyenDenTraCuu() {
        txtTimKiem.requestFocusInWindow();
        txtTimKiem.selectAll();
    }
    // Cửa sổ để chạy thử riêng; khi gắn vào màn hình chính,
    // thêm trực tiếp panel, không gọi taoCuaSo().
    public JFrame taoCuaSo() {
        JFrame cuaSo = new JFrame("QuanLyXeFrame - NIGHT INVADER");
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
        try {
            connectDB.ConnectDB.getInstance().connect();
            System.out.println("Kết nối database thành công!");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Không thể kết nối database: " + ex.getMessage());
            return;
        }
        SwingUtilities.invokeLater(() ->
                new QuanLyXeFrame().taoCuaSo().setVisible(true));
    }
}
