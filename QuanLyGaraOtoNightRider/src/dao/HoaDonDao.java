package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

import connectDB.ConnectDB;
import entity.HoaDon;

public class HoaDonDao {

    private Connection ketNoi() throws SQLException {
        Connection conn = ConnectDB.getConnection();

        if (conn == null || conn.isClosed()) {
            throw new SQLException("Chưa kết nối cơ sở dữ liệu.");
        }

        return conn;
    }

    private HoaDon docHoaDon(ResultSet rs) throws SQLException {
        Timestamp ngayLap = rs.getTimestamp("ngayLap");

        return new HoaDon(
                rs.getString("maHoaDon"),
                rs.getString("maPhieuSuaChua"),
                rs.getString("maKhachHang"),
                rs.getString("maPhuongTien"),
                rs.getString("maNhanVien"),
                ngayLap == null ? null : ngayLap.toLocalDateTime(),
                rs.getBigDecimal("vat"),
                rs.getString("trangThai"));
    }

    public ArrayList<HoaDon> layDanhSachHoaDon()
            throws SQLException {
        ArrayList<HoaDon> danhSach = new ArrayList<>();

        String sql = "SELECT * FROM dbo.HoaDon "
                + "ORDER BY ngayLap DESC, maHoaDon DESC";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                danhSach.add(docHoaDon(rs));
            }
        }

        return danhSach;
    }

    public HoaDon timTheoMa(String maHoaDon)
            throws SQLException {
        String sql = "SELECT * FROM dbo.HoaDon WHERE maHoaDon = ?";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
            ps.setString(1, maHoaDon);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return docHoaDon(rs);
                }
            }
        }

        return null;
    }

    public HoaDon timTheoMaPhieuSuaChua(String maPhieuSuaChua)
            throws SQLException {
        String sql = "SELECT * FROM dbo.HoaDon "
                + "WHERE maPhieuSuaChua = ?";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
            ps.setString(1, maPhieuSuaChua);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return docHoaDon(rs);
                }
            }
        }

        return null;
    }

    public ArrayList<HoaDon> timTheoMaKhachHang(String maKhachHang)
            throws SQLException {
        ArrayList<HoaDon> danhSach = new ArrayList<>();

        String sql = "SELECT * FROM dbo.HoaDon "
                + "WHERE maKhachHang = ? "
                + "ORDER BY ngayLap DESC, maHoaDon DESC";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
            ps.setString(1, maKhachHang);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    danhSach.add(docHoaDon(rs));
                }
            }
        }

        return danhSach;
    }

    public ArrayList<HoaDon> timTheoMaPhuongTien(String maPhuongTien)
            throws SQLException {
        ArrayList<HoaDon> danhSach = new ArrayList<>();

        String sql = "SELECT * FROM dbo.HoaDon "
                + "WHERE maPhuongTien = ? "
                + "ORDER BY ngayLap DESC, maHoaDon DESC";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
            ps.setString(1, maPhuongTien);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    danhSach.add(docHoaDon(rs));
                }
            }
        }

        return danhSach;
    }

    public ArrayList<HoaDon> timTheoTrangThai(String trangThai)
            throws SQLException {
        ArrayList<HoaDon> danhSach = new ArrayList<>();

        String sql = "SELECT * FROM dbo.HoaDon "
                + "WHERE trangThai = ? "
                + "ORDER BY ngayLap DESC, maHoaDon DESC";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
            ps.setNString(1, trangThai);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    danhSach.add(docHoaDon(rs));
                }
            }
        }

        return danhSach;
    }

    public ArrayList<HoaDon> timKiem(String tuKhoa)
            throws SQLException {
        ArrayList<HoaDon> danhSach = new ArrayList<>();

        String sql = "SELECT h.* "
                + "FROM dbo.HoaDon h "
                + "JOIN dbo.KhachHang k "
                + "ON h.maKhachHang = k.maKhachHang "
                + "JOIN dbo.PhuongTien p "
                + "ON h.maPhuongTien = p.maPhuongTien "
                + "WHERE h.maHoaDon LIKE ? "
                + "OR h.maPhieuSuaChua LIKE ? "
                + "OR k.hoTen LIKE ? "
                + "OR k.soDienThoai LIKE ? "
                + "OR p.bienSo LIKE ? "
                + "ORDER BY h.ngayLap DESC, h.maHoaDon DESC";

        String noiDung = "%" + tuKhoa.trim() + "%";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
            ps.setString(1, noiDung);
            ps.setString(2, noiDung);
            ps.setNString(3, noiDung);
            ps.setString(4, noiDung);
            ps.setString(5, noiDung);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    danhSach.add(docHoaDon(rs));
                }
            }
        }

        return danhSach;
    }

    public String taoMaHoaDon() throws SQLException {
        String sql = "SELECT MAX(TRY_CONVERT(BIGINT, "
                + "SUBSTRING(maHoaDon, 3, 28))) AS soLonNhat "
                + "FROM dbo.HoaDon WHERE maHoaDon LIKE 'HD%'";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                long soMoi = rs.getLong("soLonNhat") + 1;

                return String.format(
                        java.util.Locale.ROOT, "HD%04d", soMoi);
            }
        }

        return "HD0001";
    }

    public boolean themHoaDon(HoaDon hoaDon)
            throws SQLException {
        String sql = "INSERT INTO dbo.HoaDon "
                + "(maHoaDon, maPhieuSuaChua, maKhachHang, "
                + "maPhuongTien, maNhanVien, ngayLap, vat, trangThai) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
            ps.setString(1, hoaDon.getMaHoaDon());
            ps.setString(2, hoaDon.getMaPhieuSuaChua());
            ps.setString(3, hoaDon.getMaKhachHang());
            ps.setString(4, hoaDon.getMaPhuongTien());
            ps.setString(5, hoaDon.getMaNhanVien());
            ps.setTimestamp(6, Timestamp.valueOf(hoaDon.getNgayLap()));
            ps.setBigDecimal(7, hoaDon.getVat());
            ps.setNString(8, hoaDon.getTrangThai());

            return ps.executeUpdate() > 0;
        }
    }

    public boolean suaHoaDon(HoaDon hoaDon)
            throws SQLException {
        String sql = "UPDATE dbo.HoaDon "
                + "SET maPhieuSuaChua = ?, maKhachHang = ?, "
                + "maPhuongTien = ?, maNhanVien = ?, "
                + "ngayLap = ?, vat = ? "
                + "WHERE maHoaDon = ? AND trangThai = ?";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
            ps.setString(1, hoaDon.getMaPhieuSuaChua());
            ps.setString(2, hoaDon.getMaKhachHang());
            ps.setString(3, hoaDon.getMaPhuongTien());
            ps.setString(4, hoaDon.getMaNhanVien());
            ps.setTimestamp(5, Timestamp.valueOf(hoaDon.getNgayLap()));
            ps.setBigDecimal(6, hoaDon.getVat());
            ps.setString(7, hoaDon.getMaHoaDon());
            ps.setNString(8, HoaDon.CHUA_THANH_TOAN);

            return ps.executeUpdate() > 0;
        }
    }

    public boolean capNhatTrangThai(String maHoaDon,
            String trangThaiCu, String trangThaiMoi)
            throws SQLException {
        if (!HoaDon.CHUA_THANH_TOAN.equals(trangThaiMoi)
                && !HoaDon.DA_THANH_TOAN.equals(trangThaiMoi)
                && !HoaDon.DA_HUY.equals(trangThaiMoi)) {
            throw new IllegalArgumentException(
                    "Trạng thái hóa đơn không hợp lệ.");
        }

        String sql = "UPDATE dbo.HoaDon "
                + "SET trangThai = ? "
                + "WHERE maHoaDon = ? AND trangThai = ?";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
            ps.setNString(1, trangThaiMoi);
            ps.setString(2, maHoaDon);
            ps.setNString(3, trangThaiCu);

            return ps.executeUpdate() > 0;
        }
    }
}