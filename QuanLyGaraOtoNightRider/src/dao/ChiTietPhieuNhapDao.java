package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import connectDB.ConnectDB;
import entity.ChiTietPhieuNhap;

public class ChiTietPhieuNhapDao {

    private Connection ketNoi() throws SQLException {
        Connection conn = ConnectDB.getConnection();

        if (conn == null || conn.isClosed()) {
            throw new SQLException("Chưa kết nối cơ sở dữ liệu.");
        }

        return conn;
    }

    private ChiTietPhieuNhap docChiTiet(ResultSet rs) throws SQLException {
        return new ChiTietPhieuNhap(
                rs.getString("maPhieuNhap"),
                rs.getString("maPhuTung"),
                rs.getInt("soLuong"),
                rs.getBigDecimal("donGia"));
    }

    public ArrayList<ChiTietPhieuNhap> layDanhSachChiTiet() throws SQLException {
        ArrayList<ChiTietPhieuNhap> danhSach = new ArrayList<>();

        String sql = "SELECT maPhieuNhap, maPhuTung, soLuong, donGia "
                + "FROM dbo.ChiTietPhieuNhap "
                + "ORDER BY maPhieuNhap, maPhuTung";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                danhSach.add(docChiTiet(rs));
            }
        }

        return danhSach;
    }

    public ArrayList<ChiTietPhieuNhap> timTheoMaPhieuNhap(String maPhieuNhap) throws SQLException {
        ArrayList<ChiTietPhieuNhap> danhSach = new ArrayList<>();

        String sql = "SELECT maPhieuNhap, maPhuTung, soLuong, donGia "
                + "FROM dbo.ChiTietPhieuNhap "
                + "WHERE maPhieuNhap = ? ORDER BY maPhuTung";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
            ps.setString(1, maPhieuNhap);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    danhSach.add(docChiTiet(rs));
                }
            }
        }

        return danhSach;
    }

    public ChiTietPhieuNhap timTheoKhoa(String maPhieuNhap, String maPhuTung) throws SQLException {
        String sql = "SELECT maPhieuNhap, maPhuTung, soLuong, donGia "
                + "FROM dbo.ChiTietPhieuNhap "
                + "WHERE maPhieuNhap = ? AND maPhuTung = ?";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
            ps.setString(1, maPhieuNhap);
            ps.setString(2, maPhuTung);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return docChiTiet(rs);
                }
            }
        }

        return null;
    }

    public boolean themChiTiet(ChiTietPhieuNhap chiTiet) throws SQLException {
        String sql = "INSERT INTO dbo.ChiTietPhieuNhap "
                + "(maPhieuNhap, maPhuTung, soLuong, donGia) "
                + "VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
            ps.setString(1, chiTiet.getMaPhieuNhap());
            ps.setString(2, chiTiet.getMaPhuTung());
            ps.setInt(3, chiTiet.getSoLuong());
            ps.setBigDecimal(4, chiTiet.getDonGia());

            return ps.executeUpdate() > 0;
        }
    }

    public boolean suaChiTiet(ChiTietPhieuNhap chiTiet) throws SQLException {
        String sql = "UPDATE dbo.ChiTietPhieuNhap "
                + "SET soLuong = ?, donGia = ? "
                + "WHERE maPhieuNhap = ? AND maPhuTung = ?";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
            ps.setInt(1, chiTiet.getSoLuong());
            ps.setBigDecimal(2, chiTiet.getDonGia());
            ps.setString(3, chiTiet.getMaPhieuNhap());
            ps.setString(4, chiTiet.getMaPhuTung());

            return ps.executeUpdate() > 0;
        }
    }

    public boolean xoaChiTiet(String maPhieuNhap, String maPhuTung) throws SQLException {
        String sql = "DELETE FROM dbo.ChiTietPhieuNhap "
                + "WHERE maPhieuNhap = ? AND maPhuTung = ?";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
            ps.setString(1, maPhieuNhap);
            ps.setString(2, maPhuTung);

            return ps.executeUpdate() > 0;
        }
    }
}