package ui;

import connectDB.ConnectDB;
import service.NhanVienService;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class GaraNhanVien extends JPanel {
    private static final long serialVersionUID = 1L;
    private JTabbedPane tabbedPane;
    
    // Các Component cho Tab Quản lý nhân sự
    private JTextField txtMaNV, txtHoTen, txtSoDienThoai, txtNgayVaoLam, txtTimKiem;
    private JComboBox<String> cbVaiTro, cbTrangThai, cbLocVaiTro;
    private JTable tableNhanVien;

    // Các JLabel hiển thị cảnh báo đỏ tại các vị trí đánh dấu
    private JLabel lblLoiHoTen, lblLoiSDT, lblLoiNgayVaoLam;
    
    private NhanVienService nhanVienService;
    private DefaultTableModel tableModelNhanVien;

    public GaraNhanVien() {
        this(0);
    }

    public GaraNhanVien(int tabMacDinh) {
        nhanVienService = new NhanVienService();
        khoiTaoGiaoDien();
        
        if (tabMacDinh >= 0 && tabbedPane != null && tabMacDinh < tabbedPane.getTabCount()) {
            tabbedPane.setSelectedIndex(tabMacDinh);
        }
        
        taiLaiDuLieuBang();
    }

    private void taiLaiDuLieuBang() {
        if (tableModelNhanVien == null) return;
        tableModelNhanVien.setRowCount(0);
        try {
            String tuKhoa = txtTimKiem != null ? txtTimKiem.getText() : "";
            String vaiTro = cbLocVaiTro != null ? cbLocVaiTro.getSelectedItem().toString() : "Tất cả vai trò";
            
            List<Object[]> listNV = nhanVienService.locVaTimKiemNhanVien(tuKhoa, vaiTro);
            for (Object[] row : listNV) {
                tableModelNhanVien.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu bảng: " + e.getMessage());
        }
    }

    private void khoiTaoGiaoDien() {
        setLayout(new BorderLayout());
        JPanel panelCenter = new JPanel(new BorderLayout());
        panelCenter.setBackground(new Color(240, 243, 246));
        panelCenter.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JPanel panelCardChinh = new JPanel(new BorderLayout());
        panelCardChinh.setBackground(Color.WHITE);
        panelCardChinh.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 215, 220), 1),
                BorderFactory.createEmptyBorder(20, 25, 20, 25)
        ));

        JPanel panelCardTop = new JPanel(new BorderLayout());
        panelCardTop.setBackground(Color.WHITE);
        panelCardTop.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        JLabel lblTieuDeCard = new JLabel("QUẢN LÝ NHÂN SỰ");
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

        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 14));
        tabbedPane.setBackground(new Color(245, 247, 250));
        tabbedPane.addTab("Hồ Sơ Nhân Viên", taoTabQuanLyNhanSu());
       
        panelCardChinh.add(panelCardTop, BorderLayout.NORTH);
        panelCardChinh.add(tabbedPane, BorderLayout.CENTER);
        panelCenter.add(panelCardChinh, BorderLayout.CENTER);
        add(panelCenter, BorderLayout.CENTER);
    }

    // ==================== TAB 1: QUẢN LÝ NHÂN SỰ ====================
    private JPanel taoTabQuanLyNhanSu() {
        JPanel panelTab = new JPanel(new BorderLayout());
        panelTab.setBackground(Color.WHITE);
        panelTab.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        // Khởi tạo các ô text & Label cảnh báo
        txtMaNV = new JTextField(); txtMaNV.setEditable(false); txtMaNV.setBackground(new Color(245, 247, 250));
        txtHoTen = new JTextField();
        txtSoDienThoai = new JTextField();
        txtNgayVaoLam = new JTextField();

        lblLoiHoTen = taoLabelCanhBao();
        lblLoiSDT = taoLabelCanhBao();
        lblLoiNgayVaoLam = taoLabelCanhBao();

        cbVaiTro = new JComboBox<>(new String[]{"Nhân viên tiếp nhận", "Kỹ thuật viên", "Nhân viên Kho", "Thu ngân", "Quản lý"});
        cbVaiTro.setFont(new Font("Arial", Font.PLAIN, 13)); cbVaiTro.setBackground(Color.WHITE);
        cbTrangThai = new JComboBox<>(new String[]{"Đang làm việc", "Đã nghỉ / Khóa"});
        cbTrangThai.setFont(new Font("Arial", Font.PLAIN, 13)); cbTrangThai.setBackground(Color.WHITE);
        
        JPanel panelGridFields = new JPanel(new GridLayout(2, 3, 30, 10));
        panelGridFields.setBackground(Color.WHITE);
        panelGridFields.setBorder(BorderFactory.createEmptyBorder(10, 5, 15, 5));
        
        // Tạo từng ô nhập liệu kèm label cảnh báo màu đỏ ở dưới
        panelGridFields.add(taoOnhapLieuCoCanhBao("Mã nhân viên (Tự động)", txtMaNV, null));
        panelGridFields.add(taoOnhapLieuCoCanhBao("<html>Họ và Tên <font color='red'>(*)</font></html>", txtHoTen, lblLoiHoTen));
        panelGridFields.add(taoOnhapLieuCoCanhBao("<html>Số điện thoại <font color='red'>(*)</font></html>", txtSoDienThoai, lblLoiSDT));
        panelGridFields.add(taoOnhapLieuCoCanhBao("Vai trò / Chức vụ", cbVaiTro, null));
        panelGridFields.add(taoOnhapLieuCoCanhBao("<html>Ngày vào làm (DD/MM/YYYY) <font color='red'>(*)</font></html>", txtNgayVaoLam, lblLoiNgayVaoLam));
        panelGridFields.add(taoOnhapLieuCoCanhBao("Trạng thái", cbTrangThai, null));
        
        // LẮNG NGHE SỰ KIỆN NHẬP LIỆU REAL-TIME ĐỂ HIỂN THỊ / ẨN CẢNH BÁO
        ganSuKienKiemTraRealtime(txtHoTen, e -> lblLoiHoTen.setText(nhanVienService.kiemTraHoTen(txtHoTen.getText())));
        ganSuKienKiemTraRealtime(txtSoDienThoai, e -> lblLoiSDT.setText(nhanVienService.kiemTraSoDienThoai(txtSoDienThoai.getText())));
        ganSuKienKiemTraRealtime(txtNgayVaoLam, e -> lblLoiNgayVaoLam.setText(nhanVienService.kiemTraNgayVaoLam(txtNgayVaoLam.getText())));

        // Thanh bộ lọc & Bảng dữ liệu
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
        
        txtTimKiem.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) { taiLaiDuLieuBang(); }
        });
        
        txtTimKiem.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                if (txtTimKiem.getText().equals(" Tìm kiếm nhân viên...")) {
                    txtTimKiem.setText(""); txtTimKiem.setForeground(Color.BLACK);
                }
            }
            public void focusLost(java.awt.event.FocusEvent e) {
                if (txtTimKiem.getText().trim().isEmpty()) {
                    txtTimKiem.setText(" Tìm kiếm nhân viên..."); txtTimKiem.setForeground(Color.GRAY);
                }
            }
        });

        cbLocVaiTro = new JComboBox<>(new String[]{"Tất cả vai trò", "Nhân viên tiếp nhận", "Kỹ thuật viên", "Nhân viên Kho", "Thu ngân"});
        cbLocVaiTro.setFont(new Font("Arial", Font.PLAIN, 12));
        cbLocVaiTro.setBackground(Color.WHITE);
        cbLocVaiTro.addActionListener(e -> taiLaiDuLieuBang());
        
        ImageIcon iconTim = loadIcon("/img/icon_tim.png", 16, 16);
        if (iconTim != null) panelSearchFilter.add(new JLabel(iconTim), BorderLayout.WEST);
        
        panelSearchFilter.add(txtTimKiem, BorderLayout.CENTER);
        panelSearchFilter.add(cbLocVaiTro, BorderLayout.EAST);
        
        String[] columnNames = {"Mã NV", "Họ Tên", "SĐT", "Vai Trò", "Ngày Vào Làm", "Trạng Thái"};
        tableModelNhanVien = new DefaultTableModel(columnNames, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tableNhanVien = new JTable(tableModelNhanVien);
        
        tableNhanVien.setRowHeight(32);
        tableNhanVien.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        tableNhanVien.getTableHeader().setBackground(new Color(230, 235, 240));
        tableNhanVien.setFont(new Font("Arial", Font.PLAIN, 13));
        tableNhanVien.setSelectionBackground(new Color(40, 100, 150));
        tableNhanVien.setSelectionForeground(Color.WHITE);
        
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        tableNhanVien.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        
        // CLICK ĐỂ ĐẨY DỮ LIỆU LÊN FORM
        tableNhanVien.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int row = tableNhanVien.getSelectedRow();
                if (row >= 0) {
                    txtMaNV.setText(tableModelNhanVien.getValueAt(row, 0).toString());
                    txtHoTen.setText(tableModelNhanVien.getValueAt(row, 1).toString());
                    txtSoDienThoai.setText(tableModelNhanVien.getValueAt(row, 2).toString());
                    cbVaiTro.setSelectedItem(tableModelNhanVien.getValueAt(row, 3).toString());
                    txtNgayVaoLam.setText(tableModelNhanVien.getValueAt(row, 4).toString());
                    cbTrangThai.setSelectedItem(tableModelNhanVien.getValueAt(row, 5).toString());
                    
                    // Xóa các thông báo lỗi cũ khi chọn dòng mới
                    xoaTatCaThongBaoLoi();
                }
            }
        });

        JPanel panelTableSection = new JPanel(new BorderLayout(0, 10));
        panelTableSection.setBackground(Color.WHITE);
        panelTableSection.add(panelSearchFilter, BorderLayout.NORTH);
        panelTableSection.add(new JScrollPane(tableNhanVien), BorderLayout.CENTER);
        
        // Nút chức năng
        JPanel panelBottomButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelBottomButtons.setBackground(Color.WHITE);
        panelBottomButtons.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        
        JButton btnLamMoi = taoNutChucNang("Làm mới", Color.WHITE, new Color(50, 55, 65));
        JButton btnKhoa = taoNutChucNang("Khóa tài khoản", Color.WHITE, new Color(210, 30, 30));
        JButton btnCapNhat = taoNutChucNang("Cập nhật", new Color(40, 100, 150), Color.WHITE);
        JButton btnThemMoi = taoNutChucNang("Thêm mới", new Color(25, 135, 84), Color.WHITE);
        
        // LÀM MỚI
        btnLamMoi.addActionListener(e -> {
            txtMaNV.setText("");
            txtHoTen.setText("");
            txtSoDienThoai.setText("");
            txtNgayVaoLam.setText("");
            cbVaiTro.setSelectedIndex(0);
            cbTrangThai.setSelectedIndex(0);
            xoaTatCaThongBaoLoi();
            
            tableNhanVien.clearSelection();
            txtTimKiem.setText(" Tìm kiếm nhân viên...");
            txtTimKiem.setForeground(Color.GRAY);
            cbLocVaiTro.setSelectedIndex(0);
            taiLaiDuLieuBang();
        });

        // THÊM MỚI
        btnThemMoi.addActionListener(e -> {
            capNhatThongBaoLoiToanBo();
            try {
                boolean thanhCong = nhanVienService.themNhanVien(
                    txtHoTen.getText(), txtSoDienThoai.getText(), 
                    cbVaiTro.getSelectedItem().toString(), 
                    txtNgayVaoLam.getText(), cbTrangThai.getSelectedItem().toString()
                );
                if (thanhCong) {
                    JOptionPane.showMessageDialog(this, "Thêm mới nhân viên thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    btnLamMoi.doClick();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
            }
        });

        // CẬP NHẬT
        btnCapNhat.addActionListener(e -> {
            String maNV = txtMaNV.getText();
            if (maNV.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 nhân viên từ danh sách bên dưới để Cập nhật!", "Chú ý", JOptionPane.WARNING_MESSAGE);
                return;
            }
            capNhatThongBaoLoiToanBo();
            try {
                boolean thanhCong = nhanVienService.capNhatNhanVien(
                    maNV, txtHoTen.getText(), txtSoDienThoai.getText(), 
                    cbVaiTro.getSelectedItem().toString(), 
                    txtNgayVaoLam.getText(), cbTrangThai.getSelectedItem().toString()
                );
                if (thanhCong) {
                    JOptionPane.showMessageDialog(this, "Cập nhật hồ sơ nhân viên thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    taiLaiDuLieuBang();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
            }
        });

        // KHÓA TÀI KHOẢN
        btnKhoa.addActionListener(e -> {
            String maNV = txtMaNV.getText();
            if (maNV.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên để khóa tài khoản!", "Chú ý", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int xacNhan = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn khóa tài khoản nhân viên: " + txtHoTen.getText() + "?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (xacNhan == JOptionPane.YES_OPTION) {
                try {
                    if (nhanVienService.khoaTaiKhoan(maNV)) {
                        JOptionPane.showMessageDialog(this, "Đã khóa/cho nghỉ việc tài khoản nhân viên thành công.");
                        cbTrangThai.setSelectedItem("Đã nghỉ / Khóa");
                        taiLaiDuLieuBang();
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Lỗi khi khóa tài khoản: " + ex.getMessage(), "Lỗi cơ sở dữ liệu", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        panelBottomButtons.add(btnLamMoi);
        panelBottomButtons.add(btnKhoa);
        panelBottomButtons.add(btnCapNhat);
        panelBottomButtons.add(btnThemMoi);
        
        panelTab.add(panelGridFields, BorderLayout.NORTH);
        panelTab.add(panelTableSection, BorderLayout.CENTER);
        panelTab.add(panelBottomButtons, BorderLayout.SOUTH);
        return panelTab;
    }
    
    // ==================== HÀM TẠO CẢNH BÁO ĐỎ TRÊN GIAO DIỆN ====================
    private JLabel taoLabelCanhBao() {
        JLabel lbl = new JLabel("");
        lbl.setFont(new Font("Arial", Font.ITALIC, 11));
        lbl.setForeground(new Color(220, 50, 50)); // Màu đỏ
        return lbl;
    }

    private JPanel taoOnhapLieuCoCanhBao(String labelText, JComponent inputComponent, JLabel lblCanhBao) {
        JPanel panel = new JPanel(new BorderLayout(0, 2));
        panel.setBackground(Color.WHITE);
        
        JLabel lblHeader = new JLabel(labelText);
        lblHeader.setFont(new Font("Arial", Font.BOLD, 12));
        lblHeader.setForeground(new Color(80, 85, 95));
        
        inputComponent.setFont(new Font("Arial", Font.PLAIN, 13));
        inputComponent.setPreferredSize(new Dimension(250, 32));
        if (inputComponent instanceof JTextField) {
            inputComponent.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(200, 205, 210), 1),
                    BorderFactory.createEmptyBorder(4, 10, 4, 10)
            ));
        }
        
        panel.add(lblHeader, BorderLayout.NORTH);
        panel.add(inputComponent, BorderLayout.CENTER);
        if (lblCanhBao != null) {
            panel.add(lblCanhBao, BorderLayout.SOUTH);
        }
        return panel;
    }

    private void ganSuKienKiemTraRealtime(JTextField textField, java.util.function.Consumer<DocumentEvent> consumer) {
        textField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { consumer.accept(e); }
            public void removeUpdate(DocumentEvent e) { consumer.accept(e); }
            public void changedUpdate(DocumentEvent e) { consumer.accept(e); }
        });
    }

    private void capNhatThongBaoLoiToanBo() {
        lblLoiHoTen.setText(nhanVienService.kiemTraHoTen(txtHoTen.getText()));
        lblLoiSDT.setText(nhanVienService.kiemTraSoDienThoai(txtSoDienThoai.getText()));
        lblLoiNgayVaoLam.setText(nhanVienService.kiemTraNgayVaoLam(txtNgayVaoLam.getText()));
    }

    private void xoaTatCaThongBaoLoi() {
        lblLoiHoTen.setText("");
        lblLoiSDT.setText("");
        lblLoiNgayVaoLam.setText("");
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
            btn.setBorderPainted(false);
            btn.setBorder(BorderFactory.createEmptyBorder(9, 20, 9, 20));
        }
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private ImageIcon loadIcon(String path, int width, int height) {
        try {
            java.net.URL imgURL = getClass().getResource(path);
            if (imgURL != null) return new ImageIcon(new ImageIcon(imgURL).getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH));
        } catch (Exception e) {}
        return null;
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ex) {}
        try { ConnectDB.getInstance().connect(); } catch (SQLException ex) {
            ex.printStackTrace();
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