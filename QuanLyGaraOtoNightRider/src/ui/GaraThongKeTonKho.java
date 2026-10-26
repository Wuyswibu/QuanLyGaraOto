package ui;

import connectDB.ConnectDB;
import dao.PhuTungDao;
import entity.PhuTung;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

public class GaraThongKeTonKho extends JPanel {
    private static final long serialVersionUID = 1L;

    // Các ô hiển thị trên Form Thống Kê Tồn Kho (GridLayout 3x3)
    private JTextField txtMaPhuTung;
    private JTextField txtTenPhuTung;
    private JTextField txtDonViVaDonGia;
    private JTextField txtSoLuongTon;
    private JTextField txtMucTonToiThieu;
    private JTextField txtDanhGiaMucTon;
    private JTextField txtTongDanhMucVaSL;
    private JTextField txtSoMaSapHetHang;
    private JTextField txtTongGiaTriTonKho;

    // Thanh tìm kiếm, lọc và Bảng tồn kho
    private JTextField txtTimKiemBang;
    private JComboBox<String> cboLocMucTon;
    private JTable tableTonKho;
    private DefaultTableModel tableModel;

    private final PhuTungDao phuTungDao;
    private final String PLACEHOLDER_TIM_KIEM = "Tìm kiếm theo mã phụ tùng hoặc tên phụ tùng / vật tư (Nhấn Enter)...";

    public GaraThongKeTonKho() {
        damBaoKetNoiCSDL();
        phuTungDao = new PhuTungDao();

        khoiTaoGiaoDien();
        taiDanhSachTonKhoLenBang("Tất cả", "");
    }

    private void damBaoKetNoiCSDL() {
        try {
            Connection conn = ConnectDB.getConnection();
            if (conn == null || conn.isClosed()) {
                ConnectDB.getInstance().connect();
            }
        } catch (Exception ignored) {
        }
    }

    private void khoiTaoGiaoDien() {
        setLayout(new BorderLayout());

        // ==================== 3. NỘI DUNG CHÍNH: FORM THỐNG KÊ TỒN KHO ====================
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

        JLabel lblTieuDeCard = new JLabel("THỐNG KÊ TỒN KHO PHỤ TÙNG");
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

        // --- 3.2. Form thông tin tồn kho ---
        JLabel lblMoTa = new JLabel("Theo dõi số lượng tồn kho, cảnh báo mức tồn tối thiểu (Min-stock) và tổng giá trị vốn phụ tùng trong kho.");
        lblMoTa.setFont(new Font("Arial", Font.PLAIN, 13));
        lblMoTa.setForeground(Color.GRAY);
        lblMoTa.setBorder(BorderFactory.createEmptyBorder(8, 0, 12, 0));

        txtMaPhuTung = taoOTextKhoa("", new Color(30, 35, 45), true);
        txtTenPhuTung = taoOTextKhoa("", new Color(30, 35, 45), false);
        txtDonViVaDonGia = taoOTextKhoa("", new Color(80, 85, 95), false);
        txtSoLuongTon = taoOTextKhoa("0", new Color(30, 35, 45), true);
        txtMucTonToiThieu = taoOTextKhoa("0", new Color(80, 85, 95), false);
        txtDanhGiaMucTon = taoOTextKhoa("", new Color(25, 135, 84), true);
        txtTongDanhMucVaSL = taoOTextKhoa("0 mã | 0 sản phẩm", new Color(30, 35, 45), false);
        txtSoMaSapHetHang = taoOTextKhoa("0 mã cần nhập thêm", new Color(210, 110, 20), true);
        txtTongGiaTriTonKho = taoOTextKhoa("0 đ", new Color(180, 30, 40), true);
        txtTongGiaTriTonKho.setFont(new Font("Arial", Font.BOLD, 14));

        JPanel panelGridFields = new JPanel(new GridLayout(3, 3, 25, 10));
        panelGridFields.setBackground(Color.WHITE);
        panelGridFields.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));

        panelGridFields.add(taoOnhapLieu("Mã phụ tùng đang chọn", txtMaPhuTung));
        panelGridFields.add(taoOnhapLieu("Tên phụ tùng / vật tư", txtTenPhuTung));
        panelGridFields.add(taoOnhapLieu("Đơn vị tính & Đơn giá niêm yết", txtDonViVaDonGia));
        panelGridFields.add(taoOnhapLieu("Số lượng tồn hiện tại", txtSoLuongTon));
        panelGridFields.add(taoOnhapLieu("Mức tồn tối thiểu (Min-stock)", txtMucTonToiThieu));
        panelGridFields.add(taoOnhapLieu("Đánh giá mức tồn kho", txtDanhGiaMucTon));
        panelGridFields.add(taoOnhapLieu("Tổng danh mục & Tổng SL tồn", txtTongDanhMucVaSL));
        panelGridFields.add(taoOnhapLieu("Cảnh báo sắp hết / Hết hàng", txtSoMaSapHetHang));
        panelGridFields.add(taoOnhapLieu("Tổng giá trị tồn kho hiện tại", txtTongGiaTriTonKho));

        JPanel panelFieldsContainer = new JPanel(new BorderLayout());
        panelFieldsContainer.setBackground(Color.WHITE);
        panelFieldsContainer.add(lblMoTa, BorderLayout.NORTH);
        panelFieldsContainer.add(panelGridFields, BorderLayout.CENTER);

        // --- 3.3. Thanh tìm kiếm và lọc mức tồn ---
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
            taiDanhSachTonKhoLenBang(cboLocMucTon.getSelectedItem().toString(), tuKhoa);
        });

        JPanel pnlFilterRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        pnlFilterRight.setOpaque(false);
        JLabel lblLoc = new JLabel("Mức tồn kho:");
        lblLoc.setFont(new Font("Arial", Font.PLAIN, 12));

        cboLocMucTon = new JComboBox<>(new String[]{
                "Tất cả", "Đủ hàng", "Sắp hết hàng", "Hết hàng"
        });
        cboLocMucTon.setFont(new Font("Arial", Font.PLAIN, 12));
        cboLocMucTon.addActionListener(e -> {
            String tuKhoa = txtTimKiemBang.getText().trim();
            if (tuKhoa.equals(PLACEHOLDER_TIM_KIEM)) tuKhoa = "";
            taiDanhSachTonKhoLenBang(cboLocMucTon.getSelectedItem().toString(), tuKhoa);
        });

        pnlFilterRight.add(lblLoc);
        pnlFilterRight.add(cboLocMucTon);

        panelSearchFilter.add(txtTimKiemBang, BorderLayout.CENTER);
        panelSearchFilter.add(pnlFilterRight, BorderLayout.EAST);

        // --- 3.4. Bảng Danh Sách Tồn Kho ---
        String[] columnNames = {
                "STT", "Mã Phụ Tùng", "Tên Phụ Tùng / Vật Tư", "ĐVT",
                "SL Tồn", "Tồn Tối Thiểu", "Đơn Giá", "Thành Tiền Tồn", "Mức Độ Tồn", "Trạng Thái KD"
        };
        tableModel = new DefaultTableModel(null, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableTonKho = new JTable(tableModel);
        tableTonKho.setRowHeight(32);
        tableTonKho.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tableTonKho.getTableHeader().setBackground(new Color(240, 243, 246));
        tableTonKho.setFont(new Font("Arial", Font.PLAIN, 12));
        tableTonKho.setSelectionBackground(new Color(40, 100, 150));
        tableTonKho.setSelectionForeground(Color.WHITE);
        tableTonKho.setShowGrid(false);
        tableTonKho.setIntercellSpacing(new Dimension(0, 0));

        tableTonKho.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tableTonKho.getSelectedRow();
                if (row >= 0) {
                    hienThiDongTonKhoLenForm(row);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(tableTonKho);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 233, 236)));

        JPanel panelTableSection = new JPanel(new BorderLayout());
        panelTableSection.setBackground(Color.WHITE);
        panelTableSection.add(panelSearchFilter, BorderLayout.NORTH);
        panelTableSection.add(scrollPane, BorderLayout.CENTER);

        // --- 3.5. Khu vực nút bấm dưới cùng ---
        JPanel panelBottomButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        panelBottomButtons.setBackground(Color.WHITE);

        JButton btnLamMoi = taoNutTrang("Làm mới dữ liệu");
        JButton btnCanhBao = taoNutTrang("Xem mã sắp hết hàng");
        JButton btnXuatExcel = taoNutTrang("Xuất Excel");
        JButton btnMoNhapKho = taoNutDo("Lập phiếu nhập kho");

        btnLamMoi.addActionListener(e -> {
            txtTimKiemBang.setText(PLACEHOLDER_TIM_KIEM);
            txtTimKiemBang.setForeground(Color.GRAY);
            cboLocMucTon.setSelectedIndex(0);
            taiDanhSachTonKhoLenBang("Tất cả", "");
        });

        btnCanhBao.addActionListener(e -> cboLocMucTon.setSelectedItem("Sắp hết hàng"));

        btnMoNhapKho.addActionListener(e -> {
            Window w = SwingUtilities.getWindowAncestor(this);
            if (w instanceof GaraManHinhChinh) {
                ((GaraManHinhChinh) w).chuyenManHinh("NHAP_KHO");
            }
        });

        panelBottomButtons.add(btnLamMoi);
        panelBottomButtons.add(btnCanhBao);
        panelBottomButtons.add(btnXuatExcel);
        panelBottomButtons.add(btnMoNhapKho);

        // --- Gắn vào Card Chính ---
        panelCardChinh.add(panelCardTop, BorderLayout.NORTH);
        JPanel panelCardBody = new JPanel(new BorderLayout(0, 8));
        panelCardBody.setBackground(Color.WHITE);
        panelCardBody.add(panelFieldsContainer, BorderLayout.NORTH);
        panelCardBody.add(panelTableSection, BorderLayout.CENTER);
        panelCardBody.add(panelBottomButtons, BorderLayout.SOUTH);

        panelCardChinh.add(panelCardBody, BorderLayout.CENTER);
        panelCenter.add(panelCardChinh, BorderLayout.CENTER);

        add(panelCenter, BorderLayout.CENTER);
    }

    // ==================== XỬ LÝ LOAD DỮ LIỆU DATABASE ====================

    private void taiDanhSachTonKhoLenBang(String mucTonLoc, String tuKhoa) {
        try {
            tableModel.setRowCount(0);
            ArrayList<PhuTung> dsPhuTung;
            if (tuKhoa != null && !tuKhoa.trim().isEmpty()) {
                dsPhuTung = phuTungDao.timKiem(tuKhoa.trim());
            } else {
                dsPhuTung = phuTungDao.layDanhSachPhuTung();
            }

            long tongSLTon = 0;
            BigDecimal tongGiaTriTon = BigDecimal.ZERO;
            int soMaCanhBao = 0;

            int stt = 1;
            for (PhuTung pt : dsPhuTung) {
                int slTon = pt.getSoLuongTon();
                int tonToiThieu = pt.getMucTonToiThieu();
                BigDecimal donGia = pt.getDonGia() != null ? pt.getDonGia() : BigDecimal.ZERO;
                BigDecimal thanhTienTon = donGia.multiply(BigDecimal.valueOf(slTon));

                String mucDoTon;
                if (slTon <= 0) {
                    mucDoTon = "Hết hàng";
                    soMaCanhBao++;
                } else if (slTon <= tonToiThieu) {
                    mucDoTon = "Sắp hết hàng";
                    soMaCanhBao++;
                } else {
                    mucDoTon = "Đủ hàng";
                }

                if (mucTonLoc != null && !"Tất cả".equals(mucTonLoc)) {
                    if ("Sắp hết hàng".equals(mucTonLoc)) {
                        if (slTon > tonToiThieu) continue;
                    } else if (!mucTonLoc.equalsIgnoreCase(mucDoTon)) {
                        continue;
                    }
                }

                tongSLTon += slTon;
                tongGiaTriTon = tongGiaTriTon.add(thanhTienTon);

                tableModel.addRow(new Object[]{
                        stt++,
                        pt.getMaPhuTung(),
                        pt.getTenPhuTung(),
                        pt.getDonViTinh(),
                        slTon,
                        tonToiThieu,
                        String.format("%,.0f đ", donGia),
                        String.format("%,.0f đ", thanhTienTon),
                        mucDoTon,
                        pt.getTrangThai()
                });
            }

            txtTongDanhMucVaSL.setText((stt - 1) + " mã | Tổng SL: " + String.format("%,d", tongSLTon));
            txtSoMaSapHetHang.setText(soMaCanhBao + " mã (SL tồn <= Tối thiểu)");
            txtTongGiaTriTonKho.setText(String.format("%,.0f đ", tongGiaTriTon));

            if (tableModel.getRowCount() > 0) {
                tableTonKho.setRowSelectionInterval(0, 0);
                hienThiDongTonKhoLenForm(0);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void hienThiDongTonKhoLenForm(int row) {
        txtMaPhuTung.setText(tableModel.getValueAt(row, 1).toString());
        txtTenPhuTung.setText(tableModel.getValueAt(row, 2).toString());
        txtDonViVaDonGia.setText(tableModel.getValueAt(row, 3) + " | " + tableModel.getValueAt(row, 6));
        txtSoLuongTon.setText(tableModel.getValueAt(row, 4).toString());
        txtMucTonToiThieu.setText(tableModel.getValueAt(row, 5).toString());
        txtDanhGiaMucTon.setText(tableModel.getValueAt(row, 8) + " (" + tableModel.getValueAt(row, 9) + ")");
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
            frame.themManHinh("THONG_KE_TON_KHO", new GaraThongKeTonKho());
            frame.chuyenManHinh("THONG_KE_TON_KHO");
            frame.setVisible(true);
        });
    }
}