package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;

import connectDB.ConnectDB;
import entity.PhuongThucThanhToan;

public class PhuongThucThanhToanDao {

    private Connection ketNoi() throws SQLException {
        Connection conn = ConnectDB.getConnection();

        if (conn == null || conn.isClosed()) {
            throw new SQLException("Chưa kết nối cơ sở dữ liệu.");
        }

        return conn;
    }

    private PhuongThucThanhToan docThanhToan(ResultSet rs) throws SQLException {
        Timestamp ngayThanhToan = rs.getTimestamp("ngayThanhToan");

        return new PhuongThucThanhToan(
                rs.getString("maThanhToan"),
                rs.getString("maHoaDon"),
                ngayThanhToan == null
                        ? null : ngayThanhToan.toLocalDateTime(),
                rs.getString("phuongThuc"),
                rs.getString("maGiaoDich"),
                rs.getString("trangThai"));
    }

    public ArrayList<PhuongThucThanhToan> layDanhSachThanhToan() throws SQLException {
        ArrayList<PhuongThucThanhToan> danhSach = new ArrayList<>();

        String sql = "SELECT maThanhToan, maHoaDon, ngayThanhToan, "
                + "phuongThuc, maGiaoDich, trangThai "
                + "FROM dbo.PhuongThucThanhToan "
                + "ORDER BY ngayThanhToan DESC, maThanhToan DESC";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                danhSach.add(docThanhToan(rs));
            }
        }

        return danhSach;
    }

    public PhuongThucThanhToan timTheoMa(String maThanhToan) throws SQLException {
        String sql = "SELECT maThanhToan, maHoaDon, ngayThanhToan, "
                + "phuongThuc, maGiaoDich, trangThai "
                + "FROM dbo.PhuongThucThanhToan "
                + "WHERE maThanhToan = ?";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
            ps.setString(1, maThanhToan);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return docThanhToan(rs);
                }
            }
        }

        return null;
    }

    public PhuongThucThanhToan timTheoMaHoaDon(String maHoaDon) throws SQLException {
        String sql = "SELECT maThanhToan, maHoaDon, ngayThanhToan, "
                + "phuongThuc, maGiaoDich, trangThai "
                + "FROM dbo.PhuongThucThanhToan "
                + "WHERE maHoaDon = ?";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
            ps.setString(1, maHoaDon);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return docThanhToan(rs);
                }
            }
        }

        return null;
    }

    public ArrayList<PhuongThucThanhToan> timKiem(String tuKhoa) throws SQLException {
        ArrayList<PhuongThucThanhToan> danhSach = new ArrayList<>();

        String sql = "SELECT maThanhToan, maHoaDon, ngayThanhToan, "
                + "phuongThuc, maGiaoDich, trangThai "
                + "FROM dbo.PhuongThucThanhToan "
                + "WHERE maThanhToan LIKE ? "
                + "OR maHoaDon LIKE ? OR maGiaoDich LIKE ? "
                + "ORDER BY ngayThanhToan DESC, maThanhToan DESC";

        String noiDung = "%" + tuKhoa.trim() + "%";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
            ps.setString(1, noiDung);
            ps.setString(2, noiDung);
            ps.setString(3, noiDung);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    danhSach.add(docThanhToan(rs));
                }
            }
        }

        return danhSach;
    }

    public ArrayList<PhuongThucThanhToan> timTheoPhuongThuc(String phuongThuc) throws SQLException {
        ArrayList<PhuongThucThanhToan> danhSach = new ArrayList<>();

        String sql = "SELECT maThanhToan, maHoaDon, ngayThanhToan, "
                + "phuongThuc, maGiaoDich, trangThai "
                + "FROM dbo.PhuongThucThanhToan "
                + "WHERE phuongThuc = ? "
                + "ORDER BY ngayThanhToan DESC, maThanhToan DESC";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
            ps.setNString(1, phuongThuc);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    danhSach.add(docThanhToan(rs));
                }
            }
        }

        return danhSach;
    }

    public String taoMaThanhToan() throws SQLException {
        String sql = "SELECT MAX(TRY_CONVERT(BIGINT, "
                + "SUBSTRING(maThanhToan, 5, 26))) AS soLonNhat "
                + "FROM dbo.PhuongThucThanhToan "
                + "WHERE maThanhToan LIKE 'PTTT%'";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                long soMoi = rs.getLong("soLonNhat") + 1;

                return String.format(
                        java.util.Locale.ROOT, "PTTT%03d", soMoi);
            }
        }

        return "PTTT001";
    }

    public boolean themThanhToan(PhuongThucThanhToan thanhToan) throws SQLException {
        String sql = "INSERT INTO dbo.PhuongThucThanhToan "
                + "(maThanhToan, maHoaDon, ngayThanhToan, "
                + "phuongThuc, maGiaoDich, trangThai) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
            ps.setString(1, thanhToan.getMaThanhToan());
            ps.setString(2, thanhToan.getMaHoaDon());
            ps.setTimestamp(
                    3, Timestamp.valueOf(thanhToan.getNgayThanhToan()));
            ps.setNString(4, thanhToan.getPhuongThuc());

            if (thanhToan.getMaGiaoDich() == null) {
                ps.setNull(5, Types.VARCHAR);
            } else {
                ps.setString(5, thanhToan.getMaGiaoDich());
            }

            ps.setNString(6, thanhToan.getTrangThai());

            return ps.executeUpdate() > 0;
        }
    }
}