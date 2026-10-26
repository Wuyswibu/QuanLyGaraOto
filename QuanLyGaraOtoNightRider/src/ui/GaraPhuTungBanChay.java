package ui;

import connectDB.ConnectDB;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GaraPhuTungBanChay extends JPanel {
    private static final long serialVersionUID = 1L;

    // Các ô hiển thị trên Form Phụ Tùng Bán Chạy (GridLayout 3x3)
    private JTextField txtHangVaMaPT;
    private JTextField txtTenPhuTung;
    private JTextField txtDonViVaTonKho;
    private JTextField txtSoHoaDonXuat;
    private JTextField txtSoLuongDaBan;
    private JTextField txtDoanhThuMaNay;
    private JTextField txtTop1BanChay;
    private JTextField txtTongSLDaBanAll;
    private JTextField txtTongDoanhThuPhuTung;

    // Thanh tìm kiếm, lọc và Bảng xếp hạng
    private JTextField txtTimKiemBang;
    private JComboBox<String> cboLocTop;
    private JTable tableBanChay;
    private DefaultTableModel tableModel;

    private final String PLACEHOLDER_TIM_KIEM = "Tìm kiếm phụ tùng bán chạy theo mã hoặc tên phụ tùng (Nhấn Enter)...";

    public GaraPhuTungBanChay() {
        damBaoKetNoiCSDL();
        khoiTaoGiaoDien();
        taiDanhSachBanChayLenBang("Tất cả", "");
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

        // ==================== 3. NỘI DUNG CHÍNH: FORM PHỤ TÙNG BÁN CHẠY ====================
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

        JLabel lblTieuDeCard = new JLabel("THỐNG KÊ PHỤ TÙNG BÁN CHẠY");
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

        // --- 3.2. Form thông tin phụ tùng bán chạy ---
        JLabel lblMoTa = new JLabel("Xếp hạng các phụ tùng có sản lượng xuất bán và doanh thu cao nhất từ dữ liệu Chi tiết hóa đơn.");
        lblMoTa.setFont(new Font("Arial", Font.PLAIN, 13));
        lblMoTa.setForeground(Color.GRAY);
        lblMoTa.setBorder(BorderFactory.createEmptyBorder(8, 0, 12, 0));

        txtHangVaMaPT = taoOTextKhoa("", new Color(30, 35, 45), true);
        txtTenPhuTung = taoOTextKhoa("", new Color(30, 35, 45), false);
        txtDonViVaTonKho = taoOTextKhoa("", new Color(80, 85, 95), false);
        txtSoHoaDonXuat = taoOTextKhoa("0 hóa đơn", new Color(80, 85, 95), false);
        txtSoLuongDaBan = taoOTextKhoa("0", new Color(25, 135, 84), true);
        txtDoanhThuMaNay = taoOTextKhoa("0 đ", new Color(30, 35, 45), true);
        txtTop1BanChay = taoOTextKhoa("Chưa có dữ liệu", new Color(180, 30, 40), true);
        txtTongSLDaBanAll = taoOTextKhoa("0 sản phẩm", new Color(30, 35, 45), false);
        txtTongDoanhThuPhuTung = taoOTextKhoa("0 đ", new Color(180, 30, 40), true);
        txtTongDoanhThuPhuTung.setFont(new Font("Arial", Font.BOLD, 14));

        JPanel panelGridFields = new JPanel(new GridLayout(3, 3, 25, 10));
        panelGridFields.setBackground(Color.WHITE);
        panelGridFields.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));

        panelGridFields.add(taoOnhapLieu("Thứ hạng & Mã phụ tùng", txtHangVaMaPT));
        panelGridFields.add(taoOnhapLieu("Tên phụ tùng đang chọn", txtTenPhuTung));
        panelGridFields.add(taoOnhapLieu("Đơn vị tính & SL tồn hiện tại", txtDonViVaTonKho));
        panelGridFields.add(taoOnhapLieu("Số hóa đơn có xuất phụ tùng này", txtSoHoaDonXuat));
        panelGridFields.add(taoOnhapLieu("Tổng số lượng đã bán (Mã đang chọn)", txtSoLuongDaBan));
        panelGridFields.add(taoOnhapLieu("Doanh thu mang lại (Mã đang chọn)", txtDoanhThuMaNay));
        panelGridFields.add(taoOnhapLieu("Phụ tùng dẫn đầu (Top 1)", txtTop1BanChay));
        panelGridFields.add(taoOnhapLieu("Tổng sản lượng phụ tùng đã xuất", txtTongSLDaBanAll));
        panelGridFields.add(taoOnhapLieu("Tổng doanh thu toàn bộ phụ tùng", txtTongDoanhThuPhuTung));

        JPanel panelFieldsContainer = new JPanel(new BorderLayout());
        panelFieldsContainer.setBackground(Color.WHITE);
        panelFieldsContainer.add(lblMoTa, BorderLayout.NORTH);
        panelFieldsContainer.add(panelGridFields, BorderLayout.CENTER);

        // --- 3.3. Thanh tìm kiếm và bộ lọc Top ---
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
            taiDanhSachBanChayLenBang(cboLocTop.getSelectedItem().toString(), tuKhoa);
        });

        JPanel pnlFilterRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        pnlFilterRight.setOpaque(false);
        JLabel lblLoc = new JLabel("Hiển thị:");
        lblLoc.setFont(new Font("Arial", Font.PLAIN, 12));

        cboLocTop = new JComboBox<>(new String[]{
                "Tất cả", "Top 5 bán chạy", "Top 10 bán chạy", "Đã phát sinh bán (> 0)"
        });
        cboLocTop.setFont(new Font("Arial", Font.PLAIN, 12));
        cboLocTop.addActionListener(e -> {
            String tuKhoa = txtTimKiemBang.getText().trim();
            if (tuKhoa.equals(PLACEHOLDER_TIM_KIEM)) tuKhoa = "";
            taiDanhSachBanChayLenBang(cboLocTop.getSelectedItem().toString(), tuKhoa);
        });

        pnlFilterRight.add(lblLoc);
        pnlFilterRight.add(cboLocTop);

        panelSearchFilter.add(txtTimKiemBang, BorderLayout.CENTER);
        panelSearchFilter.add(pnlFilterRight, BorderLayout.EAST);

        // --- 3.4. Bảng Xếp Hạng Phụ Tùng Bán Chạy ---
        String[] columnNames = {
                "Xếp Hạng", "Mã Phụ Tùng", "Tên Phụ Tùng", "ĐVT",
                "Số HĐ Xuất", "Tổng SL Đã Bán", "Đơn Giá Hiện Tại", "Doanh Thu Mang Lại", "SL Tồn Hiện Tại"
        };
        tableModel = new DefaultTableModel(null, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableBanChay = new JTable(tableModel);
        tableBanChay.setRowHeight(32);
        tableBanChay.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tableBanChay.getTableHeader().setBackground(new Color(240, 243, 246));
        tableBanChay.setFont(new Font("Arial", Font.PLAIN, 12));
        tableBanChay.setSelectionBackground(new Color(40, 100, 150));
        tableBanChay.setSelectionForeground(Color.WHITE);
        tableBanChay.setShowGrid(false);
        tableBanChay.setIntercellSpacing(new Dimension(0, 0));

        tableBanChay.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tableBanChay.getSelectedRow();
                if (row >= 0) {
                    hienThiDongBanChayLenForm(row);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(tableBanChay);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 233, 236)));

        JPanel panelTableSection = new JPanel(new BorderLayout());
        panelTableSection.setBackground(Color.WHITE);
        panelTableSection.add(panelSearchFilter, BorderLayout.NORTH);
        panelTableSection.add(scrollPane, BorderLayout.CENTER);

        // --- 3.5. Khu vực nút bấm dưới cùng ---
        JPanel panelBottomButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        panelBottomButtons.setBackground(Color.WHITE);

        JButton btnLamMoi = taoNutTrang("Làm mới dữ liệu");
        JButton btnXuatExcel = taoNutTrang("Xuất Excel");
        JButton btnCapNhat = taoNutDo("Cập nhật bảng xếp hạng");

        btnLamMoi.addActionListener(e -> {
            txtTimKiemBang.setText(PLACEHOLDER_TIM_KIEM);
            txtTimKiemBang.setForeground(Color.GRAY);
            cboLocTop.setSelectedIndex(0);
            taiDanhSachBanChayLenBang("Tất cả", "");
        });

        btnCapNhat.addActionListener(e -> {
            String tuKhoa = txtTimKiemBang.getText().trim();
            if (tuKhoa.equals(PLACEHOLDER_TIM_KIEM)) tuKhoa = "";
            taiDanhSachBanChayLenBang(cboLocTop.getSelectedItem().toString(), tuKhoa);
        });

        panelBottomButtons.add(btnLamMoi);
        panelBottomButtons.add(btnXuatExcel);
        panelBottomButtons.add(btnCapNhat);

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

    private void taiDanhSachBanChayLenBang(String boLocTop, String tuKhoa) {
        tableModel.setRowCount(0);
        Connection conn = ConnectDB.getConnection();
        if (conn == null) return;

        String sql = "SELECT pt.maPhuTung, pt.tenPhuTung, pt.donViTinh, pt.donGia, pt.soLuongTon, "
                + "COUNT(DISTINCT h.maHoaDon) AS soHoaDonXuat, "
                + "ISNULL(SUM(CASE WHEN h.maHoaDon IS NOT NULL THEN ct.soLuong ELSE 0 END), 0) AS tongSLBan, "
                + "ISNULL(SUM(CASE WHEN h.maHoaDon IS NOT NULL "
                + "     THEN (ct.soLuong * ct.donGia - ISNULL(ct.giamGia, 0)) ELSE 0 END), 0) AS doanhThu "
                + "FROM dbo.PhuTung pt "
                + "LEFT JOIN dbo.CTHoaDon ct ON pt.maPhuTung = ct.maPhuTung "
                + "LEFT JOIN dbo.HoaDon h ON ct.maHoaDon = h.maHoaDon AND h.trangThai <> N'Đã hủy' "
                + "WHERE (? = '' OR pt.maPhuTung LIKE ? OR pt.tenPhuTung LIKE ?) "
                + "GROUP BY pt.maPhuTung, pt.tenPhuTung, pt.donViTinh, pt.donGia, pt.soLuongTon "
                + "ORDER BY tongSLBan DESC, doanhThu DESC, pt.maPhuTung ASC";

        String kw = tuKhoa != null ? tuKhoa.trim() : "";
        String likeKw = "%" + kw + "%";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, kw);
            ps.setString(2, likeKw);
            ps.setNString(3, likeKw);

            try (ResultSet rs = ps.executeQuery()) {
                String top1Str = "Chưa có dữ liệu";
                long tongSLAll = 0;
                BigDecimal tongDoanhThuAll = BigDecimal.ZERO;

                int hang = 1;
                while (rs.next()) {
                    String maPT = rs.getString("maPhuTung");
                    String tenPT = rs.getString("tenPhuTung");
                    String dvt = rs.getString("donViTinh");
                    BigDecimal donGia = rs.getBigDecimal("donGia") != null ? rs.getBigDecimal("donGia") : BigDecimal.ZERO;
                    int slTon = rs.getInt("soLuongTon");
                    int soHDXuat = rs.getInt("soHoaDonXuat");
                    long tongSLBan = rs.getLong("tongSLBan");
                    BigDecimal doanhThu = rs.getBigDecimal("doanhThu") != null ? rs.getBigDecimal("doanhThu") : BigDecimal.ZERO;

                    if ("Đã phát sinh bán (> 0)".equals(boLocTop) && tongSLBan <= 0) {
                        continue;
                    }
                    if ("Top 5 bán chạy".equals(boLocTop) && hang > 5) {
                        break;
                    }
                    if ("Top 10 bán chạy".equals(boLocTop) && hang > 10) {
                        break;
                    }

                    if (hang == 1 && tongSLBan > 0) {
                        top1Str = maPT + " - " + tenPT + " (" + tongSLBan + " " + dvt + ")";
                    }

                    tongSLAll += tongSLBan;
                    tongDoanhThuAll = tongDoanhThuAll.add(doanhThu);

                    tableModel.addRow(new Object[]{
                            "Top " + hang++,
                            maPT,
                            tenPT,
                            dvt,
                            soHDXuat,
                            tongSLBan,
                            String.format("%,.0f đ", donGia),
                            String.format("%,.0f đ", doanhThu),
                            slTon
                    });
                }

                txtTop1BanChay.setText(top1Str);
                txtTongSLDaBanAll.setText(String.format("%,d sản phẩm", tongSLAll));
                txtTongDoanhThuPhuTung.setText(String.format("%,.0f đ", tongDoanhThuAll));

                if (tableModel.getRowCount() > 0) {
                    tableBanChay.setRowSelectionInterval(0, 0);
                    hienThiDongBanChayLenForm(0);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void hienThiDongBanChayLenForm(int row) {
        txtHangVaMaPT.setText(tableModel.getValueAt(row, 0) + " | Mã: " + tableModel.getValueAt(row, 1));
        txtTenPhuTung.setText(tableModel.getValueAt(row, 2).toString());
        txtDonViVaTonKho.setText(tableModel.getValueAt(row, 3) + " | Tồn kho: " + tableModel.getValueAt(row, 8));
        txtSoHoaDonXuat.setText(tableModel.getValueAt(row, 4) + " hóa đơn");
        txtSoLuongDaBan.setText(tableModel.getValueAt(row, 5) + " " + tableModel.getValueAt(row, 3));
        txtDoanhThuMaNay.setText(tableModel.getValueAt(row, 7).toString());
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
            frame.themManHinh("PHU_TUNG_BAN_CHAY", new GaraPhuTungBanChay());
            frame.chuyenManHinh("PHU_TUNG_BAN_CHAY");
            frame.setVisible(true);
        });
    }
}