package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;

import connectDB.ConnectDB;
import entity.KhachHang;

public class KhachHangDao {

    private Connection ketNoi() throws SQLException {
        Connection conn = ConnectDB.getConnection();

        if (conn == null || conn.isClosed()) {
            throw new SQLException("Chưa kết nối cơ sở dữ liệu.");
        }

        return conn;
    }

    private KhachHang docKhachHang(ResultSet rs) throws SQLException {
        return new KhachHang(
                rs.getString("maKhachHang"),
                rs.getString("hoTen"),
                rs.getString("soDienThoai"),
                rs.getString("email"),
                rs.getString("diaChi"),
                rs.getBoolean("trangThai"));
    }

    public ArrayList<KhachHang> layDanhSachKhachHang() throws SQLException {
        ArrayList<KhachHang> danhSach = new ArrayList<>();

        String sql = "SELECT maKhachHang, hoTen, soDienThoai, email, diaChi, trangThai FROM dbo.KhachHang ORDER BY maKhachHang";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                danhSach.add(docKhachHang(rs));
            }
        }

        return danhSach;
    }

    public KhachHang timTheoMa(String maKhachHang) throws SQLException {
        String sql = "SELECT maKhachHang, hoTen, soDienThoai, email, diaChi, trangThai FROM dbo.KhachHang WHERE maKhachHang = ?";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
            ps.setString(1, maKhachHang);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return docKhachHang(rs);
                }
            }
        }

        return null;
    }

    public ArrayList<KhachHang> timTheoSoDienThoai(String soDienThoai) throws SQLException {
        ArrayList<KhachHang> danhSach = new ArrayList<>();

        String sql = "SELECT maKhachHang, hoTen, soDienThoai, email, diaChi, trangThai FROM dbo.KhachHang WHERE soDienThoai = ? ORDER BY maKhachHang";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
            ps.setString(1, soDienThoai);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    danhSach.add(docKhachHang(rs));
                }
            }
        }

        return danhSach;
    }

    public ArrayList<KhachHang> timKiem(String tuKhoa) throws SQLException {
        ArrayList<KhachHang> danhSach = new ArrayList<>();

        String sql = "SELECT maKhachHang, hoTen, soDienThoai, email, diaChi, trangThai FROM dbo.KhachHang WHERE maKhachHang LIKE ? OR hoTen LIKE ? OR soDienThoai LIKE ? ORDER BY maKhachHang";

        String noiDung = "%" + tuKhoa.trim() + "%";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
            ps.setString(1, noiDung);
            ps.setNString(2, noiDung);
            ps.setString(3, noiDung);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    danhSach.add(docKhachHang(rs));
                }
            }
        }

        return danhSach;
    }

    public String taoMaKhachHang() throws SQLException {
        String sql = "SELECT MAX(TRY_CONVERT(BIGINT, SUBSTRING(maKhachHang, 3, 28))) AS soLonNhat FROM dbo.KhachHang WHERE maKhachHang LIKE 'KH%'";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                long soMoi = rs.getLong("soLonNhat") + 1;
                return String.format(
                        java.util.Locale.ROOT, "KH%03d", soMoi);
            }
        }

        return "KH001";
    }

    public boolean themKhachHang(KhachHang khachHang) throws SQLException {
        String sql = "INSERT INTO dbo.KhachHang (maKhachHang, hoTen, soDienThoai, email, diaChi, trangThai) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
            ps.setString(1, khachHang.getMaKhachHang());
            ps.setNString(2, khachHang.getHoTen());
            ps.setString(3, khachHang.getSoDienThoai());

            if (khachHang.getEmail() == null) {
                ps.setNull(4, Types.VARCHAR);
            } else {
                ps.setString(4, khachHang.getEmail());
            }

            if (khachHang.getDiaChi() == null) {
                ps.setNull(5, Types.NVARCHAR);
            } else {
                ps.setNString(5, khachHang.getDiaChi());
            }

            ps.setBoolean(6, khachHang.isTrangThai());

            return ps.executeUpdate() > 0;
        }
    }

    public boolean suaKhachHang(KhachHang khachHang) throws SQLException {
        String sql = "UPDATE dbo.KhachHang SET hoTen = ?, soDienThoai = ?, email = ?, diaChi = ?, trangThai = ? WHERE maKhachHang = ?";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
            ps.setNString(1, khachHang.getHoTen());
            ps.setString(2, khachHang.getSoDienThoai());

            if (khachHang.getEmail() == null) {
                ps.setNull(3, Types.VARCHAR);
            } else {
                ps.setString(3, khachHang.getEmail());
            }

            if (khachHang.getDiaChi() == null) {
                ps.setNull(4, Types.NVARCHAR);
            } else {
                ps.setNString(4, khachHang.getDiaChi());
            }

            ps.setBoolean(5, khachHang.isTrangThai());
            ps.setString(6, khachHang.getMaKhachHang());

            return ps.executeUpdate() > 0;
        }
    }

    public boolean capNhatTrangThai(String maKhachHang,
            boolean trangThai) throws SQLException {
        String sql = "UPDATE dbo.KhachHang SET trangThai = ? WHERE maKhachHang = ?";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
            ps.setBoolean(1, trangThai);
            ps.setString(2, maKhachHang);

            return ps.executeUpdate() > 0;
        }
    }
}