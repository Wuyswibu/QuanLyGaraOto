package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;

import connectDB.ConnectDB;
import entity.HangMucDichVu;

public class HangMucDichVuDao {

    private Connection ketNoi() throws SQLException {
        Connection conn = ConnectDB.getConnection();

        if (conn == null || conn.isClosed()) {
            throw new SQLException("Chưa kết nối cơ sở dữ liệu.");
        }

        return conn;
    }

    private HangMucDichVu docHangMuc(ResultSet rs) throws SQLException {
        return new HangMucDichVu(
                rs.getString("maHangMuc"),
                rs.getString("maPhieuSuaChua"),
                rs.getString("maDichVu"),
                rs.getString("tenHangMuc"),
                rs.getString("moTa"),
                rs.getString("trangThai"),
                rs.getBigDecimal("chiPhiDuKien"),
                rs.getBigDecimal("chiPhiThucTe"),
                rs.getString("ketQua"),
                rs.getString("ghiChu"));
    }

    public ArrayList<HangMucDichVu> layDanhSachHangMuc() throws SQLException {
        ArrayList<HangMucDichVu> danhSach = new ArrayList<>();

        String sql = "SELECT * FROM dbo.HangMucDichVu "
                + "ORDER BY maHangMuc";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                danhSach.add(docHangMuc(rs));
            }
        }

        return danhSach;
    }

    public HangMucDichVu timTheoMa(String maHangMuc) throws SQLException {
        String sql = "SELECT * FROM dbo.HangMucDichVu "
                + "WHERE maHangMuc = ?";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
            ps.setString(1, maHangMuc);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return docHangMuc(rs);
                }
            }
        }

        return null;
    }

    public ArrayList<HangMucDichVu> timTheoMaPhieuSuaChua(String maPhieuSuaChua) throws SQLException {
        ArrayList<HangMucDichVu> danhSach = new ArrayList<>();

        String sql = "SELECT * FROM dbo.HangMucDichVu "
                + "WHERE maPhieuSuaChua = ? ORDER BY maHangMuc";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
            ps.setString(1, maPhieuSuaChua);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    danhSach.add(docHangMuc(rs));
                }
            }
        }

        return danhSach;
    }

    public String taoMaHangMuc() throws SQLException {
        String sql = "SELECT MAX(TRY_CONVERT(BIGINT, "
                + "SUBSTRING(maHangMuc, 5, 26))) AS soLonNhat "
                + "FROM dbo.HangMucDichVu "
                + "WHERE maHangMuc LIKE 'HMDV%'";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                long soMoi = rs.getLong("soLonNhat") + 1;

                return String.format(
                        java.util.Locale.ROOT, "HMDV%03d", soMoi);
            }
        }

        return "HMDV001";
    }

    public boolean themHangMuc(HangMucDichVu hangMuc) throws SQLException {
        String sql = "INSERT INTO dbo.HangMucDichVu "
                + "(maHangMuc, maPhieuSuaChua, maDichVu, "
                + "tenHangMuc, moTa, trangThai, chiPhiDuKien, "
                + "chiPhiThucTe, ketQua, ghiChu) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
            ps.setString(1, hangMuc.getMaHangMuc());
            ps.setString(2, hangMuc.getMaPhieuSuaChua());
            ps.setString(3, hangMuc.getMaDichVu());
            ps.setNString(4, hangMuc.getTenHangMuc());

            if (hangMuc.getMoTa() == null) {
                ps.setNull(5, Types.NVARCHAR);
            } else {
                ps.setNString(5, hangMuc.getMoTa());
            }

            ps.setNString(6, hangMuc.getTrangThai());
            ps.setBigDecimal(7, hangMuc.getChiPhiDuKien());
            ps.setBigDecimal(8, hangMuc.getChiPhiThucTe());

            if (hangMuc.getKetQua() == null) {
                ps.setNull(9, Types.NVARCHAR);
            } else {
                ps.setNString(9, hangMuc.getKetQua());
            }

            if (hangMuc.getGhiChu() == null) {
                ps.setNull(10, Types.NVARCHAR);
            } else {
                ps.setNString(10, hangMuc.getGhiChu());
            }

            return ps.executeUpdate() > 0;
        }
    }

    public boolean suaHangMuc(HangMucDichVu hangMuc) throws SQLException {
        String sql = "UPDATE dbo.HangMucDichVu "
                + "SET maPhieuSuaChua = ?, maDichVu = ?, "
                + "tenHangMuc = ?, moTa = ?, trangThai = ?, "
                + "chiPhiDuKien = ?, chiPhiThucTe = ?, "
                + "ketQua = ?, ghiChu = ? WHERE maHangMuc = ?";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
            ps.setString(1, hangMuc.getMaPhieuSuaChua());
            ps.setString(2, hangMuc.getMaDichVu());
            ps.setNString(3, hangMuc.getTenHangMuc());

            if (hangMuc.getMoTa() == null) {
                ps.setNull(4, Types.NVARCHAR);
            } else {
                ps.setNString(4, hangMuc.getMoTa());
            }

            ps.setNString(5, hangMuc.getTrangThai());
            ps.setBigDecimal(6, hangMuc.getChiPhiDuKien());
            ps.setBigDecimal(7, hangMuc.getChiPhiThucTe());

            if (hangMuc.getKetQua() == null) {
                ps.setNull(8, Types.NVARCHAR);
            } else {
                ps.setNString(8, hangMuc.getKetQua());
            }

            if (hangMuc.getGhiChu() == null) {
                ps.setNull(9, Types.NVARCHAR);
            } else {
                ps.setNString(9, hangMuc.getGhiChu());
            }

            ps.setString(10, hangMuc.getMaHangMuc());

            return ps.executeUpdate() > 0;
        }
    }

    public boolean capNhatTrangThai(String maHangMuc,String trangThai) throws SQLException {
        String sql = "UPDATE dbo.HangMucDichVu "
                + "SET trangThai = ? WHERE maHangMuc = ?";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
            ps.setNString(1, trangThai);
            ps.setString(2, maHangMuc);

            return ps.executeUpdate() > 0;
        }
    }
    
    public boolean hoanThanhHangMuc(
            String maHangMuc, String maPhieuSuaChua,
            java.math.BigDecimal chiPhiThucTe) throws SQLException {

        String sql = "UPDATE dbo.HangMucDichVu "
                + "SET chiPhiThucTe = ?, trangThai = N'Hoàn thành' "
                + "WHERE maHangMuc = ? AND maPhieuSuaChua = ?";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
            ps.setBigDecimal(1, chiPhiThucTe);
            ps.setString(2, maHangMuc);
            ps.setString(3, maPhieuSuaChua);

            return ps.executeUpdate() == 1;
        }
    }
}