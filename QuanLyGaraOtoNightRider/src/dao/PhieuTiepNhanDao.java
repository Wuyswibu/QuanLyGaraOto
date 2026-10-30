package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import connectDB.ConnectDB;
import entity.PhieuTiepNhan;

public class PhieuTiepNhanDao {
    // ==================== KẾT NỐI VÀ KHỞI TẠO ====================

    private final Connection connection;
    
    public PhieuTiepNhanDao() {
        this.connection = null;
    }
    
    /**
     * Dùng cùng kết nối transaction do Service quản lý; DAO không commit/close. 
     */
    public PhieuTiepNhanDao(Connection connection) {
        this.connection = connection;
    }
    
    private Connection ketNoi() throws SQLException {
        Connection conn = connection != null ? connection : ConnectDB.getConnection();
        if (conn == null || conn.isClosed()) {
            throw new SQLException("Chưa kết nối cơ sở dữ liệu.");
        }
        return conn;
    }
    
    // ==================== CHUYỂN DỮ LIỆU SQL THÀNH ENTITY ====================

    private PhieuTiepNhan docPhieu(ResultSet rs) throws SQLException {
        Timestamp ngayNhan = rs.getTimestamp("ngayGioTiepNhan");
        Timestamp ngayHen = rs.getTimestamp("ngayGioHenTra");
        return new PhieuTiepNhan(
                rs.getString("maTiepNhan"),
                rs.getString("maPhuongTien"),
                rs.getString("maNhanVien"),
                ngayNhan == null ? null : ngayNhan.toLocalDateTime(),
                ngayHen == null ? null : ngayHen.toLocalDateTime(),
                rs.getInt("odo"),
                rs.getString("mucNhienLieu"),
                rs.getString("tinhTrangBanDau"),
                rs.getString("yeuCauKhachHang"),
                rs.getString("ghiChu"),
                rs.getString("trangThai"));
    }
    
    // ==================== ĐỌC DANH SÁCH VÀ TÌM KIẾM ====================

    public ArrayList<PhieuTiepNhan> layDanhSachPhieuTiepNhan() throws SQLException {
        ArrayList<PhieuTiepNhan> danhSach = new ArrayList<>();
        String sql = "SELECT * FROM dbo.PhieuTiepNhan ORDER BY ngayGioTiepNhan DESC, maTiepNhan DESC";
        try (final PreparedStatement ps = ketNoi().prepareStatement(sql);
final ResultSet rs = ps.executeQuery();) {
                while (rs.next()) {
                    danhSach.add(docPhieu(rs));
                }
            }
            return danhSach;
        }
        
        public PhieuTiepNhan timTheoMa(String maTiepNhan) throws SQLException {
            String sql = "SELECT * FROM dbo.PhieuTiepNhan WHERE maTiepNhan = ?";
            try (final PreparedStatement ps = ketNoi().prepareStatement(sql);) {
                ps.setString(1, maTiepNhan);
                try (final ResultSet rs = ps.executeQuery();) {
                    if (rs.next()) {
                        return docPhieu(rs);
                    }
                }
            }
            return null;
        }
        
        public ArrayList<PhieuTiepNhan> timTheoMaPhuongTien(String maPhuongTien) throws SQLException {
            ArrayList<PhieuTiepNhan> danhSach = new ArrayList<>();
            String sql = "SELECT * FROM dbo.PhieuTiepNhan WHERE maPhuongTien = ? ORDER BY ngayGioTiepNhan DESC, maTiepNhan DESC";
            try (final PreparedStatement ps = ketNoi().prepareStatement(sql);) {
                ps.setString(1, maPhuongTien);
                try (final ResultSet rs = ps.executeQuery();) {
                    while (rs.next()) {
                        danhSach.add(docPhieu(rs));
                    }
                }
            }
            return danhSach;
        }
        
        public ArrayList<PhieuTiepNhan> timKiem(String tuKhoa) throws SQLException {
            ArrayList<PhieuTiepNhan> danhSach = new ArrayList<>();
            String sql = "SELECT t.* FROM dbo.PhieuTiepNhan t JOIN dbo.PhuongTien p ON t.maPhuongTien = p.maPhuongTien JOIN dbo.KhachHang k ON p.maKhachHang = k.maKhachHang WHERE t.maTiepNhan LIKE ? OR p.bienSo LIKE ? OR k.hoTen LIKE ? OR k.soDienThoai LIKE ? ORDER BY t.ngayGioTiepNhan DESC, t.maTiepNhan DESC";
            String noiDung = "%" + tuKhoa.trim() + "%";
            try (final PreparedStatement ps = ketNoi().prepareStatement(sql);) {
                ps.setString(1, noiDung);
                ps.setString(2, noiDung);
                ps.setNString(3, noiDung);
                ps.setString(4, noiDung);
                try (final ResultSet rs = ps.executeQuery();) {
                    while (rs.next()) {
                        danhSach.add(docPhieu(rs));
                    }
                }
            }
            return danhSach;
        }
        
        public ArrayList<PhieuTiepNhan> timTheoTrangThai(String trangThai) throws SQLException {
            ArrayList<PhieuTiepNhan> danhSach = new ArrayList<>();
            String sql = "SELECT * FROM dbo.PhieuTiepNhan WHERE trangThai = ? ORDER BY ngayGioTiepNhan DESC, maTiepNhan DESC";
            try (final PreparedStatement ps = ketNoi().prepareStatement(sql);) {
                ps.setNString(1, trangThai);
                try (final ResultSet rs = ps.executeQuery();) {
                    while (rs.next()) {
                        danhSach.add(docPhieu(rs));
                    }
                }
            }
            return danhSach;
        }
        
        // ==================== SINH MÃ TIẾP NHẬN ====================

    public String taoMaTiepNhan() throws SQLException {
            String sql = "SELECT MAX(TRY_CONVERT(BIGINT, SUBSTRING(maTiepNhan, 4, 27))) AS soLonNhat FROM dbo.PhieuTiepNhan WHERE maTiepNhan LIKE \'PTN%\'";
            try (final PreparedStatement ps = ketNoi().prepareStatement(sql);
final ResultSet rs = ps.executeQuery();) {
                    if (rs.next()) {
                        long soMoi = rs.getLong("soLonNhat") + 1;
                        return String.format(java.util.Locale.ROOT, "PTN%03d", soMoi);
                    }
                }
                return "PTN001";
            }
            
            // ==================== THÊM PHIẾU TIẾP NHẬN ====================

    public boolean themPhieuTiepNhan(PhieuTiepNhan phieu) throws SQLException {
                String sql = "INSERT INTO dbo.PhieuTiepNhan (maTiepNhan, maPhuongTien, maNhanVien, ngayGioTiepNhan, ngayGioHenTra, odo, mucNhienLieu, tinhTrangBanDau, yeuCauKhachHang, ghiChu, trangThai) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                try (final PreparedStatement ps = ketNoi().prepareStatement(sql);) {
                    ps.setString(1, phieu.getMaTiepNhan());
                    ps.setString(2, phieu.getMaPhuongTien());
                    ps.setString(3, phieu.getMaNhanVien());
                    ps.setTimestamp(4, Timestamp.valueOf(phieu.getNgayGioTiepNhan()));
                    if (phieu.getNgayGioHenTra() == null) {
                        ps.setNull(5, Types.TIMESTAMP);
                    } else {
                        ps.setTimestamp(5, Timestamp.valueOf(phieu.getNgayGioHenTra()));
                    }
                    ps.setInt(6, phieu.getOdo());
                    ps.setNString(7, phieu.getMucNhienLieu());
                    ps.setNString(8, phieu.getTinhTrangBanDau());
                    ps.setNString(9, phieu.getYeuCauKhachHang());
                    if (phieu.getGhiChu() == null) {
                        ps.setNull(10, Types.NVARCHAR);
                    } else {
                        ps.setNString(10, phieu.getGhiChu());
                    }
                    ps.setNString(11, phieu.getTrangThai());
                    return ps.executeUpdate() > 0;
                }
            }
            
            // ==================== SỬA PHIẾU TIẾP NHẬN ====================

    public boolean suaPhieuTiepNhan(PhieuTiepNhan phieu) throws SQLException {
                String sql = "UPDATE dbo.PhieuTiepNhan SET maPhuongTien = ?, maNhanVien = ?, ngayGioTiepNhan = ?, ngayGioHenTra = ?, odo = ?, mucNhienLieu = ?, tinhTrangBanDau = ?, yeuCauKhachHang = ?, ghiChu = ?, trangThai = ? WHERE maTiepNhan = ?";
                try (final PreparedStatement ps = ketNoi().prepareStatement(sql);) {
                    ps.setString(1, phieu.getMaPhuongTien());
                    ps.setString(2, phieu.getMaNhanVien());
                    ps.setTimestamp(3, Timestamp.valueOf(phieu.getNgayGioTiepNhan()));
                    if (phieu.getNgayGioHenTra() == null) {
                        ps.setNull(4, Types.TIMESTAMP);
                    } else {
                        ps.setTimestamp(4, Timestamp.valueOf(phieu.getNgayGioHenTra()));
                    }
                    ps.setInt(5, phieu.getOdo());
                    ps.setNString(6, phieu.getMucNhienLieu());
                    ps.setNString(7, phieu.getTinhTrangBanDau());
                    ps.setNString(8, phieu.getYeuCauKhachHang());
                    if (phieu.getGhiChu() == null) {
                        ps.setNull(9, Types.NVARCHAR);
                    } else {
                        ps.setNString(9, phieu.getGhiChu());
                    }
                    ps.setNString(10, phieu.getTrangThai());
                    ps.setString(11, phieu.getMaTiepNhan());
                    return ps.executeUpdate() > 0;
                }
            }
            
            // ==================== CẬP NHẬT TRẠNG THÁI ====================

    public boolean capNhatTrangThai(String maTiepNhan, String trangThai) throws SQLException {
                String sql = "UPDATE dbo.PhieuTiepNhan SET trangThai = ? WHERE maTiepNhan = ?";
                try (final PreparedStatement ps = ketNoi().prepareStatement(sql);) {
                    ps.setNString(1, trangThai);
                    ps.setString(2, maTiepNhan);
                    return ps.executeUpdate() > 0;
                }
            }
        }
