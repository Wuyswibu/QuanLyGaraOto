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

    // Lấy kết nối CSDL, tự động kết nối lại nếu bị đóng
    private Connection ketNoi() throws SQLException {
        Connection conn = ConnectDB.getConnection();

        if (conn == null || conn.isClosed()) {
            conn = ConnectDB.getInstance().connect();
        }

        return conn;
    }

    // Đọc 1 dòng từ ResultSet sang Object Entity PhanCongCa
    private PhanCongCa docPhanCongCa(ResultSet rs) throws SQLException {
        Date ngayLamViec = rs.getDate("ngayLamViec");

        return new PhanCongCa(
                rs.getString("maPhanCongCa"),
                rs.getString("maNhanVien"),
                rs.getString("maCa"),
                ngayLamViec == null ? null : ngayLamViec.toLocalDate(),
                rs.getString("trangThai"),
                rs.getString("ghiChu"));
    }

    // Lấy danh sách tất cả phân công ca
    public ArrayList<PhanCongCa> layDanhSachPhanCongCa() throws SQLException {
        ArrayList<PhanCongCa> danhSach = new ArrayList<>();

        String sql = "SELECT maPhanCongCa, maNhanVien, maCa, ngayLamViec, trangThai, ghiChu "
                + "FROM dbo.PhanCongCa "
                + "ORDER BY ngayLamViec DESC, maPhanCongCa";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                danhSach.add(docPhanCongCa(rs));
            }
        }

        return danhSach;
    }

    // Tìm phân công ca theo Mã
    public PhanCongCa timTheoMa(String maPhanCongCa) throws SQLException {
        String sql = "SELECT maPhanCongCa, maNhanVien, maCa, ngayLamViec, trangThai, ghiChu "
                + "FROM dbo.PhanCongCa "
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

    // Tìm phân công ca theo Mã Nhân Viên
    public ArrayList<PhanCongCa> timTheoMaNhanVien(String maNhanVien) throws SQLException {
        ArrayList<PhanCongCa> danhSach = new ArrayList<>();

        String sql = "SELECT maPhanCongCa, maNhanVien, maCa, ngayLamViec, trangThai, ghiChu "
                + "FROM dbo.PhanCongCa "
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

    // Tìm phân công ca theo Ngày làm việc
    public ArrayList<PhanCongCa> timTheoNgay(LocalDate ngayLamViec) throws SQLException {
        ArrayList<PhanCongCa> danhSach = new ArrayList<>();

        String sql = "SELECT maPhanCongCa, maNhanVien, maCa, ngayLamViec, trangThai, ghiChu "
                + "FROM dbo.PhanCongCa "
                + "WHERE ngayLamViec = ? "
                + "ORDER BY maCa, maNhanVien";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
            ps.setDate(1, ngayLamViec != null ? Date.valueOf(ngayLamViec) : null);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    danhSach.add(docPhanCongCa(rs));
                }
            }
        }

        return danhSach;
    }

    // Tìm phân công ca theo Mã Ca làm việc
    public ArrayList<PhanCongCa> timTheoMaCa(String maCa) throws SQLException {
        ArrayList<PhanCongCa> danhSach = new ArrayList<>();

        String sql = "SELECT maPhanCongCa, maNhanVien, maCa, ngayLamViec, trangThai, ghiChu "
                + "FROM dbo.PhanCongCa "
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

    // Tự động sinh mã phân công ca tiếp theo (dạng PCC001, PCC002,...)
    public String taoMaPhanCongCa() throws SQLException {
        String sql = "SELECT MAX(TRY_CONVERT(BIGINT, SUBSTRING(maPhanCongCa, 4, 27))) AS soLonNhat "
                + "FROM dbo.PhanCongCa "
                + "WHERE maPhanCongCa LIKE 'PCC%'";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                long max = rs.getLong("soLonNhat");
                if (!rs.wasNull()) {
                    return String.format(java.util.Locale.ROOT, "PCC%03d", max + 1);
                }
            }
        }

        return "PCC001";
    }

    // Thêm mới 1 phân công ca
    public boolean themPhanCongCa(PhanCongCa phanCong) throws SQLException {
        String sql = "INSERT INTO dbo.PhanCongCa "
                + "(maPhanCongCa, maNhanVien, maCa, ngayLamViec, trangThai, ghiChu) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
            ps.setString(1, phanCong.getMaPhanCongCa());
            ps.setString(2, phanCong.getMaNhanVien());
            ps.setString(3, phanCong.getMaCa());

            if (phanCong.getNgayLamViec() != null) {
                ps.setDate(4, Date.valueOf(phanCong.getNgayLamViec()));
            } else {
                ps.setNull(4, Types.DATE);
            }

            ps.setNString(5, phanCong.getTrangThai());

            if (phanCong.getGhiChu() == null) {
                ps.setNull(6, Types.NVARCHAR);
            } else {
                ps.setNString(6, phanCong.getGhiChu());
            }

            return ps.executeUpdate() > 0;
        }
    }

    // Cập nhật thông tin phân công ca
    public boolean suaPhanCongCa(PhanCongCa phanCong) throws SQLException {
        String sql = "UPDATE dbo.PhanCongCa "
                + "SET maNhanVien = ?, maCa = ?, ngayLamViec = ?, trangThai = ?, ghiChu = ? "
                + "WHERE maPhanCongCa = ?";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
            ps.setString(1, phanCong.getMaNhanVien());
            ps.setString(2, phanCong.getMaCa());

            if (phanCong.getNgayLamViec() != null) {
                ps.setDate(3, Date.valueOf(phanCong.getNgayLamViec()));
            } else {
                ps.setNull(3, Types.DATE);
            }

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

    // Cập nhật trạng thái phân công ca
    public boolean capNhatTrangThai(String maPhanCongCa, String trangThai) throws SQLException {
        String sql = "UPDATE dbo.PhanCongCa "
                + "SET trangThai = ? WHERE maPhanCongCa = ?";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
            ps.setNString(1, trangThai);
            ps.setString(2, maPhanCongCa);

            return ps.executeUpdate() > 0;
        }
    }

    // Xóa phân công ca theo mã
    public boolean xoaPhanCongCa(String maPhanCongCa) throws SQLException {
        String sql = "DELETE FROM dbo.PhanCongCa WHERE maPhanCongCa = ?";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
            ps.setString(1, maPhanCongCa);

            return ps.executeUpdate() > 0;
        }
    }
}