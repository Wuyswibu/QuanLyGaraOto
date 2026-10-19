package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;

import connectDB.ConnectDB;
import entity.PhieuSuaChua;

public class PhieuSuaChuaDao {

    private Connection ketNoi() throws SQLException {
        Connection conn = ConnectDB.getConnection();

        if (conn == null || conn.isClosed()) {
            throw new SQLException("Chưa kết nối cơ sở dữ liệu.");
        }

        return conn;
    }

    private PhieuSuaChua docPhieu(ResultSet rs) throws SQLException {
        Timestamp ngayLap = rs.getTimestamp("ngayLap");

        return new PhieuSuaChua(
                rs.getString("maPhieuSuaChua"),
                rs.getString("maTiepNhan"),
                ngayLap == null ? null : ngayLap.toLocalDateTime(),
                rs.getString("chanDoan"),
                rs.getString("loaiPhieu"),
                rs.getString("trangThai"),
                rs.getString("ghiChu"));
    }

    public ArrayList<PhieuSuaChua> layDanhSachPhieuSuaChua() throws SQLException {
        ArrayList<PhieuSuaChua> danhSach = new ArrayList<>();

        String sql = "SELECT * FROM dbo.PhieuSuaChua "
                + "ORDER BY ngayLap DESC, maPhieuSuaChua DESC";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                danhSach.add(docPhieu(rs));
            }
        }

        return danhSach;
    }

    public PhieuSuaChua timTheoMa(String maPhieuSuaChua) throws SQLException {
        String sql = "SELECT * FROM dbo.PhieuSuaChua "
                + "WHERE maPhieuSuaChua = ?";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
            ps.setString(1, maPhieuSuaChua);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return docPhieu(rs);
                }
            }
        }

        return null;
    }

    public PhieuSuaChua timTheoMaTiepNhan(String maTiepNhan) throws SQLException {
        String sql = "SELECT * FROM dbo.PhieuSuaChua "
                + "WHERE maTiepNhan = ?";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
            ps.setString(1, maTiepNhan);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return docPhieu(rs);
                }
            }
        }

        return null;
    }

    public ArrayList<PhieuSuaChua> timKiem(String tuKhoa) throws SQLException {
        ArrayList<PhieuSuaChua> danhSach = new ArrayList<>();

        String sql = "SELECT s.* "
                + "FROM dbo.PhieuSuaChua s "
                + "JOIN dbo.PhieuTiepNhan t "
                + "ON s.maTiepNhan = t.maTiepNhan "
                + "JOIN dbo.PhuongTien p "
                + "ON t.maPhuongTien = p.maPhuongTien "
                + "JOIN dbo.KhachHang k "
                + "ON p.maKhachHang = k.maKhachHang "
                + "WHERE s.maPhieuSuaChua LIKE ? "
                + "OR s.maTiepNhan LIKE ? "
                + "OR p.bienSo LIKE ? "
                + "OR k.hoTen LIKE ? "
                + "ORDER BY s.ngayLap DESC, s.maPhieuSuaChua DESC";

        String noiDung = "%" + tuKhoa.trim() + "%";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
            ps.setString(1, noiDung);
            ps.setString(2, noiDung);
            ps.setString(3, noiDung);
            ps.setNString(4, noiDung);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    danhSach.add(docPhieu(rs));
                }
            }
        }

        return danhSach;
    }

    public String taoMaPhieuSuaChua() throws SQLException {
        String sql = "SELECT MAX(TRY_CONVERT(BIGINT, "
                + "SUBSTRING(maPhieuSuaChua, 4, 27))) AS soLonNhat "
                + "FROM dbo.PhieuSuaChua "
                + "WHERE maPhieuSuaChua LIKE 'PSC%'";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                long soMoi = rs.getLong("soLonNhat") + 1;

                return String.format(
                        java.util.Locale.ROOT, "PSC%03d", soMoi);
            }
        }

        return "PSC001";
    }

    public boolean themPhieuSuaChua(PhieuSuaChua phieu) throws SQLException {
        String sql = "INSERT INTO dbo.PhieuSuaChua "
                + "(maPhieuSuaChua, maTiepNhan, ngayLap, "
                + "chanDoan, loaiPhieu, trangThai, ghiChu) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
            ps.setString(1, phieu.getMaPhieuSuaChua());
            ps.setString(2, phieu.getMaTiepNhan());
            ps.setTimestamp(3, Timestamp.valueOf(phieu.getNgayLap()));

            if (phieu.getChanDoan() == null) {
                ps.setNull(4, Types.NVARCHAR);
            } else {
                ps.setNString(4, phieu.getChanDoan());
            }

            ps.setNString(5, phieu.getLoaiPhieu());
            ps.setNString(6, phieu.getTrangThai());

            if (phieu.getGhiChu() == null) {
                ps.setNull(7, Types.NVARCHAR);
            } else {
                ps.setNString(7, phieu.getGhiChu());
            }

            return ps.executeUpdate() > 0;
        }
    }

    public boolean suaPhieuSuaChua(PhieuSuaChua phieu) throws SQLException {
        String sql = "UPDATE dbo.PhieuSuaChua "
                + "SET maTiepNhan = ?, ngayLap = ?, chanDoan = ?, "
                + "loaiPhieu = ?, trangThai = ?, ghiChu = ? "
                + "WHERE maPhieuSuaChua = ?";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
            ps.setString(1, phieu.getMaTiepNhan());
            ps.setTimestamp(2, Timestamp.valueOf(phieu.getNgayLap()));

            if (phieu.getChanDoan() == null) {
                ps.setNull(3, Types.NVARCHAR);
            } else {
                ps.setNString(3, phieu.getChanDoan());
            }

            ps.setNString(4, phieu.getLoaiPhieu());
            ps.setNString(5, phieu.getTrangThai());

            if (phieu.getGhiChu() == null) {
                ps.setNull(6, Types.NVARCHAR);
            } else {
                ps.setNString(6, phieu.getGhiChu());
            }

            ps.setString(7, phieu.getMaPhieuSuaChua());

            return ps.executeUpdate() > 0;
        }
    }

    public boolean capNhatTrangThai(String maPhieuSuaChua, String trangThai) throws SQLException {
        String sql = "UPDATE dbo.PhieuSuaChua "
                + "SET trangThai = ? WHERE maPhieuSuaChua = ?";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
            ps.setNString(1, trangThai);
            ps.setString(2, maPhieuSuaChua);

            return ps.executeUpdate() > 0;
        }
    }
}