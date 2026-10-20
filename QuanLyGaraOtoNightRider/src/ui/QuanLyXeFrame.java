
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

public class QuanLyXeFrame extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;
    private static final Color CHU = new Color(35, 41, 51);
    private static final Color CHU_PHU = new Color(112, 124, 139);
    private static final Color VIEN = new Color(216, 222, 230);
    private static final Color DO = new Color(188, 25, 58);

    private static final String[] TRANG_THAI = {
        "Hoạt động", "Ngừng theo dõi"
    };

    // ==================== DAO VÀ DỮ LIỆU ====================

    private final XeDao xeDao = new XeDao();

    private boolean dangSua;
    private boolean dangTaiDuLieu;

    private ArrayList<Xe> danhSachXe = new ArrayList<>();
    private LinkedHashMap<String, String> khachHang = new LinkedHashMap<>();

    // ==================== CÁC THÀNH PHẦN GIAO DIỆN ====================

    private final JTextField txtMaXe = new JTextField();
    private final JTextField txtBienSo = new JTextField();
    private final JTextField txtMauXe = new JTextField();

    private final JComboBox<String> cboChuXe = new JComboBox<>();
    private final JComboBox<String> cboTrangThaiXe =
            new JComboBox<>(TRANG_THAI);

    private final JTextField txtTimKiem = new JTextField();

    private final JComboBox<String> cboTrangThai =
            new JComboBox<>(new String[]{
                "Tất cả", "Hoạt động", "Ngừng theo dõi"
            });

    private final JLabel lblSoLuong = new JLabel("Số xe: 0");

    private final JButton btnThem = taoNut("Thêm xe", false);
    private final JButton btnSuaXe = taoNut("Sửa xe", false);
    private final JButton btnMoi = taoNut("Làm mới", false);
    private final JButton btnTraCuu = taoNut("Tra cứu xe", true);

    private final DefaultTableModel model =
            new DefaultTableModel(
                new String[]{
                    "Mã phương tiện",
                    "Biển số",
                    "Mẫu xe",
                    "Chủ xe",
                    "Trạng thái"
                }, 0) {

                private static final long serialVersionUID = 1L;

                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

    private final JTable tableXe = new JTable(model);

    private final TableRowSorter<DefaultTableModel> boLoc =
            new TableRowSorter<>(model);

    // ==================== CONSTRUCTOR ====================

    public QuanLyXeFrame() {

        setTitle("Quản lý xe - Night Invader");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        setResizable(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        khoiTaoGiaoDien();
    }

    // ==================== KHỞI TẠO GIAO DIỆN ====================

    private void khoiTaoGiaoDien() {
        setLayout(new BorderLayout());
        // ==================== 1. MENU BAR ====================
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(new Color(245, 247, 250));
        menuBar.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15)
        );
        Font menuFont = new Font("Arial", Font.BOLD, 16);
        
        JMenu menuLogo = new JMenu("NIGHT INVADER");
        menuLogo.setFont(new Font("Arial", Font.BOLD, 15));
        menuLogo.setForeground(new Color(210, 30, 30));
        menuLogo.setMargin(new Insets(5, 10, 5, 10));
        menuBar.add(menuLogo);

        // Danh sách menu 
        String[] tenCacMenu = {
                "Hệ thống", "Khách hàng ", "Xe ", "Dịch vụ ", 
                "Phụ tùng", "Kho hàng ", "Hóa đơn ", "Thống kê ", "Nhân viên "
            };
        
        String[][] cacItemMenu = {
                {"Tài khoản cá nhân", "Đổi mật khẩu", "-", "Đăng xuất"},
                {"Quản lý khách hàng"},
                {"Quản lý xe", "Tra cứu xe", "Lịch sử sửa chữa"},
                {"Quản lý dịch vụ", "Thêm dịch vụ", "Sửa dịch vụ", "Xóa dịch vụ", "-", "Lập phiếu tiếp nhận xe", "Lập phiếu sửa chữa / Báo giá", "Cập nhật tình trạng sửa xe"},
                {"Quản lý phụ tùng"},
                {"Lập phiếu nhập kho", "Lập phiếu xuất kho"},
                {"Lập hóa đơn", "Tra cứu hóa đơn"}, {"Thống kê doanh thu", "Thống kê tồn kho", "Phụ tùng bán chạy", "Phụ tùng	 bán chậm"},
                {"Quản lý nhân sự", "Phân công kỹ thuật viên"}
            };
        
        for (int i = 0; i < tenCacMenu.length; i++) {
            JMenu menu = taoMenuDropdown(tenCacMenu[i], cacItemMenu[i]);
            menu.setFont(menuFont);
            menu.setMargin(new Insets(5, 10, 5, 10));
            
            if (tenCacMenu[i].trim().equals("Xe")) {
                menu.setForeground(new Color(188, 25, 58));
                menu.setBorder(BorderFactory.createMatteBorder(0, 0, 3, 0, new Color(188, 25, 58)));
            }
            menuBar.add(menu);
        }
        // Menu tài khoản bên phải
        JMenu menuTaiKhoan = new JMenu("Xin chào, Chủ Gara");
        menuTaiKhoan.setFont(menuFont);
        menuTaiKhoan.setMargin(new Insets(5, 10, 5, 10));
        ImageIcon iconUserMenu = loadIcon("/img/icon_nguoi.jpg", 20, 20);
        if (iconUserMenu != null) {
            menuTaiKhoan.setIcon(iconUserMenu);
        }
        menuBar.add(Box.createHorizontalGlue()); // Đẩy phần tài khoản sang góc phải nhất
        menuBar.add(menuTaiKhoan);

        setJMenuBar(menuBar);

        // ==================== 2. THANH CÔNG CỤ NHANH ====================
        JPanel panelToolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        panelToolbar.setBackground(Color.WHITE);
        panelToolbar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 224, 230)));

        ImageIcon iconXeTB = loadIcon("/img/icon_xe.png", 22, 22);
        ImageIcon iconHoaDonTB = loadIcon("/img/icon_hoadon.png", 22, 22);
        ImageIcon iconNhapKhoTB = loadIcon("/img/icon_nhapkho.jpg", 22, 22);
        ImageIcon iconVectorTB = loadIcon("/img/icon-vector.png", 22, 22);

        panelToolbar.add(taoNutNhanh("Tiếp nhận xe", "F2", iconXeTB));
        panelToolbar.add(taoNutNhanh("Tình trạng sửa xe", "F5", iconVectorTB));
        panelToolbar.add(taoNutNhanh("Lập hóa đơn", "F3", iconHoaDonTB));
        panelToolbar.add(taoNutNhanh("Nhập kho", "F4", iconNhapKhoTB));

        // Ngày tháng và chi nhánh
        JPanel panelThongTinPhu = new JPanel(new GridLayout(2, 1));
        panelThongTinPhu.setBackground(Color.WHITE);
        JLabel lblNgay = new JLabel("Thứ Ba, 06/10/2026", JLabel.RIGHT);
        lblNgay.setFont(new Font("Arial", Font.PLAIN, 12));
        JLabel lblChiNhanh = new JLabel("Chi nhánh: Gara chính", JLabel.RIGHT);
        lblChiNhanh.setFont(new Font("Arial", Font.PLAIN, 11));
        lblChiNhanh.setForeground(Color.GRAY);
        panelThongTinPhu.add(lblNgay);
        panelThongTinPhu.add(lblChiNhanh);

        JPanel panelTopContainer = new JPanel(new BorderLayout());
        panelTopContainer.add(panelToolbar, BorderLayout.CENTER);
        panelTopContainer.add(panelThongTinPhu, BorderLayout.EAST);
        panelTopContainer.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 15));

        // ==================== 3. NỘI DUNG QUẢN LÝ XE ====================

        JPanel panelCenter = new JPanel(new BorderLayout());
        panelCenter.setBackground(new Color(216, 220, 228));
        panelCenter.setBorder(new EmptyBorder(20, 65, 15, 65)
        );

        JPanel khung = new JPanel(new BorderLayout());
        khung.setBackground(Color.WHITE);
        khung.setBorder(BorderFactory.createLineBorder(VIEN)
        );

        // Tiêu đề
        JPanel tieuDe = new JPanel(new BorderLayout());
        tieuDe.setBackground(new Color(246, 247, 250));
        tieuDe.setBorder(new EmptyBorder(8, 16, 8, 16)
        );

        JLabel lbl = new JLabel("QUẢN LÝ XE");
        lbl.setFont(new Font("Arial", Font.BOLD, 16));
        lbl.setForeground(CHU);

        tieuDe.add(lbl, BorderLayout.WEST);
        khung.add(tieuDe, BorderLayout.NORTH);

        JPanel noiDung = new JPanel(new BorderLayout(0, 25)
        );
        noiDung.setBackground(Color.WHITE);
        noiDung.setBorder(new EmptyBorder(12, 18, 16, 18)
        );

        // ==================== FORM THÔNG TIN XE ====================

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

        // ==================== BẢNG DANH SÁCH XE ====================

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

        // Thiết lập JTable
        tableXe.setFont(new Font("Arial", Font.PLAIN, 13));
        tableXe.setRowHeight(36);
        tableXe.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableXe.setRowSorter(boLoc);
        tableXe.setShowGrid(false);
        tableXe.setIntercellSpacing(new Dimension(0, 0) );
        tableXe.setSelectionBackground(new Color(53, 105, 139) );
        tableXe.setSelectionForeground(Color.WHITE);
        tableXe.getTableHeader().setFont( new Font("Arial", Font.BOLD, 12));
        tableXe.getTableHeader().setBackground(new Color(237, 241, 245));
        tableXe.getTableHeader().setPreferredSize(new Dimension(0, 30));
        tableXe.getTableHeader().setReorderingAllowed(false);
        tableXe.setDefaultRenderer(Object.class,new DefaultTableCellRenderer() {
        	private static final long serialVersionUID = 1L;
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean selected, boolean focus, int row, int column) {
            	super.getTableCellRendererComponent(table, value, selected, focus, row, column);
            	setBorder(new EmptyBorder(0, 8, 0, 8));
                if (!selected) {
                	setBackground(row % 2 == 0 ? Color.WHITE : new Color(247, 248, 250));
                    setForeground(CHU);
                }
                return this;
            }    
        }
        );

        int[] rong = {140, 160, 180, 280, 160};

        for (int i = 0; i < rong.length; i++) {
            tableXe.getColumnModel().getColumn(i).setPreferredWidth(rong[i]);
        }

        JScrollPane cuon = new JScrollPane(tableXe);
        cuon.setBorder(BorderFactory.createEmptyBorder()
        );
        cuon.getViewport().setBackground(Color.WHITE);

        vungBang.add(cuon, BorderLayout.CENTER);

        lblSoLuong.setForeground(CHU_PHU);
        vungBang.add(lblSoLuong, BorderLayout.SOUTH);

        noiDung.add(vungBang, BorderLayout.CENTER);

        // ==================== CÁC NÚT CHỨC NĂNG ====================

        JPanel thaoTac = new JPanel(
            new FlowLayout(FlowLayout.RIGHT, 8, 0)
        );
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
        panelCenter.add(khung, BorderLayout.CENTER);

        // ==================== 4. FOOTER ====================

        JPanel panelFooter = new JPanel(new BorderLayout());
        panelFooter.setBackground(new Color(245, 247, 250));
        panelFooter.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        
        JLabel lblTrangThai = new JLabel("Đang hoạt động | Vai trò: Chủ Gara | Chi nhánh: Gara chính");
        lblTrangThai.setFont(new Font("Arial", Font.PLAIN, 12));
        lblTrangThai.setForeground(Color.DARK_GRAY);
        
        JLabel lblBanQuyen = new JLabel("NIGHT INVADER • Gara ô tô");
        lblBanQuyen.setFont(new Font("Arial", Font.PLAIN, 12));
        lblBanQuyen.setForeground(Color.GRAY);

        panelFooter.add(lblTrangThai, BorderLayout.WEST);
        panelFooter.add(lblBanQuyen, BorderLayout.EAST);


        // ==================== GẮN VÀO FRAME ====================

        add(panelTopContainer, BorderLayout.NORTH);
        add(panelCenter, BorderLayout.CENTER);
        add(panelFooter, BorderLayout.SOUTH);

        // ==================== ĐĂNG KÝ SỰ KIỆN ====================

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

        // Tải dữ liệu ban đầu từ Database
        btnMoi.doClick();
    }

    // ==================== XỬ LÝ SỰ KIỆN ====================

	@Override
	public void actionPerformed(ActionEvent e) {
	    Object nguon = e.getSource();
	
	    // ==================== THÊM XE ====================
	    if (nguon == btnThem) {
	        dangSua = false;
	        if (!validData()) return;
	
	        try {
	            String maXe = xeDao.taoMaXe();
	            String chuXe = (String) cboChuXe.getSelectedItem();
	            String maKH = chuXe.substring(0, chuXe.indexOf(" • "));
	
	            Xe xe = new Xe(maXe, maKH, txtBienSo.getText().trim(),
	                    txtMauXe.getText().trim(), (String) cboTrangThaiXe.getSelectedItem());
	
	            if (xeDao.themXe(xe)) {
	                JOptionPane.showMessageDialog(this, "Thêm xe thành công.");
	                btnMoi.doClick();
	            } else {
	                JOptionPane.showMessageDialog(this, "Thêm xe không thành công.");
	            }
	        } catch (SQLException ex) {
	            JOptionPane.showMessageDialog(this, "Lỗi thêm xe: " + ex.getMessage());
	        }
	
	    // ==================== SỬA XE ====================
	    } else if (nguon == btnSuaXe) {
	        if (tableXe.getSelectedRow() < 0) {
	            JOptionPane.showMessageDialog(this, "Vui lòng chọn xe cần sửa.");
	            return;
	        }
	
	        dangSua = true;
	        if (!validData()) return;
	
	        try {
	            String chuXe = (String) cboChuXe.getSelectedItem();
	            String maKH = chuXe.substring(0, chuXe.indexOf(" • "));
	
	            Xe xe = new Xe(txtMaXe.getText().trim(), maKH,
	                    txtBienSo.getText().trim(), txtMauXe.getText().trim(),
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
	
	    // ==================== LÀM MỚI ====================
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
	
	            lblSoLuong.setText("Hiển thị: " + tableXe.getRowCount() + " / "
	                    + model.getRowCount() + " xe");
	
	        } catch (SQLException ex) {
	            JOptionPane.showMessageDialog(this, "Không tải được dữ liệu: " + ex.getMessage());
	        } finally {
	            dangTaiDuLieu = false;
	        }
	
	    // ==================== TRA CỨU XE ====================
	    } else if (nguon == btnTraCuu || nguon == cboTrangThai || nguon == txtTimKiem) {
	        if (dangTaiDuLieu) return;
	
	        String tuKhoa = chuanHoa(txtTimKiem.getText().trim());
	        String trangThai = (String) cboTrangThai.getSelectedItem();
	
	        boLoc.setRowFilter(new RowFilter<DefaultTableModel, Integer>() {
	            @Override
	            public boolean include(Entry<? extends DefaultTableModel, ? extends Integer> entry) {
	                boolean dungTrangThai = "Tất cả".equals(trangThai)
	                        || entry.getStringValue(4).equals(trangThai);
	
	                String noiDung = "";
	                for (int i = 0; i < 4; i++) {
	                    noiDung += " " + entry.getStringValue(i);
	                }
	
	                return dungTrangThai && chuanHoa(noiDung).contains(tuKhoa);
	            }
	        });
	
	        lblSoLuong.setText("Hiển thị: " + tableXe.getRowCount() + " / "
	                + model.getRowCount() + " xe");
	    }
	}
	
	// ==================== KIỂM TRA DỮ LIỆU ====================
	private boolean validData() {
	    String ma = txtMaXe.getText().trim();
	    String bienSo = txtBienSo.getText().replaceAll("\\s+", "").toUpperCase(Locale.ROOT);
	    String mauXe = txtMauXe.getText().trim();
	
	    if (dangSua && (!ma.matches("PT[0-9]{3,}") || ma.length() > 30)) {
	        JOptionPane.showMessageDialog(this, "Mã xe phải có dạng PT001 và tối đa 30 ký tự.");
	        return false;
	    }
	
	    if (bienSo.isEmpty() || bienSo.length() > 20) {
	        JOptionPane.showMessageDialog(this, "Biển số không được rỗng và tối đa 20 ký tự.");
	        txtBienSo.requestFocusInWindow();
	        return false;
	    }
	
	    if (mauXe.isEmpty() || mauXe.length() > 100) {
	        JOptionPane.showMessageDialog(this, "Mẫu xe không được rỗng và tối đa 100 ký tự.");
	        txtMauXe.requestFocusInWindow();
	        return false;
	    }
	
	    if (cboChuXe.getSelectedIndex() <= 0) {
	        JOptionPane.showMessageDialog(this, "Vui lòng chọn chủ xe.");
	        cboChuXe.requestFocusInWindow();
	        return false;
	    }
	
	    String tt = (String) cboTrangThaiXe.getSelectedItem();
	
	    if (!"Hoạt động".equals(tt) && !"Ngừng theo dõi".equals(tt)) {
	        JOptionPane.showMessageDialog(this, "Trạng thái xe không hợp lệ.");
	        return false;
	    }
	
	    try {
	        if (xeDao.trungBienSo(bienSo, dangSua ? ma : "")) {
	            JOptionPane.showMessageDialog(this, "Biển số đã tồn tại.");
	            txtBienSo.requestFocusInWindow();
	            return false;
	        }
	    } catch (SQLException ex) {
	        JOptionPane.showMessageDialog(this, "Không kiểm tra được biển số: " + ex.getMessage());
	        return false;
	    }
	
	    txtBienSo.setText(bienSo);
	    txtMauXe.setText(mauXe);
	    return true;
	}
	
	// ==================== TẠO Ô NHẬP LIỆU ====================
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
	            BorderFactory.createLineBorder(VIEN), new EmptyBorder(4, 8, 4, 8)));
	
	    nhan.setLabelFor(field);
	    o.add(nhan, BorderLayout.NORTH);
	    o.add(field, BorderLayout.CENTER);
	    form.add(o);
	}
	
	// ==================== TẠO NÚT ====================
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
	
	// ==================== CHUẨN HÓA TỪ KHÓA ====================
	private static String chuanHoa(String s) {
	    return Normalizer.normalize(s, Normalizer.Form.NFD)
	            .replaceAll("\\p{M}", "")
	            .replace('đ', 'd')
	            .replace('Đ', 'D')
	            .toLowerCase(Locale.ROOT);
	}
	
	// ==================== FOCUS TRA CỨU ====================
	public void chuyenDenTraCuu() {
	    txtTimKiem.requestFocusInWindow();
	    txtTimKiem.selectAll();
	}
	
	// ==================== TẢI ICON ====================
	private ImageIcon loadIcon(String path, int width, int height) {
	    try {
	        java.net.URL imgURL = getClass().getResource(path);
	
	        if (imgURL != null) {
	            ImageIcon icon = new ImageIcon(imgURL);
	            Image scaledImg = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
	            return new ImageIcon(scaledImg);
	        }
	    } catch (Exception e) {
	        System.out.println("Không thể tải ảnh từ đường dẫn: " + path);
	    }
	
	    return null;
	}
	
	// ==================== TẠO MENU DROPDOWN ====================
	private JMenu taoMenuDropdown(String tenMenu, String[] cacItem) {
	    JMenu menu = new JMenu(tenMenu);
	
	    for (String itemName : cacItem) {
	        if (itemName.equals("-")) {
	            menu.addSeparator();
	        } else {
	            JMenuItem item = new JMenuItem(itemName);
	            item.addActionListener(e -> JOptionPane.showMessageDialog(
	                    this, "Đang mở chức năng: " + itemName,
	                    "Thông báo hệ thống", JOptionPane.INFORMATION_MESSAGE));
	            menu.add(item);
	        }
	    }
	
	    return menu;
	}
	
	// ==================== TẠO NÚT NHANH TOOLBAR ====================
	private JButton taoNutNhanh(String tieuDe, String phimTat, ImageIcon icon) {
	    JButton btn = new JButton();
	    btn.setLayout(new BorderLayout(10, 0));
	
	    if (icon != null) {
	        JLabel lblIcon = new JLabel(icon);
	        btn.add(lblIcon, BorderLayout.WEST);
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
	            BorderFactory.createEmptyBorder(6, 10, 6, 10)));
	
	    btn.addActionListener(e -> JOptionPane.showMessageDialog(
	            this, "Đang mở nhanh chức năng: " + tieuDe + " (" + phimTat + ")",
	            "Phím tắt kích hoạt", JOptionPane.INFORMATION_MESSAGE));
	
	    return btn;
	}
}
