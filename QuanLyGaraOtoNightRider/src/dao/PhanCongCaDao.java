package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;

import connectDB.ConnectDB;
import entity.PhanCongCa;

public class PhanCongCaDao {

    private Connection ketNoi() throws SQLException {
        Connection conn = ConnectDB.getConnection();

        if (conn == null || conn.isClosed()) {
            throw new SQLException("Chưa kết nối cơ sở dữ liệu.");
        }

        return conn;
    }

    private PhanCongCa docPhanCongCa(ResultSet rs) throws SQLException {
        Date ngayLamViec = rs.getDate("ngayLamViec");

        return new PhanCongCa(
                rs.getString("maPhanCongCa"),
                rs.getString("maNhanVien"),
                rs.getString("maCa"),
                ngayLamViec == null
                        ? null : ngayLamViec.toLocalDate(),
                rs.getString("trangThai"),
                rs.getString("ghiChu"));
    }

    public ArrayList<PhanCongCa> layDanhSachPhanCongCa() throws SQLException {
        ArrayList<PhanCongCa> danhSach = new ArrayList<>();

        String sql = "SELECT * FROM dbo.PhanCongCa "
                + "ORDER BY ngayLamViec DESC, maPhanCongCa";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                danhSach.add(docPhanCongCa(rs));
            }
        }

        return danhSach;
    }

    public PhanCongCa timTheoMa(String maPhanCongCa) throws SQLException {
        String sql = "SELECT * FROM dbo.PhanCongCa "
                + "WHERE maPhanCongCa = ?";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
            ps.setString(1, maPhanCongCa);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return docPhanCongCa(rs);
                }
            }
        }

        return null;
    }

    public ArrayList<PhanCongCa> timTheoMaNhanVien(String maNhanVien) throws SQLException {
        ArrayList<PhanCongCa> danhSach = new ArrayList<>();

        String sql = "SELECT * FROM dbo.PhanCongCa "
                + "WHERE maNhanVien = ? "
                + "ORDER BY ngayLamViec DESC, maPhanCongCa";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
            ps.setString(1, maNhanVien);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    danhSach.add(docPhanCongCa(rs));
                }
            }
        }

        return danhSach;
    }

    public ArrayList<PhanCongCa> timTheoNgay(LocalDate ngayLamViec) throws SQLException {
        ArrayList<PhanCongCa> danhSach = new ArrayList<>();

        String sql = "SELECT * FROM dbo.PhanCongCa "
                + "WHERE ngayLamViec = ? "
                + "ORDER BY maCa, maNhanVien";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(ngayLamViec));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    danhSach.add(docPhanCongCa(rs));
                }
            }
        }

        return danhSach;
    }

    public ArrayList<PhanCongCa> timTheoMaCa(String maCa) throws SQLException {
        ArrayList<PhanCongCa> danhSach = new ArrayList<>();

        String sql = "SELECT * FROM dbo.PhanCongCa "
                + "WHERE maCa = ? "
                + "ORDER BY ngayLamViec DESC, maNhanVien";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
            ps.setString(1, maCa);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    danhSach.add(docPhanCongCa(rs));
                }
            }
        }

        return danhSach;
    }

    public String taoMaPhanCongCa() throws SQLException {
        String sql = "SELECT MAX(TRY_CONVERT(BIGINT, "
                + "SUBSTRING(maPhanCongCa, 4, 27))) AS soLonNhat "
                + "FROM dbo.PhanCongCa "
                + "WHERE maPhanCongCa LIKE 'PCC%'";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                long soMoi = rs.getLong("soLonNhat") + 1;

                return String.format(
                        java.util.Locale.ROOT, "PCC%03d", soMoi);
            }
        }

        return "PCC001";
    }

    public boolean themPhanCongCa(PhanCongCa phanCong) throws SQLException {
        String sql = "INSERT INTO dbo.PhanCongCa "
                + "(maPhanCongCa, maNhanVien, maCa, "
                + "ngayLamViec, trangThai, ghiChu) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
            ps.setString(1, phanCong.getMaPhanCongCa());
            ps.setString(2, phanCong.getMaNhanVien());
            ps.setString(3, phanCong.getMaCa());
            ps.setDate(4, Date.valueOf(phanCong.getNgayLamViec()));
            ps.setNString(5, phanCong.getTrangThai());

            if (phanCong.getGhiChu() == null) {
                ps.setNull(6, Types.NVARCHAR);
            } else {
                ps.setNString(6, phanCong.getGhiChu());
            }

            return ps.executeUpdate() > 0;
        }
    }

    public boolean suaPhanCongCa(PhanCongCa phanCong) throws SQLException {
        String sql = "UPDATE dbo.PhanCongCa "
                + "SET maNhanVien = ?, maCa = ?, ngayLamViec = ?, "
                + "trangThai = ?, ghiChu = ? "
                + "WHERE maPhanCongCa = ?";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
            ps.setString(1, phanCong.getMaNhanVien());
            ps.setString(2, phanCong.getMaCa());
            ps.setDate(3, Date.valueOf(phanCong.getNgayLamViec()));
            ps.setNString(4, phanCong.getTrangThai());

            if (phanCong.getGhiChu() == null) {
                ps.setNull(5, Types.NVARCHAR);
            } else {
                ps.setNString(5, phanCong.getGhiChu());
            }

            ps.setString(6, phanCong.getMaPhanCongCa());

            return ps.executeUpdate() > 0;
        }
    }

    public boolean capNhatTrangThai(String maPhanCongCa, String trangThai) throws SQLException {
        String sql = "UPDATE dbo.PhanCongCa "
                + "SET trangThai = ? WHERE maPhanCongCa = ?";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
            ps.setNString(1, trangThai);
            ps.setString(2, maPhanCongCa);

            return ps.executeUpdate() > 0;
        }
    }
}