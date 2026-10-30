package ui;

import connectDB.ConnectDB;
import service.PhanCongKTVService;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class GaraPhanCongKTV extends JPanel {
    private static final long serialVersionUID = 1L;
    
    // Components
    private JComboBox<String> cbPhieuSuaChua, cbKyThuatVien;
    private JTextField txtSoXeDangPhuTrach, txtGhiChuPhanCong;
    private JTable tablePhanCong;
    private DefaultTableModel tableModelPhanCong;
    
    // Service
    private PhanCongKTVService phanCongKTVService;

    public GaraPhanCongKTV() {
        phanCongKTVService = new PhanCongKTVService();
        
        khoiTaoGiaoDien();
        taiDuLieuVaoComboBox();
        loadDataToTablePhanCong();
    }

    private void taiDuLieuVaoComboBox() {
        try {
            // Tải danh sách Phiếu sửa chữa
            List<String> listPhieu = phanCongKTVService.layDanhSachPhieuSuaChua();
            cbPhieuSuaChua.removeAllItems();
            if (listPhieu.isEmpty()) {
                cbPhieuSuaChua.addItem("Không có xe nào đang chờ");
            } else {
                for (String phieu : listPhieu) cbPhieuSuaChua.addItem(phieu);
            }

            // Tải danh sách Kỹ thuật viên chuẩn xác từ DB (PQ003)
            List<String> listKTV = phanCongKTVService.layDanhSachKyThuatVien();
            cbKyThuatVien.removeAllItems();
            if (listKTV.isEmpty()) {
                cbKyThuatVien.addItem("Không có KTV nào đang hoạt động");
            } else {
                for (String ktv : listKTV) cbKyThuatVien.addItem(ktv);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu Combobox: " + e.getMessage());
        }
    }

    private void loadDataToTablePhanCong() {
        if (tableModelPhanCong == null) return;
        tableModelPhanCong.setRowCount(0);

        try {
            List<Object[]> listPC = phanCongKTVService.layDanhSachCongViecTaiXuong();
            for (Object[] row : listPC) {
                tableModelPhanCong.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải danh sách công việc: " + e.getMessage());
        }
    }

    private void khoiTaoGiaoDien() {
        setLayout(new BorderLayout());
        JPanel panelCenter = new JPanel(new BorderLayout());
        panelCenter.setBackground(new Color(240, 243, 246));
        panelCenter.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JPanel panelCardChinh = new JPanel(new BorderLayout(0, 15));
        panelCardChinh.setBackground(Color.WHITE);
        panelCardChinh.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 215, 220), 1),
                BorderFactory.createEmptyBorder(20, 25, 20, 25)
        ));
        
        JLabel lblTieuDeCard = new JLabel("ĐIỀU PHỐI & PHÂN CÔNG KỸ THUẬT VIÊN");
        lblTieuDeCard.setFont(new Font("Arial", Font.BOLD, 18));
        JLabel lblMoTa = new JLabel("Chỉ định KTV cho các Phiếu sửa chữa đang có trong xưởng.");
        lblMoTa.setForeground(Color.GRAY);
        JPanel pnlTitleWrap = new JPanel(new GridLayout(2, 1));
        pnlTitleWrap.setBackground(Color.WHITE);
        pnlTitleWrap.add(lblTieuDeCard);
        pnlTitleWrap.add(lblMoTa);
        
        cbPhieuSuaChua = new JComboBox<>();
        cbKyThuatVien = new JComboBox<>();
        cbPhieuSuaChua.setBackground(Color.WHITE);
        cbKyThuatVien.setBackground(Color.WHITE);
        
        txtSoXeDangPhuTrach = new JTextField("Đang tải..."); 
        txtSoXeDangPhuTrach.setEditable(false); 
        txtSoXeDangPhuTrach.setBackground(new Color(245, 247, 250));
        txtGhiChuPhanCong = new JTextField();
        
        JPanel panelGridFields = new JPanel(new GridLayout(2, 2, 30, 15));
        panelGridFields.setBackground(Color.WHITE);
        panelGridFields.add(taoOnhapLieu("Chọn Phiếu SC (Xe chờ xử lý)", cbPhieuSuaChua));
        panelGridFields.add(taoOnhapLieu("Chọn Kỹ Thuật Viên", cbKyThuatVien));
        panelGridFields.add(taoOnhapLieu("Ghi chú / Nhắc nhở", txtGhiChuPhanCong));
        panelGridFields.add(taoOnhapLieu("Trạng thái của KTV", txtSoXeDangPhuTrach));
        
        // Bảng danh sách phân công (Đã bỏ bớt cột Mã PC thừa)
        JLabel lblDanhSach = new JLabel("DANH SÁCH CÔNG VIỆC TẠI XƯỞNG (ĐANG KIỂM TRA / ĐANG SỬA CHỮA)");
        lblDanhSach.setFont(new Font("Arial", Font.BOLD, 14));
        lblDanhSach.setForeground(new Color(210, 30, 30));
        JPanel pnlTitleTable = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlTitleTable.setBackground(Color.WHITE);
        pnlTitleTable.add(lblDanhSach);
        
        String[] cols = {"Mã Phiếu SC", "Biển Số Xe", "Kỹ Thuật Viên", "Ngày Bắt Đầu", "Tiến Độ"};
        tableModelPhanCong = new DefaultTableModel(cols, 0) { 
            public boolean isCellEditable(int r, int c) { return false; } 
        };
        tablePhanCong = new JTable(tableModelPhanCong);
        tablePhanCong.setRowHeight(32); 
        tablePhanCong.getTableHeader().setBackground(new Color(230, 235, 240));

        JPanel panelTableSection = new JPanel(new BorderLayout(0, 5));
        panelTableSection.setBackground(Color.WHITE);
        panelTableSection.add(pnlTitleTable, BorderLayout.NORTH);
        panelTableSection.add(new JScrollPane(tablePhanCong), BorderLayout.CENTER);
        
        // Nút chức năng
        JPanel panelBottomButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelBottomButtons.setBackground(Color.WHITE);
        JButton btnLamMoi = taoNutChucNang("Làm mới", Color.WHITE, new Color(50, 55, 65));
        JButton btnHuyPC = taoNutChucNang("Hủy phân công", Color.WHITE, new Color(210, 30, 30));
        JButton btnPhanCong = taoNutChucNang("Xác nhận Phân Công", new Color(25, 135, 84), Color.WHITE);
        
        btnLamMoi.addActionListener(e -> {
            taiDuLieuVaoComboBox();
            loadDataToTablePhanCong();
            txtGhiChuPhanCong.setText("");
        });

        // Nút Xác nhận phân công
        btnPhanCong.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, 
                "Lưu ý: Database NightInvader hiện tại chưa có cột/bảng để lưu Kỹ thuật viên phụ trách Phiếu sửa chữa.\n" +
                "Vui lòng thiết kế thêm cột maKyThuatVien vào bảng PhieuSuaChua để chức năng này hoạt động hoàn thiện.", 
                "Chờ cập nhật Database", 
                JOptionPane.INFORMATION_MESSAGE);
        });

        panelBottomButtons.add(btnLamMoi); 
        panelBottomButtons.add(btnHuyPC);
        panelBottomButtons.add(btnPhanCong);
        
        panelCardChinh.add(pnlTitleWrap, BorderLayout.NORTH);
        JPanel pnlBody = new JPanel(new BorderLayout(0, 15));
        pnlBody.setBackground(Color.WHITE);
        pnlBody.add(panelGridFields, BorderLayout.NORTH);
        pnlBody.add(panelTableSection, BorderLayout.CENTER);
        pnlBody.add(panelBottomButtons, BorderLayout.SOUTH);
        panelCardChinh.add(pnlBody, BorderLayout.CENTER);
        panelCenter.add(panelCardChinh, BorderLayout.CENTER);
        
        add(panelCenter, BorderLayout.CENTER);
    }
    
    private JPanel taoOnhapLieu(String lbl, JComponent c) {
        JPanel p = new JPanel(new BorderLayout(0, 5)); p.setBackground(Color.WHITE);
        JLabel l = new JLabel(lbl); l.setFont(new Font("Arial", Font.BOLD, 12));
        c.setPreferredSize(new Dimension(250, 35));
        if (c instanceof JTextField) c.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(200, 205, 210)), BorderFactory.createEmptyBorder(4, 10, 4, 10)));
        p.add(l, BorderLayout.NORTH); p.add(c, BorderLayout.CENTER); return p;
    }
    
    private JButton taoNutChucNang(String text, Color bg, Color fg) {
        JButton btn = new JButton(text); btn.setBackground(bg); btn.setForeground(fg);
        btn.setContentAreaFilled(false); btn.setOpaque(true);
        if (bg.equals(Color.WHITE)) btn.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(fg), BorderFactory.createEmptyBorder(8, 20, 8, 20)));
        else { btn.setOpaque(true); btn.setBorderPainted(false); btn.setBorder(BorderFactory.createEmptyBorder(9, 20, 9, 20)); }
        return btn;
    }
    
    public static void main(String[] args) {
        try {
            ConnectDB.getInstance().connect();
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("GaraPhanCongKTV - NIGHT INVADER");
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setContentPane(new GaraPhanCongKTV());
            frame.setSize(1340, 840);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}