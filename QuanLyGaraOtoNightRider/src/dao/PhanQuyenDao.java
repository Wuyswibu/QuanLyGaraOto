package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

import connectDB.ConnectDB;
import entity.PhanQuyen;

public class PhanQuyenDao {

    private Connection ketNoi() throws SQLException {
        Connection conn = ConnectDB.getConnection();

        if (conn == null || conn.isClosed()) {
            throw new SQLException("Chưa kết nối cơ sở dữ liệu.");
        }

        return conn;
    }

    private PhanQuyen docPhanQuyen(ResultSet rs) throws SQLException {
        Timestamp ngayCap = rs.getTimestamp("ngayCap");

        return new PhanQuyen(
                rs.getString("maPhanQuyen"),
                rs.getString("tenChuc"),
                rs.getBoolean("choPhep"),
                ngayCap == null ? null : ngayCap.toLocalDateTime());
    }

    public ArrayList<PhanQuyen> layDanhSachPhanQuyen() throws SQLException {
        ArrayList<PhanQuyen> danhSach = new ArrayList<>();

        String sql = "SELECT maPhanQuyen, tenChuc, choPhep, ngayCap FROM dbo.PhanQuyen ORDER BY maPhanQuyen";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                danhSach.add(docPhanQuyen(rs));
            }
        }

        return danhSach;
    }

    public PhanQuyen timTheoMa(String maPhanQuyen) throws SQLException {
        String sql = "SELECT maPhanQuyen, tenChuc, choPhep, ngayCap FROM dbo.PhanQuyen WHERE maPhanQuyen = ?";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
            ps.setString(1, maPhanQuyen);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return docPhanQuyen(rs);
                }
            }
        }

        return null;
    }

    public ArrayList<PhanQuyen> timKiem(String tuKhoa) throws SQLException {
        ArrayList<PhanQuyen> danhSach = new ArrayList<>();

        String sql = "SELECT maPhanQuyen, tenChuc, choPhep, ngayCap FROM dbo.PhanQuyen WHERE maPhanQuyen LIKE ? OR tenChuc LIKE ? ORDER BY maPhanQuyen";

        String noiDung = "%" + tuKhoa.trim() + "%";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
            ps.setString(1, noiDung);
            ps.setNString(2, noiDung);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    danhSach.add(docPhanQuyen(rs));
                }
            }
        }

        return danhSach;
    }

    public ArrayList<PhanQuyen> timTheoChoPhep(boolean choPhep) throws SQLException {
        ArrayList<PhanQuyen> danhSach = new ArrayList<>();

        String sql = "SELECT maPhanQuyen, tenChuc, choPhep, ngayCap FROM dbo.PhanQuyen WHERE choPhep = ? ORDER BY maPhanQuyen";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
            ps.setBoolean(1, choPhep);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    danhSach.add(docPhanQuyen(rs));
                }
            }
        }

        return danhSach;
    }

    public String taoMaPhanQuyen() throws SQLException {
        String sql = "SELECT MAX(TRY_CONVERT(BIGINT, SUBSTRING(maPhanQuyen, 3, 28))) AS soLonNhat FROM dbo.PhanQuyen WHERE maPhanQuyen LIKE 'PQ%'";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                long soMoi = rs.getLong("soLonNhat") + 1;

                return String.format(
                        java.util.Locale.ROOT, "PQ%03d", soMoi);
            }
        }

        return "PQ001";
    }

    public boolean themPhanQuyen(PhanQuyen phanQuyen) throws SQLException {
        String sql = "INSERT INTO dbo.PhanQuyen (maPhanQuyen, tenChuc, choPhep, ngayCap) VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
            ps.setString(1, phanQuyen.getMaPhanQuyen());
            ps.setNString(2, phanQuyen.getTenChuc());
            ps.setBoolean(3, phanQuyen.isChoPhep());
            ps.setTimestamp(
                    4, Timestamp.valueOf(phanQuyen.getNgayCap()));

            return ps.executeUpdate() > 0;
        }
    }

    public boolean suaPhanQuyen(PhanQuyen phanQuyen) throws SQLException {
        String sql = "UPDATE dbo.PhanQuyen SET tenChuc = ?, choPhep = ?, ngayCap = ? WHERE maPhanQuyen = ?";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
            ps.setNString(1, phanQuyen.getTenChuc());
            ps.setBoolean(2, phanQuyen.isChoPhep());
            ps.setTimestamp(
                    3, Timestamp.valueOf(phanQuyen.getNgayCap()));
            ps.setString(4, phanQuyen.getMaPhanQuyen());

            return ps.executeUpdate() > 0;
        }
    }

    public boolean capNhatChoPhep(String maPhanQuyen, boolean choPhep) throws SQLException {
        String sql = "UPDATE dbo.PhanQuyen SET choPhep = ? WHERE maPhanQuyen = ?";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
            ps.setBoolean(1, choPhep);
            ps.setString(2, maPhanQuyen);

            return ps.executeUpdate() > 0;
        }
    }
}