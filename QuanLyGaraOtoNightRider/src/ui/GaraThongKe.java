package ui;

import connectDB.ConnectDB;
import entity.HoaDon;
import service.ThongKeService;
import service.ThongKeService.KetQuaDoanhThu;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class GaraThongKe extends JPanel {
    private static final long serialVersionUID = 1L;

    // Các ô hiển thị & lọc trên Form Thống Kê Doanh Thu (GridLayout 3x3)
    private JTextField txtTuNgay;
    private JTextField txtDenNgay;
    private JComboBox<String> cboKyThongKe;
    private JTextField txtTongSoHoaDon;
    private JTextField txtDoanhThuChoThu;
    private JTextField txtTongTienVAT;
    private JTextField txtHoaDonDangChon;
    private JTextField txtXeVaPhieuDangChon;
    private JTextField txtTongDoanhThuDaThu;

    // Thanh tìm kiếm, bộ lọc & Bảng danh sách
    private JTextField txtTimKiemBang;
    private JComboBox<String> cboLocTrangThai;
    private JTable tableDoanhThu;
    private DefaultTableModel tableModel;

    // Lớp Service xử lý nghiệp vụ thống kê
    private final ThongKeService thongKeService;

    // Bộ nhớ đệm hiển thị tên Khách hàng và Biển số xe
    private final Map<String, String> mapTenKhachHang = new HashMap<>();
    private final Map<String, String> mapBienSoXe = new HashMap<>();

    private boolean dangCapNhatCombo = false;
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private final DateTimeFormatter dtfNgay = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final String PLACEHOLDER_TIM_KIEM = "Tìm kiếm theo mã HĐ, mã phiếu SC, tên khách hoặc biển số xe (Nhấn Enter)...";

    public GaraThongKe() {
        thongKeService = new ThongKeService();

        khoiTaoGiaoDien();
        napBoNhoDemKhachHangVaXe();
        taiDuLieuDoanhThuLenBang("Tất cả", "");
    }

    // Giữ lại constructor có tham số để tránh lỗi biên dịch nếu GaraManHinhChinh chưa kịp sửa
    public GaraThongKe(int tabMacDinh) {
        this();
    }

    private void khoiTaoGiaoDien() {
        setLayout(new BorderLayout());

        // ==================== 3. NỘI DUNG CHÍNH: FORM THỐNG KÊ DOANH THU ====================
        JPanel panelCenter = new JPanel(new BorderLayout());
        panelCenter.setBackground(new Color(225, 230, 235));
        panelCenter.setBorder(BorderFactory.createEmptyBorder(20, 70, 20, 70));

        JPanel panelCardChinh = new JPanel(new BorderLayout());
        panelCardChinh.setBackground(Color.WHITE);
        panelCardChinh.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 215, 220), 1),
                BorderFactory.createEmptyBorder(20, 30, 15, 30)
        ));

        // --- 3.1. Tiêu đề card ---
        JPanel panelCardTop = new JPanel(new BorderLayout());
        panelCardTop.setBackground(Color.WHITE);

        JLabel lblTieuDeCard = new JLabel("THỐNG KÊ DOANH THU");
        lblTieuDeCard.setFont(new Font("Arial", Font.BOLD, 18));
        lblTieuDeCard.setForeground(new Color(30, 35, 45));

        JPanel pnlWindowControls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        pnlWindowControls.setBackground(Color.WHITE);
        JLabel lblMinimize = new JLabel("\u2014");
        JLabel lblMaximize = new JLabel("\u2610");
        pnlWindowControls.add(lblMinimize);
        pnlWindowControls.add(lblMaximize);

        panelCardTop.add(lblTieuDeCard, BorderLayout.WEST);
        panelCardTop.add(pnlWindowControls, BorderLayout.EAST);

        // --- 3.2. Form thông tin tổng hợp doanh thu ---
        JLabel lblMoTa = new JLabel("Tổng hợp số liệu doanh thu từ hóa đơn đã lập. Chọn một dòng dưới bảng để xem nhanh thông tin chi tiết trên form.");
        lblMoTa.setFont(new Font("Arial", Font.PLAIN, 13));
        lblMoTa.setForeground(Color.GRAY);
        lblMoTa.setBorder(BorderFactory.createEmptyBorder(8, 0, 12, 0));

        LocalDate dauThang = LocalDate.now().withDayOfMonth(1);
        txtTuNgay = new JTextField(dauThang.format(dtfNgay));
        txtDenNgay = new JTextField(LocalDate.now().format(dtfNgay));
        cboKyThongKe = new JComboBox<>(new String[]{
                "Tất cả thời gian", "Tháng này", "Năm nay", "Hôm nay"
        });

        txtTongSoHoaDon = taoOTextKhoa("0 hóa đơn", new Color(30, 35, 45), false);
        txtDoanhThuChoThu = taoOTextKhoa("0 đ", new Color(210, 110, 20), true);
        txtTongTienVAT = taoOTextKhoa("0 đ", new Color(30, 35, 45), false);
        txtHoaDonDangChon = taoOTextKhoa("Chưa chọn hóa đơn", new Color(80, 85, 95), false);
        txtXeVaPhieuDangChon = taoOTextKhoa("Chưa chọn phương tiện", new Color(80, 85, 95), false);
        txtTongDoanhThuDaThu = taoOTextKhoa("0 đ", new Color(180, 30, 40), true);
        txtTongDoanhThuDaThu.setFont(new Font("Arial", Font.BOLD, 14));

        cboKyThongKe.addActionListener(e -> {
            if (dangCapNhatCombo) return;
            String ky = cboKyThongKe.getSelectedItem().toString();
            String[] khoangNgay = thongKeService.tinhKhoangNgayTheoKy(ky, dtfNgay);
            if (khoangNgay != null) {
                txtTuNgay.setText(khoangNgay[0]);
                txtDenNgay.setText(khoangNgay[1]);
            }
        });

        JPanel panelGridFields = new JPanel(new GridLayout(3, 3, 25, 10));
        panelGridFields.setBackground(Color.WHITE);
        panelGridFields.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));

        panelGridFields.add(taoOnhapLieu("Từ ngày", txtTuNgay));
        panelGridFields.add(taoOnhapLieu("Đến ngày", txtDenNgay));
        panelGridFields.add(taoOCombo("Kỳ thống kê", cboKyThongKe));
        panelGridFields.add(taoOnhapLieu("Tổng số hóa đơn", txtTongSoHoaDon));
        panelGridFields.add(taoOnhapLieu("Doanh thu chờ thanh toán", txtDoanhThuChoThu));
        panelGridFields.add(taoOnhapLieu("Tổng tiền thuế VAT đã thu", txtTongTienVAT));
        panelGridFields.add(taoOnhapLieu("Hóa đơn & Khách hàng đang chọn", txtHoaDonDangChon));
        panelGridFields.add(taoOnhapLieu("Phương tiện & Mã phiếu SC", txtXeVaPhieuDangChon));
        panelGridFields.add(taoOnhapLieu("Tổng doanh thu đã thu (Gồm VAT)", txtTongDoanhThuDaThu));

        JPanel panelFieldsContainer = new JPanel(new BorderLayout());
        panelFieldsContainer.setBackground(Color.WHITE);
        panelFieldsContainer.add(lblMoTa, BorderLayout.NORTH);
        panelFieldsContainer.add(panelGridFields, BorderLayout.CENTER);

        // --- 3.3. Thanh tìm kiếm và bộ lọc bảng Doanh Thu ---
        JPanel panelSearchFilter = new JPanel(new BorderLayout(15, 0));
        panelSearchFilter.setBackground(new Color(240, 243, 246));
        panelSearchFilter.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));

        txtTimKiemBang = new JTextField(PLACEHOLDER_TIM_KIEM);
        txtTimKiemBang.setFont(new Font("Arial", Font.PLAIN, 12));
        txtTimKiemBang.setForeground(Color.GRAY);
        txtTimKiemBang.setBorder(null);
        txtTimKiemBang.setOpaque(false);

        txtTimKiemBang.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (txtTimKiemBang.getText().equals(PLACEHOLDER_TIM_KIEM)) {
                    txtTimKiemBang.setText("");
                    txtTimKiemBang.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (txtTimKiemBang.getText().trim().isEmpty()) {
                    txtTimKiemBang.setText(PLACEHOLDER_TIM_KIEM);
                    txtTimKiemBang.setForeground(Color.GRAY);
                }
            }
        });

        txtTimKiemBang.addActionListener(e -> {
            String tuKhoa = txtTimKiemBang.getText().trim();
            if (tuKhoa.equals(PLACEHOLDER_TIM_KIEM)) tuKhoa = "";
            taiDuLieuDoanhThuLenBang(cboLocTrangThai.getSelectedItem().toString(), tuKhoa);
        });

        JPanel pnlFilterRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        pnlFilterRight.setOpaque(false);
        JLabel lblTrangThaiLoc = new JLabel("Trạng thái:");
        lblTrangThaiLoc.setFont(new Font("Arial", Font.PLAIN, 12));

        cboLocTrangThai = new JComboBox<>(new String[]{
                "Tất cả", HoaDon.DA_THANH_TOAN, HoaDon.CHUA_THANH_TOAN, HoaDon.DA_HUY
        });
        cboLocTrangThai.setFont(new Font("Arial", Font.PLAIN, 12));
        cboLocTrangThai.addActionListener(e -> {
            String tuKhoa = txtTimKiemBang.getText().trim();
            if (tuKhoa.equals(PLACEHOLDER_TIM_KIEM)) tuKhoa = "";
            taiDuLieuDoanhThuLenBang(cboLocTrangThai.getSelectedItem().toString(), tuKhoa);
        });

        pnlFilterRight.add(lblTrangThaiLoc);
        pnlFilterRight.add(cboLocTrangThai);

        panelSearchFilter.add(txtTimKiemBang, BorderLayout.CENTER);
        panelSearchFilter.add(pnlFilterRight, BorderLayout.EAST);

        // --- 3.4. Bảng Danh Sách Hóa Đơn & Doanh Thu ---
        String[] columnNames = {
                "STT", "Mã Hóa Đơn", "Mã Phiếu SC", "Khách Hàng", "Biển Số Xe",
                "Ngày Lập", "Tạm Tính", "VAT (%)", "Tổng Thanh Toán", "Trạng Thái"
        };
        tableModel = new DefaultTableModel(null, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableDoanhThu = new JTable(tableModel);
        tableDoanhThu.setRowHeight(32);
        tableDoanhThu.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tableDoanhThu.getTableHeader().setBackground(new Color(240, 243, 246));
        tableDoanhThu.setFont(new Font("Arial", Font.PLAIN, 12));
        tableDoanhThu.setSelectionBackground(new Color(40, 100, 150));
        tableDoanhThu.setSelectionForeground(Color.WHITE);
        tableDoanhThu.setShowGrid(false);
        tableDoanhThu.setIntercellSpacing(new Dimension(0, 0));

        tableDoanhThu.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tableDoanhThu.getSelectedRow();
                if (row >= 0) {
                    String maHD = tableModel.getValueAt(row, 1).toString();
                    String maPSC = tableModel.getValueAt(row, 2).toString();
                    String khachHang = tableModel.getValueAt(row, 3).toString();
                    String bienSo = tableModel.getValueAt(row, 4).toString();
                    txtHoaDonDangChon.setText(maHD + " | " + khachHang);
                    txtXeVaPhieuDangChon.setText(bienSo + " | Phiếu: " + maPSC);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(tableDoanhThu);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 233, 236)));

        JPanel panelTableSection = new JPanel(new BorderLayout());
        panelTableSection.setBackground(Color.WHITE);
        panelTableSection.add(panelSearchFilter, BorderLayout.NORTH);
        panelTableSection.add(scrollPane, BorderLayout.CENTER);

        // --- 3.5. Khu vực nút bấm dưới cùng ---
        JPanel panelBottomButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        panelBottomButtons.setBackground(Color.WHITE);

        JButton btnLamMoi = taoNutTrang("Làm mới dữ liệu");
        JButton btnXuatExcel = taoNutDo("Xuất Excel");
        JButton btnMoLapHoaDon = taoNutTrang("Mở Lập hóa đơn");

        btnLamMoi.addActionListener(e -> {
            txtTimKiemBang.setText(PLACEHOLDER_TIM_KIEM);
            txtTimKiemBang.setForeground(Color.GRAY);
            cboLocTrangThai.setSelectedIndex(0);
            napBoNhoDemKhachHangVaXe();
            taiDuLieuDoanhThuLenBang("Tất cả", "");
        });
        btnXuatExcel.addActionListener(e ->
        thongKeService.xuatBangRaExcel(this, tableDoanhThu, "THỐNG KÊ DOANH THU GARA", "ThongKeDoanhThu")
        );

        btnMoLapHoaDon.addActionListener(e -> {
            Window w = SwingUtilities.getWindowAncestor(this);
            if (w instanceof GaraManHinhChinh) {
                ((GaraManHinhChinh) w).chuyenManHinh("LAP_HOA_DON");
            }
        });


        panelBottomButtons.add(btnLamMoi);
        panelBottomButtons.add(btnMoLapHoaDon);
        panelBottomButtons.add(btnXuatExcel);

        // --- Gắn các thành phần vào Card Chính ---
        panelCardChinh.add(panelCardTop, BorderLayout.NORTH);
        JPanel panelCardBody = new JPanel(new BorderLayout(0, 8));
        panelCardBody.setBackground(Color.WHITE);
        panelCardBody.add(panelFieldsContainer, BorderLayout.NORTH);
        panelCardBody.add(panelTableSection, BorderLayout.CENTER);
        panelCardBody.add(panelBottomButtons, BorderLayout.SOUTH);

        panelCardChinh.add(panelCardBody, BorderLayout.CENTER);
        panelCenter.add(panelCardChinh, BorderLayout.CENTER);

        // Menu, toolbar và footer thuộc GaraManHinhChinh.
        add(panelCenter, BorderLayout.CENTER);
    }

    // ==================== XỬ LÝ LOAD DỮ LIỆU QUA SERVICE ====================

    private void napBoNhoDemKhachHangVaXe() {
        mapTenKhachHang.clear();
        mapBienSoXe.clear();
        mapTenKhachHang.putAll(thongKeService.layMapTenKhachHang());
        mapBienSoXe.putAll(thongKeService.layMapBienSoXe());
    }

    private void taiDuLieuDoanhThuLenBang(String trangThaiLoc, String tuKhoa) {
        try {
            tableModel.setRowCount(0);
            KetQuaDoanhThu kq = thongKeService.thongKeDoanhThu(
                    trangThaiLoc, tuKhoa, mapTenKhachHang, mapBienSoXe, dtf);

            for (Object[] row : kq.dsDongBang) {
                tableModel.addRow(row);
            }

            txtTongSoHoaDon.setText(kq.textTongSoHoaDon);
            txtDoanhThuChoThu.setText(kq.textDoanhThuChoThu);
            txtTongTienVAT.setText(kq.textTongTienVAT);
            txtTongDoanhThuDaThu.setText(kq.textTongDoanhThuDaThu);

            if (tableModel.getRowCount() > 0) {
                tableDoanhThu.setRowSelectionInterval(0, 0);
                txtHoaDonDangChon.setText(tableModel.getValueAt(0, 1) + " | " + tableModel.getValueAt(0, 3));
                txtXeVaPhieuDangChon.setText(tableModel.getValueAt(0, 4) + " | Phiếu: " + tableModel.getValueAt(0, 2));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ==================== CÁC HÀM TIỆN ÍCH GIAO DIỆN ====================

    private JTextField taoOTextKhoa(String text, Color fg, boolean inDam) {
        JTextField txt = new JTextField(text);
        txt.setEditable(false);
        txt.setBackground(new Color(245, 247, 250));
        txt.setForeground(fg);
        txt.setFont(new Font("Arial", inDam ? Font.BOLD : Font.PLAIN, 13));
        return txt;
    }

    private JPanel taoOnhapLieu(String labelText, JTextField textField) {
        JPanel panel = new JPanel(new BorderLayout(0, 4));
        panel.setBackground(Color.WHITE);
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("Arial", Font.PLAIN, 12));
        lbl.setForeground(new Color(80, 85, 95));
        textField.setPreferredSize(new Dimension(0, 32));
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 215, 220), 1),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        panel.add(lbl, BorderLayout.NORTH);
        panel.add(textField, BorderLayout.CENTER);
        return panel;
    }

    private JPanel taoOCombo(String labelText, JComboBox<String> comboBox) {
        JPanel panel = new JPanel(new BorderLayout(0, 4));
        panel.setBackground(Color.WHITE);
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("Arial", Font.PLAIN, 12));
        lbl.setForeground(new Color(80, 85, 95));
        comboBox.setFont(new Font("Arial", Font.PLAIN, 13));
        comboBox.setBackground(Color.WHITE);
        comboBox.setPreferredSize(new Dimension(0, 32));
        panel.add(lbl, BorderLayout.NORTH);
        panel.add(comboBox, BorderLayout.CENTER);
        return panel;
    }

    private JButton taoNutTrang(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 12));
        btn.setBackground(Color.WHITE);
        btn.setContentAreaFilled(false);
        btn.setOpaque(true);
        btn.setForeground(new Color(50, 55, 65));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 205, 210), 1),
                BorderFactory.createEmptyBorder(8, 16, 8, 16)
        ));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton taoNutDo(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 12));
        btn.setBackground(new Color(180, 30, 40));
        btn.setContentAreaFilled(false);
        btn.setForeground(Color.WHITE);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(9, 18, 9, 18));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            ConnectDB.getInstance().connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> {
            GaraManHinhChinh frame = new GaraManHinhChinh();
            frame.themManHinh("THONG_KE_DOANH_THU", new GaraThongKe());
            frame.chuyenManHinh("THONG_KE_DOANH_THU");
            frame.setVisible(true);
        });
    }
}