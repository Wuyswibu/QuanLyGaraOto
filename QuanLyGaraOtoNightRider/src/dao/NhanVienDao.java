package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;

import connectDB.ConnectDB;
import entity.NhanVien;

public class NhanVienDao {

    private Connection ketNoi() throws SQLException {
        Connection conn = ConnectDB.getConnection();

        if (conn == null || conn.isClosed()) {
            throw new SQLException("Chưa kết nối cơ sở dữ liệu.");
        }

        return conn;
    }

    private NhanVien docNhanVien(ResultSet rs) throws SQLException {
        Date ngaySinh = rs.getDate("ngaySinh");
        Date ngayVaoLam = rs.getDate("ngayVaoLam");

        return new NhanVien(
                rs.getString("maNhanVien"),
                rs.getString("maPhanQuyen"),
                rs.getString("tenChuc"),
                rs.getString("hoTen"),
                rs.getString("soDienThoai"),
                rs.getString("email"),
                ngaySinh == null ? null : ngaySinh.toLocalDate(),
                ngayVaoLam == null ? null : ngayVaoLam.toLocalDate(),
                rs.getString("trangThai"));
    }

    public ArrayList<NhanVien> layDanhSachNhanVien() throws SQLException {
        ArrayList<NhanVien> danhSach = new ArrayList<>();

        String sql = "SELECT nv.maNhanVien, nv.hoTen, nv.soDienThoai, nv.email, " +
                "nv.ngayVaoLam, nv.trangThai, nv.maPhanQuyen, pq.tenChuc " +
                "FROM dbo.NhanVien nv " +
                "LEFT JOIN dbo.PhanQuyen pq ON nv.maPhanQuyen = pq.maPhanQuyen";

        try {
            Connection con = ConnectDB.getConnection();
            if (con == null || con.isClosed()) {
                con = ConnectDB.getInstance().connect();
            }

            PreparedStatement stmt = con.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                NhanVien nv = new NhanVien();
                nv.setMaNhanVien(rs.getString("maNhanVien"));
                nv.setHoTen(rs.getString("hoTen"));
                nv.setSoDienThoai(rs.getString("soDienThoai"));
                nv.setEmail(rs.getString("email"));
                nv.setMaPhanQuyen(rs.getString("maPhanQuyen"));
                nv.setTenChuc(rs.getString("tenChuc")); // Tên chức vụ từ bảng PhanQuyen

                java.sql.Date sqlNgayVao = rs.getDate("ngayVaoLam");
                if (sqlNgayVao != null) {
                    nv.setNgayVaoLam(sqlNgayVao.toLocalDate());
                }

                nv.setTrangThai(rs.getString("trangThai"));
                danhSach.add(nv);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSach;
    }

    public NhanVien timTheoMa(String maNhanVien) throws SQLException {
    	String sql =
    	        "SELECT nv.*, pq.tenChuc AS tenChuc "
    	        + "FROM dbo.NhanVien nv "
    	        + "LEFT JOIN dbo.PhanQuyen pq "
    	        + "ON nv.maPhanQuyen = pq.maPhanQuyen "
    	        + "WHERE nv.maNhanVien = ?";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
            ps.setString(1, maNhanVien);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return docNhanVien(rs);
                }
            }
        }

        return null;
    }

    public ArrayList<NhanVien> timTheoSoDienThoai(String soDienThoai) throws SQLException {
        ArrayList<NhanVien> danhSach = new ArrayList<>();

        String sql = "SELECT maNhanVien, maPhanQuyen, hoTen, "
                + "soDienThoai, email, ngaySinh, ngayVaoLam, trangThai "
                + "FROM dbo.NhanVien "
                + "WHERE soDienThoai = ? ORDER BY maNhanVien";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
            ps.setString(1, soDienThoai);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    danhSach.add(docNhanVien(rs));
                }
            }
        }

        return danhSach;
    }

    public ArrayList<NhanVien> timTheoPhanQuyen(String maPhanQuyen) throws SQLException {
        ArrayList<NhanVien> danhSach = new ArrayList<>();

        String sql = "SELECT maNhanVien, maPhanQuyen, hoTen, "
                + "soDienThoai, email, ngaySinh, ngayVaoLam, trangThai "
                + "FROM dbo.NhanVien "
                + "WHERE maPhanQuyen = ? ORDER BY maNhanVien";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
            ps.setString(1, maPhanQuyen);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    danhSach.add(docNhanVien(rs));
                }
            }
        }

        return danhSach;
    }

    public ArrayList<NhanVien> timKiem(String tuKhoa) throws SQLException {
        ArrayList<NhanVien> danhSach = new ArrayList<>();

        String sql = "SELECT maNhanVien, maPhanQuyen, hoTen, "
                + "soDienThoai, email, ngaySinh, ngayVaoLam, trangThai "
                + "FROM dbo.NhanVien "
                + "WHERE maNhanVien LIKE ? "
                + "OR hoTen LIKE ? "
                + "OR soDienThoai LIKE ? "
                + "ORDER BY maNhanVien";

        String noiDung = "%" + tuKhoa.trim() + "%";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
            ps.setString(1, noiDung);
            ps.setNString(2, noiDung);
            ps.setString(3, noiDung);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    danhSach.add(docNhanVien(rs));
                }
            }
        }

        return danhSach;
    }

    public String taoMaNhanVien() throws SQLException {
        String sql = "SELECT MAX(TRY_CONVERT(BIGINT, "
                + "SUBSTRING(maNhanVien, 3, 28))) AS soLonNhat "
                + "FROM dbo.NhanVien "
                + "WHERE maNhanVien LIKE 'NV%'";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                long soMoi = rs.getLong("soLonNhat") + 1;
                return String.format(
                        java.util.Locale.ROOT, "NV%03d", soMoi);
            }
        }

        return "NV001";
    }

    public boolean themNhanVien(NhanVien nhanVien) throws SQLException {
        String sql = "INSERT INTO dbo.NhanVien "
                + "(maNhanVien, maPhanQuyen, hoTen, soDienThoai, "
                + "email, ngaySinh, ngayVaoLam, trangThai) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
            ps.setString(1, nhanVien.getMaNhanVien());
            ps.setString(2, nhanVien.getMaPhanQuyen());
            ps.setNString(3, nhanVien.getHoTen());

            if (nhanVien.getSoDienThoai() == null) {
                ps.setNull(4, Types.VARCHAR);
            } else {
                ps.setString(4, nhanVien.getSoDienThoai());
            }

            if (nhanVien.getEmail() == null) {
                ps.setNull(5, Types.VARCHAR);
            } else {
                ps.setString(5, nhanVien.getEmail());
            }

            if (nhanVien.getNgaySinh() == null) {
                ps.setNull(6, Types.DATE);
            } else {
                ps.setDate(6, Date.valueOf(nhanVien.getNgaySinh()));
            }

            ps.setDate(7, Date.valueOf(nhanVien.getNgayVaoLam()));
            ps.setNString(8, nhanVien.getTrangThai());

            return ps.executeUpdate() > 0;
        }
    }

    public boolean suaNhanVien(NhanVien nhanVien) throws SQLException {
        String sql = "UPDATE dbo.NhanVien "
                + "SET maPhanQuyen = ?, hoTen = ?, soDienThoai = ?, "
                + "email = ?, ngaySinh = ?, ngayVaoLam = ?, "
                + "trangThai = ? WHERE maNhanVien = ?";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
            ps.setString(1, nhanVien.getMaPhanQuyen());
            ps.setNString(2, nhanVien.getHoTen());

            if (nhanVien.getSoDienThoai() == null) {
                ps.setNull(3, Types.VARCHAR);
            } else {
                ps.setString(3, nhanVien.getSoDienThoai());
            }

            if (nhanVien.getEmail() == null) {
                ps.setNull(4, Types.VARCHAR);
            } else {
                ps.setString(4, nhanVien.getEmail());
            }

            if (nhanVien.getNgaySinh() == null) {
                ps.setNull(5, Types.DATE);
            } else {
                ps.setDate(5, Date.valueOf(nhanVien.getNgaySinh()));
            }

            ps.setDate(6, Date.valueOf(nhanVien.getNgayVaoLam()));
            ps.setNString(7, nhanVien.getTrangThai());
            ps.setString(8, nhanVien.getMaNhanVien());

            return ps.executeUpdate() > 0;
        }
    }

    public boolean capNhatTrangThai(String maNhanVien, String trangThai) throws SQLException {
        String sql = "UPDATE dbo.NhanVien "
                + "SET trangThai = ? WHERE maNhanVien = ?";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
            ps.setNString(1, trangThai);
            ps.setString(2, maNhanVien);

            return ps.executeUpdate() > 0;
        }
    }
}