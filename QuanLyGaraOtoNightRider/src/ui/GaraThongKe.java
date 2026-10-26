package ui;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
public class GaraThongKe extends JPanel {
    private static final long serialVersionUID = 1L;
    private JTabbedPane tabbedPane;
    public GaraThongKe() {
        this(0);
    }
    public GaraThongKe(int tabMacDinh) {
        khoiTaoGiaoDien();
        if (tabMacDinh >= 0 && tabMacDinh < tabbedPane.getTabCount()) {
            tabbedPane.setSelectedIndex(tabMacDinh);
        }
    }
    private void khoiTaoGiaoDien() {
        setLayout(new BorderLayout());
        // ==================== 3. NỘI DUNG CHÍNH: FORM THỐNG KÊ ====================
        JPanel panelCenter = new JPanel();
        panelCenter.setBackground(new Color(240, 243, 246));
        panelCenter.setLayout(new BorderLayout());
        panelCenter.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        JPanel panelCardChinh = new JPanel(new BorderLayout());
        panelCardChinh.setBackground(Color.WHITE);
        panelCardChinh.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 215, 220), 1),
                BorderFactory.createEmptyBorder(25, 30, 25, 30)
        ));
        // --- 3.1. Tiêu đề card ---
        JPanel panelCardTop = new JPanel(new BorderLayout());
        panelCardTop.setBackground(Color.WHITE);
        panelCardTop.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        JLabel lblTieuDeCard = new JLabel("BÁO CÁO THỐNG KÊ TỔNG HỢP");
        lblTieuDeCard.setFont(new Font("Arial", Font.BOLD, 18));
        lblTieuDeCard.setForeground(new Color(30, 35, 45));
        JLabel lblMoTa = new JLabel("Tổng hợp dữ liệu doanh thu, tồn kho và phân tích kinh doanh.");
        lblMoTa.setFont(new Font("Arial", Font.PLAIN, 12));
        lblMoTa.setForeground(Color.GRAY);
        JPanel pnlTitleWrap = new JPanel(new GridLayout(2, 1));
        pnlTitleWrap.setBackground(Color.WHITE);
        pnlTitleWrap.add(lblTieuDeCard);
        pnlTitleWrap.add(lblMoTa);
        panelCardTop.add(pnlTitleWrap, BorderLayout.WEST);
        // --- 3.2. Form Thống Kê (Tabs) ---
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 14));
        tabbedPane.setBackground(new Color(245, 247, 250));
        tabbedPane.addTab("Thống kê doanh thu", taoTabDoanhThu());
        tabbedPane.addTab("Thống kê tồn kho", taoTabTonKho());
        tabbedPane.addTab("Phụ tùng bán chạy", taoTabBanChay());
        tabbedPane.addTab("Phụ tùng bán chậm", taoTabBanCham());
        // --- 3.3. Khu vực nút bấm dưới cùng ---
        JPanel panelBottomButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        panelBottomButtons.setBackground(Color.WHITE);
        panelBottomButtons.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        JButton btnXuatExcel = new JButton("Xuất Excel");
        btnXuatExcel.setFont(new Font("Arial", Font.BOLD, 13));
        btnXuatExcel.setBackground(new Color(25, 135, 84));
        btnXuatExcel.setContentAreaFilled(false);
        btnXuatExcel.setForeground(Color.WHITE);
        btnXuatExcel.setOpaque(true);
        btnXuatExcel.setBorderPainted(false);
        btnXuatExcel.setFocusPainted(false);
        btnXuatExcel.setBorder(BorderFactory.createEmptyBorder(9, 18, 9, 18));
        btnXuatExcel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnXuatExcel.addActionListener(e -> JOptionPane.showMessageDialog(this, "Đang xuất dữ liệu tab hiện tại ra file Excel...", "Thông báo", JOptionPane.INFORMATION_MESSAGE));
        JButton btnQuayLai = new JButton("Trở về Màn Hình Chính");
        btnQuayLai.setFont(new Font("Arial", Font.BOLD, 13));
        btnQuayLai.setBackground(Color.WHITE);
        btnQuayLai.setContentAreaFilled(false);
        btnQuayLai.setOpaque(true);
        btnQuayLai.setForeground(new Color(50, 55, 65));
        btnQuayLai.setFocusPainted(false);
        btnQuayLai.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 205, 210), 1),
                BorderFactory.createEmptyBorder(8, 16, 8, 16)
        ));
        btnQuayLai.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnQuayLai.addActionListener(e -> {
            Window cuaSo = SwingUtilities.getWindowAncestor(this);
            if (cuaSo instanceof GaraManHinhChinh) {
                ((GaraManHinhChinh) cuaSo).hienThiTrangChu();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Hãy mở giao diện này từ GaraManHinhChinh.");
            }
        });
        panelBottomButtons.add(btnQuayLai);
        panelBottomButtons.add(btnXuatExcel);
        // Gắn vào Card Chính
        panelCardChinh.add(panelCardTop, BorderLayout.NORTH);
        panelCardChinh.add(tabbedPane, BorderLayout.CENTER);
        panelCardChinh.add(panelBottomButtons, BorderLayout.SOUTH);
        panelCenter.add(panelCardChinh, BorderLayout.CENTER);
        // Menu, toolbar và footer dùng chung của GaraManHinhChinh.
        add(panelCenter, BorderLayout.CENTER);
    }
    // ==================== CÁC TAB THỐNG KÊ ====================
    private JPanel taoTabDoanhThu() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
        // Lọc
        JPanel panelLoc = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        panelLoc.setBackground(new Color(250, 250, 252));
        panelLoc.setBorder(BorderFactory.createLineBorder(new Color(220, 225, 230)));
        panelLoc.add(new JLabel("Từ ngày:"));
        panelLoc.add(new JTextField("01/10/2026", 10));
        panelLoc.add(new JLabel("Đến ngày:"));
        panelLoc.add(new JTextField("31/10/2026", 10));
        JButton btnLoc = new JButton("Lọc Dữ Liệu");
        btnLoc.setBackground(new Color(30, 35, 45));
        btnLoc.setForeground(Color.WHITE);
        btnLoc.setFocusPainted(false);
        panelLoc.add(btnLoc);
        // Tổng quan
        JPanel panelTongQuan = new JPanel(new GridLayout(1, 3, 20, 0));
        panelTongQuan.setOpaque(false);
        panelTongQuan.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        panelTongQuan.add(taoCardThongKe("TỔNG DOANH THU", "85,450,000 đ", new Color(25, 135, 84)));
        panelTongQuan.add(taoCardThongKe("TỔNG CHI PHÍ PHỤ TÙNG", "32,100,000 đ", new Color(210, 30, 30)));
        panelTongQuan.add(taoCardThongKe("LỢI NHUẬN GỘP", "53,350,000 đ", new Color(13, 110, 253)));
        JPanel panelTop = new JPanel(new BorderLayout(0, 5));
        panelTop.setOpaque(false);
        panelTop.add(panelLoc, BorderLayout.NORTH);
        panelTop.add(panelTongQuan, BorderLayout.CENTER);
        // Bảng
        String[] columns = {"Mã HĐ", "Ngày Lập", "Biển Số Xe", "Khách Hàng", "Tiền Công", "Tiền Phụ Tùng", "Tổng Thu"};
        Object[][] data = {
                {"HD00101", "15/10/2026", "51G-123.45", "Trần Ngọc Huy", "500,000", "1,200,000", "1,700,000"},
                {"HD00102", "15/10/2026", "60A-888.99", "Nguyễn Văn A", "1,500,000", "3,000,000", "4,500,000"}
        };
        JTable table = taoBangDuLieu(columns, data);
        panel.add(panelTop, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }
    private JPanel taoTabTonKho() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
        JPanel panelTongQuan = new JPanel(new GridLayout(1, 2, 20, 0));
        panelTongQuan.setOpaque(false);
        panelTongQuan.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        panelTongQuan.add(taoCardThongKe("TỔNG SỐ LƯỢNG PHỤ TÙNG", "1,245 CHIẾC", Color.DARK_GRAY));
        panelTongQuan.add(taoCardThongKe("TỔNG GIÁ TRỊ TỒN KHO HIỆN TẠI", "145,000,000 VNĐ", new Color(210, 30, 30)));
        String[] columns = {"Mã PT", "Tên Phụ Tùng / Vật Tư", "ĐVT", "SL Tồn", "Đơn Giá Nhập", "Thành Tiền", "Trạng Thái"};
        Object[][] data = {
                {"PT001", "Nhớt Castrol GTX 10W-40", "Can", "15", "350,000", "5,250,000", "Bình thường"},
                {"PT002", "Lọc nhớt Honda Civic 2020", "Cái", "2", "150,000", "300,000", "Cảnh báo Min-stock"}
        };
        JTable table = taoBangDuLieu(columns, data);
        panel.add(panelTongQuan, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }
    private JPanel taoTabBanChay() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
        JLabel lblTop = new JLabel("TOP 10 PHỤ TÙNG XUẤT KHO NHIỀU NHẤT KỲ");
        lblTop.setFont(new Font("Arial", Font.BOLD, 15));
        String[] columns = {"Xếp Hạng", "Mã PT", "Tên Phụ Tùng", "Số Lượng Đã Xuất", "Doanh Thu Mang Lại (VNĐ)"};
        Object[][] data = {
                {"1", "PT001", "Nhớt Castrol GTX 10W-40", "45", "20,250,000"},
                {"2", "PT005", "Bugi Iridium NGK", "30", "12,000,000"}
        };
        JTable table = taoBangDuLieu(columns, data);
        panel.add(lblTop, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }
    private JPanel taoTabBanCham() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
        JLabel lblTop = new JLabel("DANH SÁCH PHỤ TÙNG ÍT GIAO DỊCH (TỒN ĐỌNG LÂU)");
        lblTop.setFont(new Font("Arial", Font.BOLD, 15));
        String[] columns = {"Xếp Hạng", "Mã PT", "Tên Phụ Tùng", "Số Lượng Tồn", "Số Ngày Tồn", "Lần Xuất Gần Nhất"};
        Object[][] data = {
                {"1", "PT088", "Cản trước Toyota Vios 2018", "2", "120 ngày", "01/06/2026"},
                {"2", "PT091", "Gương chiếu hậu Mazda 3", "3", "85 ngày", "15/07/2026"}
        };
        JTable table = taoBangDuLieu(columns, data);
        panel.add(lblTop, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }
    // ==================== HÀM TIỆN ÍCH ====================
    private JPanel taoCardThongKe(String tieuDe, String giaTri, Color mauNhan) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(new Color(250, 252, 254)); // Nền hơi sáng xám nhạt để tách khỏi nền trắng của panel
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 225, 230), 1),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 5, 0, 0, mauNhan),
                        BorderFactory.createEmptyBorder(15, 20, 15, 20)
                )
        ));
        JLabel lblTieuDe = new JLabel(tieuDe);
        lblTieuDe.setFont(new Font("Arial", Font.BOLD, 12));
        lblTieuDe.setForeground(Color.GRAY);
        JLabel lblGiaTri = new JLabel(giaTri);
        lblGiaTri.setFont(new Font("Arial", Font.BOLD, 22));
        lblGiaTri.setForeground(mauNhan);
        card.add(lblTieuDe);
        card.add(Box.createRigidArea(new Dimension(0, 8)));
        card.add(lblGiaTri);
        return card;
    }
    private JTable taoBangDuLieu(String[] columns, Object[][] data) {
        DefaultTableModel model = new DefaultTableModel(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(model);
        table.setRowHeight(32);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(230, 235, 240));
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.setSelectionBackground(new Color(40, 100, 150));
        table.setSelectionForeground(Color.WHITE);
        // Căn phải cho cột tiền (VD cột cuối)
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        table.getColumnModel().getColumn(table.getColumnCount() - 1).setCellRenderer(rightRenderer);
        return table;
    }
    private ImageIcon loadIcon(String path, int width, int height) {
        try {
            java.net.URL imgURL = getClass().getResource(path);
            if (imgURL != null) {
                ImageIcon icon = new ImageIcon(imgURL);
                Image scaledImg = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
                return new ImageIcon(scaledImg);
            }
        } catch (Exception e) {
            System.out.println("Không thể tải ảnh: " + path);
        }
        return null;
    }
    public void chonTab(int chiSo) {
        if (chiSo >= 0 && chiSo < tabbedPane.getTabCount()) {
            tabbedPane.setSelectedIndex(chiSo);
        }
    }
    // Chạy thử riêng; ứng dụng chính vẫn chạy từ Main.
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("GaraThongKe - NIGHT INVADER");
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setContentPane(new GaraThongKe());
            frame.setSize(1340, 840);
            frame.setLocationRelativeTo(null);
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            frame.setVisible(true);
        });
    }
}
