package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import connectDB.ConnectDB;
import entity.TaiKhoan;

public class TaiKhoanDao {

    private Connection ketNoi() throws SQLException {
        Connection conn = ConnectDB.getConnection();

        if (conn == null || conn.isClosed()) {
            throw new SQLException("Chưa kết nối cơ sở dữ liệu.");
        }

        return conn;
    }

    private TaiKhoan docTaiKhoan(ResultSet rs) throws SQLException {
        return new TaiKhoan(
                rs.getString("maTaiKhoan"),
                rs.getString("maNhanVien"),
                rs.getString("tenDangNhap"),
                rs.getString("matKhau"),
                rs.getString("trangThai"));
    }

    public ArrayList<TaiKhoan> layDanhSachTaiKhoan() throws SQLException {
        ArrayList<TaiKhoan> danhSach = new ArrayList<>();

        String sql = "SELECT maTaiKhoan, maNhanVien, tenDangNhap, matKhau, trangThai FROM dbo.TaiKhoan ORDER BY maTaiKhoan";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql);
        ResultSet rs = ps.executeQuery()) {
        	while (rs.next()) {
                danhSach.add(docTaiKhoan(rs));
            }
        }

        return danhSach;
    }

    public TaiKhoan timTheoMa(String maTaiKhoan) throws SQLException {
        String sql = "SELECT maTaiKhoan, maNhanVien, tenDangNhap, matKhau, trangThai FROM dbo.TaiKhoan WHERE maTaiKhoan = ?";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
            ps.setString(1, maTaiKhoan);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return docTaiKhoan(rs);
                }
            }
        }

        return null;
    }

    public TaiKhoan timTheoMaNhanVien(String maNhanVien) throws SQLException {
        String sql = "SELECT maTaiKhoan, maNhanVien, tenDangNhap, matKhau, trangThai FROM dbo.TaiKhoan WHERE maNhanVien = ?";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
            ps.setString(1, maNhanVien);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return docTaiKhoan(rs);
                }
            }
        }

        return null;
    }

    public TaiKhoan timTheoTenDangNhap(String tenDangNhap) throws SQLException {
        String sql = "SELECT maTaiKhoan, maNhanVien, tenDangNhap, matKhau, trangThai FROM dbo.TaiKhoan WHERE tenDangNhap = ?";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
            ps.setString(1, tenDangNhap);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return docTaiKhoan(rs);
                }
            }
        }

        return null;
    }

    public ArrayList<TaiKhoan> timKiem(String tuKhoa)throws SQLException {
        ArrayList<TaiKhoan> danhSach = new ArrayList<>();

        String sql = "SELECT maTaiKhoan, maNhanVien, tenDangNhap, matKhau, trangThai FROM dbo.TaiKhoan WHERE maTaiKhoan LIKE ? OR maNhanVien LIKE ? OR tenDangNhap LIKE ? ORDER BY maTaiKhoan";

        String noiDung = "%" + tuKhoa.trim() + "%";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
            ps.setString(1, noiDung);
            ps.setString(2, noiDung);
            ps.setString(3, noiDung);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    danhSach.add(docTaiKhoan(rs));
                }
            }
        }

        return danhSach;
    }

    public boolean trungTenDangNhap(String tenDangNhap,  String maTaiKhoanBoQua) throws SQLException {
        String sql = "SELECT 1 FROM dbo.TaiKhoan WHERE tenDangNhap = ? AND maTaiKhoan <> ?";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
            ps.setString(1, tenDangNhap);
            ps.setString(2, maTaiKhoanBoQua == null ? "" : maTaiKhoanBoQua);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public String taoMaTaiKhoan() throws SQLException {
        String sql = "SELECT MAX(TRY_CONVERT(BIGINT, SUBSTRING(maTaiKhoan, 3, 28))) AS soLonNhat FROM dbo.TaiKhoan WHERE maTaiKhoan LIKE 'TK%'";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql);
        ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                long soMoi = rs.getLong("soLonNhat") + 1;
        return String.format(java.util.Locale.ROOT, "TK%03d", soMoi);
            }
        }

        return "TK001";
    }

    public boolean themTaiKhoan(TaiKhoan taiKhoan) throws SQLException {
        String sql = "INSERT INTO dbo.TaiKhoan (maTaiKhoan, maNhanVien, tenDangNhap, matKhau, trangThai) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
            ps.setString(1, taiKhoan.getMaTaiKhoan());
            ps.setString(2, taiKhoan.getMaNhanVien());
            ps.setString(3, taiKhoan.getTenDangNhap());
            ps.setString(4, taiKhoan.getMatKhau());
            ps.setNString(5, taiKhoan.getTrangThai());

            return ps.executeUpdate() > 0;
        }
    }

    public boolean suaTaiKhoan(TaiKhoan taiKhoan) throws SQLException {
        String sql = "UPDATE dbo.TaiKhoan SET maNhanVien = ?, tenDangNhap = ?, trangThai = ? WHERE maTaiKhoan = ?";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
            ps.setString(1, taiKhoan.getMaNhanVien());
            ps.setString(2, taiKhoan.getTenDangNhap());
            ps.setNString(3, taiKhoan.getTrangThai());
            ps.setString(4, taiKhoan.getMaTaiKhoan());

            return ps.executeUpdate() > 0;
        }
    }

    public boolean doiMatKhau(String maTaiKhoan, String matKhauMoi) throws SQLException {
        String sql = "UPDATE dbo.TaiKhoan SET matKhau = ? WHERE maTaiKhoan = ?";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
            ps.setString(1, matKhauMoi);
            ps.setString(2, maTaiKhoan);

            return ps.executeUpdate() > 0;
        }
    }

    public boolean capNhatTrangThai(String maTaiKhoan, String trangThai) throws SQLException {
        String sql = "UPDATE dbo.TaiKhoan SET trangThai = ? WHERE maTaiKhoan = ?";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
            ps.setNString(1, trangThai);
            ps.setString(2, maTaiKhoan);

            return ps.executeUpdate() > 0;
        }
    }
}