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
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;

public class GaraPhuTungBanCham extends JPanel {
    private static final long serialVersionUID = 1L;

    // Các ô hiển thị trên Form Phụ Tùng Bán Chậm (GridLayout 3x3)
    private JTextField txtHangVaMaPT;
    private JTextField txtTenPhuTung;
    private JTextField txtDonViVaDonGia;
    private JTextField txtSoLuongDaBan;
    private JTextField txtSoLuongTonKho;
    private JTextField txtLanXuatGanNhat;
    private JTextField txtSoMaChuaBan;
    private JTextField txtTongSLTonDong;
    private JTextField txtTongVonTonDong;

    // Thanh tìm kiếm, bộ lọc và Bảng danh sách
    private JTextField txtTimKiemBang;
    private JComboBox<String> cboLocTinhTrang;
    private JTable tableBanCham;
    private DefaultTableModel tableModel;

    private final DateTimeFormatter dtfNgay = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final String PLACEHOLDER_TIM_KIEM = "Tìm kiếm phụ tùng bán chậm / tồn đọng theo mã hoặc tên phụ tùng (Nhấn Enter)...";

    public GaraPhuTungBanCham() {
        damBaoKetNoiCSDL();
        khoiTaoGiaoDien();
        taiDanhSachBanChamLenBang("Tất cả", "");
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

        // ==================== 3. NỘI DUNG CHÍNH: FORM PHỤ TÙNG BÁN CHẬM ====================
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

        JLabel lblTieuDeCard = new JLabel("THỐNG KÊ PHỤ TÙNG BÁN CHẬM");
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

        // --- 3.2. Form thông tin phụ tùng bán chậm ---
        JLabel lblMoTa = new JLabel("Danh sách các phụ tùng ít phát sinh giao dịch hoặc tồn đọng vốn lớn để hỗ trợ cân đối kế hoạch nhập hàng.");
        lblMoTa.setFont(new Font("Arial", Font.PLAIN, 13));
        lblMoTa.setForeground(Color.GRAY);
        lblMoTa.setBorder(BorderFactory.createEmptyBorder(8, 0, 12, 0));

        txtHangVaMaPT = taoOTextKhoa("", new Color(30, 35, 45), true);
        txtTenPhuTung = taoOTextKhoa("", new Color(30, 35, 45), false);
        txtDonViVaDonGia = taoOTextKhoa("", new Color(80, 85, 95), false);
        txtSoLuongDaBan = taoOTextKhoa("0", new Color(210, 110, 20), true);
        txtSoLuongTonKho = taoOTextKhoa("0", new Color(30, 35, 45), true);
        txtLanXuatGanNhat = taoOTextKhoa("", new Color(80, 85, 95), false);
        txtSoMaChuaBan = taoOTextKhoa("0 mã chưa bán", new Color(180, 30, 40), true);
        txtTongSLTonDong = taoOTextKhoa("0 sản phẩm", new Color(30, 35, 45), false);
        txtTongVonTonDong = taoOTextKhoa("0 đ", new Color(180, 30, 40), true);
        txtTongVonTonDong.setFont(new Font("Arial", Font.BOLD, 14));

        JPanel panelGridFields = new JPanel(new GridLayout(3, 3, 25, 10));
        panelGridFields.setBackground(Color.WHITE);
        panelGridFields.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));

        panelGridFields.add(taoOnhapLieu("Xếp hạng & Mã phụ tùng", txtHangVaMaPT));
        panelGridFields.add(taoOnhapLieu("Tên phụ tùng đang chọn", txtTenPhuTung));
        panelGridFields.add(taoOnhapLieu("Đơn vị tính & Đơn giá", txtDonViVaDonGia));
        panelGridFields.add(taoOnhapLieu("Số lượng đã bán (Mã đang chọn)", txtSoLuongDaBan));
        panelGridFields.add(taoOnhapLieu("Số lượng tồn kho hiện tại", txtSoLuongTonKho));
        panelGridFields.add(taoOnhapLieu("Giao dịch xuất gần nhất", txtLanXuatGanNhat));
        panelGridFields.add(taoOnhapLieu("Số mã chưa phát sinh giao dịch", txtSoMaChuaBan));
        panelGridFields.add(taoOnhapLieu("Tổng SL tồn nhóm bán chậm (SL bán <= 2)", txtTongSLTonDong));
        panelGridFields.add(taoOnhapLieu("Tổng giá trị vốn đọng nhóm bán chậm", txtTongVonTonDong));

        JPanel panelFieldsContainer = new JPanel(new BorderLayout());
        panelFieldsContainer.setBackground(Color.WHITE);
        panelFieldsContainer.add(lblMoTa, BorderLayout.NORTH);
        panelFieldsContainer.add(panelGridFields, BorderLayout.CENTER);

        // --- 3.3. Thanh tìm kiếm và lọc ---
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
            taiDanhSachBanChamLenBang(cboLocTinhTrang.getSelectedItem().toString(), tuKhoa);
        });

        JPanel pnlFilterRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        pnlFilterRight.setOpaque(false);
        JLabel lblLoc = new JLabel("Tình trạng:");
        lblLoc.setFont(new Font("Arial", Font.PLAIN, 12));

        cboLocTinhTrang = new JComboBox<>(new String[]{
                "Tất cả", "Chưa bán được (SL bán = 0)", "Bán chậm (SL bán <= 2)"
        });
        cboLocTinhTrang.setFont(new Font("Arial", Font.PLAIN, 12));
        cboLocTinhTrang.addActionListener(e -> {
            String tuKhoa = txtTimKiemBang.getText().trim();
            if (tuKhoa.equals(PLACEHOLDER_TIM_KIEM)) tuKhoa = "";
            taiDanhSachBanChamLenBang(cboLocTinhTrang.getSelectedItem().toString(), tuKhoa);
        });

        pnlFilterRight.add(lblLoc);
        pnlFilterRight.add(cboLocTinhTrang);

        panelSearchFilter.add(txtTimKiemBang, BorderLayout.CENTER);
        panelSearchFilter.add(pnlFilterRight, BorderLayout.EAST);

        // --- 3.4. Bảng Danh Sách Phụ Tùng Bán Chậm ---
        String[] columnNames = {
                "Xếp Hạng", "Mã Phụ Tùng", "Tên Phụ Tùng", "ĐVT",
                "SL Đã Bán", "SL Tồn Kho", "Đơn Giá", "Giá Trị Vốn Đọng", "Lần Xuất Gần Nhất", "Khuyến Nghị"
        };
        tableModel = new DefaultTableModel(null, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableBanCham = new JTable(tableModel);
        tableBanCham.setRowHeight(32);
        tableBanCham.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tableBanCham.getTableHeader().setBackground(new Color(240, 243, 246));
        tableBanCham.setFont(new Font("Arial", Font.PLAIN, 12));
        tableBanCham.setSelectionBackground(new Color(40, 100, 150));
        tableBanCham.setSelectionForeground(Color.WHITE);
        tableBanCham.setShowGrid(false);
        tableBanCham.setIntercellSpacing(new Dimension(0, 0));

        tableBanCham.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tableBanCham.getSelectedRow();
                if (row >= 0) {
                    hienThiDongBanChamLenForm(row);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(tableBanCham);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 233, 236)));

        JPanel panelTableSection = new JPanel(new BorderLayout());
        panelTableSection.setBackground(Color.WHITE);
        panelTableSection.add(panelSearchFilter, BorderLayout.NORTH);
        panelTableSection.add(scrollPane, BorderLayout.CENTER);

        // --- 3.5. Khu vực nút bấm dưới cùng ---
        JPanel panelBottomButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        panelBottomButtons.setBackground(Color.WHITE);

        JButton btnLamMoi = taoNutTrang("Làm mới dữ liệu");
        JButton btnLocChuaBan = taoNutTrang("Lọc mã chưa bán (SL=0)");
        JButton btnXuatExcel = taoNutTrang("Xuất Excel");
        JButton btnCapNhat = taoNutDo("Cập nhật danh sách tồn đọng");

        btnLamMoi.addActionListener(e -> {
            txtTimKiemBang.setText(PLACEHOLDER_TIM_KIEM);
            txtTimKiemBang.setForeground(Color.GRAY);
            cboLocTinhTrang.setSelectedIndex(0);
            taiDanhSachBanChamLenBang("Tất cả", "");
        });

        btnLocChuaBan.addActionListener(e -> cboLocTinhTrang.setSelectedItem("Chưa bán được (SL bán = 0)"));

        btnCapNhat.addActionListener(e -> {
            String tuKhoa = txtTimKiemBang.getText().trim();
            if (tuKhoa.equals(PLACEHOLDER_TIM_KIEM)) tuKhoa = "";
            taiDanhSachBanChamLenBang(cboLocTinhTrang.getSelectedItem().toString(), tuKhoa);
        });

        panelBottomButtons.add(btnLamMoi);
        panelBottomButtons.add(btnLocChuaBan);
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

    private void taiDanhSachBanChamLenBang(String tinhTrangLoc, String tuKhoa) {
        tableModel.setRowCount(0);
        Connection conn = ConnectDB.getConnection();
        if (conn == null) return;

        String sql = "SELECT pt.maPhuTung, pt.tenPhuTung, pt.donViTinh, pt.donGia, pt.soLuongTon, "
                + "ISNULL(SUM(CASE WHEN h.maHoaDon IS NOT NULL THEN ct.soLuong ELSE 0 END), 0) AS tongSLBan, "
                + "MAX(CASE WHEN h.maHoaDon IS NOT NULL THEN h.ngayLap ELSE NULL END) AS lanGiaoDichGanNhat "
                + "FROM dbo.PhuTung pt "
                + "LEFT JOIN dbo.CTHoaDon ct ON pt.maPhuTung = ct.maPhuTung "
                + "LEFT JOIN dbo.HoaDon h ON ct.maHoaDon = h.maHoaDon AND h.trangThai <> N'Đã hủy' "
                + "WHERE (? = '' OR pt.maPhuTung LIKE ? OR pt.tenPhuTung LIKE ?) "
                + "GROUP BY pt.maPhuTung, pt.tenPhuTung, pt.donViTinh, pt.donGia, pt.soLuongTon "
                + "ORDER BY tongSLBan ASC, (pt.soLuongTon * pt.donGia) DESC, pt.soLuongTon DESC";

        String kw = tuKhoa != null ? tuKhoa.trim() : "";
        String likeKw = "%" + kw + "%";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, kw);
            ps.setString(2, likeKw);
            ps.setNString(3, likeKw);

            try (ResultSet rs = ps.executeQuery()) {
                int soMaChuaBan = 0;
                long tongSLTonDong = 0;
                BigDecimal tongVonDong = BigDecimal.ZERO;

                int hang = 1;
                while (rs.next()) {
                    String maPT = rs.getString("maPhuTung");
                    String tenPT = rs.getString("tenPhuTung");
                    String dvt = rs.getString("donViTinh");
                    BigDecimal donGia = rs.getBigDecimal("donGia") != null ? rs.getBigDecimal("donGia") : BigDecimal.ZERO;
                    int slTon = rs.getInt("soLuongTon");
                    long slDaBan = rs.getLong("tongSLBan");
                    Timestamp tsGanNhat = rs.getTimestamp("lanGiaoDichGanNhat");

                    if ("Chưa bán được (SL bán = 0)".equals(tinhTrangLoc) && slDaBan > 0) {
                        continue;
                    }
                    if ("Bán chậm (SL bán <= 2)".equals(tinhTrangLoc) && slDaBan > 2) {
                        continue;
                    }

                    BigDecimal vonDong = donGia.multiply(BigDecimal.valueOf(slTon));
                    if (slDaBan == 0) {
                        soMaChuaBan++;
                    }
                    if (slDaBan <= 2) {
                        tongSLTonDong += slTon;
                        tongVonDong = tongVonDong.add(vonDong);
                    }

                    String ngayGanNhat = tsGanNhat != null
                            ? tsGanNhat.toLocalDateTime().format(dtfNgay)
                            : "Chưa có giao dịch";

                    String khuyenNghi;
                    if (slDaBan == 0 && slTon > 0) {
                        khuyenNghi = "Tồn đọng - Tạm ngừng nhập";
                    } else if (slDaBan <= 2 && slTon > 5) {
                        khuyenNghi = "Luân chuyển chậm";
                    } else {
                        khuyenNghi = "Bình thường";
                    }

                    tableModel.addRow(new Object[]{
                            hang++,
                            maPT,
                            tenPT,
                            dvt,
                            slDaBan,
                            slTon,
                            String.format("%,.0f đ", donGia),
                            String.format("%,.0f đ", vonDong),
                            ngayGanNhat,
                            khuyenNghi
                    });
                }

                txtSoMaChuaBan.setText(soMaChuaBan + " mã (SL bán = 0)");
                txtTongSLTonDong.setText(String.format("%,d sản phẩm", tongSLTonDong));
                txtTongVonTonDong.setText(String.format("%,.0f đ", tongVonDong));

                if (tableModel.getRowCount() > 0) {
                    tableBanCham.setRowSelectionInterval(0, 0);
                    hienThiDongBanChamLenForm(0);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void hienThiDongBanChamLenForm(int row) {
        txtHangVaMaPT.setText("Hạng " + tableModel.getValueAt(row, 0) + " | Mã: " + tableModel.getValueAt(row, 1));
        txtTenPhuTung.setText(tableModel.getValueAt(row, 2).toString());
        txtDonViVaDonGia.setText(tableModel.getValueAt(row, 3) + " | " + tableModel.getValueAt(row, 6));
        txtSoLuongDaBan.setText(tableModel.getValueAt(row, 4) + " " + tableModel.getValueAt(row, 3));
        txtSoLuongTonKho.setText(tableModel.getValueAt(row, 5) + " (Vốn: " + tableModel.getValueAt(row, 7) + ")");
        txtLanXuatGanNhat.setText(tableModel.getValueAt(row, 8) + " | " + tableModel.getValueAt(row, 9));
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
            frame.themManHinh("PHU_TUNG_BAN_CHAM", new GaraPhuTungBanCham());
            frame.chuyenManHinh("PHU_TUNG_BAN_CHAM");
            frame.setVisible(true);
        });
    }
}