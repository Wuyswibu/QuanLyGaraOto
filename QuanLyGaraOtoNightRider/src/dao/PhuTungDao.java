package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;

import connectDB.ConnectDB;
import entity.PhuTung;

public class PhuTungDao {

    private Connection ketNoi() throws SQLException {
        Connection conn = ConnectDB.getConnection();

        if (conn == null || conn.isClosed()) {
            throw new SQLException("Chưa kết nối cơ sở dữ liệu.");
        }

        return conn;
    }

    private PhuTung docPhuTung(ResultSet rs) throws SQLException {
        return new PhuTung(
                rs.getString("maPhuTung"),
                rs.getString("maPhieuSuaChua"),
                rs.getString("tenPhuTung"),
                rs.getString("moTa"),
                rs.getString("donViTinh"),
                rs.getBigDecimal("donGia"),
                rs.getInt("soLuongTon"),
                rs.getInt("mucTonToiThieu"),
                rs.getString("trangThai"));
    }

    public ArrayList<PhuTung> layDanhSachPhuTung() throws SQLException {
        ArrayList<PhuTung> danhSach = new ArrayList<>();

        String sql = "SELECT * FROM dbo.PhuTung ORDER BY maPhuTung";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql);
        ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                danhSach.add(docPhuTung(rs));
            }
        }

        return danhSach;
    }

    public PhuTung timTheoMa(String maPhuTung) throws SQLException {
        String sql = "SELECT * FROM dbo.PhuTung WHERE maPhuTung = ?";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
            ps.setString(1, maPhuTung);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return docPhuTung(rs);
                }
            }
        }

        return null;
    }

    public ArrayList<PhuTung> timKiem(String tuKhoa) throws SQLException {
        ArrayList<PhuTung> danhSach = new ArrayList<>();

        String sql = "SELECT * FROM dbo.PhuTung "
                + "WHERE maPhuTung LIKE ? OR tenPhuTung LIKE ? "
                + "ORDER BY maPhuTung";

        String noiDung = "%" + tuKhoa.trim() + "%";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
            ps.setString(1, noiDung);
            ps.setNString(2, noiDung);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    danhSach.add(docPhuTung(rs));
                }
            }
        }

        return danhSach;
    }

    public ArrayList<PhuTung> timTheoMaPhieuSuaChua(String maPhieuSuaChua) throws SQLException {
        ArrayList<PhuTung> danhSach = new ArrayList<>();

        String sql = "SELECT * FROM dbo.PhuTung "
                + "WHERE maPhieuSuaChua = ? ORDER BY maPhuTung";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
            ps.setString(1, maPhieuSuaChua);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    danhSach.add(docPhuTung(rs));
                }
            }
        }

        return danhSach;
    }

    public ArrayList<PhuTung> layDanhSachSapHetHang() throws SQLException {
        ArrayList<PhuTung> danhSach = new ArrayList<>();

        String sql = "SELECT * FROM dbo.PhuTung "
                + "WHERE soLuongTon <= mucTonToiThieu "
                + "ORDER BY soLuongTon, maPhuTung";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                danhSach.add(docPhuTung(rs));
            }
        }

        return danhSach;
    }

    public String taoMaPhuTung() throws SQLException {
        String sql = "SELECT MAX(TRY_CONVERT(BIGINT, "
                + "SUBSTRING(maPhuTung, 3, 28))) AS soLonNhat "
                + "FROM dbo.PhuTung WHERE maPhuTung LIKE 'PU%'";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql);
        ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                long soMoi = rs.getLong("soLonNhat") + 1;

                return String.format(
                        java.util.Locale.ROOT, "PU%03d", soMoi);
            }
        }

        return "PU001";
    }

    public boolean themPhuTung(PhuTung phuTung) throws SQLException {
        String sql = "INSERT INTO dbo.PhuTung "
                + "(maPhuTung, maPhieuSuaChua, tenPhuTung, moTa, "
                + "donViTinh, donGia, soLuongTon, "
                + "mucTonToiThieu, trangThai) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
            ps.setString(1, phuTung.getMaPhuTung());

            if (phuTung.getMaPhieuSuaChua() == null) {
                ps.setNull(2, Types.VARCHAR);
            } else {
                ps.setString(2, phuTung.getMaPhieuSuaChua());
            }

            ps.setNString(3, phuTung.getTenPhuTung());

            if (phuTung.getMoTa() == null) {
                ps.setNull(4, Types.NVARCHAR);
            } else {
                ps.setNString(4, phuTung.getMoTa());
            }

            ps.setNString(5, phuTung.getDonViTinh());
            ps.setBigDecimal(6, phuTung.getDonGia());
            ps.setInt(7, phuTung.getSoLuongTon());
            ps.setInt(8, phuTung.getMucTonToiThieu());
            ps.setNString(9, phuTung.getTrangThai());

            return ps.executeUpdate() > 0;
        }
    }

    public boolean suaPhuTung(PhuTung phuTung) throws SQLException {
        String sql = "UPDATE dbo.PhuTung "
                + "SET maPhieuSuaChua = ?, tenPhuTung = ?, moTa = ?, "
                + "donViTinh = ?, donGia = ?, mucTonToiThieu = ?, "
                + "trangThai = ? WHERE maPhuTung = ?";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
            if (phuTung.getMaPhieuSuaChua() == null) {
                ps.setNull(1, Types.VARCHAR);
            } else {
                ps.setString(1, phuTung.getMaPhieuSuaChua());
            }

            ps.setNString(2, phuTung.getTenPhuTung());

            if (phuTung.getMoTa() == null) {
                ps.setNull(3, Types.NVARCHAR);
            } else {
                ps.setNString(3, phuTung.getMoTa());
            }

            ps.setNString(4, phuTung.getDonViTinh());
            ps.setBigDecimal(5, phuTung.getDonGia());
            ps.setInt(6, phuTung.getMucTonToiThieu());
            ps.setNString(7, phuTung.getTrangThai());
            ps.setString(8, phuTung.getMaPhuTung());

            return ps.executeUpdate() > 0;
        }
    }

    public boolean capNhatTrangThai(String maPhuTung, String trangThai) throws SQLException {
        String sql = "UPDATE dbo.PhuTung "
                + "SET trangThai = ? WHERE maPhuTung = ?";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
            ps.setNString(1, trangThai);
            ps.setString(2, maPhuTung);

            return ps.executeUpdate() > 0;
        }
    }

    public boolean tangTonKho(String maPhuTung, int soLuong) throws SQLException {
        if (soLuong <= 0) {
            throw new IllegalArgumentException(
                    "Số lượng tăng phải lớn hơn 0.");
        }

        String sql = "UPDATE dbo.PhuTung "
                + "SET soLuongTon = soLuongTon + ? "
                + "WHERE maPhuTung = ? "
                + "AND soLuongTon <= 2147483647 - ?";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
            ps.setInt(1, soLuong);
            ps.setString(2, maPhuTung);
            ps.setInt(3, soLuong);

            return ps.executeUpdate() > 0;
        }
    }

    public boolean giamTonKho(String maPhuTung, int soLuong) throws SQLException {
        if (soLuong <= 0) {
            throw new IllegalArgumentException(
                    "Số lượng giảm phải lớn hơn 0.");
        }

        String sql = "UPDATE dbo.PhuTung "
                + "SET soLuongTon = soLuongTon - ? "
                + "WHERE maPhuTung = ? AND soLuongTon >= ?";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
            ps.setInt(1, soLuong);
            ps.setString(2, maPhuTung);
            ps.setInt(3, soLuong);

            return ps.executeUpdate() > 0;
        }
    }
}