package service;

import java.sql.*;
import java.util.*;

import connectDB.ConnectDB;

public class YeuCauPhuTungService {

    private Connection ketNoi() throws SQLException {
        Connection c = ConnectDB.getConnection();

        if (c == null || c.isClosed()) {
            throw new SQLException("Chưa kết nối cơ sở dữ liệu.");
        }

        return c;
    }

    private PreparedStatement lenh(
            Connection c, String sql, Object... giaTri)
            throws SQLException {

        PreparedStatement p = c.prepareStatement(sql);

        for (int i = 0; i < giaTri.length; i++) {
            p.setObject(i + 1, giaTri[i]);
        }

        return p;
    }

    private String layVaiTro(Connection c, String maNV)
            throws SQLException {

        String sql =
                "SELECT q.tenChuc FROM dbo.NhanVien n "
                + "JOIN dbo.PhanQuyen q "
                + "ON q.maPhanQuyen = n.maPhanQuyen "
                + "WHERE n.maNhanVien = ? "
                + "AND n.trangThai = N'Đang làm' "
                + "AND q.choPhep = 1";

        try (PreparedStatement p = lenh(c, sql, maNV);
             ResultSet r = p.executeQuery()) {

            if (!r.next()) {
                throw new IllegalArgumentException(
                        "Nhân viên không có quyền hoạt động.");
            }

            return r.getString(1);
        }
    }

    private void kiemTraPhieu(
            Connection c, String maPSC, String maNV,
            boolean kiemTraPhuTrach) throws SQLException {

        String sql =
                "SELECT trangThai, maKyThuatVien "
                + "FROM dbo.PhieuSuaChua WITH (UPDLOCK, HOLDLOCK) "
                + "WHERE maPhieuSuaChua = ?";

        try (PreparedStatement p = lenh(c, sql, maPSC);
             ResultSet r = p.executeQuery()) {

            if (!r.next()) {
                throw new IllegalArgumentException(
                        "Không tìm thấy phiếu sửa chữa.");
            }

            String trangThai = r.getString("trangThai");

            if (trangThai == null
                    || "Đã hoàn thành".equals(trangThai)
                    || "Đã hủy".equals(trangThai)
                    || "Đã bàn giao".equals(trangThai)
                    || "Đã giao xe".equals(trangThai)) {

                throw new IllegalArgumentException(
                        "Phiếu đã kết thúc, không được yêu cầu hoặc xuất thêm.");
            }

            if (kiemTraPhuTrach
                    && !Objects.equals(
                            maNV, r.getString("maKyThuatVien"))) {

                throw new IllegalArgumentException(
                        "Bạn chỉ được yêu cầu phụ tùng "
                        + "cho phiếu được phân công phụ trách.");
            }
        }
    }

    @FunctionalInterface
    private interface CongViec<T> {
        T chay(Connection c) throws SQLException;
    }

    private <T> T giaoDich(CongViec<T> congViec)
            throws SQLException {

        synchronized (ConnectDB.class) {
            Connection c = ketNoi();

            if (!c.getAutoCommit()) {
                throw new SQLException(
                        "Có giao dịch khác đang chạy. Vui lòng thử lại.");
            }

            c.setAutoCommit(false);

            try {
                T ketQua = congViec.chay(c);
                c.commit();
                return ketQua;

            } catch (SQLException | RuntimeException ex) {
                try {
                    c.rollback();
                } catch (SQLException rollbackError) {
                    ex.addSuppressed(rollbackError);
                }

                throw ex;

            } finally {
                c.setAutoCommit(true);
            }
        }
    }

    public long guiYeuCau(
            String maPSC, String maNV,
            Map<String, Integer> danhSach,
            String ghiChu) throws SQLException {

        if (danhSach == null || danhSach.isEmpty()) {
            throw new IllegalArgumentException(
                    "Thêm ít nhất một phụ tùng.");
        }

        Map<String, Integer> banSao = new TreeMap<>(danhSach);

        for (Map.Entry<String, Integer> dong : banSao.entrySet()) {
            if (dong.getKey() == null
                    || dong.getKey().trim().isEmpty()
                    || dong.getValue() == null
                    || dong.getValue() <= 0) {

                throw new IllegalArgumentException(
                        "Phụ tùng và số lượng không hợp lệ.");
            }
        }

        if (ghiChu != null && ghiChu.trim().length() > 255) {
            throw new IllegalArgumentException(
                    "Ghi chú tối đa 255 ký tự.");
        }

        return giaoDich(c -> {
            String vaiTro = layVaiTro(c, maNV);
            boolean quanLy = "Nhân viên quản lý".equals(vaiTro);

            if (!quanLy && !"Kỹ thuật viên".equals(vaiTro)) {
                throw new IllegalArgumentException(
                        "Chỉ kỹ thuật viên hoặc quản lý được gửi yêu cầu.");
            }

            kiemTraPhieu(c, maPSC, maNV, !quanLy);

            long ma;

            String sql =
                    "INSERT INTO dbo.YeuCauPhuTung "
                    + "(maPhieuSuaChua, maNhanVienYeuCau, ghiChu) "
                    + "OUTPUT INSERTED.maYeuCau VALUES (?, ?, ?)";

            try (PreparedStatement p =
                        lenh(c, sql, maPSC, maNV, ghiChu);
                 ResultSet r = p.executeQuery()) {

                if (!r.next()) {
                    throw new SQLException("Không tạo được yêu cầu.");
                }

                ma = r.getLong(1);
            }

            for (Map.Entry<String, Integer> dong : banSao.entrySet()) {
                try (PreparedStatement p = lenh(c,
                        "INSERT INTO dbo.ChiTietYeuCauPhuTung "
                        + "(maYeuCau, maPhuTung, soLuong) VALUES (?, ?, ?)",
                        ma, dong.getKey(), dong.getValue())) {

                    if (p.executeUpdate() != 1) {
                        throw new SQLException(
                                "Không lưu được chi tiết yêu cầu.");
                    }
                }
            }

            return ma;
        });
    }

    public List<Object[]> layYeuCauCho(String maNV)
            throws SQLException {

        synchronized (ConnectDB.class) {
            Connection c = ketNoi();
            String vaiTro = layVaiTro(c, maNV);

            if (!"Nhân viên kho".equals(vaiTro)
                    && !"Nhân viên quản lý".equals(vaiTro)) {
                throw new IllegalArgumentException(
                        "Bạn không có quyền xử lý yêu cầu xuất kho.");
            }

            List<Object[]> ketQua = new ArrayList<>();

            String sql =
                    "SELECT y.maYeuCau, y.maPhieuSuaChua, "
                    + "n.hoTen, y.ngayYeuCau, y.ghiChu "
                    + "FROM dbo.YeuCauPhuTung y "
                    + "JOIN dbo.NhanVien n "
                    + "ON n.maNhanVien = y.maNhanVienYeuCau "
                    + "WHERE y.trangThai = N'Chờ xử lý' "
                    + "ORDER BY y.ngayYeuCau, y.maYeuCau";

            try (PreparedStatement p = c.prepareStatement(sql);
                 ResultSet r = p.executeQuery()) {

                while (r.next()) {
                    ketQua.add(new Object[]{
                        r.getLong(1), r.getString(2),
                        r.getString(3), r.getTimestamp(4),
                        r.getString(5)
                    });
                }
            }

            return ketQua;
        }
    }

    public List<Object[]> layChiTiet(long ma)
            throws SQLException {

        List<Object[]> ketQua = new ArrayList<>();

        String sql =
                "SELECT d.maPhuTung, p.tenPhuTung, "
                + "d.soLuong, p.soLuongTon "
                + "FROM dbo.ChiTietYeuCauPhuTung d "
                + "JOIN dbo.PhuTung p ON p.maPhuTung = d.maPhuTung "
                + "WHERE d.maYeuCau = ? ORDER BY d.maPhuTung";

        try (PreparedStatement p = lenh(ketNoi(), sql, ma);
             ResultSet r = p.executeQuery()) {

            while (r.next()) {
                ketQua.add(new Object[]{
                    r.getString(1), r.getString(2),
                    r.getInt(3), r.getInt(4)
                });
            }
        }

        return ketQua;
    }

    public String xacNhanXuat(long maYeuCau, String maNV)
            throws SQLException {

        return giaoDich(c -> {
            String vaiTro = layVaiTro(c, maNV);

            if (!"Nhân viên kho".equals(vaiTro)
                    && !"Nhân viên quản lý".equals(vaiTro)) {
                throw new IllegalArgumentException(
                        "Chỉ kho hoặc quản lý được xác nhận xuất.");
            }

            String maPSC;

            try (PreparedStatement p = lenh(c,
                    "SELECT maPhieuSuaChua, trangThai "
                    + "FROM dbo.YeuCauPhuTung WITH (UPDLOCK, HOLDLOCK) "
                    + "WHERE maYeuCau = ?", maYeuCau);
                 ResultSet r = p.executeQuery()) {

                if (!r.next()
                        || !"Chờ xử lý".equals(r.getString("trangThai"))) {
                    throw new IllegalArgumentException(
                            "Yêu cầu không còn ở trạng thái Chờ xử lý.");
                }

                maPSC = r.getString("maPhieuSuaChua");
            }

            kiemTraPhieu(c, maPSC, maNV, false);

            String maPX;

            try (PreparedStatement p = c.prepareStatement(
                    "SELECT MAX(TRY_CONVERT(BIGINT, "
                    + "SUBSTRING(maPhieuXuat, 4, 27))) "
                    + "FROM dbo.PhieuXuatKho WITH (TABLOCKX, HOLDLOCK) "
                    + "WHERE maPhieuXuat LIKE 'PXK%'");
                 ResultSet r = p.executeQuery()) {

                r.next();
                maPX = String.format(
                        Locale.ROOT, "PXK%03d", r.getLong(1) + 1);
            }

            try (PreparedStatement p = lenh(c,
                    "INSERT INTO dbo.PhieuXuatKho "
                    + "(maPhieuXuat, maPhieuSuaChua, ngayXuat, "
                    + "lyDoXuat, trangThai, ghiChu) "
                    + "VALUES (?, ?, SYSDATETIME(), "
                    + "N'Xuất sửa chữa', N'Đã xuất kho', ?)",
                    maPX, maPSC, "Theo yêu cầu phụ tùng #" + maYeuCau)) {

                if (p.executeUpdate() != 1) {
                    throw new SQLException("Không tạo được phiếu xuất.");
                }
            }

            int soDong = 0;

            try (PreparedStatement p = lenh(c,
                    "SELECT maPhuTung, soLuong "
                    + "FROM dbo.ChiTietYeuCauPhuTung "
                    + "WHERE maYeuCau = ? ORDER BY maPhuTung", maYeuCau);
                 ResultSet r = p.executeQuery()) {

                // Đọc hết danh sách trước khi thực hiện các lệnh UPDATE.
                List<Object[]> chiTiet = new ArrayList<>();

                while (r.next()) {
                    chiTiet.add(new Object[]{
                        r.getString(1), r.getInt(2)
                    });
                }

                for (Object[] dong : chiTiet) {
                    String maPT = (String) dong[0];
                    int sl = (Integer) dong[1];

                    try (PreparedStatement giam = lenh(c,
                            "UPDATE dbo.PhuTung "
                            + "SET soLuongTon = soLuongTon - ? "
                            + "WHERE maPhuTung = ? AND soLuongTon >= ?",
                            sl, maPT, sl)) {

                        if (giam.executeUpdate() != 1) {
                            throw new IllegalArgumentException(
                                    "Phụ tùng " + maPT
                                    + " không đủ tồn. Chưa xuất yêu cầu.");
                        }
                    }

                    try (PreparedStatement them = lenh(c,
                            "INSERT INTO dbo.ChiTietPhieuXuat "
                            + "(maPhieuXuat, maPhuTung, soLuong, donGia) "
                            + "SELECT ?, maPhuTung, ?, donGia "
                            + "FROM dbo.PhuTung WHERE maPhuTung = ?",
                            maPX, sl, maPT)) {

                        if (them.executeUpdate() != 1) {
                            throw new SQLException(
                                    "Không lưu được chi tiết xuất.");
                        }
                    }

                    soDong++;
                }
            }

            if (soDong == 0) {
                throw new IllegalArgumentException(
                        "Yêu cầu chưa có phụ tùng.");
            }

            try (PreparedStatement p = lenh(c,
                    "UPDATE dbo.YeuCauPhuTung "
                    + "SET trangThai = N'Đã xuất', "
                    + "maPhieuXuat = ?, maNhanVienXuat = ? "
                    + "WHERE maYeuCau = ? AND trangThai = N'Chờ xử lý'",
                    maPX, maNV, maYeuCau)) {

                if (p.executeUpdate() != 1) {
                    throw new SQLException(
                            "Không cập nhật được yêu cầu.");
                }
            }

            return maPX;
        });
    }
}