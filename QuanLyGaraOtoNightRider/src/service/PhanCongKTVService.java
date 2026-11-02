package service;

import connectDB.ConnectDB;
import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class PhanCongKTVService {

    // 1. Lấy danh sách Phiếu sửa chữa
    public List<String> layDanhSachPhieuSuaChua() throws SQLException {
        List<String> dsPhieu = new ArrayList<>();
        Connection conn = ConnectDB.getConnection();
        if (conn == null || conn.isClosed()) conn = ConnectDB.getInstance().connect();

        // TẠM THỜI BỎ ĐIỀU KIỆN "WHERE trangThai != 'Đã hoàn thành'" ĐỂ HIỂN THỊ ĐƯỢC DỮ LIỆU TEST
        String sql = "SELECT psc.maPhieuSuaChua, pt.bienSo, psc.loaiPhieu "
                   + "FROM dbo.PhieuSuaChua psc "
                   + "INNER JOIN dbo.PhieuTiepNhan ptn ON psc.maTiepNhan = ptn.maTiepNhan "
                   + "INNER JOIN dbo.PhuongTien pt ON ptn.maPhuongTien = pt.maPhuongTien "
                   + "ORDER BY psc.ngayLap ASC";

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String maPSC = rs.getString("maPhieuSuaChua");
                String bienSo = rs.getString("bienSo");
                String loai = rs.getString("loaiPhieu");
                dsPhieu.add(String.format("[%s] %s - %s", maPSC, bienSo, loai));
            }
        }
        return dsPhieu;
    }

    // 2. Lấy danh sách Kỹ thuật viên (Mã phân quyền PQ003)
    public List<String> layDanhSachKyThuatVien() throws SQLException {
        List<String> dsKTV = new ArrayList<>();
        Connection conn = ConnectDB.getConnection();
        if (conn == null || conn.isClosed()) conn = ConnectDB.getInstance().connect();

        String sql = "SELECT maNhanVien, hoTen FROM dbo.NhanVien "
                   + "WHERE maPhanQuyen = 'PQ003' AND trangThai = N'Đang làm' "
                   + "ORDER BY hoTen ASC";

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                dsKTV.add(String.format("[%s] %s", rs.getString("maNhanVien"), rs.getString("hoTen")));
            }
        }
        return dsKTV;
    }

    // 3. Lấy danh sách công việc tại xưởng (Có JOIN với NhanVien để lấy tên KTV)
    public List<Object[]> layDanhSachCongViecTaiXuong() throws SQLException {
        List<Object[]> danhSach = new ArrayList<>();
        Connection conn = ConnectDB.getConnection();
        if (conn == null || conn.isClosed()) conn = ConnectDB.getInstance().connect();

        // TẠM THỜI BỎ ĐIỀU KIỆN "WHERE trangThai != 'Đã hoàn thành'" ĐỂ HIỂN THỊ ĐƯỢC DỮ LIỆU TEST
        String sql = "SELECT psc.maPhieuSuaChua, pt.bienSo, nv.hoTen AS tenKTV, psc.ngayLap, psc.trangThai, psc.ghiChu "
                   + "FROM dbo.PhieuSuaChua psc "
                   + "INNER JOIN dbo.PhieuTiepNhan ptn ON psc.maTiepNhan = ptn.maTiepNhan "
                   + "INNER JOIN dbo.PhuongTien pt ON ptn.maPhuongTien = pt.maPhuongTien "
                   + "LEFT JOIN dbo.NhanVien nv ON psc.maKyThuatVien = nv.maNhanVien "
                   + "ORDER BY psc.ngayLap DESC";

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String maPSC = rs.getString("maPhieuSuaChua");
                String bienSo = rs.getString("bienSo");
                String tenKTV = rs.getString("tenKTV") != null ? rs.getString("tenKTV") : "Chưa phân công";
                String ngayLap = rs.getTimestamp("ngayLap") != null ? 
                        rs.getTimestamp("ngayLap").toLocalDateTime().format(dtf) : "";
                String trangThai = rs.getString("trangThai");
                String ghiChu = rs.getString("ghiChu") != null ? rs.getString("ghiChu") : "";

                danhSach.add(new Object[]{ maPSC, bienSo, tenKTV, ngayLap, trangThai, ghiChu });
            }
        }
        return danhSach;
    }

    // 4. Xác nhận lưu KTV vào CSDL
    public boolean xacNhanPhanCong(String maPSC, String maKTV, String ghiChu) throws SQLException {
        Connection conn = ConnectDB.getConnection();
        if (conn == null || conn.isClosed()) conn = ConnectDB.getInstance().connect();

        String sql = "UPDATE dbo.PhieuSuaChua SET maKyThuatVien = ?, ghiChu = ? WHERE maPhieuSuaChua = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maKTV);
            ps.setString(2, ghiChu != null ? ghiChu.trim() : "");
            ps.setString(3, maPSC);
            return ps.executeUpdate() > 0;
        }
    }

    // 5. Hủy phân công (Gán maKyThuatVien về NULL)
    public boolean huyPhanCong(String maPSC) throws SQLException {
        Connection conn = ConnectDB.getConnection();
        if (conn == null || conn.isClosed()) conn = ConnectDB.getInstance().connect();

        String sql = "UPDATE dbo.PhieuSuaChua SET maKyThuatVien = NULL WHERE maPhieuSuaChua = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maPSC);
            return ps.executeUpdate() > 0;
        }
    }
}