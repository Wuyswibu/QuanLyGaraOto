package ui;

import connectDB.ConnectDB;
import dao.PhanCongCaDao;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class GaraPhanCongKTV extends JPanel {
    private static final long serialVersionUID = 1L;
    private JComboBox<String> cbPhieuSuaChua, cbKyThuatVien;
    private JTextField txtChuyenMon, txtSoXeDangPhuTrach, txtGhiChuPhanCong;
    
    private JTable tablePhanCong;
    private DefaultTableModel tableModelPhanCong;
    
    // Sử dụng trực tiếp PhanCongCaDao
    private PhanCongCaDao phanCongCaDao;

    public GaraPhanCongKTV() {
        phanCongCaDao = new PhanCongCaDao();
        
        khoiTaoGiaoDien();
        
        // Đổ dữ liệu lên bảng
        loadDataToTablePhanCong();
    }

    // ==================== HÀM TẢI DỮ LIỆU LÊN BẢNG ====================
    private void loadDataToTablePhanCong() {
        if (tableModelPhanCong == null) return;
        tableModelPhanCong.setRowCount(0);

        try {
            // Gọi hàm vừa thêm vào trong PhanCongCaDao
            List<Object[]> listPC = phanCongCaDao.layDanhSachPhanCongKTV();
            for (Object[] row : listPC) {
                tableModelPhanCong.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu phân công: " + e.getMessage());
        }
    }

    private void khoiTaoGiaoDien() {
        setLayout(new BorderLayout());
        
        // ==================== NỘI DUNG CHÍNH ====================
        JPanel panelCenter = new JPanel(new BorderLayout());
        panelCenter.setBackground(new Color(240, 243, 246));
        panelCenter.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JPanel panelCardChinh = new JPanel(new BorderLayout(0, 15));
        panelCardChinh.setBackground(Color.WHITE);
        panelCardChinh.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 215, 220), 1),
                BorderFactory.createEmptyBorder(20, 25, 20, 25)
        ));
        
        // Tiêu đề
        JLabel lblTieuDeCard = new JLabel("ĐIỀU PHỐI & PHÂN CÔNG KỸ THUẬT VIÊN");
        lblTieuDeCard.setFont(new Font("Arial", Font.BOLD, 18));
        JLabel lblMoTa = new JLabel("Chỉ định KTV cho các Phiếu sửa chữa dựa trên chuyên môn và số lượng xe đang phụ trách.");
        lblMoTa.setForeground(Color.GRAY);
        JPanel pnlTitleWrap = new JPanel(new GridLayout(2, 1));
        pnlTitleWrap.setBackground(Color.WHITE);
        pnlTitleWrap.add(lblTieuDeCard);
        pnlTitleWrap.add(lblMoTa);
        
        // Form Nhập liệu
        cbPhieuSuaChua = new JComboBox<>(new String[]{"[SC00215] Honda Civic (Chờ kiểm tra)", "[SC00216] Mazda CX-5 (Chờ sửa chữa)"});
        cbKyThuatVien = new JComboBox<>(new String[]{"[NV002] Trần Ngọc Huy", "[NV005] Lê Văn Tám"});
        cbPhieuSuaChua.setBackground(Color.WHITE);
        cbKyThuatVien.setBackground(Color.WHITE);
        txtChuyenMon = new JTextField("Điện, Điện lạnh ô tô"); txtChuyenMon.setEditable(false); txtChuyenMon.setBackground(new Color(245, 247, 250));
        txtSoXeDangPhuTrach = new JTextField("0 xe (Đang rảnh)"); txtSoXeDangPhuTrach.setEditable(false); txtSoXeDangPhuTrach.setBackground(new Color(245, 247, 250));
        txtGhiChuPhanCong = new JTextField("Kiểm tra kỹ hệ thống lốc lạnh.");
        
        JPanel panelGridFields = new JPanel(new GridLayout(2, 3, 30, 15));
        panelGridFields.setBackground(Color.WHITE);
        panelGridFields.add(taoOnhapLieu("Chọn Phiếu SC (Xe chờ xử lý)", cbPhieuSuaChua));
        panelGridFields.add(taoOnhapLieu("Chọn Kỹ Thuật Viên", cbKyThuatVien));
        panelGridFields.add(taoOnhapLieu("Ghi chú / Nhắc nhở", txtGhiChuPhanCong));
        panelGridFields.add(taoOnhapLieu("Chuyên môn của KTV", txtChuyenMon));
        panelGridFields.add(taoOnhapLieu("Số xe đang phụ trách", txtSoXeDangPhuTrach));
        panelGridFields.add(new JLabel());
        
        // Bảng danh sách phân công
        JLabel lblDanhSach = new JLabel("DANH SÁCH CÔNG VIỆC TẠI XƯỞNG (ĐANG KIỂM TRA / ĐANG SỬA CHỮA)");
        lblDanhSach.setFont(new Font("Arial", Font.BOLD, 14));
        lblDanhSach.setForeground(new Color(210, 30, 30));
        JPanel pnlTitleTable = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlTitleTable.setBackground(Color.WHITE);
        pnlTitleTable.add(lblDanhSach);
        
        String[] cols = {"Mã PC", "Mã Phiếu SC", "Biển Số Xe", "Kỹ Thuật Viên", "Chuyên môn", "Ngày Phân Công", "Tiến Độ"};
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
        JButton btnCapNhatPC = taoNutChucNang("Cập nhật Ghi chú", new Color(40, 100, 150), Color.WHITE);
        JButton btnPhanCong = taoNutChucNang("Xác nhận Phân Công", new Color(25, 135, 84), Color.WHITE);
        
        btnLamMoi.addActionListener(e -> loadDataToTablePhanCong());

        panelBottomButtons.add(btnLamMoi); panelBottomButtons.add(btnHuyPC);
        panelBottomButtons.add(btnCapNhatPC); panelBottomButtons.add(btnPhanCong);
        
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
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            frame.setVisible(true);
        });
    }
}