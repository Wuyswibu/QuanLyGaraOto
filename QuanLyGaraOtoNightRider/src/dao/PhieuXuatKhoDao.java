package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;

import connectDB.ConnectDB;
import entity.PhieuXuatKho;

public class PhieuXuatKhoDao {

    private Connection ketNoi() throws SQLException {
        Connection conn = ConnectDB.getConnection();

        if (conn == null || conn.isClosed()) {
            throw new SQLException("Chưa kết nối cơ sở dữ liệu.");
        }

        return conn;
    }

    private PhieuXuatKho docPhieuXuat(ResultSet rs) throws SQLException {
        Timestamp ngayXuat = rs.getTimestamp("ngayXuat");

        return new PhieuXuatKho(
                rs.getString("maPhieuXuat"),
                ngayXuat == null ? null : ngayXuat.toLocalDateTime(),
                rs.getString("lyDoXuat"),
                rs.getString("trangThai"),
                rs.getString("ghiChu"));
    }

    public ArrayList<PhieuXuatKho> layDanhSachPhieuXuat() throws SQLException {
        ArrayList<PhieuXuatKho> danhSach = new ArrayList<>();

        String sql = "SELECT maPhieuXuat, ngayXuat, lyDoXuat, "
                + "trangThai, ghiChu "
                + "FROM dbo.PhieuXuatKho "
                + "ORDER BY ngayXuat DESC, maPhieuXuat DESC";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                danhSach.add(docPhieuXuat(rs));
            }
        }

        return danhSach;
    }

    public PhieuXuatKho timTheoMa(String maPhieuXuat) throws SQLException {
        String sql = "SELECT maPhieuXuat, ngayXuat, lyDoXuat, "
                + "trangThai, ghiChu "
                + "FROM dbo.PhieuXuatKho WHERE maPhieuXuat = ?";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
            ps.setString(1, maPhieuXuat);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return docPhieuXuat(rs);
                }
            }
        }

        return null;
    }

    public ArrayList<PhieuXuatKho> timKiem(String tuKhoa) throws SQLException {
        ArrayList<PhieuXuatKho> danhSach = new ArrayList<>();

        String sql = "SELECT maPhieuXuat, ngayXuat, lyDoXuat, "
                + "trangThai, ghiChu "
                + "FROM dbo.PhieuXuatKho "
                + "WHERE maPhieuXuat LIKE ? "
                + "OR lyDoXuat LIKE ? OR ghiChu LIKE ? "
                + "ORDER BY ngayXuat DESC, maPhieuXuat DESC";

        String noiDung = "%" + tuKhoa.trim() + "%";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
            ps.setString(1, noiDung);
            ps.setNString(2, noiDung);
            ps.setNString(3, noiDung);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    danhSach.add(docPhieuXuat(rs));
                }
            }
        }

        return danhSach;
    }

    public ArrayList<PhieuXuatKho> timTheoTrangThai(String trangThai) throws SQLException {
        ArrayList<PhieuXuatKho> danhSach = new ArrayList<>();

        String sql = "SELECT maPhieuXuat, ngayXuat, lyDoXuat, "
                + "trangThai, ghiChu "
                + "FROM dbo.PhieuXuatKho "
                + "WHERE trangThai = ? "
                + "ORDER BY ngayXuat DESC, maPhieuXuat DESC";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
            ps.setNString(1, trangThai);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    danhSach.add(docPhieuXuat(rs));
                }
            }
        }

        return danhSach;
    }

    public String taoMaPhieuXuat() throws SQLException {
        String sql = "SELECT MAX(TRY_CONVERT(BIGINT, "
                + "SUBSTRING(maPhieuXuat, 4, 27))) AS soLonNhat "
                + "FROM dbo.PhieuXuatKho "
                + "WHERE maPhieuXuat LIKE 'PXK%'";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                long soMoi = rs.getLong("soLonNhat") + 1;

                return String.format(
                        java.util.Locale.ROOT, "PXK%03d", soMoi);
            }
        }

        return "PXK001";
    }

    public boolean themPhieuXuat(PhieuXuatKho phieu) throws SQLException {
        String sql = "INSERT INTO dbo.PhieuXuatKho "
                + "(maPhieuXuat, ngayXuat, lyDoXuat, trangThai, ghiChu) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
            ps.setString(1, phieu.getMaPhieuXuat());
            ps.setTimestamp(2, Timestamp.valueOf(phieu.getNgayXuat()));

            if (phieu.getLyDoXuat() == null) {
                ps.setNull(3, Types.NVARCHAR);
            } else {
                ps.setNString(3, phieu.getLyDoXuat());
            }

            ps.setNString(4, phieu.getTrangThai());

            if (phieu.getGhiChu() == null) {
                ps.setNull(5, Types.NVARCHAR);
            } else {
                ps.setNString(5, phieu.getGhiChu());
            }

            return ps.executeUpdate() > 0;
        }
    }

    public boolean suaPhieuXuat(PhieuXuatKho phieu) throws SQLException {
        String sql = "UPDATE dbo.PhieuXuatKho "
                + "SET ngayXuat = ?, lyDoXuat = ?, "
                + "trangThai = ?, ghiChu = ? "
                + "WHERE maPhieuXuat = ?";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(phieu.getNgayXuat()));

            if (phieu.getLyDoXuat() == null) {
                ps.setNull(2, Types.NVARCHAR);
            } else {
                ps.setNString(2, phieu.getLyDoXuat());
            }

            ps.setNString(3, phieu.getTrangThai());

            if (phieu.getGhiChu() == null) {
                ps.setNull(4, Types.NVARCHAR);
            } else {
                ps.setNString(4, phieu.getGhiChu());
            }

            ps.setString(5, phieu.getMaPhieuXuat());

            return ps.executeUpdate() > 0;
        }
    }

    public boolean capNhatTrangThai(String maPhieuXuat, String trangThai) throws SQLException {
        String sql = "UPDATE dbo.PhieuXuatKho "
                + "SET trangThai = ? WHERE maPhieuXuat = ?";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
            ps.setNString(1, trangThai);
            ps.setString(2, maPhieuXuat);

            return ps.executeUpdate() > 0;
        }
    }
}