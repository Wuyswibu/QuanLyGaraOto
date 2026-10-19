package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import connectDB.ConnectDB;
import entity.ChiTietPhieuXuat;

public class ChiTietPhieuXuatDao {

    private Connection ketNoi() throws SQLException {
        Connection conn = ConnectDB.getConnection();

        if (conn == null || conn.isClosed()) {
            throw new SQLException("Chưa kết nối cơ sở dữ liệu.");
        }

        return conn;
    }

    private ChiTietPhieuXuat docChiTiet(ResultSet rs) throws SQLException {
        return new ChiTietPhieuXuat(
                rs.getString("maPhieuXuat"),
                rs.getString("maPhuTung"),
                rs.getInt("soLuong"),
                rs.getBigDecimal("donGia"));
    }

    public ArrayList<ChiTietPhieuXuat> layDanhSachChiTiet() throws SQLException {
        ArrayList<ChiTietPhieuXuat> danhSach = new ArrayList<>();

        String sql = "SELECT maPhieuXuat, maPhuTung, soLuong, donGia "
                + "FROM dbo.ChiTietPhieuXuat "
                + "ORDER BY maPhieuXuat, maPhuTung";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                danhSach.add(docChiTiet(rs));
            }
        }

        return danhSach;
    }

    public ArrayList<ChiTietPhieuXuat> timTheoMaPhieuXuat(String maPhieuXuat) throws SQLException {
        ArrayList<ChiTietPhieuXuat> danhSach = new ArrayList<>();

        String sql = "SELECT maPhieuXuat, maPhuTung, soLuong, donGia "
                + "FROM dbo.ChiTietPhieuXuat "
                + "WHERE maPhieuXuat = ? ORDER BY maPhuTung";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
            ps.setString(1, maPhieuXuat);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    danhSach.add(docChiTiet(rs));
                }
            }
        }

        return danhSach;
    }

    public ChiTietPhieuXuat timTheoKhoa(String maPhieuXuat, String maPhuTung) throws SQLException {
        String sql = "SELECT maPhieuXuat, maPhuTung, soLuong, donGia "
                + "FROM dbo.ChiTietPhieuXuat "
                + "WHERE maPhieuXuat = ? AND maPhuTung = ?";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
            ps.setString(1, maPhieuXuat);
            ps.setString(2, maPhuTung);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return docChiTiet(rs);
                }
            }
        }

        return null;
    }

    public boolean themChiTiet(ChiTietPhieuXuat chiTiet) throws SQLException {
        String sql = "INSERT INTO dbo.ChiTietPhieuXuat "
                + "(maPhieuXuat, maPhuTung, soLuong, donGia) "
                + "VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
            ps.setString(1, chiTiet.getMaPhieuXuat());
            ps.setString(2, chiTiet.getMaPhuTung());
            ps.setInt(3, chiTiet.getSoLuong());
            ps.setBigDecimal(4, chiTiet.getDonGia());

            return ps.executeUpdate() > 0;
        }
    }

    public boolean suaChiTiet(ChiTietPhieuXuat chiTiet) throws SQLException {
        String sql = "UPDATE dbo.ChiTietPhieuXuat "
                + "SET soLuong = ?, donGia = ? "
                + "WHERE maPhieuXuat = ? AND maPhuTung = ?";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
            ps.setInt(1, chiTiet.getSoLuong());
            ps.setBigDecimal(2, chiTiet.getDonGia());
            ps.setString(3, chiTiet.getMaPhieuXuat());
            ps.setString(4, chiTiet.getMaPhuTung());

            return ps.executeUpdate() > 0;
        }
    }

    public boolean xoaChiTiet(String maPhieuXuat, String maPhuTung) throws SQLException {
        String sql = "DELETE FROM dbo.ChiTietPhieuXuat "
                + "WHERE maPhieuXuat = ? AND maPhuTung = ?";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
            ps.setString(1, maPhieuXuat);
            ps.setString(2, maPhuTung);

            return ps.executeUpdate() > 0;
        }
    }
}