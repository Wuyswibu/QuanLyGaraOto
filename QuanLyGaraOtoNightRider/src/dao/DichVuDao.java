package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;

import connectDB.ConnectDB;
import entity.DichVu;

public class DichVuDao {

    private Connection ketNoi() throws SQLException {
        Connection conn = ConnectDB.getConnection();

        if (conn == null || conn.isClosed()) {
            throw new SQLException("Chưa kết nối cơ sở dữ liệu.");
        }

        return conn;
    }

    private DichVu docDichVu(ResultSet rs) throws SQLException {
        return new DichVu(
                rs.getString("maDichVu"),
                rs.getString("tenDichVu"),
                rs.getString("moTa"),
                rs.getBigDecimal("donGia"),
                rs.getBoolean("trangThai"));
    }

    public ArrayList<DichVu> layDanhSachDichVu() throws SQLException {
        ArrayList<DichVu> danhSach = new ArrayList<>();

        String sql = "SELECT maDichVu, tenDichVu, moTa, "
                + "donGia, trangThai "
                + "FROM dbo.DichVu ORDER BY maDichVu";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                danhSach.add(docDichVu(rs));
            }
        }

        return danhSach;
    }

    public DichVu timTheoMa(String maDichVu) throws SQLException {
        String sql = "SELECT maDichVu, tenDichVu, moTa, "
                + "donGia, trangThai "
                + "FROM dbo.DichVu WHERE maDichVu = ?";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
            ps.setString(1, maDichVu);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return docDichVu(rs);
                }
            }
        }

        return null;
    }

    public ArrayList<DichVu> timKiem(String tuKhoa) throws SQLException {
        ArrayList<DichVu> danhSach = new ArrayList<>();

        String sql = "SELECT maDichVu, tenDichVu, moTa, "
                + "donGia, trangThai "
                + "FROM dbo.DichVu "
                + "WHERE maDichVu LIKE ? OR tenDichVu LIKE ? "
                + "ORDER BY maDichVu";

        String noiDung = "%" + tuKhoa.trim() + "%";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
            ps.setString(1, noiDung);
            ps.setNString(2, noiDung);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    danhSach.add(docDichVu(rs));
                }
            }
        }

        return danhSach;
    }

    public ArrayList<DichVu> timTheoTrangThai(boolean trangThai) throws SQLException {
        ArrayList<DichVu> danhSach = new ArrayList<>();

        String sql = "SELECT maDichVu, tenDichVu, moTa, "
                + "donGia, trangThai "
                + "FROM dbo.DichVu "
                + "WHERE trangThai = ? ORDER BY maDichVu";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
            ps.setBoolean(1, trangThai);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    danhSach.add(docDichVu(rs));
                }
            }
        }

        return danhSach;
    }

    public String taoMaDichVu() throws SQLException {
        String sql = "SELECT MAX(TRY_CONVERT(BIGINT, "
                + "SUBSTRING(maDichVu, 3, 28))) AS soLonNhat "
                + "FROM dbo.DichVu "
                + "WHERE maDichVu LIKE 'DV%'";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                long soMoi = rs.getLong("soLonNhat") + 1;

                return String.format(
                        java.util.Locale.ROOT, "DV%03d", soMoi);
            }
        }

        return "DV001";
    }

    public boolean themDichVu(DichVu dichVu) throws SQLException {
        String sql = "INSERT INTO dbo.DichVu "
                + "(maDichVu, tenDichVu, moTa, donGia, trangThai) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
            ps.setString(1, dichVu.getMaDichVu());
            ps.setNString(2, dichVu.getTenDichVu());

            if (dichVu.getMoTa() == null) {
                ps.setNull(3, Types.NVARCHAR);
            } else {
                ps.setNString(3, dichVu.getMoTa());
            }

            ps.setBigDecimal(4, dichVu.getDonGia());
            ps.setBoolean(5, dichVu.isTrangThai());

            return ps.executeUpdate() > 0;
        }
    }

    public boolean suaDichVu(DichVu dichVu) throws SQLException {
        String sql = "UPDATE dbo.DichVu "
                + "SET tenDichVu = ?, moTa = ?, "
                + "donGia = ?, trangThai = ? "
                + "WHERE maDichVu = ?";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
            ps.setNString(1, dichVu.getTenDichVu());

            if (dichVu.getMoTa() == null) {
                ps.setNull(2, Types.NVARCHAR);
            } else {
                ps.setNString(2, dichVu.getMoTa());
            }

            ps.setBigDecimal(3, dichVu.getDonGia());
            ps.setBoolean(4, dichVu.isTrangThai());
            ps.setString(5, dichVu.getMaDichVu());

            return ps.executeUpdate() > 0;
        }
    }

    public boolean capNhatTrangThai(String maDichVu,
            boolean trangThai) throws SQLException {
        String sql = "UPDATE dbo.DichVu "
                + "SET trangThai = ? WHERE maDichVu = ?";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
            ps.setBoolean(1, trangThai);
            ps.setString(2, maDichVu);

            return ps.executeUpdate() > 0;
        }
    }
}