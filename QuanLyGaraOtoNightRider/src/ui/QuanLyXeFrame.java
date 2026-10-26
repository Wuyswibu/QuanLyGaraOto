package ui;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Locale;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import dao.XeDao;
import entity.Xe;
public class QuanLyXeFrame extends JPanel implements ActionListener {
    private static final long serialVersionUID = 1L;
    private static final Color CHU = new Color(35, 41, 51);
    private static final Color CHU_PHU = new Color(112, 124, 139);
    private static final Color VIEN = new Color(216, 222, 230);
    private static final Color DO = new Color(188, 25, 58);
    private static final String[] TRANG_THAI = {"Hoạt động", "Ngừng theo dõi"};
    private final XeDao xeDao = new XeDao();
    private boolean dangSua;
    private boolean dangTaiDuLieu;
    private ArrayList<Xe> danhSachXe = new ArrayList<>();
    private LinkedHashMap<String, String> khachHang = new LinkedHashMap<>();
    private final JTextField txtMaXe = new JTextField();
    private final JTextField txtBienSo = new JTextField();
    private final JTextField txtMauXe = new JTextField();
    private final JComboBox<String> cboChuXe = new JComboBox<>();
    private final JComboBox<String> cboTrangThaiXe =new JComboBox<>(TRANG_THAI);
    private final JTextField txtTimKiem = new JTextField();
    private final JComboBox<String> cboTrangThai = new JComboBox<>(new String[]{"Tất cả", "Hoạt động", "Ngừng theo dõi"});
    private final JLabel lblSoLuong = new JLabel("Số xe: 0");
    private final JButton btnThem = taoNut("Thêm xe", false);
    private final JButton btnSuaXe = taoNut("Sửa xe", false);
    private final JButton btnMoi = taoNut("Làm mới", false);
    private final JButton btnTraCuu = taoNut("Tra cứu xe", true);
    private final DefaultTableModel model = new DefaultTableModel(new String[]{"Mã phương tiện", "Biển số", "Mẫu xe","Chủ xe", "Trạng thái"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable tableXe = new JTable(model);
    private final TableRowSorter<DefaultTableModel> boLoc = new TableRowSorter<>(model);
    public QuanLyXeFrame() {
        this(null);
    }
    public QuanLyXeFrame(Runnable veManHinhChinh) {
        setLayout(new BorderLayout());
        setBackground(new Color(216, 220, 228));
        setBorder(new EmptyBorder(20, 65, 15, 65));
        JPanel khung = new JPanel(new BorderLayout());
        khung.setBackground(Color.WHITE);
        khung.setBorder(BorderFactory.createLineBorder(VIEN));
        JPanel tieuDe = new JPanel(new BorderLayout());
        tieuDe.setBackground(new Color(246, 247, 250));
        tieuDe.setBorder(new EmptyBorder(8, 16, 8, 16));
        JLabel lbl = new JLabel("QUẢN LÝ XE");
        lbl.setFont(new Font("Arial", Font.BOLD, 16));
        lbl.setForeground(CHU);
        tieuDe.add(lbl, BorderLayout.WEST);
        if (veManHinhChinh != null) {
            JButton btn = taoNut("Màn hình chính", false);
            btn.addActionListener(e -> veManHinhChinh.run());
            tieuDe.add(btn, BorderLayout.EAST);
        }
        khung.add(tieuDe, BorderLayout.NORTH);
        JPanel noiDung = new JPanel(new BorderLayout(0, 25));
        noiDung.setBackground(Color.WHITE);
        noiDung.setBorder(new EmptyBorder(12, 18, 16, 18));
        JPanel dau = new JPanel(new BorderLayout(0, 12));
        dau.setOpaque(false);
        JLabel moTa = new JLabel("Mỗi biển số gắn với chủ xe và lịch sử sửa chữa.");
        moTa.setForeground(CHU_PHU);
        moTa.setFont(new Font("Arial", Font.PLAIN, 13));
        dau.add(moTa, BorderLayout.NORTH);
        JPanel form = new JPanel(new GridLayout(3, 2, 24, 10));
        form.setOpaque(false);
        txtMaXe.setEditable(false);
        cboChuXe.setEditable(false);
        themO(form, "Mã phương tiện", txtMaXe);
        themO(form, "Chủ xe", cboChuXe);
        themO(form, "Biển số", txtBienSo);
        themO(form, "Mẫu xe", txtMauXe);
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
        JLabel lblTim = new JLabel("Tìm mã xe, biển số, mẫu xe hoặc chủ xe:");
        lblTim.setFont(new Font("Arial", Font.PLAIN, 12));
        lblTim.setForeground(CHU_PHU);
        txtTimKiem.setFont(new Font("Arial", Font.PLAIN, 13));
        tim.add(lblTim, BorderLayout.WEST);
        tim.add(txtTimKiem, BorderLayout.CENTER);
        thanhLoc.add(tim, BorderLayout.CENTER);
        JPanel loc = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        loc.setOpaque(false);
        loc.add(new JLabel("Trạng thái:"));
        cboTrangThai.setBackground(Color.WHITE);
        loc.add(cboTrangThai);
        thanhLoc.add(loc, BorderLayout.EAST);
        vungBang.add(thanhLoc, BorderLayout.NORTH);
        tableXe.setFont(new Font("Arial", Font.PLAIN, 13));
        tableXe.setRowHeight(36);
        tableXe.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableXe.setRowSorter(boLoc);
        tableXe.setShowGrid(false);
        tableXe.setIntercellSpacing(new Dimension(0, 0));
        tableXe.setSelectionBackground(new Color(53, 105, 139));
        tableXe.setSelectionForeground(Color.WHITE);
        tableXe.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tableXe.getTableHeader().setBackground(new Color(237, 241, 245));
        tableXe.getTableHeader().setPreferredSize(new Dimension(0, 30));
        tableXe.getTableHeader().setReorderingAllowed(false);
        tableXe.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
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
            tableXe.getColumnModel().getColumn(i)
                    .setPreferredWidth(rong[i]);
        }
        JScrollPane cuon = new JScrollPane(tableXe);
        cuon.setBorder(BorderFactory.createEmptyBorder());
        cuon.getViewport().setBackground(Color.WHITE);
        vungBang.add(cuon, BorderLayout.CENTER);
        lblSoLuong.setForeground(CHU_PHU);
        vungBang.add(lblSoLuong, BorderLayout.SOUTH);
        noiDung.add(vungBang, BorderLayout.CENTER);
        JPanel thaoTac = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        thaoTac.setOpaque(false);
        btnTraCuu.setContentAreaFilled(false);
        btnTraCuu.setOpaque(true);
        btnTraCuu.setBackground(DO);
        btnTraCuu.setForeground(Color.WHITE);
        thaoTac.add(btnThem);
        thaoTac.add(btnSuaXe);
        thaoTac.add(btnMoi);
        thaoTac.add(btnTraCuu);
        noiDung.add(thaoTac, BorderLayout.SOUTH);
        khung.add(noiDung, BorderLayout.CENTER);
        add(khung, BorderLayout.CENTER);
        btnThem.addActionListener(this);
        btnSuaXe.addActionListener(this);
        btnMoi.addActionListener(this);
        btnTraCuu.addActionListener(this);
        cboTrangThai.addActionListener(this);
        txtTimKiem.addActionListener(this);
        tableXe.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting() || dangTaiDuLieu) {
                return;
            }
            int dong = tableXe.getSelectedRow();
            btnSuaXe.setEnabled(dong >= 0);
            if (dong < 0) {
                return;
            }
            int dongModel = tableXe.convertRowIndexToModel(dong);
            Xe xe = danhSachXe.get(dongModel);
            txtMaXe.setText(xe.getMaPhuongTien());
            txtBienSo.setText(xe.getBienSo());
            txtMauXe.setText(xe.getMauXe());
            cboChuXe.setSelectedItem(xe.getMaKhachHang() + " • " + khachHang.get(xe.getMaKhachHang()));
            cboTrangThaiXe.setSelectedItem(xe.getTrangThai());
        });
        btnMoi.doClick();
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        Object nguon = e.getSource();
        if (nguon == btnThem) {
            dangSua = false;
            if (!validData()) {
                return;
            }
            try {
                String maXe = xeDao.taoMaXe();
                String chuXe = (String) cboChuXe.getSelectedItem();
                String maKH = chuXe.substring(0, chuXe.indexOf(" • "));
                Xe xe = new Xe(
                		maXe, 
                		maKH, 
                		txtBienSo.getText().trim(), 
                		txtMauXe.getText().trim(), 
                		(String) cboTrangThaiXe.getSelectedItem());
                if (xeDao.themXe(xe)) {
                    JOptionPane.showMessageDialog(this, "Thêm xe thành công.");
                    btnMoi.doClick();
                } else {
                    JOptionPane.showMessageDialog(this, "Thêm xe không thành công.");
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Lỗi thêm xe: " + ex.getMessage());
            }
        } else if (nguon == btnSuaXe) {
            if (tableXe.getSelectedRow() < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn xe cần sửa.");
                return;
            }
            dangSua = true;
            if (!validData()) {
                return;
            }
            try {
                String chuXe = (String) cboChuXe.getSelectedItem();
                String maKH = chuXe.substring(0, chuXe.indexOf(" • "));
                Xe xe = new Xe(
                        txtMaXe.getText().trim(),
                        maKH,
                        txtBienSo.getText().trim(),
                        txtMauXe.getText().trim(),
                        (String) cboTrangThaiXe.getSelectedItem());
                if (xeDao.suaXe(xe)) {
                    JOptionPane.showMessageDialog(this, "Cập nhật xe thành công.");
                    btnMoi.doClick();
                } else {
                    JOptionPane.showMessageDialog(this, "Cập nhật xe không thành công.");
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Lỗi cập nhật xe: " + ex.getMessage());
            }
        } else if (nguon == btnMoi) {
            try {
                ArrayList<Xe> dsMoi = xeDao.layDanhSachXe();
                LinkedHashMap<String, String> khMoi = xeDao.layKhachHang();
                dangTaiDuLieu = true;
                tableXe.clearSelection();
                boLoc.setRowFilter(null);
                model.setRowCount(0);
                danhSachXe = dsMoi;
                khachHang = khMoi;
                cboChuXe.removeAllItems();
                cboChuXe.addItem("-- Chọn khách hàng --");
                for (String ma : khachHang.keySet()) {
                    cboChuXe.addItem(ma + " • " + khachHang.get(ma));
                }
                for (Xe xe : danhSachXe) {
                    model.addRow(new Object[]{
                        xe.getMaPhuongTien(),
                        xe.getBienSo(),
                        xe.getMauXe(),
                        xe.getMaKhachHang() + " • " + khachHang.get(xe.getMaKhachHang()),
                        xe.getTrangThai()
                    });
                }
                txtTimKiem.setText("");
                cboTrangThai.setSelectedIndex(0);
                tableXe.clearSelection();
                txtMaXe.setText("");
                txtBienSo.setText("");
                txtMauXe.setText("");
                cboChuXe.setSelectedIndex(0);
                cboTrangThaiXe.setSelectedIndex(0);
                dangSua = false;
                btnSuaXe.setEnabled(false);
                txtBienSo.requestFocusInWindow();
                lblSoLuong.setText("Hiển thị: " + tableXe.getRowCount() + " / " + model.getRowCount() + " xe");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Không tải được dữ liệu: " + ex.getMessage());
            } finally {
                dangTaiDuLieu = false;
            }
        } else if (nguon == btnTraCuu || nguon == cboTrangThai || nguon == txtTimKiem) {
            if (dangTaiDuLieu) {
                return;
            }
            String tuKhoa = chuanHoa(txtTimKiem.getText().trim());
            String trangThai =(String) cboTrangThai.getSelectedItem();
            boLoc.setRowFilter(new RowFilter<DefaultTableModel, Integer>() {
                @Override
                public boolean include(Entry<? extends DefaultTableModel, ? extends Integer> entry) {
                	boolean dungTrangThai = "Tất cả".equals(trangThai) || entry.getStringValue(4).equals(trangThai);
                    String noiDung = "";
                    for (int i = 0; i < 4; i++) {
                        noiDung += " " + entry.getStringValue(i);
                    }
                    return dungTrangThai && chuanHoa(noiDung).contains(tuKhoa);
                }
            });
            lblSoLuong.setText("Hiển thị: " + tableXe.getRowCount()  + " / " + model.getRowCount() + " xe");
        }
    }
    private boolean validData() {
        String ma = txtMaXe.getText().trim();
        String bienSo = txtBienSo.getText()
                .replaceAll("\\s+", "")
                .toUpperCase(Locale.ROOT);
        String mauXe = txtMauXe.getText().trim();
        if (dangSua && (!ma.matches("PT[0-9]{3,}") || ma.length() > 30)) {
            JOptionPane.showMessageDialog(
                    this,
                    "Mã xe phải có dạng PT001 và tối đa 30 ký tự.");
            return false;
        }
        if (bienSo.isEmpty() || bienSo.length() > 20) {
            JOptionPane.showMessageDialog(
                    this,
                    "Biển số không được rỗng và tối đa 20 ký tự.");
            txtBienSo.requestFocusInWindow();
            return false;
        }
        if (mauXe.isEmpty() || mauXe.length() > 100) {
            JOptionPane.showMessageDialog(
                    this,
                    "Mẫu xe không được rỗng và tối đa 100 ký tự.");
            txtMauXe.requestFocusInWindow();
            return false;
        }
        if (cboChuXe.getSelectedIndex() <= 0) {
            JOptionPane.showMessageDialog(
                    this, "Vui lòng chọn chủ xe.");
            cboChuXe.requestFocusInWindow();
            return false;
        }
        String tt = (String) cboTrangThaiXe.getSelectedItem();
        if (!"Hoạt động".equals(tt)
                && !"Ngừng theo dõi".equals(tt)) {
            JOptionPane.showMessageDialog(
                    this, "Trạng thái xe không hợp lệ.");
            return false;
        }
        try {
            if (xeDao.trungBienSo(bienSo, dangSua ? ma : "")) {
                JOptionPane.showMessageDialog(
                        this, "Biển số đã tồn tại.");
                txtBienSo.requestFocusInWindow();
                return false;
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    this, "Không kiểm tra được biển số: "
                            + ex.getMessage());
            return false;
        }
        txtBienSo.setText(bienSo);
        txtMauXe.setText(mauXe);
        return true;
    }
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
        nut.setBackground(chinh ? DO : Color.WHITE);
        nut.setForeground(chinh ? Color.WHITE : CHU);
        nut.setContentAreaFilled(false);
        nut.setOpaque(true);
        nut.setFocusPainted(false);
        nut.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        nut.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(chinh ? DO : VIEN),
                new EmptyBorder(9, 20, 9, 20)));
        return nut;
    }
    private static String chuanHoa(String s) {
        return Normalizer.normalize(s, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replace('đ', 'd')
                .replace('Đ', 'D')
                .toLowerCase(Locale.ROOT);
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
