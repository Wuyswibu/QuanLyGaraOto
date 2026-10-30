package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;

import connectDB.ConnectDB;
import entity.CaLamViec;

public class CaLamViecDao {

    private Connection ketNoi() throws SQLException {
        Connection conn = ConnectDB.getConnection();

        if (conn == null || conn.isClosed()) {
            throw new SQLException("Chưa kết nối cơ sở dữ liệu.");
        }

        return conn;
    }

    private CaLamViec docCaLamViec(ResultSet rs) throws SQLException {
        Time gioBatDau = rs.getTime("gioBatDau");
        Time gioKetThuc = rs.getTime("gioKetThuc");

        return new CaLamViec(
                rs.getString("maCa"),
                rs.getString("tenCa"),
                gioBatDau == null ? null : gioBatDau.toLocalTime(),
                gioKetThuc == null ? null : gioKetThuc.toLocalTime(),
                rs.getBoolean("trangThai"));
    }

    public ArrayList<CaLamViec> layDanhSachCaLamViec() throws SQLException {
        ArrayList<CaLamViec> danhSach = new ArrayList<>();

        String sql = "SELECT maCa, tenCa, gioBatDau, "
                + "gioKetThuc, trangThai "
                + "FROM dbo.CaLamViec ORDER BY maCa";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql);
        ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                danhSach.add(docCaLamViec(rs));
            }
        }

        return danhSach;
    }

    public CaLamViec timTheoMa(String maCa)  throws SQLException {
        String sql = "SELECT maCa, tenCa, gioBatDau, "
                + "gioKetThuc, trangThai "
                + "FROM dbo.CaLamViec WHERE maCa = ?";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
            ps.setString(1, maCa);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return docCaLamViec(rs);
                }
            }
        }

        return null;
    }

    public ArrayList<CaLamViec> timKiem(String tuKhoa) throws SQLException {
        ArrayList<CaLamViec> danhSach = new ArrayList<>();

        String sql = "SELECT maCa, tenCa, gioBatDau, "
                + "gioKetThuc, trangThai "
                + "FROM dbo.CaLamViec "
                + "WHERE maCa LIKE ? OR tenCa LIKE ? "
                + "ORDER BY maCa";

        String noiDung = "%" + tuKhoa.trim() + "%";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
            ps.setString(1, noiDung);
            ps.setNString(2, noiDung);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    danhSach.add(docCaLamViec(rs));
                }
            }
        }

        return danhSach;
    }

    public ArrayList<CaLamViec> timTheoTrangThai(boolean trangThai) throws SQLException {
        ArrayList<CaLamViec> danhSach = new ArrayList<>();

        String sql = "SELECT maCa, tenCa, gioBatDau, "
                + "gioKetThuc, trangThai "
                + "FROM dbo.CaLamViec "
                + "WHERE trangThai = ? ORDER BY maCa";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
            ps.setBoolean(1, trangThai);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    danhSach.add(docCaLamViec(rs));
                }
            }
        }

        return danhSach;
    }

    public String taoMaCa() throws SQLException {
        String sql = "SELECT MAX(TRY_CONVERT(BIGINT, "
                + "SUBSTRING(maCa, 4, 27))) AS soLonNhat "
                + "FROM dbo.CaLamViec "
                + "WHERE maCa LIKE 'CLV%'";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                long soMoi = rs.getLong("soLonNhat") + 1;

                return String.format(
                        java.util.Locale.ROOT, "CLV%03d", soMoi);
            }
        }

        return "CLV001";
    }

    public boolean themCaLamViec(CaLamViec caLamViec) throws SQLException {
        String sql = "INSERT INTO dbo.CaLamViec "
                + "(maCa, tenCa, gioBatDau, gioKetThuc, trangThai) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
            ps.setString(1, caLamViec.getMaCa());
            ps.setNString(2, caLamViec.getTenCa());
            ps.setTime(3, Time.valueOf(caLamViec.getGioBatDau()));
            ps.setTime(4, Time.valueOf(caLamViec.getGioKetThuc()));
            ps.setBoolean(5, caLamViec.isTrangThai());

            return ps.executeUpdate() > 0;
        }
    }

    public boolean suaCaLamViec(CaLamViec caLamViec) throws SQLException {
        String sql = "UPDATE dbo.CaLamViec "
                + "SET tenCa = ?, gioBatDau = ?, "
                + "gioKetThuc = ?, trangThai = ? WHERE maCa = ?";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
            ps.setNString(1, caLamViec.getTenCa());
            ps.setTime(2, Time.valueOf(caLamViec.getGioBatDau()));
            ps.setTime(3, Time.valueOf(caLamViec.getGioKetThuc()));
            ps.setBoolean(4, caLamViec.isTrangThai());
            ps.setString(5, caLamViec.getMaCa());

            return ps.executeUpdate() > 0;
        }
    }

    public boolean capNhatTrangThai(String maCa, boolean trangThai) throws SQLException {
        String sql = "UPDATE dbo.CaLamViec "
                + "SET trangThai = ? WHERE maCa = ?";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
            ps.setBoolean(1, trangThai);
            ps.setString(2, maCa);

            return ps.executeUpdate() > 0;
        }
    }
}