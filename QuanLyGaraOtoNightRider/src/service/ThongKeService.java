package service;

import connectDB.ConnectDB;
import dao.ChiTietHoaDonDao;
import dao.HoaDonDao;
import dao.PhuTungDao;
import entity.ChiTietHoaDon;
import entity.HoaDon;
import entity.PhuTung;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.TableModel;
import java.awt.Component;
import java.awt.Desktop;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ThongKeService {
    private final HoaDonDao hoaDonDao;
    private final ChiTietHoaDonDao ctHoaDonDao;
    private final PhuTungDao phuTungDao;

    public ThongKeService() {
        damBaoKetNoiCSDL();
        this.hoaDonDao = new HoaDonDao();
        this.ctHoaDonDao = new ChiTietHoaDonDao();
        this.phuTungDao = new PhuTungDao();
    }

    public void damBaoKetNoiCSDL() {
        try {
            Connection conn = ConnectDB.getConnection();
            if (conn == null || conn.isClosed()) {
                ConnectDB.getInstance().connect();
            }
        } catch (Exception ignored) {
        }
    }

    // ==================== CÁC CLASS KẾT QUẢ THỐNG KÊ ====================

    public static class KetQuaDoanhThu {
        public final List<Object[]> dsDongBang = new ArrayList<>();
        public String textTongSoHoaDon = "0 hóa đơn";
        public String textDoanhThuChoThu = "0 đ";
        public String textTongTienVAT = "0 đ";
        public String textTongDoanhThuDaThu = "0 đ";
    }

    public static class KetQuaTonKho {
        public final List<Object[]> dsDongBang = new ArrayList<>();
        public String textTongDanhMucVaSL = "0 mã | 0 sản phẩm";
        public String textSoMaSapHetHang = "0 mã cần nhập thêm";
        public String textTongGiaTriTonKho = "0 đ";
    }

    public static class KetQuaBanCham {
        public final List<Object[]> dsDongBang = new ArrayList<>();
        public String textSoMaChuaBan = "0 mã chưa bán";
        public String textTongSLTonDong = "0 sản phẩm";
        public String textTongVonTonDong = "0 đ";
    }

    public static class KetQuaBanChay {
        public final List<Object[]> dsDongBang = new ArrayList<>();
        public String textTop1BanChay = "Chưa có dữ liệu";
        public String textTongSLDaBanAll = "0 sản phẩm";
        public String textTongDoanhThuPhuTung = "0 đ";
    }

    // ==================== 1. NGHIỆP VỤ THỐNG KÊ DOANH THU ====================

    public Map<String, String> layMapTenKhachHang() {
        Map<String, String> mapTenKhachHang = new HashMap<>();
        Connection conn = ConnectDB.getConnection();
        if (conn == null) return mapTenKhachHang;

        try (PreparedStatement ps = conn.prepareStatement("SELECT maKhachHang, hoTen FROM dbo.KhachHang");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                mapTenKhachHang.put(rs.getString("maKhachHang"), rs.getString("hoTen"));
            }
        } catch (SQLException ignored) {
        }
        return mapTenKhachHang;
    }

    public Map<String, String> layMapBienSoXe() {
        Map<String, String> mapBienSoXe = new HashMap<>();
        Connection conn = ConnectDB.getConnection();
        if (conn == null) return mapBienSoXe;

        try (PreparedStatement ps = conn.prepareStatement("SELECT maPhuongTien, bienSo, mauXe FROM dbo.PhuongTien");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String bienSo = rs.getString("bienSo");
                String mauXe = rs.getString("mauXe");
                mapBienSoXe.put(rs.getString("maPhuongTien"),
                        mauXe != null && !mauXe.isEmpty() ? bienSo + " (" + mauXe + ")" : bienSo);
            }
        } catch (SQLException ignored) {
        }
        return mapBienSoXe;
    }

    public String[] tinhKhoangNgayTheoKy(String ky, DateTimeFormatter dtfNgay) {
        LocalDate homNay = LocalDate.now();
        if ("Hôm nay".equals(ky)) {
            return new String[]{homNay.format(dtfNgay), homNay.format(dtfNgay)};
        } else if ("Tháng này".equals(ky)) {
            return new String[]{homNay.withDayOfMonth(1).format(dtfNgay), homNay.format(dtfNgay)};
        } else if ("Năm nay".equals(ky)) {
            return new String[]{homNay.withDayOfYear(1).format(dtfNgay), homNay.format(dtfNgay)};
        }
        return null;
    }

    public KetQuaDoanhThu thongKeDoanhThu(String trangThaiLoc, String tuKhoa,
                                          Map<String, String> mapTenKhachHang,
                                          Map<String, String> mapBienSoXe,
                                          DateTimeFormatter dtf) throws SQLException {
        KetQuaDoanhThu kq = new KetQuaDoanhThu();
        ArrayList<HoaDon> dsHoaDon;
        if (tuKhoa != null && !tuKhoa.trim().isEmpty()) {
            dsHoaDon = hoaDonDao.timKiem(tuKhoa.trim());
        } else if (trangThaiLoc == null || "Tất cả".equals(trangThaiLoc)) {
            dsHoaDon = hoaDonDao.layDanhSachHoaDon();
        } else {
            dsHoaDon = hoaDonDao.timTheoTrangThai(trangThaiLoc);
        }

        Map<String, BigDecimal> mapVat = layTatCaVatTuDB();
        BigDecimal tongDaThu = BigDecimal.ZERO;
        BigDecimal tongChoThu = BigDecimal.ZERO;
        BigDecimal tongTienVAT = BigDecimal.ZERO;
        int soHDDaThu = 0;
        int soHDChoThu = 0;

        int stt = 1;
        for (HoaDon hd : dsHoaDon) {
            if (trangThaiLoc != null && !"Tất cả".equals(trangThaiLoc)
                    && !trangThaiLoc.equalsIgnoreCase(hd.getTrangThai())) {
                continue;
            }

            BigDecimal vat = hd.getVat() != null
                    ? hd.getVat()
                    : mapVat.getOrDefault(hd.getMaHoaDon(), BigDecimal.ZERO);

            BigDecimal tamTinh = tinhTamTinh(hd.getMaHoaDon(), hd.getMaPhieuSuaChua());
            BigDecimal tienVat = tamTinh.multiply(vat).divide(new BigDecimal("100"));
            BigDecimal tongTien = tamTinh.add(tienVat);

            if (HoaDon.DA_THANH_TOAN.equalsIgnoreCase(hd.getTrangThai())) {
                tongDaThu = tongDaThu.add(tongTien);
                tongTienVAT = tongTienVAT.add(tienVat);
                soHDDaThu++;
            } else if (HoaDon.CHUA_THANH_TOAN.equalsIgnoreCase(hd.getTrangThai())) {
                tongChoThu = tongChoThu.add(tongTien);
                soHDChoThu++;
            }

            String tenKH = mapTenKhachHang.getOrDefault(hd.getMaKhachHang(), hd.getMaKhachHang());
            String bienSo = mapBienSoXe.getOrDefault(hd.getMaPhuongTien(), hd.getMaPhuongTien());
            String ngayLapStr = hd.getNgayLap() != null ? hd.getNgayLap().format(dtf) : "";

            kq.dsDongBang.add(new Object[]{
                    stt++,
                    hd.getMaHoaDon(),
                    hd.getMaPhieuSuaChua(),
                    hd.getMaKhachHang() + " - " + tenKH,
                    bienSo,
                    ngayLapStr,
                    String.format("%,.0f đ", tamTinh),
                    vat.stripTrailingZeros().toPlainString() + "%",
                    String.format("%,.0f đ", tongTien),
                    hd.getTrangThai()
            });
        }

        kq.textTongSoHoaDon = (stt - 1) + " hóa đơn (Đã thu: " + soHDDaThu + ", Chờ: " + soHDChoThu + ")";
        kq.textDoanhThuChoThu = String.format("%,.0f đ", tongChoThu);
        kq.textTongTienVAT = String.format("%,.0f đ", tongTienVAT);
        kq.textTongDoanhThuDaThu = String.format("%,.0f đ", tongDaThu);
        return kq;
    }

    public BigDecimal tinhTamTinh(String maHoaDon, String maPSC) {
        BigDecimal tamTinh = BigDecimal.ZERO;
        try {
            ArrayList<ChiTietHoaDon> dsCT = ctHoaDonDao.timTheoMaHoaDon(maHoaDon);
            for (ChiTietHoaDon ct : dsCT) {
                BigDecimal sl = BigDecimal.valueOf(ct.getSoLuong());
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
        } catch (SQLException ignored) {
        }
        return tamTinh;
    }

    public Map<String, BigDecimal> layTatCaVatTuDB() {
        Map<String, BigDecimal> map = new HashMap<>();
        Connection conn = ConnectDB.getConnection();
        if (conn == null) return map;
        String sql = "SELECT maHoaDon, vat FROM dbo.HoaDon";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                map.put(rs.getString("maHoaDon"),
                        rs.getBigDecimal("vat") != null ? rs.getBigDecimal("vat") : BigDecimal.ZERO);
            }
        } catch (SQLException ignored) {
        }
        return map;
    }

    // ==================== 2. NGHIỆP VỤ THỐNG KÊ TỒN KHO ====================

    public KetQuaTonKho thongKeTonKho(String mucTonLoc, String tuKhoa) throws SQLException {
        KetQuaTonKho kq = new KetQuaTonKho();
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

            kq.dsDongBang.add(new Object[]{
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

        kq.textTongDanhMucVaSL = (stt - 1) + " mã | Tổng SL: " + String.format("%,d", tongSLTon);
        kq.textSoMaSapHetHang = soMaCanhBao + " mã (SL tồn <= Tối thiểu)";
        kq.textTongGiaTriTonKho = String.format("%,.0f đ", tongGiaTriTon);
        return kq;
    }

    // ==================== 3. NGHIỆP VỤ THỐNG KÊ PHỤ TÙNG BÁN CHẬM ====================

    public KetQuaBanCham thongKePhuTungBanCham(String tinhTrangLoc, String tuKhoa, DateTimeFormatter dtfNgay) throws SQLException {
        KetQuaBanCham kq = new KetQuaBanCham();
        Connection conn = ConnectDB.getConnection();
        if (conn == null) return kq;

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

                    kq.dsDongBang.add(new Object[]{
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

                kq.textSoMaChuaBan = soMaChuaBan + " mã (SL bán = 0)";
                kq.textTongSLTonDong = String.format("%,d sản phẩm", tongSLTonDong);
                kq.textTongVonTonDong = String.format("%,.0f đ", tongVonDong);
            }
        }
        return kq;
    }

    // ==================== 4. NGHIỆP VỤ THỐNG KÊ PHỤ TÙNG BÁN CHẠY ====================

    public KetQuaBanChay thongKePhuTungBanChay(String boLocTop, String tuKhoa) throws SQLException {
        KetQuaBanChay kq = new KetQuaBanChay();
        Connection conn = ConnectDB.getConnection();
        if (conn == null) return kq;

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

                    kq.dsDongBang.add(new Object[]{
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

                kq.textTop1BanChay = top1Str;
                kq.textTongSLDaBanAll = String.format("%,d sản phẩm", tongSLAll);
                kq.textTongDoanhThuPhuTung = String.format("%,.0f đ", tongDoanhThuAll);
            }
        }
        return kq;
    }
    
 // ==================== 5. NGHIỆP VỤ XUẤT EXCEL (.XLS / .CSV) ====================

    /**
     * Xuất dữ liệu từ JTable ra file Excel (.xls có kẻ bảng định dạng sẵn hoặc .csv chuẩn UTF-8)
     */
    public void xuatBangRaExcel(Component parent, JTable table, String tieuDeBaoCao, String tenFileGoiY) {
        if (table == null || table.getRowCount() == 0) {
            JOptionPane.showMessageDialog(parent,
                    "Không có dữ liệu trong bảng để xuất ra Excel!",
                    "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn nơi lưu file thống kê Excel");
        String thoiGian = LocalDateTime.now().format(DateTimeFormatter.ofPattern("ddMMyyyy_HHmmss"));
        fileChooser.setSelectedFile(new File(tenFileGoiY + "_" + thoiGian + ".xls"));

        FileNameExtensionFilter filterXls = new FileNameExtensionFilter("Microsoft Excel (*.xls)", "xls");
        FileNameExtensionFilter filterCsv = new FileNameExtensionFilter("Excel CSV UTF-8 (*.csv)", "csv");
        fileChooser.addChoosableFileFilter(filterXls);
        fileChooser.addChoosableFileFilter(filterCsv);
        fileChooser.setFileFilter(filterXls);

        int userSelection = fileChooser.showSaveDialog(parent);
        if (userSelection != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File fileToSave = fileChooser.getSelectedFile();
        String filePath = fileToSave.getAbsolutePath();
        boolean laFileCsv = fileChooser.getFileFilter() == filterCsv || filePath.toLowerCase().endsWith(".csv");

        if (!filePath.toLowerCase().endsWith(".xls") && !filePath.toLowerCase().endsWith(".csv")) {
            filePath += laFileCsv ? ".csv" : ".xls";
            fileToSave = new File(filePath);
        }

        if (fileToSave.exists()) {
            int confirm = JOptionPane.showConfirmDialog(parent,
                    "File \"" + fileToSave.getName() + "\" đã tồn tại. Bạn có muốn ghi đè không?",
                    "Xác nhận ghi đè", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }
        }

        TableModel model = table.getModel();
        try (BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(fileToSave), StandardCharsets.UTF_8))) {

            // Ghi BOM UTF-8 để Microsoft Excel nhận diện đúng tiếng Việt có dấu
            bw.write('\uFEFF');

            if (laFileCsv) {
                // Xuất định dạng CSV
                bw.write(tieuDeBaoCao.toUpperCase());
                bw.newLine();
                for (int col = 0; col < model.getColumnCount(); col++) {
                    bw.write("\"" + model.getColumnName(col).replace("\"", "\"\"") + "\"");
                    if (col < model.getColumnCount() - 1) bw.write(",");
                }
                bw.newLine();

                for (int row = 0; row < model.getRowCount(); row++) {
                    for (int col = 0; col < model.getColumnCount(); col++) {
                        Object val = model.getValueAt(row, col);
                        String str = val != null ? val.toString().replace("\"", "\"\"") : "";
                        bw.write("\"" + str + "\"");
                        if (col < model.getColumnCount() - 1) bw.write(",");
                    }
                    bw.newLine();
                }
            } else {
                // Xuất định dạng bảng Excel (.xls) có kẻ khung và tô màu tiêu đề
                bw.write("<html xmlns:x=\"urn:schemas-microsoft-com:office:excel\">");
                bw.write("<head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"></head>");
                bw.write("<body>");
                bw.write("<h2 style=\"font-family: Arial; color: #1E232D;\">" + tieuDeBaoCao.toUpperCase() + "</h2>");
                bw.write("<p style=\"font-family: Arial; font-size: 12px;\">Thời gian xuất: "
                        + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) + "</p>");
                bw.write("<table border=\"1\" style=\"border-collapse: collapse; font-family: Arial; font-size: 12px;\">");

                // Dòng tiêu đề cột
                bw.write("<tr style=\"background-color: #286496; color: #FFFFFF; font-weight: bold; height: 30px;\">");
                for (int col = 0; col < model.getColumnCount(); col++) {
                    bw.write("<th style=\"padding: 6px;\">" + model.getColumnName(col) + "</th>");
                }
                bw.write("</tr>");

                // Các dòng dữ liệu
                for (int row = 0; row < model.getRowCount(); row++) {
                    bw.write("<tr>");
                    for (int col = 0; col < model.getColumnCount(); col++) {
                        Object val = model.getValueAt(row, col);
                        String str = val != null ? val.toString() : "";
                        bw.write("<td style=\"padding: 5px; mso-number-format:'\\@';\">" + str + "</td>");
                    }
                    bw.write("</tr>");
                }
                bw.write("</table></body></html>");
            }

            bw.flush();

            int openConfirm = JOptionPane.showConfirmDialog(parent,
                    "Xuất file Excel thành công!\nĐường dẫn: " + fileToSave.getAbsolutePath() + "\n\nBạn có muốn mở file ngay không?",
                    "Xuất Excel thành công", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);

            if (openConfirm == JOptionPane.YES_OPTION && Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(fileToSave);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(parent,
                    "Lỗi khi xuất file Excel: " + ex.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}