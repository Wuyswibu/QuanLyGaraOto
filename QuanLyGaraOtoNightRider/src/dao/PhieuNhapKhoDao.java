package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;

import connectDB.ConnectDB;
import entity.PhieuNhapKho;

public class PhieuNhapKhoDao {

    private Connection ketNoi() throws SQLException {
        Connection conn = ConnectDB.getConnection();

        if (conn == null || conn.isClosed()) {
            throw new SQLException("Chưa kết nối cơ sở dữ liệu.");
        }

        return conn;
    }

    private PhieuNhapKho docPhieuNhap(ResultSet rs) throws SQLException {
        Timestamp ngayNhap = rs.getTimestamp("ngayNhap");

        return new PhieuNhapKho(
                rs.getString("maPhieuNhap"),
                ngayNhap == null ? null : ngayNhap.toLocalDateTime(),
                rs.getString("trangThai"),
                rs.getString("ghiChu"));
    }

    public ArrayList<PhieuNhapKho> layDanhSachPhieuNhap() throws SQLException {
        ArrayList<PhieuNhapKho> danhSach = new ArrayList<>();

        String sql = "SELECT maPhieuNhap, ngayNhap, trangThai, ghiChu "
                + "FROM dbo.PhieuNhapKho "
                + "ORDER BY ngayNhap DESC, maPhieuNhap DESC";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql);
        ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                danhSach.add(docPhieuNhap(rs));
            }
        }

        return danhSach;
    }

    public PhieuNhapKho timTheoMa(String maPhieuNhap) throws SQLException {
        String sql = "SELECT maPhieuNhap, ngayNhap, trangThai, ghiChu "
                + "FROM dbo.PhieuNhapKho WHERE maPhieuNhap = ?";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
            ps.setString(1, maPhieuNhap);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return docPhieuNhap(rs);
                }
            }
        }

        return null;
    }

    public ArrayList<PhieuNhapKho> timKiem(String tuKhoa) throws SQLException {
        ArrayList<PhieuNhapKho> danhSach = new ArrayList<>();

        String sql = "SELECT maPhieuNhap, ngayNhap, trangThai, ghiChu "
                + "FROM dbo.PhieuNhapKho "
                + "WHERE maPhieuNhap LIKE ? OR ghiChu LIKE ? "
                + "ORDER BY ngayNhap DESC, maPhieuNhap DESC";

        String noiDung = "%" + tuKhoa.trim() + "%";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
            ps.setString(1, noiDung);
            ps.setNString(2, noiDung);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    danhSach.add(docPhieuNhap(rs));
                }
            }
        }

        return danhSach;
    }

    public ArrayList<PhieuNhapKho> timTheoTrangThai(String trangThai) throws SQLException {
        ArrayList<PhieuNhapKho> danhSach = new ArrayList<>();

        String sql = "SELECT maPhieuNhap, ngayNhap, trangThai, ghiChu "
                + "FROM dbo.PhieuNhapKho "
                + "WHERE trangThai = ? "
                + "ORDER BY ngayNhap DESC, maPhieuNhap DESC";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
            ps.setNString(1, trangThai);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    danhSach.add(docPhieuNhap(rs));
                }
            }
        }

        return danhSach;
    }

    public String taoMaPhieuNhap() throws SQLException {
        String sql = "SELECT MAX(TRY_CONVERT(BIGINT, "
                + "SUBSTRING(maPhieuNhap, 4, 27))) AS soLonNhat "
                + "FROM dbo.PhieuNhapKho "
                + "WHERE maPhieuNhap LIKE 'PNK%'";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                long soMoi = rs.getLong("soLonNhat") + 1;

                return String.format(
                        java.util.Locale.ROOT, "PNK%03d", soMoi);
            }
        }

        return "PNK001";
    }

    public boolean themPhieuNhap(PhieuNhapKho phieu) throws SQLException {
        String sql = "INSERT INTO dbo.PhieuNhapKho "
                + "(maPhieuNhap, ngayNhap, trangThai, ghiChu) "
                + "VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
            ps.setString(1, phieu.getMaPhieuNhap());
            ps.setTimestamp(2, Timestamp.valueOf(phieu.getNgayNhap()));
            ps.setNString(3, phieu.getTrangThai());

            if (phieu.getGhiChu() == null) {
                ps.setNull(4, Types.NVARCHAR);
            } else {
                ps.setNString(4, phieu.getGhiChu());
            }

            return ps.executeUpdate() > 0;
        }
    }

    public boolean suaPhieuNhap(PhieuNhapKho phieu) throws SQLException {
        String sql = "UPDATE dbo.PhieuNhapKho "
                + "SET ngayNhap = ?, trangThai = ?, ghiChu = ? "
                + "WHERE maPhieuNhap = ?";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(phieu.getNgayNhap()));
            ps.setNString(2, phieu.getTrangThai());

            if (phieu.getGhiChu() == null) {
                ps.setNull(3, Types.NVARCHAR);
            } else {
                ps.setNString(3, phieu.getGhiChu());
            }

            ps.setString(4, phieu.getMaPhieuNhap());

            return ps.executeUpdate() > 0;
        }
    }

    public boolean capNhatTrangThai(String maPhieuNhap, String trangThai) throws SQLException {
        String sql = "UPDATE dbo.PhieuNhapKho "
                + "SET trangThai = ? WHERE maPhieuNhap = ?";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
            ps.setNString(1, trangThai);
            ps.setString(2, maPhieuNhap);

            return ps.executeUpdate() > 0;
        }
    }
}