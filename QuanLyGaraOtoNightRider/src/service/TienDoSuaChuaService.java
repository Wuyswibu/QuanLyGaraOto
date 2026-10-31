package service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import connectDB.ConnectDB;
import dao.HangMucDichVuDao;
import dao.PhieuSuaChuaDao;
import entity.HangMucDichVu;
import entity.PhieuSuaChua;

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

    public static class ChiTiet {
        public final PhieuSuaChua phieu;
        public final List<HangMucDichVu> hangMuc;

        public ChiTiet(PhieuSuaChua phieu, List<HangMucDichVu> hangMuc) {
            this.phieu = phieu;
            this.hangMuc = hangMuc;
        }
    }

    public ChiTiet layChiTiet(String maPhieu) throws SQLException {
        synchronized (ConnectDB.class) {
            PhieuSuaChua phieu = batBuocCoPhieu(maPhieu);
            return new ChiTiet(phieu, hangMucDao.timTheoMaPhieuSuaChua(maPhieu));
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
