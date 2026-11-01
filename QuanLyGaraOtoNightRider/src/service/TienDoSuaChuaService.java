package service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import connectDB.ConnectDB;
import dao.HangMucDichVuDao;
import dao.PhieuSuaChuaDao;
import entity.HangMucDichVu;
import entity.PhieuSuaChua;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

/** Nghiệp vụ tiến độ; dùng lại DAO hiện có, không dùng sp_getapplock. */
public class TienDoSuaChuaService {
    public static final String CHUA_BAT_DAU = "Chưa bắt đầu";
    public static final String DANG_THUC_HIEN = "Đang thực hiện";
    public static final String HOAN_THANH = "Hoàn thành";
    public static final String DANG_SUA = "Đang sửa chữa";
    public static final String CHO_KIEM_TRA = "Chờ kiểm tra";
    public static final String DA_HOAN_THANH = "Đã hoàn thành";

    private final PhieuSuaChuaDao phieuDao = new PhieuSuaChuaDao();
    private final HangMucDichVuDao hangMucDao = new HangMucDichVuDao();

    public List<PhieuSuaChua> timPhieu(String tuKhoa) throws SQLException {
        synchronized (ConnectDB.class) {
            return tuKhoa == null || tuKhoa.trim().isEmpty()
                    ? phieuDao.layDanhSachPhieuSuaChua()
                    : phieuDao.timKiem(tuKhoa.trim());
        }
    }

    public static class PhuTungHienThi {
        public final String ten;
        public final int soLuong;
        public final BigDecimal donGia;
        private final BigDecimal thanhTien;

        public PhuTungHienThi(String ten, int soLuong, BigDecimal donGia, BigDecimal thanhTien) {
            this.ten = ten;
            this.soLuong = soLuong;
            this.donGia = donGia;
            this.thanhTien = thanhTien;
        }

        public BigDecimal getThanhTien() {
            return thanhTien;
        }
    }

    public static class ChiTiet {
        public final PhieuSuaChua phieu;
        public final List<HangMucDichVu> hangMuc;
        public final List<PhuTungHienThi> phuTung;
        public final String tenKyThuatVien;

        public ChiTiet(
                PhieuSuaChua phieu,
                List<HangMucDichVu> hangMuc,
                List<PhuTungHienThi> phuTung,
                String tenKyThuatVien) {
            this.phieu = phieu;
            this.hangMuc = hangMuc;
            this.phuTung = phuTung;
            this.tenKyThuatVien = tenKyThuatVien;
        }
    }

    public ChiTiet layChiTiet(String maPhieu) throws SQLException {
        synchronized (ConnectDB.class) {
            Connection conn = ConnectDB.getConnection();

            if (conn == null || conn.isClosed()) {
                throw new SQLException("Chưa kết nối cơ sở dữ liệu.");
            }

            PhieuSuaChua phieu = batBuocCoPhieu(maPhieu);
            String tenKyThuatVien = "Chưa phân công";

            String sqlKTV = "SELECT nv.hoTen "
                    + "FROM dbo.PhieuSuaChua p "
                    + "LEFT JOIN dbo.NhanVien nv "
                    + "ON p.maKyThuatVien = nv.maNhanVien "
                    + "WHERE p.maPhieuSuaChua = ?";

            try (PreparedStatement ps = conn.prepareStatement(sqlKTV)) {
                ps.setString(1, maPhieu);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String hoTen = rs.getString("hoTen");

                        if (hoTen != null) {
                            tenKyThuatVien = hoTen;
                        }
                    }
                }
            }

            List<PhuTungHienThi> phuTung = new ArrayList<>();

            String sqlPhuTung =
                    "SELECT p.maPhuTung, p.tenPhuTung, "
                    + "SUM(ct.soLuong) AS soLuong, "
                    + "CASE WHEN MIN(ct.donGia) = MAX(ct.donGia) "
                    + "THEN MIN(ct.donGia) ELSE NULL END AS donGia, "
                    + "SUM(CAST(ct.soLuong AS DECIMAL(18,0)) "
                    + "* ct.donGia) AS thanhTien "
                    + "FROM dbo.PhieuXuatKho x "
                    + "JOIN dbo.ChiTietPhieuXuat ct "
                    + "ON ct.maPhieuXuat = x.maPhieuXuat "
                    + "JOIN dbo.PhuTung p "
                    + "ON p.maPhuTung = ct.maPhuTung "
                    + "WHERE x.maPhieuSuaChua = ? "
                    + "AND x.trangThai IN (N'Đã xuất kho', N'Đã xác nhận') "
                    + "GROUP BY p.maPhuTung, p.tenPhuTung "
                    + "ORDER BY p.maPhuTung";

            try (PreparedStatement ps = conn.prepareStatement(sqlPhuTung)) {
                ps.setString(1, maPhieu);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                    	phuTung.add(new PhuTungHienThi(
                    	        rs.getString("tenPhuTung"),
                    	        rs.getInt("soLuong"),
                    	        rs.getBigDecimal("donGia"),
                    	        rs.getBigDecimal("thanhTien")));
                    }
                }
            }

            return new ChiTiet(
                    phieu,
                    hangMucDao.timTheoMaPhieuSuaChua(maPhieu),
                    phuTung,
                    tenKyThuatVien);
        }
    }

    public static boolean daDong(String trangThai) {
        return DA_HOAN_THANH.equals(trangThai) || "Chờ bàn giao".equals(trangThai)
                || "Đã giao xe".equals(trangThai) || "Đã hủy".equals(trangThai);
    }

    public static int soHoanThanh(List<HangMucDichVu> danhSach) {
        int so = 0;
        for (HangMucDichVu h : danhSach) {
            if (HOAN_THANH.equals(h.getTrangThai())) so++;
        }
        return so;
    }

    /** Cập nhật theo mã PSC; đồng bộ trạng thái hạng mục trong cùng transaction. */
    public void capNhatTrangThaiPhieu(String maPhieu, String trangThai) throws SQLException {
    	if (DA_HOAN_THANH.equals(trangThai)) {
    	    throw new IllegalArgumentException(
    	            "Vui lòng xác nhận tiền công thực tế trước khi hoàn thành.");
    	}
        if (!Arrays.asList("Chờ sửa chữa", DANG_SUA, CHO_KIEM_TRA, DA_HOAN_THANH).contains(trangThai)) {
            throw new IllegalArgumentException("Trạng thái phiếu không hợp lệ.");
        }
        trongTransaction(() -> {
            PhieuSuaChua phieu = batBuocCoPhieu(maPhieu);
            kiemTraChoSua(phieu);
            List<HangMucDichVu> ds = hangMucDao.timTheoMaPhieuSuaChua(maPhieu);
            if (ds.isEmpty() && (CHO_KIEM_TRA.equals(trangThai) || DA_HOAN_THANH.equals(trangThai))) {
                throw new IllegalArgumentException("Phiếu chưa có hạng mục dịch vụ, chưa thể xác nhận sửa xong.");
            }
            String ttHangMuc = "Chờ sửa chữa".equals(trangThai) ? "Chờ thực hiện"
                    : DANG_SUA.equals(trangThai) ? DANG_THUC_HIEN : HOAN_THANH;
            for (HangMucDichVu h : ds) {
                if (!hangMucDao.capNhatTrangThai(h.getMaHangMuc(), ttHangMuc)) {
                    throw new SQLException("Không đồng bộ được hạng mục " + h.getMaHangMuc());
                }
            }
            if (!phieuDao.capNhatTrangThai(maPhieu, trangThai)) {
                throw new SQLException("Không cập nhật được phiếu " + maPhieu);
            }
        });
    }

    private PhieuSuaChua batBuocCoPhieu(String ma) throws SQLException {
        if (ma == null || ma.trim().isEmpty()) {
            throw new IllegalArgumentException("Vui lòng chọn phiếu sửa chữa.");
        }
        PhieuSuaChua phieu = phieuDao.timTheoMa(ma);
        if (phieu == null) throw new IllegalArgumentException("Phiếu sửa chữa không còn tồn tại.");
        return phieu;
    }

    private void kiemTraChoSua(PhieuSuaChua phieu) {
        if (daDong(phieu.getTrangThai())) {
            throw new IllegalArgumentException("Phiếu đã hoàn thành hoặc kết thúc, không thể sửa tiến độ.");
        }
    }
    
    private BigDecimal kiemTraTienCong(String noiDung, String tenHangMuc) {
        String gia = noiDung == null ? "" : noiDung.trim();

        if (!gia.matches("[0-9]+(?:\\.[0-9]{1,2})?")) {
            throw new IllegalArgumentException(
                    "Tiền công của \"" + tenHangMuc
                    + "\" phải là số không âm, tối đa 2 chữ số thập phân.\n"
                    + "Ví dụ: 150000. Không nhập dấu phân cách hàng nghìn.");
        }

        BigDecimal tien = new BigDecimal(gia);

        if (tien.compareTo(new BigDecimal("9999999999999999.99")) > 0) {
            throw new IllegalArgumentException(
                    "Tiền công của \"" + tenHangMuc + "\" vượt giới hạn.");
        }

        return tien;
    }

    public void hoanThanhPhieu(
            String maPhieu, Map<String, String> tienCongTheoHangMuc)
            throws SQLException {

        if (tienCongTheoHangMuc == null) {
            throw new IllegalArgumentException(
                    "Chưa có thông tin tiền công thực tế.");
        }

        // Giữ bản dữ liệu riêng cho thao tác lưu.
        Map<String, String> duLieu = new LinkedHashMap<>(tienCongTheoHangMuc);

        trongTransaction(() -> {
            PhieuSuaChua phieu = batBuocCoPhieu(maPhieu);
            kiemTraChoSua(phieu);

            List<HangMucDichVu> danhSach =
                    hangMucDao.timTheoMaPhieuSuaChua(maPhieu);

            if (danhSach.isEmpty()) {
                throw new IllegalArgumentException(
                        "Phiếu chưa có hạng mục dịch vụ.");
            }

            if (duLieu.size() != danhSach.size()) {
                throw new IllegalArgumentException(
                        "Danh sách hạng mục đã thay đổi. Hãy tải lại phiếu.");
            }

            Map<String, BigDecimal> tienDaKiemTra = new LinkedHashMap<>();

            // Kiểm tra đầy đủ trước khi ghi xuống database.
            for (HangMucDichVu h : danhSach) {
                String maHangMuc = h.getMaHangMuc();

                if (!duLieu.containsKey(maHangMuc)) {
                    throw new IllegalArgumentException(
                            "Thiếu tiền công của hạng mục: " + h.getTenHangMuc());
                }

                tienDaKiemTra.put(
                        maHangMuc,
                        kiemTraTienCong(
                                duLieu.get(maHangMuc), h.getTenHangMuc()));
            }

            for (HangMucDichVu h : danhSach) {
                boolean thanhCong = hangMucDao.hoanThanhHangMuc(
                        h.getMaHangMuc(),
                        maPhieu,
                        tienDaKiemTra.get(h.getMaHangMuc()));

                if (!thanhCong) {
                    throw new SQLException(
                            "Không lưu được tiền công của: " + h.getTenHangMuc());
                }
            }

            if (!phieuDao.capNhatTrangThai(maPhieu, DA_HOAN_THANH)) {
                throw new SQLException(
                        "Không cập nhật được trạng thái phiếu " + maPhieu);
            }
        });
    }

    @FunctionalInterface
    private interface CongViec { void chay() throws SQLException; }

    /** Hai cập nhật dùng cùng Connection của các DAO, cùng commit hoặc rollback. */
    private void trongTransaction(CongViec congViec) throws SQLException {
        synchronized (ConnectDB.class) {
            Connection c = ConnectDB.getConnection();
            if (c == null || c.isClosed()) throw new SQLException("Chưa kết nối SQL. Hãy chạy từ Main.");
            if (!c.getAutoCommit()) throw new SQLException("Kết nối đang có thao tác khác. Vui lòng thử lại.");
            boolean daKetThuc = false;
            Throwable loiGoc = null;
            try {
                c.setAutoCommit(false);
                congViec.chay();
                c.commit();
                daKetThuc = true;
            } catch (SQLException | RuntimeException ex) {
                loiGoc = ex;
                try {
                    c.rollback();
                    daKetThuc = true;
                } catch (SQLException loiRollback) {
                    ex.addSuppressed(loiRollback);
                    try { c.close(); } catch (SQLException loiDong) { ex.addSuppressed(loiDong); }
                }
                throw ex;
            } finally {
                if (daKetThuc) {
                    try { c.setAutoCommit(true); }
                    catch (SQLException ex) {
                        try { c.close(); } catch (SQLException loiDong) { ex.addSuppressed(loiDong); }
                        if (loiGoc != null) loiGoc.addSuppressed(ex);
                        else ex.printStackTrace(); // Commit đã thành công; không báo lưu thất bại.
                    }
                }
            }
        }
    }
}
