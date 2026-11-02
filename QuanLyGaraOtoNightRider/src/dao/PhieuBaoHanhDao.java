package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;

import connectDB.ConnectDB;
import entity.PhieuBaoHanh;

public class PhieuBaoHanhDao {

    private Connection ketNoi() throws SQLException {
        Connection conn = ConnectDB.getConnection();

        if (conn == null || conn.isClosed()) {
            throw new SQLException("Chưa kết nối cơ sở dữ liệu.");
        }

        return conn;
    }

    private PhieuBaoHanh docPhieu(ResultSet rs) throws SQLException {
        Timestamp ngayYeuCau = rs.getTimestamp("ngayYeuCau");

        return new PhieuBaoHanh(
                rs.getString("maBaoHanh"),
                rs.getString("maPhieuSuaChua"),
                rs.getString("maPhuongTien"),
                ngayYeuCau == null
                        ? null : ngayYeuCau.toLocalDateTime(),
                rs.getString("noiDungYeuCau"),
                rs.getString("ketQuaKiemTra"),
                rs.getString("ketQuaXuLy"),
                rs.getString("lyDoTuChoi"),
                rs.getString("trangThai"));
    }

    public ArrayList<PhieuBaoHanh> layDanhSachPhieuBaoHanh() throws SQLException {
        ArrayList<PhieuBaoHanh> danhSach = new ArrayList<>();

        String sql = "SELECT * FROM dbo.PhieuBaoHanh "
                + "ORDER BY ngayYeuCau DESC, maBaoHanh DESC";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                danhSach.add(docPhieu(rs));
            }
        }

        return danhSach;
    }

    public PhieuBaoHanh timTheoMa(String maBaoHanh) throws SQLException {
        String sql = "SELECT * FROM dbo.PhieuBaoHanh "
                + "WHERE maBaoHanh = ?";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
            ps.setString(1, maBaoHanh);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return docPhieu(rs);
                }
            }
        }

        return null;
    }

    public ArrayList<PhieuBaoHanh> timTheoMaPhieuSuaChua(String maPhieuSuaChua) throws SQLException {
        ArrayList<PhieuBaoHanh> danhSach = new ArrayList<>();

        String sql = "SELECT * FROM dbo.PhieuBaoHanh "
                + "WHERE maPhieuSuaChua = ? "
                + "ORDER BY ngayYeuCau DESC, maBaoHanh DESC";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
            ps.setString(1, maPhieuSuaChua);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    danhSach.add(docPhieu(rs));
                }
            }
        }

        return danhSach;
    }

    public ArrayList<PhieuBaoHanh> timTheoMaPhuongTien(String maPhuongTien) throws SQLException {
        ArrayList<PhieuBaoHanh> danhSach = new ArrayList<>();

        String sql = "SELECT * FROM dbo.PhieuBaoHanh "
                + "WHERE maPhuongTien = ? "
                + "ORDER BY ngayYeuCau DESC, maBaoHanh DESC";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
            ps.setString(1, maPhuongTien);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    danhSach.add(docPhieu(rs));
                }
            }
        }

        return danhSach;
    }

    public ArrayList<PhieuBaoHanh> timTheoTrangThai(String trangThai)  throws SQLException {
        ArrayList<PhieuBaoHanh> danhSach = new ArrayList<>();

        String sql = "SELECT * FROM dbo.PhieuBaoHanh "
                + "WHERE trangThai = ? "
                + "ORDER BY ngayYeuCau DESC, maBaoHanh DESC";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
            ps.setNString(1, trangThai);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    danhSach.add(docPhieu(rs));
                }
            }
        }

        return danhSach;
    }

    public ArrayList<PhieuBaoHanh> timKiem(String tuKhoa) throws SQLException {
        ArrayList<PhieuBaoHanh> danhSach = new ArrayList<>();

        String sql = "SELECT b.* "
                + "FROM dbo.PhieuBaoHanh b "
                + "JOIN dbo.PhuongTien p "
                + "ON b.maPhuongTien = p.maPhuongTien "
                + "JOIN dbo.KhachHang k "
                + "ON p.maKhachHang = k.maKhachHang "
                + "WHERE b.maBaoHanh LIKE ? "
                + "OR b.maPhieuSuaChua LIKE ? "
                + "OR p.bienSo LIKE ? "
                + "OR k.hoTen LIKE ? "
                + "OR k.soDienThoai LIKE ? "
                + "ORDER BY b.ngayYeuCau DESC, b.maBaoHanh DESC";

        String noiDung = "%" + tuKhoa.trim() + "%";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
            ps.setString(1, noiDung);
            ps.setString(2, noiDung);
            ps.setString(3, noiDung);
            ps.setNString(4, noiDung);
            ps.setString(5, noiDung);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    danhSach.add(docPhieu(rs));
                }
            }
        }

        return danhSach;
    }

    public String taoMaBaoHanh() throws SQLException {
        String sql = "SELECT MAX(TRY_CONVERT(BIGINT, "
                + "SUBSTRING(maBaoHanh, 4, 27))) AS soLonNhat "
                + "FROM dbo.PhieuBaoHanh "
                + "WHERE maBaoHanh LIKE 'PBH%'";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                long soMoi = rs.getLong("soLonNhat") + 1;

                return String.format(
                        java.util.Locale.ROOT, "PBH%03d", soMoi);
            }
        }

        return "PBH001";
    }

    public boolean themPhieuBaoHanh(PhieuBaoHanh phieu) throws SQLException {
        String sql = "INSERT INTO dbo.PhieuBaoHanh "
                + "(maBaoHanh, maPhieuSuaChua, maPhuongTien, "
                + "ngayYeuCau, noiDungYeuCau, ketQuaKiemTra, "
                + "ketQuaXuLy, lyDoTuChoi, trangThai) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
            ps.setString(1, phieu.getMaBaoHanh());
            ps.setString(2, phieu.getMaPhieuSuaChua());
            ps.setString(3, phieu.getMaPhuongTien());
            ps.setTimestamp(
                    4, Timestamp.valueOf(phieu.getNgayYeuCau()));
            ps.setNString(5, phieu.getNoiDungYeuCau());

            if (phieu.getKetQuaKiemTra() == null) {
                ps.setNull(6, Types.NVARCHAR);
            } else {
                ps.setNString(6, phieu.getKetQuaKiemTra());
            }

            if (phieu.getKetQuaXuLy() == null) {
                ps.setNull(7, Types.NVARCHAR);
            } else {
                ps.setNString(7, phieu.getKetQuaXuLy());
            }

            if (phieu.getLyDoTuChoi() == null) {
                ps.setNull(8, Types.NVARCHAR);
            } else {
                ps.setNString(8, phieu.getLyDoTuChoi());
            }

            ps.setNString(9, phieu.getTrangThai());

            return ps.executeUpdate() > 0;
        }
    }

    public boolean suaPhieuBaoHanh(PhieuBaoHanh phieu) throws SQLException {
        String sql = "UPDATE dbo.PhieuBaoHanh "
                + "SET maPhieuSuaChua = ?, maPhuongTien = ?, "
                + "ngayYeuCau = ?, noiDungYeuCau = ?, "
                + "ketQuaKiemTra = ?, ketQuaXuLy = ?, "
                + "lyDoTuChoi = ?, trangThai = ? "
                + "WHERE maBaoHanh = ?";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
            ps.setString(1, phieu.getMaPhieuSuaChua());
            ps.setString(2, phieu.getMaPhuongTien());
            ps.setTimestamp(
                    3, Timestamp.valueOf(phieu.getNgayYeuCau()));
            ps.setNString(4, phieu.getNoiDungYeuCau());

            if (phieu.getKetQuaKiemTra() == null) {
                ps.setNull(5, Types.NVARCHAR);
            } else {
                ps.setNString(5, phieu.getKetQuaKiemTra());
            }

            if (phieu.getKetQuaXuLy() == null) {
                ps.setNull(6, Types.NVARCHAR);
            } else {
                ps.setNString(6, phieu.getKetQuaXuLy());
            }

            if (phieu.getLyDoTuChoi() == null) {
                ps.setNull(7, Types.NVARCHAR);
            } else {
                ps.setNString(7, phieu.getLyDoTuChoi());
            }

            ps.setNString(8, phieu.getTrangThai());
            ps.setString(9, phieu.getMaBaoHanh());

            return ps.executeUpdate() > 0;
        }
    }

    public boolean capNhatTrangThai(String maBaoHanh, String trangThai) throws SQLException {
        String sql = "UPDATE dbo.PhieuBaoHanh "
                + "SET trangThai = ? WHERE maBaoHanh = ?";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
            ps.setNString(1, trangThai);
            ps.setString(2, maBaoHanh);

            return ps.executeUpdate() > 0;
        }
    }
}