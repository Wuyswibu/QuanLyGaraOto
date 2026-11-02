package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import entity.Xe;
import connectDB.ConnectDB;

public class XeDao {
    private Connection ketNoi() throws SQLException {
        Connection conn = ConnectDB.getConnection();
        if (conn == null || conn.isClosed()) {
            throw new SQLException("Chưa kết nối database. Hãy chạy connect() trước.");
        }
        return conn;
    }

    public ArrayList<Xe> layDanhSachXe() throws SQLException {
        ArrayList<Xe> ds = new ArrayList<>();
        String sql = "SELECT maPhuongTien, maKhachHang, bienSo, mauXe, trangThai "
                + "FROM dbo.PhuongTien ORDER BY maPhuongTien";
        try (PreparedStatement stmt = ketNoi().prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                ds.add(new Xe(rs.getString("maPhuongTien"), rs.getString("maKhachHang"),
                        rs.getString("bienSo"), rs.getString("mauXe"), rs.getString("trangThai")));
            }
        }
        return ds;
    }

    public LinkedHashMap<String, String> layKhachHang() throws SQLException {
        LinkedHashMap<String, String> ds = new LinkedHashMap<>();
        String sql = "SELECT maKhachHang, hoTen FROM dbo.KhachHang ORDER BY maKhachHang";
        try (PreparedStatement stmt = ketNoi().prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) ds.put(rs.getString("maKhachHang"), rs.getString("hoTen"));
        }
        return ds;
    }

    public String layTenKhachHang(String ma) throws SQLException {
        String sql = "SELECT hoTen FROM dbo.KhachHang WHERE maKhachHang = ?";
        try (PreparedStatement stmt = ketNoi().prepareStatement(sql)) {
            stmt.setString(1, ma);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getString("hoTen") : "";
            }
        }
    }

    public boolean trungBienSo(String bienSo, String maXe) throws SQLException {
        String sql = "SELECT 1 FROM dbo.PhuongTien WHERE "
                + "UPPER(REPLACE(REPLACE(REPLACE(bienSo, ' ', ''), '-', ''), '.', '')) = ? "
                + "AND maPhuongTien <> ?";
        try (PreparedStatement stmt = ketNoi().prepareStatement(sql)) {
            stmt.setString(1, bienSo.replace(" ", "").replace("-", "").replace(".", "")
                    .toUpperCase(java.util.Locale.ROOT));
            stmt.setString(2, maXe);
            try (ResultSet rs = stmt.executeQuery()) { return rs.next(); }
        }
    }

    public String taoMaXe() throws SQLException {
        String sql = "SELECT ISNULL(MAX(TRY_CONVERT(BIGINT, SUBSTRING(maPhuongTien, 3, 28))), 0) "
                + "FROM dbo.PhuongTien WHERE maPhuongTien LIKE 'PT[0-9]%'";
        try (PreparedStatement stmt = ketNoi().prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            rs.next();
            return String.format(java.util.Locale.ROOT, "PT%03d", rs.getLong(1) + 1);
        }
    }

    public boolean themXe(Xe xe) throws SQLException {
        String sql = "INSERT INTO dbo.PhuongTien "
                + "(maPhuongTien, maKhachHang, bienSo, mauXe, trangThai) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = ketNoi().prepareStatement(sql)) {
            stmt.setString(1, xe.getMaPhuongTien());
            stmt.setString(2, xe.getMaKhachHang());
            stmt.setString(3, xe.getBienSo());
            stmt.setString(4, xe.getMauXe());
            stmt.setString(5, xe.getTrangThai());
            return stmt.executeUpdate() == 1;
        }
    }

    public boolean suaXe(Xe xe) throws SQLException {
        String sql = "UPDATE dbo.PhuongTien SET maKhachHang=?, bienSo=?, mauXe=?, trangThai=? "
                + "WHERE maPhuongTien=?";
        try (PreparedStatement stmt = ketNoi().prepareStatement(sql)) {
            stmt.setString(1, xe.getMaKhachHang());
            stmt.setString(2, xe.getBienSo());
            stmt.setString(3, xe.getMauXe());
            stmt.setString(4, xe.getTrangThai());
            stmt.setString(5, xe.getMaPhuongTien());
            return stmt.executeUpdate() == 1;
        }
    }
}
