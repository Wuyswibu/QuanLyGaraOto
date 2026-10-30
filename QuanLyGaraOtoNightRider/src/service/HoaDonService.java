package service;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import connectDB.ConnectDB;
import dao.ChiTietHoaDonDao;
import dao.HoaDonDao;
import dao.KhachHangDao;
import dao.NhanVienDao;
import dao.PhieuSuaChuaDao;
import dao.XeDao;
import entity.ChiTietHoaDon;
import entity.HoaDon;
import entity.KhachHang;
import entity.NhanVien;
import entity.PhieuSuaChua;
import entity.Xe;

public class HoaDonService {

    private final String BANK_ID = "STB";
    private final String ACCOUNT_NO = "067020062006";
    private final String ACCOUNT_NAME = "TRAN NGO THANH TRONG";
    private final HoaDonDao hoaDonDao;
    private final ChiTietHoaDonDao ctHoaDonDao;
    private final PhieuSuaChuaDao phieuSuaChuaDao;
    private final KhachHangDao khachHangDao;
    private final NhanVienDao nhanVienDao;
    private final XeDao xeDao;

    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss - dd/MM/yyyy");

    public HoaDonService() {
        this.hoaDonDao = new HoaDonDao();
        this.ctHoaDonDao = new ChiTietHoaDonDao();
        this.phieuSuaChuaDao = new PhieuSuaChuaDao();
        this.khachHangDao = new KhachHangDao();
        this.nhanVienDao = new NhanVienDao();
        this.xeDao = new XeDao();
    }

    // ==================== 1. CÁC HÀM LẤY DANH MỤC ====================
    public ArrayList<PhieuSuaChua> layDanhSachPhieuSuaChua() throws SQLException {
        return phieuSuaChuaDao.layDanhSachPhieuSuaChua();
    }

    public ArrayList<KhachHang> layDanhSachKhachHang() throws SQLException {
        return khachHangDao.layDanhSachKhachHang();
    }

    public ArrayList<Xe> layDanhSachXe() throws SQLException {
        return xeDao.layDanhSachXe();
    }

    public ArrayList<NhanVien> layDanhSachNhanVien() throws SQLException {
        return nhanVienDao.layDanhSachNhanVien();
    }

    public KhachHang timKhachHangTheoMa(String maKH) throws SQLException {
    	if (maKH == null || maKH.trim().isEmpty()) return null;
        try {
            return khachHangDao.timTheoMa(maKH.trim());
        } catch (Exception e) {
            // Trả về đối tượng rỗng nếu không tìm thấy để tránh crash ứng dụng
            return null;
        }
    }

    public NhanVien timNhanVienTheoMa(String maNV) throws SQLException {
    	if (maNV == null || maNV.trim().isEmpty()) return null;
        try {
            return nhanVienDao.timTheoMa(maNV.trim());
        } catch (Exception e) {
            // Fallback trực tiếp bằng câu lệnh JDBC an toàn nếu DAO bị lỗi cột tenChuc
            String sql = "SELECT * FROM dbo.NhanVien WHERE maNhanVien = ?";
            try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql)) {
                ps.setString(1, maNV.trim());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        NhanVien nv = new NhanVien();
                        nv.setMaNhanVien(rs.getString("maNhanVien"));
                        try { nv.setHoTen(rs.getString("hoTen")); } catch (Exception ignored) {}
                        return nv;
                    }
                }
            } catch (Exception ignored) {}
            return null;
        }
    }

    public Xe timXeTheoMa(String maPT) throws SQLException {
        for (Xe x : xeDao.layDanhSachXe()) {
            if (x.getMaPhuongTien().equals(maPT)) {
                return x;
            }
        }
        return null;
    }

    // ==================== 2. CÁC HÀM TRUY VẤN & TRA CỨU HÓA ĐƠN ====================
    public String taoMaHoaDonMoi() throws SQLException {
        return hoaDonDao.taoMaHoaDon();
    }

    public HoaDon timHoaDonTheoMa(String maHoaDon) throws SQLException {
        return hoaDonDao.timTheoMa(maHoaDon);
    }

    public HoaDon timHoaDonTheoMaPhieuSuaChua(String maPSC) throws SQLException {
        return hoaDonDao.timTheoMaPhieuSuaChua(maPSC);
    }

    public ArrayList<HoaDon> layDanhSachHoaDonTheoLoc(String trangThaiLoc) throws SQLException {
        if (trangThaiLoc == null || trangThaiLoc.equals("Tất cả")) {
            return hoaDonDao.layDanhSachHoaDon();
        }
        return hoaDonDao.timTheoTrangThai(trangThaiLoc);
    }

    public ArrayList<HoaDon> timKiemHoaDon(String tuKhoa) throws SQLException {
        return hoaDonDao.timKiem(tuKhoa);
    }

    public ArrayList<HoaDon> traCuuHoaDonKetHop(String tuKhoa, String trangThaiChon, String placeholder) throws SQLException {
        if (tuKhoa == null || tuKhoa.trim().isEmpty() || tuKhoa.equals(placeholder)) {
            return layDanhSachHoaDonTheoLoc(trangThaiChon);
        }
        ArrayList<HoaDon> dsTim = hoaDonDao.timKiem(tuKhoa.trim());
        if (trangThaiChon == null || trangThaiChon.equals("Tất cả")) {
            return dsTim;
        }
        ArrayList<HoaDon> dsKetQua = new ArrayList<>();
        for (HoaDon hd : dsTim) {
            if (trangThaiChon.equals(hd.getTrangThai())) {
                dsKetQua.add(hd);
            }
        }
        return dsKetQua;
    }

    public ArrayList<ChiTietHoaDon> layChiTietHoaDon(String maHoaDon) throws SQLException {
        return ctHoaDonDao.timTheoMaHoaDon(maHoaDon);
    }

    // ==================== 3. CÁC HÀM NGHIỆP VỤ LẬP / CẬP NHẬT / THANH TOÁN ====================
    public boolean themHoaDonMoi(HoaDon hdMoi) throws SQLException {
        return hoaDonDao.themHoaDon(hdMoi);
    }

    public boolean capNhatHoaDon(HoaDon hdTonTai, String maPSC, String maKH, String maPT, String maNV, BigDecimal vat, String trangThaiMoi) throws SQLException {
        LocalDateTime ngayLapGoc = hdTonTai.getNgayLap() != null ? hdTonTai.getNgayLap() : LocalDateTime.now();
        HoaDon hdSua = new HoaDon(hdTonTai.getMaHoaDon(), maPSC, maKH, maPT, maNV, ngayLapGoc, vat, hdTonTai.getTrangThai());
        hdSua.setVat(vat);

        boolean kqSua = hoaDonDao.suaHoaDon(hdSua);
        boolean kqTrangThai = true;
        if (!hdTonTai.getTrangThai().equals(trangThaiMoi)) {
            kqTrangThai = hoaDonDao.capNhatTrangThai(hdTonTai.getMaHoaDon(), hdTonTai.getTrangThai(), trangThaiMoi);
        }
        return kqSua || kqTrangThai;
    }

    public boolean xacNhanThanhToan(String maHoaDon, String trangThaiHienTai) throws SQLException {
        return hoaDonDao.capNhatTrangThai(maHoaDon, trangThaiHienTai, HoaDon.DA_THANH_TOAN);
    }

    public String[] layThongTinLienKetTuPhieuSuaChua(String maPSC) {
        String sql = "SELECT p.maKhachHang, p.maPhuongTien, t.maNhanVien "
                + "FROM dbo.PhieuSuaChua s "
                + "JOIN dbo.PhieuTiepNhan t ON s.maTiepNhan = t.maTiepNhan "
                + "JOIN dbo.PhuongTien p ON t.maPhuongTien = p.maPhuongTien "
                + "WHERE s.maPhieuSuaChua = ?";
        try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql)) {
            ps.setString(1, maPSC);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new String[]{
                            rs.getString("maKhachHang"),
                            rs.getString("maPhuongTien"),
                            rs.getString("maNhanVien")
                    };
                }
            }
        } catch (SQLException e) {
            // Bỏ qua nếu bảng PhieuTiepNhan không có cột maNhanVien
        }
        return null;
    }

    // ==================== 4. CÁC HÀM TÍNH TIỀN & HỖ TRỢ TRUY VẤN PHỤ ====================
    public BigDecimal tinhTamTinh(String maHoaDon, String maPSC) {
        BigDecimal tamTinh = BigDecimal.ZERO;
        try {
            ArrayList<ChiTietHoaDon> dsCT = ctHoaDonDao.timTheoMaHoaDon(maHoaDon);
            for (ChiTietHoaDon ct : dsCT) {
                BigDecimal sl = new BigDecimal(ct.getSoLuong());
                BigDecimal dg = ct.getDonGia() != null ? ct.getDonGia() : BigDecimal.ZERO;
                BigDecimal gg = ct.getGiamGia() != null ? ct.getGiamGia() : BigDecimal.ZERO;
                tamTinh = tamTinh.add(dg.multiply(sl).subtract(gg));
            }
            if (tamTinh.compareTo(BigDecimal.ZERO) == 0 && maPSC != null && !maPSC.isEmpty()) {
                String sql = "SELECT ISNULL(SUM(donGia), 0) AS tongPT FROM dbo.PhuTung WHERE maPhieuSuaChua = ?";
                try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql)) {
                    ps.setString(1, maPSC);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next() && rs.getBigDecimal("tongPT") != null) {
                            tamTinh = tamTinh.add(rs.getBigDecimal("tongPT"));
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tamTinh;
    }

    public BigDecimal tinhTongTienHoaDon(String maHoaDon, String maPSC, BigDecimal vat) {
        BigDecimal tamTinh = tinhTamTinh(maHoaDon, maPSC);
        return tinhTongTienTuTamTinh(tamTinh, vat);
    }

    public BigDecimal tinhTongTienTuTamTinh(BigDecimal tamTinh, BigDecimal vat) {
        if (tamTinh == null) tamTinh = BigDecimal.ZERO;
        if (vat == null || vat.compareTo(BigDecimal.ZERO) < 0) vat = BigDecimal.ZERO;
        BigDecimal tienVat = tamTinh.multiply(vat).divide(new BigDecimal("100"));
        return tamTinh.add(tienVat);
    }

    public Map<String, BigDecimal> layTatCaVatTuDB() {
        Map<String, BigDecimal> map = new HashMap<>();
        String sql = "SELECT maHoaDon, vat FROM dbo.HoaDon";
        try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                map.put(rs.getString("maHoaDon"), rs.getBigDecimal("vat") != null ? rs.getBigDecimal("vat") : BigDecimal.ZERO);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return map;
    }

    public String layTenPhuTungTuDB(String maPhuTung) {
        String sql = "SELECT tenPhuTung FROM dbo.PhuTung WHERE maPhuTung = ?";
        try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql)) {
            ps.setString(1, maPhuTung);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("tenPhuTung");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return maPhuTung;
    }

    // ==================== 5. HIỂN THỊ HÓA ĐƠN GIẤY & XUẤT FILE PDF ====================

    /**
     * Mở hộp thoại Hóa đơn giấy (Khi bấm nút "Xem hóa đơn")
     */
    public void hienThiHoaDonGiay(Component parent, String maHoaDon) {
        if (maHoaDon == null || maHoaDon.trim().isEmpty()) {
            JOptionPane.showMessageDialog(parent,
                    "Vui lòng chọn một hóa đơn trong bảng để xem!",
                    "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            HoaDon hd = timHoaDonTheoMa(maHoaDon.trim());
            if (hd == null) {
                JOptionPane.showMessageDialog(parent,
                        "Hóa đơn " + maHoaDon + " chưa được lưu trong CSDL!",
                        "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Window owner = SwingUtilities.getWindowAncestor(parent);
            JDialog dialog = new JDialog(owner, "Chi tiết hóa đơn", Dialog.ModalityType.APPLICATION_MODAL);
            dialog.setLayout(new BorderLayout());

            JPanel panelGiay = taoPanelHoaDonGiay(hd);

            JScrollPane scrollPane = new JScrollPane(panelGiay);
            scrollPane.setBorder(null);
            scrollPane.getVerticalScrollBar().setUnitIncrement(16);
            scrollPane.setPreferredSize(new Dimension(480, 600));

            // Khu vực 2 nút dưới cùng: "Quay lại" và "In hóa đơn" giống mẫu
            JPanel panelBottom = new JPanel(new GridLayout(1, 2, 12, 0));
            panelBottom.setBackground(new Color(252, 249, 242));
            panelBottom.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(225, 220, 210)),
                    new EmptyBorder(12, 24, 14, 24)
            ));

            JButton btnQuayLai = new JButton("Quay lại");
            btnQuayLai.setFont(new Font("Arial", Font.BOLD, 13));
            btnQuayLai.setBackground(new Color(235, 228, 218));
            btnQuayLai.setForeground(new Color(70, 55, 45));
            btnQuayLai.setFocusPainted(false);
            btnQuayLai.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
            btnQuayLai.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnQuayLai.addActionListener(e -> dialog.dispose());

            JButton btnInPDF = new JButton("In hóa đơn");
            btnInPDF.setFont(new Font("Arial", Font.BOLD, 13));
            btnInPDF.setBackground(new Color(95, 65, 50));
            btnInPDF.setForeground(Color.WHITE);
            btnInPDF.setFocusPainted(false);
            btnInPDF.setBorderPainted(false);
            btnInPDF.setOpaque(true);
            btnInPDF.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
            btnInPDF.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnInPDF.addActionListener(e -> xuatHoaDonRaPDF(dialog, hd.getMaHoaDon()));

            panelBottom.add(btnQuayLai);
            panelBottom.add(btnInPDF);

            dialog.add(scrollPane, BorderLayout.CENTER);
            dialog.add(panelBottom, BorderLayout.SOUTH);
            dialog.pack();
            dialog.setLocationRelativeTo(parent);
            dialog.setVisible(true);

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(parent,
                    "Lỗi tải thông tin hóa đơn: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Xuất trực tiếp Hóa đơn giấy ra file PDF (.pdf) khi bấm nút "In hóa đơn"
     */
    public void xuatHoaDonRaPDF(Component parent, String maHoaDon) {
        if (maHoaDon == null || maHoaDon.trim().isEmpty()) {
            JOptionPane.showMessageDialog(parent,
                    "Vui lòng chọn một hóa đơn để xuất PDF!",
                    "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            HoaDon hd = timHoaDonTheoMa(maHoaDon.trim());
            if (hd == null) {
                JOptionPane.showMessageDialog(parent,
                        "Hóa đơn " + maHoaDon + " chưa được lưu vào CSDL. Hãy lưu hóa đơn trước khi in!",
                        "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Lưu hóa đơn PDF");
            fileChooser.setSelectedFile(new File("HoaDon_" + hd.getMaHoaDon() + ".pdf"));
            FileNameExtensionFilter pdfFilter = new FileNameExtensionFilter("PDF Documents (*.pdf)", "pdf");
            fileChooser.addChoosableFileFilter(pdfFilter);
            fileChooser.setFileFilter(pdfFilter);

            int chon = fileChooser.showSaveDialog(parent);
            if (chon != JFileChooser.APPROVE_OPTION) {
                return;
            }

            File filePdf = fileChooser.getSelectedFile();
            if (!filePdf.getName().toLowerCase().endsWith(".pdf")) {
                filePdf = new File(filePdf.getAbsolutePath() + ".pdf");
            }

            if (filePdf.exists()) {
                int overwrite = JOptionPane.showConfirmDialog(parent,
                        "File \"" + filePdf.getName() + "\" đã tồn tại. Bạn có muốn ghi đè không?",
                        "Xác nhận ghi đè", JOptionPane.YES_NO_OPTION);
                if (overwrite != JOptionPane.YES_OPTION) {
                    return;
                }
            }

            JPanel panelGiay = taoPanelHoaDonGiay(hd);
            ghiPanelRaFilePDF(panelGiay, filePdf);

            int openConfirm = JOptionPane.showConfirmDialog(parent,
                    "Xuất hóa đơn PDF thành công!\nĐường dẫn: " + filePdf.getAbsolutePath() + "\n\nBạn có muốn mở file PDF ngay không?",
                    "In hóa đơn PDF", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);

            if (openConfirm == JOptionPane.YES_OPTION && Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(filePdf);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(parent,
                    "Lỗi khi xuất hóa đơn PDF: " + ex.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Dựng giao diện tờ Hóa đơn giấy (Paper Receipt)
     */
    private JPanel taoPanelHoaDonGiay(HoaDon hd) throws SQLException {
        Color bgPaper = new Color(252, 249, 242);
        Color textDark = new Color(40, 35, 30);
        Color textMuted = new Color(120, 115, 110);

        JPanel root = new JPanel();
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
        root.setBackground(bgPaper);
        root.setBorder(new EmptyBorder(24, 30, 24, 30));

        // --- 1. TIÊU ĐỀ GARA & MÃ HÓA ĐƠN ---
        JLabel lblTenGara = new JLabel("GARA Ô TÔ NIGHT INVADER", SwingConstants.CENTER);
        lblTenGara.setFont(new Font("Arial", Font.BOLD, 19));
        lblTenGara.setForeground(textDark);
        lblTenGara.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblPhuDe = new JLabel("Hóa đơn dịch vụ & phụ tùng", SwingConstants.CENTER);
        lblPhuDe.setFont(new Font("Arial", Font.PLAIN, 12));
        lblPhuDe.setForeground(textMuted);
        lblPhuDe.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblMaHD = new JLabel("#" + hd.getMaHoaDon(), SwingConstants.CENTER);
        lblMaHD.setFont(new Font("Arial", Font.BOLD, 13));
        lblMaHD.setForeground(textDark);
        lblMaHD.setAlignmentX(Component.CENTER_ALIGNMENT);

        root.add(lblTenGara);
        root.add(Box.createVerticalStrut(5));
        root.add(lblPhuDe);
        root.add(Box.createVerticalStrut(4));
        root.add(lblMaHD);
        root.add(Box.createVerticalStrut(14));
        root.add(taoDuongKeNgang());
        root.add(Box.createVerticalStrut(12));

        // --- 2. THÔNG TIN CHUNG CỦA HÓA ĐƠN ---
        KhachHang kh = timKhachHangTheoMa(hd.getMaKhachHang());
        NhanVien nv = timNhanVienTheoMa(hd.getMaNhanVien());
        Xe xe = timXeTheoMa(hd.getMaPhuongTien());

        String thoiGianStr = hd.getNgayLap() != null ? hd.getNgayLap().format(dtf) : LocalDateTime.now().format(dtf);
        String nhanVienStr = nv != null ? nv.getHoTen() : hd.getMaNhanVien();
        String khachHangStr = kh != null
                ? kh.getHoTen() + (kh.getSoDienThoai() != null ? " (" + kh.getSoDienThoai() + ")" : "")
                : hd.getMaKhachHang();
        String phuongTienStr = xe != null
                ? xe.getBienSo() + (xe.getMauXe() != null ? " - " + xe.getMauXe() : "")
                : hd.getMaPhuongTien();

        root.add(taoDongThongTin("Thời gian:", thoiGianStr, textMuted, textDark, false));
        root.add(Box.createVerticalStrut(6));
        root.add(taoDongThongTin("Nhân viên:", nhanVienStr, textMuted, textDark, false));
        root.add(Box.createVerticalStrut(6));
        root.add(taoDongThongTin("Khách hàng:", khachHangStr, textMuted, textDark, false));
        root.add(Box.createVerticalStrut(6));
        root.add(taoDongThongTin("Phương tiện:", phuongTienStr, textMuted, textDark, false));
        root.add(Box.createVerticalStrut(6));
        root.add(taoDongThongTin("Phiếu sửa chữa:", hd.getMaPhieuSuaChua(), textMuted, textDark, false));
        root.add(Box.createVerticalStrut(6));
        root.add(taoDongThongTin("Trạng thái:", hd.getTrangThai(), textMuted, textDark, false));

        root.add(Box.createVerticalStrut(12));
        root.add(taoDuongKeNgang());
        root.add(Box.createVerticalStrut(12));

        // --- 3. DANH SÁCH SẢN PHẨM / PHỤ TÙNG ---
        root.add(taoDongThongTin("SẢN PHẨM / PHỤ TÙNG", "THÀNH TIỀN", textMuted, textMuted, true));
        root.add(Box.createVerticalStrut(10));

        ArrayList<ChiTietHoaDon> dsChiTiet = layChiTietHoaDon(hd.getMaHoaDon());
        BigDecimal tongTienHang = BigDecimal.ZERO;
        BigDecimal tongGiamGia = BigDecimal.ZERO;

        if (!dsChiTiet.isEmpty()) {
            for (ChiTietHoaDon ct : dsChiTiet) {
                String tenPT = layTenPhuTungTuDB(ct.getMaPhuTung());
                BigDecimal sl = BigDecimal.valueOf(ct.getSoLuong());
                BigDecimal dg = ct.getDonGia() != null ? ct.getDonGia() : BigDecimal.ZERO;
                BigDecimal gg = ct.getGiamGia() != null ? ct.getGiamGia() : BigDecimal.ZERO;
                BigDecimal tienGoc = dg.multiply(sl);
                BigDecimal thanhTien = tienGoc.subtract(gg);

                tongTienHang = tongTienHang.add(tienGoc);
                tongGiamGia = tongGiamGia.add(gg);

                String moTaSL = ct.getSoLuong() + " x " + String.format("%,.0f đ", dg);
                if (gg.compareTo(BigDecimal.ZERO) > 0) {
                    moTaSL += " (-" + String.format("%,.0f đ", gg) + ")";
                }

                root.add(taoMucPhuTung(tenPT, moTaSL, String.format("%,.0f đ", thanhTien), textDark, textMuted));
                root.add(Box.createVerticalStrut(8));
            }
        } else {
            // Dự phòng lấy từ bảng PhuTung theo maPhieuSuaChua nếu CTHoaDon trống
            String sql = "SELECT tenPhuTung, donGia FROM dbo.PhuTung WHERE maPhieuSuaChua = ?";
            try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql)) {
                ps.setString(1, hd.getMaPhieuSuaChua());
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        String tenPT = rs.getString("tenPhuTung");
                        BigDecimal dg = rs.getBigDecimal("donGia") != null ? rs.getBigDecimal("donGia") : BigDecimal.ZERO;
                        tongTienHang = tongTienHang.add(dg);
                        root.add(taoMucPhuTung(tenPT, "1 x " + String.format("%,.0f đ", dg),
                                String.format("%,.0f đ", dg), textDark, textMuted));
                        root.add(Box.createVerticalStrut(8));
                    }
                }
            }
        }

        BigDecimal tamTinh = tongTienHang.subtract(tongGiamGia);
        BigDecimal vatPercent = hd.getVat() != null
                ? hd.getVat()
                : layTatCaVatTuDB().getOrDefault(hd.getMaHoaDon(), BigDecimal.ZERO);
        BigDecimal tienVat = tamTinh.multiply(vatPercent).divide(new BigDecimal("100"));
        BigDecimal tongCong = tamTinh.add(tienVat);

        root.add(Box.createVerticalStrut(4));
        root.add(taoDuongKeNgang());
        root.add(Box.createVerticalStrut(12));

        // --- 4. TẠM TÍNH, GIẢM GIÁ, VAT & TỔNG CỘNG ---
        root.add(taoDongThongTin("Tạm tính", String.format("%,.0f đ", tongTienHang), textMuted, textDark, false));
        root.add(Box.createVerticalStrut(6));
        root.add(taoDongThongTin("Giảm giá", "-" + String.format("%,.0f đ", tongGiamGia), textMuted, textDark, false));
        root.add(Box.createVerticalStrut(6));
        root.add(taoDongThongTin("VAT (" + vatPercent.stripTrailingZeros().toPlainString() + "%)",
                String.format("%,.0f đ", tienVat), textMuted, textDark, false));

        root.add(Box.createVerticalStrut(12));
        root.add(taoDuongKeNgang());
        root.add(Box.createVerticalStrut(12));

        JPanel pnlTongCong = new JPanel(new BorderLayout());
        pnlTongCong.setOpaque(false);
        pnlTongCong.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        JLabel lblTong = new JLabel("Tổng cộng");
        lblTong.setFont(new Font("Arial", Font.BOLD, 16));
        lblTong.setForeground(textDark);
        JLabel lblGiaTriTong = new JLabel(String.format("%,.0f đ", tongCong));
        lblGiaTriTong.setFont(new Font("Arial", Font.BOLD, 16));
        lblGiaTriTong.setForeground(textDark);
        pnlTongCong.add(lblTong, BorderLayout.WEST);
        pnlTongCong.add(lblGiaTriTong, BorderLayout.EAST);
        root.add(pnlTongCong);
        



        root.add(Box.createVerticalStrut(22));
        JLabel lblCamOn = new JLabel("Cảm ơn quý khách!", SwingConstants.CENTER);
        lblCamOn.setFont(new Font("Arial", Font.BOLD | Font.ITALIC, 12));
        lblCamOn.setForeground(textMuted);
        lblCamOn.setAlignmentX(Component.CENTER_ALIGNMENT);
        root.add(lblCamOn);

        return root;
    }

    private JPanel taoDongThongTin(String labelLeft, String valueRight, Color fgLeft, Color fgRight, boolean inDamCaHai) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22));

        JLabel lblL = new JLabel(labelLeft);
        lblL.setFont(new Font("Arial", inDamCaHai ? Font.BOLD : Font.PLAIN, 12));
        lblL.setForeground(fgLeft);

        JLabel lblR = new JLabel(valueRight);
        lblR.setFont(new Font("Arial", Font.BOLD, 12));
        lblR.setForeground(fgRight);

        row.add(lblL, BorderLayout.WEST);
        row.add(lblR, BorderLayout.EAST);
        return row;
    }

    private JPanel taoMucPhuTung(String tenPT, String soLuongVaDonGia, String thanhTien, Color textDark, Color textMuted) {
        JPanel itemPanel = new JPanel(new BorderLayout(8, 2));
        itemPanel.setOpaque(false);
        itemPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JLabel lblTen = new JLabel(tenPT);
        lblTen.setFont(new Font("Arial", Font.BOLD, 13));
        lblTen.setForeground(textDark);

        JLabel lblThanhTien = new JLabel(thanhTien);
        lblThanhTien.setFont(new Font("Arial", Font.BOLD, 13));
        lblThanhTien.setForeground(textDark);

        JLabel lblSub = new JLabel(soLuongVaDonGia);
        lblSub.setFont(new Font("Arial", Font.PLAIN, 11));
        lblSub.setForeground(textMuted);

        itemPanel.add(lblTen, BorderLayout.WEST);
        itemPanel.add(lblThanhTien, BorderLayout.EAST);
        itemPanel.add(lblSub, BorderLayout.SOUTH);
        return itemPanel;
    }

    private JComponent taoDuongKeNgang() {
        JSeparator sep = new JSeparator(SwingConstants.HORIZONTAL);
        sep.setForeground(new Color(220, 215, 205));
        sep.setBackground(new Color(252, 249, 242));
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
        return sep;
    }

    /**
     * Kết xuất JPanel Hóa đơn giấy ra file PDF chuẩn (Độ phân giải cao 2x, hỗ trợ 100% tiếng Việt)
     */
    private void ghiPanelRaFilePDF(JPanel panel, File filePdf) throws Exception {
        int width = 440;
        panel.setSize(width, Short.MAX_VALUE);
        Dimension pref = panel.getPreferredSize();
        int height = Math.max(pref.height, 560);
        panel.setSize(width, height);
        layoutDeQuy(panel);

        // Render ở độ phân giải gấp đôi (2x DPI) để chữ trên PDF cực kỳ sắc nét
        int scale = 2;
        int imgW = width * scale;
        int imgH = height * scale;
        BufferedImage image = new BufferedImage(imgW, imgH, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.scale(scale, scale);
        panel.printAll(g2);
        g2.dispose();

        // Nén sang luồng JPEG chất lượng cao (0.95f)
        ByteArrayOutputStream jpegStream = new ByteArrayOutputStream();
        ImageWriter jpgWriter = ImageIO.getImageWritersByFormatName("jpg").next();
        ImageWriteParam jpgParam = jpgWriter.getDefaultWriteParam();
        jpgParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        jpgParam.setCompressionQuality(0.95f);
        try (ImageOutputStream ios = ImageIO.createImageOutputStream(jpegStream)) {
            jpgWriter.setOutput(ios);
            jpgWriter.write(null, new IIOImage(image, null, null), jpgParam);
        }
        jpgWriter.dispose();
        byte[] jpegBytes = jpegStream.toByteArray();

        // Đóng gói luồng ảnh vào cấu trúc PDF 1.4 tiêu chuẩn
        ByteArrayOutputStream pdfOut = new ByteArrayOutputStream();
        List<Integer> offsets = new ArrayList<>();

        ghiChuoiPDF(pdfOut, "%PDF-1.4\n");

        // Object 1: Catalog
        offsets.add(pdfOut.size());
        ghiChuoiPDF(pdfOut, "1 0 obj\n<< /Type /Catalog /Pages 2 0 R >>\nendobj\n");

        // Object 2: Pages
        offsets.add(pdfOut.size());
        ghiChuoiPDF(pdfOut, "2 0 obj\n<< /Type /Pages /Kids [3 0 R] /Count 1 >>\nendobj\n");

        // Object 3: Page
        offsets.add(pdfOut.size());
        ghiChuoiPDF(pdfOut, "3 0 obj\n<< /Type /Page /Parent 2 0 R /Resources << /XObject << /Im0 4 0 R >> >> "
                + "/MediaBox [0 0 " + width + " " + height + "] /Contents 5 0 R >>\nendobj\n");

        // Object 4: Image XObject
        offsets.add(pdfOut.size());
        ghiChuoiPDF(pdfOut, "4 0 obj\n<< /Type /XObject /Subtype /Image /Width " + imgW + " /Height " + imgH
                + " /ColorSpace /DeviceRGB /BitsPerComponent 8 /Filter /DCTDecode /Length " + jpegBytes.length
                + " >>\nstream\n");
        pdfOut.write(jpegBytes);
        ghiChuoiPDF(pdfOut, "\nendstream\nendobj\n");

        // Object 5: Content Stream
        String content = "q " + width + " 0 0 " + height + " 0 0 cm /Im0 Do Q\n";
        byte[] contentBytes = content.getBytes(StandardCharsets.US_ASCII);
        offsets.add(pdfOut.size());
        ghiChuoiPDF(pdfOut, "5 0 obj\n<< /Length " + contentBytes.length + " >>\nstream\n");
        pdfOut.write(contentBytes);
        ghiChuoiPDF(pdfOut, "endstream\nendobj\n");

        // Bảng XREF & Trailer
        int xrefOffset = pdfOut.size();
        ghiChuoiPDF(pdfOut, "xref\n0 6\n0000000000 65535 f \n");
        for (int off : offsets) {
            ghiChuoiPDF(pdfOut, String.format("%010d 00000 n \n", off));
        }
        ghiChuoiPDF(pdfOut, "trailer\n<< /Size 6 /Root 1 0 R >>\nstartxref\n" + xrefOffset + "\n%%EOF");

        try (FileOutputStream fos = new FileOutputStream(filePdf)) {
            pdfOut.writeTo(fos);
        }
    }

    private void layoutDeQuy(Container container) {
        container.doLayout();
        for (Component comp : container.getComponents()) {
            if (comp instanceof Container) {
                layoutDeQuy((Container) comp);
            }
        }
    }
    

    private void ghiChuoiPDF(ByteArrayOutputStream out, String str) throws Exception {
        out.write(str.getBytes(StandardCharsets.US_ASCII));
    }
}